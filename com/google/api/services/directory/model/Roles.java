package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Roles extends GenericJson
{
    @Key
    private String etag;
    @Key
    private List<Role> items;
    @Key
    private String kind;
    @Key
    private String nextPageToken;
    
    public String getEtag() {
        return this.etag;
    }
    
    public Roles setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public List<Role> getItems() {
        return this.items;
    }
    
    public Roles setItems(final List<Role> items) {
        this.items = items;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Roles setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public Roles setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public Roles set(final String fieldName, final Object value) {
        return (Roles)super.set(fieldName, value);
    }
    
    public Roles clone() {
        return (Roles)super.clone();
    }
    
    static {
        Data.nullOf((Class)Role.class);
    }
}
