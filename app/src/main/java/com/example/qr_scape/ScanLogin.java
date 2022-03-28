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

public class ScanLogin extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView ScanLogin;
    final String USERNAME = "USERNAME";
    final String PROFILES = "Profiles";
    SharedPreferences sharedPreferences;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScanLogin=new ZXingScannerView(this);
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
        setContentView(ScanLogin);
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
                                    SharedPreferences.Editor shEditor = sharedPreferences.edit();
                                    shEditor.putString(USERNAME,rawResult.getText());
                                    shEditor.commit();
                                    String savedUserName = sharedPreferences.getString(USERNAME,null);
                                    if (savedUserName != null) {
                                        Intent intent = new Intent(getApplicationContext(), Home.class);
                                        startActivity(intent);
                                    }


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
        ScanLogin.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScanLogin.setResultHandler(this);
        ScanLogin.startCamera();
    }


}
