package com.example.mycontactlist.Utils;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;



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

    LocationManager locationManager;
    LocationListener gpsListener;
    LocationListener networkListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_gps_utility);
        initListButton();
        initMapButton();
        initSettingsButton();
        initGetLocationButton();


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
    public void onLocationChanged(Location location) {

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
