package com.example.mycontactlist.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mycontactlist.ContactListActivity;
import com.example.mycontactlist.ContactSettingsActivity;
import com.example.mycontactlist.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;



/*  Exercises from chapter 7:
1. Create a layout that displays the latitude, longitude, and accuracy for the network sensor and for the GPS sensor.
 Add a listener for each and have it display its reported location in the appropriate onscreen widget. Run it on a
 device. Walk around with the app open to this screen and observe the differences.

 2. Modify the layout in Exercise 1 to have a third set of latitude, longitude, and accuracy labeled
 best location. Code a method to test for the best location and put the values in these widgets.
 Run the app and again observe the results.
 */

public class Gps_Network_Utility_Tester extends AppCompatActivity implements
                com.google.android.gms.location.LocationListener,
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener,
                OnMapReadyCallback {


    final int PERMISSION_REQUEST_LOCATION = 101;
    GoogleMap gMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    LocationManager locationManager;
    LocationListener gpsListener;
    LocationListener networkListener;
//    FusedLocationProviderClient fLpC= LocationServices.getFusedLocationProviderClient(Gps_Network_Utility_Tester.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_gps_utility);


        initListButton();
        initMapButton();
        initSettingsButton();
        initGetLocationButton();


        createLocationRequest();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();}


    }
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getBaseContext(), "Lat: " + location.getLatitude() +
                        " Long: " + location.getLongitude() +
                        " Accuracy: " + location.getAccuracy(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(gpsListener);
            locationManager.removeUpdates(networkListener);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void initGetLocationButton() {
        Button locationButton = (Button) findViewById(R.id.buttonGetLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    locationManager = (LocationManager) getBaseContext().
                            getSystemService(Context.LOCATION_SERVICE);

                    gpsListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            TextView txtLatitude = (TextView) findViewById(R.id.textLatitude);
                            TextView txtLongitude = (TextView) findViewById(R.id.textLongitude);
                            TextView txtAccuracy = (TextView) findViewById(R.id.textAccuracy);

                            txtLatitude.setText(String.valueOf(location.getLatitude()));
                            txtLongitude.setText(String.valueOf(location.getLongitude()));
                            txtAccuracy.setText(String.valueOf(location.getAccuracy()));


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
                    };

                    EditText editAddress = (EditText) findViewById(R.id.editAddress);
                    EditText editCity = (EditText) findViewById(R.id.editCity);
                    EditText editState = (EditText) findViewById(R.id.editState);
                    EditText editZipCode = (EditText) findViewById(R.id.editZip);

                    if (ContextCompat.checkSelfPermission(Gps_Network_Utility_Tester.this,Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,networkListener);

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Error, Location not available ",
                            Toast.LENGTH_LONG).show();
                }


            }
        });
    }


    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Gps_Network_Utility_Tester.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonMap);
        ibList.setEnabled(false);
    }

    private void initSettingsButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Gps_Network_Utility_Tester.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}


/*  private void enableMyLocation() {

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(ContactMapActivity2.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ContactMapActivity2.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        Snackbar.make(findViewById(R.id.activity_contact_map),
                                "MyContactList requires this permission to locate your contacts",
                                Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                ActivityCompat.requestPermissions(ContactMapActivity2.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_LOCATION);
                            }
                        })
                                .show();
                    } else {
                        ActivityCompat.requestPermissions(ContactMapActivity2.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_LOCATION); }
                } else {
                    mMap.setMyLocationEnabled(true);
                }
            }else{
                mMap.setMyLocationEnabled(true);
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error requesting permission", Toast.LENGTH_LONG).show();


        }
    }



    @Override
    public void onRequestPermissionsResult (int requestCode,
                                            @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                enableMyLocation();

            } else {
                Toast.makeText(ContactMapActivity2.this, "MyContactList will not locate  your contacts.", Toast.LENGTH_LONG).show();
            }
        }
    }*/