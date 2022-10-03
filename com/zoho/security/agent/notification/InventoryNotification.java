package com.zoho.security.agent.notification;

import java.util.logging.Level;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_CONFIG_PUSH;
import com.zoho.security.agent.AppSenseAgent;
import com.zoho.security.agent.Components;
import org.json.JSONObject;
import java.util.logging.Logger;

public class InventoryNotification extends DefaultNotificationReceiver
{
    private static final Logger LOGGER;
    
    @Override
    public Object getRecentDataOnChange(final JSONObject propertyObj, final Components.COMPONENT_NAME subComponent) {
        switch (subComponent) {
            case CACERT: {
                final String caCertHash = AppSenseAgent.getCACertsHash();
                if (caCertHash == null) {
                    ZSEC_CONFIG_PUSH.pushConfigPushError("CACERT_HASH_PUSH", "Hash NOT Found on the Server", (ExecutionTimer)null);
                    break;
                }
                if (!propertyObj.has(subComponent.getValue())) {
                    InventoryNotification.LOGGER.log(Level.INFO, "cacerts hash are pushed to Appsense for 1st time");
                    return caCertHash;
                }
                if (!propertyObj.getString(subComponent.getValue()).equals(caCertHash)) {
                    InventoryNotification.LOGGER.log(Level.INFO, "Updated cacerts hash are pushed to Appsense");
                    return caCertHash;
                }
                break;
            }
            case MILESTONE: {
                final String milestone = AppSenseAgent.getMilestoneVersion();
                if (milestone == null) {
                    ZSEC_CONFIG_PUSH.pushConfigPushError("MILESTONE_VERSION_PUSH", "version NOT Found on the Server", (ExecutionTimer)null);
                    break;
                }
                if (!propertyObj.has(subComponent.getValue())) {
                    InventoryNotification.LOGGER.log(Level.INFO, " milestone version pushed to Appsense for 1st time");
                    return AppSenseAgent.getMilestoneVersion();
                }
                if (!propertyObj.getString(subComponent.getValue()).equals(milestone)) {
                    InventoryNotification.LOGGER.log(Level.INFO, "Updated milestone version pushed to Appsense");
                    return AppSenseAgent.getMilestoneVersion();
                }
                break;
            }
        }
        return null;
    }
    
    @Override
    public boolean isChangePushEnabled(final Components.COMPONENT_NAME subComponent) {
        switch (subComponent) {
            case CACERT: {
                return AppSenseAgent.isEnableCACertPush();
            }
            case MILESTONE: {
                return AppSenseAgent.isMilestoneVersionPush();
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(InventoryNotification.class.getName());
    }
}
