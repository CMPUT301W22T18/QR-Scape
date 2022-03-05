/*
 * ProfileActivity
 *
 * Version 2
 *
 * Feb 17 2022
 *
 * Licence info here
 */
package com.example.qr_scape;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
        contactInfoText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    HashMap data = new HashMap<>();
                    String infoText = contactInfoText.getText().toString();
                    data.put(CONTACTINFO, infoText);
                    documentReference.update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(null, "Successfully updated contact info");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(null, "Failed to update contact info");
                                }
                            });
                }
            }
        });

        // When user is finished inputting text, remove focus
        contactInfoText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i){
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                    case EditorInfo.IME_ACTION_PREVIOUS:
                        contactInfoText.clearFocus();
                }
                return true;
            }
        });
    }

}
