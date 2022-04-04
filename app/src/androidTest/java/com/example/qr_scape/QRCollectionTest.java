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
public class QRCollectionTest {
    private Solo solo;
    private String testComment = "This is testing comment functionality.";
    private String saltedHash = "THISi5AT3st5alTedHa5h";
    final private String USERNAME = "Username";
    SharedPreferences sharedPreferences;

    @Rule
    public ActivityTestRule<QRCollectionActivity> rule =
            new ActivityTestRule<>(QRCollectionActivity.class,true,true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name),context.MODE_PRIVATE);
    }


    @Test
    public void checkPersonalQrCode() throws Exception{
        solo.sleep(1000);

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name),context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(USERNAME, null);
        String isOwner = sharedPreferences.getString("Owner",null);
        assertTrue(solo.searchText(savedUsername));

        if (isOwner.equals("False")) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("QRCodeInstance")
                    .whereEqualTo("user", savedUsername)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        Log.d("list", queryDocumentSnapshots.getDocuments().toString());
                        for (DocumentSnapshot d : list) {
                            assertEquals(d.get("user").toString(), savedUsername);
                        }
                    }
                }
            });
        }
    }
}
