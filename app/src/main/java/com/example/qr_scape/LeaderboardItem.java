package com.example.qr_scape;
/*
 * Used for creating the recycler view of the leaderboard
 * @author Kiran Deol
 */
public class LeaderboardItem {
    String usernameItem;
    String scoreItem;
    String rankItem;
    int photoItem;
    LeaderboardItem(String username, String score, String rank, int photoId) {
        this.usernameItem = username;
        this.scoreItem = score;
        this.rankItem = rank;
        this.photoItem = photoId;
    }
}
