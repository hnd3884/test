package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ActiveTimeRange extends GenericJson
{
    @Key
    private String activeDuration;
    @Key
    private String startTime;
    
    public String getActiveDuration() {
        return this.activeDuration;
    }
    
    public ActiveTimeRange setActiveDuration(final String activeDuration) {
        this.activeDuration = activeDuration;
        return this;
    }
    
    public String getStartTime() {
        return this.startTime;
    }
    
    public ActiveTimeRange setStartTime(final String startTime) {
        this.startTime = startTime;
        return this;
    }
    
    public ActiveTimeRange set(final String s, final Object o) {
        return (ActiveTimeRange)super.set(s, o);
    }
    
    public ActiveTimeRange clone() {
        return (ActiveTimeRange)super.clone();
    }
}
