package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ServiceAccountKey extends GenericJson
{
    @Key
    private String data;
    @Key
    private String id;
    @Key
    private String publicData;
    @Key
    private String type;
    
    public String getData() {
        return this.data;
    }
    
    public ServiceAccountKey setData(final String data) {
        this.data = data;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public ServiceAccountKey setId(final String id) {
        this.id = id;
        return this;
    }
    
    public String getPublicData() {
        return this.publicData;
    }
    
    public ServiceAccountKey setPublicData(final String publicData) {
        this.publicData = publicData;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public ServiceAccountKey setType(final String type) {
        this.type = type;
        return this;
    }
    
    public ServiceAccountKey set(final String fieldName, final Object value) {
        return (ServiceAccountKey)super.set(fieldName, value);
    }
    
    public ServiceAccountKey clone() {
        return (ServiceAccountKey)super.clone();
    }
}
