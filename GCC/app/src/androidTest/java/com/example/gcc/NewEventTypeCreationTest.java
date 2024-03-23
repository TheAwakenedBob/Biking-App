
package com.example.gcc;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import org.junit.Test;

import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

public class NewEventTypeCreationTest {

    @Test
    public void testFieldNameAddition(){

        NewEventTypeCreation eventTypeCreationTest = new NewEventTypeCreation();
        eventTypeCreationTest.arrayList = new ArrayList<>();
        onView(withId(R.id.fieldName)).perform(typeText("Level"), closeSoftKeyboard());
        onView(withId(R.id.addFieldNameButton)).perform(click());
        onView(withId(R.id.fieldName)).perform(typeText("Time"), closeSoftKeyboard());
        onView(withId(R.id.addFieldNameButton)).perform(click());
        assertEquals(2, eventTypeCreationTest.arrayList.size());
    }
}
