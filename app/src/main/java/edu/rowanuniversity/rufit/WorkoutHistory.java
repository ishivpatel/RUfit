package edu.rowanuniversity.rufit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

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
    Button detailView;
    RecyclerView recyclerView;
    DetailViewAdapter adapter;
    List<WorkoutsData> data;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_history);

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

    private List<WorkoutsData> getdata(int position) {

        Random rand = new Random(100);
        if(position == 0){
            data = new ArrayList<>();
            for(int i=0; i<7;i++){
            WorkoutsData temp = new WorkoutsData(" "+ rand.nextInt(7) + 1 +" ",
                    " "+rand.nextInt(150), " "+rand.nextInt(150)," "+rand.nextInt(150)+ " ");
                data.add(temp);
            }
        }
        if(position == 1){
            data = new ArrayList<>();
            for(int i=0; i<30;i++){
                WorkoutsData temp = new WorkoutsData(" "+ rand.nextInt(30) + 1 +" ",
                        " "+rand.nextInt(150), " "+rand.nextInt(150)," "+rand.nextInt(150)+ " ");
                data.add(temp);
            }
        }
        if(position == 2){
            data = new ArrayList<>();
            for(int i=0; i<365;i++){
                WorkoutsData temp = new WorkoutsData(" "+ rand.nextInt(30) + 1 +" ",
                        " "+rand.nextInt(150), " "+rand.nextInt(150)," "+rand.nextInt(150)+ " ");
                data.add(temp);
            }
        }

        return data;
    }

    private void generateMonthlyEntries() {
        entries = new ArrayList<>();
        entries = new ArrayList<>();
        // turn your data into Entry objects
        //x- should be sorted
        entries.add(new Entry(0, 85));
        entries.add(new Entry(1, 55));
        entries.add(new Entry(2, 83));
        entries.add(new Entry(3, 89));
        entries.add(new Entry(4, 71));
        entries.add(new Entry(5, 24));
        entries.add(new Entry(6, 32));
        entries.add(new Entry(7, 29));
        entries.add(new Entry(8, 19));
        entries.add(new Entry(9, 67));
        entries.add(new Entry(10, 77));
        entries.add(new Entry(11, 37));
        //Has to be sorted by x axis value
        Collections.sort(entries, new EntryXComparator());
        generateGraphData();
    }

    private void generateWeeklyEntries() {
        entries = new ArrayList<>();
        entries = new ArrayList<>();
        // turn your data into Entry objects
        //x- should be sorted
        entries.add(new Entry(0, 95));
        entries.add(new Entry(1, 25));
        entries.add(new Entry(2, 89));
        entries.add(new Entry(3, 35));
        entries.add(new Entry(4, 16));
        //Has to be sorted by x axis value
        Collections.sort(entries, new EntryXComparator());
        generateGraphData();
    }

    private void generateDailyEntries() {
        entries = new ArrayList<>();
        // turn your data into Entry objects
        //x- should be sorted
        entries.add(new Entry(0, 55));
        entries.add(new Entry(1, 65));
        entries.add(new Entry(2, 8));
        entries.add(new Entry(3, 85));
        entries.add(new Entry(4, 76));
        entries.add(new Entry(5, 34));
        entries.add(new Entry(6, 7));
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
