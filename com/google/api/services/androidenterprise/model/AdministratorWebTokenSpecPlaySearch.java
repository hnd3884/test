package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AdministratorWebTokenSpecPlaySearch extends GenericJson
{
    @Key
    private Boolean approveApps;
    @Key
    private Boolean enabled;
    
    public Boolean getApproveApps() {
        return this.approveApps;
    }
    
    public AdministratorWebTokenSpecPlaySearch setApproveApps(final Boolean approveApps) {
        this.approveApps = approveApps;
        return this;
    }
    
    public Boolean getEnabled() {
        return this.enabled;
    }
    
    public AdministratorWebTokenSpecPlaySearch setEnabled(final Boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public AdministratorWebTokenSpecPlaySearch set(final String fieldName, final Object value) {
        return (AdministratorWebTokenSpecPlaySearch)super.set(fieldName, value);
    }
    
    public AdministratorWebTokenSpecPlaySearch clone() {
        return (AdministratorWebTokenSpecPlaySearch)super.clone();
    }
}
