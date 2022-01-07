package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.ProductOrderReportItem;
import com.flytekart.utils.Utilities;

import java.util.List;

public class ProductOrderReportAdapter extends RecyclerView.Adapter<ProductOrderReportAdapter.ProductOrderViewHolder> {

    private List<ProductOrderReportItem> productOrderReportItems;

    public ProductOrderReportAdapter(List<ProductOrderReportItem> productOrderReportItems) {
        this.productOrderReportItems = productOrderReportItems;
    }

    @NonNull
    @Override
    public ProductOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_product_order, parent, false);
        return new ProductOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductOrderViewHolder holder, int position) {
        ProductOrderReportItem productOrderReportItem = productOrderReportItems.get(position);
        holder.tvProductName.setText(productOrderReportItem.getProductName());
        holder.tvVariantName.setText(productOrderReportItem.getVariantName());
        holder.tvCategoryName.setText(productOrderReportItem.getCategoryName());
        holder.tvOrderedQuantity.setText(String.valueOf(productOrderReportItem.getOrderedQuantity()));
        holder.tvOrderedPrice.setText(Utilities.getFormattedMoney(productOrderReportItem.getOrderedPrice()));
        holder.tvDeliveredQuantity.setText(String.valueOf(productOrderReportItem.getDeliveredQuantity()));
        holder.tvDeliveredPrice.setText(Utilities.getFormattedMoney(productOrderReportItem.getDeliveredPrice()));
    }

    @Override
    public int getItemCount() {
        return productOrderReportItems.size();
    }

    public static class ProductOrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvProductName;
        private final TextView tvVariantName;
        private final TextView tvCategoryName;
        private final TextView tvOrderedQuantity;
        private final TextView tvOrderedPrice;
        private final TextView tvDeliveredQuantity;
        private final TextView tvDeliveredPrice;

        public ProductOrderViewHolder(View view) {
            super(view);
            tvProductName = view.findViewById(R.id.tv_product_name);
            tvVariantName = view.findViewById(R.id.tv_variant_name);
            tvCategoryName = view.findViewById(R.id.tv_category_name);
            tvOrderedQuantity = view.findViewById(R.id.tv_ordered_quantity);
            tvOrderedPrice = view.findViewById(R.id.tv_ordered_price);
            tvDeliveredQuantity = view.findViewById(R.id.tv_delivered_quantity);
            tvDeliveredPrice = view.findViewById(R.id.tv_delivered_price);
        }
    }
}
