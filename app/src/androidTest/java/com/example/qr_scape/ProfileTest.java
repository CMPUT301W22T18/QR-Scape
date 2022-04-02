package com.example.qr_scape;


import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
public class ProfileTest {
    private Solo solo;
    final private  String PROFILES = "Profiles";
    final private String USERNAME = "Username";
    final private String ContactInfo = "Test Contact info";
    private String Username = "testingusername";
    SharedPreferences sharedPreferences;


    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class,true,true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name),context.MODE_PRIVATE);

        solo.clickOnView(solo.getView(R.id.login_create_profile_button));
        solo.clickOnView((EditText) solo.getView(R.id.login_username_edittext));
        solo.enterText((EditText) solo.getView(R.id.login_username_edittext), Username);
        solo.clickOnView(solo.getView(R.id.login_confirm_button));
        solo.sleep(500);
        solo.clickOnView(solo.getView(R.id.nav_profile));
    }

    @After
    public void deleteAccount() {
        SharedPreferences.Editor shEditor = sharedPreferences.edit();
        shEditor.clear().commit();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PROFILES).document(Username).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("deleting profile", "Deleted profile");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("deleting profile", "Failed to delete profile");
                    }
                });
    }

    @Test
    public void hasUsernameTest() {
        assertTrue(solo.waitForText(Username));
    }

    @Test
    public void updateContactInfoTest() {
        solo.enterText((EditText) solo.getView(R.id.profile_contact_info_edittext), ContactInfo);
        solo.clickOnView(solo.getView(R.id.profile_confirm_button));
        solo.sleep(1000);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Profiles").document(Username).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String test = documentSnapshot.getString("Contact info");
                            if (documentSnapshot.getString("Contact info").equals(ContactInfo)) {

                            } else {
                                assertTrue(false);
                            }
                        }
                    }
                });
    }

    @Test
    public void reflectsUpdateTest() {
        HashMap data = new HashMap<>();
        data.put("Contact info", ContactInfo);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Profiles").document(Username).update(data)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(null, "Successfully updated contact info");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(null, "Failed to update contact info");
                    }
                });
        solo.sleep(1000);
        assertTrue(solo.waitForText(ContactInfo));
    }

}
