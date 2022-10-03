package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class User extends GenericJson
{
    @Key
    private String accountIdentifier;
    @Key
    private String accountType;
    @Key
    private String displayName;
    @Key
    private String id;
    @Key
    private String managementType;
    @Key
    private String primaryEmail;
    
    public String getAccountIdentifier() {
        return this.accountIdentifier;
    }
    
    public User setAccountIdentifier(final String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
        return this;
    }
    
    public String getAccountType() {
        return this.accountType;
    }
    
    public User setAccountType(final String accountType) {
        this.accountType = accountType;
        return this;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public User setDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public User setId(final String id) {
        this.id = id;
        return this;
    }
    
    public String getManagementType() {
        return this.managementType;
    }
    
    public User setManagementType(final String managementType) {
        this.managementType = managementType;
        return this;
    }
    
    public String getPrimaryEmail() {
        return this.primaryEmail;
    }
    
    public User setPrimaryEmail(final String primaryEmail) {
        this.primaryEmail = primaryEmail;
        return this;
    }
    
    public User set(final String fieldName, final Object value) {
        return (User)super.set(fieldName, value);
    }
    
    public User clone() {
        return (User)super.clone();
    }
}
