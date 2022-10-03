package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AdministratorWebTokenSpecPrivateApps extends GenericJson
{
    @Key
    private Boolean enabled;
    
    public Boolean getEnabled() {
        return this.enabled;
    }
    
    public AdministratorWebTokenSpecPrivateApps setEnabled(final Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public AdministratorWebTokenSpecPrivateApps set(final String fieldName, final Object value) {
        return (AdministratorWebTokenSpecPrivateApps)super.set(fieldName, value);
    }
    
    public AdministratorWebTokenSpecPrivateApps clone() {
        return (AdministratorWebTokenSpecPrivateApps)super.clone();
    }
}
