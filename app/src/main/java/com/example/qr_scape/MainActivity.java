package com.example.qr_scape;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ArrayAdapter<String> qrListAdapter;
    ArrayList<QRCode> qrDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent intent = new Intent(this,LoginActivity.class);
        //startActivity(intent);

        //addQRCode("QRText1", "Brick", , null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference QRRef = db.collection("QRCodeInstance");

        qrDataList = new ArrayList<QRCode>();

        QRRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                // Clear the old list
                qrDataList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    Log.d(TAG, String.valueOf(doc.getData().get("Province Name")));
                    String saltedHash = doc.getId();
                    String realHash = (String) doc.getData().get("realHash");
                    Bitmap photo = (Bitmap) doc.getData().get("Photo");
                    double latitude = (double) doc.getData().get("Latitude");
                    double longitude = (double) doc.getData().get("Longitude");
                    int score = (int) doc.getData().get("Score");
                    String username = (String) doc.getData().get("Username");
                    qrDataList.add(new QRCode(realHash, saltedHash, username, latitude, longitude, photo, score)); // Adding the cities and provinces from FireStore
                }
                qrListAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
            }
        });
//

        deleteQRCode(qrDataList.get(1));
    }

    /**
     * Add QR codes to database
     * @param QRText
     * @param username
     * @param latitude
     * @param longitude
     * @param photo
     * @author Ty Greve
     * @version 2
     */
    // Add QRCode Method (To be nest in the scanner class)
    public void addQRCode(String QRText, String username, double latitude, double longitude, Bitmap photo) {
        // Validate user input
        if ((QRText.equals(null)) || (username.equals(null))) {
            Toast.makeText(MainActivity.this, "Must fill-in both fields", Toast.LENGTH_SHORT).show();
        }

        // Create QRCode Instance object
        QRCode qrCode = new QRCode(QRText, username, latitude, longitude, photo);


        if (QRText.length() > 0 && username.length() > 0) {

            // Access a Cloud Firestore instance from your Activity
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create HashMap for QRCodeInstance and put fields from the qrCode object into it
            HashMap<String, Object> data = new HashMap<>();
            data.put("Latitude", qrCode.getLatitude());
            data.put("Longitude", qrCode.getLongitude());
            data.put("Photo", null);
            data.put("Score", qrCode.getScore());
            data.put("Username", username);
            data.put("RealHash", qrCode.getQRHash());

            // Create HashMap for QRCodes (real/physical) and put fields into it
            HashMap<String, Object> data1 = new HashMap<>();
            data1.put("Score", qrCode.getScore());

            // Store to Firestore the QRCodeInstance
            // Get reference to Firestore collection and Document ID
            db.collection("QRCodeInstance").document(qrCode.getQRHashSalted())
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

            // Store to Firestore the QRCode (real/physical)
            db.collection("QRCodes").document(qrCode.getQRHash())
                    .set(data1) // Set fields in the Firestore database
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
    }//end addQRCode

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
}