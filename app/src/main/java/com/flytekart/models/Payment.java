package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payment implements Parcelable {
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("lastUpdatedAt")
    @Expose
    private String lastUpdatedAt;
    @SerializedName("deletedAt")
    @Expose
    private String deletedAt;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("lastUpdatedBy")
    @Expose
    private String lastUpdatedBy;
    @SerializedName("employeeCreatedBy")
    @Expose
    private String employeeCreatedBy;
    @SerializedName("employeeLastUpdatedBy")
    @Expose
    private String employeeLastUpdatedBy;
    @SerializedName("deletedBy")
    @Expose
    private String deletedBy;
    @SerializedName("employeeDeletedBy")
    @Expose
    private String employeeDeletedBy;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("orderId")
    @Expose
    private String orderId;

    @SerializedName("amount")
    @Expose
    private double amount;

    @SerializedName("paymentType")
    @Expose
    private PaymentType paymentType;

    @SerializedName("paymentStatus")
    @Expose
    private PaymentStatus paymentStatus;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getEmployeeCreatedBy() {
        return employeeCreatedBy;
    }

    public void setEmployeeCreatedBy(String employeeCreatedBy) {
        this.employeeCreatedBy = employeeCreatedBy;
    }

    public String getEmployeeLastUpdatedBy() {
        return employeeLastUpdatedBy;
    }

    public void setEmployeeLastUpdatedBy(String employeeLastUpdatedBy) {
        this.employeeLastUpdatedBy = employeeLastUpdatedBy;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getEmployeeDeletedBy() {
        return employeeDeletedBy;
    }

    public void setEmployeeDeletedBy(String employeeDeletedBy) {
        this.employeeDeletedBy = employeeDeletedBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.createdAt);
        dest.writeString(this.lastUpdatedAt);
        dest.writeString(this.deletedAt);
        dest.writeString(this.createdBy);
        dest.writeString(this.lastUpdatedBy);
        dest.writeString(this.employeeCreatedBy);
        dest.writeString(this.employeeLastUpdatedBy);
        dest.writeString(this.deletedBy);
        dest.writeString(this.employeeDeletedBy);
        dest.writeString(this.id);
        dest.writeString(this.orderId);
        dest.writeDouble(this.amount);
        dest.writeParcelable(this.paymentType, flags);
        dest.writeParcelable(this.paymentStatus, flags);
    }

    public void readFromParcel(Parcel source) {
        this.createdAt = source.readString();
        this.lastUpdatedAt = source.readString();
        this.deletedAt = source.readString();
        this.createdBy = source.readString();
        this.lastUpdatedBy = source.readString();
        this.employeeCreatedBy = source.readString();
        this.employeeLastUpdatedBy = source.readString();
        this.deletedBy = source.readString();
        this.employeeDeletedBy = source.readString();
        this.id = source.readString();
        this.orderId = source.readString();
        this.amount = source.readDouble();
        this.paymentType = source.readParcelable(PaymentType.class.getClassLoader());
        this.paymentStatus = source.readParcelable(PaymentStatus.class.getClassLoader());
    }

    public Payment() {
    }

    protected Payment(Parcel in) {
        this.createdAt = in.readString();
        this.lastUpdatedAt = in.readString();
        this.deletedAt = in.readString();
        this.createdBy = in.readString();
        this.lastUpdatedBy = in.readString();
        this.employeeCreatedBy = in.readString();
        this.employeeLastUpdatedBy = in.readString();
        this.deletedBy = in.readString();
        this.employeeDeletedBy = in.readString();
        this.id = in.readString();
        this.orderId = in.readString();
        this.amount = in.readDouble();
        this.paymentType = in.readParcelable(PaymentType.class.getClassLoader());
        this.paymentStatus = in.readParcelable(PaymentStatus.class.getClassLoader());
    }

    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel source) {
            return new Payment(source);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };
}
