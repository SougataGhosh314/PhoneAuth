package com.brainstorm314.sougataghosh.phoneauth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    EditText newPassword, newPasswordC;
    DatabaseReference usersRef;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        user = getIntent().getStringExtra("userID");

        if (user != null) {

            newPassword = findViewById(R.id.newPassword);
            newPasswordC = findViewById(R.id.newPasswordC);

            findViewById(R.id.savePassword).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pass, passC;
                    pass = newPassword.getText().toString().trim();
                    passC = newPasswordC.getText().toString().trim();
                    if (pass.equals(passC)) {

                        usersRef.child(user).child("pass").setValue(pass);
                        Toast.makeText(getApplicationContext(),
                                "Password changed successfully!",
                                Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Passwords do not match!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "Log in again", Toast.LENGTH_LONG).show();

            try {
                Thread.sleep(3000);
            }catch (Exception e){
                e.printStackTrace();
            }

            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
