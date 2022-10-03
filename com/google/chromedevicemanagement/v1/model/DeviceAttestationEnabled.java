package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceAttestationEnabled extends GenericJson
{
    @Key
    private Boolean attestationForContentProtectionEnabled;
    @Key
    private Boolean deviceAttestationEnabled;
    
    public Boolean getAttestationForContentProtectionEnabled() {
        return this.attestationForContentProtectionEnabled;
    }
    
    public DeviceAttestationEnabled setAttestationForContentProtectionEnabled(final Boolean attestationForContentProtectionEnabled) {
        this.attestationForContentProtectionEnabled = attestationForContentProtectionEnabled;
        return this;
    }
    
    public Boolean getDeviceAttestationEnabled() {
        return this.deviceAttestationEnabled;
    }
    
    public DeviceAttestationEnabled setDeviceAttestationEnabled(final Boolean deviceAttestationEnabled) {
        this.deviceAttestationEnabled = deviceAttestationEnabled;
        return this;
    }
    
    public DeviceAttestationEnabled set(final String s, final Object o) {
        return (DeviceAttestationEnabled)super.set(s, o);
    }
    
    public DeviceAttestationEnabled clone() {
        return (DeviceAttestationEnabled)super.clone();
    }
}
