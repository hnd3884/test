package com.zoho.security.agent.notification;

import java.util.List;
import com.zoho.security.eventfw.pojos.log.ZSEC_CONFIG_PUSH;
import org.json.JSONArray;
import com.zoho.security.eventfw.pojos.log.ZSEC_APPSENSE_NOTIFICATION;
import java.util.logging.Level;
import com.zoho.security.agent.SecurityXMLPush;
import com.zoho.security.agent.AppSenseAgent;
import com.zoho.security.eventfw.ExecutionTimer;
import org.json.JSONObject;
import com.zoho.security.agent.Components;
import java.util.logging.Logger;

public class HashNotification extends DefaultNotificationReceiver
{
    private static final Logger LOGGER;
    
    @Override
    public boolean receive(final Components.COMPONENT component, final Components.COMPONENT_NAME subComponent, final JSONObject dataObj) {
        try {
            switch (subComponent) {
                case SECURITYXML: {
                    final JSONArray hashes = dataObj.getJSONArray("VALUE");
                    final ExecutionTimer timer = ExecutionTimer.startInstance();
                    if (AppSenseAgent.isEnableSecurityXMLPush()) {
                        SecurityXMLPush.pushSecurityXML(hashes);
                        HashNotification.LOGGER.log(Level.SEVERE, " Security xml push time taken {0} , no files {1}", new Object[] { timer.getExecutionTime(), hashes.length() });
                    }
                    return true;
                }
            }
        }
        catch (final Exception e) {
            ZSEC_APPSENSE_NOTIFICATION.pushExceptionWithComponent(subComponent.getValue(), e.getMessage(), (ExecutionTimer)null);
        }
        return false;
    }
    
    @Override
    public Object getRecentDataOnChange(final JSONObject propertyObj, final Components.COMPONENT_NAME subComponent) {
        switch (subComponent) {
            case SECURITYXML: {
                if (!propertyObj.has(subComponent.getValue())) {
                    ZSEC_CONFIG_PUSH.pushConfigPushError("SECURITY XML DETAILS NOT FOUND IN LOCALCONFIG", "Pushing all the changes", (ExecutionTimer)null);
                    return AppSenseAgent.getFileHashArray();
                }
                final List<String> xmlhashes = DefaultNotificationReceiver.getXMLHashesAsList(propertyObj.getJSONArray(subComponent.getValue()));
                boolean isAdded = false;
                for (int i = 0; i < AppSenseAgent.getFileHashArray().length(); ++i) {
                    final String hash = AppSenseAgent.getFileHashArray().getJSONObject(i).getString("HASH");
                    if (!xmlhashes.contains(hash)) {
                        isAdded = true;
                    }
                    else {
                        xmlhashes.remove(hash);
                    }
                }
                if (isAdded || xmlhashes.size() > 0) {
                    HashNotification.LOGGER.log(Level.INFO, "Modified security xmls are pushed to Appsense");
                    return AppSenseAgent.getFileHashArray();
                }
                break;
            }
        }
        return null;
    }
    
    @Override
    public boolean isChangePushEnabled(final Components.COMPONENT_NAME subComponent) {
        switch (subComponent) {
            case SECURITYXML: {
                return AppSenseAgent.isEnableSecurityXMLPush();
            }
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(HashNotification.class.getName());
    }
}
