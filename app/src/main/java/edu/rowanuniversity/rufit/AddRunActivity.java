package edu.rowanuniversity.rufit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import edu.rowanuniversity.rufit.rufitObjects.Run;

/**
 * Created by Naomi on 3/28/2017.
 *
 * Allows a user to manually enter a previous run.
 */

public class AddRunActivity extends AppCompatActivity {

    private TextView distanceText, timeText, paceText, paceDisplay, dateText, typeText, feelText, notesText;
    private EditText editDistance, editTime, dateEdit, notesEdit;
    private Spinner typeSpinner;
    private SeekBar seekBar;
    private Button submit, startRun;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String userID;
    private String runID;
    final Context context = this;
    private Run run;

    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_add_run);

        Intent intent = getIntent();

        Toolbar t = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        distanceText = (TextView) findViewById(R.id.distanceText);
        timeText = (TextView) findViewById(R.id.timeText);
        paceText = (TextView) findViewById(R.id.paceText);
        paceDisplay = (TextView) findViewById(R.id.paceDisplay);
        dateText = (TextView) findViewById(R.id.dateText);
        typeText = (TextView) findViewById(R.id.typeText);
        feelText = (TextView) findViewById(R.id.feelText);
        notesText = (TextView) findViewById(R.id.notesText);

        editDistance = (EditText) findViewById(R.id.editDistance);
        editTime = (EditText) findViewById(R.id.editTime);
        dateEdit = (EditText) findViewById(R.id.dateEdit);
        notesEdit = (EditText) findViewById(R.id.notesEdit);

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        submit = (Button) findViewById(R.id.submit);
        startRun = (Button) findViewById(R.id.startRun);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        //database instance
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //retrieve current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        editDistance.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!editDistance.getText().toString().equals("") ||
                        !editDistance.getText().toString().equals(null)) {
                    double mileage = Double.parseDouble(editDistance.getText().toString());
                    double time = Double.parseDouble(editTime.getText().toString());
                    double p = ((time/mileage) + (time%mileage)) / 60;
                    double rounded =  Math.round(p * 100) / 100;
                    double toSeconds = rounded * 60;
                    paceDisplay.setText(Double.toString(toSeconds));
                }
                return false;
            }
        });

        editTime.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(!editDistance.getText().toString().equals("") ||
                        !editDistance.getText().toString().equals(null)) {
                    double mileage = Double.parseDouble(editDistance.getText().toString());
                    double time = Double.parseDouble(editTime.getText().toString());
                    double p = ((time/mileage) + (time%mileage)) / 60;
                    double rounded =  Math.round(p * 100) / 100;
                    double toSeconds = rounded * 60;
                    paceDisplay.setText(Double.toString(toSeconds));
                }
                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When submit button clicked, update user's changes to database
                double mileage = Double.parseDouble(editDistance.getText().toString());
                int time = Integer.parseInt(editTime.getText().toString());
                //float pace = (float) time/distance;
                Date myDate;
                String date = dateEdit.getText().toString();
                int feel = seekBar.getProgress();
                String feelString = Integer.toString(feel);
                String type = typeSpinner.getSelectedItem().toString();
                String notes = notesEdit.getText().toString();
                myRef.child("users").child(userID).child("runs").setValue(new Run("",null,mileage,time,"",feelString,type,
                        notes));
                //myRef.child("users").child(userID).child("runs").setValue(new Run(distance,pace,time,date,
                //        feel,type,notes));
                leaveActivity();
            }

        });

        startRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStartRun();
            }
        });

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


}

