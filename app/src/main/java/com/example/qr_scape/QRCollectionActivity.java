package com.example.qr_scape;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class QRCollectionActivity extends AppCompatActivity {
    ListView qrList;
    ArrayAdapter<String> qrListAdapter;
    ArrayList<String> qrDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_collections_layout);

        qrList = findViewById(R.id.qrList);

        // qrHashes: This will store all the hash codes of the QR codes which are added.
        String[] qrHashes = {"Hello", "You", "Are", "Amazing"};

        qrDataList = new ArrayList<>();
        qrDataList.addAll(Arrays.asList(qrHashes));

        qrListAdapter = new ArrayAdapter<>(this, R.layout.qr_list_content, qrDataList);
        qrList.setAdapter(qrListAdapter);
    }
}
