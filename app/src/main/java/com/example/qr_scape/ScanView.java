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
import com.google.common.hash.Hashing;
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

import java.nio.charset.StandardCharsets;
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
    SharedPreferences sharedPreferences;
    double latitude;
    double longitude;
    private int score;
    String photo;
    private String QRHash;
    String username;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScanView=new ZXingScannerView(this);
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
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
        String QRText = rawResult.getText();
        if (QRText.length() > 10 && QRText.startsWith("QR-Scape:")) {
            // user can scan other players profile QR to check game status
            String username = QRText.substring(9);
            db = FirebaseFirestore.getInstance();
            db.collection(PROFILES)
                    .orderBy(FieldPath.documentId())
                    .startAt(username)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                Log.d(null,"Successfully searched for users");
                                // populate
                                for (QueryDocumentSnapshot document : task.getResult()){

                                    if(document.getId().equals(username)){
                                        // intent to use username in another activity
                                        Intent intent = new Intent(getApplicationContext(), ScannedQR_GameStatus.class);
                                        intent.putExtra(USERNAME, username);
                                        startActivity(intent);


                                    }
                                }


                            } else {
                                Log.d(null,"Failed to search for users");
                            }
                        }
                    });
            setResult(200, new Intent());
        } else {
            Intent intent = new Intent();
            intent.putExtra("data", QRText);
            setResult(200, intent);
        }
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
    public static int calculateScore(String QRText){

        int num_repeats = 0;
        int current_index = 1;
        int score = 0;
        for (int i = 1; i < QRText.length(); i++){
            char c = QRText.charAt(i);

            if (QRText.charAt(i) == QRText.charAt(i - 1)) {
                num_repeats += 1;
            } else if (num_repeats > 0) {
                int hexVal = Integer.parseInt(Character.toString(QRText.charAt(i - 1)), 16);
                score += Math.pow(hexVal, num_repeats);
                num_repeats = 0;
            }
        }

        // To do: Write the logic to calculate a QR codes score based on the QRID
        return score;
    }

    private String generateHash(String QRText){
        // From: https://stackoverflow.com/
        // Link: https://stackoverflow.com/a/18340262
        // Author: https://stackoverflow.com/users/69875/jonathan
        // License: https://creativecommons.org/licenses/by-sa/3.0/
        final String QRTextHash = Hashing.sha256()
                .hashString(QRText, StandardCharsets.UTF_8)
                .toString();
        // To do: Write the logic to generate the hash of the QR code text
        return QRTextHash;
    }



}