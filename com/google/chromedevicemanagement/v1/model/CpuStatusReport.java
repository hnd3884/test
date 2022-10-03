package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class CpuStatusReport extends GenericJson
{
    @Key
    private List<CpuTemperatureInfo> cpuTemperatureInfo;
    @Key
    private List<Integer> cpuUtilizationPercentageInfo;
    @Key
    private String reportTime;
    
    public List<CpuTemperatureInfo> getCpuTemperatureInfo() {
        return this.cpuTemperatureInfo;
    }
    
    public CpuStatusReport setCpuTemperatureInfo(final List<CpuTemperatureInfo> cpuTemperatureInfo) {
        this.cpuTemperatureInfo = cpuTemperatureInfo;
        return this;
    }
    
    public List<Integer> getCpuUtilizationPercentageInfo() {
        return this.cpuUtilizationPercentageInfo;
    }
    
    public CpuStatusReport setCpuUtilizationPercentageInfo(final List<Integer> cpuUtilizationPercentageInfo) {
        this.cpuUtilizationPercentageInfo = cpuUtilizationPercentageInfo;
        return this;
    }
    
    public String getReportTime() {
        return this.reportTime;
    }
    
    public CpuStatusReport setReportTime(final String reportTime) {
        this.reportTime = reportTime;
        return this;
    }
    
    public CpuStatusReport set(final String s, final Object o) {
        return (CpuStatusReport)super.set(s, o);
    }
    
    public CpuStatusReport clone() {
        return (CpuStatusReport)super.clone();
    }
    
    static {
        Data.nullOf((Class)CpuTemperatureInfo.class);
    }
}
