package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserWebsite extends GenericJson
{
    @Key
    private String customType;
    @Key
    private Boolean primary;
    @Key
    private String type;
    @Key
    private String value;
    
    public String getCustomType() {
        return this.customType;
    }
    
    public UserWebsite setCustomType(final String customType) {
        this.customType = customType;
        return this;
    }
    
    public Boolean getPrimary() {
        return this.primary;
    }
    
    public UserWebsite setPrimary(final Boolean primary) {
        this.primary = primary;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public UserWebsite setType(final String type) {
        this.type = type;
        return this;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public UserWebsite setValue(final String value) {
        this.value = value;
        return this;
    }
    
    public UserWebsite set(final String fieldName, final Object value) {
        return (UserWebsite)super.set(fieldName, value);
    }
    
    public UserWebsite clone() {
        return (UserWebsite)super.clone();
    }
}
