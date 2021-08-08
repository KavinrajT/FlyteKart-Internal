package com.flytekart.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.flytekart.Flytekart;
import com.flytekart.R;
import com.flytekart.models.OrderItem;
import com.flytekart.models.OrderResponse;
import com.flytekart.models.response.BaseErrorResponse;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.ui.views.TitleBarLayout;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity implements TitleBarLayout.TitleBarIconClickListener, View.OnClickListener {

    private OrderResponse orderResponse;
    private String accessToken;
    private String clientId;

    private TextView tvName;
    private TextView tvEmailId;
    private LinearLayout llItems;
    private TextView tvSubTotal;
    private TextView tvTax;
    private TextView tvTotal;
    private TextView tvOrderStatus;
    private TextView tvPaymentType;
    private TextView tvPaymentStatus;
    private TextView tvUpdateOrderStatus;
    private LinearLayout llCustomerDetails;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tvName = findViewById(R.id.tv_name);
        tvEmailId = findViewById(R.id.tv_email_id);
        llItems = findViewById(R.id.ll_items);
        tvSubTotal = findViewById(R.id.tv_sub_total);
        tvTax = findViewById(R.id.tv_tax);
        tvTotal = findViewById(R.id.tv_total);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        tvPaymentType = findViewById(R.id.tv_payment_type);
        tvPaymentStatus = findViewById(R.id.tv_payment_status);
        tvUpdateOrderStatus = findViewById(R.id.tv_update_order_status);
        llCustomerDetails = findViewById(R.id.ll_customer_details);
        layoutInflater = LayoutInflater.from(this);

        orderResponse = getIntent().getParcelableExtra(Constants.ORDER);

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);

        getData();
    }


    private void getData() {
        Call<BaseResponse<OrderResponse>> getOrderByIdCall = Flytekart.getApiService().getOrderById(accessToken, orderResponse.getOrder().getId(), clientId);
        getOrderByIdCall.enqueue(new CustomCallback<BaseResponse<OrderResponse>>() {
            @Override
            public void onFailure(Call<BaseResponse<OrderResponse>> call, Throwable t) {
                Logger.e("Order List API call failure.");
                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<OrderResponse>> call, Response<BaseResponse<OrderResponse>> response) {
                Logger.e("Order API success");
                orderResponse = response.body().getBody();
                setOrderDetails();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<OrderResponse>> call, BaseErrorResponse responseBody) {
                Logger.e("Order API call  response status code : " + responseBody.getStatusCode());
                Toast.makeText(getApplicationContext(), responseBody.getApiError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOrderDetails() {
        if (this.orderResponse != null) {
            tvName.setText(orderResponse.getOrder().getEndUser().getName());
            tvEmailId.setText(orderResponse.getOrder().getEndUser().getEmail());

            // TODO Run a loop to fill llItems
            setItemsData();

            tvSubTotal.setText(Utilities.getFormattedMoney(orderResponse.getOrderTotal().getTotalPrice()));
            tvTax.setText(Utilities.getFormattedMoney(orderResponse.getOrderTotal().getTotalTax()));
            tvTotal.setText(Utilities.getFormattedMoney(orderResponse.getOrderTotal().getTotal()));

            tvOrderStatus.setText(Utilities.getFormattedOrderStatus(orderResponse.getOrder().getOrderStatus().getName()));
            // We shouldn't allow any edits on delivered(except if returns are there) or in-progress orders.
            if (orderResponse.getOrder().getOrderStatus().getName().equalsIgnoreCase("DELIVERED") ||
                    orderResponse.getOrder().getOrderStatus().getName().equalsIgnoreCase("IN_PROGRESS") ||
                    orderResponse.getOrder().getOrderStatus().getName().equalsIgnoreCase("CANCELED")) {
                tvUpdateOrderStatus.setVisibility(View.GONE);
            } else {
                tvUpdateOrderStatus.setVisibility(View.VISIBLE);
                tvUpdateOrderStatus.setOnClickListener(this);
            }
            llCustomerDetails.setOnClickListener(this);
        }
    }

    private void setItemsData() {
        List<OrderItem> orderItems = orderResponse.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            View v = layoutInflater.inflate(R.layout.list_item_order_item, null);

            TextView tvItemName = v.findViewById(R.id.tv_item_name);
            TextView tvVariantName = v.findViewById(R.id.tv_variant_name);
            TextView tvQuantity = v.findViewById(R.id.tv_quantity);
            TextView tvItemPrice = v.findViewById(R.id.tv_item_price);

            tvItemName.setText(orderItem.getStoreVariant().getVariant().getProduct().getName());
            tvVariantName.setText(orderItem.getStoreVariant().getVariant().getName());
            tvQuantity.setText(String.valueOf(orderItem.getQuantity()));
            tvItemPrice.setText(Utilities.getFormattedMoney(orderItem.getTotalPrice()));

            llItems.addView(v);
        }
    }

    @Override
    public void onTitleBarRightIconClicked(View view) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_update_order_status: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Update order status");
                String message = "Do you want to update the order status from %1$s to %2$s?";
                message = String.format(message,
                        Utilities.getFormattedOrderStatus(orderResponse.getOrder().getOrderStatus().getName()),
                        Utilities.getNextFormattedOrderStatus(orderResponse.getOrder().getOrderStatus().getName()));
                builder.setMessage(message);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(OrderDetailsActivity.this, "To be implemented", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing. Just close the dialog
                    }
                });
                builder.show();
                break;
            }
            case R.id.ll_customer_details: {
                Intent customerDetailsIntent = new Intent(this, CustomerDetailsActivity.class);
                customerDetailsIntent.putExtra(Constants.END_USER, orderResponse.getOrder().getEndUser());
                startActivity(customerDetailsIntent);
                break;
            }
        }
    }
}