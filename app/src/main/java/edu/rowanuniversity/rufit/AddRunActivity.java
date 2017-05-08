package edu.rowanuniversity.rufit;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


import edu.rowanuniversity.rufit.rufitObjects.Goal;
import edu.rowanuniversity.rufit.rufitObjects.Run;
import edu.rowanuniversity.rufit.rufitObjects.RunType;
import edu.rowanuniversity.rufit.rufitObjects.Shoe;
import edu.rowanuniversity.rufit.rufitObjects.User;
import edu.rowanuniversity.rufit.timedurationpicker.TimeDurationPicker;
import edu.rowanuniversity.rufit.timedurationpicker.TimeDurationPickerDialogFragment;
import edu.rowanuniversity.rufit.timedurationpicker.TimeDurationUtil;


/**
 * Created by Naomi on 3/28/2017.
 *
 * Allows a user to manually enter a previous run.
 * Also used when user edits a selected run.
 *
 */

public class AddRunActivity extends AppCompatActivity {

    private TextView paceDisplay, caloriesText;
    private EditText editDistance, dateEdit, notesEdit, editName;
    private Spinner typeSpinner, shoeSpinner;
    private SeekBar seekBar;
    private Button submit;
    private EditText editTime;
    private ImageView backButton;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference myRef, runRef;
    private String userID;
    private Run run = new Run();
    private HashMap<String,Object> currentUser;
    private Goal userGoals;
    private HashMap<String,Shoe> shoes;
    private ArrayAdapter<String> shoeAdapter;
    private GenericTypeIndicator<HashMap<String,Shoe>> sGeneric = new GenericTypeIndicator<HashMap<String,Shoe>>() {};
    private GenericTypeIndicator<User<String,Object>> generic = new GenericTypeIndicator<User<String,Object>>() {};
    private int check = 0;
    private int weight;

    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_add_run);

        //Add back button to toolbar
        Toolbar t = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("");
        backButton = (ImageView) findViewById(R.id.backbutton_addRunActivity);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Drop-down spinner for type and shoes
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner) ;
        shoeSpinner = (Spinner) findViewById(R.id.shoeSpinner);

        //Display labels for calculated pace and calories
        paceDisplay = (TextView) findViewById(R.id.paceDisplay);
        caloriesText = (TextView) findViewById(R.id.caloriesEdit);

        //Edit text fields for run data
        editName = (EditText) findViewById(R.id.editName);
        editDistance = (EditText) findViewById(R.id.editDistance);
        editTime = (EditText) findViewById(R.id.editTime);
        dateEdit = (EditText) findViewById(R.id.dateEdit);
        notesEdit = (EditText) findViewById(R.id.notesEdit);

        //Bar for run difficulty
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        //Save run button
        submit = (Button) findViewById(R.id.submit);

        //retrieve current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        //database instance
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users").child(userID);
        runRef = myRef.child("runs");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Retrieve user shoes
                shoes = new HashMap<>();
                shoes = dataSnapshot.child("shoes").getValue(sGeneric);

                //App acts wierd due to listener consistently being set off
                //Setting shoe spinner once fixes it
                if(check == 0) {
                    populateShoeSpinner();
                }

                //Retrieve User's weight for pace calculation
                currentUser = dataSnapshot.getValue(generic);
                HashMap<String,Object> info = (HashMap<String,Object>) currentUser.get("info");
                weight = Integer.parseInt(info.get("weight").toString());


                //Retrieve user goals
                DataSnapshot goalsSnapshot = dataSnapshot.child("goals");
                userGoals = new Goal();
                userGoals.setMilesPerWeekTarget(Double.parseDouble(goalsSnapshot.child("milesPerWeekTarget").getValue().toString()));
                userGoals.setRunsPerWeekTarget(Integer.parseInt(goalsSnapshot.child("runsPerWeekTarget").getValue().toString()));
                if (goalsSnapshot.child("dateOfRace").getValue() == null) {
                    userGoals.setDaysUntilRace("");
                } else {
                    userGoals.setDaysUntilRace(goalsSnapshot.child("dateOfRace").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        editName.setSingleLine();
        dateEdit.setSingleLine();
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    pickDate();
            }
        });

        editDistance.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editDistance.setSingleLine();
        editDistance.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                distanceDataChange(v);
                return false;
            }
        });

        editTime.setSingleLine();
        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickerDialogFragment p = new PickerDialogFragment();
                p.show(getFragmentManager(), "dialog");
            }
        });

        //TYPE
        populateTypeSpinner();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRun();
            }
        });

        seekBar.setBackgroundColor(Color.rgb(53, 123, 173));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                //Depedning on user input, color of seekbar changes
                if (progress == 0) {
                    seekBar.setBackgroundColor(Color.rgb(53, 123, 173));
                } else if (progress ==1) {
                    seekBar.setBackgroundColor(Color.rgb(53, 173, 56));
                } else if (progress ==2) {
                    seekBar.setBackgroundColor(Color.rgb(247, 225, 59));
                }else if ( progress ==3) {
                    seekBar.setBackgroundColor(Color.rgb(255,140,0));
                }else if ( progress == 4) {
                    seekBar.setBackgroundColor(Color.rgb(198, 19, 19));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        //Gets data from run passed through activities when editing a run activity.
        if (getIntent().hasExtra("run")) {
            run = (Run) getIntent().getSerializableExtra("run");
            setData();
        }
    }

    /**
     * When user taps date field, a calendar dialog pops up requesting user to select date of activity.
     */
    private void  pickDate() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        //Used for editting run feature. Gets date of run being editted and sets calendar to date.
        if(!dateEdit.getText().toString().equals("")) {
            String pattern =  "MM/dd/yyyy";
            DateTime date = DateTime.parse(dateEdit.getText().toString(), DateTimeFormat.forPattern(pattern));
            mYear = date.getYear();
            mMonth = date.getMonthOfYear() - 1;
            mDay = date.getDayOfMonth();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddRunActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month = "" + (monthOfYear +1);
                        String day = "" +dayOfMonth;
                        if(monthOfYear <= 8) {
                            month = "0" +(monthOfYear +1);
                        }
                        if(dayOfMonth <= 9) {
                            day = "0" + dayOfMonth;
                        }
                        String pickedDate = month+ "/" + day + "/" + year;
                        dateEdit.setText(pickedDate);
                        run.setDate(pickedDate);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    /**
     * Takes shoes currently stored by user and displays them within spinner for selection.
     */
    private void populateShoeSpinner(){
        final List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("None");
        //SHOES
        final List<String> spinnerArray2 = new ArrayList<>();
        if(shoes!=null) {
            for (String s : shoes.keySet()) {
                spinnerArray2.add(shoes.get(s).getName());
            }
            shoeAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray2);
            shoeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            shoeSpinner.setAdapter(shoeAdapter);
            if(run.getShoe() != null) {
                shoeSpinner.setSelection(shoeAdapter.getPosition(run.getShoe()));
            }

        }

        //Ensure spinner is populated only once
        check = 1;

    }

    /**
     * Creates spinner with a variety of different types of runs for user.
     */
    private void populateTypeSpinner() {
        final List<String> spinnerArray1 = new ArrayList<>();
        for (int i = 0; i < RunType.values().length; i++) {
            spinnerArray1.add((RunType.values()[i]).toString());
        }
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray1);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        if(run.getType() != null) {
            typeSpinner.setSelection(typeAdapter.getPosition(run.getType()));
        }
    }

    /**
     * When user enters a distance, calories are calculated and pace displayed.
     * @param v
     */
    private void distanceDataChange(TextView v) {
        double caloriesRate = weight * 0.75;
        double calories = caloriesRate * Double.parseDouble(v.getText().toString());
        caloriesText.setText("" + Math.round(calories));
        run.setMileage(Double.parseDouble(v.getText().toString()));
        if(!editTime.getText().toString().isEmpty()) {
            int mins = run.getPace() / 60;
            int sec = run.getPace() % 60;
            paceDisplay.setText(String.format("%02d", mins) + ":" + String.format("%02d", sec));
        }
    }

    /**
     * Sets fields of local run object and stores to Firebase
     */
    private void submitRun() {
        //Add run to Firebase
        run.setName(editName.getText().toString());
        run.setNotes(notesEdit.getText().toString());
        run.setShoe(shoeSpinner.getSelectedItem() == null ? "" : shoeSpinner.getSelectedItem().toString());
        run.setType(typeSpinner.getSelectedItem().toString());
        run.setCalories(Integer.parseInt(caloriesText.getText().toString()));
        run.setFeel(seekBar.getProgress());
        runRef.push().setValue(run);

        //Update goals
        if(userGoals.getMilesPerWeekTarget()>0) {
            userGoals.addMiles(run.getMileage());
        }
        leaveActivity();
    }

    /**
     * For editing run, sets fields of the current run being modified.
     */
    private void setData() {
        editTime.setText(String.format("%02d",run.getTime()/3600) + ":" + String.format("%02d", run.getTime()/60) + ":" + String.format("%02d", run.getTime()%60));
        editDistance.setText("" +run.getMileage());
        editName.setText("" + run.getName());
        dateEdit.setText("" + run.getDate());
        notesEdit.setText(run.getNotes() == null ? "" :  run.getNotes().toString());
        caloriesText.setText("" + run.getCalories());
        paceDisplay.setText(String.format("%02d", run.getPace() / 60) + ":" + String.format("%02d", run.getPace() % 60));
        seekBar.setProgress(run.getFeel());
    }

    /**
     * Goes back to dashboard
     */
    private void leaveActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();
    }

    /**
     * Inner class used for time picker dialog.
     */
    @SuppressLint("ValidFragment")
    public class PickerDialogFragment extends TimeDurationPickerDialogFragment {

        @Override
        protected long getInitialDuration() {
            return 0;
        }

        @Override
        protected int setTimeUnits() {
            return TimeDurationPicker.HH_MM_SS;
        }

        @Override
        public void onDurationSet(TimeDurationPicker view, long duration) {
            int hours = TimeDurationUtil.hoursOf(duration);
            int minutes = TimeDurationUtil.minutesInHourOf(duration);
            int seconds = TimeDurationUtil.secondsInMinuteOf(duration);
            run.setTime(TimeDurationUtil.secondsOf(duration));
            editTime.setText(String.format("%02d", hours)+ ":" +String.format("%02d", minutes)+ ":" + String.format("%02d", seconds));
            if(!editDistance.getText().toString().isEmpty()) {
                int mins = run.getPace() / 60;
                int sec = run.getPace() % 60;
                paceDisplay.setText(String.format("%02d", mins) + ":" + String.format("%02d", sec));
            }
        }
    }
}

