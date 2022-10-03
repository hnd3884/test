package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ShowHomeButton extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private String showHomeButtonMode;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public ShowHomeButton setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public String getShowHomeButtonMode() {
        return this.showHomeButtonMode;
    }
    
    public ShowHomeButton setShowHomeButtonMode(final String showHomeButtonMode) {
        this.showHomeButtonMode = showHomeButtonMode;
        return this;
    }
    
    public ShowHomeButton set(final String s, final Object o) {
        return (ShowHomeButton)super.set(s, o);
    }
    
    public ShowHomeButton clone() {
        return (ShowHomeButton)super.clone();
    }
}
