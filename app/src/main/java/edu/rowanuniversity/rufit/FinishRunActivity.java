package edu.rowanuniversity.rufit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.rowanuniversity.rufit.rufitObjects.Goal;
import edu.rowanuniversity.rufit.rufitObjects.Run;
import edu.rowanuniversity.rufit.rufitObjects.RunType;
import edu.rowanuniversity.rufit.rufitObjects.Shoe;
import edu.rowanuniversity.rufit.rufitObjects.User;

public class FinishRunActivity extends AppCompatActivity {

    private Run run;

    private TextView dateView,distanceView,timeView,paceDisplay,caloriesDisplay;
    private EditText editName,notesEdit;
    private Spinner shoeSpinner, typeSpinner;
    private SeekBar seekBar;
    private Button submit, delete;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference myRef, runRef,shoeRef, goalRef;
    private String userID;
    private HashMap<String,Object> currentUser;
    private Goal userGoals;
    private HashMap<String,Shoe> shoes;
    private GenericTypeIndicator<HashMap<String,Shoe>> sGeneric = new GenericTypeIndicator<HashMap<String,Shoe>>() {};
    private GenericTypeIndicator<User<String,Object>> generic = new GenericTypeIndicator<User<String,Object>>() {};
    private int check = 0;
    private int weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_run);

        Intent intent = getIntent();
        run = (Run) intent.getSerializableExtra("Run");

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner) ;
        shoeSpinner = (Spinner) findViewById(R.id.shoeSpinner);

        distanceView = (TextView) findViewById(R.id.distanceView);
        timeView = (TextView) findViewById(R.id.timeView);
        paceDisplay = (TextView) findViewById(R.id.paceDisplay);
        dateView = (TextView) findViewById(R.id.dateView);
        caloriesDisplay = (TextView) findViewById(R.id.caloriesEdit);

        editName = (EditText) findViewById(R.id.editName);
        notesEdit = (EditText) findViewById(R.id.notesEdit);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        submit = (Button) findViewById(R.id.submit);
        delete = (Button) findViewById(R.id.delete);

        distanceView.setText(Double.toString(run.getMileage()));
        int time = run.getTime();
        timeView.setText(Integer.toString(time));
        paceDisplay.setText(Integer.toString(run.getPace()));
        dateView.setText(run.getDate());
        caloriesDisplay.setText(Integer.toString(run.getCalories()));

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

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run.setName(editName.getText().toString());
            }
        });
        notesEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run.setNotes(notesEdit.getText().toString());
            }
        });

        //TYPE
        final List<String> spinnerArray1 = new ArrayList<>();
        for(int i = 0; i < RunType.values().length; i++) {
            spinnerArray1.add((RunType.values()[i]).toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runRef.push().setValue(run);

                //Update goals
                if(userGoals.getMilesPerWeekTarget()>0) {
                    userGoals.addMiles(run.getMileage());
                }

                leaveActivity();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               leaveActivity();
            }
        });
    }


    private void leaveActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
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
}
