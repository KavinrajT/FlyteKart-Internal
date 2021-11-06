package com.flytekart.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryStoreCategoryDTO implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("parentCategoryId")
    @Expose
    private String parentCategoryId;

    @SerializedName("isActive")
    @Expose
    private boolean isActive;

    @SerializedName("storeCategoryId")
    @Expose
    private String storeCategoryId;

    @SerializedName("storeCategoryDeletedAt")
    @Expose
    private String storeCategoryDeletedAt;

    @SerializedName("storeCategoryDeletedBy")
    @Expose
    private String storeCategoryDeletedBy;

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

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStoreCategoryId() {
        return storeCategoryId;
    }

    public void setStoreCategoryId(String storeCategoryId) {
        this.storeCategoryId = storeCategoryId;
    }

    public String getStoreCategoryDeletedAt() {
        return storeCategoryDeletedAt;
    }

    public void setStoreCategoryDeletedAt(String storeCategoryDeletedAt) {
        this.storeCategoryDeletedAt = storeCategoryDeletedAt;
    }

    public String getStoreCategoryDeletedBy() {
        return storeCategoryDeletedBy;
    }

    public void setStoreCategoryDeletedBy(String storeCategoryDeletedBy) {
        this.storeCategoryDeletedBy = storeCategoryDeletedBy;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.parentCategoryId);
        dest.writeByte(this.isActive ? (byte) 1 : (byte) 0);
        dest.writeString(this.storeCategoryId);
        dest.writeString(this.storeCategoryDeletedAt);
        dest.writeString(this.storeCategoryDeletedBy);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
        this.parentCategoryId = source.readString();
        this.isActive = source.readByte() != 0;
        this.storeCategoryId = source.readString();
        this.storeCategoryDeletedAt = source.readString();
        this.storeCategoryDeletedBy = source.readString();
    }

    public CategoryStoreCategoryDTO() {
    }

    protected CategoryStoreCategoryDTO(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.parentCategoryId = in.readString();
        this.isActive = in.readByte() != 0;
        this.storeCategoryId = in.readString();
        this.storeCategoryDeletedAt = in.readString();
        this.storeCategoryDeletedBy = in.readString();
    }

    public static final Parcelable.Creator<CategoryStoreCategoryDTO> CREATOR = new Parcelable.Creator<CategoryStoreCategoryDTO>() {
        @Override
        public CategoryStoreCategoryDTO createFromParcel(Parcel source) {
            return new CategoryStoreCategoryDTO(source);
        }

        @Override
        public CategoryStoreCategoryDTO[] newArray(int size) {
            return new CategoryStoreCategoryDTO[size];
        }
    };
}
