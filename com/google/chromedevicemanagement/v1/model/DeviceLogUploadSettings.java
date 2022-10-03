package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceLogUploadSettings extends GenericJson
{
    @Key
    private Boolean systemLogUploadEnabled;
    
    public Boolean getSystemLogUploadEnabled() {
        return this.systemLogUploadEnabled;
    }
    
    public DeviceLogUploadSettings setSystemLogUploadEnabled(final Boolean systemLogUploadEnabled) {
        this.systemLogUploadEnabled = systemLogUploadEnabled;
        return this;
    }
    
    public DeviceLogUploadSettings set(final String s, final Object o) {
        return (DeviceLogUploadSettings)super.set(s, o);
    }
    
    public DeviceLogUploadSettings clone() {
        return (DeviceLogUploadSettings)super.clone();
    }
}
