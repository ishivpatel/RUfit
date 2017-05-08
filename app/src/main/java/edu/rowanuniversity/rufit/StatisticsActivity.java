package edu.rowanuniversity.rufit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;

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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import edu.rowanuniversity.rufit.rufitObjects.Record;
import edu.rowanuniversity.rufit.rufitObjects.Run;


/**
 * StatisticsActivity.java
 *
 * Created by Klaydon Balicanta on 4/1/17.
 * First Significant Edit by Klaydon Balicanta on 4/18/17
 */

public class StatisticsActivity extends AppCompatActivity{
    RelativeLayout records;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageView backButton;
    DatabaseReference myRef,db, recordRef;
    Record userRecords;
    private String userID;
    /*The following was added with the intent of adding functionality to run data access*/
    ArrayList<Run> dailyData;
    ArrayList<Run> weeklyData;
    ArrayList<Run> monthlyData;
    ArrayList<Run> allRunData;

    Map<Integer,Double> weeksMap;
    Map<Integer,Double> monthsMap;
    Map<Integer,Double> daysMap;
    HashMap<String, Run> runMap;
    /*-------------------------------------*/

    private GenericTypeIndicator<HashMap<String,Run>> gRun = new GenericTypeIndicator<HashMap<String,Run>>() {};

    protected void onCreate(Bundle savedInstanceState) {
        records = (RelativeLayout) findViewById(R.id.statistics);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        database = FirebaseDatabase.getInstance();
        db = database.getReference();
        myRef = db.child("users").child(userID);
        recordRef = myRef.child("records");

        Toolbar t = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        backButton = (ImageView) findViewById(R.id.sttstcs_backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });

        recordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userRecords = new Record();
                userRecords.setRecordDistance(dataSnapshot.getValue(Record.class).getRecordDistance());
                userRecords.setRecordPace(dataSnapshot.getValue(Record.class).getRecordPace());
                userRecords.setRecordTime(dataSnapshot.getValue(Record.class).getRecordTime());
                displayRecords();
            }

            @Override
            public void onCancelled(DatabaseError databaseError){}
        });
    }

    /**
     * displayRecords takes the TextViews and assigns users run records to the Statistic Panel Screen
     */
    private void displayRecords() {
        //TODO: access the specific run information to access date infor on longest/fastest
        //TODO: before storing the longest/fastest data, store the run id and then have that accessible so the user can click on that run
        //TODO: differentiate the runs when they're listed
        TextView tvLRun = (TextView) findViewById(R.id.lngstRun_distance);
        TextView tvLRunDate = (TextView) findViewById(R.id.lngstRun_date);
        TextView tvFRunPace = (TextView) findViewById(R.id.fststPace_pace);
        TextView tvFRunPaceDate = (TextView) findViewById(R.id.fststPace_date);
        TextView tvFRunTime = (TextView) findViewById(R.id.fststTime_time);
        TextView tvFRunTimeDate = (TextView) findViewById(R.id.fststTime_date);
        TextView tvRunTotal = (TextView) findViewById(R.id.runTotal);
        TextView tvRunDistance = (TextView) findViewById(R.id.runDistance);

        //Record Runs
        tvLRun.setText("Distance: " + userRecords.getRecordDistance());
        tvLRunDate.setText("Date: ");
        tvFRunPace.setText("Pace: " + userRecords.getRecordPace());
        tvFRunPaceDate.setText("Date: ");
        tvFRunTime.setText("Time: " + userRecords.getRecordTime());
        tvFRunTimeDate.setText("Date: ");

        //Cumuluative Run Information, Initially it's all time
        //TODO: set a clicker to switch to the past day/week/year and simulate what's happening in workouthistory
        tvRunTotal.setText("Total Runs: ");
        tvRunDistance.setText("Total Distance Run: ");
    }

    private void getRunInfo(DataSnapshot runSnapshot) {
        runMap = runSnapshot.getValue(gRun);
        //dailyData = new ArrayList<>();      //Run data to be used in graphical display for WorkoutHistory
        //weeklyData = new ArrayList<>();     //Run data to be used in graphical display for WorkoutHistory
        //monthlyData = new ArrayList<>();    //Run data to be used in graphical display for WorkoutHistory
        allRunData = new ArrayList<>();     //Run data to be used in run and distance accumulator for Statistics
        //weeksMap = new TreeMap<Integer, Double>();
        //monthsMap = new TreeMap<Integer, Double>();
        //daysMap = new TreeMap<Integer, Double>();

        DateTime now =DateTime.now();
        //Get current year and current week of the year

        DateTime weekAgo = now.minusWeeks(1);
        DateTime monthAgo = now.minusMonths(1);
        DateTime yearAgo = now.minusYears(1);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");


        for (String key : runMap.keySet()) {
            Run run = runMap.get(key);
            //Date stored as MM/dd/yyyy
            DateTime dateOfRun = formatter.parseDateTime(run.getDate());
            if(dateOfRun.isAfter(weekAgo)) {
                dailyData.add(run);
            }
            if (dateOfRun.isAfter(monthAgo)) {
                weeklyData.add(run);
            }
            if (dateOfRun.isAfter(yearAgo)) {
                monthlyData.add(run);
            }


            DateTime cDate = formatter.parseDateTime(run.getDate());

            //Gets total mileage for a each month
            //To be used in monthly
            if(monthsMap.containsKey(cDate.getMonthOfYear()) && !monthsMap.isEmpty()) {
                monthsMap.put(cDate.getMonthOfYear(),monthsMap.get(cDate.getMonthOfYear()) + run.getMileage());
            } else {
                monthsMap.put(cDate.getMonthOfYear(),run.getMileage());
            }


            //Gets a total of the mileage for a given week
            // To be used in displaying weekly mileage totals
            if(weeksMap.containsKey(cDate.getWeekyear()) && !weeksMap.isEmpty()) {
                weeksMap.put(cDate.getWeekyear(),weeksMap.get(cDate.getWeekyear()) + run.getMileage());
            } else {
                weeksMap.put(cDate.getWeekOfWeekyear(),run.getMileage());
            }

            //Gets a total of the mileage for a given day
            //To be used in displaying daily mileage
            if(daysMap.containsKey(cDate.getDayOfWeek()) && !daysMap.isEmpty()) {
                daysMap.put(cDate.getDayOfWeek(),daysMap.get(cDate.getDayOfWeek()) + run.getMileage());
            } else {
                daysMap.put(cDate.getDayOfWeek(),run.getMileage());
            }
        }
        //check = 1;
    }
}