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
        /**
         * Given the hash of a QR CODE (the QRID), we compute its score
         * Inputs: String QRID
         * Outputs: int score
         */
        int num_repeats = 0;
        int current_index = 1;
        int score = 0;

        for (int i = 1; i < QRID.length(); i++){
            char c = QRID.charAt(i);
            if (QRID.charAt(i) == QRID.charAt(i - 1)) {
                num_repeats += 1;
            } else if (num_repeats > 0) {
                int hexVal = Integer.parseInt(Character.toString(QRID.charAt(i - 1)), 16);
                score += Math.pow(hexVal, num_repeats);
                num_repeats = 0;
            }

        }
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
