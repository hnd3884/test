package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Asps extends GenericJson
{
    @Key
    private String etag;
    @Key
    private List<Asp> items;
    @Key
    private String kind;
    
    public String getEtag() {
        return this.etag;
    }
    
    public Asps setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public List<Asp> getItems() {
        return this.items;
    }
    
    public Asps setItems(final List<Asp> items) {
        this.items = items;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Asps setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Asps set(final String fieldName, final Object value) {
        return (Asps)super.set(fieldName, value);
    }
    
    public Asps clone() {
        return (Asps)super.clone();
    }
    
    static {
        Data.nullOf((Class)Asp.class);
    }
}
