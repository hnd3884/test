package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class AppState extends GenericJson
{
    @Key
    private List<KeyedAppState> keyedAppState;
    @Key
    private String packageName;
    
    public List<KeyedAppState> getKeyedAppState() {
        return this.keyedAppState;
    }
    
    public AppState setKeyedAppState(final List<KeyedAppState> keyedAppState) {
        this.keyedAppState = keyedAppState;
        return this;
    }
    
    public String getPackageName() {
        return this.packageName;
    }
    
    public AppState setPackageName(final String packageName) {
        this.packageName = packageName;
        return this;
    }
    
    public AppState set(final String fieldName, final Object value) {
        return (AppState)super.set(fieldName, value);
    }
    
    public AppState clone() {
        return (AppState)super.clone();
    }
}
