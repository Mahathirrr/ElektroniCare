package com.example.imageview.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageview.R;
import com.example.imageview.adapters.PhotoAdapter;
import com.example.imageview.models.RepairRequest;
import com.example.imageview.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateRequestActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextInputEditText etDeviceType, etDeviceBrand, etDeviceModel, etProblemDescription;
    private Button btnAddPhoto, btnSubmitRequest;
    private RecyclerView rvPhotos;
    private ProgressBar progressBar;

    private PhotoAdapter photoAdapter;
    private List<Uri> photoUris;
    private List<String> photoUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        // Initialize views
        etDeviceType = findViewById(R.id.etDeviceType);
        etDeviceBrand = findViewById(R.id.etDeviceBrand);
        etDeviceModel = findViewById(R.id.etDeviceModel);
        etProblemDescription = findViewById(R.id.etProblemDescription);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        rvPhotos = findViewById(R.id.rvPhotos);
        progressBar = findViewById(R.id.progressBar);

        // Initialize photo lists
        photoUris = new ArrayList<>();
        photoUrls = new ArrayList<>();

        // Set up RecyclerView
        photoAdapter = new PhotoAdapter(this, photoUris);
        rvPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPhotos.setAdapter(photoAdapter);

        // Set up click listeners
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnSubmitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });

        // Set up photo adapter listener
        photoAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                photoUris.remove(position);
                photoAdapter.notifyItemRemoved(position);
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            photoUris.add(imageUri);
            photoAdapter.notifyItemInserted(photoUris.size() - 1);
        }
    }

    private void submitRequest() {
        String deviceType = etDeviceType.getText().toString().trim();
        String deviceBrand = etDeviceBrand.getText().toString().trim();
        String deviceModel = etDeviceModel.getText().toString().trim();
        String problemDescription = etProblemDescription.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(deviceType)) {
            etDeviceType.setError("Jenis perangkat diperlukan");
            etDeviceType.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(deviceBrand)) {
            etDeviceBrand.setError("Merek perangkat diperlukan");
            etDeviceBrand.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(deviceModel)) {
            etDeviceModel.setError("Model perangkat diperlukan");
            etDeviceModel.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(problemDescription)) {
            etProblemDescription.setError("Deskripsi masalah diperlukan");
            etProblemDescription.requestFocus();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        btnSubmitRequest.setEnabled(false);

        // Create repair request
        String userId = FirebaseUtil.getCurrentUserId();
        RepairRequest request = new RepairRequest(userId, deviceType, deviceBrand, deviceModel, problemDescription);

        // Save request to Firestore
        DocumentReference requestRef = FirebaseUtil.getRequestsCollection().document();
        String requestId = requestRef.getId();
        request.setId(requestId);

        requestRef.set(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Upload photos if any
                    if (photoUris.size() > 0) {
                        uploadPhotos(requestId);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnSubmitRequest.setEnabled(true);
                        Toast.makeText(CreateRequestActivity.this, "Permintaan berhasil dibuat", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    btnSubmitRequest.setEnabled(true);
                    Toast.makeText(CreateRequestActivity.this, "Gagal membuat permintaan: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadPhotos(String requestId) {
        photoUrls.clear();
        final int totalPhotos = photoUris.size();
        final int[] uploadedCount = {0};

        for (Uri photoUri : photoUris) {
            String photoName = UUID.randomUUID().toString();
            StorageReference photoRef = FirebaseUtil.getRequestPhotosReference(requestId).child(photoName);

            photoRef.putFile(photoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get download URL
                            photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    photoUrls.add(uri.toString());
                                    uploadedCount[0]++;

                                    // Check if all photos are uploaded
                                    if (uploadedCount[0] == totalPhotos) {
                                        // Update request with photo URLs
                                        DocumentReference requestRef = FirebaseUtil.getRequestDocument(requestId);
                                        requestRef.update("photoUrls", photoUrls)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progressBar.setVisibility(View.GONE);
                                                        btnSubmitRequest.setEnabled(true);
                                                        Toast.makeText(CreateRequestActivity.this, "Permintaan berhasil dibuat", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressBar.setVisibility(View.GONE);
                                                        btnSubmitRequest.setEnabled(true);
                                                        Toast.makeText(CreateRequestActivity.this, "Gagal menyimpan URL foto: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            btnSubmitRequest.setEnabled(true);
                            Toast.makeText(CreateRequestActivity.this, "Gagal mengunggah foto: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
