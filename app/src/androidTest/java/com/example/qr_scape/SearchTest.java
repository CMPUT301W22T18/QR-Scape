package com.example.qr_scape;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SearchTest {
    private Solo solo;
    SharedPreferences sharedPreferences;
    private final String Username = "testingusername";

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class,true,true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name), Context.MODE_PRIVATE);

        solo.clickOnView(solo.getView(R.id.login_create_profile_button));
        solo.clickOnView((EditText) solo.getView(R.id.login_username_edittext));
        solo.enterText((EditText) solo.getView(R.id.login_username_edittext), Username);
        solo.clickOnView(solo.getView(R.id.login_confirm_button));
        solo.sleep(500);
        solo.clickOnView(solo.getView(R.id.nav_search));
    }

    @Test
    public void userSearchTest() {
        solo.enterText((EditText) solo.getView(R.id.search_terms_edittext), "a");
        solo.clickOnView(solo.getView(R.id.search_search_button));
        assertTrue(solo.waitForText("a"));
    }

    @Test void codeSearchTest() {
        solo.clickOnView(solo.getView(R.id.search_setting_button));
        solo.enterText((EditText) solo.getView(R.id.search_terms_edittext), "9");
        solo.clickOnView(solo.getView(R.id.search_search_button));
        assertTrue(solo.waitForText("9"));
    }
}
