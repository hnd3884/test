package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class CpuTemperatureInfo extends GenericJson
{
    @Key
    private String label;
    @Key
    private Integer temperatureCelsius;
    @Key
    private String timestamp;
    
    public String getLabel() {
        return this.label;
    }
    
    public CpuTemperatureInfo setLabel(final String label) {
        this.label = label;
        return this;
    }
    
    public Integer getTemperatureCelsius() {
        return this.temperatureCelsius;
    }
    
    public CpuTemperatureInfo setTemperatureCelsius(final Integer temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
        return this;
    }
    
    public String getTimestamp() {
        return this.timestamp;
    }
    
    public CpuTemperatureInfo setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
        return this;
    }
    
    public CpuTemperatureInfo set(final String s, final Object o) {
        return (CpuTemperatureInfo)super.set(s, o);
    }
    
    public CpuTemperatureInfo clone() {
        return (CpuTemperatureInfo)super.clone();
    }
}
