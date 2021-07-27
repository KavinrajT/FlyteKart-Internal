package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order implements Parcelable {
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
    @SerializedName("user")
    @Expose
    private EndUser endUser;
    @SerializedName("storeId")
    @Expose
    private String storeId;
    @SerializedName("orderStatus")
    @Expose
    private OrderStatus orderStatus;
    @SerializedName("orderSource")
    @Expose
    private OrderSource orderSource;

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

    public EndUser getEndUser() {
        return endUser;
    }

    public void setEndUser(EndUser endUser) {
        this.endUser = endUser;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderSource getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(OrderSource orderSource) {
        this.orderSource = orderSource;
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
        dest.writeParcelable(this.endUser, flags);
        dest.writeString(this.storeId);
        dest.writeParcelable(this.orderStatus, flags);
        dest.writeParcelable(this.orderSource, flags);
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
        this.endUser = source.readParcelable(EndUser.class.getClassLoader());
        this.storeId = source.readString();
        this.orderStatus = source.readParcelable(OrderStatus.class.getClassLoader());
        this.orderSource = source.readParcelable(OrderSource.class.getClassLoader());
    }

    public Order() {
    }

    protected Order(Parcel in) {
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
        this.endUser = in.readParcelable(EndUser.class.getClassLoader());
        this.storeId = in.readString();
        this.orderStatus = in.readParcelable(OrderStatus.class.getClassLoader());
        this.orderSource = in.readParcelable(OrderSource.class.getClassLoader());
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
