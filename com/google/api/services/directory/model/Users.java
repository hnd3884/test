package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Users extends GenericJson
{
    @Key
    private String etag;
    @Key
    private String kind;
    @Key
    private String nextPageToken;
    @Key("trigger_event")
    private String triggerEvent;
    @Key
    private List<User> users;
    
    public String getEtag() {
        return this.etag;
    }
    
    public Users setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public Users setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public String getNextPageToken() {
        return this.nextPageToken;
    }
    
    public Users setNextPageToken(final String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }
    
    public String getTriggerEvent() {
        return this.triggerEvent;
    }
    
    public Users setTriggerEvent(final String triggerEvent) {
        this.triggerEvent = triggerEvent;
        return this;
    }
    
    public List<User> getUsers() {
        return this.users;
    }
    
    public Users setUsers(final List<User> users) {
        this.users = users;
        return this;
    }
    
    public Users set(final String fieldName, final Object value) {
        return (Users)super.set(fieldName, value);
    }
    
    public Users clone() {
        return (Users)super.clone();
    }
    
    static {
        Data.nullOf((Class)User.class);
    }
}
