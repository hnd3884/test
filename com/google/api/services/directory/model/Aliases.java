package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class Aliases extends GenericJson
{
    @Key
    private List<Object> aliases;
    @Key
    private String etag;
    @Key
    private String kind;
    
    public List<Object> getAliases() {
        return this.aliases;
    }
    
    public Aliases setAliases(final List<Object> aliases) {
        this.aliases = aliases;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Aliases setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Aliases setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Aliases set(final String fieldName, final Object value) {
        return (Aliases)super.set(fieldName, value);
    }
    
    public Aliases clone() {
        return (Aliases)super.clone();
    }
}
