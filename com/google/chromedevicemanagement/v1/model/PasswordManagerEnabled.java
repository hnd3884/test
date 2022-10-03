package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class PasswordManagerEnabled extends GenericJson
{
    @Key
    private String enabledMode;
    @Key
    private PolicyOptions policyOptions;
    
    public String getEnabledMode() {
        return this.enabledMode;
    }
    
    public PasswordManagerEnabled setEnabledMode(final String enabledMode) {
        this.enabledMode = enabledMode;
        return this;
    }
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public PasswordManagerEnabled setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public PasswordManagerEnabled set(final String s, final Object o) {
        return (PasswordManagerEnabled)super.set(s, o);
    }
    
    public PasswordManagerEnabled clone() {
        return (PasswordManagerEnabled)super.clone();
    }
}
