package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class ServiceAccountKeysListResponse extends GenericJson
{
    @Key
    private List<ServiceAccountKey> serviceAccountKey;
    
    public List<ServiceAccountKey> getServiceAccountKey() {
        return this.serviceAccountKey;
    }
    
    public ServiceAccountKeysListResponse setServiceAccountKey(final List<ServiceAccountKey> serviceAccountKey) {
        this.serviceAccountKey = serviceAccountKey;
        return this;
    }
    
    public ServiceAccountKeysListResponse set(final String fieldName, final Object value) {
        return (ServiceAccountKeysListResponse)super.set(fieldName, value);
    }
    
    public ServiceAccountKeysListResponse clone() {
        return (ServiceAccountKeysListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)ServiceAccountKey.class);
    }
}
