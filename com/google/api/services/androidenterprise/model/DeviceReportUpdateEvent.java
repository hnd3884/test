package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceReportUpdateEvent extends GenericJson
{
    @Key
    private String deviceId;
    @Key
    private DeviceReport report;
    @Key
    private String userId;
    
    public String getDeviceId() {
        return this.deviceId;
    }
    
    public DeviceReportUpdateEvent setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
    
    public DeviceReport getReport() {
        return this.report;
    }
    
    public DeviceReportUpdateEvent setReport(final DeviceReport report) {
        this.report = report;
        return this;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public DeviceReportUpdateEvent setUserId(final String userId) {
        this.userId = userId;
        return this;
    }
    
    public DeviceReportUpdateEvent set(final String fieldName, final Object value) {
        return (DeviceReportUpdateEvent)super.set(fieldName, value);
    }
    
    public DeviceReportUpdateEvent clone() {
        return (DeviceReportUpdateEvent)super.clone();
    }
}
