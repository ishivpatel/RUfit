package edu.rowanuniversity.rufit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    TextView shoe, pace;
    ImageView feel1, feel2, feel3, feel4, feel5;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_workout);
        currentRun = (Run) getIntent().getSerializableExtra("Key");
        DateTitle = (TextView) findViewById(R.id.date_value);
        CaloriesBurned = (TextView) findViewById(R.id.calories_workout);
        DistanceRan = (TextView) findViewById(R.id.distance_workout);
        TimeWorkout = (TextView) findViewById(R.id.duration_workout);
        pace  =(TextView) findViewById(R.id.speed_workout);
        notes = (TextView) findViewById(R.id.note_value);
        shoe = (TextView) findViewById(R.id.shoe_value);
        feel1 = (ImageView) findViewById(R.id.feel_value1) ;
        feel2 = (ImageView) findViewById(R.id.feel_value2) ;
        feel3 = (ImageView) findViewById(R.id.feel_value3) ;
        feel4 = (ImageView) findViewById(R.id.feel_value4) ;
        feel5 = (ImageView) findViewById(R.id.feel_value5) ;

        DateTitle.setText(currentRun.getDate());
        CaloriesBurned.setText("" + currentRun.getCalories());
        DistanceRan.setText("" + currentRun.getMileage());
        TimeWorkout.setText(String.format("%02d", currentRun.getTime()/60) + ":" + String.format("%02d", currentRun.getTime()%60));
        pace.setText(String.format("%02d", currentRun.getPace()/60) + ":" + String.format("%02d", currentRun.getPace()%60));
        notes.setText(currentRun.getNotes() == null ? "" : currentRun.getNotes());
        shoe.setText("" + currentRun.getShoe());
        switch(currentRun.getFeel()) {
            case 0 : feel1.setColorFilter(Color.CYAN);
                break;
            case 1 : feel1.setColorFilter(Color.GREEN);
                feel2.setColorFilter(Color.GREEN);
               break;
            case  2 :  feel1.setColorFilter(Color.YELLOW);
                feel2.setColorFilter(Color.YELLOW);
                feel3.setColorFilter(Color.YELLOW);
                break;
            case 3: feel1.setColorFilter(Color.rgb(255, 140, 0));
                feel2.setColorFilter(Color.rgb(255, 140, 0));
                feel3.setColorFilter(Color.rgb(255, 140, 0));
                feel4.setColorFilter(Color.rgb(255, 140, 0));
                break;
            case 4 : feel1.setColorFilter(Color.RED);
                feel2.setColorFilter(Color.RED);
                feel3.setColorFilter(Color.RED);
                feel4.setColorFilter(Color.RED);
                feel5.setColorFilter(Color.RED);
        }


        backbutton = (Button) findViewById(R.id.backbutton_detail_workout);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();
            }
        });



    }
}
