package com.example.gcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ParticipantActivity extends AppCompatActivity {

    public static boolean isLoggedin;
    TextInputEditText editTextSearch;
    Button buttonSearch;

    ListView eventTypesList;

    DatabaseReference databaseEvents;
    List<String> results;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant);

        editTextSearch = findViewById(R.id.search_bar);
        buttonSearch = findViewById(R.id.search_button);

        String role = getIntent().getStringExtra("role");
        TextView userRole = findViewById(R.id.userRole);
        userRole.setText("Welcome, " + role);

        databaseEvents = FirebaseDatabase.getInstance().getReference("Events");
        eventTypesList = (ListView) findViewById(R.id.listOfEventTypes);
        results = new ArrayList<>();

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = String.valueOf(editTextSearch.getText());
                Log.d("SearchDebug", "Search term: " + search);

                if (TextUtils.isEmpty(search)) {
                    Toast.makeText(ParticipantActivity.this, "Please enter something to search!", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Events");

                Query checkUserDatabase = dr.orderByChild("Name").equalTo(search);

                //Clear the existing data outside
                results.clear();

                checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Iterate through the search results
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            //Get the name directly from DataSnapshot
                            String eventName = postSnapshot.child("Name").getValue(String.class);
                            //Check if eventName contains the search string
                            if (eventName != null && eventName.contains(search)) {
                                results.add(eventName);
                            }
                        }

                        //After checking for EventName and nothings found, check for EventType, then Club name
                        checkEventType(search);
                        checkClub(search);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            private void checkEventType(String search) {
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Events");

                Query checkUserDatabase = dr.orderByChild("Event Type").equalTo(search);

                checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String eventType = postSnapshot.child("Event Type").getValue(String.class);
                            if (eventType != null && eventType.contains(search)) {
                                String eventName = postSnapshot.child("Name").getValue(String.class);
                                results.add(eventName);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            private void checkClub(String search) {
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Events");

                Query checkUserDatabase = dr.orderByChild("Club name").equalTo(search);

                checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String eventType = postSnapshot.child("Club name").getValue(String.class);
                            if (eventType != null && eventType.contains(search)) {
                                String eventName = postSnapshot.child("Name").getValue(String.class);
                                results.add(eventName);
                            }
                        }

                        //Update the ListView with the new search results
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(ParticipantActivity.this, android.R.layout.simple_spinner_item, results);
                        eventTypesList.setAdapter(spinnerAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        eventTypesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Check if the position is valid
                if (i >= 0 && i < eventTypesList.getCount()) {
                    String eventName = (String) eventTypesList.getItemAtPosition(i);
                    showRegister(eventName);
                }

                return true;
            }
        });

    }

    protected void onStart() {
        super.onStart();
        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                results.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Gets events from postSnapshot as a string
                    String eventType = postSnapshot.getKey();
                    //Adds it to list of events
                    results.add(eventType);

                    //Now we need an adapter to display to ListView
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(ParticipantActivity.this, android.R.layout.simple_spinner_item, results);
                    eventTypesList.setAdapter(spinnerAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onClickLogOutButton(View view) {
        isLoggedin = false;
        finish();
    }

    private void showRegister(String eventName1) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.register_event, null);
        dialogBuilder.setView(dialogView);

        ListView fieldsListView = dialogView.findViewById(R.id.Fields);
        final Button buttonRegister = (Button) dialogView.findViewById(R.id.buttonRegister);
        final Button rate = (Button) dialogView.findViewById(R.id.rate);

        dialogBuilder.setTitle(eventName1);
        final AlertDialog b = dialogBuilder.create();
        b.show();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Events").child(eventName1);
        //Whole section below works to show specific event details
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //List to store field details
                    List<String> fieldDetails = new ArrayList<>();

                    //Loop through the event details
                    for (DataSnapshot fieldSnapshot : dataSnapshot.getChildren()) {
                        String fieldName = fieldSnapshot.getKey();

                        //Exclude "name" and "registeredUsers" attributes
                        if (!fieldName.equals("name") && !fieldName.equals("registeredUsers")) {
                            String fieldValue = String.valueOf(fieldSnapshot.getValue());
                            fieldDetails.add(fieldName + ": " + fieldValue);
                        }
                    }

                    //Create an ArrayAdapter to display the field details in the ListView
                    ArrayAdapter<String> fieldsAdapter = new ArrayAdapter<>(ParticipantActivity.this, android.R.layout.simple_list_item_1, fieldDetails);

                    //Set the adapter for the Fields ListView
                    fieldsListView.setAdapter(fieldsAdapter);
                } else {
                    Toast.makeText(ParticipantActivity.this, "Event not found.", Toast.LENGTH_SHORT).show();
                    b.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eventName = eventName1;

                DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Events").child(eventName);

                //Get the current user's username from intent
                Intent intent = getIntent();
                String username = intent.getStringExtra("username");

                dr.child("registeredUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Adds user to "registeredUsers" field, doesn't matter if the field exists already or not, firebase will automatically add it in if necessary
                        dr.child("registeredUsers").child(username).setValue(true);
                        b.dismiss();

                        Toast.makeText(ParticipantActivity.this, "Registered for the event!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog(eventName1);
            }
        });
    }

    private void showRatingDialog(String eventName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rating, null);
        dialogBuilder.setView(dialogView);

        final RatingBar ratingNum = (RatingBar) dialogView.findViewById(R.id.ratingBar);
        final TextInputEditText ratingDesc = (TextInputEditText) dialogView.findViewById(R.id.rating_desc);
        final Button rateButton = (Button) dialogView.findViewById(R.id.rateButton);

        dialogBuilder.setTitle(eventName);
        final AlertDialog b = dialogBuilder.create();
        b.show();


        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rating = ratingNum.getRating();
                String desc = ratingDesc.getText().toString().trim();
                String username = getIntent().getStringExtra("username");

                String entireRating = "username:"+username+" rating:"+rating+" desc:"+desc;

                DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Events").child(eventName);

                dr.child("Rating").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Adds user to "registeredUsers" field, doesn't matter if the field exists already or not, firebase will automatically add it in if necessary
                        dr.child("Rating").child(username).setValue(entireRating);
                        b.dismiss();

                        Toast.makeText(ParticipantActivity.this, "Rating Successfully Saved!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });


}


}