package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VariantAttributeValue implements Parcelable {

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
    @SerializedName("variant")
    @Expose
    private Variant variant;
    @SerializedName("attributeValue")
    @Expose
    private AttributeValue attributeValue;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("lastUpdatedBy")
    @Expose
    private String lastUpdatedBy;
    @SerializedName("deletedBy")
    @Expose
    private String deletedBy;

    public VariantAttributeValue() {
    }

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

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    public AttributeValue getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(AttributeValue attributeValue) {
        this.attributeValue = attributeValue;
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
        dest.writeParcelable(this.variant, flags);
        dest.writeParcelable(this.attributeValue, flags);
        dest.writeString(this.createdBy);
        dest.writeString(this.lastUpdatedBy);
        dest.writeString(this.deletedBy);
    }

    public void readFromParcel(Parcel source) {
        this.createdAt = source.readString();
        this.lastUpdatedAt = source.readString();
        this.deletedAt = source.readString();
        this.id = source.readString();
        this.variant = source.readParcelable(Variant.class.getClassLoader());
        this.attributeValue = source.readParcelable(AttributeValue.class.getClassLoader());
        this.createdBy = source.readString();
        this.lastUpdatedBy = source.readString();
        this.deletedBy = source.readString();
    }

    protected VariantAttributeValue(Parcel in) {
        this.createdAt = in.readString();
        this.lastUpdatedAt = in.readString();
        this.deletedAt = in.readString();
        this.id = in.readString();
        this.variant = in.readParcelable(Variant.class.getClassLoader());
        this.attributeValue = in.readParcelable(AttributeValue.class.getClassLoader());
        this.createdBy = in.readString();
        this.lastUpdatedBy = in.readString();
        this.deletedBy = in.readString();
    }

    public static final Parcelable.Creator<VariantAttributeValue> CREATOR = new Parcelable.Creator<VariantAttributeValue>() {
        @Override
        public VariantAttributeValue createFromParcel(Parcel source) {
            return new VariantAttributeValue(source);
        }

        @Override
        public VariantAttributeValue[] newArray(int size) {
            return new VariantAttributeValue[size];
        }
    };
}