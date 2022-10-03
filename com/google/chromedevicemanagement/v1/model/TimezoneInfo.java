package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class TimezoneInfo extends GenericJson
{
    @Key
    private String posix;
    @Key
    private String region;
    
    public String getPosix() {
        return this.posix;
    }
    
    public TimezoneInfo setPosix(final String posix) {
        this.posix = posix;
        return this;
    }
    
    public String getRegion() {
        return this.region;
    }
    
    public TimezoneInfo setRegion(final String region) {
        this.region = region;
        return this;
    }
    
    public TimezoneInfo set(final String s, final Object o) {
        return (TimezoneInfo)super.set(s, o);
    }
    
    public TimezoneInfo clone() {
        return (TimezoneInfo)super.clone();
    }
}
