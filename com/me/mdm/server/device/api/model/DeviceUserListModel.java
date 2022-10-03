package com.me.mdm.server.device.api.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceUserListModel
{
    private List<DeviceUserModel> devices;
    
    public List<DeviceUserModel> getDevices() {
        return this.devices;
    }
    
    public void setDevices(final List<DeviceUserModel> devices) {
        this.devices = devices;
    }
}
