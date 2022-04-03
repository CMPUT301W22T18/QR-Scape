package com.example.qr_scape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
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
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ScoreTest {
    private Solo solo;
    private String Username = "william smith";
    final private  String PROFILES = "Profiles";
    SharedPreferences sharedPreferences;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class,true,true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name),context.MODE_PRIVATE);
    }

    @Before
    public void deleteAccount() {
        SharedPreferences.Editor shEditor = sharedPreferences.edit();
        shEditor.clear().commit();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(PROFILES).document(Username).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("deleting profile", "Deleted profile");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("deleting profile", "Failed to delete profile");
                    }
                });
    }


    public void createProfile() {
        solo.clickOnView(solo.getView(R.id.login_create_profile_button));
        solo.clickOnView((EditText) solo.getView(R.id.login_username_edittext));
        solo.enterText((EditText) solo.getView(R.id.login_username_edittext), "william smith");
        solo.clickOnView(solo.getView(R.id.login_confirm_button));
        solo.clickOnView(solo.getView(R.id.nav_profile));
    }

    public void preUpdateProfileFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> testProfile = new HashMap<>();
        testProfile.put("Highest Score", 99);
        testProfile.put("Total Score", 121);
        testProfile.put("Total Scans", 21);

        db.collection(PROFILES).document(Username)
                .set(testProfile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ScoreTest", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ScoreTest", "Error writing document", e);
                    }
                });
    }

    public void updateStats() {
        // Hash below was taken from the eClass project desc
        ScoreActivity score = new ScoreActivity(Username, "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6");
        score.updateLowestScore();
        score.updateHighestScore();
        score.updateTotalScore();
        score.updateNumberOfScans();
    }

    @Test
    public void testUpdate() throws Exception{
        createProfile();
        preUpdateProfileFirestore();
        solo.sleep(4000);
        solo.clickOnView(solo.getView(R.id.personal_stats_button));

        TextView highestScoreView = (TextView) solo.getView((R.id.highest_score_value));
        String highestScore = highestScoreView.getText().toString();
        assertEquals("99", highestScore);

        TextView numberScansView = (TextView) solo.getView((R.id.num_codes_value));
        String numberScans = numberScansView.getText().toString();
        assertEquals("21", numberScans);

        TextView sumScoresView = (TextView) solo.getView((R.id.sum_scores_value));
        String sumScore = sumScoresView.getText().toString();
        assertEquals("121", sumScore);

        TextView lowestScoreView = (TextView) solo.getView((R.id.lowest_score_value));
        String lowScore = lowestScoreView.getText().toString();
        assertEquals("0", lowScore);

        solo.goBack();

        updateStats();
        solo.sleep(6000);
        solo.clickOnView(solo.getView(R.id.personal_stats_button));

        TextView highestScoreViewUpdated = (TextView) solo.getView((R.id.highest_score_value));
        String highestScoreUpdated = highestScoreViewUpdated.getText().toString();
        assertEquals("111", highestScoreUpdated);

        TextView numberScansViewUpdated = (TextView) solo.getView((R.id.num_codes_value));
        String numberScansUpdated = numberScansViewUpdated.getText().toString();
        assertEquals("22", numberScansUpdated);

        TextView sumScoresViewUpdated = (TextView) solo.getView((R.id.sum_scores_value));
        String sumScoreUpdated = sumScoresViewUpdated.getText().toString();
        assertEquals("232", sumScoreUpdated);

        TextView lowestScoreViewUpdated = (TextView) solo.getView((R.id.lowest_score_value));
        String lowScoreUpdated = lowestScoreViewUpdated.getText().toString();
        assertEquals("111", lowScoreUpdated);
    }

    @After
    public void afterTest(){
        deleteAccount();
    }

}
