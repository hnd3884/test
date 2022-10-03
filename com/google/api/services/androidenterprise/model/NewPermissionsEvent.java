package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class NewPermissionsEvent extends GenericJson
{
    @Key
    private List<String> approvedPermissions;
    @Key
    private String productId;
    @Key
    private List<String> requestedPermissions;
    
    public List<String> getApprovedPermissions() {
        return this.approvedPermissions;
    }
    
    public NewPermissionsEvent setApprovedPermissions(final List<String> approvedPermissions) {
        this.approvedPermissions = approvedPermissions;
        return this;
    }
    
    public String getProductId() {
        return this.productId;
    }
    
    public NewPermissionsEvent setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public List<String> getRequestedPermissions() {
        return this.requestedPermissions;
    }
    
    public NewPermissionsEvent setRequestedPermissions(final List<String> requestedPermissions) {
        this.requestedPermissions = requestedPermissions;
        return this;
    }
    
    public NewPermissionsEvent set(final String fieldName, final Object value) {
        return (NewPermissionsEvent)super.set(fieldName, value);
    }
    
    public NewPermissionsEvent clone() {
        return (NewPermissionsEvent)super.clone();
    }
}
