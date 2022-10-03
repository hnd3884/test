package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AutoInstallConstraint extends GenericJson
{
    @Key
    private String chargingStateConstraint;
    @Key
    private String deviceIdleStateConstraint;
    @Key
    private String networkTypeConstraint;
    
    public String getChargingStateConstraint() {
        return this.chargingStateConstraint;
    }
    
    public AutoInstallConstraint setChargingStateConstraint(final String chargingStateConstraint) {
        this.chargingStateConstraint = chargingStateConstraint;
        return this;
    }
    
    public String getDeviceIdleStateConstraint() {
        return this.deviceIdleStateConstraint;
    }
    
    public AutoInstallConstraint setDeviceIdleStateConstraint(final String deviceIdleStateConstraint) {
        this.deviceIdleStateConstraint = deviceIdleStateConstraint;
        return this;
    }
    
    public String getNetworkTypeConstraint() {
        return this.networkTypeConstraint;
    }
    
    public AutoInstallConstraint setNetworkTypeConstraint(final String networkTypeConstraint) {
        this.networkTypeConstraint = networkTypeConstraint;
        return this;
    }
    
    public AutoInstallConstraint set(final String fieldName, final Object value) {
        return (AutoInstallConstraint)super.set(fieldName, value);
    }
    
    public AutoInstallConstraint clone() {
        return (AutoInstallConstraint)super.clone();
    }
}
