package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserExternalId extends GenericJson
{
    @Key
    private String customType;
    @Key
    private String type;
    @Key
    private String value;
    
    public String getCustomType() {
        return this.customType;
    }
    
    public UserExternalId setCustomType(final String customType) {
        this.customType = customType;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public UserExternalId setType(final String type) {
        this.type = type;
        return this;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public UserExternalId setValue(final String value) {
        this.value = value;
        return this;
    }
    
    public UserExternalId set(final String fieldName, final Object value) {
        return (UserExternalId)super.set(fieldName, value);
    }
    
    public UserExternalId clone() {
        return (UserExternalId)super.clone();
    }
}
