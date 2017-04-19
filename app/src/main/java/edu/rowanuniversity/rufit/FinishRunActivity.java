package edu.rowanuniversity.rufit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import edu.rowanuniversity.rufit.rufitObjects.Run;

public class FinishRunActivity extends AppCompatActivity {

    private Run run;
    private ArrayList<LatLng> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_run);

        Intent intent = getIntent();
        run = (Run) intent.getSerializableExtra("Run");
        locations = (ArrayList) intent.getSerializableExtra("Locations");

    }

}
