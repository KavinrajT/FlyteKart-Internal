package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderResponse implements Parcelable {
    @SerializedName("order")
    @Expose
    private Order order;
    @SerializedName("orderItems")
    @Expose
    private List<OrderItem> orderItems = null;
    @SerializedName("payments")
    @Expose
    private List<Payment> payments = null;
    @SerializedName("orderTotal")
    @Expose
    private OrderTotal orderTotal;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public OrderTotal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(OrderTotal orderTotal) {
        this.orderTotal = orderTotal;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.order, flags);
        dest.writeTypedList(this.orderItems);
        dest.writeTypedList(this.payments);
        dest.writeParcelable(this.orderTotal, flags);
    }

    public void readFromParcel(Parcel source) {
        this.order = source.readParcelable(Order.class.getClassLoader());
        this.orderItems = source.createTypedArrayList(OrderItem.CREATOR);
        this.payments = source.createTypedArrayList(Payment.CREATOR);
        this.orderTotal = source.readParcelable(OrderTotal.class.getClassLoader());
    }

    public OrderResponse() {
    }

    protected OrderResponse(Parcel in) {
        this.order = in.readParcelable(Order.class.getClassLoader());
        this.orderItems = in.createTypedArrayList(OrderItem.CREATOR);
        this.payments = in.createTypedArrayList(Payment.CREATOR);
        this.orderTotal = in.readParcelable(OrderTotal.class.getClassLoader());
    }

    public static final Parcelable.Creator<OrderResponse> CREATOR = new Parcelable.Creator<OrderResponse>() {
        @Override
        public OrderResponse createFromParcel(Parcel source) {
            return new OrderResponse(source);
        }

        @Override
        public OrderResponse[] newArray(int size) {
            return new OrderResponse[size];
        }
    };
}
