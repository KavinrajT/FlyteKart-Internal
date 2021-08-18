package com.flytekart.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateStoreProductRequest {

    @SerializedName("storeProductId")
    @Expose
    private String storeProductId;

    @SerializedName("storeId")
    @Expose
    private String storeId;

    @SerializedName("productId")
    @Expose
    private String productId;

    @SerializedName("isActive")
    @Expose
    private boolean isActive;

    public String getStoreProductId() {
        return storeProductId;
    }

    public void setStoreProductId(String storeProductId) {
        this.storeProductId = storeProductId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
