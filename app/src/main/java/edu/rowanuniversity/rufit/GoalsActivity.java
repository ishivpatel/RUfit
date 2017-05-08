package edu.rowanuniversity.rufit;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.rowanuniversity.rufit.rufitObjects.Goal;

/**
 * Created by Catherine Dougherty on 3/19/2017.
 *
 * Purpose : Main activity for displaying and editting user's personal information
 * Last Update : 04.08.2017
 *
 */

public class GoalsActivity extends AppCompatActivity {
    CardView goalBlock1, goalBlock2, goalBlock3;
    ImageButton newGoal;
    TextView goalGreeting;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageView backButton;
    private DatabaseReference goalRef, myRef;
    private Goal userGoals;
    private String userID;
    private int mYear, mMonth, mDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goals_activity);

        goalBlock1 = (CardView) findViewById(R.id.firstGoal);
        goalBlock2 = (CardView) findViewById(R.id.secondGoal);
        goalBlock3 = (CardView) findViewById(R.id.thirdGoal);
        goalGreeting = (TextView) findViewById(R.id.goalGreeting);

        //edit buttons for each goal card
        ImageButton editGoal1 = (ImageButton) findViewById(R.id.editGoal1);
        ImageButton editGoal2 = (ImageButton) findViewById(R.id.editGoal2);
        ImageButton editGoal3 = (ImageButton) findViewById(R.id.editGoal3);

        //delete buttons for each goal card
        ImageButton deleteGoal1 = (ImageButton) findViewById(R.id.delete_goal_button1);
        ImageButton deleteGoal2 = (ImageButton) findViewById(R.id.delete_goal_button2);
        ImageButton deleteGoal3 = (ImageButton) findViewById(R.id.delete_goal_button3);

        //Set toolbar and back button
        Toolbar t = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("");
        backButton = (ImageView) findViewById(R.id.backButton_goalActivity);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //retrieve current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        //database instance
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users").child(userID);
        goalRef = myRef.child("goals");

        //Updates display components when database reference is changed
        goalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {    update(dataSnapshot);}
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        //Floating action button adds new goals
        newGoal = (ImageButton) findViewById(R.id.addGoal);
        newGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGoalDialog();
            }
        });

        //EDIT RUNS PER WEEK
        editGoal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weeklyRunsDialog();
            }
        });

        //EDIT WEEKLY MILEAGE
        editGoal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weeklyMileageDialog();
            }
        });

        //EDIT UPCOMING RACE
        editGoal3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upcomingRaceDialog();
            }
        });

        //DELETE RUNS PER WEEK
        deleteGoal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserGoal(v);
            }
        });

        //DELETE WEEKLY MILEAGE
        deleteGoal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserGoal(v);
            }
        });

        //DELETE UPCOMING RACE
        deleteGoal3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserGoal(v);
            }
        });
    }

    /**
     * Updates view to reflect current goals data for user in database
     * @param dataSnapshot
     */
    private void update( DataSnapshot dataSnapshot) {
        userGoals = new Goal();
        userGoals.setMilesPerWeekTarget(Double.parseDouble(dataSnapshot.child("milesPerWeekTarget").getValue().toString()));
        userGoals.setRunsPerWeekTarget(Integer.parseInt(dataSnapshot.child("runsPerWeekTarget").getValue().toString()));
        userGoals.setMilesPerWeekActual(Double.parseDouble(dataSnapshot.child("milesPerWeekActual").getValue().toString()));
        userGoals.setRunsPerWeekActual(Integer.parseInt(dataSnapshot.child("runsPerWeekActual").getValue().toString()));
        if (dataSnapshot.child("dateOfRace").getValue() == null) {
            userGoals.setDaysUntilRace("");
        } else {
            userGoals.setDaysUntilRace(dataSnapshot.child("dateOfRace").getValue().toString());
        }

        //If user hasn't added any shoes display greeting
        if (userGoals.getMilesPerWeekTarget() > 0 ||
                userGoals.getRunsPerWeekTarget() > 0||
                userGoals.getDaysUntilRace() > 0) {
                goalGreeting.setVisibility(View.GONE);
        }
        displayGoal(); //Displays update information for each goal
    }

    /**
     * Set up display for any goals he user has set
     */
    private void displayGoal() {
        //RUNS PER WEEK GOAL
        if (userGoals.getRunsPerWeekTarget()  > 0) {
            TextView tv1 = (TextView) findViewById(R.id.firstLine);
            TextView tv2 = (TextView) findViewById(R.id.secondLine);
            TextView percent1 = (TextView) findViewById(R.id.percent1);
            ProgressBar pBar1 = (ProgressBar) findViewById(R.id.goalBar1);

            int percent = (userGoals.getRunsPerWeekActual() * 100) / userGoals.getRunsPerWeekTarget();
            if(percent>100) {
                percent = 100;
            }

            tv1.setText("Runs Per Week :");
            tv2.setText("You have done " + userGoals.getRunsPerWeekActual() + " days of activity. \n" +
                    "Your goal is " + userGoals.getRunsPerWeekTarget() + ".");
            percent1.setText(percent + "%");

            pBar1.setProgress(percent);
            goalBlock1.setVisibility(View.VISIBLE); //Hide goal if user has not set it
        } else {
            goalBlock1.setVisibility(View.GONE); //Hide goal if user has not set it
        }

        //WEEKLY MILEAGE GOAL
        if (userGoals.getMilesPerWeekTarget()  > 0) {
            TextView tv1 = (TextView) findViewById(R.id.firstLine2);
            TextView tv2 = (TextView) findViewById(R.id.secondLine2);
            TextView percent2 = (TextView) findViewById(R.id.percent2);
            ProgressBar pBar2 = (ProgressBar) findViewById(R.id.goalBar2);

            double dp = (userGoals.getMilesPerWeekActual()*100)/userGoals.getMilesPerWeekTarget();
            int percent = (int) dp;
            if(percent > 100) {
                percent = 100;
            }

            tv1.setText("Miles Per Week :");
            tv2.setText("You ran " + userGoals.getMilesPerWeekActual() + " miles this week. \n" +
                    "Your goal is " + userGoals.getMilesPerWeekTarget() +" miles.");
            percent2.setText(percent +"%");
            pBar2.setProgress(percent);

            goalBlock2.setVisibility(View.VISIBLE); //Hide goal if user has not set it
        } else {
            goalBlock2.setVisibility(View.GONE);
        }

        //DAYS UNTIL UPCOMING RACE
        if (userGoals.getDaysUntilRace() >= 0) {
            TextView tv1 = (TextView) findViewById(R.id.firstLine3);
            TextView tv2 = (TextView) findViewById(R.id.secondLine3);
            tv1.setText("Days Until Race :");
            tv2.setText("" + userGoals.getDaysUntilRace() + " days until your race !");
            goalBlock3.setVisibility(View.VISIBLE); //Hide goal if user has not set it
        } else {
            goalBlock3.setVisibility(View.GONE); //Hide goal if user has not set it
        }
    }

    /**
     * Dialog pop-up for when user clicks floating action button.
     * Prompts user to select which type of goal they wish to add.
     */
    private void addGoalDialog() {
        final List<String> choices = new ArrayList<String>();

        //Checks for goal categories user does not have yet.
        //If unused, adds to list of choices.
        if(userGoals.getMilesPerWeekTarget() <= 0) {
            choices.add( "New Weekly Mileage Goal");
        }
        if(userGoals.getRunsPerWeekTarget() <= 0) {
            choices.add("Goal Number of Runs Per Week");
        }
        if(userGoals.getDaysUntilRace() < 0) {
            choices.add("Countdown To Upcoming Race");
        }

        //Create sequence of items
        final CharSequence[] c = choices.toArray(new String[choices.size()]);

        //Create dialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Add New Goal");

        //If all goal categories are used already.
        if(choices.size() ==0) {
            dialogBuilder.setMessage("You already have enough goals!");
        } else {
            dialogBuilder.setItems(c, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    String selectedText = c[item].toString();
                    if(selectedText.equals("New Weekly Mileage Goal")) {
                        weeklyMileageDialog();
                        dialog.dismiss();
                    }else if(selectedText.equals("Goal Number of Runs Per Week")) {
                        weeklyRunsDialog();
                        dialog.dismiss();
                    }else if(selectedText.equals("Countdown To Upcoming Race")) {
                        upcomingRaceDialog();
                        dialog.dismiss();
                    }else {
                        //something went wrong :')
                    }
                }
            });
        }
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    /**
     * Use date picker calendar for when user wants to add new upcoming race goal or edit
     * upcoming race goal.
     */
    private void upcomingRaceDialog() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        if(userGoals.getDaysUntilRace() >= 0) {
            DateTime date = DateTime.parse(userGoals.getDateOfRace());
            mYear = date.getYear();
            mMonth = date.getMonthOfYear()-1; //wtf does month indexing start at zero.
            mDay = date.getDayOfMonth();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(GoalsActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String pickedDate = "" + year + "-" + ++monthOfYear + "-" + dayOfMonth;
                        userGoals.setDaysUntilRace(pickedDate);
                        goalRef.setValue(userGoals);
                        Toast.makeText(GoalsActivity.this, "Goal Added", Toast.LENGTH_LONG).show();

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    /**
     * Dialog when user would like to add new weekly mileage goal or edit weekly mileage goal.
     */
    private void weeklyMileageDialog() {
        AlertDialog.Builder a = new AlertDialog.Builder(GoalsActivity.this);
        a.setTitle("Set Weekly Mileage");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        a.setView(input);

        a.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().equals("")) {
                    Toast.makeText(GoalsActivity.this, "You Entered Invalid Input", Toast.LENGTH_LONG).show();
                } else {
                    userGoals.setMilesPerWeekTarget(Double.parseDouble(input.getText().toString()));
                    goalRef.setValue(userGoals);
                    Toast.makeText(GoalsActivity.this, "Goal Added", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        a.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        a.create();
        a.show();
    }

    /**
     * Dialog for when user would like to add new goal or update existing goal of runs
     * per week.
     */
    private void weeklyRunsDialog() {
        AlertDialog.Builder a = new AlertDialog.Builder(GoalsActivity.this);
        a.setTitle("Set Runs Per Week");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        a.setView(input);

        a.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int i;
                if(input.getText().toString().equals("")) {
                    Toast.makeText(GoalsActivity.this, "You Entered Invalid Input", Toast.LENGTH_LONG).show();
                } else {
                    userGoals.setRunsPerWeekTarget(Integer.parseInt(input.getText().toString()));
                    goalRef.setValue(userGoals);
                    Toast.makeText(GoalsActivity.this, "Goal Added", Toast.LENGTH_LONG).show();
                }
            }
        });

        a.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        a.create();
        a.show();
    }

    /**
     * Deletes a user's selected goal
     * @param v passes the View object of the selected goal
     */
    private void deleteUserGoal(View v) {
        final int id = v.getId();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GoalsActivity.this);
        alertDialog.setTitle("Delete Goal");
        alertDialog.setMessage("Are you sure you want to delete this goal ?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (id) {
                    case R.id.delete_goal_button1 :
                        userGoals.setRunsPerWeekTarget(0);
                        break;
                    case R.id.delete_goal_button2 :
                        userGoals.setMilesPerWeekTarget(0.0);
                        break;
                    case R.id.delete_goal_button3 :
                        userGoals.setDaysUntilRace("");
                        break;
                }
                goalRef.setValue(userGoals);
                Toast.makeText(GoalsActivity.this, "Goal Deleted", Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }
}