package edu.rowanuniversity.rufit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView username, drawerusername;
    FirebaseUser user;
    final String ROOT = "users";
    String text = "Hello";
    Toolbar toolbar;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(DashboardActivity.this, SignupActivity.class);
            startActivity(intent);
        }else{
            updateUser();
        }
    }

    public void updateUser(){
        user = auth.getCurrentUser();
        //text = user.getEmail();
        //Unique UUID For each user for Database
        myRef  = database.getReference(ROOT).child(user.getUid());
        //TODO: ADD Actual Values
        //myRef.setValue(true);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

        username = (TextView) findViewById(R.id.user_name);
        drawerusername = (TextView) findViewById(R.id.drawer_user_name);
        username.setText(user.getEmail());
        //drawerusername.setText(user.getEmail());



        //*For testing -- delete
        //TextView personalInfo = (TextView) findViewById(R.id.tempTextView);
        //personalInfo.setOnClickListener(new View.OnClickListener() {
          //  public void onClick(View v) {
            //    Intent intent = new Intent(DashboardActivity.this, PersonalInfoActivity.class);
              //  startActivity(intent);
            //}
        //});
    }

    public void onResume(){
        super.onResume();
        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
        }else{
            updateUser();
        }
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.workout_history) {
            // Handle the login
           /* Intent intent = new Intent(this, WorkoutHistory.class);
            startActivityForResult(intent, 1);*/

        } else if (id == R.id.add_workout) {

            /*Intent intent = new Intent(this, AddWorkoutManually.class);
            startActivityForResult(intent, 2);*/
        } else if (id == R.id.add_shoe) {



        } else if (id == R.id.leaderboard) {


        } else if (id == R.id.goals) {

        }else if(id == R.id.personalInfo){
            Intent intent = new Intent(this, PersonalInfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.about) {

        }
        else if(id == R.id.settings){
           /* Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 3);*/
        }
        else if(id == R.id.signout){
            auth.signOut();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
