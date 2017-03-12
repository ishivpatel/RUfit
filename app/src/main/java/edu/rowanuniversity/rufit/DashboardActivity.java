package edu.rowanuniversity.rufit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView SignOut, username;
    FirebaseUser user;
    final String ROOT = "users";
    String text = "Hello";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
        }else{
            updateUser();
        }

        SignOut = (TextView) findViewById(R.id.hello_world);
        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                if(auth.getCurrentUser() == null){
                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void updateUser(){
        user = auth.getCurrentUser();
        text = user.getEmail();
        //Unique UUID For each user for Database
        myRef  = database.getReference(ROOT).child(user.getUid());
        //TODO: ADD Actual Values
        myRef.setValue("Created a User");
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

        username = (TextView) findViewById(R.id.user_name);
        username.setText(user.getEmail());

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
}
