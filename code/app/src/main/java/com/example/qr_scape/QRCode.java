package com.example.qr_scape;

import com.google.common.hash.Hashing;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class QRCode {
    private String QRHash;
    private String location;
    private int score;

    // Constructors
    public QRCode(String QRText, String user) { // Only QRText provided
        this.QRHash = generateHash((QRText + user)); // QRHash is salted with the username
        this.score = calculateScore(QRText);
        this.location = null;
    }
    public QRCode(String QRText, String user, String location) { // QRText and location provided
        this.QRHash = generateHash((QRText + user)); // QRHash is salted with the username
        this.location = location;
        this.score = calculateScore(QRText);
    }


    public String getQRHash() {
        return QRHash;
    }

    public void setQRHash(String QRHash) {
        this.QRHash = QRHash;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    private int calculateScore(String QRText){
        int score = -1;
        String normalizedQRHash = generateHash(QRText);
        // To do: Write the logic to calculate a QR codes score based on the QRHash
        return score;
    }

    private String generateHash(String QRText){
        // From: https://stackoverflow.com/
        // Link: https://stackoverflow.com/a/18340262
        // Author: https://stackoverflow.com/users/69875/jonathan
        // License: https://creativecommons.org/licenses/by-sa/3.0/
        final String QRTextHash = Hashing.sha256()
                .hashString(QRText, StandardCharsets.UTF_8)
                .toString();
        // To do: Write the logic to generate the hash of the QR code text
        return QRTextHash;
    }

}
