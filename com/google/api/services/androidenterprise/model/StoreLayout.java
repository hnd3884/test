package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class StoreLayout extends GenericJson
{
    @Key
    private String homepageId;
    @Key
    private String storeLayoutType;
    
    public String getHomepageId() {
        return this.homepageId;
    }
    
    public StoreLayout setHomepageId(final String homepageId) {
        this.homepageId = homepageId;
        return this;
    }
    
    public String getStoreLayoutType() {
        return this.storeLayoutType;
    }
    
    public StoreLayout setStoreLayoutType(final String storeLayoutType) {
        this.storeLayoutType = storeLayoutType;
        return this;
    }
    
    public StoreLayout set(final String fieldName, final Object value) {
        return (StoreLayout)super.set(fieldName, value);
    }
    
    public StoreLayout clone() {
        return (StoreLayout)super.clone();
    }
}
