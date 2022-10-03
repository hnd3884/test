package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class OsUpdateStatus extends GenericJson
{
    @Key
    private String lastRebootTime;
    @Key
    private String lastUpdateCheckTime;
    @Key
    private String lastUpdateTime;
    @Key
    private String newPlatformVersion;
    @Key
    private String newRequiredPlatformVersion;
    @Key
    private String updateStatus;
    
    public String getLastRebootTime() {
        return this.lastRebootTime;
    }
    
    public OsUpdateStatus setLastRebootTime(final String lastRebootTime) {
        this.lastRebootTime = lastRebootTime;
        return this;
    }
    
    public String getLastUpdateCheckTime() {
        return this.lastUpdateCheckTime;
    }
    
    public OsUpdateStatus setLastUpdateCheckTime(final String lastUpdateCheckTime) {
        this.lastUpdateCheckTime = lastUpdateCheckTime;
        return this;
    }
    
    public String getLastUpdateTime() {
        return this.lastUpdateTime;
    }
    
    public OsUpdateStatus setLastUpdateTime(final String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }
    
    public String getNewPlatformVersion() {
        return this.newPlatformVersion;
    }
    
    public OsUpdateStatus setNewPlatformVersion(final String newPlatformVersion) {
        this.newPlatformVersion = newPlatformVersion;
        return this;
    }
    
    public String getNewRequiredPlatformVersion() {
        return this.newRequiredPlatformVersion;
    }
    
    public OsUpdateStatus setNewRequiredPlatformVersion(final String newRequiredPlatformVersion) {
        this.newRequiredPlatformVersion = newRequiredPlatformVersion;
        return this;
    }
    
    public String getUpdateStatus() {
        return this.updateStatus;
    }
    
    public OsUpdateStatus setUpdateStatus(final String updateStatus) {
        this.updateStatus = updateStatus;
        return this;
    }
    
    public OsUpdateStatus set(final String s, final Object o) {
        return (OsUpdateStatus)super.set(s, o);
    }
    
    public OsUpdateStatus clone() {
        return (OsUpdateStatus)super.clone();
    }
}
