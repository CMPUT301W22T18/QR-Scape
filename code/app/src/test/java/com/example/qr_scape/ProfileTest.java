/*
 * ProfileTest
 *
 * Version 1
 *
 * Feb 17 2022
 *
 * Licence info here
 */
package com.example.qr_scape;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProfileTest {

    public Profile userProfile() {
        return new Profile("User","UserPassword", "UserContactInfo");
    }

    public Profile nonUserProfile() {
        return new Profile("NonUser","NonUserContactInfo");
    }

    @Test
    public void testName() {
        assertEquals("User",userProfile().getName());
        assertEquals("NonUser",nonUserProfile().getName());
    }

    @Test
    public void testPassword() {
        assertEquals("UserPassword",userProfile().getPassword());
        assertEquals(null,nonUserProfile().getPassword());
    }

    @Test
    public void testContactInfo() {
        assertEquals("UserContactInfo",userProfile().getContactInfo());
        assertEquals("NonUserContactInfo",nonUserProfile().getContactInfo());
    }

    @Test
    public void testAddContactInfo() {
        Profile profile = new Profile("test","oldContactInfo");
        String contactInfo = "newContactInfo";
        profile.setContactInfo(contactInfo);
        assertEquals(contactInfo, profile.getContactInfo());
    }
}
