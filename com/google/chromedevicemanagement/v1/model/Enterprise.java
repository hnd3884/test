package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Enterprise extends GenericJson
{
    @Key
    private DefaultPolicy defaultPolicy;
    @Key
    private String enterpriseId;
    @Key
    private String enterpriseName;
    @Key
    private String name;
    @Key
    private Upgrades upgrades;
    
    public DefaultPolicy getDefaultPolicy() {
        return this.defaultPolicy;
    }
    
    public Enterprise setDefaultPolicy(final DefaultPolicy defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
        return this;
    }
    
    public String getEnterpriseId() {
        return this.enterpriseId;
    }
    
    public Enterprise setEnterpriseId(final String enterpriseId) {
        this.enterpriseId = enterpriseId;
        return this;
    }
    
    public String getEnterpriseName() {
        return this.enterpriseName;
    }
    
    public Enterprise setEnterpriseName(final String enterpriseName) {
        this.enterpriseName = enterpriseName;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Enterprise setName(final String name) {
        this.name = name;
        return this;
    }
    
    public Upgrades getUpgrades() {
        return this.upgrades;
    }
    
    public Enterprise setUpgrades(final Upgrades upgrades) {
        this.upgrades = upgrades;
        return this;
    }
    
    public Enterprise set(final String s, final Object o) {
        return (Enterprise)super.set(s, o);
    }
    
    public Enterprise clone() {
        return (Enterprise)super.clone();
    }
}
