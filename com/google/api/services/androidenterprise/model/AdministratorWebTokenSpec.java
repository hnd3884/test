package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AdministratorWebTokenSpec extends GenericJson
{
    @Key
    private AdministratorWebTokenSpecManagedConfigurations managedConfigurations;
    @Key
    private String parent;
    @Key
    private List<String> permission;
    @Key
    private AdministratorWebTokenSpecPlaySearch playSearch;
    @Key
    private AdministratorWebTokenSpecPrivateApps privateApps;
    @Key
    private AdministratorWebTokenSpecStoreBuilder storeBuilder;
    @Key
    private AdministratorWebTokenSpecWebApps webApps;
    @Key
    private AdministratorWebTokenSpecZeroTouch zeroTouch;
    
    public AdministratorWebTokenSpecManagedConfigurations getManagedConfigurations() {
        return this.managedConfigurations;
    }
    
    public AdministratorWebTokenSpec setManagedConfigurations(final AdministratorWebTokenSpecManagedConfigurations managedConfigurations) {
        this.managedConfigurations = managedConfigurations;
        return this;
    }
    
    public String getParent() {
        return this.parent;
    }
    
    public AdministratorWebTokenSpec setParent(final String parent) {
        this.parent = parent;
        return this;
    }
    
    public List<String> getPermission() {
        return this.permission;
    }
    
    public AdministratorWebTokenSpec setPermission(final List<String> permission) {
        this.permission = permission;
        return this;
    }
    
    public AdministratorWebTokenSpecPlaySearch getPlaySearch() {
        return this.playSearch;
    }
    
    public AdministratorWebTokenSpec setPlaySearch(final AdministratorWebTokenSpecPlaySearch playSearch) {
        this.playSearch = playSearch;
        return this;
    }
    
    public AdministratorWebTokenSpecPrivateApps getPrivateApps() {
        return this.privateApps;
    }
    
    public AdministratorWebTokenSpec setPrivateApps(final AdministratorWebTokenSpecPrivateApps privateApps) {
        this.privateApps = privateApps;
        return this;
    }
    
    public AdministratorWebTokenSpecStoreBuilder getStoreBuilder() {
        return this.storeBuilder;
    }
    
    public AdministratorWebTokenSpec setStoreBuilder(final AdministratorWebTokenSpecStoreBuilder storeBuilder) {
        this.storeBuilder = storeBuilder;
        return this;
    }
    
    public AdministratorWebTokenSpecWebApps getWebApps() {
        return this.webApps;
    }
    
    public AdministratorWebTokenSpec setWebApps(final AdministratorWebTokenSpecWebApps webApps) {
        this.webApps = webApps;
        return this;
    }
    
    public AdministratorWebTokenSpecZeroTouch getZeroTouch() {
        return this.zeroTouch;
    }
    
    public AdministratorWebTokenSpec setZeroTouch(final AdministratorWebTokenSpecZeroTouch zeroTouch) {
        this.zeroTouch = zeroTouch;
        return this;
    }
    
    public AdministratorWebTokenSpec set(final String fieldName, final Object value) {
        return (AdministratorWebTokenSpec)super.set(fieldName, value);
    }
    
    public AdministratorWebTokenSpec clone() {
        return (AdministratorWebTokenSpec)super.clone();
    }
}
