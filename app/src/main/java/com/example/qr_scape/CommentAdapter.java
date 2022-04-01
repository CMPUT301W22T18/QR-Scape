//Copyright 2022, Ty Greve
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
package com.example.qr_scape;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.myviewholder> {
    ArrayList<Comment> commentDataList;

    public CommentAdapter(ArrayList<Comment> commentDataList) {
        this.commentDataList = commentDataList;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_content, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.comment_text.setText(commentDataList.get(position).getCommentText());
        holder.comment_user.setText(commentDataList.get(position).getUsername());
        holder.comment_timestamp.setText(commentDataList.get(position).getTimestamp());
    }

    @Override
    public int getItemCount() {
        return commentDataList.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {
        TextView comment_user, comment_text, comment_hash, comment_timestamp;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            comment_text = itemView.findViewById(R.id.comment_text);
            comment_user = itemView.findViewById(R.id.comment_user);
            comment_timestamp = itemView.findViewById(R.id.comment_timestamp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // fill the edit text view on the comments_layout.xml with comment_text

//                    final String USERNAME = "Username";
//                    final String ISOWNER = "isOwner";
//                    // Check shared preferences for username or isOwner flag
//                    SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);
//                    String username = sharedPreferences.getString(USERNAME, null);
//                    String isOwner = sharedPreferences.getString(USERNAME, null);
//
//                    if (comment_user.getText().toString().equals(username) || isOwner.equals("True")){
//                        Button deleteButton = findViewById(R.id.deleteCommentButton);
//                        deleteButton.setVisibility(View.VISIBLE);
//                    }

                }
            });

        }
    }
}
