package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ProductPolicy extends GenericJson
{
    @Key
    private AutoInstallPolicy autoInstallPolicy;
    @Key
    private String autoUpdateMode;
    @Key
    private ManagedConfiguration managedConfiguration;
    @Key
    private String productId;
    @Key
    private List<String> trackIds;
    @Key
    private List<String> tracks;
    
    public AutoInstallPolicy getAutoInstallPolicy() {
        return this.autoInstallPolicy;
    }
    
    public ProductPolicy setAutoInstallPolicy(final AutoInstallPolicy autoInstallPolicy) {
        this.autoInstallPolicy = autoInstallPolicy;
        return this;
    }
    
    public String getAutoUpdateMode() {
        return this.autoUpdateMode;
    }
    
    public ProductPolicy setAutoUpdateMode(final String autoUpdateMode) {
        this.autoUpdateMode = autoUpdateMode;
        return this;
    }
    
    public ManagedConfiguration getManagedConfiguration() {
        return this.managedConfiguration;
    }
    
    public ProductPolicy setManagedConfiguration(final ManagedConfiguration managedConfiguration) {
        this.managedConfiguration = managedConfiguration;
        return this;
    }
    
    public String getProductId() {
        return this.productId;
    }
    
    public ProductPolicy setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public List<String> getTrackIds() {
        return this.trackIds;
    }
    
    public ProductPolicy setTrackIds(final List<String> trackIds) {
        this.trackIds = trackIds;
        return this;
    }
    
    public List<String> getTracks() {
        return this.tracks;
    }
    
    public ProductPolicy setTracks(final List<String> tracks) {
        this.tracks = tracks;
        return this;
    }
    
    public ProductPolicy set(final String fieldName, final Object value) {
        return (ProductPolicy)super.set(fieldName, value);
    }
    
    public ProductPolicy clone() {
        return (ProductPolicy)super.clone();
    }
}
