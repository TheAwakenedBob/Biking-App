package com.example.gcc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ListView;
import android.widget.Toast;

import java.util.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewEventTypeCreation extends AppCompatActivity {

    private Button saveButton, addFieldNameButton;
    private ListView list;

    private EditText editText;

    ArrayList<String> arrayList;

    ArrayAdapter<String> adapter;

    DatabaseReference databaseEvents;

    EditText eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_types);

        databaseEvents = FirebaseDatabase.getInstance().getReference("EventTypes");

        addFieldNameButton = findViewById(R.id.addFieldNameButton);
        editText = (EditText) findViewById(R.id.fieldName);
        list = (ListView) findViewById(R.id.fieldsList);
        arrayList = new ArrayList<>();
        ArrayAdapter adapter2 = new ArrayAdapter (NewEventTypeCreation.this, android.R.layout.simple_list_item_1, arrayList);
        list.setAdapter(adapter2);

        addFieldNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText = (EditText) findViewById(R.id.fieldName);
                if (editText.length()>0) {

                    arrayList.add(editText.getText().toString());
                    editText.setText("");

                    //displaying a success toast
                    Toast.makeText(NewEventTypeCreation.this, "Field name added", Toast.LENGTH_LONG).show();
                } else {
                    //if the value is not given displaying a toast
                    Toast.makeText(NewEventTypeCreation.this, "Please enter a value", Toast.LENGTH_LONG).show();
                }
                adapter2.notifyDataSetChanged();
            }
        });


    }


    public void onClickSaveButton(View view) {
        eventName = findViewById(R.id.eventName);
        if (arrayList.size()>1 && eventName.getText().toString() != null) {
            String eventNameID = eventName.getText().toString();
            databaseEvents.child(eventNameID).setValue(arrayList);
            arrayList.clear();
            Toast.makeText(this, "Event type saved!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Please fill out all the fields!", Toast.LENGTH_LONG).show();
        }
        finish();
    }
}