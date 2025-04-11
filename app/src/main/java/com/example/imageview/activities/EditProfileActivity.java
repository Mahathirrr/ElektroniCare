package com.example.imageview.activities;

import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.imageview.R;
import com.example.imageview.models.User;
import com.example.imageview.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Toolbar toolbar;
    private CircleImageView ivProfileImage;
    private EditText etName, etPhone, etAddress;
    private Button btnSave;
    private ProgressBar progressBar;

    private User currentUser;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_profile);

        // Load user data
        loadUserData();

        // Set up click listeners
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        
        String userId = FirebaseUtil.getCurrentUserId();
        FirebaseUtil.getUsersCollection().document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        
                        if (task.isSuccessful() && task.getResult() != null) {
                            currentUser = task.getResult().toObject(User.class);
                            
                            if (currentUser != null) {
                                // Populate fields with user data
                                etName.setText(currentUser.getName());
                                etPhone.setText(currentUser.getPhone());
                                etAddress.setText(currentUser.getAddress());
                                
                                // Load profile image
                                if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
                                    Glide.with(EditProfileActivity.this)
                                            .load(currentUser.getProfileImageUrl())
                                            .placeholder(R.drawable.ic_profile_placeholder)
                                            .into(ivProfileImage);
                                }
                            }
                        } else {
                            Toast.makeText(EditProfileActivity.this, R.string.error_loading_profile, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openImagePicker() {
        // Intent to open image picker
        // Implementation would go here
    }

    private void saveProfile() {
        // Validate input
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        
        if (name.isEmpty()) {
            etName.setError(getString(R.string.name_required));
            return;
        }
        
        if (phone.isEmpty()) {
            etPhone.setError(getString(R.string.phone_required));
            return;
        }
        
        if (address.isEmpty()) {
            etAddress.setError(getString(R.string.address_required));
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        
        // If there's a new image, upload it first
        if (imageUri != null) {
            uploadProfileImage(name, phone, address);
        } else {
            updateUserProfile(name, phone, address, currentUser.getProfileImageUrl());
        }
    }

    private void uploadProfileImage(final String name, final String phone, final String address) {
        String userId = FirebaseUtil.getCurrentUserId();
        FirebaseUtil.getStorageReference().child("profile_images").child(userId)
                .putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        updateUserProfile(name, phone, address, imageUrl);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(EditProfileActivity.this, "Gagal mengunggah gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserProfile(String name, String phone, String address, String profileImageUrl) {
        String userId = FirebaseUtil.getCurrentUserId();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("address", address);
        if (profileImageUrl != null) {
            updates.put("profileImageUrl", profileImageUrl);
        }
        
        FirebaseUtil.getUsersCollection().document(userId)
                .update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditProfileActivity.this, R.string.error_updating_profile, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}