/*
 * ProfileActivity
 *
 * Version 2
 *
 * Feb 17 2022
 *
 * Copyright 2022 Dallin Dmytryk, Harsh Shah, Kiran Deol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.qr_scape;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * ProfileActivity
 * Activity meant to display user profile
 * also allows setting profile contact info
 * It has QR Code which can be generated to view game status
 * @author Dallin Dmytryk
 * @author Harsh Shah
 * @version 2
 */
public class ProfileActivity extends AppCompatActivity {
    final String USERNAME = "Username";
    final String PROFILES = "Profiles";
    final String CONTACTINFO = "Contact info";
    TextView usernameText;
    EditText contactInfoText;
    TextView codeEditText;
    TextView codeEditText1;
    Button confirmButton;
    Button codeButton;
    Button codeButton1;
    BottomNavigationView bottomNavigationView;
    ImageView imageView;
    FirebaseFirestore db;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_title_layout);

        // Check shared preferences for username
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
        String savedUserName = sharedPreferences.getString(USERNAME,null);

        // Initialize database
        db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(PROFILES).document(savedUserName);

        // Have field update
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    Log.d(null,"Successfully loaded profile");
                    String infoText = value.getString(CONTACTINFO);
                    contactInfoText.setText(infoText);
                } else {
                    Log.d(null, "Couldn't load profile");
                }
            }
        });

        // Set username text
        usernameText = findViewById(R.id.profile_username_text);
        usernameText.setText(savedUserName);

        // Set intractable contact info text
        contactInfoText = findViewById(R.id.profile_contact_info_edittext);
        contactInfoText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confirmButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Add confirm button functionality
        confirmButton = findViewById(R.id.profile_confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap data = new HashMap<>();
                String infoText = contactInfoText.getText().toString();
                data.put(CONTACTINFO, infoText);
                documentReference.update(data)
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(null, "Successfully updated contact info");
                                confirmButton.setVisibility(view.INVISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(null, "Failed to update contact info");
                            }
                        });
            }
        });
        // From: https://www.youtube.com
        // Link: https://www.youtube.com/watch?v=yJh22Wk74V8&t=425s
        // Author: https://www.youtube.com/channel/UCklYpZX_-QqHOeSUH4GVQpA
        // License: https://creativecommons.org/licenses/by-sa/3.0/
        // set create QR code functionality
        // it takes in username and generates the QR code with username!
        imageView = findViewById(R.id.profile_imageview);
        codeEditText= findViewById(R.id.profile_code_edittext);
        codeEditText1= findViewById(R.id.profile_code_edittext1);
        codeEditText.setText("QR-Scape:" + savedUserName);
        codeEditText1.setText("Status:" + savedUserName);
        codeButton = findViewById(R.id.profile_generate_button);
        codeButton1 = findViewById(R.id.profile_generate_button1);
        codeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode(codeEditText1.getText().toString(), BarcodeFormat.QR_CODE,500,500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageView.setImageBitmap(bitmap);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode(codeEditText.getText().toString(), BarcodeFormat.QR_CODE,500,500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageView.setImageBitmap(bitmap);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
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
     * Opens the stats page for the user
     * @author Kiran Deol
     */
    public void openStats(View view) {
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
        String savedUserName = sharedPreferences.getString(USERNAME,null);
        Intent openStatsIntent = new Intent(this, PersonalStats.class);
        openStatsIntent.putExtra("Profile", savedUserName);
        startActivity(openStatsIntent);
    }
}
