package com.example.imageview.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageview.R;
import com.example.imageview.models.Repair;
import com.example.imageview.utils.CurrencyUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
public class RepairAdapter extends RecyclerView.Adapter<RepairAdapter.RepairViewHolder> {

    private Context context;
    private List<Repair> repairList;
    private OnRepairClickListener listener;

    public interface OnRepairClickListener {
        void onRepairClick(Repair repair);
    }

    public RepairAdapter(Context context, List<Repair> repairList, OnRepairClickListener listener) {
        this.context = context;
        this.repairList = repairList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RepairViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_repair, parent, false);
        return new RepairViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RepairViewHolder holder, int position) {
        Repair repair = repairList.get(position);
        holder.bind(repair);
    }

    @Override
    public int getItemCount() {
        return repairList.size();
    }

    public class RepairViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDeviceInfo, tvServiceProviderName, tvRepairStatus, tvPrice;

        public RepairViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeviceInfo = itemView.findViewById(R.id.tvDeviceInfo);
            tvServiceProviderName = itemView.findViewById(R.id.tvServiceProviderName);
            tvRepairStatus = itemView.findViewById(R.id.tvRepairStatus);
            tvPrice = itemView.findViewById(R.id.tvPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRepairClick(repairList.get(position));
                    }
                }
            });
        }

        public void bind(Repair repair) {
            // Load request data to get device info
            FirebaseFirestore.getInstance().collection("repair_requests")
                    .document(repair.getRequestId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String deviceBrand = documentSnapshot.getString("deviceBrand");
                                String deviceModel = documentSnapshot.getString("deviceModel");
                                tvDeviceInfo.setText(deviceBrand + " " + deviceModel);
                            }
                        }
                    });

            tvServiceProviderName.setText(repair.getServiceProviderName());
            tvPrice.setText(CurrencyUtil.formatRupiah(repair.getPrice()));

            // Set repair status text and color
            String status = repair.getRepairStatus();
            if (status.equals("received")) {
                tvRepairStatus.setText("Diterima");
                tvRepairStatus.setTextColor(ContextCompat.getColor(context, R.color.pending));
            } else if (status.equals("diagnosed")) {
                tvRepairStatus.setText("Didiagnosa");
                tvRepairStatus.setTextColor(ContextCompat.getColor(context, R.color.in_progress));
            } else if (status.equals("repairing")) {
                tvRepairStatus.setText("Sedang Diperbaiki");
                tvRepairStatus.setTextColor(ContextCompat.getColor(context, R.color.in_progress));
            } else if (status.equals("completed")) {
                tvRepairStatus.setText("Selesai");
                tvRepairStatus.setTextColor(ContextCompat.getColor(context, R.color.completed));
            }
        }
    }
}
