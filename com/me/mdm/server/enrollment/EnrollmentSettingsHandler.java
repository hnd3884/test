package com.me.mdm.server.enrollment;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.enrollment.approval.EnrollmentApprovalHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import org.json.JSONException;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public abstract class EnrollmentSettingsHandler
{
    protected Logger logger;
    private static EnrollmentSettingsHandler enrollmentSettingsHandler;
    public static final String OLD_AUTH_MODE = "OLD_AUTH_MODE";
    public static final String NEW_AUTH_MODE = "NEW_AUTH_MODE";
    
    protected EnrollmentSettingsHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static EnrollmentSettingsHandler getInstance() {
        if (EnrollmentSettingsHandler.enrollmentSettingsHandler == null) {
            try {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    EnrollmentSettingsHandler.enrollmentSettingsHandler = (EnrollmentSettingsHandler)Class.forName("com.me.mdmcloud.server.enroll.EnrollmentSettingsHandlerCloudImpl").newInstance();
                }
                else {
                    EnrollmentSettingsHandler.enrollmentSettingsHandler = (EnrollmentSettingsHandler)Class.forName("com.me.mdm.onpremise.server.enrollment.EnrollmentSettingsHandlerOnPremiseImpl").newInstance();
                }
            }
            catch (final Exception ex) {
                Logger.getLogger(EnrollmentSettingsHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return EnrollmentSettingsHandler.enrollmentSettingsHandler;
    }
    
    public void handlePostAuthModeChange(final JSONObject settingsEvent) throws Exception {
        try {
            final Long customerID = settingsEvent.getLong("CUSTOMER_ID");
            final Integer newAuthMode = settingsEvent.getInt("NEW_AUTH_MODE");
            this.resendEnrollmentInvitation(customerID, newAuthMode, settingsEvent.getLong("USER_ID"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    protected void updateAuthModeForEnrollmentInvitations(final Long customerID, final Integer authMode, final Long userID) throws Exception {
        final SelectQuery selectQuery = this.getPendingInvitationRequestsQuery(customerID, authMode);
        final DataObject dobj = MDMUtil.getPersistence().get(selectQuery);
        dobj.set("DeviceEnrollmentRequest", "AUTH_MODE", (Object)authMode);
        dobj.set("DeviceEnrollmentRequest", "USER_ID", (Object)userID);
        MDMUtil.getPersistence().update(dobj);
    }
    
    protected void resendEnrollmentInvitation(final Long customerID, final Integer authMode, final Long userID) throws Exception {
        final SelectQuery selectQuery = this.getPendingInvitationRequestsQuery(customerID, authMode);
        final DataObject dobj = MDMUtil.getPersistence().get(selectQuery);
        final Iterator<Row> iterator = dobj.getRows("DeviceEnrollmentRequest");
        while (iterator.hasNext()) {
            final Properties properties = new Properties();
            final Row row = iterator.next();
            ((Hashtable<String, Object>)properties).put("ENROLLMENT_REQUEST_ID", row.get("ENROLLMENT_REQUEST_ID"));
            ((Hashtable<String, Long>)properties).put("USER_ID", userID);
            ((Hashtable<String, Integer>)properties).put("AUTH_MODE", authMode);
            ((Hashtable<String, Boolean>)properties).put("regenerateDeviceToken", true);
            MDMEnrollmentRequestHandler.getInstance().resendEnrollmentRequest(properties);
        }
    }
    
    private SelectQuery getPendingInvitationRequestsQuery(final Long customerID, final Integer authMode) {
        final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        squery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        squery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        squery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
        squery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "AUTH_MODE"));
        squery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "USER_ID"));
        final Criteria invitation = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)1, 0);
        final Criteria pendingRequest = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)new Integer[] { 1, 0 }, 8);
        final Criteria customer = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria deviceAuthModeDoesNotMatchNewAuthMode = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "AUTH_MODE"), (Object)authMode, 1);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        Criteria c = invitation.and(pendingRequest).and(customer).and(deviceAuthModeDoesNotMatchNewAuthMode).and(userNotInTrashCriteria);
        if (authMode == 2 || authMode == 3) {
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS()) {
                final Criteria domainUsers = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 1, false);
                c = c.and(domainUsers);
            }
        }
        squery.setCriteria(c);
        return squery;
    }
    
    public JSONObject processMessage(final JSONObject inputJSON) throws Exception {
        JSONObject responseJSON = new JSONObject();
        JSONObject msgResponseJSON = new JSONObject();
        try {
            final Long customerID = inputJSON.optLong("CUSTOMER_ID", (long)CustomerInfoUtil.getInstance().getCustomerId());
            final String msgRequestType = String.valueOf(inputJSON.get("MsgRequestType"));
            final JSONObject msgRequest = inputJSON.optJSONObject("MsgRequest");
            if (msgRequest != null) {
                msgRequest.put("CUSTOMER_ID", (Object)customerID);
            }
            if (msgRequestType != null && !msgRequestType.isEmpty()) {
                if (msgRequestType.equalsIgnoreCase("SaveEnrollSettings")) {
                    this.saveEnrollSettings(msgRequest);
                }
                else if (msgRequestType.equalsIgnoreCase("getEnrollSettings")) {
                    msgResponseJSON = this.getEnrollSettings(customerID);
                }
                else if (msgRequestType.equalsIgnoreCase("SaveSelfEnrollSettings")) {
                    this.saveSelfEnrollSettings(msgRequest);
                }
                else if (msgRequestType.equalsIgnoreCase("getSelfEnrollSettings")) {
                    msgResponseJSON = this.getSelfEnrollSettings(customerID);
                }
                if (!inputJSON.optBoolean("isApi", false)) {
                    responseJSON.put("Status", (Object)"Acknowledged");
                    if (msgResponseJSON.length() != 0) {
                        responseJSON.put("MsgResponse", (Object)msgResponseJSON);
                    }
                }
                else {
                    responseJSON = msgResponseJSON;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in process message:", e);
            responseJSON.put("Status", (Object)"Error");
            if (!(e instanceof SyMException)) {
                throw e;
            }
            final SyMException ex = (SyMException)e;
            responseJSON.put("ErrorMsg", (Object)ex.getMessage());
            responseJSON.put("ErrorCode", ex.getErrorCode());
            responseJSON.put("ErrorKey", (Object)ex.getErrorKey());
        }
        return responseJSON;
    }
    
    private DataObject getEnrollmentSettingsDO(final String tableName, final Long customerID) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentSettings"));
        query.addSelectColumn(Column.getColumn("EnrollmentSettings", "*"));
        if (tableName != null) {
            query.addJoin(new Join("EnrollmentSettings", tableName, new String[] { "ENROLLMENT_SETTINGS_ID" }, new String[] { "ENROLLMENT_SETTINGS_ID" }, 1));
            query.addSelectColumn(Column.getColumn(tableName, "*"));
        }
        query.setCriteria(new Criteria(Column.getColumn("EnrollmentSettings", "CUSTOMER_ID"), (Object)customerID, 0));
        return MDMUtil.getPersistence().get(query);
    }
    
    public JSONObject getSelfEnrollmentSettings(final Long customerID) {
        try {
            final DataObject dobj = this.getEnrollmentSettingsDO("SelfEnrollmentSettings", customerID);
            final JSONObject json = new JSONObject();
            final Iterator iterator = dobj.getRows("SelfEnrollmentSettings");
            if (iterator.hasNext()) {
                json.put("ENABLE_SELF_ENROLLMENT", dobj.getValue("SelfEnrollmentSettings", "ENABLE_SELF_ENROLLMENT", (Criteria)null));
                json.put("NOTIFY_SELF_ENROLLMENT", dobj.getValue("SelfEnrollmentSettings", "NOTIFY_SELF_ENROLLMENT", (Criteria)null));
                json.put("OWNED_BY_OPTION", dobj.getValue("SelfEnrollmentSettings", "OWNED_BY_OPTION", (Criteria)null));
                json.put("APPROVAL_MODE", dobj.getValue("SelfEnrollmentSettings", "APPROVAL_MODE", (Criteria)null));
                json.put("UPDATED_BY", dobj.getValue("SelfEnrollmentSettings", "UPDATED_BY", (Criteria)null));
                return json;
            }
            json.put("ENABLE_SELF_ENROLLMENT", this.isSelfEnrollmentEnabledByDefault());
            json.put("NOTIFY_SELF_ENROLLMENT", false);
            json.put("OWNED_BY_OPTION", 0);
            json.put("APPROVAL_MODE", 0);
            return json;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public JSONObject getInvitationEnrollmentSettings(final Long customerID) {
        try {
            final DataObject dobj = this.getEnrollmentSettingsDO("InvitationEnrollmentSettings", customerID);
            final JSONObject json = new JSONObject();
            final Iterator<Row> iterator = dobj.getRows("InvitationEnrollmentSettings");
            if (iterator.hasNext()) {
                json.put("AUTH_MODE", dobj.getValue("InvitationEnrollmentSettings", "AUTH_MODE", (Criteria)null));
                return json;
            }
            json.put("AUTH_MODE", 1);
            return json;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public JSONObject getCommonEnrollmentSettings(final Long customerID) {
        try {
            final DataObject dobj = this.getEnrollmentSettingsDO(null, customerID);
            final JSONObject json = new JSONObject();
            final Iterator<Row> iterator = dobj.getRows("EnrollmentSettings");
            if (iterator.hasNext()) {
                json.put("NOTIFY_DEVICE_UNMANAGED", dobj.getValue("EnrollmentSettings", "NOTIFY_DEVICE_UNMANAGED", (Criteria)null));
                return json;
            }
            json.put("NOTIFY_DEVICE_UNMANAGED", (Object)Boolean.FALSE);
            return json;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public Long addOrUpdateCommonEnrollmentSettings(final JSONObject json) throws Exception {
        try {
            DataObject dobj = DBUtil.getDataObjectFromDB("EnrollmentSettings", "CUSTOMER_ID", (Object)json.getLong("CUSTOMER_ID"));
            if (dobj == null || dobj.isEmpty()) {
                dobj = (DataObject)new WritableDataObject();
                final Row r = new Row("EnrollmentSettings");
                r.set("CUSTOMER_ID", (Object)json.getLong("CUSTOMER_ID"));
                r.set("NOTIFY_DEVICE_UNMANAGED", (Object)json.getBoolean("NOTIFY_DEVICE_UNMANAGED"));
                dobj.addRow(r);
            }
            else {
                final Row r = dobj.getRow("EnrollmentSettings");
                r.set("NOTIFY_DEVICE_UNMANAGED", (Object)json.getBoolean("NOTIFY_DEVICE_UNMANAGED"));
                dobj.updateRow(r);
            }
            MDMUtil.getPersistence().update(dobj);
            return (Long)dobj.getRow("EnrollmentSettings").get("ENROLLMENT_SETTINGS_ID");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public Long addBaseEnrollmentSettings(final Long customerID) {
        try {
            DataObject dobj = DBUtil.getDataObjectFromDB("EnrollmentSettings", "CUSTOMER_ID", (Object)customerID);
            if (dobj == null || dobj.isEmpty()) {
                dobj = (DataObject)new WritableDataObject();
                final Row r = new Row("EnrollmentSettings");
                r.set("CUSTOMER_ID", (Object)customerID);
                dobj.addRow(r);
                MDMUtil.getPersistence().update(dobj);
            }
            return (Long)dobj.getRow("EnrollmentSettings").get("ENROLLMENT_SETTINGS_ID");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public long addOrUpdateInvitationEnrollmentSettings(final JSONObject json) throws Exception {
        final Long settingsID = this.addBaseEnrollmentSettings(json.getLong("CUSTOMER_ID"));
        DataObject dobj = this.getEnrollmentSettingsDO("InvitationEnrollmentSettings", json.getLong("CUSTOMER_ID"));
        final Iterator<Row> iterator = dobj.getRows("InvitationEnrollmentSettings");
        final int newAuthMode = json.getInt("AUTH_MODE");
        final JSONObject newJson = new JSONObject();
        newJson.put("NEW_AUTH_MODE", newAuthMode);
        newJson.put("CUSTOMER_ID", json.getLong("CUSTOMER_ID"));
        newJson.put("USER_ID", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
        getInstance().handlePreAuthModeChange(newJson);
        int oldAuthMode;
        if (!iterator.hasNext()) {
            dobj = (DataObject)new WritableDataObject();
            final Row r = new Row("InvitationEnrollmentSettings");
            r.set("ENROLLMENT_SETTINGS_ID", (Object)settingsID);
            oldAuthMode = 1;
            r.set("AUTH_MODE", (Object)json.getInt("AUTH_MODE"));
            dobj.addRow(r);
        }
        else {
            final Row r = dobj.getRow("InvitationEnrollmentSettings");
            oldAuthMode = (int)r.get("AUTH_MODE");
            r.set("AUTH_MODE", (Object)json.getInt("AUTH_MODE"));
            dobj.updateRow(r);
        }
        MDMUtil.getPersistence().update(dobj);
        if (oldAuthMode != newAuthMode) {
            newJson.put("OLD_AUTH_MODE", oldAuthMode);
            final String eventMsg = "mdm.log.enroll.invite_auth_setting";
            final String remarkArgs = this.getAuthModeString(oldAuthMode) + "@@@" + this.getAuthModeString(newAuthMode);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2064, null, MDMUtil.getInstance().getCurrentlyLoggedOnUserName(), eventMsg, remarkArgs, json.getLong("CUSTOMER_ID"));
            getInstance().handlePostAuthModeChange(newJson);
        }
        return settingsID;
    }
    
    private String getAuthModeString(final int authMode) throws Exception {
        try {
            switch (authMode) {
                case 1: {
                    return I18N.getMsg("mdm.log.enroll.invite_auth_otp", new Object[0]);
                }
                case 2: {
                    return I18N.getMsg("mdm.log.enroll.invite_auth_ad", new Object[0]);
                }
                case 3: {
                    return I18N.getMsg("dc.mdm.enroll.twofactor_title", new Object[0]);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error while getting aith mode text", e);
            throw e;
        }
        return null;
    }
    
    public void handlePreAuthModeChange(final JSONObject settingsEvent) throws JSONException, Exception {
    }
    
    public long addOrUpdateSelfEnrollmentSettings(final JSONObject json) throws Exception {
        final Long settingsID = this.addBaseEnrollmentSettings(json.getLong("CUSTOMER_ID"));
        DataObject dobj = this.getEnrollmentSettingsDO("SelfEnrollmentSettings", json.getLong("CUSTOMER_ID"));
        final Iterator<Row> iterator = dobj.getRows("SelfEnrollmentSettings");
        if (!iterator.hasNext()) {
            dobj = (DataObject)new WritableDataObject();
            final Row r = new Row("SelfEnrollmentSettings");
            r.set("ENROLLMENT_SETTINGS_ID", (Object)settingsID);
            r.set("ENABLE_SELF_ENROLLMENT", (Object)json.getBoolean("ENABLE_SELF_ENROLLMENT"));
            r.set("NOTIFY_SELF_ENROLLMENT", (Object)json.getBoolean("NOTIFY_SELF_ENROLLMENT"));
            r.set("OWNED_BY_OPTION", (Object)json.getInt("OWNED_BY_OPTION"));
            r.set("APPROVAL_MODE", (Object)json.getInt("APPROVAL_MODE"));
            r.set("UPDATED_BY", (Object)json.getLong("UPDATED_BY"));
            dobj.addRow(r);
        }
        else {
            final Row r = dobj.getRow("SelfEnrollmentSettings");
            r.set("ENABLE_SELF_ENROLLMENT", (Object)json.getBoolean("ENABLE_SELF_ENROLLMENT"));
            r.set("NOTIFY_SELF_ENROLLMENT", (Object)json.getBoolean("NOTIFY_SELF_ENROLLMENT"));
            r.set("OWNED_BY_OPTION", (Object)json.getInt("OWNED_BY_OPTION"));
            r.set("APPROVAL_MODE", (Object)json.getInt("APPROVAL_MODE"));
            r.set("UPDATED_BY", (Object)json.getLong("UPDATED_BY"));
            dobj.updateRow(r);
        }
        MDMUtil.getPersistence().update(dobj);
        MDMMessageHandler.getInstance().messageAction("AUTHENTICATE_DEVICE_ENROLLMENT", json.getLong("CUSTOMER_ID"));
        return settingsID;
    }
    
    public void selfEnrollmentActionLog(final JSONObject json) throws Exception {
        final boolean enableSelfEnroll = json.getBoolean("ENABLE_SELF_ENROLLMENT");
        final String eventMsg = enableSelfEnroll ? "dc.mdm.actionlog.enrollment.self_enroll_enable" : "dc.mdm.actionlog.enrollment.self_enroll_disable";
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2064, null, MDMUtil.getInstance().getCurrentlyLoggedOnUserName(), eventMsg, "", json.getLong("CUSTOMER_ID"));
    }
    
    private void saveEnrollSettings(final JSONObject requestJSON) throws JSONException, Exception {
        final Long customerID = requestJSON.optLong("CUSTOMER_ID", (long)CustomerInfoUtil.getInstance().getCustomerId());
        requestJSON.put("CUSTOMER_ID", (Object)customerID);
        this.addOrUpdateCommonEnrollmentSettings(requestJSON);
        this.addOrUpdateDirectoryAuthenticationSettings(requestJSON);
        this.addOrUpdateInvitationEnrollmentSettings(requestJSON);
        final Boolean notifyOnDeviceUnmanaged = requestJSON.optBoolean("NOTIFY_DEVICE_UNMANAGED", (boolean)Boolean.FALSE);
        if (notifyOnDeviceUnmanaged) {
            new AlertMailGeneratorUtil().setCustomerEMailAddress((long)customerID, String.valueOf(requestJSON.get("EMAIL_UNMANAGED_ALERTS")), "MDM-DEVICE-UNMANAGED");
        }
    }
    
    private JSONObject getEnrollSettings(final Long customerID) throws Exception {
        JSONObject responseJSON = new JSONObject();
        responseJSON = this.getInvitationEnrollmentSettings(customerID);
        JSONUtil.getInstance();
        JSONUtil.putAll(responseJSON, this.getCommonEnrollmentSettings(customerID));
        final String email = new AlertMailGeneratorUtil().getCustomerEMailAddress(customerID, "MDM-DEVICE-UNMANAGED");
        responseJSON.put("EMAIL_UNMANAGED_ALERTS", (Object)((email == null) ? "" : email));
        responseJSON.put("IS_AUTHENTICATION_HANDLING_AVAILABLE", (Object)this.isAuthenticationHandlingAvailable(customerID));
        return responseJSON;
    }
    
    private void saveSelfEnrollSettings(final JSONObject requestJSON) throws JSONException, Exception {
        final Long customerID = requestJSON.optLong("CUSTOMER_ID", (long)CustomerInfoUtil.getInstance().getCustomerId());
        requestJSON.put("CUSTOMER_ID", (Object)customerID);
        this.addOrUpdateSelfEnrollmentSettings(requestJSON);
        this.selfEnrollmentActionLog(requestJSON);
        if (requestJSON.getInt("APPROVAL_MODE") != 0 && requestJSON.has("ApprovalCriteria")) {
            EnrollmentApprovalHandler.getInstance().addOrUpdateApprovalCriteria(requestJSON.getJSONObject("ApprovalCriteria"));
        }
        else {
            EnrollmentApprovalHandler.getInstance().clearApprovalCriteria();
        }
        this.addOrUpdateDirectoryAuthenticationSettings(requestJSON);
        final Boolean notifySelfEnrollment = requestJSON.optBoolean("NOTIFY_SELF_ENROLLMENT", (boolean)Boolean.FALSE);
        if (notifySelfEnrollment) {
            new AlertMailGeneratorUtil().setCustomerEMailAddress((long)customerID, String.valueOf(requestJSON.get("EMAIL_SELF_ENROLLMENT_ALERTS")), "MDM-SELF-ENROLLMENT");
        }
        this.configureSelfEnrollmentGroups(requestJSON);
    }
    
    private JSONObject getSelfEnrollSettings(final Long customerId) {
        JSONObject responseJSON = new JSONObject();
        try {
            responseJSON = this.getSelfEnrollmentSettings(customerId);
            final String email = new AlertMailGeneratorUtil().getCustomerEMailAddress(customerId, "MDM-SELF-ENROLLMENT");
            responseJSON.put("EMAIL_SELF_ENROLLMENT_ALERTS", (Object)((email == null) ? "" : email));
            final JSONObject approvalCriteria = EnrollmentApprovalHandler.getInstance().getCriteria(1);
            if (approvalCriteria != null) {
                responseJSON.put("ApprovalCriteria", (Object)approvalCriteria);
            }
            responseJSON.put("IS_APPROVER_HANDLING_AVAILABLE", this.isApproverHandlingAvailable());
            final JSONObject configuredGroups = MDMGroupHandler.getInstance().getSelfEnrollmentConfiguredGroups(customerId);
            responseJSON.put("SELF_ENROLLMENT_GROUPS", (Object)configuredGroups);
            final Boolean showPlatform = this.selfEnrollmentGrouphasPlatformSpecificSettings(configuredGroups);
            responseJSON.put("SHOW_PLATFORM", (Object)showPlatform);
            final Boolean showOwnedBy = this.selfEnrollmentGrouphasOwnedBySpecificSettings(configuredGroups);
            responseJSON.put("SHOW_OWNED_BY", (Object)showOwnedBy);
            responseJSON.put("IS_AUTHENTICATION_HANDLING_AVAILABLE", (Object)this.isAuthenticationHandlingAvailable(customerId));
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollmentSettingsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return responseJSON;
    }
    
    private Boolean selfEnrollmentGrouphasPlatformSpecificSettings(final JSONObject configuredGroups) {
        final int[] array;
        final int[] platformGroupType = array = new int[] { 1, 2, 3, 4, 5, 6 };
        for (final int platform : array) {
            final String groupId = configuredGroups.optString(platform + "", "--");
            if (!groupId.equals("--")) {
                return true;
            }
        }
        return false;
    }
    
    public Boolean selfEnrollmentGrouphasOwnedBySpecificSettings(final JSONObject configuredGroups) {
        final int[] array;
        final int[] ownedByType = array = new int[] { 3, 3, 5 };
        for (final int ownedBy : array) {
            final String groupId = configuredGroups.optString(ownedBy + "", "--");
            if (!groupId.equals("--")) {
                return true;
            }
        }
        return this.getShowSelfEnrollmentOwnedByCriteria();
    }
    
    public boolean getShowSelfEnrollmentOwnedByCriteria() {
        boolean showOwnedByCriteria = false;
        try {
            showOwnedByCriteria = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("showOwnedByFilter");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred while getShowSelfEnrollmentOwnedByCriteria", e);
        }
        return showOwnedByCriteria;
    }
    
    public void sendUnmanagedDeviceNotififcationMail(final JSONObject mailAttributes) {
        try {
            final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil(this.logger);
            final Properties prop = new Properties();
            ((Hashtable<String, String>)prop).put("$user_name$", mailAttributes.optString("NAME", "--"));
            ((Hashtable<String, String>)prop).put("$device_name$", mailAttributes.optString("device_name", "--"));
            ((Hashtable<String, String>)prop).put("$platform_type$", mailAttributes.optString("platformType", "--"));
            ((Hashtable<String, String>)prop).put("$device_serial_number$", mailAttributes.optString("SERIAL_NUMBER", "--"));
            ((Hashtable<String, String>)prop).put("$device_model$", mailAttributes.optString("MODEL_NAME", "--"));
            ((Hashtable<String, String>)prop).put("$device_groups$", mailAttributes.optString("groups", "--"));
            ((Hashtable<String, String>)prop).put("$mail_address$", mailAttributes.optString("EMAIL_ADDRESS", "--"));
            ((Hashtable<String, Boolean>)prop).put("appendFooter", true);
            mailGenerator.sendMail(MDMAlertConstants.DEVICE_UNMANAGED_NOTIF_MAIL_TEMPLATE, "MDM-DEVICE-UNMANAGED", Long.valueOf(mailAttributes.optLong("customerID")), prop);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in sendSelfEnrollmentMail", exp);
        }
    }
    
    public boolean isSelfEnrollmentEnabled(final Long customerId) {
        boolean isSelfEnroll = false;
        try {
            final DataObject dObj = this.getEnrollmentSettingsDO("SelfEnrollmentSettings", customerId);
            if (!dObj.isEmpty()) {
                final Row selfEnrollRow = dObj.getRow("SelfEnrollmentSettings");
                if (selfEnrollRow != null) {
                    isSelfEnroll = (boolean)selfEnrollRow.get("ENABLE_SELF_ENROLLMENT");
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in isSelfEnrollmentEnabled", exp);
        }
        return isSelfEnroll;
    }
    
    private void configureSelfEnrollmentGroups(final JSONObject requestData) {
        try {
            MDMGroupHandler.getInstance().configureSelfEnrollmentGroups(requestData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while configureSelfEnrollmentGroups", ex);
        }
    }
    
    protected abstract boolean isApproverHandlingAvailable();
    
    protected abstract boolean isSelfEnrollmentEnabledByDefault();
    
    public abstract int getAuthMode(final Long p0);
    
    public abstract boolean isADAuthenticationApplicable();
    
    protected abstract JSONObject isAuthenticationHandlingAvailable(final Long p0);
    
    protected abstract void addOrUpdateDirectoryAuthenticationSettings(final JSONObject p0) throws JSONException;
    
    public Boolean isDeviceUnmanagedNotifyEnabledInSetup() {
        Boolean isNotifyEnabled = false;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentSettings"));
            selectQuery.addJoin(new Join("EnrollmentSettings", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
            selectQuery.addSelectColumn(new Column("EnrollmentSettings", "ENROLLMENT_SETTINGS_ID"));
            selectQuery.addSelectColumn(new Column("EnrollmentSettings", "CUSTOMER_ID"));
            selectQuery.addSelectColumn(new Column("EnrollmentSettings", "NOTIFY_DEVICE_UNMANAGED"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("EnrollmentSettings", "NOTIFY_DEVICE_UNMANAGED"), (Object)true, 0));
            final DataObject notifyDO = SyMUtil.getPersistence().get(selectQuery);
            final int doSize = notifyDO.size("EnrollmentSettings");
            if (doSize > 0) {
                isNotifyEnabled = true;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in isDeviceUnmanagedNotifyEnabled", ex);
        }
        return isNotifyEnabled;
    }
    
    static {
        EnrollmentSettingsHandler.enrollmentSettingsHandler = null;
    }
}
