/*
 * ProfileActivity
 *
 * Version 2
 *
 * Feb 17 2022
 *
 * Copyright 2022 Dallin Dmytryk
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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;

/**
 * ProfileActivity
 * Activity meant to display user profile
 * also allows setting profile contact info
 * @author Dallin Dmytryk
 * @version 2
 */
public class ProfileActivity extends AppCompatActivity {
    final String USERNAME = "Username";
    final String PROFILES = "Profiles";
    final String CONTACTINFO = "Contact info";
    TextView usernameText;
    EditText contactInfoText;
    Button confirmButton;
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

    }

}
