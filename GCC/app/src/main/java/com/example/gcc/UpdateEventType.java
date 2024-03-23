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

public class UpdateEventType extends AppCompatActivity{

    TextView eventType;
    ListView fieldNamesList;
    EditText fieldName;
    ArrayList<String> eventTypeFields;
    DatabaseReference databaseEventTypeFields;
    ArrayAdapter<String> adapter;
    String event;
    Button addField, updateEventType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_update_event_type);

        event = getIntent().getStringExtra("eventType");
        eventType = findViewById(R.id.eventType);
        eventType.setText(event);
        fieldName = findViewById(R.id.addFieldName);
        eventTypeFields = new ArrayList<>();
        databaseEventTypeFields = FirebaseDatabase.getInstance().getReference("EventTypes/"+event);
        fieldNamesList = (ListView) findViewById(R.id.fields);
        adapter = new ArrayAdapter<>(UpdateEventType.this, android.R.layout.simple_list_item_1, eventTypeFields);
        fieldNamesList.setAdapter(adapter);
        addField = findViewById(R.id.addField);
        updateEventType = findViewById(R.id.updateEventType);

        fieldNamesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String field = eventTypeFields.get(i);
                showUpdateDeleteDialog(field);
                return true;
            }
        });

        addField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addField();
            }
        });

        updateEventType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEventType();
            }
        });
        
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
                    String eventTypeField = (String) postSnapshot.getValue();
                    //Adds it to list of eventTypes
                    eventTypeFields.add(eventTypeField);

                    //Now we need an adapter to display to ListView
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void addField(){
        fieldName = findViewById(R.id.addFieldName);
        String field  = fieldName.getText().toString();
        if (!eventTypeFields.contains(field)) {
            eventTypeFields.add(field);
            adapter.notifyDataSetChanged();
            //Fix adding and updating + deleting stuff
            Toast.makeText(UpdateEventType.this, "Field added!", Toast.LENGTH_LONG).show();
            fieldName.setText("");
        } else{
            Toast.makeText(UpdateEventType.this, "Field already existing!", Toast.LENGTH_LONG).show();
        }
    }

    public void updateEventType(){
        databaseEventTypeFields.setValue(eventTypeFields);
        eventTypeFields.clear();
        Toast.makeText(this, "Event Type updated!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void showUpdateDeleteDialog(String field) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.newFieldName);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateField);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteField);

        dialogBuilder.setTitle(field);
        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    updateNameField(name, field);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFieldName(field);
                b.dismiss();
            }
        });
    }

    void updateNameField(String name, String field){
        //Updating
        eventTypeFields.set(eventTypeFields.indexOf(field), name);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Field Updated", Toast.LENGTH_LONG).show();
    }

    boolean deleteFieldName(String field) {
        //Removing
        eventTypeFields.remove(field);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Product Deleted", Toast.LENGTH_LONG).show();
        return true;
    }

}
