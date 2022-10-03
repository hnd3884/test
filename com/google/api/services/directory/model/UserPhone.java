package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserPhone extends GenericJson
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
    
    public UserPhone setCustomType(final String customType) {
        this.customType = customType;
        return this;
    }
    
    public Boolean getPrimary() {
        return this.primary;
    }
    
    public UserPhone setPrimary(final Boolean primary) {
        this.primary = primary;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public UserPhone setType(final String type) {
        this.type = type;
        return this;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public UserPhone setValue(final String value) {
        this.value = value;
        return this;
    }
    
    public UserPhone set(final String fieldName, final Object value) {
        return (UserPhone)super.set(fieldName, value);
    }
    
    public UserPhone clone() {
        return (UserPhone)super.clone();
    }
}
