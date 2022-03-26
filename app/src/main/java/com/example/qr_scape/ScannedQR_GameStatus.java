package com.example.qr_scape;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ScannedQR_GameStatus extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    TextView usernameText;
    final String USERNAME = "USERNAME";
    final String PROFILES = "Profiles";
    TextView highestText;
    TextView lowestText;
    TextView totalScoreText;
    TextView totalScansText;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_qr_game_status);
        // set bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
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
        usernameText = findViewById(R.id.otherprofile_username_text);
        highestText = findViewById(R.id.otherprofile_highest_text);
        lowestText = findViewById(R.id.otherprofile_lowest_text);
        totalScoreText = findViewById(R.id.otherprofile_totalscore_text);
        totalScansText = findViewById(R.id.otherprofile_totalscans_text);
//
        // Get user from intent
        Intent intent = getIntent();
        String username = intent.getStringExtra(USERNAME);
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(PROFILES).document(username);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    Log.d(null, "Successfully loaded profile");
                    // set username
                    usernameText.setText(username);

                    final Object[] currentNumberOfScans = new Object[1];
                    final Object[] highestScore = new Object[1];
                    final Object[] lowestScore = new Object[1];
                    final Object[] totalScore = new Object[1];
                    currentNumberOfScans[0] = value.get("Total Scans");
                    highestScore[0] = value.get("Highest Score");
                    lowestScore[0] = value.get("Lowest Score");
                    totalScore[0] = value.get("Total Score");
                    if (currentNumberOfScans[0]  == null) {
                        totalScansText.setText(String.valueOf(0));
                    } else {
                        totalScansText.setText(String.valueOf(currentNumberOfScans[0]));
                    }

                    if (highestScore[0]  == null) {
                        highestText.setText(String.valueOf(0));
                    } else {
                        highestText.setText(String.valueOf(highestScore[0]));
                    }

                    if (lowestScore[0]  == null) {
                        lowestText.setText(String.valueOf(0));
                    } else {
                        lowestText.setText(String.valueOf(highestScore[0]));
                    }

                    if (totalScore[0]  == null) {
                        totalScoreText.setText(String.valueOf(0));
                    } else {
                        totalScoreText.setText(String.valueOf(totalScore[0]));
                    }
                } else {
                    Log.d(null, "Failed to load profile");
                }
            }
        });


    }
}