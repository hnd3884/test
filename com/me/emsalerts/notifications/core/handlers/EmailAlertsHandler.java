package com.me.emsalerts.notifications.core.handlers;

import java.util.List;
import java.util.Arrays;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import com.me.emsalerts.notifications.core.AlertsUtil;
import java.util.HashMap;
import com.me.emsalerts.notifications.core.TemplatesUtil;
import com.me.emsalerts.notifications.core.AlertDetails;
import java.util.logging.Logger;

public class EmailAlertsHandler implements EMSAlertsHandlerAPI
{
    Logger alertsLogger;
    
    public EmailAlertsHandler() {
        this.alertsLogger = Logger.getLogger("EMSAlertsLogger");
    }
    
    @Override
    public boolean isAlertConfigured(final AlertDetails alertDetails) throws Exception {
        alertDetails.mediumID = new TemplatesUtil().getMediumIdByName("EMAIL");
        return super.isAlertConfigured(alertDetails);
    }
    
    @Override
    public HashMap constructAlertData(final AlertDetails alertDetails) throws Exception {
        final HashMap alertData = new HashMap();
        final AlertsUtil alertsUtil = new AlertsUtil();
        final JSONObject content = (JSONObject)super.constructAlertData(alertDetails);
        final String subject = (String)content.get("subject");
        String description = (String)content.get("description");
        description = alertsUtil.appendNote(description);
        alertData.put("subject", alertsUtil.expandedContent(alertDetails.alertProps, subject));
        alertData.put("description", alertsUtil.expandedContent(alertDetails.alertProps, description));
        return alertData;
    }
    
    @Override
    public HashMap sendAlert(final Object recipient, final HashMap alertsData) {
        final String subject = alertsData.get("subject");
        final String description = alertsData.get("description");
        final JSONObject additionalParams = alertsData.getOrDefault("additionalParams", null);
        final Boolean quickSend = alertsData.getOrDefault("quickSend", Boolean.FALSE);
        final String strToAddress = (String)recipient;
        final HashMap alertStatus = new HashMap();
        try {
            final Hashtable<String, String> mailSenderDetails = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails();
            final String fromName = mailSenderDetails.get("mail.fromName");
            final String fromAddress = mailSenderDetails.get("mail.fromAddress");
            if (strToAddress == null || strToAddress.isEmpty()) {
                this.alertsLogger.log(Level.WARNING, "E-Mail address is null.  Cant Proceed!!!");
                return null;
            }
            final String strMessage = description;
            if (strMessage == null || "".equals(strMessage) || "null".equals(strMessage)) {
                this.alertsLogger.log(Level.WARNING, "Mail Body Contents is NULL.  Cant Proceed!!!");
                return null;
            }
            final MailDetails mailDetails = new MailDetails(fromAddress, strToAddress);
            mailDetails.bodyContent = strMessage;
            mailDetails.senderDisplayName = fromName;
            mailDetails.subject = subject;
            if (additionalParams != null && additionalParams.length() > 0) {
                mailDetails.additionalParams = additionalParams;
            }
            if (quickSend) {
                this.alertsLogger.log(Level.WARNING, "send alert mail to the user");
                final JSONObject mailStatus = ApiFactoryProvider.getMailSettingAPI().sendMail(mailDetails);
                alertStatus.put("status", "error");
                if (mailStatus.getBoolean("Status")) {
                    alertStatus.put("status", "success");
                }
            }
            else {
                ApiFactoryProvider.getMailSettingAPI().addToMailQueue(mailDetails, 0);
                this.alertsLogger.log(Level.WARNING, "Successfully added alert mail to mail queue");
                alertStatus.put("status", "success");
            }
        }
        catch (final Exception ex) {
            this.alertsLogger.log(Level.WARNING, "Exception occured at sendAlertMail", ex);
            alertStatus.put("status", "error");
        }
        return alertStatus;
    }
    
    @Override
    public String getRecipients(final Long eventCode, final Long mediumID, final Long customerID, final Long userID) {
        String emailAddresses = "";
        try {
            final TemplatesUtil templatesUtil = new TemplatesUtil();
            final Long eventID = templatesUtil.getEventIDForCode(eventCode);
            emailAddresses = templatesUtil.getEmailAddressForEvent(customerID, userID, Arrays.asList(eventID));
        }
        catch (final Exception e) {
            this.alertsLogger.log(Level.WARNING, "Exception occured while getting the recipients ", e);
        }
        return emailAddresses;
    }
    
    @Override
    public boolean isMediumSettingsConfigured(final AlertDetails alertDetails) {
        return ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured() && !alertDetails.isHelpDeskMode;
    }
}
