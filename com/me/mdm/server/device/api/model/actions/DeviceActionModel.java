package com.me.mdm.server.device.api.model.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceActionModel extends BaseAPIModel
{
    @JsonProperty("device_id")
    private Long deviceId;
    @JsonProperty("command_Name")
    private String commandName;
    
    public String getCommandName() {
        return this.commandName;
    }
    
    public Long getDeviceId() {
        return this.deviceId;
    }
    
    public void setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public void setCommandName(final String commandName) {
        this.commandName = commandName;
    }
}
