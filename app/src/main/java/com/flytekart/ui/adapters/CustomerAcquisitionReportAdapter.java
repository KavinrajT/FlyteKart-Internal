package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.CustomerAcquisitionReportItem;

import java.util.List;

public class CustomerAcquisitionReportAdapter extends RecyclerView.Adapter<CustomerAcquisitionReportAdapter.CustomerAcquisitionViewHolder> {

    private List<CustomerAcquisitionReportItem> customerAcquisitionReportItems;

    public CustomerAcquisitionReportAdapter(List<CustomerAcquisitionReportItem> customerAcquisitionReportItems) {
        this.customerAcquisitionReportItems = customerAcquisitionReportItems;
    }

    @NonNull
    @Override
    public CustomerAcquisitionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_customer_acquisition, parent, false);
        return new CustomerAcquisitionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAcquisitionViewHolder holder, int position) {
        CustomerAcquisitionReportItem customerAcquisitionReportItem = customerAcquisitionReportItems.get(position);
        holder.tvDate.setText(customerAcquisitionReportItem.getCreatedAt());
        holder.tvCount.setText(String.valueOf(customerAcquisitionReportItem.getTotal()));
    }

    @Override
    public int getItemCount() {
        return customerAcquisitionReportItems.size();
    }

    public static class CustomerAcquisitionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate;
        private final TextView tvCount;

        public CustomerAcquisitionViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_date);
            tvCount = view.findViewById(R.id.tv_count);
        }
    }
}
