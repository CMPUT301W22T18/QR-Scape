package com.example.qr_scape;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QRCollectionAdapter extends RecyclerView.Adapter<QRCollectionAdapter.myviewholder>{

    ArrayList<QRCode> qrDataList;

    public QRCollectionAdapter(ArrayList<QRCode> qrDataList) {
        this.qrDataList = qrDataList;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_list_content,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.qr_user.setText(qrDataList.get(position).getUsername());
        holder.qr_score.setText(Integer.toString(qrDataList.get(position).getScore()));
        holder.qr_hash.setText(qrDataList.get(position).getQRHash());
        holder.qr_lat.setText(String.valueOf(qrDataList.get(position).getLatitude()));
        holder.qr_long.setText(String.valueOf(qrDataList.get(position).getLongitude()));

    }

    @Override
    public int getItemCount() {
        return qrDataList.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {
        TextView qr_user, qr_score, qr_hash, qr_lat, qr_long;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            qr_user = itemView.findViewById(R.id.qr_user);
            qr_score = itemView.findViewById(R.id.qr_score);
            qr_hash = itemView.findViewById(R.id.qr_hash);
            qr_lat = itemView.findViewById(R.id.qr_lat);
            qr_long = itemView.findViewById(R.id.qr_long);

        }
    }
}