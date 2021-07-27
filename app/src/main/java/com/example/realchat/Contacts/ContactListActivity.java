package com.example.realchat.Contacts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;

import com.example.realchat.DbContacts;
import com.example.realchat.MainPageActivity;
import com.example.realchat.R;
import com.example.realchat.Utils.CountryToPhonePrefix;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactListActivity extends AppCompatActivity {

    private ArrayList<ContactModel>contactsList;
    public static ArrayList<ContactModel> appUserList ;
    private RecyclerView mrecyclerView;
    private ContactModelListAdapter mAdapter;
    private Query query;
    private ValueEventListener mQueryListener;
    private ArrayList<String>Phones;
    Set<ContactModel> set = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent =getIntent();
        Phones=new ArrayList<>();
        contactsList = new ArrayList<>();
        //appUserList = new ArrayList<>();
        mrecyclerView = findViewById(R.id.contacts_recyclerview);

        appUserList = new ArrayList<>();
        mAdapter = new ContactModelListAdapter(appUserList,ContactListActivity.this);
        mrecyclerView.setAdapter(mAdapter);
        mrecyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
//        getPermissions();
        getContactList();

//        appUserList = new ArrayList<>(set);
        Log.d("App User List",String.valueOf(appUserList.size()));

//        set.addAll(appUserList);
//        appUserList.clear();
//        appUserList.addAll(set);


        Log.d("Contact List",String.valueOf(contactsList.size()));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity(new Intent(this, MainPageActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void getContactList() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        String ISOPrefix = getCountryISO();
        while(phones.moveToNext()) {

            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNum = phoneNum .replace(" ", "");
            phoneNum  = phoneNum .replace("-", "");
            phoneNum = phoneNum .replace("(", "");
            phoneNum  = phoneNum .replace(")", "");

            if(!String.valueOf(phoneNum.charAt(0)).equals("+"))
                phoneNum = ISOPrefix + phoneNum;

            ContactModel contact = new ContactModel("",name, phoneNum);
            contactsList.add(contact);
            getUserDetails(contact);

        }
    }

    private String getCountryISO() {
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);
    }

    private void getUserDetails(ContactModel contact) {
        //Log.d("List Size",String.valueOf(appUserList.size()));
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("users"); //.child("users");
        query = mUserDB.orderByChild("phone").equalTo(contact.getMobileNumber());

        mQueryListener = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange","Called");
                if (dataSnapshot.exists()) {
                    String phone = "",
                            name = "";
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.child("phone").getValue() != null) {
                            phone = childSnapshot.child("phone").getValue().toString();
                            Log.d("Phonennnnnnnnnn", phone);
                        }
                        if (childSnapshot.child("name").getValue() != null)
                            name = childSnapshot.child("name").getValue().toString();


                        ContactModel mUser = new ContactModel(childSnapshot.getKey(), name, phone);
                        if (name.equals(phone))
                            for (ContactModel mContactIterator : contactsList) {
                                if (mContactIterator.getMobileNumber().equals(mUser.getMobileNumber())) {
                                    mUser.setName(mContactIterator.getName());
                                }
                            }

                        //int pos =appUserList.size()-1;
                        //if(pos < 0 || !(appUserList.contains( mUser)))

                            //appUserList=new ArrayList<>(set);
                         if(!Phones.contains(mUser.getMobileNumber())) {
                             appUserList.add(mUser);

                             mAdapter.notifyItemInserted(appUserList.size() - 1);
                             DbContacts db = new DbContacts(getApplicationContext());
                             db.addNewConnection(childSnapshot.getKey(), name, phone);
                             Phones.add(mUser.getMobileNumber());

                         }

                        //set.addAll(appUserList);
                       //appUserList.clear();
                        //appUserList.addAll(set);
                        //mAdapter.notifyDataSetChanged();
                        //

                        return;
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        query.addValueEventListener(mQueryListener);


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        query.removeEventListener(mQueryListener);
    }
}