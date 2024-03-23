package com.example.gcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class EventOrganiserActivity extends AppCompatActivity {


    public static boolean isLoggedin;

    TextView createNewEvent, changeInfo;


    ListView eventTypesList;

    DatabaseReference databaseEvents;
    List<String> eventTypes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_organizer);

        String role = getIntent().getStringExtra("role");
        TextView userRole = findViewById(R.id.userRole);
        userRole.setText("Welcome, " + role);


        createNewEvent = findViewById(R.id.newEvent);
        changeInfo = findViewById(R.id.changeInfo);


        databaseEvents = FirebaseDatabase.getInstance().getReference("Events");
        eventTypesList = (ListView) findViewById(R.id.listOfEventTypes);
        eventTypes = new ArrayList<>();

        createNewEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventOrganiserActivity.this, NewEventCreation.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
            }
        });
        changeInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventOrganiserActivity.this, CyclingClubInfo.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                intent.putExtra("password", getIntent().getStringExtra("password"));
                startActivity(intent);
            }
        });

        eventTypesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String eventType = eventTypes.get(i);
                showUpdateDeleteDialog(eventType);
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventTypes.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Gets eventType from postSnapshot as a string
                    String eventType = postSnapshot.getKey();
                    //Adds it to list of eventTypes
                    eventTypes.add(eventType);

                    //Now we need an adapter to display to ListView
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(EventOrganiserActivity.this, android.R.layout.simple_spinner_item, eventTypes);

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

    private void showUpdateDeleteDialog(String field) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog_event_type, null);
        dialogBuilder.setView(dialogView);

        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateField);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteField);

        dialogBuilder.setTitle(field);
        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEvent(field);
                b.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEvent(field);
                b.dismiss();
            }
        });
    }

    private void updateEvent(String name){
        Intent intent = new Intent(this, UpdateEvent.class);
        intent.putExtra("event", name);
        startActivity(intent);
    };

    private void deleteEvent(String name){
        databaseEvents.child(name).removeValue();
        Toast.makeText(this, "Event Type Deleted!",Toast.LENGTH_LONG).show();
    };

}