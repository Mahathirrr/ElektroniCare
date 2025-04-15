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
import com.example.imageview.activities.RequestDetailActivity;
import com.example.imageview.activities.TrackRepairActivity;
import com.example.imageview.adapters.RepairAdapter;
import com.example.imageview.adapters.RequestAdapter;
import com.example.imageview.models.Repair;
import com.example.imageview.models.RepairRequest;
import com.example.imageview.utils.FirebaseUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class HomeFragment extends Fragment implements RequestAdapter.OnRequestClickListener, RepairAdapter.OnRepairClickListener {

    private RecyclerView rvActiveRequests, rvActiveRepairs;
    private TextView tvNoActiveRequests, tvNoActiveRepairs;
    private ProgressBar progressBar;

    private ShimmerFrameLayout shimmerFrameLayout;
    private RequestAdapter requestAdapter;
    private RepairAdapter repairAdapter;
    private List<RepairRequest> requestList;
    private List<Repair> repairList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        rvActiveRequests = view.findViewById(R.id.rvActiveRequests);
        rvActiveRepairs = view.findViewById(R.id.rvActiveRepairs);
        tvNoActiveRequests = view.findViewById(R.id.tvNoActiveRequests);
        tvNoActiveRepairs = view.findViewById(R.id.tvNoActiveRepairs);
        progressBar = view.findViewById(R.id.progressBar);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);

        // Initialize lists and adapters
        requestList = new ArrayList<>();
        repairList = new ArrayList<>();

        requestAdapter = new RequestAdapter(getContext(), requestList, this);
        rvActiveRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rvActiveRequests.setAdapter(requestAdapter);

        repairAdapter = new RepairAdapter(getContext(), repairList, this);
        rvActiveRepairs.setLayoutManager(new LinearLayoutManager(getContext()));
        rvActiveRepairs.setAdapter(repairAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        shimmerFrameLayout.startShimmer();
        progressBar.setVisibility(View.VISIBLE);
        loadActiveRequests();
    }

    private void loadActiveRequests() {
        String userId = FirebaseUtil.getCurrentUserId();

        FirebaseUtil.getRequestsCollection()
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            requestList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RepairRequest request = document.toObject(RepairRequest.class);
                                requestList.add(request);
                            }
                            requestAdapter.notifyDataSetChanged();

                            // Show/hide no active requests message
                            if (requestList.isEmpty()) {
                                tvNoActiveRequests.setVisibility(View.VISIBLE);
                                rvActiveRequests.setVisibility(View.GONE);
                            } else {
                                tvNoActiveRequests.setVisibility(View.GONE);
                                rvActiveRequests.setVisibility(View.VISIBLE);
                            }

                            // Load active repairs
                            loadActiveRepairs();
                        } else {
                            shimmerFrameLayout.stopShimmer();
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Gagal memuat permintaan: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void loadActiveRepairs() {
        String userId = FirebaseUtil.getCurrentUserId();

        FirebaseUtil.getRepairsCollection()
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {shimmerFrameLayout.stopShimmer();
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            repairList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Repair repair = document.toObject(Repair.class);
                                if (!repair.getRepairStatus().equals("completed")) {
                                    repairList.add(repair);
                                }
                            }
                            repairAdapter.notifyDataSetChanged();

                            // Show/hide no active repairs message
                            if (repairList.isEmpty()) {
                                tvNoActiveRepairs.setVisibility(View.VISIBLE);
                                rvActiveRepairs.setVisibility(View.GONE);
                            } else {
                                tvNoActiveRepairs.setVisibility(View.GONE);
                                rvActiveRepairs.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getContext(), "Gagal memuat perbaikan: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestClick(RepairRequest request) {
        Intent intent = new Intent(getContext(), RequestDetailActivity.class);
        intent.putExtra("requestId", request.getId());
        startActivity(intent);
    }

    @Override
    public void onRepairClick(Repair repair) {
        Intent intent = new Intent(getContext(), TrackRepairActivity.class);
        intent.putExtra("repairId", repair.getId());
        startActivity(intent);
    }
}
