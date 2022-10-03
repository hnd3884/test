package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class AutoInstallPolicy extends GenericJson
{
    @Key
    private List<AutoInstallConstraint> autoInstallConstraint;
    @Key
    private String autoInstallMode;
    @Key
    private Integer autoInstallPriority;
    @Key
    private Integer minimumVersionCode;
    
    public List<AutoInstallConstraint> getAutoInstallConstraint() {
        return this.autoInstallConstraint;
    }
    
    public AutoInstallPolicy setAutoInstallConstraint(final List<AutoInstallConstraint> autoInstallConstraint) {
        this.autoInstallConstraint = autoInstallConstraint;
        return this;
    }
    
    public String getAutoInstallMode() {
        return this.autoInstallMode;
    }
    
    public AutoInstallPolicy setAutoInstallMode(final String autoInstallMode) {
        this.autoInstallMode = autoInstallMode;
        return this;
    }
    
    public Integer getAutoInstallPriority() {
        return this.autoInstallPriority;
    }
    
    public AutoInstallPolicy setAutoInstallPriority(final Integer autoInstallPriority) {
        this.autoInstallPriority = autoInstallPriority;
        return this;
    }
    
    public Integer getMinimumVersionCode() {
        return this.minimumVersionCode;
    }
    
    public AutoInstallPolicy setMinimumVersionCode(final Integer minimumVersionCode) {
        this.minimumVersionCode = minimumVersionCode;
        return this;
    }
    
    public AutoInstallPolicy set(final String fieldName, final Object value) {
        return (AutoInstallPolicy)super.set(fieldName, value);
    }
    
    public AutoInstallPolicy clone() {
        return (AutoInstallPolicy)super.clone();
    }
    
    static {
        Data.nullOf((Class)AutoInstallConstraint.class);
    }
}
