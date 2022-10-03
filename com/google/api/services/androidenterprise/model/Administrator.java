package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Administrator extends GenericJson
{
    @Key
    private String email;
    
    public String getEmail() {
        return this.email;
    }
    
    public Administrator setEmail(final String email) {
        this.email = email;
        return this;
    }
    
    public Administrator set(final String fieldName, final Object value) {
        return (Administrator)super.set(fieldName, value);
    }
    
    public Administrator clone() {
        return (Administrator)super.clone();
    }
}
