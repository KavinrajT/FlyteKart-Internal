package com.flytekart.models.request;

import com.flytekart.models.AttributeValueDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateVariantVavRequest {

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
    private double price;

    @SerializedName("tax")
    @Expose
    private double tax;

    @SerializedName("originalPrice")
    @Expose
    private double originalPrice;

    @SerializedName("active")
    @Expose
    private boolean active;

    @SerializedName("attributeValueDTOs")
    @Expose
    private List<AttributeValueDTO> attributeValueDTOs;

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
