package com.me.mdm.server.apps.mac;

import java.util.Hashtable;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class AddMEMDMMacAppTask implements SchedulerExecutionInterface
{
    private final Logger logger;
    
    public AddMEMDMMacAppTask() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void executeTask(final Properties props) {
        this.logger.log(Level.INFO, "Adding of Me MDM Mac Agent task started");
        try {
            MDMAgentSettingsHandler.getInstance().toggleMDMMacAgentAutoDistributionStatus(Long.parseLong(((Hashtable<K, Object>)props).get("customerId").toString()), Boolean.TRUE);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Mac Agent failed to get added to app repository", e);
        }
        this.logger.log(Level.INFO, "Adding of Me MDM Mac Agent task completed.");
    }
}
