////Copyright 2022, Harsh Shah
////
////Licensed under the Apache License, Version 2.0 (the "License");
////you may not use this file except in compliance with the License.
////You may obtain a copy of the License at
////
////    http://www.apache.org/licenses/LICENSE-2.0
////
////Unless required by applicable law or agreed to in writing, software
////distributed under the License is distributed on an "AS IS" BASIS,
////WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
////See the License for the specific language governing permissions and
////limitations under the License.
package com.example.qr_scape;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Location Activity allows the user to find QR Codes current location
 * it shows a map which can be zoomed in, out based on the lat/long which is feed into
 * The navigation bar is allowed to browse it and switch between different
 * activities
 * @author Harsh Shah
 */
// From: https://www.youtube.com
// Link: https://www.youtube.com/watch?v=p0PoKEPI65o
// Author: https://www.youtube.com/channel/UCUIF5MImktJLDWDKe5oTdJQ
// License: https://creativecommons.org/licenses/by-sa/3.0/
public class Location extends FragmentActivity implements OnMapReadyCallback {
    BottomNavigationView bottomNavigationView;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    GoogleMap map;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        searchView =  findViewById(R.id.sv_location);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(Location.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when permission granted
            //call
            getCurrentLocation();
        }else{
            // when persmission denied
            // request permission
            ActivityCompat.requestPermissions(Location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44 );
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_location);

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

                    case R.id.nav_location: ;
                        return true;
                }
                return false;
            }
        });
        // search view implemented for searching
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location !=null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(Location.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1 );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    MarkerOptions options = new MarkerOptions().position(latLng)
                            .title(location);
                    //zoom map
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    // Add marker on map
                    map.addMarker(options);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        supportMapFragment.getMapAsync(this);



     /**
      * getCurrentLocation allows the user to check
      * QR Codes current location once the user grants the permission
      * Getting location of multiple QR Codes will be implemented in the next version
      */
    }
    private void getCurrentLocation() {
        // Initialize task location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<android.location.Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                if(location !=null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            // initialize lat lng
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                            //create marker options
                            MarkerOptions options = new MarkerOptions().position(latLng)
                                    .title("QR Code");
                            ArrayList<LatLng>arrayList = new ArrayList<LatLng>();
                            LatLng Calgary = new LatLng(  51.049999,  -114.066666 );
                            MarkerOptions option1 = new MarkerOptions().position(Calgary);
                            LatLng RedDeer = new LatLng(52.268112,-113.811241 );
                            MarkerOptions option2 = new MarkerOptions().position(RedDeer);
                            LatLng StAlbert = new LatLng(53.630280, -113.625832 );
                            MarkerOptions option3 = new MarkerOptions().position(StAlbert);
                            //zoom map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            // Add marker on map
                            googleMap.addMarker(options);
                            googleMap.addMarker(option1);
                            googleMap.addMarker(option2);
                            googleMap.addMarker(option3);

                        }
                    });
                }
            }
        });
    }

    /**
     * checks permission from the user considering their privacy
      */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // when permission granted
                // Call method
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }
}