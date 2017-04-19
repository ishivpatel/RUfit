package edu.rowanuniversity.rufit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.rowanuniversity.rufit.rufitObjects.Goal;
import edu.rowanuniversity.rufit.rufitObjects.Record;
import edu.rowanuniversity.rufit.rufitObjects.Run;
import edu.rowanuniversity.rufit.rufitObjects.Shoe;
import edu.rowanuniversity.rufit.rufitObjects.User;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference myRef, goalRef, shoeRef;
    TextView drawerusername;
    CircleImageView userImage;
    CardView goalsCard,recentRunCard, startRunCard;
    FirebaseUser user;
    StorageReference mStorage;
    StorageReference filePath;

    ProgressDialog mProgressDialog;
    private HashMap<String,Object> currentUser;
    private HashMap<String, Run> runMap;
    private HashMap<String,Shoe> userShoes;
    private Goal userGoals;
    private Record userRecords;
    final String ROOT = "users";
    private String text = "Welcome!";
    private  int check;
    Toolbar toolbar;
    NavigationView navigationView;
    private GenericTypeIndicator<HashMap<String,Run>> gRun = new GenericTypeIndicator<HashMap<String,Run>>() {};
    private GenericTypeIndicator<User<String,Object>> generic = new GenericTypeIndicator<User<String,Object>>() {};
    private GenericTypeIndicator<HashMap<String,Shoe>> gShoe = new GenericTypeIndicator<HashMap<String,Shoe>>() {};
    private static final int GALLERY_REQUEST_CODE = 991;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        check = 0;

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
        userImage = (CircleImageView) header.findViewById(R.id.userImage);
        mStorage = FirebaseStorage.getInstance().getReference().child("userImage");
        mProgressDialog = new ProgressDialog(this);

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
        //Unique UUID For each user for Database
        myRef  = database.getReference(ROOT).child(user.getUid());
        goalRef  = myRef.child("goals");
        shoeRef = myRef.child("shoes");
        try{
            Glide.with(getApplicationContext()).using(new FirebaseImageLoader())
                    .load(mStorage.child(user.getUid()))
                    .error(R.drawable.rufit_userimage)
                    .into(userImage);


        }catch (Exception e){

        }
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked Image", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateDashboardData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        drawerusername.setText(currentUser == null ? text : ((HashMap<String,Object>) currentUser.get("info")).get("username").toString());

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


    /**
     * Populates user's dashboard with their personal information to cards
     * //TODO : populate dashboard with user data
     */
    private void updateDashboardData(DataSnapshot d) {
        currentUser = d.getValue(generic);
        DataSnapshot goalSnapshot = d.child("goals");
        DataSnapshot runsSnapshot = d.child("runs");
        DataSnapshot shoeSnapshot = d.child("shoes");
        DataSnapshot recordSnapshot = d.child("records");

        //init goals
        userGoals = new Goal();
        userGoals.setWeekOfYear(Integer.parseInt(goalSnapshot.child("weekOfYear").getValue().toString()));
        userGoals.setMilesPerWeekTarget(Double.parseDouble(goalSnapshot.child("milesPerWeekTarget").getValue().toString()));
        userGoals.setRunsPerWeekTarget(Integer.parseInt(goalSnapshot.child("runsPerWeekTarget").getValue().toString()));
        userGoals.setMilesPerWeekActual(Double.parseDouble(goalSnapshot.child("milesPerWeekActual").getValue().toString()));
        userGoals.setRunsPerWeekActual(Integer.parseInt(goalSnapshot.child("runsPerWeekActual").getValue().toString()));
        if (goalSnapshot.child("dateOfRace").getValue() == null) {
            userGoals.setDaysUntilRace("");
        } else {
            userGoals.setDaysUntilRace(goalSnapshot.child("dateOfRace").getValue().toString());
        }

        //init runs
        if(runsSnapshot.exists() && runsSnapshot.getValue() != null) {
            runMap = runsSnapshot.getValue(gRun);
        } else {
            LinearLayout l1 = (LinearLayout) findViewById(R.id.row1);
            LinearLayout l2 = (LinearLayout) findViewById(R.id.row2);
            TextView cardDate  =(TextView) findViewById(R.id.cardDate);
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.GONE);
            cardDate.setText("You don't have any runs yet !");
        }

        //init shoes
        userShoes = shoeSnapshot.getValue(gShoe);

        if(currentUser == null){
            drawerusername.setText(text);
        }else {
            HashMap<String, Object> temp = (HashMap<String, Object>) currentUser.get("info");
            drawerusername.setText(temp.get("username").toString());
        }

        //init records
        userRecords = new Record();
        userRecords.setRecordDistance(Integer.parseInt(recordSnapshot.child("recordDistance").getValue().toString()));
        userRecords.setRecordTime(Integer.parseInt(recordSnapshot.child("recordTime").getValue().toString()));
        userRecords.setRecordPace(Integer.parseInt(recordSnapshot.child("recordPace").getValue().toString()));


        //Background updates relating to user goals
        if(check == 0) {
            refreshGoalData();
        }
        updateGoalsCard();
        updateRecentRunCard(runsSnapshot);

    }

    private void updateGoalsCard() {

        RelativeLayout r1 = (RelativeLayout) findViewById(R.id.r1);
        RelativeLayout r2 = (RelativeLayout) findViewById(R.id.r2);
        TextView noGoal = (TextView) findViewById(R.id.noGoalGreeting);

        if (!(userGoals.getMilesPerWeekTarget() > 0) && !(userGoals.getRunsPerWeekTarget() > 0)) {
            noGoal.setVisibility(View.VISIBLE);
        }

        //Components for 1st goal progress
        ProgressBar goalBar1 = (ProgressBar) findViewById(R.id.goalBar1);
        TextView userGoal1 = (TextView) findViewById(R.id.goal1);
        TextView userGoalPercent1 = (TextView) findViewById(R.id.goalPercent1);
        //Components for 2st goal progress
        ProgressBar goalBar2 = (ProgressBar) findViewById(R.id.goalBar2);
        TextView userGoal2 = (TextView) findViewById(R.id.goal2);
        TextView userGoalPercent2 = (TextView) findViewById(R.id.goalPercent2);

        //Goal userGoals = d.child("goals").getValue(Goal.class);
        if (userGoals.getRunsPerWeekTarget() > 0) {
            userGoal1.setText("Runs Per Week Progress:");
            int percent1 = (userGoals.getRunsPerWeekActual() * 100) / userGoals.getRunsPerWeekTarget();
            if(percent1 > 100) {
                percent1 = 100;
            }
            userGoalPercent1.setText("" + percent1 + "%");
            goalBar1.setProgress(percent1);
        } else {
            r1.setVisibility(View.GONE);
        }

        if (userGoals.getMilesPerWeekTarget() > 0) {
            userGoal2.setText("Weekly Mileage Progress:");
            double p = (userGoals.getMilesPerWeekActual() * 100) / Double.parseDouble("" + userGoals.getMilesPerWeekTarget());
            int percent2 = (int) p;
            if(percent2 > 100) {
                percent2 = 100;
            }
            userGoalPercent2.setText("" + percent2 + "%");
            goalBar2.setProgress(percent2);
        } else {
            r2.setVisibility(View.GONE);
        }

    }

    private void updateRecentRunCard(DataSnapshot d) {
        if (d.exists() && d.getValue() != null) {
            Run mostRecentRun = null;
            for (String id : runMap.keySet()) {
                Run current = runMap.get(id);
                if (mostRecentRun == null) {
                    mostRecentRun = current;
                } else {
                    //Date stored as MM/dd/yyyy
                    DateTime currRun = DateTime.parse(current.getDate(), DateTimeFormat.forPattern("MM/dd/yyyy"));
                    DateTime recRun = DateTime.parse(mostRecentRun.getDate(), DateTimeFormat.forPattern("MM/dd/yyyy"));

                    if (DateTimeComparator.getDateOnlyInstance().compare(currRun, recRun) > 0) {
                        mostRecentRun = current;
                    }
                }
            }

            TextView dist = (TextView) findViewById(R.id.cardDist);
            TextView date = (TextView) findViewById(R.id.cardDate);
            TextView time = (TextView) findViewById(R.id.cardTime);
            TextView pace = (TextView) findViewById(R.id.cardPace);
            View feel = (View) findViewById(R.id.cardFeel);


            dist.setText(mostRecentRun.getMileage() + " miles");
            date.setText(mostRecentRun.getDate());

            int hours = mostRecentRun.getTime() / 3600;
            int minutes = mostRecentRun.getTime() / 60;
            int sec = mostRecentRun.getTime() % 60;
            time.setText((hours > 0 ? String.format("%02d", hours) + ":" : "") + String.format("%02d", minutes) + ":" + String.format("%02d", sec));

            hours = mostRecentRun.getPace() / 3600;
            minutes = mostRecentRun.getPace() / 60;
            sec = mostRecentRun.getPace() % 60;
            pace.setText(String.format("%02d", minutes) + ":" + String.format("%02d", sec));

            switch (mostRecentRun.getFeel()) {
                case 0:
                    feel.setBackgroundColor(Color.rgb(53, 123, 173));
                    break;
                case 1:
                    feel.setBackgroundColor(Color.rgb(53, 173, 56));
                    break;
                case 2:
                    feel.setBackgroundColor(Color.rgb(247, 225, 59));
                    break;
                case 3:
                    feel.setBackgroundColor(Color.rgb(255, 140, 0));
                    break;
                case 4:
                    feel.setBackgroundColor(Color.rgb(198, 19, 19));
                    break;
            }
        }
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

        startRunCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, StartRunActivity.class);
                startActivity(intent);
            }
        });

        //TODO: RecentRun card action
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            mProgressDialog.setMessage("Uploading, Please Wait");
            mProgressDialog.show();

            filePath = mStorage.child(user.getUid());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Picasso.with(getApplicationContext()).load(downloadUri).fit().centerCrop().into(userImage);
                }
            });

        }
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

    private void refreshGoalData () {
        //At the beginning of each week, refreshes user's weekly goal data.
        DateTime now = DateTime.now();
        if((now.getWeekOfWeekyear() > userGoals.getWeekOfYear()) || now.getWeekOfWeekyear() == 0 ) {
            userGoals.setWeekOfYear(now.getWeekOfWeekyear());
            userGoals.setRunsPerWeekActual(0);
            userGoals.setMilesPerWeekActual(0.0);
        }

        //Calculates weekly mileage and runs per week
        double mileage = 0.0;
        int numOfRuns = 0;
        if(runMap != null && !(runMap.isEmpty())) {
            //Get current year and current week of the year
            int year = now.getYear();
            int currWeek = now.getWeekOfWeekyear();
            Calendar c = Calendar.getInstance();
            c.clear();
            //Set calendar to beginning of week
            c.set(Calendar.YEAR, year);
            c.set(Calendar.WEEK_OF_YEAR, currWeek);
            Date beginningOfWeek = c.getTime();

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

            for (String key : runMap.keySet()) {
                Run run = runMap.get(key);
                //Date stored as MM/dd/yyyy
                try {
                    Date dateOfRun = formatter.parse(run.getDate());
                    if (dateOfRun.compareTo(beginningOfWeek) >= 0) {
                        mileage += run.getMileage();
                        numOfRuns++;
                    }
                }catch (ParseException p) {
                    //idfk
                }

            }

            if(userShoes != null && !userShoes.isEmpty()) {
                //While we're here, we're going to update the mileage for each shoe
                for (String shoeKey : userShoes.keySet()) {
                    Shoe currShoe = userShoes.get(shoeKey);
                    currShoe.setMileage(0.0);
                    for (String runKey : runMap.keySet()) {
                        if (currShoe.getName().equals(runMap.get(runKey).getShoe())) {
                            currShoe.addMileage(runMap.get(runKey).getMileage());
                        }
                    }
                }
            }

        }
        userGoals.setRunsPerWeekActual(numOfRuns);
        userGoals.setMilesPerWeekActual(mileage);


        goalRef.setValue(userGoals);
        shoeRef.setValue(userShoes);

        check = 1;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.workout_history) {
            Intent intent = new Intent(this, WorkoutHistory.class);
            startActivity(intent);
        } else if (id == R.id.add_workout) {
            Intent intent = new Intent(this, AddRunActivity.class);
            startActivity(intent);
        } else if (id == R.id.add_shoe) {
            Intent intent = new Intent(this, ShoeActivity.class);
            startActivity(intent);
        }/*keep commented out
        else if (id == R.id.leaderboard) {

        }*/
        else if (id == R.id.goals) {
            Intent intent = new Intent(this, GoalsActivity.class);
            startActivity(intent);
        } else if(id == R.id.statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        } else if(id == R.id.personalInfo){
            Intent intent = new Intent(this, PersonalInfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.about) {
            Intent intent = new Intent(DashboardActivity.this, AboutActivity.class);
            startActivity(intent);
        }
       /*  else if(id == R.id.settings){
           Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }*/
        else if(id == R.id.signout){
            auth.signOut();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}