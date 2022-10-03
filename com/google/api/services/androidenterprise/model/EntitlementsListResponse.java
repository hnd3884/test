package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class EntitlementsListResponse extends GenericJson
{
    @Key
    private List<Entitlement> entitlement;
    
    public List<Entitlement> getEntitlement() {
        return this.entitlement;
    }
    
    public EntitlementsListResponse setEntitlement(final List<Entitlement> entitlement) {
        this.entitlement = entitlement;
        return this;
    }
    
    public EntitlementsListResponse set(final String fieldName, final Object value) {
        return (EntitlementsListResponse)super.set(fieldName, value);
    }
    
    public EntitlementsListResponse clone() {
        return (EntitlementsListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)Entitlement.class);
    }
}
