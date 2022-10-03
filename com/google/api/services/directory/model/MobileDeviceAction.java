package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class MobileDeviceAction extends GenericJson
{
    @Key
    private String action;
    
    public String getAction() {
        return this.action;
    }
    
    public MobileDeviceAction setAction(final String action) {
        this.action = action;
        return this;
    }
    
    public MobileDeviceAction set(final String fieldName, final Object value) {
        return (MobileDeviceAction)super.set(fieldName, value);
    }
    
    public MobileDeviceAction clone() {
        return (MobileDeviceAction)super.clone();
    }
}
