package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class Domains2 extends GenericJson
{
    @Key
    private List<Domains> domains;
    @Key
    private String etag;
    @Key
    private String kind;
    
    public List<Domains> getDomains() {
        return this.domains;
    }
    
    public Domains2 setDomains(final List<Domains> domains) {
        this.domains = domains;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Domains2 setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Domains2 setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Domains2 set(final String fieldName, final Object value) {
        return (Domains2)super.set(fieldName, value);
    }
    
    public Domains2 clone() {
        return (Domains2)super.clone();
    }
    
    static {
        Data.nullOf((Class)Domains.class);
    }
}
