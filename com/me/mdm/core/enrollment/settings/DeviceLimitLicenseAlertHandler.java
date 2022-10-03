package com.me.mdm.core.enrollment.settings;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.emsalerts.notifications.core.TemplatesDAOUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import com.me.emsalerts.notifications.core.handlers.EmailAlertsHandler;
import com.me.emsalerts.notifications.core.AlertDetails;
import com.me.emsalerts.notifications.core.TemplatesUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.time.LocalDate;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Properties;
import com.me.mdm.server.alerts.AlertConstants;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;

public class DeviceLimitLicenseAlertHandler
{
    private Logger logger;
    public static final String SPECIFIED_PERCENTAGE = "$mdm.specifiedpercent$";
    public static final String USER_NAME = "$mdm.user_name$";
    public static final String ENROLLED_COUNT = "$mdm.enrolled_count$";
    public static final String LICENSE_COUNT = "$mdm.license_count$";
    public static final String TO_PURCHASE_COUNT = "$mdm.to_purchase_count$";
    public static final String CUSTOMER_NAME = "$device.customername$";
    public static final String MONTH = "$mdm.month$";
    
    public DeviceLimitLicenseAlertHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static DeviceLimitLicenseAlertHandler getInstance() {
        return new DeviceLimitLicenseAlertHandler();
    }
    
