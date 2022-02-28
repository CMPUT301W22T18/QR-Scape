package com.example.qr_scape;

import com.google.common.hash.Hashing;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class QRCode {
    private String QRHash;
    private String location;
    private int score;

    // Constructors
    public QRCode(String QRText) { // Only QRText provided
        this.QRHash = generateHash(QRText);
        this.score = calculateScore(QRHash);
        this.location = null;
    }
    public QRCode(String QRText, String location) { // QRText and location provided
        this.QRHash = generateHash(QRText);
        this.location = location;
        this.score = calculateScore(QRHash);
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


    private int calculateScore(String QRHash){
        int score = -1;
        // To do: Write the logic to calculate a QR codes score based on the QRHash
        return score;
    }

    private String generateHash(String QRText){
        final String QRTextHash = Hashing.sha256()
                .hashString(QRText, StandardCharsets.UTF_8)
                .toString();
        // To do: Write the logic to generate the hash of the QR code text
        return QRTextHash;
    }

}
