package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Domains extends GenericJson
{
    @Key
    @JsonString
    private Long creationTime;
    @Key
    private List<DomainAlias> domainAliases;
    @Key
    private String domainName;
    @Key
    private String etag;
    @Key
    private Boolean isPrimary;
    @Key
    private String kind;
    @Key
    private Boolean verified;
    
    public Long getCreationTime() {
        return this.creationTime;
    }
    
    public Domains setCreationTime(final Long creationTime) {
        this.creationTime = creationTime;
        return this;
    }
    
    public List<DomainAlias> getDomainAliases() {
        return this.domainAliases;
    }
    
    public Domains setDomainAliases(final List<DomainAlias> domainAliases) {
        this.domainAliases = domainAliases;
        return this;
    }
    
    public String getDomainName() {
        return this.domainName;
    }
    
    public Domains setDomainName(final String domainName) {
        this.domainName = domainName;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Domains setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public Boolean getIsPrimary() {
        return this.isPrimary;
    }
    
    public Domains setIsPrimary(final Boolean isPrimary) {
        this.isPrimary = isPrimary;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Domains setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Boolean getVerified() {
        return this.verified;
    }
    
    public Domains setVerified(final Boolean verified) {
        this.verified = verified;
        return this;
    }
    
    public Domains set(final String fieldName, final Object value) {
        return (Domains)super.set(fieldName, value);
    }
    
    public Domains clone() {
        return (Domains)super.clone();
    }
    
    static {
        Data.nullOf((Class)DomainAlias.class);
    }
}
