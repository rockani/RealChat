package com.example.realchat.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.realchat.Contacts.ContactListActivity;
import com.example.realchat.Contacts.ContactModel;
import com.example.realchat.DbContacts;
import com.example.realchat.DbHandler;
import com.example.realchat.MainActivity;
import com.example.realchat.MainPageActivity;
import com.example.realchat.R;
import com.example.realchat.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.icu.text.MessagePattern.ArgType.SELECT;

public class CreateChatActivity extends AppCompatActivity  {

    private static final int PICKFILE_REQUEST_CODE =1 ;
    ImageButton send,attachFile; EditText message;
    String userId;
    RecyclerView mRecyclerView; MessageListAdapter mAdapter;
    ArrayList<MessageModel>MessageList;
    FirebaseUser current=FirebaseAuth.getInstance().getCurrentUser();
    ValueEventListener listener;
    DatabaseReference ref;
    private ImageView imageView;
    DatabaseReference Ref;
    //a Uri object to store file path
    private Uri filePath;
    Button upload;
    private DbHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);
        send = findViewById(R.id.btn_send_data);
        attachFile = findViewById(R.id.attach);
        message = findViewById(R.id.edit_message);
        Intent intent = getIntent();
        userId = intent.getStringExtra("User ID");
        Toast.makeText(this,userId,Toast.LENGTH_SHORT).show();
        mRecyclerView = findViewById(R.id.recycler_content);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        upload=findViewById(R.id.button);

        MessageList = new ArrayList<MessageModel>();
        //showpreviouschat();
        getMessageList();
        //ref.removeEventListener(listener);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getText().toString()!=null){
                    createUniqueChat();
                    message.setText("");
                }
            }
        });

        attachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFile();
            }
        });
       upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });


    }

    private void getMessageList() {

        FirebaseDatabase mUserDB = FirebaseDatabase.getInstance();
        ref = mUserDB.getReference().child("chats").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()+userId);
        Ref = mUserDB.getReference().child("chats").child(userId.toString()+current.getUid());



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MessageList.clear();
                for (DataSnapshot childsnapshot : snapshot.getChildren()) {
                    MessageModel messageModel =
                            new MessageModel( childsnapshot.child("Message").getValue().toString(),
                                    childsnapshot.child("Sender Id").getValue().toString(),
                                    childsnapshot.child("Time").getValue().toString()
                            );
                    MessageList.add(messageModel);
                    //mAdapter.notifyItemInserted(MessageList.size()-1);

                }
                mAdapter = new MessageListAdapter(MessageList, CreateChatActivity.this);

                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(MessageList.size()-1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Ref.addValueEventListener(listener);

    }

//    private void showpreviouschat() {
//        mAdapter = new MessageListAdapter(MessageList,getApplicationContext());
//        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//    }


    private void createUniqueChat(){
        FirebaseDatabase mUserDB = FirebaseDatabase.getInstance();
        DatabaseReference mref = ref.push();
        DatabaseReference mRef = Ref.push();
       // String key = mref.getKey();
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("Message",message.getText().toString());
        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        userMap.put("Time",stamp.toString());
        userMap.put("Sender Id",current.getUid());

        mref.setValue(userMap);
        mRef.setValue(userMap);
        //MessageList.add(new MessageModel(message.getText().toString(),current.getUid(),stamp.toString()));
//        mAdapter.notifyItemInserted(MessageList.size()-1);
        dbHandler = new DbHandler(CreateChatActivity.this);
        DbContacts dbContacts = new DbContacts(CreateChatActivity.this);
        String name="",phoneNum="";
        for(ContactModel contact: ContactListActivity.appUserList){
            if(contact.getUid().equals(userId)){
                name=contact.getName();
                phoneNum = contact.getMobileNumber();
                break;
            }
        }
        Log.d("Check",userId+name+phoneNum);
        UserModel newUser = new UserModel(userId,name,phoneNum);
        for(UserModel model :dbContacts.readCourses()){
            if(model.getUid().equals(userId)){
                return;
            }
        }
        dbHandler.addNewConnection(userId,name,phoneNum);






    }

    private void shareFile() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        // Ask specifically for something that can be opened:
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        startActivityForResult(
                Intent.createChooser(chooseFile, "Choose a file"),
                PICKFILE_REQUEST_CODE
        );
    }
    // And then somewhere, in your activity:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            filePath = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                imageView.setImageBitmap(bitmap);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


//            try {
//                // open the user-picked file for reading:
//                InputStream in = getContentResolver().openInputStream(content_describer);
//                // now read the content:
//                reader = new BufferedReader(new InputStreamReader(in));
//                String line;
//                StringBuilder builder = new StringBuilder();
//                while ((line = reader.readLine()) != null) {
//                    builder.append(line);
//                }
//                // Do something with the content in
//                //some_view.setText(builder.toString());
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
        }
    }


    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference storageReference= FirebaseStorage.getInstance().getReference();
            StorageReference riversRef = storageReference.child("images/"
                    + UUID.randomUUID().toString());
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }

}