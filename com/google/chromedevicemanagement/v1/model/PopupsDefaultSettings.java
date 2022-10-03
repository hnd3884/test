package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class PopupsDefaultSettings extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private String popupsDefaultMode;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public PopupsDefaultSettings setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public String getPopupsDefaultMode() {
        return this.popupsDefaultMode;
    }
    
    public PopupsDefaultSettings setPopupsDefaultMode(final String popupsDefaultMode) {
        this.popupsDefaultMode = popupsDefaultMode;
        return this;
    }
    
    public PopupsDefaultSettings set(final String s, final Object o) {
        return (PopupsDefaultSettings)super.set(s, o);
    }
    
    public PopupsDefaultSettings clone() {
        return (PopupsDefaultSettings)super.clone();
    }
}
