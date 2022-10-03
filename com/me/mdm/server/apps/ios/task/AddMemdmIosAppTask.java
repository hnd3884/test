package com.me.mdm.server.apps.ios.task;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class AddMemdmIosAppTask implements SchedulerExecutionInterface
{
    private final Logger logger;
    
    public AddMemdmIosAppTask() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void executeTask(final Properties props) {
        this.logger.log(Level.INFO, "Inside AddMemdmIosAppTask Task");
        try {
            final Long customerId = Long.valueOf(((Hashtable<K, Object>)props).get("customerId").toString());
            final Long userId = Long.valueOf(((Hashtable<K, Object>)props).get("userId").toString());
            if (customerId != null && customerId != -1L) {
                this.logger.log(Level.INFO, "Adding default iOS agent settings for customer {0}", customerId);
                final JSONObject agentSettings = new JSONObject();
                agentSettings.put("CUSTOMER_ID", (Object)customerId);
                agentSettings.put("IS_NATIVE_APP_ENABLE", true);
                agentSettings.put("VALIDATE_CHECKSUM", true);
                MDMAgentSettingsHandler.getInstance().addOrUpdateiOSSettings(agentSettings);
                IosNativeAppHandler.getInstance().addIOSNativeAgent(customerId, userId);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in AddMemdmIosAppTask Task ", ex);
        }
        this.logger.log(Level.INFO, "Completed AddMemdmIosAppTask Task");
    }
}
