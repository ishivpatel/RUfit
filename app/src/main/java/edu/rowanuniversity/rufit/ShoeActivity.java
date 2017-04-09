package edu.rowanuniversity.rufit;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import edu.rowanuniversity.rufit.rufitObjects.Shoe;

/**
 * Created by catherine on 4/8/2017.
 * Last Updated: 04.08.2017
 *
 * Displays screen for user's shoes. Allows user to manage shoe archive and view
 * shoe details
 *
 * TODO : Slight upgrade would to keep track of number of runs user has linked with
 *      a certain shoe and also total duration of time spent running with that shoe.
 */

public class ShoeActivity extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener{

    private HashMap<String,Shoe> userShoes;
    private ImageView backbutton;
    private ImageButton newShoe;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    DatabaseReference myRef,db;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoes_activity);

        //retrieve current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        //database instance
        database = FirebaseDatabase.getInstance();
        db = database.getReference();
        myRef = db.child("users").child(userID);

        //Set toolbar and back button
        Toolbar t = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("");
        backbutton = (ImageView) findViewById(R.id.backbutton_shoeactivity);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Floating action button adds new goals
        newShoe = (ImageButton) findViewById(R.id.addShoe);
        newShoe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShoeDialog();
            }
        });

        //Updates display components when database reference is changed
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { update(dataSnapshot);           }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    /**
     * Method called by the database listener. Whenever a change in the database
     * reference is registered, this method updates local reference and diaplays.
     * @param dataSnapshot Snapshot of database
     */
    private void update(DataSnapshot dataSnapshot) {
        DataSnapshot shoeRef = dataSnapshot.child("shoes");
        userShoes = new HashMap<>();
        //If user hasn't added any shoes display greeting
        if (shoeRef.getValue() == null) {
            findViewById(R.id.shoeGreeting).setVisibility(View.VISIBLE);
        } else {
            for(DataSnapshot d : shoeRef.getChildren()) { //iterates through shoes
                Shoe s = new Shoe();
                s.setName(d.getValue(Shoe.class).getName());
                s.setMileage(d.getValue(Shoe.class).getMileage());
                userShoes.put(d.getKey(),s); // updates local reference to match db
            }
            initViews(); //initialize views
        }
    }

    /**
     * Create and display information for each shoe using RecyclerView
     */
    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new ShoeAdapter(userShoes);
        recyclerView.setAdapter(adapter);

        //Add listener for each item
        //Allows for single tap and long press on items
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, this));

    }

    /**
     * When user presses + button.
     * Asks user for name or new shoe to add.
     */
    private void addShoeDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShoeActivity.this);
        alertDialog.setTitle("Add New Shoe");

        final TextView name = new TextView(this);
        final EditText input = new EditText(this);
        input.setLines(1);

        //Layout within dialog box
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.addView(name);
        l.addView(input);

        alertDialog.setView(l);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().equals("")) { //User submits empty input
                    Toast.makeText(ShoeActivity.this,"Please Enter Valid Name", Toast.LENGTH_LONG).show();
                } else {
                    Shoe newShoe = new Shoe(input.getText().toString()); //create new shoe
                    myRef.child("shoes").push().setValue(newShoe); // add shoe to database
                    Toast.makeText(ShoeActivity.this,"Shoe Added", Toast.LENGTH_LONG).show();
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    /**
     * EDIT SHOE :Single clicking the item prompts user to edit the name of the shoe.
     * @param childView View of the item that was clicked.
     * @param position  Position of the item that was clicked.
     */
    @Override
    public void onItemClick(View childView, final int position) {
        final TextView shoeText = (TextView)childView.findViewById(R.id.shoe_name);
        final EditText input = new EditText(this) ;
        final String shoe = shoeText.getText().toString();
        input.setLines(1);
        input.setText(shoe);

        AlertDialog.Builder a  = new AlertDialog.Builder(ShoeActivity.this);
        a.setTitle("Edit Shoe Name");
        a.setView(input);
        a.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Iterates over shoes and checks for name matching user selected item
                for(String key : userShoes.keySet()) {
                    Shoe currShoe = userShoes.get(key);
                    if(currShoe.getName().equals(shoe)) {
                        Shoe s = new Shoe(input.getText().toString());
                        s.setMileage(currShoe.getMileage());
                        myRef.child("shoes").child(key).setValue(s);
                        Toast.makeText(ShoeActivity.this,shoe + " Updated", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        a.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        a.create();
        a.show();
    }

    /**
     * REMOVE SHOE :When user does a long press on the shoe item it prompts user if they would
     * like to delete shoe.
     * @param childView View of the item that was long pressed.
     * @param position  Position of the item that was long pressed.
     */
    @Override
    public void onItemLongPress(final View childView, final int position) {
        final TextView shoeText = (TextView)childView.findViewById(R.id.shoe_name);
        final String shoe = shoeText.getText().toString();

        AlertDialog.Builder a  = new AlertDialog.Builder(ShoeActivity.this);
        a.setTitle("Delete");
        a.setMessage("Do you want to remove " + shoe + " from your archive?");
        a.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Iterates over shoes and checks for matching user selected item
                for(String key : userShoes.keySet()) {
                    Shoe currShoe = userShoes.get(key);
                    if(currShoe.getName().equals(shoe)) {
                        myRef.child("shoes").child(key).removeValue();
                        Toast.makeText(ShoeActivity.this,shoe + " Removed", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        a.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        a.create();
        a.show();
    }
}