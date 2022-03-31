/*
 * Search
 *
 * March 16 2022
 *
 * Version 1
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Search
 * activity for searching users or qrcodes
 * allows text entry
 * then lists results
 * @author Dallin Dmytryk
 * @version 3
 */
public class Search extends AppCompatActivity {
    final String PLAYERS = "PLAYERS";
    final String PROFILES = "Profiles";
    final String CODES = "CODES";
    final String CODEINSTANCES = "QRCodeInstance";
    final String REALHASH = "RealHash";
    final String USERNAME = "USERNAME";
    BottomNavigationView bottomNavigationView;
    EditText searchField;
    Button modeSelector;
    Button searchButton;
    ListView playerList;
    UserAdapter userAdapter;
    RecyclerView codeList;
    QRCollectionAdapter codeAdapter;
    ArrayList<QRCode> codeDataList;
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
        playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // open detailed view for user
                Intent intent = new Intent(view.getContext(), OtherProfileActivity.class);
                intent.putExtra(USERNAME, userAdapter.getItem(i).getName());
                startActivity(intent);
            }
        });

        // Set userAdapter
        userAdapter = new UserAdapter(this,new ArrayList<User>());
        playerList.setAdapter(userAdapter);

        // Set code list
        codeList = (RecyclerView) findViewById(R.id.search_code_result_listview);
        codeList.setLayoutManager(new LinearLayoutManager(this));

        // Set codeAdapter
        codeDataList = new ArrayList<>();
        codeAdapter = new QRCollectionAdapter(codeDataList);
        codeList.setAdapter(codeAdapter);

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
        String searchTerms = searchField.getText().toString().toLowerCase(Locale.ROOT);
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
        userAdapter.clear();
        db.collection(PROFILES)
                .orderBy(FieldPath.documentId())
                .startAt(searchTerms)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d(null,"Successfully searched for users");
                            // populate
                            for (QueryDocumentSnapshot document : task.getResult()){
                                User user = new User(document.getId(), "");
                                userAdapter.add(user);
                            }
                        } else {
                            Log.d(null,"Failed to search for users");
                        }
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
        codeDataList.clear();
        db.collection(CODEINSTANCES)
                .orderBy(REALHASH)
                .startAt(searchTerms)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d(null,"Successfully searched for codes");
                            // populate
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String qr_username = document.getString("Username");
                                Integer qr_scoreLong = Math.toIntExact(document.getLong("Score"));
                                String qr_realHash = document.getString("RealHash");
                                String qr_saltedHash = document.getId().toString();
                                //String qr_Photo = d.getString("Photo");
                                Double qr_Longitude = document.getDouble("Longitude");
                                Double qr_Latitude = document.getDouble("Latitude");
                                QRCode qrCode = new QRCode(qr_realHash,qr_saltedHash,qr_username,
                                        qr_Latitude, qr_Longitude, null, qr_scoreLong);
                                codeDataList.add(qrCode);
                            }
                        } else {
                            Log.d(null,"Failed to search for codes");
                        }
                        codeAdapter.notifyDataSetChanged();
                    }
                });
    }
}