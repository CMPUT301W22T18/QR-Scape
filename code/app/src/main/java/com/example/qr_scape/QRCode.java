package com.example.qr_scape;

import com.google.common.hash.Hashing;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * This class handles the creation of QR code objects
 */
public class QRCode {
    private String QRHash;
    private String location;
    private int score;

    // Constructors
    /**
     * This method is the constructor method which creates an object
     * of type QRCode, without location
     * @param QRText
     * @param user
     */
    public QRCode(String QRText, String user) { // Only QRText provided
        this.QRHash = generateHash((QRText + user)); // QRHash is salted with the username
        this.score = calculateScore(QRText);
        this.location = null;
    }
    /**
     * This method is the constructor method which creates an object
     * of type QRCode, with location
     * @param QRText
     * @param user
     * @param location
     */
    public QRCode(String QRText, String user, String location) { // QRText and location provided
        this.QRHash = generateHash((QRText + user)); // QRHash is salted with the username
        this.location = location;
        this.score = calculateScore(QRText);
    }

    /**
     * This method gets the QR code hash (salted with the username) that
     * identifies this unique QRCode object
     * @return QRHash
     * Returns the QRHash of the QRCode object
     */
    public String getQRHash() {
        return QRHash;
    }
    /**
     * This method sets the QR code hash (salted with the username) that
     * identifies this unique QRCode object
     * @param QRHash
     */
    public void setQRHash(String QRHash) {
        this.QRHash = QRHash;
    }
    /**
     * This method gets the location of the QRCode object
     * @return location
     * Returns the location of the QRCode object
     */
    public String getLocation() {
        return location;
    }
    /**
     * This method sets the location of the QRCode object
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
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
     * This method calculates the score of the QRCode object based on the hash
     * of the QRCodeText
     * @param QRID
     * @return score
     * Returns the score of the QRCode object
     */
    public static int calculateScore(String QRText){
            /**
             * Given the hash of a QR CODE (the QRID), we compute its score
             * Inputs: String QRID
             * Outputs: int score
             */
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
