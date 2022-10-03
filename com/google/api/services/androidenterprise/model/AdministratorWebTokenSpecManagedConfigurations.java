package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AdministratorWebTokenSpecManagedConfigurations extends GenericJson
{
    @Key
    private Boolean enabled;
    
    public Boolean getEnabled() {
        return this.enabled;
    }
    
    public AdministratorWebTokenSpecManagedConfigurations setEnabled(final Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public AdministratorWebTokenSpecManagedConfigurations set(final String fieldName, final Object value) {
        return (AdministratorWebTokenSpecManagedConfigurations)super.set(fieldName, value);
    }
    
    public AdministratorWebTokenSpecManagedConfigurations clone() {
        return (AdministratorWebTokenSpecManagedConfigurations)super.clone();
    }
}
