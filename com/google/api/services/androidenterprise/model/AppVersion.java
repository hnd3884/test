package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AppVersion extends GenericJson
{
    @Key
    private Boolean isProduction;
    @Key
    private String track;
    @Key
    private List<String> trackId;
    @Key
    private Integer versionCode;
    @Key
    private String versionString;
    
    public Boolean getIsProduction() {
        return this.isProduction;
    }
    
    public AppVersion setIsProduction(final Boolean isProduction) {
        this.isProduction = isProduction;
        return this;
    }
    
    public String getTrack() {
        return this.track;
    }
    
    public AppVersion setTrack(final String track) {
        this.track = track;
        return this;
    }
    
    public List<String> getTrackId() {
        return this.trackId;
    }
    
    public AppVersion setTrackId(final List<String> trackId) {
        this.trackId = trackId;
        return this;
    }
    
    public Integer getVersionCode() {
        return this.versionCode;
    }
    
    public AppVersion setVersionCode(final Integer versionCode) {
        this.versionCode = versionCode;
        return this;
    }
    
    public String getVersionString() {
        return this.versionString;
    }
    
    public AppVersion setVersionString(final String versionString) {
        this.versionString = versionString;
        return this;
    }
    
    public AppVersion set(final String fieldName, final Object value) {
        return (AppVersion)super.set(fieldName, value);
    }
    
    public AppVersion clone() {
        return (AppVersion)super.clone();
    }
}
