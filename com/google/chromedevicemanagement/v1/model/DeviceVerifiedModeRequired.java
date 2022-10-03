package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceVerifiedModeRequired extends GenericJson
{
    @Key
    private Boolean deviceVerifiedModeRequired;
    
    public Boolean getDeviceVerifiedModeRequired() {
        return this.deviceVerifiedModeRequired;
    }
    
    public DeviceVerifiedModeRequired setDeviceVerifiedModeRequired(final Boolean deviceVerifiedModeRequired) {
        this.deviceVerifiedModeRequired = deviceVerifiedModeRequired;
        return this;
    }
    
    public DeviceVerifiedModeRequired set(final String s, final Object o) {
        return (DeviceVerifiedModeRequired)super.set(s, o);
    }
    
    public DeviceVerifiedModeRequired clone() {
        return (DeviceVerifiedModeRequired)super.clone();
    }
}
