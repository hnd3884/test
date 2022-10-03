package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class IssueDeviceCommandRequest extends GenericJson
{
    @Key
    private DeviceCommand deviceCommand;
    
    public DeviceCommand getDeviceCommand() {
        return this.deviceCommand;
    }
    
    public IssueDeviceCommandRequest setDeviceCommand(final DeviceCommand deviceCommand) {
        this.deviceCommand = deviceCommand;
        return this;
    }
    
    public IssueDeviceCommandRequest set(final String s, final Object o) {
        return (IssueDeviceCommandRequest)super.set(s, o);
    }
    
    public IssueDeviceCommandRequest clone() {
        return (IssueDeviceCommandRequest)super.clone();
    }
}
