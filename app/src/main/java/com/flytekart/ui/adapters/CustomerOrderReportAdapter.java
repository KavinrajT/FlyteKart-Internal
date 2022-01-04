package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.CustomerOrderReportItem;
import com.flytekart.models.ProductOrderReportItem;
import com.flytekart.utils.Utilities;

import java.util.List;

public class CustomerOrderReportAdapter extends RecyclerView.Adapter<CustomerOrderReportAdapter.CustomerOrderViewHolder> {

    private List<CustomerOrderReportItem> customerOrderReportItems;

    public CustomerOrderReportAdapter(List<CustomerOrderReportItem> customerOrderReportItems) {
        this.customerOrderReportItems = customerOrderReportItems;
    }

    @NonNull
    @Override
    public CustomerOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_customer_order, parent, false);
        return new CustomerOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerOrderViewHolder holder, int position) {
        CustomerOrderReportItem customerOrderReportItem = customerOrderReportItems.get(position);
        holder.tvCustomerName.setText(customerOrderReportItem.getName());
        holder.tvOrderCount.setText(String.valueOf(customerOrderReportItem.getTotalOrderCount()));
        holder.tvOrderedValue.setText(Utilities.getFormattedMoney(customerOrderReportItem.getTotalOrderValue()));
        holder.tvLastOrderedAt.setText(customerOrderReportItem.getLastOrderedAt());
    }

    @Override
    public int getItemCount() {
        return customerOrderReportItems.size();
    }

    public static class CustomerOrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustomerName;
        private TextView tvOrderCount;
        private TextView tvOrderedValue;
        private TextView tvLastOrderedAt;

        public CustomerOrderViewHolder(View view) {
            super(view);
            tvCustomerName = view.findViewById(R.id.tv_customer_name);
            tvOrderCount = view.findViewById(R.id.tv_order_count);
            tvOrderedValue = view.findViewById(R.id.tv_ordered_value);
            tvLastOrderedAt = view.findViewById(R.id.tv_last_ordered_at);
        }
    }
}
