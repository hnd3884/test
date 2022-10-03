package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DisableSafeBrowsingProceedAnyway extends GenericJson
{
    @Key
    private Boolean disableSafeBrowsingProceedAnyway;
    @Key
    private PolicyOptions policyOptions;
    
    public Boolean getDisableSafeBrowsingProceedAnyway() {
        return this.disableSafeBrowsingProceedAnyway;
    }
    
    public DisableSafeBrowsingProceedAnyway setDisableSafeBrowsingProceedAnyway(final Boolean disableSafeBrowsingProceedAnyway) {
        this.disableSafeBrowsingProceedAnyway = disableSafeBrowsingProceedAnyway;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public DisableSafeBrowsingProceedAnyway setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public DisableSafeBrowsingProceedAnyway set(final String s, final Object o) {
        return (DisableSafeBrowsingProceedAnyway)super.set(s, o);
    }
    
    public DisableSafeBrowsingProceedAnyway clone() {
        return (DisableSafeBrowsingProceedAnyway)super.clone();
    }
}
