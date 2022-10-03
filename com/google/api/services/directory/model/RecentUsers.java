package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class RecentUsers extends GenericJson
{
    @Key
    private String email;
    @Key
    private String type;
    
    public String getEmail() {
        return this.email;
    }
    
    public RecentUsers setEmail(final String email) {
        this.email = email;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public RecentUsers setType(final String type) {
        this.type = type;
        return this;
    }
    
    public RecentUsers set(final String fieldName, final Object value) {
        return (RecentUsers)super.set(fieldName, value);
    }
    
    public RecentUsers clone() {
        return (RecentUsers)super.clone();
    }
}
