package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.Product;

import java.util.List;

public class ProductRecyclerListAdapter extends RecyclerView.Adapter<ProductRecyclerListAdapter.ProductViewHolder> {

    private List<Product> products;

    public ProductRecyclerListAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_store, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        if (product != null) {
            holder.getTextView().setText(product.getName());
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ProductViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tv_store_name);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
