package com.me.mdm.server.deployment;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.apps.AppsLicensesHandler;
import com.adventnet.sym.server.mdm.apps.AppsLicensesHandlerEvent;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.deployment.app.noui.AppDeploymentPolicyNonUiManager;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Collection;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Properties;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.util.List;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DeplymentConfigHandler
{
    static final Logger LOGGER;
    
    private Long persistDeploymentConfig(final JSONObject dataJSON, final boolean single) throws Exception {
        DeplymentConfigHandler.LOGGER.log(Level.INFO, "In persistSingleDeploymentConfig, Going to process {0}, isSingle : {1}", new Object[] { dataJSON, single });
        final Long deploymentConfigId = this.addOrUpdateDeploymentConfig(dataJSON);
        DeplymentConfigHandler.LOGGER.log(Level.INFO, "deploymentConfigId {0}", new Object[] { String.valueOf(deploymentConfigId) });
        final DeploymentPolicyHandler policyHandler = new DeploymentPolicyHandler();
        try {
            if (single) {
                final Long depPolicyID = policyHandler.persistDeploymentPolicies(dataJSON.getJSONObject("DeploymentPolicy"), deploymentConfigId);
                DeplymentConfigHandler.LOGGER.log(Level.INFO, "depPolicyID {0}", new Object[] { String.valueOf(depPolicyID) });
                return depPolicyID;
            }
            final JSONArray deploymentPolicies = dataJSON.getJSONArray("DeploymentPolicy");
            for (int i = 0; i < deploymentPolicies.length(); ++i) {
                policyHandler.persistDeploymentPolicies(deploymentPolicies.getJSONObject(i), deploymentConfigId);
            }
        }
        catch (final JSONException ex) {
            DeplymentConfigHandler.LOGGER.log(Level.SEVERE, null, (Throwable)ex);
            throw new IllegalArgumentException("Mandatory keys missing " + ex.getMessage(), ex.getCause());
        }
        return deploymentConfigId;
    }
    
    public Long persistDeploymentConfig(final JSONObject dataJSON) throws Exception {
        return this.persistDeploymentConfig(dataJSON, false);
    }
    
    public Long persistSingleDeploymentConfig(final JSONObject dataJSON) throws Exception {
        return this.persistDeploymentConfig(dataJSON, true);
    }
    
    private Long addOrUpdateDeploymentConfig(final JSONObject dataJSON) throws DataAccessException {
        try {
            Long deploymentConfigId = dataJSON.optLong("DEPLOYMENT_CONFIG_ID", -1L);
            final Long customerId = dataJSON.getLong("CUSTOMER_ID");
            final String configName = String.valueOf(dataJSON.get("DEPLOYMENT_CONFIG_NAME"));
            final String description = dataJSON.optString("DEPLOYMENT_CONFIG_DESCRIPTION", "--");
            final Long modifiedBy = dataJSON.getLong("LAST_MODIFIED_BY");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeploymentConfig"));
            final Criteria depConfIdCriteria = new Criteria(new Column("DeploymentConfig", "DEPLOYMENT_CONFIG_ID"), (Object)deploymentConfigId, 0);
            selectQuery.setCriteria(depConfIdCriteria);
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            DataObject dO = DataAccess.get(selectQuery);
            final long currentTime = MDMUtil.getCurrentTimeInMillis();
            if (dO.isEmpty()) {
                final Row row = new Row("DeploymentConfig");
                row.set("DEPLOYMENT_CONFIG_NAME", (Object)configName);
                row.set("DEPLOYMENT_CONFIG_DESCRIPTION", (Object)description);
                row.set("CUSTOMER_ID", (Object)customerId);
                row.set("CREATED_BY", (Object)modifiedBy);
                row.set("CREATION_TIME", (Object)currentTime);
                row.set("LAST_MODIFIED_BY", (Object)modifiedBy);
                row.set("LAST_MODIFIED_TIME", (Object)currentTime);
                dO.addRow(row);
            }
            else {
                final Row row = dO.getRow("DeploymentConfig");
                row.set("DEPLOYMENT_CONFIG_NAME", (Object)configName);
                row.set("DEPLOYMENT_CONFIG_DESCRIPTION", (Object)description);
                row.set("CUSTOMER_ID", (Object)customerId);
                row.set("LAST_MODIFIED_BY", (Object)modifiedBy);
                row.set("LAST_MODIFIED_TIME", (Object)currentTime);
                dO.updateRow(row);
            }
            dO = DataAccess.update(dO);
            deploymentConfigId = (Long)dO.getRow("DeploymentConfig").get("DEPLOYMENT_CONFIG_ID");
            return deploymentConfigId;
        }
        catch (final JSONException ex) {
            DeplymentConfigHandler.LOGGER.log(Level.SEVERE, null, (Throwable)ex);
            throw new IllegalArgumentException("Mandatory keys missing " + ex.getMessage(), ex.getCause());
        }
    }
    
    public JSONObject getDeploymentConfig(final Long deploymentConfigId) throws DataAccessException, JSONException, Exception {
        final JSONObject deploymentData = new JSONObject();
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeploymentConfig"));
        Criteria depConfIdCriteria = new Criteria(new Column("DeploymentConfig", "DEPLOYMENT_CONFIG_ID"), (Object)deploymentConfigId, 0);
        selectQuery.setCriteria(depConfIdCriteria);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        DataObject dO = DataAccess.get(selectQuery);
        if (dO.isEmpty()) {
            return null;
        }
        final Row row = dO.getRow("DeploymentConfig");
        deploymentData.put("DEPLOYMENT_CONFIG_ID", row.get("DEPLOYMENT_CONFIG_ID"));
        deploymentData.put("DEPLOYMENT_CONFIG_NAME", row.get("DEPLOYMENT_CONFIG_NAME"));
        deploymentData.put("DEPLOYMENT_CONFIG_DESCRIPTION", row.get("DEPLOYMENT_CONFIG_DESCRIPTION"));
        deploymentData.put("LAST_MODIFIED_BY", row.get("LAST_MODIFIED_BY"));
        deploymentData.put("CREATED_BY", row.get("CREATED_BY"));
        deploymentData.put("CREATION_TIME", row.get("CREATION_TIME"));
        deploymentData.put("LAST_MODIFIED_TIME", row.get("LAST_MODIFIED_TIME"));
        selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeploymentPolicy"));
        depConfIdCriteria = new Criteria(new Column("DeploymentPolicy", "DEPLOYMENT_CONFIG_ID"), (Object)deploymentConfigId, 0);
        selectQuery.setCriteria(depConfIdCriteria);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        dO = DataAccess.get(selectQuery);
        final JSONArray policyArray = new JSONArray();
        final DeploymentPolicyHandler policyHandler = new DeploymentPolicyHandler();
        if (!dO.isEmpty()) {
            final Iterator rowIter = dO.getRows("DeploymentPolicy");
            while (rowIter.hasNext()) {
                final Row depPolicyRow = rowIter.next();
                final JSONObject deploymentPolicyJSON = new JSONObject();
                final Long depPolicyId = (Long)depPolicyRow.get("DEPLOYMENT_POLICY_ID");
                final Integer depPolicyTypeId = (Integer)depPolicyRow.get("DEPLOYMENT_POLICY_TYPE_ID");
                deploymentPolicyJSON.put("DEPLOYMENT_POLICY_ID", (Object)depPolicyId);
                deploymentPolicyJSON.put("DEPLOYMENT_POLICY_TYPE", depPolicyRow.get("DEPLOYMENT_POLICY_TYPE"));
                deploymentPolicyJSON.put("DEPLOYMENT_POLICY_TYPE_ID", (Object)depPolicyTypeId);
                deploymentPolicyJSON.put("PolicyDetails", (Object)policyHandler.getDeploymentPolicyDetails(depPolicyId, depPolicyTypeId));
                policyArray.put((Object)deploymentPolicyJSON);
            }
        }
        deploymentData.put("DeploymentPolicy", (Object)policyArray);
        return deploymentData;
    }
    
    public JSONObject getEffectiveDeploymentConfig(final Long resourceId, final Long profileId) throws DataAccessException, Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMResourceToDeploymentConfigs"));
        final Join depConfigJoin = new Join("MDMResourceToDeploymentConfigs", "DeploymentConfig", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2);
        final Join depPolicyJoin = new Join("MDMResourceToDeploymentConfigs", "DeploymentPolicy", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2);
        final Criteria resourceCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria profileCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileId, 0);
        sQuery.addJoin(depConfigJoin);
        sQuery.addJoin(depPolicyJoin);
        sQuery.setCriteria(resourceCriteria.and(profileCriteria));
        final Column depTypeColumn = Column.getColumn("DeploymentPolicy", "DEPLOYMENT_POLICY_TYPE_ID");
        final List groupByColList = new ArrayList();
        groupByColList.add(depTypeColumn);
        final GroupByClause groupBy = new GroupByClause(groupByColList);
        sQuery.setGroupByClause(groupBy);
        sQuery.addSelectColumn(depTypeColumn);
        final JSONArray policyArray = new JSONArray();
        final DeploymentPolicyHandler policyHandler = new DeploymentPolicyHandler();
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection connection = null;
        DataSet ds = null;
        try {
            connection = relapi.getConnection();
            ds = relapi.executeQuery((Query)sQuery, connection);
            while (ds.next()) {
                final int depConfigType = (int)ds.getValue("DEPLOYMENT_POLICY_TYPE_ID");
                final JSONObject deploymentPolicyJSON = policyHandler.getEffectiveDeploymentPolicyDetails(resourceId, profileId, depConfigType);
                policyArray.put((Object)deploymentPolicyJSON);
            }
        }
        catch (final Exception ex) {
            DeplymentConfigHandler.LOGGER.log(Level.SEVERE, null, ex);
        }
        finally {
            if (connection != null) {
                connection.close();
            }
            if (ds != null) {
                ds.close();
            }
        }
        if (policyArray.length() < 1) {
            return null;
        }
        final JSONObject deploymentData = new JSONObject();
        deploymentData.put("DeploymentPolicy", (Object)policyArray);
        return deploymentData;
    }
    
    public void updateDeploymentSettingsForApp(final Properties prop) throws Exception {
        final Boolean isApp = ((Hashtable<K, Boolean>)prop).get("isAppConfig");
        final String chunkSizeStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("profile_chunk_size");
        final int chunckSize = (chunkSizeStr == null) ? 500 : Integer.parseInt(chunkSizeStr);
        final Long customerID = ((Hashtable<K, Long>)prop).get("customerId");
        if (isApp) {
            AppsUtil.getInstance().addOrUpdateAppSettings(prop);
            final List resourceList = new ArrayList(((Hashtable<K, List>)prop).get("resourceList"));
            final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)prop).get("profileCollectionMap");
            final Properties profileToBusinessStore = ((Hashtable<K, Properties>)prop).get("profileToBusinessStore");
            if (profileToBusinessStore != null && !profileToBusinessStore.isEmpty()) {
                this.removeExistingLicenseAssociationsForApps(prop, customerID);
            }
            final ArrayList profileList = new ArrayList(profileCollectionMap.keySet());
            final JSONObject depDetails = new JSONObject();
            depDetails.put("EMAIL_NOTIFY_END_USER", (Object)((Hashtable<K, Boolean>)prop).get("isNotify"));
            depDetails.put("FORCE_APP_INSTALL", (Object)((Hashtable<K, Boolean>)prop).get("isSilentInstall"));
            depDetails.put("DO_NOT_UNINSTALL", (Object)((Hashtable<K, Boolean>)prop).get("doNotUninstall"));
            depDetails.put("SEND_ENROLLMENT_REQUEST", (Object)((Hashtable<K, Boolean>)prop).get("sendEnrollmentRequest"));
            Long userID = ((Hashtable<K, Long>)prop).get("loggedOnUser");
            if (userID == null) {
                userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            }
            final Long deploymentConfigId = new AppDeploymentPolicyNonUiManager().getDeploymentConfigIdForAppDeployment(((Hashtable<K, Long>)prop).get("customerId"), userID, depDetails);
            DeplymentConfigHandler.LOGGER.log(Level.INFO, "ResourceList {0}, ProfileList {1} - DeploymentConfigID {2}", new Object[] { resourceList, profileList, deploymentConfigId });
            final Boolean isGroup = ((Hashtable<K, Boolean>)prop).get("isGroup");
            if (isGroup) {
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution") && MDMGroupHandler.getInstance().isInCycle(resourceList)) {
                    return;
                }
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution")) {
                    try {
                        final List<Long> subGroups = MDMGroupHandler.getInstance().getSubGroupList(resourceList);
                        if (subGroups != null) {
                            resourceList.addAll(subGroups);
                        }
                    }
                    catch (final SQLException | QueryConstructionException e) {
                        throw new SyMException(120001, "SubGroup detection failed", (Throwable)null);
                    }
                }
                final List<Long> userGroupList = new ArrayList<Long>(resourceList);
                final List<Long> deviceGroupList = new ArrayList<Long>(resourceList);
                final List existingUserGroupList = MDMGroupHandler.getInstance().getMDMUserGroupList();
                if (existingUserGroupList != null && !existingUserGroupList.isEmpty()) {
                    userGroupList.retainAll(existingUserGroupList);
                    deviceGroupList.removeAll(existingUserGroupList);
                    if (!userGroupList.isEmpty()) {
                        for (final Long groupId : userGroupList) {
                            final List dummyList = new ArrayList();
                            dummyList.add(groupId);
                            new MDMResourceToProfileDeploymentConfigHandler().persistMDMResourceToProfileDeploymentConfig(dummyList, profileList, deploymentConfigId, MDMResourceToProfileDeploymentConfigHandler.USER_GROUP_DEP_CONFIG_SOURCE, null, profileToBusinessStore);
                            final List userMemberList = MDMGroupHandler.getMemberIdListForGroups(dummyList, 2);
                            new MDMResourceToProfileDeploymentConfigHandler().persistMDMResourceToProfileDeploymentConfig(userMemberList, profileList, deploymentConfigId, MDMResourceToProfileDeploymentConfigHandler.USER_GROUP_DEP_CONFIG_SOURCE, groupId, profileToBusinessStore);
                            final List managedDeviceList = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userMemberList);
                            final Iterator devicesListIterator = MDMUtil.getInstance().splitListIntoSubLists(managedDeviceList, chunckSize).iterator();
                            while (devicesListIterator.hasNext()) {
                                new MDMResourceToProfileDeploymentConfigHandler().persistMDMResourceToProfileDeploymentConfig(devicesListIterator.next(), profileList, deploymentConfigId, MDMResourceToProfileDeploymentConfigHandler.USER_GROUP_DEP_CONFIG_SOURCE, groupId, profileToBusinessStore);
                            }
                        }
                    }
                }
                if (!deviceGroupList.isEmpty()) {
                    for (final Long groupId : deviceGroupList) {
                        final List dummyList = new ArrayList();
                        dummyList.add(groupId);
                        new MDMResourceToProfileDeploymentConfigHandler().persistMDMResourceToProfileDeploymentConfig(dummyList, profileList, deploymentConfigId, MDMResourceToProfileDeploymentConfigHandler.DEVICE_GROUP_DEP_CONFIG_SOURCE, null, profileToBusinessStore);
                        final List<Integer> list = new ArrayList<Integer>();
                        list.add(120);
                        list.add(121);
                        final List groupDeviceList = MDMGroupHandler.getMemberIdListForGroups(dummyList, list);
                        final List groupDeviceSplitList = MDMUtil.getInstance().splitListIntoSubLists(groupDeviceList, chunckSize);
                        final Iterator groupDeviceSplitIterator = groupDeviceSplitList.iterator();
                        while (groupDeviceSplitIterator.hasNext()) {
                            new MDMResourceToProfileDeploymentConfigHandler().persistMDMResourceToProfileDeploymentConfig(groupDeviceSplitIterator.next(), profileList, deploymentConfigId, MDMResourceToProfileDeploymentConfigHandler.DEVICE_GROUP_DEP_CONFIG_SOURCE, groupId, profileToBusinessStore);
                        }
                    }
                }
            }
            else if (resourceList != null && !resourceList.isEmpty()) {
                final List resSplitList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, 500);
                for (final List tempResList : resSplitList) {
                    new MDMResourceToProfileDeploymentConfigHandler().persistMDMResourceToProfileDeploymentConfig(tempResList, profileList, deploymentConfigId, MDMResourceToProfileDeploymentConfigHandler.DEVICE_DEP_CONFIG_SOURCE, null, profileToBusinessStore);
                    final List userList = ManagedUserHandler.getInstance().getManagedUserListFromResList(tempResList);
                    if (userList != null && !userList.isEmpty()) {
                        for (final Long tempUserID : userList) {
                            final List dummyUserList = new ArrayList();
                            dummyUserList.add(tempUserID);
                            final List managedDeviceList2 = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(dummyUserList);
                            new MDMResourceToProfileDeploymentConfigHandler().persistMDMResourceToProfileDeploymentConfig(managedDeviceList2, profileList, deploymentConfigId, MDMResourceToProfileDeploymentConfigHandler.USER_DEP_CONFIG_SOURCE, tempUserID, profileToBusinessStore);
                        }
                    }
                }
            }
        }
    }
    
    public Long getDeploymentPolicyID(final Long customerId, final Boolean forceInstall, final Boolean notify, final Boolean doNotUninstall, final Boolean sendEnrollmentRequest) {
        Long depConfigID = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeploymentConfig"));
            final Join appDeployJoin = new Join("DeploymentConfig", "AppDeploymentPolicy", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 2);
            sQuery.addJoin(appDeployJoin);
            final Criteria silentInstallCriteria = new Criteria(new Column("AppDeploymentPolicy", "FORCE_APP_INSTALL"), (Object)forceInstall, 0);
            final Criteria notifyCriteria = new Criteria(new Column("AppDeploymentPolicy", "EMAIL_NOTIFY_END_USER"), (Object)notify, 0);
            final Criteria uninstallCriteria = new Criteria(new Column("AppDeploymentPolicy", "DO_NOT_UNINSTALL"), (Object)doNotUninstall, 0);
            final Criteria sendEnrollmentRequestCriteria = new Criteria(new Column("AppDeploymentPolicy", "SEND_ENROLLMENT_REQUEST"), (Object)sendEnrollmentRequest, 0);
            final Criteria customerIdCriteria = new Criteria(new Column("DeploymentConfig", "CUSTOMER_ID"), (Object)customerId, 0);
            sQuery.setCriteria(customerIdCriteria.and(silentInstallCriteria).and(notifyCriteria).and(uninstallCriteria).and(sendEnrollmentRequestCriteria));
            sQuery.addSelectColumn(Column.getColumn("DeploymentConfig", "DEPLOYMENT_CONFIG_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sQuery);
            if (!dataObject.isEmpty()) {
                final Row depPolicyRow = dataObject.getFirstRow("DeploymentConfig");
                depConfigID = (Long)depPolicyRow.get("DEPLOYMENT_CONFIG_ID");
            }
        }
        catch (final Exception e) {
            DeplymentConfigHandler.LOGGER.log(Level.SEVERE, "Exception in getDeploymentPolicyID", e);
        }
        return depConfigID;
    }
    
    private void removeExistingLicenseAssociationsForApps(final Properties prop, final Long customerID) {
        try {
            final Boolean isGroup = ((Hashtable<K, Boolean>)prop).getOrDefault("isGroup", false);
            final List resourceList = new ArrayList(((Hashtable<K, List>)prop).get("resourceList"));
            if (isGroup) {
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution") && MDMGroupHandler.getInstance().isInCycle(resourceList)) {
                    return;
                }
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution")) {
                    try {
                        final List<Long> subGroups = MDMGroupHandler.getInstance().getSubGroupList(resourceList);
                        if (subGroups != null) {
                            resourceList.addAll(subGroups);
                        }
                    }
                    catch (final SQLException | QueryConstructionException e) {
                        throw new SyMException(120001, "SubGroup detection failed", (Throwable)null);
                    }
                }
            }
            Properties profileToBusinessStore = new Properties();
            if (prop.containsKey("profileToBusinessStore")) {
                profileToBusinessStore = ((Hashtable<K, Properties>)prop).get("profileToBusinessStore");
            }
            final Properties licenseDetails = new Properties();
            ((Hashtable<String, List>)licenseDetails).put("configSourceList", resourceList);
            ((Hashtable<String, Properties>)licenseDetails).put("profileToBusinessStore", profileToBusinessStore);
            final AppsLicensesHandlerEvent appsLicensesHandlerEvent = new AppsLicensesHandlerEvent(customerID, licenseDetails);
            AppsLicensesHandler.getInstance().invokeLicenseHandlingListener(appsLicensesHandlerEvent, 2);
        }
        catch (final Exception e2) {
            DeplymentConfigHandler.LOGGER.log(Level.SEVERE, "Exception in removeExistingLicenseAssociationsForApps", e2);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
