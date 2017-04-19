package edu.rowanuniversity.rufit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.rowanuniversity.rufit.rufitObjects.Run;

public class StartRunActivity extends FragmentActivity implements android.location.LocationListener,OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private Button start, stop;
    private Chronometer chronometer;
    GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    Location mCurrLocation;
    String mLastUpdateTime;
    //Marker mCurrLocationMarker;
    private FirebaseDatabase database;
    private DatabaseReference myRef, db, runRef;
    private String userID;
    private FirebaseAuth auth;
    private FirebaseUser user;
    public static final int MAP_ZOOM_LEVEL = 16;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final long UPDATE_INTERVAL_IN_MS = 1200;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MS = UPDATE_INTERVAL_IN_MS / 4;
    public static final String TAG = StartRunActivity.class.getSimpleName();
    private boolean mRequestingLocationUpdates = false;
    private LocationManager locationManager;
    private Location mLastLocation;
    private double startLoggingTime;
    private Run run;
    Intent intent;
    private ArrayList<LatLng> locations;
    String bestProvider;
    Criteria criteria;
    Polyline route;
    float distanceCovered;
    int weight;
    private HashMap<String,Object> currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_run);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "checking permissions");
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        run = new Run();
        locations = new ArrayList<>();

        //user = dataSnapshot.getValue(generic);
        //HashMap<String,Object> info = (HashMap<String,Object>) user.get("info");
        //weight = Integer.parseInt(info.get("weight").toString());

        database = FirebaseDatabase.getInstance();
        db = database.getReference();
        myRef = db.child("users").child(userID);
        runRef = myRef.child("runs");

        chronometer = (Chronometer) findViewById(R.id.chronometer);

        start = (Button) findViewById(R.id.start_button);
        stop = (Button) findViewById(R.id.stop_button);

        intent = new Intent(this,FinishRunActivity.class);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                startLoggingTime = chronometer.getDrawingTime();
                startLogging();
            }
        });
        stop.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationTracking();
                chronometer.stop();

                String[] splits = chronometer.getText().toString().split(":");
                int time;
                if(Integer.parseInt(splits[0]) == 0) {
                    time = 1;
                }
                else {
                    time = Integer.parseInt(splits[0]);
                }
                run.setTime(time);

                double miles = distanceCovered * .000621371;
                DecimalFormat df = new DecimalFormat("#.##");
                miles = Double.valueOf(df.format(miles));
                run.setMileage(miles);

                Calendar c = Calendar.getInstance();
                SimpleDateFormat dformat = new SimpleDateFormat("MM/dd/yyyy");
                String formattedDate = dformat.format(c.getTime());
                run.setDate(formattedDate);

                run.calculatePace();

                //TBH idk how to calculate and set calories
                run.setCalories(693);

                intent.putExtra("Run",run);
                startActivity(intent);
            }
        }));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void stopLocationTracking() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        //Toast.makeText(this,"Google API Clinet connected",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "onLocationChanged called", Toast.LENGTH_LONG).show();
        mCurrLocation = location;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        mLastUpdateTime = dateFormat.format(date).toString();

        if (mLastLocation != null) {
            float distance = mLastLocation.distanceTo(mCurrLocation);
            distanceCovered = distanceCovered + distance;
            double miles = distanceCovered * .000621371;
            DecimalFormat df = new DecimalFormat("#.##");
            miles = Double.valueOf(df.format(miles));
            if(miles > .1) {
                Toast.makeText(this, "distance covered: " + miles + " miles",
                        Toast.LENGTH_SHORT).show();
            }
            mLastLocation = mCurrLocation;
        }
        
        LatLng latLng = new LatLng(mCurrLocation.getLatitude(),mCurrLocation.getLongitude());
        locations.add(latLng);
        mMap.clear();
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < locations.size(); i++) {
            LatLng point = locations.get(i);
            options.add(point);
        }
        route = mMap.addPolyline(options);

        //drawLocations();

        /*
        LatLng mLatlng = new LatLng(mCurrLocation.getLatitude(),
                mCurrLocation.getLongitude());
        MarkerOptions mMarkerOptions = new MarkerOptions()
                .position(mLatlng)
                .title(mLastUpdateTime)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(mMarkerOptions);
        */
    }

    private void calculateDistance() {

    }

    private void calculateTime() {

    }

    private void saveToFirebase() {
        Map mLocations = new HashMap();
        mLocations.put("timestamp", mLastUpdateTime);
        Map mCoordinate = new HashMap();
        mCoordinate.put("latitude", mCurrLocation.getLatitude());
        mCoordinate.put("longitude", mCurrLocation.getLongitude());
        mLocations.put("location", mCoordinate);
        //runRef.push().setValue(mLocations);
    }

    private void drawLocations() {
        Toast.makeText(this,"Drawing Locations",Toast.LENGTH_SHORT);
        Query queryRef = myRef.orderByChild("timestamp").startAt(startLoggingTime);
        queryRef.addChildEventListener(new ChildEventListener() {
            LatLngBounds bounds;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map data = (Map) dataSnapshot.getValue();
                String timestamp = (String) data.get("timestamp");
                Map mCoordinate = (HashMap) data.get("location");
                double latitude = (double) mCoordinate.get("latitude");
                double longitude = (double) mCoordinate.get("longitude");

                LatLng mLatlng = new LatLng(latitude, longitude);

                builder.include(mLatlng);
                bounds = builder.build();

                MarkerOptions mMarkerOption = new MarkerOptions()
                        .position(mLatlng)
                        .title(timestamp)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                Marker mMarker = mMap.addMarker(mMarkerOption);
                //markerList.add(mMarker);

                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_ZOOM_LEVEL));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void startLogging() {
        Toast.makeText(this, "logging started", Toast.LENGTH_LONG).show();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        checkLocationPermission();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }
    
    @Override
    public void onConnected(Bundle bundle) {
        checkLocationPermission();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
      if(mLastLocation == null) {
            locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
      }
      else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()),
                    MAP_ZOOM_LEVEL));
      }
      //Toast.makeText(this,"CONNECTED",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) { }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
