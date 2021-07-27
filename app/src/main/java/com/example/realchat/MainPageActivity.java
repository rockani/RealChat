package com.example.realchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realchat.Chat.CreateChatActivity;
import com.example.realchat.Contacts.ContactListActivity;
import com.example.realchat.User.ConnectionModel;
import com.example.realchat.User.ConnectionsListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {


    private RecyclerView mChatList;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;
    private ArrayList<UserModel> ConnectionsList=new ArrayList<>();
    private FloatingActionButton mContact;
    FirebaseUser currUser;
    private ConnectionsListAdapter mConnectionsListAdapter;
    private  RecyclerView recyclerView;
    private DbHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connections_list);
        Intent intent =getIntent();


        getPermissions();

        recyclerView = findViewById(R.id.Recyclerview);
        getConnectionsList();
        mConnectionsListAdapter = new ConnectionsListAdapter(ConnectionsList,MainPageActivity.this);
        recyclerView.setAdapter(mConnectionsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainPageActivity.this));


        mContact = findViewById(R.id.getContacts);
        currUser = FirebaseAuth.getInstance().getCurrentUser();
//        mLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
//                return;
//            }
//        });

        mContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ContactListActivity.class));
                finish();
            }
        });

        //mChatList = findViewById(R.id.recyclerview);
        //mChatListAdapter = new UserListAdapter(UserList,getApplicationContext());

    }

    private void getConnectionsList() {
        dbHandler = new DbHandler(MainPageActivity.this);
        ConnectionsList = dbHandler.readCourses();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
        return true;
    }
    private void  getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
    }

}