package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ManagedConfigurationsSettings extends GenericJson
{
    @Key
    @JsonString
    private Long lastUpdatedTimestampMillis;
    @Key
    private String mcmId;
    @Key
    private String name;
    
    public Long getLastUpdatedTimestampMillis() {
        return this.lastUpdatedTimestampMillis;
    }
    
    public ManagedConfigurationsSettings setLastUpdatedTimestampMillis(final Long lastUpdatedTimestampMillis) {
        this.lastUpdatedTimestampMillis = lastUpdatedTimestampMillis;
        return this;
    }
    
    public String getMcmId() {
        return this.mcmId;
    }
    
    public ManagedConfigurationsSettings setMcmId(final String mcmId) {
        this.mcmId = mcmId;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ManagedConfigurationsSettings setName(final String name) {
        this.name = name;
        return this;
    }
    
    public ManagedConfigurationsSettings set(final String fieldName, final Object value) {
        return (ManagedConfigurationsSettings)super.set(fieldName, value);
    }
    
    public ManagedConfigurationsSettings clone() {
        return (ManagedConfigurationsSettings)super.clone();
    }
}
