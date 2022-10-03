package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserAttestationEnabled extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private Boolean userAttestationEnabled;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public UserAttestationEnabled setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public Boolean getUserAttestationEnabled() {
        return this.userAttestationEnabled;
    }
    
    public UserAttestationEnabled setUserAttestationEnabled(final Boolean userAttestationEnabled) {
        this.userAttestationEnabled = userAttestationEnabled;
        return this;
    }
    
    public UserAttestationEnabled set(final String s, final Object o) {
        return (UserAttestationEnabled)super.set(s, o);
    }
    
    public UserAttestationEnabled clone() {
        return (UserAttestationEnabled)super.clone();
    }
}
