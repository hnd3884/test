package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ArcEnabled extends GenericJson
{
    @Key
    private Boolean arcEnabled;
    @Key
    private PolicyOptions policyOptions;
    
    public Boolean getArcEnabled() {
        return this.arcEnabled;
    }
    
    public ArcEnabled setArcEnabled(final Boolean arcEnabled) {
        this.arcEnabled = arcEnabled;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public ArcEnabled setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public ArcEnabled set(final String s, final Object o) {
        return (ArcEnabled)super.set(s, o);
    }
    
    public ArcEnabled clone() {
        return (ArcEnabled)super.clone();
    }
}
