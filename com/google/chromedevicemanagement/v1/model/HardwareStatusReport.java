package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class HardwareStatusReport extends GenericJson
{
    @Key
    private List<CpuTemperatureInfo> cpuTemperatureInfo;
    @Key
    private Integer cpuUtilizationPct;
    @Key
    private String reportTime;
    @Key
    @JsonString
    private Long systemRamFree;
    
    public List<CpuTemperatureInfo> getCpuTemperatureInfo() {
        return this.cpuTemperatureInfo;
    }
    
    public HardwareStatusReport setCpuTemperatureInfo(final List<CpuTemperatureInfo> cpuTemperatureInfo) {
        this.cpuTemperatureInfo = cpuTemperatureInfo;
        return this;
    }
    
    public Integer getCpuUtilizationPct() {
        return this.cpuUtilizationPct;
    }
    
    public HardwareStatusReport setCpuUtilizationPct(final Integer cpuUtilizationPct) {
        this.cpuUtilizationPct = cpuUtilizationPct;
        return this;
    }
    
    public String getReportTime() {
        return this.reportTime;
    }
    
    public HardwareStatusReport setReportTime(final String reportTime) {
        this.reportTime = reportTime;
        return this;
    }
    
    public Long getSystemRamFree() {
        return this.systemRamFree;
    }
    
    public HardwareStatusReport setSystemRamFree(final Long systemRamFree) {
        this.systemRamFree = systemRamFree;
        return this;
    }
    
    public HardwareStatusReport set(final String s, final Object o) {
        return (HardwareStatusReport)super.set(s, o);
    }
    
    public HardwareStatusReport clone() {
        return (HardwareStatusReport)super.clone();
    }
}
