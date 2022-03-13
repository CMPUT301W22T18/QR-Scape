package com.example.qr_scape;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;


public class ScoreActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference profilesRef = db.collection("Profiles");
    final CollectionReference qrCodesRef = db.collection("QRCodes");
    private static final String TAG = "ScoreActivity";
    String profile;
    String qrHash;
    int score;

    public ScoreActivity(String currentProfile, String QRHash) {
        this.profile = currentProfile;
        this.qrHash = QRHash;
        this.score = QRCode.calculateScore(qrHash);
    }

    public void updateNumberOfScans() {
        /**
         * Updates a user's total number of scans
         * Inputs: none
         * Outputs: int - new total number of scans
         */
        final Object[] currentNumberOfScans = new Object[1];
        Task<DocumentSnapshot> userRef = profilesRef.document(profile).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.get("Total Scans"));
                        currentNumberOfScans[0] = document.get("Total Scans");
                        Log.d(TAG, "current number of scans: " + currentNumberOfScans[0]);
                        if (currentNumberOfScans[0]  == null) {
                            Log.d(TAG, "DocumentSnapshot data: " + "it is NULL");
                            HashMap<String, Integer> data = new HashMap<>();
                            data.put("Total Scans", 1);
                            profilesRef.document(profile).set(data, SetOptions.merge());
                        } else {
                            Log.d(TAG, "DocumentSnapshot data: " + "it is not null");
                            HashMap<String, Integer> data = new HashMap<>();
                            data.put("Total Scans", Integer.parseInt(currentNumberOfScans[0].toString()) + 1);
                            profilesRef.document(profile).set(data, SetOptions.merge());
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
    public void updateHighestScore() {
        /**
         * Updates a user's highest score
         * Inputs: none
         * Outputs: int - new total number of scans
         */
        final Object[] highestScores = new Object[1];
        Task<DocumentSnapshot> userRef = profilesRef.document(profile).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.get("Highest Score"));
                        highestScores[0] = document.get("Highest Score");
                        Log.d(TAG, "current number of scans: " + highestScores[0]);
                        if (highestScores[0]  == null) {
                            Log.d(TAG, "DocumentSnapshot data: " + "it is NULL");
                            HashMap<String, Integer> data = new HashMap<>();
                            data.put("Highest Score", score);
                            profilesRef.document(profile).set(data, SetOptions.merge());
                        } else {
                            Log.d(TAG, "DocumentSnapshot data: " + "it is not null");
                            if (score > Integer.parseInt(highestScores[0].toString())) {
                                HashMap<String, Integer> data = new HashMap<>();
                                data.put("Highest Score", score);
                                profilesRef.document(profile).set(data, SetOptions.merge());
                            }

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
    public void updateTotalScore() {
        /**
         * Updates a user's total score
         * Inputs: none
         * Outputs: none
         */
        final Object[] totalScore = new Object[1];
        Task<DocumentSnapshot> userRef = profilesRef.document(profile).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.get("Total Score"));
                        totalScore[0] = document.get("Total Score");
                        Log.d(TAG, "current number of scans: " + totalScore[0]);
                        if (totalScore[0]  == null) {
                            Log.d(TAG, "DocumentSnapshot data: " + "it is NULL");
                            HashMap<String, Integer> data = new HashMap<>();
                            data.put("Total Score", score);
                            profilesRef.document(profile).set(data, SetOptions.merge());
                        } else {
                            Log.d(TAG, "DocumentSnapshot data: " + "it is not null");
                            HashMap<String, Integer> data = new HashMap<>();
                            data.put("Total Score", Integer.parseInt(totalScore[0].toString()) + score);
                            profilesRef.document(profile).set(data, SetOptions.merge());
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
