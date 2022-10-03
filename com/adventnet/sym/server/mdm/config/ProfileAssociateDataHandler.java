package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import org.json.JSONException;
import org.json.JSONArray;
import com.me.mdm.server.seqcommands.BaseSeqCmdStatusUpdateHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue.AssociationQueueHandler;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Set;
import com.adventnet.sym.server.mdm.apps.AppsLicensesHandler;
import com.adventnet.sym.server.mdm.apps.AppsLicensesHandlerEvent;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.server.profiles.ProfileDistributionListHandler;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONObject;
import com.me.mdm.server.tracker.mics.MICSAppDistributionFeatureController;
import com.adventnet.sym.server.mdm.apps.MDDeviceInstalledAppsHandler;
import com.me.mdm.server.deployment.MDMResourceToProfileDeploymentConfigHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Map;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.status.GroupCollectionStatusSummary;
import java.util.HashSet;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.me.mdm.server.config.ProfileAssociateHandler;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.me.mdm.server.apps.appupdatepolicy.AppUpdatesToResourceHandler;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.List;
import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class ProfileAssociateDataHandler
{
    public static Logger logger;
    public static Logger profileDistributionLog;
    private DataObject finalDO;
    private DataObject finalGroupDO;
    private DataObject existGroupProfileDO;
    private DataObject existResourceRelDO;
    private DataObject profileDO;
    private Boolean processAsChucks;
    private Integer chunckSize;
    private Integer profileLenSize;
    private Boolean forceChunkGlobal;
    
    public ProfileAssociateDataHandler() {
        this.finalDO = null;
        this.finalGroupDO = null;
        this.existGroupProfileDO = null;
        this.existResourceRelDO = null;
        this.profileDO = null;
        this.processAsChucks = Boolean.FALSE;
        String chunkSizeStr = null;
        try {
            chunkSizeStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("profile_chunk_size");
            this.chunckSize = ((chunkSizeStr == null) ? 500 : Integer.parseInt(chunkSizeStr));
            final String profileChunkSizeStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("profile_len_size");
            final String forcechunkGlobalStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("profile_len_size");
            this.profileLenSize = ((profileChunkSizeStr == null) ? 5 : Integer.parseInt(profileChunkSizeStr));
            this.forceChunkGlobal = ((forcechunkGlobalStr == null) ? Boolean.FALSE : Boolean.parseBoolean(forcechunkGlobalStr));
        }
        catch (final Exception e) {
            this.chunckSize = 500;
            this.profileLenSize = 5;
            this.forceChunkGlobal = Boolean.FALSE;
        }
    }
    
    public void associateProfileForGroup(final Properties prop) throws Exception {
        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "****** Associate Profile for group initiated {0}", prop);
        Properties properties = new Properties();
        properties = (Properties)prop.clone();
        final List groupList = ((Hashtable<K, List>)properties).get("resourceList");
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution") && MDMGroupHandler.getInstance().isInCycle(groupList)) {
            return;
        }
        final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)properties).get("profileCollectionMap");
        Long userId = ((Hashtable<K, Long>)properties).get("loggedOnUser");
        final List profileList = new ArrayList(profileCollectionMap.keySet());
        final Boolean isAppConfig = ((Hashtable<K, Boolean>)properties).get("isAppConfig");
        final HashMap profileProperties = ((Hashtable<K, HashMap>)properties).get("profileProperties");
        this.finalDO = MDMUtil.getPersistence().constructDataObject();
        this.finalGroupDO = MDMUtil.getPersistence().constructDataObject();
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution")) {
            try {
                final List<Long> subGroups = MDMGroupHandler.getInstance().getSubGroupList(groupList);
                ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "Sub groups found from parent group are {0}", new Object[] { subGroups });
                if (subGroups != null) {
                    groupList.removeAll(subGroups);
                    groupList.addAll(subGroups);
                }
                ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "New groupList after adding sub groups and removing duplicate resource ids are {0}", new Object[] { groupList });
            }
            catch (final SQLException | QueryConstructionException e) {
                throw new SyMException(120001, "SubGroup detection failed", (Throwable)null);
            }
        }
        this.getExistingGroupProfileDO(profileList, groupList);
        if (userId == null) {
            userId = MDMUtil.getInstance().getLoggedInUserID();
            Long DcSystemUserID = null;
            try {
                DcSystemUserID = DMUserHandler.getUserID(EventConstant.DC_SYSTEM_USER);
            }
            catch (final Exception e) {
                ProfileAssociateDataHandler.profileDistributionLog.log(Level.WARNING, "HARMLESS EXCEPTION : caused due to not able to get DC system user in cloud");
            }
            if (DcSystemUserID != null && userId.equals(DcSystemUserID)) {
                userId = null;
            }
        }
        this.getProfileDO(profileList);
        final Iterator profileIter = profileList.iterator();
        final Map<Long, List<Long>> collectionToApplicableResourceMap = new HashMap<Long, List<Long>>();
        while (profileIter.hasNext()) {
            final Long profileID = profileIter.next();
            final Long collectionID = profileCollectionMap.get(profileID);
            final int platformType = this.getProfilePlatformType(profileID);
            final int profileType = this.getProfileType(profileID);
            Long curUser = userId;
            if (profileProperties != null) {
                final HashMap props = profileProperties.get(profileID);
                if (props != null) {
                    final Long profileAssociatedUser = props.get("associatedByUser");
                    if (profileAssociatedUser != null) {
                        curUser = profileAssociatedUser;
                    }
                }
            }
            List<Long> grpListForDist = new ArrayList<Long>(groupList);
            if (profileType == 10) {
                final Map<String, List<Long>> map = AppConfigPolicyDBHandler.getInstance().removeGroupsWithOEMProfileFromSameVendor(groupList, profileID, collectionID);
                grpListForDist = map.get("modifiedGroupList");
                collectionToApplicableResourceMap.put(collectionID, grpListForDist);
                ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "Groups with OEM profile of same vendor present - ProfileId:{0};Collection:{1};GroupList:{2}", new Object[] { profileID, collectionID, map.get("removedGroupList") });
            }
            if (isAppConfig) {
                final Map<String, List<Long>> retMap = AppVersionHandler.getInstance(platformType).removeGroupsContainingLatestVersionOfApp(groupList, profileID, collectionID, prop);
                grpListForDist = retMap.get("modifiedGroupList");
                collectionToApplicableResourceMap.put(collectionID, grpListForDist);
                ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "HigherVersionAppAlreadyPresent - groups removed from distribution list as they already have a higher version of the app - ProfileId:{0};Collection:{1};GroupList:{2}", new Object[] { profileID, collectionID, retMap.get("removedGroupList") });
                final Map<String, List<Long>> map2 = AppUpdatesToResourceHandler.getInstance(101).scheduleAppUpdatesForResourceBasedOnPolicy(grpListForDist, profileID, collectionID, properties);
                final List groupsScheduledList = map2.get("scheduledList");
                if (groupsScheduledList != null && !groupsScheduledList.isEmpty()) {
                    grpListForDist.removeAll(groupsScheduledList);
                    collectionToApplicableResourceMap.get(collectionID).removeAll(groupsScheduledList);
                    ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "Groups scheduled for profile {0} collection {1} groupList {2} and new collection applicable resource map {3}", new Object[] { profileID, collectionID, groupsScheduledList, collectionToApplicableResourceMap.get(collectionID) });
                }
            }
            this.updateGrouptoProfileDetails(profileID, collectionID, grpListForDist, curUser, Boolean.FALSE);
        }
        MDMUtil.getPersistence().update(this.finalGroupDO);
        final List<Long> userGrouList = new ArrayList<Long>();
        final List<Long> deivceGrouList = new ArrayList<Long>();
        final Iterator iter = groupList.iterator();
        while (iter.hasNext()) {
            final HashMap groupMap = CustomGroupUtil.getInstance().getResourceProperties(Long.valueOf(Long.parseLong(iter.next().toString())));
            final Integer groupType = groupMap.get("GROUP_TYPE");
            final Long resID = groupMap.get("GROUP_ID");
            if (groupType == 7) {
                userGrouList.add(resID);
            }
            else {
                deivceGrouList.add(resID);
            }
        }
        ((Hashtable<String, Integer>)properties).put("deploymentSource", 101);
        if (!userGrouList.isEmpty()) {
            final List resList = MDMGroupHandler.getMemberIdListForGroups(groupList, 2);
            if (resList != null) {
                ((Hashtable<String, List>)properties).put("resourceList", resList);
                ((Hashtable<String, Integer>)properties).put("resourceType", 2);
                ((Hashtable<String, List<Long>>)properties).put("configSourceList", userGrouList);
                ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "Profile distributed to the user Group with properties : {0}", properties);
                ProfileAssociateHandler.getInstance().associateCollectionToMDMResource(properties);
            }
            MDMUtil.getPersistence().update(this.finalDO);
        }
        if (!deivceGrouList.isEmpty()) {
            final Boolean associateToDevice = ((Hashtable<K, Boolean>)properties).get("associateToDevice");
            if (associateToDevice == null || associateToDevice) {
                final HashMap devicePlatformMap = MDMCustomGroupUtil.getInstance().getPlatformBasedMemberIdForGroups(groupList);
                final ArrayList resourceList = new ArrayList();
                for (final Object platform : devicePlatformMap.keySet()) {
                    resourceList.addAll(devicePlatformMap.get(platform));
                }
                if (!resourceList.isEmpty()) {
                    ((Hashtable<String, HashMap>)properties).put("deviceMap", devicePlatformMap);
                    ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "Profile distributed to the device with properties : {0}", properties);
                    ((Hashtable<String, List<Long>>)properties).put("configSourceList", deivceGrouList);
                    this.associateProfileForDevice(properties);
                }
                else {
                    ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "Profile distributed to the device with properties : {0}", properties);
                    MDMUtil.getPersistence().update(this.finalDO);
                }
            }
            else if (!associateToDevice && this.finalDO != null && !this.finalDO.isEmpty()) {
                MDMUtil.getPersistence().update(this.finalDO);
            }
        }
        final Iterator collectionItem = profileList.iterator();
        while (collectionItem.hasNext()) {
            final Long collectionId = profileCollectionMap.get(collectionItem.next());
            List<Long> grpListForDist = collectionToApplicableResourceMap.get(collectionId);
            if (grpListForDist == null) {
                grpListForDist = groupList;
            }
            GroupCollectionStatusSummary.getInstance().updateGroupCollectionStatusSummary(grpListForDist, collectionId);
            if (isAppConfig) {
                AppsUtil.getInstance().addOrUpdateAppCatalogToGroup(grpListForDist, collectionId);
            }
        }
        ((Hashtable<String, Map<Long, List<Long>>>)prop).put("collectionToApplicableResource", collectionToApplicableResourceMap);
        com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "****** Associate Profile for group successfully completed ");
    }
    
    public void associateProfileForDevice(final Properties properties) throws DataAccessException, Exception {
        final long startTime = System.currentTimeMillis();
        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "****** Associate Profile for device initiated {0}", properties);
        final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)properties).get("profileCollectionMap");
        final List profileList = new ArrayList(profileCollectionMap.keySet());
        Long userId = null;
        if (properties.containsKey("loggedOnUser")) {
            userId = ((Hashtable<K, Long>)properties).get("loggedOnUser");
        }
        final HashMap profileProperties = ((Hashtable<K, HashMap>)properties).get("profileProperties");
        if (userId == null) {
            userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        }
        HashMap deviceMap = ((Hashtable<K, HashMap>)properties).get("deviceMap");
        if (deviceMap == null) {
            final List resourceList = ((Hashtable<K, List>)properties).get("resourceList");
            deviceMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceList);
            ((Hashtable<String, HashMap>)properties).put("deviceMap", deviceMap);
        }
        final List allDevice = this.getAllDeviceList(deviceMap);
        List excludeList = new ArrayList();
        if (properties.containsKey("excludeList")) {
            excludeList = ((Hashtable<K, List>)properties).get("excludeList");
        }
        allDevice.removeAll(excludeList);
        final Boolean isAppConfig = ((Hashtable<K, Boolean>)properties).get("isAppConfig");
        ((Hashtable<String, List>)properties).put("DeviceList", allDevice);
        this.evaluateChunkFlag(allDevice, profileList);
        if (!this.processAsChucks) {
            this.getExistingDeviceProfileDO(allDevice, profileList);
        }
        this.getProfileDO(profileList);
        if (this.finalDO == null) {
            this.finalDO = MDMUtil.getPersistence().constructDataObject();
        }
        final boolean profileOrigin = ((Hashtable<K, Boolean>)properties).get("profileOrigin");
        final Properties collectionToBusinessStore = new Properties();
        final Map<Long, List<Long>> collnToDevicesToBeIgnoredMap = new HashMap<Long, List<Long>>();
        final Map<Long, List<Long>> collectionToApplicableResourceMap = new HashMap<Long, List<Long>>();
        Properties profileToBusinessStore = new Properties();
        if (isAppConfig) {
            if (!properties.containsKey("profileToBusinessStore")) {
                final List resIDs = new ArrayList(allDevice);
                final List configSourceIDs = ((Hashtable<K, List>)properties).get("configSourceList");
                profileToBusinessStore = new MDMResourceToProfileDeploymentConfigHandler().getProfileToBusinessStoreProp(resIDs, configSourceIDs, profileList);
            }
            else {
                profileToBusinessStore = ((Hashtable<K, Properties>)properties).get("profileToBusinessStore");
            }
        }
        final List<List> profileSubLists = MDMUtil.getInstance().splitListIntoSubLists(new ArrayList(profileList), (this.profileLenSize > 0) ? ((int)this.profileLenSize) : 5);
        final Iterator<List> profileSubListsIterator = (Iterator<List>)profileSubLists.iterator();
        while (profileSubListsIterator.hasNext()) {
            for (final Long profileId : profileSubListsIterator.next()) {
                final Long collectionId = profileCollectionMap.get(profileId);
                if (isAppConfig && profileToBusinessStore != null && !profileToBusinessStore.isEmpty() && profileToBusinessStore.containsKey(profileId)) {
                    ((Hashtable<Long, Object>)collectionToBusinessStore).put(collectionId, ((Hashtable<K, Object>)profileToBusinessStore).get(profileId));
                }
                final int profileType = this.getProfileType(profileId);
                final int platformType = this.getProfilePlatformType(profileId);
                final int aliasPlatformType = this.getAliasProfilePlatformType(profileId);
                final Long propUser = this.getUserIdFromProfileProperties(profileProperties, profileId);
                final Long curUser = (propUser == null) ? userId : propUser;
                List deviceList = new ArrayList(allDevice);
                List applicableDeviceList = new ArrayList();
                final List notApplicableList = new ArrayList();
                String remarks = "--";
                String notApplicableRemarks = "dc.mdm.devicemgmt.not_supported_profile_platform";
                this.setDeviceList(applicableDeviceList, notApplicableList, aliasPlatformType, deviceMap, properties);
                if (isAppConfig) {
                    final Map<String, List<Long>> retMap = AppVersionHandler.getInstance(platformType).removeDevicesContainingLatestVersionOfApp(deviceList, profileId, collectionId, properties);
                    deviceList = retMap.get("modifiedDeviceList");
                    final List<Long> removedDeviceList = retMap.get("removedDeviceList");
                    if (removedDeviceList != null && !removedDeviceList.isEmpty()) {
                        collnToDevicesToBeIgnoredMap.put(collectionId, removedDeviceList);
                        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "HigherVersionAppAlreadyPresent - devices removed from distribution list as they already have a higher version of the app - ProfileId:{0};Collection:{1};DeviceList:{2}", new Object[] { profileId, collectionId, removedDeviceList });
                    }
                    collectionToApplicableResourceMap.put(collectionId, new ArrayList<Long>(deviceList));
                    final Boolean isAppUpgrade = ((Hashtable<K, Boolean>)properties).get("isAppUpgrade");
                    final Boolean isSilentInstall = properties.get("isSilentInstall") != null && ((Hashtable<K, Boolean>)properties).get("isSilentInstall");
                    final Integer packageType = AppsUtil.getInstance().getAppPackageTypeFromCollectionId(collectionId);
                    remarks = com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().getInstallAppFromCatalogRemark(packageType, aliasPlatformType, isAppUpgrade);
                    if (isSilentInstall) {
                        remarks = this.getSilentInstallRemarks(properties);
                    }
                    applicableDeviceList = new MDDeviceInstalledAppsHandler().removeInstalledAppResourceFromList(applicableDeviceList, collectionId);
                    final List<Long> deviceListWhichHaveBetaVersionOfTheAppAlready = collnToDevicesToBeIgnoredMap.get(collectionId);
                    if (deviceListWhichHaveBetaVersionOfTheAppAlready != null && !deviceListWhichHaveBetaVersionOfTheAppAlready.isEmpty()) {
                        applicableDeviceList.removeAll(collnToDevicesToBeIgnoredMap.get(collectionId));
                    }
                    notApplicableRemarks = "dc.mdm.devicemgmt.not_supported_app_platform";
                    collectionToApplicableResourceMap.get(collectionId).removeAll(notApplicableList);
                    final Map<String, List<Long>> map = AppUpdatesToResourceHandler.getInstance(120).scheduleAppUpdatesForResourceBasedOnPolicy(collectionToApplicableResourceMap.get(collectionId), profileId, collectionId, properties);
                    final List collectionScheduledDeviceList = map.get("scheduledList");
                    if (collectionScheduledDeviceList != null && !collectionScheduledDeviceList.isEmpty()) {
                        applicableDeviceList.removeAll(collectionScheduledDeviceList);
                        collectionToApplicableResourceMap.get(collectionId).removeAll(collectionScheduledDeviceList);
                        deviceList.removeAll(collectionScheduledDeviceList);
                        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "App update scheduled for collection {0} - profile {1} - resourceList - {1} new collection applicable resource map", new Object[] { collectionId, profileId, collectionScheduledDeviceList, collectionToApplicableResourceMap.get(collectionId) });
                    }
                }
                if (profileType == 10) {
                    final Map<String, List<Long>> map2 = AppConfigPolicyDBHandler.getInstance().removeDevicesWithOEMProfileFromSameVendor(deviceList, profileId, collectionId);
                    deviceList = map2.get("modifiedDeviceList");
                    final List<Long> removedDeviceList = map2.get("removedDeviceList");
                    if (removedDeviceList != null && !removedDeviceList.isEmpty()) {
                        collnToDevicesToBeIgnoredMap.put(collectionId, removedDeviceList);
                        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "OEM profile already present in devices from same vendor - ProfileId:{0};Collection:{1};DeviceList:{2}", new Object[] { profileId, collectionId, removedDeviceList });
                    }
                    collectionToApplicableResourceMap.put(collectionId, new ArrayList<Long>(deviceList));
                    final List<Long> collectionListToBeIgnored = collnToDevicesToBeIgnoredMap.get(collectionId);
                    if (collectionListToBeIgnored != null && !collectionListToBeIgnored.isEmpty()) {
                        applicableDeviceList.removeAll(collnToDevicesToBeIgnoredMap.get(collectionId));
                        notApplicableList.removeAll(collnToDevicesToBeIgnoredMap.get(collectionId));
                    }
                    collectionToApplicableResourceMap.get(collectionId).removeAll(notApplicableList);
                }
                this.addOrUpdateRecentProfileForResource(deviceList, profileId, collectionId, Boolean.FALSE);
                this.addOrUpdateResourceProfileHistory(deviceList, profileId, collectionId, profileOrigin, Boolean.FALSE, curUser);
                this.addOrUpdateCollnToResources(applicableDeviceList, collectionId, remarks, 12);
                this.addOrUpdateCollnToResources(notApplicableList, collectionId, notApplicableRemarks, 8);
            }
            MDMUtil.getPersistence().update(this.finalDO);
        }
        if (collectionToBusinessStore != null && !collectionToBusinessStore.isEmpty()) {
            ((Hashtable<String, Properties>)properties).put("collectionToBusinessStore", collectionToBusinessStore);
        }
        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "****** Colln added for device successfully completed {0}", System.currentTimeMillis() - startTime);
        if (!collectionToApplicableResourceMap.isEmpty()) {
            ((Hashtable<String, Map<Long, List<Long>>>)properties).put("collectionToApplicableResource", collectionToApplicableResourceMap);
        }
        final JSONObject loggedOnuserJson = this.getAssociatedUserJSON(profileProperties, userId, profileList);
        ((Hashtable<String, String>)properties).put("loggedOnUserJSON", loggedOnuserJson.toString());
        this.setPlatformToProfileCollectionMap(properties);
        com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
        String commandName = properties.getProperty("commandName", null);
        if (isAppConfig) {
            AppsUtil.getInstance().addOrUpdateAppCatalogSync(allDevice);
            if (properties.get("isSilentInstall") != null && ((Hashtable<K, Boolean>)properties).get("isSilentInstall")) {
                MICSAppDistributionFeatureController.addTrackingData(MICSAppDistributionFeatureController.AppDistributionType.Silent_Distribution);
            }
            else {
                MICSAppDistributionFeatureController.addTrackingData(MICSAppDistributionFeatureController.AppDistributionType.App_Catalog);
            }
        }
        if (commandName == null) {
            if (isAppConfig) {
                commandName = "InstallApplication";
            }
            else {
                commandName = "InstallProfile";
            }
        }
        ((Hashtable<String, String>)properties).put("commandName", commandName);
        this.assignDeviceCommandAsync(properties);
        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "****** Associate Profile for device successfully completed {0}", System.currentTimeMillis() - startTime);
    }
    
    private void evaluateChunkFlag(final List allDevice, final List profileList) {
        if (allDevice.size() > this.chunckSize && profileList.size() > this.profileLenSize) {
            this.processAsChucks = Boolean.TRUE;
        }
        if (this.forceChunkGlobal || MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ForceCheckProcessing")) {
            this.processAsChucks = Boolean.TRUE;
        }
    }
    
    public void disassociateProfileForGroup(final Properties prop) throws DataAccessException, Exception {
        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "****** DisAssociate Profile for group initiated {0}", prop);
        final Properties properties = (Properties)prop.clone();
        final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)properties).get("profileCollectionMap");
        final List groupList = ((Hashtable<K, List>)properties).get("resourceList");
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution") && MDMGroupHandler.getInstance().isInCycle(groupList)) {
            return;
        }
        final List profileList = new ArrayList(profileCollectionMap.keySet());
        Long userId = ((Hashtable<K, Long>)properties).get("loggedOnUser");
        final Boolean isAppConfig = ((Hashtable<K, Boolean>)properties).get("isAppConfig");
        final HashMap profileProperties = ((Hashtable<K, HashMap>)properties).get("profileProperties");
        this.finalDO = MDMUtil.getPersistence().constructDataObject();
        this.finalGroupDO = MDMUtil.getPersistence().constructDataObject();
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution")) {
            try {
                final List<Long> subGroups = MDMGroupHandler.getInstance().getSubGroupList(groupList);
                if (subGroups != null) {
                    groupList.addAll(subGroups);
                }
            }
            catch (final SQLException | QueryConstructionException e) {
                throw new SyMException(120001, "SubGroup detection failed", (Throwable)null);
            }
        }
        this.getExistingGroupProfileDO(profileList, groupList);
        if (userId == null) {
            userId = MDMUtil.getInstance().getLoggedInUserID();
            Long DcSystemUserID = null;
            try {
                DcSystemUserID = DMUserHandler.getUserID(EventConstant.DC_SYSTEM_USER);
            }
            catch (final Exception e) {
                ProfileAssociateDataHandler.logger.log(Level.WARNING, "HARMLESS EXCEPTION : caused due to not able to get DC system user in cloud");
            }
            if (DcSystemUserID != null && userId.equals(DcSystemUserID)) {
                userId = null;
            }
        }
        for (final Long profileID : profileList) {
            final Long collectionID = profileCollectionMap.get(profileID);
            Long curUser = userId;
            if (profileProperties != null) {
                final HashMap props = profileProperties.get(profileID);
                if (props != null) {
                    final Long profileAssociatedUser = props.get("associatedByUser");
                    if (profileAssociatedUser != null) {
                        curUser = profileAssociatedUser;
                    }
                }
            }
            this.updateGrouptoProfileDetails(profileID, collectionID, groupList, curUser, Boolean.TRUE);
        }
        MDMUtil.getPersistence().update(this.finalGroupDO);
        final Boolean isGroupListener = (properties.get("isGroupListener") == null) ? false : ((Hashtable<K, Boolean>)properties).get("isGroupListener");
        ((Hashtable<String, Boolean>)properties).put("isGroup", Boolean.TRUE);
        final List<Long> userGrouList = new ArrayList<Long>();
        final List<Long> deivceGrouList = new ArrayList<Long>();
        final Iterator iter = groupList.iterator();
        while (iter.hasNext()) {
            final HashMap groupMap = CustomGroupUtil.getInstance().getResourceProperties(Long.valueOf(Long.parseLong(iter.next().toString())));
            final Integer groupType = groupMap.get("GROUP_TYPE");
            final Long resID = groupMap.get("GROUP_ID");
            if (groupType == 7) {
                userGrouList.add(resID);
            }
            else {
                deivceGrouList.add(resID);
            }
        }
        if (!userGrouList.isEmpty()) {
            final List resList = MDMGroupHandler.getMemberIdListForGroups(groupList, 2);
            if (resList != null && !resList.isEmpty()) {
                ((Hashtable<String, Integer>)properties).put("profileOriginInt", 2);
                ((Hashtable<String, List>)properties).put("resourceList", resList);
                ((Hashtable<String, Integer>)properties).put("resourceType", 2);
                ((Hashtable<String, List<Long>>)properties).put("configSourceList", userGrouList);
                ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "User group profile DisAssociation user group ids {0}", resList);
                ProfileAssociateHandler.getInstance().disassociateCollectionFromMDMResource(properties);
            }
            MDMUtil.getPersistence().update(this.finalDO);
        }
        if (!deivceGrouList.isEmpty()) {
            final HashMap devicePlatformMap = MDMCustomGroupUtil.getInstance().getPlatformBasedMemberIdForGroups(groupList);
            final ArrayList resourceList = new ArrayList();
            for (final Object platform : devicePlatformMap.keySet()) {
                resourceList.addAll(devicePlatformMap.get(platform));
            }
            if (!resourceList.isEmpty()) {
                ((Hashtable<String, HashMap>)properties).put("deviceMap", devicePlatformMap);
                ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
                ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "Device profile DisAssociation device group ids {0}", deivceGrouList);
                ((Hashtable<String, List<Long>>)properties).put("configSourceList", deivceGrouList);
                this.disassociateProfileForDevice(properties);
            }
            else {
                ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "Device profile DisAssociation device group ids {0} for empty devices", deivceGrouList);
                MDMUtil.getPersistence().update(this.finalDO);
            }
        }
        final Iterator collectionItem = profileList.iterator();
        while (collectionItem.hasNext()) {
            final Long collectionId = profileCollectionMap.get(collectionItem.next());
            GroupCollectionStatusSummary.getInstance().updateGroupCollectionStatusSummary(groupList, collectionId);
            if (isAppConfig) {
                AppsUtil.getInstance().deleteAppCatalogToGroupRelation(groupList, collectionId);
            }
        }
    }
    
    public void disassociateProfileForDevice(final Properties properties) throws Exception {
        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "****** DisAssociate Profile for device initiated {0}", properties);
        HashMap devicePlatformMap = ((Hashtable<K, HashMap>)properties).get("deviceMap");
        final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)properties).get("profileCollectionMap");
        final HashMap profileProperties = ((Hashtable<K, HashMap>)properties).get("profileProperties");
        List resourceList = null;
        if (devicePlatformMap == null) {
            resourceList = ((Hashtable<K, List>)properties).get("resourceList");
        }
        else {
            resourceList = new ArrayList();
            for (final Object platform : devicePlatformMap.keySet()) {
                resourceList.addAll(devicePlatformMap.get(platform));
            }
        }
        final List profileList = new ArrayList(profileCollectionMap.keySet());
        Long userId = ((Hashtable<K, Long>)properties).get("loggedOnUser");
        if (userId == null) {
            userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        }
        final Boolean isAppConfig = ((Hashtable<K, Boolean>)properties).get("isAppConfig");
        this.evaluateChunkFlag(resourceList, profileList);
        if (!this.processAsChucks) {
            this.getExistingDeviceProfileDO(resourceList, profileList);
        }
        this.getProfileDO(profileList);
        final ProfileDistributionListHandler handler = ProfileDistributionListHandler.getDistributionProfileListHandler(0);
        List excludeGroupList = null;
        Boolean isGroup = ((Hashtable<K, Boolean>)properties).get("isGroup");
        final Boolean isGroupListener = (properties.get("isGroupListener") == null) ? false : ((Hashtable<K, Boolean>)properties).get("isGroupListener");
        if (isGroup != null && isGroup) {
            excludeGroupList = ((Hashtable<K, List>)properties).get("resourceList");
        }
        else {
            isGroup = false;
        }
        if (this.processAsChucks) {
            this.getProfileResourceDO(resourceList, new ArrayList(profileCollectionMap.keySet()));
        }
        if (this.existResourceRelDO != null && !this.existResourceRelDO.isEmpty()) {
            resourceList = new ArrayList(MDMDBUtil.getColumnValuesAsSet(this.existResourceRelDO.getRows("CollnToResources"), "RESOURCE_ID"));
            if (resourceList.isEmpty()) {
                return;
            }
            devicePlatformMap = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceList);
            ((Hashtable<String, HashMap>)properties).put("deviceMap", devicePlatformMap);
        }
        final List allDevice = new ArrayList(resourceList);
        final HashMap<Long, List> excludeProfileForGroup = handler.getGroupDeviceExcludeProfileMap(allDevice, profileCollectionMap, excludeGroupList);
        final HashMap<Long, List> excludeProfileForDevice = handler.getDeviceExcludeProfileMap(allDevice, profileCollectionMap);
        final HashMap<Long, List> excludeProfileForUserGroup = handler.getUserGroupDeviceExcludeProfileMap(allDevice, profileCollectionMap, excludeGroupList);
        final HashMap<Long, List<Long>> nonAssociatedResources = handler.getNonAssociatedResources(profileList, allDevice);
        if (isAppConfig) {
            List configSourceList = ((Hashtable<K, List>)properties).get("configSourceList");
            if (configSourceList == null || configSourceList.isEmpty()) {
                configSourceList = resourceList;
            }
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(allDevice.get(0)));
            final Properties licenseDetails = new Properties();
            ((Hashtable<String, List>)licenseDetails).put("configSourceList", configSourceList);
            ((Hashtable<String, List>)licenseDetails).put("resourceList", resourceList);
            ((Hashtable<String, List>)licenseDetails).put("profileList", profileList);
            final AppsLicensesHandlerEvent appsLicensesHandlerEvent = new AppsLicensesHandlerEvent(customerID, licenseDetails);
            AppsLicensesHandler.getInstance().invokeLicenseHandlingListener(appsLicensesHandlerEvent, 1);
        }
        final HashMap<Long, List> collnToProfileDirectRemovalResources = this.getDirectlyRemovalResources(profileCollectionMap);
        final HashMap collectionToDirectLicenseRemovalDevices = new HashMap();
        if (this.finalDO == null) {
            this.finalDO = MDMUtil.getPersistence().constructDataObject();
        }
        final Iterator profileIter = profileList.iterator();
        final HashMap<Long, List> collectionToApplicableRes = new HashMap<Long, List>();
        while (profileIter.hasNext()) {
            final Long profileId = profileIter.next();
            final Long collectionId = profileCollectionMap.get(profileId);
            final List collectionRemovalList = collnToProfileDirectRemovalResources.get(collectionId);
            final int platform2 = this.getAliasProfilePlatformType(profileId);
            final List tempResourceList = new ArrayList(resourceList);
            final Set directRemovalList = new HashSet();
            if (platform2 != 0) {
                for (final int platformKey : devicePlatformMap.keySet()) {
                    if (platformKey != platform2) {
                        directRemovalList.addAll(devicePlatformMap.get(platformKey));
                    }
                }
            }
            collectionRemovalList.removeAll(directRemovalList);
            final Set excludedResourceList = new HashSet();
            if (isGroup || isGroupListener) {
                final List groupExclude = excludeProfileForGroup.get(profileId);
                if (groupExclude != null) {
                    tempResourceList.removeAll(groupExclude);
                    collectionRemovalList.removeAll(groupExclude);
                    excludedResourceList.addAll(groupExclude);
                }
                final List userGroupExclude = excludeProfileForUserGroup.get(profileId);
                if (userGroupExclude != null) {
                    tempResourceList.removeAll(userGroupExclude);
                    collectionRemovalList.removeAll(userGroupExclude);
                    excludedResourceList.addAll(userGroupExclude);
                }
                final List deviceExclude = excludeProfileForDevice.get(profileId);
                if (deviceExclude != null) {
                    tempResourceList.removeAll(deviceExclude);
                    collectionRemovalList.removeAll(deviceExclude);
                    excludedResourceList.addAll(deviceExclude);
                }
            }
            final List<Long> nonAssociatedResourceList = nonAssociatedResources.get(profileId);
            nonAssociatedResourceList.removeAll(directRemovalList);
            if (nonAssociatedResourceList != null && nonAssociatedResourceList.size() > 0) {
                tempResourceList.removeAll(nonAssociatedResourceList);
                excludedResourceList.addAll(nonAssociatedResourceList);
                ProfileAssociateDataHandler.logger.log(Level.INFO, "Non associated profile removed from device  profile Id {0} resource List {2}", new Object[] { profileId, nonAssociatedResourceList });
            }
            Long curUser = userId;
            if (profileProperties != null) {
                final HashMap props = profileProperties.get(profileId);
                if (props != null) {
                    final Long profileAssociatedUser = props.get("associatedByUser");
                    if (profileAssociatedUser != null) {
                        curUser = profileAssociatedUser;
                    }
                }
            }
            ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "Disassociation of profile, Resource List : {0} and Profile Id : {1}", new Object[] { tempResourceList, profileId });
            this.addOrUpdateRecentProfileForResource(tempResourceList, profileId, collectionId, Boolean.TRUE);
            this.addOrUpdateResourceProfileHistory(tempResourceList, profileId, collectionId, Boolean.FALSE, Boolean.TRUE, curUser);
            if (directRemovalList != null) {
                tempResourceList.removeAll(directRemovalList);
            }
            if (collectionRemovalList != null) {
                tempResourceList.removeAll(collectionRemovalList);
            }
            collectionToApplicableRes.put(collectionId, new ArrayList(tempResourceList));
            this.addOrUpdateCollnToResources(tempResourceList, collectionId, "", 12, Boolean.TRUE);
            directRemovalList.addAll(collectionRemovalList);
            directRemovalList.removeAll(excludedResourceList);
            ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "Inside disassociate profile for collection:{0} removalResourceList:{1} directRemovalResourceList:{2} collectionRemovalResourceList:{3} ExcludeResourceList:{4}", new Object[] { collectionId, tempResourceList, directRemovalList, collectionRemovalList, excludedResourceList });
            this.addOrUpdateCollnToResources(new ArrayList(directRemovalList), collectionId, "", 6, Boolean.TRUE);
            if (this.processAsChucks) {
                this.getProfileResourceDO(allDevice, new ArrayList(profileCollectionMap.keySet()));
            }
            collectionToDirectLicenseRemovalDevices.put(collectionId, new ArrayList(directRemovalList));
            collnToProfileDirectRemovalResources.put(collectionId, collectionRemovalList);
            this.deleteRecentProfileForResource(new ArrayList(directRemovalList), profileId, collectionId);
            this.handleDirectProfileRemoval(collectionId, new ArrayList(directRemovalList), isAppConfig);
        }
        final JSONObject loggedOnuserJson = this.getAssociatedUserJSON(profileProperties, userId, profileList);
        ((Hashtable<String, String>)properties).put("loggedOnUserJSON", loggedOnuserJson.toString());
        final String commandName = properties.getProperty("commandName", null);
        if (isAppConfig) {
            AppsUtil.getInstance().addOrUpdateAppCatalogSync(allDevice);
        }
        if (commandName == null) {
            if (isAppConfig) {
                ((Hashtable<String, String>)properties).put("commandName", "RemoveApplication");
            }
            else {
                ((Hashtable<String, String>)properties).put("commandName", "RemoveProfile");
            }
        }
        ((Hashtable<String, HashMap<Long, List>>)properties).put("collectionToApplicableResource", collectionToApplicableRes);
        ((Hashtable<String, HashMap>)properties).put("collectionToDirectLicenseRemovalDevices", collectionToDirectLicenseRemovalDevices);
        ((Hashtable<String, HashMap<Long, List>>)properties).put("collnToProfileDirectRemovalResources", collnToProfileDirectRemovalResources);
        MDMUtil.getPersistence().update(this.finalDO);
        com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
        this.setPlatformToProfileCollectionMap(properties);
        this.assignDeviceCommandAsync(properties);
    }
    
    public void setPlatformToProfileCollectionMap(final Properties properties) throws DataAccessException {
        final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)properties).get("profileCollectionMap");
        final List iosProfile = new ArrayList();
        final List androidProfile = new ArrayList();
        final List windowsProfile = new ArrayList();
        final List chromeProfile = new ArrayList();
        final List iosConfig = new ArrayList();
        final List androidConfig = new ArrayList();
        final List windowsConfig = new ArrayList();
        final List chromeConfig = new ArrayList();
        final HashMap profileToPlatformMap = new HashMap();
        final HashMap collectionToPlatformMap = new HashMap();
        final List profileList = new ArrayList(profileCollectionMap.keySet());
        for (final Long profileId : profileList) {
            final Long collectionId = profileCollectionMap.get(profileId);
            final int platform = this.getAliasProfilePlatformType(profileId);
            switch (platform) {
                case 1: {
                    iosProfile.add(profileId);
                    iosConfig.add(collectionId);
                    continue;
                }
                case 2: {
                    androidProfile.add(profileId);
                    androidConfig.add(collectionId);
                    continue;
                }
                case 3: {
                    windowsProfile.add(profileId);
                    windowsConfig.add(collectionId);
                    continue;
                }
                case 4: {
                    chromeProfile.add(profileId);
                    chromeConfig.add(collectionId);
                    continue;
                }
                case 0: {
                    iosProfile.add(profileId);
                    iosConfig.add(collectionId);
                    androidProfile.add(profileId);
                    androidConfig.add(collectionId);
                    windowsProfile.add(profileId);
                    windowsConfig.add(collectionId);
                    chromeProfile.add(profileId);
                    chromeConfig.add(collectionId);
                    continue;
                }
            }
        }
        profileToPlatformMap.put(1, iosProfile);
        profileToPlatformMap.put(2, androidProfile);
        profileToPlatformMap.put(3, windowsProfile);
        profileToPlatformMap.put(4, chromeProfile);
        collectionToPlatformMap.put(1, iosConfig);
        collectionToPlatformMap.put(2, androidConfig);
        collectionToPlatformMap.put(3, windowsConfig);
        collectionToPlatformMap.put(4, chromeConfig);
        ((Hashtable<String, HashMap>)properties).put("profileToPlatformMap", profileToPlatformMap);
        ((Hashtable<String, HashMap>)properties).put("collectionToPlatformMap", collectionToPlatformMap);
    }
    
    private void updateGrouptoProfileDetails(final Long profileID, final Long collectionID, final List groupList, final Long userId, final Boolean markedForDelete) throws DataAccessException {
        this.addOrUpdateRecentProfileForGroup(groupList, profileID, collectionID, markedForDelete);
        this.addOrUpdateGroupToProfileHistory(groupList, profileID, collectionID, userId);
    }
    
    private void addOrUpdateRecentProfileForGroup(final List groupList, final Long profileId, final Long collectionId, final Boolean markedForDelete) throws DataAccessException {
        for (final Object groupId : groupList) {
            Row recRow = null;
            if (!this.existGroupProfileDO.isEmpty()) {
                final Criteria cGroup = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)groupId, 0);
                final Criteria cProfile = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
                recRow = this.existGroupProfileDO.getRow("RecentProfileForGroup", cGroup.and(cProfile));
            }
            if (recRow == null) {
                recRow = new Row("RecentProfileForGroup");
                recRow.set("GROUP_ID", (Object)groupId);
                recRow.set("PROFILE_ID", (Object)profileId);
                recRow.set("COLLECTION_ID", (Object)collectionId);
                recRow.set("MARKED_FOR_DELETE", (Object)markedForDelete);
                this.finalGroupDO.addRow(recRow);
            }
            else {
                recRow.set("MARKED_FOR_DELETE", (Object)markedForDelete);
                recRow.set("COLLECTION_ID", (Object)collectionId);
                this.finalGroupDO.updateBlindly(recRow);
            }
        }
    }
    
    private void addOrUpdateGroupToProfileHistory(final List groupList, final Long profileId, final Long collectionId, final Long userId) throws DataAccessException {
        final Long currentTime = System.currentTimeMillis();
        for (final Object groupId : groupList) {
            Row historyRow = null;
            if (!this.existGroupProfileDO.isEmpty()) {
                final Criteria cGroup = new Criteria(new Column("GroupToProfileHistory", "GROUP_ID"), (Object)groupId, 0);
                final Criteria cProfile = new Criteria(new Column("GroupToProfileHistory", "PROFILE_ID"), (Object)profileId, 0);
                final Criteria cCollection = new Criteria(new Column("GroupToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
                historyRow = this.existGroupProfileDO.getRow("GroupToProfileHistory", cGroup.and(cProfile).and(cCollection));
            }
            if (historyRow == null) {
                historyRow = new Row("GroupToProfileHistory");
                historyRow.set("GROUP_ID", (Object)groupId);
                historyRow.set("PROFILE_ID", (Object)profileId);
                historyRow.set("COLLECTION_ID", (Object)collectionId);
                historyRow.set("ASSOCIATED_BY", (Object)userId);
                historyRow.set("COLLECTION_STATUS", (Object)2);
                historyRow.set("ASSOCIATED_TIME", (Object)currentTime);
                historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                historyRow.set("LAST_MODIFIED_TIME", (Object)currentTime);
                historyRow.set("REMARKS", (Object)"");
                this.finalGroupDO.addRow(historyRow);
            }
            else {
                historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                historyRow.set("LAST_MODIFIED_TIME", (Object)currentTime);
                historyRow.set("COLLECTION_STATUS", (Object)2);
                historyRow.set("REMARKS", (Object)"");
                this.finalGroupDO.updateBlindly(historyRow);
            }
        }
    }
    
    private void addOrUpdateRecentProfileForResource(final List resourceList, final Long profileId, final Long collectionId, final Boolean markForDelete) throws DataAccessException {
        if (this.finalDO == null) {
            this.finalDO = MDMUtil.getPersistence().constructDataObject();
        }
        final List resSplitList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, ((boolean)this.processAsChucks) ? ((int)this.chunckSize) : resourceList.size());
        for (final List curResList : resSplitList) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
            selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "*"));
            final Criteria resCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)curResList.toArray(), 8);
            final Criteria collnCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
            selectQuery.setCriteria(resCriteria.and(collnCriteria));
            if (this.processAsChucks) {
                this.existResourceRelDO = MDMUtil.getPersistenceLite().get(selectQuery);
            }
            for (final Object resourceId : curResList) {
                Row resRow = null;
                if (!this.existResourceRelDO.isEmpty()) {
                    final Criteria cResource = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
                    final Criteria cProfile = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
                    resRow = this.existResourceRelDO.getRow("RecentProfileForResource", cResource.and(cProfile));
                }
                if (resRow == null) {
                    if (markForDelete) {
                        continue;
                    }
                    resRow = new Row("RecentProfileForResource");
                    resRow.set("RESOURCE_ID", (Object)resourceId);
                    resRow.set("PROFILE_ID", (Object)profileId);
                    resRow.set("COLLECTION_ID", (Object)collectionId);
                    resRow.set("MARKED_FOR_DELETE", (Object)markForDelete);
                    this.finalDO.addRow(resRow);
                }
                else {
                    resRow.set("MARKED_FOR_DELETE", (Object)markForDelete);
                    resRow.set("COLLECTION_ID", (Object)collectionId);
                    this.finalDO.updateBlindly(resRow);
                }
            }
        }
    }
    
    private void deleteRecentProfileForResource(final List directRemovalList, final Long profileId, final Long collectionId) throws DataAccessException {
        if (!this.existResourceRelDO.isEmpty()) {
            final Criteria cResource = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)directRemovalList.toArray(), 8);
            final Criteria cProfile = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria cCollection = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 0);
            this.finalDO.deleteRows("RecentProfileForResource", cProfile.and(cResource).and(cCollection));
        }
    }
    
    private void addOrUpdateResourceProfileHistory(final List resourceList, final Long profileId, final Long collectionId, final Boolean profileOrigin, final Boolean markForDelete, final Long userId) throws DataAccessException {
        final List resSplitList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, ((boolean)this.processAsChucks) ? ((int)this.chunckSize) : resourceList.size());
        final Iterator iterator = resSplitList.iterator();
        final long currentTime = System.currentTimeMillis();
        while (iterator.hasNext()) {
            final List curResList = iterator.next();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ResourceToProfileHistory"));
            selectQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "*"));
            final Criteria resCriteria = new Criteria(Column.getColumn("ResourceToProfileHistory", "RESOURCE_ID"), (Object)curResList.toArray(), 8);
            final Criteria collnCriteria = new Criteria(Column.getColumn("ResourceToProfileHistory", "PROFILE_ID"), (Object)profileId, 0);
            selectQuery.setCriteria(resCriteria.and(collnCriteria));
            if (this.processAsChucks) {
                this.existResourceRelDO = MDMUtil.getPersistenceLite().get(selectQuery);
            }
            for (final Object resourceId : curResList) {
                Row historyRow = null;
                if (!this.existResourceRelDO.isEmpty()) {
                    final Criteria cResource = new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceId, 0);
                    final Criteria cCollection = new Criteria(new Column("ResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
                    historyRow = this.existResourceRelDO.getRow("ResourceToProfileHistory", cResource.and(cCollection));
                }
                if (historyRow == null) {
                    if (markForDelete) {
                        continue;
                    }
                    historyRow = new Row("ResourceToProfileHistory");
                    historyRow.set("RESOURCE_ID", (Object)resourceId);
                    historyRow.set("PROFILE_ID", (Object)profileId);
                    historyRow.set("COLLECTION_ID", (Object)collectionId);
                    historyRow.set("ASSOCIATED_BY", (Object)userId);
                    historyRow.set("ASSOCIATED_TIME", (Object)currentTime);
                    historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                    historyRow.set("LAST_MODIFIED_TIME", (Object)currentTime);
                    historyRow.set("PROFILE_ORIGIN_TYPE", (Object)profileOrigin);
                    historyRow.set("REMARKS", (Object)"");
                    this.finalDO.addRow(historyRow);
                }
                else {
                    historyRow.set("LAST_MODIFIED_BY", (Object)userId);
                    historyRow.set("LAST_MODIFIED_TIME", (Object)currentTime);
                    historyRow.set("PROFILE_ORIGIN_TYPE", (Object)profileOrigin);
                    historyRow.set("REMARKS", (Object)"");
                    this.finalDO.updateBlindly(historyRow);
                }
            }
        }
    }
    
    private void addOrUpdateCollnToResources(final List resourceList, final Long collectionId, final String remarks, final int status) throws DataAccessException {
        this.addOrUpdateCollnToResources(resourceList, collectionId, remarks, status, Boolean.FALSE);
    }
    
    private void addOrUpdateCollnToResources(final List resourceList, final Long collectionId, final String remarks, final int status, final Boolean markedForDelete) throws DataAccessException {
        final List resSplitList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, ((boolean)this.processAsChucks) ? ((int)this.chunckSize) : resourceList.size());
        final Iterator iterator = resSplitList.iterator();
        final long currentTime = System.currentTimeMillis();
        while (iterator.hasNext()) {
            final List curResList = iterator.next();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CollnToResources"));
            selectQuery.addJoin(new Join("CollnToResources", "MDMCollnToResErrorCode", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn("CollnToResources", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MDMCollnToResErrorCode", "*"));
            final Criteria resCriteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)curResList.toArray(), 8);
            final Criteria collnCriteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 0);
            selectQuery.setCriteria(resCriteria.and(collnCriteria));
            if (this.processAsChucks) {
                this.existResourceRelDO = MDMUtil.getPersistenceLite().get(selectQuery);
            }
            for (final Object resourceId : curResList.toArray()) {
                Row collectionRow = null;
                Row collectionErrorRow = null;
                if (!this.existResourceRelDO.isEmpty()) {
                    final Criteria cResource = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceId, 0);
                    final Criteria cCollection = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 0);
                    collectionRow = this.existResourceRelDO.getRow("CollnToResources", cResource.and(cCollection));
                    collectionErrorRow = this.existResourceRelDO.getRow("MDMCollnToResErrorCode", cResource.and(cCollection));
                }
                if (collectionRow == null) {
                    if (!markedForDelete) {
                        ProfileAssociateDataHandler.logger.log(Level.INFO, "Inserting resource {0} and collection {1} in CollnToResources", new Object[] { resourceId, collectionId });
                        collectionRow = new Row("CollnToResources");
                        collectionRow.set("COLLECTION_ID", (Object)collectionId);
                        collectionRow.set("RESOURCE_ID", (Object)resourceId);
                        collectionRow.set("STATUS", (Object)status);
                        collectionRow.set("APPLIED_TIME", (Object)currentTime);
                        collectionRow.set("AGENT_APPLIED_TIME", (Object)currentTime);
                        collectionRow.set("REMARKS", (Object)remarks);
                        collectionRow.set("REMARKS_EN", (Object)"--");
                        this.finalDO.addRow(collectionRow);
                    }
                }
                else {
                    collectionRow.set("STATUS", (Object)status);
                    collectionRow.set("APPLIED_TIME", (Object)currentTime);
                    collectionRow.set("AGENT_APPLIED_TIME", (Object)currentTime);
                    collectionRow.set("REMARKS", (Object)remarks);
                    collectionRow.set("REMARKS_EN", (Object)"--");
                    this.finalDO.updateBlindly(collectionRow);
                }
                if (collectionErrorRow != null) {
                    this.finalDO.deleteRow(collectionErrorRow);
                }
            }
        }
    }
    
    private void getExistingGroupProfileDO(final List profileList, final List groupList) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        sQuery.addJoin(new Join("Profile", "GroupToProfileHistory", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        sQuery.addJoin(new Join("GroupToProfileHistory", "RecentProfileForGroup", new String[] { "PROFILE_ID", "GROUP_ID" }, new String[] { "PROFILE_ID", "GROUP_ID" }, 1));
        sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria cProfile = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileList.toArray(), 8);
        final Criteria cGroup = new Criteria(new Column("GroupToProfileHistory", "GROUP_ID"), (Object)groupList.toArray(), 8);
        sQuery.setCriteria(cProfile.and(cGroup));
        this.existGroupProfileDO = MDMUtil.getPersistence().get(sQuery);
    }
    
    private void getExistingDeviceProfileDO(final List resList, final List profileList) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ResourceToProfileHistory"));
        sQuery.addJoin(new Join("ResourceToProfileHistory", "RecentProfileForResource", new String[] { "RESOURCE_ID", "PROFILE_ID" }, new String[] { "RESOURCE_ID", "PROFILE_ID" }, 1));
        sQuery.addJoin(new Join("ResourceToProfileHistory", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 1));
        sQuery.addJoin(new Join("CollnToResources", "MDMCollnToResErrorCode", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 1));
        sQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria cGroup = new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resList.toArray(), 8);
        final Criteria cProfile = new Criteria(new Column("ResourceToProfileHistory", "PROFILE_ID"), (Object)profileList.toArray(), 8);
        sQuery.setCriteria(cGroup.and(cProfile));
        this.existResourceRelDO = MDMUtil.getPersistence().get(sQuery);
    }
    
    private List getAllDeviceList(final HashMap deviceMap) {
        final List allDevice = new ArrayList();
        for (final int platform : deviceMap.keySet()) {
            allDevice.addAll(deviceMap.get(platform));
        }
        return allDevice;
    }
    
    private int getAliasProfilePlatformType(final Long profileId) throws DataAccessException {
        final Criteria cProfile = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0);
        final int platform = (int)this.profileDO.getValue("Profile", "PLATFORM_TYPE", cProfile);
        return (platform == 6 || platform == 7) ? 1 : platform;
    }
    
    private int getProfilePlatformType(final Long profileId) throws DataAccessException {
        final Criteria cProfile = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0);
        final int platform = (int)this.profileDO.getValue("Profile", "PLATFORM_TYPE", cProfile);
        return platform;
    }
    
    private int getProfileType(final Long profileId) throws DataAccessException {
        final Criteria cProfile = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0);
        final int profileType = (int)this.profileDO.getValue("Profile", "PROFILE_TYPE", cProfile);
        return profileType;
    }
    
    private void getProfileDO(final List profileList) throws DataAccessException {
        final Criteria cProfile = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileList.toArray(), 8);
        this.profileDO = MDMUtil.getPersistence().get("Profile", cProfile);
    }
    
    private void assignDeviceCommandAsync(final Properties properties) throws Exception {
        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, " Assign Device Command {0}", properties);
        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, " Assign Device Command {0}", properties);
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
        ((Hashtable<String, Object>)taskProps).put("deviceMap", ((Hashtable<K, Object>)properties).get("deviceMap"));
        ((Hashtable<String, Object>)taskProps).put("collectionToPlatformMap", ((Hashtable<K, Object>)properties).get("collectionToPlatformMap"));
        if (properties.containsKey("collectionToBusinessStore")) {
            ((Hashtable<String, Object>)taskProps).put("collectionToBusinessStore", ((Hashtable<K, Object>)properties).get("collectionToBusinessStore"));
        }
        ((Hashtable<String, Boolean>)taskProps).put("isNewApp", ((Hashtable<K, Boolean>)properties).getOrDefault("isNewApp", false));
        ((Hashtable<String, Object>)taskProps).put("profileToPlatformMap", ((Hashtable<K, Object>)properties).get("profileToPlatformMap"));
        ((Hashtable<String, Object>)taskProps).put("profileCollnMap", ((Hashtable<K, Object>)properties).get("profileCollectionMap"));
        ((Hashtable<String, Object>)taskProps).put("isAppConfig", ((Hashtable<K, Object>)properties).get("isAppConfig"));
        ((Hashtable<String, Object>)taskProps).put("UserId", ((Hashtable<K, Object>)properties).get("loggedOnUserJSON"));
        ((Hashtable<String, Object>)taskProps).put("commandName", ((Hashtable<K, Object>)properties).get("commandName"));
        if (properties.containsKey("isAppUpgrade")) {
            ((Hashtable<String, Object>)taskProps).put("isAppUpgrade", ((Hashtable<K, Object>)properties).get("isAppUpgrade"));
        }
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
        if (properties.containsKey("collectionToDirectLicenseRemovalDevices")) {
            ((Hashtable<String, HashMap<?, ?>>)taskProps).put("collectionToDirectLicenseRemovalDevices", ((Hashtable<K, HashMap<?, ?>>)properties).get("collectionToDirectLicenseRemovalDevices"));
        }
        ((Hashtable<String, Object>)taskProps).put("customerId", ((Hashtable<K, Object>)properties).get("customerId"));
        ((Hashtable<String, Integer>)taskProps).put("commandType", 1);
        Integer toBeAssociatedAppSource = ((Hashtable<K, Integer>)properties).get("toBeAssociatedAppSource");
        if (toBeAssociatedAppSource == null) {
            toBeAssociatedAppSource = MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_GROUP_POLICY;
        }
        ((Hashtable<String, Integer>)taskProps).put("toBeAssociatedAppSource", toBeAssociatedAppSource);
        this.appendSenderDetailsWithTaskProps(taskProps, new JSONObject((String)((Hashtable<K, String>)properties).get("loggedOnUserJSON")));
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "AssignDeviceCommandTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        AssociationQueueHandler.getInstance().executeTask(taskInfoMap, taskProps);
    }
    
    private void assignUserCommandAsync(final Properties properties) throws Exception {
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
        ((Hashtable<String, Object>)taskProps).put("usersList", ((Hashtable<K, Object>)properties).get("usersList"));
        ((Hashtable<String, Object>)taskProps).put("collectionToPlatformMap", ((Hashtable<K, Object>)properties).get("collectionToPlatformMap"));
        ((Hashtable<String, Object>)taskProps).put("profileToPlatformMap", ((Hashtable<K, Object>)properties).get("profileToPlatformMap"));
        ((Hashtable<String, Object>)taskProps).put("profileCollnMap", ((Hashtable<K, Object>)properties).get("profileCollectionMap"));
        ((Hashtable<String, Object>)taskProps).put("isAppConfig", ((Hashtable<K, Object>)properties).get("isAppConfig"));
        ((Hashtable<String, Object>)taskProps).put("UserId", ((Hashtable<K, Object>)properties).get("loggedOnUser"));
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
        this.appendSenderDetailsWithTaskProps(taskProps, new JSONObject((String)((Hashtable<K, String>)properties).get("loggedOnUser")));
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "AssignUserCommandTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        AssociationQueueHandler.getInstance().executeTask(taskInfoMap, taskProps);
    }
    
    private HashMap<Long, List> getDirectlyRemovalResources(final HashMap profileCollectionMap) throws DataAccessException {
        final HashMap<Long, List> collToRes = new HashMap<Long, List>();
        for (final Long profileId : profileCollectionMap.keySet()) {
            final Long collectionId = profileCollectionMap.get(profileId);
            final Criteria collectionCriteria = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 0);
            final List yetToApplyList = new ArrayList();
            final Criteria yetToApply = new Criteria(new Column("CollnToResources", "STATUS"), (Object)new Integer[] { 12, 8, 18 }, 8);
            final Iterator yetToApplyIter = this.existResourceRelDO.getRows("CollnToResources", yetToApply.and(collectionCriteria));
            while (yetToApplyIter.hasNext()) {
                final Row row = yetToApplyIter.next();
                yetToApplyList.add(row.get("RESOURCE_ID"));
            }
            final List alreadyDeletedList = new ArrayList();
            final Criteria alreadyDeleted = new Criteria(new Column("CollnToResources", "STATUS"), (Object)new Integer[] { 6, 8 }, 8, (boolean)Boolean.FALSE);
            final Iterator alreadyDeletedIter = this.existResourceRelDO.getRows("CollnToResources", alreadyDeleted.and(collectionCriteria));
            while (alreadyDeletedIter.hasNext()) {
                final Row row2 = alreadyDeletedIter.next();
                alreadyDeletedList.add(row2.get("RESOURCE_ID"));
            }
            final Criteria resCollectionCriteria = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria notMarkedForDeleteCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)(boolean)Boolean.FALSE, 0, (boolean)Boolean.FALSE).and(new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)yetToApplyList.toArray(), 8));
            final Criteria markedForDeleteCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)(boolean)Boolean.TRUE, 0, (boolean)Boolean.FALSE).and(new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)alreadyDeletedList.toArray(), 8));
            final Criteria criteria = resCollectionCriteria.and(notMarkedForDeleteCriteria.or(markedForDeleteCriteria));
            final Iterator iter = this.existResourceRelDO.getRows("RecentProfileForResource", criteria);
            final List resourceList = new ArrayList();
            while (iter.hasNext()) {
                final Row row3 = iter.next();
                resourceList.add(row3.get("RESOURCE_ID"));
            }
            collToRes.put(collectionId, resourceList);
        }
        return collToRes;
    }
    
    public void associateProfileFromSequencialCmd(final Properties prop) throws DataAccessException {
        final List resourceList = ((Hashtable<K, List>)prop).get("resourceList");
        final Long profileId = ((Hashtable<K, Long>)prop).get("profileID");
        final Long logerOnUser = ((Hashtable<K, Long>)prop).get("UserId");
        final Boolean isAppConfig = ((Hashtable<K, Boolean>)prop).get("isAppConfig");
        final boolean profileOrigin = ((Hashtable<K, Boolean>)prop).get("profileOrigin");
        final Long collectionId = ((Hashtable<K, Long>)prop).get("collectionId");
        final Boolean isBaseCommand = (prop.get("baseCmd") != null) ? ((Hashtable<K, Boolean>)prop).get("baseCmd") : Boolean.FALSE;
        final List profileList = new ArrayList();
        profileList.add(profileId);
        if (this.existResourceRelDO == null) {
            this.getExistingDeviceProfileDO(resourceList, profileList);
        }
        this.addOrUpdateRecentProfileForResource(resourceList, profileId, collectionId, Boolean.FALSE);
        this.addOrUpdateResourceProfileHistory(resourceList, profileId, collectionId, profileOrigin, Boolean.FALSE, logerOnUser);
        if (!isBaseCommand) {
            if (isAppConfig) {
                final String remarks = "mdm.windows.silent_app_remark";
                final List removedInstalledAppResourceList = new MDDeviceInstalledAppsHandler().removeInstalledAppResourceFromList(resourceList, collectionId);
                this.addOrUpdateCollnToResources(removedInstalledAppResourceList, collectionId, remarks, 12);
                AppsUtil.getInstance().addOrUpdateAppCatalogSync(resourceList);
            }
            else {
                this.addOrUpdateCollnToResources(resourceList, collectionId, "--", 12);
            }
        }
        com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
    }
    
    public void commitChangestoDB() throws DataAccessException {
        MDMUtil.getPersistence().update(this.finalDO);
    }
    
    private void handleDirectProfileRemoval(final Long collectionId, final List directRemovalResourceListForCollection, final Boolean appConfig) throws DataAccessException {
        final List collectionList = new ArrayList();
        collectionList.add(collectionId);
        String remarks = "dc.db.mdm.collection.Successfully_removed_the_policy";
        List deleteCommandList = new ArrayList();
        final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
        if (appConfig || appGroupId != null) {
            deleteCommandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "InstallApplication");
            deleteCommandList.addAll(DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "UpdateApplication"));
            AppsUtil.getInstance().deleteAppResourceRel(directRemovalResourceListForCollection, appGroupId);
            new MDDeviceInstalledAppsHandler().removeInstalledAppResourceRelation(directRemovalResourceListForCollection, appGroupId);
            remarks = "dc.db.mdm.collection.Successfully_removed_the_app";
        }
        else {
            deleteCommandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "InstallProfile");
        }
        ProfileAssociateDataHandler.profileDistributionLog.log(Level.INFO, "Profile is directly removed for collectionId:{0} commandId:{1} resourceList:{2}", new Object[] { collectionId, deleteCommandList, directRemovalResourceListForCollection });
        DeviceCommandRepository.getInstance().clearCommandsFromCacheForResources(deleteCommandList, directRemovalResourceListForCollection, 1);
        DeviceCommandRepository.getInstance().deleteResourcesCommands(deleteCommandList, directRemovalResourceListForCollection, 1);
    }
    
    String getSilentInstallRemarks(final Properties properties) {
        String remarks = "--";
        final Boolean isAppUpgrade = ((Hashtable<K, Boolean>)properties).get("isAppUpgrade");
        remarks = ((isAppUpgrade != null && isAppUpgrade) ? "mdm.appmgmt.appsilentupgradedesc" : "mdm.windows.silent_app_remark");
        return remarks;
    }
    
    private void appendSenderDetailsWithTaskProps(final Properties taskProps, final JSONObject loggedOnUserID) {
        try {
            ((Hashtable<String, JSONObject>)taskProps).put("additionalParams", loggedOnUserID);
        }
        catch (final Exception e) {
            ProfileAssociateDataHandler.logger.log(Level.SEVERE, "Exception while appending the sender address with Task Properties", e);
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
    
    public void updateGroupCollectionsForSequentialCmds(final HashMap params) throws Exception {
        final List groupList = params.get("groupList");
        final JSONObject userIdJSON = new JSONObject((String)params.get("userID"));
        final HashMap hashMap = new BaseSeqCmdStatusUpdateHandler().getCollectionsToUpdateForGroup(params);
        final HashMap profileCollectionMap = hashMap.get("profileToCollectionMap");
        final HashMap subTobaseMap = hashMap.get("subProfileToBaseMap");
        final List profileList = new ArrayList(profileCollectionMap.keySet());
        this.finalDO = MDMUtil.getPersistence().constructDataObject();
        this.finalGroupDO = MDMUtil.getPersistence().constructDataObject();
        this.getExistingGroupProfileDO(profileList, groupList);
        for (final Long profileId : profileList) {
            final List pList = new ArrayList();
            pList.add(profileId);
            final Long userId = userIdJSON.getLong(subTobaseMap.get(profileId).toString());
            this.updateGrouptoProfileDetails(profileId, profileCollectionMap.get(profileId), groupList, userId, Boolean.FALSE);
            final Long collectionId = profileCollectionMap.get(profileId);
            GroupCollectionStatusSummary.getInstance().updateGroupCollectionStatusSummary(groupList, collectionId);
            AppsUtil.getInstance().addOrUpdateAppCatalogToGroup(groupList, collectionId);
        }
        MDMUtil.getPersistence().update(this.finalGroupDO);
        com.adventnet.sym.server.mdm.config.ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
        MDMUtil.getPersistence().update(this.finalDO);
    }
    
    public SelectQuery getProfileAssociatedForResourceQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        return selectQuery;
    }
    
    public List getConfigAppliedForResources(final List resourceList, final Integer configId) throws DataAccessException {
        final List configAssociatedList = new ArrayList();
        try {
            if (!resourceList.isEmpty()) {
                final SelectQuery query = this.getProfileAssociatedForResourceQuery();
                final Criteria succeededCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)6, 0);
                final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final Criteria configCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)configId, 0);
                final Criteria associatedCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
                final Criteria finalCriteria = succeededCriteria.and(resourceCriteria).and(configCriteria).and(associatedCriteria);
                query.setCriteria(finalCriteria);
                query.addSelectColumn(new Column("RecentProfileForResource", "*"));
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(query);
                if (!dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("RecentProfileForResource");
                    while (iterator.hasNext()) {
                        final Row profileResourceRow = iterator.next();
                        configAssociatedList.add(profileResourceRow.get("RESOURCE_ID"));
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            ProfileAssociateDataHandler.logger.log(Level.SEVERE, "Exception while accessing data");
            throw e;
        }
        return configAssociatedList;
    }
    
    public JSONObject getRestrictionAppliedOnResource(final List resourceList, final String tableName, final Criteria criteria) throws Exception {
        List appliedResourceList = new ArrayList();
        final JSONObject resourceObject = new JSONObject();
        final HashSet<Long> resourceSet = new HashSet<Long>();
        try {
            if (!resourceList.isEmpty()) {
                final SelectQuery selectQuery = this.getProfileAssociatedForResourceQuery();
                selectQuery.addJoin(new Join("ConfigDataItem", tableName, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
                final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final Criteria succeededCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)6, 0);
                final Criteria appliedCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
                Criteria finalCriteria = resourceCriteria.and(succeededCriteria).and(appliedCriteria);
                if (criteria != null) {
                    finalCriteria = finalCriteria.and(criteria);
                }
                selectQuery.setCriteria(finalCriteria);
                selectQuery.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
                selectQuery.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
                selectQuery.addSelectColumn(new Column("RecentProfileForResource", "COLLECTION_ID"));
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("RecentProfileForResource");
                    while (iterator.hasNext()) {
                        final Row resourceRow = iterator.next();
                        final Long resourceId = (Long)resourceRow.get("RESOURCE_ID");
                        final Long collectionId = (Long)resourceRow.get("COLLECTION_ID");
                        resourceSet.add(resourceId);
                        if (resourceObject.has(resourceId.toString())) {
                            final JSONArray resourceCollectionArray = resourceObject.getJSONArray(resourceId.toString());
                            resourceCollectionArray.put((Object)collectionId);
                        }
                        else {
                            final JSONArray resourceCollectionArray = new JSONArray();
                            resourceCollectionArray.put((Object)collectionId);
                            resourceObject.put(resourceId.toString(), (Object)resourceCollectionArray);
                        }
                    }
                }
            }
            appliedResourceList = new ArrayList(resourceSet);
            resourceObject.put("RESOURCE_ID", (Object)appliedResourceList);
        }
        catch (final DataAccessException e) {
            ProfileAssociateDataHandler.logger.log(Level.SEVERE, "Exception while access data.", (Throwable)e);
            throw e;
        }
        catch (final JSONException e2) {
            ProfileAssociateDataHandler.logger.log(Level.SEVERE, "Exception in json parsing", (Throwable)e2);
            throw e2;
        }
        return resourceObject;
    }
    
    public List getRestrictionAssociatedOnResource(final List resourceList, final String tableName, final Criteria criteria) throws DataAccessException {
        final List appliedResourceList = new ArrayList();
        try {
            if (!resourceList.isEmpty()) {
                final SelectQuery selectQuery = this.getProfileAssociatedForResourceQuery();
                selectQuery.addJoin(new Join("ConfigDataItem", tableName, new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
                Criteria finalCriteria;
                final Criteria resourceCriteria = finalCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                if (criteria != null) {
                    finalCriteria = finalCriteria.and(criteria);
                }
                selectQuery.setCriteria(finalCriteria);
                selectQuery.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
                selectQuery.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("RecentProfileForResource");
                    while (iterator.hasNext()) {
                        final Row resourceRow = iterator.next();
                        appliedResourceList.add(resourceRow.get("RESOURCE_ID"));
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            ProfileAssociateDataHandler.logger.log(Level.SEVERE, "Exception while access data.", (Throwable)e);
            throw e;
        }
        return appliedResourceList;
    }
    
    public SelectQuery getPrevVerOfProfileConfigAssociatedForResourceQuery() {
        final SelectQuery selectQuery = this.getPrevVerOfProfileAssociatedForResourceQuery();
        selectQuery.addJoin(new Join("ResourceToProfileHistory", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        return selectQuery;
    }
    
    public SelectQuery getPrevVerOfProfileAssociatedForResourceQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
        selectQuery.addJoin(new Join("ProfileToCollection", "ResourceToProfileHistory", new String[] { "COLLECTION_ID", "PROFILE_ID" }, new String[] { "COLLECTION_ID", "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ResourceToProfileHistory", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        return selectQuery;
    }
    
    public JSONObject getPreVerOfProfileAssociatedForResource(final List resourceList, final List collectionIds, List profileId) {
        final JSONObject resourceObject = new JSONObject();
        DMDataSetWrapper dataSet = null;
        try {
            if (!resourceList.isEmpty()) {
                if (profileId == null) {
                    profileId = new ProfileHandler().getProfileIDsFromCollectionIDs(collectionIds);
                }
                final SelectQuery selectQuery = this.getPrevVerOfProfileAssociatedForResourceQuery();
                final Criteria resourceCriteria = new Criteria(new Column("ResourceToProfileHistory", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final Criteria notCollectionCriteria = new Criteria(new Column("ResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionIds.toArray(), 9);
                final Criteria succeededCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)6, 0);
                final Criteria profileIdCriteria = new Criteria(new Column("ResourceToProfileHistory", "PROFILE_ID"), (Object)profileId.toArray(), 8);
                final Criteria finalCriteria = resourceCriteria.and(succeededCriteria).and(notCollectionCriteria).and(profileIdCriteria);
                selectQuery.setCriteria(finalCriteria);
                final Column resourceColumn = new Column("ResourceToProfileHistory", "RESOURCE_ID");
                final Column collectionColumn = new Column("ProfileToCollection", "COLLECTION_ID");
                final Column profileColumn = new Column("ProfileToCollection", "PROFILE_ID");
                selectQuery.addSelectColumn(resourceColumn);
                selectQuery.addSelectColumn(collectionColumn);
                selectQuery.addSelectColumn(profileColumn);
                final Column versionColumn = new Column("ProfileToCollection", "PROFILE_VERSION").maximum();
                versionColumn.setColumnAlias("PROFILE_VERSION");
                selectQuery.addSelectColumn(versionColumn);
                final List groupByColumn = new ArrayList();
                groupByColumn.add(resourceColumn);
                groupByColumn.add(collectionColumn);
                groupByColumn.add(profileColumn);
                final GroupByClause groupByClause = new GroupByClause(groupByColumn);
                selectQuery.setGroupByClause(groupByClause);
                dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
                if (dataSet != null) {
                    while (dataSet.next()) {
                        final String resourceId = String.valueOf(dataSet.getValue("RESOURCE_ID"));
                        final Long resourceCollectionId = (Long)dataSet.getValue("COLLECTION_ID");
                        final String resourceProfileId = String.valueOf(dataSet.getValue("PROFILE_ID"));
                        if (resourceObject.has(resourceId)) {
                            final JSONObject profileResourceCollObject = resourceObject.getJSONObject(resourceId);
                            profileResourceCollObject.put(resourceProfileId, (Object)resourceCollectionId);
                        }
                        else {
                            final JSONObject profileResourceCollObject = new JSONObject();
                            profileResourceCollObject.put(resourceProfileId, (Object)resourceCollectionId);
                            resourceObject.put(resourceId, (Object)profileResourceCollObject);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            ProfileAssociateDataHandler.logger.log(Level.SEVERE, "Exception while performing sql in prev associated profile", e);
        }
        return resourceObject;
    }
    
    public void init(final List resList, final List profileList) throws DataAccessException {
        this.getExistingDeviceProfileDO(resList, profileList);
    }
    
    private void getProfileResourceDO(final List resList, final List profileList) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria cGroup = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resList.toArray(), 8);
        final Criteria cProfile = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileList.toArray(), 8);
        selectQuery.setCriteria(cGroup.and(cProfile));
        this.existResourceRelDO = MDMUtil.getPersistence().get(selectQuery);
    }
    
    @Deprecated
    public void updateRecentProfileForResource(final List<Long> resourceId, final Long appGroupId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
        final Criteria c1 = new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), 0);
        selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", c1, 2));
        final Column collectionCol = new Column("MdAppToCollection", "COLLECTION_ID");
        collectionCol.setColumnAlias("collectioncol");
        selectQuery.addSelectColumn(collectionCol);
        selectQuery.addSelectColumn(new Column("MdAppToCollection", "APP_ID"));
        final Column appGrpCol = new Column("MdAppCatalogToResource", "APP_GROUP_ID");
        appGrpCol.setColumnAlias("appgrpcol");
        selectQuery.addSelectColumn(appGrpCol);
        final Column resourceCol = new Column("MdAppCatalogToResource", "RESOURCE_ID");
        resourceCol.setColumnAlias("resourcecol");
        selectQuery.addSelectColumn(resourceCol);
        final Criteria resCri = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId.toArray(), 8);
        final Criteria appGroupCri = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
        selectQuery.setCriteria(resCri.and(appGroupCri));
        final DerivedTable derivedTable = new DerivedTable("usertable", (Query)selectQuery);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("RecentProfileForResource");
        updateQuery.addJoin(new Join(Table.getTable("RecentProfileForResource"), (Table)derivedTable, new String[] { "RESOURCE_ID" }, new String[] { "resourcecol" }, 2));
        final Criteria recentResCri = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId.toArray(), 8);
        final Criteria appGrpCri = new Criteria(Column.getColumn("usertable", "appgrpcol"), (Object)appGroupId, 0);
        updateQuery.setCriteria(recentResCri.and(appGrpCri));
        updateQuery.setUpdateColumn("COLLECTION_ID", (Object)Column.getColumn("usertable", "collectioncol"));
        MDMUtil.getPersistence().update(updateQuery);
    }
    
    private Long getUserIdFromProfileProperties(final HashMap profileProperties, final Long profileId) {
        Long curUser = null;
        if (profileProperties != null) {
            final HashMap props = profileProperties.get(profileId);
            if (props != null) {
                final Long profileAssociatedUser = props.get("associatedByUser");
                if (profileAssociatedUser != null) {
                    curUser = profileAssociatedUser;
                }
            }
        }
        return curUser;
    }
    
    private void setDeviceList(final List applicableDeviceList, final List notApplicableList, final int aliasPlatformType, final HashMap deviceMap, final Properties properties) {
        if (aliasPlatformType == 0) {
            for (final int platformKey : deviceMap.keySet()) {
                applicableDeviceList.addAll(deviceMap.get(platformKey));
            }
        }
        else if (deviceMap == null) {
            applicableDeviceList.addAll(((Hashtable<K, List>)properties).get("resourceList"));
        }
        else {
            applicableDeviceList.addAll(deviceMap.get(aliasPlatformType));
            for (final int platformKey : deviceMap.keySet()) {
                if (platformKey != aliasPlatformType) {
                    notApplicableList.addAll(deviceMap.get(platformKey));
                }
            }
        }
    }
    
    static {
        ProfileAssociateDataHandler.logger = Logger.getLogger("MDMConfigLogger");
        ProfileAssociateDataHandler.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
    }
}
