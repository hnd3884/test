package com.me.tools.zcutil;

import java.util.Map;
import java.util.Properties;

public class AddUser
{
    private String did;
    private String userType;
    private String licenseType;
    private String licenseCategory;
    private String country;
    private String state;
    private String city;
    private String ipAddress;
    private String productName;
    private String isCloud;
    private Properties userProp;
    private Properties additionalProp;
    
    public AddUser() {
        this.did = null;
        this.userType = null;
        this.licenseType = null;
        this.licenseCategory = null;
        this.country = null;
        this.state = null;
        this.city = null;
        this.ipAddress = null;
        this.productName = null;
        this.isCloud = null;
        this.userProp = null;
        this.additionalProp = null;
        this.userProp = new Properties();
    }
    
    public void setDid(final String did) {
        this.did = did;
        this.userProp.setProperty("did", did);
    }
    
    public void setUserType(final String userType) {
        this.userType = userType;
        this.userProp.setProperty("User_Type", userType);
    }
    
    public void setLicenseType(final String licenseType) {
        this.licenseType = licenseType;
        this.userProp.setProperty("License_Type", licenseType);
    }
    
    public void setLicenseCategory(final String licenseCategory) {
        this.licenseCategory = licenseCategory;
        this.userProp.setProperty("License_Category", licenseCategory);
    }
    
    public void setProduct(final String productName) {
        this.productName = productName;
        this.userProp.setProperty("Product", this.productName);
    }
    
    public void setIsCloud(final String isCloud) {
        this.isCloud = isCloud;
        this.userProp.setProperty("is_od", this.isCloud);
    }
    
    public void setCountry(final String country) {
        this.country = country;
        this.userProp.setProperty("country", country);
    }
    
    public void setState(final String state) {
        this.state = state;
        this.userProp.setProperty("state", state);
    }
    
    public void setCity(final String city) {
        this.city = city;
        this.userProp.setProperty("city", city);
    }
    
    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
        this.userProp.setProperty("user_ip", ipAddress);
    }
    
    public void setAdditionalProp(final Properties additionalProp) {
        this.additionalProp = additionalProp;
    }
    
    public String getDid(final String did) {
        return this.did;
    }
    
    public String getUserType(final String userType) {
        return this.userType;
    }
    
    public String getLicenseType(final String licenseType) {
        return this.licenseType;
    }
    
    public String getLicenseCategory(final String licenseCategory) {
        return this.licenseCategory;
    }
    
    public String getCountry(final String country) {
        return this.country;
    }
    
    public String getState(final String state) {
        return this.state;
    }
    
    public String getCity(final String city) {
        return this.city;
    }
    
    public String getIpAddress(final String ipAddress) {
        return this.ipAddress;
    }
    
    public Properties getUserProp() {
        if (this.additionalProp != null) {
            this.userProp.putAll(this.additionalProp);
        }
        return this.userProp;
    }
}
