package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StoreVariant implements Parcelable {
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("lastUpdatedAt")
    @Expose
    private String lastUpdatedAt;
    @SerializedName("deletedAt")
    @Expose
    private String deletedAt;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("store")
    @Expose
    private Store store;
    @SerializedName("variant")
    @Expose
    private Variant variant;
    @SerializedName("price")
    @Expose
    private double price;
    @SerializedName("tax")
    @Expose
    private double tax;
    @SerializedName("originalPrice")
    @Expose
    private double originalPrice;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("active")
    @Expose
    private boolean active;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("lastUpdatedBy")
    @Expose
    private String lastUpdatedBy;
    @SerializedName("deletedBy")
    @Expose
    private String deletedBy;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
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
        dest.writeString(this.id);
        dest.writeParcelable(this.store, flags);
        dest.writeParcelable(this.variant, flags);
        dest.writeDouble(this.price);
        dest.writeDouble(this.tax);
        dest.writeDouble(this.originalPrice);
        dest.writeInt(this.quantity);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
        dest.writeString(this.createdBy);
        dest.writeString(this.lastUpdatedBy);
        dest.writeString(this.deletedBy);
    }

    public void readFromParcel(Parcel source) {
        this.createdAt = source.readString();
        this.lastUpdatedAt = source.readString();
        this.deletedAt = source.readString();
        this.id = source.readString();
        this.store = source.readParcelable(Store.class.getClassLoader());
        this.variant = source.readParcelable(Variant.class.getClassLoader());
        this.price = source.readDouble();
        this.tax = source.readDouble();
        this.originalPrice = source.readDouble();
        this.quantity = source.readInt();
        this.active = source.readByte() != 0;
        this.createdBy = source.readString();
        this.lastUpdatedBy = source.readString();
        this.deletedBy = source.readString();
    }

    public StoreVariant() {
    }

    protected StoreVariant(Parcel in) {
        this.createdAt = in.readString();
        this.lastUpdatedAt = in.readString();
        this.deletedAt = in.readString();
        this.id = in.readString();
        this.store = in.readParcelable(Store.class.getClassLoader());
        this.variant = in.readParcelable(Variant.class.getClassLoader());
        this.price = in.readDouble();
        this.tax = in.readDouble();
        this.originalPrice = in.readDouble();
        this.quantity = in.readInt();
        this.active = in.readByte() != 0;
        this.createdBy = in.readString();
        this.lastUpdatedBy = in.readString();
        this.deletedBy = in.readString();
    }

    public static final Parcelable.Creator<StoreVariant> CREATOR = new Parcelable.Creator<StoreVariant>() {
        @Override
        public StoreVariant createFromParcel(Parcel source) {
            return new StoreVariant(source);
        }

        @Override
        public StoreVariant[] newArray(int size) {
            return new StoreVariant[size];
        }
    };
}
