package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.Product;
import com.flytekart.models.Variant;
import com.flytekart.utils.Utilities;

import java.util.List;

public class VariantsAdapter extends RecyclerView.Adapter<VariantsAdapter.VariantViewHolder> {

    private List<Variant> variants;

    public VariantsAdapter(List<Variant> variants) {
        this.variants = variants;
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_variant, parent, false);
        return new VariantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
        Variant variant = variants.get(position);
        holder.tvVariantName.setText(variant.getName());
        holder.tvVariantPrice.setText(Utilities.getFormattedMoney(variant.getPrice()));
    }

    @Override
    public int getItemCount() {
        return variants.size();
    }

    public static class VariantViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVariantName;
        private TextView tvVariantPrice;

        public VariantViewHolder(View view) {
            super(view);
            tvVariantName = itemView.findViewById(R.id.tv_variant_name);
            tvVariantPrice = itemView.findViewById(R.id.tv_variant_price);
        }
    }
}
