package com.zoho.security.agent.notification;

import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_APPSENSE_NOTIFICATION;
import com.zoho.security.instrumentation.WAFInstrumentException;
import java.util.logging.Level;
import com.zoho.security.wafad.WAFAttackDiscovery;
import org.json.JSONObject;
import com.zoho.security.agent.Components;
import java.util.logging.Logger;

public class WAFAttackDiscoveryNotification extends DefaultNotificationReceiver
{
    private static final Logger LOGGER;
    
    @Override
    public boolean receive(final Components.COMPONENT component, final Components.COMPONENT_NAME subComponent, final JSONObject dataObj) {
        try {
            final JSONObject adInstrumentInfo = dataObj.getJSONObject("VALUE");
            switch (subComponent) {
                case AD_ADD: {
                    WAFAttackDiscovery.add(adInstrumentInfo);
                    break;
                }
                case AD_UPDATE: {
                    WAFAttackDiscovery.update(adInstrumentInfo);
                    break;
                }
                case AD_REMOVE: {
                    WAFAttackDiscovery.remove(adInstrumentInfo);
                    break;
                }
            }
            return true;
        }
        catch (final WAFInstrumentException adInstrumentEx) {
            WAFAttackDiscoveryNotification.LOGGER.log(Level.SEVERE, "Attack Discovery Notification Failed. Component : \"{0}\" SubComponent : \"{1}\" JSON Object : \"{2}\" Exception : \"{3}\"", new Object[] { component, subComponent, dataObj, adInstrumentEx.toString() });
        }
        catch (final Exception e) {
            ZSEC_APPSENSE_NOTIFICATION.pushExceptionWithComponents(subComponent.name(), component.name(), e.getMessage(), (ExecutionTimer)null);
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(WAFAttackDiscoveryNotification.class.getName());
    }
}
