package com.example.imageview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imageview.R;
import com.example.imageview.utils.FirebaseUtil;
import com.google.firebase.FirebaseApp;
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2000; // 2 seconds
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase", e);
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                // Navigate to OnboardingActivity
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
            } catch (Exception e) {
                Log.e(TAG, "Error navigating from splash screen", e);
                // If there's an error, go to login activity as fallback
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
