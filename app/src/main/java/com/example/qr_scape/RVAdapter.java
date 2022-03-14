//Copyright 2022, Kiran Deol
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapater for recycler view of leaderboard
 * @link https://developer.android.com/guide/topics/ui/layout/recyclerview
 * @author Kiran Deol
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PlayerViewHolder>{

    List<LeaderboardItem> players;

    RVAdapter(List<LeaderboardItem> players){
        this.players = players;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        PlayerViewHolder pvh = new PlayerViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder player, int position) {
        player.playerUsername.setText(players.get(position).usernameItem);
        player.playerScore.setText(players.get(position).scoreItem);
        player.playerRank.setText(players.get(position).rankItem);
        player.playerPhoto.setImageResource(players.get(position).photoItem);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView playerUsername;
        TextView playerScore;
        TextView playerRank;
        ImageView playerPhoto;

        PlayerViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            playerUsername = (TextView)itemView.findViewById(R.id.leaderboard_username);
            playerScore = (TextView)itemView.findViewById(R.id.leaderboard_score);
            playerRank = (TextView)itemView.findViewById(R.id.leaderboard_rank);
            playerPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

}
