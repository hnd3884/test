package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ChromeOsDeviceAction extends GenericJson
{
    @Key
    private String action;
    @Key
    private String deprovisionReason;
    
    public String getAction() {
        return this.action;
    }
    
    public ChromeOsDeviceAction setAction(final String action) {
        this.action = action;
        return this;
    }
    
    public String getDeprovisionReason() {
        return this.deprovisionReason;
    }
    
    public ChromeOsDeviceAction setDeprovisionReason(final String deprovisionReason) {
        this.deprovisionReason = deprovisionReason;
        return this;
    }
    
    public ChromeOsDeviceAction set(final String fieldName, final Object value) {
        return (ChromeOsDeviceAction)super.set(fieldName, value);
    }
    
    public ChromeOsDeviceAction clone() {
        return (ChromeOsDeviceAction)super.clone();
    }
}
