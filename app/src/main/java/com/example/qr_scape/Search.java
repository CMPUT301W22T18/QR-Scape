/*
 * Search
 *
 * March 16 2022
 *
 * Version 1
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Search
 * activity for searching users or qrcodes
 * allows text entry
 * then lists results
 * @author Dallin Dmytryk
 * @version 1
 */
public class Search extends AppCompatActivity {
    String PLAYERS = "PLAYERS";
    String PROFILES = "Profiles";
    String CODES = "CODES";
    BottomNavigationView bottomNavigationView;
    EditText searchField;
    Button modeSelector;
    Button searchButton;
    ListView playerList;
    ListView codeList;
    String mode = PLAYERS;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_title_layout);

        // Set database
        db = FirebaseFirestore.getInstance();

        // Set bottom navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
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

        // Set search bar
        searchField = findViewById(R.id.search_terms_edittext);

        // Set mode button
        modeSelector = findViewById(R.id.search_setting_button);
        modeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchMode();
            }
        });

        // Set search button
        searchButton = findViewById(R.id.search_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeSearch();
            }
        });

        // Set player list
        playerList = findViewById(R.id.search_player_result_listview);

        // Set code list
        codeList = findViewById(R.id.search_code_result_listview);

    }

    /**
     * switches the search mode
     * will switch between players and codes
     * sets listview visibility and mode button text
     */
    private void switchMode() {
        if (mode == PLAYERS) {
            mode = CODES;
            modeSelector.setText(CODES);
            playerList.setVisibility(View.GONE);
            codeList.setVisibility(View.VISIBLE);
        } else {
            mode = PLAYERS;
            modeSelector.setText(PLAYERS);
            playerList.setVisibility(View.VISIBLE);
            codeList.setVisibility(View.GONE);
        }
    }

    /**
     * executeSearch
     * executes a search dependant on search mode
     * will only execute if search field is filled
     */
    private void executeSearch() {
        String searchTerms = searchField.getText().toString();
        if (searchTerms.length() > 0) {
            if (mode == PLAYERS) {
                playerSearch(searchTerms);
            } else {
                codeSearch(searchTerms);
            }
        }
    }

    // Code modified from TVAC Studio
    // https://www.youtube.com/watch?v=2z0HlIY7M9s
    /**
     * Searches for players
     * will search the database for Profiles with searchTerms
     * in the document ID
     * @param searchTerms String term to be searched
     */
    private void playerSearch(String searchTerms) {
        db.collection(PROFILES)
                .orderBy(FieldPath.documentId())
                .startAt(searchTerms)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(null,"Successfully searched for players");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(null,"Failed to search for players");
                    }
                });
    }

    // Code modified from TVAC Studio
    // https://www.youtube.com/watch?v=2z0HlIY7M9s
    /**
     * Searches for codes
     * will search the database for QRCodes with searchTerms
     * in the document ID
     * @param searchTerms String term to be searched
     */
    private void codeSearch(String searchTerms) {

    }
}