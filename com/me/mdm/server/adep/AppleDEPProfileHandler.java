package com.me.mdm.server.adep;

import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.SortColumn;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Iterator;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import com.me.mdm.core.enrollment.AppleDEPDeviceForEnrollmentHandler;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONArray;
import com.me.mdm.server.deviceaccounts.AccountDetailsHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.core.enrollment.OutOfBoxEnrollmentSettingsHandler;
import com.me.mdm.server.adep.mac.AccountConfiguration;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONObject;
import java.util.logging.Logger;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

public class AppleDEPProfileHandler extends AppleDEPHandler
{
    private static DualHashBidiMap skipApiKeysMap;
    public static Logger logger;
    
    public static AppleDEPProfileHandler getInstance(final Long tokenId) {
        return new AppleDEPProfileHandler(tokenId);
    }
    
    public static AppleDEPProfileHandler getInstance(final Long tokenId, final Long custoemrID) {
        return new AppleDEPProfileHandler(tokenId, custoemrID);
    }
    
    private AppleDEPProfileHandler(final Long tokenID) {
        super(tokenID);
    }
    
    private AppleDEPProfileHandler(final Long tokenId, final Long customerID) {
        super(tokenId, customerID);
    }
    
    public void createProfile(final JSONObject depJSON) throws Exception {
        final Boolean isAwaitingConfigurationRequired = depJSON.optBoolean("IS_ACCOUNT_CONFIG_ENABLED", (boolean)Boolean.FALSE) && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MacAccountConfig");
        final EnrollmentTemplateHandler handler = new EnrollmentTemplateHandler();
        depJSON.put("CUSTOMER_ID", (Object)this.customerId);
        depJSON.put("DEP_TOKEN_ID", (Object)this.tokenId);
        depJSON.put("ENABLE_AWAIT_CONFIG", (Object)isAwaitingConfigurationRequired);
        handler.addorUpdateDEPEnrollmentTemplate(depJSON);
        final Long hiddenCustomGroupForTokenID = DEPEnrollmentUtil.createNewCustomGroupForDEPToken(this.tokenId);
        final Long templateID = depJSON.optLong("TEMPLATE_ID");
        EnrollmentTemplateHandler.addOrUpdateEnrollmentTemplateToInvisibleGroup(hiddenCustomGroupForTokenID, templateID);
        if (depJSON.has("GROUP_RESOURCE_ID")) {
            final List<Long> defaultAssignGroupSelectedInUI = JSONUtil.getInstance().convertLongJSONArrayTOList(depJSON.getJSONArray("GROUP_RESOURCE_ID"));
            AppleDEPProfileHandler.logger.log(Level.INFO, "Adding DEP token to device groups {0}", defaultAssignGroupSelectedInUI.toArray());
            EnrollmentTemplateHandler.addOrUpdateEnrollmentTemplateToGroup(defaultAssignGroupSelectedInUI, templateID);
        }
        else {
            AppleDEPProfileHandler.logger.log(Level.INFO, "Adding DEP token to device groups - Null");
            EnrollmentTemplateHandler.addOrUpdateEnrollmentTemplateToGroup(null, templateID);
        }
        this.saveAdminEnrollADAuthSettings(depJSON);
        if (isAwaitingConfigurationRequired) {
            AppleDEPProfileHandler.logger.log(Level.INFO, "Adding DEP token to account configuration. Is awaiting configuration is enabled.");
            this.saveAccountConfiguration(depJSON, templateID);
        }
        else {
            AppleDEPProfileHandler.logger.log(Level.INFO, "Removing DEP token to account configuration. Is awaiting configuration is disabled.");
            final AccountConfiguration accountHandler = new AccountConfiguration();
            final Long accountConfigID = accountHandler.getAccountConfigIDForDEPEnrollTemplate(templateID);
            if (accountConfigID != null) {
                this.disassociateAccountConfigFromEnrollmentTemplate(this.customerId, accountConfigID);
            }
        }
        final Boolean isSharedIPad = depJSON.optBoolean("IS_MULTIUSER");
        if (isSharedIPad) {
            AppleDEPProfileHandler.logger.log(Level.INFO, "Adding DEP token to shared device configuration. Is shared iPad is enabled.");
            final JSONObject sharedDeviceConfigJSON = depJSON.optJSONObject("shared_device_config");
            if (sharedDeviceConfigJSON != null) {
                this.saveSharedDeviceConfiguration(sharedDeviceConfigJSON, templateID);
            }
            else {
                AppleDEPProfileHandler.logger.log(Level.WARNING, "No shared iPad Config received but isMultiUser is enabled");
            }
        }
        else {
            AppleDEPProfileHandler.logger.log(Level.INFO, "Removing DEP token to shared device configuration. Is shared iPad is disabled.");
            this.deleteSharedDeviceConfigurationForTemplate(templateID);
        }
        AppleDEPWebServicetHandler.getInstance(this.tokenId, this.customerId).updateCursor(null);
        this.assignDEPProfile();
    }
    
