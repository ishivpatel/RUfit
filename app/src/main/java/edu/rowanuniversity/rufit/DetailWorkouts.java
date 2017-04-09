package edu.rowanuniversity.rufit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by shiv on 3/31/2017.
 */

public class DetailWorkouts extends AppCompatActivity {
    Button backbutton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_workout);

        backbutton = (Button) findViewById(R.id.backbutton_detail_workout);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Save Changes before finish
                finish();
            }
        });



    }
}
