package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceExtnModel
{
    @JsonProperty("NAME")
    private String name;
    @JsonProperty("APN_USER_NAME")
    private String apnUserName;
    @JsonProperty("MANAGED_DEVICE_ID")
    private Long deviceId;
    @JsonProperty("USER_ID")
    private Long userId;
    @JsonProperty("DESCRIPTION")
    private String description;
    @JsonProperty("LAST_MODIFIED_TIME")
    private Long lastModifiedTime;
    @JsonProperty("IS_MODIFIED")
    private boolean isModified;
    @JsonProperty("ASSET_OWNER")
    private String assetOwner;
    @JsonProperty("ASSET_TAG")
    private String assetTag;
    @JsonProperty("OFFICE")
    private String office;
    @JsonProperty("BRANCH")
    private String branch;
    @JsonProperty("LOCATION")
    private String location;
    @JsonProperty("AREA_MANAGER")
    private String areaManager;
    @JsonProperty("APN_PASSWORD")
    private String apnPassword;
    @JsonProperty("PURCHASE_DATE")
    private Long purchaseDate;
    @JsonProperty("PURCHASE_ORDER_NUMBER")
    private String purchaseOrderNumber;
    @JsonProperty("PURCHASE_PRICE")
    private String purchasePrice;
    @JsonProperty("PURCHASE_TYPE")
    private String purchaseType;
    @JsonProperty("WARRANTY_EXPIRATION_DATE")
    private Long warrantyExpirationDate;
    @JsonProperty("WARRANTY_NUMBER")
    private String warrantyNumber;
    @JsonProperty("WARRANTY_TYPE")
    private String warrantyType;
    @JsonProperty("GENERIC_IDENTIFIER")
    private String genericIdentifier;
    @JsonProperty("additional_data")
    private HashMap<String, Object> additionalData;
    
    public HashMap<String, Object> getAdditionalData() {
        return this.additionalData;
    }
    
    public void setAdditionalData(final HashMap<String, Object> additionalData) {
        this.additionalData = additionalData;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getApnUserName() {
        return this.apnUserName;
    }
    
    public void setApnUserName(final String apnUserName) {
        this.apnUserName = apnUserName;
    }
    
    public Long getDeviceId() {
        return this.deviceId;
    }
    
    public void setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public Long getUserId() {
        return this.userId;
    }
    
    public void setUserId(final Long userId) {
        this.userId = userId;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public Long getLastModifiedTime() {
        return this.lastModifiedTime;
    }
    
    public void setLastModifiedTime(final Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
    
    @JsonIgnore
    public boolean isModified() {
        return this.isModified;
    }
    
    public void setModified(final boolean modified) {
        this.isModified = modified;
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
    
    public String getGenericIdentifier() {
        return this.genericIdentifier;
    }
    
    public void setGenericIdentifier(final String genericIdentifier) {
        this.genericIdentifier = genericIdentifier;
    }
}
