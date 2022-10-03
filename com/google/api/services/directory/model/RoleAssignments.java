package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class RoleAssignments extends GenericJson
{
    @Key
    private String etag;
    @Key
    private List<RoleAssignment> items;
    @Key
    private String kind;
    @Key
    private String nextPageToken;
    
    public String getEtag() {
        return this.etag;
    }
    
    public RoleAssignments setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public List<RoleAssignment> getItems() {
        return this.items;
    }
    
    public RoleAssignments setItems(final List<RoleAssignment> items) {
        this.items = items;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public RoleAssignments setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public RoleAssignments setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public RoleAssignments set(final String fieldName, final Object value) {
        return (RoleAssignments)super.set(fieldName, value);
    }
    
    public RoleAssignments clone() {
        return (RoleAssignments)super.clone();
    }
    
    static {
        Data.nullOf((Class)RoleAssignment.class);
    }
}
