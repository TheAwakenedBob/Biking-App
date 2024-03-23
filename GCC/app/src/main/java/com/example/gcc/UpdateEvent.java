package com.example.gcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateEvent extends AppCompatActivity{

    TextView eventType;
    ListView fieldNamesList;
    ArrayList<String> eventTypeFields;
    DatabaseReference databaseEventTypeFields;
    ArrayAdapter<String> adapter;
    String event;
    String field;
    String value;
    Button save;
    Map<String, Object> eventDetails = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_event);

        event = getIntent().getStringExtra("event");
        eventType = findViewById(R.id.eventType4);
        eventType.setText(event);
        eventTypeFields = new ArrayList<>();
        databaseEventTypeFields = FirebaseDatabase.getInstance().getReference("Events/"+event);
        fieldNamesList = (ListView) findViewById(R.id.Fields);
        adapter = new ArrayAdapter<>(UpdateEvent.this, android.R.layout.simple_list_item_1, eventTypeFields);
        fieldNamesList.setAdapter(adapter);
        save = findViewById(R.id.Save);

        fieldNamesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String combined = eventTypeFields.get(i);
                showUpdateDeleteDialog(combined);
                return true;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

    }

    public void save(){
        databaseEventTypeFields.updateChildren(eventDetails);
        eventTypeFields.clear();
        Toast.makeText(this, "Event Saved!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseEventTypeFields.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventTypeFields.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Gets eventType from postSnapshot as a string
                    String eventTypeField = (String) (postSnapshot.getKey() + ": " + postSnapshot.getValue().toString());
                    //Adds it to list of eventTypes
                    eventTypeFields.add(eventTypeField);
                    eventDetails.put(postSnapshot.getKey(), postSnapshot.getValue().toString());

                    //Now we need an adapter to display to ListView
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void updateEventType(){
        databaseEventTypeFields.setValue(eventTypeFields);
        eventTypeFields.clear();
        Toast.makeText(this, "Event Type updated!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void showUpdateDeleteDialog(String combined) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.newFieldName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateField);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteField);

        dialogBuilder.setTitle(combined);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    updateNameField(name, combined);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFieldName(combined);
                b.dismiss();
            }
        });
    }

    private void updateNameField(String name, String combined){
        //Updating
        String[] temp = combined.split(":");
        eventDetails.put(temp[0], name);

        eventTypeFields.set(eventTypeFields.indexOf(combined), (temp[0] + ": " + name));
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Field Updated", Toast.LENGTH_LONG).show();
    }

    private boolean deleteFieldName(String field) {
        //Removing
        eventTypeFields.remove(field);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Product Deleted", Toast.LENGTH_LONG).show();
        return true;
    }

}
