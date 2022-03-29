package com.example.qr_scape;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Expanded_QR_View extends AppCompatActivity {

    private FirebaseFirestore db;
    SharedPreferences sharedPreferences;
    private ArrayList<QRCode> qrHashList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_qr_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name),MODE_PRIVATE);
        String savedUserName = sharedPreferences.getString("Username",null);

        TextView qr_user = findViewById(R.id.tv_username);
        TextView qr_hash = findViewById(R.id.tv_hash);
        TextView qr_score = findViewById(R.id.tv_score);
        TextView qr_longitude = findViewById(R.id.tv_longitude);
        TextView qr_latitude = findViewById(R.id.tv_latitude);
        TextView qr_scannedBy = findViewById(R.id.tv_scannedBy);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String score = intent.getStringExtra("score");
        String hash = intent.getStringExtra("hash");
        String longitude = intent.getStringExtra("long");
        String latitude = intent.getStringExtra("lat");

        qr_user.setText(username);
        qr_hash.setText(hash);
        qr_score.setText(score);
        qr_longitude.setText(longitude);
        qr_latitude.setText(latitude);

        qrHashList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        db.collection("QRCodeInstance")
                .whereEqualTo("RealHash",hash)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    Log.d("hash_list", queryDocumentSnapshots.getDocuments().toString());
                    for (DocumentSnapshot d : list){

                        QRCode qr = d.toObject(QRCode.class);
                        String qr_username = d.getString("Username");
                        Integer qr_scoreLong = Math.toIntExact(d.getLong("Score"));
                        String qr_realHash = d.getString("RealHash");
                        Double qr_Longitude = d.getDouble("Longitude");
                        Double qr_Latitude = d.getDouble("Latitude");
                        QRCode qrCode = new QRCode(qr_realHash, qr_Latitude, qr_Longitude, qr_scoreLong,qr_username);
                        qrHashList.add(qrCode);
                        Log.d("size",String.valueOf(qrHashList.size()));
                        String scannedBy = String.valueOf(qrHashList.size());
                        qr_scannedBy.setText(scannedBy);
                    }
                }
            }
        });




    }
}