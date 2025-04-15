package com.example.imageview.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.example.imageview.R;
import com.example.imageview.fragments.HomeFragment;
import com.example.imageview.fragments.OffersFragment;
import com.example.imageview.fragments.ProfileFragment;
import com.example.imageview.fragments.RequestsFragment;
import com.example.imageview.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabCreateRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Ensure Firebase is initialized
            FirebaseApp.initializeApp(this);

            // Check if user is logged in
            if (!FirebaseUtil.isLoggedIn()) {
                Log.d(TAG, "User not logged in, redirecting to LoginActivity");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            // Initialize views
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            fabCreateRequest = findViewById(R.id.fab_create_request);

            // Set up listeners

            // Get the NavController
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            NavController navController = navHostFragment.getNavController();
            bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.replace(R.id.fragment_container,getFragment(item));
                transaction.commit();
                return true;

            });


            fabCreateRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, CreateRequestActivity.class));
                }
            });

            // Set default fragment
            if (savedInstanceState == null) {
                try {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                } catch (Exception e) {
                    Log.e(TAG, "Error setting default fragment", e);
                    // Fallback to manually loading the home fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment())
                            .commit();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in MainActivity onCreate", e);
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Redirect to login as fallback
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }


    private Fragment getFragment(MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_home) {
            selectedFragment = new HomeFragment();
        }
        else if (itemId == R.id.navigation_requests) {
            selectedFragment = new RequestsFragment();
        }
        else if (itemId == R.id.navigation_offers) {
            selectedFragment = new OffersFragment();
        }
        else if (itemId == R.id.navigation_profile) {
            selectedFragment = new ProfileFragment();
        }
        return selectedFragment;
    }
}
