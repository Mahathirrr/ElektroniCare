package com.example.imageview.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.imageview.R;
import com.example.imageview.models.User;
import com.example.imageview.utils.FirebaseUtil;
public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etAddress;
    private Button btnSave;
    private ProgressBar progressBar;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.profile);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        // Load user data
        loadUserData();

        // Set up save button
        btnSave.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        showLoading(true);
        FirebaseUtil.getUsersCollection().document(FirebaseUtil.getCurrentUserId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    showLoading(false);
                    if (documentSnapshot.exists()) {
                        currentUser = documentSnapshot.toObject(User.class);
                        populateUserData();
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(ProfileActivity.this,
                            getString(R.string.error_loading_profile),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void populateUserData() {
        if (currentUser != null) {
            etName.setText(currentUser.getName());
            etEmail.setText(currentUser.getEmail());
            etPhone.setText(currentUser.getPhone());
            etAddress.setText(currentUser.getAddress());
        }
    }

    private void saveUserData() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty()) {
            etName.setError(getString(R.string.name_required));
            etName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError(getString(R.string.phone_required));
            etPhone.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            etAddress.setError(getString(R.string.address_required));
            etAddress.requestFocus();
            return;
        }

        // Update user object
        currentUser.setName(name);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        // Save to Firestore
        showLoading(true);
        FirebaseUtil.getUsersCollection().document(FirebaseUtil.getCurrentUserId())
                .set(currentUser)
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);
                    Toast.makeText(ProfileActivity.this,
                            getString(R.string.profile_updated),
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(ProfileActivity.this,
                            getString(R.string.error_updating_profile),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!isLoading);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
