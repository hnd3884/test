package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class DomainAliases extends GenericJson
{
    @Key
    private List<DomainAlias> domainAliases;
    @Key
    private String etag;
    @Key
    private String kind;
    
    public List<DomainAlias> getDomainAliases() {
        return this.domainAliases;
    }
    
    public DomainAliases setDomainAliases(final List<DomainAlias> domainAliases) {
        this.domainAliases = domainAliases;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public DomainAliases setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public DomainAliases setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public DomainAliases set(final String fieldName, final Object value) {
        return (DomainAliases)super.set(fieldName, value);
    }
    
    public DomainAliases clone() {
        return (DomainAliases)super.clone();
    }
    
    static {
        Data.nullOf((Class)DomainAlias.class);
    }
}
