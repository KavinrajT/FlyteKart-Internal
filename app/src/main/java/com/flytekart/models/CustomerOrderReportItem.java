package com.flytekart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerOrderReportItem {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("totalOrderCount")
    @Expose
    private int totalOrderCount;
    @SerializedName("totalOrderValue")
    @Expose
    private double totalOrderValue;
    @SerializedName("lastOrderedAt")
    @Expose
    private String lastOrderedAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(int totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public double getTotalOrderValue() {
        return totalOrderValue;
    }

    public void setTotalOrderValue(double totalOrderValue) {
        this.totalOrderValue = totalOrderValue;
    }

    public String getLastOrderedAt() {
        return lastOrderedAt;
    }

    public void setLastOrderedAt(String lastOrderedAt) {
        this.lastOrderedAt = lastOrderedAt;
    }
}
