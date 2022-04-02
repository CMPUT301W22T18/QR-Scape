////Copyright 2022, Harsh Shah, Ty Greve
////
////Licensed under the Apache License, Version 2.0 (the "License");
////you may not use this file except in compliance with the License.
////You may obtain a copy of the License at
////
////    http://www.apache.org/licenses/LICENSE-2.0
////
////Unless required by applicable law or agreed to in writing, software
////distributed under the License is distributed on an "AS IS" BASIS,
////WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
////See the License for the specific language governing permissions and
////limitations under the License.

package com.example.qr_scape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
/**
 * ScanLogin Activity allows to scan a QR code via login page,
 * which loads its own profile on a different device
 * with respective profile consisting of its stats, user info etc
 * @author Harsh Shah
 */
// From: https://www.youtube.com
// Link: https://www.youtube.com/watch?v=Sb4avOp7D_k
// Author: https://www.youtube.com/channel/UCfqdbTgV61qlbEgJNw5FpiA
// License: https://creativecommons.org/licenses/by-sa/3.0/
public class ScanLogin extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView ScanLogin;
    final String USERNAME = "Username";
    final String PROFILES = "Profiles";
    final String OWNER = "Owner";
    SharedPreferences sharedPreferences;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScanLogin=new ZXingScannerView(this);
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
        setContentView(ScanLogin);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        getSupportActionBar().setCustomView(R.layout.toolbar_title_layout);
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ScanLogin.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    @Override
    public void handleResult(Result rawResult) {
        String qrText = rawResult.getText();
        if (qrText.length() > 10 && qrText.startsWith("QR-Scape:")) {
            String username = qrText.substring(9);
            db = FirebaseFirestore.getInstance();
            db.collection(PROFILES)
                    .orderBy(FieldPath.documentId())
                    .startAt(username)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(null, "Successfully searched for users");
                                // populate
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    if (document.getId().equals(username)) {
                                        SharedPreferences.Editor shEditor = sharedPreferences.edit();
                                        shEditor.putString(USERNAME, username);
                                        shEditor.commit();

                                        String savedUserName = sharedPreferences.getString(USERNAME, null);
                                        Log.d("ScannedLogin", savedUserName);
                                        if (savedUserName != null) {
                                            Intent intent = new Intent(getApplicationContext(), Home.class);
                                            startActivity(intent);
                                        }


                                    }
                                }


                            } else {
                                Log.d(null, "Failed to search for users");
                            }
                        }
                    });
        }
        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScanLogin.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScanLogin.setResultHandler(this);
        ScanLogin.startCamera();
    }

    /**
     * Saves username's status as owner or not
     * @param username string Profile username
     */
    private void checkOwnerStatus(String username) {
        final Object[] ownerStatus = new Object[1];
        Task<DocumentSnapshot> userRef = db.collection("Profiles").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ownerStatus[0] = document.get("Owner");
                        if (ownerStatus[0]  == null | String.valueOf(ownerStatus[0]).equals("False")) {
                            SharedPreferences.Editor shEditor = sharedPreferences.edit();
                            shEditor.putString(OWNER, "False");
                            shEditor.commit();
                            Log.d("LoginActivity: Is owner?", "They are NOT owner on sharedprefs");
                        } else {
                            SharedPreferences.Editor shEditor = sharedPreferences.edit();
                            shEditor.putString(OWNER, "True");
                            shEditor.commit();
                            Log.d("LoginActivity: Is owner?", "They are an owner on sharedprefs");
                        }
                    } else {
                        Log.d("LoginActivity", "No such document");
                    }
                } else {
                    Log.d("LoginActivity", "get failed with ", task.getException());
                }
            }
        });
    }


}
