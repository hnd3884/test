package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ForcedReenrollment extends GenericJson
{
    @Key
    private String forcedReenrollmentMode;
    
    public String getForcedReenrollmentMode() {
        return this.forcedReenrollmentMode;
    }
    
    public ForcedReenrollment setForcedReenrollmentMode(final String forcedReenrollmentMode) {
        this.forcedReenrollmentMode = forcedReenrollmentMode;
        return this;
    }
    
    public ForcedReenrollment set(final String s, final Object o) {
        return (ForcedReenrollment)super.set(s, o);
    }
    
    public ForcedReenrollment clone() {
        return (ForcedReenrollment)super.clone();
    }
}
