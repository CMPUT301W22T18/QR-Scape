package com.example.qr_generator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    Button button;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.edittext);
        button = (Button)findViewById(R.id.button);
        imageView = (ImageView)findViewById(R.id.imageview);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode(editText.getText().toString(),BarcodeFormat.QR_CODE,500,500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageView.setImageBitmap(bitmap);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}