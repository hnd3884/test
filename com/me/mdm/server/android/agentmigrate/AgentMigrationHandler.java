package com.me.mdm.server.android.agentmigrate;

import java.util.Hashtable;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import org.json.JSONArray;
import java.util.Properties;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.ds.query.Join;
import java.util.Collection;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Logger;

public class AgentMigrationHandler
{
    public static final String AGENT_MIGRATION_ALLOWED = "AGENT_MIGRATION_ALLOWED";
    public static final Integer SAFE_AGENT_MIGRATION_SUPPORTED_VERSION;
    public static final Integer AGENT_MIGRATE_STATUS_COMMAND_NOT_SENT;
    public static final Integer AGENT_MIGRATE_STATUS_IN_PROGRESS;
    public static final Integer AGENT_MIGRATE_STATUS_SUCCESS;
    public static final Integer AGENT_MIGRATE_STATUS_FAILED;
    public static final String AGENT_MIGRATE_STAGE_INITIATED = "Initiated";
    public static final String AGENT_MIGRATE_STAGE_NEWAGENTDOWNLOAD = "NewAgentDownload";
    public static final String AGENT_MIGRATE_STAGE_NEWAGENTINSTALL = "NewAgentInstall";
    public static final String AGENT_MIGRATE_STAGE_DATAMIGRATION_INIT = "MigrationDataSending";
    public static final String AGENT_MIGRATE_STAGE_DATAMIGRATION = "MigrationDataProcessing";
    public static final String AGENT_MIGRATE_STAGE_DEVICEADMINACTIVATION = "DeviceAdminActivation";
    public static final String AGENT_MIGRATE_STAGE_ELM = "ELMActivation";
    public static final String AGENT_MIGRATE_STAGE_GCM = "GCMRegistration";
    public static final Integer AGENT_MIGRATE_OLD_AGENT_STATUS_UNINSTALL_INITIATED;
    public static final Integer AGENT_MIGRATE_OLD_AGENT_STATUS_UNINSTALL_SUCCESS;
    public static final Integer AGENT_MIGRATE_OLD_AGENT_STATUS_UNINSTALL_FAILED;
    public static final Logger LOGGER;
    public static AgentMigrationHandler handler;
    
    public static AgentMigrationHandler getInstance() {
        return AgentMigrationHandler.handler;
    }
    
    public boolean isCapableForMigration(final Long resourceId) throws Exception {
        final Integer status = (Integer)DBUtil.getValueFromDB("AgentMigration", "RESOURCE_ID", (Object)resourceId, "MIGRATION_STATUS");
        return status != null && !status.equals(AgentMigrationHandler.AGENT_MIGRATE_STATUS_SUCCESS);
    }
    
