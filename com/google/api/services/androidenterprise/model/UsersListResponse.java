package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class UsersListResponse extends GenericJson
{
    @Key
    private List<User> user;
    
    public List<User> getUser() {
        return this.user;
    }
    
    public UsersListResponse setUser(final List<User> user) {
        this.user = user;
        return this;
    }
    
    public UsersListResponse set(final String fieldName, final Object value) {
        return (UsersListResponse)super.set(fieldName, value);
    }
    
    public UsersListResponse clone() {
        return (UsersListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)User.class);
    }
}
