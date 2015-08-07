package com.example.vynv.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.vynv.myapplication.adapter.ContactPhoneAdapter;
import com.example.vynv.myapplication.objects.ContactPhone;

import java.util.ArrayList;


public class ContactPhoneActivity extends Activity {

    ListView listView;
    Cursor cursor;
    ArrayList<ContactPhone> contactPhones;
    ContactPhoneAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_phone);
        listView = (ListView) findViewById(R.id.lsContact);
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        startManagingCursor(cursor);
        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone._ID};
        if(cursor.moveToFirst()) {
            contactPhones = new ArrayList<>();
            while(cursor.moveToNext()) {
                ContactPhone contactPhone = new ContactPhone();
                contactPhone.setPhoneID_(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)));
                contactPhone.setPhoneNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                contactPhone.setPhoneNamePeople(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                contactPhones.add(contactPhone);
            }
        }
       mAdapter=new ContactPhoneAdapter(getApplicationContext(),contactPhones);
        listView.setAdapter(mAdapter);
      mAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("number_phone",contactPhones.get(position).getPhoneNumber());
                startActivity(intent);
                finish();
            }
        });
    }

}
