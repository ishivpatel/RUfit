package edu.rowanuniversity.rufit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Iterator;
import java.util.Map;

import edu.rowanuniversity.rufit.rufitObjects.Run;

/**
 * Created by shiv on 3/31/2017.
 */

public class DetailWorkouts extends AppCompatActivity {

    Button donebutton;
    ImageView backbutton;
    Run currentRun;
    TextView DateTitle;
    TextView CaloriesBurned;
    TextView DistanceRan;
    TextView TimeWorkout;
    TextView notes;
    TextView shoe, pace;
    private ImageView feel1, feel2, feel3, feel4, feel5, edit, delete;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference runRef;
    final String ROOT = "users";
    FirebaseUser user;



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
        backbutton = (ImageView) findViewById(R.id.backbuttonnnnnn);
        edit = (ImageView) findViewById(R.id.editttttt);
        delete = (ImageView) findViewById(R.id.del);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        runRef = database.getReference(ROOT).child(user.getUid()).child("runs");


        DateTitle.setText(currentRun.getDate());
        CaloriesBurned.setText("" + currentRun.getCalories());
        DistanceRan.setText("" + currentRun.getMileage());

        TimeWorkout.setText(String.format("%02d",currentRun.getTime()/3600) + ":" + String.format("%02d", currentRun.getTime()/60) + ":" + String.format("%02d", currentRun.getTime()%60));
        pace.setText(String.format("%02d", currentRun.getPace()/60) + ":" + String.format("%02d", currentRun.getPace()%60));
        notes.setText(currentRun.getNotes() == null ? "" : currentRun.getNotes());

        shoe.setText("" + currentRun.getShoe());
        switch(currentRun.getFeel()) {
            case 0 : feel1.setColorFilter(Color.rgb(53, 123, 173));
                break;
            case 1 : feel1.setColorFilter(Color.rgb(53, 173, 56));
                feel2.setColorFilter(Color.rgb(53, 173, 56));
               break;
            case  2 :  feel1.setColorFilter(Color.rgb(247, 225, 59));
                feel2.setColorFilter(Color.rgb(247, 225, 59));
                feel3.setColorFilter(Color.rgb(247, 225, 59));
                break;
            case 3: feel1.setColorFilter(Color.rgb(255, 140, 0));
                feel2.setColorFilter(Color.rgb(255, 140, 0));
                feel3.setColorFilter(Color.rgb(255, 140, 0));
                feel4.setColorFilter(Color.rgb(255, 140, 0));
                break;
            case 4 : feel1.setColorFilter(Color.rgb(198, 19, 19));
                feel2.setColorFilter(Color.rgb(198, 19, 19));
                feel3.setColorFilter(Color.rgb(198, 19, 19));
                feel4.setColorFilter(Color.rgb(198, 19, 19));
                feel5.setColorFilter(Color.rgb(198, 19, 19));
        }



        donebutton = (Button) findViewById(R.id.backButton_detailWorkout);

        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailWorkouts.this, AddRunActivity.class);
                intent.putExtra("run",currentRun);
                startActivity(intent);
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder a  = new AlertDialog.Builder(DetailWorkouts.this);
                a.setTitle("Delete");
                a.setMessage("Are you sure you want to delete this run?");
                a.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runRef.child(currentRun.getId()).removeValue();
                        finish();
                    }
                });
                a.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        a.create();
                a.show();
            }
        });

    }
}