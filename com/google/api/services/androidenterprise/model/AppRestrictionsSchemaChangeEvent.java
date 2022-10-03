package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AppRestrictionsSchemaChangeEvent extends GenericJson
{
    @Key
    private String productId;
    
    public String getProductId() {
        return this.productId;
    }
    
    public AppRestrictionsSchemaChangeEvent setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public AppRestrictionsSchemaChangeEvent set(final String fieldName, final Object value) {
        return (AppRestrictionsSchemaChangeEvent)super.set(fieldName, value);
    }
    
    public AppRestrictionsSchemaChangeEvent clone() {
        return (AppRestrictionsSchemaChangeEvent)super.clone();
    }
}
