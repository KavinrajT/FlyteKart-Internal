package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.OrdersOverTimeReportItem;
import com.flytekart.models.ProductOrderReportItem;
import com.flytekart.utils.Utilities;

import java.util.List;

public class OrdersOverTimeReportAdapter extends RecyclerView.Adapter<OrdersOverTimeReportAdapter.OrdersOverTimeViewHolder> {

    private List<OrdersOverTimeReportItem> ordersOverTimeReportItems;

    public OrdersOverTimeReportAdapter(List<OrdersOverTimeReportItem> ordersOverTimeReportItems) {
        this.ordersOverTimeReportItems = ordersOverTimeReportItems;
    }

    @NonNull
    @Override
    public OrdersOverTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_orders_over_time, parent, false);
        return new OrdersOverTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersOverTimeViewHolder holder, int position) {
        OrdersOverTimeReportItem ordersOverTimeReportItem = ordersOverTimeReportItems.get(position);
        holder.tvDate.setText(Utilities.getFormattedCalendarString(ordersOverTimeReportItem.getCreatedAt(), "dd-MM-yyyy"));
        holder.tvOrderedQuantity.setText(String.valueOf(ordersOverTimeReportItem.getPlacedOrders()));
        holder.tvOrderedUnits.setText(String.valueOf(ordersOverTimeReportItem.getTotalOrderedUnits()));
        holder.tvOrderedPrice.setText(Utilities.getFormattedMoney(ordersOverTimeReportItem.getTotalOrderedValue()));
        holder.tvDeliveredQuantity.setText(String.valueOf(ordersOverTimeReportItem.getDeliveredOrders()));
        holder.tvDeliveredUnits.setText(String.valueOf(ordersOverTimeReportItem.getTotalDeliveredUnits()));
        holder.tvDeliveredPrice.setText(Utilities.getFormattedMoney(ordersOverTimeReportItem.getTotalDeliveredValue()));
    }

    @Override
    public int getItemCount() {
        return ordersOverTimeReportItems.size();
    }

    public static class OrdersOverTimeViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvOrderedQuantity;
        private TextView tvOrderedUnits;
        private TextView tvOrderedPrice;
        private TextView tvDeliveredQuantity;
        private TextView tvDeliveredUnits;
        private TextView tvDeliveredPrice;

        public OrdersOverTimeViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_date);
            tvOrderedQuantity = view.findViewById(R.id.tv_ordered_quantity);
            tvOrderedUnits = view.findViewById(R.id.tv_ordered_units);
            tvOrderedPrice = view.findViewById(R.id.tv_ordered_price);
            tvDeliveredQuantity = view.findViewById(R.id.tv_delivered_quantity);
            tvDeliveredUnits = view.findViewById(R.id.tv_delivered_units);
            tvDeliveredPrice = view.findViewById(R.id.tv_delivered_price);
        }
    }
}
