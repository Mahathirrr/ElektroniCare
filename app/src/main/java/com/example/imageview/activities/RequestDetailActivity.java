package com.example.imageview.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageview.R;
import com.example.imageview.adapters.PhotoViewAdapter;
import com.example.imageview.models.RepairRequest;
import com.example.imageview.utils.DateTimeUtil;
import com.example.imageview.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
public class RequestDetailActivity extends AppCompatActivity {

    private TextView tvDeviceType, tvDeviceBrand, tvDeviceModel, tvStatus, tvDateCreated, tvProblemDescription;
    private RecyclerView rvPhotos;
    private TextView tvNoPhotos;
    private Button btnViewOffers, btnEditRequest, btnDeleteRequest;
    private ProgressBar progressBar;

    private PhotoViewAdapter photoAdapter;
    private RepairRequest request;
    private String requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        // Get request ID from intent
        requestId = getIntent().getStringExtra("requestId");
        if (requestId == null) {
            Toast.makeText(this, "Permintaan tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        tvDeviceType = findViewById(R.id.tvDeviceType);
        tvDeviceBrand = findViewById(R.id.tvDeviceBrand);
        tvDeviceModel = findViewById(R.id.tvDeviceModel);
        tvStatus = findViewById(R.id.tvStatus);
        tvDateCreated = findViewById(R.id.tvDateCreated);
        tvProblemDescription = findViewById(R.id.tvProblemDescription);
        rvPhotos = findViewById(R.id.rvPhotos);
        tvNoPhotos = findViewById(R.id.tvNoPhotos);
        btnViewOffers = findViewById(R.id.btnViewOffers);
        btnEditRequest = findViewById(R.id.btnEditRequest);
        btnDeleteRequest = findViewById(R.id.btnDeleteRequest);
        progressBar = findViewById(R.id.progressBar);

        // Set up RecyclerView
        photoAdapter = new PhotoViewAdapter(this, new ArrayList<>());
        rvPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPhotos.setAdapter(photoAdapter);

        // Set up click listeners
        btnViewOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestDetailActivity.this, OffersActivity.class);
                intent.putExtra("requestId", requestId);
                startActivity(intent);
            }
        });

        btnEditRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestDetailActivity.this, EditRequestActivity.class);
                intent.putExtra("request", request);
                startActivity(intent);
            }
        });

        btnDeleteRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        // Load request data
        loadRequestData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload request data when returning to this activity
        loadRequestData();
    }

    private void loadRequestData() {
        progressBar.setVisibility(View.VISIBLE);

        DocumentReference requestRef = FirebaseUtil.getRequestDocument(requestId);
        requestRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        request = document.toObject(RepairRequest.class);
                        displayRequestData();
                    } else {
                        Toast.makeText(RequestDetailActivity.this, "Permintaan tidak ditemukan", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(RequestDetailActivity.this, "Gagal memuat data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void displayRequestData() {
        tvDeviceType.setText(request.getDeviceType());
        tvDeviceBrand.setText(request.getDeviceBrand());
        tvDeviceModel.setText(request.getDeviceModel());
        tvProblemDescription.setText(request.getProblemDescription());
        tvDateCreated.setText(DateTimeUtil.formatDate(request.getCreatedAt()));

        // Set status text and color
        String status = request.getStatus();
        if (status.equals("pending")) {
            tvStatus.setText("Menunggu");
        } else if (status.equals("in_progress")) {
            tvStatus.setText("Dalam Proses");
            btnEditRequest.setEnabled(false);
            btnDeleteRequest.setEnabled(false);
        } else if (status.equals("completed")) {
            tvStatus.setText("Selesai");
            btnEditRequest.setEnabled(false);
            btnDeleteRequest.setEnabled(false);
        } else if (status.equals("cancelled")) {
            tvStatus.setText("Dibatalkan");
            btnEditRequest.setEnabled(false);
            btnViewOffers.setEnabled(false);
        }

        // Display photos if any
        List<String> photoUrls = request.getPhotoUrls();
        if (photoUrls != null && !photoUrls.isEmpty()) {
            photoAdapter.setPhotoUrls(photoUrls);
            rvPhotos.setVisibility(View.VISIBLE);
            tvNoPhotos.setVisibility(View.GONE);
        } else {
            rvPhotos.setVisibility(View.GONE);
            tvNoPhotos.setVisibility(View.VISIBLE);
        }

        // Show/hide view offers button based on offers count
        if (request.getOffersCount() > 0) {
            btnViewOffers.setText(getString(R.string.view_offers) + " (" + request.getOffersCount() + ")");
        } else {
            btnViewOffers.setText(R.string.view_offers);
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Permintaan");
        builder.setMessage("Apakah Anda yakin ingin menghapus permintaan ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRequest();
            }
        });
        builder.setNegativeButton("Tidak", null);
        builder.show();
    }

    private void deleteRequest() {
        progressBar.setVisibility(View.VISIBLE);

        // Delete photos from storage if any
        List<String> photoUrls = request.getPhotoUrls();
        if (photoUrls != null && !photoUrls.isEmpty()) {
            for (String photoUrl : photoUrls) {
                StorageReference photoRef = FirebaseUtil.getStorage().getReferenceFromUrl(photoUrl);
                photoRef.delete();
            }
        }

        // Delete request from Firestore
        DocumentReference requestRef = FirebaseUtil.getRequestDocument(requestId);
        requestRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RequestDetailActivity.this, "Permintaan berhasil dihapus", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RequestDetailActivity.this, "Gagal menghapus permintaan: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
