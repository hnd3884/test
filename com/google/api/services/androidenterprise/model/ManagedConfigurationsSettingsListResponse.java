package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class ManagedConfigurationsSettingsListResponse extends GenericJson
{
    @Key
    private List<ManagedConfigurationsSettings> managedConfigurationsSettings;
    
    public List<ManagedConfigurationsSettings> getManagedConfigurationsSettings() {
        return this.managedConfigurationsSettings;
    }
    
    public ManagedConfigurationsSettingsListResponse setManagedConfigurationsSettings(final List<ManagedConfigurationsSettings> managedConfigurationsSettings) {
        this.managedConfigurationsSettings = managedConfigurationsSettings;
        return this;
    }
    
    public ManagedConfigurationsSettingsListResponse set(final String fieldName, final Object value) {
        return (ManagedConfigurationsSettingsListResponse)super.set(fieldName, value);
    }
    
    public ManagedConfigurationsSettingsListResponse clone() {
        return (ManagedConfigurationsSettingsListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)ManagedConfigurationsSettings.class);
    }
}
