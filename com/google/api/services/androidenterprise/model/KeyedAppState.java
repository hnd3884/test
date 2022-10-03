package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class KeyedAppState extends GenericJson
{
    @Key
    private String data;
    @Key
    private String key;
    @Key
    private String message;
    @Key
    private String severity;
    @Key
    @JsonString
    private Long stateTimestampMillis;
    
    public String getData() {
        return this.data;
    }
    
    public KeyedAppState setData(final String data) {
        this.data = data;
        return this;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public KeyedAppState setKey(final String key) {
        this.key = key;
        return this;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public KeyedAppState setMessage(final String message) {
        this.message = message;
        return this;
    }
    
    public String getSeverity() {
        return this.severity;
    }
    
    public KeyedAppState setSeverity(final String severity) {
        this.severity = severity;
        return this;
    }
    
    public Long getStateTimestampMillis() {
        return this.stateTimestampMillis;
    }
    
    public KeyedAppState setStateTimestampMillis(final Long stateTimestampMillis) {
        this.stateTimestampMillis = stateTimestampMillis;
        return this;
    }
    
    public KeyedAppState set(final String fieldName, final Object value) {
        return (KeyedAppState)super.set(fieldName, value);
    }
    
    public KeyedAppState clone() {
        return (KeyedAppState)super.clone();
    }
}
