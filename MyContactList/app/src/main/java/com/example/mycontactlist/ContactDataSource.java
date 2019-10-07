package com.example.mycontactlist;

import android.content.ContentValues;
import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ContactDataSource {

    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ContactDataSource(Context context) {
        dbHelper = new ContactDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

    }

    public void close() {
        dbHelper.close();
    }



    public boolean insertContact(Contact contact) {
        boolean didSucceed = false;
        try {

            ContentValues initialValues = new ContentValues();

            initialValues.put("contactname", contact.getContactName());
            initialValues.put("streetaddress", contact.getStreetAddress());
            initialValues.put("city", contact.getCity());
            initialValues.put("state", contact.getState());
            initialValues.put("zipcode", contact.getZipCode());
            initialValues.put("phonenumber", contact.getPhoneNumber());
            initialValues.put("cellnumber", contact.getCellNumber());
            initialValues.put("email", contact.getEMail());
            initialValues.put("birthday", String.valueOf(contact.getBirthday().getTimeInMillis()));
            initialValues.put("bff", contact.isBestFriend());

            if (contact.getPicture() != null){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                contact.getPicture().compress(Bitmap.CompressFormat.PNG,100,baos);
                byte[]photo = baos.toByteArray();
                initialValues.put("contactphoto", photo);
            }

            didSucceed = database.insert("contact", null, initialValues) > 0;

        } catch (Exception ex) {

            System.out.println("Insert Contact Error");

        }
        return didSucceed;
    }

    public boolean updateContact(Contact contact) {
        boolean didSucceed = false;
        try {

            Long rowId = (long) contact.getContactID();
            ContentValues updateValues = new ContentValues();

            updateValues.put("contactname", contact.getContactName());
            updateValues.put("streetaddress", contact.getStreetAddress());
            updateValues.put("city", contact.getCity());
            updateValues.put("state", contact.getState());
            updateValues.put("zipcode", contact.getZipCode());
            updateValues.put("phonenumber", contact.getPhoneNumber());
            updateValues.put("cellnumber", contact.getCellNumber());
            updateValues.put("email", contact.getEMail());
            updateValues.put("birthday", String.valueOf(contact.getBirthday().getTimeInMillis()));
            updateValues.put("bff", contact.isBestFriend());

            if (contact.getPicture() != null){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                contact.getPicture().compress(Bitmap.CompressFormat.PNG,100,baos);
                byte[]photo = baos.toByteArray();
                updateValues.put("contactphoto", photo);
            }


            didSucceed = database.update("contact", updateValues, "_id=" + rowId, null) > 0;

        } catch (Exception ex) {


        }
        return didSucceed;
    }

    public int getLastContactId() {
        int lastId = -1;
        try {
            String query = "Select MAX(_id) from contact";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();

        } catch (Exception e) {
            lastId = -1;
        }
        return lastId;
    }

    /*
    Exercise 2
     */
    public boolean updateAddress(ContactAddress contactAddress) {

        boolean didSucceed = false;

        Long rowId = (long) contactAddress.getContactID();
        ContentValues updateValues = new ContentValues();

        try {

            updateValues.put("streetaddress", contactAddress.getStreetAddress());
            updateValues.put("city", contactAddress.getCity());
            updateValues.put("state", contactAddress.getState());
            updateValues.put("zipcode", contactAddress.getZipCode());

            didSucceed = database.update("contact", updateValues, "_id=" + rowId, null) > 0;


        } catch (Exception e3) {
            e3.printStackTrace();
        }

        return didSucceed;
    }
    public ArrayList<String> getContactName() {

        ArrayList<String> contactNames = new ArrayList<>();

        try {
            String query = "Select contactname from contact";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                contactNames.add(cursor.getString(0));
                cursor.moveToNext();

            }
            cursor.close();

        } catch (Exception ex) {
            contactNames = new ArrayList<String>();
        }

        return contactNames;
    }

    public ArrayList<Contact> getContacts(String sortField, String sortOrder) {
        ArrayList<Contact> contacts = new ArrayList<>();

        try {
            String query = "SELECT * FROM contact ORDER BY " + sortField + " " + sortOrder;

            Cursor cursor = database.rawQuery(query, null);

            Contact newContact;
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                newContact = new Contact();
                newContact.setContactID(cursor.getInt(0));
                newContact.setContactName(cursor.getString(1));
                newContact.setStreetAddress(cursor.getString(2));
                newContact.setCity(cursor.getString(3));
                newContact.setState(cursor.getString(4));
                newContact.setZipCode(cursor.getString(5));
                newContact.setPhoneNumber(cursor.getString(6));
                newContact.setCellNumber(cursor.getString(7));
                newContact.setEMail(cursor.getString(8));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
                newContact.setBirthday(calendar);

               if(cursor.getInt(10) > 0){
                   newContact.setAsBestFriend(1);
               }

                contacts.add(newContact);
                cursor.moveToNext();
            }
            cursor.close();


        } catch (Exception e) {
            contacts = new ArrayList<Contact>();

        }
        return contacts;

    }
    public Contact getSpecificContact(int contactId){
        Contact contact = new Contact();
        String query = "SELECT * FROM contact WHERE _id =" + contactId;
        Cursor cursor = database.rawQuery(query,null);

        if (cursor.moveToFirst()) {

            contact.setContactID(cursor.getInt(0));
            contact.setContactName(cursor.getString(1));
            contact.setStreetAddress(cursor.getString(2));
            contact.setCity(cursor.getString(3));
            contact.setState(cursor.getString(4));
            contact.setZipCode(cursor.getString(5));
            contact.setPhoneNumber(cursor.getString(6));
            contact.setCellNumber(cursor.getString(7));
            contact.setEMail(cursor.getString(8));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
            if(cursor.getInt(10) > 0){
                contact.setAsBestFriend(1);
            }
            byte[]photo = cursor.getBlob(11);
            if(photo != null){
                ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
                Bitmap tempPicture = BitmapFactory.decodeStream(imageStream);
                contact.setPicture(tempPicture);
            }

            contact.setBirthday(calendar);
            cursor.close();

        }

            return contact;

        }
    public boolean deleteContact(int contactId) {
        boolean didDelete = false;
        try {
            didDelete = database.delete("contact", "_id=" + contactId, null) > 0;
        } catch (Exception e) {
            //Do nothing -return value already set to false
        }
        return didDelete;
    }

    }
