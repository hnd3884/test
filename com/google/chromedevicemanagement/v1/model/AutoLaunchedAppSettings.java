package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AutoLaunchedAppSettings extends GenericJson
{
    @Key
    private String appId;
    @Key
    private String appName;
    @Key
    private Boolean enableAutoLoginBailout;
    @Key
    private Boolean promptForNetworkWhenOffline;
    
    public String getAppId() {
        return this.appId;
    }
    
    public AutoLaunchedAppSettings setAppId(final String appId) {
        this.appId = appId;
        return this;
    }
    
    public String getAppName() {
        return this.appName;
    }
    
    public AutoLaunchedAppSettings setAppName(final String appName) {
        this.appName = appName;
        return this;
    }
    
    public Boolean getEnableAutoLoginBailout() {
        return this.enableAutoLoginBailout;
    }
    
    public AutoLaunchedAppSettings setEnableAutoLoginBailout(final Boolean enableAutoLoginBailout) {
        this.enableAutoLoginBailout = enableAutoLoginBailout;
        return this;
    }
    
    public Boolean getPromptForNetworkWhenOffline() {
        return this.promptForNetworkWhenOffline;
    }
    
    public AutoLaunchedAppSettings setPromptForNetworkWhenOffline(final Boolean promptForNetworkWhenOffline) {
        this.promptForNetworkWhenOffline = promptForNetworkWhenOffline;
        return this;
    }
    
    public AutoLaunchedAppSettings set(final String s, final Object o) {
        return (AutoLaunchedAppSettings)super.set(s, o);
    }
    
    public AutoLaunchedAppSettings clone() {
        return (AutoLaunchedAppSettings)super.clone();
    }
}
