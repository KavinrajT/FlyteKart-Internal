package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductOrderReportItem {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("variantName")
    @Expose
    private String variantName;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("orderedQuantity")
    @Expose
    private int orderedQuantity;
    @SerializedName("orderedPrice")
    @Expose
    private double orderedPrice;
    @SerializedName("deliveredQuantity")
    @Expose
    private int deliveredQuantity;
    @SerializedName("deliveredPrice")
    @Expose
    private double deliveredPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(int orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public double getOrderedPrice() {
        return orderedPrice;
    }

    public void setOrderedPrice(double orderedPrice) {
        this.orderedPrice = orderedPrice;
    }

    public int getDeliveredQuantity() {
        return deliveredQuantity;
    }

    public void setDeliveredQuantity(int deliveredQuantity) {
        this.deliveredQuantity = deliveredQuantity;
    }

    public double getDeliveredPrice() {
        return deliveredPrice;
    }

    public void setDeliveredPrice(double deliveredPrice) {
        this.deliveredPrice = deliveredPrice;
    }
}
