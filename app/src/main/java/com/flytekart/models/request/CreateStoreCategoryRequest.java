package com.flytekart.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateStoreCategoryRequest {

    @SerializedName("storeCategoryId")
    @Expose
    private String storeCategoryId;

    @SerializedName("storeId")
    @Expose
    private String storeId;

    @SerializedName("categoryId")
    @Expose
    private String categoryId;

    @SerializedName("isActive")
    @Expose
    private boolean isActive;

    public String getStoreCategoryId() {
        return storeCategoryId;
    }

    public void setStoreCategoryId(String storeCategoryId) {
        this.storeCategoryId = storeCategoryId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
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
}
