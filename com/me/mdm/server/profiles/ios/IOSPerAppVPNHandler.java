package com.me.mdm.server.profiles.ios;

import java.util.Hashtable;
import java.util.Set;
import com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue.AssociationQueueHandler;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.Properties;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class IOSPerAppVPNHandler
{
    private Logger logger;
    
    public IOSPerAppVPNHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public HashMap<Long, List<Long>> getAppInstalledResourceForCollection(final List<Long> collectionIds, final List<Long> resourceIds) throws DataAccessException {
        final HashMap<Long, List<Long>> collectionResource = new HashMap<Long, List<Long>>();
        final HashMap<Long, List<Long>> appGroupColletion = this.getAppGroupIdsForCollections(collectionIds);
        final HashSet appGroupIds = new HashSet();
        for (final Long key : appGroupColletion.keySet()) {
            appGroupIds.addAll(appGroupColletion.get(key));
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
        final Criteria resourceCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
        final Criteria appGroupCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
        final Criteria appInstalledCriteria = new Criteria(new Column("MdAppCatalogToResource", "STATUS"), (Object)2, 0);
        selectQuery.setCriteria(resourceCriteria.and(appGroupCriteria).and(appInstalledCriteria));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "RESOURCE_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            for (final Long collectionId : appGroupColletion.keySet()) {
                final List<Long> groupIds = appGroupColletion.get(collectionId);
                for (final Long resourceId : resourceIds) {
                    final Criteria resourceIdCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
                    final Criteria appCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)groupIds.toArray(), 8);
                    if (MDMDBUtil.getDOSize(dataObject, "MdAppCatalogToResource", resourceIdCriteria.and(appCriteria)) == groupIds.size()) {
                        if (collectionResource.containsKey(collectionId)) {
                            final List<Long> resources = collectionResource.get(collectionId);
                            resources.add(resourceId);
                        }
                        else {
                            final List<Long> resources = new ArrayList<Long>();
                            resources.add(resourceId);
                            collectionResource.put(collectionId, resources);
                        }
                    }
                }
            }
        }
        return collectionResource;
    }
    
    private SelectQuery getAppQueryForCollection(final List<Long> collectionIds) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CfgDataToCollection"));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigDataItem", "AppLockPolicyApps", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        final Criteria collectionCriteria = new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
        final Criteria configCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)521, 0);
        selectQuery.setCriteria(collectionCriteria.and(configCriteria));
        return selectQuery;
    }
    
    public JSONObject getAppGroupIdForCollection(final Long collectionId) {
        final JSONObject appDetailObject = new JSONObject();
        final JSONArray appArray = new JSONArray();
        try {
            final List<Long> collectionIds = new ArrayList<Long>();
            collectionIds.add(collectionId);
            final SelectQuery selectQuery = this.getAppQueryForCollection(collectionIds);
            selectQuery.addSelectColumn(new Column("AppLockPolicyApps", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(new Column("AppLockPolicyApps", "CONFIG_DATA_ITEM_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("AppLockPolicyApps");
                while (iterator.hasNext()) {
                    final Row appRow = iterator.next();
                    appArray.put((Object)appRow.get("APP_GROUP_ID"));
                }
            }
            appDetailObject.put("APP_GROUP_ID", (Object)appArray);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in getAppGroupIdForCollection", (Throwable)e);
        }
        return appDetailObject;
    }
    
    private HashMap getAppGroupIdsForCollections(final List<Long> collectionIds) {
        final HashMap<Long, List<Long>> object = new HashMap<Long, List<Long>>();
        try {
            final SelectQuery selectQuery = this.getAppQueryForCollection(collectionIds);
            selectQuery.addSelectColumn(new Column("AppLockPolicyApps", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
            final DMDataSetWrapper wrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (wrapper.next()) {
                final Long appGroupId = (Long)wrapper.getValue("APP_GROUP_ID");
                final Long collectionId = (Long)wrapper.getValue("COLLECTION_ID");
                if (object.containsKey(collectionId)) {
                    final List<Long> list = object.get(collectionId);
                    list.add(appGroupId);
                }
                else {
                    final List<Long> list = new ArrayList<Long>();
                    list.add(appGroupId);
                    object.put(collectionId, list);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getAppGroupIdsForCollections", e);
        }
        return object;
    }
    
    public void checkAndAddPerAppVpnConfigurationToResource(final Long resourceId, final Long customerId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
            selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            selectQuery.addJoin(new Join("CfgDataToCollection", "MdCollectionCommand", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("MdCollectionCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            final Criteria joinCriteria = new Criteria(new Column("MdCommands", "COMMAND_TYPE"), (Object)"InstallProfile", 0).and(new Criteria(new Column("MdCommands", "COMMAND_ID"), (Object)new Column("MdCommandsToDevice", "COMMAND_ID"), 0)).and(new Criteria(new Column("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceId, 0));
            selectQuery.addJoin(new Join("MdCommands", "MdCommandsToDevice", joinCriteria, 1));
            final Criteria resourceIdCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria configCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)521, 0);
            final Criteria associatedCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria statusCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)16, 0);
            final Criteria commandCriteria = new Criteria(new Column("MdCommandsToDevice", "COMMAND_DEVICE_ID"), (Object)null, 0);
            selectQuery.setCriteria(resourceIdCriteria.and(configCriteria).and(associatedCriteria).and(statusCriteria).and(commandCriteria));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("CfgDataToCollection");
                final List<Long> collectionIds = new ArrayList<Long>();
                while (iterator.hasNext()) {
                    final Row collectionRow = iterator.next();
                    collectionIds.add((Long)collectionRow.get("COLLECTION_ID"));
                }
                if (collectionIds.size() > 0) {
                    final List<Long> resourceList = new ArrayList<Long>();
                    resourceList.add(resourceId);
                    final HashMap<Long, List<Long>> map = this.getAppInstalledResourceForCollection(collectionIds, resourceList);
                    final List<Long> applicableCollectionList = new ArrayList<Long>();
                    for (final Long collectionId : map.keySet()) {
                        if (map.get(collectionId).size() > 0) {
                            applicableCollectionList.add(collectionId);
                        }
                    }
                    if (applicableCollectionList.size() > 0) {
                        final HashMap<Long, Long> profileCollnMap = new HashMap<Long, Long>();
                        final JSONObject loggedInUser = new JSONObject();
                        final HashMap profileToPlatformMap = new HashMap();
                        final HashMap collectionToPlatformMap = new HashMap();
                        final List<Long> profileList = new ArrayList<Long>();
                        final HashMap deviceMap = new HashMap();
                        final Set ios = new HashSet();
                        ios.add(resourceId);
                        deviceMap.put(1, ios);
                        final SelectQuery profileHistory = (SelectQuery)new SelectQueryImpl(new Table("ResourceToProfileHistory"));
                        profileHistory.setCriteria(new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0).and(new Criteria(new Column("ResourceToProfileHistory", "COLLECTION_ID"), (Object)applicableCollectionList.toArray(), 8)));
                        profileHistory.addSelectColumn(new Column("ResourceToProfileHistory", "RESOURCE_ID"));
                        profileHistory.addSelectColumn(new Column("ResourceToProfileHistory", "COLLECTION_ID"));
                        profileHistory.addSelectColumn(new Column("ResourceToProfileHistory", "LAST_MODIFIED_BY"));
                        profileHistory.addSelectColumn(new Column("ResourceToProfileHistory", "PROFILE_ID"));
                        profileHistory.addSelectColumn(new Column("ResourceToProfileHistory", "RESOURCE_HISTORY_ID"));
                        final DataObject dataObject2 = MDMUtil.getPersistenceLite().get(profileHistory);
                        if (!dataObject2.isEmpty()) {
                            final Iterator profileIterator = dataObject2.getRows("ResourceToProfileHistory");
                            while (profileIterator.hasNext()) {
                                final Row profileRow = profileIterator.next();
                                final Long profileId = (Long)profileRow.get("PROFILE_ID");
                                final Long collectionId2 = (Long)profileRow.get("COLLECTION_ID");
                                final Long loginUser = (Long)profileRow.get("LAST_MODIFIED_BY");
                                loggedInUser.put(String.valueOf(profileId), (Object)loginUser);
                                profileCollnMap.put(profileId, collectionId2);
                                profileList.add(profileId);
                            }
                        }
                        profileToPlatformMap.put(1, profileList);
                        profileToPlatformMap.put(2, new ArrayList());
                        profileToPlatformMap.put(3, new ArrayList());
                        profileToPlatformMap.put(4, new ArrayList());
                        collectionToPlatformMap.put(1, applicableCollectionList);
                        collectionToPlatformMap.put(2, new ArrayList());
                        collectionToPlatformMap.put(3, new ArrayList());
                        collectionToPlatformMap.put(4, new ArrayList());
                        final Properties commandProperties = new Properties();
                        ((Hashtable<String, JSONObject>)commandProperties).put("additionalParams", loggedInUser);
                        ((Hashtable<String, String>)commandProperties).put("commandName", "InstallProfile");
                        ((Hashtable<String, HashMap<Long, Long>>)commandProperties).put("profileCollnMap", profileCollnMap);
                        ((Hashtable<String, HashMap<Long, Long>>)commandProperties).put("profileCollectionMap", profileCollnMap);
                        ((Hashtable<String, HashMap>)commandProperties).put("deviceMap", deviceMap);
                        ((Hashtable<String, String>)commandProperties).put("actual_className", "com.adventnet.sym.server.mdm.config.task.AssignDeviceCommandTask");
                        ((Hashtable<String, Boolean>)commandProperties).put("isGroup", false);
                        ((Hashtable<String, String>)commandProperties).put("UserId", loggedInUser.toString());
                        ((Hashtable<String, Long>)commandProperties).put("customerId", customerId);
                        ((Hashtable<String, Integer>)commandProperties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_USER);
                        ((Hashtable<String, Boolean>)commandProperties).put("isAppConfig", false);
                        ((Hashtable<String, HashMap>)commandProperties).put("profileToPlatformMap", profileToPlatformMap);
                        ((Hashtable<String, HashMap>)commandProperties).put("collectionToPlatformMap", collectionToPlatformMap);
                        ((Hashtable<String, Integer>)commandProperties).put("commandType", 1);
                        final HashMap taskInfoMap = new HashMap();
                        taskInfoMap.put("taskName", "AssignDeviceCommandTask");
                        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                        taskInfoMap.put("poolName", "mdmPool");
                        AssociationQueueHandler.getInstance().executeTask(taskInfoMap, commandProperties);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checkAndAddPerAppVpnConfigurationToResource", e);
        }
    }
}
