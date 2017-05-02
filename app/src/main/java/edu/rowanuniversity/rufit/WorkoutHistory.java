package edu.rowanuniversity.rufit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    ImageView backButton;
    ToggleSwitch toggle_switch;
    RecyclerView recyclerView;
    DetailViewAdapter adapter;
    ArrayList<Run> dailyData;
    ArrayList<Run> weeklyData;
    ArrayList<Run> monthlyData;
    ArrayList<Run> allRunData;
    Map<Integer,Double> weeksMap;
    Map<Integer,Double> monthsMap;
    Map<Integer,Double> daysMap;
    private int check = 0;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference myRef, runRef;
    final String ROOT = "users";
    FirebaseUser user;
    private GenericTypeIndicator<HashMap<String,Run>> gRun = new GenericTypeIndicator<HashMap<String,Run>>() {};
    HashMap<String, Run> runMap;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_history);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(WorkoutHistory.this, LoginActivity.class);
            startActivity(intent);
        }else{
            user = auth.getCurrentUser();
            //Unique UUID For each user for Database
            myRef  = database.getReference(ROOT).child(user.getUid());
            runRef = myRef.child("runs");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot runSnapshot= dataSnapshot.child("runs");

                    if(check ==0) {
                        if (runSnapshot.exists() && runSnapshot.getValue() != null) {
                            getRuns(runSnapshot);
                        }
                        //TODO IF RUNSSNAPSHOT IS NULL
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        recyclerView = (RecyclerView) findViewById(R.id.details_recyclerView);


        chart = (LineChart) findViewById(R.id.workout_history_chart);
        backButton = (ImageView) findViewById(R.id.backbutton_workout_history);
        toggle_switch = (ToggleSwitch) findViewById(R.id.toggle_switch);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toggle_switch.setCheckedTogglePosition(0);
        toggle_switch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if(check == 1) {
                    if (position == 0) {
                        //DailyView
                        //gennerateEntries(dailyData);
                        gennerateEntries(daysMap);
                        chart.invalidate();
                        Collections.sort(dailyData,new CustomComparator());
                        adapter = new DetailViewAdapter(getApplicationContext(), dailyData);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    } else if (position == 1) {
                        //WeeklyView
                        //gennerateEntries(weeklyData);
                        gennerateEntries(weeksMap);
                        chart.invalidate();
                        Collections.sort(weeklyData,new CustomComparator());
                        adapter = new DetailViewAdapter(getApplicationContext(), weeklyData);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    } else {
                        //MonthlyView
                        //gennerateEntries(monthlyData);
                        gennerateEntries(monthsMap);
                        chart.invalidate();
                        Collections.sort(monthlyData,new CustomComparator());
                        adapter = new DetailViewAdapter(getApplicationContext(), monthlyData);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                }
            }
        });


    }

    private void getRuns(DataSnapshot runSnapshot) {
        runMap = runSnapshot.getValue(gRun);
        dailyData = new ArrayList<>();      //Run data to be used in graphical display for WorkoutHistory
        weeklyData = new ArrayList<>();     //Run data to be used in graphical display for WorkoutHistory
        monthlyData = new ArrayList<>();    //Run data to be used in graphical display for WorkoutHistory
        allRunData = new ArrayList<>();     //Run data to be used in run and distance accumulator for Statistics
        weeksMap = new TreeMap<Integer, Double>();
        monthsMap = new TreeMap<Integer, Double>();
        daysMap = new TreeMap<Integer, Double>();

        DateTime now =DateTime.now();
        //Get current year and current week of the year

        DateTime weekAgo = now.minusWeeks(1);
        DateTime monthAgo = now.minusMonths(1);
        DateTime yearAgo = now.minusYears(1);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");


        for (String key : runMap.keySet()) {
            Run run = runMap.get(key);
            run.setId(key);
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


        check = 1;
    }

    private void gennerateEntries(Map<Integer,Double> data) {
        entries = new ArrayList<>();
        // turn your data into Entry objects
        //x- should be sorted
        int i = 0;
        for(Integer index : data.keySet()){
            entries.add(new Entry(i, Float.parseFloat(data.get(index).toString())));
            i++;
        }
        //Has to be sorted by x axis value
        //Collections.sort(entries, new EntryXComparator());
        generateGraphData();
    }

    private void generateGraphData() {

        if(entries.size() > 30) {
            entries = entries.subList(0,29);
        }
        dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(getResources().getColor(R.color.AntiqueWhite));
        dataSet.setValueTextColor(getResources().getColor(R.color.Wheat));
        dataSet.setValueTextSize(10);


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


    public class CustomComparator implements Comparator<Run> {
        @Override
        public int compare(Run o1, Run o2) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            try {
                Date first = formatter.parse(o1.getDate());
                Date second = formatter.parse(o2.getDate());
                return second.compareTo(first);
            } catch (ParseException p) {
                Toast.makeText(WorkoutHistory.this, "Error", Toast.LENGTH_LONG);
            }
            return 0;
        }
    }

}
