package com.me.mdm.server.notification;

import java.util.Map;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleUsersDirectory;
import com.me.idps.core.util.DirectoryUtil;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ChromeInServerNotificationImpl extends WakeUpProcessor
{
    private static ChromeInServerNotificationImpl chromeInServerNotificationImpl;
    private Logger logger;
    
    public ChromeInServerNotificationImpl() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static ChromeInServerNotificationImpl getInstance() {
        if (ChromeInServerNotificationImpl.chromeInServerNotificationImpl == null) {
            ChromeInServerNotificationImpl.chromeInServerNotificationImpl = new ChromeInServerNotificationImpl();
        }
        return ChromeInServerNotificationImpl.chromeInServerNotificationImpl;
    }
    
    @Override
    public HashMap wakeUpDevices(final List resourceList, final int notificationType) {
        this.logger.log(Level.INFO, "Inserver Chrome processing begins");
        for (final Object resourceId : resourceList) {
            try {
                final JSONObject wakeupData = new JSONObject();
                if (notificationType == 4) {
                    final String udid = (String)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", resourceId, "UDID");
                    final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID((Long)resourceId);
                    wakeupData.put("UDID", (Object)udid);
                    wakeupData.put("ActAs", (Object)"Device");
                    wakeupData.put("GOOGLE_ESA", (Object)GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT).put("CUSTOMER_ID", (Object)customerId));
                }
                else {
                    final Long customerId2 = CustomerInfoUtil.getInstance().getCustomerIDForResID((Long)resourceId);
                    final JSONObject esaDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId2, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
                    final String emailAddress = DirectoryUtil.getInstance().getFirstDirObjAttrValue((Long)resourceId, Long.valueOf(106L));
                    final GoogleUsersDirectory userDirectory = new GoogleUsersDirectory();
                    userDirectory.initialize(esaDetails);
                    final JSONObject userDetails = userDirectory.getUser(esaDetails, emailAddress);
                    if (userDetails.has("users")) {
                        wakeupData.put("GUID", (Object)userDetails.getJSONObject("users").keys().next());
                        wakeupData.put("ActAs", (Object)"User");
                        wakeupData.put("GOOGLE_ESA", (Object)esaDetails.put("CUSTOMER_ID", (Object)customerId2));
                    }
                }
                final DCQueue queue = DCQueueHandler.getQueue("chrome-inserver-wakeup");
                final DCQueueData queueData = new DCQueueData();
                queueData.postTime = System.currentTimeMillis();
                queueData.queueData = wakeupData.toString();
                queue.addToQueue(queueData);
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, null, ex);
            }
        }
        final Map notificationData = new HashMap();
        notificationData.put("NotificationCompleteList", new ArrayList(resourceList));
        notificationData.put("NotificationSuccessList", resourceList);
        notificationData.put("NotificationFailureList", new ArrayList());
        notificationData.put("NotificationReRegisterList", new ArrayList());
        notificationData.put("NotificationError", -1);
        notificationData.put("NotificationType", notificationType);
        this.processWakeUpResults(notificationData);
        this.logger.log(Level.INFO, "InServer Chrome processing ends");
        return (HashMap)notificationData;
    }
    
    static {
        ChromeInServerNotificationImpl.chromeInServerNotificationImpl = null;
    }
}
