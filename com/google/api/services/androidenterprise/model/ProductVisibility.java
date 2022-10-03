package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ProductVisibility extends GenericJson
{
    @Key
    private String productId;
    @Key
    private List<String> trackIds;
    @Key
    private List<String> tracks;
    
    public String getProductId() {
        return this.productId;
    }
    
    public ProductVisibility setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public List<String> getTrackIds() {
        return this.trackIds;
    }
    
    public ProductVisibility setTrackIds(final List<String> trackIds) {
        this.trackIds = trackIds;
        return this;
    }
    
    public List<String> getTracks() {
        return this.tracks;
    }
    
    public ProductVisibility setTracks(final List<String> tracks) {
        this.tracks = tracks;
        return this;
    }
    
    public ProductVisibility set(final String fieldName, final Object value) {
        return (ProductVisibility)super.set(fieldName, value);
    }
    
    public ProductVisibility clone() {
        return (ProductVisibility)super.clone();
    }
}
