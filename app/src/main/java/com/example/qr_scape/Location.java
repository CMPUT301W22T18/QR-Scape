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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    GoogleMap map;
    RecyclerView recyclerView;
    Marker marker;
    Marker marker1;
    Circle Mapcircle;
    FirebaseFirestore db;
    ArrayList<LatLng> arrayList;
    ArrayList<QRCode> qrDataList;
    SearchView searchView;

    //QRCollectionAdapter qrCollectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        searchView =  findViewById(R.id.sv_location);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        client = LocationServices.getFusedLocationProviderClient(this);
        arrayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        db.collection("QRCodeInstance").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    Log.d("list", queryDocumentSnapshots.getDocuments().toString());
                    for (DocumentSnapshot d : list){

                        QRCode qr = d.toObject(QRCode.class);
                        String qr_username = d.getString("Username");
                        Integer qr_scoreLong = Math.toIntExact(d.getLong("Score"));
                        String qr_realHash = d.getString("RealHash");
                        //String qr_Photo = d.getString("Photo");
                        Double qr_Longitude = d.getDouble("Longitude");
                        Double qr_Latitude = d.getDouble("Latitude");
                        Log.i("location", String.valueOf(qr_Latitude));
                        Log.i("location", String.valueOf(qr_Longitude));
                        LatLng latLngfire = new LatLng(qr_Latitude, qr_Longitude);
                        arrayList.add(latLngfire);
                        Log.d("firelocation", String.valueOf(arrayList));
//                        QRCode qrCode = new QRCode(qr_realHash, qr_Latitude, qr_Longitude, qr_scoreLong,qr_username);
//                        qrDataList.add(qrCode);
                    }

                }
            }
        });




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


        /**
         * getCurrentLocation allows the user to check
         * QR Codes current location once the user grants the permission
         * Getting location of multiple QR Codes will be implemented in the next version
         */
    }
    public void getCurrentLocation() {
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
                            Log.i("currentLocation", String.valueOf(latLng));
                            //create marker options
                            MarkerOptions options7 = new MarkerOptions().position(latLng)
                                    .title("CurrentLocation");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            googleMap.addMarker(options7);

                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    String searchlocation = searchView.getQuery().toString();
                                    List<Address> addressList = null;


                                    if(searchlocation !=null || !searchlocation.equals("")){
                                        Geocoder geocoder = new Geocoder(Location.this);
                                        try {
                                            addressList = geocoder.getFromLocationName(searchlocation, 1 );
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Address address = addressList.get(0);
                                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                        if(marker != null){
                                            marker.remove();
                                            Mapcircle.remove();

                                        }
                                        MarkerOptions options = new MarkerOptions().position(latLng)
                                                .title(searchlocation);
                                        //zoom map
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                                        // Add marker on map
                                        marker = googleMap.addMarker(options);

                                        CircleOptions circleOptions = new CircleOptions();
                                        // Specifying the center of the circle
                                        circleOptions.center(latLng);
                                        // Radius of the circle
                                        circleOptions.radius(50);
                                        // Border color of the circle
                                        circleOptions.strokeColor(Color.BLACK);
                                        // Fill color of the circle
                                        circleOptions.fillColor(0x30ff0000);
                                        // Border width of the circle
                                        circleOptions.strokeWidth(2);
                                        // Adding the circle to the GoogleMap
                                        Mapcircle = googleMap.addCircle(circleOptions);
                                        for ( int i = 0; i<arrayList.size(); i++){

                                            MarkerOptions options87 = new MarkerOptions().position(arrayList.get(i))
                                                    .title("QR Code");
                                            // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(arrayList.get(i), 10));
                                            googleMap.addMarker(options87);

                                        }

                                    }

                                    return false;

                                }


                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    return false;
                                }

                            });


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