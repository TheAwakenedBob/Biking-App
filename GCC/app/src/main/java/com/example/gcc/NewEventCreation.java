package com.example.gcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewEventCreation extends AppCompatActivity {

    private Spinner spinner;
    private Button saveButton, addFieldNameButton;
    private EditText editText, eventName;

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseEventsTypes;
    private DatabaseReference databaseEvents;
    private DatabaseReference clubName;
    private LinearLayout dynamicFieldsContainer;
    private String username;
    private String CN;
    boolean Club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_creation);

        username = getIntent().getStringExtra("username");
        databaseEventsTypes = FirebaseDatabase.getInstance().getReference("EventTypes");
        databaseEvents = FirebaseDatabase.getInstance().getReference("Events");
        CN = "Enter club name";
        Club = false;
        try {
            clubName = FirebaseDatabase.getInstance().getReference("users/" + username + "/clubName");
            Club = true;
        }
        catch(Exception e){
        }
        spinner = findViewById(R.id.eventTypes);
        dynamicFieldsContainer = findViewById(R.id.editFields);

        arrayList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //Once a spinner element is selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedEventType = spinner.getSelectedItem().toString();
                DatabaseReference fieldsRef = databaseEventsTypes.child(selectedEventType);
                dynamicFieldsContainer.removeAllViews();

                //Getting fields
                fieldsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String fieldValue = childSnapshot.getValue(String.class);

                                EditText editText = new EditText(NewEventCreation.this);

                                //Setting hint and layout parameters
                                editText.setHint(fieldValue);
                                if(editText.getHint().equals("Club name")) {
                                    editText.setText(CN);
                                }
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                editText.setLayoutParams(params);

                                //Adding the EditText to the container
                                dynamicFieldsContainer.addView(editText);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        if(Club) {
            clubName.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue()!= null) {
                        CN = snapshot.getValue().toString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        //Fetch eventTypes from Firebase and update spinner
        databaseEventsTypes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String eventType = postSnapshot.getKey();
                    arrayList.add(eventType);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(NewEventCreation.this, "Failed to load event types", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onClickSaveButton(View view) {
        eventName = findViewById(R.id.eventName);
        boolean add = true;
        if (eventName.getText().toString() != null && add) {
            String selectedEventType = spinner.getSelectedItem().toString();
            String name = eventName.getText().toString();

            //Creating a map to store the event details
            Map<String, Object> eventDetails = new HashMap<>();
            eventDetails.put("Name", name);
            eventDetails.put("Event Type", selectedEventType);

            //Iterate through the dynamicFieldsContainer to access each EditText
            for (int i = 0; i < dynamicFieldsContainer.getChildCount(); i++) {
                View childView = dynamicFieldsContainer.getChildAt(i);

                if (childView instanceof EditText) {
                    EditText editText = (EditText) childView;
                    //Field Name is the editText hint
                    String fieldName = editText.getHint().toString();
                    String fieldValue = editText.getText().toString();

                    if(!fieldName.equals("Activity type") && !fieldName.equals("Club name")){
                        try{
                            int temp = Integer.parseInt(fieldValue);
                            //Add field details to map
                            eventDetails.put(fieldName, fieldValue);

                        }
                        catch(Exception e){
                            Toast.makeText(this, "Error, String entered for Integer Values. Event not created.", Toast.LENGTH_SHORT).show();
                            add = false;
                        }
                    }
                    else {
                        eventDetails.put(fieldName, fieldValue);
                    }
                }
            }

            //Save event to Firebase
            if(add) {
                databaseEvents.child(name).setValue(eventDetails);

                Toast.makeText(this, "Event created!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please add field names and an event name!", Toast.LENGTH_LONG).show();
        }
        finish();
    }


}
