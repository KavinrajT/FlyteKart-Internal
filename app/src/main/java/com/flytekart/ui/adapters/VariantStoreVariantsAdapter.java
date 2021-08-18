package com.flytekart.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.VariantStoreVariantDTO;

import java.util.List;

public class VariantStoreVariantsAdapter extends RecyclerView.Adapter<VariantStoreVariantsAdapter.VariantViewHolder> {

    private Context context;
    private List<VariantStoreVariantDTO> variants;

    public VariantStoreVariantsAdapter(List<VariantStoreVariantDTO> variants) {
        this.variants = variants;
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_store_variant, parent, false);
        return new VariantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
        VariantStoreVariantDTO variant = variants.get(position);
        holder.tvVariantName.setText(variant.getName());
        boolean isActive = false;
        if (variant.getStoreVariantId() != null && variant.getStoreVariantDeletedAt() == null) {
            isActive = true;
        }

        holder.ivVariantEnabled.setImageResource(R.drawable.ic_check_filled);
        if (isActive) {
            holder.ivVariantEnabled.setColorFilter(ContextCompat.getColor(context, R.color.phone_green));
            holder.tvVariantStatus.setText(R.string.available);
        } else {
            holder.ivVariantEnabled.setColorFilter(ContextCompat.getColor(context, R.color.dark_grey));
            holder.tvVariantStatus.setText(R.string.not_available);
        }
    }

    @Override
    public int getItemCount() {
        return variants.size();
    }

    public static class VariantViewHolder extends RecyclerView.ViewHolder {
        private final View llVariantName;
        private final TextView tvVariantName;
        private final TextView tvVariantStatus;
        private final ImageView ivVariantEnabled;
        private final View ivVariantNext;

        public VariantViewHolder(View view) {
            super(view);
            llVariantName = view.findViewById(R.id.ll_variant_name);
            tvVariantName = view.findViewById(R.id.tv_variant_name);
            tvVariantStatus = view.findViewById(R.id.tv_variant_status);
            ivVariantEnabled = view.findViewById(R.id.iv_variant_enabled);
            ivVariantNext = view.findViewById(R.id.iv_variant_next);
        }
    }
}
