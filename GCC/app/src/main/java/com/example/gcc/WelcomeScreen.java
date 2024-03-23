package com.example.gcc;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.Objects;

public class WelcomeScreen extends AppCompatActivity {
    private static final int delay = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        AdminActivity.isLoggedIn=true;

        String user = getIntent().getStringExtra("email");
        TextView userEmailName = findViewById(R.id.Welcome);
        userEmailName.setText("Welcome, " + user);

        String role = getIntent().getStringExtra("role");
        TextView userRole = findViewById(R.id.UserRole);
        userRole.setText("Your Role is, " + role);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("WelcomeScreen", "Role: " + role);
                if (role.equals("Event organizer")){
                    Intent intent = new Intent(WelcomeScreen.this, EventOrganiserActivity.class);
                    intent.putExtra("role", role);
                    intent.putExtra("username",user);
                    intent.putExtra("password",getIntent().getStringExtra("password"));
                    startActivity(intent);

                }
                else if (role.equals("Administrator")){
                    Intent intent = new Intent(WelcomeScreen.this, AdminActivity.class);
                    intent.putExtra("role", role);
                    startActivity(intent);

                }
                else if (role.equals("Participant")){
                    Intent intent = new Intent(WelcomeScreen.this, ParticipantActivity.class);
                    intent.putExtra("role", role);
                    intent.putExtra("username",user);
                    startActivity(intent);

                }

                finish();
            }
        }, delay);
    }

}