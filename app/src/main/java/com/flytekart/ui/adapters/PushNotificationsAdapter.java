package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.OrderResponse;
import com.flytekart.models.PushNotification;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Utilities;

import java.util.List;

public class PushNotificationsAdapter extends RecyclerView.Adapter<PushNotificationsAdapter.PushNotificationViewHolder> {

    private List<PushNotification> pushNotifications;

    public PushNotificationsAdapter(List<PushNotification> pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    @NonNull
    @Override
    public PushNotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_push_notification, parent, false);
        return new PushNotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PushNotificationViewHolder holder, int position) {
        PushNotification pushNotification = pushNotifications.get(position);
        holder.tvPushNotificationTitle.setText(pushNotification.getTitle());
        holder.tvPushNotificationBody.setText(pushNotification.getBody());
        holder.tvPushNotificationSentBy.setText(Constants.EMPTY);
        holder.tvPushNotificationSentAt.setText(Constants.EMPTY);
    }

    @Override
    public int getItemCount() {
        return pushNotifications.size();
    }

    public static class PushNotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPushNotificationTitle;
        private TextView tvPushNotificationBody;
        private TextView tvPushNotificationSentBy;
        private TextView tvPushNotificationSentAt;

        public PushNotificationViewHolder(View view) {
            super(view);
            tvPushNotificationTitle = view.findViewById(R.id.tv_push_notification_title);
            tvPushNotificationBody = view.findViewById(R.id.tv_push_notification_body);
            tvPushNotificationSentBy = view.findViewById(R.id.tv_push_notification_sent_by);
            tvPushNotificationSentAt = view.findViewById(R.id.tv_push_notification_sent_at);
        }
    }
}
