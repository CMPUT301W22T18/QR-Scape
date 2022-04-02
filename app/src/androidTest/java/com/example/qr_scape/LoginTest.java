package com.example.qr_scape;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    private Solo solo;
    private String Username = "TestingUsername";
    final private String USERNAME = "Username";
    final private  String PROFILES = "Profiles";

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class,true,true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Before
    public void deleteAccount() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name),context.MODE_PRIVATE);
        SharedPreferences.Editor shEditor = sharedPreferences.edit();
        shEditor.putString(USERNAME,null);
        shEditor.commit();

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
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void createProfile() throws Exception{
        assertTrue(true);
    }

}
