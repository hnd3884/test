package com.me.mdm.api.command.device;

import com.me.mdm.api.APIEndpointStratergy;
import com.me.mdm.api.command.CommandWrapper;
import com.me.mdm.api.command.CommandAPIStratergy;
import com.me.mdm.api.ApiRequestHandler;

public class DeviceCommandAPIRequestHandler extends ApiRequestHandler
{
    public DeviceCommandAPIRequestHandler() {
        super(new CommandAPIStratergy(new DeviceCommandWrapper()));
    }
}
