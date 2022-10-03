package com.me.mdm.server.enrollment;

import java.util.Hashtable;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import com.me.mdm.server.metracker.MEMDMTrackerUtil;
import com.adventnet.ds.query.CaseExpression;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.net.URLEncoder;
import org.json.JSONException;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.inv.MDCustomDetailsRequestHandler;
import com.me.mdm.api.user.UserFacade;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.i18n.I18N;
import org.apache.commons.lang.StringEscapeUtils;
import com.me.devicemanagement.framework.winaccess.ADAccessProvider;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.util.DirectoryAttributeConstants;
import java.util.List;
import org.apache.commons.lang.WordUtils;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.enrollment.ios.MDMProfileInstallationHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Join;
import java.util.HashMap;
import com.me.mdm.server.android.knox.enroll.KnoxActivationManager;
import com.me.mdm.server.android.knox.enroll.KnoxLicenseHandler;
import com.me.devicemanagement.framework.server.alerts.sms.SMSAPI;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.core.EnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.core.EREvent;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.Map;
import com.me.idps.core.util.ADSyncDataHandler;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import java.util.logging.Logger;

public class MDMEnrollmentRequestHandler
{
    public Logger logger;
    public Logger assignUserLogger;
    String sourceClass;
    private static MDMEnrollmentRequestHandler requestHandler;
    
    public MDMEnrollmentRequestHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.assignUserLogger = Logger.getLogger("MDMAssignUser");
        this.sourceClass = "MDMEnrollmentRequestHandler";
    }
    
    public static MDMEnrollmentRequestHandler getInstance() {
        if (MDMEnrollmentRequestHandler.requestHandler == null) {
            MDMEnrollmentRequestHandler.requestHandler = new MDMEnrollmentRequestHandler();
        }
        return MDMEnrollmentRequestHandler.requestHandler;
    }
    
    public Properties addEnrollmentRequest(final Properties properties) {
        final Properties retProperties = new Properties();
        final String sourceMethod = "sendEnrollmentRequest";
        Integer requestState = 2;
        String userName = null;
        try {
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, properties.toString());
            userName = ((Hashtable<K, String>)properties).get("NAME");
            final Long customerID = ((Hashtable<K, Long>)properties).get("CUSTOMER_ID");
            final Boolean addToExsistingUser = ((Hashtable<K, Boolean>)properties).get("addToExistingUser");
            final Long groupId = ((Hashtable<K, Long>)properties).get("GROUP_RESOURCE_ID");
            final int platformType = ((Hashtable<K, Integer>)properties).get("PLATFORM_TYPE");
            final int ownedBy = ((Hashtable<K, Integer>)properties).get("OWNED_BY");
            final String udid = ((Hashtable<K, String>)properties).get("UDID");
            boolean sendInvitation = ((Hashtable<K, Boolean>)properties).get("sendInvitation");
            final boolean byAdmin = properties.get("byAdmin") != null && ((Hashtable<K, Boolean>)properties).get("byAdmin");
            if (byAdmin) {
                sendInvitation = false;
            }
            if (addToExsistingUser == null && udid != null && this.isBulkEnrollUDIDExist(udid)) {
                requestState = 5;
                ((Hashtable<String, Integer>)retProperties).put("ENROLL_STATUS", requestState);
                return retProperties;
            }
            final String domainName = ((Hashtable<K, String>)properties).get("DOMAIN_NETBIOS_NAME");
            if (DMDomainDataHandler.getInstance().isADManagedDomain(domainName, customerID)) {
                properties.putAll(ADSyncDataHandler.getInstance().getDirUserProps(customerID, domainName, userName));
            }
            else {
                ((Hashtable<String, String>)properties).put("FIRST_NAME", userName);
                ((Hashtable<String, String>)properties).put("DISPLAY_NAME", userName);
            }
            final DataObject managedUserObject = ManagedUserHandler.getInstance().addOrUpdateManagedUser(properties);
            final Long managedUserID = (Long)managedUserObject.getFirstValue("ManagedUser", "MANAGED_USER_ID");
            final String mailID = (String)managedUserObject.getFirstValue("ManagedUser", "EMAIL_ADDRESS");
            ((Hashtable<String, Long>)properties).put("MANAGED_USER_ID", managedUserID);
            ((Hashtable<String, String>)properties).put("EMAIL_ADDRESS", mailID);
            final Long enrollmentRequestID = this.addOrUpdateEnrollmentRequest(properties);
            ((Hashtable<String, Long>)properties).put("ENROLLMENT_REQUEST_ID", enrollmentRequestID);
            MDMEnrollmentOTPHandler.getInstance().addEntryInOTPTable(enrollmentRequestID);
            if (properties.get("ENROLLMENT_TYPE") != null && ((Hashtable<K, Integer>)properties).get("ENROLLMENT_TYPE") == 1) {
                final JSONObject json = new JSONObject();
                json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                json.put("IS_INVITED_BY_ADMIN", byAdmin);
                json.put("REGISTRATION_STATUS", 0);
                getInstance().addOrUpdateInvitationDetails(json);
            }
            if (groupId != null && groupId != 0L) {
                this.addEnrollmentGroupTable(enrollmentRequestID, groupId);
            }
            ((Hashtable<String, Long>)properties).put("MANAGED_USER_ID", managedUserID);
            ((Hashtable<String, Long>)properties).put("ENROLLMENT_REQUEST_ID", enrollmentRequestID);
            final String otpPassword = MDMEnrollmentOTPHandler.getInstance().getOTPPassword(enrollmentRequestID);
            final Boolean isSelfEnroll = ((Hashtable<K, Boolean>)properties).get("isSelfEnroll");
            final Boolean isBulkEnroll = ((Hashtable<K, Boolean>)properties).get("isBulkEnroll");
            boolean isSMSSent = false;
            if (sendInvitation && isSelfEnroll == null) {
                if (!properties.containsKey("sendEmail") || ((Hashtable<K, Boolean>)properties).get("sendEmail")) {
                    final EREvent erEvent = new EREvent(String.valueOf(enrollmentRequestID), otpPassword);
                    EnrollmentRequestHandler.getInstance().invokeEnrollmentRequestListeners(erEvent, 6);
                    if (isBulkEnroll == null) {
                        this.setLastEnrollParam(platformType, ownedBy);
                    }
                    ((Hashtable<String, Boolean>)properties).put("NewEnrollReq", true);
                    this.sendEnrollmentMail(properties);
                }
                isSMSSent = (!properties.containsKey("sendSMS") || !((Hashtable<K, Boolean>)properties).get("sendSMS") || this.sendEnrollmentInvitationThroughSMS(properties));
            }
            this.updateStatus(isSMSSent, properties, enrollmentRequestID);
            requestState = 1;
            ((Hashtable<String, Long>)retProperties).put("CUSTOMER_ID", customerID);
            ((Hashtable<String, Long>)retProperties).put("MANAGED_USER_ID", managedUserID);
            ((Hashtable<String, Long>)retProperties).put("ENROLLMENT_REQUEST_ID", enrollmentRequestID);
            ((Hashtable<String, Integer>)retProperties).put("ENROLL_STATUS", requestState);
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred during enrollment request for user :  " + userName, (Throwable)exp);
        }
        return retProperties;
    }
    
    private void updateEnrollmentRemarksAndStatus(final Long enrollmentRequestID, final String remarksMsg) throws Exception {
        final SelectQuery enrollmentRequestQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        enrollmentRequestQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        enrollmentRequestQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "REMARKS"));
        enrollmentRequestQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0));
        final DataObject enrollmentRequestObj = SyMUtil.getPersistence().get(enrollmentRequestQuery);
        if (enrollmentRequestObj.size("DeviceEnrollmentRequest") > 0) {
            final Row enrollmentRequestRow = enrollmentRequestObj.getFirstRow("DeviceEnrollmentRequest");
            enrollmentRequestRow.set("REMARKS", (Object)remarksMsg);
            enrollmentRequestObj.updateRow(enrollmentRequestRow);
            SyMUtil.getPersistence().update(enrollmentRequestObj);
        }
    }
    
    private boolean sendEnrollmentInvitationThroughSMS(final Properties enrollRequestProperties) throws Exception {
        final SMSAPI smsAPI = MDMApiFactoryProvider.getSMSAPI();
        final boolean smsconfigured = smsAPI.isSMSSettingsConfigured();
        final int creditsAvailable = smsAPI.getRemainingCredits();
        final Long enrollmentRequestID = ((Hashtable<K, Long>)enrollRequestProperties).get("ENROLLMENT_REQUEST_ID");
        int senderID = 1;
        if (smsconfigured && creditsAvailable > 0) {
            final Integer platformType = ((Hashtable<K, Integer>)enrollRequestProperties).get("PLATFORM_TYPE");
            Properties smsProperties = new Properties();
            ((Hashtable<String, Long>)smsProperties).put("ENROLLMENT_REQUEST_ID", ((Hashtable<K, Long>)enrollRequestProperties).get("ENROLLMENT_REQUEST_ID"));
            ((Hashtable<String, String>)smsProperties).put("OTPPassword", MDMEnrollmentOTPHandler.getInstance().getOTPPassword(enrollmentRequestID));
            ((Hashtable<String, Object>)smsProperties).put("AUTH_MODE", ((Hashtable<K, Object>)enrollRequestProperties).get("AUTH_MODE"));
            final String phoneNumber = ((Hashtable<K, String>)enrollRequestProperties).get("PHONE_NUMBER");
            final String[] codeWithNum = phoneNumber.split("-");
            ((Hashtable<String, String>)smsProperties).put("PHONE_NUMBER", codeWithNum[0].substring(1, codeWithNum[0].length()) + codeWithNum[1]);
            ((Hashtable<String, String>)smsProperties).put("COUNTRY_CODE", codeWithNum[0].substring(1, codeWithNum[0].length()));
            smsProperties = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().appendProductSpecificSMSProperties(enrollmentRequestID, platformType, enrollRequestProperties, smsProperties);
            final Long customerID = ((Hashtable<K, Long>)enrollRequestProperties).get("CUSTOMER_ID");
            final String tinyURL = smsProperties.getProperty("ENROLLMENT_URL");
            if (tinyURL.equalsIgnoreCase("--") || tinyURL.equals("")) {
                final String sEventLogRemarks = "dc.mdm.actionlog.sms_sending_failed";
                final String sLoggedOnUserName = MDMUtil.getInstance().getCurrentlyLoggedOnUserName();
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, sLoggedOnUserName, sEventLogRemarks, "", customerID);
                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                logJSON.put((Object)"REMARKS", (Object)"sms-delivery-failure");
                logJSON.put((Object)"ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                MDMOneLineLogger.log(Level.INFO, "SEND_SMS_REQUEST", logJSON);
                getInstance().updateSMSSentStatus(enrollmentRequestID, senderID, 100, sEventLogRemarks, null, phoneNumber, "--");
                return false;
            }
            final JSONObject responseJSONObject = this.sendEnrollmentSMS(smsProperties);
            final int remainingCredits = (int)responseJSONObject.get("REMAINING_CREDITS");
            final String smsSentMobilePhoneNumber = (String)responseJSONObject.get("PHONE_NUMBER");
            final int smsStatus = (int)responseJSONObject.get("STATUS");
            final String smsRemarks = (String)responseJSONObject.get("REMARKS");
            final String messageId = responseJSONObject.optString("MESSAGE_ID", "--");
            String sEventLogRemarks2 = "";
            if (smsStatus == 0) {
                sEventLogRemarks2 = "dc.mdm.actionlog.sms_notification_sent";
                final Object remarksArgs = phoneNumber + "@@@" + remainingCredits;
                final String sLoggedOnUserName2 = MDMUtil.getInstance().getCurrentlyLoggedOnUserName();
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, sLoggedOnUserName2, sEventLogRemarks2, remarksArgs, customerID);
                final org.json.simple.JSONObject logJSON2 = new org.json.simple.JSONObject();
                logJSON2.put((Object)"REMARKS", (Object)"sms-delivery-success");
                logJSON2.put((Object)"ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                MDMOneLineLogger.log(Level.INFO, "SEND_SMS_REQUEST", logJSON2);
            }
            else {
                sEventLogRemarks2 = "dc.mdm.actionlog.sms_notification_failure";
                final Object remarksArgs = phoneNumber + "@@@" + remainingCredits;
                final String sLoggedOnUserName2 = MDMUtil.getInstance().getCurrentlyLoggedOnUserName();
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, sLoggedOnUserName2, sEventLogRemarks2, remarksArgs, customerID);
                final org.json.simple.JSONObject logJSON2 = new org.json.simple.JSONObject();
                logJSON2.put((Object)"REMARKS", (Object)"sms-delivery-failure");
                logJSON2.put((Object)"ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                MDMOneLineLogger.log(Level.INFO, "SEND_SMS_REQUEST", logJSON2);
            }
            if (responseJSONObject.has("MSG_SERVICE_ID") && responseJSONObject.get("MSG_SERVICE_ID") != null) {
                senderID = (int)responseJSONObject.get("MSG_SERVICE_ID");
            }
            getInstance().updateSMSSentStatus(enrollmentRequestID, senderID, smsStatus, smsRemarks, smsProperties.getProperty("MESSAGE"), phoneNumber, messageId);
        }
        else {
            this.logger.log(Level.INFO, "MDMEnrollmentRequestHandler: User not allowed to send SMS. SMS configured : {0} Credits remaining : {1}", new Object[] { smsconfigured, creditsAvailable });
            getInstance().updateSMSSentStatus(enrollmentRequestID, senderID, 10001, "dc.mdm.actionlog.no_sms_credits", null, enrollRequestProperties.getProperty("PHONE_NUMBER"), "--");
        }
        return true;
    }
    
    private void updateSMSSentStatus(final long enrollmentRequestId, final int messsageServiceID, final int smsStatus, final String smsRemarks, final String smsData, final String phoneNumber, final String messageId) throws Exception {
        final Criteria enrollReqCriteria = new Criteria(Column.getColumn("DEVICEENROLLREQTOSMS", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0);
        final DataObject smsStatusUpdate = MDMUtil.getPersistence().get("DEVICEENROLLREQTOSMS", enrollReqCriteria);
        if (smsStatusUpdate.isEmpty()) {
            final Row smsStatusRow = new Row("DEVICEENROLLREQTOSMS");
            smsStatusRow.set("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestId);
            smsStatusRow.set("SMS_CODE", (Object)smsStatus);
            smsStatusRow.set("SMS_REMARKS", (Object)smsRemarks);
            smsStatusRow.set("MESSAGE_SERVICE_ID", (Object)messsageServiceID);
            smsStatusRow.set("SMS_DATA", (Object)smsData);
            smsStatusRow.set("PHONE_NUMBER", (Object)phoneNumber);
            smsStatusRow.set("MESSAGE_ID", (Object)messageId);
            smsStatusUpdate.addRow(smsStatusRow);
            MDMUtil.getPersistence().add(smsStatusUpdate);
        }
        else {
            final Row smsStatusRow = smsStatusUpdate.getFirstRow("DEVICEENROLLREQTOSMS");
            smsStatusRow.set("SMS_CODE", (Object)smsStatus);
            smsStatusRow.set("SMS_REMARKS", (Object)smsRemarks);
            smsStatusRow.set("MESSAGE_SERVICE_ID", (Object)messsageServiceID);
            smsStatusRow.set("SMS_DATA", (Object)smsData);
            smsStatusRow.set("PHONE_NUMBER", (Object)phoneNumber);
            smsStatusRow.set("MESSAGE_ID", (Object)messageId);
            smsStatusUpdate.updateRow(smsStatusRow);
            MDMUtil.getPersistence().update(smsStatusUpdate);
        }
    }
    
    private void updateSMSSentStatus(final long enrollmentRequestId, final int smsStatus, final String smsRemarks, final String messageId) throws Exception {
        final Criteria enrollReqCriteria = new Criteria(Column.getColumn("DEVICEENROLLREQTOSMS", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0);
        final DataObject smsStatusUpdate = MDMUtil.getPersistence().get("DEVICEENROLLREQTOSMS", enrollReqCriteria);
        if (smsStatusUpdate.isEmpty()) {
            final Row smsStatusRow = new Row("DEVICEENROLLREQTOSMS");
            smsStatusRow.set("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestId);
            smsStatusRow.set("SMS_CODE", (Object)smsStatus);
            smsStatusRow.set("SMS_REMARKS", (Object)smsRemarks);
            smsStatusRow.set("MESSAGE_ID", (Object)messageId);
            smsStatusUpdate.addRow(smsStatusRow);
            MDMUtil.getPersistence().add(smsStatusUpdate);
        }
        else {
            final Row smsStatusRow = smsStatusUpdate.getFirstRow("DEVICEENROLLREQTOSMS");
            smsStatusRow.set("SMS_CODE", (Object)smsStatus);
            smsStatusRow.set("SMS_REMARKS", (Object)smsRemarks);
            smsStatusRow.set("MESSAGE_ID", (Object)messageId);
            smsStatusUpdate.updateRow(smsStatusRow);
            MDMUtil.getPersistence().update(smsStatusUpdate);
        }
    }
    
    private void updateSMSSentStatus(final long enrollmentRequestId, final int smsStatus, final String smsRemarks, final String sms, final String phoneNumber, final String messageId) throws Exception {
        final Criteria enrollReqCriteria = new Criteria(Column.getColumn("DEVICEENROLLREQTOSMS", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0);
        final DataObject smsStatusUpdate = MDMUtil.getPersistence().get("DEVICEENROLLREQTOSMS", enrollReqCriteria);
        if (smsStatusUpdate.isEmpty()) {
            final Row smsStatusRow = new Row("DEVICEENROLLREQTOSMS");
            smsStatusRow.set("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestId);
            smsStatusRow.set("SMS_CODE", (Object)smsStatus);
            smsStatusRow.set("SMS_REMARKS", (Object)smsRemarks);
            smsStatusRow.set("SMS_DATA", (Object)sms);
            smsStatusRow.set("PHONE_NUMBER", (Object)phoneNumber);
            smsStatusRow.set("MESSAGE_ID", (Object)messageId);
            smsStatusUpdate.addRow(smsStatusRow);
            MDMUtil.getPersistence().add(smsStatusUpdate);
        }
        else {
            final Row smsStatusRow = smsStatusUpdate.getFirstRow("DEVICEENROLLREQTOSMS");
            smsStatusRow.set("SMS_CODE", (Object)smsStatus);
            smsStatusRow.set("SMS_REMARKS", (Object)smsRemarks);
            smsStatusRow.set("SMS_DATA", (Object)sms);
            smsStatusRow.set("PHONE_NUMBER", (Object)phoneNumber);
            smsStatusRow.set("MESSAGE_ID", (Object)messageId);
            smsStatusUpdate.updateRow(smsStatusRow);
            MDMUtil.getPersistence().update(smsStatusUpdate);
        }
    }
    
    private JSONObject getSMSSentStatus(final long enrollmentRequestId) throws Exception {
        final Criteria enrollReqCriteria = new Criteria(Column.getColumn("DEVICEENROLLREQTOSMS", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0);
        final DataObject smsStatusUpdate = MDMUtil.getPersistence().get("DEVICEENROLLREQTOSMS", enrollReqCriteria);
        final JSONObject smsObjectRow = new JSONObject();
        if (smsStatusUpdate.size("DEVICEENROLLREQTOSMS") > 0) {
            final Row smsStatusRow = smsStatusUpdate.getFirstRow("DEVICEENROLLREQTOSMS");
            smsObjectRow.put("SMS_CODE", (Object)smsStatusRow.get("SMS_CODE"));
            smsObjectRow.put("SMS_REMARKS", (Object)smsStatusRow.get("SMS_REMARKS"));
        }
        return smsObjectRow;
    }
    
    public void updateEnrollFailedStatus(final Long reqId, final String remarks, final int errorCode) {
        try {
            final JSONObject json = new JSONObject();
            json.put("ENROLLMENT_REQUEST_ID", (Object)reqId);
            json.put("REQUEST_STATUS", 0);
            json.put("REMARKS", (Object)remarks);
            json.put("ERROR_CODE", errorCode);
            this.updateEnrollmentStatus(json);
        }
        catch (final Exception exp) {
            this.logger.severe("Exception occured in updateEnrollFailedStatus:" + exp);
        }
    }
    
    public Properties sendEnrollmentRequest(final Properties properties) {
        Properties prop = new Properties();
        ((Hashtable<String, String>)prop).put("ENROLL_STATUS", "-1");
        try {
            prop = this.addEnrollmentRequest(properties);
            final Boolean sKnoxStatus = ((Hashtable<K, Boolean>)properties).get("KNOX_LIC_DS");
            if (sKnoxStatus != null && sKnoxStatus) {
                final HashMap KnoxLicense = KnoxLicenseHandler.getInstance().getKnoxCustomerLicense(((Hashtable<K, Long>)prop).get("CUSTOMER_ID"));
                KnoxActivationManager.getInstance().addEnrollmentReqToKnoxRel(((Hashtable<K, Long>)prop).get("ENROLLMENT_REQUEST_ID"), KnoxLicense.get("LICENSE_ID"));
            }
        }
        catch (final Exception exp) {
            Logger.getLogger("MDMEnrollment").log(Level.SEVERE, null, exp);
        }
        return prop;
    }
    
    public boolean isBulkEnrollUDIDExist(final String udid) {
        boolean isBulkEnrollUDID = false;
        try {
            if (udid != null) {
                final Criteria udidCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "UDID"), (Object)udid, 0);
                final DataObject dObj = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", udidCri);
                if (!dObj.isEmpty()) {
                    isBulkEnrollUDID = true;
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in isBulkEnrollUDID", ex);
        }
        return isBulkEnrollUDID;
    }
    
    public void resendEnrollmentRequest(final Properties props) throws SyMException {
        final String sourceMethod = "resendEnrollmentRequest";
        Long enrollmentRequestID = null;
        try {
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, props.toString());
            enrollmentRequestID = ((Hashtable<K, Long>)props).get("ENROLLMENT_REQUEST_ID");
            final JSONObject json_obj = new JSONObject();
            json_obj.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            json_obj.put("REGISTRATION_STATUS", 0);
            getInstance().addOrUpdateInvitationDetails(json_obj);
            final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            squery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            squery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            squery.addJoin(new Join("DeviceEnrollmentRequest", "InvitationEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            squery.addJoin(new Join("DeviceEnrollmentRequest", "EREmailInvitationFailure", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            squery.addJoin(new Join("DeviceEnrollmentRequest", "DEVICEENROLLREQTOSMS", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            squery.addSelectColumn(Column.getColumn((String)null, "*"));
            squery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0));
            final DataObject dobj = MDMUtil.getPersistence().get(squery);
            if (!dobj.isEmpty()) {
                final Row enrollRequestRow = dobj.getRow("DeviceEnrollmentRequest");
                final int platform = (int)enrollRequestRow.get("PLATFORM_TYPE");
                final Row managedUserRow = dobj.getRow("ManagedUser");
                final Long managed_user_id = (Long)managedUserRow.get("MANAGED_USER_ID");
                final String email_id = (String)managedUserRow.get("EMAIL_ADDRESS");
                final Row resourceRow = dobj.getRow("Resource");
                final String userName = (String)resourceRow.get("NAME");
                final String domain = (String)resourceRow.get("DOMAIN_NETBIOS_NAME");
                final Long customerID = (Long)resourceRow.get("CUSTOMER_ID");
                final Row inviteRow = dobj.getRow("InvitationEnrollmentRequest");
                Boolean byAdmin = false;
                if (inviteRow != null) {
                    byAdmin = (Boolean)inviteRow.get("IS_INVITED_BY_ADMIN");
                }
                Boolean sendSMS = false;
                final Row smsRow = dobj.getRow("DEVICEENROLLREQTOSMS");
                if (smsRow != null) {
                    sendSMS = true;
                }
                final Properties properties = new Properties();
                properties.putAll(props);
                ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", platform);
                ((Hashtable<String, String>)properties).put("DOMAIN_NETBIOS_NAME", domain);
                ((Hashtable<String, Long>)properties).put("ENROLLMENT_REQUEST_ID", enrollmentRequestID);
                ((Hashtable<String, Long>)properties).put("MANAGED_USER_ID", managed_user_id);
                ((Hashtable<String, String>)properties).put("NAME", userName);
                ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", customerID);
                ((Hashtable<String, String>)properties).put("EMAIL_ADDRESS", email_id);
                if (sendSMS) {
                    ((Hashtable<String, Boolean>)properties).put("sendSMS", sendSMS);
                }
                MDMEnrollmentOTPHandler.getInstance().addEntryInOTPTable(enrollmentRequestID);
                final String otpPassword = MDMEnrollmentOTPHandler.getInstance().getOTPPassword(enrollmentRequestID);
                final Properties enrollRequestStatusProperties = new Properties();
                ((Hashtable<String, Long>)enrollRequestStatusProperties).put("ENROLLMENT_REQUEST_ID", enrollmentRequestID);
                if (byAdmin) {
                    ((Hashtable<String, String>)enrollRequestStatusProperties).put("REMARKS", "dc.mdm.enroll.request_created");
                }
                else {
                    CustomerInfoUtil.getInstance();
                    if (CustomerInfoUtil.isSAS()) {
                        ((Hashtable<String, String>)enrollRequestStatusProperties).put("REMARKS", "mdm.enroll.successfully_resend_enrollment_request");
                    }
                    else {
                        ((Hashtable<String, String>)enrollRequestStatusProperties).put("REMARKS", "dc.mdm.enroll.successfully_added_mail_to_queue");
                    }
                }
                ((Hashtable<String, Boolean>)enrollRequestStatusProperties).put("IS_SELF_ENROLLMENT", Boolean.FALSE);
                ((Hashtable<String, Long>)enrollRequestStatusProperties).put("REQUESTED_TIME", new Long(System.currentTimeMillis()));
                ((Hashtable<String, Integer>)enrollRequestStatusProperties).put("REQUEST_STATUS", new Integer(1));
                ((Hashtable<String, Integer>)enrollRequestStatusProperties).put("PLATFORM_TYPE", platform);
                final Long userID = ((Hashtable<K, Long>)props).get("USER_ID");
                if (userID != null) {
                    ((Hashtable<String, Long>)enrollRequestStatusProperties).put("USER_ID", userID);
                }
                ((Hashtable<String, Integer>)enrollRequestStatusProperties).put("AUTH_MODE", ((Hashtable<K, Integer>)props).get("AUTH_MODE"));
                ((Hashtable<String, Integer>)enrollRequestStatusProperties).put("ENROLLMENT_TYPE", 1);
                ((Hashtable<String, String>)enrollRequestStatusProperties).put("UDID", "");
                ((Hashtable<String, Boolean>)enrollRequestStatusProperties).put("byAdmin", byAdmin);
                this.addOrUpdateEnrollmentRequest(enrollRequestStatusProperties);
                if (properties.containsKey("regenerateDeviceToken") && ((Hashtable<K, Boolean>)properties).get("regenerateDeviceToken")) {
                    final JSONObject json = new JSONObject();
                    json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                    MDMDeviceAPIKeyGenerator.getInstance().updateAPIKey(json);
                }
                final Long managedDeviceID = ManagedDeviceHandler.getInstance().getManagedDeviceIDFromEnrollRequestID(enrollmentRequestID);
                if (managedDeviceID != null) {
                    final Properties deviceProperties = new Properties();
                    ((Hashtable<String, Long>)deviceProperties).put("RESOURCE_ID", managedDeviceID);
                    ((Hashtable<String, Integer>)deviceProperties).put("MANAGED_STATUS", new Integer(1));
                    if (byAdmin) {
                        ((Hashtable<String, String>)enrollRequestStatusProperties).put("REMARKS", "dc.mdm.enroll.request_created");
                    }
                    else {
                        CustomerInfoUtil.getInstance();
                        if (CustomerInfoUtil.isSAS()) {
                            ((Hashtable<String, String>)deviceProperties).put("REMARKS", "mdm.enroll.successfully_resend_enrollment_request");
                        }
                        else {
                            ((Hashtable<String, String>)deviceProperties).put("REMARKS", "dc.mdm.enroll.successfully_added_mail_to_queue");
                        }
                    }
                    ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(deviceProperties);
                }
                ((Hashtable<String, Boolean>)properties).put("NewEnrollReq", false);
                final Properties managedUserProperties = ManagedUserHandler.getInstance().getManagedUserDetailsForRequestAsProperties(enrollmentRequestID);
                properties.putAll(managedUserProperties);
                ((Hashtable<String, Boolean>)properties).put("NewEnrollReq", false);
                boolean isSMSSent = false;
                if (!byAdmin) {
                    final EREvent erEvent = new EREvent(String.valueOf(enrollmentRequestID), otpPassword);
                    EnrollmentRequestHandler.getInstance().invokeEnrollmentRequestListeners(erEvent, 7);
                    if (!properties.containsKey("sendEmail") || ((Hashtable<K, Boolean>)properties).get("sendEmail")) {
                        final Row erEmailInvitationFailureRow = dobj.getRow("EREmailInvitationFailure");
                        if (erEmailInvitationFailureRow != null) {
                            dobj.deleteRow(erEmailInvitationFailureRow);
                            MDMUtil.getPersistence().update(dobj);
                            this.logger.info("MDMEnrollmentRequestHandler::resendEnrollmentRequest : Previous Email Failure Entry Deleted for the request ");
                        }
                        this.sendEnrollmentMail(properties);
                    }
                    isSMSSent = (!properties.containsKey("sendSMS") || !((Hashtable<K, Boolean>)properties).get("sendSMS") || this.sendEnrollmentInvitationThroughSMS(properties));
                    if (!isSMSSent) {
                        this.updateStatus(isSMSSent, properties, enrollmentRequestID);
                    }
                }
                if (((Hashtable<K, Integer>)properties).get("PLATFORM_TYPE") == 1) {
                    MDMProfileInstallationHandler.getInstance().clearProfileInstallationStatus(enrollmentRequestID);
                }
            }
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred during re-sending enrollment request for request id : " + enrollmentRequestID, (Throwable)exp);
        }
    }
    
    public Long getEnrollmentRequestIdFromManagedDeviceID(final Long managedDeviceID) {
        Long enrollmentId = null;
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        sql.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sql.setCriteria(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceID, 0));
        sql.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "*"));
        try {
            final DataObject dao = MDMUtil.getPersistence().get(sql);
            if (!dao.isEmpty()) {
                enrollmentId = (Long)dao.getFirstRow("EnrollmentRequestToDevice").get("ENROLLMENT_REQUEST_ID");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getUDIDFromEnrollmentRequestID", ex);
        }
        return enrollmentId;
    }
    
    @Deprecated
    public Long getEnrollmentRequestIdFromUdid(final String udid) {
        Long enrollmentId = null;
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        sql.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sql.setCriteria(new Criteria(new Column("ManagedDevice", "UDID"), (Object)udid, 0, (boolean)Boolean.FALSE));
        sql.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "*"));
        try {
            final DataObject dao = MDMUtil.getPersistence().get(sql);
            if (!dao.isEmpty()) {
                enrollmentId = (Long)dao.getFirstRow("EnrollmentRequestToDevice").get("ENROLLMENT_REQUEST_ID");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getUDIDFromEnrollmentRequestID", ex);
        }
        return enrollmentId;
    }
    
    public long getManagedEnrollmentRequestIdForUdid(final String udid, final long customerId) {
        if (MDMStringUtils.isEmpty(udid) || customerId == -1L) {
            return -1L;
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        final Join enrollmentRequestToDeviceJoin = new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        selectQuery.addJoin(enrollmentRequestToDeviceJoin);
        final Join resourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        selectQuery.addJoin(resourceJoin);
        final Criteria udidCriteria = new Criteria(new Column("ManagedDevice", "UDID"), (Object)udid, 0, (boolean)Boolean.FALSE);
        final Criteria customerIdCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.setCriteria(udidCriteria.and(customerIdCriteria));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "*"));
        long enrollmentRequestId = -1L;
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Long tempErid = (Long)dataObject.getFirstRow("EnrollmentRequestToDevice").get("ENROLLMENT_REQUEST_ID");
                enrollmentRequestId = ((tempErid == null) ? -1L : tempErid);
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception while picking Enrollment request ID for the given UDID", (Throwable)e);
        }
        return enrollmentRequestId;
    }
    
    public String getEnrollmentRequestIdFromManagedUserIDs(final Long[] managedUserIDs) {
        final StringBuilder strBuilder = new StringBuilder();
        try {
            final DataObject dObj = MDMUtil.getPersistence().get(MDMUtil.formSelectQuery("ManagedUserToDevice", new Criteria(new Column("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)managedUserIDs, 8), new ArrayList((Collection<? extends E>)Arrays.asList(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"))), (ArrayList)null, (ArrayList)null, new ArrayList((Collection<? extends E>)Arrays.asList(new Join("ManagedUserToDevice", "EnrollmentRequestToDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2))), (Criteria)null));
            if (dObj != null && !dObj.isEmpty()) {
                final Iterator itr = dObj.getRows("EnrollmentRequestToDevice");
                while (itr != null && itr.hasNext()) {
                    final Row row = itr.next();
                    final Long enrollmentReqID = (Long)row.get("ENROLLMENT_REQUEST_ID");
                    if (strBuilder.length() > 0) {
                        strBuilder.append(",");
                    }
                    strBuilder.append(enrollmentReqID);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, null, ex);
        }
        final String result = strBuilder.toString();
        this.logger.log(Level.INFO, "managed user ids: {0} | erids: {1}", new Object[] { managedUserIDs, result });
        return result;
    }
    
    public int getOwnedByForEnrollmentRequest(final Long enrollmentRequestid) {
        int ownedBy = 0;
        try {
            final Criteria cEnrollmentRequets = new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestid, 0);
            final DataObject dao = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", cEnrollmentRequets);
            if (!dao.isEmpty()) {
                ownedBy = (int)dao.getFirstValue("DeviceEnrollmentRequest", "OWNED_BY");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getOwnedByForEnrollmentRequest", e);
        }
        return ownedBy;
    }
    
    public Long addOrUpdateEnrollmentRequest(final Properties properties) throws Exception {
        DataObject dataObject = null;
        Long managedUserID = null;
        Long enrollmentRequestID = null;
        final String sourceMethod = "addOrUpdateEnrollmentRequest";
        try {
            managedUserID = ((Hashtable<K, Long>)properties).get("MANAGED_USER_ID");
            enrollmentRequestID = ((Hashtable<K, Long>)properties).get("ENROLLMENT_REQUEST_ID");
            Integer ownedBy = ((Hashtable<K, Integer>)properties).get("OWNED_BY");
            final Integer platformType = ((Hashtable<K, Integer>)properties).get("PLATFORM_TYPE");
            final Long requested_time = ((Hashtable<K, Long>)properties).get("REQUESTED_TIME");
            Integer requestStatus = ((Hashtable<K, Integer>)properties).get("REQUEST_STATUS");
            Integer enrollmentType = ((Hashtable<K, Integer>)properties).get("ENROLLMENT_TYPE");
            Boolean isSelfEnroll = ((Hashtable<K, Boolean>)properties).get("IS_SELF_ENROLLMENT");
            String udid = ((Hashtable<K, String>)properties).get("UDID");
            final Boolean byAdmin = ((Hashtable<K, Boolean>)properties).get("byAdmin");
            if (udid == null || udid.isEmpty()) {
                udid = null;
            }
            if (isSelfEnroll == null) {
                isSelfEnroll = false;
            }
            if (enrollmentType == null) {
                if (isSelfEnroll) {
                    enrollmentType = 2;
                }
                else {
                    enrollmentType = 1;
                }
            }
            final String remarks = ((Hashtable<K, String>)properties).get("REMARKS");
            final Long userId = ((Hashtable<K, Long>)properties).get("USER_ID");
            final Integer authmode = ((Hashtable<K, Integer>)properties).get("AUTH_MODE");
            if (requestStatus == null) {
                requestStatus = new Integer(1);
            }
            final Criteria criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0);
            dataObject = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", criteria);
            if (dataObject.isEmpty()) {
                if (ownedBy == null) {
                    ownedBy = new Integer(0);
                }
                final Row enrollRequestRow = new Row("DeviceEnrollmentRequest");
                enrollRequestRow.set("MANAGED_USER_ID", (Object)managedUserID);
                enrollRequestRow.set("OWNED_BY", (Object)ownedBy);
                enrollRequestRow.set("PLATFORM_TYPE", (Object)platformType);
                enrollRequestRow.set("REQUESTED_TIME", (Object)System.currentTimeMillis());
                enrollRequestRow.set("REQUEST_STATUS", (Object)requestStatus);
                enrollRequestRow.set("IS_SELF_ENROLLMENT", (Object)isSelfEnroll);
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS() || enrollmentType != 1 || (enrollmentType == 1 && byAdmin != null && byAdmin)) {
                    enrollRequestRow.set("REMARKS", (Object)"dc.mdm.enroll.request_created");
                }
                else {
                    enrollRequestRow.set("REMARKS", (Object)"dc.mdm.enroll.successfully_added_mail_to_queue");
                }
                enrollRequestRow.set("USER_ID", (Object)userId);
                enrollRequestRow.set("AUTH_MODE", (Object)authmode);
                enrollRequestRow.set("ENROLLMENT_TYPE", (Object)enrollmentType);
                enrollRequestRow.set("UDID", (Object)udid);
                dataObject.addRow(enrollRequestRow);
            }
            else {
                final Row enrollRequestRow = dataObject.getRow("DeviceEnrollmentRequest");
                if (ownedBy != null && (int)enrollRequestRow.get("OWNED_BY") != ownedBy) {
                    enrollRequestRow.set("OWNED_BY", (Object)ownedBy);
                }
                if (platformType != null && (int)enrollRequestRow.get("PLATFORM_TYPE") != platformType) {
                    enrollRequestRow.set("PLATFORM_TYPE", (Object)platformType);
                }
                if (requestStatus != null && (int)enrollRequestRow.get("REQUEST_STATUS") != requestStatus) {
                    enrollRequestRow.set("REQUEST_STATUS", (Object)requestStatus);
                }
                if (requested_time != null) {
                    enrollRequestRow.set("REQUESTED_TIME", (Object)requested_time);
                }
                if (remarks != null) {
                    enrollRequestRow.set("REMARKS", (Object)remarks);
                }
                if (authmode != null) {
                    enrollRequestRow.set("AUTH_MODE", (Object)authmode);
                }
                if (enrollmentType != null) {
                    enrollRequestRow.set("ENROLLMENT_TYPE", (Object)enrollmentType);
                }
                enrollRequestRow.set("IS_SELF_ENROLLMENT", (Object)isSelfEnroll);
                enrollRequestRow.set("UDID", (Object)udid);
                dataObject.updateRow(enrollRequestRow);
            }
            dataObject = MDMUtil.getPersistence().update(dataObject);
            enrollmentRequestID = (Long)dataObject.getFirstValue("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
            final JSONObject json = new JSONObject();
            json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(json);
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured during add or update of enrollment request for user id : " + managedUserID, (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        return enrollmentRequestID;
    }
    
    private void setLastEnrollParam(final int platformType, final int ownedBy) {
        try {
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (loginId != null) {
                final Criteria loginCri = new Criteria(Column.getColumn("LastUsedEnrollmentParams", "LOGIN_ID"), (Object)loginId, 0);
                final DataObject dObj = MDMUtil.getPersistence().get("LastUsedEnrollmentParams", loginCri);
                Row enrollParamrow = null;
                if (dObj.isEmpty()) {
                    enrollParamrow = new Row("LastUsedEnrollmentParams");
                    enrollParamrow.set("LOGIN_ID", (Object)loginId);
                    enrollParamrow.set("PLATFORM_TYPE", (Object)platformType);
                    enrollParamrow.set("OWNED_BY", (Object)ownedBy);
                    dObj.addRow(enrollParamrow);
                }
                else {
                    enrollParamrow = dObj.getRow("LastUsedEnrollmentParams", loginCri);
                    enrollParamrow.set("PLATFORM_TYPE", (Object)platformType);
                    enrollParamrow.set("OWNED_BY", (Object)ownedBy);
                    dObj.updateRow(enrollParamrow);
                }
                MDMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in setLastEnrollParam", exp);
        }
    }
    
    private void sendEnrollmentMail(final Properties enrollRequestProperties) throws Exception {
        final Long customerId = ((Hashtable<K, Long>)enrollRequestProperties).get("CUSTOMER_ID");
        final Long enrollmentRequestID = ((Hashtable<K, Long>)enrollRequestProperties).get("ENROLLMENT_REQUEST_ID");
        final Boolean newReq = ((Hashtable<K, Boolean>)enrollRequestProperties).get("NewEnrollReq");
        final Integer platformType = ((Hashtable<K, Integer>)enrollRequestProperties).get("PLATFORM_TYPE");
        final String emailAddress = ((Hashtable<K, String>)enrollRequestProperties).get("EMAIL_ADDRESS");
        final String firstName = ((Hashtable<K, String>)enrollRequestProperties).get("FIRST_NAME");
        final String middleName = ((Hashtable<K, String>)enrollRequestProperties).get("MIDDLE_NAME");
        final String lastName = ((Hashtable<K, String>)enrollRequestProperties).get("LAST_NAME");
        final String displayName = ((Hashtable<K, String>)enrollRequestProperties).get("DISPLAY_NAME");
        final String userName = ((Hashtable<K, String>)enrollRequestProperties).get("NAME");
        final Integer authMode = ((Hashtable<K, Integer>)enrollRequestProperties).get("AUTH_MODE");
        final Long alertType = MDMEnrollmentUtil.getInstance().getEnrollmentMailTemplateID(platformType, authMode);
        final String otpPassword = MDMEnrollmentOTPHandler.getInstance().getOTPPassword(enrollmentRequestID);
        final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(this.logger);
        Properties enrollMailProperties = new Properties();
        ((Hashtable<String, String>)enrollMailProperties).put("$passcode$", otpPassword);
        ((Hashtable<String, String>)enrollMailProperties).put("$user_emailid$", emailAddress);
        ((Hashtable<String, String>)enrollMailProperties).put("$user_name$", userName);
        ((Hashtable<String, String>)enrollMailProperties).put("$playStore_url$", "https://play.google.com/store/apps/details?id=com.manageengine.mdm.android&MDMSrc=2");
        if (!MDMStringUtils.isEmpty(firstName)) {
            ((Hashtable<String, String>)enrollMailProperties).put("$first_name$", WordUtils.capitalize(firstName));
        }
        else {
            ((Hashtable<String, String>)enrollMailProperties).put("$first_name$", "");
        }
        if (!MDMStringUtils.isEmpty(middleName)) {
            ((Hashtable<String, String>)enrollMailProperties).put("$middle_name$", WordUtils.capitalize(middleName));
        }
        else {
            ((Hashtable<String, String>)enrollMailProperties).put("$middle_name$", "");
        }
        if (!MDMStringUtils.isEmpty(lastName)) {
            ((Hashtable<String, String>)enrollMailProperties).put("$last_name$", WordUtils.capitalize(lastName));
        }
        else {
            ((Hashtable<String, String>)enrollMailProperties).put("$last_name$", "");
        }
        if (!MDMStringUtils.isEmpty(displayName) && !displayName.equalsIgnoreCase("---")) {
            ((Hashtable<String, String>)enrollMailProperties).put("$display_name$", WordUtils.capitalize(displayName));
        }
        else if (!MDMStringUtils.isEmpty(userName) && !userName.equalsIgnoreCase("---")) {
            ((Hashtable<String, String>)enrollMailProperties).put("$display_name$", WordUtils.capitalize(userName));
        }
        else {
            ((Hashtable<String, String>)enrollMailProperties).put("$display_name$", "");
        }
        final JSONObject additionalProps = new JSONObject();
        additionalProps.put("EnrollmentRequestID", (Object)enrollmentRequestID);
        additionalProps.put("NewEnrollReq", (Object)newReq);
        ((Hashtable<String, JSONObject>)enrollMailProperties).put("additionalParams", additionalProps);
        enrollMailProperties = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().appendEnrollmentPropertiesForEnrollemntInMail(enrollmentRequestID, platformType, enrollRequestProperties, enrollMailProperties);
        getInstance().updateMailSentStatus();
        final MDMEnrollmentUtil enrollmentUtil = new MDMEnrollmentUtil();
        if (enrollmentUtil.previousTemplateVersionsExist(customerId, platformType)) {
            final Integer enrollmentSettingsAuthMode = EnrollmentSettingsHandler.getInstance().getInvitationEnrollmentSettings(customerId).getInt("AUTH_MODE");
            final long mailTemplateID = MDMEnrollmentUtil.getInstance().getEnrollmentMailTemplateID(platformType, enrollmentSettingsAuthMode);
            enrollmentUtil.deleteOldTemplateVersion(customerId, mailTemplateID);
        }
        mailGenerator.sendMail(alertType, "MDM", customerId, enrollMailProperties);
    }
    
    public void addEnrollmentToGroupEntries(final Long enrollmentRequestID, final String groupId) throws Exception {
        final List<Long> list = new ArrayList<Long>();
        if (!groupId.equalsIgnoreCase("-1") && !groupId.equalsIgnoreCase("0")) {
            for (final String s : groupId.split(",")) {
                list.add(Long.parseLong(s));
            }
        }
        this.addEnrollmentToGroupEntries(enrollmentRequestID, list);
    }
    
    public void addEnrollmentToGroupEntries(final Long enrollmentRequestID, final List<Long> groupId) throws Exception {
        if (enrollmentRequestID != null && groupId != null && !groupId.isEmpty()) {
            final List<Long> listToDelete = new ArrayList<Long>();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentRequestToGroup"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToGroup", "ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToGroup", "GROUP_RESOURCE_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("EnrollmentRequestToGroup", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0));
            final DataObject enrollGroupDO = MDMUtil.getPersistenceLite().get(selectQuery);
            Row enrollGroupRow = null;
            if (enrollGroupDO.isEmpty()) {
                for (final Long gid : groupId) {
                    enrollGroupRow = new Row("EnrollmentRequestToGroup");
                    enrollGroupRow.set("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                    enrollGroupRow.set("GROUP_RESOURCE_ID", (Object)gid);
                    enrollGroupDO.addRow(enrollGroupRow);
                }
                MDMUtil.getPersistenceLite().add(enrollGroupDO);
            }
            else {
                final Iterator iterator = enrollGroupDO.getRows("EnrollmentRequestToGroup");
                while (iterator.hasNext()) {
                    enrollGroupRow = iterator.next();
                    final Long gid = (Long)enrollGroupRow.get("GROUP_RESOURCE_ID");
                    if (groupId.contains(gid)) {
                        groupId.remove(gid);
                    }
                    else {
                        listToDelete.add(gid);
                    }
                }
                if (!groupId.isEmpty()) {
                    for (final Long gid2 : groupId) {
                        enrollGroupRow = new Row("EnrollmentRequestToGroup");
                        enrollGroupRow.set("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                        enrollGroupRow.set("GROUP_RESOURCE_ID", (Object)gid2);
                        enrollGroupDO.addRow(enrollGroupRow);
                    }
                }
                if (!listToDelete.isEmpty()) {
                    enrollGroupDO.deleteRows("EnrollmentRequestToGroup", new Criteria(Column.getColumn("EnrollmentRequestToGroup", "GROUP_RESOURCE_ID"), (Object)listToDelete.toArray(), 8));
                }
                MDMUtil.getPersistence().update(enrollGroupDO);
            }
        }
    }
    
    public void addEnrollmentGroupTable(final Long enrollmentRequestID, final Long groupId) throws Exception {
        if (enrollmentRequestID != null && groupId != null && groupId != -1L && groupId != 0L) {
            final Criteria enrollReqCri = new Criteria(Column.getColumn("EnrollmentRequestToGroup", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0);
            final Criteria groupCri = new Criteria(Column.getColumn("EnrollmentRequestToGroup", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
            final Criteria cri = enrollReqCri.and(groupCri);
            Row enrollGroupRow = null;
            final DataObject enrollGroupDO = MDMUtil.getPersistence().get("EnrollmentRequestToGroup", cri);
            if (enrollGroupDO.isEmpty()) {
                enrollGroupRow = new Row("EnrollmentRequestToGroup");
                enrollGroupRow.set("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                enrollGroupRow.set("GROUP_RESOURCE_ID", (Object)groupId);
                enrollGroupDO.addRow(enrollGroupRow);
                MDMUtil.getPersistence().add(enrollGroupDO);
            }
            SyMLogger.debug(this.logger, this.sourceClass, this.sourceClass, "Successfully added Group for Enrollment Id   :" + enrollmentRequestID);
        }
        else {
            SyMLogger.info(this.logger, this.sourceClass, this.sourceClass, "Input Param either Group for Enrollment Id is null::  EnrollmentRequestId  :" + enrollmentRequestID + "  and GroupId :" + groupId);
        }
    }
    
    public List<Long> getGroupEnrollmentId(final Long enrollmentRequestID) throws Exception {
        final List<Long> groupId = new ArrayList<Long>();
        Row row = null;
        final Criteria enrollReqCri = new Criteria(Column.getColumn("EnrollmentRequestToGroup", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0);
        final DataObject dObj = MDMUtil.getPersistence().get("EnrollmentRequestToGroup", enrollReqCri);
        if (!dObj.isEmpty()) {
            final Iterator iterator = dObj.getRows("EnrollmentRequestToGroup");
            while (iterator.hasNext()) {
                row = iterator.next();
                groupId.add((Long)row.get("GROUP_RESOURCE_ID"));
            }
        }
        return groupId;
    }
    
    private void addOrUpdateEnrollReqErrCode(final Long reqId, final int errorCode) {
        try {
            final Row errRow = new Row("DeviceEnrollReqToErrCode");
            errRow.set("ENROLLMENT_REQUEST_ID", (Object)reqId);
            final DataObject DO = MDMUtil.getPersistence().get("DeviceEnrollReqToErrCode", errRow);
            if (DO.isEmpty()) {
                errRow.set("ERROR_CODE", (Object)errorCode);
                DO.addRow(errRow);
                MDMUtil.getPersistence().add(DO);
            }
            else {
                errRow.set("ERROR_CODE", (Object)errorCode);
                DO.updateRow(errRow);
                MDMUtil.getPersistence().update(DO);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while adding error code for request ", e);
        }
    }
    
    public boolean isDeviceRequestValid(final Long enrollmentRequestID) {
        boolean isRequestValid = false;
        final String sourceMethod = "isDeviceRequestValid";
        try {
            final Criteria requestCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0);
            final Criteria pendingCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)new Integer[] { 1, 2, 0 }, 8);
            final Criteria criteria = requestCriteria.and(pendingCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", criteria);
            if (!dataObject.isEmpty()) {
                isRequestValid = true;
            }
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while checking device enrollment request is valid or not.", (Throwable)exp);
        }
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, enrollmentRequestID + " is Device request id Valid : " + isRequestValid);
        return isRequestValid;
    }
    
    public boolean isDeviceRequestValid(final Long enrollmentRequestID, final int platformType) {
        boolean isRequestValid = false;
        final String sourceMethod = "isDeviceRequestValid";
        try {
            final Criteria requestCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0);
            final Criteria pendingCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)new Integer[] { 1, 2, 0 }, 8);
            final Criteria platformCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria criteria = requestCriteria.and(pendingCriteria).and(platformCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", criteria);
            if (!dataObject.isEmpty()) {
                isRequestValid = true;
            }
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while checking device enrollment request is valid or not.", (Throwable)exp);
        }
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, enrollmentRequestID + " is Device request id Valid : " + isRequestValid);
        return isRequestValid;
    }
    
    public void updateDeviceRequestStatus(final Long enrollmentRequestID, final int requestProcessStatus, final int platform) {
        try {
            this.logger.log(Level.INFO, "updating request status for request id :{0} status :{1}", new Object[] { enrollmentRequestID, requestProcessStatus });
            final JSONObject json = new JSONObject();
            json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            json.put("REQUEST_STATUS", requestProcessStatus);
            json.put("PLATFORM_TYPE", platform);
            if (requestProcessStatus == 3) {
                json.put("REMARKS", (Object)"dc.mdm.db.agent.enroll.agent_enroll_finished");
                final JSONObject json_obj = new JSONObject();
                json_obj.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                json_obj.put("REGISTRATION_STATUS", 3);
                getInstance().addOrUpdateInvitationDetails(json_obj);
            }
            this.updateEnrollmentStatus(json);
        }
        catch (final Exception exp) {
            this.logger.severe("Exception occured in updateDeviceRequestStatus:" + exp);
        }
    }
    
    private void updateEnrollmentStatus(final JSONObject json) {
        try {
            final Long enrollmentRequestID = json.getLong("ENROLLMENT_REQUEST_ID");
            final int requestProcessStatus = json.getInt("REQUEST_STATUS");
            final DataObject dataObject = this.getDeviceEnrollmentRequest(enrollmentRequestID);
            if (!dataObject.isEmpty()) {
                final Row requestRow = dataObject.getFirstRow("DeviceEnrollmentRequest");
                requestRow.set("REQUEST_STATUS", (Object)requestProcessStatus);
                if (json.has("REMARKS")) {
                    final String remarks = String.valueOf(json.get("REMARKS"));
                    requestRow.set("REMARKS", (Object)remarks);
                }
                if (json.has("PLATFORM_TYPE")) {
                    requestRow.set("PLATFORM_TYPE", (Object)json.optInt("PLATFORM_TYPE", 0));
                }
                dataObject.updateRow(requestRow);
                MDMUtil.getPersistence().update(dataObject);
            }
            if (json.has("ERROR_CODE") && json.getInt("ERROR_CODE") != -1) {
                this.addOrUpdateEnrollReqErrCode(enrollmentRequestID, json.getInt("ERROR_CODE"));
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    private DataObject getDeviceEnrollmentRequest(final Long enrollRequestID) {
        final String sourceMethod = "getDeviceEnrollmentRequest";
        DataObject dataObject = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollRequestID, 0);
            dataObject = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", criteria);
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occured while fetching DeviceEnrollmentRequest table.", (Throwable)exp);
        }
        return dataObject;
    }
    
    public void updateEnrollmentStatusAndErrorCode(final Long reqId, final int status, final String remarks, final int errorCode) {
        try {
            final JSONObject json = new JSONObject();
            json.put("ENROLLMENT_REQUEST_ID", (Object)reqId);
            json.put("REQUEST_STATUS", status);
            json.put("REMARKS", (Object)remarks);
            json.put("ERROR_CODE", errorCode);
            this.updateEnrollmentStatus(json);
        }
        catch (final Exception exp) {
            this.logger.severe("Exception occured in updateEnrollmentStatusAndErrorCode:" + exp);
        }
    }
    
    public int getEnrollmentRequestStatus(final Long enrollmentRequestId, final String deviceUDID) {
        int enrollmentStatus = 1;
        try {
            if (enrollmentRequestId == null) {
                return enrollmentStatus;
            }
            final JSONObject input = new JSONObject();
            input.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestId);
            final JSONObject json = this.getEnrollmentRequestStatusAndErrorCode(input);
            if (json.has("REQUEST_STATUS")) {
                enrollmentStatus = json.getInt("REQUEST_STATUS");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> " Exception while getting enrollment status for enrollment request id " + n);
        }
        return enrollmentStatus;
    }
    
    private Properties getADUserDetails(final String domainName, final String userName, final String password, final Long customerID) throws Exception {
        final List propList = new ArrayList();
        propList.add(DirectoryAttributeConstants.getAttrKey(Long.valueOf(107L)));
        propList.add(DirectoryAttributeConstants.getAttrKey(Long.valueOf(106L)));
        propList.add(DirectoryAttributeConstants.getAttrKey(Long.valueOf(112L)));
        propList.add(DirectoryAttributeConstants.getAttrKey(Long.valueOf(109L)));
        propList.add(DirectoryAttributeConstants.getAttrKey(Long.valueOf(108L)));
        propList.add(DirectoryAttributeConstants.getAttrKey(Long.valueOf(110L)));
        propList.add(DirectoryAttributeConstants.getAttrKey(Long.valueOf(111L)));
        return IdpsFactoryProvider.getIdpsAccessAPI(domainName, customerID).getThisADObjectProperties(domainName, 2, propList, userName, customerID);
    }
    
    public JSONObject validateUserName(final JSONObject json) {
        final JSONObject result = new JSONObject();
        try {
            final String domainName = String.valueOf(json.get("DOMAIN_NETBIOS_NAME"));
            final String userName = String.valueOf(json.get("NAME"));
            final String password = json.optString("PASSWORD", "");
            final Long customerID = json.getLong("customerID");
            if (!MDMUtil.getInstance().isEmpty(password) || domainName.equalsIgnoreCase("MDM") || ADAccessProvider.getInstance().isValidADObjectName(domainName, userName, 2)) {
                result.put("IS_USERNAME_VALID", true);
                final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
                squery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
                squery.setCriteria(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0)).and(new Criteria(Column.getColumn("Resource", "NAME"), (Object)userName, 0, false)).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0, false)));
                squery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
                squery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
                final DataObject dobj = MDMUtil.getPersistence().get(squery);
                final Iterator<Row> iter = dobj.getRows("ManagedUser");
                if (iter.hasNext()) {
                    final Row r = iter.next();
                    final String managedEmail = (String)r.get("EMAIL_ADDRESS");
                    if (!MDMUtil.getInstance().isValidEmail(managedEmail)) {
                        result.put("EMAIL_ADDRESS", (Object)"");
                        if (!domainName.equalsIgnoreCase("MDM")) {
                            final Properties aduser = this.getADUserDetails(domainName, userName, password, customerID);
                            if (aduser != null && aduser.containsKey("mail") && MDMUtil.getInstance().isValidEmail((String)((Hashtable<K, String>)aduser).get("mail"))) {
                                result.put("EMAIL_ADDRESS", (Object)((Hashtable<K, String>)aduser).get("mail"));
                            }
                        }
                    }
                    else {
                        result.put("EMAIL_ADDRESS", (Object)managedEmail);
                    }
                    result.put("IS_MANAGED_USER", true);
                }
                else if (!domainName.equalsIgnoreCase("MDM")) {
                    final Properties aduser2 = this.getADUserDetails(domainName, userName, password, customerID);
                    if (aduser2 != null) {
                        if (aduser2.containsKey("userPrincipalName") && !MDMUtil.getInstance().isEmpty(((Hashtable<K, String>)aduser2).get("userPrincipalName")) && userName.equalsIgnoreCase(((Hashtable<K, String>)aduser2).get("userPrincipalName"))) {
                            result.put("USER_NAME", (Object)((Hashtable<K, String>)aduser2).get("userPrincipalName"));
                        }
                        if (aduser2.containsKey("sAMAccountName") && !MDMUtil.getInstance().isEmpty(((Hashtable<K, String>)aduser2).get("sAMAccountName")) && userName.equalsIgnoreCase(((Hashtable<K, String>)aduser2).get("sAMAccountName"))) {
                            result.put("USER_NAME", (Object)((Hashtable<K, String>)aduser2).get("sAMAccountName"));
                        }
                        if (aduser2.containsKey("givenName") && !MDMUtil.getInstance().isEmpty(((Hashtable<K, String>)aduser2).get("givenName"))) {
                            result.put("FIRST_NAME", (Object)((Hashtable<K, String>)aduser2).get("givenName"));
                        }
                        if (aduser2.containsKey("initials") && !MDMUtil.getInstance().isEmpty(((Hashtable<K, String>)aduser2).get("initials"))) {
                            result.put("MIDDLE_NAME", (Object)((Hashtable<K, String>)aduser2).get("initials"));
                        }
                        if (aduser2.containsKey("sn") && !MDMUtil.getInstance().isEmpty(((Hashtable<K, String>)aduser2).get("sn"))) {
                            result.put("LAST_NAME", (Object)((Hashtable<K, String>)aduser2).get("sn"));
                        }
                        if (aduser2.containsKey("displayName") && !MDMUtil.getInstance().isEmpty(((Hashtable<K, String>)aduser2).get("displayName"))) {
                            result.put("DISPLAY_NAME", (Object)((Hashtable<K, String>)aduser2).get("displayName"));
                        }
                        if (aduser2.containsKey("mail") && MDMUtil.getInstance().isValidEmail((String)((Hashtable<K, String>)aduser2).get("mail"))) {
                            result.put("EMAIL_ADDRESS", (Object)((Hashtable<K, String>)aduser2).get("mail"));
                            result.put("IS_MANAGED_USER", false);
                            result.put("IS_EMAIL_IN_AD", true);
                        }
                        else {
                            result.put("EMAIL_ADDRESS", (Object)"");
                            result.put("IS_MANAGED_USER", false);
                            result.put("IS_EMAIL_IN_AD", false);
                        }
                    }
                }
                else {
                    result.put("EMAIL_ADDRESS", (Object)"");
                    result.put("IS_MANAGED_USER", false);
                    result.put("IS_EMAIL_IN_AD", false);
                }
            }
            else {
                result.put("IS_USERNAME_VALID", false);
                result.put("ERROR_MSG", (Object)I18N.getMsg("dc.webclient.som.UserName_not_found", new Object[] { StringEscapeUtils.escapeHtml(userName), StringEscapeUtils.escapeHtml(domainName) }));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while validating username {0}", ex);
        }
        return result;
    }
    
    public int getAddedEnrollmentRequestCountForCustomer(final Long customerID, final JSONObject json) throws Exception {
        int enrolledDeviceCount = 0;
        final SelectQuery query = this.getAddedEnrollmentRequestQuery();
        Criteria c = query.getCriteria();
        c = c.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
        if (json != null) {
            final Iterator<String> iter = json.keys();
            while (iter.hasNext()) {
                final String column = iter.next();
                c = c.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", column), json.get(column), 0));
            }
        }
        query.setCriteria(c);
        enrolledDeviceCount = DBUtil.getRecordCount(query, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
        return enrolledDeviceCount;
    }
    
    public int getAddedEnrollmentRequestCount(final Integer platform) throws Exception {
        if (platform != null) {
            final Criteria platformCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)platform, 0);
            return this.getAddedEnrollmentRequestCount(platformCri);
        }
        return this.getAddedEnrollmentRequestCount((Criteria)null);
    }
    
    public int getAddedEnrollmentRequestCount() throws Exception {
        return this.getAddedEnrollmentRequestCount((Criteria)null);
    }
    
    public int getAddedEnrollmentRequestCount(final Criteria criteria) throws Exception {
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            int enrolledDeviceCount = 0;
            final SelectQuery query = this.getAddedEnrollmentRequestQuery();
            if (criteria != null) {
                Criteria c = query.getCriteria();
                if (c == null) {
                    c = criteria;
                }
                else {
                    c = c.and(criteria);
                }
                query.setCriteria(c);
            }
            enrolledDeviceCount = DBUtil.getRecordCount(query, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
            return enrolledDeviceCount;
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.WARNING, "Exception occured while getting added Enrollment request count: {0}", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
    }
    
    public ArrayList getAddedEnrollmentRequestList() throws Exception {
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final ArrayList addedEnrollmentRequestList = new ArrayList();
            final SelectQuery query = this.getAddedEnrollmentRequestQuery();
            query.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
            query.addSelectColumn(new Column("Resource", "NAME"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("Resource");
                while (iterator.hasNext()) {
                    final Row resourceRow = iterator.next();
                    final HashMap resourceInfo = new HashMap();
                    resourceInfo.put("RESOURCE_ID", resourceRow.get("RESOURCE_ID"));
                    resourceInfo.put("NAME", resourceRow.get("NAME"));
                    addedEnrollmentRequestList.add(resourceInfo);
                }
            }
            return addedEnrollmentRequestList;
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.WARNING, "Exception occured while getting added Enrollment request list : {0}", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
    }
    
    private SelectQuery getAddedEnrollmentRequestQuery() {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ManagedUser"));
        query.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        query.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("DeviceEnrollmentRequest", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        query.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        query.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        query.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addJoin(new Join("DeviceEnrollmentRequest", "InvitationEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        final Criteria enrollNotSelfReqCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)2, 1);
        final Criteria enrollSelfReqCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)2, 0);
        final Criteria enrollStatusCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)3, 0);
        final Criteria managedDeviceCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 1);
        final Criteria selfEnrollDeviceCheckedIn = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "UDID"), (Object)null, 1);
        Criteria enrollReqCri = enrollNotSelfReqCri.or(enrollSelfReqCri.and(enrollStatusCri.or(managedDeviceCri).or(selfEnrollDeviceCheckedIn)));
        final Criteria deviceForEnroll = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
        final Criteria noDeviceForEnroll = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 0);
        final Criteria deviceToUser = new Criteria(Column.getColumn("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
        final Criteria nonTemplateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)null, 0);
        final Criteria templateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)null, 1);
        final Criteria adminEnrollmentCriteria = templateCriteria.and(noDeviceForEnroll.or(deviceForEnroll.and(deviceToUser))).or(nonTemplateCriteria);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        enrollReqCri = enrollReqCri.and(adminEnrollmentCriteria).and(userNotInTrashCriteria);
        query.setCriteria(enrollReqCri);
        return query;
    }
    
    public boolean is5223PortBlocked(final Long customerID) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            sQuery.addJoin(new Join("Resource", "DeviceEnrollmentRequest", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            sQuery.addJoin(new Join("DeviceEnrollmentRequest", "DeviceEnrollReqToErrCode", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            sQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)0, 0).and(new Criteria(Column.getColumn("DeviceEnrollReqToErrCode", "ERROR_CODE"), (Object)51201, 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0))));
            final int recordCount = DBUtil.getRecordCount(sQuery, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
            return recordCount > 0;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occured while checking if any device has 5223 port blocked : {0}", ex);
            return false;
        }
    }
    
    public JSONObject getEnrollmentRequestStatusAndErrorCode(final JSONObject jsonObject) {
        try {
            final Long erid = jsonObject.getLong("ENROLLMENT_REQUEST_ID");
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            sQuery.addJoin(new Join("DeviceEnrollmentRequest", "DeviceEnrollReqToErrCode", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", "DEVICEENROLLMENTREQUEST.ENROLLMENT_REQUEST_ID"));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollReqToErrCode", "ENROLLMENT_REQUEST_ID", "DEVICEENROLLREQTOERRCODE.ENROLLMENT_REQUEST_ID"));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollReqToErrCode", "ERROR_CODE"));
            sQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
            MDMUtil.getInstance();
            final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            final JSONObject json = new JSONObject();
            if (!dobj.isEmpty()) {
                final Row r = dobj.getRow("DeviceEnrollmentRequest");
                json.put("REQUEST_STATUS", (Object)r.get("REQUEST_STATUS"));
                if ((int)r.get("REQUEST_STATUS") == 0) {
                    final Row errorRow = dobj.getRow("DeviceEnrollReqToErrCode", new Criteria(Column.getColumn("DeviceEnrollReqToErrCode", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
                    final Integer errorCode = (Integer)errorRow.get("ERROR_CODE");
                    json.put("ERROR_CODE", (Object)errorCode);
                }
            }
            return json;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting enrollment request status and error code : {0} ", ex);
            return new JSONObject();
        }
    }
    
    public Long getCustomerIDForEnrollmentRequest(final Long enrollmentRequestID) {
        try {
            final SelectQuery sQuery = this.getEnrollmentDetailsQuery(enrollmentRequestID);
            sQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            final Row r = dobj.getRow("Resource");
            return (Long)r.get("CUSTOMER_ID");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "HARM-LESS Exception while getting customer id {0}", ex.getMessage());
            return null;
        }
    }
    
    public int getEnrollmentRequestStatus(final Long enrollmentRequestId) {
        int enrollReqStatus = -1;
        try {
            final Criteria enrollCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", enrollCri);
            if (!dObj.isEmpty()) {
                enrollReqStatus = (int)dObj.getValue("DeviceEnrollmentRequest", "REQUEST_STATUS", enrollCri);
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, "Exception in getEnrollmentRequestStatus {0}", ex);
        }
        return enrollReqStatus;
    }
    
    public void updateMailSentStatus() {
        int count = 0;
        try {
            final Criteria statusCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)1, 0);
            final Criteria enrollTypeCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)1, 0);
            final Criteria remarksCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REMARKS"), (Object)"dc.mdm.enroll.successfully_added_mail_to_queue", 0);
            count = DBUtil.getRecordActualCount("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", statusCriteria.and(remarksCriteria).and(enrollTypeCriteria));
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMEnrollmentRequestHandler.class.getName()).log(Level.SEVERE, "{0}", ex);
        }
        ApiFactoryProvider.getCacheAccessAPI().putCache("MDM_ENROLL_MAIL_IN_QUEUE", (Object)(count > 0), 2);
    }
    
    public boolean getMailSentStatus() {
        return ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_ENROLL_MAIL_IN_QUEUE", 2) != null && (boolean)ApiFactoryProvider.getCacheAccessAPI().getCache("MDM_ENROLL_MAIL_IN_QUEUE", 2);
    }
    
    public HashMap getEnrollmentMap(final Long enrollmentRequestId) {
        final HashMap enrollemntMap = new HashMap();
        int enrollmentType = 1;
        try {
            final Criteria enrollReqCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", enrollReqCri);
            if (!dObj.isEmpty()) {
                enrollmentType = (int)dObj.getFirstValue("DeviceEnrollmentRequest", "ENROLLMENT_TYPE");
                final Long managedUserId = (Long)dObj.getFirstValue("DeviceEnrollmentRequest", "MANAGED_USER_ID");
                final Long techUserId = (Long)dObj.getFirstValue("DeviceEnrollmentRequest", "USER_ID");
                if (managedUserId != null) {
                    enrollemntMap.put("MANAGED_USER_ID", managedUserId);
                }
                enrollemntMap.put("ENROLLMENT_TYPE", enrollmentType);
                enrollemntMap.put("USER_ID", techUserId);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in isSelfEnrollment", exp);
        }
        return enrollemntMap;
    }
    
    public int getEnrollmentType(final Long enrollmentRequestId) {
        int enrollmentType = 1;
        try {
            final Criteria enrollReqCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("DeviceEnrollmentRequest", enrollReqCri);
            if (!dObj.isEmpty()) {
                enrollmentType = (int)dObj.getFirstValue("DeviceEnrollmentRequest", "ENROLLMENT_TYPE");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in isSelfEnrollment", exp);
        }
        return enrollmentType;
    }
    
    public void updateEnrollmentRequestProperties(final Long erid, final JSONObject enrollmentRequestDetails) {
        try {
            final UpdateQuery uquery = (UpdateQuery)new UpdateQueryImpl("DeviceEnrollmentRequest");
            final Iterator iter = enrollmentRequestDetails.keys();
            while (iter.hasNext()) {
                final String key = iter.next();
                if (!key.equals("ENROLLMENT_REQUEST_ID")) {
                    uquery.setUpdateColumn(key, enrollmentRequestDetails.get(key));
                }
            }
            uquery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
            MDMUtil.getPersistence().update(uquery);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in updateEnrollmentRequestProperties", e);
        }
    }
    
    public int getWakeUpRetryCount(final Long customerId) throws Exception {
        int enrolledDeviceCount = 0;
        final SelectQuery query = this.getAddedEnrollmentRequestQuery();
        query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentNotification", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        Criteria c = query.getCriteria();
        final Criteria wakeUpNotif = new Criteria(Column.getColumn("EnrollmentNotification", "IS_SOURCE_TOKEN_UPDATE"), (Object)false, 0);
        final Criteria enrollStatus = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)1, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        c = c.and(wakeUpNotif.and(enrollStatus).and(customerCriteria));
        query.setCriteria(c);
        enrolledDeviceCount = DBUtil.getRecordCount(query, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
        return enrolledDeviceCount;
    }
    
    public JSONObject getEnrollmentRequestProperties(final Long erid) {
        return this.getEnrollmentRequestProperties(erid, null);
    }
    
    public JSONObject getEnrollmentRequestProperties(final Long erid, final Long customerId) {
        final JSONObject json = new JSONObject();
        try {
            final SelectQuery sQuery = this.getEnrollmentDetailsQuery(erid);
            sQuery.addSelectColumn(Column.getColumn("Resource", "*"));
            sQuery.addSelectColumn(Column.getColumn("ManagedUser", "*"));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "*"));
            sQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "*"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            sQuery.addSelectColumn(Column.getColumn("DEVICEENROLLREQTOSMS", "*"));
            if (customerId != null) {
                final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
                sQuery.setCriteria(sQuery.getCriteria().and(customerCriteria));
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                final List tableList = dataObject.getTableNames();
                for (int i = 0; i < tableList.size(); ++i) {
                    final String tableName = tableList.get(i);
                    final Row row = dataObject.getFirstRow(tableName);
                    final List columnList = row.getColumns();
                    for (int j = 0; j < columnList.size(); ++j) {
                        final String columnName = columnList.get(j);
                        json.put(tableName + "." + columnName, row.get(columnName));
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getEnrollmentRequestProperties", e);
        }
        return json;
    }
    
    private SelectQuery getEnrollmentDetailsQuery(final Long erid) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        sQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        sQuery.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "DEVICEENROLLREQTOSMS", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        sQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0).and(userNotInTrashCriteria));
        return sQuery;
    }
    
    public void modifyEnrollUserDetails(final JSONObject json) throws Exception {
        final String deviceName = String.valueOf(json.get("DEVICE_NAME"));
        final Long erid = json.getLong("ENROLLMENT_REQUEST_ID");
        final HashMap oldManagedUserDetails = ManagedUserHandler.getInstance().getManagedUserDetailsForRequest(erid);
        final String oldUserName = oldManagedUserDetails.get("NAME");
        final String oldDomainName = oldManagedUserDetails.get("DOMAIN_NETBIOS_NAME");
        Long managedUserID = -1L;
        if (json.has("MANAGED_USER_ID")) {
            managedUserID = JSONUtil.optLongForUVH(json, "MANAGED_USER_ID", Long.valueOf(-1L));
        }
        else {
            managedUserID = ManagedUserHandler.getInstance().addOrUpdateAndGetUserId(json);
        }
        HashMap newManagedUserDetails = ManagedUserHandler.getInstance().getManagedUserDetails(managedUserID);
        final String newUserName = newManagedUserDetails.get("NAME");
        final String newDomainName = newManagedUserDetails.get("DOMAIN_NETBIOS_NAME");
        DMSecurityLogger.info(this.assignUserLogger, AdminEnrollmentHandler.class.getName(), "modifyEnrollUserDetails()", "Fetching user details to proceed with user assignment : " + json.toString(), (Object)null);
        JSONObject managedUserJSON = new JSONObject();
        final Long customerId = json.getLong("CUSTOMER_ID");
        final String userName = json.optString("NAME");
        final Long toAssignUserId = JSONUtil.optLongForUVH(json, "MANAGED_USER_ID", Long.valueOf(-1L));
        Long userId;
        if (toAssignUserId != -1L) {
            this.assignUserLogger.log(Level.INFO, "Changing user with user ID picked from picklist user API : {0}", toAssignUserId);
            new UserFacade().validateIfUsersExists(Arrays.asList(toAssignUserId), customerId);
            if (!ManagedUserHandler.getInstance().isUserManaged(toAssignUserId, customerId)) {
                this.assignUserLogger.log(Level.INFO, "Given Domain user is not managed user hence adding the same to Managed user");
                final Properties domainUserProps = ADSyncDataHandler.getInstance().getDirUserProps(toAssignUserId, customerId);
                ((Hashtable<String, Long>)domainUserProps).put("MANAGED_USER_ID", toAssignUserId);
                if (domainUserProps != null && !domainUserProps.isEmpty()) {
                    DMSecurityLogger.info(this.assignUserLogger, AdminEnrollmentHandler.class.getName(), "modifyEnrollUserDetails()", "Fetched domain user props for given user id : " + toAssignUserId + " props : " + domainUserProps.toString(), (Object)null);
                    ManagedUserHandler.getInstance().addOrUpdateManagedUser(domainUserProps);
                }
            }
            userId = toAssignUserId;
        }
        else {
            this.assignUserLogger.log(Level.INFO, "Assigning user with user name, email and domain as input");
            final JSONObject userJSON = new JSONObject();
            userJSON.put("USER_IDENTIFIER", (Object)"NAME");
            userJSON.put("NAME", (Object)userName);
            userJSON.put("DOMAIN_NETBIOS_NAME", (Object)json.optString("DOMAIN_NETBIOS_NAME"));
            userJSON.put("CUSTOMER_ID", (Object)customerId);
            userJSON.put("USER_IDENTIFIER", (Object)"NAME");
            managedUserJSON = ManagedUserHandler.getInstance().getManagedUserDetails(userJSON);
            userId = JSONUtil.optLongForUVH(managedUserJSON, "MANAGED_USER_ID", Long.valueOf(-1L));
            if (userId == -1L) {
                userId = AdminEnrollmentHandler.validateAndAddUser(json, !json.optBoolean("skip_user_validation", (boolean)Boolean.FALSE));
            }
        }
        this.assignUserLogger.log(Level.INFO, "Assigning user with managedUserId:{0}", userId);
        if (managedUserJSON.has("MANAGED_USER_ID")) {
            newManagedUserDetails = JSONUtil.getInstance().ConvertToSameDataTypeHash(managedUserJSON);
        }
        else {
            newManagedUserDetails = ManagedUserHandler.getInstance().getManagedUserDetails(userId);
        }
        newManagedUserDetails.put("MANAGED_USER_ID", userId);
        if (!oldUserName.equalsIgnoreCase(newUserName) || !oldDomainName.equalsIgnoreCase(newDomainName)) {
            ManagedUserHandler.getInstance().changeUser(managedUserID, new Long[] { json.getLong("ENROLLMENT_REQUEST_ID") });
            final String i18n = "dc.mdm.enroll.change_user_message_actionlog";
            final Object remarksArgs = oldUserName + "@@@" + newUserName + "@@@" + deviceName;
            final String loginUserName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, loginUserName, i18n, remarksArgs, json.getLong("CUSTOMER_ID"));
            final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
            logJSON.put((Object)"REMARKS", (Object)"assign-success");
            logJSON.put((Object)"MANAGED_USER_ID", (Object)managedUserID);
            logJSON.put((Object)"ENROLLMENT_REQUEST_ID", (Object)erid);
            MDMOneLineLogger.log(Level.INFO, "DEVICE_USER_ASSIGNED", logJSON);
        }
        if (newDomainName != null && newDomainName.equalsIgnoreCase("MDM") && json.has("EMAIL_ADDRESS")) {
            final String oldEmailAddress = newManagedUserDetails.get("EMAIL_ADDRESS");
            final String newEmailAddress = String.valueOf(json.get("EMAIL_ADDRESS"));
            if (!MDMUtil.getInstance().isValidEmail(oldEmailAddress) || !oldEmailAddress.equalsIgnoreCase(newEmailAddress)) {
                json.put("UPDATE_BLINDLY", true);
                ManagedUserHandler.getInstance().updateEmailAddressForManagedUser(json, json.getLong("CUSTOMER_ID"));
            }
        }
        final Long resourceID = json.getLong("MANAGED_DEVICE_ID");
        if (json.optBoolean("isDeviceAwaitingUserInManaged")) {
            if (!MDMEnrollmentUtil.getInstance().isLicenseLimitReached(customerId)) {
                ManagedDeviceHandler.getInstance().updateManagedStatusToEnrolled(Arrays.asList(resourceID));
                this.assignUserLogger.log(Level.INFO, "Reassigning device stuck in managed tab with waiting for user assignment: ", resourceID);
            }
            else {
                this.assignUserLogger.log(Level.INFO, "Unable to reassign device stuck in managed tab with waiting for user assignment because of license: ", resourceID);
            }
        }
        final org.json.simple.JSONObject deviceJSON = new org.json.simple.JSONObject();
        deviceJSON.put((Object)"NAME", (Object)deviceName);
        deviceJSON.put((Object)"MANAGED_DEVICE_ID", (Object)resourceID);
        deviceJSON.put((Object)"IS_MODIFIED", (Object)true);
        deviceJSON.put((Object)"ENROLLMENT_REQUEST_ID", (Object)erid);
        MDCustomDetailsRequestHandler.getInstance().addOrUpdateCustomDeviceDetails(deviceJSON);
        if (deviceName != null && !deviceName.equals("")) {
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            MDCustomDetailsRequestHandler.getInstance().checkAndSendDeviceNameUpdateCommand(resourceList);
        }
    }
    
    public void addOrUpdateInvitationDetails(final JSONObject json) throws JSONException, DataAccessException {
        final String sourceMethod = "addOrUpdateInvitationDetails";
        try {
            if (json.opt("ENROLLMENT_REQUEST_ID") != null) {
                final Long erid = json.getLong("ENROLLMENT_REQUEST_ID");
                final Criteria criteria = new Criteria(Column.getColumn("InvitationEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0);
                final DataObject dataObject = MDMUtil.getPersistence().get("InvitationEnrollmentRequest", criteria);
                if (dataObject.isEmpty()) {
                    final Row inviteRow = new Row("InvitationEnrollmentRequest");
                    inviteRow.set("ENROLLMENT_REQUEST_ID", (Object)erid);
                    inviteRow.set("IS_INVITED_BY_ADMIN", (Object)json.optBoolean("IS_INVITED_BY_ADMIN", false));
                    inviteRow.set("REGISTRATION_STATUS", (Object)json.optInt("REGISTRATION_STATUS", 0));
                    final DataObject dobj = (DataObject)new WritableDataObject();
                    dobj.addRow(inviteRow);
                    MDMUtil.getPersistence().add(dobj);
                }
                else {
                    final Row inviteRow = dataObject.getRow("InvitationEnrollmentRequest");
                    final int platformType = json.optInt("PlatformType");
                    if (json.opt("IS_INVITED_BY_ADMIN") != null) {
                        inviteRow.set("IS_INVITED_BY_ADMIN", (Object)json.optBoolean("IS_INVITED_BY_ADMIN"));
                    }
                    if (json.has("REGISTRATION_STATUS")) {
                        final int status = json.getInt("REGISTRATION_STATUS");
                        if (status == 1 && platformType != 1 && ManagedUserHandler.getInstance().getNoOfRequests(json.optLong("MANAGED_USER_ID"), platformType) != 1) {
                            return;
                        }
                        inviteRow.set("REGISTRATION_STATUS", (Object)json.getInt("REGISTRATION_STATUS"));
                    }
                    dataObject.updateRow(inviteRow);
                    MDMUtil.getPersistence().update(dataObject);
                }
            }
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while adding entry in invitation enrollment table", (Throwable)exp);
        }
    }
    
    public JSONObject getEnrollmentDetails(final Long erid) {
        return this.getEnrollmentDetails(erid, null);
    }
    
    public JSONObject getEnrollmentDetails(final Long erid, final Long customerId) {
        JSONObject details = new JSONObject();
        try {
            final JSONObject json = getInstance().getEnrollmentRequestProperties(erid, customerId);
            if (json.length() > 0) {
                details.put("email_address", (Object)URLEncoder.encode(json.optString("ManagedUser.EMAIL_ADDRESS"), "UTF-8"));
                final int platformType = json.optInt("DeviceEnrollmentRequest.PLATFORM_TYPE");
                final int enrollmentType = json.optInt("DeviceEnrollmentRequest.ENROLLMENT_TYPE");
                details.put("auth_mode", json.optInt("DeviceEnrollmentRequest.AUTH_MODE"));
                details.put("owned_by", json.optInt("DeviceEnrollmentRequest.OWNED_BY"));
                details.put("phone_number", json.optInt("DEVICEENROLLREQTOSMS.PHONE_NUMBER"));
                details.put("play_store_url", (Object)"https://play.google.com/store/apps/details?id=com.manageengine.mdm.android");
                details.put("platform_type", platformType);
                details.put("platform", (Object)MDMEnrollmentUtil.getPlatformString(platformType));
                details.put("otp_password", (Object)MDMEnrollmentOTPHandler.getInstance().getOTPPassword(erid));
                details.put("enrollment_type_constant", enrollmentType);
                switch (enrollmentType) {
                    case 1: {
                        details.put("ENROLLMENT_TYPE", (Object)I18N.getMsg("dc.common.enrollment.invitation", new Object[0]));
                        break;
                    }
                    case 2: {
                        details.put("ENROLLMENT_TYPE", (Object)I18N.getMsg("dc.mdm.enroll.self_enrollment", new Object[0]));
                        break;
                    }
                    case 3: {
                        details.put("ENROLLMENT_TYPE", (Object)I18N.getMsg("mdm.enroll.admin_enroll", new Object[0]));
                        break;
                    }
                }
                details = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().appendEnrollmentPropertiesForEnrollmentInUI(erid, platformType, details);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMEnrollmentUtil.class.getName()).log(Level.SEVERE, "Exception occurred in getEnrollmentDetails", ex);
        }
        return details;
    }
    
    public JSONObject getCurrentTechnicianDetails(final Long customerID) {
        final JSONObject userDetails = new JSONObject();
        String userName = null;
        String domainName = null;
        String emailAddress = null;
        String phoneNumber = null;
        Boolean isEditable = true;
        try {
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                emailAddress = DMUserHandler.getDCUser(loginId);
                final JSONObject userDetailsCloud = ManagedUserHandler.getInstance().getManagedUserDetailsForEmailAddress(emailAddress, customerID);
                if (userDetailsCloud == null || userDetailsCloud.length() == 0) {
                    userName = emailAddress.split("@")[0];
                    phoneNumber = "";
                }
                else {
                    domainName = (String)userDetailsCloud.get("DOMAIN_NETBIOS_NAME");
                    userName = (String)userDetailsCloud.get("NAME");
                    phoneNumber = (String)(userDetailsCloud.has("PHONE_NUMBER") ? userDetailsCloud.get("PHONE_NUMBER") : "");
                    isEditable = false;
                }
            }
            else {
                final long userid = MDMUtil.getInstance().getLoggedInUserID();
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
                selectQuery.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
                selectQuery.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 1));
                selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 1));
                selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
                final Criteria criteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), (Object)userid, 0);
                selectQuery.setCriteria(criteria);
                final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
                userName = (String)DO.getFirstValue("AaaLogin", "NAME");
                domainName = (String)DO.getFirstValue("AaaLogin", "DOMAINNAME");
                if (domainName == null || domainName.equalsIgnoreCase("-")) {
                    domainName = "MDM";
                }
                emailAddress = (String)DO.getFirstValue("AaaContactInfo", "EMAILID");
                if (emailAddress != null && !emailAddress.equals("")) {
                    isEditable = false;
                }
                final HashMap map = ManagedUserHandler.getInstance().getManagedUserDetailsForUserName(userName, domainName, customerID);
                if (!map.isEmpty() && map.containsKey("MANAGED_USER_ID")) {
                    emailAddress = map.get("EMAIL_ADDRESS");
                    isEditable = false;
                }
            }
            userDetails.put("UserName", (Object)userName);
            userDetails.put("DomainName", (Object)domainName);
            userDetails.put("EmailAddress", (Object)URLEncoder.encode(emailAddress, "UTF-8"));
            userDetails.put("PHONE_NUMBER", (Object)phoneNumber);
            userDetails.put("DISPLAY_IMAGE", (Object)(ApiFactoryProvider.getUtilAccessAPI().getServerURL() + "/images/user_2.png"));
            userDetails.put("isEditable", (Object)isEditable);
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMEnrollmentUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userDetails;
    }
    
    public SelectQuery getInvitationEnrollRequestQuery() {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
        query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        query.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addJoin(new Join("DeviceEnrollmentRequest", "InvitationEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        final Criteria inviteMyself = new Criteria(Column.getColumn("InvitationEnrollmentRequest", "IS_INVITED_BY_ADMIN"), (Object)true, 0);
        final Criteria inviteUser = new Criteria(Column.getColumn("InvitationEnrollmentRequest", "IS_INVITED_BY_ADMIN"), (Object)false, 0);
        final Criteria enrolled = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 1);
        final CaseExpression inviteMyselfCount = new CaseExpression("inviteMyselfCount");
        inviteMyselfCount.addWhen(inviteMyself, (Object)new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        final CaseExpression inviteUserCount = new CaseExpression("inviteUserCount");
        inviteUserCount.addWhen(inviteUser, (Object)new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        final CaseExpression enrolledMyselfCount = new CaseExpression("enrolledMyselfCount");
        enrolledMyselfCount.addWhen(inviteMyself.and(enrolled), (Object)new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        final CaseExpression enrolledUserCount = new CaseExpression("enrolledUserCount");
        enrolledUserCount.addWhen(inviteUser.and(enrolled), (Object)new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        query.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(inviteMyselfCount));
        query.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(inviteUserCount));
        query.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(enrolledMyselfCount));
        query.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(enrolledUserCount));
        return query;
    }
    
    public boolean isBOMPresent(final InputStream input) throws IOException {
        final BufferedReader r = new BufferedReader(new InputStreamReader(input, "UTF8"));
        boolean bomPresent = false;
        final String UTF8_BOM = "\ufeff";
        final String s = r.readLine();
        if (s != null && s.startsWith(UTF8_BOM)) {
            bomPresent = true;
        }
        r.close();
        return bomPresent;
    }
    
    public boolean isUTFencoding(final InputStream input) {
        boolean isUTFencoding = false;
        final InputStreamReader is = new InputStreamReader(input);
        if (is.getEncoding().equalsIgnoreCase("utf8")) {
            isUTFencoding = true;
        }
        return isUTFencoding;
    }
    
    private JSONObject sendEnrollmentSMS(final Properties smsInputProperties) throws Exception {
        final SMSAPI smsAPI = MDMApiFactoryProvider.getSMSAPI();
        String message = "";
        final Long enrollmentRequestID = ((Hashtable<K, Long>)smsInputProperties).get("ENROLLMENT_REQUEST_ID");
        final String otpPassword = ((Hashtable<K, String>)smsInputProperties).get("OTPPassword");
        final Integer authMode = ((Hashtable<K, Integer>)smsInputProperties).get("AUTH_MODE");
        final String tinyURL = ((Hashtable<K, String>)smsInputProperties).get("ENROLLMENT_URL");
        String phoneNumber = ((Hashtable<K, String>)smsInputProperties).get("PHONE_NUMBER");
        phoneNumber = phoneNumber.replace(" ", "");
        final String countryCode = ((Hashtable<K, String>)smsInputProperties).get("COUNTRY_CODE");
        final String senderAddress = ((Hashtable<K, String>)smsInputProperties).get("SENDER_ADDRESS");
        message = "Your IT admin " + senderAddress + " has requested you to enroll your device using " + tinyURL + " . ";
        if (authMode == 3 || authMode == 1) {
            message = message + "OTP : " + otpPassword;
        }
        final Properties smsProperties = new Properties();
        ((Hashtable<String, String>)smsProperties).put("PHONE_NUMBER", phoneNumber);
        ((Hashtable<String, String>)smsProperties).put("COUNTRY_CODE", countryCode);
        ((Hashtable<String, String>)smsProperties).put("MESSAGE", message);
        ((Hashtable<String, String>)smsProperties).put("SENDER_NAME", "ME MDM");
        ((Hashtable<String, Long>)smsProperties).put("ENROLLMENTREQUEST_ID", enrollmentRequestID);
        ((Hashtable<String, String>)smsInputProperties).put("MESSAGE", message);
        this.logger.log(Level.INFO, "MDMEnrollmentRequestHandler: SMS Properties: {0}", smsProperties);
        return smsAPI.sendHTTPToSMS(smsProperties);
    }
    
    private void updateStatus(final boolean isSMSSent, final Properties properties, final Long enrollmentRequestID) throws Exception {
        final SMSAPI smsAPI = MDMApiFactoryProvider.getSMSAPI();
        if (smsAPI.isSMSSettingsConfigured()) {
            final Object newEnrollReqRawObject = ((Hashtable<K, Object>)properties).get("NewEnrollReq");
            Boolean newEnrollReq = false;
            if (newEnrollReqRawObject != null) {
                newEnrollReq = Boolean.valueOf(newEnrollReqRawObject.toString());
            }
            String remarksMsg = "dc.mdm.enroll.request_created";
            if (properties.containsKey("sendEmail") && properties.containsKey("sendSMS") && ((Hashtable<K, Boolean>)properties).get("sendEmail") && ((Hashtable<K, Boolean>)properties).get("sendSMS")) {
                final JSONObject smsObjectRow = this.getSMSSentStatus(enrollmentRequestID);
                if (isSMSSent && (int)smsObjectRow.get("SMS_CODE") == 0) {
                    remarksMsg = (newEnrollReq ? "dc.mdm.enroll.email_sms" : "mdm.enroll.successfully_resend_enrollment_request");
                }
                else {
                    remarksMsg = "dc.mdm.enroll.email";
                }
            }
            else if (properties.containsKey("sendEmail") && ((Hashtable<K, Boolean>)properties).get("sendEmail")) {
                remarksMsg = "dc.mdm.enroll.email";
            }
            else if (properties.containsKey("sendSMS") && ((Hashtable<K, Boolean>)properties).get("sendSMS")) {
                final JSONObject smsObjectRow = this.getSMSSentStatus(enrollmentRequestID);
                if (isSMSSent && (int)smsObjectRow.get("SMS_CODE") == 0) {
                    remarksMsg = "dc.mdm.enroll.sms";
                }
                else {
                    remarksMsg = "dc.mdm.enroll.enroll_fail";
                }
            }
            this.updateEnrollmentRemarksAndStatus(enrollmentRequestID, remarksMsg);
        }
    }
    
    public Long[] getEnrollmentRequestIdsFromManagedDeviceIDs(final Long[] managedDeviceID) {
        final Long[] enrollmentId = new Long[managedDeviceID.length];
        final SelectQuery sql = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        sql.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        sql.setCriteria(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)managedDeviceID, 8));
        sql.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "*"));
        try {
            final DataObject dao = MDMUtil.getPersistence().get(sql);
            if (!dao.isEmpty()) {
                final Iterator itr = dao.getRows("EnrollmentRequestToDevice");
                int i = 0;
                while (itr.hasNext()) {
                    final Row row = itr.next();
                    enrollmentId[i++] = (Long)row.get("ENROLLMENT_REQUEST_ID");
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getUDIDFromEnrollmentRequestID", ex);
        }
        return enrollmentId;
    }
    
    public void incrementOTPFailedAttemptCount(final Long enrollmentRequestID) {
        try {
            this.logger.log(Level.INFO, "Incrementing otp failed count for enrollment request ID: {0}", enrollmentRequestID);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("OTPPassword"));
            selectQuery.addSelectColumn(Column.getColumn("OTPPassword", "ENROLLMENT_REQUEST_ID"));
            selectQuery.addSelectColumn(Column.getColumn("OTPPassword", "FAILED_ATTEMPTS"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("OTPPassword", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            final Row otpRow = dataObject.getRow("OTPPassword");
            final int failedAttempts = (int)otpRow.get("FAILED_ATTEMPTS");
            otpRow.set("FAILED_ATTEMPTS", (Object)(failedAttempts + 1));
            dataObject.updateRow(otpRow);
            MDMUtil.getPersistenceLite().update(dataObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while incrementing enrollment failed attempt count", e);
        }
    }
    
    static {
        MDMEnrollmentRequestHandler.requestHandler = null;
    }
}
