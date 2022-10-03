package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class GraphicsAdapterInfo extends GenericJson
{
    @Key
    @JsonString
    private Long deviceId;
    @Key
    private String driverVersion;
    @Key
    private String name;
    
    public Long getDeviceId() {
        return this.deviceId;
    }
    
    public GraphicsAdapterInfo setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
        return this;
    }
    
    public String getDriverVersion() {
        return this.driverVersion;
    }
    
    public GraphicsAdapterInfo setDriverVersion(final String driverVersion) {
        this.driverVersion = driverVersion;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public GraphicsAdapterInfo setName(final String name) {
        this.name = name;
        return this;
    }
    
    public GraphicsAdapterInfo set(final String s, final Object o) {
        return (GraphicsAdapterInfo)super.set(s, o);
    }
    
    public GraphicsAdapterInfo clone() {
        return (GraphicsAdapterInfo)super.clone();
    }
}
