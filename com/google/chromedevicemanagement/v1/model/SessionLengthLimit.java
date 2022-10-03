package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class SessionLengthLimit extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private String sessionLengthLimit;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public SessionLengthLimit setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public String getSessionLengthLimit() {
        return this.sessionLengthLimit;
    }
    
    public SessionLengthLimit setSessionLengthLimit(final String sessionLengthLimit) {
        this.sessionLengthLimit = sessionLengthLimit;
        return this;
    }
    
    public SessionLengthLimit set(final String s, final Object o) {
        return (SessionLengthLimit)super.set(s, o);
    }
    
    public SessionLengthLimit clone() {
        return (SessionLengthLimit)super.clone();
    }
}
