//Copyright 2022, Harsh Shah, Kiran Deol
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
package com.example.qr_scape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Home activity is the base activity
 * which brings the user to it, when successfully logged in
 * It has a leaderboard button which allows users to check the high score, low score,
 * total scans, etc.
 * @author  Harsh Shah, Kiren Deol
 */
// From: https://www.youtube.com
// Link: https://www.youtube.com/watch?v=lOTIedfP1OA
// Author: https://www.youtube.com/channel/UC2Dn1EkW8zglMgNkddhRVhg
// License: https://creativecommons.org/licenses/by-sa/3.0/
public class Home extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore db;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_title_layout);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        notifyOwner();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
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
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0,0);
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
     * Called to open the leaderboard activity
     * @param view
     */
    public void openLeaderboard(View view) {
        startActivity(new Intent(Home.this, LeaderboardActivity.class));
    }

    public void openQRCodes(View view) {
        startActivity(new Intent(Home.this, QRCollectionActivity.class));
    }

    /**
     * Notifies user if they are now an owner
     */
    public void notifyOwner() {
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
        String ownerBool = sharedPreferences.getString("Owner","null");
        if (ownerBool.equals("True")) {
            Log.d("Is owner?", "owner");
            Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), R.string.new_owner, Snackbar.LENGTH_LONG)
                    .show();
        } else {
            Log.d("Is owner?", "not owner");
        }
    }
}
