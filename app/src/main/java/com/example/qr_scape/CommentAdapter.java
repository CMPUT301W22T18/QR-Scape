package com.example.qr_scape;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.myviewholder> {
    ArrayList<Comment> qrDataList;

    public CommentAdapter(ArrayList<Comment> qrDataList) {
        this.qrDataList = qrDataList;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_content, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.comment_user.setText(qrDataList.get(position).getUsername());
        holder.comment_text.setText(qrDataList.get(position).getCommentText());
        holder.comment_hash.setText(qrDataList.get(position).getQrInstance());
        holder.comment_timestamp.setText(qrDataList.get(position).getTimestamp());

    }

    @Override
    public int getItemCount() {
        return qrDataList.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {
        TextView comment_user, comment_text, comment_hash, comment_timestamp;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(view.getContext(), Expanded_QR_View.class);
//                    intent.putExtra("username",qrDataList.get(getAdapterPosition()).getUsername());
//                    intent.putExtra("score",String.valueOf(qrDataList.get(getAdapterPosition()).getScore()));
//                    intent.putExtra("hash",qrDataList.get(getAdapterPosition()).getQRHash());
//                    intent.putExtra("long",String.valueOf(qrDataList.get(getAdapterPosition()).getLongitude()));
//                    intent.putExtra("lat",String.valueOf(qrDataList.get(getAdapterPosition()).getLatitude()));
//                    view.getContext().startActivity(intent);
//                }
//            });

            comment_user = itemView.findViewById(R.id.comment_user);
            comment_text = itemView.findViewById(R.id.comment_text);
            comment_hash = itemView.findViewById(R.id.comment_hash);
            comment_timestamp = itemView.findViewById(R.id.comment_timestamp);

        }
    }
}
