package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceCommand extends GenericJson
{
    @Key
    @JsonString
    private Long commandId;
    @Key
    private String createTime;
    @Key
    private String payload;
    @Key
    private String type;
    @Key
    private String validDuration;
    
    public Long getCommandId() {
        return this.commandId;
    }
    
    public DeviceCommand setCommandId(final Long commandId) {
        this.commandId = commandId;
        return this;
    }
    
    public String getCreateTime() {
        return this.createTime;
    }
    
    public DeviceCommand setCreateTime(final String createTime) {
        this.createTime = createTime;
        return this;
    }
    
    public String getPayload() {
        return this.payload;
    }
    
    public DeviceCommand setPayload(final String payload) {
        this.payload = payload;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public DeviceCommand setType(final String type) {
        this.type = type;
        return this;
    }
    
    public String getValidDuration() {
        return this.validDuration;
    }
    
    public DeviceCommand setValidDuration(final String validDuration) {
        this.validDuration = validDuration;
        return this;
    }
    
    public DeviceCommand set(final String s, final Object o) {
        return (DeviceCommand)super.set(s, o);
    }
    
    public DeviceCommand clone() {
        return (DeviceCommand)super.clone();
    }
}
