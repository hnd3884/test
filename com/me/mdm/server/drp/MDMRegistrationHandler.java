package com.me.mdm.server.drp;

import java.util.Hashtable;
import com.adventnet.persistence.WritableDataObject;
import com.me.mdm.server.enrollment.EnrollRestrictionHandler;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.server.enrollment.MDMAgentUpdateHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.List;
import java.util.Collection;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import org.apache.commons.codec.binary.Base64;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.enrollment.MDMEnrollmentOTPHandler;
import com.me.mdm.core.auth.APIKey;
import java.util.Properties;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.enrollment.approval.EnrollmentApprovalHandler;
import com.me.mdm.server.enrollment.approval.EnrollmentRequest;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.idps.core.util.ADSyncDataHandler;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import org.json.JSONObject;
import java.util.logging.Logger;
import java.util.HashMap;

public class MDMRegistrationHandler
{
    private static MDMRegistrationHandler handler;
    private static HashMap restrictionClassMap;
    Logger logger;
    
    public MDMRegistrationHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static MDMRegistrationHandler getInstance(final String devicePlatform) {
        (MDMRegistrationHandler.restrictionClassMap = new HashMap()).put(1, "com.me.mdm.server.enrollment.ios.IOSEnrollRestrictionHandler");
        if (devicePlatform.equalsIgnoreCase("WindowsPhone")) {
            MDMRegistrationHandler.handler = new WpMDMRegistrationHandler();
        }
        else if (devicePlatform.equalsIgnoreCase("Android")) {
            MDMRegistrationHandler.handler = new AndroidMDMRegistrationHandler();
        }
        else if (devicePlatform.equalsIgnoreCase("iOS")) {
            MDMRegistrationHandler.handler = new AppleMDMRegistrationHandler();
        }
        else {
            MDMRegistrationHandler.handler = new MDMRegistrationHandler();
        }
        return MDMRegistrationHandler.handler;
    }
    
    public JSONObject processMessage(final JSONObject requestJSON) throws Exception {
        final String messageType = String.valueOf(requestJSON.get("MsgRequestType"));
        if (messageType.equalsIgnoreCase("Discover")) {
            return this.processDiscoverMessage(requestJSON);
        }
        if (messageType.equalsIgnoreCase("Authenticate")) {
            return this.processAuthenticateMessage(requestJSON);
        }
        if (messageType.equalsIgnoreCase("TokenUpdate")) {
            return this.processTokenUpdateMessage(requestJSON);
        }
        if (messageType.equalsIgnoreCase("RegistrationStatusUpdate")) {
            return this.processRegistrationStatusUpdateMessage(requestJSON);
        }
        if (messageType.equalsIgnoreCase("MdmClientDetails")) {
            return this.processMdmClientDetailsMessage(requestJSON);
        }
        if (messageType.equalsIgnoreCase("DeRegistrationStatusUpdate")) {
            return this.processDeRegistrationStatusUpdateMessage(requestJSON);
        }
        if (messageType.equalsIgnoreCase("DeviceProvisioningSettings")) {
            return this.processDeviceProvisioningMessage(requestJSON);
        }
        return new JSONObject();
    }
    
