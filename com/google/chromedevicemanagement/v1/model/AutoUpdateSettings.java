package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class AutoUpdateSettings extends GenericJson
{
    @Key
    private Boolean allowKioskAppControlChromeVersion;
    @Key
    private Boolean rebootAfterUpdate;
    @Key
    private String scatterFactorDuration;
    @Key
    private String targetPlatformVersionPrefix;
    @Key
    private Boolean updateEnabled;
    
    public Boolean getAllowKioskAppControlChromeVersion() {
        return this.allowKioskAppControlChromeVersion;
    }
    
    public AutoUpdateSettings setAllowKioskAppControlChromeVersion(final Boolean allowKioskAppControlChromeVersion) {
        this.allowKioskAppControlChromeVersion = allowKioskAppControlChromeVersion;
        return this;
    }
    
    public Boolean getRebootAfterUpdate() {
        return this.rebootAfterUpdate;
    }
    
    public AutoUpdateSettings setRebootAfterUpdate(final Boolean rebootAfterUpdate) {
        this.rebootAfterUpdate = rebootAfterUpdate;
        return this;
    }
    
    public String getScatterFactorDuration() {
        return this.scatterFactorDuration;
    }
    
    public AutoUpdateSettings setScatterFactorDuration(final String scatterFactorDuration) {
        this.scatterFactorDuration = scatterFactorDuration;
        return this;
    }
    
    public String getTargetPlatformVersionPrefix() {
        return this.targetPlatformVersionPrefix;
    }
    
    public AutoUpdateSettings setTargetPlatformVersionPrefix(final String targetPlatformVersionPrefix) {
        this.targetPlatformVersionPrefix = targetPlatformVersionPrefix;
        return this;
    }
    
    public Boolean getUpdateEnabled() {
        return this.updateEnabled;
    }
    
    public AutoUpdateSettings setUpdateEnabled(final Boolean updateEnabled) {
        this.updateEnabled = updateEnabled;
        return this;
    }
    
    public AutoUpdateSettings set(final String s, final Object o) {
        return (AutoUpdateSettings)super.set(s, o);
    }
    
    public AutoUpdateSettings clone() {
        return (AutoUpdateSettings)super.clone();
    }
}
