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
 * Scan View Activity opens when scan button is pressed
 * It allows the user to scan a QR code and get a textvalue of it
 * @author Harsh Shah
  */
// From: https://www.youtube.com
// Link: https://www.youtube.com/watch?v=Sb4avOp7D_k
// Author: https://www.youtube.com/channel/UCfqdbTgV61qlbEgJNw5FpiA
// License: https://creativecommons.org/licenses/by-sa/3.0/
public class ScanView extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView ScanView;
    final String USERNAME = "USERNAME";
    final String PROFILES = "Profiles";

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScanView=new ZXingScannerView(this);
        setContentView(ScanView);
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ScanView.startCamera();
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
        QR_Scan.scantext.setText(rawResult.getText());
        // user can scan other players profile QR to check game status
        db = FirebaseFirestore.getInstance();
            db.collection(PROFILES)
                    .orderBy(FieldPath.documentId())
                    .startAt(rawResult.getText())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                Log.d(null,"Successfully searched for users");
                                // populate
                                for (QueryDocumentSnapshot document : task.getResult()){

                                    if(document.getId().equals(rawResult.getText())){
                                        // intent to use username in another activity
                                        Intent intent = new Intent(getApplicationContext(), ScannedQR_GameStatus.class);
                                        intent.putExtra(USERNAME, rawResult.getText());
                                        startActivity(intent);

                                    }
                                }


                            } else {
                                Log.d(null,"Failed to search for users");
                            }
                        }
                    });

        onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScanView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScanView.setResultHandler(this);
        ScanView.startCamera();
    }


}