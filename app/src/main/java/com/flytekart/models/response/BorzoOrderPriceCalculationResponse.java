package com.flytekart.models.response;

import com.flytekart.models.BorzoOrder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class BorzoOrderPriceCalculationResponse {

    @SerializedName("is_successful")
    @Expose
    private boolean isSuccessful;

    @SerializedName("order")
    @Expose
    private BorzoOrder order;

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public BorzoOrder getOrder() {
        return order;
    }

    public void setOrder(BorzoOrder order) {
        this.order = order;
    }
}
