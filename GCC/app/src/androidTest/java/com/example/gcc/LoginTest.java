
package com.example.gcc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import org.junit.Test;

import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

import android.content.Context;

import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Test
    public void testSuccessfulLoginWithExtraSpace(){
        Login testActivity = new Login();
        onView(withId(R.id.username)).perform(typeText("gccadmin "), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("GCCROCKS!"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        assertTrue(testActivity.isLoggedIn);
    }

    @Test
    public void TestUnsuccessfulLogin(){
        Login testActivity = new Login();
        onView(withId(R.id.username)).perform(typeText("Jake"), closeSoftKeyboard());
        onView(withId(R.id.username)).perform(typeText("GCCROCKS!"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        assertFalse(testActivity.isLoggedIn);
    }

    @Test
    public void TestRegister(){
        Login testActivity = new Login();
        onView(withId(R.id.registerNow)).perform(click());
        intended(hasComponent(Register.class.getName()));
    }

    @Test
    public void TestProperRedirection(){
        ActivityScenario<Login> testActivity = ActivityScenario.launch(Login.class);
        onView(withId(R.id.username)).perform(typeText("gccadmin "), closeSoftKeyboard());
        onView(withId(R.id.username)).perform(typeText("GCCROCKS!"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        intended(allOf(
                hasComponent(EventOrganiserActivity.class.getName()),
                IntentMatchers.hasExtra("username", "role")
        ));
    }

}