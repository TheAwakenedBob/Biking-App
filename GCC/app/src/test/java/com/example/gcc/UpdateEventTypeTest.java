package com.example.gcc;

import org.junit.Test;
import org.junit.Rule;
import static org.junit.Assert.*;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

@RunWith(MockitoJUnitRunner.class)
public class UpdateEventTypeTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    Context mMockContext;

    @Test
    public void testFieldNamesDeletion(){

        UpdateEventType updateEventTypeTest = new UpdateEventType();
        updateEventTypeTest.eventTypeFields = new ArrayList<>();
        updateEventTypeTest.adapter = new ArrayAdapter<>(updateEventTypeTest, android.R.layout.simple_list_item_1, updateEventTypeTest.eventTypeFields);
        updateEventTypeTest.eventTypeFields.add("Number Of Participants");
        updateEventTypeTest.eventTypeFields.add("Intensity");
        updateEventTypeTest.eventTypeFields.add("Duration");

        updateEventTypeTest.deleteFieldName("Intensity");
        assertEquals(2, updateEventTypeTest.eventTypeFields.size());
    }

    @Test
    public void testFieldNamesUpdate(){

        UpdateEventType updateEventTypeTest = new UpdateEventType();
        updateEventTypeTest.eventTypeFields = new ArrayList<>();
        updateEventTypeTest.adapter = new ArrayAdapter<>(updateEventTypeTest, android.R.layout.simple_list_item_1, updateEventTypeTest.eventTypeFields);
        updateEventTypeTest.eventTypeFields.add("Number Of Participants");
        updateEventTypeTest.eventTypeFields.add("Intensity");
        updateEventTypeTest.eventTypeFields.add("Duration");

        updateEventTypeTest.updateNameField("Difficulty","Intensity");

        assertFalse(updateEventTypeTest.eventTypeFields.contains("Intensity"));
        assertTrue(updateEventTypeTest.eventTypeFields.contains("Difficulty"));
        assertEquals(1,updateEventTypeTest.eventTypeFields.indexOf("Difficulty"));
    }
}

