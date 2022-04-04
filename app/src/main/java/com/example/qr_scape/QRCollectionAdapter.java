package com.example.qr_scape;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * QRCollectionActivity
 * This adapter class helps to display all the QR codes on a card view in QRCollections
 * @author Kashish Sansanwal
 * @version 1
 */

public class QRCollectionAdapter extends RecyclerView.Adapter<QRCollectionAdapter.myviewholder> {
    ArrayList<QRCode> qrDataList;

    public QRCollectionAdapter(ArrayList<QRCode> qrDataList) {
        this.qrDataList = qrDataList;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_list_content, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.qr_user.setText(qrDataList.get(position).getUsername());
        holder.qr_score.setText(Integer.toString(qrDataList.get(position).getScore()));
        holder.qr_hash.setText(qrDataList.get(position).getQRHash().substring(0,10));
        holder.qr_lat.setText(String.valueOf(qrDataList.get(position).getLatitude()));
        holder.qr_long.setText(String.valueOf(qrDataList.get(position).getLongitude()));
        final long SIZE = 1024*64;
        String imageUrl = qrDataList.get(position).getQRHashSalted() + ".jpg";

        // get storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathRef  = storageRef.child(imageUrl);

        // get image
        pathRef.getBytes(SIZE).
                addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d("Image Load", "Image loaded");
                        Bitmap image = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                        holder.qr_thumb.setImageBitmap(image);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Image Load", "Failed to load Image");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return qrDataList.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {
        TextView qr_user, qr_score, qr_hash, qr_lat, qr_long;
        ImageView qr_thumb;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), Expanded_QR_View.class);
                    intent.putExtra("username",qrDataList.get(getAdapterPosition()).getUsername());
                    intent.putExtra("score",String.valueOf(qrDataList.get(getAdapterPosition()).getScore()));
                    intent.putExtra("realHash",qrDataList.get(getAdapterPosition()).getQRHash());
                    intent.putExtra("hash",qrDataList.get(getAdapterPosition()).getQRHashSalted());
                    Log.d("Test",qrDataList.get(getAdapterPosition()).getQRHashSalted());
                    intent.putExtra("long",String.valueOf(qrDataList.get(getAdapterPosition()).getLongitude()));
                    intent.putExtra("lat",String.valueOf(qrDataList.get(getAdapterPosition()).getLatitude()));
                    view.getContext().startActivity(intent);
                }
            });
            qr_thumb = itemView.findViewById(R.id.imageView5);
            qr_user = itemView.findViewById(R.id.comment_text);
            qr_score = itemView.findViewById(R.id.comment_user);
            qr_hash = itemView.findViewById(R.id.comment_timestamp);
            qr_lat = itemView.findViewById(R.id.comment_lat);
            qr_long = itemView.findViewById(R.id.comment_DNE);

        }
    }
}
