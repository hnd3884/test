package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class ChromeOsMoveDevicesToOu extends GenericJson
{
    @Key
    private List<String> deviceIds;
    
    public List<String> getDeviceIds() {
        return this.deviceIds;
    }
    
    public ChromeOsMoveDevicesToOu setDeviceIds(final List<String> deviceIds) {
        this.deviceIds = deviceIds;
        return this;
    }
    
    public ChromeOsMoveDevicesToOu set(final String fieldName, final Object value) {
        return (ChromeOsMoveDevicesToOu)super.set(fieldName, value);
    }
    
    public ChromeOsMoveDevicesToOu clone() {
        return (ChromeOsMoveDevicesToOu)super.clone();
    }
}
