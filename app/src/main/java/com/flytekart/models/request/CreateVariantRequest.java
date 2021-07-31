package com.flytekart.models.request;

import com.flytekart.models.AttributeValueDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateVariantRequest {

    @SerializedName("variantId")
    @Expose
    private String variantId;

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
    private float price;

    @SerializedName("originalPrice")
    @Expose
    private float originalPrice;

    @SerializedName("active")
    @Expose
    private boolean active;

    @SerializedName("attributeValueDTOs")
    @Expose
    private List<AttributeValueDTO> attributeValueDTOs;

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<AttributeValueDTO> getAttributeValueDTOs() {
        return attributeValueDTOs;
    }

    public void setAttributeValueDTOs(List<AttributeValueDTO> attributeValueDTOs) {
        this.attributeValueDTOs = attributeValueDTOs;
    }
}
