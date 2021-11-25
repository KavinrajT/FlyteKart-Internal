package com.flytekart.models.request;

import com.flytekart.models.AttributeValueDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateVariantRequest {

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

    @SerializedName("active")
    @Expose
    private boolean active;

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

    public void setPrice(Double  price) {
        this.price = price;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double  tax) {
        this.tax = tax;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double  originalPrice) {
        this.originalPrice = originalPrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
