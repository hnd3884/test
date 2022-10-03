package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class SafeBrowsingEnabled extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private String safeBrowsingEnabledMode;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public SafeBrowsingEnabled setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public String getSafeBrowsingEnabledMode() {
        return this.safeBrowsingEnabledMode;
    }
    
    public SafeBrowsingEnabled setSafeBrowsingEnabledMode(final String safeBrowsingEnabledMode) {
        this.safeBrowsingEnabledMode = safeBrowsingEnabledMode;
        return this;
    }
    
    public SafeBrowsingEnabled set(final String s, final Object o) {
        return (SafeBrowsingEnabled)super.set(s, o);
    }
    
    public SafeBrowsingEnabled clone() {
        return (SafeBrowsingEnabled)super.clone();
    }
}
