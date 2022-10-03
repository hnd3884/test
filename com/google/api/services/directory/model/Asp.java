package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Asp extends GenericJson
{
    @Key
    private Integer codeId;
    @Key
    @JsonString
    private Long creationTime;
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    @JsonString
    private Long lastTimeUsed;
    @Key
    private String name;
    @Key
    private String userKey;
    
    public Integer getCodeId() {
        return this.codeId;
    }
    
    public Asp setCodeId(final Integer codeId) {
        this.codeId = codeId;
        return this;
    }
    
    public Long getCreationTime() {
        return this.creationTime;
    }
    
    public Asp setCreationTime(final Long creationTime) {
        this.creationTime = creationTime;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Asp setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Asp setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Long getLastTimeUsed() {
        return this.lastTimeUsed;
    }
    
    public Asp setLastTimeUsed(final Long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Asp setName(final String name) {
        this.name = name;
        return this;
    }
    
    public String getUserKey() {
        return this.userKey;
    }
    
    public Asp setUserKey(final String userKey) {
        this.userKey = userKey;
        return this;
    }
    
    public Asp set(final String fieldName, final Object value) {
        return (Asp)super.set(fieldName, value);
    }
    
    public Asp clone() {
        return (Asp)super.clone();
    }
}
