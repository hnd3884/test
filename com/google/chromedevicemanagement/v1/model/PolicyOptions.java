package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class PolicyOptions extends GenericJson
{
    @Key
    private String mode;
    
    public String getMode() {
        return this.mode;
    }
    
    public PolicyOptions setMode(final String mode) {
        this.mode = mode;
        return this;
    }
    
    public PolicyOptions set(final String s, final Object o) {
        return (PolicyOptions)super.set(s, o);
    }
    
    public PolicyOptions clone() {
        return (PolicyOptions)super.clone();
    }
}
