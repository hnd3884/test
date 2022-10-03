package com.me.emsalerts.notifications.core;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.emsalerts.notifications.core.handlers.AlertsQueueHandler;

public class AlertsAPIFactory
{
    private static AlertsQueueHandler alertsQueueHandler;
    
    public static synchronized AlertsQueueHandler getAlertsQueueHandler() {
        if (AlertsAPIFactory.alertsQueueHandler == null) {
            AlertsAPIFactory.alertsQueueHandler = (AlertsQueueHandler)ApiFactoryProvider.getImplClassInstance("ALERTS_QUEUE_HANDLER");
        }
        return AlertsAPIFactory.alertsQueueHandler;
    }
    
    static {
        AlertsAPIFactory.alertsQueueHandler = null;
    }
}
