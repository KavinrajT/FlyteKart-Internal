package com.flytekart.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.flytekart.models.Address;
import com.flytekart.models.OrderItem;
import com.flytekart.models.OrderResponse;
import com.flytekart.models.Payment;
import com.flytekart.models.request.CODPaymentRequest;
import com.flytekart.models.request.UpdateOrderStatusRequest;
import com.flytekart.models.response.APIError;
import com.flytekart.models.response.BaseResponse;
import com.flytekart.models.response.BorzoOrderPriceCalculationResponse;
import com.flytekart.network.CustomCallback;
import com.flytekart.utils.Constants;
import com.flytekart.utils.Logger;
import com.flytekart.utils.Utilities;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private OrderResponse orderResponse;
    private String accessToken;
    private String clientId;
    private int position;

    private TextView tvName;
    private TextView tvEmailId;
    private TextView tvPhoneNumber;
    private TextView tvDeliveryAddress;
    private LinearLayout llItems;
    private TextView tvSubTotal;
    private TextView tvTax;
    private TextView tvTotal;
    private TextView tvOrderStatus;
    private TextView tvPaymentType;
    private TextView tvPaid;
    private TextView tvBalance;
    private TextView tvUpdateOrderStatus;
    private TextView tvDeliveryPartner;
    private LinearLayout llCustomerDetails;
    private LinearLayout llDeliveryPartner;
    private LayoutInflater layoutInflater;
    private ProgressDialog progressDialog;
    private String deliveryPartner;

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
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        tvDeliveryAddress = findViewById(R.id.tv_delivery_address);
        llItems = findViewById(R.id.ll_items);
        tvSubTotal = findViewById(R.id.tv_sub_total);
        tvTax = findViewById(R.id.tv_tax);
        tvTotal = findViewById(R.id.tv_total);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        tvPaymentType = findViewById(R.id.tv_payment_type);
        tvPaid = findViewById(R.id.tv_paid);
        tvBalance = findViewById(R.id.tv_balance);
        tvUpdateOrderStatus = findViewById(R.id.tv_update_order_status);
        tvDeliveryPartner = findViewById(R.id.tv_delivery_partner);
        llCustomerDetails = findViewById(R.id.ll_customer_details);
        llDeliveryPartner = findViewById(R.id.ll_delivery_partner);
        layoutInflater = LayoutInflater.from(this);

        orderResponse = getIntent().getParcelableExtra(Constants.ORDER);

        SharedPreferences sharedPreferences = Utilities.getSharedPreferences();
        accessToken = sharedPreferences.getString(Constants.SHARED_PREF_KEY_ACCESS_TOKEN, Constants.EMPTY);
        clientId = sharedPreferences.getString(Constants.SHARED_PREF_KEY_CLIENT_ID, Constants.EMPTY);
        position = getIntent().getIntExtra(Constants.POSITION, 0);

        getData();
    }

    private void getData() {
        showProgress(true);
        Call<BaseResponse<OrderResponse>> getOrderByIdCall = Flytekart.getApiService().getOrderById(accessToken, orderResponse.getOrder().getId(), clientId);
        getOrderByIdCall.enqueue(new CustomCallback<BaseResponse<OrderResponse>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<OrderResponse>> call) {
                Logger.e("Order List API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<OrderResponse>> call, Response<BaseResponse<OrderResponse>> response) {
                Logger.e("Order API success");
                showProgress(false);
                orderResponse = response.body().getBody();
                setOrderDetails();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<OrderResponse>> call, APIError responseBody) {
                Logger.e("Order API call  response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDeliveryAddressToUI() {
        Address deliveryAddress = orderResponse.getOrder().getDeliveryAddress();
        StringBuilder builder = new StringBuilder();
        builder.append(deliveryAddress.getLine1()).append(Constants.COMMA_SPACE);
        builder.append(deliveryAddress.getLine2()).append(Constants.COMMA_SPACE);
        builder.append(deliveryAddress.getCity()).append(Constants.COMMA_SPACE);
        builder.append(deliveryAddress.getState()).append(Constants.COMMA_SPACE);
        builder.append(deliveryAddress.getCountry()).append(Constants.COMMA_SPACE);
        builder.append(deliveryAddress.getZip());
        tvDeliveryAddress.setText(builder.toString());
    }

    private double getPaidAmount() {
        double paid = 0;
        if (orderResponse.getPayments() != null) {
            for (Payment payment : orderResponse.getPayments()) {
                if (payment.getPaymentStatus().getName().equalsIgnoreCase(Constants.PaymentStatus.PAID)) {
                    paid = paid + payment.getAmount();
                }
            }
        }
        return paid;
    }

    private void setPaidAndBalanceToUI() {
        double paid = getPaidAmount();
        double balance = orderResponse.getOrderTotal().getTotal() - paid;
        tvPaid.setText(Utilities.getFormattedMoney(paid));
        tvBalance.setText(Utilities.getFormattedMoney(balance));

        if (orderResponse.getPayments().size() == 0) {
            tvPaymentType.setText(Constants.EMPTY);
        } else if (orderResponse.getPayments().size() == 1) {
            tvPaymentType.setText(orderResponse.getPayments().get(0).getPaymentType().getName());
        } else {
            tvPaymentType.setText(Constants.MULTIPLE);
        }

        if (balance > 0 &&
                orderResponse.getOrder().getOrderStatus().getName().equalsIgnoreCase(Constants.OrderStatus.OUT_FOR_DELIVERY)) {
            tvUpdateOrderStatus.setText(R.string.collection_payment);
        } else {
            tvUpdateOrderStatus.setText(R.string.update_order_status);
        }
    }

    private void setOrderDetails() {
        if (this.orderResponse != null) {
            tvName.setText(orderResponse.getOrder().getEndUser().getName());
            tvEmailId.setText(orderResponse.getOrder().getEndUser().getEmail());
            tvPhoneNumber.setText(orderResponse.getOrder().getEndUser().getPhoneNumber());

            // Run a loop to fill llItems
            setItemsData();

            tvSubTotal.setText(Utilities.getFormattedMoney(orderResponse.getOrderTotal().getTotalPrice()));
            tvTax.setText(Utilities.getFormattedMoney(orderResponse.getOrderTotal().getTotalTax()));
            tvTotal.setText(Utilities.getFormattedMoney(orderResponse.getOrderTotal().getTotal()));

            tvOrderStatus.setText(Utilities.getFormattedOrderStatus(orderResponse.getOrder().getOrderStatus().getName()));
            // We shouldn't allow any edits on delivered(except if returns are there) or in-progress orders.
            if (orderResponse.getOrder().getOrderStatus().getName().equalsIgnoreCase(Constants.OrderStatus.DELIVERED) ||
                    orderResponse.getOrder().getOrderStatus().getName().equalsIgnoreCase(Constants.OrderStatus.IN_PROGRESS) ||
                    orderResponse.getOrder().getOrderStatus().getName().equalsIgnoreCase(Constants.OrderStatus.CANCELED)) {
                tvUpdateOrderStatus.setVisibility(View.GONE);
            } else {
                tvUpdateOrderStatus.setVisibility(View.VISIBLE);
                tvUpdateOrderStatus.setOnClickListener(this);
            }
            if (orderResponse.getOrder().getOrderStatus().getName().equalsIgnoreCase(Constants.OrderStatus.PROCESSED)) {
                llDeliveryPartner.setVisibility(View.VISIBLE);
                llDeliveryPartner.setOnClickListener(this);
                // TODO Need to update based on selected delivery partner
                tvUpdateOrderStatus.setText(R.string.update_order_status);
                tvDeliveryPartner.setText(Constants.DeliveryPartner.NONE);
                deliveryPartner = Constants.DeliveryPartner.NONE;
            } else {
                llDeliveryPartner.setVisibility(View.GONE);
                tvUpdateOrderStatus.setText(R.string.update_order_status);
                tvDeliveryPartner.setText(Constants.DeliveryPartner.NONE);
                deliveryPartner = Constants.DeliveryPartner.NONE;
            }
            llCustomerDetails.setOnClickListener(this);

            setDeliveryAddressToUI();
            setPaidAndBalanceToUI();
        }
    }

    private void setItemsData() {
        List<OrderItem> orderItems = orderResponse.getOrderItems();
        llItems.removeAllViews();
        for (OrderItem orderItem : orderItems) {
            View v = layoutInflater.inflate(R.layout.list_item_order_item, null);

            TextView tvItemName = v.findViewById(R.id.tv_item_name);
            TextView tvVariantName = v.findViewById(R.id.tv_variant_name);
            TextView tvCategoryName = v.findViewById(R.id.tv_category_name);
            TextView tvQuantity = v.findViewById(R.id.tv_quantity);
            TextView tvItemPrice = v.findViewById(R.id.tv_item_price);

            tvItemName.setText(orderItem.getStoreVariant().getVariant().getProduct().getName());
            tvVariantName.setText(orderItem.getStoreVariant().getVariant().getName());
            tvCategoryName.setText(orderItem.getStoreVariant().getVariant().getProduct().getCategory().getName());
            tvQuantity.setText(String.valueOf(orderItem.getQuantity()));
            tvItemPrice.setText(Utilities.getFormattedMoney(orderItem.getTotalPrice()));

            llItems.addView(v);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent data = new Intent();
        data.putExtra(Constants.POSITION, position);
        data.putExtra(Constants.ORDER, orderResponse);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_update_order_status: {
                if (orderResponse.getOrder().getOrderStatus().getName().equalsIgnoreCase(Constants.OrderStatus.PROCESSED)
                        && deliveryPartner.equalsIgnoreCase(Constants.DeliveryPartner.BORZO)) {
                    getBorzoEstimate();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Update order status");
                    double paid = getPaidAmount();
                    double balance = orderResponse.getOrderTotal().getTotal() - paid;
                    String message;
                    if (balance > 0 && orderResponse.getOrder().getOrderStatus().getName()
                            .equalsIgnoreCase(Constants.OrderStatus.OUT_FOR_DELIVERY)) {
                        message = "Do you want collect all payment amount?";
                    } else {
                        message = "Do you want to update the order status from %1$s to %2$s?";
                        message = String.format(message,
                                Utilities.getFormattedOrderStatus(orderResponse.getOrder().getOrderStatus().getName()),
                                Utilities.getNextFormattedOrderStatus(orderResponse.getOrder().getOrderStatus().getName()));
                    }
                    builder.setMessage(message);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //getBorzoEstimate();
                            if (Utilities.getNextOrderStatus(
                                    orderResponse.getOrder().getOrderStatus().getName())
                                    .equalsIgnoreCase(Constants.OrderStatus.ACCEPTED)) {
                                acceptOrder();
                            } else if (Utilities.getNextOrderStatus(
                                    orderResponse.getOrder().getOrderStatus().getName())
                                    .equalsIgnoreCase(Constants.OrderStatus.PROCESSING)) {
                                processOrder();
                            } else if (Utilities.getNextOrderStatus(
                                    orderResponse.getOrder().getOrderStatus().getName())
                                    .equalsIgnoreCase(Constants.OrderStatus.PROCESSED)) {
                                processedOrder();
                            } else if (Utilities.getNextOrderStatus(
                                    orderResponse.getOrder().getOrderStatus().getName())
                                    .equalsIgnoreCase(Constants.OrderStatus.OUT_FOR_DELIVERY)) {
                                outForDeliveryOrder();
                            } else if (Utilities.getNextOrderStatus(
                                    orderResponse.getOrder().getOrderStatus().getName())
                                    .equalsIgnoreCase(Constants.OrderStatus.DELIVERED)) {
                                double paid = getPaidAmount();
                                double balance = orderResponse.getOrderTotal().getTotal() - paid;
                                if (balance <= 0) {
                                    deliverOrder();
                                } else {
                                    collectPayment(balance);
                                }
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing. Just close the dialog
                        }
                    });
                    builder.show();
                }
                break;
            }
            case R.id.ll_customer_details: {
                Intent customerDetailsIntent = new Intent(this, CustomerDetailsActivity.class);
                customerDetailsIntent.putExtra(Constants.END_USER, orderResponse.getOrder().getEndUser());
                startActivity(customerDetailsIntent);
                break;
            }
            case R.id.ll_delivery_partner: {
                showDeliveryPartnerAlertDialog();
                break;
            }
        }
    }

    private void showDeliveryPartnerAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetailsActivity.this);
        alertDialog.setTitle("AlertDialog");
        String[] items = {"None", "Self", "Borzo"};
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        deliveryPartner = Constants.DeliveryPartner.NONE;
                        tvDeliveryPartner.setText(Constants.DeliveryPartner.NONE);
                        tvUpdateOrderStatus.setText(R.string.update_order_status);
                        Toast.makeText(OrderDetailsActivity.this, "No delivery partner selected", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        deliveryPartner = Constants.DeliveryPartner.SELF;
                        tvDeliveryPartner.setText(Constants.DeliveryPartner.SELF);
                        tvUpdateOrderStatus.setText(R.string.update_order_status);
                        Toast.makeText(OrderDetailsActivity.this, "Own fleet delivery selected", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        deliveryPartner = Constants.DeliveryPartner.BORZO;
                        tvDeliveryPartner.setText(Constants.DeliveryPartner.BORZO);
                        tvUpdateOrderStatus.setText(R.string.get_borzo_estimate);
                        Toast.makeText(OrderDetailsActivity.this, "Borzo delivery selected", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void getBorzoEstimate() {
        showProgress(true);
        Call<BaseResponse<BorzoOrderPriceCalculationResponse>> getBorzoEstimate = Flytekart.getApiService().getBorzoEstimate(accessToken, clientId, orderResponse.getOrder().getId());
        getBorzoEstimate.enqueue(new CustomCallback<BaseResponse<BorzoOrderPriceCalculationResponse>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<BorzoOrderPriceCalculationResponse>> call) {
                Logger.e("Borzo estimate API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<BorzoOrderPriceCalculationResponse>> call, Response<BaseResponse<BorzoOrderPriceCalculationResponse>> response) {
                Logger.e("Borzo estimate API success");
                showProgress(false);
                BorzoOrderPriceCalculationResponse calculationResponse = response.body().getBody();
                showBorzoEstimate(calculationResponse);
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<BorzoOrderPriceCalculationResponse>> call, APIError responseBody) {
                Logger.e("Borzo estimate API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBorzoEstimate(BorzoOrderPriceCalculationResponse calculationResponse) {
        // Show estimate in an alertDialog.
        // If yes is selected, send API call for Borzo.
        /*Toast.makeText(getApplicationContext(), "Estimated payment amount: "
                + calculationResponse.getOrder().getPaymentAmount(), Toast.LENGTH_SHORT).show();*/

        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
        builder.setTitle("Do you want to book Borzo delivery?");
        builder.setMessage("Estimated payment amount: "
                + calculationResponse.getOrder().getPaymentAmount());
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Make API call for placing Borzo delivery
                placeBorzoOrder();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.show();
    }

    private void placeBorzoOrder() {
        showProgress(true);
        Call<BaseResponse<BorzoOrderPriceCalculationResponse>> placeBorzoOrderCall = Flytekart.getApiService().placeBorzoOrder(accessToken, clientId, orderResponse.getOrder().getId());
        placeBorzoOrderCall.enqueue(new CustomCallback<BaseResponse<BorzoOrderPriceCalculationResponse>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<BorzoOrderPriceCalculationResponse>> call) {
                Logger.e("Borzo estimate API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<BorzoOrderPriceCalculationResponse>> call, Response<BaseResponse<BorzoOrderPriceCalculationResponse>> response) {
                Logger.e("Borzo estimate API success");
                showProgress(false);
                BorzoOrderPriceCalculationResponse calculationResponse = response.body().getBody();
                showBorzoOrderStatus(calculationResponse);
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<BorzoOrderPriceCalculationResponse>> call, APIError responseBody) {
                Logger.e("Borzo estimate API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBorzoOrderStatus(BorzoOrderPriceCalculationResponse calculationResponse) {
        // TODO Update order status and Borzo orderId to UI.
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
        builder.setMessage("Borzo delivery order placed successfully");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.show();
    }

    private void acceptOrder() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(orderResponse.getOrder().getId());
        showProgress(true);
        Call<BaseResponse<OrderResponse>> acceptOrderCall = Flytekart.getApiService().acceptOrder(accessToken, clientId, request);
        acceptOrderCall.enqueue(new CustomCallback<BaseResponse<OrderResponse>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<OrderResponse>> call) {
                Logger.e("Accept order API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<OrderResponse>> call, Response<BaseResponse<OrderResponse>> response) {
                Logger.e("Accept order API success");
                showProgress(false);
                orderResponse = response.body().getBody();
                setOrderDetails();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<OrderResponse>> call, APIError responseBody) {
                Logger.e("Accept order API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processOrder() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(orderResponse.getOrder().getId());
        showProgress(true);
        Call<BaseResponse<OrderResponse>> processOrderCall = Flytekart.getApiService().processOrder(accessToken, clientId, request);
        processOrderCall.enqueue(new CustomCallback<BaseResponse<OrderResponse>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<OrderResponse>> call) {
                Logger.e("Accept order API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<OrderResponse>> call, Response<BaseResponse<OrderResponse>> response) {
                Logger.e("Accept order API success");
                showProgress(false);
                orderResponse = response.body().getBody();
                setOrderDetails();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<OrderResponse>> call, APIError responseBody) {
                Logger.e("Accept order API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processedOrder() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(orderResponse.getOrder().getId());
        showProgress(true);
        Call<BaseResponse<OrderResponse>> processOrderCall = Flytekart.getApiService().processedOrder(accessToken, clientId, request);
        processOrderCall.enqueue(new CustomCallback<BaseResponse<OrderResponse>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<OrderResponse>> call) {
                Logger.e("Accept order API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<OrderResponse>> call, Response<BaseResponse<OrderResponse>> response) {
                Logger.e("Accept order API success");
                showProgress(false);
                orderResponse = response.body().getBody();
                setOrderDetails();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<OrderResponse>> call, APIError responseBody) {
                Logger.e("Accept order API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void outForDeliveryOrder() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(orderResponse.getOrder().getId());
        showProgress(true);
        Call<BaseResponse<OrderResponse>> processOrderCall = Flytekart.getApiService().outForDeliveryOrder(accessToken, clientId, request);
        processOrderCall.enqueue(new CustomCallback<BaseResponse<OrderResponse>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<OrderResponse>> call) {
                Logger.e("Accept order API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<OrderResponse>> call, Response<BaseResponse<OrderResponse>> response) {
                Logger.e("Accept order API success");
                showProgress(false);
                orderResponse = response.body().getBody();
                setOrderDetails();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<OrderResponse>> call, APIError responseBody) {
                Logger.e("Accept order API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO Collect payment before delivery
    private void deliverOrder() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(orderResponse.getOrder().getId());
        showProgress(true);
        Call<BaseResponse<OrderResponse>> processOrderCall = Flytekart.getApiService().deliverOrder(accessToken, clientId, request);
        processOrderCall.enqueue(new CustomCallback<BaseResponse<OrderResponse>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<OrderResponse>> call) {
                Logger.e("Accept order API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<OrderResponse>> call, Response<BaseResponse<OrderResponse>> response) {
                Logger.e("Accept order API success");
                showProgress(false);
                orderResponse = response.body().getBody();
                setOrderDetails();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<OrderResponse>> call, APIError responseBody) {
                Logger.e("Accept order API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void collectPayment(double balance) {
        CODPaymentRequest request = new CODPaymentRequest();
        request.setOrderId(orderResponse.getOrder().getId());
        request.setPaymentMode("COD");
        request.setAmount(balance);
        showProgress(true);
        Call<BaseResponse<OrderResponse>> collectCODPaymentCall = Flytekart.getApiService().collectCODPayment(accessToken, clientId, request);
        collectCODPaymentCall.enqueue(new CustomCallback<BaseResponse<OrderResponse>>() {
            @Override
            public void onFlytekartGenericErrorResponse(Call<BaseResponse<OrderResponse>> call) {
                Logger.e("Accept order API call failure.");
                showProgress(false);
                Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFlytekartSuccessResponse(Call<BaseResponse<OrderResponse>> call, Response<BaseResponse<OrderResponse>> response) {
                Logger.e("Accept order API success");
                showProgress(false);
                orderResponse = response.body().getBody();
                setOrderDetails();
            }

            @Override
            public void onFlytekartErrorResponse(Call<BaseResponse<OrderResponse>> call, APIError responseBody) {
                Logger.e("Accept order API call response status code : " + responseBody.getStatus());
                showProgress(false);
                Toast.makeText(getApplicationContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showProgress(boolean show) {
        if (show) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this);
            }
            progressDialog.setMessage(getResources().getString(R.string.progress_please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}