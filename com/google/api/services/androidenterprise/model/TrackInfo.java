package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class TrackInfo extends GenericJson
{
    @Key
    private String trackAlias;
    @Key
    private String trackId;
    
    public String getTrackAlias() {
        return this.trackAlias;
    }
    
    public TrackInfo setTrackAlias(final String trackAlias) {
        this.trackAlias = trackAlias;
        return this;
    }
    
    public String getTrackId() {
        return this.trackId;
    }
    
    public TrackInfo setTrackId(final String trackId) {
        this.trackId = trackId;
        return this;
    }
    
    public TrackInfo set(final String fieldName, final Object value) {
        return (TrackInfo)super.set(fieldName, value);
    }
    
    public TrackInfo clone() {
        return (TrackInfo)super.clone();
    }
}
