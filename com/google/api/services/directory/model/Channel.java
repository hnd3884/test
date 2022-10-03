package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import java.util.Map;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Channel extends GenericJson
{
    @Key
    private String address;
    @Key
    @JsonString
    private Long expiration;
    @Key
    private String id;
    @Key
    private String kind;
    @Key
    private Map<String, String> params;
    @Key
    private Boolean payload;
    @Key
    private String resourceId;
    @Key
    private String resourceUri;
    @Key
    private String token;
    @Key
    private String type;
    
    public String getAddress() {
        return this.address;
    }
    
    public Channel setAddress(final String address) {
        this.address = address;
        return this;
    }
    
    public Long getExpiration() {
        return this.expiration;
    }
    
    public Channel setExpiration(final Long expiration) {
        this.expiration = expiration;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public Channel setId(final String id) {
        this.id = id;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Channel setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Map<String, String> getParams() {
        return this.params;
    }
    
    public Channel setParams(final Map<String, String> params) {
        this.params = params;
        return this;
    }
    
    public Boolean getPayload() {
        return this.payload;
    }
    
    public Channel setPayload(final Boolean payload) {
        this.payload = payload;
        return this;
    }
    
    public String getResourceId() {
        return this.resourceId;
    }
    
    public Channel setResourceId(final String resourceId) {
        this.resourceId = resourceId;
        return this;
    }
    
    public String getResourceUri() {
        return this.resourceUri;
    }
    
    public Channel setResourceUri(final String resourceUri) {
        this.resourceUri = resourceUri;
        return this;
    }
    
    public String getToken() {
        return this.token;
    }
    
    public Channel setToken(final String token) {
        this.token = token;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public Channel setType(final String type) {
        this.type = type;
        return this;
    }
    
    public Channel set(final String fieldName, final Object value) {
        return (Channel)super.set(fieldName, value);
    }
    
    public Channel clone() {
        return (Channel)super.clone();
    }
}