    public void checkAndSendLicenseAlertMail(final DeviceEvent deviceEvent) throws Exception {
        try {
            final JSONObject licensePercentJSON = DeviceLicensePercentSettingsHandler.getInstance().getDeviceLicensePercentSettings(deviceEvent.customerID);
            final int maxThreshold = licensePercentJSON.optInt("max_threshold", -1);
            final int minThreshold = licensePercentJSON.optInt("min_threshold", -1);
            final int enrolledCount = MDMEnrollmentUtil.getInstance().getEnrolledDeviceCount(deviceEvent.customerID);
            final int licenseCount = ManagedDeviceHandler.getInstance().getPurchasedMobileDeviceCount(deviceEvent.customerID);
            licensePercentJSON.put("enrolled_count", enrolledCount);
            licensePercentJSON.put("license_count", licenseCount);
            licensePercentJSON.put("customer_name", (Object)MDMCustomerInfoUtil.getInstance().getCustomerName(deviceEvent.customerID));
            licensePercentJSON.put("customer_id", (Object)deviceEvent.customerID);
            if (maxThreshold != -1 && licenseCount != -1) {
                DMSecurityLogger.info(this.logger, DeviceLimitLicenseAlertHandler.class.getName(), "checkAndSendLicenseAlertMail", "Sending license exceeded specified limit alert mails with data: {0}", (Object)licensePercentJSON);
                this.sendLicenseExceededMail(licensePercentJSON);
            }
            if (minThreshold != -1 && licenseCount != -1) {
                DMSecurityLogger.info(this.logger, DeviceLimitLicenseAlertHandler.class.getName(), "checkAndSendLicenseAlertMail", "Sending license below specified limit alert mails with data: {0}", (Object)licensePercentJSON);
                this.sendLicenseBelowLimitMail(licensePercentJSON);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checkAndSendLicenseAlertMail", e);
            throw e;
        }
    }
    
    public void sendLicenseExceededMail(final JSONObject alertJSON) throws Exception {
        final int enrolledCount = alertJSON.getInt("enrolled_count");
        final int maxThreshold = alertJSON.getInt("max_threshold");
        final int licenseCount = alertJSON.getInt("license_count");
        final Long customerId = JSONUtil.optLongForUVH(alertJSON, "customer_id", Long.valueOf(-1L));
        final Long technicianId = this.getEventLastUpdatedUser(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED, customerId);
        if (enrolledCount >= maxThreshold * licenseCount / 100) {
            final Properties alertProps = new Properties();
            ((Hashtable<String, Integer>)alertProps).put("$mdm.specifiedpercent$", maxThreshold);
            ((Hashtable<String, String>)alertProps).put("$mdm.user_name$", DMUserHandler.getUserNameFromUserID(this.getEventLastUpdatedUser(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED, JSONUtil.optLongForUVH(alertJSON, "customer_id", Long.valueOf(-1L)))));
            ((Hashtable<String, Integer>)alertProps).put("$mdm.enrolled_count$", enrolledCount);
            ((Hashtable<String, Integer>)alertProps).put("$mdm.license_count$", licenseCount);
            ((Hashtable<String, Integer>)alertProps).put("$mdm.to_purchase_count$", licenseCount - enrolledCount);
            ((Hashtable<String, String>)alertProps).put("$device.customername$", alertJSON.getString("customer_name"));
            ((Hashtable<String, String>)alertProps).put("$mdm.month$", LocalDate.now().getMonth().toString());
            this.sendAlertMail(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_EXCEEDED, customerId, technicianId, alertProps);
            MessageProvider.getInstance().unhideMessage("MDM_DEVICE_LICENSE_PERCENT_ALERT", customerId);
        }
        else {
            MessageProvider.getInstance().hideMessage("MDM_DEVICE_LICENSE_PERCENT_ALERT", customerId);
        }
    }
    
    public void sendLicenseBelowLimitMail(final JSONObject alertJSON) throws Exception {
        final int enrolledCount = alertJSON.getInt("enrolled_count");
        final int minThreshold = alertJSON.getInt("min_threshold");
        final int licenseCount = alertJSON.getInt("license_count");
        final Long customerId = JSONUtil.optLongForUVH(alertJSON, "customer_id", Long.valueOf(-1L));
        final Long technicianId = this.getEventLastUpdatedUser(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT, customerId);
        if (enrolledCount < minThreshold * licenseCount / 100) {
            final Properties alertProps = new Properties();
            ((Hashtable<String, Integer>)alertProps).put("$mdm.specifiedpercent$", minThreshold);
            ((Hashtable<String, String>)alertProps).put("$device.customername$", alertJSON.getString("customer_name"));
            ((Hashtable<String, String>)alertProps).put("$mdm.user_name$", DMUserHandler.getUserNameFromUserID(technicianId));
            ((Hashtable<String, Integer>)alertProps).put("$mdm.license_count$", licenseCount);
            ((Hashtable<String, String>)alertProps).put("$mdm.month$", LocalDate.now().getMonth().toString());
            ((Hashtable<String, Integer>)alertProps).put("$mdm.enrolled_count$", enrolledCount);
            this.sendAlertMail(AlertConstants.LicenseAlertConstant.LICENSE_PERCENT_BELOW_MINIMUM_LIMIT, customerId, technicianId, alertProps);
        }
    }
    
    public void sendAlertMail(final Long eventCode, final Long customerId, final Long technicianID, final Properties alertProps) {
        final TemplatesUtil templatesUtil = new TemplatesUtil();
        try {
            final AlertDetails alertDetails = new AlertDetails(eventCode, customerId, technicianID, false);
            alertDetails.alertProps = alertProps;
            alertDetails.mediumID = templatesUtil.getMediumIdByName("EMAIL");
            final EmailAlertsHandler emailAlertsHandler = new EmailAlertsHandler();
            final HashMap alertData = emailAlertsHandler.constructAlertData(alertDetails);
            DMSecurityLogger.info(this.logger, DeviceLimitLicenseAlertHandler.class.getName(), "sendAlertMail", "sending mail alert with data: {0}", (Object)alertData);
            final JSONArray emailIdList = this.getEmailAddressForEvent(customerId, technicianID, Arrays.asList(templatesUtil.getEventIDForCode(eventCode)));
            for (int i = 0; i < emailIdList.length(); ++i) {
                final String email = emailIdList.getString(i);
                emailAlertsHandler.sendAlert((Object)email, alertData);
            }
            this.logger.log(Level.INFO, "Alert sent for AlertType = {0}", eventCode);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exception while sending mail for Alert type = " + n);
        }
    }
    
    public JSONArray getEmailAddressForEvent(final Long customerID, final Long userID, final List eventIDList) {
        try {
            final DataObject mediumDO = MDMUtil.getPersistence().get("EventMediums", new Criteria(Column.getColumn("EventMediums", "MEDIUM_NAME"), (Object)"EMAIL", 0));
            final Long mediumID = (Long)(mediumDO.isEmpty() ? -1L : mediumDO.getFirstRow("EventMediums").get("MEDIUM_ID"));
            final DataObject eventTemplateDO = new TemplatesDAOUtil().getAlertsForEventID(eventIDList, customerID, userID, mediumID);
            if (!eventTemplateDO.isEmpty()) {
                final Row eventTemplateRow = eventTemplateDO.getFirstRow("EventToTemplateRel");
                final Long eventToTemplateRelID = (Long)eventTemplateRow.get("EVENT_TEMPLATE_REL_ID");
                final DataObject eventEmailDetailsDO = MDMUtil.getPersistence().get("EventEmailDetails", new Criteria(Column.getColumn("EventEmailDetails", "EVENT_TEMPLATE_REL_ID"), (Object)eventToTemplateRelID, 0));
                if (!eventEmailDetailsDO.isEmpty()) {
                    final JSONArray emailJSONArray = new JSONArray();
                    final Iterator iterator = eventEmailDetailsDO.getRows("EventEmailDetails");
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        emailJSONArray.put((Object)String.valueOf(row.get("EMAIL_ADDRESS")));
                    }
                    return emailJSONArray;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occured while getting email address for an event", e);
        }
        return new JSONArray();
    }
    
    public Long getEventLastUpdatedUser(final Long eventCode, final Long customerID) throws DataAccessException {
        final TemplatesUtil templatesUtil = new TemplatesUtil();
        final Long eventID = templatesUtil.getEventIDForCode(eventCode);
        final SelectQuery eventsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EventToTemplateRel"));
        final Join customerJoin = new Join("EventToTemplateRel", "EventTemplateCustomerRel", new String[] { "EVENT_TEMPLATE_REL_ID" }, new String[] { "EVENT_TEMPLATE_REL_ID" }, 2);
        final Criteria eventIDCriteria = new Criteria(Column.getColumn("EventToTemplateRel", "EVENT_ID"), (Object)eventID, 0);
        final Criteria mediumIDCriteria = new Criteria(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"), (Object)templatesUtil.getMediumIdByName("EMAIL"), 0);
        final Criteria customerIDCriteria = new Criteria(Column.getColumn("EventTemplateCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        eventsQuery.addJoin(customerJoin);
        eventsQuery.setCriteria(eventIDCriteria.and(mediumIDCriteria).and(customerIDCriteria));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "EVENT_TEMPLATE_REL_ID"));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "CREATED_BY"));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "MEDIUM_ID"));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "TEMPLATE_ID"));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "EVENT_ID"));
        eventsQuery.addSelectColumn(Column.getColumn("EventToTemplateRel", "ENABLED_STATUS"));
        eventsQuery.addSelectColumn(Column.getColumn("EventTemplateCustomerRel", "EVENT_TEMPLATE_REL_ID"));
        eventsQuery.addSelectColumn(Column.getColumn("EventTemplateCustomerRel", "CUSTOMER_ID"));
        final DataObject eventsDO = MDMUtil.getPersistence().get(eventsQuery);
        Long userID = null;
        if (!eventsDO.isEmpty()) {
            userID = (Long)eventsDO.getRow("EventToTemplateRel").get("CREATED_BY");
        }
        return userID;
    }
}
