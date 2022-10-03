package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class PowerManagementIdleSettings extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private String powerManagementIdleSettings;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public PowerManagementIdleSettings setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public String getPowerManagementIdleSettings() {
        return this.powerManagementIdleSettings;
    }
    
    public PowerManagementIdleSettings setPowerManagementIdleSettings(final String powerManagementIdleSettings) {
        this.powerManagementIdleSettings = powerManagementIdleSettings;
        return this;
    }
    
    public PowerManagementIdleSettings set(final String s, final Object o) {
        return (PowerManagementIdleSettings)super.set(s, o);
    }
    
    public PowerManagementIdleSettings clone() {
        return (PowerManagementIdleSettings)super.clone();
    }
}
