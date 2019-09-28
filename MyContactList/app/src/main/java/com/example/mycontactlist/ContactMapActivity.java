package com.example.mycontactlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;

public class ContactMapActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener gpsListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map);
        initListButton();
        initMapButton();
        initSettingsButton();
        initGetLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        try{
            locationManager.removeUpdates(gpsListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void initGetLocation(){

        Button locationButton = (Button)findViewById(R.id.buttonGetLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText editAddress = (EditText) findViewById(R.id.editAddress);
                EditText editCity = (EditText) findViewById(R.id.editCity);
                EditText editState = (EditText) findViewById(R.id.editState);
                EditText editZipCode = (EditText) findViewById(R.id.editZip);

                String address = editAddress.getText().toString() + ", " +
                        editCity.getText().toString() + ", " +
                        editState.getText().toString() + ", " +
                        editZipCode.getText().toString();

                List<Address> addresses = null;
                Geocoder geo = new Geocoder(ContactMapActivity.this);
                try {
                    locationManager = (LocationManager) getBaseContext().
                            getSystemService(Context.LOCATION_SERVICE);

                    gpsListener = new LocationListener() {
                        public void onLocationChanged(Location location) {

                            TextView txtLatitude = (TextView) findViewById(R.id.textLatitude);
                            TextView txtLongitude = (TextView) findViewById(R.id.textLongitude);
                            TextView txtAccuracy = (TextView) findViewById(R.id.textAccuracy);

                            txtLatitude.setText(String.valueOf(location.getLatitude()));
                            txtLongitude.setText(String.valueOf(location.getLongitude()));
                            txtAccuracy.setText(String.valueOf(location.getAccuracy()));

                        }

                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onProviderDisabled(String provider) {
                        }


                    };

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 0, 0, gpsListener);


                } catch (Exception e){
                    Toast.makeText(getBaseContext(), "Error, Location not available",
                            Toast.LENGTH_LONG).show();
                }

            }
        }
    }


    private void initListButton() {

        ImageButton imageButtonList = (ImageButton) findViewById(R.id.imageButtonList);
        imageButtonList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }


    private void initMapButton() {
            ImageButton mapButton = (ImageButton) findViewById(R.id.imageButtonMap);
            mapButton.setEnabled(false);
        }


    private void initSettingsButton() {

        ImageButton imageButtonList = (ImageButton) findViewById(R.id.imageButtonSettings);
        imageButtonList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactMapActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }
}

