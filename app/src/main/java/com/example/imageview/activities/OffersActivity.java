package com.example.imageview.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageview.R;
import com.example.imageview.adapters.OfferAdapter;
import com.example.imageview.models.Offer;
import com.example.imageview.models.Repair;
import com.example.imageview.models.RepairRequest;
import com.example.imageview.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class OffersActivity extends AppCompatActivity implements OfferAdapter.OnOfferActionListener {

    private TextView tvOffersTitle, tvRequestInfo, tvNoOffers;
    private RecyclerView rvOffers;
    private ProgressBar progressBar;

    private OfferAdapter offerAdapter;
    private List<Offer> offerList;
    private String requestId;
    private RepairRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        // Get request ID from intent
        requestId = getIntent().getStringExtra("requestId");
        if (requestId == null) {
            Toast.makeText(this, "Permintaan tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        tvOffersTitle = findViewById(R.id.tvOffersTitle);
        tvRequestInfo = findViewById(R.id.tvRequestInfo);
        tvNoOffers = findViewById(R.id.tvNoOffers);
        rvOffers = findViewById(R.id.rvOffers);
        progressBar = findViewById(R.id.progressBar);

        // Initialize offer list and adapter
        offerList = new ArrayList<>();
        offerAdapter = new OfferAdapter(this, offerList, this);
        rvOffers.setLayoutManager(new LinearLayoutManager(this));
        rvOffers.setAdapter(offerAdapter);

        // Load request and offers data
        loadRequestData();
    }

    private void loadRequestData() {
        progressBar.setVisibility(View.VISIBLE);

        DocumentReference requestRef = FirebaseUtil.getRequestDocument(requestId);
        requestRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        request = document.toObject(RepairRequest.class);
                        tvRequestInfo.setText(request.getDeviceBrand() + " " + request.getDeviceModel() + " - " + request.getProblemDescription());
                        loadOffers();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OffersActivity.this, "Permintaan tidak ditemukan", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OffersActivity.this, "Gagal memuat data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadOffers() {
        FirebaseUtil.getOffersCollection()
                .whereEqualTo("requestId", requestId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            offerList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Offer offer = document.toObject(Offer.class);
                                offerList.add(offer);
                            }
                            offerAdapter.notifyDataSetChanged();

                            // Show/hide no offers message
                            if (offerList.isEmpty()) {
                                tvNoOffers.setVisibility(View.VISIBLE);
                                rvOffers.setVisibility(View.GONE);
                            } else {
                                tvNoOffers.setVisibility(View.GONE);
                                rvOffers.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(OffersActivity.this, "Gagal memuat penawaran: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onAcceptOffer(Offer offer) {
        progressBar.setVisibility(View.VISIBLE);

        // Update offer status to accepted
        DocumentReference offerRef = FirebaseUtil.getOfferDocument(offer.getId());
        offerRef.update("status", "accepted")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update request status to in_progress
                        DocumentReference requestRef = FirebaseUtil.getRequestDocument(requestId);
                        requestRef.update("status", "in_progress")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Create repair record
                                        createRepairRecord(offer);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(OffersActivity.this, "Gagal memperbarui status permintaan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OffersActivity.this, "Gagal menerima penawaran: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void createRepairRecord(Offer offer) {
        // Get service provider data
        FirebaseUtil.getUserDocument(offer.getServiceProviderId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String serviceProviderPhone = document.getString("phone");

                                // Create repair record
                                Repair repair = new Repair(
                                        requestId,
                                        offer.getId(),
                                        FirebaseUtil.getCurrentUserId(),
                                        offer.getServiceProviderId(),
                                        offer.getServiceProviderName(),
                                        serviceProviderPhone,
                                        offer.getPrice()
                                );

                                // Save repair to Firestore
                                DocumentReference repairRef = FirebaseUtil.getRepairsCollection().document();
                                repair.setId(repairRef.getId());

                                repairRef.set(repair)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Reject all other offers
                                                rejectOtherOffers(offer.getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(OffersActivity.this, "Gagal membuat catatan perbaikan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(OffersActivity.this, "Penyedia layanan tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(OffersActivity.this, "Gagal memuat data penyedia layanan: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void rejectOtherOffers(String acceptedOfferId) {
        FirebaseUtil.getOffersCollection()
                .whereEqualTo("requestId", requestId)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String offerId = document.getId();
                                if (!offerId.equals(acceptedOfferId)) {
                                    document.getReference().update("status", "rejected");
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(OffersActivity.this, "Penawaran diterima", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(OffersActivity.this, "Gagal memperbarui penawaran lain: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onRejectOffer(Offer offer) {
        progressBar.setVisibility(View.VISIBLE);

        // Update offer status to rejected
        DocumentReference offerRef = FirebaseUtil.getOfferDocument(offer.getId());
        offerRef.update("status", "rejected")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OffersActivity.this, "Penawaran ditolak", Toast.LENGTH_SHORT).show();

                        // Refresh offers list
                        loadOffers();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OffersActivity.this, "Gagal menolak penawaran: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
