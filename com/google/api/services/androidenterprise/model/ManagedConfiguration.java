package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ManagedConfiguration extends GenericJson
{
    @Key
    private ConfigurationVariables configurationVariables;
    @Key
    private String kind;
    @Key
    private List<ManagedProperty> managedProperty;
    @Key
    private String productId;
    
    public ConfigurationVariables getConfigurationVariables() {
        return this.configurationVariables;
    }
    
    public ManagedConfiguration setConfigurationVariables(final ConfigurationVariables configurationVariables) {
        this.configurationVariables = configurationVariables;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public ManagedConfiguration setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public List<ManagedProperty> getManagedProperty() {
        return this.managedProperty;
    }
    
    public ManagedConfiguration setManagedProperty(final List<ManagedProperty> managedProperty) {
        this.managedProperty = managedProperty;
        return this;
    }
    
    public String getProductId() {
        return this.productId;
    }
    
    public ManagedConfiguration setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public ManagedConfiguration set(final String fieldName, final Object value) {
        return (ManagedConfiguration)super.set(fieldName, value);
    }
    
    public ManagedConfiguration clone() {
        return (ManagedConfiguration)super.clone();
    }
}