    private void saveAdminEnrollADAuthSettings(final JSONObject depJSON) {
        final Long templateId = depJSON.getLong("TEMPLATE_ID");
        final JSONObject body = new JSONObject();
        body.put("template_id", (Object)templateId);
        body.put("user_id", depJSON.getLong("ADDED_USER"));
        body.put("user_assignment_type", depJSON.optBoolean("ENABLE_SELF_ENROLL", (boolean)Boolean.FALSE) ? 4 : 0);
        if (depJSON.has("GROUP_RESOURCE_ID")) {
            body.put("group_ids", (Object)depJSON.getJSONArray("GROUP_RESOURCE_ID"));
        }
        OutOfBoxEnrollmentSettingsHandler.getInstance().addOrUpdateSettingsTemplate(body);
    }
    
    private void deleteSharedDeviceConfigurationForTemplate(final Long templateID) throws DataAccessException {
        AppleDEPProfileHandler.logger.log(Level.INFO, "Going to delete Shared device configuration for template:{0}", templateID);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AppleSharedDeviceConfigTemplate");
        deleteQuery.setCriteria(new Criteria(new Column("AppleSharedDeviceConfigTemplate", "TEMPLATE_ID"), (Object)templateID, 0));
        MDMUtil.getPersistence().delete(deleteQuery);
    }
    
    private void saveSharedDeviceConfiguration(final JSONObject sharedDeviceConfigJSON, final Long templateID) throws Exception {
        final Long quoataSize = sharedDeviceConfigJSON.optLong("quota_size", -1L);
        final int residentUsers = sharedDeviceConfigJSON.optInt("resident_users", -1);
        DataObject sharedDeviceDO = DBUtil.getDataObjectFromDB("AppleSharedDeviceConfigTemplate", "TEMPLATE_ID", (Object)templateID);
        Row tableRow = null;
        boolean isNew = false;
        if (sharedDeviceDO != null) {
            tableRow = sharedDeviceDO.getFirstRow("AppleSharedDeviceConfigTemplate");
        }
        else {
            sharedDeviceDO = (DataObject)new WritableDataObject();
            tableRow = new Row("AppleSharedDeviceConfigTemplate");
            tableRow.set("TEMPLATE_ID", (Object)templateID);
            isNew = true;
        }
        tableRow.set("QUOTA_SIZE", (Object)quoataSize);
        tableRow.set("NO_RESIDENT_USERS", (Object)residentUsers);
        if (isNew) {
            sharedDeviceDO.addRow(tableRow);
        }
        else {
            sharedDeviceDO.updateRow(tableRow);
        }
        MDMUtil.getPersistence().update(sharedDeviceDO);
        AppleDEPProfileHandler.logger.log(Level.INFO, "Saved Shared Device Config{0} for :{1}", new Object[] { sharedDeviceConfigJSON, templateID });
    }
    
