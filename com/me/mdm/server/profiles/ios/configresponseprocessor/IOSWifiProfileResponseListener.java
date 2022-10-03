package com.me.mdm.server.profiles.ios.configresponseprocessor;

import java.util.Hashtable;
import java.util.Iterator;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.config.task.AssignDeviceCommandTask;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.Properties;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.logging.Level;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class IOSWifiProfileResponseListener implements MDMProfileResponseListener
{
    private static final Logger LOGGER;
    boolean isNotify;
    
    public IOSWifiProfileResponseListener() {
        this.isNotify = false;
    }
    
    @Override
    public JSONObject successHandler(final JSONObject params) {
        try {
            final Long resourceId = params.optLong("resourceId");
            final Long customerId = params.optLong("customerId");
            final Integer platformType = params.getInt("platformType");
            if (platformType == 1) {
                final List resourceList = new ArrayList();
                resourceList.add(resourceId);
                final DataObject dataObject = this.getInProgressConfigDOForResource(resourceId);
                if (dataObject != null && !dataObject.isEmpty()) {
                    this.sendWifiRestrictionInProgressCollection(dataObject, resourceId, customerId);
                    this.sendWifiUpdateInProgressCollection(dataObject, resourceId, customerId);
                    this.sendWifiRemovalInProgressCollection(dataObject, resourceId, customerId);
                }
            }
        }
        catch (final Exception e) {
            IOSWifiProfileResponseListener.LOGGER.log(Level.SEVERE, "Exception in wifi response listener", e);
        }
        return null;
    }
    
    @Override
    public JSONObject failureHandler(final JSONObject params) {
        return null;
    }
    
    @Override
    public boolean isNotify(final JSONObject params) {
        return this.isNotify;
    }
    
    public DataObject getInProgressConfigDOForResource(final Long resourceId) {
        try {
            final SelectQuery selectQuery = new ProfileAssociateDataHandler().getProfileAssociatedForResourceQuery();
            selectQuery.addJoin(new Join("RecentProfileForResource", "MdCollectionCommand", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("MdCollectionCommand", "MdCommandsToDevice", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 1));
            selectQuery.addJoin(new Join("ConfigDataItem", "RestrictionsPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
            selectQuery.addJoin(new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "RESOURCE_ID", "COLLECTION_ID", "PROFILE_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID", "PROFILE_ID" }, 2));
            final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria noCommandCriteria = new Criteria(new Column("MdCommandsToDevice", "COMMAND_ID"), (Object)null, 0);
            final Criteria inProgressCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)16, 0);
            final Criteria finalCriteria = resourceCriteria.and(noCommandCriteria).and(inProgressCriteria);
            selectQuery.setCriteria(finalCriteria);
            selectQuery.addSelectColumn(new Column("RecentProfileForResource", "*"));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "RESOURCE_HISTORY_ID"));
            selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "ASSOCIATED_BY"));
            selectQuery.addSelectColumn(new Column("ConfigData", "CONFIG_DATA_ID"));
            selectQuery.addSelectColumn(new Column("ConfigData", "CONFIG_ID"));
            selectQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
            selectQuery.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ID"));
            selectQuery.addSelectColumn(new Column("RestrictionsPolicy", "*"));
            return MDMUtil.getPersistenceLite().get(selectQuery);
        }
        catch (final DataAccessException e) {
            IOSWifiProfileResponseListener.LOGGER.log(Level.SEVERE, "Exception while performing DB operation in wifi listener", (Throwable)e);
            return null;
        }
    }
    
    private void sendWifiRestrictionInProgressCollection(final DataObject dataObject, final Long resourceId, final Long customerId) {
        final List<Long> collectionList = new ArrayList<Long>();
        try {
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            final Criteria installCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria restrictionCriteria = new Criteria(new Column("RestrictionsPolicy", "FORCE_WIFI_WHITELISTING"), (Object)true, 0);
            final Iterator iterator = dataObject.getRows("RestrictionsPolicy", restrictionCriteria);
            final HashMap<Long, Long> profileCollnMap = new HashMap<Long, Long>();
            final JSONObject loggedInUser = new JSONObject();
            final HashMap profileToPlatformMap = new HashMap();
            final HashMap collectionToPlatformMap = new HashMap();
            final List<Long> profileList = new ArrayList<Long>();
            while (iterator.hasNext()) {
                final Row restrictionRow = iterator.next();
                final List<String> tableName = new ArrayList<String>();
                tableName.add("RecentProfileForResource");
                tableName.add("ResourceToProfileHistory");
                tableName.add("CfgDataToCollection");
                tableName.add("ConfigData");
                tableName.add("ConfigDataItem");
                tableName.add("RestrictionsPolicy");
                final DataObject restrictionDataObject = dataObject.getDataObject((List)tableName, restrictionRow);
                final Iterator restrictionIterator = restrictionDataObject.getRows("ResourceToProfileHistory", installCriteria);
                while (restrictionIterator.hasNext()) {
                    final Row resourceRow = restrictionIterator.next();
                    final Long collectionId = (Long)resourceRow.get("COLLECTION_ID");
                    final Long profileId = (Long)resourceRow.get("PROFILE_ID");
                    final Long loginUser = (Long)resourceRow.get("ASSOCIATED_BY");
                    collectionList.add(collectionId);
                    loggedInUser.put(String.valueOf(profileId), (Object)loginUser);
                    profileCollnMap.put(profileId, collectionId);
                    profileList.add(profileId);
                }
            }
            if (!collectionList.isEmpty()) {
                profileToPlatformMap.put(1, profileList);
                profileToPlatformMap.put(2, new ArrayList());
                profileToPlatformMap.put(3, new ArrayList());
                profileToPlatformMap.put(4, new ArrayList());
                collectionToPlatformMap.put(1, collectionList);
                collectionToPlatformMap.put(2, new ArrayList());
                collectionToPlatformMap.put(3, new ArrayList());
                collectionToPlatformMap.put(4, new ArrayList());
                final HashMap deviceMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceList);
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
                new AssignDeviceCommandTask().executeTask(commandProperties);
            }
        }
        catch (final DataAccessException e) {
            IOSWifiProfileResponseListener.LOGGER.log(Level.SEVERE, "Exception while performing DB operation in Wifi Listener", (Throwable)e);
        }
        catch (final JSONException e2) {
            IOSWifiProfileResponseListener.LOGGER.log(Level.SEVERE, "Exception while parsing JSON", (Throwable)e2);
        }
    }
    
    private void sendWifiRemovalInProgressCollection(final DataObject dataObject, final Long resourceId, final Long customerId) {
        final List<Long> collectionList = new ArrayList<Long>();
        try {
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            final HashMap<Long, Long> profileCollnMap = new HashMap<Long, Long>();
            final JSONObject loggedInUser = new JSONObject();
            final HashMap profileToPlatformMap = new HashMap();
            final HashMap collectionToPlatformMap = new HashMap();
            final List<Long> profileList = new ArrayList<Long>();
            final HashMap<Long, List> collectionToApplicableRes = new HashMap<Long, List>();
            final Criteria wifiPolicyCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)new Integer[] { 177, 774 }, 8);
            final Iterator iterator = dataObject.getRows("ConfigData", wifiPolicyCriteria);
            final Criteria removalCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)true, 0);
            while (iterator.hasNext()) {
                final Row wifiRemovalRow = iterator.next();
                final List<String> tableName = new ArrayList<String>();
                tableName.add("RecentProfileForResource");
                tableName.add("ResourceToProfileHistory");
                tableName.add("CfgDataToCollection");
                tableName.add("ConfigData");
                final DataObject wifiRemovalDO = dataObject.getDataObject((List)tableName, wifiRemovalRow);
                final Iterator wifiRemovalIterator = wifiRemovalDO.getRows("ResourceToProfileHistory", removalCriteria);
                while (wifiRemovalIterator.hasNext()) {
                    final Row resourceRow = wifiRemovalIterator.next();
                    final Long collectionId = (Long)resourceRow.get("COLLECTION_ID");
                    final Long profileId = (Long)resourceRow.get("PROFILE_ID");
                    final Long loginUser = (Long)resourceRow.get("ASSOCIATED_BY");
                    collectionList.add(collectionId);
                    loggedInUser.put(String.valueOf(profileId), (Object)loginUser);
                    profileCollnMap.put(profileId, collectionId);
                    collectionToApplicableRes.put(collectionId, resourceList);
                    profileList.add(profileId);
                }
            }
            final List<Long> inProgressCollectionList = new ArrayList<Long>();
            final Iterator inProgressRemovalIterator = dataObject.getRows("RecentProfileForResource", removalCriteria);
            while (inProgressRemovalIterator.hasNext()) {
                final Row inProgressRow = inProgressRemovalIterator.next();
                inProgressCollectionList.add((Long)inProgressRow.get("COLLECTION_ID"));
            }
            final List prevInprogressProfile = new IOSWifiRestrictionResponseListener().getProfileIdForPrevRestrictedProfile(resourceId, inProgressCollectionList);
            if (!prevInprogressProfile.isEmpty()) {
                final Criteria prevProfileCriteria = new Criteria(new Column("ResourceToProfileHistory", "PROFILE_ID"), (Object)prevInprogressProfile.toArray(), 8);
                final Iterator prevIterator = dataObject.getRows("ResourceToProfileHistory", prevProfileCriteria);
                while (prevIterator.hasNext()) {
                    final Row prevResourceRow = prevIterator.next();
                    final Long collectionId2 = (Long)prevResourceRow.get("COLLECTION_ID");
                    final Long profileId2 = (Long)prevResourceRow.get("PROFILE_ID");
                    final Long loginUser2 = (Long)prevResourceRow.get("ASSOCIATED_BY");
                    collectionList.add(collectionId2);
                    loggedInUser.put(String.valueOf(profileId2), (Object)loginUser2);
                    profileCollnMap.put(profileId2, collectionId2);
                }
            }
            if (!collectionList.isEmpty()) {
                profileToPlatformMap.put(1, profileList);
                profileToPlatformMap.put(2, new ArrayList());
                profileToPlatformMap.put(3, new ArrayList());
                profileToPlatformMap.put(4, new ArrayList());
                collectionToPlatformMap.put(1, collectionList);
                collectionToPlatformMap.put(2, new ArrayList());
                collectionToPlatformMap.put(3, new ArrayList());
                collectionToPlatformMap.put(4, new ArrayList());
                final HashMap deviceMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceList);
                final Properties commandProperties = new Properties();
                ((Hashtable<String, JSONObject>)commandProperties).put("additionalParams", loggedInUser);
                ((Hashtable<String, String>)commandProperties).put("commandName", "RemoveProfile");
                ((Hashtable<String, HashMap<Long, Long>>)commandProperties).put("profileCollnMap", profileCollnMap);
                ((Hashtable<String, HashMap>)commandProperties).put("deviceMap", deviceMap);
                ((Hashtable<String, String>)commandProperties).put("actual_className", "com.adventnet.sym.server.mdm.config.task.AssignDeviceCommandTask");
                ((Hashtable<String, Boolean>)commandProperties).put("isGroup", false);
                ((Hashtable<String, String>)commandProperties).put("UserId", loggedInUser.toString());
                ((Hashtable<String, Long>)commandProperties).put("customerId", customerId);
                ((Hashtable<String, Integer>)commandProperties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_USER);
                ((Hashtable<String, Boolean>)commandProperties).put("isAppConfig", false);
                ((Hashtable<String, HashMap<Long, List>>)commandProperties).put("collectionToApplicableResource", collectionToApplicableRes);
                ((Hashtable<String, HashMap>)commandProperties).put("collnToProfileDirectRemovalResources", new HashMap());
                ((Hashtable<String, HashMap>)commandProperties).put("profileToPlatformMap", profileToPlatformMap);
                ((Hashtable<String, HashMap>)commandProperties).put("collectionToPlatformMap", collectionToPlatformMap);
                new AssignDeviceCommandTask().executeTask(commandProperties);
            }
        }
        catch (final DataAccessException e) {
            IOSWifiProfileResponseListener.LOGGER.log(Level.SEVERE, "Exception while performing DO in remove wifi listener", (Throwable)e);
        }
        catch (final JSONException e2) {
            IOSWifiProfileResponseListener.LOGGER.log(Level.SEVERE, "Exception while parsing JSON in remove wifi listener", (Throwable)e2);
        }
    }
    
    private void sendWifiUpdateInProgressCollection(final DataObject dataObject, final Long resourceId, final Long customerId) {
        final List<Long> collectionList = new ArrayList<Long>();
        try {
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            final HashMap<Long, Long> profileCollnMap = new HashMap<Long, Long>();
            final JSONObject loggedInUser = new JSONObject();
            final HashMap profileToPlatformMap = new HashMap();
            final HashMap collectionToPlatformMap = new HashMap();
            final List<Long> profileList = new ArrayList<Long>();
            final HashMap<Long, List> collectionToApplicableRes = new HashMap<Long, List>();
            final List<Long> tempCollectionList = new ArrayList<Long>();
            final Criteria associationCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Iterator iterator = dataObject.getRows("RecentProfileForResource", associationCriteria);
            while (iterator.hasNext()) {
                final Row resourceRow = iterator.next();
                final Long collectionId = (Long)resourceRow.get("COLLECTION_ID");
                tempCollectionList.add(collectionId);
            }
            final List prevInprogressProfile = new IOSWifiRestrictionResponseListener().getProfileIdForPrevRestrictedProfile(resourceId, tempCollectionList);
            if (!prevInprogressProfile.isEmpty()) {
                final Criteria prevProfileCriteria = new Criteria(new Column("ResourceToProfileHistory", "PROFILE_ID"), (Object)prevInprogressProfile.toArray(), 8);
                final Iterator prevIterator = dataObject.getRows("ResourceToProfileHistory", prevProfileCriteria);
                while (prevIterator.hasNext()) {
                    final Row prevResourceRow = prevIterator.next();
                    final Long collectionId2 = (Long)prevResourceRow.get("COLLECTION_ID");
                    final Long profileId = (Long)prevResourceRow.get("PROFILE_ID");
                    final Long loginUser = (Long)prevResourceRow.get("ASSOCIATED_BY");
                    collectionList.add(collectionId2);
                    loggedInUser.put(String.valueOf(profileId), (Object)loginUser);
                    profileCollnMap.put(profileId, collectionId2);
                    profileList.add(profileId);
                }
            }
            if (!collectionList.isEmpty()) {
                profileToPlatformMap.put(1, profileList);
                profileToPlatformMap.put(2, new ArrayList());
                profileToPlatformMap.put(3, new ArrayList());
                profileToPlatformMap.put(4, new ArrayList());
                collectionToPlatformMap.put(1, collectionList);
                collectionToPlatformMap.put(2, new ArrayList());
                collectionToPlatformMap.put(3, new ArrayList());
                collectionToPlatformMap.put(4, new ArrayList());
                final HashMap deviceMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceList);
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
                new AssignDeviceCommandTask().executeTask(commandProperties);
            }
        }
        catch (final DataAccessException ex) {}
        catch (final JSONException ex2) {}
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
