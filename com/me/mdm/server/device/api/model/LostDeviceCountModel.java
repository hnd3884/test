package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LostDeviceCountModel
{
    @JsonProperty("lostDeviceCount")
    private int lostDeviceCount;
    
    public void setLostDeviceCount(final int lostDeviceCount) {
        this.lostDeviceCount = lostDeviceCount;
    }
}
