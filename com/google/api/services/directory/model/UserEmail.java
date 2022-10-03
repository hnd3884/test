package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserEmail extends GenericJson
{
    @Key
    private String address;
    @Key
    private String customType;
    @Key
    private Boolean primary;
    @Key
    private String type;
    
    public String getAddress() {
        return this.address;
    }
    
    public UserEmail setAddress(final String address) {
        this.address = address;
        return this;
    }
    
    public String getCustomType() {
        return this.customType;
    }
    
    public UserEmail setCustomType(final String customType) {
        this.customType = customType;
        return this;
    }
    
    public Boolean getPrimary() {
        return this.primary;
    }
    
    public UserEmail setPrimary(final Boolean primary) {
        this.primary = primary;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public UserEmail setType(final String type) {
        this.type = type;
        return this;
    }
    
    public UserEmail set(final String fieldName, final Object value) {
        return (UserEmail)super.set(fieldName, value);
    }
    
    public UserEmail clone() {
        return (UserEmail)super.clone();
    }
}
