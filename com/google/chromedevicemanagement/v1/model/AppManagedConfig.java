package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AppManagedConfig extends GenericJson
{
    @Key
    private String appManagedConfig;
    
    public String getAppManagedConfig() {
        return this.appManagedConfig;
    }
    
    public AppManagedConfig setAppManagedConfig(final String appManagedConfig) {
        this.appManagedConfig = appManagedConfig;
        return this;
    }
    
    public AppManagedConfig set(final String s, final Object o) {
        return (AppManagedConfig)super.set(s, o);
    }
    
    public AppManagedConfig clone() {
        return (AppManagedConfig)super.clone();
    }
}
