package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class MaintenanceWindow extends GenericJson
{
    @Key
    @JsonString
    private Long durationMs;
    @Key
    @JsonString
    private Long startTimeAfterMidnightMs;
    
    public Long getDurationMs() {
        return this.durationMs;
    }
    
    public MaintenanceWindow setDurationMs(final Long durationMs) {
        this.durationMs = durationMs;
        return this;
    }
    
    public Long getStartTimeAfterMidnightMs() {
        return this.startTimeAfterMidnightMs;
    }
    
    public MaintenanceWindow setStartTimeAfterMidnightMs(final Long startTimeAfterMidnightMs) {
        this.startTimeAfterMidnightMs = startTimeAfterMidnightMs;
        return this;
    }
    
    public MaintenanceWindow set(final String fieldName, final Object value) {
        return (MaintenanceWindow)super.set(fieldName, value);
    }
    
    public MaintenanceWindow clone() {
        return (MaintenanceWindow)super.clone();
    }
}
