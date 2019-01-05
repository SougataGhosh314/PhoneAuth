package com.brainstorm314.sougataghosh.phoneauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCountryCode;
    private EditText editTextPhoneNumber, editTextUserId;
    private Button buttonSendOTP, buttonSubmit;
    private String numberP;

    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setOrientation();

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        findViews();
        setOnClickListeners();
    }

    private void setOrientation(){
        int currentOrientation = this.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE){
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else{
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void setOnClickListeners(){

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String u = editTextUserId.getText().toString().trim().toUpperCase();
                Log.e("DEBUG", u);

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(u)){
                            Log.e("DEBUG", "1");
                            Log.e("DEBUG", snapshot.child(u).child("phone").toString());
//                            Log.e("DEBUG", snapshot
//                                            .child(user)
//                                            .child("phone")
//                                            .getValue(String.class));

                            numberP = snapshot
                                    .child(u)
                                    .child("phone")
                                    .getValue()
                                    .toString();

                            int len = numberP.length();
                            String toShow = "******" + numberP.charAt(len-4)
                                    + numberP.charAt(len-3) + numberP.charAt(len-2)
                                    + numberP.charAt(len-1);
                            editTextPhoneNumber.setText(toShow);
                        } else{
                            Toast.makeText(getApplicationContext(),
                                    "User ID doesn't exist!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Database error",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        buttonSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = editTextUserId.getText().toString().trim().toUpperCase();
                final String code = CountryData.countryAreaCodes[spinnerCountryCode.getSelectedItemPosition()];
//                final String number = editTextPhoneNumber.getText().toString().trim();
                final String number = numberP;

                String phoneNumber = "+" + code + number;
                Intent intent = new Intent(MainActivity.this,
                        VerifyPhoneActivity.class);
                intent.putExtra("phonenumber", phoneNumber);
                intent.putExtra("userID", user);
                startActivity(intent);

//                if (number.isEmpty() || number.length() < 10) {
//                    editTextPhoneNumber.setError("Valid number is required");
//                    editTextPhoneNumber.requestFocus();
//                    return;
//                }
//
//                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot snapshot) {
//                        if (snapshot.hasChild(user)) {
//                            if (snapshot
//                                    .child(user)
//                                    .child("phone")
//                                    .getValue()
//                                    .toString()
//                                    .equals(number)){
//                                String phoneNumber = "+" + code + number;
//                                Intent intent = new Intent(MainActivity.this,
//                                        VerifyPhoneActivity.class);
//                                intent.putExtra("phonenumber", phoneNumber);
//                                intent.putExtra("userID", user);
//                                startActivity(intent);
//                                //code
//
//                            } else {
//                                Toast.makeText(getApplicationContext(),
//                                        "Phone number doesn't match with User ID",
//                                        Toast.LENGTH_LONG).show();
//                            }
//                        }else{
//                            Toast.makeText(getApplicationContext(),
//                                    "User ID doesn't exist!",
//                                    Toast.LENGTH_LONG).show();
//                            // does not exist
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(getApplicationContext(),
//                                "Error in fetching data",
//                                Toast.LENGTH_LONG).show();
//                    }
//                });
            }
        });
    }

    private void findViews(){
        spinnerCountryCode = findViewById(R.id.spinnerCountries);
        spinnerCountryCode.setAdapter(new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));
        spinnerCountryCode.setSelection(Arrays.asList(CountryData.countryNames).indexOf("India"));


        editTextPhoneNumber = findViewById(R.id.editTextMobile);
        buttonSendOTP = findViewById(R.id.buttonContinue);
        buttonSubmit = findViewById(R.id.submitUserId);
        editTextUserId = findViewById(R.id.editTextUserId);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setOrientation();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
