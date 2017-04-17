package edu.rowanuniversity.rufit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import edu.rowanuniversity.rufit.rufitObjects.Run;

/**
 * Created by shiv on 3/31/2017.
 */

public class WorkoutHistory  extends AppCompatActivity{

    LineChart chart;
    List<Entry> entries;
    LineDataSet dataSet;
    LineData lineData;
    ImageView back_button;
    ToggleSwitch toggle_switch;
    RecyclerView recyclerView;
    DetailViewAdapter adapter;
    List<Run> data;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    final String ROOT = "users";
    FirebaseUser user;
    private GenericTypeIndicator<HashMap<String,Run>> gRun = new GenericTypeIndicator<HashMap<String,Run>>() {};
    HashMap<String, Run> runMap;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_history);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        data = new ArrayList<>();

        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(WorkoutHistory.this, LoginActivity.class);
            startActivity(intent);
        }else{
            getRuns();
        }

        recyclerView = (RecyclerView) findViewById(R.id.details_recyclerView);

        chart = (LineChart) findViewById(R.id.workout_history_chart);
        back_button = (ImageView) findViewById(R.id.backbutton_workout_history);
        toggle_switch = (ToggleSwitch) findViewById(R.id.toggle_switch);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toggle_switch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if(position == 0){
                    //DailyView
                    generateDailyEntries();
                    chart.invalidate();
                    adapter = new DetailViewAdapter(getApplicationContext(), getdata(0));
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }else if(position ==1){
                    //WeeklyView
                    generateWeeklyEntries();
                    chart.invalidate();
                    adapter = new DetailViewAdapter(getApplicationContext(), getdata(1));
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }else{
                    //MonthlyView
                    generateMonthlyEntries();
                    chart.invalidate();
                    adapter = new DetailViewAdapter(getApplicationContext(), getdata(2));
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            }
        });

        toggle_switch.setCheckedTogglePosition(0);
    }

    private void getRuns() {
        user = auth.getCurrentUser();
        //Unique UUID For each user for Database
        myRef  = database.getReference(ROOT).child(user.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchData(DataSnapshot d) {

        DataSnapshot runsSnapshot = d.child("runs");
        if (runsSnapshot.exists() && runsSnapshot.getValue() != null) {
            runMap = runsSnapshot.getValue(gRun);
            for(String id: runMap.keySet()){
                data.add(runMap.get(id));
            }
        }
    }

    private List<Run> getdata ( int position){

            /*if (position == 0) {

                if(data.size() == 0){
                    Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
                }
            }
            if (position == 1) {
                data = new ArrayList<>();
                data = getRunsData();
            }
            if (position == 2) {
                data = new ArrayList<>();
                data = getRunsData();
            }
*/
        return data;
    }



    private void generateMonthlyEntries() {
        entries = new ArrayList<>();
        // turn your data into Entry objects
        //x- should be sorted
        for(int i=0;i<data.size();i++){
            entries.add(new Entry(i, (float) data.get(i).getMileage()));
        }

        //Has to be sorted by x axis value
        Collections.sort(entries, new EntryXComparator());
        generateGraphData();
    }

    private void generateWeeklyEntries() {
        entries = new ArrayList<>();
        // turn your data into Entry objects
        //x- should be sorted
        for(int i=0;i<data.size();i++){
            entries.add(new Entry(i, (float) data.get(i).getMileage()));
        }

        //Has to be sorted by x axis value
        Collections.sort(entries, new EntryXComparator());
        generateGraphData();
    }

    private void generateDailyEntries() {
        entries = new ArrayList<>();
        // turn your data into Entry objects
        //x- should be sorted
        for(int i=0;i<data.size();i++){
            entries.add(new Entry(i, (float) data.get(i).getMileage()));
        }

        //Has to be sorted by x axis value
        Collections.sort(entries, new EntryXComparator());
        generateGraphData();
    }

    private void generateGraphData() {

        dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(getResources().getColor(R.color.AntiqueWhite));
        dataSet.setValueTextColor(getResources().getColor(R.color.Wheat));


        lineData = new LineData(dataSet);
        chart.setData(lineData);

        designGraph();
        chart.getDescription().setText("Workouts");
        chart.getDescription().setTextColor(getResources().getColor(R.color.WhiteSmoke));
        chart.getDescription().setTextSize(10);

    }

    private void designGraph() {

        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getLegend().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
    }
}
