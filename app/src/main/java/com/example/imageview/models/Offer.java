package com.example.imageview.models;

import java.io.Serializable;
import java.util.Date;
public class Offer implements Serializable {
    private String id;
    private String requestId;
    private String serviceProviderId;
    private String serviceProviderName;
    private double price;
    private int estimatedDays;
    private String description;
    private String status; // "pending", "accepted", "rejected"
    private Date createdAt;
    private Date updatedAt;

    // Empty constructor needed for Firestore
    public Offer() {
        this.status = "pending";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Offer(String requestId, String serviceProviderId, String serviceProviderName, double price, int estimatedDays, String description) {
        this.requestId = requestId;
        this.serviceProviderId = serviceProviderId;
        this.serviceProviderName = serviceProviderName;
        this.price = price;
        this.estimatedDays = estimatedDays;
        this.description = description;
        this.status = "pending";
        this.createdAt = new Date();
        this.updatedAt = new Date();
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getEstimatedDays() {
        return estimatedDays;
    }

    public void setEstimatedDays(int estimatedDays) {
        this.estimatedDays = estimatedDays;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
