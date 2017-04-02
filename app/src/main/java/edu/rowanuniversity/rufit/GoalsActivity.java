package edu.rowanuniversity.rufit;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.rowanuniversity.rufit.rufitObjects.UserInfo;

/**
 * Created by Catherine Dougherty on 3/28/2017.
 *
 * Purpose : Main activity for displaying user's goals and progress made on goals.
 * Last Update : 04.01.2017
 *
 */

public class GoalsActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageView backbutton;
    DatabaseReference myRef;
    private String userID;
    final Context context = this;
    private UserInfo uInfo; //Object holding user's current personal information

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goals_activity);

        Toolbar t = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); //doesn't do anything yet
        backbutton = (ImageView) findViewById(R.id.backbutton_personalinfoactivity);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}