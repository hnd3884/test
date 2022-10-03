package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Install extends GenericJson
{
    @Key
    private String installState;
    @Key
    private String productId;
    @Key
    private Integer versionCode;
    
    public String getInstallState() {
        return this.installState;
    }
    
    public Install setInstallState(final String installState) {
        this.installState = installState;
        return this;
    }
    
    public String getProductId() {
        return this.productId;
    }
    
    public Install setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public Integer getVersionCode() {
        return this.versionCode;
    }
    
    public Install setVersionCode(final Integer versionCode) {
        this.versionCode = versionCode;
        return this;
    }
    
    public Install set(final String fieldName, final Object value) {
        return (Install)super.set(fieldName, value);
    }
    
    public Install clone() {
        return (Install)super.clone();
    }
}
