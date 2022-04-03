package com.example.qr_scape;


import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NavBarTests {
    private Solo solo;
    final private  String PROFILES = "Profiles";
    final private String USERNAME = "Username";
    final private String ContactInfo = "Test Contact info";
    private String Username = "testingusername";
    SharedPreferences sharedPreferences;


    @Rule
    public ActivityTestRule<Home> rule =
            new ActivityTestRule<>(Home.class,true,true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void openSearch() {
        solo.clickOnView(solo.getView(R.id.nav_search));
        solo.assertCurrentActivity("Check that current activity is search", Search.class);
    }

    @Test
    public void openScan() {
        solo.clickOnView(solo.getView(R.id.nav_scan));
        solo.assertCurrentActivity("Check that current activity is search", QR_Scan.class);
    }

    @Test
    public void openLocation() {
        solo.clickOnView(solo.getView(R.id.nav_location));
        solo.assertCurrentActivity("Check that current activity is search", Location.class);
    }

    @Test
    public void openProfile() {
        solo.clickOnView(solo.getView(R.id.nav_profile));
        solo.assertCurrentActivity("Check that current activity is search", ProfileActivity.class);
    }

    @Test
    public void openHome() {
        // must enter another activity before testing home
        solo.clickOnView(solo.getView(R.id.nav_profile));
        solo.clickOnView(solo.getView(R.id.nav_home));
        solo.assertCurrentActivity("Check that current activity is search", Home.class);
    }

}
