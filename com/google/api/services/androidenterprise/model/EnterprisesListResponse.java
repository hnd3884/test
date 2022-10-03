package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class EnterprisesListResponse extends GenericJson
{
    @Key
    private List<Enterprise> enterprise;
    
    public List<Enterprise> getEnterprise() {
        return this.enterprise;
    }
    
    public EnterprisesListResponse setEnterprise(final List<Enterprise> enterprise) {
        this.enterprise = enterprise;
        return this;
    }
    
    public EnterprisesListResponse set(final String fieldName, final Object value) {
        return (EnterprisesListResponse)super.set(fieldName, value);
    }
    
    public EnterprisesListResponse clone() {
        return (EnterprisesListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)Enterprise.class);
    }
}
