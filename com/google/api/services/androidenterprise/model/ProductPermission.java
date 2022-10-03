package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ProductPermission extends GenericJson
{
    @Key
    private String permissionId;
    @Key
    private String state;
    
    public String getPermissionId() {
        return this.permissionId;
    }
    
    public ProductPermission setPermissionId(final String permissionId) {
        this.permissionId = permissionId;
        return this;
    }
    
    public String getState() {
        return this.state;
    }
    
    public ProductPermission setState(final String state) {
        this.state = state;
        return this;
    }
    
    public ProductPermission set(final String fieldName, final Object value) {
        return (ProductPermission)super.set(fieldName, value);
    }
    
    public ProductPermission clone() {
        return (ProductPermission)super.clone();
    }
}
