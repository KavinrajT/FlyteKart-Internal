package com.flytekart.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CODPaymentRequest {

    @SerializedName("orderId")
    @Expose
    private String orderId;

    @SerializedName("amount")
    @Expose
    private double amount;

    @SerializedName("paymentMode")
    @Expose
    private String paymentMode;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
