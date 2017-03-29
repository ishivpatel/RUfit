package edu.rowanuniversity.rufit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by shiv on 3/27/2017.
 */

public class AboutActivity extends AppCompatActivity{

    ImageView backbutton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        backbutton = (ImageView) findViewById(R.id.backbutton_aboutus);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
