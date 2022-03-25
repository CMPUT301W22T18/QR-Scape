package com.example.qr_scape;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Expanded_QR_View extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_qr_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView qr_user = findViewById(R.id.tv_username);
        TextView qr_hash = findViewById(R.id.tv_hash);
        TextView qr_score = findViewById(R.id.tv_score);
        TextView qr_longitude = findViewById(R.id.tv_longitude);
        TextView qr_latitude = findViewById(R.id.tv_latitude);

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


    }
}