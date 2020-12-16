package com.flytekart.myapplication.models;

public class Product {

    private String name;
    private Double price;
    private Double originalPrice;
    private String description;
    private Double quantity;
    private boolean isInStock;
    private boolean showAdvanceOption;
    private boolean showAdvanceInventory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public boolean isInStock() {
        return isInStock;
    }

    public void setInStock(boolean inStock) {
        this.isInStock = inStock;
    }

    public boolean isShowAdvanceOption() {
        return showAdvanceOption;
    }

    public void setShowAdvanceOption(boolean showAdvanceOption) {
        this.showAdvanceOption = showAdvanceOption;
    }

    public boolean isShowAdvanceInventory() {
        return showAdvanceInventory;
    }

    public void setShowAdvanceInventory(boolean showAdvanceInventory) {
        this.showAdvanceInventory = showAdvanceInventory;
    }
}
