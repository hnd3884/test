package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AppStatus extends GenericJson
{
    @Key
    private String appId;
    @Key
    private String appName;
    @Key
    private String extensionVersion;
    @Key
    private String status;
    
    public String getAppId() {
        return this.appId;
    }
    
    public AppStatus setAppId(final String appId) {
        this.appId = appId;
        return this;
    }
    
    public String getAppName() {
        return this.appName;
    }
    
    public AppStatus setAppName(final String appName) {
        this.appName = appName;
        return this;
    }
    
    public String getExtensionVersion() {
        return this.extensionVersion;
    }
    
    public AppStatus setExtensionVersion(final String extensionVersion) {
        this.extensionVersion = extensionVersion;
        return this;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public AppStatus setStatus(final String status) {
        this.status = status;
        return this;
    }
    
    public AppStatus set(final String s, final Object o) {
        return (AppStatus)super.set(s, o);
    }
    
    public AppStatus clone() {
        return (AppStatus)super.clone();
    }
}
