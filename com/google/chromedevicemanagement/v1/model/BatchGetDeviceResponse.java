package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BatchGetDeviceResponse extends GenericJson
{
    @Key
    private List<Device> devices;
    @Key
    private List<UpdateFailureInfo> failedDevices;
    
    public List<Device> getDevices() {
        return this.devices;
    }
    
    public BatchGetDeviceResponse setDevices(final List<Device> devices) {
        this.devices = devices;
        return this;
    }
    
    public List<UpdateFailureInfo> getFailedDevices() {
        return this.failedDevices;
    }
    
    public BatchGetDeviceResponse setFailedDevices(final List<UpdateFailureInfo> failedDevices) {
        this.failedDevices = failedDevices;
        return this;
    }
    
    public BatchGetDeviceResponse set(final String s, final Object o) {
        return (BatchGetDeviceResponse)super.set(s, o);
    }
    
    public BatchGetDeviceResponse clone() {
        return (BatchGetDeviceResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)Device.class);
    }
}
