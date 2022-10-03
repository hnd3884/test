package com.me.mdm.core.enrollment;

import java.util.Hashtable;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.mdm.server.dep.AdminEnrollmentHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.DataObject;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import org.json.JSONArray;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class OutOfBoxEnrollmentSettingsHandler
{
    Logger logger;
    public static final String TEMPLATE_ID = "template_id";
    private static final String AUTH_MODE = "AuthMode";
    public static final String USER_ID = "user_id";
    public static final String AUTH_URL = "AutherzationURL";
    public static final String USER_ASSIGNMENT_TYPE = "user_assignment_type";
    private static OutOfBoxEnrollmentSettingsHandler enrollmentSettingsHandler;
    
    public OutOfBoxEnrollmentSettingsHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static OutOfBoxEnrollmentSettingsHandler getInstance() {
        if (OutOfBoxEnrollmentSettingsHandler.enrollmentSettingsHandler == null) {
            OutOfBoxEnrollmentSettingsHandler.enrollmentSettingsHandler = new OutOfBoxEnrollmentSettingsHandler();
        }
        return OutOfBoxEnrollmentSettingsHandler.enrollmentSettingsHandler;
    }
    
    public void addOrUpdateSettingsTemplate(final JSONObject settingsJSON) throws APIHTTPException {
        try {
            final Long userId = settingsJSON.getLong("user_id");
            final Long templateId = settingsJSON.getLong("template_id");
            if (!DMUserHandler.isUserInRole(DMUserHandler.getLoginIdForUserId(userId), "MDM_Enrollment_Write")) {
                throw new APIHTTPException("EN001", new Object[0]);
            }
            final int enableAdAuth = settingsJSON.optInt("user_assignment_type", 0);
            final int authMode = (enableAdAuth == 0) ? enableAdAuth : 4;
            JSONArray groupIdJsonArray = settingsJSON.optJSONArray("group_ids");
            if (groupIdJsonArray == null) {
                groupIdJsonArray = new JSONArray();
            }
            final Long currentTime = System.currentTimeMillis();
            this.logger.log(Level.INFO, "The template ID {0} the auth mode is {1} the user id is {2}", new Object[] { templateId, authMode, userId });
            final Row settingsRow = new Row("OutofBoxEnrollmentSettings");
            settingsRow.set("TEMPLATE_ID", (Object)templateId);
            settingsRow.set("AUTH_MODE", (Object)authMode);
            settingsRow.set("LAST_MODIFIED_BY", (Object)userId);
            settingsRow.set("LAST_MODIFIED_TIME", (Object)currentTime);
            try {
                final DataObject settingObject = MDMUtil.getPersistence().get("OutofBoxEnrollmentSettings", (Criteria)null);
                if (!this.shouldRowBeUpdated(templateId)) {
                    settingObject.addRow(settingsRow);
                }
                else {
                    settingObject.updateRow(settingsRow);
                }
                MDMUtil.getPersistence().update(settingObject);
                this.addOrUpdateSettingsTogroup(templateId, groupIdJsonArray);
            }
            catch (final DataAccessException exp) {
                this.logger.log(Level.SEVERE, "Cannot insert row in the database ", (Throwable)exp);
            }
        }
        catch (final JSONException exp2) {
            this.logger.log(Level.SEVERE, "Cannot fetch details from JSON ", (Throwable)exp2);
        }
    }
    
    public void addOrUpdateSettingsTogroup(final Long templateId, final JSONArray groupIdJsonArray) throws DataAccessException, JSONException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("OutofBoxEnrollmentSettingsToGroup");
        deleteQuery.setCriteria(new Criteria(Column.getColumn("OutofBoxEnrollmentSettingsToGroup", "TEMPLATE_ID"), (Object)templateId, 0));
        MDMUtil.getPersistenceLite().delete(deleteQuery);
        final DataObject dataObject = (DataObject)new WritableDataObject();
        for (int i = 0; i < groupIdJsonArray.length(); ++i) {
            final Row row = new Row("OutofBoxEnrollmentSettingsToGroup");
            row.set("TEMPLATE_ID", (Object)templateId);
            row.set("RESOURCE_ID", (Object)Long.parseLong(String.valueOf(groupIdJsonArray.get(i))));
            dataObject.addRow(row);
        }
        MDMUtil.getPersistenceLite().add(dataObject);
    }
    
    private boolean shouldRowBeUpdated(final Long templateId) {
        boolean result = false;
        final Table settingsTable = new Table("OutofBoxEnrollmentSettings");
        final Column templateColumn = new Column("OutofBoxEnrollmentSettings", "TEMPLATE_ID");
        final SelectQuery existingSettings = (SelectQuery)new SelectQueryImpl(settingsTable);
        final Criteria existingSettingsCriteria = new Criteria(new Column("OutofBoxEnrollmentSettings", "TEMPLATE_ID"), (Object)templateId, 0);
        existingSettings.addSelectColumn(templateColumn);
        existingSettings.setCriteria(existingSettingsCriteria);
        try {
            final DataObject settings = MDMUtil.getPersistence().get(existingSettings);
            if (settings == null || settings.isEmpty()) {
                result = false;
            }
            else {
                final Object columnData = settings.getFirstRow("OutofBoxEnrollmentSettings").get("TEMPLATE_ID");
                result = (columnData != null);
            }
        }
        catch (final DataAccessException exp) {
            this.logger.log(Level.SEVERE, "Cannot determine whether to update or add new row ", (Throwable)exp);
        }
        return result;
    }
    
    public int getAuthMode(final Long templateId) {
        int authMode = 0;
        final Table settingsTable = new Table("OutofBoxEnrollmentSettings");
        final SelectQuery findAuthMode = (SelectQuery)new SelectQueryImpl(settingsTable);
        findAuthMode.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "AUTH_MODE"));
        findAuthMode.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "TEMPLATE_ID"));
        findAuthMode.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "LAST_MODIFIED_TIME"));
        findAuthMode.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "LAST_MODIFIED_BY"));
        final Criteria authModeCriteria = new Criteria(new Column("OutofBoxEnrollmentSettings", "TEMPLATE_ID"), (Object)templateId, 0);
        findAuthMode.setCriteria(authModeCriteria);
        try {
            final DataObject authModeObject = MDMUtil.getPersistence().get(findAuthMode);
            final Object selectedColumn = authModeObject.getFirstRow("OutofBoxEnrollmentSettings").get("AUTH_MODE");
            if (selectedColumn != null) {
                authMode = (int)selectedColumn;
            }
        }
        catch (final DataAccessException exp) {
            this.logger.log(Level.SEVERE, "Cannot fetch the auth mode from DB ", (Throwable)exp);
        }
        return authMode;
    }
    
    public Long getLastModifiedTime(final Long templateId) {
        long lastModified = -1L;
        final Table settingsTable = new Table("OutofBoxEnrollmentSettings");
        final SelectQuery getModifiedTime = (SelectQuery)new SelectQueryImpl(settingsTable);
        getModifiedTime.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "AUTH_MODE"));
        getModifiedTime.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "TEMPLATE_ID"));
        getModifiedTime.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "LAST_MODIFIED_TIME"));
        getModifiedTime.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "LAST_MODIFIED_BY"));
        final Criteria lastModifiedCriteria = new Criteria(new Column("OutofBoxEnrollmentSettings", "TEMPLATE_ID"), (Object)templateId, 0);
        getModifiedTime.setCriteria(lastModifiedCriteria);
        try {
            final DataObject authModeObject = MDMUtil.getPersistence().get(getModifiedTime);
            final Object selectedColumn = authModeObject.getFirstRow("OutofBoxEnrollmentSettings").get("LAST_MODIFIED_TIME");
            if (selectedColumn != null) {
                lastModified = (long)selectedColumn;
            }
        }
        catch (final DataAccessException exp) {
            this.logger.log(Level.SEVERE, "Cannot fetch the modified time from DB ", (Throwable)exp);
        }
        return lastModified;
    }
    
    public Long getLastModifiedUser(final Long templateId) {
        long lastModified = -1L;
        final Table settingsTable = new Table("OutofBoxEnrollmentSettings");
        final SelectQuery getModifiedUser = (SelectQuery)new SelectQueryImpl(settingsTable);
        getModifiedUser.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "AUTH_MODE"));
        getModifiedUser.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "TEMPLATE_ID"));
        getModifiedUser.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "LAST_MODIFIED_TIME"));
        getModifiedUser.addSelectColumn(new Column("OutofBoxEnrollmentSettings", "LAST_MODIFIED_BY"));
        final Criteria lastModifiedUserCriteria = new Criteria(new Column("OutofBoxEnrollmentSettings", "TEMPLATE_ID"), (Object)templateId, 0);
        getModifiedUser.setCriteria(lastModifiedUserCriteria);
        try {
            final DataObject authModeObject = MDMUtil.getPersistence().get(getModifiedUser);
            final Object selectedColumn = authModeObject.getFirstRow("OutofBoxEnrollmentSettings").get("LAST_MODIFIED_BY");
            if (selectedColumn != null) {
                lastModified = (long)selectedColumn;
            }
        }
        catch (final DataAccessException exp) {
            this.logger.log(Level.SEVERE, "Cannot fetch the last modified user from DB ", (Throwable)exp);
        }
        return lastModified;
    }
    
    public void deleteEnrollmentSetting(final Long templateId) {
        final DeleteQuery dQuery = (DeleteQuery)new DeleteQueryImpl("OutofBoxEnrollmentSettings");
        final Criteria authModeCriteria = new Criteria(Column.getColumn("OutofBoxEnrollmentSettings", "TEMPLATE_ID"), (Object)templateId, 0);
        dQuery.setCriteria(authModeCriteria);
        try {
            MDMUtil.getPersistence().delete(dQuery);
        }
        catch (final DataAccessException exp) {
            this.logger.log(Level.SEVERE, "Cannot delete the row from DB ", (Throwable)exp);
        }
    }
    
    private JSONObject getAuthorizationUrl(final int authMode, final Long deviceForEnrollId, final int platform) {
        final JSONObject authorizationJSON = new JSONObject();
        try {
            if (authMode == 4) {
                final JSONObject inputJSON = new JSONObject();
                inputJSON.put("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollId);
                inputJSON.put("PlatformType", platform);
                authorizationJSON.put("AutherzationURL", (Object)MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getAdminEnrollAuthenticationUrl(inputJSON));
                authorizationJSON.put("AuthMode", authMode);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Cannot put values into JSON ", exp);
        }
        return authorizationJSON;
    }
    
    public JSONObject getAuthorizationUrl(final Long enrollmentTemplateID, final Long deviceForEnrollId, final int platform) {
        return this.getAuthorizationUrl(this.getAuthMode(enrollmentTemplateID), deviceForEnrollId, platform);
    }
    
    public JSONObject processPostAdAuth(final JSONObject enrollmentJson) {
        final AdminEnrollmentHandler enrollmentHandler = new AdminEnrollmentHandler();
        JSONObject responseMsg;
        try {
            final Long deviceForEnrollmentId = enrollmentJson.getLong("DeviceForEnrollmentId");
            final Long managedUserId = enrollmentJson.getLong("ManagedUserId");
            this.logger.log(Level.INFO, " The device for enrollment ID {0} the managed user is {1}", new Object[] { deviceForEnrollmentId, managedUserId });
            final DeviceForEnrollmentHandler enrollmentUserAssociationHandler = new DeviceForEnrollmentHandler();
            enrollmentUserAssociationHandler.addOrUpdateUserForDevice(deviceForEnrollmentId, managedUserId);
            responseMsg = enrollmentHandler.getDeviceEnrollmentInfo(enrollmentJson, enrollmentJson.getInt("PlatformType"));
            final JSONObject json = new JSONObject();
            if (responseMsg.has("EnrollmentReqID")) {
                final Long enrollmentRequest = responseMsg.getLong("EnrollmentReqID");
                json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequest.toString());
                final JSONObject enrollmentDetailsJSON = MDMEnrollmentRequestHandler.getInstance().getEnrollmentDetails(enrollmentRequest);
                responseMsg.put("device_ownedby", enrollmentDetailsJSON.get("owned_by"));
            }
            final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            responseMsg.put("serverName", (Object)((Hashtable<K, String>)natProps).get("NAT_ADDRESS"));
            responseMsg.put("portNumber", (Object)((Hashtable<K, Integer>)natProps).get("NAT_HTTPS_PORT"));
            json.put("decodeToken", true);
            if (enrollmentJson.has("PlatformType") && enrollmentJson.get("PlatformType").equals(1)) {
                json.put("decodeToken", false);
            }
            final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(json);
            if (key != null && key.getVersion() == APIKey.VERSION_2_0) {
                responseMsg.put("Services", (Object)key.toClientJSON());
            }
            final JSONObject jsonObject = responseMsg;
            final String s = "IsOnPremise";
            CustomerInfoUtil.getInstance();
            jsonObject.put(s, !CustomerInfoUtil.isSAS());
            MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", responseMsg.getLong("CustomerID"));
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while processing post AD auth process", exp);
            responseMsg = new JSONObject();
        }
        JSONObject myResponseMessage;
        try {
            myResponseMessage = JSONUtil.getInstance().convertLongToString(responseMsg);
        }
        catch (final Exception e) {
            myResponseMessage = new JSONObject();
            this.logger.log(Level.SEVERE, " Cannot convert the Long to string ", e);
        }
        return myResponseMessage;
    }
    
    public long getDeviceForEnrollmentId(final JSONObject deviceJson) {
        final DeviceForEnrollmentHandler enrollmentHandler = new DeviceForEnrollmentHandler();
        Long deviceForEnrollmentId = enrollmentHandler.getDeviceForEnrollmentId(deviceJson);
        this.logger.log(Level.INFO, "deviceforEnrollmentId {0} for device details {1} inside getDeviceForEnrollmentId", new Object[] { deviceForEnrollmentId, deviceJson.toString() });
        if (deviceForEnrollmentId == null) {
            try {
                final EnrollmentTemplateHandler templateHandler = new EnrollmentTemplateHandler();
                final JSONObject deviceDetails = deviceJson.getJSONObject("Message");
                final JSONObject templateJson = templateHandler.getEnrollmentTemplateForTemplateToken((String)deviceDetails.get("TemplateToken"));
                deviceDetails.put("CustomerId", templateJson.getLong("CUSTOMER_ID"));
                deviceForEnrollmentId = enrollmentHandler.addDeviceForEnrollment(deviceDetails, templateJson.getInt("TEMPLATE_TYPE"));
                this.logger.log(Level.INFO, "deviceforEnrollmentId {0} for template details {1} inside getDeviceForEnrollmentId", new Object[] { deviceForEnrollmentId, templateJson.toString() });
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Cannot assign deviceForEnrollmentId for device ", exp);
            }
        }
        try {
            final JSONObject templateJSON = new EnrollmentTemplateHandler().getEnrollmentTemplateForTemplateToken(String.valueOf(deviceJson.getJSONObject("Message").get("TemplateToken")));
            if (templateJSON != null) {
                final Long templateId = templateJSON.getLong("TEMPLATE_ID");
                this.addDeviceForEnrollmentToGroup(templateId, deviceForEnrollmentId);
                final List deviceList = new ArrayList();
                deviceList.add(deviceForEnrollmentId);
                EnrollmentTemplateHandler.addOrUpdateTemplateToDeviceForEnrollment(deviceList, templateJSON.getLong("TEMPLATE_ID"));
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Cannot assign group for deviceForEnrollmentId: ", exp);
        }
        return deviceForEnrollmentId;
    }
    
    private void addDeviceForEnrollmentToGroup(final Long templateId, final Long deviceforEnrollId) throws Exception {
        final JSONArray groupIdJsonArray = this.getAutoAssignGroupIdsForTemplateId(templateId);
        final DataObject deviceForEnrollToGroup = (DataObject)new WritableDataObject();
        for (int i = 0; i < groupIdJsonArray.length(); ++i) {
            final Long groupId = Long.parseLong(String.valueOf(groupIdJsonArray.get(i)));
            final Row deviceForEnrollToGroupRow = new Row("DeviceEnrollmentToGroup");
            deviceForEnrollToGroupRow.set("ASSOCIATED_GROUP_ID", (Object)groupId);
            deviceForEnrollToGroupRow.set("ENROLLMENT_DEVICE_ID", (Object)deviceforEnrollId);
            deviceForEnrollToGroup.addRow(deviceForEnrollToGroupRow);
        }
        MDMUtil.getPersistenceLite().add(deviceForEnrollToGroup);
    }
    
    public JSONObject getAdminEnrollADAuthSettings(final Long templateId, final Long userID) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("OutofBoxEnrollmentSettings"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("OutofBoxEnrollmentSettings", "TEMPLATE_ID"), (Object)templateId, 0));
            selectQuery.addSelectColumn(Column.getColumn("OutofBoxEnrollmentSettings", "TEMPLATE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("OutofBoxEnrollmentSettings", "AUTH_MODE"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final int authMode = (int)dataObject.getFirstValue("OutofBoxEnrollmentSettings", "AUTH_MODE");
                final int ad_auth_enabled = (authMode == 4) ? 1 : 0;
                jsonObject.put("user_assignment_type", ad_auth_enabled);
                if (authMode == 4) {
                    jsonObject.put("group_ids", (Object)this.getAutoAssignGroupIdsForTemplateId(templateId));
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getAdminEnrollAuthSettings", exp);
        }
        return jsonObject;
    }
    
    private JSONArray getAutoAssignGroupIdsForTemplateId(final Long templateId) throws Exception {
        final JSONArray jsonArray = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("OutofBoxEnrollmentSettingsToGroup"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("OutofBoxEnrollmentSettingsToGroup", "TEMPLATE_ID"), (Object)templateId, 0));
        selectQuery.addSelectColumn(Column.getColumn("OutofBoxEnrollmentSettingsToGroup", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("OutofBoxEnrollmentSettingsToGroup", "TEMPLATE_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("OutofBoxEnrollmentSettingsToGroup");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                jsonArray.put((Object)String.valueOf(row.get("RESOURCE_ID")));
            }
        }
        return jsonArray;
    }
    
    public JSONObject getPostAuthInputJSON(final Long deviceForEnrollmentId, final Long managedUserId) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "EnrollmentTemplateToDeviceEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("EnrollmentTemplateToDeviceEnrollment", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "IMEI"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplateToDeviceEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplateToDeviceEnrollment", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentId, 0));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        Long templateID = -1L;
        int templateType = -1;
        if (!dataObject.isEmpty()) {
            Row row = dataObject.getFirstRow("DeviceForEnrollment");
            jsonObject.put("IMEI", (Object)row.get("IMEI"));
            jsonObject.put("SerialNumber", (Object)row.get("SERIAL_NUMBER"));
            templateID = (Long)dataObject.getFirstValue("EnrollmentTemplateToDeviceEnrollment", "TEMPLATE_ID");
            row = dataObject.getRow("EnrollmentTemplate", new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)templateID, 0));
            templateType = (int)((row == null) ? -1 : row.get("TEMPLATE_TYPE"));
        }
        jsonObject.put("DeviceForEnrollmentId", (Object)deviceForEnrollmentId);
        jsonObject.put("ManagedUserId", (Object)managedUserId);
        jsonObject.put("TemplateToken", (Object)EnrollmentTemplateHandler.getTemplateTokenForTemplateId(templateID));
        jsonObject.put("PlatformType", new EnrollmentTemplateHandler().getPlatformForTemplate(templateType));
        return jsonObject;
    }
    
    static {
        OutOfBoxEnrollmentSettingsHandler.enrollmentSettingsHandler = null;
    }
}
