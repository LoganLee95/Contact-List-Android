package com.example.mycontactlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;


public class ContactSettingsActivity extends AppCompatActivity {

    private ScrollView scrollViewObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_settings);
        scrollViewObject = (ScrollView)findViewById(R.id.scrollView);
        initListButton();
        initMapButton();
        initSettingsButton();
        initSettings();
        initSortByClick();
        initSortOrderClick();
        initColorChooser();
    }

    private void initSortByClick(){
        RadioGroup rgSortBy = (RadioGroup) findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
                RadioButton rbCity = (RadioButton) findViewById(R.id.radioCity);

                if (rbName.isChecked()) {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "contactname").commit();
                } else if (rbCity.isChecked()) {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "city").commit();
                } else {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "birthday").commit();
                }
            }

        });
    }
    private void initSortOrderClick(){
        RadioGroup rgSortOrder = (RadioGroup) findViewById(R.id.radioGroupSortOrder);
        rgSortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rbAscending = (RadioButton)findViewById(R.id.radioAscending);
                if (rbAscending.isChecked()){
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortorder","ASC").commit();
                }else {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortorder","DESC").commit();

                }

            }
        });
    }
    //Exercise 1 start
    private void initColorChooser(){

        RadioGroup rgColorChooser = (RadioGroup)findViewById(R.id.radioGroupColorChooser);
        rgColorChooser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rbNormal = (RadioButton)findViewById(R.id.radioNormal);
                RadioButton rbYellow = (RadioButton)findViewById(R.id.radioYellow);
                RadioButton rbBlue = (RadioButton)findViewById(R.id.radioBlue);


                if(rbNormal.isChecked()){
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("colorchoice","standard").commit();
                    scrollViewObject.setBackgroundResource(R.color.standard_background);
                }else if(rbYellow.isChecked()){
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("colorchoice","yellow").commit();
                    scrollViewObject.setBackgroundResource(R.color.settings_background_1);

                }
                else if(rbBlue.isChecked()){
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("colorchoice","blue").commit();
                    scrollViewObject.setBackgroundResource(R.color.settings_background_3);

                }
                else {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("colorchoice","green").commit();
                    scrollViewObject.setBackgroundResource(R.color.settings_background_2);
                }


            }
        });
    }

    private void initSettings(){
        String sortBy = getSharedPreferences("MyContactListPreferences",
        Context.MODE_PRIVATE).getString("sortfield","contactname");

        String sortOrder = getSharedPreferences("MyContactListPreferences",
        Context.MODE_PRIVATE).getString("sortorder","ASC");

        String colorChooser = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("colorchoice","standard");

        RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
        RadioButton rbCity = (RadioButton) findViewById(R.id.radioCity);
        RadioButton rbBirthday = (RadioButton) findViewById(R.id.radioBirthday);

        if(sortBy.equalsIgnoreCase("contactname")) {
            rbName.setChecked(true);
        }else if(sortBy.equalsIgnoreCase("city")) {
            rbCity.setChecked(true);
        }else {
            rbBirthday.setChecked(true);
        }

        RadioButton rbAscending = (RadioButton) findViewById(R.id.radioAscending);
        RadioButton rbDescending = (RadioButton) findViewById(R.id.radioDescending);

        if(sortOrder.equalsIgnoreCase(("ASC"))) {
            rbAscending.setChecked(true);

        }else {
            rbDescending.setChecked(true);}
        /* Exercise 1. */
        RadioButton rbNormal = (RadioButton)findViewById(R.id.radioNormal);
        RadioButton rbYellow = (RadioButton)findViewById(R.id.radioYellow);
        RadioButton rbGreen = (RadioButton)findViewById(R.id.radioGreen);
        RadioButton rbBlue = (RadioButton)findViewById(R.id.radioBlue);


        if(colorChooser.equalsIgnoreCase("standard")){
            rbNormal.setChecked(true);
            scrollViewObject.setBackgroundResource(R.color.standard_background);

        }else if(colorChooser.equalsIgnoreCase("yellow")){
            rbYellow.setChecked(true);
            scrollViewObject.setBackgroundResource(R.color.settings_background_1);

        }else if(colorChooser.equalsIgnoreCase("green")){
        rbGreen.setChecked(true);
        scrollViewObject.setBackgroundResource(R.color.settings_background_2);

        }else{
            rbBlue.setChecked(true);
            scrollViewObject.setBackgroundResource(R.color.settings_background_3);
        }
    }
    private void initListButton() {

        ImageButton imageButtonList = (ImageButton) findViewById(R.id.imageButtonList);
        imageButtonList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactListActivity.class);
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
                Intent intent = new Intent(ContactSettingsActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }

    private void initSettingsButton() {

        ImageButton iButtonSettings = (ImageButton) findViewById(R.id.imageButtonSettings);
        iButtonSettings.setEnabled(false);

    }
}