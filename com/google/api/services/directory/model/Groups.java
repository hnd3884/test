package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Groups extends GenericJson
{
    @Key
    private String etag;
    @Key
    private List<Group> groups;
    @Key
    private String kind;
    @Key
    private String nextPageToken;
    
    public String getEtag() {
        return this.etag;
    }
    
    public Groups setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public List<Group> getGroups() {
        return this.groups;
    }
    
    public Groups setGroups(final List<Group> groups) {
        this.groups = groups;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Groups setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public Groups setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public Groups set(final String fieldName, final Object value) {
        return (Groups)super.set(fieldName, value);
    }
    
    public Groups clone() {
        return (Groups)super.clone();
    }
    
    static {
        Data.nullOf((Class)Group.class);
    }
}
