package com.me.mdm.server.remotesession;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class RemoteControlCapabilitiesStartupUtil implements SchedulerExecutionInterface
{
    Logger mdmLogger;
    
    public RemoteControlCapabilitiesStartupUtil() {
        this.mdmLogger = Logger.getLogger("MDMRemoteControlLogger");
    }
    
    public void executeTask(final Properties props) {
        this.mdmLogger.log(Level.INFO, "RemoteControlCapabilitiesStartupUtil: Task initiated");
        final String taskName = String.valueOf(((Hashtable<K, Object>)props).get("taskName"));
        if (taskName.equalsIgnoreCase("CapabilitesInfoCommand")) {
            new RemoteControlCapabilitiesUtil().addCapabilitiesCommandToUnpopulatedDevices();
        }
    }
}
