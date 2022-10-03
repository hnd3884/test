package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class DeviceReport extends GenericJson
{
    @Key
    private List<AppState> appState;
    @Key
    @JsonString
    private Long lastUpdatedTimestampMillis;
    
    public List<AppState> getAppState() {
        return this.appState;
    }
    
    public DeviceReport setAppState(final List<AppState> appState) {
        this.appState = appState;
        return this;
    }
    
    public Long getLastUpdatedTimestampMillis() {
        return this.lastUpdatedTimestampMillis;
    }
    
    public DeviceReport setLastUpdatedTimestampMillis(final Long lastUpdatedTimestampMillis) {
        this.lastUpdatedTimestampMillis = lastUpdatedTimestampMillis;
        return this;
    }
    
    public DeviceReport set(final String fieldName, final Object value) {
        return (DeviceReport)super.set(fieldName, value);
    }
    
    public DeviceReport clone() {
        return (DeviceReport)super.clone();
    }
    
    static {
        Data.nullOf((Class)AppState.class);
    }
}
