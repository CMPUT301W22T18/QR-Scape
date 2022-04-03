package com.example.qr_scape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
public class CommentTest {
    private Solo solo;
    private String testComment = "This is testing comment functionality.";
    final private String USERNAME = "Username";
    final private  String PROFILES = "Profiles";
    SharedPreferences sharedPreferences;

    @Rule
    public ActivityTestRule<CommentActivity> rule =
            new ActivityTestRule<>(CommentActivity.class,true,true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name),context.MODE_PRIVATE);
    }

    @Before
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

    public void addComment() {
        solo.clickOnView((EditText) solo.getView(R.id.editTextComment));
        solo.enterText((EditText) solo.getView(R.id.editTextComment), testComment);
        solo.clickOnView(solo.getView(R.id.addCommentButton));
    }

    @Test
    public void addCommentTest() throws Exception{
        addComment();
        solo.sleep(1000);
        assertTrue(solo.searchText(testComment));

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name),context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(USERNAME, null);
        assertTrue(solo.searchText(savedUsername));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PROFILES).document(Username).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                        } else {
                            assertTrue(false);
                        }
                    }
                });
    }

    @Test
    public void scanProfileTest() throws Exception{

    }

    @Test
    public void profileAlreadyExistTest() throws Exception{
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, String> data = new HashMap<>();
        data.put("Contact info", "");
        db.collection(PROFILES)
                .document(Username)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(null, "Successfully created user");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(null,"Failed to create user");
                    }
                });
        createProfile();
        assertTrue(solo.waitForText("Username is taken"));
    }

}
