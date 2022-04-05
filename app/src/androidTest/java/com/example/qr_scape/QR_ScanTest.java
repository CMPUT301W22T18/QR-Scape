package com.example.qr_scape;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.util.HashMap;

@RunWith(AndroidJUnit4.class)

public class QR_ScanTest {
    private Solo solo;
    SharedPreferences sharedPreferences;
    private TextView latTextView;
    private TextView longTextView;
    private TextView hashTextView;
    private String testlatitude = "53.467760";
    private String testlongitude = "-113.396160";
    private String testscore = "32";
    private String testhash = "AFTGHDRGFG";

    private TextView scoreTextView;
    double latitude;
    double longitude;
    int score;

    @Rule
    public ActivityTestRule<QR_Scan> rule =
            new ActivityTestRule<>(QR_Scan.class,true,true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.app_name),context.MODE_PRIVATE);
    }


    @Test
    public void checkCurrentLocation() throws Exception{
        solo.clickOnView(solo.getView(R.id.scanbtn));
//        solo.clickOnView(solo.getView(R.id.buttonCurrentLocation));
//        assertTrue(solo.searchText(testlatitude));
//        assertTrue(solo.searchText(testlongitude));

    }
}
