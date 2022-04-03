package com.flytekart.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BorzoOrder {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;
    @SerializedName("order_name")
    @Expose
    private String orderName;
    @SerializedName("vehicle_type_id")
    @Expose
    private int vehicleTypeId;
    @SerializedName("created_datetime")
    @Expose
    private String createdDatetime;
    @SerializedName("finish_datetime")
    @Expose
    private String finishDatetime;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("status_description")
    @Expose
    private String statusDescription;
    @SerializedName("matter")
    @Expose
    private String matter;
    @SerializedName("total_weight_kg")
    @Expose
    private int totalWeightKg;
    @SerializedName("is_client_notification_enabled")
    @Expose
    private boolean isClientNotificationEnabled;
    @SerializedName("is_contact_person_notification_enabled")
    @Expose
    private boolean isContactPersonNotificationEnabled;
    @SerializedName("loaders_count")
    @Expose
    private int loadersCount;
    @SerializedName("backpayment_details")
    @Expose
    private String backpaymentDetails;
    @SerializedName("points")
    @Expose
    private ArrayList<BorzoPoint> points;
    @SerializedName("payment_amount")
    @Expose
    private String paymentAmount;
    @SerializedName("delivery_fee_amount")
    @Expose
    private String deliveryFeeAmount;
    @SerializedName("weight_fee_amount")
    @Expose
    private String weightFeeAmount;
    @SerializedName("insurance_amount")
    @Expose
    private String insuranceAmount;
    @SerializedName("insurance_fee_amount")
    @Expose
    private String insuranceFeeAmount;
    @SerializedName("loading_fee_amount")
    @Expose
    private String loadingFeeAmount;
    @SerializedName("money_transfer_fee_amount")
    @Expose
    private String moneyTransferFeeAmount;
    @SerializedName("suburban_delivery_fee_amount")
    @Expose
    private String suburbanDeliveryFeeAmount;
    @SerializedName("overnight_fee_amount")
    @Expose
    private String overnightFeeAmount;
    @SerializedName("intercity_delivery_fee_amount")
    @Expose
    private String intercityDeliveryFeeAmount;
    @SerializedName("discount_amount")
    @Expose
    private String discountAmount;
    @SerializedName("promo_code_discount_amount")
    @Expose
    private String promoCodeDiscountAmount;
    @SerializedName("backpayment_amount")
    @Expose
    private String backpaymentAmount;
    @SerializedName("cod_fee_amount")
    @Expose
    private String codFeeAmount;
    @SerializedName("door_to_door_fee_amount")
    @Expose
    private String doorToDoorFeeAmount;
    @SerializedName("backpayment_photo_url")
    @Expose
    private String backpaymentPhotoUrl;
    @SerializedName("itinerary_document_url")
    @Expose
    private String itineraryDocumentUrl;
    @SerializedName("waybill_document_url")
    @Expose
    private String waybillDocumentUrl;
    @SerializedName("courier")
    @Expose
    private BorzoCourier courier;
    @SerializedName("is_motobox_required")
    @Expose
    private boolean isMotoboxRequired;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("bank_card_id")
    @Expose
    private String bankCardId;
    @SerializedName("applied_promo_code")
    @Expose
    private String appliedPromoCode;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public int getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(int vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(String createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public String getFinishDatetime() {
        return finishDatetime;
    }

    public void setFinishDatetime(String finishDatetime) {
        this.finishDatetime = finishDatetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getMatter() {
        return matter;
    }

    public void setMatter(String matter) {
        this.matter = matter;
    }

    public int getTotalWeightKg() {
        return totalWeightKg;
    }

    public void setTotalWeightKg(int totalWeightKg) {
        this.totalWeightKg = totalWeightKg;
    }

    public boolean isClientNotificationEnabled() {
        return isClientNotificationEnabled;
    }

    public void setClientNotificationEnabled(boolean clientNotificationEnabled) {
        isClientNotificationEnabled = clientNotificationEnabled;
    }

    public boolean isContactPersonNotificationEnabled() {
        return isContactPersonNotificationEnabled;
    }

    public void setContactPersonNotificationEnabled(boolean contactPersonNotificationEnabled) {
        isContactPersonNotificationEnabled = contactPersonNotificationEnabled;
    }

    public int getLoadersCount() {
        return loadersCount;
    }

    public void setLoadersCount(int loadersCount) {
        this.loadersCount = loadersCount;
    }

    public String getBackpaymentDetails() {
        return backpaymentDetails;
    }

    public void setBackpaymentDetails(String backpaymentDetails) {
        this.backpaymentDetails = backpaymentDetails;
    }

    public ArrayList<BorzoPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<BorzoPoint> points) {
        this.points = points;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getDeliveryFeeAmount() {
        return deliveryFeeAmount;
    }

    public void setDeliveryFeeAmount(String deliveryFeeAmount) {
        this.deliveryFeeAmount = deliveryFeeAmount;
    }

    public String getWeightFeeAmount() {
        return weightFeeAmount;
    }

    public void setWeightFeeAmount(String weightFeeAmount) {
        this.weightFeeAmount = weightFeeAmount;
    }

    public String getInsuranceAmount() {
        return insuranceAmount;
    }

    public void setInsuranceAmount(String insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }

    public String getInsuranceFeeAmount() {
        return insuranceFeeAmount;
    }

    public void setInsuranceFeeAmount(String insuranceFeeAmount) {
        this.insuranceFeeAmount = insuranceFeeAmount;
    }

    public String getLoadingFeeAmount() {
        return loadingFeeAmount;
    }

    public void setLoadingFeeAmount(String loadingFeeAmount) {
        this.loadingFeeAmount = loadingFeeAmount;
    }

    public String getMoneyTransferFeeAmount() {
        return moneyTransferFeeAmount;
    }

    public void setMoneyTransferFeeAmount(String moneyTransferFeeAmount) {
        this.moneyTransferFeeAmount = moneyTransferFeeAmount;
    }

    public String getSuburbanDeliveryFeeAmount() {
        return suburbanDeliveryFeeAmount;
    }

    public void setSuburbanDeliveryFeeAmount(String suburbanDeliveryFeeAmount) {
        this.suburbanDeliveryFeeAmount = suburbanDeliveryFeeAmount;
    }

    public String getOvernightFeeAmount() {
        return overnightFeeAmount;
    }

    public void setOvernightFeeAmount(String overnightFeeAmount) {
        this.overnightFeeAmount = overnightFeeAmount;
    }

    public String getIntercityDeliveryFeeAmount() {
        return intercityDeliveryFeeAmount;
    }

    public void setIntercityDeliveryFeeAmount(String intercityDeliveryFeeAmount) {
        this.intercityDeliveryFeeAmount = intercityDeliveryFeeAmount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getPromoCodeDiscountAmount() {
        return promoCodeDiscountAmount;
    }

    public void setPromoCodeDiscountAmount(String promoCodeDiscountAmount) {
        this.promoCodeDiscountAmount = promoCodeDiscountAmount;
    }

    public String getBackpaymentAmount() {
        return backpaymentAmount;
    }

    public void setBackpaymentAmount(String backpaymentAmount) {
        this.backpaymentAmount = backpaymentAmount;
    }

    public String getCodFeeAmount() {
        return codFeeAmount;
    }

    public void setCodFeeAmount(String codFeeAmount) {
        this.codFeeAmount = codFeeAmount;
    }

    public String getDoorToDoorFeeAmount() {
        return doorToDoorFeeAmount;
    }

    public void setDoorToDoorFeeAmount(String doorToDoorFeeAmount) {
        this.doorToDoorFeeAmount = doorToDoorFeeAmount;
    }

    public String getBackpaymentPhotoUrl() {
        return backpaymentPhotoUrl;
    }

    public void setBackpaymentPhotoUrl(String backpaymentPhotoUrl) {
        this.backpaymentPhotoUrl = backpaymentPhotoUrl;
    }

    public String getItineraryDocumentUrl() {
        return itineraryDocumentUrl;
    }

    public void setItineraryDocumentUrl(String itineraryDocumentUrl) {
        this.itineraryDocumentUrl = itineraryDocumentUrl;
    }

    public String getWaybillDocumentUrl() {
        return waybillDocumentUrl;
    }

    public void setWaybillDocumentUrl(String waybillDocumentUrl) {
        this.waybillDocumentUrl = waybillDocumentUrl;
    }

    public BorzoCourier getCourier() {
        return courier;
    }

    public void setCourier(BorzoCourier courier) {
        this.courier = courier;
    }

    public boolean isMotoboxRequired() {
        return isMotoboxRequired;
    }

    public void setMotoboxRequired(boolean motoboxRequired) {
        isMotoboxRequired = motoboxRequired;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBankCardId() {
        return bankCardId;
    }

    public void setBankCardId(String bankCardId) {
        this.bankCardId = bankCardId;
    }

    public String getAppliedPromoCode() {
        return appliedPromoCode;
    }

    public void setAppliedPromoCode(String appliedPromoCode) {
        this.appliedPromoCode = appliedPromoCode;
    }
}
