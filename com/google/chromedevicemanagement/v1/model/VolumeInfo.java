package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class VolumeInfo extends GenericJson
{
    @Key
    @JsonString
    private Long storageFreeBytes;
    @Key
    @JsonString
    private Long storageTotalBytes;
    @Key
    private String volumeId;
    
    public Long getStorageFreeBytes() {
        return this.storageFreeBytes;
    }
    
    public VolumeInfo setStorageFreeBytes(final Long storageFreeBytes) {
        this.storageFreeBytes = storageFreeBytes;
        return this;
    }
    
    public Long getStorageTotalBytes() {
        return this.storageTotalBytes;
    }
    
    public VolumeInfo setStorageTotalBytes(final Long storageTotalBytes) {
        this.storageTotalBytes = storageTotalBytes;
        return this;
    }
    
    public String getVolumeId() {
        return this.volumeId;
    }
    
    public VolumeInfo setVolumeId(final String volumeId) {
        this.volumeId = volumeId;
        return this;
    }
    
    public VolumeInfo set(final String s, final Object o) {
        return (VolumeInfo)super.set(s, o);
    }
    
    public VolumeInfo clone() {
        return (VolumeInfo)super.clone();
    }
}
