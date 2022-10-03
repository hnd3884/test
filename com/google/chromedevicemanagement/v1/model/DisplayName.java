package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DisplayName extends GenericJson
{
    @Key
    private String displayName;
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public DisplayName setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public DisplayName set(final String s, final Object o) {
        return (DisplayName)super.set(s, o);
    }
    
    public DisplayName clone() {
        return (DisplayName)super.clone();
    }
}
