package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class ListDevicesResponse extends GenericJson
{
    @Key
    private List<Device> devices;
    @Key
    private String nextPageToken;
    
    public List<Device> getDevices() {
        return this.devices;
    }
    
    public ListDevicesResponse setDevices(final List<Device> devices) {
        this.devices = devices;
        return this;
    }
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public ListDevicesResponse setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public ListDevicesResponse set(final String s, final Object o) {
        return (ListDevicesResponse)super.set(s, o);
    }
    
    public ListDevicesResponse clone() {
        return (ListDevicesResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)Device.class);
    }
}
