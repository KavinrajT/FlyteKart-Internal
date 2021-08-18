package com.flytekart.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateStoreVariantRequest {

    @SerializedName("storeVariantId")
    @Expose
    private String storeVariantId;

    @SerializedName("storeId")
    @Expose
    private String storeId;

    @SerializedName("variantId")
    @Expose
    private String variantId;

    @SerializedName("isActive")
    @Expose
    private boolean isActive;

    @SerializedName("price")
    @Expose
    private Double price;

    @SerializedName("tax")
    @Expose
    private Double tax;

    @SerializedName("originalPrice")
    @Expose
    private Double originalPrice;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    public String getStoreVariantId() {
        return storeVariantId;
    }

    public void setStoreVariantId(String storeVariantId) {
        this.storeVariantId = storeVariantId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
