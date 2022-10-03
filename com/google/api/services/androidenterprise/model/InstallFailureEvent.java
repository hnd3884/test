package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class InstallFailureEvent extends GenericJson
{
    @Key
    private String deviceId;
    @Key
    private String failureDetails;
    @Key
    private String failureReason;
    @Key
    private String productId;
    @Key
    private String userId;
    
    public String getDeviceId() {
        return this.deviceId;
    }
    
    public InstallFailureEvent setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
    
    public String getFailureDetails() {
        return this.failureDetails;
    }
    
    public InstallFailureEvent setFailureDetails(final String failureDetails) {
        this.failureDetails = failureDetails;
        return this;
    }
    
    public String getFailureReason() {
        return this.failureReason;
    }
    
    public InstallFailureEvent setFailureReason(final String failureReason) {
        this.failureReason = failureReason;
        return this;
    }
    
    public String getProductId() {
        return this.productId;
    }
    
    public InstallFailureEvent setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public InstallFailureEvent setUserId(final String userId) {
        this.userId = userId;
        return this;
    }
    
    public InstallFailureEvent set(final String fieldName, final Object value) {
        return (InstallFailureEvent)super.set(fieldName, value);
    }
    
    public InstallFailureEvent clone() {
        return (InstallFailureEvent)super.clone();
    }
}
