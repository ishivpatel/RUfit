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
 * TODO: CATHERINE BROK THE SPINNER IDFK WHAT I DID IM SORRY
 */

public class AddRunActivity extends AppCompatActivity {

    private TextView distanceText, timeText, paceText, paceDisplay, dateText, typeText, feelText, notesText, caloriesText;
    private EditText editDistance, dateEdit, notesEdit, editName, typeEdit, shoeEdit;
    private Spinner typeSpinner, shoeSpinner;
    private SeekBar seekBar;
    private Button submit, startRun;
    EditText editTime;
    private ImageView backButton;
    private LinearLayout shoeLayout, typeLayout;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference myRef, runRef,shoeRef, goalRef;
    private String userID;
    Run run = new Run();
    private HashMap<String,Object> currentUser;
    private Goal userGoals;
    private HashMap<String,Shoe> shoes;
    private GenericTypeIndicator<HashMap<String,Shoe>> sGeneric = new GenericTypeIndicator<HashMap<String,Shoe>>() {};
    private GenericTypeIndicator<User<String,Object>> generic = new GenericTypeIndicator<User<String,Object>>() {};
    private int check = 0;
    private int weight;

    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_add_run);

        Intent intent = getIntent();

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

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner) ;
        shoeSpinner = (Spinner) findViewById(R.id.shoeSpinner);

        distanceText = (TextView) findViewById(R.id.distanceText);
        timeText = (TextView) findViewById(R.id.timeText);
        paceText = (TextView) findViewById(R.id.paceText);
        paceDisplay = (TextView) findViewById(R.id.paceDisplay);
        dateText = (TextView) findViewById(R.id.dateText);
        typeText = (TextView) findViewById(R.id.typeText);
        feelText = (TextView) findViewById(R.id.feelText);
        notesText = (TextView) findViewById(R.id.notesText);
        caloriesText = (TextView) findViewById(R.id.caloriesEdit);

        editName = (EditText) findViewById(R.id.editName);
        editDistance = (EditText) findViewById(R.id.editDistance);
        editTime = (EditText) findViewById(R.id.editTime);
        dateEdit = (EditText) findViewById(R.id.dateEdit);
        notesEdit = (EditText) findViewById(R.id.notesEdit);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        submit = (Button) findViewById(R.id.submit);
        //startRun = (Button) findViewById(R.id.startRun);

        //retrieve current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();
        //database instance
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users").child(userID);
        runRef = myRef.child("runs");
        shoeRef = myRef.child("shoes");
        goalRef = myRef.child("goals");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Retrieve user shoes
                shoes = new HashMap<>();
                shoes = dataSnapshot.child("shoes").getValue(sGeneric);
                if(check == 0) {
                    populateShoeSpinner();
                }

                //Retrive User's weight
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
                double caloriesRate = weight * 0.75;
                double calories = caloriesRate * Double.parseDouble(v.getText().toString());
                caloriesText.setText("" + Math.round(calories));
                run.setMileage(Double.parseDouble(v.getText().toString()));
                if(!editTime.getText().toString().isEmpty()) {
                    int mins = run.getPace() / 60;
                    int sec = run.getPace() % 60;
                    paceDisplay.setText(String.format("%02d", mins) + ":" + String.format("%02d", sec));
                }
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
        final List<String> spinnerArray1 = new ArrayList<>();
        for(int i = 0; i <RunType.values().length; i++) {
            spinnerArray1.add((RunType.values()[i]).toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray1);
               adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               typeSpinner.setAdapter(adapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add run to Firebase
                run.setName(editName.getText().toString());
                run.setNotes(notesEdit.getText().toString());
                run.setShoe(shoeSpinner.getSelectedItem() == null ? "" : shoeSpinner.getSelectedItem().toString());
                run.setType(typeSpinner.getSelectedItem().toString());
                run.setCalories(Integer.parseInt(caloriesText.getText().toString()));
                runRef.push().setValue(run);

                //Update goals
                if(userGoals.getMilesPerWeekTarget()>0) {
                    userGoals.addMiles(run.getMileage());
                }
                leaveActivity();
            }
        });

        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                progressChanged = progress;
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
                run.setFeel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void  pickDate() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
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

    private void populateShoeSpinner(){
        final List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("None");
        //SHOES
        final List<String> spinnerArray2 = new ArrayList<>();
        if(shoes!=null) {
            for (String s : shoes.keySet()) {
                spinnerArray2.add(shoes.get(s).getName());
            }
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray2);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            shoeSpinner.setAdapter(adapter2);
        }
        check = 1;

    }

    private void leaveActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();
    }

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

