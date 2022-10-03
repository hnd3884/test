package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class ManagedPropertyBundle extends GenericJson
{
    @Key
    private List<ManagedProperty> managedProperty;
    
    public List<ManagedProperty> getManagedProperty() {
        return this.managedProperty;
    }
    
    public ManagedPropertyBundle setManagedProperty(final List<ManagedProperty> managedProperty) {
        this.managedProperty = managedProperty;
        return this;
    }
    
    public ManagedPropertyBundle set(final String fieldName, final Object value) {
        return (ManagedPropertyBundle)super.set(fieldName, value);
    }
    
    public ManagedPropertyBundle clone() {
        return (ManagedPropertyBundle)super.clone();
    }
    
    static {
        Data.nullOf((Class)ManagedProperty.class);
    }
}
