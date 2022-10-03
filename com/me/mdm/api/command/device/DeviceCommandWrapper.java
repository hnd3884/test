package com.me.mdm.api.command.device;

import com.me.mdm.server.inv.actions.InvActionUtil;
import com.me.mdm.api.command.CommandWrapper;

public class DeviceCommandWrapper extends CommandWrapper
{
    @Override
    public String getEquivalentCommandName(final String commandName) {
        if (commandName.contains("containers")) {
            return null;
        }
        if (commandName.equalsIgnoreCase("scan")) {
            return "scan";
        }
        if (commandName.equalsIgnoreCase("lock")) {
            return "DeviceLock";
        }
        if (commandName.equalsIgnoreCase("control")) {
            return "RemoteSession";
        }
        if (commandName.equalsIgnoreCase("alarm")) {
            return "DeviceRing";
        }
        if (commandName.equalsIgnoreCase("erase")) {
            return "EraseDevice";
        }
        if (commandName.equalsIgnoreCase("enterprise/erase")) {
            return "CorporateWipe";
        }
        if (commandName.equalsIgnoreCase("passcode/clear")) {
            return "ClearPasscode";
        }
        if (commandName.equalsIgnoreCase("passcode/reset")) {
            return "ResetPasscode";
        }
        if (commandName.equalsIgnoreCase("location/get")) {
            return "GetLocation";
        }
        if (commandName.equalsIgnoreCase("shutdown")) {
            return "ShutDownDevice";
        }
        if (commandName.equalsIgnoreCase("restart")) {
            return "RestartDevice";
        }
        if (commandName.equalsIgnoreCase("lostmode/enable")) {
            return "EnableLostMode";
        }
        if (commandName.equalsIgnoreCase("lostmode/disable")) {
            return "DisableLostMode";
        }
        if (commandName.equalsIgnoreCase("clear_app_data")) {
            return "ClearAppData";
        }
        return InvActionUtil.getEquivalentCommandName(commandName);
    }
}
