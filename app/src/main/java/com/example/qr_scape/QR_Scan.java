//Copyright 2022, Harsh Shah, Kiran Deol, Ty Greve
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Base64;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
/**
 * QR Scan Activity is for scanning the QR code
 * allows user to get the score of the scanned QR code
 * Record the image of the object/location
 * Allows users to get the geo location as Latitude, Longitude and record it
 * Through the navigation bar, the user can browse different activities
 * such as, if on QR can activity, it can switch to home, profile etc.
 * While the user scans a QR code and gets the location, it gets updated to firestore real time
 * Photos captured are in progress as we cant update it in firestore for now
 * We have score being updated on scanned QR code into the player stats, which is on profile
 * (latest update on photos is, bitmap is encoded to base64-string and we saw an instance of string in firebase as image)
 * @author Harsh Shah
 *
 */
public class QR_Scan extends AppCompatActivity  {
    String scanPhoto;
    double scanLatitude;
    double scanLongitude;
    String scanQRText;

    BottomNavigationView bottomNavigationView;
    Button scanbtn;
    ImageView imageView;
    Button btOpen;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textLatLong;
    private ProgressBar progressBar;
    public static TextView scantext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        scantext=(TextView)findViewById(R.id.scantext);
        scanbtn=(Button) findViewById(R.id.scanbtn);

        imageView = findViewById(R.id.image_view);
        btOpen = findViewById(R.id.bt_open);
        // get latitude and longitude
        textLatLong = findViewById(R.id.textLatLong);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.buttonCurrentLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            QR_Scan.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                } else {
                    getCurrentLocation();
                }
            }
        });
        //Request For Camera Permission
        if(ContextCompat.checkSelfPermission(QR_Scan.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(QR_Scan.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);

            }
        });
        // From: https://www.youtube.com
        // Link: https://www.youtube.com/watch?v=Sb4avOp7D_k
        // Author: https://www.youtube.com/channel/UCfqdbTgV61qlbEgJNw5FpiA
        // License: https://creativecommons.org/licenses/by-sa/3.0/
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ScanView.class));

                scanQRText = scantext.getText().toString();
                final String USERNAME = "Username";

                // Check shared preferences for username
                SharedPreferences sharedPreferences;
                sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
                String username = sharedPreferences.getString(USERNAME,null);
                QRCode qrCode = new QRCode(scanQRText, username, scanLatitude, scanLongitude, scanPhoto);
                // Update user's scores
                ScoreActivity scoreUpdater = new ScoreActivity(username, qrCode.getQRHash());
                scoreUpdater.updateHighestScore();
                scoreUpdater.updateNumberOfScans();
                scoreUpdater.updateLowestScore();
                scoreUpdater.updateTotalScore();
                addQRCode(scanQRText, scanLatitude, scanLongitude, scanPhoto);
            }
        });
        // From: https://www.youtube.com
        // Link: https://www.youtube.com/watch?v=lOTIedfP1OA
        // Author: https://www.youtube.com/channel/UC2Dn1EkW8zglMgNkddhRVhg
        // License: https://creativecommons.org/licenses/by-sa/3.0/
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_scan);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_scan:
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), Search.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_location: ;
                        startActivity(new Intent(getApplicationContext(), Location.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }
    /**
     * Asks user for the permission for tracking the location
     * for privacy reasons
     * If denied, it sends a toast message for permission denied
     * @param requestCode Requests for permission
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets the current location once the user grants the permission
     * required lat and longitude is captured
     */
    // From: https://www.youtube.com
    // Link: https://www.youtube.com/watch?v=ari3iD-3q8c
    // Author: https://www.youtube.com/channel/UCmL5TAblHHgh1xhabmPjYgw
    // License: https://creativecommons.org/licenses/by-sa/3.0/
    private void getCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(QR_Scan.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(QR_Scan.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() >0){
                            int latestLocationIndex = locationResult.getLocations().size() -1;
                            double latitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            textLatLong.setText(
                                    String.format(
                                            "Latitude: %s\nLongitude: %s",
                                            latitude,
                                            longitude
                                    )
                            );
                            // Set global variables
                            scanLatitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();;
                            scanLongitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();;
                            addQRCode(scanQRText, scanLatitude, scanLongitude, scanPhoto);
                        }

                        progressBar.setVisibility(View.GONE);

                    }
                }, Looper.getMainLooper());
    }
    /**
     * Captures the image via camera and gets image using bitmap
     * Imageview is used to set the image which was captured
     * @param requestCode requests for permission
     * @param resultCode checks the status
     *
     */
    // From: https://www.youtube.com
    // Link: https://www.youtube.com/watch?v=RaOyw84625w
    // Author: https://www.youtube.com/channel/UCUIF5MImktJLDWDKe5oTdJQ
    // License: https://creativecommons.org/licenses/by-sa/3.0/
    // capture image and show it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            // get capture Image
            //ByteArrayOutputStream bao = new ByteArrayOutputStream();

            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            // Set Capture Image to ImageView
            imageView.setImageBitmap(captureImage);

            // Set global variable
            //scanPhoto = captureImage;
            //addQRCode(scanQRText, scanLatitude, scanLongitude, scanPhoto);

//            captureImage.compress(Bitmap.CompressFormat.PNG, 100, bao);
//            captureImage.recycle();
//            byte[] byteArray = bao.toByteArray();
//            String imageB64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            scanPhoto = imageB64;

        }
    }

    /**
     * Add QR codes to database
     * @param QRText
     * @param latitude
     * @param longitude
     * @param photo
     * @author Ty Greve
     * @version 2
     */
    // Add QRCode Method (To be nest in the scanner class)
    public void addQRCode(String QRText, double latitude, double longitude, String photo) {
        final String USERNAME = "Username";

        // Check shared preferences for username
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME,null);

        // Validate user input
        if ((QRText.equals(null)) || (username.equals(null))) {
            Toast.makeText(QR_Scan.this, "Must fill-in both fields", Toast.LENGTH_SHORT).show();
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
            data.put("Photo", qrCode.getPhoto());
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

}
