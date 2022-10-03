package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.me.mdm.api.model.BaseAPIModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceLocation extends BaseAPIModel
{
    @JsonProperty("device_id")
    private Long deviceId;
    @JsonProperty("last_known")
    private boolean lastKnown;
    @JsonProperty("no_of_days")
    private int noOfDays;
    
    public DeviceLocation() {
        this.noOfDays = -1;
    }
    
    public Long getDeviceId() {
        return this.deviceId;
    }
    
    public void setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public boolean isLastKnown() {
        return this.lastKnown;
    }
    
    public void setLastKnown(final boolean lastKnown) {
        this.lastKnown = lastKnown;
    }
    
    public int getNoOfDays() {
        return this.noOfDays;
    }
    
    public void setNoOfDays(final int noOfDays) {
        this.noOfDays = noOfDays;
    }
}
