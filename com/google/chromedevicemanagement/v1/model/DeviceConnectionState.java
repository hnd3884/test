package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceConnectionState extends GenericJson
{
    @Key
    private String connectionState;
    @Key
    private String updateTime;
    
    public String getConnectionState() {
        return this.connectionState;
    }
    
    public DeviceConnectionState setConnectionState(final String connectionState) {
        this.connectionState = connectionState;
        return this;
    }
    
    public String getUpdateTime() {
        return this.updateTime;
    }
    
    public DeviceConnectionState setUpdateTime(final String updateTime) {
        this.updateTime = updateTime;
        return this;
    }
    
    public DeviceConnectionState set(final String s, final Object o) {
        return (DeviceConnectionState)super.set(s, o);
    }
    
    public DeviceConnectionState clone() {
        return (DeviceConnectionState)super.clone();
    }
}
