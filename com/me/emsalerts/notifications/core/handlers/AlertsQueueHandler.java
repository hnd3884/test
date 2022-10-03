package com.me.emsalerts.notifications.core.handlers;

import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.emsalerts.notifications.core.AlertDetails;

public interface AlertsQueueHandler
{
    public static final String ALERTS_QUEUE = "ems-alerts-queue";
    
    default void addToAlertsQueue(final AlertDetails alertDetails) {
        try {
            final Long eventCode = alertDetails.eventCode;
            final Long technicianID = alertDetails.technicianID;
            final DCQueue queue = DCQueueHandler.getQueue("ems-alerts-queue");
            final DCQueueData dcQueueData = new DCQueueData();
            dcQueueData.fileName = "alert_" + eventCode + "_" + technicianID + "_" + System.currentTimeMillis() + ".txt";
            dcQueueData.postTime = System.currentTimeMillis();
            dcQueueData.queueData = alertDetails;
            queue.addToQueue(dcQueueData, alertDetails.toString());
        }
        catch (final Exception e) {
            final Logger alertsLogger = Logger.getLogger("EMSAlertsLogger");
            alertsLogger.log(Level.WARNING, "Exception occured while adding to alerts queue ", e);
        }
    }
}
