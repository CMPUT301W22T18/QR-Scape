package com.example.qr_scape;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Expanded_QR_View Activity
 * Open each QR code instance in a detailed view with all the information(hash,location,scanned by)
 * This class also helps to see comments on the qr codes and delete the qr code.
 * @author Kashish Sansanwal
 * @version 2
 */

public class Expanded_QR_View extends AppCompatActivity {
    private FirebaseFirestore db;
    SharedPreferences sharedPreferences;
    private ArrayList<QRCode> qrHashList;
    BottomNavigationView bottomNavigationView;
    String saltedHash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_qr_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_title_layout);

        TextView qr_user = findViewById(R.id.tv_username);
        TextView qr_hash = findViewById(R.id.tv_hash);
        TextView qr_score = findViewById(R.id.tv_score);
        TextView qr_longitude = findViewById(R.id.tv_longitude);
        TextView qr_latitude = findViewById(R.id.tv_latitude);
        TextView qr_scannedBy = findViewById(R.id.tv_scannedBy);
        ImageView qr_image = findViewById(R.id.iv_qr);


        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String score = intent.getStringExtra("score");
        saltedHash = intent.getStringExtra("hash");
        String longitude = intent.getStringExtra("long");
        String latitude = intent.getStringExtra("lat");
        String realHash = intent.getStringExtra("realHash");
        downloadImage(intent.getStringExtra("hash"), qr_image);

        qr_user.setText(username);
        qr_hash.setText(realHash.substring(0,10));
        qr_score.setText(score);
        qr_longitude.setText(longitude);
        qr_latitude.setText(latitude);

        qrHashList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        //Get the salted hashes stored inside an arraylist
        db.collection("QRCodeInstance")
                .whereEqualTo("RealHash",realHash)
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
        String currentUser = sharedPreferences.getString("Username",null);
        if (isOwner.equals("True") || currentUser.equals(username)) {
            deleteButton.setVisibility(View.VISIBLE);
        }

        //Bottom Navigation bar
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
                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                intent.putExtra("saltedHash", saltedHash);
                view.getContext().startActivity(intent);
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
        Log.d("Can they delete the QR code? ", "Is an owner, can delete!");


        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // From: https://stackoverflow.com/
        // Link: https://stackoverflow.com/a/49147139
        // Author: https://stackoverflow.com/users/5246885/alex-mamo
        // License: https://creativecommons.org/licenses/by-sa/3.0/
        // Delete every document from the Firestore QRCodeInstance Collection that has the realHash value
        // Get reference to Firestore collection
        CollectionReference itemsRef = db.collection("QRCodeInstance");

        // Delete the document from the Firestore QRCodes (real/physical) Collection
        // Get reference to Firestore collection and Document ID
        itemsRef.document(qrCode.getQRHash())
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
                        Log.d(TAG, "Error deleting the document!" + e);
                    }
                });

        startActivity(new Intent(Expanded_QR_View.this, QRCollectionActivity.class));
    }//end ownerDeleteQRCode

    /**
     * dowloadImage
     * Will download if possible an image for a qr code scan
     * and apply it to the view
     * @author Dallin Dmytryk
     * @param hash String representing the qr instance hash
     * @param view ImageView to apply image to
     */
    private void downloadImage(String hash, ImageView view) {
        final long SIZE = 1024*64;
        String imageUrl = hash + ".jpg";

        // get storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathRef  = storageRef.child(imageUrl);

        // get image
        pathRef.getBytes(SIZE).
                addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d("Image Load", "Image loaded");
                        Bitmap image = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                        view.setImageBitmap(image);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Image Load", "Failed to load Image");
                    }
                });
    }
}
