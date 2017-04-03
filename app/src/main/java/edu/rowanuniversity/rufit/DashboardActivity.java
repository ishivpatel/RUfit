package edu.rowanuniversity.rufit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView drawerusername;
    CardView goalsCard,recentRunCard, startRunCard;
    FirebaseUser user;
    final String ROOT = "users";
    String text = "Welcome!";
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
        View header = navigationView.getHeaderView(0);
        drawerusername = (TextView) header.findViewById(R.id.drawer_user_name);

        //Allow actions when cards on dashboard are clicked
        setCardActions();

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
        drawerusername.setText(user.getEmail());

        //Save for later
        //populateDash();
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
           Intent intent = new Intent(this, WorkoutHistory.class);
            startActivity(intent);

        } else if (id == R.id.add_workout) {

            /*Intent intent = new Intent(this, AddWorkoutManually.class);
            startActivity(intent);*/
        } else if (id == R.id.add_shoe) {



        } else if (id == R.id.leaderboard) {


        } else if (id == R.id.goals) {
            Intent intent = new Intent(this, GoalsActivity.class);
            startActivity(intent);
        }else if(id == R.id.personalInfo){
            Intent intent = new Intent(this, PersonalInfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.about) {
            Intent intent = new Intent(DashboardActivity.this, AboutActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.settings){
           /* Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);*/
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

    /**
     * Populates user's dashboard with their personal information to cards
     * //TODO : populate dashboard with user data
     */
    public void populateDash() {
        //User can click card to quickstart new run
        CardView startRunCard = (CardView) findViewById(R.id.cardStartRun);

        //Components for 1st goal progress
        ProgressBar goalBar = (ProgressBar) findViewById(R.id.goalBar1);
        TextView userGoal = (TextView) findViewById(R.id.goal1);
        TextView userGoalPercent = (TextView) findViewById(R.id.goalPercent1);

    }

    public void setCardActions () {
        startRunCard = (CardView) findViewById(R.id.cardStartRun);
        recentRunCard = (CardView) findViewById(R.id.cardRecentRun);
        goalsCard = (CardView) findViewById(R.id.cardGoals);

        goalsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, GoalsActivity.class);
                startActivity(intent);
            }
        });

        //TODO: StartRun card action
        //TODO: RecentRun card action
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
