package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderItem implements Parcelable {
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
    @SerializedName("orderItemStatusId")
    @Expose
    private String orderItemStatusId;
    @SerializedName("storeVariant")
    @Expose
    private StoreVariant storeVariant;
    @SerializedName("unitPrice")
    @Expose
    private float unitPrice;
    @SerializedName("unitTax")
    @Expose
    private float unitTax;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("totalPrice")
    @Expose
    private float totalPrice;
    @SerializedName("totalTax")
    @Expose
    private float totalTax;

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

    public String getOrderItemStatusId() {
        return orderItemStatusId;
    }

    public void setOrderItemStatusId(String orderItemStatusId) {
        this.orderItemStatusId = orderItemStatusId;
    }

    public StoreVariant getStoreVariant() {
        return storeVariant;
    }

    public void setStoreVariant(StoreVariant storeVariant) {
        this.storeVariant = storeVariant;
    }

    public float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public float getUnitTax() {
        return unitTax;
    }

    public void setUnitTax(float unitTax) {
        this.unitTax = unitTax;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

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
        dest.writeString(this.orderItemStatusId);
        dest.writeParcelable(this.storeVariant, flags);
        dest.writeFloat(this.unitPrice);
        dest.writeFloat(this.unitTax);
        dest.writeInt(this.quantity);
        dest.writeFloat(this.totalPrice);
        dest.writeFloat(this.totalTax);
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
        this.orderItemStatusId = source.readString();
        this.storeVariant = source.readParcelable(StoreVariant.class.getClassLoader());
        this.unitPrice = source.readFloat();
        this.unitTax = source.readFloat();
        this.quantity = source.readInt();
        this.totalPrice = source.readFloat();
        this.totalTax = source.readFloat();
    }

    public OrderItem() {
    }

    protected OrderItem(Parcel in) {
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
        this.orderItemStatusId = in.readString();
        this.storeVariant = in.readParcelable(StoreVariant.class.getClassLoader());
        this.unitPrice = in.readFloat();
        this.unitTax = in.readFloat();
        this.quantity = in.readInt();
        this.totalPrice = in.readFloat();
        this.totalTax = in.readFloat();
    }

    public static final Parcelable.Creator<OrderItem> CREATOR = new Parcelable.Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel source) {
            return new OrderItem(source);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };
}
