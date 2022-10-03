package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Alias extends GenericJson
{
    @Key
    private String alias;
    @Key
    private String etag;
    @Key
    private String id;
    @Key
    private String kind;
    @Key
    private String primaryEmail;
    
    public String getAlias() {
        return this.alias;
    }
    
    public Alias setAlias(final String alias) {
        this.alias = alias;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Alias setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public Alias setId(final String id) {
        this.id = id;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Alias setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getPrimaryEmail() {
        return this.primaryEmail;
    }
    
    public Alias setPrimaryEmail(final String primaryEmail) {
        this.primaryEmail = primaryEmail;
        return this;
    }
    
    public Alias set(final String fieldName, final Object value) {
        return (Alias)super.set(fieldName, value);
    }
    
    public Alias clone() {
        return (Alias)super.clone();
    }
}
