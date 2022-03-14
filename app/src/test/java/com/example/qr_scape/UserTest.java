/*
 * UserTest
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

public class UserTest {

    public User userUser() {
        return new User("User","UserContactInfo");
    }

    public User nonUserUser() {
        return new User("NonUser","NonUserContactInfo");
    }

    @Test
    public void testName() {
        assertEquals("User",userUser().getName());
        assertEquals("NonUser",nonUserUser().getName());
    }


    @Test
    public void testContactInfo() {
        assertEquals("UserContactInfo",userUser().getContactInfo());
        assertEquals("NonUserContactInfo",nonUserUser().getContactInfo());
    }

    @Test
    public void testAddContactInfo() {
        User user = new User("test","oldContactInfo");
        String contactInfo = "newContactInfo";
        user.setContactInfo(contactInfo);
        assertEquals(contactInfo, user.getContactInfo());
    }
}
