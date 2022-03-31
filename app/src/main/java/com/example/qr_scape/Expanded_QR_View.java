package com.example.qr_scape;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

import java.util.ArrayList;
import java.util.List;

public class Expanded_QR_View extends AppCompatActivity {

    private FirebaseFirestore db;
    SharedPreferences sharedPreferences;
    private ArrayList<QRCode> qrHashList;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_qr_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
        String savedUserName = sharedPreferences.getString("Username",null);

        TextView qr_user = findViewById(R.id.tv_username);
        TextView qr_hash = findViewById(R.id.tv_hash);
        TextView qr_score = findViewById(R.id.tv_score);
        TextView qr_longitude = findViewById(R.id.tv_longitude);
        TextView qr_latitude = findViewById(R.id.tv_latitude);
        TextView qr_scannedBy = findViewById(R.id.tv_scannedBy);

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

        qrHashList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        db.collection("QRCodeInstance")
                .whereEqualTo("RealHash",hash)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    Log.d("hash_list", queryDocumentSnapshots.getDocuments().toString());
                    for (DocumentSnapshot d : list){

                        QRCode qr = d.toObject(QRCode.class);
                        String qr_username = d.getString("Username");
                        Integer qr_scoreLong = Math.toIntExact(d.getLong("Score"));
                        String qr_realHash = d.getString("RealHash");
                        Double qr_Longitude = d.getDouble("Longitude");
                        Double qr_Latitude = d.getDouble("Latitude");
                        QRCode qrCode = new QRCode(qr_realHash, qr_Latitude, qr_Longitude, qr_scoreLong,qr_username);
                        qrHashList.add(qrCode);
                        Log.d("size",String.valueOf(qrHashList.size()));
                        String scannedBy = String.valueOf(qrHashList.size());
                        qr_scannedBy.setText(scannedBy);
                    }
                }
            }
        });

        Button deleteButton;
        deleteButton = (Button) findViewById(R.id.owner_delete_qrcode);
        deleteButton.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        String isOwner = sharedPreferences.getString("Owner", null);
        if (isOwner.equals("True")) {
            deleteButton.setVisibility(View.VISIBLE);
        }

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
