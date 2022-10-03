package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class ManagedConfigurationsForDeviceListResponse extends GenericJson
{
    @Key
    private List<ManagedConfiguration> managedConfigurationForDevice;
    
    public List<ManagedConfiguration> getManagedConfigurationForDevice() {
        return this.managedConfigurationForDevice;
    }
    
    public ManagedConfigurationsForDeviceListResponse setManagedConfigurationForDevice(final List<ManagedConfiguration> managedConfigurationForDevice) {
        this.managedConfigurationForDevice = managedConfigurationForDevice;
        return this;
    }
    
    public ManagedConfigurationsForDeviceListResponse set(final String fieldName, final Object value) {
        return (ManagedConfigurationsForDeviceListResponse)super.set(fieldName, value);
    }
    
    public ManagedConfigurationsForDeviceListResponse clone() {
        return (ManagedConfigurationsForDeviceListResponse)super.clone();
    }
    
    static {
        Data.nullOf((Class)ManagedConfiguration.class);
    }
}
