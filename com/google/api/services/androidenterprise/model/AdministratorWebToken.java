package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AdministratorWebToken extends GenericJson
{
    @Key
    private String token;
    
    public String getToken() {
        return this.token;
    }
    
    public AdministratorWebToken setToken(final String token) {
        this.token = token;
        return this;
    }
    
    public AdministratorWebToken set(final String fieldName, final Object value) {
        return (AdministratorWebToken)super.set(fieldName, value);
    }
    
    public AdministratorWebToken clone() {
        return (AdministratorWebToken)super.clone();
    }
}
