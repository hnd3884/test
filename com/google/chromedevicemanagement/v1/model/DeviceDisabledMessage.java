package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceDisabledMessage extends GenericJson
{
    @Key
    private String deviceDisabledMessage;
    
    public String getDeviceDisabledMessage() {
        return this.deviceDisabledMessage;
    }
    
    public DeviceDisabledMessage setDeviceDisabledMessage(final String deviceDisabledMessage) {
        this.deviceDisabledMessage = deviceDisabledMessage;
        return this;
    }
    
    public DeviceDisabledMessage set(final String s, final Object o) {
        return (DeviceDisabledMessage)super.set(s, o);
    }
    
    public DeviceDisabledMessage clone() {
        return (DeviceDisabledMessage)super.clone();
    }
}
