package com.example.qr_scape;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class QRCollectionActivity extends AppCompatActivity {
    public int currentlySelected = -1;
    FirebaseFirestore db;
    ArrayAdapter<QRCode> QRCollectionAdapter;
    ArrayList<QRCode> QRCollectionDataList;


    public void deleteQRCodeClicked(View view) {
        if (currentlySelected == -1){
            Toast.makeText(QRCollectionActivity.this, "No QR Code Selected", Toast.LENGTH_SHORT).show();
        } else {
            String QRID = QRCollectionDataList.get(currentlySelected).getQRID().toString();
            if (QRID.equals(null)){
                Toast.makeText(QRCollectionActivity.this, "No QR Code Selected", Toast.LENGTH_SHORT).show();
            } else {
                // From: https://www.youtube.com/
                // URL: https://www.youtube.com/watch?v=1gerxvFAGio
                // Author: https://www.youtube.com/channel/UC_Fh8kvtkVPkeihBs42jGcA
                // Licence: https://creativecommons.org/licenses/by-sa/3.0/
                DocumentReference QRRef = db.collection("QRCodes").document(QRID);
                QRRef.delete();
                QRCollectionAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
            }
        }
        currentlySelected = -1; // Reset selected after deletion
    }
}
