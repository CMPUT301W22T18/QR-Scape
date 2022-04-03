package com.example.qr_scape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CommentTest {
    private Solo solo;
    private String testComment = "This is testing comment functionality.";
    private String saltedHash = "THISi5AT3st5alTedHa5h";
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
        db.collection("Comments")
                .whereEqualTo("qrInstance", saltedHash)
                .whereEqualTo("user", savedUsername)
                .whereEqualTo("commentText", testComment)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    Log.d("list", queryDocumentSnapshots.getDocuments().toString());
                    for (DocumentSnapshot d : list){
                        assertEquals(d.get("user").toString(), savedUsername);
                        assertEquals(d.get("commentText").toString(), testComment);
                        assertEquals(d.get("qrInstance").toString(), saltedHash);
                    }
                }
            }
        });

    }
}
