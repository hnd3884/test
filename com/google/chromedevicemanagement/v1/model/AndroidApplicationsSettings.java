package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class AndroidApplicationsSettings extends GenericJson
{
    @Key
    private List<AndroidApp> apps;
    @Key
    private ArcPlaystoreAccess arcPlaystoreAccess;
    
    public List<AndroidApp> getApps() {
        return this.apps;
    }
    
    public AndroidApplicationsSettings setApps(final List<AndroidApp> apps) {
        this.apps = apps;
        return this;
    }
    
    public ArcPlaystoreAccess getArcPlaystoreAccess() {
        return this.arcPlaystoreAccess;
    }
    
    public AndroidApplicationsSettings setArcPlaystoreAccess(final ArcPlaystoreAccess arcPlaystoreAccess) {
        this.arcPlaystoreAccess = arcPlaystoreAccess;
        return this;
    }
    
    public AndroidApplicationsSettings set(final String s, final Object o) {
        return (AndroidApplicationsSettings)super.set(s, o);
    }
    
    public AndroidApplicationsSettings clone() {
        return (AndroidApplicationsSettings)super.clone();
    }
}
