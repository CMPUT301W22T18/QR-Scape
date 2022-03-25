package com.example.qr_scape;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class Expanded_QR_View extends AppCompatActivity {

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
        Button deleteScan = findViewById(R.id.deleteScanButton);

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

        deleteScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteQRCode(hash);
                startActivity(new Intent(Expanded_QR_View.this, QRCollectionActivity.class));
            }
        });

    }

    /**
     * Deletes an instance of a user QR code from the database
     * @param hashSalted
     * @author Ty Greve
     * @version 1
     */
    public void deleteQRCode(String hashSalted){
        // Deletes an instance (scan by a user) of a QR code. QRCode (real/physical) remains in the database

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Delete the document from the Firestore QRCodeInstance Collection
        // Get reference to Firestore collection and Document ID
        db.collection("QRCodeInstance").document(hashSalted)
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

}