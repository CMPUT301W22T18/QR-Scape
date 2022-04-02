//Copyright 2022, Kiran Deol
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Displays the leaderboard with the option of sorting by the three main stats
 * Does not rank users who are not on the board yet
 * @author Kiran Deol
 */

public class LeaderboardActivity extends AppCompatActivity {
    FirebaseFirestore db;
    List<LeaderboardItem> players;
    Spinner spinner;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.background));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_title_layout);
        db = FirebaseFirestore.getInstance();
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        players = new ArrayList<>();
        final CollectionReference profilesRef = db.collection("Profiles");
        RVAdapter adapter = new RVAdapter(players);
        rv.setAdapter(adapter);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

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

        spinner = (Spinner) findViewById(R.id.leaderboard_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(LeaderboardActivity.this,
                R.array.leaderboard_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("ITEM SELECTED", adapterView.getItemAtPosition(i).toString());
                if (adapterView.getItemAtPosition(i).toString().equals("Total Scans")) {
                    profilesRef.orderBy("Total Scans", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                                FirebaseFirestoreException error) {
                            // Clear the old list
                            players.clear();
                            int currentRank = 0;
                            for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                                String username = " " + doc.getId();
                                String score = "  " + doc.getData().get("Total Scans");
                                currentRank += 1;
                                Log.d("score detected is: ", score);
                                if (!score.equals("null")) {
                                    players.add(new LeaderboardItem(username, score, " #" + String.valueOf(currentRank), R.drawable.trophy_icon)); // Adding the cities and provinces from FireStore
                                } else {
                                    players.add(new LeaderboardItem(username, " 0", " #" + String.valueOf(currentRank), R.drawable.trophy_icon)); // Adding the cities and provinces from FireStore

                                }
                            }
                            adapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                        }
                    });
                } else if (adapterView.getItemAtPosition(i).toString().equals("Highest Score")) {
                    profilesRef.orderBy("Highest Score", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                                FirebaseFirestoreException error) {
                            // Clear the old list
                            players.clear();
                            int currentRank = 0;
                            for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                                String username = " " + doc.getId();
                                String score = "  " + doc.getData().get("Highest Score");
                                currentRank += 1;
                                Log.d("score detected is: ", score);
                                if (!score.equals("null")) {
                                    players.add(new LeaderboardItem(username, score, " #" + String.valueOf(currentRank), R.drawable.trophy_icon)); // Adding the cities and provinces from FireStore
                                } else {
                                    players.add(new LeaderboardItem(username, " 0", " #" + String.valueOf(currentRank), R.drawable.trophy_icon)); // Adding the cities and provinces from FireStore

                                }
                            }
                            adapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                        }
                    });
                } else if (adapterView.getItemAtPosition(i).toString().equals("Total Score")) {
                    profilesRef.orderBy("Total Score", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                                FirebaseFirestoreException error) {
                            // Clear the old list
                            players.clear();
                            int currentRank = 0;
                            for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                                String username = " " + doc.getId();
                                String score = "  " + doc.getData().get("Total Score");
                                currentRank += 1;
                                Log.d("score detected is: ", score);
                                if (!score.equals("null")) {
                                    players.add(new LeaderboardItem(username, score, " #" + String.valueOf(currentRank), R.drawable.trophy_icon)); // Adding the cities and provinces from FireStore
                                } else {
                                    players.add(new LeaderboardItem(username, " 0", " #" + String.valueOf(currentRank), R.drawable.trophy_icon)); // Adding the cities and provinces from FireStore

                                }
                            }
                            adapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // no implementation given
            }

        });

    }

}
