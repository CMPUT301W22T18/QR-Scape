package com.example.qr_scape;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this is test code for loginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}