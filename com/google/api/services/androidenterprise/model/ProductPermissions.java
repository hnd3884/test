package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class ProductPermissions extends GenericJson
{
    @Key
    private List<ProductPermission> permission;
    @Key
    private String productId;
    
    public List<ProductPermission> getPermission() {
        return this.permission;
    }
    
    public ProductPermissions setPermission(final List<ProductPermission> permission) {
        this.permission = permission;
        return this;
    }
    
    public String getProductId() {
        return this.productId;
    }
    
    public ProductPermissions setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public ProductPermissions set(final String fieldName, final Object value) {
        return (ProductPermissions)super.set(fieldName, value);
    }
    
    public ProductPermissions clone() {
        return (ProductPermissions)super.clone();
    }
    
    static {
        Data.nullOf((Class)ProductPermission.class);
    }
}
