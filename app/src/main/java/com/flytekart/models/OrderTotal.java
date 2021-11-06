package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderTotal implements Parcelable {
    @SerializedName("totalPrice")
    @Expose
    private double totalPrice;
    @SerializedName("totalTax")
    @Expose
    private double totalTax;
    @SerializedName("total")
    @Expose
    private double total;

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(double totalTax) {
        this.totalTax = totalTax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.totalPrice);
        dest.writeDouble(this.totalTax);
        dest.writeDouble(this.total);
    }

    public void readFromParcel(Parcel source) {
        this.totalPrice = source.readDouble();
        this.totalTax = source.readDouble();
        this.total = source.readDouble();
    }

    public OrderTotal() {
    }

    protected OrderTotal(Parcel in) {
        this.totalPrice = in.readDouble();
        this.totalTax = in.readDouble();
        this.total = in.readDouble();
    }

    public static final Parcelable.Creator<OrderTotal> CREATOR = new Parcelable.Creator<OrderTotal>() {
        @Override
        public OrderTotal createFromParcel(Parcel source) {
            return new OrderTotal(source);
        }

        @Override
        public OrderTotal[] newArray(int size) {
            return new OrderTotal[size];
        }
    };
}
