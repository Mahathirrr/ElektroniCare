package com.example.imageview.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageview.R;
import com.example.imageview.adapters.PhotoAdapter;
import com.example.imageview.models.RepairRequest;
import com.example.imageview.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
public class EditRequestActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etDeviceBrand, etDeviceModel, etProblemDescription;
    private Spinner spinnerDeviceType;
    private RecyclerView rvPhotos;
    private Button btnUpdateRequest;
    private ProgressBar progressBar;

    private PhotoAdapter photoAdapter;
    private RepairRequest repairRequest;
    private String requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_request);

        // Get request ID from intent
        requestId = getIntent().getStringExtra("requestId");
        if (requestId == null) {
            Toast.makeText(this, "Error: Request ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        etDeviceBrand = findViewById(R.id.etDeviceBrand);
        etDeviceModel = findViewById(R.id.etDeviceModel);
        etProblemDescription = findViewById(R.id.etProblemDescription);
        spinnerDeviceType = findViewById(R.id.spinnerDeviceType);
        rvPhotos = findViewById(R.id.rvPhotos);
        btnUpdateRequest = findViewById(R.id.btnUpdateRequest);
        progressBar = findViewById(R.id.progressBar);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_request);

        // Set up RecyclerView
        rvPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Load request data
        loadRequestData();

        // Set up button click listener
        btnUpdateRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRequest();
            }
        });
    }

    private void loadRequestData() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUtil.getRequestsCollection().document(requestId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful() && task.getResult() != null) {
                            repairRequest = task.getResult().toObject(RepairRequest.class);

                            if (repairRequest != null) {
                                // Populate fields with request data
                                etDeviceBrand.setText(repairRequest.getDeviceBrand());
                                etDeviceModel.setText(repairRequest.getDeviceModel());
                                etProblemDescription.setText(repairRequest.getProblemDescription());

                                // Set spinner selection
                                String deviceType = repairRequest.getDeviceType();
                                for (int i = 0; i < spinnerDeviceType.getAdapter().getCount(); i++) {
                                    if (spinnerDeviceType.getAdapter().getItem(i).toString().equals(deviceType)) {
                                        spinnerDeviceType.setSelection(i);
                                        break;
                                    }
                                }

                                // Set up photo adapter
                                photoAdapter = new PhotoAdapter(EditRequestActivity.this, repairRequest.getPhotoUrls(), true);
                                rvPhotos.setAdapter(photoAdapter);
                            } else {
                                Toast.makeText(EditRequestActivity.this, "Error: Request not found", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(EditRequestActivity.this, "Error loading request: " +
                                            (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void updateRequest() {
        // Validate input
        String deviceBrand = etDeviceBrand.getText().toString().trim();
        String deviceModel = etDeviceModel.getText().toString().trim();
        String problemDescription = etProblemDescription.getText().toString().trim();
        String deviceType = spinnerDeviceType.getSelectedItem().toString();

        if (deviceBrand.isEmpty() || deviceModel.isEmpty() || problemDescription.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Create update map
        Map<String, Object> updates = new HashMap<>();
        updates.put("deviceType", deviceType);
        updates.put("deviceBrand", deviceBrand);
        updates.put("deviceModel", deviceModel);
        updates.put("problemDescription", problemDescription);

        // Update Firestore
        FirebaseUtil.getRequestsCollection().document(requestId)
                .update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Toast.makeText(EditRequestActivity.this, "Permintaan berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditRequestActivity.this, "Gagal memperbarui permintaan: " +
                                            (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                    Toast.LENGTH_SHORT).show();
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
