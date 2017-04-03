package edu.rowanuniversity.rufit;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import edu.rowanuniversity.rufit.rufitObjects.Goal;

/**
 * Created by Catherine Dougherty on 3/19/2017.
 *
 * Purpose : Main activity for displaying and editting user's personal information
 * Last Update : 03.26.2017
 *
 * TODO : improve error handling.
 * TODO : allow for addition of multiple days until race goals
 */

public class GoalsActivity extends AppCompatActivity {
    RelativeLayout goalBlock1, goalBlock2, goalBlock3;
    FloatingActionButton fab;
    TextView goalGreeting;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageView backbutton;
    DatabaseReference myRef,db;
    Goal userGoals;
    private String userID;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goals_activity);

        goalBlock1 = (RelativeLayout) findViewById(R.id.firstGoal);
        goalBlock2 = (RelativeLayout) findViewById(R.id.secondGoal);
        goalBlock3 = (RelativeLayout) findViewById(R.id.thirdGoal);
        goalGreeting = (TextView) findViewById(R.id.goalGreeting);

        Toolbar t = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("");
        backbutton = (ImageView) findViewById(R.id.backbutton_goalactivity);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.addGoal);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGoalDialog();
            }
        });

        //retrieve current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        //database instance
        database = FirebaseDatabase.getInstance();
        db = database.getReference();
        myRef = db.child("users").child(userID);

        //Updates display components when database reference is changed
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { update(dataSnapshot);           }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });



        FloatingActionButton editGoal1 = (FloatingActionButton) findViewById(R.id.editGoal1);
        FloatingActionButton editGoal2 = (FloatingActionButton) findViewById(R.id.editGoal2);
        FloatingActionButton editGoal3 = (FloatingActionButton) findViewById(R.id.editGoal3);
        editGoal3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(GoalsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String pickedDate = "" + year + "-" + monthOfYear + "-" + dayOfMonth;
                                myRef.child("goals").child("dateOfRace").setValue(pickedDate);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void displayGoal() {
        if (userGoals.getRunsPerWeekTarget()  > 0) {
            TextView tv1 = (TextView) findViewById(R.id.firstLine);
            TextView tv2 = (TextView) findViewById(R.id.secondLine);
            TextView percent1 = (TextView) findViewById(R.id.percent1) ;
            ProgressBar pBar1 = (ProgressBar) findViewById(R.id.goalBar1);

            int percent = (userGoals.getRunsPerWeekActual()*100)/userGoals.getRunsPerWeekTarget();

            tv1.setText("Runs Per Week :");
            tv2.setText("You have done " + userGoals.getRunsPerWeekActual() + " days of activity. \n" +
                    "Your goal is " + userGoals.getRunsPerWeekTarget() + ".");
            percent1.setText(percent +"%");

            pBar1.setProgress(percent);

        }else {
            goalBlock1.setVisibility(View.GONE);
        }

        if (userGoals.getMilesPerWeekTarget()  > 0) {
            TextView tv1 = (TextView) findViewById(R.id.firstLine2);
            TextView tv2 = (TextView) findViewById(R.id.secondLine2);
            TextView percent2 = (TextView) findViewById(R.id.percent2) ;
            ProgressBar pBar2 = (ProgressBar) findViewById(R.id.goalBar2);

            int percent = (userGoals.getMilesPerWeekActual()*100)/userGoals.getMilesPerWeekTarget();

            tv1.setText("Miles per Week :");
            tv2.setText("You have ran " + userGoals.getMilesPerWeekActual() + " miles this week. \n" +
                    "Your goal is " + userGoals.getMilesPerWeekTarget() +" miles.");
            percent2.setText(percent +"%");
            pBar2.setProgress(percent);

        }else {
            goalBlock2.setVisibility(View.GONE);
        }

        if (userGoals.getDaysUntilRace()  >=  0) {

            TextView tv1 = (TextView) findViewById(R.id.firstLine3);
            TextView tv2 = (TextView) findViewById(R.id.secondLine3);
            tv1.setText("Days Until Race :");
            tv2.setText("" + userGoals.getDaysUntilRace() + " days until your race !");
        } else {
            goalBlock3.setVisibility(View.GONE);
        }

    }

    private void addGoalDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Add New Goal");

        final String choices[] = new String[3];
        int i = 0;
        if(userGoals.getMilesPerWeekTarget() <= 0) {
            choices[i++] = "New Weekly Mileage Goal";
        }
        if(userGoals.getRunsPerWeekTarget() <=0) {
            choices[i++] = "Goal Number of Runs Per Week";
        }
        if(userGoals.getDateOfRace() == null) {
            choices[i] = "Countdown To Upcoming Race";
        }

        if(choices.length ==0) {
            alertDialogBuilder.setMessage("You already have enough goals!");
        } else {

        }
        alertDialogBuilder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //alertDialogBuilder.setMessage("Would you like to add a new goal?");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(choices.length == 0) {
                    dialog.dismiss();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void update( DataSnapshot dataSnapshot) {
        DataSnapshot d = dataSnapshot.child("users").child(userID).child("goals");
        userGoals = new Goal();
        if(d.getValue() == null) {
            goalGreeting.setText("Try setting a few goals !");
        } else {
            userGoals.setDaysUntilRace(d.getValue(Goal.class).getDateOfRace());
            userGoals.setMilesPerWeekTarget(d.getValue(Goal.class).getMilesPerWeekTarget());
            userGoals.setRunsPerWeekTarget(d.getValue(Goal.class).getRunsPerWeekTarget());
            userGoals.setMilesPerWeekActual(d.getValue(Goal.class).getMilesPerWeekActual());
            userGoals.setRunsPerWeekActual(d.getValue(Goal.class).getRunsPerWeekActual());
        }
        displayGoal();

    }
}