    public boolean isAgentVersionSupportedForMigration(final Long resourceId) throws Exception {
        final Long versionCode = (Long)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceId, "AGENT_VERSION_CODE");
        return versionCode != null && versionCode.intValue() >= AgentMigrationHandler.SAFE_AGENT_MIGRATION_SUPPORTED_VERSION;
    }
    
    public boolean isMigrationAllowed() {
        final String migrationAllowed = MDMUtil.getSyMParameter("AGENT_MIGRATION_ALLOWED");
        return migrationAllowed != null && Boolean.parseBoolean(migrationAllowed);
    }
    
    public void addOrUpdateResourceToAgentMigration(final JSONObject data) {
        try {
            final Long resourceId = data.getLong("RESOURCE_ID");
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AgentMigration"));
            final Criteria resCriteria = new Criteria(new Column("AgentMigration", "RESOURCE_ID"), (Object)resourceId, 0);
            sQuery.addSelectColumn(new Column("AgentMigration", "RESOURCE_ID"));
            sQuery.addSelectColumn(new Column("AgentMigration", "MIGRATION_STATUS"));
            sQuery.addSelectColumn(new Column("AgentMigration", "MIGRATION_STATE"));
            sQuery.addSelectColumn(new Column("AgentMigration", "COMMAND_INITIATED_AT"));
            sQuery.addSelectColumn(new Column("AgentMigration", "OLD_GCM_ID"));
            sQuery.addSelectColumn(new Column("AgentMigration", "STATUS_LAST_POSTED_AT"));
            sQuery.addSelectColumn(new Column("AgentMigration", "REMARKS"));
            sQuery.addSelectColumn(new Column("AgentMigration", "OLD_AGENT_UNINSALL_STATUS"));
            sQuery.setCriteria(resCriteria);
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (dO.isEmpty()) {
                final Row row = new Row("AgentMigration");
                row.set("RESOURCE_ID", (Object)resourceId);
                this.setRowWithAgentMigrationData(row, data);
                dO.addRow(row);
                MDMUtil.getPersistence().add(dO);
            }
            else {
                final Row row = dO.getFirstRow("AgentMigration");
                this.setRowWithAgentMigrationData(row, data);
                dO.updateRow(row);
                MDMUtil.getPersistence().update(dO);
            }
        }
        catch (final Exception e) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception while adding a resource to AgentMigration Table:: Data :{0} {1}", new Object[] { data.toString(), e.getStackTrace() });
        }
    }
    
    private void setRowWithAgentMigrationData(final Row row, final JSONObject data) {
        final Integer status = data.optInt("MIGRATION_STATUS", -2);
        final String state = data.optString("MIGRATION_STATE", "");
        final Long cmdInitiatedAt = data.optLong("COMMAND_INITIATED_AT", -2L);
        final Long lastStatusUpdateTime = data.optLong("STATUS_LAST_POSTED_AT", -2L);
        final String oldGcmId = data.optString("OLD_GCM_ID", "");
        final String remarks = data.optString("REMARKS", "");
        final Integer oldAgentUninstallStatus = data.optInt("OLD_AGENT_UNINSALL_STATUS", -2);
        if (status != -2) {
            row.set("MIGRATION_STATUS", (Object)status);
        }
        if (!state.equals("")) {
            row.set("MIGRATION_STATE", (Object)state);
        }
        if (cmdInitiatedAt != -2L) {
            row.set("COMMAND_INITIATED_AT", (Object)cmdInitiatedAt);
        }
        if (lastStatusUpdateTime != -2L) {
            row.set("STATUS_LAST_POSTED_AT", (Object)lastStatusUpdateTime);
        }
        if (!oldGcmId.equals("")) {
            row.set("OLD_GCM_ID", (Object)oldGcmId);
        }
        if (!remarks.equals("")) {
            row.set("REMARKS", (Object)remarks);
        }
        if (oldAgentUninstallStatus != -2) {
            row.set("OLD_AGENT_UNINSALL_STATUS", (Object)oldAgentUninstallStatus);
        }
    }
    
    public void deleteResourceFromAgentMigration(final Long resourceId) {
        try {
            final Criteria resCriteria = new Criteria(new Column("AgentMigration", "RESOURCE_ID"), (Object)resourceId, 0);
            DataAccess.delete("AgentMigration", resCriteria);
        }
        catch (final Exception e) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception while deleting entry for AgentMigration Table", e);
        }
    }
    
    public void initiateAgentMigrateCommandForAll() {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AgentMigration"));
            sQuery.addSelectColumn(new Column((String)null, "*"));
            sQuery.setCriteria(new Criteria(new Column("AgentMigration", "MIGRATION_STATUS"), (Object)AgentMigrationHandler.AGENT_MIGRATE_STATUS_SUCCESS, 1));
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (!dO.isEmpty()) {
                final Iterator rows = dO.getRows("AgentMigration");
                final List<Long> resourceList = new ArrayList<Long>();
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    resourceList.add((Long)row.get("RESOURCE_ID"));
                }
                this.initiateAgentMigrationForDevice(resourceList);
            }
        }
        catch (final Exception e) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception while Initiating AgentMigrate command {0}", e);
        }
    }
    
    public void initiateAgentMigrationForDevice(final List<Long> resourceList) throws Exception {
        DeviceCommandRepository.getInstance().addSAFEApkMigrateCommand(resourceList);
        NotificationHandler.getInstance().SendNotification(resourceList, 2);
        final JSONObject data = new JSONObject();
        data.put("RESOURCES", (Collection)resourceList);
        data.put("MIGRATION_STATUS", (Object)AgentMigrationHandler.AGENT_MIGRATE_STATUS_IN_PROGRESS);
        data.put("MIGRATION_STATE", (Object)"Initiated");
        data.put("COMMAND_INITIATED_AT", System.currentTimeMillis());
        data.put("STATUS_LAST_POSTED_AT", 0);
        data.put("REMARKS", (Object)"dc.mdm.device_mgmt.safe_migration.status.initiated");
        final Criteria statusNotNull = new Criteria(Column.getColumn("AgentMigration", "MIGRATION_STATUS"), (Object)null, 1);
        final Criteria statusNotSuccess = new Criteria(Column.getColumn("AgentMigration", "MIGRATION_STATUS"), (Object)AgentMigrationHandler.AGENT_MIGRATE_STATUS_SUCCESS, 1);
        final Criteria criteria = statusNotNull.and(statusNotSuccess);
        this.updateAgentMigrationStatusForResources(data, criteria);
    }
    
    public void populateAgentMigration() {
        try {
            AgentMigrationHandler.LOGGER.info("Inside PopulateAgentMigration() Method");
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
            final Join mdAppGroupDetailsJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join migrationTableJoin = new Join("ManagedDevice", "AgentMigration", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1);
            final Criteria versionSCriteria = new Criteria(new Column("ManagedDevice", "AGENT_VERSION"), (Object)"S", 12, false);
            final Criteria agentCriteria = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)3, 0);
            Criteria osCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"2.*", 3);
            osCriteria = osCriteria.and(new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"3.*", 3));
            osCriteria = osCriteria.and(new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.0*", 3));
            osCriteria = osCriteria.and(new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.1.*", 3));
            osCriteria = osCriteria.and(new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.2.*", 3));
            final Criteria migrationCriteria = new Criteria(new Column("AgentMigration", "RESOURCE_ID"), (Object)null, 0);
            final Criteria supportedAgentVersionCriteria = new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION_CODE"), (Object)AgentMigrationHandler.SAFE_AGENT_MIGRATION_SUPPORTED_VERSION, 4);
            final Criteria criteria = osCriteria.and(agentCriteria).and(versionSCriteria).and(migrationCriteria).and(supportedAgentVersionCriteria);
            sQuery.addJoin(mdAppGroupDetailsJoin);
            sQuery.addJoin(migrationTableJoin);
            sQuery.setCriteria(criteria);
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            int count = 0;
            if (!dO.isEmpty()) {
                final Iterator rows = dO.getRows("ManagedDevice");
                while (rows.hasNext()) {
                    final JSONObject data = new JSONObject();
                    final Row row = rows.next();
                    data.put("RESOURCE_ID", (Object)row.get("RESOURCE_ID"));
                    this.addOrUpdateResourceToAgentMigration(data);
                    ++count;
                }
            }
            AgentMigrationHandler.LOGGER.info("Population of AgentMigration Finished");
            AgentMigrationHandler.LOGGER.log(Level.INFO, "Total Capable Devices for Migration are : {0}", count);
        }
        catch (final Exception e) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception while Populating the AgentMigration Table{0}", e);
        }
    }
    
    public void handlePostAgentMigration(final Long resourceId) {
        try {
            AgentMigrationHandler.LOGGER.log(Level.INFO, "Handling Post Agent Migration for Resource :{0}", resourceId);
            final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
            final List resourceList = new ArrayList();
            final JSONObject data = new JSONObject();
            data.put("RESOURCE_ID", (Object)resourceId);
            resourceList.add(resourceId);
            DeviceCommandRepository.getInstance().addUnmanageOldAgentCommand(resourceList);
            data.put("OLD_AGENT_UNINSALL_STATUS", (Object)AgentMigrationHandler.AGENT_MIGRATE_OLD_AGENT_STATUS_UNINSTALL_INITIATED);
            this.addOrUpdateResourceToAgentMigration(data);
            DeviceCommandRepository.getInstance().addSyncAgentSettingsCommand(resourceList, 1);
            DeviceCommandRepository.getInstance().addLanguageLicenseCommand(resourceList, 1);
            DeviceCommandRepository.getInstance().addKNOXAvailabilityCommand(resourceList, "GetKnoxAvailabilityUpgrade");
            this.reAssociateProfilesToMigratedAgent(resourceId);
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
        }
        catch (final Exception e) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception while Handling the Post AgentMigration Actions", e);
        }
    }
    
    private void reAssociateProfilesToMigratedAgent(final Long resourceId) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        final Join profileJoin = new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria profileTypeCriteria = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)1, 0);
        final Criteria profileMarkedForDeleteCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        sQuery.addJoin(profileJoin);
        sQuery.setCriteria(resourceCriteria.and(profileTypeCriteria).and(profileMarkedForDeleteCriteria));
        final DataObject dO = MDMUtil.getPersistence().get(sQuery);
        if (!dO.isEmpty()) {
            final List commandList = new ArrayList();
            final List resourceList = new ArrayList();
            resourceList.add(resourceId);
            final Iterator iter = dO.getRows("RecentProfileForResource");
            while (iter.hasNext()) {
                final Row row = iter.next();
                commandList.add(DBUtil.getValueFromDB("MdCollectionCommand", "COLLECTION_ID", (Object)row.get("COLLECTION_ID"), "COMMAND_ID"));
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, (Long)row.get("COLLECTION_ID"), 12);
            }
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceList);
        }
    }
    
    public Long getCommandIdForCollection(final Long collectionId) {
        try {
            return (Long)DBUtil.getValueFromDB("MdCollectionCommand", "COLLECTION_ID", (Object)collectionId, "COMMAND_ID");
        }
        catch (final Exception e) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception while get command id for collection Table", e);
            return -1L;
        }
    }
    
    public void processAgentMigrationStatus(final HashMap hmap) {
        final String UUID = hmap.get("UDID");
        final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(UUID);
        AgentMigrationHandler.LOGGER.log(Level.INFO, "Processing Migration Status from Agent {0} and Resource Id {1}", new String[] { UUID, resourceId.toString() });
        try {
            final JSONObject msgJSON = new JSONObject((String)hmap.get("Message"));
            final HashMap<String, String> msg = JSONUtil.getInstance().ConvertJSONObjectToHash(msgJSON);
            final Integer agentMigrationStatus = Integer.parseInt(msg.get("MigrationStatus"));
            final String agentMigrationState = msg.get("MigrationStage");
            final String remarks = msg.get("Remarks");
            final JSONObject data = new JSONObject();
            data.put("RESOURCE_ID", (Object)resourceId);
            data.put("MIGRATION_STATUS", (Object)agentMigrationStatus);
            data.put("MIGRATION_STATE", (Object)agentMigrationState);
            data.put("REMARKS", (Object)this.getRemarksForAgentMigrationState(agentMigrationState, agentMigrationStatus));
            data.put("STATUS_LAST_POSTED_AT", System.currentTimeMillis());
            AgentMigrationHandler.LOGGER.log(Level.INFO, "Going to Update Resource Id :{0} Data {1}", new Object[] { resourceId, data });
            this.addOrUpdateResourceToAgentMigration(data);
            if (agentMigrationStatus == AgentMigrationHandler.AGENT_MIGRATE_STATUS_SUCCESS) {
                final String registrationID = msg.get("RegistrationID");
                final String versionName = msg.get("AgentVersion");
                final Integer versionCode = Integer.parseInt(msg.get("AgentVersionCode"));
                final JSONObject hAndroidCommMap = new JSONObject();
                hAndroidCommMap.put("NOTIFICATION_TOKEN_ENCRYPTED", (Object)registrationID);
                PushNotificationHandler.getInstance().addOrUpdateManagedIdToNotificationRel(resourceId, 2, hAndroidCommMap);
                final Properties deviceData = new Properties();
                ((Hashtable<String, Long>)deviceData).put("RESOURCE_ID", resourceId);
                ((Hashtable<String, String>)deviceData).put("AGENT_VERSION", versionName);
                ((Hashtable<String, Long>)deviceData).put("AGENT_VERSION_CODE", (long)versionCode);
                ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(deviceData);
                this.handlePostAgentMigration(resourceId);
            }
            else if (agentMigrationStatus == AgentMigrationHandler.AGENT_MIGRATE_STATUS_FAILED) {
                final List resList = new ArrayList();
                resList.add(resourceId);
            }
            else if (agentMigrationStatus == AgentMigrationHandler.AGENT_MIGRATE_STATUS_IN_PROGRESS && agentMigrationState != null && agentMigrationState.equalsIgnoreCase("ELMActivation")) {
                this.backupOldGCMId(resourceId);
            }
        }
        catch (final Exception ex) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception while Processing Agent Migrate status ", ex);
        }
    }
    
    private void backupOldGCMId(final Long resourceId) {
        try {
            final String gcmId = (String)DBUtil.getValueFromDB("IOSDeviceCommDetails", "RESOURCE_ID", (Object)resourceId, "PUSH_MAGIC");
            final JSONObject data = new JSONObject();
            data.put("RESOURCE_ID", (Object)resourceId);
            data.put("OLD_GCM_ID", (Object)gcmId);
            this.addOrUpdateResourceToAgentMigration(data);
        }
        catch (final Exception ex) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception while Backup Old GCM ID ", ex);
        }
    }
    
    public void handleUnamanageOldAgentResponse(final HashMap map) {
        try {
            final String strUDID = map.get("UDID");
            final String strStatus = map.get("Status");
            final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            final JSONObject data = new JSONObject();
            data.put("RESOURCE_ID", (Object)resourceId);
            if (strStatus.equalsIgnoreCase("Acknowledged")) {
                data.put("OLD_AGENT_UNINSALL_STATUS", (Object)AgentMigrationHandler.AGENT_MIGRATE_OLD_AGENT_STATUS_UNINSTALL_SUCCESS);
            }
            else {
                data.put("OLD_AGENT_UNINSALL_STATUS", (Object)AgentMigrationHandler.AGENT_MIGRATE_OLD_AGENT_STATUS_UNINSTALL_FAILED);
            }
            this.addOrUpdateResourceToAgentMigration(data);
        }
        catch (final Exception e) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception while Handling the UnmanageOldAgent Command response", e);
        }
    }
    
    public int getCount(final Integer status, final String stage) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AgentMigration"));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        Criteria criteria = null;
        if (status != null) {
            criteria = new Criteria(new Column("AgentMigration", "MIGRATION_STATUS"), (Object)status, 0);
        }
        if (stage != null) {
            if (criteria != null) {
                criteria.and(new Criteria(new Column("AgentMigration", "MIGRATION_STATE"), (Object)stage, 0, false));
            }
            else {
                criteria = new Criteria(new Column("AgentMigration", "MIGRATION_STATE"), (Object)stage, 0, false);
            }
        }
        try {
            return DBUtil.getRecordCount("AgentMigration", "RESOURCE_ID", criteria);
        }
        catch (final Exception ex) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception while retriving count for Status :{0} Stage : {1} Exception :", new Object[] { status, stage, ex });
            return -1;
        }
    }
    
    public int getYetToUpgradeDeviceCount() {
        int count = -1;
        try {
            final Criteria notSuccessCriteria = new Criteria(Column.getColumn("AgentMigration", "MIGRATION_STATUS"), (Object)AgentMigrationHandler.AGENT_MIGRATE_STATUS_SUCCESS, 1);
            count = DBUtil.getRecordCount("AgentMigration", "RESOURCE_ID", notSuccessCriteria);
        }
        catch (final Exception ex) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception occurred while getYetToUpgradeDeviceCount", ex);
        }
        return count;
    }
    
    public void updateAgentMigrationStatusForResources(final JSONObject data, final Criteria crit) {
        AgentMigrationHandler.LOGGER.log(Level.INFO, " Update Data : {0}", data);
        try {
            final Integer status = data.optInt("MIGRATION_STATUS", -2);
            final String state = data.optString("MIGRATION_STATE", "");
            final Long cmdInitiatedAt = data.optLong("COMMAND_INITIATED_AT", -2L);
            final Long lastStatusUpdateTime = data.optLong("STATUS_LAST_POSTED_AT", -2L);
            final String remarks = data.optString("REMARKS", "");
            final List<Long> resourceList = new ArrayList<Long>();
            final JSONArray resLis = (JSONArray)data.get("RESOURCES");
            for (int i = 0; i < resLis.length(); ++i) {
                resourceList.add(new Long(String.valueOf(resLis.get(i))));
            }
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AgentMigration");
            updateQuery.setUpdateColumn("MIGRATION_STATUS", (Object)status);
            updateQuery.setUpdateColumn("MIGRATION_STATE", (Object)state);
            updateQuery.setUpdateColumn("COMMAND_INITIATED_AT", (Object)cmdInitiatedAt);
            updateQuery.setUpdateColumn("STATUS_LAST_POSTED_AT", (Object)lastStatusUpdateTime);
            updateQuery.setUpdateColumn("REMARKS", (Object)remarks);
            Criteria criteria;
            final Criteria resoruceCriteria = criteria = new Criteria(Column.getColumn("AgentMigration", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            if (crit != null) {
                criteria = criteria.and(crit);
            }
            updateQuery.setCriteria(criteria);
            MDMUtil.getPersistence().update(updateQuery);
            AgentMigrationHandler.LOGGER.log(Level.INFO, "Update migration status succeeded");
        }
        catch (final Exception ex) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception occurred while updateAgentMigrationStatusForResources ", ex);
        }
    }
    
    private String getRemarksForAgentMigrationState(final String state, final int status) {
        return (status == AgentMigrationHandler.AGENT_MIGRATE_STATUS_FAILED) ? this.getMigrationFailureStateRemarks(state) : this.getMigrationSuccessStatesRemrks(state);
    }
    
    private String getMigrationSuccessStatesRemrks(final String state) {
        String remarks = "";
        if (state.equalsIgnoreCase("Initiated")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.initiated";
        }
        else if (state.equalsIgnoreCase("NewAgentDownload")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.downloading_app";
        }
        else if (state.equalsIgnoreCase("NewAgentInstall")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.installing_app";
        }
        else if (state.equalsIgnoreCase("MigrationDataSending")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.data_migration";
        }
        else if (state.equalsIgnoreCase("MigrationDataProcessing")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.data_processing";
        }
        else if (state.equalsIgnoreCase("ELMActivation")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.elm_activation";
        }
        else if (state.equalsIgnoreCase("DeviceAdminActivation")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.device_admin_activated";
        }
        else if (state.equalsIgnoreCase("GCMRegistration")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.succeeded";
        }
        return remarks;
    }
    
    private String getMigrationFailureStateRemarks(final String state) {
        String remarks = "";
        if (state.equalsIgnoreCase("NewAgentDownload")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.failed_downlod";
        }
        else if (state.equalsIgnoreCase("NewAgentInstall")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.failed_install";
        }
        else if (state.equalsIgnoreCase("MigrationDataSending")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.failed_data_migration";
        }
        else if (state.equalsIgnoreCase("MigrationDataProcessing")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.failed_data_process";
        }
        else if (state.equalsIgnoreCase("ELMActivation")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.failed_elm_activation";
        }
        else if (state.equalsIgnoreCase("GCMRegistration")) {
            remarks = "dc.mdm.device_mgmt.safe_migration.status.failed_gcm_registration";
        }
        return remarks;
    }
    
    public void handleSAFEMigrationDevicesVersionCode() {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
            final Join mddeviceInfoJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            sQuery.addJoin(mddeviceInfoJoin);
            final Criteria unknownAgentVersionCriteria = new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION_CODE"), (Object)(-1), 0);
            sQuery.setCriteria(unknownAgentVersionCriteria);
            sQuery.addSelectColumn(new Column("ManagedDevice", "*"));
            final DataObject deviceDO = MDMUtil.getPersistence().get(sQuery);
            if (deviceDO != null && !deviceDO.isEmpty()) {
                final Iterator deviceIterator = deviceDO.getRows("ManagedDevice");
                while (deviceIterator.hasNext()) {
                    final Row row = deviceIterator.next();
                    final String agentVersion = (String)row.get("AGENT_VERSION");
                    if (agentVersion != null && agentVersion.contains(".")) {
                        final String[] versionSplit = agentVersion.split("\\.");
                        row.set("AGENT_VERSION_CODE", (Object)Long.valueOf(versionSplit[2]));
                        deviceDO.updateRow(row);
                    }
                }
                MDMUtil.getPersistence().update(deviceDO);
            }
        }
        catch (final Exception ex) {
            AgentMigrationHandler.LOGGER.log(Level.WARNING, "Exception occurred while handleSAFEMigrationDevicesVersionCode", ex);
        }
    }
    
    static {
        SAFE_AGENT_MIGRATION_SUPPORTED_VERSION = 110;
        AGENT_MIGRATE_STATUS_COMMAND_NOT_SENT = -1;
        AGENT_MIGRATE_STATUS_IN_PROGRESS = 1;
        AGENT_MIGRATE_STATUS_SUCCESS = 0;
        AGENT_MIGRATE_STATUS_FAILED = 2;
        AGENT_MIGRATE_OLD_AGENT_STATUS_UNINSTALL_INITIATED = 1;
        AGENT_MIGRATE_OLD_AGENT_STATUS_UNINSTALL_SUCCESS = 2;
        AGENT_MIGRATE_OLD_AGENT_STATUS_UNINSTALL_FAILED = 3;
        LOGGER = Logger.getLogger("MDMLogger");
        AgentMigrationHandler.handler = new AgentMigrationHandler();
    }
}
