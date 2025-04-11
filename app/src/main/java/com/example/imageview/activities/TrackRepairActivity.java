package com.example.imageview.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.imageview.R;
import com.example.imageview.models.Repair;
import com.example.imageview.models.RepairRequest;
import com.example.imageview.utils.CurrencyUtil;
import com.example.imageview.utils.DateTimeUtil;
import com.example.imageview.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
public class TrackRepairActivity extends AppCompatActivity {

    private TextView tvDeviceInfo;
    private TextView tvServiceProviderName, tvServiceProviderContact;
    private Button btnContactProvider;
    private View viewStatusReceived, viewStatusDiagnosed, viewStatusRepairing, viewStatusCompleted;
    private TextView tvCurrentStatus, tvStatusDescription, tvEstimatedCompletion;
    private TextView tvPrice, tvPaymentStatus;
    private Button btnPayNow;
    private ProgressBar progressBar;

    private String repairId;
    private Repair repair;
    private RepairRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_repair);

        // Get repair ID from intent
        repairId = getIntent().getStringExtra("repairId");
        if (repairId == null) {
            Toast.makeText(this, "Perbaikan tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo);
        tvServiceProviderName = findViewById(R.id.tvServiceProviderName);
        tvServiceProviderContact = findViewById(R.id.tvServiceProviderContact);
        btnContactProvider = findViewById(R.id.btnContactProvider);
        viewStatusReceived = findViewById(R.id.viewStatusReceived);
        viewStatusDiagnosed = findViewById(R.id.viewStatusDiagnosed);
        viewStatusRepairing = findViewById(R.id.viewStatusRepairing);
        viewStatusCompleted = findViewById(R.id.viewStatusCompleted);
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);
        tvStatusDescription = findViewById(R.id.tvStatusDescription);
        tvEstimatedCompletion = findViewById(R.id.tvEstimatedCompletion);
        tvPrice = findViewById(R.id.tvPrice);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        btnPayNow = findViewById(R.id.btnPayNow);
        progressBar = findViewById(R.id.progressBar);

        // Set up click listeners
        btnContactProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repair != null && repair.getServiceProviderPhone() != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + repair.getServiceProviderPhone()));
                    startActivity(intent);
                }
            }
        });

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // In a real app, this would navigate to a payment screen
                // For this demo, we'll just update the payment status
                updatePaymentStatus();
            }
        });

        // Load repair data
        loadRepairData();
    }

    private void loadRepairData() {
        progressBar.setVisibility(View.VISIBLE);

        DocumentReference repairRef = FirebaseUtil.getRepairDocument(repairId);
        repairRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        repair = document.toObject(Repair.class);
                        loadRequestData();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(TrackRepairActivity.this, "Perbaikan tidak ditemukan", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TrackRepairActivity.this, "Gagal memuat data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadRequestData() {
        DocumentReference requestRef = FirebaseUtil.getRequestDocument(repair.getRequestId());
        requestRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        request = document.toObject(RepairRequest.class);
                        displayRepairData();
                    } else {
                        Toast.makeText(TrackRepairActivity.this, "Permintaan tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TrackRepairActivity.this, "Gagal memuat data permintaan: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void displayRepairData() {
        // Display device info
        tvDeviceInfo.setText(request.getDeviceBrand() + " " + request.getDeviceModel() + " - " + request.getProblemDescription());

        // Display service provider info
        tvServiceProviderName.setText(repair.getServiceProviderName());
        tvServiceProviderContact.setText(repair.getServiceProviderPhone());

        // Display repair status
        updateStatusIndicators();

        // Display payment info
        tvPrice.setText(CurrencyUtil.formatRupiah(repair.getPrice()));

        if (repair.getPaymentStatus().equals("pending")) {
            tvPaymentStatus.setText("Belum Dibayar");
            tvPaymentStatus.setTextColor(ContextCompat.getColor(this, R.color.error));
            btnPayNow.setVisibility(View.VISIBLE);
        } else {
            tvPaymentStatus.setText("Sudah Dibayar");
            tvPaymentStatus.setTextColor(ContextCompat.getColor(this, R.color.success));
            btnPayNow.setVisibility(View.GONE);
        }
    }

    private void updateStatusIndicators() {
        String status = repair.getRepairStatus();
        int primaryColor = ContextCompat.getColor(this, R.color.primary);
        int grayColor = ContextCompat.getColor(this, R.color.gray);

        // Reset all indicators to gray
        viewStatusReceived.setBackgroundColor(grayColor);
        viewStatusDiagnosed.setBackgroundColor(grayColor);
        viewStatusRepairing.setBackgroundColor(grayColor);
        viewStatusCompleted.setBackgroundColor(grayColor);

        // Update status text and description
        if (status.equals("received")) {
            viewStatusReceived.setBackgroundColor(primaryColor);
            tvCurrentStatus.setText("Perangkat Diterima");
            tvStatusDescription.setText("Perangkat Anda telah diterima oleh penyedia layanan dan sedang menunggu diagnosa.");
        } else if (status.equals("diagnosed")) {
            viewStatusReceived.setBackgroundColor(primaryColor);
            viewStatusDiagnosed.setBackgroundColor(primaryColor);
            tvCurrentStatus.setText("Perangkat Didiagnosa");
            tvStatusDescription.setText("Perangkat Anda telah didiagnosa dan akan segera diperbaiki.");
        } else if (status.equals("repairing")) {
            viewStatusReceived.setBackgroundColor(primaryColor);
            viewStatusDiagnosed.setBackgroundColor(primaryColor);
            viewStatusRepairing.setBackgroundColor(primaryColor);
            tvCurrentStatus.setText("Sedang Diperbaiki");
            tvStatusDescription.setText("Teknisi sedang melakukan perbaikan pada perangkat Anda.");
        } else if (status.equals("completed")) {
            viewStatusReceived.setBackgroundColor(primaryColor);
            viewStatusDiagnosed.setBackgroundColor(primaryColor);
            viewStatusRepairing.setBackgroundColor(primaryColor);
            viewStatusCompleted.setBackgroundColor(primaryColor);
            tvCurrentStatus.setText("Perbaikan Selesai");
            tvStatusDescription.setText("Perangkat Anda telah selesai diperbaiki dan siap untuk diambil.");
        }

        // Display estimated completion date
        if (repair.getEstimatedCompletionDate() != null) {
            tvEstimatedCompletion.setText("Estimasi selesai: " + DateTimeUtil.formatDate(repair.getEstimatedCompletionDate()));
        } else {
            tvEstimatedCompletion.setText("Estimasi selesai: Belum ditentukan");
        }
    }

    private void updatePaymentStatus() {
        progressBar.setVisibility(View.VISIBLE);

        DocumentReference repairRef = FirebaseUtil.getRepairDocument(repairId);
        repairRef.update("paymentStatus", "paid")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(TrackRepairActivity.this, "Pembayaran berhasil", Toast.LENGTH_SHORT).show();
                            repair.setPaymentStatus("paid");
                            displayRepairData();
                        } else {
                            Toast.makeText(TrackRepairActivity.this, "Gagal memperbarui status pembayaran: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
