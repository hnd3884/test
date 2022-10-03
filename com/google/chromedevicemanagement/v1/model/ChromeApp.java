package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ChromeApp extends GenericJson
{
    @Key
    private Boolean accessToKeysAllowed;
    @Key
    private String appId;
    @Key
    private AppInstallAllowed appInstallAllowed;
    @Key
    private String appName;
    @Key
    private Boolean enterpriseChallengeAllowed;
    @Key
    private String extensionPolicy;
    @Key
    private Boolean installed;
    @Key
    private Boolean pinned;
    @Key
    private String url;
    
    public Boolean getAccessToKeysAllowed() {
        return this.accessToKeysAllowed;
    }
    
    public ChromeApp setAccessToKeysAllowed(final Boolean accessToKeysAllowed) {
        this.accessToKeysAllowed = accessToKeysAllowed;
        return this;
    }
    
    public String getAppId() {
        return this.appId;
    }
    
    public ChromeApp setAppId(final String appId) {
        this.appId = appId;
        return this;
    }
    
    public AppInstallAllowed getAppInstallAllowed() {
        return this.appInstallAllowed;
    }
    
    public ChromeApp setAppInstallAllowed(final AppInstallAllowed appInstallAllowed) {
        this.appInstallAllowed = appInstallAllowed;
        return this;
    }
    
    public String getAppName() {
        return this.appName;
    }
    
    public ChromeApp setAppName(final String appName) {
        this.appName = appName;
        return this;
    }
    
    public Boolean getEnterpriseChallengeAllowed() {
        return this.enterpriseChallengeAllowed;
    }
    
    public ChromeApp setEnterpriseChallengeAllowed(final Boolean enterpriseChallengeAllowed) {
        this.enterpriseChallengeAllowed = enterpriseChallengeAllowed;
        return this;
    }
    
    public String getExtensionPolicy() {
        return this.extensionPolicy;
    }
    
    public ChromeApp setExtensionPolicy(final String extensionPolicy) {
        this.extensionPolicy = extensionPolicy;
        return this;
    }
    
    public Boolean getInstalled() {
        return this.installed;
    }
    
    public ChromeApp setInstalled(final Boolean installed) {
        this.installed = installed;
        return this;
    }
    
    public Boolean getPinned() {
        return this.pinned;
    }
    
    public ChromeApp setPinned(final Boolean pinned) {
        this.pinned = pinned;
        return this;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public ChromeApp setUrl(final String url) {
        this.url = url;
        return this;
    }
    
    public ChromeApp set(final String s, final Object o) {
        return (ChromeApp)super.set(s, o);
    }
    
    public ChromeApp clone() {
        return (ChromeApp)super.clone();
    }
}
