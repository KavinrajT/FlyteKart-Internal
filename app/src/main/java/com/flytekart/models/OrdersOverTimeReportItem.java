package com.flytekart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrdersOverTimeReportItem {
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("placedOrders")
    @Expose
    private int placedOrders;
    @SerializedName("deliveredOrders")
    @Expose
    private int deliveredOrders;
    @SerializedName("totalOrderedUnits")
    @Expose
    private int totalOrderedUnits;
    @SerializedName("totalOrderedValue")
    @Expose
    private double totalOrderedValue;
    @SerializedName("totalDeliveredUnits")
    @Expose
    private int totalDeliveredUnits;
    @SerializedName("totalDeliveredValue")
    @Expose
    private double totalDeliveredValue;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getPlacedOrders() {
        return placedOrders;
    }

    public void setPlacedOrders(int placedOrders) {
        this.placedOrders = placedOrders;
    }

    public int getDeliveredOrders() {
        return deliveredOrders;
    }

    public void setDeliveredOrders(int deliveredOrders) {
        this.deliveredOrders = deliveredOrders;
    }

    public int getTotalOrderedUnits() {
        return totalOrderedUnits;
    }

    public void setTotalOrderedUnits(int totalOrderedUnits) {
        this.totalOrderedUnits = totalOrderedUnits;
    }

    public double getTotalOrderedValue() {
        return totalOrderedValue;
    }

    public void setTotalOrderedValue(double totalOrderedValue) {
        this.totalOrderedValue = totalOrderedValue;
    }

    public int getTotalDeliveredUnits() {
        return totalDeliveredUnits;
    }

    public void setTotalDeliveredUnits(int totalDeliveredUnits) {
        this.totalDeliveredUnits = totalDeliveredUnits;
    }

    public double getTotalDeliveredValue() {
        return totalDeliveredValue;
    }

    public void setTotalDeliveredValue(double totalDeliveredValue) {
        this.totalDeliveredValue = totalDeliveredValue;
    }
}
