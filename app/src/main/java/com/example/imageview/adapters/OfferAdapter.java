package com.example.imageview.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageview.R;
import com.example.imageview.models.Offer;
import com.example.imageview.utils.CurrencyUtil;
import com.example.imageview.utils.DateTimeUtil;

import java.util.List;
public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private Context context;
    private List<Offer> offerList;
    private OnOfferActionListener listener;

    public interface OnOfferActionListener {
        void onAcceptOffer(Offer offer);
        void onRejectOffer(Offer offer);
    }

    public OfferAdapter(Context context, List<Offer> offerList, OnOfferActionListener listener) {
        this.context = context;
        this.offerList = offerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offerList.get(position);
        holder.bind(offer);
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServiceProviderName, tvPrice, tvEstimatedTime, tvOfferDescription, tvStatus;
        private Button btnAcceptOffer, btnRejectOffer;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceProviderName = itemView.findViewById(R.id.tvServiceProviderName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvEstimatedTime = itemView.findViewById(R.id.tvEstimatedTime);
            tvOfferDescription = itemView.findViewById(R.id.tvOfferDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAcceptOffer = itemView.findViewById(R.id.btnAcceptOffer);
            btnRejectOffer = itemView.findViewById(R.id.btnRejectOffer);

            btnAcceptOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onAcceptOffer(offerList.get(position));
                    }
                }
            });

            btnRejectOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRejectOffer(offerList.get(position));
                    }
                }
            });
        }

        public void bind(Offer offer) {
            tvServiceProviderName.setText(offer.getServiceProviderName());
            tvPrice.setText(CurrencyUtil.formatRupiah(offer.getPrice()));
            tvEstimatedTime.setText("Estimasi: " + DateTimeUtil.formatEstimatedDays(offer.getEstimatedDays()));
            tvOfferDescription.setText(offer.getDescription());

            // Set status text and color
            String status = offer.getStatus();
            if (status.equals("pending")) {
                tvStatus.setText("Menunggu");
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.pending));
                btnAcceptOffer.setVisibility(View.VISIBLE);
                btnRejectOffer.setVisibility(View.VISIBLE);
            } else if (status.equals("accepted")) {
                tvStatus.setText("Diterima");
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.completed));
                btnAcceptOffer.setVisibility(View.GONE);
                btnRejectOffer.setVisibility(View.GONE);
            } else if (status.equals("rejected")) {
                tvStatus.setText("Ditolak");
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.cancelled));
                btnAcceptOffer.setVisibility(View.GONE);
                btnRejectOffer.setVisibility(View.GONE);
            }
        }
    }
}
