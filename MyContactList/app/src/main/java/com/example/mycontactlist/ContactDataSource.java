package com.example.mycontactlist;

import android.content.ContentValues;
import android.content.Context;
import java.sql.SQLException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ContactDataSource {

    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ContactDataSource(Context context){
        dbHelper = new ContactDBHelper(context);
    }
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

    }
    public void close(){
        dbHelper.close();
    }
    public boolean insertContact(Contact contact) {
        boolean didSucceed = false;
        try {

            ContentValues initialValues = new ContentValues();

            initialValues.put("contactname",contact.getContactName());
            initialValues.put("streetaddress",contact.getStreetAddress());
            initialValues.put("city",contact.getCity());
            initialValues.put("state",contact.getState());
            initialValues.put("zipcode",contact.getZipCode());
            initialValues.put("phonenumber",contact.getPhoneNumber());
            initialValues.put("cellnumber",contact.getCellNumber());
            initialValues.put("email",contact.getEMail());
            initialValues.put("birthday",String.valueOf(contact.getBirthday().getTimeInMillis()));

            didSucceed = database.insert("contact",null,initialValues) > 0;

        } catch (Exception ex) {

        }
        return didSucceed;
    }
    public boolean updateContact(Contact contact){
        boolean didSucceed = false;
        try{

            Long rowId = (long)contact.getContactID();
            ContentValues updateValues = new ContentValues();

            updateValues.put("contactname",contact.getContactName());
            updateValues.put("streetaddress",contact.getStreetAddress());
            updateValues.put("city",contact.getCity());
            updateValues.put("state",contact.getState());
            updateValues.put("zipcode",contact.getZipCode());
            updateValues.put("phonenumber",contact.getPhoneNumber());
            updateValues.put("cellnumber",contact.getCellNumber());
            updateValues.put("email",contact.getEMail());
            updateValues.put("birthday",
                    String.valueOf(contact.getBirthday().getTimeInMillis()));

            didSucceed = database.update("contact",updateValues, "_id=" + rowId, null) > 0;

        }catch (Exception ex){

        }



        return didSucceed;
    }
    public int getLastContactId(){
        int lastId = -1;
        try {
            String query = "Select MAX(_id) from contact";
            Cursor cursor = database.rawQuery(query,null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();

        }catch (Exception e2){
            lastId = -1;
        }
        return lastId;
    }


}
