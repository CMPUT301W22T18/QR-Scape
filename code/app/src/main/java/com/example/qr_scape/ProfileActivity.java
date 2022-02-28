/*
 * ProfileActivity
 *
 * Version 1
 *
 * Feb 17 2022
 *
 * Licence info here
 */
package com.example.qr_scape;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * ProfileActivity
 * Activity meant to display user profile
 * also allows setting profile contact info
 * @author Dallin Dmytryk
 * @version 1
 */
public class ProfileActivity extends AppCompatActivity {
    TextView usernameText;
    EditText contactInfoText;
    FirebaseFirestore db;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));

        usernameText = findViewById(R.id.profile_username_text);

        contactInfoText = findViewById(R.id.profile_contact_info_edittext);
    }
}
