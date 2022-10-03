package com.me.mdm.server.profiles.ios.configresponseprocessor;

import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.profiles.ios.configNotApplicableHandler.IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.config.task.AssignDeviceCommandTask;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.Properties;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.List;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class IOSWifiRestrictionResponseListener implements MDMProfileResponseListener
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject successHandler(final JSONObject params) {
        final JSONObject listenerResponse = new JSONObject();
        try {
            final Long resourceId = params.optLong("resourceId");
            final Long customerId = params.optLong("customerId");
            final Integer platformType = params.getInt("platformType");
            if (platformType == 1) {
                final List resourceList = new ArrayList();
                resourceList.add(resourceId);
                final Criteria restrictionCriteria = new Criteria(new Column("RestrictionsPolicy", "FORCE_WIFI_WHITELISTING"), (Object)true, 0);
                final JSONObject restrictionObject = new ProfileAssociateDataHandler().getRestrictionAppliedOnResource(resourceList, "RestrictionsPolicy", restrictionCriteria);
                final List restrictionAppliedList = (List)restrictionObject.get("RESOURCE_ID");
                if (restrictionAppliedList.isEmpty()) {
                    final DataObject dataObject = new IOSWifiProfileResponseListener().getInProgressConfigDOForResource(resourceId);
                    if (!dataObject.isEmpty()) {
                        this.sendInProgressInstallProfileCollection(dataObject, resourceId, customerId);
                        this.sendInProgressRemoveProfileCollection(dataObject, resourceId, customerId);
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            IOSWifiRestrictionResponseListener.LOGGER.log(Level.SEVERE, "Exception while performing Do operation in wifi restriction", (Throwable)e);
        }
        catch (final JSONException e2) {
            IOSWifiRestrictionResponseListener.LOGGER.log(Level.SEVERE, "Exception while performing JSON operation in wifi restriction", (Throwable)e2);
        }
        catch (final Exception e3) {
            IOSWifiRestrictionResponseListener.LOGGER.log(Level.SEVERE, "Exception while performing available resource in wifi restriction", e3);
        }
        return listenerResponse;
    }
    
    @Override
    public JSONObject failureHandler(final JSONObject params) {
        return this.successHandler(params);
    }
    
    @Override
    public boolean isNotify(final JSONObject params) {
        return false;
    }
    
    private void sendInProgressInstallProfileCollection(final DataObject dataObject, final Long resourceId, final Long customerId) {
        final List<Long> collectionList = new ArrayList<Long>();
        try {
            if (!dataObject.isEmpty()) {
                final List<Long> resourceList = new ArrayList<Long>();
                resourceList.add(resourceId);
                final Criteria associationCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
                final Iterator iterator = dataObject.getRows("RecentProfileForResource", associationCriteria);
                final List<Long> tempCollectionList = new ArrayList<Long>();
                final HashMap<Long, Long> profileCollnMap = new HashMap<Long, Long>();
                final JSONObject loggedInUser = new JSONObject();
                final HashMap profileToPlatformMap = new HashMap();
                final HashMap collectionToPlatformMap = new HashMap();
                final List<Long> profileList = new ArrayList<Long>();
                while (iterator.hasNext()) {
                    final Row resourceRow = iterator.next();
                    final Long collectionId = (Long)resourceRow.get("COLLECTION_ID");
                    tempCollectionList.add(collectionId);
                }
                if (!tempCollectionList.isEmpty()) {
                    final Criteria collectionCriteria = new Criteria(new Column("ResourceToProfileHistory", "COLLECTION_ID"), (Object)tempCollectionList.toArray(), 8);
                    final Criteria wifiPolicyCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)new Integer[] { 177, 774 }, 8);
                    final Iterator wifiIterator = dataObject.getRows("ConfigData", wifiPolicyCriteria);
                    while (wifiIterator.hasNext()) {
                        final Row wifiRow = wifiIterator.next();
                        final List<String> tableName = new ArrayList<String>();
                        tableName.add("RecentProfileForResource");
                        tableName.add("ResourceToProfileHistory");
                        tableName.add("CfgDataToCollection");
                        tableName.add("ConfigData");
                        final DataObject wifiDataObject = dataObject.getDataObject((List)tableName, wifiRow);
                        final Iterator wifiCollectionIterator = wifiDataObject.getRows("ResourceToProfileHistory", collectionCriteria);
                        while (wifiCollectionIterator.hasNext()) {
                            final Row wifiResourceRow = wifiCollectionIterator.next();
                            final Long collectionId2 = (Long)wifiResourceRow.get("COLLECTION_ID");
                            final Long profileId = (Long)wifiResourceRow.get("PROFILE_ID");
                            final Long loginUser = (Long)wifiResourceRow.get("ASSOCIATED_BY");
                            collectionList.add(collectionId2);
                            loggedInUser.put(String.valueOf(profileId), (Object)loginUser);
                            profileCollnMap.put(profileId, collectionId2);
                            profileList.add(profileId);
                        }
                    }
                    final List prevInprogressProfile = this.getProfileIdForPrevRestrictedProfile(resourceId, tempCollectionList);
                    if (!prevInprogressProfile.isEmpty()) {
                        final Criteria prevProfileCriteria = new Criteria(new Column("ResourceToProfileHistory", "PROFILE_ID"), (Object)prevInprogressProfile.toArray(), 8);
                        final Iterator prevIterator = dataObject.getRows("ResourceToProfileHistory", prevProfileCriteria);
                        while (prevIterator.hasNext()) {
                            final Row prevResourceRow = prevIterator.next();
                            final Long collectionId3 = (Long)prevResourceRow.get("COLLECTION_ID");
                            final Long profileId2 = (Long)prevResourceRow.get("PROFILE_ID");
                            final Long loginUser2 = (Long)prevResourceRow.get("ASSOCIATED_BY");
                            collectionList.add(collectionId3);
                            loggedInUser.put(String.valueOf(profileId2), (Object)loginUser2);
                            profileCollnMap.put(profileId2, collectionId3);
                            profileList.add(profileId2);
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
            }
        }
        catch (final DataAccessException e) {
            IOSWifiRestrictionResponseListener.LOGGER.log(Level.INFO, "Exception while performing DB operation in wifi profile listener", (Throwable)e);
        }
        catch (final JSONException e2) {
            IOSWifiRestrictionResponseListener.LOGGER.log(Level.SEVERE, "Exception while parsing JSON in wifi restriction removal", (Throwable)e2);
        }
    }
    
    private void sendInProgressRemoveProfileCollection(final DataObject dataObject, final Long resourceId, final Long customerId) {
        final List<Long> collectionList = new ArrayList<Long>();
        try {
            if (!dataObject.isEmpty()) {
                final List<Long> resourceList = new ArrayList<Long>();
                resourceList.add(resourceId);
                final Criteria associationCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)true, 0);
                final Iterator iterator = dataObject.getRows("RecentProfileForResource", associationCriteria);
                final List<Long> tempCollectionList = new ArrayList<Long>();
                final HashMap<Long, Long> profileCollnMap = new HashMap<Long, Long>();
                final HashMap<Long, List> collectionToApplicableRes = new HashMap<Long, List>();
                final JSONObject loggedInUser = new JSONObject();
                final HashMap profileToPlatformMap = new HashMap();
                final HashMap collectionToPlatformMap = new HashMap();
                final HashMap<Long, List> collectionToDirectRemoval = new HashMap<Long, List>();
                final List<Long> profileList = new ArrayList<Long>();
                while (iterator.hasNext()) {
                    final Row resourceRow = iterator.next();
                    tempCollectionList.add((Long)resourceRow.get("COLLECTION_ID"));
                }
                if (!tempCollectionList.isEmpty()) {
                    final Criteria collectionCriteria = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)tempCollectionList.toArray(), 8);
                    final Criteria wifiPolicyCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)new Integer[] { 177, 774 }, 8);
                    final Iterator wifiIterator = dataObject.getRows("ConfigData", wifiPolicyCriteria);
                    while (wifiIterator.hasNext()) {
                        final Row wifiRow = wifiIterator.next();
                        final List<String> tableName = new ArrayList<String>();
                        tableName.add("RecentProfileForResource");
                        tableName.add("ResourceToProfileHistory");
                        tableName.add("CfgDataToCollection");
                        tableName.add("ConfigData");
                        final DataObject wifiRemovalObject = dataObject.getDataObject((List)tableName, wifiRow);
                        final Iterator wifiRemovalIterator = wifiRemovalObject.getRows("ResourceToProfileHistory", collectionCriteria);
                        while (wifiRemovalIterator.hasNext()) {
                            final Row wifiResourceRow = wifiRemovalIterator.next();
                            final Long collectionId = (Long)wifiResourceRow.get("COLLECTION_ID");
                            final Long profileId = (Long)wifiResourceRow.get("PROFILE_ID");
                            final Long loginUserId = (Long)wifiResourceRow.get("ASSOCIATED_BY");
                            collectionList.add(collectionId);
                            profileCollnMap.put(profileId, collectionId);
                            loggedInUser.put(String.valueOf(profileId), (Object)loginUserId);
                            collectionToApplicableRes.put(collectionId, resourceList);
                            collectionToDirectRemoval.put(collectionId, new ArrayList());
                            profileList.add(profileId);
                        }
                    }
                    final List prevInprogressProfile = this.getProfileIdForPrevRestrictedProfile(resourceId, tempCollectionList);
                    if (!prevInprogressProfile.isEmpty()) {
                        final Criteria prevProfileCriteria = new Criteria(new Column("ResourceToProfileHistory", "PROFILE_ID"), (Object)prevInprogressProfile.toArray(), 8);
                        final Iterator prevIterator = dataObject.getRows("ResourceToProfileHistory", prevProfileCriteria);
                        while (prevIterator.hasNext()) {
                            final Row prevResourceRow = prevIterator.next();
                            final Long collectionId2 = (Long)prevResourceRow.get("COLLECTION_ID");
                            final Long profileId2 = (Long)prevResourceRow.get("PROFILE_ID");
                            final Long loginUser = (Long)prevResourceRow.get("ASSOCIATED_BY");
                            collectionList.add(collectionId2);
                            loggedInUser.put(String.valueOf(profileId2), (Object)loginUser);
                            profileCollnMap.put(profileId2, collectionId2);
                            collectionToApplicableRes.put(collectionId2, resourceList);
                            collectionToDirectRemoval.put(collectionId2, new ArrayList());
                            profileList.add(profileId2);
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
                        ((Hashtable<String, HashMap<Long, List>>)commandProperties).put("collnToProfileDirectRemovalResources", collectionToDirectRemoval);
                        ((Hashtable<String, HashMap>)commandProperties).put("profileToPlatformMap", profileToPlatformMap);
                        ((Hashtable<String, HashMap>)commandProperties).put("collectionToPlatformMap", collectionToPlatformMap);
                        new AssignDeviceCommandTask().executeTask(commandProperties);
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            IOSWifiRestrictionResponseListener.LOGGER.log(Level.SEVERE, "Exception while performing DB operation in remove wifi rest listener", (Throwable)e);
        }
        catch (final JSONException e2) {
            IOSWifiRestrictionResponseListener.LOGGER.log(Level.SEVERE, "Exception while parsing DB operation in remove wifi rest listener", (Throwable)e2);
        }
    }
    
    public List getProfileIdForPrevRestrictedProfile(final Long resourceId, final List collectionIdList) {
        final List<Long> prevProfileIdList = new ArrayList<Long>();
        try {
            if (!collectionIdList.isEmpty()) {
                final List<Long> resourceList = new ArrayList<Long>();
                resourceList.add(resourceId);
                final List profileId = new ProfileHandler().getProfileIDsFromCollectionIDs(collectionIdList);
                final ProfileAssociateDataHandler profileAssociateDataHandler = new ProfileAssociateDataHandler();
                final JSONObject previousVersionObject = profileAssociateDataHandler.getPreVerOfProfileAssociatedForResource(resourceList, collectionIdList, profileId);
                if (previousVersionObject.length() > 0) {
                    final SelectQuery selectQuery = profileAssociateDataHandler.getPrevVerOfProfileConfigAssociatedForResourceQuery();
                    final Criteria resourceCriteria = new IOSRestrictWifiProfileUpdateOnWifiRestrictionApplied().getCriteriaForWifiFromPrevObject(previousVersionObject);
                    selectQuery.setCriteria(resourceCriteria);
                    selectQuery.addSelectColumn(new Column("ResourceToProfileHistory", "*"));
                    final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                    if (!dataObject.isEmpty()) {
                        final Iterator resourceIterator = dataObject.getRows("ResourceToProfileHistory");
                        while (resourceIterator.hasNext()) {
                            final Row resourceCollectionRow = resourceIterator.next();
                            prevProfileIdList.add((Long)resourceCollectionRow.get("PROFILE_ID"));
                        }
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            IOSWifiRestrictionResponseListener.LOGGER.log(Level.SEVERE, "Exception while performing DB operation prev restriction profile", (Throwable)e);
        }
        return prevProfileIdList;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
