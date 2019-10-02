package com.example.mycontactlist;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class ContactMapActivity2 extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,com.google.android.gms.location.LocationListener {


    private GoogleMap mMap;
    final int PERMISSION_REQUEST_LOCATION = 101;

    ArrayList<Contact> contacts = new ArrayList<>();
    Contact currentContact = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map2);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

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

            initListButton();
            initMapButton();
            initSettingsButton();
            initMapTypeButton();

        }


    @Override
    public void onPause() {
        super.onPause();

    }
    /**
     *Triggered from permission request dialog.
     * @param requestCode value sent to request with Permission Request Location
     * @param permissions String value permissions
     * @param grantResults int result to be granted
     */

    @Override
    public void onRequestPermissionsResult (int requestCode,
                                            @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);

            } else {
                Snackbar.make(findViewById(R.id.activity_contact),
                        "MyContactList requires this permission to locate " +
                                "your contacts", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                ActivityCompat.requestPermissions(
                                        ContactMapActivity2.this,
                                        new String[]{
                                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_LOCATION);
                            }
                        })
                        .show();

            }
        }
    }






    @Override
    public void onMyLocationClick(@NonNull Location location) {

        Toast.makeText(getBaseContext(), "Lat: " +
                        location.getLatitude() +
                        " Long: " + location.getLongitude() +
                        " Accuracy: " + location.getAccuracy(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {

            Snackbar.make(findViewById(R.id.activity_contact),
                    "MyContactList requires this permission to locate " +
                            "your contacts", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            ActivityCompat.requestPermissions(
                                    ContactMapActivity2.this,
                                    new String[]{
                                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSION_REQUEST_LOCATION);
                        }
                    })
                    .show();


        }
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

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
                List<Address> addresses = null;

                String address = currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + " " +
                        currentContact.getZipCode();

                String phoneNum = currentContact.getPhoneNumber();

                try {
                    addresses = geo.getFromLocationName(address, 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(addresses != null){
                LatLng point = new LatLng(addresses.get(0).getLatitude(),
                        addresses.get(0).getLongitude());
                builder.include(point);

                mMap.addMarker(new MarkerOptions().position(point).
                        title(currentContact.getContactName()).snippet(address + "\n" +
                        " " + phoneNum).icon(BitmapDescriptorFactory.fromResource(R.mipmap.custom_map_pin)));



                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
                        measuredWidth, measuredHeight, 450));}
            }


        }else {
            if(currentContact != null) {
                Geocoder geo = new Geocoder(this);
                List<Address> addresses = null;

                String address = currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + " " +
                        currentContact.getZipCode();

                String phoneNum = currentContact.getPhoneNumber();

                try {
                    addresses = geo.getFromLocationName(address, 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(addresses != null) {
                    LatLng point = new
                            LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.imagename)
                    // changes the marker icon to custom image

                    mMap.addMarker(new MarkerOptions().position(point).
                            title(currentContact.getContactName()).snippet(address + "\n " +
                            " " + phoneNum).icon(BitmapDescriptorFactory.fromResource(R.mipmap.custom_map_pin)));//Displays the address under the pin (Marker)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 100));

                }
            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        ContactMapActivity2.this).create();
                alertDialog.setTitle("No Data");
                alertDialog.setMessage("No data is available for the mapping function.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                        "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }});
                alertDialog.show();
            }





}
    }


    @Override
    public void onLocationChanged(Location location) {
            Toast.makeText(getBaseContext(), "Lat: " +
                            location.getLatitude() +
                            " Long: " + location.getLongitude() +
                            " Accuracy: " + location.getAccuracy(),
                    Toast.LENGTH_LONG).show();

        }
}
