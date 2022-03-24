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

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    ListView qrList;
    ArrayAdapter<String> qrListAdapter;
    ArrayList<String> qrDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_collections_layout);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference QRRef = db.collection("QRCodeInstance");

        //qrDataList = new ArrayList<String>();

        qrList = findViewById(R.id.qrList);

        // qrHashes: This will store all the hash codes of the QR codes which are added.
        //String[] qrHashes = {"Hello", "You", "Are", "Amazing"};

        qrDataList = new ArrayList<>();
        //qrDataList.addAll(Arrays.asList(qrHashes));

        qrListAdapter = new ArrayAdapter<>(this, R.layout.qr_list_content, qrDataList);
        qrList.setAdapter(qrListAdapter);

        Task<QuerySnapshot> qrRef = db.collection("QRCodeInstance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> hash_obj = document.getData();
                        String hash_value = (String) hash_obj.toString();
                        qrDataList.add(hash_value);
                        qrListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

//        QRRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
//                    FirebaseFirestoreException error) {
//                // Clear the old list
//                qrDataList.clear();
//                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
//                {
//                    Log.d(TAG, String.valueOf(doc.getData().get("Province Name")));
//                    String saltedHash = doc.getId();
//                    String realHash = (String) doc.getData().get("realHash");
//                    Bitmap photo = (Bitmap) doc.getData().get("Photo");
//                    double latitude = (double) doc.getData().get("Latitude");
//                    double longitude = (double) doc.getData().get("Longitude");
//                    int score = (int) doc.getData().get("Score");
//                    String username = (String) doc.getData().get("Username");
//                    qrDataList.add(new QRCode(realHash, saltedHash, username, latitude, longitude, photo, score)); // Adding the cities and provinces from FireStore
//                }
//                qrListAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
//            }
//        });
////
//
//        deleteQRCode(qrDataList.get(1));

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
     * @param QRText
     * @param latitude
     * @param longitude
     * @param photo
     * @author Ty Greve
     * @version 2
     */
    // Add QRCode Method (To be nest in the scanner class)
    public void addComment(String comment, String QRHashSalted) {
        final String USERNAME = "Username";

        // Check shared preferences for username of the user that is making the comment
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME,null);

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
            db.collection("Comments").document(qrCode.getQRHashSalted())
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
