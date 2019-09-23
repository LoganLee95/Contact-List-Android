package com.example.mycontactlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private ArrayList<Contact> items;
    private Context adapterContext;

    public ContactAdapter(Context context, ArrayList<Contact> items){
        super(context, R.layout.list_item,items);
        adapterContext = context;
        this.items = items;
    }
    @Override
    public View getView(int position,View convertView, ViewGroup parent){
        View v = convertView;

        try {
            Contact contact = items.get(position);

            if (v ==  null){
                LayoutInflater vi = (LayoutInflater)
                        adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            TextView contactName = (TextView) v.findViewById(R.id.textContactName);
            contactName.setTextColor(parent.getResources().getColor(R.color.system_red));
            TextView contactNumber = (TextView) v.findViewById(R.id.textPhoneNumber);
            TextView cellNumber = (TextView) v.findViewById(R.id.textCellNumber);
            ImageView bestFriendLabel  = (ImageView)v.findViewById(R.id.starImage);
            Button b = (Button) v.findViewById(R.id.buttonDeleteContact);
            contactName.setText(contact.getContactName());
            contactNumber.setText(contact.getPhoneNumber());
            cellNumber.setText(contact.getCellNumber());
            b.setVisibility(View.INVISIBLE);

            if(contact.getCity() != null) {
                if (contact.getCity().equals("Tucker")) {
                    contact.setBestFriend(true);
                }
            }

            if(contact.isBestFriend()){
                bestFriendLabel.setVisibility(View.VISIBLE);
            }else {
                bestFriendLabel.setVisibility(View.INVISIBLE);
            }



        }catch(Exception e3){
            e3.printStackTrace();
            e3.getCause();
        }
        return v;
    }
    public void showDelete(final int position, final View convertView,
                           final Context context, final Contact contact){
        View v = convertView;
        final Button b = (Button) v.findViewById(R.id.buttonDeleteContact);
        if(b.getVisibility() == View.INVISIBLE) {
            b.setVisibility(View.VISIBLE);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideDelete(position,convertView,context);
                    items.remove(contact);
                    deleteOption(contact.getContactID(),context);


                }
            });
        }else{
            hideDelete(position,convertView,context);
        }

    }
    private void deleteOption(int contactToDelete, Context context){
        ContactDataSource db = new ContactDataSource(context);
        try{
            db.open();
            db.deleteContact(contactToDelete);
            db.close();
        }catch (Exception e){
            Toast.makeText(adapterContext,"Delete contact failed",Toast.LENGTH_LONG).show();
        }
        this.notifyDataSetChanged();

    }
    public void hideDelete(int position,View convertView, Context context) {
        View v = convertView;
        final Button b = (Button) v.findViewById(R.id.buttonDeleteContact);
        b.setVisibility(View.INVISIBLE);
        b.setOnClickListener(null);
    }
}