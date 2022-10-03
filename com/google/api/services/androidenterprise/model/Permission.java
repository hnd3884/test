package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Permission extends GenericJson
{
    @Key
    private String description;
    @Key
    private String name;
    @Key
    private String permissionId;
    
    public String getDescription() {
        return this.description;
    }
    
    public Permission setDescription(final String description) {
        this.description = description;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Permission setName(final String name) {
        this.name = name;
        return this;
    }
    
    public String getPermissionId() {
        return this.permissionId;
    }
    
    public Permission setPermissionId(final String permissionId) {
        this.permissionId = permissionId;
        return this;
    }
    
    public Permission set(final String fieldName, final Object value) {
        return (Permission)super.set(fieldName, value);
    }
    
    public Permission clone() {
        return (Permission)super.clone();
    }
}