    private void saveAccountConfiguration(final JSONObject depJSON, final Long enrollTemplateID) {
        try {
            final JSONObject accountSettings = JSONUtil.getInstance().changeJSONKeyCase(depJSON, 2);
            final Long customerID = JSONUtil.optLongForUVH(accountSettings, "CUSTOMER_ID".toLowerCase(), Long.valueOf(-1L));
            accountSettings.put("CUSTOMER_ID", (Object)customerID);
            final SelectQuery query = this.getEnrollmentTemplateSQ();
            query.addJoin(new Join("DEPEnrollmentTemplate", "AccountConfigToDEPEnroll", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
            query.addJoin(new Join("AccountConfigToDEPEnroll", "MdMacAccountConfigSettings", new String[] { "ACCOUNT_CONFIG_ID" }, new String[] { "ACCOUNT_CONFIG_ID" }, 2));
            query.addJoin(new Join("MdMacAccountConfigSettings", "MdMacAccountToConfig", new String[] { "ACCOUNT_CONFIG_ID" }, new String[] { "ACCOUNT_CONFIG_ID" }, 2));
            query.addJoin(new Join("MdMacAccountToConfig", "MdComputerAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
            query.addSelectColumn(Column.getColumn("MdComputerAccount", "*"));
            query.addSelectColumn(Column.getColumn("MdMacAccountConfigSettings", "*"));
            final Criteria criteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)enrollTemplateID, 0);
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            Long accountID = null;
            if (!dataObject.isEmpty()) {
                final Row accountConfigRow = dataObject.getFirstRow("MdMacAccountConfigSettings");
                final Long accountConfigID = (Long)accountConfigRow.get("ACCOUNT_CONFIG_ID");
                accountID = AccountConfiguration.getInstance().checkAndRemoveAcountConfiguration(accountConfigID, customerID);
            }
            final AccountDetailsHandler handler = new AccountDetailsHandler();
            accountSettings.put("ACCOUNT_TYPE".toLowerCase(), 1);
            accountSettings.put("HASH_ALGORITHM".toLowerCase(), 1);
            if (accountID != null) {
                accountSettings.put("ACCOUNT_ID", (Object)accountID);
            }
            JSONObject responseJSON = handler.addOrUpdateAccount(accountSettings);
            final JSONArray accountArray = new JSONArray();
            final JSONObject accountJSON = new JSONObject();
            accountJSON.put("ACCOUNT_ID".toLowerCase(), (Object)JSONUtil.optLongForUVH(responseJSON, "ACCOUNT_ID", Long.valueOf(-1L)));
            accountJSON.put("HIDDEN".toLowerCase(), accountSettings.optBoolean("HIDDEN".toLowerCase(), (boolean)Boolean.TRUE));
            accountArray.put((Object)accountJSON);
            accountSettings.put("accounts", (Object)accountArray);
            responseJSON = handler.addOrUpdateMacAccountConfig(accountSettings);
            final Long accountConfigID2 = JSONUtil.optLongForUVH(responseJSON, "ACCOUNT_CONFIG_ID", Long.valueOf(-1L));
            AccountConfiguration.getInstance().addOrModifyAccounntConfigurationToDEPEnrollTemplate(accountConfigID2, enrollTemplateID);
        }
        catch (final Exception e) {
            AppleDEPProfileHandler.logger.log(Level.SEVERE, "Exception in saving Account Configuration for DEP.", e);
        }
    }
    
    public void disassociateAccountConfigFromEnrollmentTemplate(final Long customerID, final Long accountConfig) {
        try {
            AccountConfiguration.getInstance().checkAndRemoveAcountConfiguration(accountConfig, customerID);
        }
        catch (final Exception e) {
            AppleDEPProfileHandler.logger.log(Level.SEVERE, "Unable to disassociate Account configuration from dep enrollment template", e);
        }
    }
    
    public void createAndAssignDEPProfileAsynchronously() {
        try {
            final JSONObject queueData = new JSONObject();
            queueData.put("DEP_TOKEN_ID", (Object)this.tokenId);
            final CommonQueueData depQueueItem = new CommonQueueData();
            depQueueItem.setJsonQueueData(queueData);
            depQueueItem.setTaskName("DEPAssignProfileTask");
            depQueueItem.setClassName("com.me.mdm.server.adep.DEPAssignProfileTask");
            depQueueItem.setCustomerId(this.customerId);
            CommonQueueUtil.getInstance().addToQueue(depQueueItem, CommonQueues.MDM_ENROLLMENT);
        }
        catch (final Exception ex) {
            AppleDEPProfileHandler.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private String getDEPSyncCursor() throws DataAccessException {
        AppleDEPProfileHandler.logger.log(Level.INFO, "Inside getDEPSyncCursor");
        String cursor = null;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DEPTokenDetails"));
        Criteria criteria = new Criteria(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"), (Object)this.tokenId, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("DEPTokenDetails", "CUSTOMER_ID"), (Object)this.customerId, 0));
        sQuery.setCriteria(criteria);
        sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "DEP_TOKEN_ID"));
        sQuery.addSelectColumn(Column.getColumn("DEPTokenDetails", "CURSOR"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sQuery);
        final Row row = DO.getFirstRow("DEPTokenDetails");
        cursor = (String)row.get("CURSOR");
        return cursor;
    }
    
    protected void updateTimeDFEForActiveDevices(final Long customerId, final Long tokenId) throws Exception {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DeviceForEnrollment");
        updateQuery.addJoin(new Join("DeviceForEnrollment", "DEPDevicesSyncData", new String[] { "SERIAL_NUMBER" }, new String[] { "SERIAL_NUMBER" }, 2));
        Criteria columnCriteria = new Criteria(new Column("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerId, 0);
        columnCriteria = columnCriteria.and(new Criteria(new Column("DEPDevicesSyncData", "DEP_TOKEN_ID"), (Object)tokenId, 0));
        columnCriteria = columnCriteria.and(new Criteria(new Column("DEPDevicesSyncData", "DEVICE_STATUS"), (Object)1, 0));
        updateQuery.setCriteria(columnCriteria);
        updateQuery.setUpdateColumn("UPDATED_TIME", (Object)MDMUtil.getCurrentTime());
        MDMUtil.getPersistence().update(updateQuery);
    }
    
    public JSONObject fetchOrSyncDEPDevices() throws Exception {
        AppleDEPProfileHandler.logger.log(Level.INFO, "Inside fetchOrSyncDEPDevices");
        final ADEPServerSyncHandler syncHander = ADEPServerSyncHandler.getInstance(this.tokenId, this.customerId);
        List<String> addedDevicesList = null;
        List<String> deletedDevicesList = null;
        final JSONObject dataJSON = new JSONObject();
        try {
            syncHander.addOrUpdateServerSyncDetails(3, -1, "--");
            final Long currentTime = MDMUtil.getCurrentTime();
            final String cursor = this.getDEPSyncCursor();
            final boolean abmFetch = MDMFeatureParamsHandler.getInstance().isFeatureEnabledInDB("ABMFetch");
            final boolean fetch = abmFetch || cursor == null;
            if (fetch) {
                addedDevicesList = this.fetchDEPDevices(null);
            }
            else {
                final Map<String, List<String>> serialNumberMap = this.syncDEPDevices(cursor);
                addedDevicesList = serialNumberMap.get("addedDevices");
                deletedDevicesList = serialNumberMap.get("deletedDevices");
            }
            final AppleDEPDeviceForEnrollmentHandler handler = new AppleDEPDeviceForEnrollmentHandler();
            new SyncDevicesMETracker().trackSyncedDevicesCount();
            AppleDEPProfileHandler.logger.log(Level.INFO, "DEP devices fetch or sync completed");
            if (fetch) {
                handler.addOrUpdateDEPdevicesSyncData(this.tokenId, addedDevicesList);
            }
            else {
                handler.addOrUpdateDEPdevicesSyncData(this.tokenId, addedDevicesList, deletedDevicesList);
            }
            final Long hiddenCustomGroupForTokenID = DEPEnrollmentUtil.createNewCustomGroupForDEPToken(this.tokenId);
            handler.addDevicesInDEPTokenToInternalGroup(this.tokenId, hiddenCustomGroupForTokenID);
            syncHander.addOrUpdateServerSyncDetails(1, -1, "--");
            EnrollmentTemplateHandler.addOrUpdateTemplateToDeviceForEnrollment(this.tokenId);
            final JSONObject profileJSON = this.getDEPProfileDetails();
            final String profileUUID = String.valueOf(profileJSON.get("PROFILE_UUID"));
            final List<String> devicesListForAssigningDEPProfile = this.getDevicesListForAssigningDEPProfile(profileUUID);
            if (!fetch) {
                this.updateTimeDFEForActiveDevices(this.customerId, this.tokenId);
            }
            handler.deleteEnrolledDevice(this.customerId, this.tokenId, currentTime);
            dataJSON.put("addedDevicesList", (Collection)addedDevicesList);
            dataJSON.put("devicesListForAssigningDEPProfile", (Collection)devicesListForAssigningDEPProfile);
            dataJSON.put("profileUUID", (Object)profileUUID);
        }
        catch (final Exception e) {
            AppleDEPProfileHandler.logger.log(Level.WARNING, "Exception while fetching or syncing ABM devices. ", e);
            syncHander.addOrUpdateServerSyncDetails(2, 1013, "OtherError");
        }
        return dataJSON;
    }
    
    private List<String> fetchDEPDevices(final String cursor) throws Exception {
        final List<String> serialNumberList = new ArrayList<String>();
        final JSONObject deviceJSON = AppleDEPWebServicetHandler.getInstance(this.tokenId, this.customerId).getDeviceJSON(cursor, true);
        final JSONArray deviceArray = (JSONArray)deviceJSON.get("devices");
        final Set<String> serialNumberSet = new HashSet<String>();
        for (int i = 0; i < deviceArray.length(); ++i) {
            serialNumberSet.add(String.valueOf(deviceArray.getJSONObject(i).get("serial_number")));
        }
        this.processDeviceDetails(deviceArray, true);
        final Boolean moreToFollow = deviceJSON.getBoolean("more_to_follow");
        if (moreToFollow) {
            final String cursorStr = String.valueOf(deviceJSON.get("cursor"));
            final List<String> newDeviceList = this.fetchDEPDevices(cursorStr);
            serialNumberSet.addAll(newDeviceList);
        }
        serialNumberList.addAll(serialNumberSet);
        return serialNumberList;
    }
    
    private Map<String, List<String>> syncDEPDevices(final String cursor) throws Exception {
        final Map<String, List<String>> serialNumberMap = new HashMap<String, List<String>>();
        final Set<String> addedSerialNumberSet = new HashSet<String>();
        final Set<String> deletedSerialNumberSet = new HashSet<String>();
        final JSONArray addedOrUpdatedDeviceArray = new JSONArray();
        final JSONObject deviceJSON = AppleDEPWebServicetHandler.getInstance(this.tokenId, this.customerId).getDeviceJSON(cursor, false);
        final JSONArray newDeviceArray = (JSONArray)deviceJSON.get("devices");
        for (int i = 0; i < newDeviceArray.length(); ++i) {
            final String serialNumber = String.valueOf(newDeviceArray.getJSONObject(i).get("serial_number"));
            final String opType = String.valueOf(newDeviceArray.getJSONObject(i).get("op_type"));
            if (opType.equals("deleted")) {
                deletedSerialNumberSet.add(serialNumber);
            }
            else if (opType.equals("added") || opType.equals("modified")) {
                addedSerialNumberSet.add(serialNumber);
                addedOrUpdatedDeviceArray.put((Object)newDeviceArray.getJSONObject(i));
            }
        }
        this.processDeviceDetails(addedOrUpdatedDeviceArray, false);
        final Boolean moreToFollow = deviceJSON.getBoolean("more_to_follow");
        if (moreToFollow) {
            final String cursorStr = String.valueOf(deviceJSON.get("cursor"));
            final Map<String, List<String>> partialSerialNumberMap = this.syncDEPDevices(cursorStr);
            addedSerialNumberSet.addAll(partialSerialNumberMap.get("addedDevices"));
            deletedSerialNumberSet.addAll(partialSerialNumberMap.get("deletedDevices"));
        }
        final List<String> addedSerialNumberList = new ArrayList<String>();
        final List<String> deletedSerialNumberList = new ArrayList<String>();
        addedSerialNumberList.addAll(addedSerialNumberSet);
        deletedSerialNumberList.addAll(deletedSerialNumberSet);
        serialNumberMap.put("addedDevices", addedSerialNumberList);
        serialNumberMap.put("deletedDevices", deletedSerialNumberList);
        return serialNumberMap;
    }
    
    public void processDeviceDetails(final JSONArray deviceArray, final boolean fetch) throws Exception {
        final DeviceForEnrollmentHandler handler = new DeviceForEnrollmentHandler();
        final JSONObject tokenJSON = new JSONObject();
        tokenJSON.put("CUSTOMER_ID", (Object)this.customerId);
        tokenJSON.put("DEP_TOKEN_ID", (Object)this.tokenId);
        handler.processDEPDeviceList(deviceArray, tokenJSON, fetch);
    }
    
    public JSONObject fetchAndExcludeDEPDevices() throws Exception {
        final JSONObject dataJSON = this.fetchOrSyncDEPDevices();
        return dataJSON;
    }
    
    private List<String> getDevicesListForAssigningDEPProfile(final String profileUUID) throws Exception {
        List<String> devicesListForAssigningDEPProfile = null;
        try {
            devicesListForAssigningDEPProfile = new ArrayList<String>();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppleDEPDeviceForEnrollment"));
            sQuery.addJoin(new Join("AppleDEPDeviceForEnrollment", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            Criteria criteria1 = new Criteria(new Column("AppleDEPDeviceForEnrollment", "DEP_TOKEN_ID"), (Object)this.tokenId, 0);
            Criteria criteria2 = new Criteria(new Column("AppleDEPDeviceForEnrollment", "PROFILE_UUID"), (Object)profileUUID, 1);
            criteria2 = criteria2.or(new Criteria(new Column("AppleDEPDeviceForEnrollment", "PROFILE_STATUS"), (Object)DEPConstants.DFEProfileStatus.ASSIGNED, 1));
            criteria1 = criteria1.and(criteria2);
            sQuery.setCriteria(criteria1);
            sQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
            sQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                final Iterator item1 = DO.getRows("DeviceForEnrollment");
                while (item1.hasNext()) {
                    final Row DFERow = item1.next();
                    devicesListForAssigningDEPProfile.add(DFERow.get("SERIAL_NUMBER").toString());
                }
            }
        }
        catch (final Exception e) {
            AppleDEPProfileHandler.logger.log(Level.SEVERE, "Error while executing getDevicesListForAssigningDEPProfile method", e);
        }
        return devicesListForAssigningDEPProfile;
    }
    
    public void assignDEPProfile() throws Exception {
        try {
            final JSONObject profileJOSN = new JSONObject();
            final JSONObject dataJSON = this.fetchAndExcludeDEPDevices();
            final JSONArray devicesListForAssigningDEPProfile = (JSONArray)dataJSON.get("devicesListForAssigningDEPProfile");
            final String profileUUID = (String)dataJSON.get("profileUUID");
            AppleDEPProfileHandler.logger.info("Profile being assigned to devices: " + devicesListForAssigningDEPProfile);
            final JSONArray deviceArray = devicesListForAssigningDEPProfile;
            if (deviceArray.length() > 0) {
                profileJOSN.put("devices", (Object)deviceArray);
                profileJOSN.put("profile_uuid", (Object)profileUUID);
                AppleDEPWebServicetHandler.getInstance(this.tokenId, this.customerId).assignDEPDevice(profileJOSN);
            }
        }
        catch (final SyMException ex) {
            if (ex.getMessage() == null || !ex.getMessage().toLowerCase().contains("PROFILE_NOT_FOUND".toLowerCase())) {
                throw ex;
            }
            AppleDEPProfileHandler.logger.info("Profile not found...so redefining DEP Profile...");
            this.createProfile(this.getDEPProfileDetails());
        }
    }
    
    private SelectQuery getEnrollmentTemplateSQ() {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplate"));
        sQuery.addJoin(new Join("EnrollmentTemplate", "DEPEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addJoin(new Join("DEPEnrollmentTemplate", "EnrollmentTemplateToGroupRel", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentTemplateToGroupRel", "DEPTokenToGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2));
        return sQuery;
    }
    
    public JSONObject getDEPProfileDetails() throws Exception {
        JSONObject profileJson = null;
        final Long customerID = this.customerId;
        final SelectQuery sQuery = this.getEnrollmentTemplateSQ();
        sQuery.addJoin(new Join("DEPEnrollmentTemplate", "AccountConfigToDEPEnroll", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        sQuery.addJoin(new Join("AccountConfigToDEPEnroll", "MdMacAccountConfigSettings", new String[] { "ACCOUNT_CONFIG_ID" }, new String[] { "ACCOUNT_CONFIG_ID" }, 1));
        sQuery.addJoin(new Join("MdMacAccountConfigSettings", "MdMacAccountToConfig", new String[] { "ACCOUNT_CONFIG_ID" }, new String[] { "ACCOUNT_CONFIG_ID" }, 1));
        sQuery.addJoin(new Join("MdMacAccountToConfig", "MdComputerAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 1));
        sQuery.addJoin(new Join("DEPEnrollmentTemplate", "AppleSharedDeviceConfigTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        sQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "*"));
        sQuery.addSelectColumn(Column.getColumn("DEPEnrollmentTemplate", "*"));
        sQuery.addSelectColumn(Column.getColumn("DEPTokenToGroup", "*"));
        sQuery.addSelectColumn(Column.getColumn("MdMacAccountConfigSettings", "*"));
        sQuery.addSelectColumn(Column.getColumn("MdMacAccountToConfig", "*"));
        sQuery.addSelectColumn(Column.getColumn("MdComputerAccount", "*"));
        sQuery.addSelectColumn(Column.getColumn("AppleSharedDeviceConfigTemplate", "*"));
        sQuery.addSortColumn(new SortColumn(Column.getColumn("EnrollmentTemplate", "ADDED_TIME"), false));
        Criteria cCustomerId = new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        if (this.tokenId != null) {
            final Criteria cTokenID = new Criteria(new Column("DEPTokenToGroup", "DEP_TOKEN_ID"), (Object)this.tokenId, 0);
            cCustomerId = cCustomerId.and(cTokenID);
        }
        sQuery.setCriteria(cCustomerId);
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        if (!DO.isEmpty()) {
            profileJson = new JSONObject();
            final Row profileRow = DO.getFirstRow("DEPEnrollmentTemplate");
            final Row teplateRow = DO.getFirstRow("EnrollmentTemplate");
            final Row tokenToGroupRow = DO.getFirstRow("DEPTokenToGroup");
            profileJson.put("TEMPLATE_NAME", teplateRow.get("TEMPLATE_NAME"));
            profileJson.put("PROFILE_UUID", profileRow.get("PROFILE_UUID"));
            profileJson.put("PROFILE_UUID", profileRow.get("PROFILE_UUID"));
            profileJson.put("DEP_TOKEN_ID", (Object)String.valueOf(tokenToGroupRow.get("DEP_TOKEN_ID")));
            profileJson.put("AUTO_ADVANCE_TV", profileRow.get("AUTO_ADVANCE_TV"));
            profileJson.put("IS_MULTIUSER", profileRow.get("IS_MULTIUSER"));
            profileJson.put("ACTIVATION_BY", profileRow.get("ACTIVATION_BY"));
            final List<String> columnList = profileRow.getColumns();
            for (final String eachColumn : columnList) {
                if (eachColumn.startsWith("SKIP_") && (boolean)profileRow.get(eachColumn)) {
                    profileJson.put(eachColumn, true);
                }
            }
            final Boolean isAwatingConfiguration = (Boolean)profileRow.get("ENABLE_AWAIT_CONFIG");
            profileJson.put("IS_ACCOUNT_CONFIG_ENABLED", (Object)isAwatingConfiguration);
            if (isAwatingConfiguration) {
                this.addAccountConfigDetails(profileJson, DO);
            }
            profileJson.put("CUSTOMER_ID", (Object)String.valueOf(teplateRow.get("CUSTOMER_ID")));
            final Long userId = Long.valueOf(teplateRow.get("ADDED_USER").toString());
            profileJson.put("ADDED_USER", (Object)userId);
            profileJson.put("USER_NAME", (Object)DMUserHandler.getUserNameFromUserID(userId));
            profileJson.put("ENABLE_SELF_ENROLL", profileRow.get("ENABLE_SELF_ENROLL"));
            final List<Long> groupID = EnrollmentTemplateHandler.getDefaultGroupIDForTemplate((Long)teplateRow.get("TEMPLATE_ID"));
            if (groupID.size() > 0) {
                profileJson.put("GROUP_RESOURCE_ID", (Object)JSONUtil.getInstance().convertListToJSONArray(groupID));
            }
            final Boolean isMultiUser = (Boolean)profileRow.get("IS_MULTIUSER");
            if (isMultiUser) {
                this.addSharediPadProfileJSON(profileJson, DO);
            }
        }
        if (profileJson.getInt("ACTIVATION_BY") == -1) {
            if (!profileJson.getBoolean("ENABLE_SELF_ENROLL")) {
                profileJson.put("ACTIVATION_BY", 1);
            }
            else if (profileJson.optBoolean("SKIP_ACC_CREATION")) {
                profileJson.put("ACTIVATION_BY", 1);
            }
            else {
                profileJson.put("ACTIVATION_BY", 2);
            }
        }
        return profileJson;
    }
    
    private void addSharediPadProfileJSON(final JSONObject profileJson, final DataObject aDo) {
        try {
            if (aDo.containsTable("AppleSharedDeviceConfigTemplate")) {
                final Row sharediPadRow = aDo.getFirstRow("AppleSharedDeviceConfigTemplate");
                final JSONObject sharedJSON = new JSONObject();
                sharedJSON.put("resident_users", sharediPadRow.get("NO_RESIDENT_USERS"));
                sharedJSON.put("quota_size", sharediPadRow.get("QUOTA_SIZE"));
                profileJson.put("shared_device_config", (Object)sharedJSON);
            }
        }
        catch (final Exception ex) {
            AppleDEPProfileHandler.logger.log(Level.WARNING, "Exceptipn in addSharediPadProfileJSON", ex);
        }
    }
    
    private void addAccountConfigDetails(final JSONObject profileJson, final DataObject dataObject) {
        try {
            if (!dataObject.containsTable("MdComputerAccount")) {
                profileJson.put("IS_ACCOUNT_CONFIG_ENABLED", (Object)Boolean.FALSE);
                return;
            }
            final Row accountConfigRow = dataObject.getFirstRow("MdMacAccountConfigSettings");
            final Row accountRow = dataObject.getFirstRow("MdComputerAccount");
            final Row accountRelRow = dataObject.getFirstRow("MdMacAccountToConfig");
            profileJson.put("SHORT_NAME", accountRow.get("SHORT_NAME"));
            profileJson.put("FULL_NAME", accountRow.get("FULL_NAME"));
            profileJson.put("HIDDEN", accountRelRow.get("HIDDEN"));
            profileJson.put("SKIP_ACC_CREATION", accountConfigRow.get("SKIP_ACC_CREATION"));
            profileJson.put("SET_REGULAR_ACCOUNT", accountConfigRow.get("SET_REGULAR_ACCOUNT"));
        }
        catch (final Exception e) {
            AppleDEPProfileHandler.logger.log(Level.WARNING, "Account Config Details not set profile JSON", e);
        }
    }
    
    public void syncDeviceDetailsOnRemoval(final JSONObject resourceJSON) {
        AppleDEPProfileHandler.logger.log(Level.INFO, "Entering syncDeviceDetailsOnRemoval(): ");
        final int status = DEPEnrollmentUtil.getDEPEnrollmentStatus(this.customerId);
        if (status > 2) {
            final String serialNum = resourceJSON.optString("SERIAL_NUMBER");
            final String[] devicesArray = { serialNum };
            final JSONObject deviceJSON = new JSONObject();
            JSONObject deviceObject = new JSONObject();
            try {
                deviceJSON.put("devices", (Object)devicesArray);
                deviceObject = AppleDEPWebServicetHandler.getInstance(this.tokenId, this.customerId).getDeviceDetails(deviceJSON);
            }
            catch (final Exception ex) {
                AppleDEPProfileHandler.logger.log(Level.SEVERE, "Exception in syncDeviceDetailsOnRemoval(): ", ex);
            }
            final JSONObject specificDeviceObject = deviceObject.optJSONObject("devices").optJSONObject(serialNum);
            if (specificDeviceObject.optString("response_status").startsWith("NOT_")) {
                return;
            }
            if (specificDeviceObject.optString("response_status").equalsIgnoreCase("SUCCESS")) {
                try {
                    final JSONObject processDevice = deviceObject.getJSONObject("devices");
                    final Iterator i = processDevice.keys();
                    final JSONArray deviceJsonArray = new JSONArray();
                    while (i.hasNext()) {
                        final String key = i.next();
                        deviceJsonArray.put(processDevice.get(key));
                    }
                    this.processDeviceDetails(deviceJsonArray, true);
                }
                catch (final Exception ex2) {
                    AppleDEPProfileHandler.logger.log(Level.SEVERE, "Exception in syncDeviceDetailsOnRemoval(): ", ex2);
                }
            }
            AppleDEPProfileHandler.logger.log(Level.INFO, "DEP sync done - syncDeviceDetailsOnRemoval(): ");
        }
        AppleDEPProfileHandler.logger.log(Level.INFO, "Exiting syncDeviceDetailsOnRemoval(): ");
    }
    
    private static void initializeSkipSettingKeyMap() {
        (AppleDEPProfileHandler.skipApiKeysMap = new DualHashBidiMap()).put((Object)"SKIP_APPLE_ID", (Object)"AppleID");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_BIOMETRIC", (Object)"Biometric");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_DIAGNOSTICS", (Object)"Diagnostics");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_LOCATION", (Object)"Location");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_PASSCODE", (Object)"Passcode");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_PAYMENT", (Object)"Payment");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_RESTORE", (Object)"Restore");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_SIRI", (Object)"Siri");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_TOS", (Object)"TOS");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_ZOOM", (Object)"Zoom");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_HOME_BTN_SENSITIVITY", (Object)"HomeButtonSensitivity");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_DISPLAY_TONE", (Object)"DisplayTone");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_ANDROID", (Object)"Android");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_KEYBOARD", (Object)"Keyboard");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_ONBOARDING", (Object)"OnBoarding");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_WATCH_MIGRATION", (Object)"WatchMigration");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_APPEARANCE", (Object)"Appearance");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_IMESSAGE_FACETIME", (Object)"iMessageAndFaceTime");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_SCREENTIME", (Object)"ScreenTime");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_SOFTWARE_UPDATE", (Object)"SoftwareUpdate");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_PRIVACY", (Object)"Privacy");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_FILEVAULT", (Object)"FileVault");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_ICLOULD_DIAGNOSTICS", (Object)"iCloudDiagnostics");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_ICLOUD_STORAGE", (Object)"iCloudStorage");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_REGISTRATION", (Object)"Registration");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_TV_SCREENSAVER", (Object)"ScreenSaver");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_TV_TAPTOSETUP", (Object)"TapToSetup");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_TV_HOMESYNC", (Object)"TVHomeScreenSync");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_TV_PROVIDER", (Object)"TVProviderSignIn");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_TV_ROOM", (Object)"TVRoom");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_SIM_SETUP", (Object)"SIMSetup");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_WELCOME", (Object)"Welcome");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_UNLOCK_WITH_WATCH", (Object)"UnlockWithWatch");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_APPSTORE", (Object)"AppStore");
        AppleDEPProfileHandler.skipApiKeysMap.put((Object)"SKIP_TERMS_OF_ADDRESS", (Object)"TermsOfAddress");
    }
    
    public static String getSkipSettingKey(final String setting) {
        if (AppleDEPProfileHandler.skipApiKeysMap == null) {
            initializeSkipSettingKeyMap();
        }
        return (String)((AppleDEPProfileHandler.skipApiKeysMap.get((Object)setting) == null) ? AppleDEPProfileHandler.skipApiKeysMap.getKey((Object)setting) : ((String)AppleDEPProfileHandler.skipApiKeysMap.get((Object)setting)));
    }
    
    public static JSONArray getSkipSettingsArray(final JSONObject profileJson) {
        final JSONArray skipArray = new JSONArray();
        final Iterator keysIt = profileJson.keys();
        while (keysIt.hasNext()) {
            final String eachKey = keysIt.next().toString();
            if (eachKey.startsWith("SKIP_") && !eachKey.equalsIgnoreCase("SKIP_ACC_CREATION")) {
                skipArray.put((Object)getSkipSettingKey(eachKey));
            }
        }
        return skipArray;
    }
    
    static {
        AppleDEPProfileHandler.skipApiKeysMap = null;
        AppleDEPProfileHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
