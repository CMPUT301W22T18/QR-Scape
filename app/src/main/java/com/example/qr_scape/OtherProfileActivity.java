/*
 * OtherProfileActivity
 *
 * Version 1
 *
 * March 22 2022
 * Copyright 2022 Dallin Dmytryk, Kiran Deol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.qr_scape;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * OtherProfileActivity
 * Activity meant to display the info of another user
 * Displays username and contact info
 * to be started with an intent containing a User
 * @author Dallin Dmytryk
 * @version 1
 */
public class OtherProfileActivity extends AppCompatActivity {
    final String USERNAME = "USERNAME";
    final String PROFILES = "Profiles";
    final String CONTACTINFO = "Contact info";
    BottomNavigationView bottomNavigationView;
    TextView usernameText;
    TextView contactInfoText;
    TextView highestText;
    TextView lowestText;
    TextView totalScoreText;
    TextView totalScansText;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_title_layout);

        // set bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        return true;

                    case R.id.nav_scan:
                        startActivity(new Intent(getApplicationContext(), QR_Scan.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(), Search.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_location:
                        startActivity(new Intent(getApplicationContext(), Location.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        usernameText = findViewById(R.id.otherprofile_username_text);
        contactInfoText = findViewById(R.id.otherprofile_contact_info_text);
        highestText = findViewById(R.id.otherprofile_highest_text);
        lowestText = findViewById(R.id.otherprofile_lowest_text);
        totalScoreText = findViewById(R.id.otherprofile_totalscore_text);
        totalScansText = findViewById(R.id.otherprofile_totalscans_text);

        // Get user from intent
        Intent intent = getIntent();
        String username = intent.getStringExtra(USERNAME);

        // Set database
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(PROFILES).document(username);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    Log.d(null, "Successfully loaded profile");
                    // set username
                    usernameText.setText(username);
                    // set contact info text
                    String infoText = value.getString(CONTACTINFO);
                    contactInfoText.setText(infoText);
                    final Object[] currentNumberOfScans = new Object[1];
                    final Object[] highestScore = new Object[1];
                    final Object[] lowestScore = new Object[1];
                    final Object[] totalScore = new Object[1];
                    currentNumberOfScans[0] = value.get("Total Scans");
                    highestScore[0] = value.get("Highest Score");
                    lowestScore[0] = value.get("Lowest Score");
                    totalScore[0] = value.get("Total Score");
                    if (currentNumberOfScans[0] == null) {
                        totalScansText.setText(String.valueOf(0));
                    } else {
                        totalScansText.setText(String.valueOf(currentNumberOfScans[0]));
                    }

                    if (highestScore[0] == null) {
                        highestText.setText(String.valueOf(0));
                    } else {
                        highestText.setText(String.valueOf(highestScore[0]));
                    }

                    if (lowestScore[0] == null) {
                        lowestText.setText(String.valueOf(0));
                    } else {
                        lowestText.setText(String.valueOf(highestScore[0]));
                    }

                    if (totalScore[0] == null) {
                        totalScoreText.setText(String.valueOf(0));
                    } else {
                        totalScoreText.setText(String.valueOf(totalScore[0]));
                    }
                } else {
                    Log.d(null, "Failed to load profile");
                }
            }
        });

        Button deleteProfileButton = findViewById(R.id.deleteProfileButton);
        deleteProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = usernameText.getText().toString();
                deleteProfile(user);
                startActivity(new Intent(OtherProfileActivity.this, Search.class));
            }
        });

    }


    /**
     * Deletes the Profile of a user from the database
     * @param username
     * @author Ty Greve
     * @version 1
     */
    public void deleteProfile(String username){
        // Deletes an profile document in the database

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Delete the document from the Firestore Profiles Collection
        // Get reference to Firestore collection and Document ID
        db.collection("Profiles").document(username)
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
    }//end deleteProfile

}