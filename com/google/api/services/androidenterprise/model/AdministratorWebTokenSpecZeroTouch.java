package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AdministratorWebTokenSpecZeroTouch extends GenericJson
{
    @Key
    private Boolean enabled;
    
    public Boolean getEnabled() {
        return this.enabled;
    }
    
    public AdministratorWebTokenSpecZeroTouch setEnabled(final Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public AdministratorWebTokenSpecZeroTouch set(final String fieldName, final Object value) {
        return (AdministratorWebTokenSpecZeroTouch)super.set(fieldName, value);
    }
    
    public AdministratorWebTokenSpecZeroTouch clone() {
        return (AdministratorWebTokenSpecZeroTouch)super.clone();
    }
}
