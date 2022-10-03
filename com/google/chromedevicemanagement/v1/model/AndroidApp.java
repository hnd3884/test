package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AndroidApp extends GenericJson
{
    @Key
    private String appId;
    @Key
    private AppManagedConfig appManagedConfig;
    @Key
    private Boolean installAllowed;
    @Key
    private Boolean installed;
    @Key
    private Boolean pinned;
    
    public String getAppId() {
        return this.appId;
    }
    
    public AndroidApp setAppId(final String appId) {
        this.appId = appId;
        return this;
    }
    
    public AppManagedConfig getAppManagedConfig() {
        return this.appManagedConfig;
    }
    
    public AndroidApp setAppManagedConfig(final AppManagedConfig appManagedConfig) {
        this.appManagedConfig = appManagedConfig;
        return this;
    }
    
    public Boolean getInstallAllowed() {
        return this.installAllowed;
    }
    
    public AndroidApp setInstallAllowed(final Boolean installAllowed) {
        this.installAllowed = installAllowed;
        return this;
    }
    
    public Boolean getInstalled() {
        return this.installed;
    }
    
    public AndroidApp setInstalled(final Boolean installed) {
        this.installed = installed;
        return this;
    }
    
    public Boolean getPinned() {
        return this.pinned;
    }
    
    public AndroidApp setPinned(final Boolean pinned) {
        this.pinned = pinned;
        return this;
    }
    
    public AndroidApp set(final String s, final Object o) {
        return (AndroidApp)super.set(s, o);
    }
    
    public AndroidApp clone() {
        return (AndroidApp)super.clone();
    }
}
