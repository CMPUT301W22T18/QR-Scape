package com.example.qr_scape;

import java.io.File;

public class QRCode {
    private String QRID;
    private String location;
    private int score;
    private File imageQR;

    // Constructors
    public QRCode(String QRID) { // Only QRID provided
        this.QRID = QRID;
        this.score = calculateScore(QRID);
        this.imageQR = generateImageQR(QRID);
        this.location = null;
    }
    public QRCode(String QRID, String location) { // QRID and location provided
        this.QRID = QRID;
        this.location = location;
        this.score = calculateScore(QRID);
        this.imageQR = generateImageQR(QRID);
    }
    public QRCode(File imageQR) { // Only imageQR provided
        this.imageQR = imageQR;
        this.QRID = generateQRID(imageQR);
        this.score = calculateScore(QRID);
        this.location = null;
    }
    public QRCode(String location, File imageQR) { // imageQR and location
        this.location = location;
        this.imageQR = imageQR;
        this.QRID = generateQRID(imageQR);
        this.score = calculateScore(QRID);
    }

    public String getQRID() {
        return QRID;
    }

    public void setQRID(String QRID) {
        this.QRID = QRID;
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

    public File getImageQR() {
        return imageQR;
    }

    public void setImageQR(File imageQR) {
        this.imageQR = imageQR;
    }

    private int calculateScore(String QRID){
        int score = -1;
        // To do: Write the logic to calculate a QR codes score based on the QRID
        return score;
    }

    private String generateQRID(File imageQR){
        String newQRID = null;
        // To do: Write the logic to generate a QRID based on the imageQR
        return newQRID;
    }

    private File generateImageQR(String QRID){
        File newImageQR = null;
        // To do: Write the logic to generate a imageQR based on the QRID
        return newImageQR;
    }

}
