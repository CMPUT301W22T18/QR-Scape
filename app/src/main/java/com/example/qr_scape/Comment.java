package com.example.qr_scape;

public class Comment {
    private String qrInstance;
    private String commentText;
    private String timestamp;
    private String username;

    public Comment() {
    }

    public Comment(String qrInstance, String commentText, String timestamp,  String username) {
        this.qrInstance = qrInstance;
        this.commentText = commentText;
        this.timestamp = timestamp;
        this.username = username;
    }

    public String getQrInstance() {
        return qrInstance;
    }

    public void setQrInstance(String qrInstance) {
        this.qrInstance = qrInstance;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
