package com.example.qr_scape;


import static android.content.Context.MODE_PRIVATE;

import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {
    private Solo solo;
    SharedPreferences sharedPreferences;
    String username = "TestUsername";
    String CONTACTINFO = "Contact info";
    FirebaseFirestore db;
    DocumentReference docRef;


    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception{
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context context = instrumentation.getTargetContext();
        // Clear shared preferences
        context.getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
        // Delete DB entry
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("Profiles").document(username);
        docRef.delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(null,"Failed to delete profile");
                        assertTrue(false);
                    }
                });
        solo = new Solo(instrumentation,rule.getActivity());
        solo.waitForActivity("LoginActivity", 2000);
        solo.clickOnView(solo.getView(R.id.login_create_profile_button));
        solo.enterText((EditText) solo.getView(R.id.login_username_edittext), username);
        solo.clickOnView(solo.getView(R.id.login_confirm_button));
        solo.waitForActivity("ProfileActivity", 2000);
    }

    @After
    public void close() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context context = instrumentation.getTargetContext();
        // Clear shared preferences
        context.getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }

    @Test
    public void correctName() {
        assertTrue(solo.waitForText(username, 1, 2000));
    }

    @Test
    public void correctInfo() {
        HashMap data = new HashMap<>();
        data.put(CONTACTINFO, "Test Contact Info");
        docRef.update(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(null,"Failed to update contact info");
                assertTrue(false);
            }
        });
        assertTrue(solo.waitForText("Test Contact Info", 1, 2000));
        data = new HashMap<>();
        data.put(CONTACTINFO, "New Test Contact Info");
        docRef.update(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(null,"Failed to update contact info");
                assertTrue(false);
            }
        });
        assertTrue(solo.waitForText("New Test Contact Info", 1, 2000));
    }

    @Test
    public void infoUpdates() throws InterruptedException {
        solo.enterText((EditText) solo.getView(R.id.profile_contact_info_edittext),
                "Test Contact Info");
        assertTrue(solo.waitForText("Test Contact Info", 1, 2000));
        assertTrue(solo.getView(R.id.profile_confirm_button).getVisibility() == View.VISIBLE);
        solo.clickOnView(solo.getView(R.id.profile_confirm_button));
        solo.sleep(2000);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String value = documentSnapshot.getString(CONTACTINFO);
                            if (value.equals(new String("Test Contact Info"))) {

                            } else {
                                assertTrue(false);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        assertTrue(false);
                    }
                });
    }
}
