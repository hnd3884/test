package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ChromeApplicationsSettings extends GenericJson
{
    @Key
    private AppInstallAllowed appInstallAllowed;
    @Key
    private List<ChromeApp> apps;
    
    public AppInstallAllowed getAppInstallAllowed() {
        return this.appInstallAllowed;
    }
    
    public ChromeApplicationsSettings setAppInstallAllowed(final AppInstallAllowed appInstallAllowed) {
        this.appInstallAllowed = appInstallAllowed;
        return this;
    }
    
    public List<ChromeApp> getApps() {
        return this.apps;
    }
    
    public ChromeApplicationsSettings setApps(final List<ChromeApp> apps) {
        this.apps = apps;
        return this;
    }
    
    public ChromeApplicationsSettings set(final String s, final Object o) {
        return (ChromeApplicationsSettings)super.set(s, o);
    }
    
    public ChromeApplicationsSettings clone() {
        return (ChromeApplicationsSettings)super.clone();
    }
    
    static {
        Data.nullOf((Class)ChromeApp.class);
    }
}
