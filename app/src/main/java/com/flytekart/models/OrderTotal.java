package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderTotal implements Parcelable {
    @SerializedName("totalPrice")
    @Expose
    private float totalPrice;
    @SerializedName("totalTax")
    @Expose
    private float totalTax;
    @SerializedName("total")
    @Expose
    private float total;

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public float getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(float totalTax) {
        this.totalTax = totalTax;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.totalPrice);
        dest.writeFloat(this.totalTax);
        dest.writeFloat(this.total);
    }

    public void readFromParcel(Parcel source) {
        this.totalPrice = source.readFloat();
        this.totalTax = source.readFloat();
        this.total = source.readFloat();
    }

    public OrderTotal() {
    }

    protected OrderTotal(Parcel in) {
        this.totalPrice = in.readFloat();
        this.totalTax = in.readFloat();
        this.total = in.readFloat();
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
