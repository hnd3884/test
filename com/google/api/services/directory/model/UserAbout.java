package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserAbout extends GenericJson
{
    @Key
    private String contentType;
    @Key
    private String value;
    
    public String getContentType() {
        return this.contentType;
    }
    
    public UserAbout setContentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public UserAbout setValue(final String value) {
        this.value = value;
        return this;
    }
    
    public UserAbout set(final String fieldName, final Object value) {
        return (UserAbout)super.set(fieldName, value);
    }
    
    public UserAbout clone() {
        return (UserAbout)super.clone();
    }
}
