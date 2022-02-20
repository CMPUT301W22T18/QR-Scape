/*
 * LoginActivity
 *
 * Version 1
 *
 * Feb 17 2022
 *
 * Licence info here
 */
package com.example.qr_scape;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * LoginActivity
 * Activity for logging the user into the app
 * Will first check for stored credentials on device
 * Presents user with option to create account
 * or scan a QR code to login
 * Upon successfully doing any of the above moves user to home screen
 * @author Dallin Dmytryk
 * @version 1
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for credentials on device
        checkDeviceCredentials();

        // Create Account functionality
        Button createAccountButton = findViewById("BUTTON ID HERE");
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Scan QR Code functionality
        Button scanProfileButton = findViewById("BUTTON ID HERE");
        scanProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void checkDeviceCredentials(){

    }

}
