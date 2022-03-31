//Copyright 2022, Ty Greve
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * Handle the collection of comments on QR codes (displaying, adding, etc.)
 */

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Comment> qrDataList;
    private FirebaseFirestore db;
    private CommentAdapter commentAdapter;
    String saltedHash;
    //ListView qrList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=(RecyclerView)findViewById(R.id.comment_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        qrDataList = new ArrayList<>();
        commentAdapter = new CommentAdapter(qrDataList);
        recyclerView.setAdapter(commentAdapter);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        saltedHash = intent.getStringExtra("saltedHash");

        db.collection("Comments")
            .whereEqualTo("qrInstance", saltedHash)
            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    Log.d("list", queryDocumentSnapshots.getDocuments().toString());
                    for (DocumentSnapshot d : list){

                        Comment comment = d.toObject(Comment.class);
                        String comment_username = d.getString("user");
                        String comment_saltedHash = d.getString("qrInstance");
                        String comment_text = d.getString("commentText");
                        String comment_timestamp = d.getString("timestamp");
                        Comment comment1 = new Comment(comment_saltedHash, comment_text, comment_timestamp, comment_username);
                        qrDataList.add(comment1);
                    }
                    commentAdapter.notifyDataSetChanged();
                }
            }
        });

        Button addComment = findViewById(R.id.addCommentButton);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String saltedHash = qr_hash.getText().toString();

                EditText commentEditText = findViewById(R.id.editTextComment);
                String commentText = commentEditText.getText().toString();
                if (commentText.equalsIgnoreCase(null)){
                    Toast.makeText(CommentActivity.this, "Must fill-in comment field", Toast.LENGTH_SHORT).show();
                } else {
                    addComment(commentText, saltedHash);
                    commentEditText.setText(null);
                }
                commentAdapter.notifyDataSetChanged();
            }
        });

    }


    /**
     * Deletes an instance of a user QR code from the database
     *
     * @param qrCode
     * @author Ty Greve
     * @version 1
     */
    public void deleteQRCode(QRCode qrCode) {
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
     *
     * @param qrCode
     * @author Ty Greve
     * @version 1
     */
    public void ownerDeleteQRCode(QRCode qrCode) {
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

        // Get current timestamp
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy/hh:mm:ss");
        String timestamp = s.format(new Date());

        // Validate user input
        if ((comment.equals(null)) || (username.equals(null))) {
            Toast.makeText(CommentActivity.this, "Must fill-in a comment.", Toast.LENGTH_SHORT).show();
        }


        if (comment.length() > 0 && username.length() > 0) {

            // Access a Cloud Firestore instance from your Activity
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create HashMap for QRCodeInstance and put fields from the qrCode object into it
            HashMap<String, Object> data = new HashMap<>();
            data.put("commentText", comment);
            data.put("qrInstance", QRHashSalted);
            data.put("user", username);
            data.put("timestamp", timestamp);

            // Add comment to data list
            Comment commentObj = new Comment(QRHashSalted, comment, timestamp, username);
            qrDataList.add(commentObj);


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