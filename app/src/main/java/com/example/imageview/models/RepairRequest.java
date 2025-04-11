package com.example.imageview.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class RepairRequest implements Serializable {
    private String id;
    private String userId;
    private String deviceType;
    private String deviceBrand;
    private String deviceModel;
    private String problemDescription;
    private List<String> photoUrls;
    private String status; // "pending", "in_progress", "completed", "cancelled"
    private Date createdAt;
    private Date updatedAt;
    private int offersCount;

    // Empty constructor needed for Firestore
    public RepairRequest() {
        this.photoUrls = new ArrayList<>();
        this.status = "pending";
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.offersCount = 0;
    }

    public RepairRequest(String userId, String deviceType, String deviceBrand, String deviceModel, String problemDescription) {
        this.userId = userId;
        this.deviceType = deviceType;
        this.deviceBrand = deviceBrand;
        this.deviceModel = deviceModel;
        this.problemDescription = problemDescription;
        this.photoUrls = new ArrayList<>();
        this.status = "pending";
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.offersCount = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public void addPhotoUrl(String photoUrl) {
        if (this.photoUrls == null) {
            this.photoUrls = new ArrayList<>();
        }
        this.photoUrls.add(photoUrl);
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

    public int getOffersCount() {
        return offersCount;
    }

    public void setOffersCount(int offersCount) {
        this.offersCount = offersCount;
    }

    public void incrementOffersCount() {
        this.offersCount++;
    }

    public void decrementOffersCount() {
        if (this.offersCount > 0) {
            this.offersCount--;
        }
    }
}
