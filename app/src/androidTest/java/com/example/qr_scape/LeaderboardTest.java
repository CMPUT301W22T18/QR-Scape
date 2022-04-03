package com.example.qr_scape;


import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
public class LeaderboardTest {
    private Solo solo;
    SharedPreferences sharedPreferences;


    @Rule
    public ActivityTestRule<LeaderboardActivity> rule =
            new ActivityTestRule<>(LeaderboardActivity.class,true,true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name),context.MODE_PRIVATE);
    }

    @Test
    public void updateContactInfoTest() {
        solo.waitForActivity(LeaderboardActivity.class);
        solo.pressSpinnerItem(0, 0);
        assertTrue(solo.isSpinnerTextSelected(0, "Total Score"));

        solo.pressSpinnerItem(0, 1);
        assertTrue(solo.isSpinnerTextSelected(0, "Highest Score"));

        solo.pressSpinnerItem(0, 2);
        assertTrue(solo.isSpinnerTextSelected(0, "Total Scans"));
    }

}
