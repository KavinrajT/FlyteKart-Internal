package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductStoreProductDTO implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("categoryId")
    @Expose
    private String categoryId;

    @SerializedName("isActive")
    @Expose
    private boolean isActive;

    @SerializedName("storeProductId")
    @Expose
    private String storeProductId;

    @SerializedName("storeProductDeletedAt")
    @Expose
    private String storeProductDeletedAt;

    @SerializedName("storeProductDeletedBy")
    @Expose
    private String storeProductDeletedBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStoreProductId() {
        return storeProductId;
    }

    public void setStoreProductId(String storeProductId) {
        this.storeProductId = storeProductId;
    }

    public String getStoreProductDeletedAt() {
        return storeProductDeletedAt;
    }

    public void setStoreProductDeletedAt(String storeProductDeletedAt) {
        this.storeProductDeletedAt = storeProductDeletedAt;
    }

    public String getStoreProductDeletedBy() {
        return storeProductDeletedBy;
    }

    public void setStoreProductDeletedBy(String storeProductDeletedBy) {
        this.storeProductDeletedBy = storeProductDeletedBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.categoryId);
        dest.writeByte(this.isActive ? (byte) 1 : (byte) 0);
        dest.writeString(this.storeProductId);
        dest.writeString(this.storeProductDeletedAt);
        dest.writeString(this.storeProductDeletedBy);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
        this.categoryId = source.readString();
        this.isActive = source.readByte() != 0;
        this.storeProductId = source.readString();
        this.storeProductDeletedAt = source.readString();
        this.storeProductDeletedBy = source.readString();
    }

    public ProductStoreProductDTO() {
    }

    protected ProductStoreProductDTO(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.categoryId = in.readString();
        this.isActive = in.readByte() != 0;
        this.storeProductId = in.readString();
        this.storeProductDeletedAt = in.readString();
        this.storeProductDeletedBy = in.readString();
    }

    public static final Parcelable.Creator<ProductStoreProductDTO> CREATOR = new Parcelable.Creator<ProductStoreProductDTO>() {
        @Override
        public ProductStoreProductDTO createFromParcel(Parcel source) {
            return new ProductStoreProductDTO(source);
        }

        @Override
        public ProductStoreProductDTO[] newArray(int size) {
            return new ProductStoreProductDTO[size];
        }
    };
}
