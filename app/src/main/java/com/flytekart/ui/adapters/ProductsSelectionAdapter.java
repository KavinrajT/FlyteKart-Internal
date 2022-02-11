package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.Category;
import com.flytekart.models.Product;

import java.util.List;

public class ProductsSelectionAdapter extends RecyclerView.Adapter<ProductsSelectionAdapter.ProductViewHolder> {

    private List<Product> products;
    private Category category;

    public ProductsSelectionAdapter(List<Product> products, Category category) {
        this.products = products;
        this.category = category;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductCategoryName.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductName;
        private TextView tvProductCategoryName;

        public ProductViewHolder(View view) {
            super(view);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductCategoryName = itemView.findViewById(R.id.tv_product_category_name);
        }
    }
}
