package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Privileges extends GenericJson
{
    @Key
    private String etag;
    @Key
    private List<Privilege> items;
    @Key
    private String kind;
    
    public String getEtag() {
        return this.etag;
    }
    
    public Privileges setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public List<Privilege> getItems() {
        return this.items;
    }
    
    public Privileges setItems(final List<Privilege> items) {
        this.items = items;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Privileges setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Privileges set(final String fieldName, final Object value) {
        return (Privileges)super.set(fieldName, value);
    }
    
    public Privileges clone() {
        return (Privileges)super.clone();
    }
    
    static {
        Data.nullOf((Class)Privilege.class);
    }
}
