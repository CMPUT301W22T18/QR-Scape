/*
 * User
 *
 * Version 1
 *
 * Feb 17 2022
 *
 * Licence info here
 */
package com.example.qr_scape;

/**
 * This class represents a users user
 * Contains username, password, contactInfo
 * @author Dallin Dmytryk
 * @version 1
 */
public class User {
    private String name;
    private String contactInfo;
    private Boolean owner;

    /**
     * Create a user object with assigned name and contact info
     * For other users a user may encounter
     * @param name
     * @param contactInfo
     */
    public User(String name, String contactInfo) {
        this.name = name;
        this.contactInfo = contactInfo;
    }

    /**
     * Create a user object with assigned name and contact info
     * For other users a user may encounter
     * @param name
     * @param contactInfo
     * @param owner
     */
    public User(String name, String contactInfo, Boolean owner) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.owner = owner;
    }

    /**
     * Gets the name of the user
     * @return the name of the user
     */
    public String getName() {
        return name;
    }
    

    /**
     * Gets the contact info of a user
     * @return the contact info of a user
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * Sets the contact info of a user
     * @param contactInfo string with user contact info
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
