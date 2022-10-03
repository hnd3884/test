package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AppInstallAllowed extends GenericJson
{
    @Key
    private String appInstallAllowedType;
    
    public String getAppInstallAllowedType() {
        return this.appInstallAllowedType;
    }
    
    public AppInstallAllowed setAppInstallAllowedType(final String appInstallAllowedType) {
        this.appInstallAllowedType = appInstallAllowedType;
        return this;
    }
    
    public AppInstallAllowed set(final String s, final Object o) {
        return (AppInstallAllowed)super.set(s, o);
    }
    
    public AppInstallAllowed clone() {
        return (AppInstallAllowed)super.clone();
    }
}
