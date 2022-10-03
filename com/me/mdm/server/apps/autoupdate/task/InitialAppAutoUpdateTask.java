package com.me.mdm.server.apps.autoupdate.task;

import java.util.Hashtable;
import java.util.logging.Level;
import com.me.mdm.server.apps.autoupdate.AutoAppUpdateHandler;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class InitialAppAutoUpdateTask implements SchedulerExecutionInterface
{
    Logger mdmLogger;
    
    public InitialAppAutoUpdateTask() {
        this.mdmLogger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void executeTask(final Properties properties) {
        try {
            AutoAppUpdateHandler.getInstance().handleInitialAutoAppUpdate(((Hashtable<K, Long>)properties).get("customerId"));
        }
        catch (final Exception e) {
            this.mdmLogger.log(Level.SEVERE, "Couldn't initiate auto update on initial config", e);
        }
    }
}
