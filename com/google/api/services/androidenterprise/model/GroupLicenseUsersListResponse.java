package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class GroupLicenseUsersListResponse extends GenericJson
{
    @Key
    private List<User> user;
    
    public List<User> getUser() {
        return this.user;
    }
    
    public GroupLicenseUsersListResponse setUser(final List<User> user) {
        this.user = user;
        return this;
    }
    
    public GroupLicenseUsersListResponse set(final String fieldName, final Object value) {
        return (GroupLicenseUsersListResponse)super.set(fieldName, value);
    }
    
    public GroupLicenseUsersListResponse clone() {
        return (GroupLicenseUsersListResponse)super.clone();
    }
}
