package com.example.imageview.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.imageview.R;

import java.util.ArrayList;
import java.util.List;
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private Context context;
    private List<Uri> photoUris;
    private List<String> photoUrlStrings;
    private OnItemClickListener listener;
    private boolean isUrlString;
    private boolean isReadOnly;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    // Constructor for Uri list (for new photos)
    public PhotoAdapter(Context context, List<Uri> photoUris) {
        this.context = context;
        this.photoUris = photoUris;
        this.isUrlString = false;
        this.isReadOnly = false;
    }

    // Constructor for String URLs (for existing photos from Firestore)
    public PhotoAdapter(Context context, List<String> photoUrlStrings, boolean isReadOnly) {
        this.context = context;
        this.photoUrlStrings = photoUrlStrings;
        this.isUrlString = true;
        this.isReadOnly = isReadOnly;
        this.photoUris = new ArrayList<>(); // Initialize empty list to avoid null pointer
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        if (isUrlString) {
            String photoUrl = photoUrlStrings.get(position);
            Glide.with(context)
                    .load(photoUrl)
                    .centerCrop()
                    .into(holder.ivPhoto);
        } else {
            Uri photoUri = photoUris.get(position);
            Glide.with(context)
                    .load(photoUri)
                    .centerCrop()
                    .into(holder.ivPhoto);
        }

        // Show/hide remove button based on read-only status
        holder.ivRemovePhoto.setVisibility(isReadOnly ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return isUrlString ? photoUrlStrings.size() : photoUris.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto, ivRemovePhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            ivRemovePhoto = itemView.findViewById(R.id.ivRemovePhoto);

            ivRemovePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}
