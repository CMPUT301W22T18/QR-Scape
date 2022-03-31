package com.example.qr_scape;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Expanded_QR_View extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_qr_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView qr_user = findViewById(R.id.tv_username);
        TextView qr_hash = findViewById(R.id.tv_hash);
        TextView qr_score = findViewById(R.id.tv_score);
        TextView qr_longitude = findViewById(R.id.tv_longitude);
        TextView qr_latitude = findViewById(R.id.tv_latitude);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String score = intent.getStringExtra("score");
        String hash = intent.getStringExtra("hash");
        String longitude = intent.getStringExtra("long");
        String latitude = intent.getStringExtra("lat");

        qr_user.setText(username);
        qr_hash.setText(hash);
        qr_score.setText(score);
        qr_longitude.setText(longitude);
        qr_latitude.setText(latitude);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_scan:
                        startActivity(new Intent(getApplicationContext(), QR_Scan.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), Search.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_profile:
                        return true;

                    case R.id.nav_location:
                        startActivity(new Intent(getApplicationContext(), Location.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        Button openComments = findViewById(R.id.seeCommentsButton);
        openComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String saltedHash = qr_hash.getText().toString();
//                deleteQRCode(saltedHash);

                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                intent.putExtra("saltedHash", hash);
                view.getContext().startActivity(intent);

                //startActivity(new Intent(Expanded_QR_View.this, CommentActivity.class));
            }
        });

    }

    /**
     * Delete every instance of user QR codes (scanned instances) and the
     * real/physical QR code from the database
     * @author Ty Greve, Kiran Deol
     * @version 2
     */
    public void ownerDeleteQRCode(View view){
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String score = intent.getStringExtra("score");
        String hash = intent.getStringExtra("hash");
        String longitude = intent.getStringExtra("long");
        String latitude = intent.getStringExtra("lat");

        QRCode qrCode = new QRCode(hash, Double.parseDouble(latitude), Double.parseDouble(longitude), Integer.parseInt(score), username);
        // Deletes a QRCode (real/physical) and EVERY user instances (scans) of that QR code
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        String isOwner = sharedPreferences.getString("Owner", null);
        if (isOwner.equals("True")) {
            Log.d("Can they delete the QR code? ", "Is an owner, can delete!");
            startActivity(new Intent(Expanded_QR_View.this, QRCollectionActivity.class));

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
            Query query = itemsRef.whereEqualTo("RealHash", qrCode.getQRHash()).whereEqualTo("Username", qrCode.getUsername());
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
        } else {
            Log.d("Can they delete the QR code? ", "Not an owner, can't delete!");
        }

    }//end ownerDeleteQRCode
}
