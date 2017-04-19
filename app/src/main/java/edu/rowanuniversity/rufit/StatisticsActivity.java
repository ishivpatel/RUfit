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
import com.google.firebase.database.ValueEventListener;

import edu.rowanuniversity.rufit.rufitObjects.Record;


/**
 * StatisticsActivity.java
 *
 * Created by Klaydon Balicanta on 4/1/17.
 * First Significant Edit by Klaydon Balicanta on 4/18/17
 *
 *
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
        TextView tvLRun = (TextView) findViewById(R.id.lngstRun_distance);
        TextView tvLRunDate = (TextView) findViewById(R.id.lngstRun_date);
        TextView tvFRunPace = (TextView) findViewById(R.id.fststPace_pace);
        TextView tvFRunPaceDate = (TextView) findViewById(R.id.fststPace_date);
        TextView tvFRunTime = (TextView) findViewById(R.id.fststTime_time);
        TextView tvFRunTimeDate = (TextView) findViewById(R.id.fststTime_date);
        TextView tvRunTotal = (TextView) findViewById(R.id.runTotal);
        TextView tvRunDistance = (TextView) findViewById(R.id.runDistance);


        tvLRun.setText("Distance: " + userRecords.getRecordDistance());
        tvLRunDate.setText("Date: ");
        tvFRunPace.setText("Pace: " + userRecords.getRecordPace());
        tvFRunPaceDate.setText("Date: ");
        tvFRunTime.setText("Time: " + userRecords.getRecordTime());
        tvFRunTimeDate.setText("Date: ");
        tvRunTotal.setText("Total Runs: ");
        tvRunDistance.setText("Total Distance Run: ");
        //TODO: 4/8/17 make runs clickable so that when you click on a run (by clicking on date or other value regarding that specific run then it will bring up the run description...if that makes sense
    }
}