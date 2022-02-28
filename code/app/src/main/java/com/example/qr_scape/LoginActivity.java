/*
 * LoginActivity
 *
 * Version 3
 *
 * Feb 17 2022
 *
 * Licence info here
 */
package com.example.qr_scape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

/**
 * LoginActivity
 * Activity for logging the user into the app
 * Will first check for stored credentials on device
 * Presents user with option to create account
 * or scan a QR code to login
 * Upon successfully doing any of the above moves user to home screen
 * @author Dallin Dmytryk
 * @version 3
 */
public class LoginActivity extends AppCompatActivity {
    final String PROFILES = "Profiles";
    final String USERNAME = "Username";
    LinearLayout buttonLayout;
    LinearLayout createProfileLayout;
    FirebaseFirestore db;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        buttonLayout = findViewById(R.id.login_button_layout);
        createProfileLayout = findViewById(R.id.login_create_profile_layout);
        EditText usernameText = findViewById(R.id.login_username_edittext);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection(PROFILES);

        // Check for credentials on device
        checkDeviceCredentials();

        // Create Account functionality
        Button createAccountButton = findViewById(R.id.login_create_profile_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonLayout.setVisibility(View.GONE);
                createProfileLayout.setVisibility(View.VISIBLE);
            }
        });

        // Back Button functionality
        Button backButton = findViewById(R.id.login_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonLayout.setVisibility(View.VISIBLE);
                createProfileLayout.setVisibility(View.GONE);
                usernameText.setText("");
            }
        });

        // Confirm create profile functionality
        Button createProfileButton = findViewById(R.id.login_confirm_button);
        createProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameText.getText().toString();
                // validate username
                if (username.length() == 0) {
                    // warn user that input is incorrect
                    Snackbar.make(usernameText,R.string.invalid_username_warning,Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                // check if username already exists
                DocumentReference docRef = db.collection("Profiles").document(username);
                docRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Log.d(null, "Successfully checked for user existing");
                                if (documentSnapshot.exists()) {
                                    // warn user that name is taken
                                    Snackbar.make(usernameText, R.string.username_taken_warning, Snackbar.LENGTH_SHORT)
                                            .show();
                                    return;
                                } else {
                                    createProfile(username);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(null, "Failed to check if user exists");
                            }
                        });
            }
        });

        // Scan QR Code functionality
        Button scanProfileButton = findViewById(R.id.login_scan_profile_code_button);
        scanProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // to be implemented later
            }
        });
    }

    private void checkDeviceCredentials(){
        String savedUserName = sharedPreferences.getString(USERNAME,null);
        if (savedUserName != null) {
            nextActivity();
        }
    }

    /**
     * Creates a profile on the firestore database
     * On success saves credentials
     * @param username String for profiles username
     */
    private void createProfile(String username) {
        HashMap<String, String> data = new HashMap<>();
        data.put("Contact info", "");
        db.collection(PROFILES)
                .document(username)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(null, "Successfully created user");
                        saveUserCredentials(username);
                        nextActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(null,"Failed to create user");
                    }
                });
    }

    private void saveUserCredentials(String username){
        SharedPreferences.Editor shEditor = sharedPreferences.edit();
        shEditor.putString(USERNAME,username);
        shEditor.commit();
    }

    /**
     * Starts the next activity
     */
    private void nextActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

}
