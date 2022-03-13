package com.example.qr_scape;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.hash.Hashing;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * This class handles the creation of QR code objects
 */
public class QRCode {
    private String QRHashSalted;
    private String QRHash;
    private double latitude;
    private double longitude;
    private int score;
    private String username;
    private Bitmap photo;

    // Constructors
    /**
     * This method is the constructor method which creates an object
     * of type QRCode, without location
     * @param realHash
     * @param saltedHash
     * @param latitude
     * @param longitude
     * @param photo
     * @param score
     * @param user
     */
    public QRCode(String realHash, String saltedHash,String user, double latitude, double longitude, Bitmap photo, int score) { // Only QRText provided
        this.QRHash = realHash; // Generate QRHash
        this.QRHashSalted = saltedHash; // QRHash is salted with the username
        this.score = score;
        this.longitude = longitude;
        this.latitude = latitude;
        this.username = user;
        this.photo = photo;
    }

    /**
     * This method is the constructor method which creates an object
     * of type QRCode, with location
     * @param QRText
     * @param user
     * @param latitude
     * @param longitude
     * @param photo
     */

    public QRCode(String QRText, String user, double latitude, double longitude, Bitmap photo) { // QRText and location provided
        this.QRHash = generateHash(QRText); // Generate QRHash
        this.QRHashSalted = generateHash((QRText + user)); // QRHash is salted with the username
        this.longitude = longitude;
        this.latitude = latitude;
        this.score = calculateScore(QRHash);
        this.username = user;
        this.photo = photo;
    }

    // Getters and Setters

    /**
     * This method gets the QR code hash (salted with the username) that
     * identifies this unique QRCode object
     * @return QRHash
     * Returns the QRHash of the QRCode object
     */
    public String getQRHashSalted() {
        return QRHashSalted;
    }

    /**
     * This method sets the QR code hash (salted with the username) that
     * identifies this unique QRCode object
     * @param QRHashSalted
     */
    public void setQRHashSalted(String QRHashSalted) {
        this.QRHashSalted = QRHashSalted;
    }

    /**
     * This method gets the QR code hash (not salted with the username) that
     * identifies this unique QRCode physical qr code
     * @return QRHash
     * Returns the QRHash of the physical QRCode object
     */
    public String getQRHash() {
        return QRHash;
    }

    /**
     * This method sets the QR code hash (not salted with the username) that
     * identifies the physical QRCode object
     * @param QRHash
     */
    public void setQRHash(String QRHash) {
        this.QRHash = QRHash;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * This method gets the score of the QRCode object
     * @return score
     * Returns the score of the QRCode object
     */
    public int getScore() {
        return score;
    }

    /**
     * This method sets the score of the QRCode object
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * This method calculates the score of the QRCode text
     * @param QRText
     * @return score
     * Returns the score of the QRCode text
     */
    public static int calculateScore(String QRText){

            int num_repeats = 0;
            int current_index = 1;
            int score = 0;
            for (int i = 1; i < QRText.length(); i++){
                char c = QRText.charAt(i);
                
                if (QRText.charAt(i) == QRText.charAt(i - 1)) {
                    num_repeats += 1;
                } else if (num_repeats > 0) {
                    int hexVal = Integer.parseInt(Character.toString(QRText.charAt(i - 1)), 16);
                    score += Math.pow(hexVal, num_repeats);
                    num_repeats = 0;
                }
            }
    
            // To do: Write the logic to calculate a QR codes score based on the QRID
            return score;
    }

    /**
     * This method gets the SHA-256 hash of the QRText string parameter
     * @param QRText
     * @return QRTextHash
     * Returns the QRTextHash of the QRText string
     */
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
