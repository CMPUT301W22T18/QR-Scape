package com.example.qr_scape;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QRCode qrCode = new QRCode("MyText", "TyGreve");

        TextView text = (TextView)findViewById(R.id.textView1);
        text.setText(qrCode.getQRHash());

    }
}