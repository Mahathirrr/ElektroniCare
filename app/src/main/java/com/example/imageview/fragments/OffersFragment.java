package com.example.imageview.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageview.R;
import com.example.imageview.activities.OffersActivity;
import com.example.imageview.adapters.OfferAdapter;
import com.example.imageview.models.Offer;
import com.example.imageview.models.RepairRequest;
import com.example.imageview.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class OffersFragment extends Fragment implements OfferAdapter.OnOfferActionListener {

    private RecyclerView rvOffers;
    private TextView tvNoOffers;
    private ProgressBar progressBar;

    private OfferAdapter offerAdapter;
    private List<Offer> offerList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offers, container, false);

        // Initialize views
        rvOffers = view.findViewById(R.id.rvOffers);
        tvNoOffers = view.findViewById(R.id.tvNoOffers);
        progressBar = view.findViewById(R.id.progressBar);

        // Initialize list and adapter
        offerList = new ArrayList<>();
        offerAdapter = new OfferAdapter(getContext(), offerList, this);
        rvOffers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOffers.setAdapter(offerAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOffers();
    }

    private void loadOffers() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = FirebaseUtil.getCurrentUserId();

        // First get all user's requests
        FirebaseUtil.getRequestsCollection()
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> requestIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RepairRequest request = document.toObject(RepairRequest.class);
                                requestIds.add(request.getId());
                            }

                            if (requestIds.isEmpty()) {
                                progressBar.setVisibility(View.GONE);
                                showNoOffers();
                                return;
                            }

                            // Now get all offers for these requests
                            loadOffersForRequests(requestIds);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Gagal memuat permintaan: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void loadOffersForRequests(List<String> requestIds) {
        FirebaseUtil.getOffersCollection()
                .whereIn("requestId", requestIds)
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

                            if (offerList.isEmpty()) {
                                showNoOffers();
                            } else {
                                showOffers();
                            }
                        } else {
                            Toast.makeText(getContext(), "Gagal memuat penawaran: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showNoOffers() {
        tvNoOffers.setVisibility(View.VISIBLE);
        rvOffers.setVisibility(View.GONE);
    }

    private void showOffers() {
        tvNoOffers.setVisibility(View.GONE);
        rvOffers.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAcceptOffer(Offer offer) {
        // Update offer status in Firestore
        FirebaseUtil.getOffersCollection().document(offer.getId())
                .update("status", "accepted")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Penawaran diterima", Toast.LENGTH_SHORT).show();
                        loadOffers(); // Reload offers
                    } else {
                        Toast.makeText(getContext(), "Gagal menerima penawaran: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onRejectOffer(Offer offer) {
        // Update offer status in Firestore
        FirebaseUtil.getOffersCollection().document(offer.getId())
                .update("status", "rejected")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Penawaran ditolak", Toast.LENGTH_SHORT).show();
                        loadOffers(); // Reload offers
                    } else {
                        Toast.makeText(getContext(), "Gagal menolak penawaran: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
