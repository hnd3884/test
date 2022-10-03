package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceUnaffiliatedCrostiniAllowed extends GenericJson
{
    @Key
    private Boolean deviceUnaffiliatedCrostiniAllowed;
    
    public Boolean getDeviceUnaffiliatedCrostiniAllowed() {
        return this.deviceUnaffiliatedCrostiniAllowed;
    }
    
    public DeviceUnaffiliatedCrostiniAllowed setDeviceUnaffiliatedCrostiniAllowed(final Boolean deviceUnaffiliatedCrostiniAllowed) {
        this.deviceUnaffiliatedCrostiniAllowed = deviceUnaffiliatedCrostiniAllowed;
        return this;
    }
    
    public DeviceUnaffiliatedCrostiniAllowed set(final String s, final Object o) {
        return (DeviceUnaffiliatedCrostiniAllowed)super.set(s, o);
    }
    
    public DeviceUnaffiliatedCrostiniAllowed clone() {
        return (DeviceUnaffiliatedCrostiniAllowed)super.clone();
    }
}
