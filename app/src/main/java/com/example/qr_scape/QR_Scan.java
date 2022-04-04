//Copyright 2022, Harsh Shah, Kiran Deol, Ty Greve,  Dallin Dmytryk
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
import androidx.appcompat.app.ActionBar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * QR Scan Activity is for scanning the QR code
 * allows user to get the score and hash of the scanned QR code
 * Record the image of the object/location
 * Allows users to get the geo location as Latitude, Longitude and record it
 * Through the navigation bar, the user can browse different activities
 * such as, if on QR can activity, it can switch to home, profile etc.
 * While the user scans a QR code and gets the location, it gets updated to firestore real time
 * Photos captured are stored in the storage and can be viewed
 * on the app via collection list as well
 * @author Harsh Shah,  Dallin Dmytryk
 *
 *
 */
public class QR_Scan extends AppCompatActivity  {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    BottomNavigationView bottomNavigationView;
    Button scanButton;
    Button locationButton;
    Button confirmButton;
    Button denyButton;
    Button photoButton;
    String scanPhoto;
    String scanQRText;
    ImageView imageView;
    LinearLayout table;
    LinearLayout confirmDenyView;
    LinearLayout detailView;
    private TextView latTextView;
    private TextView longTextView;
    private TextView hashTextView;
    private TextView scoreTextView;
    double latitude;
    double longitude;
    int score;
    String hash;
    QRCode qrCode;
    Bitmap image;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_title_layout);

        storage = FirebaseStorage.getInstance();

        // set current location button
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

        // set add photo button
        photoButton = findViewById(R.id.button_open_camera);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Request For Camera Permission
                if(ContextCompat.checkSelfPermission(QR_Scan.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QR_Scan.this,
                            new String[]{
                                    Manifest.permission.CAMERA
                            },
                            100);
                } else {
                    // Open Camera
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 100);
                }
            }
        });

        // set scan button functionality
        // From: https://www.youtube.com
        // Link: https://www.youtube.com/watch?v=Sb4avOp7D_k
        // Author: https://www.youtube.com/channel/UCfqdbTgV61qlbEgJNw5FpiA
        // License: https://creativecommons.org/licenses/by-sa/3.0/
        scanButton = findViewById(R.id.scanbtn);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                startActivityForResult(new Intent(getApplicationContext(), ScanView.class), 200);
            }
        });

        // set confirm button
        confirmButton = findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQRCode();
                ScoreActivity scoreUpdater = new ScoreActivity(qrCode.getUsername(), qrCode.getQRHash());
                scoreUpdater.updateHighestScore();
                scoreUpdater.updateNumberOfScans();
                scoreUpdater.updateLowestScore();
                scoreUpdater.updateTotalScore();
                savePhoto(image);
                clear();
            }
        });

        // set deny button
        denyButton = findViewById(R.id.button_deny);
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });

        // set detail add view
        detailView = findViewById(R.id.detail_add_view);

        // set hash text
        hashTextView = findViewById(R.id.code_hash);

        // set score text
        scoreTextView = findViewById(R.id.code_score);

        // set latt text
        latTextView = findViewById(R.id.code_latt);

        // set long text
        longTextView = findViewById(R.id.code_long);

        // set confirm deny view
        confirmDenyView = findViewById(R.id.confirm_deny_view);

        // set image view
        imageView = findViewById(R.id.image_view);

        // set detail table
        table = findViewById(R.id.info_table);

        // set bottom navigation
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
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0,0);
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
     * clear
     * resets values
     * hides ui elements
     */
    public void clear() {
        imageView.setImageBitmap(null);
        table.setVisibility(View.GONE);
        detailView.setVisibility(View.GONE);
        confirmDenyView.setVisibility(View.GONE);
        hashTextView.setText(null);
        latTextView.setText(null);
        longTextView.setText(null);
        qrCode = null;
        score = 0;
        latitude = 0;
        longitude = 0;
        hash = null;
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
                            latitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            qrCode.setLatitude(latitude);
                            longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            qrCode.setLongitude(longitude);
                            latTextView.setText(String.valueOf(latitude));
                            longTextView.setText(String.valueOf(longitude));
                            // Set global variables
                        }

                    }
                }, Looper.getMainLooper());
    }
    /**
     * Deals with results
     * If 200 then parse qrText
     * if 100 add image
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

            image = (Bitmap) data.getExtras().get("data");
            // Set Capture Image to ImageView
            imageView.setImageBitmap(image);

        } else if (requestCode == 200) {
            if (data.getStringExtra("data") != null) {
                parseQRText(data.getStringExtra("data"));
            }
        }
    }

    /**
     * parseQRText
     * creates a QRcode based off of given text
     * updates displays and makes visable
     * @param qrText String
     */
    protected void parseQRText(String qrText) {
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
        String savedUserName = sharedPreferences.getString("Username",null);
        qrCode = new QRCode(qrText, savedUserName, 0, 0, null);
        hashTextView.setText(qrCode.getQRHash());
        detailView.setVisibility(View.VISIBLE);
        confirmDenyView.setVisibility(View.VISIBLE);
        table.setVisibility(View.VISIBLE);
        scoreTextView.setText(String.valueOf(qrCode.getScore()));
    }

    /**
     * Add QR codes to database
     * @author Ty Greve
     * @version 2
     */
    // Add QRCode Method (To be nest in the scanner class)
    public void addQRCode() {

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create HashMap for QRCodeInstance and put fields from the qrCode object into it
        HashMap<String, Object> data = new HashMap<>();
        data.put("Latitude", qrCode.getLatitude());
        data.put("Longitude", qrCode.getLongitude());
        data.put("Photo", qrCode.getPhoto());
        data.put("Score", qrCode.getScore());
        data.put("Username", qrCode.getUsername());
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
                        Log.d(TAG, "Data could not be added!" + e);
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
                        Log.d(TAG, "Data could not be added!" + e);
                    }
                });

    }

    /**
     * savePhoto
     * compresses and saves a photo to the database
     * saved as the qrCode saltedhash.jpg
     * @param image a bitmao containg the image to be saved
     */
    private void savePhoto(Bitmap image) {
        if (image != null) {
            // compress image and covert to byte stream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] byteArray = baos.toByteArray();

            // upload image
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child(qrCode.getQRHashSalted() + ".jpg");
            imageRef.putBytes(byteArray)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("image upload", "Successfully uploaded photo");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("image upload", "Failed to upload photo");
                        }
                    });
        }
    }

}
