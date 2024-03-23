package com.example.gcc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {


    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
    RadioGroup buttonGroup;
    RadioButton buttonRole;

    ProgressBar progressBar;
    TextView textView;
    FirebaseDatabase db;
    DatabaseReference dr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail = findViewById(R.id.rating_desc);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        buttonGroup = findViewById(R.id.Role);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                db = FirebaseDatabase.getInstance();
                dr = db.getReference("users");
                int roleId = buttonGroup.getCheckedRadioButtonId();
                buttonRole = findViewById(roleId);


                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Please fill out all the fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(buttonRole==null){
                    Toast.makeText(Register.this, "Select a role", Toast.LENGTH_SHORT).show();
                    return;
                }
                String role = buttonRole.getText().toString();


                UserInfo ui = new UserInfo(email, password, role);
                dr.child(email).setValue(ui);


                Toast.makeText(Register.this, "Account created.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);


            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

    }

}