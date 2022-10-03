package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class IncognitoModeAvailability extends GenericJson
{
    @Key
    private String incognitoModeAvailability;
    @Key
    private PolicyOptions policyOptions;
    
    public String getIncognitoModeAvailability() {
        return this.incognitoModeAvailability;
    }
    
    public IncognitoModeAvailability setIncognitoModeAvailability(final String incognitoModeAvailability) {
        this.incognitoModeAvailability = incognitoModeAvailability;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public IncognitoModeAvailability setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public IncognitoModeAvailability set(final String s, final Object o) {
        return (IncognitoModeAvailability)super.set(s, o);
    }
    
    public IncognitoModeAvailability clone() {
        return (IncognitoModeAvailability)super.clone();
    }
}
