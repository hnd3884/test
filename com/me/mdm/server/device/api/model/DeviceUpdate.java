package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.me.mdm.api.model.BaseAPIModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceUpdate extends BaseAPIModel
{
    private Long deviceId;
    @JsonProperty("device_name")
    private String deviceName;
    @JsonProperty("apn_username")
    private String apnUserName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("asset_owner")
    private String assetOwner;
    @JsonProperty("asset_tag")
    private String assetTag;
    @JsonProperty("office")
    private String office;
    @JsonProperty("branch")
    private String branch;
    @JsonProperty("location")
    private String location;
    @JsonProperty("area_manager")
    private String areaManager;
    @JsonProperty("apn_password")
    private String apnPassword;
    @JsonProperty("purchase_date")
    private Long purchaseDate;
    @JsonProperty("purchase_order_number")
    private String purchaseOrderNumber;
    @JsonProperty("purchase_price")
    private String purchasePrice;
    @JsonProperty("purchase_type")
    private String purchaseType;
    @JsonProperty("warranty_expiration_date")
    private Long warrantyExpirationDate;
    @JsonProperty("warranty_number")
    private String warrantyNumber;
    @JsonProperty("warranty_type")
    private String warrantyType;
    @JsonProperty("registered_time")
    private Long registeredTime;
    @JsonProperty("enrollment_request_time")
    private Long enrollmentRequestTime;
    @JsonProperty("enrollment_type")
    private Integer enrollmentType;
    
    public Long getDeviceId() {
        return this.deviceId;
    }
    
    public void setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceName() {
        return this.deviceName;
    }
    
    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getApnUserName() {
        return this.apnUserName;
    }
    
    public void setApnUserName(final String apnUserName) {
        this.apnUserName = apnUserName;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getAssetOwner() {
        return this.assetOwner;
    }
    
    public void setAssetOwner(final String assetOwner) {
        this.assetOwner = assetOwner;
    }
    
    public String getAssetTag() {
        return this.assetTag;
    }
    
    public void setAssetTag(final String assetTag) {
        this.assetTag = assetTag;
    }
    
    public String getOffice() {
        return this.office;
    }
    
    public void setOffice(final String office) {
        this.office = office;
    }
    
    public String getBranch() {
        return this.branch;
    }
    
    public void setBranch(final String branch) {
        this.branch = branch;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(final String location) {
        this.location = location;
    }
    
    public String getAreaManager() {
        return this.areaManager;
    }
    
    public void setAreaManager(final String areaManager) {
        this.areaManager = areaManager;
    }
    
    public String getApnPassword() {
        return this.apnPassword;
    }
    
    public void setApnPassword(final String apnPassword) {
        this.apnPassword = apnPassword;
    }
    
    public Long getPurchaseDate() {
        return this.purchaseDate;
    }
    
    public void setPurchaseDate(final Long purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public String getPurchaseOrderNumber() {
        return this.purchaseOrderNumber;
    }
    
    public void setPurchaseOrderNumber(final String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }
    
    public String getPurchasePrice() {
        return this.purchasePrice;
    }
    
    public void setPurchasePrice(final String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    public String getPurchaseType() {
        return this.purchaseType;
    }
    
    public void setPurchaseType(final String purchaseType) {
        this.purchaseType = purchaseType;
    }
    
    public Long getWarrantyExpirationDate() {
        return this.warrantyExpirationDate;
    }
    
    public void setWarrantyExpirationDate(final Long warrantyExpirationDate) {
        this.warrantyExpirationDate = warrantyExpirationDate;
    }
    
    public String getWarrantyNumber() {
        return this.warrantyNumber;
    }
    
    public void setWarrantyNumber(final String warrantyNumber) {
        this.warrantyNumber = warrantyNumber;
    }
    
    public String getWarrantyType() {
        return this.warrantyType;
    }
    
    public void setWarrantyType(final String warrantyType) {
        this.warrantyType = warrantyType;
    }
    
    public Long getRegisteredTime() {
        return this.registeredTime;
    }
    
    public void setRegisteredTime(final Long registeredTime) {
        this.registeredTime = registeredTime;
    }
    
    public Long getEnrollmentRequestTime() {
        return this.enrollmentRequestTime;
    }
    
    public void setEnrollmentRequestTime(final Long enrollmentRequestTime) {
        this.enrollmentRequestTime = enrollmentRequestTime;
    }
    
    public Integer getEnrollmentType() {
        return this.enrollmentType;
    }
    
    public void setEnrollmentType(final Integer enrollmentType) {
        this.enrollmentType = enrollmentType;
    }
}
