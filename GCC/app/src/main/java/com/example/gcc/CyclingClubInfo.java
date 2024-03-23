package com.example.gcc;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CyclingClubInfo extends AppCompatActivity {
    EditText eventOrganizerUsername, eventOrganizerPassword, eventOrganizerSocMedLink, eventOrganizerContactName, eventOrganizerPhoneNumber, eventOrganizerClubName;

    String username, password;
    Button saveButton;

    DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_organizer_personal_info);

        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        userRef = FirebaseDatabase.getInstance().getReference("users/"+username);
        eventOrganizerUsername = findViewById(R.id.eventOrganizerUsername);
        eventOrganizerPassword = findViewById(R.id.eventOrganizerPassword);
        saveButton = findViewById(R.id.saveInfoButton);
        eventOrganizerClubName = findViewById(R.id.ClubName);
        eventOrganizerSocMedLink = findViewById(R.id.eventOrganizerSocMedLink);
        eventOrganizerContactName = findViewById(R.id.eventOrganizerContactName);
        eventOrganizerPhoneNumber = findViewById(R.id.eventOrganizerPhoneNumber);

        eventOrganizerUsername.setText(username);
        eventOrganizerPassword.setText(password);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( TextUtils.isEmpty(eventOrganizerSocMedLink.getText()) || TextUtils.isEmpty(eventOrganizerPhoneNumber.getText()) || TextUtils.isEmpty(eventOrganizerClubName.getText())){
                    Toast.makeText(CyclingClubInfo.this, "Please fill out the fields followed by *",Toast.LENGTH_LONG).show();
                }else{
                    //updateInformation();
                    userRef.child("clubName").setValue(String.valueOf(eventOrganizerClubName.getText()).trim());
                    userRef.child("socialMediaLink").setValue(String.valueOf(eventOrganizerSocMedLink.getText()).trim());
                    if(!TextUtils.isEmpty(eventOrganizerContactName.getText())){
                        userRef.child("contactName").setValue(String.valueOf(eventOrganizerContactName.getText()).trim());
                    }
                    userRef.child("contactPhoneNumber").setValue(String.valueOf(eventOrganizerPhoneNumber.getText()).trim());
                    Toast.makeText(CyclingClubInfo.this, "Personal Information Modified",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

    }

}
