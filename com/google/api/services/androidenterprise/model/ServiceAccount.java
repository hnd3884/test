package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ServiceAccount extends GenericJson
{
    @Key
    private ServiceAccountKey key;
    @Key
    private String name;
    
    public ServiceAccountKey getKey() {
        return this.key;
    }
    
    public ServiceAccount setKey(final ServiceAccountKey key) {
        this.key = key;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ServiceAccount setName(final String name) {
        this.name = name;
        return this;
    }
    
    public ServiceAccount set(final String fieldName, final Object value) {
        return (ServiceAccount)super.set(fieldName, value);
    }
    
    public ServiceAccount clone() {
        return (ServiceAccount)super.clone();
    }
}
