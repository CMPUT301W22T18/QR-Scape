//Copyright 2022, Ty Greve, Kash Sansanwal
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
package com.example.qr_scape;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Handles the user's collection of QR codes (displaying, deleting, adding, etc.)
 */

public class QRCollectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<QRCode> qrDataList;
    private FirebaseFirestore db;
    private QRCollectionAdapter qrCollectionAdapter;
    SharedPreferences sharedPreferences;
    BottomNavigationView bottomNavigationView;
    //ListView qrList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_collections_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_title_layout);

        recyclerView=(RecyclerView)findViewById(R.id.qr_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        qrDataList = new ArrayList<>();
        qrCollectionAdapter = new QRCollectionAdapter(qrDataList);
        recyclerView.setAdapter(qrCollectionAdapter);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);


        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
        String savedUserName = sharedPreferences.getString("Username",null);
        String isOwner = sharedPreferences.getString("Owner",null);
        db = FirebaseFirestore.getInstance();
        qrCollectionAdapter.notifyDataSetChanged();

        if (isOwner.equals("True")){
            db.collection("QRCodeInstance")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(!queryDocumentSnapshots.isEmpty()){
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        Log.d("list", queryDocumentSnapshots.getDocuments().toString());
                        for (DocumentSnapshot d : list){

                            QRCode qr = d.toObject(QRCode.class);
                            String qr_username = d.getString("Username");
                            Integer qr_scoreLong = Math.toIntExact(d.getLong("Score"));
                            String qr_realHash = d.getString("RealHash");
                            String qr_saltedHash = d.getId().toString();
                            //String qr_Photo = d.getString("Photo");
                            Double qr_Longitude = d.getDouble("Longitude");
                            Double qr_Latitude = d.getDouble("Latitude");
                            QRCode qrCode = new QRCode(qr_realHash,qr_saltedHash ,qr_username, qr_Latitude, qr_Longitude,null, qr_scoreLong);
                            qrDataList.add(qrCode);
                        }
                        qrCollectionAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        else{
            db.collection("QRCodeInstance")
                    .whereEqualTo("Username",savedUserName)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(!queryDocumentSnapshots.isEmpty()){
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        Log.d("list", queryDocumentSnapshots.getDocuments().toString());
                        for (DocumentSnapshot d : list){

                            QRCode qr = d.toObject(QRCode.class);
                            String qr_username = d.getString("Username");
                            Integer qr_scoreLong = Math.toIntExact(d.getLong("Score"));
                            String qr_realHash = d.getString("RealHash");
                            String qr_saltedHash = d.getId().toString();
                            //String qr_Photo = d.getString("Photo");
                            Double qr_Longitude = d.getDouble("Longitude");
                            Double qr_Latitude = d.getDouble("Latitude");
                            QRCode qrCode = new QRCode(qr_realHash,qr_saltedHash ,qr_username, qr_Latitude, qr_Longitude,null, qr_scoreLong);
                            qrDataList.add(qrCode);
                        }
                        qrCollectionAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

//        bottomNavigationView.setSelectedItemId(R.id.nav_home);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.nav_home:
//                        startActivity(new Intent(getApplicationContext(), Home.class));
//                        overridePendingTransition(0,0);
//                        return true;
//
//                    case R.id.nav_scan:
//                        startActivity(new Intent(getApplicationContext(), QR_Scan.class));
//                        overridePendingTransition(0,0);
//                        return true;
//                    case R.id.nav_search:
//                        startActivity(new Intent(getApplicationContext(), Search.class));
//                        overridePendingTransition(0,0);
//                        return true;
//                    case R.id.nav_profile:
//                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                        overridePendingTransition(0,0);
//                        return true;
//                    case R.id.nav_location:
//                        startActivity(new Intent(getApplicationContext(), Location.class));
//                        overridePendingTransition(0,0);
//                        return true;
//                }
//                return false;
//            }
//        });
//
    }


    /**
     * Deletes an instance of a user QR code from the database
     * @param qrCode
     * @author Ty Greve
     * @version 1
     */
    public void deleteQRCode(QRCode qrCode){
        // Deletes an instance (scan by a user) of a QR code. QRCode (real/physical) remains in the database

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Delete the document from the Firestore QRCodeInstance Collection
        // Get reference to Firestore collection and Document ID
        db.collection("QRCodeInstance").document(qrCode.getQRHashSalted())
                .delete() // delete document in the Firestore database
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document has been deleted successfully!");
                        if (qrDataList.contains(qrCode)){
                            qrDataList.remove(qrCode);
                        }
                        qrCollectionAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error deleting the document!" + e.toString());
                    }
                });
    }//end deleteQRCode

    /**
     * Delete every instance of user QR codes (scanned instances) and the
     * real/physical QR code from the database
     * @param qrCode
     * @author Ty Greve
     * @version 1
     */
    public void ownerDeleteQRCode(QRCode qrCode){
        // Deletes a QRCode (real/physical) and EVERY user instances (scans) of that QR code

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // From: https://stackoverflow.com/
        // Link: https://stackoverflow.com/a/49147139
        // Author: https://stackoverflow.com/users/5246885/alex-mamo
        // License: https://creativecommons.org/licenses/by-sa/3.0/
        // Delete every document from the Firestore QRCodeInstance Collection that has the realHash value
        // Get reference to Firestore collection
        CollectionReference itemsRef = db.collection("QRCodeInstance");
        // Make query is realHash == qrcode.getQRHash()
        Query query = itemsRef.whereEqualTo("realHash", qrCode.getQRHash());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        itemsRef.document(document.getId()).delete();
                        if (qrDataList.contains(qrCode)){
                            qrDataList.remove(qrCode);
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        // Delete the document from the Firestore QRCodes (real/physical) Collection
        // Get reference to Firestore collection and Document ID
        db.collection("QRCodes").document(qrCode.getQRHash())
                .delete() // delete document in the Firestore database
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document has been deleted successfully!");
                        if (qrDataList.contains(qrCode)){
                            qrDataList.remove(qrCode);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error deleting the document!" + e.toString());
                    }
                });
    }//end ownerDeleteQRCode

    /**
     * Add comment on QR code to database
     *
     * @author Ty Greve
     * @version 2
     */
    // Add QRCode Method (To be nest in the scanner class)
    public void addComment(String comment, String QRHashSalted) {
        final String USERNAME = "Username";

        // Check shared preferences for username of the user that is making the comment
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME, null);

        // Validate user input
        if ((comment.equals(null)) || (username.equals(null))) {
            Toast.makeText(QRCollectionActivity.this, "Must fill-in a comment.", Toast.LENGTH_SHORT).show();
        }


        if (comment.length() > 0 && username.length() > 0) {

            // Access a Cloud Firestore instance from your Activity
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create HashMap for QRCodeInstance and put fields from the qrCode object into it
            HashMap<String, Object> data = new HashMap<>();
            data.put("commentText", comment);
            data.put("qrInstance", QRHashSalted);
            data.put("user", username);
            data.put("timestamp", "time_stamp");

            // Store to Firestore the QRCodeInstance
            // Get reference to Firestore collection and Document ID
            db.collection("Comments").document()
                    .set(data) // Set fields in the Firestore database
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Data has been added successfully!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Data could not be added!" + e.toString());
                        }
                    });
        }

    }
}
