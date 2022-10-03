package com.me.mdm.api.command.container;

import com.me.mdm.server.inv.actions.InvActionUtil;
import com.me.mdm.api.command.CommandWrapper;

public class ContainerCommandWrapper extends CommandWrapper
{
    @Override
    public String getEquivalentCommandName(final String endpointPath) {
        if (endpointPath.equalsIgnoreCase("containers/install")) {
            return "CreateContainer";
        }
        if (endpointPath.equalsIgnoreCase("containers/uninstall")) {
            return "RemoveContainer";
        }
        if (endpointPath.equalsIgnoreCase("containers/lock")) {
            return "ContainerLock";
        }
        if (endpointPath.equalsIgnoreCase("containers/unlock")) {
            return "ContainerUnlock";
        }
        if (endpointPath.equalsIgnoreCase("containers/passcode/clear")) {
            return "ClearContainerPasscode";
        }
        return InvActionUtil.getEquivalentCommandName(endpointPath);
    }
}
