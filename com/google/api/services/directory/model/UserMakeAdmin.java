package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserMakeAdmin extends GenericJson
{
    @Key
    private Boolean status;
    
    public Boolean getStatus() {
        return this.status;
    }
    
    public UserMakeAdmin setStatus(final Boolean status) {
        this.status = status;
        return this;
    }
    
    public UserMakeAdmin set(final String fieldName, final Object value) {
        return (UserMakeAdmin)super.set(fieldName, value);
    }
    
    public UserMakeAdmin clone() {
        return (UserMakeAdmin)super.clone();
    }
}
