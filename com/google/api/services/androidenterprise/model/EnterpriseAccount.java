package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class EnterpriseAccount extends GenericJson
{
    @Key
    private String accountEmail;
    
    public String getAccountEmail() {
        return this.accountEmail;
    }
    
    public EnterpriseAccount setAccountEmail(final String accountEmail) {
        this.accountEmail = accountEmail;
        return this;
    }
    
    public EnterpriseAccount set(final String fieldName, final Object value) {
        return (EnterpriseAccount)super.set(fieldName, value);
    }
    
    public EnterpriseAccount clone() {
        return (EnterpriseAccount)super.clone();
    }
}
