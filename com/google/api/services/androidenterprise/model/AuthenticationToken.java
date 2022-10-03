package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AuthenticationToken extends GenericJson
{
    @Key
    private String token;
    
    public String getToken() {
        return this.token;
    }
    
    public AuthenticationToken setToken(final String token) {
        this.token = token;
        return this;
    }
    
    public AuthenticationToken set(final String fieldName, final Object value) {
        return (AuthenticationToken)super.set(fieldName, value);
    }
    
    public AuthenticationToken clone() {
        return (AuthenticationToken)super.clone();
    }
}
