package com.me.mdm.server.notification;

import java.util.Iterator;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class PollingImpl extends WakeUpProcessor
{
    private static PollingImpl pollingImpl;
    private Logger logger;
    
    public PollingImpl() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static PollingImpl getInstance() {
        if (PollingImpl.pollingImpl == null) {
            PollingImpl.pollingImpl = new PollingImpl();
        }
        return PollingImpl.pollingImpl;
    }
    
    @Override
    public HashMap wakeUpDevices(final List resourceList, final int notificationType) {
        this.logger.log(Level.INFO, "Poll processing begins");
        List hideMessageList = new ArrayList();
        hideMessageList = this.getHideMessages(notificationType);
        this.hideErrMessages(hideMessageList);
        final Map notificationData = new HashMap();
        notificationData.put("NotificationCompleteList", new ArrayList(resourceList));
        notificationData.put("NotificationSuccessList", resourceList);
        notificationData.put("NotificationFailureList", new ArrayList());
        notificationData.put("NotificationReRegisterList", new ArrayList());
        notificationData.put("NotificationError", -1);
        notificationData.put("NotificationType", notificationType);
        this.processWakeUpResults(notificationData);
        this.logger.log(Level.INFO, "Poll processing ends");
        return new HashMap();
    }
    
    private List getHideMessages(final int notificationType) {
        final List hideMessageList = new ArrayList();
        if (notificationType == 3) {
            hideMessageList.add("WNS_URL_BLOCKED");
            hideMessageList.add("WNS_WAKEUP_FAILED_CONTACT_SUPPORT");
            hideMessageList.add("WNS_URL_PROXY_AUTH_FAILED");
        }
        return hideMessageList;
    }
    
    private void hideErrMessages(final List hideMessageList) {
        if (hideMessageList != null) {
            for (final String message : hideMessageList) {
                MessageProvider.getInstance().hideMessage(message);
            }
        }
    }
    
    static {
        PollingImpl.pollingImpl = null;
    }
}
