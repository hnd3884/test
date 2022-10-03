package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Tokens extends GenericJson
{
    @Key
    private String etag;
    @Key
    private List<Token> items;
    @Key
    private String kind;
    
    public String getEtag() {
        return this.etag;
    }
    
    public Tokens setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public List<Token> getItems() {
        return this.items;
    }
    
    public Tokens setItems(final List<Token> items) {
        this.items = items;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Tokens setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Tokens set(final String fieldName, final Object value) {
        return (Tokens)super.set(fieldName, value);
    }
    
    public Tokens clone() {
        return (Tokens)super.clone();
    }
    
    static {
        Data.nullOf((Class)Token.class);
    }
}
