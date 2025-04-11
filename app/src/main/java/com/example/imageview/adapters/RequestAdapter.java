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
import com.example.imageview.models.RepairRequest;
import com.example.imageview.utils.DateTimeUtil;

import java.util.List;
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<RepairRequest> requestList;
    private OnRequestClickListener listener;

    public interface OnRequestClickListener {
        void onRequestClick(RepairRequest request);
    }

    public RequestAdapter(Context context, List<RepairRequest> requestList, OnRequestClickListener listener) {
        this.context = context;
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RepairRequest request = requestList.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDeviceInfo, tvProblemDescription, tvStatus, tvDateCreated, tvOffersCount;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeviceInfo = itemView.findViewById(R.id.tvDeviceInfo);
            tvProblemDescription = itemView.findViewById(R.id.tvProblemDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            tvOffersCount = itemView.findViewById(R.id.tvOffersCount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRequestClick(requestList.get(position));
                    }
                }
            });
        }

        public void bind(RepairRequest request) {
            tvDeviceInfo.setText(request.getDeviceBrand() + " " + request.getDeviceModel());
            tvProblemDescription.setText(request.getProblemDescription());
            tvDateCreated.setText(DateTimeUtil.formatDate(request.getCreatedAt()));

            // Set status text and color
            String status = request.getStatus();
            if (status.equals("pending")) {
                tvStatus.setText("Menunggu");
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.pending));
            } else if (status.equals("in_progress")) {
                tvStatus.setText("Dalam Proses");
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.in_progress));
            } else if (status.equals("completed")) {
                tvStatus.setText("Selesai");
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.completed));
            } else if (status.equals("cancelled")) {
                tvStatus.setText("Dibatalkan");
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.cancelled));
            }

            // Show offers count if any
            int offersCount = request.getOffersCount();
            if (offersCount > 0) {
                tvOffersCount.setVisibility(View.VISIBLE);
                tvOffersCount.setText(offersCount + " Penawaran");
            } else {
                tvOffersCount.setVisibility(View.GONE);
            }
        }
    }
}
