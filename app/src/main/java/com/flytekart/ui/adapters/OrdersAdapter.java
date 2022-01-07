package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.OrderResponse;
import com.flytekart.utils.Utilities;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<OrderResponse> orderResponses;

    public OrdersAdapter(List<OrderResponse> orderResponses) {
        this.orderResponses = orderResponses;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderResponse orderResponse = orderResponses.get(position);
        if (orderResponse.getOrder().getEndUser().getName() != null) {
            holder.tvUserName.setText(orderResponse.getOrder().getEndUser().getName());
        } else {
            holder.tvUserName.setText(orderResponse.getOrder().getEndUser().getPhoneNumber());
        }
        holder.tvOrderTime.setText(Utilities.getFormattedCalendarString(orderResponse.getOrder().getOrderPlacedAt()));
        holder.tvOrderSource.setText(orderResponse.getOrder().getOrderSource().getName());
        holder.tvOrderStatus.setText(Utilities.getFormattedOrderStatus(orderResponse.getOrder().getOrderStatus().getName()));
        holder.tvOrderTotal.setText(Utilities.getFormattedMoney(orderResponse.getOrderTotal().getTotal()));
    }

    @Override
    public int getItemCount() {
        return orderResponses.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName;
        private TextView tvOrderTime;
        private TextView tvOrderSource;
        private TextView tvOrderStatus;
        private TextView tvOrderTotal;

        public OrderViewHolder(View view) {
            super(view);
            tvUserName = view.findViewById(R.id.tv_user_name);
            tvOrderTime = view.findViewById(R.id.tv_order_time);
            tvOrderSource = view.findViewById(R.id.tv_order_source);
            tvOrderStatus = view.findViewById(R.id.tv_order_status);
            tvOrderTotal = view.findViewById(R.id.tv_order_total);
        }
    }
}
