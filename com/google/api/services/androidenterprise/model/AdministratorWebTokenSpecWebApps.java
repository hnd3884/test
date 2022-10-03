package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AdministratorWebTokenSpecWebApps extends GenericJson
{
    @Key
    private Boolean enabled;
    
    public Boolean getEnabled() {
        return this.enabled;
    }
    
    public AdministratorWebTokenSpecWebApps setEnabled(final Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public AdministratorWebTokenSpecWebApps set(final String fieldName, final Object value) {
        return (AdministratorWebTokenSpecWebApps)super.set(fieldName, value);
    }
    
    public AdministratorWebTokenSpecWebApps clone() {
        return (AdministratorWebTokenSpecWebApps)super.clone();
    }
}
