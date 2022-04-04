package com.example.qr_scape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;

public class ExpandedQRViewTest {
//    private Solo solo;
//    private final String Username = "Username";
//    SharedPreferences sharedPreferences;
//
//
//    @Rule
//    public ActivityTestRule<Expanded_QR_View> rule =
//            new ActivityTestRule<>(Expanded_QR_View.class,true,true);
//
//    @Before
//    public void setUp() throws Exception {
//        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
//        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);
//    }

//    @Test
//    public void checkExpandedQR() throws Exception{
//
//        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name),context.MODE_PRIVATE);
//        String savedUsername = sharedPreferences.getString("Username", null);
//        String isOwner = sharedPreferences.getString("Owner",null);
//        assertTrue(solo.searchText(savedUsername));
//
//        if (isOwner.equals("False")) {
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            db.collection("QRCodeInstance")
//                    .whereEqualTo("user", savedUsername)
//                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                @RequiresApi(api = Build.VERSION_CODES.N)
//                @Override
//                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                        Log.d("list", queryDocumentSnapshots.getDocuments().toString());
//                        for (DocumentSnapshot d : list) {
//                            solo.assertCurrentActivity("Check current activity is ExpandedQRView", Expanded_QR_View.class);
//                            assertEquals(d.get("user").toString(), savedUsername);
//                        }
//                    }
//                }
//            });
//        }
//    }
}