    protected JSONObject processAuthenticateMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final String registrationType = msgRequestJSON.optString("RegistrationType", "MDMRegistration");
        if (registrationType.equalsIgnoreCase("MDMRegistration")) {
            return this.processDeviceAuthenticateMessage(requestJSON);
        }
        return this.processAppAuthenticateMessage(requestJSON);
    }
    
    protected JSONObject processDeviceAuthenticateMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final JSONObject msgResponseJSON = new JSONObject();
        try {
            responseJSON.put("MsgResponseType", (Object)"AuthenticateResponse");
            final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
            final String authenticationMode = String.valueOf(msgRequestJSON.get("AuthMode"));
            Long enrollmentRequestID = Long.parseLong(msgRequestJSON.optString("EnrollmentRequestID", "-1"));
            msgRequestJSON.put("EnrollmentRequestID", (Object)enrollmentRequestID);
            boolean isDeviceAllowed = false;
            if (enrollmentRequestID == -1L) {
                final JSONObject discoverJSON = this.processDiscoverMessage(requestJSON);
                if (String.valueOf(discoverJSON.get("Status")).equalsIgnoreCase("Acknowledged")) {
                    final JSONObject discoverResponseJSON = discoverJSON.getJSONObject("MsgResponse");
                    enrollmentRequestID = discoverResponseJSON.optLong("EnrollmentRequestID", -1L);
                    msgRequestJSON.put("EnrollmentRequestID", (Object)enrollmentRequestID);
                }
            }
            try {
                final JSONObject paramsJSON = new JSONObject();
                paramsJSON.put("AuthMode", (Object)authenticationMode);
                this.allowEnrollment(enrollmentRequestID, this.getPlatformConstant(String.valueOf(requestJSON.get("DevicePlatform"))), paramsJSON);
            }
            catch (final SyMException e) {
                if (enrollmentRequestID != -1L) {
                    MDMEnrollmentRequestHandler.getInstance().updateEnrollFailedStatus(enrollmentRequestID, e.getErrorKey(), e.getErrorCode());
                }
                responseJSON.put("Status", (Object)"Error");
                msgResponseJSON.put("ErrorMsg", (Object)e.getMessage());
                msgResponseJSON.put("ErrorCode", e.getErrorCode());
                msgResponseJSON.put("ErrorKey", (Object)e.getErrorKey());
                responseJSON.put("MsgResponse", (Object)msgResponseJSON);
                return responseJSON;
            }
            if (authenticationMode.equalsIgnoreCase("ActiveDirectory")) {
                isDeviceAllowed = this.processADAuthentication(requestJSON, msgRequestJSON, responseJSON, msgResponseJSON);
            }
            else if (authenticationMode.equalsIgnoreCase("OTP")) {
                isDeviceAllowed = this.processOTPPasscode(requestJSON, msgRequestJSON, msgResponseJSON);
            }
            else if (authenticationMode.equalsIgnoreCase("AzureADToken")) {
                isDeviceAllowed = this.processAzureADToken(requestJSON, msgRequestJSON, msgResponseJSON);
            }
            else {
                isDeviceAllowed = this.processOTPPasscode(requestJSON, msgRequestJSON, msgResponseJSON);
                if (isDeviceAllowed) {
                    isDeviceAllowed = this.processADAuthentication(requestJSON, msgRequestJSON, responseJSON, msgResponseJSON);
                }
            }
            if (isDeviceAllowed) {
                enrollmentRequestID = msgResponseJSON.getLong("EnrollmentRequestID");
                msgResponseJSON.put("OwnedBy", (Object)this.getOwnedByString((Integer)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID, "OWNED_BY")));
                MDMEnrollmentRequestHandler.getInstance().updateEnrollmentStatusAndErrorCode(enrollmentRequestID, 1, "dc.mdm.enroll.request_created", -1);
                responseJSON.put("Status", (Object)"Acknowledged");
            }
            else {
                responseJSON.put("Status", (Object)"Error");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occured during device authentication", exp);
            msgResponseJSON.put("ErrorCode", 12007);
            msgResponseJSON.put("ErrorMsg", (Object)"Authentication has failed");
            msgResponseJSON.put("ErrorKey", (Object)"dc.mdm.enroll.auth_failed");
        }
        responseJSON.put("MsgResponse", (Object)msgResponseJSON);
        return responseJSON;
    }
    
    private boolean processADAuthentication(final JSONObject requestJSON, final JSONObject msgRequestJSON, final JSONObject responseJSON, final JSONObject msgResponseJSON) throws Exception {
        boolean isDeviceAllowed = false;
        boolean isEmailValid = true;
        final String platformValue = String.valueOf(requestJSON.get("DevicePlatform"));
        String userName = String.valueOf(msgRequestJSON.get("UserName"));
        final String domainName = String.valueOf(msgRequestJSON.get("DomainName"));
        final String password = String.valueOf(msgRequestJSON.get("ADPassword"));
        Long enrollmentRequestID = msgRequestJSON.optLong("EnrollmentRequestID", -1L);
        final int platformType = this.getPlatformConstant(platformValue);
        Long customerID = null;
        if (enrollmentRequestID == -1L) {
            customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
        }
        else {
            customerID = MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(enrollmentRequestID);
        }
        msgRequestJSON.put("CustomerID", (Object)customerID);
        final boolean isSelfEnrollEnabled = EnrollmentSettingsHandler.getInstance().isSelfEnrollmentEnabled(customerID);
        boolean isValidatedThroughSelfEnrollment = false;
        String sEventLogRemarks = "dc.mdm.actionlog.enrollment.self_enroll_request";
        Properties requestProperties = null;
        if (enrollmentRequestID == -1L && isSelfEnrollEnabled) {
            isValidatedThroughSelfEnrollment = true;
        }
        if (enrollmentRequestID != -1L || isSelfEnrollEnabled) {
            if (isValidatedThroughSelfEnrollment) {
                final Properties ADUserProps = ADSyncDataHandler.getInstance().getDirUserProps(customerID, domainName, userName);
                final String email = msgRequestJSON.optString("EmailAddress");
                if (!email.equalsIgnoreCase(((Hashtable<K, String>)ADUserProps).get("EMAIL_ADDRESS"))) {
                    isEmailValid = false;
                }
            }
            if (isEmailValid) {
                isDeviceAllowed = IdpsFactoryProvider.getIdpsAccessAPI(domainName, customerID).validatePassword(domainName, userName, password, customerID);
            }
        }
        Label_0385: {
            if (isDeviceAllowed) {
                if (isSelfEnrollEnabled && enrollmentRequestID == -1L) {
                    if (EnrollmentSettingsHandler.getInstance().getSelfEnrollmentSettings(customerID).getInt("APPROVAL_MODE") != 2) {
                        break Label_0385;
                    }
                    try {
                        EnrollmentApprovalHandler.approveEnrollmentRequest(new EnrollmentRequest(msgRequestJSON));
                        break Label_0385;
                    }
                    catch (final SyMException ex) {
                        isDeviceAllowed = false;
                        msgResponseJSON.put("ErrorCode", ex.getErrorCode());
                        msgResponseJSON.put("ErrorMsg", (Object)ex.getMessage());
                        msgResponseJSON.put("ErrorKey", (Object)ex.getErrorKey());
                        return isDeviceAllowed;
                    }
                }
                final JSONObject json = new JSONObject();
                json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                json.put("REGISTRATION_STATUS", 2);
                MDMEnrollmentRequestHandler.getInstance().addOrUpdateInvitationDetails(json);
            }
        }
        if (!isDeviceAllowed) {
            sEventLogRemarks = "dc.mdm.actionlog.enrollment.self_enroll_AD_failed";
            if (isValidatedThroughSelfEnrollment) {
                MDMEnrollmentUtil.getInstance().addSelfEnrollEventLog(sEventLogRemarks, userName, customerID);
                if (isEmailValid) {
                    msgResponseJSON.put("ErrorCode", 12003);
                    msgResponseJSON.put("ErrorMsg", (Object)"AD_Validation_has_failed");
                    msgResponseJSON.put("ErrorKey", (Object)"dc.mdm.enroll.ad_val_failed");
                }
                else {
                    msgResponseJSON.put("ErrorCode", 52111);
                    msgResponseJSON.put("ErrorMsg", (Object)"Email_is_not_of_the_provided_AD_user");
                    msgResponseJSON.put("ErrorKey", (Object)"dc.mdm.enroll.email_not_valid");
                }
            }
            else {
                sEventLogRemarks = "dc.mdm.enroll.AD_val_failed";
                MDMEnrollmentRequestHandler.getInstance().updateEnrollFailedStatus(enrollmentRequestID, sEventLogRemarks, 12003);
                msgResponseJSON.put("ErrorCode", 12003);
                msgResponseJSON.put("ErrorMsg", (Object)"AD_Validation_has_failed");
                msgResponseJSON.put("ErrorKey", (Object)"dc.mdm.enroll.ad_val_failed");
            }
        }
        if (isDeviceAllowed && isValidatedThroughSelfEnrollment) {
            this.fetchValidUserDetails(responseJSON, msgRequestJSON, msgResponseJSON);
            final String emailAddress = msgRequestJSON.optString("EmailAddress");
            userName = msgRequestJSON.optString("UserName");
            final String sOwnedBy = msgRequestJSON.optString("OwnedBy", "Corporate");
            final Integer ownedBy = this.getOwnedByConstant(sOwnedBy);
            final Long defaultMDMGroupId = MDMGroupHandler.getInstance().getDefaultMDMSelfEnrollGroupId(customerID, platformType, ownedBy);
            final Properties enrollProp = MDMEnrollmentUtil.getInstance().buildEnrollmentProperties(domainName, userName, defaultMDMGroupId, emailAddress, String.valueOf(ownedBy), customerID, true, String.valueOf(platformType), true);
            ((Hashtable<String, Boolean>)enrollProp).put("isSelfEnroll", true);
            requestProperties = MDMEnrollmentRequestHandler.getInstance().addEnrollmentRequest(enrollProp);
            enrollmentRequestID = ((Hashtable<K, Long>)requestProperties).get("ENROLLMENT_REQUEST_ID");
            MDMEnrollmentUtil.getInstance().addSelfEnrollEventLog(sEventLogRemarks, userName, customerID);
            final int enrollStatus = ((Hashtable<K, Integer>)requestProperties).get("ENROLL_STATUS");
            if (enrollStatus == 1) {
                sEventLogRemarks = "dc.mdm.actionlog.enrollment.self_enroll_request_success";
                MDMEnrollmentUtil.getInstance().addSelfEnrollEventLog(sEventLogRemarks, userName, customerID);
            }
            else {
                sEventLogRemarks = "dc.mdm.actionlog.enrollment.self_enroll_request_failed";
                MDMEnrollmentUtil.getInstance().addSelfEnrollEventLog(sEventLogRemarks, userName, customerID);
            }
        }
        if (isDeviceAllowed) {
            msgResponseJSON.put("EnrollmentRequestID", (Object)enrollmentRequestID);
            msgResponseJSON.put("CustomerID", (Object)customerID);
            final JSONObject json = new JSONObject();
            json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(json);
            msgResponseJSON.put("Services", (Object)key.toClientJSON());
        }
        return isDeviceAllowed;
    }
    
    private boolean processOTPPasscode(final JSONObject requestJSON, final JSONObject msgRequestJSON, final JSONObject msgResponseJSON) {
        boolean isDeviceAllowed = false;
        try {
            Long enrollmentRequestID = msgRequestJSON.optLong("EnrollmentRequestID", -1L);
            final int failedAttempts = MDMEnrollmentOTPHandler.getInstance().getFailedAttempts(enrollmentRequestID);
            if (failedAttempts < 3) {
                final String otpPasscode = String.valueOf(msgRequestJSON.get("OTPPassword"));
                if (enrollmentRequestID != -1L) {
                    msgRequestJSON.put("AuthMode", (Object)this.getAuthenticationModeString(MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestProperties(enrollmentRequestID).getInt("DeviceEnrollmentRequest.AUTH_MODE")));
                }
                final DataObject dataObject = this.getEnrollmentRequestDataObject(requestJSON, msgRequestJSON);
                if (!dataObject.isEmpty()) {
                    final Criteria cOPT = new Criteria(new Column("OTPPassword", "OTP_PASSWORD"), (Object)otpPasscode, 0).and(new Criteria(Column.getColumn("OTPPassword", "FAILED_ATTEMPTS"), (Object)3, 6));
                    final Row optRow = dataObject.getRow("OTPPassword", cOPT);
                    final Criteria expiredCriteria = new Criteria(Column.getColumn("OTPPassword", "EXPIRE_TIME"), (Object)System.currentTimeMillis(), 5);
                    final Row opExptRow = dataObject.getRow("OTPPassword", cOPT.and(expiredCriteria));
                    if (optRow == null) {
                        MDMEnrollmentRequestHandler.getInstance().updateEnrollFailedStatus(enrollmentRequestID, "dc.mdm.enroll.opt_val_failed", 12004);
                        msgResponseJSON.put("ErrorCode", 12004);
                        msgResponseJSON.put("ErrorMsg", (Object)"OTP Validation has failed");
                        msgResponseJSON.put("ErrorKey", (Object)"dc.mdm.enroll.otp_val_failed");
                        MDMEnrollmentRequestHandler.getInstance().incrementOTPFailedAttemptCount(enrollmentRequestID);
                    }
                    else if (opExptRow == null) {
                        MDMEnrollmentRequestHandler.getInstance().updateEnrollFailedStatus(enrollmentRequestID, "dc.mdm.enroll.opt_expire_failed", 12010);
                        msgResponseJSON.put("ErrorCode", 12010);
                        msgResponseJSON.put("ErrorMsg", (Object)"OTP has expired");
                        msgResponseJSON.put("ErrorKey", (Object)"dc.mdm.enroll.otp_expired");
                    }
                    else {
                        enrollmentRequestID = (Long)opExptRow.get("ENROLLMENT_REQUEST_ID");
                        msgResponseJSON.put("EnrollmentRequestID", (Object)enrollmentRequestID);
                        final Row resourceRow = dataObject.getRow("Resource");
                        final Long customerID = (Long)resourceRow.get("CUSTOMER_ID");
                        msgResponseJSON.put("CustomerID", (Object)customerID);
                        final JSONObject json = new JSONObject();
                        json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                        final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(json);
                        msgResponseJSON.put("Services", (Object)key.toClientJSON());
                        isDeviceAllowed = true;
                        final JSONObject jsonobj = new JSONObject();
                        jsonobj.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                        jsonobj.put("REGISTRATION_STATUS", 2);
                        MDMEnrollmentRequestHandler.getInstance().addOrUpdateInvitationDetails(jsonobj);
                    }
                }
            }
            else {
                MDMEnrollmentRequestHandler.getInstance().updateEnrollFailedStatus(enrollmentRequestID, "dc.mdm.enroll.otp_invalidated", 12015);
                msgResponseJSON.put("ErrorCode", 12015);
                msgResponseJSON.put("ErrorMsg", (Object)"OTP is invalidated due to repeated failed attempts. Contact your admin to regenerate OTP.");
                msgResponseJSON.put("ErrorKey", (Object)I18N.getMsg("dc.mdm.enroll.otp_invalidated_max_attempts", new Object[0]));
                MDMEnrollmentRequestHandler.getInstance().incrementOTPFailedAttemptCount(enrollmentRequestID);
                isDeviceAllowed = false;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occured during authentication {0}", exp);
        }
        return isDeviceAllowed;
    }
    
    private Boolean processAzureADToken(final JSONObject requestJSON, final JSONObject msgRequestJSON, final JSONObject msgResponseJSON) throws JSONException {
        Boolean isDeviceAllowed = Boolean.FALSE;
        String sEventLogRemarks = "mdm.enrollment.azure_ad_enrollment";
        final String jwtTokenString = String.valueOf(msgRequestJSON.get("AzureAdWebToken"));
        final String[] jwtTokens = jwtTokenString.split("\\.");
        final JSONObject azureWebTokenHeaderJSON = new JSONObject(new String(Base64.decodeBase64(jwtTokens[0])));
        final JSONObject azureWebTokenBodyJSON = new JSONObject(new String(Base64.decodeBase64(jwtTokens[1])));
        final String userPrincipalName = String.valueOf(azureWebTokenBodyJSON.get("upn"));
        final String emailAddress = String.valueOf(azureWebTokenBodyJSON.get("unique_name"));
        final String userName = String.valueOf(azureWebTokenBodyJSON.get("name"));
        final String sEventLogRemarksArgs = userName + "@@@" + userPrincipalName;
        final String domainName = userPrincipalName.split("@")[1];
        final String sOwnedBy = msgRequestJSON.optString("OwnedBy", "Corporate");
        Long customerID = msgRequestJSON.optLong("CustomerID", -99L);
        if (customerID.equals(-99L)) {
            customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
        }
        final Properties enrollProp = MDMEnrollmentUtil.getInstance().buildEnrollmentProperties(domainName, userPrincipalName, null, emailAddress, String.valueOf(this.getOwnedByConstant(sOwnedBy)), customerID, true, String.valueOf(this.getPlatformConstant(String.valueOf(requestJSON.get("DevicePlatform")))), true);
        ((Hashtable<String, Boolean>)enrollProp).put("isSelfEnroll", true);
        final Properties requestProperties = MDMEnrollmentRequestHandler.getInstance().addEnrollmentRequest(enrollProp);
        final Long enrollmentRequestID = ((Hashtable<K, Long>)requestProperties).get("ENROLLMENT_REQUEST_ID");
        MDMEnrollmentUtil.getInstance().addSelfEnrollEventLog(sEventLogRemarks, sEventLogRemarksArgs, customerID);
        final int enrollStatus = ((Hashtable<K, Integer>)requestProperties).get("ENROLL_STATUS");
        if (enrollStatus == 1) {
            sEventLogRemarks = "mdm.enrollment.azure_ad_enrollment_success";
            MDMEnrollmentUtil.getInstance().addSelfEnrollEventLog(sEventLogRemarks, sEventLogRemarksArgs, customerID);
            isDeviceAllowed = Boolean.TRUE;
        }
        else {
            sEventLogRemarks = "mdm.enrollment.azure_ad_enrollment_failed";
            MDMEnrollmentUtil.getInstance().addSelfEnrollEventLog(sEventLogRemarks, sEventLogRemarksArgs, customerID);
        }
        if (isDeviceAllowed) {
            msgResponseJSON.put("EnrollmentRequestID", (Object)enrollmentRequestID);
            msgResponseJSON.put("CustomerID", (Object)customerID);
            final JSONObject json = new JSONObject();
            json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(json);
            msgResponseJSON.put("Services", (Object)key.toClientJSON());
        }
        return isDeviceAllowed;
    }
    
    protected JSONObject processAppAuthenticateMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final JSONObject msgResponseJSON = new JSONObject();
        try {
            final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
            String authEnrollmentId = msgRequestJSON.optString("OTPPassword", (String)null);
            if (authEnrollmentId == null) {
                authEnrollmentId = String.valueOf(msgRequestJSON.get("OTPPasscode"));
            }
            responseJSON.put("MsgResponseType", (Object)"AuthenticateResponse");
            final SelectQuery enrollAuthQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("IOSNativeAppAuthentication"));
            final Join deviceJoin = new Join("IOSNativeAppAuthentication", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join erJoin = new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1);
            enrollAuthQuery.addJoin(deviceJoin);
            enrollAuthQuery.addJoin(erJoin);
            enrollAuthQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            enrollAuthQuery.addSelectColumn(Column.getColumn("IOSNativeAppAuthentication", "MANAGED_DEVICE_ID"));
            enrollAuthQuery.addSelectColumn(Column.getColumn("IOSNativeAppAuthentication", "AUTH_CODE"));
            enrollAuthQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            enrollAuthQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
            enrollAuthQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
            final Criteria enrollIdCri = new Criteria(Column.getColumn("IOSNativeAppAuthentication", "AUTH_CODE"), (Object)authEnrollmentId, 0);
            enrollAuthQuery.setCriteria(enrollIdCri);
            final DataObject enrollAuthDO = MDMUtil.getPersistence().get(enrollAuthQuery);
            if (!enrollAuthDO.isEmpty()) {
                final String udid = (String)enrollAuthDO.getFirstValue("ManagedDevice", "UDID");
                responseJSON.put("Status", (Object)"Acknowledged");
                if (udid != null) {
                    msgResponseJSON.put("UDID", (Object)udid);
                    final Long erid = (Long)enrollAuthDO.getFirstValue("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID");
                    if (erid != null) {
                        msgResponseJSON.put("EnrollmentRequestID", (Object)erid);
                    }
                }
            }
            else {
                responseJSON.put("Status", (Object)"Error");
                msgResponseJSON.put("ErrorKey", (Object)"dc.mdm.actionlog.enrollment.invalid_enrollmentId");
                msgResponseJSON.put("ErrorMsg", (Object)I18N.getMsg("dc.mdm.actionlog.enrollment.invalid_enrollmentId", new Object[0]));
                msgResponseJSON.put("ErrorCode", 12011);
            }
        }
        catch (final Exception exp) {
            msgResponseJSON.put("ErrorKey", (Object)"dc.mdm.actionlog.enrollment.invalid_enrollmentId");
            msgResponseJSON.put("ErrorMsg", (Object)I18N.getMsg("dc.mdm.actionlog.enrollment.invalid_enrollmentId", new Object[0]));
            msgResponseJSON.put("ErrorCode", 12011);
        }
        responseJSON.put("MsgResponse", (Object)msgResponseJSON);
        return responseJSON;
    }
    
    protected JSONObject processTokenUpdateMessage(final JSONObject requestJSON) throws Exception {
        return null;
    }
    
    protected JSONObject processDiscoverMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final JSONObject messageResponseJSON = new JSONObject();
        try {
            responseJSON.put("MsgResponseType", (Object)"DiscoverResponse");
            final Integer platformType = this.getPlatformConstant(String.valueOf(requestJSON.get("DevicePlatform")));
            final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
            final Long erid = msgRequestJSON.optLong("EnrollmentRequestID", -1L);
            final String email = msgRequestJSON.optString("EmailAddress");
            final Boolean isLangPackEnabled = LicenseProvider.getInstance().isLanguagePackEnabled();
            DataObject dataObject = null;
            Boolean enrollReqFound;
            if (erid == -1L && platformType == 1) {
                enrollReqFound = false;
            }
            else {
                dataObject = this.getEnrollmentRequestDataObject(requestJSON, msgRequestJSON);
                enrollReqFound = !dataObject.isEmpty();
            }
            JSONArray jsonDomainArr = null;
            Long enrollmentRequestID = -1L;
            final JSONObject json = new JSONObject();
            if (!enrollReqFound) {
                if (erid != -1L) {
                    if (this.isOtherPlatformRequest(requestJSON, responseJSON)) {
                        return responseJSON;
                    }
                    responseJSON.put("Status", (Object)"Error");
                    messageResponseJSON.put("ErrorMsg", (Object)"Invalid Enrollment Id");
                    messageResponseJSON.put("ErrorKey", (Object)"dc.mdm.enroll.device_enrolled_failed");
                    messageResponseJSON.put("ErrorCode", 12001);
                }
                else {
                    Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
                    if (msgRequestJSON.has("CustomerID")) {
                        customerId = msgRequestJSON.getLong("CustomerID");
                    }
                    final String authMode = msgRequestJSON.optString("AuthMode", "ActiveDirectory");
                    final boolean isSelfEnroll = EnrollmentSettingsHandler.getInstance().isSelfEnrollmentEnabled(customerId);
                    if (isSelfEnroll || authMode.equalsIgnoreCase("AzureADToken")) {
                        responseJSON.put("Status", (Object)"Acknowledged");
                        messageResponseJSON.put("AuthMode", (Object)authMode);
                        final List domainNameList = MDMEnrollmentUtil.getInstance().getDomainNamesWithoutGSuite(customerId);
                        final JSONObject enrollmentSettingRow = EnrollmentSettingsHandler.getInstance().getSelfEnrollmentSettings(customerId);
                        if (enrollmentSettingRow != null) {
                            messageResponseJSON.put("OwnedBy", (Object)this.getOwnedByString(enrollmentSettingRow.getInt("OWNED_BY_OPTION")));
                        }
                        messageResponseJSON.put("IsLanguagePackEnabled", (Object)isLangPackEnabled);
                        jsonDomainArr = new JSONArray((Collection)domainNameList);
                        messageResponseJSON.put("DomainNameList", (Object)jsonDomainArr);
                    }
                    else {
                        responseJSON.put("Status", (Object)"Error");
                        if (email != null && !email.isEmpty()) {
                            messageResponseJSON.put("ErrorMsg", (Object)"Enter the Email Address given in the Enrollment Mail");
                            messageResponseJSON.put("ErrorKey", (Object)"dc.mdm.actionlog.enrollment.invalid_email");
                            messageResponseJSON.put("ErrorCode", 12001);
                        }
                        else {
                            messageResponseJSON.put("ErrorMsg", (Object)"Self Enrollment is Disabled! Contact your system administrator to enroll your device.");
                            messageResponseJSON.put("ErrorKey", (Object)"dc.mdm.enroll.self_enrollment_disable");
                            messageResponseJSON.put("ErrorCode", 51011);
                        }
                    }
                }
            }
            else {
                responseJSON.put("Status", (Object)"Acknowledged");
                messageResponseJSON.put("IsLanguagePackEnabled", (Object)isLangPackEnabled);
                final Row enrollRow = dataObject.getRow("DeviceEnrollmentRequest");
                enrollmentRequestID = (Long)enrollRow.get("ENROLLMENT_REQUEST_ID");
                final Integer authMode2 = (Integer)enrollRow.get("AUTH_MODE");
                final Integer ownedBy = (Integer)enrollRow.get("OWNED_BY");
                final Long muid = (Long)enrollRow.get("MANAGED_USER_ID");
                if (authMode2 != 1) {
                    messageResponseJSON.put("OwnedBy", (Object)this.getOwnedByString(ownedBy));
                }
                final Row resourceRow = dataObject.getRow("Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)muid, 0));
                final String userName = (String)resourceRow.get("NAME");
                final String domainName = (String)resourceRow.get("DOMAIN_NETBIOS_NAME");
                messageResponseJSON.put("UserName", (Object)userName);
                jsonDomainArr = new JSONArray();
                jsonDomainArr.put((Object)domainName);
                messageResponseJSON.put("DomainNameList", (Object)jsonDomainArr);
                messageResponseJSON.put("AuthMode", (Object)this.getAuthenticationModeString(authMode2));
                messageResponseJSON.put("EnrollmentRequestID", (Object)enrollmentRequestID);
                json.put("MANAGED_USER_ID", (Object)muid);
                json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                json.put("PlatformType", (Object)platformType);
                json.put("REGISTRATION_STATUS", 1);
            }
            if (String.valueOf(responseJSON.get("Status")).equalsIgnoreCase("Acknowledged")) {
                try {
                    final JSONObject paramsJSON = new JSONObject();
                    paramsJSON.put("AuthMode", (Object)msgRequestJSON.optString("AuthMode", ""));
                    this.allowEnrollment(enrollmentRequestID, this.getPlatformConstant(String.valueOf(requestJSON.get("DevicePlatform"))), paramsJSON);
                }
                catch (final SyMException e) {
                    if (enrollmentRequestID != -1L) {
                        MDMEnrollmentRequestHandler.getInstance().updateEnrollFailedStatus(enrollmentRequestID, e.getErrorKey(), e.getErrorCode());
                    }
                    responseJSON.put("Status", (Object)"Error");
                    messageResponseJSON.put("ErrorMsg", (Object)e.getMessage());
                    messageResponseJSON.put("ErrorCode", e.getErrorCode());
                    messageResponseJSON.put("ErrorKey", (Object)e.getErrorKey());
                    responseJSON.put("MsgResponse", (Object)messageResponseJSON);
                    return responseJSON;
                }
                MDMEnrollmentRequestHandler.getInstance().addOrUpdateInvitationDetails(json);
            }
        }
        catch (final Exception exp) {
            responseJSON.put("Status", (Object)"Error");
            messageResponseJSON.put("ErrorMsg", (Object)"Enrollment request not found for given details!");
            messageResponseJSON.put("ErrorKey", (Object)"dc.mdm.enroll.device_enrolled_failed");
            messageResponseJSON.put("ErrorCode", 12001);
            this.logger.log(Level.SEVERE, "Exception in processDiscoverMessage |", exp);
        }
        responseJSON.put("MsgResponse", (Object)messageResponseJSON);
        return responseJSON;
    }
    
    protected JSONObject processRegistrationStatusUpdateMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final String registrationType = msgRequestJSON.optString("RegistrationType", "MDMRegistration");
        if (registrationType.equalsIgnoreCase("MDMRegistration")) {
            return this.registerMDMDevice(requestJSON);
        }
        return this.registerMDMApp(requestJSON);
    }
    
    protected JSONObject processMdmClientDetailsMessage(final JSONObject requestJSON) throws JSONException {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"MdmClientDetailsResponse");
        final JSONObject messageRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final String eridStr = String.valueOf(messageRequestJSON.get("EnrollmentRequestID"));
        final Long erid = Long.parseLong(eridStr);
        final String udid = ManagedDeviceHandler.getInstance().getUDIDFromEnrollmentRequestID(erid);
        final JSONObject messageResponseJSON = new JSONObject();
        if (udid == null) {
            responseJSON.put("Status", (Object)"Error");
            messageResponseJSON.put("ErrorCode", 51016);
            messageResponseJSON.put("ErrorKey", (Object)"dc.db.mdm.not_yet_enrolled_successfully");
            messageResponseJSON.put("ErrorMsg", (Object)"UDID not present. Device Not yet Enrolled Successfully.");
        }
        else {
            responseJSON.put("Status", (Object)"Acknowledged");
            messageResponseJSON.put("UDID", (Object)udid);
        }
        responseJSON.put("MsgResponse", (Object)messageResponseJSON);
        return responseJSON;
    }
    
    private JSONObject registerMDMDevice(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        try {
            responseJSON.put("MsgResponseType", (Object)"RegistrationStatusUpdateResponse");
            final String platform = String.valueOf(requestJSON.get("DevicePlatform"));
            final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
            final int platformType = this.getPlatformConstant(platform);
            final String deviceUDID = String.valueOf(msgRequestJSON.get("UDID"));
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
            final String deviceName = String.valueOf(msgRequestJSON.get("NAME"));
            final Long customerID = msgRequestJSON.optLong("CustomerID");
            final Long enrollmentRequestID = msgRequestJSON.optLong("EnrollmentRequestID");
            final int agentType = msgRequestJSON.getInt("agenttype");
            final String status = requestJSON.optString("Status");
            final JSONObject properties = new JSONObject();
            properties.put("CUSTOMER_ID", (Object)customerID);
            properties.put("UDID", (Object)deviceUDID);
            properties.put("NAME", (Object)deviceName);
            properties.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
            properties.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            properties.put("MANAGED_USER_ID", (Object)enrollmentRequestID);
            properties.put("MANAGED_STATUS", (Object)new Integer(2));
            properties.put("AGENT_TYPE", agentType);
            properties.put("REMARKS", (Object)"dc.mdm.db.agent.enroll.agent_enroll_finished");
            properties.put("PLATFORM_TYPE", platformType);
            properties.put("REQUEST_STATUS", 3);
            properties.put("isAppleConfig", false);
            ManagedDeviceHandler.getInstance().addOrUpdateManagedDevice(properties);
            if (status.equalsIgnoreCase("Acknowledged")) {
                this.addorUpdateIOSAgentInstallationStatus(resourceID, 1);
            }
            responseJSON.put("Status", (Object)"Acknowledged");
        }
        catch (final Exception exp) {
            responseJSON.put("Status", (Object)"Error");
        }
        return responseJSON;
    }
    
    private JSONObject registerMDMApp(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        try {
            responseJSON.put("MsgResponseType", (Object)"ResgistrationStatusUpdateResponse");
            String status = requestJSON.optString("Status", (String)null);
            final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
            final String deviceUDID = String.valueOf(msgRequestJSON.get("UDID"));
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
            if (status == null) {
                status = String.valueOf(msgRequestJSON.get("Status"));
            }
            if (status.equalsIgnoreCase("Acknowledged")) {
                final Boolean isAgentupgrade = msgRequestJSON.optBoolean("IsAppUpgraded", false);
                if (isAgentupgrade) {
                    this.handleAppUpgrade(msgRequestJSON);
                }
                else {
                    final String platformValue = requestJSON.optString("DevicePlatform");
                    final int platformtype = this.getPlatformConstant(platformValue);
                    this.handleAppRegistration(resourceID, platformtype);
                }
                this.processPostAppRegistration(requestJSON);
            }
            responseJSON.put("Status", (Object)"Acknowledged");
            final boolean isLangPackEnabled = LicenseProvider.getInstance().isLanguagePackEnabled();
            responseJSON.put("IsLanguagePackEnabled", isLangPackEnabled);
        }
        catch (final Exception exp) {
            responseJSON.put("Status", (Object)"Error");
        }
        return responseJSON;
    }
    
    public int getPlatformConstant(final String platformValue) {
        int platform = -1;
        if (platformValue != null) {
            if (platformValue.equalsIgnoreCase("ios")) {
                platform = 1;
            }
            else if (platformValue.equalsIgnoreCase("android")) {
                platform = 2;
            }
            else if (StringUtils.containsIgnoreCase((CharSequence)platformValue, (CharSequence)"windows")) {
                platform = 3;
            }
            else if (StringUtils.containsIgnoreCase((CharSequence)platformValue, (CharSequence)"mac")) {
                platform = 1;
            }
        }
        return platform;
    }
    
    protected String getAuthenticationModeString(final int authMode) {
        String authenticationMode = null;
        if (authMode == 2) {
            authenticationMode = "ActiveDirectory";
        }
        else if (authMode == 1) {
            authenticationMode = "OTP";
        }
        else {
            authenticationMode = "Combined";
        }
        return authenticationMode;
    }
    
    void addorUpdateIOSAgentInstallationStatus(final Long deviceId, final int installationStatus) {
        try {
            final Criteria deviceCri = new Criteria(Column.getColumn("IOSNativeAppStatus", "RESOURCE_ID"), (Object)deviceId, 0);
            final DataObject dObj = MDMUtil.getPersistence().get("IOSNativeAppStatus", deviceCri);
            Row iOSNativeRow = null;
            if (dObj.isEmpty()) {
                iOSNativeRow = new Row("IOSNativeAppStatus");
                iOSNativeRow.set("RESOURCE_ID", (Object)deviceId);
                iOSNativeRow.set("INSTALLATION_STATUS", (Object)installationStatus);
                iOSNativeRow.set("INSTALLED_TIME", (Object)System.currentTimeMillis());
                iOSNativeRow.set("LAST_CONTACTED_TIME", (Object)System.currentTimeMillis());
                dObj.addRow(iOSNativeRow);
                MDMUtil.getPersistence().add(dObj);
            }
            else {
                iOSNativeRow = dObj.getRow("IOSNativeAppStatus", deviceCri);
                iOSNativeRow.set("INSTALLATION_STATUS", (Object)installationStatus);
                iOSNativeRow.set("LAST_CONTACTED_TIME", (Object)System.currentTimeMillis());
                dObj.updateRow(iOSNativeRow);
                MDMUtil.getPersistence().update(dObj);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in addorupdateIOSAgentInstallationStatus ", ex);
        }
    }
    
    private void handleAppUpgrade(final JSONObject msgRequestJSON) {
        try {
            final HashMap upgradeMap = JSONUtil.getInstance().ConvertJSONObjectToHash(msgRequestJSON);
            MDMAgentUpdateHandler.getInstance().updateAgentUpgradeStatus(upgradeMap);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " Exception in registerMDMAgentUpgrade ", e);
        }
    }
    
    protected void handleAppRegistration(final Long resourceID, final int platformType) throws Exception {
        this.addorUpdateIOSAgentInstallationStatus(resourceID, 1);
        final Criteria criteria = new Criteria(Column.getColumn("IOSNativeAppAuthentication", "MANAGED_DEVICE_ID"), (Object)resourceID, 0);
        DataAccess.delete(criteria);
        final List resourceList = new ArrayList();
        resourceList.add(resourceID);
        if (platformType == 1) {
            final boolean isMacDevice = MDMUtil.getInstance().isMacDevice(resourceID);
            if (!isMacDevice) {
                DeviceCommandRepository.getInstance().addDefaultAppCatalogCommand(resourceID, "DefaultRemoveAppCatalogWebClips");
                this.addiOSMEMDMAppCommands(resourceList);
                NotificationHandler.getInstance().SendNotification(resourceList, platformType);
            }
            else {
                this.addMacOSMEMDMAppCommands(resourceList);
            }
        }
    }
    
    void addiOSMEMDMAppCommands(final List resourceList) {
        DeviceCommandRepository.getInstance().addSyncAgentSettingsCommand(resourceList, 2);
        DeviceCommandRepository.getInstance().addLocationConfigurationCommand(resourceList, 2);
        DeviceCommandRepository.getInstance().addTermsSyncCommand(resourceList, 2);
        DeviceCommandRepository.getInstance().addBatteryConfigurationCommand(resourceList, 2);
        DeviceCommandRepository.getInstance().addLanguageLicenseCommand(resourceList, 2);
        DeviceCommandRepository.getInstance().addSyncPrivacySettingsCommand(resourceList, 2);
        this.logger.log(Level.INFO, "DeviceRegistrationServlet: Added set of commands to send to iOS Agent , resourceID{0}", resourceList);
    }
    
    void addMacOSMEMDMAppCommands(final List resourceList) {
        DeviceCommandRepository.getInstance().addLocationConfigurationCommand(resourceList, 2);
        this.logger.log(Level.INFO, "DeviceRegistrationServlet: Added set of commands to send to macOS Agent , resourceID{0}", resourceList);
    }
    
    protected void processPostAppRegistration(final JSONObject requestJSON) throws JSONException {
    }
    
    private void fetchValidUserDetails(final JSONObject responseJSON, final JSONObject msgRequestJSON, final JSONObject msgResponseJSON) throws JSONException, SyMException, DataAccessException {
        final String userName = msgRequestJSON.optString("UserName");
        final String domainName = msgRequestJSON.optString("DomainName");
        final String email = msgRequestJSON.optString("EmailAddress");
        JSONObject json = new JSONObject();
        json.put("SKIP_AD_OBJ_VAL", true);
        json.put("DOMAIN_NETBIOS_NAME", (Object)domainName);
        json.put("NAME", (Object)userName);
        json.put("customerID", (Object)CustomerInfoUtil.getInstance().getDefaultCustomer());
        json.put("EMAIL_ADDRESS", (Object)email);
        json.put("PASSWORD", (Object)msgRequestJSON.optString("ADPassword"));
        json = MDMEnrollmentRequestHandler.getInstance().validateUserName(json);
        if (json.has("USER_NAME") && !MDMUtil.getInstance().isEmpty(String.valueOf(json.get("USER_NAME")))) {
            msgRequestJSON.put("UserName", (Object)String.valueOf(json.get("USER_NAME")));
        }
        if (json.has("EMAIL_ADDRESS") && !MDMUtil.getInstance().isEmpty(String.valueOf(json.get("EMAIL_ADDRESS")))) {
            msgRequestJSON.put("EmailAddress", (Object)String.valueOf(json.get("EMAIL_ADDRESS")));
        }
    }
    
    private String getOwnedByString(final Integer ownedBy) {
        switch (ownedBy) {
            case 1: {
                return "corporate";
            }
            case 2: {
                return "personal";
            }
            default: {
                return null;
            }
        }
    }
    
    public void allowEnrollment(final long enrollmentRequestID, final int platformType, final JSONObject paramsJSON) throws SyMException {
        try {
            EnrollRestrictionHandler handler = null;
            if (MDMRegistrationHandler.restrictionClassMap.containsKey(platformType)) {
                handler = (EnrollRestrictionHandler)Class.forName(MDMRegistrationHandler.restrictionClassMap.get(platformType)).newInstance();
            }
            else {
                handler = new EnrollRestrictionHandler();
            }
            final Long customerId = MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(enrollmentRequestID);
            handler.allowEnrollment(customerId, enrollmentRequestID == -1L, paramsJSON);
        }
        catch (final SyMException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception occured in allowEnrolment {0}", ex2);
        }
    }
    
    private Integer getOwnedByConstant(final String ownedBy) {
        if (ownedBy.equalsIgnoreCase("corporate")) {
            return 1;
        }
        if (ownedBy.equalsIgnoreCase("personal")) {
            return 2;
        }
        return null;
    }
    
    private Integer getAuthModeConstant(final String authMode) {
        if (authMode.equalsIgnoreCase("OTP")) {
            return 1;
        }
        if (authMode.equalsIgnoreCase("ActiveDirectory") || authMode.equalsIgnoreCase("AzureADToken")) {
            return 2;
        }
        if (authMode.equalsIgnoreCase("Combined")) {
            return 3;
        }
        return null;
    }
    
    public DataObject getEnrollmentRequestDataObject(final JSONObject requestJSON, final JSONObject msgRequestJSON) {
        DataObject dataObject = (DataObject)new WritableDataObject();
        try {
            final Long enrollmentRequestID = msgRequestJSON.optLong("EnrollmentRequestID", -1L);
            final String emailAddress = msgRequestJSON.optString("EmailAddress");
            final Integer platform = this.getPlatformConstant(String.valueOf(requestJSON.get("DevicePlatform")));
            final Integer authMode = this.getAuthModeConstant(msgRequestJSON.optString("AuthMode"));
            final String otpPasscode = msgRequestJSON.optString("OTPPassword");
            Integer enrollType = 1;
            if (authMode != null && enrollmentRequestID != -1L) {
                enrollType = (Integer)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID, "ENROLLMENT_TYPE");
            }
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            final Join userJoin = new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
            sQuery.addJoin(userJoin);
            final Join enrolljoin = new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
            sQuery.addJoin(enrolljoin);
            sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            sQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            if (otpPasscode != null && !otpPasscode.isEmpty()) {
                final Join optjoin = new Join("DeviceEnrollmentRequest", "OTPPassword", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
                sQuery.addJoin(optjoin);
            }
            final Criteria statusCriteria = new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)new Integer[] { 1, 0 }, 8).and(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)4, 1).or(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)null, 0)));
            final Criteria platformCriteria = new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)platform, 0).or(new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)0, 0));
            Criteria criteria = statusCriteria.and(platformCriteria);
            if (authMode != null) {
                criteria = criteria.and(new Criteria(new Column("DeviceEnrollmentRequest", "AUTH_MODE"), (Object)authMode, 0));
            }
            Criteria enrollTypeCriteria = new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)1, 0);
            if (enrollmentRequestID != -1L) {
                enrollTypeCriteria = new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)enrollType, 0);
            }
            Label_0775: {
                Label_0629: {
                    if (platform == 1) {
                        CustomerInfoUtil.getInstance();
                        if (!CustomerInfoUtil.isSAS()) {
                            break Label_0629;
                        }
                    }
                    if ((emailAddress != null && !emailAddress.isEmpty()) || enrollmentRequestID == -1L) {
                        if (emailAddress == null || emailAddress.isEmpty()) {
                            return (DataObject)new WritableDataObject();
                        }
                        criteria = criteria.and(enrollTypeCriteria).and(new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)emailAddress, 0, (boolean)Boolean.FALSE));
                        if (enrollmentRequestID == -1L || platform != 2) {
                            break Label_0775;
                        }
                        CustomerInfoUtil.getInstance();
                        if (CustomerInfoUtil.isSAS()) {
                            criteria = criteria.and(new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0));
                        }
                        break Label_0775;
                    }
                }
                criteria = criteria.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0));
            }
            sQuery.setCriteria(criteria);
            sQuery.addSelectColumn(Column.getColumn("Resource", "*"));
            sQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "*"));
            if (otpPasscode != null && !otpPasscode.isEmpty()) {
                sQuery.addSelectColumn(Column.getColumn("OTPPassword", "ENROLLMENT_REQUEST_ID", "OTPPASSWORD.ENROLLMENT_REQUEST_ID"));
                sQuery.addSelectColumn(Column.getColumn("OTPPassword", "EXPIRE_TIME"));
                sQuery.addSelectColumn(Column.getColumn("OTPPassword", "OTP_PASSWORD"));
                sQuery.addSelectColumn(Column.getColumn("OTPPassword", "FAILED_ATTEMPTS"));
            }
            dataObject = MDMUtil.getPersistence().get(sQuery);
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(MDMEnrollmentRequestHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final JSONException ex2) {
            Logger.getLogger(MDMRegistrationHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
        }
        catch (final Exception ex3) {
            Logger.getLogger(MDMRegistrationHandler.class.getName()).log(Level.SEVERE, null, ex3);
        }
        return dataObject;
    }
    
    protected JSONObject processDeRegistrationStatusUpdateMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final String registrationType = msgRequestJSON.optString("DeRegistrationType", "MDMRegistration");
        return this.deRegisterMDMApp(requestJSON);
    }
    
    protected JSONObject processDeviceProvisioningMessage(final JSONObject requestJSON) throws Exception {
        throw new SyMException(15001, "Message Not Implemented For Provided Platform", (Throwable)null);
    }
    
    private JSONObject deRegisterMDMApp(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        try {
            responseJSON.put("MsgResponseType", (Object)"DeResgistrationStatusUpdateResponse");
            String status = requestJSON.optString("Status", (String)null);
            final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
            final String deviceUDID = String.valueOf(msgRequestJSON.get("UDID"));
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
            if (status == null) {
                status = String.valueOf(msgRequestJSON.get("Status"));
            }
            if (status.equalsIgnoreCase("Acknowledged")) {
                final String platformValue = requestJSON.optString("DevicePlatform");
                final int platformtype = this.getPlatformConstant(platformValue);
                this.handleAppDeRegistration(resourceID, platformtype);
                if (ManagedDeviceHandler.getInstance().isDeviceRemoved(resourceID)) {
                    DeviceCommandRepository.getInstance().clearCommandFromDevice(deviceUDID, resourceID, "RemoveDevice", 2);
                    if (!DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(deviceUDID)) {
                        ManagedDeviceHandler.getInstance().removeDeviceInTrash(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID));
                    }
                }
            }
            responseJSON.put("Status", (Object)"Acknowledged");
        }
        catch (final Exception exp) {
            responseJSON.put("Status", (Object)"Error");
        }
        return responseJSON;
    }
    
    protected void handleAppDeRegistration(final Long resourceID, final int platformType) throws Exception {
        this.addorUpdateIOSAgentInstallationStatus(resourceID, 0);
    }
    
    private boolean isOtherPlatformRequest(final JSONObject requestJSON, final JSONObject responseJSON) throws Exception {
        final int platformType = this.getPlatformConstant(String.valueOf(requestJSON.get("DevicePlatform")));
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final Long erid = msgRequestJSON.optLong("EnrollmentRequestID", -1L);
        final Row r = DBUtil.getRowFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)erid);
        if (r != null) {
            final int requestStatus = (int)r.get("REQUEST_STATUS");
            if (requestStatus == 1 || requestStatus == 0) {
                final Integer platformTypeFromDB = (Integer)r.get("PLATFORM_TYPE");
                if (platformType != platformTypeFromDB && platformTypeFromDB != 0) {
                    responseJSON.put("Status", (Object)"Error");
                    final JSONObject messageResponseJSON = new JSONObject();
                    messageResponseJSON.put("ErrorMsg", (Object)"Enrollment Invitation of other platform");
                    messageResponseJSON.put("ErrorKey", (Object)"dc.mdm.actionlog.enrollment.invalid_enrollmentId");
                    switch (platformTypeFromDB) {
                        case 1: {
                            messageResponseJSON.put("ErrorKey", (Object)"dc.mdm.actionlog.enrollment.ios_erid");
                            break;
                        }
                        case 2: {
                            messageResponseJSON.put("ErrorKey", (Object)"dc.mdm.actionlog.enrollment.android_erid");
                            break;
                        }
                        case 3: {
                            messageResponseJSON.put("ErrorKey", (Object)"dc.mdm.actionlog.enrollment.windows_erid");
                            break;
                        }
                    }
                    messageResponseJSON.put("ErrorCode", 12001);
                    responseJSON.put("MsgResponse", (Object)messageResponseJSON);
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        MDMRegistrationHandler.handler = null;
        MDMRegistrationHandler.restrictionClassMap = null;
    }
}
