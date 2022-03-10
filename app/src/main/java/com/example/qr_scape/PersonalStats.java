package com.example.qr_scape;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PersonalStats extends AppCompatActivity {
    TextView numScans;
    TextView numScansValue;
    TextView highestScore;
    TextView highestScoreValue;
    TextView sumScores;
    TextView sumScoresValue;
    private static final String TAG = "PersonalStats";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_stats);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference profilesRef = db.collection("Profiles");
        numScans = findViewById(R.id.num_codes);
        numScansValue = findViewById(R.id.num_codes_value);
        highestScore = findViewById(R.id.highest_score);
        highestScoreValue = findViewById(R.id.highest_score_value);
        sumScores = findViewById(R.id.sum_scores);
        sumScoresValue = findViewById(R.id.sum_scores_value);
        Intent intent = getIntent();
        String profile = intent.getStringExtra("Profile");
        final Object[] currentNumberOfScans = new Object[1];
        final Object[] highestScore = new Object[1];
        final Object[] totalScore = new Object[1];
        Task<DocumentSnapshot> userRef = profilesRef.document(profile).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        currentNumberOfScans[0] = document.get("Total Scans");
                        highestScore[0] = document.get("Highest Score");
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
