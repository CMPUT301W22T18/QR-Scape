/*
 * Profile
 *
 * Version 1
 *
 * Feb 17 2022
 *
 * Licence info here
 */
package com.example.qr_scape;

/**
 * This class represents a users profile
 * Contains username, password, contactInfo
 * @author Dallin Dmytryk
 * @version 1
 */
public class Profile {
    private String name;
    private String password;
    private String contactInfo;

    /**
     * Create a profile object with assigned name and contact info
     * For other profiles a user may encounter
     * @param name
     * @param contactInfo
     */
    public Profile(String name, String contactInfo) {
        this.name = name;
        this.contactInfo = contactInfo;
    }

    /**
     * Create a profile object with assigned name, password and contact info
     * For the player
     * @param name
     * @param password
     * @param contactInfo
     */
    public Profile(String name, String password, String contactInfo) {
        this.name = name;
        this.password = password;
        this.contactInfo = contactInfo;
    }

    /**
     * Gets the name of the profile
     * @return the name of the profile
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the password of a profile
     * @return the password of a profile
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the contact info of a profile
     * @return the contact info of a profile
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * Sets the contact info of a profile
     * @param contactInfo string with profile contact info
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
