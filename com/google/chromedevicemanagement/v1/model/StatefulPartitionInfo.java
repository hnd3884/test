package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class StatefulPartitionInfo extends GenericJson
{
    @Key
    @JsonString
    private Long availableSpace;
    @Key
    @JsonString
    private Long totalSpace;
    
    public Long getAvailableSpace() {
        return this.availableSpace;
    }
    
    public StatefulPartitionInfo setAvailableSpace(final Long availableSpace) {
        this.availableSpace = availableSpace;
        return this;
    }
    
    public Long getTotalSpace() {
        return this.totalSpace;
    }
    
    public StatefulPartitionInfo setTotalSpace(final Long totalSpace) {
        this.totalSpace = totalSpace;
        return this;
    }
    
    public StatefulPartitionInfo set(final String s, final Object o) {
        return (StatefulPartitionInfo)super.set(s, o);
    }
    
    public StatefulPartitionInfo clone() {
        return (StatefulPartitionInfo)super.clone();
    }
}
