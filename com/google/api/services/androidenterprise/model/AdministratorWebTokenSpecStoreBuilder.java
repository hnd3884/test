package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AdministratorWebTokenSpecStoreBuilder extends GenericJson
{
    @Key
    private Boolean enabled;
    
    public Boolean getEnabled() {
        return this.enabled;
    }
    
    public AdministratorWebTokenSpecStoreBuilder setEnabled(final Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public AdministratorWebTokenSpecStoreBuilder set(final String fieldName, final Object value) {
        return (AdministratorWebTokenSpecStoreBuilder)super.set(fieldName, value);
    }
    
    public AdministratorWebTokenSpecStoreBuilder clone() {
        return (AdministratorWebTokenSpecStoreBuilder)super.clone();
    }
}
