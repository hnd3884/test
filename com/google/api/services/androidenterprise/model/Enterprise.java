package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class Enterprise extends GenericJson
{
    @Key
    private List<Administrator> administrator;
    @Key
    private String id;
    @Key
    private String name;
    @Key
    private String primaryDomain;
    
    public List<Administrator> getAdministrator() {
        return this.administrator;
    }
    
    public Enterprise setAdministrator(final List<Administrator> administrator) {
        this.administrator = administrator;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public Enterprise setId(final String id) {
        this.id = id;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Enterprise setName(final String name) {
        this.name = name;
        return this;
    }
    
    public String getPrimaryDomain() {
        return this.primaryDomain;
    }
    
    public Enterprise setPrimaryDomain(final String primaryDomain) {
        this.primaryDomain = primaryDomain;
        return this;
    }
    
    public Enterprise set(final String fieldName, final Object value) {
        return (Enterprise)super.set(fieldName, value);
    }
    
    public Enterprise clone() {
        return (Enterprise)super.clone();
    }
    
    static {
        Data.nullOf((Class)Administrator.class);
    }
}
