package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class FreeRamStatusReport extends GenericJson
{
    @Key
    private String reportTime;
    @Key
    @JsonString
    private List<Long> systemRamFreeInfo;
    
    public String getReportTime() {
        return this.reportTime;
    }
    
    public FreeRamStatusReport setReportTime(final String reportTime) {
        this.reportTime = reportTime;
        return this;
    }
    
    public List<Long> getSystemRamFreeInfo() {
        return this.systemRamFreeInfo;
    }
    
    public FreeRamStatusReport setSystemRamFreeInfo(final List<Long> systemRamFreeInfo) {
        this.systemRamFreeInfo = systemRamFreeInfo;
        return this;
    }
    
    public FreeRamStatusReport set(final String s, final Object o) {
        return (FreeRamStatusReport)super.set(s, o);
    }
    
    public FreeRamStatusReport clone() {
        return (FreeRamStatusReport)super.clone();
    }
}
