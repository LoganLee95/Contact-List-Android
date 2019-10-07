package com.example.mycontactlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.mycontactlist.DatePickerDialog.*;
import com.google.android.material.snackbar.Snackbar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import android.text.format.DateFormat;
import java.util.Calendar;

public class ContactActivity extends AppCompatActivity implements SaveDateListener {

    private Contact currentContact;
    final static int OLD_BUILD_VERSION = 23;
    final static int PERMISSION_REQUEST_PHONE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initListButton();
        initMapButton();
        initSettingsButton();
        initToggleButton();
        initCallFunction();


        Bundle extras = getIntent().getExtras();

        if(extras != null){
            initContact(extras.getInt("contactid"));
        }else {

            currentContact = new Contact();
        }

        initBestFriendSwitch();
        setForEditing(false);
        initChangeDateButton();
        initTextChangedEvents();
        initSaveButton();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                          @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode) {
            case PERMISSION_REQUEST_PHONE: {
                if(grantResults.length > 0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ContactActivity.this, "You may now call from this app.",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ContactActivity.this,"You will not be able to make calls" +
                            "from this app.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void callContact(String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (Build.VERSION.SDK_INT >= OLD_BUILD_VERSION &&
        ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.CALL_PHONE) !=
        PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        else{
            startActivity(intent);
        }
    }

    private void initListButton() {

        ImageButton imageButtonList = (ImageButton) findViewById(R.id.imageButtonList);
        imageButtonList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }

    private void initMapButton() {

        ImageButton mapButton = (ImageButton) findViewById(R.id.imageButtonMap);
        mapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactActivity.this, ContactMapActivity2.class);
                if (currentContact.getContactID() == -1) {
                    Toast.makeText(getBaseContext(), "Contact must be saved before it can be " +
                            "mapped", Toast.LENGTH_LONG).show();

                }else{
                    intent.putExtra("contactid", currentContact.getContactID());
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }

    private void initSettingsButton() {

        ImageButton imageButtonList = (ImageButton) findViewById(R.id.imageButtonSettings);
        imageButtonList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }

    private void initToggleButton() {
        final ToggleButton editToggle = (ToggleButton) findViewById(R.id.toggleButtonEdit);
        editToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setForEditing(editToggle.isChecked());

            }
        });
    }

    private void setForEditing(boolean enabled) {
        EditText editName = (EditText) findViewById(R.id.editName);
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        EditText editState = (EditText) findViewById(R.id.editState);
        EditText editZipCode = (EditText) findViewById(R.id.editZip);
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        EditText editCell = (EditText) findViewById(R.id.editCell);
        EditText editEmail = (EditText) findViewById(R.id.editEMail);
        Button buttonChange = (Button) findViewById(R.id.btnBirthday);
        Button buttonSave = (Button) findViewById(R.id.buttonSave);
        Switch friendSwitch = findViewById (R.id.friendSwitch);

        editName.setEnabled(enabled);
        editAddress.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editState.setEnabled(enabled);
        editZipCode.setEnabled(enabled);
        editEmail.setEnabled(enabled);
        buttonChange.setEnabled(enabled);
        buttonSave.setEnabled(enabled);
        friendSwitch.setEnabled(enabled);

        if (enabled) {
            editName.requestFocus();
            editPhone.setInputType(InputType.TYPE_CLASS_PHONE);
            editCell.setInputType(InputType.TYPE_CLASS_PHONE);

        } else {
            ScrollView s = (ScrollView) findViewById(R.id.scrollView);
            s.fullScroll(ScrollView.FOCUS_UP);
            s.clearFocus();
            editPhone.setInputType(InputType.TYPE_NULL);
            editCell.setInputType(InputType.TYPE_NULL);

        }


    }


    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        TextView birthDay = (TextView) findViewById(R.id.textBirthday);
        birthDay.setText(DateFormat.format("MM/dd/yyyy", selectedTime.getTimeInMillis()).toString());
        currentContact.setBirthday(selectedTime);
    }

    private void initChangeDateButton() {
        Button changeDate = (Button) findViewById(R.id.btnBirthday);
        changeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                FragmentManager fragmentManager = getSupportFragmentManager();
                DatePickerDialog datePickerDialog = new DatePickerDialog();
                datePickerDialog.show(fragmentManager,"DatePick");
            }

        });
    }
    private void initTextChangedEvents(){
        final EditText etContactName = (EditText) findViewById(R.id.editName);
        etContactName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentContact.setContactName(etContactName.getText().toString());

            }
        });

        final EditText etStreetAddress = (EditText)findViewById(R.id.editAddress);
        etStreetAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentContact.setStreetAddress(etStreetAddress.getText().toString());

            }
        });

        final EditText etCityName = (EditText)findViewById(R.id.editCity);
        etCityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentContact.setCity(etCityName.getText().toString());

            }
        });

        final EditText etStateName = (EditText)findViewById(R.id.editState);
        etStateName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentContact.setState(etStateName.getText().toString());

            }
        });

        final EditText etZipCode = (EditText)findViewById(R.id.editZip);
        etZipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentContact.setZipCode(etZipCode.getText().toString());

            }
        });

        final EditText etHomePhone = (EditText)findViewById(R.id.editHome);
        etHomePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentContact.setPhoneNumber(etHomePhone.getText().toString());

            }
        });

        final EditText etCellPhone = (EditText)findViewById(R.id.editCell);
        etCellPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentContact.setCellNumber(etCellPhone.getText().toString());

            }
        });

        final EditText etEmailAddress = (EditText)findViewById(R.id.editEMail);
        etEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentContact.setEMail(etEmailAddress.getText().toString());

            }
        });

        etHomePhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        etCellPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }
    private void initBestFriendSwitch(){
        Switch friendSwitch = (Switch) findViewById(R.id.friendSwitch);

        friendSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(currentContact.isBestFriend() == 0){
                    currentContact.setAsBestFriend(1);
                }else
                currentContact.setAsBestFriend(0);
            }
        });

    }
    private void initContact(int id){
        ContactDataSource ds = new ContactDataSource(ContactActivity.this);
        try{
            ds.open();
            currentContact = ds.getSpecificContact(id);
            ds.close();

        } catch (Exception e) {
            Toast.makeText(this,"Load Contact Failed",Toast.LENGTH_LONG).show();
        }

        EditText editName = (EditText) findViewById(R.id.editName);
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        EditText editState = (EditText) findViewById(R.id.editState);
        EditText editZipCode = (EditText) findViewById(R.id.editZip);
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        EditText editCell = (EditText) findViewById(R.id.editCell);
        EditText editEmail = (EditText) findViewById(R.id.editEMail);
        TextView birthDay = (TextView) findViewById(R.id.textBirthday);
        Switch friendSwitch = (Switch)findViewById(R.id.friendSwitch);


        editName.setText(currentContact.getContactName());
        editAddress.setText(currentContact.getStreetAddress());
        editCity.setText(currentContact.getCity());
        editState.setText(currentContact.getState());
        editZipCode.setText(currentContact.getZipCode());
        editPhone.setText(currentContact.getPhoneNumber());
        editCell.setText(currentContact.getCellNumber());
        editEmail.setText(currentContact.getEMail());
        birthDay.setText(DateFormat.format("MM/dd/yyyy",
                currentContact.getBirthday().getTimeInMillis()).toString());

        //If the loaded contact is a a bff switch is set to on
        friendSwitch.setChecked(currentContact.isBestFriend() == 1);
    }
    private void initSaveButton(){
        Button saveButton = (Button)findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                hideKeyboard();
                boolean wasSuccess = false;
                ContactDataSource dataSource = new ContactDataSource(ContactActivity.this);
                try{
                    dataSource.open();

                    if(currentContact.getContactID() == -1) {
                        wasSuccess = dataSource.insertContact(currentContact);

                        if(wasSuccess) {
                        int newId = dataSource.getLastContactId();
                        currentContact.setContactID(newId);}

                    }else {
                        wasSuccess = dataSource.updateContact(currentContact);

                    }
                    dataSource.close();
                }catch(Exception e1){
                    wasSuccess = false;

                }

                if(wasSuccess) {
                    ToggleButton editToggle = (ToggleButton) findViewById(R.id.toggleButtonEdit);
                    editToggle.toggle();
                    setForEditing(false);
                }
            }
        });
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editName = (EditText) findViewById(R.id.editName);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        imm.hideSoftInputFromWindow(editAddress.getWindowToken(), 0);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        imm.hideSoftInputFromWindow(editCity.getWindowToken(), 0);
        EditText editState= (EditText) findViewById(R.id.editState);
        imm.hideSoftInputFromWindow(editState.getWindowToken(), 0);
        EditText editZip = (EditText) findViewById(R.id.editZip);
        imm.hideSoftInputFromWindow(editZip.getWindowToken(), 0);
        EditText editHome = (EditText) findViewById(R.id.editHome);
        imm.hideSoftInputFromWindow(editHome.getWindowToken(), 0);
        EditText editCell = (EditText) findViewById(R.id.editCell);
        imm.hideSoftInputFromWindow(editCell.getWindowToken(), 0);
        EditText editEMail = (EditText) findViewById(R.id.editEMail);
        imm.hideSoftInputFromWindow(editEMail.getWindowToken(), 0);
    }
    private void initCallFunction(){
        EditText editPhone = (EditText)findViewById(R.id.editHome);
        editPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                checkPhonePermission(currentContact.getPhoneNumber());
                return false;
            }
        });

        EditText editCell = findViewById(R.id.editCell);
        editCell.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                checkPhonePermission(currentContact.getCellNumber());
                return false;
            }
        });
    }



    private void checkPhonePermission(String phoneNumber) {
        if(Build.VERSION.SDK_INT >= OLD_BUILD_VERSION){
            if(ContextCompat.checkSelfPermission(ContactActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                if(ActivityCompat.shouldShowRequestPermissionRationale(ContactActivity.this,
                        Manifest.permission.CALL_PHONE)) {
                    Snackbar.make(findViewById(R.id.activity_contact),
                            "MyContactList requires this permission to place a call from the app.",
                            Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            ActivityCompat.requestPermissions(
                                    ContactActivity.this,
                                    new String[] {
                                            Manifest.permission.CALL_PHONE },
                                    PERMISSION_REQUEST_PHONE);
                        }
                    }).show();

                }else {
                    ActivityCompat.requestPermissions(ContactActivity.this, new
                                    String[]{Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST_PHONE);

                }
            }else {
                callContact(phoneNumber);
            }
        }else{
            callContact(phoneNumber);
        }
    }



}