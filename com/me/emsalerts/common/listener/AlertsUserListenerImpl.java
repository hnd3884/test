package com.me.emsalerts.common.listener;

import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.authentication.UserEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.authentication.AbstractUserListener;

public class AlertsUserListenerImpl extends AbstractUserListener
{
    private static Logger logger;
    
    public void userDeleted(final UserEvent userEvent) {
        final JSONObject dataObject = new JSONObject();
        dataObject.put("userID", (Object)userEvent.userID);
        final int USER_EVENT = 101;
        try {
            final DCQueue queue = DCQueueHandler.getQueue("ems-userdelete-alert-queue");
            final DCQueueData queueData = new DCQueueData();
            final Long postedTime = System.currentTimeMillis();
            queueData.postTime = postedTime;
            queueData.queueDataType = USER_EVENT;
            queueData.fileName = "user-deletion-listener-" + postedTime + ".txt";
            queueData.queueData = dataObject;
            queue.addToQueue(queueData);
            AlertsUserListenerImpl.logger.log(Level.INFO, "AlertUserListenerImpl: Added to queue ");
        }
        catch (final Exception e) {
            AlertsUserListenerImpl.logger.log(Level.INFO, "AlertUserListenerImpl: Exception with add to queue " + e.getMessage());
        }
    }
    
    static {
        AlertsUserListenerImpl.logger = Logger.getLogger("EMSAlertsLogger");
    }
}
