package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class CpuInfo extends GenericJson
{
    @Key
    private String architecture;
    @Key
    private Integer maxClockSpeed;
    @Key
    private String modelName;
    
    public String getArchitecture() {
        return this.architecture;
    }
    
    public CpuInfo setArchitecture(final String architecture) {
        this.architecture = architecture;
        return this;
    }
    
    public Integer getMaxClockSpeed() {
        return this.maxClockSpeed;
    }
    
    public CpuInfo setMaxClockSpeed(final Integer maxClockSpeed) {
        this.maxClockSpeed = maxClockSpeed;
        return this;
    }
    
    public String getModelName() {
        return this.modelName;
    }
    
    public CpuInfo setModelName(final String modelName) {
        this.modelName = modelName;
        return this;
    }
    
    public CpuInfo set(final String s, final Object o) {
        return (CpuInfo)super.set(s, o);
    }
    
    public CpuInfo clone() {
        return (CpuInfo)super.clone();
    }
}
