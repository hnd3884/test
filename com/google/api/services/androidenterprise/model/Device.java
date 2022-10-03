package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Device extends GenericJson
{
    @Key
    private String androidId;
    @Key
    private String managementType;
    @Key
    private Policy policy;
    @Key
    private DeviceReport report;
    
    public String getAndroidId() {
        return this.androidId;
    }
    
    public Device setAndroidId(final String androidId) {
        this.androidId = androidId;
        return this;
    }
    
    public String getManagementType() {
        return this.managementType;
    }
    
    public Device setManagementType(final String managementType) {
        this.managementType = managementType;
        return this;
    }
    
    public Policy getPolicy() {
        return this.policy;
    }
    
    public Device setPolicy(final Policy policy) {
        this.policy = policy;
        return this;
    }
    
    public DeviceReport getReport() {
        return this.report;
    }
    
    public Device setReport(final DeviceReport report) {
        this.report = report;
        return this;
    }
    
    public Device set(final String fieldName, final Object value) {
        return (Device)super.set(fieldName, value);
    }
    
    public Device clone() {
        return (Device)super.clone();
    }
}
