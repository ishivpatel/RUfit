package edu.rowanuniversity.rufit;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import edu.rowanuniversity.rufit.rufitObjects.Goal;
import edu.rowanuniversity.rufit.rufitObjects.Run;
import edu.rowanuniversity.rufit.rufitObjects.RunType;
import edu.rowanuniversity.rufit.rufitObjects.Shoe;
import edu.rowanuniversity.rufit.timedurationpicker.TimeDurationPicker;
import edu.rowanuniversity.rufit.timedurationpicker.TimeDurationPickerDialogFragment;
import edu.rowanuniversity.rufit.timedurationpicker.TimeDurationUtil;


/**
 * Created by Naomi on 3/28/2017.
 *
 * Allows a user to manually enter a previous run.
 */

public class AddRunActivity extends AppCompatActivity {

    private TextView distanceText, timeText, paceText, paceDisplay, dateText, typeText, feelText, notesText;
    private EditText editDistance, dateEdit, notesEdit, editName;
    private Spinner typeSpinner, shoeSpinner;
    private SeekBar seekBar;
    private Button submit, startRun;
    EditText editTime;
    private ImageView backbutton;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference myRef, runRef,shoeRef, goalRef;
    private String userID;
    Run run = new Run();
    private Goal userGoals;
    private HashMap<String,Shoe> shoes;
    private GenericTypeIndicator<HashMap<String,Shoe>> sGeneric = new GenericTypeIndicator<HashMap<String,Shoe>>() {};

    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_add_run);

        Intent intent = getIntent();

        Toolbar t = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("");
        backbutton = (ImageView) findViewById(R.id.backbutton_addrunactivity);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        distanceText = (TextView) findViewById(R.id.distanceText);
        timeText = (TextView) findViewById(R.id.timeText);
        paceText = (TextView) findViewById(R.id.paceText);
        paceDisplay = (TextView) findViewById(R.id.paceDisplay);
        dateText = (TextView) findViewById(R.id.dateText);
        typeText = (TextView) findViewById(R.id.typeText);
        feelText = (TextView) findViewById(R.id.feelText);
        notesText = (TextView) findViewById(R.id.notesText);

        editName = (EditText) findViewById(R.id.editName);
        editDistance = (EditText) findViewById(R.id.editDistance);
        editTime = (EditText) findViewById(R.id.editTime);
        dateEdit = (EditText) findViewById(R.id.dateEdit);
        notesEdit = (EditText) findViewById(R.id.notesEdit);

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        shoeSpinner = (Spinner) findViewById(R.id.shoeSpinner);
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
                shoes = dataSnapshot.child("shoes").getValue(sGeneric);

                //Retrieve user goals
                DataSnapshot goalsSnapshot = dataSnapshot.child("goals");
                userGoals = new Goal();
                userGoals.setMilesPerWeekTarget(Integer.parseInt(goalsSnapshot.child("milesPerWeekTarget").getValue().toString()));
                userGoals.setRunsPerWeekTarget(Integer.parseInt(goalsSnapshot.child("runsPerWeekTarget").getValue().toString()));
                if (goalsSnapshot.child("dateOfRace").getValue() == null) {
                    userGoals.setDaysUntilRace("");
                } else {
                    userGoals.setDaysUntilRace(goalsSnapshot.child("dateOfRace").getValue().toString());
                }

                populateTypeSpinner();
                populateShoeSpinner();
               // currentUser = dataSnapshot.getValue(uGeneric);
                //Toast.makeText(AddRunActivity.this, shoes.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        editName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                run.setName(editName.getText().toString());
                return false;
            }
        });

        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    pickDate();
            }
        });

        editDistance.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editDistance.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                run.setMileage(Double.parseDouble(editDistance.getText().toString()));
                if(run.getTime() > 0) {
                    //TODO : Pace isnt working correctly
                    paceDisplay.setText(run.getPace()/60 + ":" + String.format("%02d",(run.getPace()%60) * 60));
                }
                /*if(!editDistance.getText().toString().equals("") ||
                        !editDistance.getText().toString().equals(null)) {
                    double mileage = Double.parseDouble(editDistance.getText().toString());
                    double time = Double.parseDouble(editTime.getText().toString());
                    double p = ((time/mileage) + (time%mileage)) / 60;
                    double rounded =  Math.round(p * 100) / 100;
                    double toSeconds = rounded * 60;
                    paceDisplay.setText(Double.toString(toSeconds));
                }*/
                return false;
            }
        });

        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickerDialogFragment p = new PickerDialogFragment();
                p.show(getFragmentManager(), "dialog");
            }
        });

        notesEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                run.setNotes(notesEdit.getText().toString());
                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add run to Firebase
                runRef.push().setValue(run);

                //Update mileage on shoe used
                if(run.getShoe() != null) {
                    for(String id: shoes.keySet()) {
                        if(shoes.get(id).getName().equals(run.getShoe())){
                            shoes.get(id).addMileage(run.getMileage());
                        }
                    }
                    shoeRef.setValue(shoes);
                }

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
                    seekBar.setBackgroundColor(Color.CYAN);
                } else if (progress ==1) {
                    seekBar.setBackgroundColor(Color.GREEN);
                } else if (progress ==2) {
                    seekBar.setBackgroundColor(Color.YELLOW);
                }else if ( progress ==3) {
                    seekBar.setBackgroundColor(Color.rgb(255,140,0));
                }else if ( progress == 4) {
                    seekBar.setBackgroundColor(Color.RED);
                }
                run.setFeel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

      //  startRun.setOnClickListener(new View.OnClickListener() {
        //    @Override
         //   public void onClick(View v) {
          //      goToStartRun();
           // }
       // });

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
                        String pickedDate =  monthOfYear+ "/" + dayOfMonth + "/" + year;
                        dateEdit.setText(pickedDate);
                        run.setDate(pickedDate);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void populateTypeSpinner(){
        final List<String> spinnerArray = new ArrayList<>();

        for(int i = 0; i <RunType.values().length; i++) {
            spinnerArray.add((RunType.values()[i]).toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                run.setType(spinnerArray.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void populateShoeSpinner(){
        final List<String> spinnerArray = new ArrayList<>();
        if(shoes != null) {
            for (String s : shoes.keySet()) {
                spinnerArray.add(shoes.get(s).getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            shoeSpinner.setAdapter(adapter);

            shoeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    run.setShoe(spinnerArray.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void goToStartRun() {
        Intent intent = new Intent(this, StartRunActivity.class);
        startActivity(intent);
    }


    /*
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    */



    public void leaveActivity() {
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
            if(run.getMileage() > 0) {
                paceDisplay.setText(run.getPace()/60 + "" + run.getPace()%60);
            }
        }
    }
}

