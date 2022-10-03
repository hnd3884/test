package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AllowToUpdateDeviceAttribute extends GenericJson
{
    @Key
    private Boolean allowToUpdateDeviceAttributes;
    
    public Boolean getAllowToUpdateDeviceAttributes() {
        return this.allowToUpdateDeviceAttributes;
    }
    
    public AllowToUpdateDeviceAttribute setAllowToUpdateDeviceAttributes(final Boolean allowToUpdateDeviceAttributes) {
        this.allowToUpdateDeviceAttributes = allowToUpdateDeviceAttributes;
        return this;
    }
    
    public AllowToUpdateDeviceAttribute set(final String s, final Object o) {
        return (AllowToUpdateDeviceAttribute)super.set(s, o);
    }
    
    public AllowToUpdateDeviceAttribute clone() {
        return (AllowToUpdateDeviceAttribute)super.clone();
    }
}
