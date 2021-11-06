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
import com.flytekart.models.ProductStoreProductDTO;

import java.util.List;

public class ProductStoreProductsAdapter extends RecyclerView.Adapter<ProductStoreProductsAdapter.ProductViewHolder> {

    private Context context;
    private ProductClickListener productClickListener;
    private List<ProductStoreProductDTO> products;

    public ProductStoreProductsAdapter(ProductClickListener productClickListener, List<ProductStoreProductDTO> products) {
        this.productClickListener = productClickListener;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_store_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductStoreProductDTO product = products.get(position);
        holder.tvProductName.setText(product.getName());
        boolean isActive = false;
        if (product.getStoreProductId() != null && product.getStoreProductDeletedAt() == null) {
            isActive = true;
        }

        holder.ivProductEnabled.setImageResource(R.drawable.ic_check_filled);
        if (isActive) {
            holder.ivProductEnabled.setColorFilter(ContextCompat.getColor(context, R.color.phone_green));
            holder.tvProductStatus.setText(R.string.available);
            CategoryNameClickListener categoryNameClickListener = new CategoryNameClickListener(position);
            holder.llProductName.setOnClickListener(categoryNameClickListener);
            holder.ivProductNext.setOnClickListener(categoryNameClickListener);
            holder.ivProductNext.setVisibility(View.VISIBLE);
        } else {
            holder.ivProductEnabled.setColorFilter(ContextCompat.getColor(context, R.color.dark_grey));
            holder.tvProductStatus.setText(R.string.not_available);
            holder.llProductName.setOnClickListener(null);
            holder.ivProductNext.setOnClickListener(null);
            holder.ivProductNext.setVisibility(View.INVISIBLE);
        }


        holder.ivProductEnabled.setOnClickListener(new EditClickListener(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private class CategoryNameClickListener implements View.OnClickListener {
        private int position;

        public CategoryNameClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            productClickListener.onProductClicked(position);
        }
    }

    private class EditClickListener implements View.OnClickListener {
        private int position;

        public EditClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            productClickListener.onEdit(position);
        }
    }


    public interface ProductClickListener {
        void onProductClicked(int position);
        void onEdit(int position);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final View llProductName;
        private final TextView tvProductName;
        private final TextView tvProductStatus;
        private final ImageView ivProductEnabled;
        private final View ivProductNext;

        public ProductViewHolder(View view) {
            super(view);
            llProductName = view.findViewById(R.id.ll_product_name);
            tvProductName = view.findViewById(R.id.tv_product_name);
            tvProductStatus = view.findViewById(R.id.tv_product_status);
            ivProductEnabled = view.findViewById(R.id.iv_product_enabled);
            ivProductNext = view.findViewById(R.id.iv_product_next);
        }
    }
}
