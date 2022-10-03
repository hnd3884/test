package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceState extends GenericJson
{
    @Key
    private String accountState;
    
    public String getAccountState() {
        return this.accountState;
    }
    
    public DeviceState setAccountState(final String accountState) {
        this.accountState = accountState;
        return this;
    }
    
    public DeviceState set(final String fieldName, final Object value) {
        return (DeviceState)super.set(fieldName, value);
    }
    
    public DeviceState clone() {
        return (DeviceState)super.clone();
    }
}
