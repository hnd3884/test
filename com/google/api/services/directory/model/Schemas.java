package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Schemas extends GenericJson
{
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private List<Schema> schemas;
    
    public String getEtag() {
        return this.etag;
    }
    
    public Schemas setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Schemas setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public List<Schema> getSchemas() {
        return this.schemas;
    }
    
    public Schemas setSchemas(final List<Schema> schemas) {
        this.schemas = schemas;
        return this;
    }
    
    public Schemas set(final String fieldName, final Object value) {
        return (Schemas)super.set(fieldName, value);
    }
    
    public Schemas clone() {
        return (Schemas)super.clone();
    }
    
    static {
        Data.nullOf((Class)Schema.class);
    }
}
