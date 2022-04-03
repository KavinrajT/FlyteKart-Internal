package com.flytekart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BorzoPoint {
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("contact_person")
    @Expose
    private BorzoContactPerson contactPerson;
    @SerializedName("client_order_id")
    @Expose
    private String clientOrderId;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("taking_amount")
    @Expose
    private String takingAmount;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("isOrderPaymentHere")
    @Expose
    private boolean isOrderPaymentHere;
    @SerializedName("building_number")
    @Expose
    private String buildingNumber;
    @SerializedName("entrance_number")
    @Expose
    private String entranceNumber;
    @SerializedName("intercom_code")
    @Expose
    private String intercomCode;
    @SerializedName("floor_number")
    @Expose
    private String floorNumber;
    @SerializedName("apartment_number")
    @Expose
    private String apartmentNumber;
    @SerializedName("delivery_id")
    @Expose
    private Integer deliveryId;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BorzoContactPerson getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(BorzoContactPerson contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTakingAmount() {
        return takingAmount;
    }

    public void setTakingAmount(String takingAmount) {
        this.takingAmount = takingAmount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isOrderPaymentHere() {
        return isOrderPaymentHere;
    }

    public void setOrderPaymentHere(boolean orderPaymentHere) {
        isOrderPaymentHere = orderPaymentHere;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getEntranceNumber() {
        return entranceNumber;
    }

    public void setEntranceNumber(String entranceNumber) {
        this.entranceNumber = entranceNumber;
    }

    public String getIntercomCode() {
        return intercomCode;
    }

    public void setIntercomCode(String intercomCode) {
        this.intercomCode = intercomCode;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public Integer getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Integer deliveryId) {
        this.deliveryId = deliveryId;
    }
}
