package com.me.mdm.server.deployment;

import java.util.Hashtable;
import com.adventnet.ds.query.SortColumn;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Iterator;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Properties;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class MDMResourceToProfileDeploymentConfigHandler
{
    static final Logger LOGGER;
    public static final Integer DEVICE_GROUP_DEP_CONFIG_SOURCE;
    public static final Integer USER_GROUP_DEP_CONFIG_SOURCE;
    public static final Integer DEVICE_DEP_CONFIG_SOURCE;
    public static final Integer USER_DEP_CONFIG_SOURCE;
    
    public void addOrUpdateMDMResourceToProfileDeployment(final Long resourceId, final Long profileId, final Long deploymentConfigId, final int source, final Long sourceID, final Long businessStoreID) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMResourceToDeploymentConfigs"));
        final Criteria resourceCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria profileCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria sourceIDCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)sourceID, 0);
        sQuery.setCriteria(resourceCriteria.and(profileCriteria).and(sourceIDCriteria));
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            final Row row = new Row("MDMResourceToDeploymentConfigs");
            row.set("RESOURCE_ID", (Object)resourceId);
            row.set("PROFILE_ID", (Object)profileId);
            row.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
            row.set("CONFIG_SOURCE_ID", (Object)sourceID);
            row.set("DEPLOYMENT_CONFIG_SOURCE", (Object)source);
            row.set("BUSINESSSTORE_ID", (Object)businessStoreID);
            row.set("ADDED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            dO.addRow(row);
        }
        else {
            final Row row = dO.getRow("MDMResourceToDeploymentConfigs");
            row.set("ADDED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            row.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
            row.set("BUSINESSSTORE_ID", (Object)businessStoreID);
            dO.updateRow(row);
        }
        DataAccess.update(dO);
        MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "MDMResourceToProfileDeploymentConfigHandler.addOrUpdateMDMResourceToProfileDeployment processed for Resource Id : {0}, Profile Id {1}, DeploymentConfigId {2}", new Object[] { resourceId, profileId, deploymentConfigId });
    }
    
    public void persistMDMResourceToProfileDeploymentConfig(final List<Long> resourceList, final List<Long> profileList, final Long deploymentConfigId, final int source, final Long sourceId, final Properties profileToBusinessStore) throws Exception {
        MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "persistMDMResourceToProfileDeploymentConfig for Resource list {0}, profile list{1}, deploymentconfig Id {2}", new Object[] { resourceList, profileList, deploymentConfigId });
        final long currentTime = MDMUtil.getCurrentTimeInMillis();
        final Criteria resourceCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final Criteria profileCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileList.toArray(), 8);
        final Criteria sourceCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)sourceId, 0);
        if (sourceId != null) {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MDMResourceToDeploymentConfigs");
            updateQuery.setCriteria(resourceCriteria.and(profileCriteria).and(sourceCriteria));
            updateQuery.setUpdateColumn("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
            updateQuery.setUpdateColumn("ADDED_TIME", (Object)currentTime);
            MDMUtil.getPersistence().update(updateQuery);
            final DataObject dataObject = (DataObject)new WritableDataObject();
            for (final Long profileId : profileList) {
                final List<Long> dummyResourceList = new ArrayList<Long>(resourceList);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
                final Criteria indivprofileCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileId, 0);
                selectQuery.setCriteria(resourceCriteria.and(indivprofileCriteria).and(sourceCriteria));
                selectQuery.addSelectColumn(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"));
                final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
                while (dmDataSetWrapper.next()) {
                    dummyResourceList.remove(dmDataSetWrapper.getValue("RESOURCE_ID"));
                }
                for (final Long resourceId : dummyResourceList) {
                    final Row row = new Row("MDMResourceToDeploymentConfigs");
                    row.set("RESOURCE_ID", (Object)resourceId);
                    row.set("PROFILE_ID", (Object)profileId);
                    row.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
                    row.set("DEPLOYMENT_CONFIG_SOURCE", (Object)source);
                    row.set("CONFIG_SOURCE_ID", (Object)sourceId);
                    if (profileToBusinessStore != null && !profileToBusinessStore.isEmpty()) {
                        row.set("BUSINESSSTORE_ID", ((Hashtable<K, Object>)profileToBusinessStore).get(profileId));
                    }
                    else {
                        row.set("BUSINESSSTORE_ID", (Object)null);
                    }
                    row.set("ADDED_TIME", (Object)currentTime);
                    dataObject.addRow(row);
                }
            }
            MDMUtil.getPersistence().add(dataObject);
        }
        else {
            final DataObject dataObject2 = (DataObject)new WritableDataObject();
            for (final Long profileId2 : profileList) {
                final List<Long> dummyResourceList = new ArrayList<Long>(resourceList);
                final List<Long> existingResourceList = new ArrayList<Long>();
                final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
                final Criteria sameSourceCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), 0);
                final Criteria indivprofileCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileId2, 0);
                selectQuery2.setCriteria(resourceCriteria.and(indivprofileCriteria).and(sameSourceCriteria));
                selectQuery2.addSelectColumn(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"));
                final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery2);
                while (dmDataSetWrapper.next()) {
                    final Long resourceId2 = (Long)dmDataSetWrapper.getValue("RESOURCE_ID");
                    dummyResourceList.remove(resourceId2);
                    existingResourceList.add(resourceId2);
                }
                for (final Long resourceId : dummyResourceList) {
                    final Row row = new Row("MDMResourceToDeploymentConfigs");
                    row.set("RESOURCE_ID", (Object)resourceId);
                    row.set("PROFILE_ID", (Object)profileId2);
                    row.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
                    row.set("DEPLOYMENT_CONFIG_SOURCE", (Object)source);
                    row.set("CONFIG_SOURCE_ID", (Object)resourceId);
                    if (profileToBusinessStore != null && !profileToBusinessStore.isEmpty()) {
                        row.set("BUSINESSSTORE_ID", ((Hashtable<K, Object>)profileToBusinessStore).get(profileId2));
                    }
                    else {
                        row.set("BUSINESSSTORE_ID", (Object)null);
                    }
                    row.set("ADDED_TIME", (Object)currentTime);
                    dataObject2.addRow(row);
                }
                final UpdateQuery updateQuery2 = (UpdateQuery)new UpdateQueryImpl("MDMResourceToDeploymentConfigs");
                final Criteria existingResCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)existingResourceList.toArray(), 8);
                updateQuery2.setCriteria(existingResCriteria.and(indivprofileCriteria));
                updateQuery2.setUpdateColumn("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
                updateQuery2.setUpdateColumn("ADDED_TIME", (Object)currentTime);
                if (profileToBusinessStore != null && !profileToBusinessStore.isEmpty()) {
                    updateQuery2.setUpdateColumn("BUSINESSSTORE_ID", ((Hashtable<K, Object>)profileToBusinessStore).get(profileId2));
                }
                MDMUtil.getPersistence().update(updateQuery2);
            }
            MDMUtil.getPersistence().add(dataObject2);
        }
        if (source == MDMResourceToProfileDeploymentConfigHandler.USER_GROUP_DEP_CONFIG_SOURCE) {
            MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "persistMDMResourceToProfileDeploymentConfig processed for User Id : {3} , Resources {0}, Profile Id {1}, DeploymentConfigId {2}", new Object[] { resourceList, profileList, deploymentConfigId, sourceId });
        }
        else if (source == MDMResourceToProfileDeploymentConfigHandler.DEVICE_GROUP_DEP_CONFIG_SOURCE) {
            MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "persistMDMResourceToProfileDeploymentConfig processed for Group Id : {3}, Resources {0}, Profile Id {1}, DeploymentConfigId {2}", new Object[] { resourceList, profileList, deploymentConfigId, sourceId });
        }
        else {
            MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "persistMDMResourceToProfileDeploymentConfig processed for Resources : {0}, Profile Id {1}, DeploymentConfigId {2}", new Object[] { resourceList, profileList, deploymentConfigId });
        }
    }
    
    public void deleteMDMResourceToDeploymentConfig(final Long configSourceID, final List<Long> resourceList, final List<Long> profileList) throws DataAccessException {
        MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "Deletion of deployment configs of resource {0} for profiles {1}", new Object[] { resourceList, profileList });
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MDMResourceToDeploymentConfigs");
        final Criteria profileCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileList.toArray(), 8);
        final Criteria sourceCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)configSourceID, 0);
        final Criteria resCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        deleteQuery.setCriteria(sourceCriteria.and(profileCriteria).and(resCriteria));
        DataAccess.delete(deleteQuery);
    }
    
    public void deleteMDMResourceToDeploymentConfig(final List<Long> resourceList, final List<Long> profileList) throws DataAccessException {
        MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "Deletion of deployment configs of resource {0} for profiles {1}", new Object[] { resourceList, profileList });
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MDMResourceToDeploymentConfigs");
        final Criteria profileCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileList.toArray(), 8);
        final Criteria sourceCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)resourceList.toArray(), 8);
        deleteQuery.setCriteria(sourceCriteria.and(profileCriteria));
        DataAccess.delete(deleteQuery);
    }
    
    public Properties getAppToBusinessStoreMap(final Long configSourceID, final List profileList) {
        final Properties profileToBusinessStore = new Properties();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
            final Criteria configSourceCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)configSourceID, 0);
            final Criteria businessCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)null, 1);
            final Criteria profileCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileList.toArray(), 8);
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "*"));
            selectQuery.setCriteria(configSourceCriteria.and(profileCriteria).and(businessCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            for (int i = 0; i < profileList.size(); ++i) {
                final Row row = dataObject.getRow("MDMResourceToDeploymentConfigs", new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), profileList.get(i), 0));
                if (row != null) {
                    final Long businessStoreID = (Long)row.get("BUSINESSSTORE_ID");
                    if (businessStoreID != null) {
                        ((Hashtable<Object, Long>)profileToBusinessStore).put(profileList.get(i), businessStoreID);
                    }
                }
            }
        }
        catch (final Exception ex) {
            MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.SEVERE, "Exception in getDeploymentConfigDO");
        }
        return profileToBusinessStore;
    }
    
    public DataObject getAppsDeploymentConfigDO(final List<Long> resourceList, final List configSourceList, final List<Long> profileList, final int platformType) {
        DataObject depDO = (DataObject)new WritableDataObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
            selectQuery.addJoin(new Join("MDMResourceToDeploymentConfigs", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("MDMResourceToDeploymentConfigs", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria profileCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileList.toArray(), 8);
            final Criteria platformCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria appCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0);
            Criteria configSourceCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)configSourceList.toArray(), 8);
            if (resourceList != null && !resourceList.isEmpty()) {
                final Criteria resCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                configSourceCriteria = configSourceCriteria.and(resCriteria);
            }
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "*"));
            selectQuery.setCriteria(configSourceCriteria.and(profileCriteria).and(platformCriteria).and(appCriteria));
            depDO = MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception ex) {
            MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.SEVERE, "Exception in getDeploymentConfigDO");
        }
        return depDO;
    }
    
    public void deleteMDMResourceToDeploymentConfig(final List<Long> sourceList, final List<Long> resourceList, final List<Long> profileList) throws DataAccessException {
        MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "Deletion of deployment configs of source {0} resource {1} for profiles {2}", new Object[] { sourceList, resourceList, profileList });
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MDMResourceToDeploymentConfigs");
        final Criteria profileCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileList.toArray(), 8);
        Criteria sourceCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)sourceList.toArray(), 8);
        if (resourceList != null && !resourceList.isEmpty()) {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            sourceCriteria = sourceCriteria.and(resourceCriteria);
        }
        deleteQuery.setCriteria(sourceCriteria.and(profileCriteria));
        DataAccess.delete(deleteQuery);
    }
    
    public void copyDeploymentConfig(final List toBeAddedResourceIDs, final Long sourceResourceID) throws DataAccessException {
        MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "Copying deployment configs of {0} to {1}", new Object[] { sourceResourceID, toBeAddedResourceIDs });
        final Iterator configsToBeAdded = this.getAllDepConfigsForResource(sourceResourceID);
        if (configsToBeAdded != null) {
            while (configsToBeAdded.hasNext()) {
                final Row oldRow = configsToBeAdded.next();
                for (final Object resource : toBeAddedResourceIDs) {
                    final Long sourceID = (Long)oldRow.get("CONFIG_SOURCE_ID");
                    final Long deploymentConfigId = (Long)oldRow.get("DEPLOYMENT_CONFIG_ID");
                    final int source = (int)oldRow.get("DEPLOYMENT_CONFIG_SOURCE");
                    final Long profileId = (Long)oldRow.get("PROFILE_ID");
                    final Long businessStoreID = (Long)oldRow.get("BUSINESSSTORE_ID");
                    this.addOrUpdateMDMResourceToProfileDeployment((Long)resource, profileId, deploymentConfigId, source, sourceID, businessStoreID);
                }
            }
        }
    }
    
    public Iterator getAllDepConfigsForResource(final Long resourceId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMResourceToDeploymentConfigs"));
        final Criteria resourceCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceId, 0);
        sQuery.setCriteria(resourceCriteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (dO.isEmpty()) {
            return null;
        }
        return dO.getRows("MDMResourceToDeploymentConfigs");
    }
    
    public void handleDeployConfigForNewGroupMembers(final List resourceList, final Long groupId, final int groupType) {
        MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.INFO, "Adding new deployment for group members");
        try {
            new MDMResourceToProfileDeploymentConfigHandler().copyDeploymentConfig(resourceList, groupId);
            if (groupType == 7) {
                final List userDevicesList = ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(resourceList);
                new MDMResourceToProfileDeploymentConfigHandler().copyDeploymentConfig(userDevicesList, groupId);
            }
        }
        catch (final Exception ex) {
            MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.SEVERE, "Exception when adding new deployment policies in groupMemberAdded", ex);
        }
    }
    
    public List<Long> getResListForAppWithSameLicenseInOtherSource(final Long profileID, final Long businessStoreID, final List resList, final List excludeSource) {
        final List<Long> resultResList = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
            final Criteria resCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resList.toArray(), 8);
            final Criteria excludeSourceCrit = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)excludeSource.toArray(), 9);
            final Criteria businessStoreCrit = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria appCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileID, 0);
            selectQuery.setCriteria(resCriteria.and(excludeSourceCrit).and(businessStoreCrit).and(appCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("MDMResourceToDeploymentConfigs");
            while (iterator.hasNext()) {
                final Row configRow = iterator.next();
                final Long resourceID = (Long)configRow.get("RESOURCE_ID");
                if (!resultResList.contains(resourceID)) {
                    resultResList.add(resourceID);
                }
            }
        }
        catch (final Exception e) {
            MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.SEVERE, "Exception in getResListForAppWithSameLicenseInOtherSource", e);
        }
        return resultResList;
    }
    
    public JSONObject getAppLicenseDetailsForResources(final List<Long> resourceList, final List<Long> configSourceList, final List<Long> profileList, final int platformType) {
        final JSONObject appToDeviceLicenseDetails = new JSONObject();
        try {
            final DataObject depConfigDo = this.getAppsDeploymentConfigDO(resourceList, configSourceList, profileList, platformType);
            if (!depConfigDo.isEmpty()) {
                for (int i = 0; i < profileList.size(); ++i) {
                    final Long profileID = profileList.get(i);
                    final Iterator iter = depConfigDo.getRows("MDMResourceToDeploymentConfigs", new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileID, 0));
                    while (iter.hasNext()) {
                        final Row configRow = iter.next();
                        JSONObject businessStoreDetails = appToDeviceLicenseDetails.optJSONObject(String.valueOf(profileID));
                        final Long businessStoreID = (Long)configRow.get("BUSINESSSTORE_ID");
                        if (businessStoreDetails == null) {
                            businessStoreDetails = new JSONObject();
                        }
                        if (businessStoreID != null) {
                            final JSONArray deviceArray = businessStoreDetails.optJSONArray(String.valueOf(businessStoreID));
                            List deviceList = null;
                            if (deviceArray == null) {
                                deviceList = new ArrayList();
                            }
                            else {
                                deviceList = deviceArray.toList();
                            }
                            final Long deviceID = (Long)configRow.get("RESOURCE_ID");
                            if (!deviceList.contains(deviceID)) {
                                deviceList.add(deviceID);
                            }
                            businessStoreDetails.put(String.valueOf(businessStoreID), (Collection)deviceList);
                            appToDeviceLicenseDetails.put(String.valueOf(profileID), (Object)businessStoreDetails);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.SEVERE, "Exception in getAppLicenseDetailsForResources");
        }
        return appToDeviceLicenseDetails;
    }
    
    public Properties getProfileToBusinessStoreProp(final List resourceIDs, final List configSourceIDs, final List profileIDs) {
        final Properties profileToBusinessStoreProp = new Properties();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
            selectQuery.addJoin(new Join("MDMResourceToDeploymentConfigs", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceToDeploymentConfigs", "*"));
            Criteria finalCriteria = null;
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            final Criteria profileCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileIDs.toArray(), 8);
            final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria businessStoreIDNotNull = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)null, 1);
            final Column addedTimeCol = Column.getColumn("MDMResourceToDeploymentConfigs", "ADDED_TIME");
            final SortColumn sortColumn = new SortColumn(addedTimeCol, false);
            finalCriteria = resourceCriteria.and(profileCriteria).and(managedCriteria).and(businessStoreIDNotNull);
            if (configSourceIDs != null && !configSourceIDs.isEmpty()) {
                finalCriteria = finalCriteria.and(new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)configSourceIDs.toArray(), 8));
            }
            selectQuery.setCriteria(finalCriteria);
            selectQuery.addSortColumn(sortColumn);
            final DataObject depDo = MDMUtil.getPersistence().get(selectQuery);
            for (int i = 0; i < profileIDs.size(); ++i) {
                final Row row = depDo.getRow("MDMResourceToDeploymentConfigs", new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), profileIDs.get(i), 0));
                if (row != null) {
                    final Long businessStoreID = (Long)row.get("BUSINESSSTORE_ID");
                    if (businessStoreID != null && !profileToBusinessStoreProp.containsKey(profileIDs.get(i))) {
                        ((Hashtable<Object, Long>)profileToBusinessStoreProp).put(profileIDs.get(i), businessStoreID);
                    }
                }
            }
        }
        catch (final Exception e) {
            MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.SEVERE, "Exception in getBusinessDeploymentPolicy", e);
        }
        return profileToBusinessStoreProp;
    }
    
    public JSONObject getProfileDirectlyDistributedForResources(final List configSourceList) {
        final JSONObject platformToProfileMap = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMResourceToDeploymentConfigs"));
            selectQuery.addJoin(new Join("MDMResourceToDeploymentConfigs", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("MDMResourceToDeploymentConfigs", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria devResCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)120, 0);
            final Criteria configSourceCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "CONFIG_SOURCE_ID"), (Object)configSourceList.toArray(), 8);
            final Criteria appCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0);
            selectQuery.setCriteria(devResCriteria.and(configSourceCriteria).and(appCriteria));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iter = dataObject.getRows("Profile");
            while (iter.hasNext()) {
                final Row profileRow = iter.next();
                final Long profileID = (Long)profileRow.get("PROFILE_ID");
                final int platformType = (int)profileRow.get("PLATFORM_TYPE");
                final JSONArray tempProfileArray = platformToProfileMap.optJSONArray(String.valueOf(platformType));
                List tempProfileList = null;
                if (tempProfileArray == null) {
                    tempProfileList = new ArrayList();
                }
                else {
                    tempProfileList = tempProfileArray.toList();
                }
                if (!tempProfileList.contains(profileID)) {
                    tempProfileList.add(profileID);
                }
                platformToProfileMap.put(String.valueOf(platformType), (Collection)tempProfileList);
            }
        }
        catch (final Exception e) {
            MDMResourceToProfileDeploymentConfigHandler.LOGGER.log(Level.SEVERE, "Exception in getProfileDirectlyDistributedForResources", e);
        }
        return platformToProfileMap;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
        DEVICE_GROUP_DEP_CONFIG_SOURCE = 1;
        USER_GROUP_DEP_CONFIG_SOURCE = 2;
        DEVICE_DEP_CONFIG_SOURCE = 3;
        USER_DEP_CONFIG_SOURCE = 4;
    }
}
