package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class SystemTimezoneSettings extends GenericJson
{
    @Key
    private String automaticTimezoneDetectionType;
    @Key
    private String systemTimezone;
    
    public String getAutomaticTimezoneDetectionType() {
        return this.automaticTimezoneDetectionType;
    }
    
    public SystemTimezoneSettings setAutomaticTimezoneDetectionType(final String automaticTimezoneDetectionType) {
        this.automaticTimezoneDetectionType = automaticTimezoneDetectionType;
        return this;
    }
    
    public String getSystemTimezone() {
        return this.systemTimezone;
    }
    
    public SystemTimezoneSettings setSystemTimezone(final String systemTimezone) {
        this.systemTimezone = systemTimezone;
        return this;
    }
    
    public SystemTimezoneSettings set(final String s, final Object o) {
        return (SystemTimezoneSettings)super.set(s, o);
    }
    
    public SystemTimezoneSettings clone() {
        return (SystemTimezoneSettings)super.clone();
    }
}
