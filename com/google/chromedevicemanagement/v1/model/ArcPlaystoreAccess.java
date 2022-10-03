package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ArcPlaystoreAccess extends GenericJson
{
    @Key
    private String arcPlaystoreAccessMode;
    
    public String getArcPlaystoreAccessMode() {
        return this.arcPlaystoreAccessMode;
    }
    
    public ArcPlaystoreAccess setArcPlaystoreAccessMode(final String arcPlaystoreAccessMode) {
        this.arcPlaystoreAccessMode = arcPlaystoreAccessMode;
        return this;
    }
    
    public ArcPlaystoreAccess set(final String s, final Object o) {
        return (ArcPlaystoreAccess)super.set(s, o);
    }
    
    public ArcPlaystoreAccess clone() {
        return (ArcPlaystoreAccess)super.clone();
    }
}
