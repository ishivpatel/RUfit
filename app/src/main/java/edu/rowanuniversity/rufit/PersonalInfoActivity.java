package edu.rowanuniversity.rufit;

import edu.rowanuniversity.rufit.rufitObjects.*;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Catherine Dougherty on 3/19/2017.
 *
 * Purpose : Main activity for displaying and editting user's personal information
 * Last Update : 03.26.2017
 *
 * TODO : improve error handling. im sure this thing can break easily if wrong input is input
 */

public class PersonalInfoActivity extends AppCompatActivity {
    TextView usernameView, ageView, genderView, heightView, weightView, emailView;
    LinearLayout usernameRow, ageRow, genderRow, heightRow, weightRow, emailRow;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    DatabaseReference myRef;
    private String userID;
    final Context context = this;
    private UserInfo uInfo; //Object holding user's current personal information

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info_activity);

        Toolbar t = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //doesn't do anything yet

        //text display fields
        usernameView = (TextView) findViewById(R.id.usernameView);
        ageView = (TextView) findViewById(R.id.ageView);
        genderView = (TextView) findViewById(R.id.genderView);
        heightView = (TextView) findViewById(R.id.heightView);
        weightView = (TextView) findViewById(R.id.weightView);
        emailView = (TextView) findViewById(R.id.emailView);

        //row of each information section
        usernameRow = (LinearLayout) findViewById(R.id.usernameRow);
        ageRow = (LinearLayout) findViewById(R.id.ageRow);
        genderRow = (LinearLayout) findViewById(R.id.genderRow);
        heightRow = (LinearLayout) findViewById(R.id.heightRow);
        weightRow = (LinearLayout) findViewById(R.id.weightRow);
        emailRow = (LinearLayout) findViewById(R.id.emailRow);

        //database instance
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //retrieve current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        //Updates display components when database reference is changed
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { showData(dataSnapshot);            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        //When user clicks username row, dialog box appears allowing editting of username
        usernameRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog);

                //Retrieve components
                Button dialogCancelButton = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogSubmitButton = (Button) dialog.findViewById(R.id.customDialogSubmit);
                final EditText editText = (EditText) dialog.findViewById(R.id.editText);

                //When cancel button clicked, close dialog.
                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //When submit button clicked, update user's changes to database
                dialogSubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String input = editText.getText().toString();
                        myRef.child("users").child(userID).child("username").setValue(input);
                        dialog.dismiss();
                    }
                });
                dialog.show(); //display dialog
            }
        });

        //When user clicks age row, dialog box appears allowing editting of age
        ageRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.age_dialog);

                //Retrieve components
                Button dialogCancelButton = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogSubmitButton = (Button) dialog.findViewById(R.id.customDialogSubmit);
                final NumberPicker agePicker = (NumberPicker) dialog.findViewById(R.id.np);

                //initalize the number pickers min, max, and default value
                agePicker.setMinValue(10);
                agePicker.setMaxValue(100);
                agePicker.setValue(uInfo.getAge()); //default is current user's age

                //Close dialog when cancel button clicked
                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //Save updated age to db
                dialogSubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int age = agePicker.getValue();
                        myRef.child("users").child(userID).child("age").setValue(age);
                        dialog.dismiss();
                    }
                });
                dialog.show(); //display dialog
            }
        });

        //When user clicks gender row, dialog box appears allowing editting of gender
        genderRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.gender_dialog);

                //Retrieve components
                Button dialogCancelButton = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogSubmitButton = (Button) dialog.findViewById(R.id.customDialogSubmit);
                final NumberPicker gPicker = (NumberPicker) dialog.findViewById(R.id.np);

                //Used a number picker component for gender selection because I can't
                // figure out radio buttons
                gPicker.setMinValue(0);
                gPicker.setMaxValue(1); //size  = 2
                gPicker.setDisplayedValues(new String[]{"Male", "Female"}); // display Male and Female
                gPicker.setValue(uInfo.getGender() != null && uInfo.getGender().equals("Male") ? 0 : 1); // if index 0, then male, else female

                //close dialog box on clicking cancel button
                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //Update database with changes
                dialogSubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String gender = gPicker.getValue() == 0 ? "Male" : "Female";
                        myRef.child("users").child(userID).child("gender").setValue(gender);
                        dialog.dismiss();
                    }
                });
                dialog.show(); //show dialog
            }
        });

        //When user clicks weight row, dialog box appears allowing editting of weight
        heightRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.height_dialog);

                //Retrieve components
                Button dialogCancelButton = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogSubmitButton = (Button) dialog.findViewById(R.id.customDialogSubmit);
                final EditText feetInput = (EditText) dialog.findViewById(R.id.feetInput);
                final EditText inchesInput = (EditText) dialog.findViewById(R.id.inchesInput);

                //initialize input fields with current user's height information
                feetInput.setText("" +uInfo.getHeight()/12);
                inchesInput.setText("" +uInfo.getHeight()%12);

                //Close bro
                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //update db with any changes
                dialogSubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int feet = Integer.parseInt(feetInput.getText().toString());
                        int inches = Integer.parseInt(inchesInput.getText().toString());
                        int height = (feet*12) + inches; //height is saved as total number of inches
                        myRef.child("users").child(userID).child("height").setValue(height);
                        dialog.dismiss();
                    }
                });
                dialog.show(); // show dialog
            }
        });

        //When user clicks weight row, dialog box appears allowing editting of weight
        weightRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.weight_dialog);

                //retrieve components
                Button dialogCancelButton = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogSubmitButton = (Button) dialog.findViewById(R.id.customDialogSubmit);
                final EditText weightInput = (EditText) dialog.findViewById(R.id.weightInput);

                weightInput.setText("" + uInfo.getWeight()); //initalize default value as user's current weight
                //close
                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //when submit button clicked, update db with input weight
                dialogSubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int weight = Integer.parseInt(weightInput.getText().toString());
                        myRef.child("users").child(userID).child("weight").setValue(weight);
                        dialog.dismiss();
                    }
                });
                dialog.show(); //show dialog
            }
        });
    }

    /*
     * This method updates text displays of user's personal information whenever it
     * reads a change in database reference
     * @param dataSnapshot 'snapshot' of entire database
     */
    private void showData(DataSnapshot dataSnapshot) {
        DataSnapshot d = dataSnapshot.child("users");
        emailView.setText(user.getEmail());

        //handling for when users are created with no personal info.
        if(!(d.child(userID).hasChild("username"))) {
            usernameView.setText("Add Username!");
        }
        if(!(d.child(userID).hasChild("age"))) {
            ageView.setText("Add your age!");
        }
        if(!(d.child(userID).hasChild("gender"))) {
            genderView.setText("Add gender!");
        }
        if(!(d.child(userID).hasChild("weight"))) {
            weightView.setText("Add weight!");
        }
        if(!(d.child(userID).hasChild("height"))) {
            heightView.setText("Add height!");
        }

        if(d.child(userID).hasChildren()) {
            uInfo = new UserInfo();
            //set username
            uInfo.setUsername(d.child(userID).getValue(UserInfo.class).getUsername().toString());
            //set age
            uInfo.setAge(d.child(userID).getValue(UserInfo.class).getAge());
            //set gender
            uInfo.setGender(d.child(userID).getValue(UserInfo.class).getGender());
            //set height
            uInfo.setHeight(d.child(userID).getValue(UserInfo.class).getHeight());
            //set weight
            uInfo.setWeight(d.child(userID).getValue(UserInfo.class).getWeight());

            //update text displays
            usernameView.setText(uInfo.getUsername());
            ageView.setText("" + uInfo.getAge());
            genderView.setText(uInfo.getGender());
            heightView.setText(((uInfo.getHeight() / 12) + "' " + uInfo.getHeight() % 12 + "\""));
            weightView.setText(uInfo.getWeight() + " lbs.");
        }
    }
}

