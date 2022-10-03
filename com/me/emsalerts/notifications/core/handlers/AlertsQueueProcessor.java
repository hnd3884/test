package com.me.emsalerts.notifications.core.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import com.me.emsalerts.notifications.core.AlertDetails;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class AlertsQueueProcessor extends DCQueueDataProcessor
{
    private String sourceClass;
    String[] alertHandlers;
    Logger alertsLogger;
    
    public AlertsQueueProcessor() {
        this.sourceClass = "AlertsDataProcessor";
        this.alertHandlers = new String[] { "com.me.emsalerts.notifications.core.handlers.EmailAlertsHandler", "com.me.emsalerts.notifications.core.handlers.SMSAlertsHandler" };
        this.alertsLogger = Logger.getLogger("EMSAlertsLogger");
    }
    
    public void processData(final DCQueueData qData) {
        final String sourceMethod = "processData";
        try {
            if (qData == null) {
                this.alertsLogger.log(Level.WARNING, "qData is null");
                return;
            }
            final AlertDetails alertDetails = (AlertDetails)qData.queueData;
            for (final Object handler : this.alertHandlers) {
                final Class alertsHandlerClass = Class.forName((String)handler);
                if (this.isAlertConfigured(alertsHandlerClass, alertDetails)) {
                    if (this.isMediumSettingsConfigured(alertsHandlerClass, alertDetails)) {
                        final HashMap alertInputData = this.prepareAlertsData(alertsHandlerClass, alertDetails);
                        final Object recipients = this.getRecipients(alertsHandlerClass, alertDetails);
                        if (recipients != null) {
                            this.sendAlert(alertsHandlerClass, recipients, alertInputData);
                        }
                        else {
                            this.alertsLogger.log(Level.WARNING, "Could not find the recipient info for alert " + alertDetails.eventCode + " and medium id" + alertDetails.mediumID);
                        }
                    }
                    else {
                        this.alertsLogger.log(Level.WARNING, "Medium Settings not configured while processing alert " + alertDetails.eventCode + " for medium id " + alertDetails.mediumID);
                    }
                }
                else {
                    this.alertsLogger.log(Level.WARNING, "Alert not configured for Event code " + alertDetails.eventCode + "  and Medium Id " + alertDetails.mediumID);
                }
            }
        }
        catch (final Exception e) {
            this.alertsLogger.log(Level.SEVERE, "Exception occured in " + sourceMethod + " method in class " + this.sourceClass);
            this.alertsLogger.log(Level.SEVERE, "Queue data received during exception :", qData);
            this.alertsLogger.log(Level.SEVERE, "Exception occured while processing alerts queue data ", e);
        }
    }
    
    private Object getRecipients(final Class alertsHandlerClass, final AlertDetails alertDetails) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        final Method recipientMethod = alertsHandlerClass.getMethod("getRecipients", Long.class, Long.class, Long.class, Long.class);
        final Object recipients = recipientMethod.invoke(alertsHandlerClass.newInstance(), alertDetails.eventCode, alertDetails.mediumID, alertDetails.customerID, alertDetails.technicianID);
        return recipients;
    }
    
    public HashMap prepareAlertsData(final Class alertsHandlerClass, final AlertDetails alertDetails) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        final Method constructDataMethod = alertsHandlerClass.getMethod("constructAlertData", AlertDetails.class);
        final HashMap alertsData = (HashMap)constructDataMethod.invoke(alertsHandlerClass.newInstance(), alertDetails);
        return alertsData;
    }
    
    public HashMap sendAlert(final Class alertHandlerClass, final Object recipients, final HashMap alertInputData) throws Exception {
        final Method sendAlertsMethod = alertHandlerClass.getMethod("sendAlert", Object.class, HashMap.class);
        final HashMap alertsResponse = (HashMap)sendAlertsMethod.invoke(alertHandlerClass.newInstance(), recipients, alertInputData);
        return alertsResponse;
    }
    
    public boolean isAlertConfigured(final Class alertHandlerClass, final AlertDetails alertDetails) throws Exception {
        final Method alertConfiguredMethod = alertHandlerClass.getMethod("isAlertConfigured", AlertDetails.class);
        return (boolean)alertConfiguredMethod.invoke(alertHandlerClass.newInstance(), alertDetails);
    }
    
    public boolean isMediumSettingsConfigured(final Class alertHandlerClass, final AlertDetails alertDetails) throws Exception {
        final Method mediumConfiguredMethod = alertHandlerClass.getMethod("isMediumSettingsConfigured", AlertDetails.class);
        return (boolean)mediumConfiguredMethod.invoke(alertHandlerClass.newInstance(), alertDetails);
    }
}
