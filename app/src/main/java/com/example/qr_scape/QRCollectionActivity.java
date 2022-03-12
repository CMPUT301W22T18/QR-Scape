
package com.example.qr_scape;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class QRCollectionActivity extends AppCompatActivity {
    ListView qrList;
    ArrayAdapter<String> qrListAdapter;
    ArrayList<String> qrDataList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference qrCodesRef = db.collection("QRCodes");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_collections_layout);

        qrList = findViewById(R.id.qrList);

        // qrHashes: This will store all the hash codes of the QR codes which are added.
        //String[] qrHashes = {"Hello", "You", "Are", "Amazing"};

        qrDataList = new ArrayList<>();
        //qrDataList.addAll(Arrays.asList(qrHashes));

        qrListAdapter = new ArrayAdapter<>(this, R.layout.qr_list_content, qrDataList);
        qrList.setAdapter(qrListAdapter);

        Task<QuerySnapshot> qrRef = db.collection("QRCodeInstance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> hash_obj = document.getData();
                        String hash_value = (String) hash_obj.toString();
                        qrDataList.add(hash_value);
                        qrListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}


