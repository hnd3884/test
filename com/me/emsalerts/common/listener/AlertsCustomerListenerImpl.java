package com.me.emsalerts.common.listener;

import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.customer.CustomerEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customer.CustomerListener;

public class AlertsCustomerListenerImpl implements CustomerListener
{
    private static Logger logger;
    
    public AlertsCustomerListenerImpl() {
        AlertsCustomerListenerImpl.logger.log(Level.INFO, "Registering AlertsCustomerListenerImpl...");
    }
    
    public void customerAdded(final CustomerEvent customerEvent) {
    }
    
    public void customerDeleted(final CustomerEvent customerEvent) {
        final JSONObject dataObject = new JSONObject();
        final int CUSTOMER_EVENT = 102;
        try {
            dataObject.put("customerEvent", (Object)customerEvent);
            final DCQueue queue = DCQueueHandler.getQueue("ems-userdelete-alert-queue");
            final DCQueueData queueData = new DCQueueData();
            final Long postedTime = System.currentTimeMillis();
            queueData.postTime = postedTime;
            queueData.queueDataType = CUSTOMER_EVENT;
            queueData.fileName = "user-deletion-listener-" + postedTime + ".txt";
            queueData.queueData = dataObject;
            queue.addToQueue(queueData);
        }
        catch (final Exception e) {
            AlertsCustomerListenerImpl.logger.log(Level.INFO, "AlertsCustomerListenerImpl: Exception with add to queue " + e.getMessage());
        }
    }
    
    public void customerUpdated(final CustomerEvent customerEvent) {
    }
    
    public void firstCustomerAdded(final CustomerEvent customerEvent) {
    }
    
    static {
        AlertsCustomerListenerImpl.logger = Logger.getLogger("EMSAlertsLogger");
    }
}
