package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VariantStoreVariantDTO implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("productId")
    @Expose
    private String productId;

    @SerializedName("sku")
    @Expose
    private String sku;

    @SerializedName("price")
    @Expose
    private Double price;

    @SerializedName("tax")
    @Expose
    private Double tax;

    @SerializedName("originalPrice")
    @Expose
    private Double originalPrice;

    @SerializedName("isActive")
    @Expose
    private boolean isActive;

    @SerializedName("storeVariantId")
    @Expose
    private String storeVariantId;

    @SerializedName("storeVariantIsActive")
    @Expose
    private boolean storeVariantIsActive;

    @SerializedName("storeVariantPrice")
    @Expose
    private Double storeVariantPrice;

    @SerializedName("storeVariantTax")
    @Expose
    private Double storeVariantTax;

    @SerializedName("storeVariantOriginalPrice")
    @Expose
    private Double storeVariantOriginalPrice;

    @SerializedName("storeVariantQuantity")
    @Expose
    private Integer storeVariantQuantity;

    @SerializedName("storeVariantDeletedAt")
    @Expose
    private String storeVariantDeletedAt;

    @SerializedName("storeVariantDeletedBy")
    @Expose
    private String storeVariantDeletedBy;

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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStoreVariantId() {
        return storeVariantId;
    }

    public void setStoreVariantId(String storeVariantId) {
        this.storeVariantId = storeVariantId;
    }

    public boolean isStoreVariantIsActive() {
        return storeVariantIsActive;
    }

    public void setStoreVariantIsActive(boolean storeVariantIsActive) {
        this.storeVariantIsActive = storeVariantIsActive;
    }

    public Double getStoreVariantPrice() {
        return storeVariantPrice;
    }

    public void setStoreVariantPrice(Double storeVariantPrice) {
        this.storeVariantPrice = storeVariantPrice;
    }

    public Double getStoreVariantTax() {
        return storeVariantTax;
    }

    public void setStoreVariantTax(Double storeVariantTax) {
        this.storeVariantTax = storeVariantTax;
    }

    public Double getStoreVariantOriginalPrice() {
        return storeVariantOriginalPrice;
    }

    public void setStoreVariantOriginalPrice(Double storeVariantOriginalPrice) {
        this.storeVariantOriginalPrice = storeVariantOriginalPrice;
    }

    public Integer getStoreVariantQuantity() {
        return storeVariantQuantity;
    }

    public void setStoreVariantQuantity(Integer storeVariantQuantity) {
        this.storeVariantQuantity = storeVariantQuantity;
    }

    public String getStoreVariantDeletedAt() {
        return storeVariantDeletedAt;
    }

    public void setStoreVariantDeletedAt(String storeVariantDeletedAt) {
        this.storeVariantDeletedAt = storeVariantDeletedAt;
    }

    public String getStoreVariantDeletedBy() {
        return storeVariantDeletedBy;
    }

    public void setStoreVariantDeletedBy(String storeVariantDeletedBy) {
        this.storeVariantDeletedBy = storeVariantDeletedBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.productId);
        dest.writeString(this.sku);
        dest.writeValue(this.price);
        dest.writeValue(this.tax);
        dest.writeValue(this.originalPrice);
        dest.writeByte(this.isActive ? (byte) 1 : (byte) 0);
        dest.writeString(this.storeVariantId);
        dest.writeByte(this.storeVariantIsActive ? (byte) 1 : (byte) 0);
        dest.writeValue(this.storeVariantPrice);
        dest.writeValue(this.storeVariantTax);
        dest.writeValue(this.storeVariantOriginalPrice);
        dest.writeValue(this.storeVariantQuantity);
        dest.writeString(this.storeVariantDeletedAt);
        dest.writeString(this.storeVariantDeletedBy);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
        this.productId = source.readString();
        this.sku = source.readString();
        this.price = (Double) source.readValue(Double.class.getClassLoader());
        this.tax = (Double) source.readValue(Double.class.getClassLoader());
        this.originalPrice = (Double) source.readValue(Double.class.getClassLoader());
        this.isActive = source.readByte() != 0;
        this.storeVariantId = source.readString();
        this.storeVariantIsActive = source.readByte() != 0;
        this.storeVariantPrice = (Double) source.readValue(Double.class.getClassLoader());
        this.storeVariantTax = (Double) source.readValue(Double.class.getClassLoader());
        this.storeVariantOriginalPrice = (Double) source.readValue(Double.class.getClassLoader());
        this.storeVariantQuantity = (Integer) source.readValue(Integer.class.getClassLoader());
        this.storeVariantDeletedAt = source.readString();
        this.storeVariantDeletedBy = source.readString();
    }

    public VariantStoreVariantDTO() {
    }

    protected VariantStoreVariantDTO(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.productId = in.readString();
        this.sku = in.readString();
        this.price = (Double) in.readValue(Double.class.getClassLoader());
        this.tax = (Double) in.readValue(Double.class.getClassLoader());
        this.originalPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.isActive = in.readByte() != 0;
        this.storeVariantId = in.readString();
        this.storeVariantIsActive = in.readByte() != 0;
        this.storeVariantPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.storeVariantTax = (Double) in.readValue(Double.class.getClassLoader());
        this.storeVariantOriginalPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.storeVariantQuantity = (Integer) in.readValue(Double.class.getClassLoader());
        this.storeVariantDeletedAt = in.readString();
        this.storeVariantDeletedBy = in.readString();
    }

    public static final Parcelable.Creator<VariantStoreVariantDTO> CREATOR = new Parcelable.Creator<VariantStoreVariantDTO>() {
        @Override
        public VariantStoreVariantDTO createFromParcel(Parcel source) {
            return new VariantStoreVariantDTO(source);
        }

        @Override
        public VariantStoreVariantDTO[] newArray(int size) {
            return new VariantStoreVariantDTO[size];
        }
    };
}
