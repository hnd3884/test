package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserName extends GenericJson
{
    @Key
    private String familyName;
    @Key
    private String fullName;
    @Key
    private String givenName;
    
    public String getFamilyName() {
        return this.familyName;
    }
    
    public UserName setFamilyName(final String familyName) {
        this.familyName = familyName;
        return this;
    }
    
    public String getFullName() {
        return this.fullName;
    }
    
    public UserName setFullName(final String fullName) {
        this.fullName = fullName;
        return this;
    }
    
    public String getGivenName() {
        return this.givenName;
    }
    
    public UserName setGivenName(final String givenName) {
        this.givenName = givenName;
        return this;
    }
    
    public UserName set(final String fieldName, final Object value) {
        return (UserName)super.set(fieldName, value);
    }
    
    public UserName clone() {
        return (UserName)super.clone();
    }
}
