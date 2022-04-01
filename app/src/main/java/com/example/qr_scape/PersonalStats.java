//Copyright 2022, Kiran Deol
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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Displays a user's 4 main personal stats: highest/lowest score, total score, sum scans
 * @author Kiran Deol
 */
public class PersonalStats extends AppCompatActivity {
    TextView numScans;
    TextView numScansValue;
    TextView highestScore;
    TextView highestScoreValue;
    TextView lowestScore;
    TextView lowestScoreValue;
    TextView sumScores;
    TextView sumScoresValue;
    private static final String TAG = "PersonalStats";
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_stats);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_title_layout);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference profilesRef = db.collection("Profiles");
        numScans = findViewById(R.id.num_codes);
        numScansValue = findViewById(R.id.num_codes_value);
        highestScore = findViewById(R.id.highest_score);
        highestScoreValue = findViewById(R.id.highest_score_value);
        lowestScore = findViewById(R.id.lowest_score);
        lowestScoreValue = findViewById(R.id.lowest_score_value);
        sumScores = findViewById(R.id.sum_scores);
        sumScoresValue = findViewById(R.id.sum_scores_value);
        Intent intent = getIntent();
        String profile = intent.getStringExtra("Profile");
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
        final Object[] currentNumberOfScans = new Object[1];
        final Object[] highestScore = new Object[1];
        final Object[] lowestScore = new Object[1];
        final Object[] totalScore = new Object[1];
        Task<DocumentSnapshot> userRef = profilesRef.document(profile).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentNumberOfScans[0] = document.get("Total Scans");
                        highestScore[0] = document.get("Highest Score");
                        lowestScore[0] = document.get("Lowest Score");
                        totalScore[0] = document.get("Total Score");

                        if (currentNumberOfScans[0]  == null) {
                            numScansValue.setText(String.valueOf(0));
                        } else {
                            numScansValue.setText(String.valueOf(currentNumberOfScans[0]));
                        }

                        if (highestScore[0]  == null) {
                            highestScoreValue.setText(String.valueOf(0));
                        } else {
                            highestScoreValue.setText(String.valueOf(highestScore[0]));
                        }

                        if (lowestScore[0]  == null) {
                            lowestScoreValue.setText(String.valueOf(0));
                        } else {
                            lowestScoreValue.setText(String.valueOf(highestScore[0]));
                        }

                        if (totalScore[0]  == null) {
                            sumScoresValue.setText(String.valueOf(0));
                        } else {
                            sumScoresValue.setText(String.valueOf(totalScore[0]));
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
