package edu.rowanuniversity.rufit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.rowanuniversity.rufit.rufitObjects.Run;

/**
 * Created by shiv on 3/31/2017.
 */

public class DetailWorkouts extends AppCompatActivity {
    Button backbutton;
    Run currentRun;
    TextView DateTitle;
    TextView CaloriesBurned;
    TextView DistanceRan;
    TextView TimeWorkout;
    TextView notes;
    TextView shoe;
    TextView feel;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_workout);
        currentRun = (Run) getIntent().getSerializableExtra("Key");
        DateTitle = (TextView) findViewById(R.id.date_value);
        CaloriesBurned = (TextView) findViewById(R.id.calories_workout);
        DistanceRan = (TextView) findViewById(R.id.distance_workout);
        TimeWorkout = (TextView) findViewById(R.id.duration_workout);
        notes = (TextView) findViewById(R.id.note_value);
        shoe = (TextView) findViewById(R.id.shoe_value);
        feel = (TextView) findViewById(R.id.feel_value);

        DateTitle.setText(currentRun.getDate());
        CaloriesBurned.setText("" + currentRun.getCalories());
        DistanceRan.setText("" + currentRun.getMileage());
        TimeWorkout.setText("" + currentRun.getTime());
        notes.setText("" +currentRun.getNotes());
        shoe.setText("" + currentRun.getShoe());
        feel.setText("" +currentRun.getFeel());



        backbutton = (Button) findViewById(R.id.backbutton_detail_workout);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();
            }
        });



    }
}
