package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserUndelete extends GenericJson
{
    @Key
    private String orgUnitPath;
    
    public String getOrgUnitPath() {
        return this.orgUnitPath;
    }
    
    public UserUndelete setOrgUnitPath(final String orgUnitPath) {
        this.orgUnitPath = orgUnitPath;
        return this;
    }
    
    public UserUndelete set(final String fieldName, final Object value) {
        return (UserUndelete)super.set(fieldName, value);
    }
    
    public UserUndelete clone() {
        return (UserUndelete)super.clone();
    }
}
