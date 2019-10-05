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
import androidx.core.content.ContextCompat;

import com.example.mycontactlist.Utils.PermissionUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ContactMapActivity2 extends AppCompatActivity
        implements
        OnMapReadyCallback,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener {

    private GoogleMap mMap;
    private final int PERMISSION_REQUEST_LOCATION_CODE = 1;
    private Boolean mPermissionDenied = false;

    private ArrayList<Contact> contacts = new ArrayList<>();
    private Contact currentContact = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_map2);
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

            initListButton();
            initMapButton();
            initSettingsButton();
            initMapTypeButton();

        }

    public void addMarkersToMap() {

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
                Address tempAddress;


                String address = currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + " " +
                        currentContact.getZipCode();

                try {

                    Addresses = geo.getFromLocationName(address, 1);


                } catch (IOException e) {
                    e.printStackTrace();

                }


                try{

                    if(Addresses == null){
                        throw new NullPointerException();
                    }
                    tempAddress = Addresses.get(0);

                    LatLng point = new LatLng(tempAddress.getLatitude(),tempAddress.getLongitude());

                    builder.include(point);

                    mMap.addMarker(new MarkerOptions().position(point).
                            title(currentContact.getContactName()).snippet(address));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),
                            measuredWidth, measuredHeight, 450));

                }catch(NullPointerException e){
                        Toast.makeText(getBaseContext()," Geocoder is unable to connect, please try again later :(",
                                Toast.LENGTH_LONG).show();

                }

                }




        } else {
            if (currentContact != null) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                Geocoder geo = new Geocoder(this);
                Address tempAddress;
                List<Address> Addresses = null;


                String address = currentContact.getStreetAddress() + ", " +
                        currentContact.getCity() + ", " +
                        currentContact.getState() + " " +
                        currentContact.getZipCode();


                try {
                    Addresses = geo.getFromLocationName(address, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try{

                    if(Addresses == null){
                        throw new NullPointerException();
                    }
                    tempAddress = Addresses.get(0);

                    LatLng point = new LatLng(tempAddress.getLatitude(),tempAddress.getLongitude());

                    builder.include(point);

                    mMap.addMarker(new MarkerOptions().position(point).
                            title(currentContact.getContactName()).snippet(address));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
                }catch(NullPointerException e){
                    Toast.makeText(getBaseContext()," Geocoder is unable to connect, please try again later :(",
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();
        addMarkersToMap();
        }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, PERMISSION_REQUEST_LOCATION_CODE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,true);

        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }

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
        if (requestCode != PERMISSION_REQUEST_LOCATION_CODE) {
            return;

            }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
        }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onMyLocationButtonClick() {

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


}