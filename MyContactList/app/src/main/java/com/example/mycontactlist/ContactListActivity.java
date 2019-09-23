package com.example.mycontactlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/*
1. Add the contact’s cell phone number to the complex list.
 The list should display the contact name on the first line
 and Home: the number Cell:the number on the second line.

2. Find a small star shaped graphic and add it to the
layout if the contact is a “Best Friend Forever.”

3. Add another line to the list so that the list displays:
Contact Name
Street Address
City, State, Zip,
Phone number

4. Modify the custom adapter to alternately display the contact name in red and blue.
 For example, the first name in the list will be red, the second will be blue, the third is red, and so on.
 */

public class ContactListActivity extends AppCompatActivity {

    boolean isDeleting = false;
    ContactAdapter adapter;
    ArrayList<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initListButton();
        initMapButton();
        initSettingsButton();
        initItemClick();
        initAddContactButton();
        initDeleteButton();
    }

    @Override
    public void onResume() {

        super.onResume();

        String sortBy = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortfield", "contactname");

        String sortOrder = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortorder","ASC");


        ContactDataSource ds = new ContactDataSource(this);

        try {
            ds.open();
            contacts = ds.getContacts(sortBy,sortOrder);
            ds.close();

            if(contacts.size() > 0){
                ListView listview = (ListView) findViewById(R.id.lvContacts);
                adapter = new ContactAdapter(this,contacts);
                listview.setAdapter(adapter);
            }else{
                Intent intent = new Intent(ContactListActivity.this,ContactActivity.class);
                startActivity(intent);
            }

        }catch (Exception e){
            Toast.makeText(this,"Error retrieving contacts",Toast.LENGTH_LONG).show();

        }
    }
    private void initListButton() {

        ImageButton imageButtonList = (ImageButton) findViewById(R.id.imageButtonList);
        imageButtonList.setEnabled(false);
    }

    private void initMapButton() {

        ImageButton mapButton = (ImageButton) findViewById(R.id.imageButtonMap);
        mapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactListActivity.this, ContactMapActivity.class);
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
                Intent intent = new Intent(ContactListActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
    }
    private void initItemClick() {
        ListView listview = (ListView)findViewById(R.id.lvContacts);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Contact selectedContact = contacts.get(position);
                if(isDeleting){
                    adapter.showDelete(position,itemClicked,ContactListActivity.this,selectedContact);
                }else {

                    Intent intent = new Intent(ContactListActivity.this, ContactActivity.class);
                    intent.putExtra("contactid", selectedContact.getContactID());
                    startActivity(intent);
                }

            }
        });
    }
    private void initAddContactButton(){
        Button newContact = (Button)findViewById(R.id.buttonAdd);
        newContact.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this,ContactActivity.class);
                startActivity(intent);

            }
        });
    }
    private void initDeleteButton(){
        final Button deleteButton = (Button)findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(isDeleting){
                    deleteButton.setText("Delete");
                    isDeleting = false;
                    adapter.notifyDataSetChanged();

                }else{
                    deleteButton.setText("Done Deleting");
                    isDeleting = true;
                }
            }
        });
    }


}