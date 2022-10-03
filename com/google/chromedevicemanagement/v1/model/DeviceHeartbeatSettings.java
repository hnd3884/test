package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceHeartbeatSettings extends GenericJson
{
    @Key
    private Boolean heartbeatEnabled;
    
    public Boolean getHeartbeatEnabled() {
        return this.heartbeatEnabled;
    }
    
    public DeviceHeartbeatSettings setHeartbeatEnabled(final Boolean heartbeatEnabled) {
        this.heartbeatEnabled = heartbeatEnabled;
        return this;
    }
    
    public DeviceHeartbeatSettings set(final String s, final Object o) {
        return (DeviceHeartbeatSettings)super.set(s, o);
    }
    
    public DeviceHeartbeatSettings clone() {
        return (DeviceHeartbeatSettings)super.clone();
    }
}
