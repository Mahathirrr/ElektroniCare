package com.example.imageview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageview.R;
import com.example.imageview.adapters.RequestAdapter;
import com.example.imageview.models.RepairRequest;
import com.example.imageview.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity implements RequestAdapter.OnRequestClickListener {

    private Toolbar toolbar;
    private RecyclerView rvRequests;
    private TextView tvNoRequests;
    private ProgressBar progressBar;

    private RequestAdapter requestAdapter;
    private List<RepairRequest> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        rvRequests = findViewById(R.id.rvRequests);
        tvNoRequests = findViewById(R.id.tvNoRequests);
        progressBar = findViewById(R.id.progressBar);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_requests);

        // Initialize list and adapter
        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(this, requestList, this);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(requestAdapter);
    }

    @Override
    protected void onResume() {
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
                            
                            if (requestList.isEmpty()) {
                                showNoRequests();
                            } else {
                                showRequests();
                            }
                        } else {
                            Toast.makeText(RequestsActivity.this, "Gagal memuat permintaan: " + 
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"), 
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showNoRequests() {
        tvNoRequests.setVisibility(View.VISIBLE);
        rvRequests.setVisibility(View.GONE);
    }

    private void showRequests() {
        tvNoRequests.setVisibility(View.GONE);
        rvRequests.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestClick(RepairRequest request) {
        Intent intent = new Intent(this, RequestDetailActivity.class);
        intent.putExtra("requestId", request.getId());
        startActivity(intent);
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