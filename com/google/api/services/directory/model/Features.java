package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Features extends GenericJson
{
    @Key
    private String etag;
    @Key
    private List<Feature> features;
    @Key
    private String kind;
    @Key
    private String nextPageToken;
    
    public String getEtag() {
        return this.etag;
    }
    
    public Features setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public List<Feature> getFeatures() {
        return this.features;
    }
    
    public Features setFeatures(final List<Feature> features) {
        this.features = features;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Features setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public Features setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public Features set(final String fieldName, final Object value) {
        return (Features)super.set(fieldName, value);
    }
    
    public Features clone() {
        return (Features)super.clone();
    }
    
    static {
        Data.nullOf((Class)Feature.class);
    }
}
