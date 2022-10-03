package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class Buildings extends GenericJson
{
    @Key
    private List<Building> buildings;
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private String nextPageToken;
    
    public List<Building> getBuildings() {
        return this.buildings;
    }
    
    public Buildings setBuildings(final List<Building> buildings) {
        this.buildings = buildings;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public Buildings setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Buildings setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public Buildings setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public Buildings set(final String fieldName, final Object value) {
        return (Buildings)super.set(fieldName, value);
    }
    
    public Buildings clone() {
        return (Buildings)super.clone();
    }
    
    static {
        Data.nullOf((Class)Building.class);
    }
}
