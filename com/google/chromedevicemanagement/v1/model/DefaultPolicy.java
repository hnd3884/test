package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DefaultPolicy extends GenericJson
{
    @Key
    private DevicePolicy devicePolicy;
    @Key
    private UserPolicy userPolicy;
    
    public DevicePolicy getDevicePolicy() {
        return this.devicePolicy;
    }
    
    public DefaultPolicy setDevicePolicy(final DevicePolicy devicePolicy) {
        this.devicePolicy = devicePolicy;
        return this;
    }
    
    public UserPolicy getUserPolicy() {
        return this.userPolicy;
    }
    
    public DefaultPolicy setUserPolicy(final UserPolicy userPolicy) {
        this.userPolicy = userPolicy;
        return this;
    }
    
    public DefaultPolicy set(final String s, final Object o) {
        return (DefaultPolicy)super.set(s, o);
    }
    
    public DefaultPolicy clone() {
        return (DefaultPolicy)super.clone();
    }
}
