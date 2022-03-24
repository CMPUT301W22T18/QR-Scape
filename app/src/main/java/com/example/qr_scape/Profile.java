/*
 * ProfileActivity
 *
 * Version 2
 *
 * Feb 17 2022
 *
 * Copyright 2022 Harsh Shah, Dallin Dmytryk
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Profile opens profile activity
 * allowing user to check their profile
 * browse between activities
 */

public class Profile extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    EditText editText;
    Button button;
    ImageView imageView;
    String name;
    String password;
    String contactInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //editText = (EditText)findViewById(R.id.edittext);
        //button = (Button)findViewById(R.id.button);
        //imageView = (ImageView)findViewById(R.id.imageview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode(editText.getText().toString(),BarcodeFormat.QR_CODE,500,500);
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
     * Gets the name of the profile
     * @return the name of the profile
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the password of a profile
     * @return the password of a profile
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the contact info of a profile
     * @return the contact info of a profile
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * Sets the contact info of a profile
     * @param contactInfo string with profile contact info
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public void qrCollectionButtonClicked(View view) {
        startActivity(new Intent(Profile.this, QRCollectionActivity.class));
    }
}