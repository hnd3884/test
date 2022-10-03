package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DomainAlias extends GenericJson
{
    @Key
    @JsonString
    private Long creationTime;
    @Key
    private String domainAliasName;
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private String parentDomainName;
    @Key
    private Boolean verified;
    
    public Long getCreationTime() {
        return this.creationTime;
    }
    
    public DomainAlias setCreationTime(final Long creationTime) {
        this.creationTime = creationTime;
        return this;
    }
    
    public String getDomainAliasName() {
        return this.domainAliasName;
    }
    
    public DomainAlias setDomainAliasName(final String domainAliasName) {
        this.domainAliasName = domainAliasName;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public DomainAlias setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public DomainAlias setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getParentDomainName() {
        return this.parentDomainName;
    }
    
    public DomainAlias setParentDomainName(final String parentDomainName) {
        this.parentDomainName = parentDomainName;
        return this;
    }
    
    public Boolean getVerified() {
        return this.verified;
    }
    
    public DomainAlias setVerified(final Boolean verified) {
        this.verified = verified;
        return this;
    }
    
    public DomainAlias set(final String fieldName, final Object value) {
        return (DomainAlias)super.set(fieldName, value);
    }
    
    public DomainAlias clone() {
        return (DomainAlias)super.clone();
    }
}
