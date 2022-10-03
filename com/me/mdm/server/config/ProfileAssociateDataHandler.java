package com.me.mdm.server.config;

import java.util.Hashtable;
import com.adventnet.persistence.Row;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.me.mdm.server.apps.appupdatepolicy.AppUpdatesToResourceHandler;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue.AssociationQueueHandler;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.Arrays;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.status.ManagedUserCollectionStatusSummary;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.Map;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.adventnet.persistence.DataObject;

public class ProfileAssociateDataHandler
{
    private DataObject existingResourceProfileDO;
    private DataObject finalDO;
    private DataObject profileDO;
    private DataObject existResourceRelDO;
    private Logger logger;
    private Logger profileDistributionLog;
    
    public ProfileAssociateDataHandler() {
        this.existingResourceProfileDO = null;
        this.finalDO = null;
        this.profileDO = null;
        this.existResourceRelDO = null;
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
    }
    
    public void associateProfileToManagedUser(final Properties props) {
        try {
            this.profileDistributionLog.log(Level.INFO, "associateProfileToManagedUser initiated with props {0}", new Object[] { props });
            final List userList = ((Hashtable<K, List>)props).get("resourceList");
            final HashMap<Long, Long> profileCollectionMap = ((Hashtable<K, HashMap<Long, Long>>)props).get("profileCollectionMap");
            final Long loggedOnUser = ((Hashtable<K, Long>)props).get("loggedOnUser");
            final List<Long> profileList = new ArrayList<Long>(profileCollectionMap.keySet());
            final Boolean isAppConfig = ((Hashtable<K, Boolean>)props).get("isAppConfig");
            Integer profileOrigin = ((Hashtable<K, Integer>)props).get("profileOriginInt");
            final Boolean sendEnrollmentRequest = ((Hashtable<K, Boolean>)props).get("sendEnrollmentRequest");
            if (profileOrigin == null) {
                profileOrigin = 2;
            }
            HashMap profileProperties = ((Hashtable<K, HashMap>)props).get("profileProperties");
            if (profileProperties == null) {
                profileProperties = new HashMap();
            }
            this.populateExistingResourceProfileDO(userList, profileList);
            this.finalDO = MDMUtil.getPersistence().constructDataObject();
            if (profileProperties == null) {
                profileProperties = new HashMap();
            }
            this.updateMDMResourceToProfileDetails(profileList, userList, profileCollectionMap, loggedOnUser, Boolean.FALSE, profileOrigin, profileProperties, isAppConfig, props);
            final Map<Long, List<Long>> collnToApplicableResList = profileProperties.get("collectionToApplicableResource");
            final HashMap devicePlatformMap = ManagedUserHandler.getInstance().getPlatformBasedDeviceIdsForManagedUser(userList);
            ((Hashtable<String, HashMap>)props).put("deviceMap", devicePlatformMap);
            ((Hashtable<String, List<Long>>)props).put("resourceList", ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userList));
            ((Hashtable<String, Boolean>)props).put("isGroup", Boolean.FALSE);
            ProfileAssociateHandler.getInstance().associateCollectionForResource(props);
            MDMUtil.getPersistence().update(this.finalDO);
            for (final Object profileId : profileList) {
                final Long collectionId = profileCollectionMap.get(profileId);
                List<Long> clonedUserList = new ArrayList<Long>(userList);
                if (isAppConfig) {
                    clonedUserList = collnToApplicableResList.get(collectionId);
                }
                ManagedUserCollectionStatusSummary.getInstance().updateManagedUserCollectionStatusSummary(clonedUserList, collectionId);
                if (isAppConfig) {
                    AppsUtil.getInstance().addOrUpdateAppCatalogToUser(clonedUserList, collectionId);
                }
                if (sendEnrollmentRequest != null && sendEnrollmentRequest) {
                    for (final Object user : userList) {
                        final int profilePlatform = ProfileUtil.getInstance().getPlatformType((Long)profileId);
                        if (DBUtil.getColumnValuesAsList(MDMUtil.getPersistence().get("DeviceEnrollmentRequest", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), user, 0).and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)profilePlatform, 0))).getRows("DeviceEnrollmentRequest"), "MANAGED_USER_ID").size() == 0) {
                            new EnrollmentFacade().createEnrollmentRequests(Arrays.asList((Long)user), profilePlatform, ((Hashtable<K, Long>)props).get("customerId"));
                        }
                    }
                }
            }
            com.me.mdm.server.config.ProfileAssociateHandler.getInstance().updateMDMResourceProfileSummary();
            MDMUtil.getPersistence().update(this.finalDO);
        }
        catch (final Exception e) {
            this.profileDistributionLog.log(Level.SEVERE, "Exception in associateProfileToManagedUser", e);
        }
    }
    
    public void associateProfileToDirectoryUser(final Properties props) {
        try {
            this.profileDistributionLog.log(Level.INFO, "associateProfileToDirectoryUser initiated with props {0}", new Object[] { props });
            final List userList = ((Hashtable<K, List>)props).get("resourceList");
            final HashMap<Long, Long> profileCollectionMap = ((Hashtable<K, HashMap<Long, Long>>)props).get("profileCollectionMap");
            final Long loggedOnUser = ((Hashtable<K, Long>)props).get("loggedOnUser");
            final List<Long> profileList = new ArrayList<Long>(profileCollectionMap.keySet());
            final Boolean isAppConfig = ((Hashtable<K, Boolean>)props).get("isAppConfig");
            Integer profileOrigin = ((Hashtable<K, Integer>)props).get("profileOriginInt");
            if (profileOrigin == null) {
                profileOrigin = 2;
            }
            this.populateExistingResourceProfileDO(userList, profileList);
            this.finalDO = MDMUtil.getPersistence().constructDataObject();
            HashMap profileProperties = ((Hashtable<K, HashMap>)props).get("profileProperties");
            if (profileProperties == null) {
                profileProperties = new HashMap();
            }
            this.updateMDMResourceToProfileDetails(profileList, userList, profileCollectionMap, loggedOnUser, Boolean.FALSE, profileOrigin, profileProperties, isAppConfig, props);
            final Map<Long, List<Long>> collnToApplicableResList = profileProperties.get("collectionToApplicableResource");
            if (collnToApplicableResList != null) {
                ((Hashtable<String, Map<Long, List<Long>>>)props).put("collectionToApplicableResource", collnToApplicableResList);
            }
            for (final Object profileId : profileList) {
                final Long collectionId = profileCollectionMap.get(profileId);
                List<Long> clonedUserList = new ArrayList<Long>(userList);
                if (isAppConfig) {
                    clonedUserList = collnToApplicableResList.get(collectionId);
                }
                ManagedUserCollectionStatusSummary.getInstance().updateManagedUserCollectionStatusSummary(clonedUserList, collectionId);
                if (isAppConfig) {
                    AppsUtil.getInstance().addOrUpdateAppCatalogToUser(clonedUserList, collectionId);
                }
            }
            final JSONObject loggedOnuserJson = this.getAssociatedUserJSON(profileProperties, loggedOnUser, profileList);
            ((Hashtable<String, String>)props).put("loggedOnUserJSON", loggedOnuserJson.toString());
            com.me.mdm.server.config.ProfileAssociateHandler.getInstance().updateMDMResourceProfileSummary();
            MDMUtil.getPersistence().update(this.finalDO);
            this.assignUserCommandAsync(props);
        }
        catch (final Exception e) {
            this.profileDistributionLog.log(Level.SEVERE, "Exception in associateProfileToDirectoryUser", e);
        }
    }
    
    public void disassociateProfileFromManagedUser(final Properties props) {
        try {
            this.profileDistributionLog.log(Level.INFO, "disassociateProfileFromManagedUser initiated with props {0}", new Object[] { props });
            final Integer profileOrigin = ((Hashtable<K, Integer>)props).get("profileOriginInt");
            final HashMap<Long, Long> profileCollectionMap = ((Hashtable<K, HashMap<Long, Long>>)props).get("profileCollectionMap");
            final List<Long> userList = ((Hashtable<K, List<Long>>)props).get("resourceList");
            final List<Long> profileList = new ArrayList<Long>(profileCollectionMap.keySet());
            final Long loggedOnUser = ((Hashtable<K, Long>)props).get("loggedOnUser");
            final Boolean isAppConfig = ((Hashtable<K, Boolean>)props).get("isAppConfig");
            HashMap profileProperties = ((Hashtable<K, HashMap>)props).get("profileProperties");
            if (profileProperties == null) {
                profileProperties = new HashMap();
            }
            this.populateExistingResourceProfileDO(userList, profileList);
            this.finalDO = MDMUtil.getPersistence().constructDataObject();
            this.updateMDMResourceToProfileDetails(profileList, userList, profileCollectionMap, loggedOnUser, Boolean.TRUE, profileOrigin, profileProperties, isAppConfig, props);
            final HashMap devicePlatformMap = ManagedUserHandler.getInstance().getPlatformBasedDeviceIdsForManagedUser(userList);
            ((Hashtable<String, HashMap>)props).put("deviceMap", devicePlatformMap);
            if (profileOrigin != 101) {
                ((Hashtable<String, Boolean>)props).put("isGroup", Boolean.FALSE);
            }
            else {
                ((Hashtable<String, Boolean>)props).put("isGroup", Boolean.TRUE);
            }
            ((Hashtable<String, List<Long>>)props).put("resourceList", ManagedUserHandler.getInstance().getManagedDevicesListForManagedUsers(userList));
            ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(props);
            MDMUtil.getPersistence().update(this.finalDO);
            final Iterator collectionItem = profileList.iterator();
            while (collectionItem.hasNext()) {
                final Long collectionId = profileCollectionMap.get(collectionItem.next());
                ManagedUserCollectionStatusSummary.getInstance().updateManagedUserCollectionStatusSummary(userList, collectionId);
                if (isAppConfig) {
                    AppsUtil.getInstance().deleteAppCatalogToUserRelation(userList, collectionId);
                }
            }
        }
        catch (final Exception e) {
            this.profileDistributionLog.log(Level.SEVERE, "Exception in disassociateProfileFromManagedUser()", e);
        }
    }
    
    public void disassociateProfileFromDirectoryUser(final Properties props) {
        try {
            this.profileDistributionLog.log(Level.INFO, "disassociateProfileFromDirectory user initiated with props {0}", new Object[] { props });
            final Integer profileOrigin = ((Hashtable<K, Integer>)props).get("profileOriginInt");
            final HashMap<Long, Long> profileCollectionMap = ((Hashtable<K, HashMap<Long, Long>>)props).get("profileCollectionMap");
            final List<Long> userList = ((Hashtable<K, List<Long>>)props).get("resourceList");
            final List<Long> profileList = new ArrayList<Long>(profileCollectionMap.keySet());
            final Long loggedOnUser = ((Hashtable<K, Long>)props).get("loggedOnUser");
            final Boolean isAppConfig = ((Hashtable<K, Boolean>)props).get("isAppConfig");
            HashMap profileProperties = ((Hashtable<K, HashMap>)props).get("profileProperties");
            if (profileProperties == null) {
                profileProperties = new HashMap();
            }
            this.populateExistingResourceProfileDO(userList, profileList);
            this.finalDO = MDMUtil.getPersistence().constructDataObject();
            this.updateMDMResourceToProfileDetails(profileList, userList, profileCollectionMap, loggedOnUser, Boolean.TRUE, profileOrigin, profileProperties, isAppConfig, props);
            MDMUtil.getPersistence().update(this.finalDO);
            final Iterator collectionItem = profileList.iterator();
            while (collectionItem.hasNext()) {
                final Long collectionId = profileCollectionMap.get(collectionItem.next());
                ManagedUserCollectionStatusSummary.getInstance().updateManagedUserCollectionStatusSummary(userList, collectionId);
                if (isAppConfig) {
                    AppsUtil.getInstance().deleteAppCatalogToUserRelation(userList, collectionId);
                }
            }
            final JSONObject loggedOnuserJson = this.getAssociatedUserJSON(profileProperties, loggedOnUser, profileList);
            ((Hashtable<String, String>)props).put("loggedOnUserJSON", loggedOnuserJson.toString());
            this.assignUserCommandAsync(props);
        }
        catch (final Exception e) {
            this.profileDistributionLog.log(Level.SEVERE, "Exception in disassociateProfileFromManagedUser()", e);
        }
    }
    
    private void assignUserCommandAsync(final Properties properties) throws Exception {
        this.profileDistributionLog.log(Level.INFO, "assignUserCommandAsync initiated with props {0}", new Object[] { properties });
        final Properties taskProps = new Properties();
        if (properties.get("isGroup") != null) {
            final Boolean isGroup = ((Hashtable<K, Boolean>)properties).get("isGroup");
            if (isGroup) {
                ((Hashtable<String, Object>)taskProps).put("groupList", ((Hashtable<K, Object>)properties).get("resourceList"));
            }
            ((Hashtable<String, Object>)taskProps).put("isGroup", ((Hashtable<K, Object>)properties).get("isGroup"));
        }
        else {
            ((Hashtable<String, Boolean>)taskProps).put("isGroup", false);
        }
        ((Hashtable<String, Object>)taskProps).put("usersList", ((Hashtable<K, Object>)properties).get("resourceList"));
        ((Hashtable<String, Object>)taskProps).put("collectionToPlatformMap", ((Hashtable<K, Object>)properties).get("collectionToPlatformMap"));
        ((Hashtable<String, Object>)taskProps).put("profileToPlatformMap", ((Hashtable<K, Object>)properties).get("profileToPlatformMap"));
        ((Hashtable<String, Object>)taskProps).put("profileCollnMap", ((Hashtable<K, Object>)properties).get("profileCollectionMap"));
        ((Hashtable<String, Object>)taskProps).put("isAppConfig", ((Hashtable<K, Object>)properties).get("isAppConfig"));
        ((Hashtable<String, Object>)taskProps).put("UserId", ((Hashtable<K, Object>)properties).get("loggedOnUserJSON"));
        ((Hashtable<String, Object>)taskProps).put("commandName", ((Hashtable<K, Object>)properties).get("commandName"));
        final Boolean isAppConfig = ((Hashtable<K, Boolean>)properties).get("isAppConfig");
        if (isAppConfig && properties.get("isSilentInstall") != null) {
            ((Hashtable<String, Boolean>)taskProps).put("isSilentInstall", ((Hashtable<K, Boolean>)properties).get("isSilentInstall"));
            ((Hashtable<String, Boolean>)taskProps).put("isNotify", ((Hashtable<K, Boolean>)properties).get("isNotify"));
        }
        final HashMap collectionToApplicableResource = ((Hashtable<K, HashMap>)properties).get("collectionToApplicableResource");
        if (collectionToApplicableResource != null) {
            ((Hashtable<String, HashMap>)taskProps).put("collectionToApplicableResource", collectionToApplicableResource);
        }
        final HashMap directlyRemovableRes = ((Hashtable<K, HashMap>)properties).get("collnToProfileDirectRemovalResources");
        if (directlyRemovableRes != null) {
            ((Hashtable<String, HashMap>)taskProps).put("collnToProfileDirectRemovalResources", directlyRemovableRes);
        }
        ((Hashtable<String, Object>)taskProps).put("customerId", ((Hashtable<K, Object>)properties).get("customerId"));
        Integer toBeAssociatedAppSource = ((Hashtable<K, Integer>)properties).get("toBeAssociatedAppSource");
        if (toBeAssociatedAppSource == null) {
            toBeAssociatedAppSource = MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_GROUP_POLICY;
        }
        ((Hashtable<String, Integer>)taskProps).put("toBeAssociatedAppSource", toBeAssociatedAppSource);
        ((Hashtable<String, Integer>)taskProps).put("commandType", 2);
        this.appendSenderDetailsWithTaskProps(taskProps, new JSONObject((String)((Hashtable<K, String>)properties).get("loggedOnUserJSON")));
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "AssignUserCommandTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        AssociationQueueHandler.getInstance().executeTask(taskInfoMap, taskProps);
    }
    
    private void appendSenderDetailsWithTaskProps(final Properties taskProps, final JSONObject loggedOnUserID) {
        try {
            ((Hashtable<String, JSONObject>)taskProps).put("additionalParams", loggedOnUserID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while appending the sender address with Task Properties", e);
        }
    }
    
    private void populateExistingResourceProfileDO(final List resourceList, final List profileList) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            sQuery.addJoin(new Join("Profile", "RecentProfileForMDMResource", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
            sQuery.addJoin(new Join("Profile", "MDMResourceToProfileHistory", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria cProfile = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileList.toArray(), 8);
            final Criteria resourceIDCriteria = new Criteria(new Column("MDMResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            sQuery.setCriteria(cProfile.and(resourceIDCriteria));
            this.existingResourceProfileDO = MDMUtil.getPersistence().get(sQuery);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, null, (Throwable)e);
        }
    }
    
    private void updateMDMResourceToProfileDetails(final List<Long> profileList, final List resourceList, final HashMap<Long, Long> profileCollectionMap, Long loggedInUserId, final Boolean markedForDelete, final int profileOrigin, final HashMap profileProperties, final Boolean isAppConfig, final Properties collnProps) throws Exception {
        final Iterator profileIter = profileList.iterator();
        final Map<Long, List<Long>> collectionToApplicableResourceMap = new HashMap<Long, List<Long>>();
        while (profileIter.hasNext()) {
            final Long profileId = profileIter.next();
            final Long collectionId = profileCollectionMap.get(profileId);
            final int platformType = ProfileUtil.getInstance().getPlatformType(profileId);
            final int profileType = ProfileUtil.getProfileType(profileId);
            List<Long> clonedResourceList = new ArrayList<Long>(resourceList);
            if (isAppConfig && !markedForDelete) {
                final Map<String, List<Long>> retMap = AppVersionHandler.getInstance(platformType).removeUsersContainingLatestVersionOfApp(clonedResourceList, profileId, collectionId, collnProps);
                clonedResourceList = retMap.get("modifiedUserList");
                final List<Long> removedUserList = retMap.get("removedUserList");
                this.profileDistributionLog.log(Level.INFO, "Higher version app already present user list {0} profile Id {1} collection Id {2}", new Object[] { removedUserList, profileId, collectionId });
                collectionToApplicableResourceMap.put(collectionId, clonedResourceList);
                final Properties props = new Properties();
                props.putAll(profileProperties);
                final Map<String, List<Long>> map = AppUpdatesToResourceHandler.getInstance(2).scheduleAppUpdatesForResourceBasedOnPolicy(collectionToApplicableResourceMap.get(collectionId), profileId, collectionId, props);
                final List<Long> scheduledUsers = map.get("scheduledList");
                if (scheduledUsers != null && !scheduledUsers.isEmpty()) {
                    collectionToApplicableResourceMap.get(collectionId).removeAll(scheduledUsers);
                    clonedResourceList.removeAll(scheduledUsers);
                    this.logger.log(Level.INFO, "App update scheduled for profile {0} collection {1} for users {2}", new Object[] { profileId, collectionId, scheduledUsers });
                }
            }
            if (profileType == 10 && !markedForDelete) {
                final Map<String, List<Long>> map2 = AppConfigPolicyDBHandler.getInstance().removeUsersWithOEMProfileFromSameVendor(clonedResourceList, profileId, collectionId);
                clonedResourceList = map2.get("modifiedUserList");
                final List<Long> removedUserList = map2.get("removedUserList");
                this.profileDistributionLog.log(Level.INFO, "OEM app already present user list {0} from same vendor. ProfileId {1} collection Id {2}", new Object[] { removedUserList, profileId, collectionId });
                collectionToApplicableResourceMap.put(collectionId, clonedResourceList);
            }
            if (profileProperties != null) {
                final List keyset = new ArrayList(profileProperties.keySet());
                if (keyset.size() != 0) {
                    loggedInUserId = (Long)profileProperties.get(profileId).get("associatedByUser");
                }
            }
            if (loggedInUserId == null) {
                loggedInUserId = MDMUtil.getInstance().getLoggedInUserID();
            }
            this.addOrUpdateRecentProfileForMDMResource(clonedResourceList, profileId, collectionId, markedForDelete, loggedInUserId);
            this.addOrUpdateMDMResourceToProfileHistory(clonedResourceList, profileId, collectionId, profileOrigin, loggedInUserId);
        }
        if (isAppConfig) {
            profileProperties.put("collectionToApplicableResource", collectionToApplicableResourceMap);
        }
        this.profileDistributionLog.log(Level.INFO, "CollnToApplicableResourec {0}", new Object[] { collectionToApplicableResourceMap });
    }
    
    private void addOrUpdateMDMResourceToProfileHistory(final List<Long> resourceList, final long profileId, final long collectionId, final int profileOrigin, Long loggedInUserId) throws DataAccessException {
        for (final Long resourceId : resourceList) {
            if (loggedInUserId == null) {
                loggedInUserId = com.me.mdm.server.config.ProfileAssociateHandler.getInstance().getAssociatedByForProfile(profileId, resourceId);
            }
            Row historyRow = null;
            if (!this.existingResourceProfileDO.isEmpty()) {
                final Criteria cUser = new Criteria(new Column("MDMResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0);
                final Criteria cProfile = new Criteria(new Column("MDMResourceToProfileHistory", "PROFILE_ID"), (Object)profileId, 0);
                final Criteria cCollection = new Criteria(new Column("MDMResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
                historyRow = this.existingResourceProfileDO.getRow("MDMResourceToProfileHistory", cUser.and(cProfile).and(cCollection));
            }
            if (historyRow == null) {
                historyRow = new Row("MDMResourceToProfileHistory");
                historyRow.set("RESOURCE_ID", (Object)resourceId);
                historyRow.set("PROFILE_ID", (Object)profileId);
                historyRow.set("COLLECTION_ID", (Object)collectionId);
                historyRow.set("ASSOCIATED_BY", (Object)loggedInUserId);
                historyRow.set("COLLECTION_STATUS", (Object)2);
                historyRow.set("PROFILE_ORIGIN", (Object)profileOrigin);
                historyRow.set("ASSOCIATED_TIME", (Object)System.currentTimeMillis());
                historyRow.set("LAST_MODIFIED_BY", (Object)loggedInUserId);
                historyRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                historyRow.set("REMARKS", (Object)"");
                this.finalDO.addRow(historyRow);
            }
            else {
                historyRow.set("LAST_MODIFIED_BY", (Object)loggedInUserId);
                historyRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                historyRow.set("COLLECTION_STATUS", (Object)2);
                historyRow.set("REMARKS", (Object)"");
                this.finalDO.updateBlindly(historyRow);
            }
        }
    }
    
    private void addOrUpdateRecentProfileForMDMResource(final List<Long> resourceList, final Long profileId, final Long collectionId, final Boolean markedForDelete, Long loggedInUserId) throws DataAccessException {
        for (final Long resourceId : resourceList) {
            if (loggedInUserId == null) {
                loggedInUserId = com.me.mdm.server.config.ProfileAssociateHandler.getInstance().getAssociatedByForProfile(profileId, resourceId);
            }
            Row recRow = null;
            if (!this.existingResourceProfileDO.isEmpty()) {
                final Criteria cResource = new Criteria(new Column("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)resourceId, 0);
                final Criteria cProfile = new Criteria(new Column("RecentProfileForMDMResource", "PROFILE_ID"), (Object)profileId, 0);
                recRow = this.existingResourceProfileDO.getRow("RecentProfileForMDMResource", cResource.and(cProfile));
            }
            if (recRow == null) {
                recRow = new Row("RecentProfileForMDMResource");
                recRow.set("RESOURCE_ID", (Object)resourceId);
                recRow.set("PROFILE_ID", (Object)profileId);
                recRow.set("COLLECTION_ID", (Object)collectionId);
                recRow.set("MARKED_FOR_DELETE", (Object)markedForDelete);
                recRow.set("ASSOCIATED_BY", (Object)loggedInUserId);
                recRow.set("ASSOCIATED_TIME", (Object)System.currentTimeMillis());
                recRow.set("LAST_MODIFIED_BY", (Object)loggedInUserId);
                recRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                this.finalDO.addRow(recRow);
            }
            else {
                recRow.set("MARKED_FOR_DELETE", (Object)markedForDelete);
                recRow.set("LAST_MODIFIED_BY", (Object)loggedInUserId);
                recRow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
                recRow.set("COLLECTION_ID", (Object)collectionId);
                this.finalDO.updateBlindly(recRow);
            }
        }
    }
    
    private JSONObject getAssociatedUserJSON(final HashMap profileProperties, final Long userID, final List profileList) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        if (userID != null) {
            for (final Long profileID : profileList) {
                jsonObject.put(profileID.toString(), (Object)userID);
            }
        }
        else {
            for (final Long profileID : profileList) {
                final Long user = (Long)profileProperties.get(profileID).get("associatedByUser");
                jsonObject.put(profileID.toString(), (Object)user);
            }
        }
        return jsonObject;
    }
}
