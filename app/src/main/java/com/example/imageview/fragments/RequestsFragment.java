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
import com.example.imageview.adapters.RequestAdapter;
import com.example.imageview.models.RepairRequest;
import com.example.imageview.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class RequestsFragment extends Fragment implements RequestAdapter.OnRequestClickListener {

    private RecyclerView rvRequests;
    private TextView tvNoRequests;
    private ProgressBar progressBar;

    private RequestAdapter requestAdapter;
    private List<RepairRequest> requestList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        // Initialize views
        rvRequests = view.findViewById(R.id.rvRequests);
        tvNoRequests = view.findViewById(R.id.tvNoRequests);
        progressBar = view.findViewById(R.id.progressBar);

        // Initialize list and adapter
        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(getContext(), requestList, this);
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRequests.setAdapter(requestAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRequests();
    }

    private void loadRequests() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = FirebaseUtil.getCurrentUserId();

        FirebaseUtil.getRequestsCollection()
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            requestList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RepairRequest request = document.toObject(RepairRequest.class);
                                requestList.add(request);
                            }
                            requestAdapter.notifyDataSetChanged();

                            // Show/hide no requests message
                            if (requestList.isEmpty()) {
                                tvNoRequests.setVisibility(View.VISIBLE);
                                rvRequests.setVisibility(View.GONE);
                            } else {
                                tvNoRequests.setVisibility(View.GONE);
                                rvRequests.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getContext(), "Gagal memuat permintaan: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
}
