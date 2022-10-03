package com.me.emsalerts.notifications.core.handlers;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import com.me.emsalerts.common.tracking.AlertsTrackingParamUtil;
import com.me.emsalerts.sms.core.CustomSMSProvider;
import java.util.LinkedHashMap;
import com.me.emsalerts.sms.core.SMSUtil;
import org.json.JSONArray;
import com.me.emsalerts.notifications.core.AlertsUtil;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.emsalerts.notifications.core.TemplatesUtil;
import com.me.emsalerts.notifications.core.AlertDetails;
import java.util.logging.Logger;

public class SMSAlertsHandler implements EMSAlertsHandlerAPI
{
    Logger alertsLogger;
    
    public SMSAlertsHandler() {
        this.alertsLogger = Logger.getLogger("EMSAlertsLogger");
    }
    
    @Override
    public boolean isAlertConfigured(final AlertDetails alertDetails) throws Exception {
        alertDetails.mediumID = new TemplatesUtil().getMediumIdByName("SMS");
        return super.isAlertConfigured(alertDetails);
    }
    
    @Override
    public HashMap constructAlertData(final AlertDetails alertDetails) throws Exception {
        final HashMap alertData = new HashMap();
        alertDetails.mediumID = new TemplatesUtil().getMediumIdByName("SMS");
        final JSONObject content = (JSONObject)super.constructAlertData(alertDetails);
        final String smsContent = (String)content.get("content");
        alertData.put("content", new AlertsUtil().expandedContent(alertDetails.alertProps, smsContent));
        return alertData;
    }
    
    @Override
    public Object sendAlert(final Object recipient, final HashMap alertsData) {
        final JSONArray sendSMSResponses = new JSONArray();
        final HashMap response = new HashMap();
        try {
            final String smsRecipients = (String)recipient;
            final String[] phoneNumber = smsRecipients.split(",");
            String message = alertsData.get("content");
            final HashMap smsConfiguration = SMSUtil.checkSMSConfig();
            final HashMap smsServices = SMSUtil.getServiceDetails(smsConfiguration.get("smsConfigID"));
            final Long serviceID = Long.valueOf(String.valueOf(smsServices.get("serviceID")));
            final Long actionID = SMSUtil.getActionID(serviceID, "sendsms");
            final Hashtable smsActionConfig = SMSUtil.getSMSActionConfigDetails(actionID);
            if (smsActionConfig.get("unicode") instanceof LinkedHashMap) {
                final LinkedHashMap unicode = smsActionConfig.get("unicode");
                final boolean unicodeEnabled = unicode.get("isConfigured") != null && unicode.get("isConfigured");
                if (unicodeEnabled) {
                    message = message.replaceAll("\\\\t", " ");
                    message = message.replaceAll("\\\\n", "\n");
                    message = SMSUtil.convertToUnicode(message);
                }
            }
            for (int i = 0; i < phoneNumber.length; ++i) {
                final JSONObject sendSMSResponse = new CustomSMSProvider().sendMessage(smsActionConfig, phoneNumber[i], message);
                if (sendSMSResponse.get("smsStatus").toString().equalsIgnoreCase("success")) {
                    AlertsTrackingParamUtil.incrementTrackingParam("SMS_SUCCESS_COUNT");
                }
                else {
                    AlertsTrackingParamUtil.incrementTrackingParam("SMS_FAILURE_COUNT");
                }
                sendSMSResponses.put((Object)sendSMSResponse);
            }
            response.put("successResponse", sendSMSResponses);
        }
        catch (final Exception e) {
            AlertsTrackingParamUtil.incrementTrackingParam("SMS_FAILURE_COUNT");
            this.alertsLogger.log(Level.WARNING, "Exception occured while sending SMS ", e);
        }
        return response;
    }
    
    @Override
    public String getRecipients(final Long eventCode, final Long mediumID, final Long customerID, final Long userID) {
        try {
            final TemplatesUtil templatesUtil = new TemplatesUtil();
            final Long eventID = templatesUtil.getEventIDForCode(eventCode);
            final List eventIDList = new ArrayList();
            eventIDList.add(eventID);
            final Long[] recipientUserID = templatesUtil.getSMSUserIDForEvent(customerID, userID, eventIDList);
            String phoneNumber = "";
            if (recipientUserID.length != 0) {
                phoneNumber = new AlertsUtil().getPhoneNumberOfUserID(recipientUserID);
            }
            final String directPhoneNumber = (String)templatesUtil.getPhoneNoForEvent(customerID, userID, eventIDList);
            if (!directPhoneNumber.equals("")) {
                if (phoneNumber.equals("")) {
                    phoneNumber = directPhoneNumber;
                }
                else {
                    phoneNumber = phoneNumber + "," + directPhoneNumber;
                }
            }
            return phoneNumber;
        }
        catch (final Exception e) {
            this.alertsLogger.log(Level.WARNING, "Exception occured while getting receipients");
            return null;
        }
    }
    
    @Override
    public boolean isMediumSettingsConfigured(final AlertDetails alertDetails) {
        final boolean isSMSSettingsConfigured = new CustomSMSProvider().isSMSSettingsConfigured();
        try {
            if (isSMSSettingsConfigured) {
                final DataObject smsServiceDO = DataAccess.get("SMSServices", new Criteria(Column.getColumn("SMSServices", "SERVICENAME"), (Object)"custom", 0));
                if (!smsServiceDO.isEmpty()) {
                    final int smsServiceStatus = (int)smsServiceDO.getFirstRow("SMSServices").get("STATUS");
                    if (smsServiceStatus == 1) {
                        return true;
                    }
                }
                return false;
            }
        }
        catch (final Exception e) {
            this.alertsLogger.log(Level.WARNING, "Exception while checking isMediumSettingsConfigured ", e);
            return false;
        }
        return isSMSSettingsConfigured;
    }
}
