package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class ManagedConfigurationsForUserListResponse extends GenericJson
{
    @Key
    private List<ManagedConfiguration> managedConfigurationForUser;
    
    public List<ManagedConfiguration> getManagedConfigurationForUser() {
        return this.managedConfigurationForUser;
    }
    
    public ManagedConfigurationsForUserListResponse setManagedConfigurationForUser(final List<ManagedConfiguration> managedConfigurationForUser) {
        this.managedConfigurationForUser = managedConfigurationForUser;
        return this;
    }
    
    public ManagedConfigurationsForUserListResponse set(final String fieldName, final Object value) {
        return (ManagedConfigurationsForUserListResponse)super.set(fieldName, value);
    }
    
    public ManagedConfigurationsForUserListResponse clone() {
        return (ManagedConfigurationsForUserListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)ManagedConfiguration.class);
    }
}
