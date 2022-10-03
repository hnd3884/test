package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class CpuStatusCollection extends GenericJson
{
    @Key
    private List<CpuTemperatureInfo> cpuTemperatureInfos;
    @Key
    private Integer cpuUtilizationPct;
    
    public List<CpuTemperatureInfo> getCpuTemperatureInfos() {
        return this.cpuTemperatureInfos;
    }
    
    public CpuStatusCollection setCpuTemperatureInfos(final List<CpuTemperatureInfo> cpuTemperatureInfos) {
        this.cpuTemperatureInfos = cpuTemperatureInfos;
        return this;
    }
    
    public Integer getCpuUtilizationPct() {
        return this.cpuUtilizationPct;
    }
    
    public CpuStatusCollection setCpuUtilizationPct(final Integer cpuUtilizationPct) {
        this.cpuUtilizationPct = cpuUtilizationPct;
        return this;
    }
    
    public CpuStatusCollection set(final String s, final Object o) {
        return (CpuStatusCollection)super.set(s, o);
    }
    
    public CpuStatusCollection clone() {
        return (CpuStatusCollection)super.clone();
    }
    
    static {
        Data.nullOf((Class)CpuTemperatureInfo.class);
    }
}
