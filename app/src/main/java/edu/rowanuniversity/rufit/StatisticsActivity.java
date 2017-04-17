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
 * Created by Klaydon Balicanta on 4/1/17.
 */

public class StatisticsActivity extends AppCompatActivity{
    RelativeLayout records;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageView backbutton;
    DatabaseReference myRef,db, recordRef;
    Record userRecords;
    private String userID;

    protected void OnCreate(Bundle savedInstanceState) {
        records = (RelativeLayout) findViewById(R.id.firstGoal);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        database = FirebaseDatabase.getInstance();
        db = database.getReference();
        myRef = db.child("users").child(userID);
        //recordRef = myRef.child("statistics"); //would only need if adding values

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

        recordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                update(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Updates view to reflect current statistics stored for user in database
     * @param dataSnapshot
     */
    private void update( DataSnapshot dataSnapshot) {
        DataSnapshot d = dataSnapshot.child("statistics");
        userRecords = new Record();

        userRecords.setRecordDistance(d.getValue(Record.class).getRecordDistance());
        userRecords.setRecordPace(d.getValue(Record.class).getRecordPace());
        userRecords.setRecordTime(d.getValue(Record.class).getRecordTime());
        displayRecords();
    }


    private void displayRecords() {

        //check to see if statistics are empty, and if they are, display a prompt to user saying WHAT UP YO
        TextView tvLRun = (TextView) findViewById(R.id.lngstRun_distance);
        TextView tvLRunDate = (TextView) findViewById(R.id.lngstRun_date);
        TextView tvFRunPace = (TextView) findViewById(R.id.fststPace_pace);
        TextView tvFRunPaceDate = (TextView) findViewById(R.id.fststPace_date);
        TextView tvFRunTime = (TextView) findViewById(R.id.fststTime_time);
        TextView tvFRunTimeDate = (TextView) findViewById(R.id.fststTime_date);

        tvLRun.setText("Distance - " + userRecords.getRecordDistance());
        tvLRunDate.setText("Date - ");
        tvFRunPace.setText("Pace - " + userRecords.getRecordPace());
        tvFRunPaceDate.setText("Date - ");
        tvFRunTime.setText("Time - " + userRecords.getRecordTime());
        tvFRunTimeDate.setText("Date - ");
        //TODO: 4/8/17 setup after we implement interchangable units to do km/s or mi/s or whatever
        //TODO: 4/8/17 make runs clickable so that when you click on a run (by clicking on date or other value regarding that specific run then it will bring up the run description...if that makes sense
    }
}