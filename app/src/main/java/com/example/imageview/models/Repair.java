package com.example.imageview.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
public class Repair implements Serializable {
    private String id;
    private String requestId;
    private String offerId;
    private String userId;
    private String serviceProviderId;
    private String serviceProviderName;
    private String serviceProviderPhone;
    private double price;
    private String repairStatus; // "received", "diagnosed", "repairing", "completed"
    private String paymentStatus; // "pending", "paid"
    private Date estimatedCompletionDate;
    private Date createdAt;
    private Date updatedAt;

    // Empty constructor needed for Firestore
    public Repair() {
        this.repairStatus = "received";
        this.paymentStatus = "pending";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Repair(String requestId, String offerId, String userId, String serviceProviderId,
                  String serviceProviderName, String serviceProviderPhone, double price) {
        this.requestId = requestId;
        this.offerId = offerId;
        this.userId = userId;
        this.serviceProviderId = serviceProviderId;
        this.serviceProviderName = serviceProviderName;
        this.serviceProviderPhone = serviceProviderPhone;
        this.price = price;
        this.repairStatus = "received";
        this.paymentStatus = "pending";
        this.createdAt = new Date();
        this.updatedAt = new Date();

        // Set estimated completion date to 7 days from now by default
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        this.estimatedCompletionDate = calendar.getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(String serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getServiceProviderName() {
        return serviceProviderName;
    }

    public void setServiceProviderName(String serviceProviderName) {
        this.serviceProviderName = serviceProviderName;
    }

    public String getServiceProviderPhone() {
        return serviceProviderPhone;
    }

    public void setServiceProviderPhone(String serviceProviderPhone) {
        this.serviceProviderPhone = serviceProviderPhone;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRepairStatus() {
        return repairStatus;
    }

    public void setRepairStatus(String repairStatus) {
        this.repairStatus = repairStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getEstimatedCompletionDate() {
        return estimatedCompletionDate;
    }

    public void setEstimatedCompletionDate(Date estimatedCompletionDate) {
        this.estimatedCompletionDate = estimatedCompletionDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
