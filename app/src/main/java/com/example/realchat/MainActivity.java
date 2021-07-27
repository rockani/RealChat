package com.example.realchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Ye Tag hai";
    private FirebaseAuth mAuth;

    private EditText mPhoneNumber, mCode1,mCode2,mCode3,mCode4,mCode5,mCode6;
    private Button mSend;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Boolean LoggedIn=false;
    String mVerificationId;
    FirebaseDatabase rootnode;
    DatabaseReference mUserDB;
    private ValueEventListener mUserDBListener;
    CountryCodePicker ccp;
    String selected_country_code="+91";
    LinearLayout linearLayout;
    TextView resend,verif_failed;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        userIsLoggedIn();
        //.child("users");
        mSend = findViewById(R.id.send);
        linearLayout = findViewById(R.id.root_otp_layout);
        resend = findViewById(R.id.resend_code);
        verif_failed = findViewById(R.id.verification_failed);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verif_failed.setVisibility(View.INVISIBLE);
                resend.setVisibility(View.INVISIBLE);
                if (mVerificationId != null)
                    verifyPhoneNumberWithCode();
                else
                    startPhoneNumberVerification();
            }
        });

      resend.setOnClickListener(
              new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                              .setPhoneNumber(selected_country_code+mPhoneNumber.getText().toString()) // Phone number to verify
                              .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                              .setActivity(MainActivity.this) // Activity (for callback binding)
                              .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
                              .setForceResendingToken(mResendToken).build();  // ForceResendingToken from callbacks

                      PhoneAuthProvider.verifyPhoneNumber(options);
                  }
              }
      );




        mPhoneNumber = findViewById(R.id.phoneNumber);
        mCode1 = findViewById(R.id.otp_edit_box1); mCode1.setText("");
        mCode2 = findViewById(R.id.otp_edit_box2);  mCode2.setText("");
        mCode3 = findViewById(R.id.otp_edit_box3);  mCode3.setText("");
        mCode4 = findViewById(R.id.otp_edit_box4);  mCode4.setText("");
        mCode5 = findViewById(R.id.otp_edit_box5);  mCode5.setText("");
        mCode6 = findViewById(R.id.otp_edit_box6);  mCode6.setText("");
        EditText[] edit = {mCode1,mCode2,mCode3,mCode4,mCode5,mCode6};

        mCode1.addTextChangedListener(new GenericTextWatcher(mCode1, edit));
        mCode2.addTextChangedListener(new GenericTextWatcher(mCode2, edit));
        mCode3.addTextChangedListener(new GenericTextWatcher(mCode3, edit));
        mCode4.addTextChangedListener(new GenericTextWatcher(mCode4, edit));
        mCode5.addTextChangedListener(new GenericTextWatcher(mCode5, edit));
        mCode6.addTextChangedListener(new GenericTextWatcher(mCode6, edit));





        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                verif_failed.setVisibility(View.VISIBLE);
                resend.setVisibility(View.VISIBLE);

            }


            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                mVerificationId = verificationId;
                mResendToken = forceResendingToken;
                mSend.setText("Verify Code");
                linearLayout.setVisibility(View.VISIBLE);

            }
        };
        ccp = findViewById(R.id.ccp);


    }

    private void verifyPhoneNumberWithCode () {
        String code = mCode1.getText().toString()+mCode2.getText().toString()+ mCode3.getText().toString()+mCode4.getText().toString()+ mCode5.getText().toString()+mCode6.getText().toString();
        Log.d(TAG,code);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential (PhoneAuthCredential phoneAuthCredential){
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Log.d("Task Result","Task Successful");
                    if (user != null) {

//                            Map<String, Object> userMap = new HashMap<>();
//                            userMap.put("phone", user.getPhoneNumber());
//                            userMap.put("name", user.getPhoneNumber());
//                            mUserDB.updateChildren(userMap);
                        addDatatoFirebase(user);
                    }

                }

            }
        });
    }

    private void userIsLoggedIn () {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            LoggedIn=true;

            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            finish();
            return;
        }
    }

    private void startPhoneNumberVerification () {
//            PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                    mPhoneNumber.getText().toString(),
//                    60,
//                    TimeUnit.SECONDS,
//                    this,
//                    mCallbacks);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(selected_country_code+mPhoneNumber.getText().toString())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public class FireBaseUserModel{
        public String PhoneNum;
        public String Name;
       // public String UserID;
        public FireBaseUserModel(String phoneNum, String name) {
            PhoneNum = phoneNum;
            Name = name;
            //UserID = userID;
        }
    }

    public class GenericTextWatcher implements TextWatcher {
        private final EditText[] editText;
        private View view;
        public GenericTextWatcher(View view, EditText editText[])
        {
            this.editText = editText;
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {

                case R.id.otp_edit_box1:
                    if (text.length() == 1)
                        editText[1].requestFocus();
                    break;
                case R.id.otp_edit_box2:

                    if (text.length() == 1)
                        editText[2].requestFocus();
                    else if (text.length() == 0)
                        editText[0].requestFocus();
                    break;
                case R.id.otp_edit_box3:
                    if (text.length() == 1)
                        editText[3].requestFocus();
                    else if (text.length() == 0)
                        editText[1].requestFocus();
                    break;
                case R.id.otp_edit_box4:

                    if (text.length() == 1)
                        editText[4].requestFocus();
                    else if (text.length() == 0)
                        editText[2].requestFocus();
                    break;
                case R.id.otp_edit_box5:

                    if (text.length() == 1)
                        editText[5].requestFocus();
                    else if (text.length() == 0)
                        editText[3].requestFocus();
                    break;
                case R.id.otp_edit_box6:
                    if (text.length() == 0)
                        editText[4].requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    }


    public void onCountryPickerClick(View view) {
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //Alert.showMessage(RegistrationActivity.this, ccp.getSelectedCountryCodeWithPlus());
                selected_country_code = ccp.getSelectedCountryCodeWithPlus();
            }
        });
    }
    private void addDatatoFirebase(FirebaseUser user) {
        Log.d("Called","addDatatoFirebase");
        rootnode = FirebaseDatabase.getInstance();
        mUserDB = rootnode.getReference().child("users").child(user.getUid());
        mUserDBListener = (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("phone", user.getPhoneNumber());
                    userMap.put("name", user.getPhoneNumber());
                    mUserDB.setValue(userMap);
                }
                userIsLoggedIn();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mUserDB.addListenerForSingleValueEvent(mUserDBListener);
        //Log.d("UserDB",mUserDB.toString())                     ;
        //Toast.makeText(MainActivity.this, "data added", Toast.LENGTH_SHORT).show();
        //userIsLoggedIn();
        mUserDB.removeEventListener(mUserDBListener);
        startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
        finish();

    }


}


