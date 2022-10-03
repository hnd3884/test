package com.me.mdm.core.enrollment.settings;

import java.util.List;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import java.util.Map;
import org.json.JSONArray;
import java.util.logging.Level;
import com.me.emsalerts.notifications.core.MediumDAOUtil;
import java.util.LinkedHashMap;
import com.me.emsalerts.notifications.core.TemplatesDAOUtil;
import java.util.HashMap;
import com.me.emsalerts.notifications.core.TemplatesUtil;
import com.me.mdm.server.alerts.AlertConstants;
import com.me.devicemanagement.framework.server.license.LicensePercentHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DeviceLicensePercentSettingsHandler
{
    private Logger logger;
    
    public DeviceLicensePercentSettingsHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static DeviceLicensePercentSettingsHandler getInstance() {
        return new DeviceLicensePercentSettingsHandler();
    }
    
    public void addOrUpdateDeviceLicensePercent(final JSONObject requestJSON) {
        final Long customerId = JSONUtil.optLongForUVH(requestJSON, "customerId", Long.valueOf(-1L));
        final int maxThreshold = requestJSON.getInt("max_threshold");
        final int minThreshold = requestJSON.getInt("min_threshold");
        final JSONArray emailJSONArray = requestJSON.getJSONArray("alert_email_ids");
        new LicensePercentHandler().updateDeviceLicPercent((long)customerId, AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED, maxThreshold, AlertConstants.LicenseAlertConstant.GREATER_THAN);
        new LicensePercentHandler().updateDeviceLicPercent((long)customerId, AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT, minThreshold, AlertConstants.LicenseAlertConstant.LESS_THAN);
        final Long userId = JSONUtil.optLongForUVH(requestJSON, "userId", Long.valueOf(-1L));
        final TemplatesUtil templatesUtil = new TemplatesUtil();
        try {
            final Long eventID = templatesUtil.getEventIDForCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED);
            final Long subCategoryID = templatesUtil.getSubCategoryIDForEventCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED);
            final Long mediumId = templatesUtil.getMediumIdByName("EMAIL");
            Long templateID = templatesUtil.getTemplateIDForUser(subCategoryID, userId, customerId);
            if (templateID == -1L) {
                final Map templateMap = new HashMap();
                templateMap.put("userID", userId);
                templateMap.put("subCategoryID", subCategoryID);
                final TemplatesDAOUtil templatesDAOUtil = new TemplatesDAOUtil();
                templateMap.put("templateName", "Template " + System.currentTimeMillis());
                templateMap.put("description", "Template for event " + AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED);
                templateID = new TemplatesDAOUtil().addOrUpdateTemplateDetails(templateMap, (Long)null);
            }
            DataObject eventsDO = templatesUtil.getEventTemplateRelDO(eventID, templateID, mediumId, userId, customerId);
            Long eventTemplateRelId = null;
            if (!eventsDO.containsTable("EventTemplateCustomerRel")) {
                templatesUtil.addOrUpdateEventTemplateRel(eventID, templateID, mediumId, userId, customerId, true);
                eventsDO = templatesUtil.getEventTemplateRelDO(eventID, templateID, mediumId, userId, customerId);
                final TemplatesDAOUtil templatesDAOUtil2 = new TemplatesDAOUtil();
                templatesDAOUtil2.addTemplateCustomerRel(templateID, customerId);
            }
            eventTemplateRelId = (Long)eventsDO.getFirstRow("EventTemplateCustomerRel").get("EVENT_TEMPLATE_REL_ID");
            final LinkedHashMap mediumMap = new LinkedHashMap();
            mediumMap.put("mediumID", mediumId);
            mediumMap.put("mediumData", "{\"subject\": \"License Usage exceeded $mdm.specifiedpercent$% of the allocated limit for customer - $device.customername$ \",\"description\": \"Dear $mdm.user_name$, You have enrolled ($mdm.enrolled_count$) devices into MDM. This exceeds $mdm.specifiedpercent$% of the licenses purchased for the month of $mdm.month$. To enroll and managed additional devices, purchase more licenses from MDM. Contact our support team at mdm-support@manageengine.com\"}");
            new MediumDAOUtil().populateMediumData(templateID, mediumMap);
            for (int i = 0; i < emailJSONArray.length(); ++i) {
                final String emailId = emailJSONArray.getJSONObject(i).getString("email");
                templatesUtil.addOrUpdateEmailAddressToEvent(eventTemplateRelId, emailId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while configuring alert for License Percent exceeded", e);
        }
        try {
            final Long eventID = templatesUtil.getEventIDForCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT);
            final Long subCategoryID = templatesUtil.getSubCategoryIDForEventCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT);
            Long templateID2 = templatesUtil.getTemplateIDForUser(subCategoryID, userId, customerId);
            if (templateID2 == -1L) {
                final Map templateMap2 = new HashMap();
                templateMap2.put("userID", userId);
                templateMap2.put("subCategoryID", subCategoryID);
                final TemplatesDAOUtil templatesDAOUtil3 = new TemplatesDAOUtil();
                templateMap2.put("templateName", "Template " + System.currentTimeMillis());
                templateMap2.put("description", "Template for event " + AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT);
                templateID2 = new TemplatesDAOUtil().addOrUpdateTemplateDetails(templateMap2, (Long)null);
            }
            final Long mediumId2 = templatesUtil.getMediumIdByName("EMAIL");
            DataObject eventsDO = templatesUtil.getEventTemplateRelDO(eventID, templateID2, mediumId2, userId, customerId);
            Long eventTemplateRelId = null;
            if (!eventsDO.containsTable("EventTemplateCustomerRel")) {
                templatesUtil.addOrUpdateEventTemplateRel(eventID, templateID2, mediumId2, userId, customerId, true);
                eventsDO = templatesUtil.getEventTemplateRelDO(eventID, templateID2, mediumId2, userId, customerId);
                final TemplatesDAOUtil templatesDAOUtil2 = new TemplatesDAOUtil();
                templatesDAOUtil2.addTemplateCustomerRel(templateID2, customerId);
            }
            eventTemplateRelId = (Long)eventsDO.getFirstRow("EventTemplateCustomerRel").get("EVENT_TEMPLATE_REL_ID");
            final LinkedHashMap mediumMap = new LinkedHashMap();
            mediumMap.put("mediumID", mediumId2);
            mediumMap.put("mediumData", "{\"subject\": \"License Usage reached $mdm.specifiedpercent$ of the allocated limit for customer - $device.customername$ \",\"description\": \"Dear $mdm.user_name$, You have enrolled $mdm.enrolled_count$ devices, and have purchased $mdm.license_count$ licenses for the month of $mdm.month$. Kindly purchase only the required number of licenses for the devices enrolled.\"}");
            new MediumDAOUtil().populateMediumData(templateID2, mediumMap);
            for (int i = 0; i < emailJSONArray.length(); ++i) {
                final String emailId = emailJSONArray.getJSONObject(i).getString("email");
                templatesUtil.addOrUpdateEmailAddressToEvent(eventTemplateRelId, emailId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while configuring alert for License Percent below minimum limit", e);
        }
    }
    
    public JSONObject getDeviceLicensePercentSettingsWithEmail(final Long customerId, final Long userId) throws Exception {
        try {
            final JSONObject responseJSON = new JSONObject();
            final int maxThreshold = new LicensePercentHandler().getDeviceLicPercent(customerId, AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED, AlertConstants.LicenseAlertConstant.GREATER_THAN);
            final int minThreshold = new LicensePercentHandler().getDeviceLicPercent(customerId, AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT, AlertConstants.LicenseAlertConstant.LESS_THAN);
            if (maxThreshold != -1) {
                responseJSON.put("max_threshold", maxThreshold);
            }
            if (minThreshold != -1) {
                responseJSON.put("min_threshold", minThreshold);
            }
            final TemplatesUtil templatesUtil = new TemplatesUtil();
            final ArrayList eventList = new ArrayList();
            eventList.add(templatesUtil.getEventIDForCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED));
            eventList.add(templatesUtil.getEventIDForCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT));
            final String emailId = templatesUtil.getEmailAddressForEvent(customerId, userId, (List)eventList);
            final JSONObject emailJSON = new JSONObject();
            emailJSON.put("email", (Object)emailId);
            final JSONArray emailArray = new JSONArray();
            emailArray.put((Object)emailJSON);
            responseJSON.put("alert_email_ids", (Object)emailArray);
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceLicensePercentSettings() -- ", e);
            throw e;
        }
    }
    
    public JSONObject getDeviceLicensePercentSettings(final Long customerId) throws Exception {
        try {
            final JSONObject responseJSON = new JSONObject();
            final int maxThreshold = new LicensePercentHandler().getDeviceLicPercent(customerId, AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED, AlertConstants.LicenseAlertConstant.GREATER_THAN);
            final int minThreshold = new LicensePercentHandler().getDeviceLicPercent(customerId, AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT, AlertConstants.LicenseAlertConstant.LESS_THAN);
            if (maxThreshold != -1) {
                responseJSON.put("max_threshold", maxThreshold);
            }
            if (minThreshold != -1) {
                responseJSON.put("min_threshold", minThreshold);
            }
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceLicensePercentSettings() -- ", e);
            throw e;
        }
    }
    
    public void deleteDeviceLicensePercent(final Long customerId, final Long userId) throws Exception {
        try {
            LicensePercentHandler.getInstance().deleteDeviceLicPercent(customerId, AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED);
            LicensePercentHandler.getInstance().deleteDeviceLicPercent(customerId, AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT);
            final TemplatesUtil templatesUtil = new TemplatesUtil();
            final Long mediumId = templatesUtil.getMediumIdByName("EMAIL");
            Long subCategoryID = templatesUtil.getSubCategoryIDForEventCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED);
            Long templateID = templatesUtil.getTemplateIDForUser(subCategoryID, userId, customerId);
            templatesUtil.deleteEventToTemplateRel(mediumId, templateID, userId, customerId);
            subCategoryID = templatesUtil.getSubCategoryIDForEventCode(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT);
            templateID = templatesUtil.getTemplateIDForUser(subCategoryID, userId, customerId);
            templatesUtil.deleteEventToTemplateRel(mediumId, templateID, userId, customerId);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in deleteDeviceLicensePercent() -- ", e);
            throw e;
        }
    }
}
