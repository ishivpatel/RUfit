package edu.rowanuniversity.rufit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import edu.rowanuniversity.rufit.rufitObjects.Info;
import edu.rowanuniversity.rufit.rufitObjects.User;

/**
 * Created by Catherine Dougherty on 3/19/2017.
 *
 * Purpose : Main activity for displaying and editting user's personal information
 * Last Update : 04.08.2017
 *
 */

public class PersonalInfoActivity extends AppCompatActivity {
    TextView usernameView, ageView, genderView, heightView, weightView, emailView;
    LinearLayout usernameRow, ageRow, genderRow, heightRow, weightRow, emailRow;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageView backbutton;
    DatabaseReference myRef;
    private String userID;
    final Context context = this;
    private Info uInfo; //Object holding user's current personal information

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info_activity);

        Toolbar t = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("");
        backbutton = (ImageView) findViewById(R.id.backbutton_personalinfoactivity);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        //retrieve current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        //database instance
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users").child(userID).child("info");


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
                        uInfo.setUsername(editText.getText().toString());
                        myRef.setValue(uInfo);
                        Toast.makeText(PersonalInfoActivity.this, "Username Updated", Toast.LENGTH_LONG).show();
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
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                if(uInfo.getDob() != null && !uInfo.getDob().equals("")) {
                    DateTime date = DateTime.parse(uInfo.getDob());
                    mYear = date.getYear();
                    mMonth = date.getMonthOfYear()-1; //wtf does month indexing start at zero.
                    mDay = date.getDayOfMonth();
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalInfoActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String pickedDate = "" + year + "-" + ++monthOfYear + "-" + dayOfMonth;
                                Toast.makeText(PersonalInfoActivity.this, "Date of Birth Updated", Toast.LENGTH_LONG).show();
                                uInfo.setDob(pickedDate);
                                myRef.setValue(uInfo);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
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
                        uInfo.setGender(gPicker.getValue() == 0 ? "Male" : "Female");
                        myRef.setValue(uInfo);
                        Toast.makeText(PersonalInfoActivity.this, "Gender Updated", Toast.LENGTH_LONG).show();
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
                feetInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                inchesInput.setInputType(InputType.TYPE_CLASS_NUMBER);

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
                        if(feetInput.getText().toString().equals("") || inchesInput.getText().toString().equals("")){
                            Toast.makeText(PersonalInfoActivity.this, "You Entered Invalid Input", Toast.LENGTH_LONG).show();
                        } else {
                            int feet = Integer.parseInt(feetInput.getText().toString());
                            int inches = Integer.parseInt(inchesInput.getText().toString());
                            int height = (feet * 12) + inches; //height is saved as total number of inches
                            uInfo.setHeight(height);
                            myRef.setValue(uInfo);
                            Toast.makeText(PersonalInfoActivity.this, "Height Updated", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show(); // show dialog
            }
        });

        //When user clicks weight row, dialog box appears allowing editing of weight
        weightRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.weight_dialog);

                //retrieve components
                Button dialogCancelButton = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogSubmitButton = (Button) dialog.findViewById(R.id.customDialogSubmit);
                final EditText weightInput = (EditText) dialog.findViewById(R.id.weightInput);

                weightInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                weightInput.setText(""); //initialize default value as user's current weight
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
                                if(weightInput.getText().toString().equals("")) {
                                    Toast.makeText(PersonalInfoActivity.this, "You Entered Invalid Input", Toast.LENGTH_LONG).show();
                                } else {
                                    uInfo.setWeight(Integer.parseInt(weightInput.getText().toString()));
                                    myRef.setValue(uInfo);
                                    Toast.makeText(PersonalInfoActivity.this, "Weight Updated", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
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
        emailView.setText(user.getEmail());

        if(dataSnapshot.hasChildren()) {
            uInfo = new Info();
            uInfo = dataSnapshot.getValue(Info.class);

            //update text displays
            usernameView.setText(uInfo.getUsername());
            ageView.setText(uInfo.getDob());
            genderView.setText(uInfo.getGender());
            heightView.setText(((uInfo.getHeight() / 12) + "' " + uInfo.getHeight() % 12 + "\""));
            weightView.setText(uInfo.getWeight() + " lbs.");

            if(uInfo.getDob() != null) {
                DateTime date = DateTime.parse(uInfo.getDob());
                int mYear = date.getYear();
                String month = new DateFormatSymbols().getMonths()[date.getMonthOfYear() - 1];
                int mDay = date.getDayOfMonth();
                ageView.setText(month + " " + mDay + ", " + mYear);
            }
        }
    }
}