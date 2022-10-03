package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class NewDeviceEvent extends GenericJson
{
    @Key
    private String deviceId;
    @Key
    private String dpcPackageName;
    @Key
    private String managementType;
    @Key
    private String userId;
    
    public String getDeviceId() {
        return this.deviceId;
    }
    
    public NewDeviceEvent setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
    
    public String getDpcPackageName() {
        return this.dpcPackageName;
    }
    
    public NewDeviceEvent setDpcPackageName(final String dpcPackageName) {
        this.dpcPackageName = dpcPackageName;
        return this;
    }
    
    public String getManagementType() {
        return this.managementType;
    }
    
    public NewDeviceEvent setManagementType(final String managementType) {
        this.managementType = managementType;
        return this;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public NewDeviceEvent setUserId(final String userId) {
        this.userId = userId;
        return this;
    }
    
    public NewDeviceEvent set(final String fieldName, final Object value) {
        return (NewDeviceEvent)super.set(fieldName, value);
    }
    
    public NewDeviceEvent clone() {
        return (NewDeviceEvent)super.clone();
    }
}
