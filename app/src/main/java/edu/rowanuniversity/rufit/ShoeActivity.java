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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    private GenericTypeIndicator<HashMap<String,Shoe>> generic = new GenericTypeIndicator<HashMap<String,Shoe>>() {};
    private HashMap<String,Shoe> userShoes;
    private ImageView backButton;
    private ImageButton newShoe;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference myRef,db,shoeRef;
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
        shoeRef = myRef.child("shoes");

        //Set toolbar and back button
        Toolbar t = (Toolbar) findViewById(R.id.topToolBar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("");
        backButton = (ImageView) findViewById(R.id.backButton_shoeActivity);
        backButton.setOnClickListener(new View.OnClickListener() {
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
        shoeRef.addValueEventListener(new ValueEventListener() {
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

        userShoes = dataSnapshot.getValue(generic);

        //If user hasn't added any shoes display greeting
        if (dataSnapshot.exists() && !(userShoes == null)) {
            findViewById(R.id.shoeGreeting).setVisibility(View.GONE);
             //initialize views
        } else {
            userShoes = new HashMap<>();
        }
        initViews();
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
        input.setSingleLine(true);

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
                    //String newRef = shoeRef.push().getKey();
                    Shoe newShoe = new Shoe(input.getText().toString()); //create new shoe
                    //userShoes.put(newRef, newShoe);
                    shoeRef.push().setValue(newShoe); // add shoe to database
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
        input.setSingleLine(true);
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
                        userShoes.put(key,s);
                        shoeRef.setValue(userShoes);
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
        a.setMessage("Are you sure you want to remove " + shoe + " from your archive?");
        a.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Iterates over shoes and checks for matching user selected item
                Iterator it = userShoes.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String,Object> pair = (Map.Entry)it.next();
                        if(userShoes.get(pair.getKey()).getName().equals(shoe)) {
                            it.remove();
                            shoeRef.setValue(userShoes);
                            Toast.makeText(ShoeActivity.this,shoe + " Removed", Toast.LENGTH_LONG).show();
                        } // avoids a ConcurrentModificationException
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