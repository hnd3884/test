package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class CrostiniAllowed extends GenericJson
{
    @Key
    private Boolean crostiniAllowed;
    @Key
    private PolicyOptions policyOptions;
    
    public Boolean getCrostiniAllowed() {
        return this.crostiniAllowed;
    }
    
    public CrostiniAllowed setCrostiniAllowed(final Boolean crostiniAllowed) {
        this.crostiniAllowed = crostiniAllowed;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public CrostiniAllowed setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public CrostiniAllowed set(final String s, final Object o) {
        return (CrostiniAllowed)super.set(s, o);
    }
    
    public CrostiniAllowed clone() {
        return (CrostiniAllowed)super.clone();
    }
}
