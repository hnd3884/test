package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class CalendarResources extends GenericJson
{
    @Key
    private String etag;
    @Key
    private List<CalendarResource> items;
    @Key
    private String kind;
    @Key
    private String nextPageToken;
    
    public String getEtag() {
        return this.etag;
    }
    
    public CalendarResources setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public List<CalendarResource> getItems() {
        return this.items;
    }
    
    public CalendarResources setItems(final List<CalendarResource> items) {
        this.items = items;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public CalendarResources setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public CalendarResources setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public CalendarResources set(final String fieldName, final Object value) {
        return (CalendarResources)super.set(fieldName, value);
    }
    
    public CalendarResources clone() {
        return (CalendarResources)super.clone();
    }
    
    static {
        Data.nullOf((Class)CalendarResource.class);
    }
}
