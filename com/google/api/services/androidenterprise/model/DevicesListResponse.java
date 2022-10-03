package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class DevicesListResponse extends GenericJson
{
    @Key
    private List<Device> device;
    
    public List<Device> getDevice() {
        return this.device;
    }
    
    public DevicesListResponse setDevice(final List<Device> device) {
        this.device = device;
        return this;
    }
    
    public DevicesListResponse set(final String fieldName, final Object value) {
        return (DevicesListResponse)super.set(fieldName, value);
    }
    
    public DevicesListResponse clone() {
        return (DevicesListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)Device.class);
    }
}
