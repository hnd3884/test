package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AppUpdateEvent extends GenericJson
{
    @Key
    private String productId;
    
    public String getProductId() {
        return this.productId;
    }
    
    public AppUpdateEvent setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public AppUpdateEvent set(final String fieldName, final Object value) {
        return (AppUpdateEvent)super.set(fieldName, value);
    }
    
    public AppUpdateEvent clone() {
        return (AppUpdateEvent)super.clone();
    }
}
