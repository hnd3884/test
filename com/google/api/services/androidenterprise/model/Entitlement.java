package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Entitlement extends GenericJson
{
    @Key
    private String productId;
    @Key
    private String reason;
    
    public String getProductId() {
        return this.productId;
    }
    
    public Entitlement setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public Entitlement setReason(final String reason) {
        this.reason = reason;
        return this;
    }
    
    public Entitlement set(final String fieldName, final Object value) {
        return (Entitlement)super.set(fieldName, value);
    }
    
    public Entitlement clone() {
        return (Entitlement)super.clone();
    }
}
