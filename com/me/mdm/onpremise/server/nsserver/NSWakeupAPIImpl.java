package com.me.mdm.onpremise.server.nsserver;

import org.json.JSONObject;
import com.me.devicemanagement.framework.server.api.NSResponseAPI;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.api.DCNotificationServiceAPI;
import com.me.mdm.server.notification.NSWakeupAPI;

public class NSWakeupAPIImpl extends NSWakeupAPI
{
    private static NSWakeupAPIImpl nsWakeupImpl;
    DCNotificationServiceAPI NSImpl;
    public Logger logger;
    
    public NSWakeupAPIImpl() {
        this.NSImpl = ApiFactoryProvider.getDCNotificationServiceAPI();
        this.logger = Logger.getLogger("MDMWakupReqLogger");
    }
    
    public HashMap wakeUpDevices(final List resourceList, final int notificationType) {
        final HashMap retHash = new HashMap();
        List notificationSuccessList = new ArrayList();
        List notificationFailureList = new ArrayList();
        final List notRegister = new ArrayList();
        final int er = 2;
        try {
            if (this.NSImpl.isNSEnabled()) {
                final List getLiveResourceList = this.NSImpl.getLiveResourceList();
                this.logger.log(Level.INFO, "LIVE RESOURCE LIST FROM THE NS: {0}", getLiveResourceList);
                for (final Long resourceID : resourceList) {
                    final NSResponseAPI nsResponse = this.NSImpl.sendRequest(resourceID, "WAKEUP\n", (String)null);
                    this.logger.log(Level.INFO, "RESPONSE FROM THE DEVICE: {0}:{1}", new Object[] { resourceID, nsResponse });
                    if (nsResponse.getResponseStatus(resourceID) == 0) {
                        notificationSuccessList.add(resourceID);
                        this.logger.log(Level.INFO, "WAKEUP: {0}:SUCCESS", resourceID);
                    }
                    else {
                        this.logger.log(Level.INFO, "WAKEUP: {0}:FAILURE", resourceID);
                        notificationFailureList.add(resourceID);
                    }
                }
            }
            else {
                this.logger.log(Level.INFO, "NS SERVICE DISABLED IN THIS SERVER");
                notificationFailureList = resourceList;
            }
        }
        catch (final Exception ex) {
            notificationSuccessList = new ArrayList();
            notificationFailureList = new ArrayList(resourceList);
            this.logger.log(Level.WARNING, "Exception while sending request to client .. ", ex);
        }
        finally {
            retHash.put("NotificationSuccessList", notificationSuccessList);
            retHash.put("NotificationFailureList", notificationFailureList);
            retHash.put("NotificationCompleteList", resourceList);
            retHash.put("NotificationUnregisteredList", notRegister);
            retHash.put("NotificationType", notificationType);
            retHash.put("NotificationError", er);
            this.processWakeUpResults((Map)retHash);
        }
        return retHash;
    }
    
    public NSWakeupAPI getInstance() {
        if (NSWakeupAPIImpl.nsWakeupImpl == null) {
            NSWakeupAPIImpl.nsWakeupImpl = new NSWakeupAPIImpl();
        }
        return NSWakeupAPIImpl.nsWakeupImpl;
    }
    
    public JSONObject getNSConfig() {
        final JSONObject nsData = new JSONObject();
        try {
            if (this.NSImpl.isNSEnabled()) {
                nsData.put("NsPort", this.NSImpl.getNSPort());
                nsData.put("NsSyncIntervalSeconds", 600);
            }
            else {
                this.logger.log(Level.INFO, "NS not enabled");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getting NSConfig", ex);
        }
        this.logger.log(Level.INFO, "Getting Android ns policy : {0}", nsData);
        return nsData;
    }
    
    static {
        NSWakeupAPIImpl.nsWakeupImpl = null;
    }
}
