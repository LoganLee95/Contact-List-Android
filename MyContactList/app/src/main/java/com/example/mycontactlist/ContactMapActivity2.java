package com.example.mycontactlist;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mycontactlist.utils.Gps_Network_Utility_Tester;
import com.example.mycontactlist.utils.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ContactMapActivity2 extends AppCompatActivity
        implements
        OnMapReadyCallback,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;


    private GoogleMap mMap;

    private ArrayList<Contact> contacts = new ArrayList<>();
    private Contact currentContact = null;

    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    TextView textDirection;
    Location mCurrentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map2);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if(accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(mySensorEventListener, accelerometer,
                    SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(mySensorEventListener, magnetometer,
                    SensorManager.SENSOR_DELAY_FASTEST);

        }else{
            Toast.makeText(this, "Sensors not found",Toast.LENGTH_LONG).show();
        }
        textDirection = (TextView)findViewById(R.id.textHeading);

        Bundle extras = getIntent().getExtras();
        try {
            ContactDataSource ds = new ContactDataSource(ContactMapActivity2.this);
            ds.open();
            if (extras != null) {
                currentContact = ds.getSpecificContact(extras.getInt("contactid"));

            } else {
                contacts = ds.getContacts("contactname", "ASC");

            }
            ds.close();

        } catch (Exception e) {
            Toast.makeText(this, "Contact(s) could not be retrieved.", Toast.LENGTH_LONG).show();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(ContactMapActivity2.this);

        getLastCurrentLocation();
        createLocationRequest();

        initListButton();
        initMapButton();
        initSettingsButton();
        initMapTypeButton();
        initGpsTesterbutton();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Toast.makeText(getBaseContext(),"Current location: " + "Lat: " + location.getLatitude() +
                                    "\nLong: " + location.getLongitude() +
                                    "\nAccuracy: " + location.getAccuracy(),
                            Toast.LENGTH_LONG).show();
                }
            }
        };

    }

    private void getLastCurrentLocation(){

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        Log.e("OnSuccess","Something went wrong, location came back null");

                            if (location != null) {
                                mCurrentLocation = location;


                        }
                    }
                });

    }


    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(25000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    protected void onResume() {
        super.onResume();
        enableMyLocation();
        startLocationUpdates();

    }

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
}

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        enableMyLocation();

        Point size = new Point();
        WindowManager w = getWindowManager();
        w.getDefaultDisplay().getSize(size);
        int measuredWidth = size.x;
        int measuredHeight = size.y;

        if (contacts.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < contacts.size(); i++) {
                currentContact = contacts.get(i);

                Geocoder geo = new Geocoder(this);
                List<Address> Addresses = null;

                String address = currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + " " +
                        currentContact.getZipCode();

                try {

                    Addresses = geo.getFromLocationName(address, 1);


                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "The connection to the Geocoder may have " +
                                    "dropped during loading. If your map is missing markers," +
                                    ", please check your contacts addresses for accuracy" +
                                    " and reopen the map.",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }


                try {

                    if (Addresses == null) {
                        throw new NullPointerException();
                    }

                    LatLng point = new LatLng(Addresses.get(0).getLatitude(), Addresses.get(0).getLongitude());


                    builder.include(point);

                    mMap.addMarker(new MarkerOptions().position(point).
                            title(currentContact.getContactName()).snippet(address));

                } catch (NullPointerException e) {
                    Toast.makeText(getBaseContext(), "The connection to the Geocoder may have " +
                                    "dropped during loading. If your map is missing markers," +
                                    ", please check your contacts addresses for accuracy" +
                                    " and reopen the map.",
                            Toast.LENGTH_LONG).show();

                }
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
                    measuredWidth, measuredHeight, 100));

        } else {

            if (currentContact != null) {

                Geocoder geo = new Geocoder(this);
                List<Address> singleContact = null;


                String address = currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + " " +
                        currentContact.getZipCode();


                try {
                    singleContact = geo.getFromLocationName(address, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "The connection to the Geocoder may have dropped." +
                                    " If your map is missing a marker, please confirm the contacts address " +
                                    "and then reopen the map.",
                            Toast.LENGTH_LONG).show();
                }

                try {

                    if (singleContact == null) {
                        throw new NullPointerException("Geocoder producing Null objects");
                    }
                    if (singleContact.size() > 0) {


                        LatLng point = new LatLng(singleContact.get(0).getLatitude(), singleContact.get(0).getLongitude());

                        mMap.addMarker(new MarkerOptions().position(point).
                                title(currentContact.getContactName()).snippet(address));

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
                    } else {
                        throw new NullPointerException("Geocoder producing Null objects");
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(getBaseContext(), "The connection to the Geocoder may have dropped." +
                                    " If your map is missing markers, please confirm the contacts address " +
                                    "and then reopen the map.",
                            Toast.LENGTH_LONG).show();

                }
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        ContactMapActivity2.this).create();
                alertDialog.setTitle("No Data");
                alertDialog.setMessage("No data is available for the mapping function.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                        "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                alertDialog.show();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }




    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getBaseContext(),"Current location: " + "Lat: " + location.getLatitude() +
                        "\nLong: " + location.getLongitude() +
                        "\nAccuracy: " + location.getAccuracy(),
                Toast.LENGTH_LONG).show();

    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            startLocationUpdates();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Updating GMS Location .....\n Hint: click on the blue dot" +
                "to see current latitude and longitude.", Toast.LENGTH_LONG).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void initListButton() {

        ImageButton imageButtonList = (ImageButton) findViewById(R.id.imageButtonList);
        imageButtonList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity2.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }

    private SensorEventListener mySensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        float[] accelerometerValues;
        float[] magneticValues;

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                accelerometerValues = event.values;}


            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                magneticValues = event.values;}

            if (accelerometerValues != null && magneticValues != null) {
                float[] R = new float[9];
                float[] I = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I,
                        accelerometerValues, magneticValues);

                if (success) {
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation);

                    float azimut = (float) Math.toDegrees(orientation[0]);
                    if (azimut < 0.0f) {
                        azimut += 360.0f;
                    }
                    String direction;
                    if (azimut >= 315 || azimut < 45) {
                        direction = "N";
                    } else if (azimut >= 225 && azimut < 315) {
                        direction = "W";
                    } else if (azimut >= 135 && azimut < 225) {
                        direction = "S";
                    } else {
                        direction = "E";
                    }
                    textDirection.setText(direction);
                }
            }

        }
    };


    private void initMapButton() {
        ImageButton mapButton = (ImageButton) findViewById(R.id.imageButtonMap);
        mapButton.setEnabled(false);
    }


    private void initSettingsButton() {

        ImageButton imageButtonList = (ImageButton) findViewById(R.id.imageButtonSettings);
        imageButtonList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity2.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }
    private void initMapTypeButton(){
        final Button satelliteBtn = (Button)findViewById(R.id.buttonMapType);
        satelliteBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String currentSetting = satelliteBtn.getText().toString();
                if(currentSetting.equalsIgnoreCase("Satellite View")) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    satelliteBtn.setText("Normal View");


                }else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    satelliteBtn.setText("Satellite View");
                }
            }
        });


    }
    private void initGpsTesterbutton() {

        final Button gpsTesterButton = findViewById(R.id.buttonLocationTester);
        gpsTesterButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(ContactMapActivity2.this, Gps_Network_Utility_Tester.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        gpsTesterButton.setVisibility(View.INVISIBLE);
    }
}