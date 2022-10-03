package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceUserInfo extends GenericJson
{
    @Key
    private String email;
    @Key
    private String userType;
    
    public String getEmail() {
        return this.email;
    }
    
    public DeviceUserInfo setEmail(final String email) {
        this.email = email;
        return this;
    }
    
    public String getUserType() {
        return this.userType;
    }
    
    public DeviceUserInfo setUserType(final String userType) {
        this.userType = userType;
        return this;
    }
    
    public DeviceUserInfo set(final String s, final Object o) {
        return (DeviceUserInfo)super.set(s, o);
    }
    
    public DeviceUserInfo clone() {
        return (DeviceUserInfo)super.clone();
    }
}
