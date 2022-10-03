package com.adventnet.sym.server.mdm.config.task;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.announcement.handler.AndroidAnnouncementCommandTaskHandler;
import com.me.mdm.server.apps.blacklist.DeviceBlacklistHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONObject;
import com.me.mdm.server.apps.android.afw.GooglePlayEnterpriseBusinessStore;
import com.me.mdm.server.apps.android.afw.appmgmt.PlayStoreAppDistributionRequestHandler;
import com.me.mdm.server.apps.android.afw.appmgmt.AdvPlayStoreAppDistributionHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.adventnet.sym.server.mdm.apps.android.AndroidAppLicenseMgmtHandler;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.profiles.MDMCollectionNotApplicableHandler;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicableHandler;
import java.util.Collection;
import java.util.ArrayList;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AndroidCollectionCommandTask implements CollectionCommandTask
{
    public static Logger profileDistributionLog;
    private Logger logger;
    
    public AndroidCollectionCommandTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void installProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install Profile sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, true, "InstallProfile");
            SeqCmdUtils.getInstance().removeEarlierVersionSeqProfileCommand(resourceList, profileCollectionMap);
        }
        for (int j = 0; j < collectionList.size(); ++j) {
            final Long collectionID = collectionList.get(j);
            final Long profileId = collectionCommandTaskData.getProfileIdForCollection(collectionID);
            final List configIds = MDMConfigUtil.getConfigIds(collectionID);
            final List collectionResourceList = new ArrayList(resourceList);
            if (configIds != null) {
                final List naList = MDMConfigNotApplicableHandler.getInstance(profileId, collectionID).invokeConfigNotApplicationListeners(configIds, new ArrayList<Long>(resourceList), collectionCommandTaskData.getCustomerId());
                collectionResourceList.removeAll(naList);
                AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "Marked following devices {0} for collection {1} as not applicable", new Object[] { naList, collectionList.get(j) });
                final List collectionNAList = new MDMCollectionNotApplicableHandler(collectionID).invokeCollectionNotApplicableListener(configIds, resourceList, collectionCommandTaskData.getCustomerId());
                collectionResourceList.removeAll(collectionNAList);
                AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "Marked following devices {0} for collection List{1} as not applicable", new Object[] { collectionNAList, collectionList.get(j) });
            }
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(collectionResourceList, collectionID, collResConstant, remark);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionID), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, collectionResourceList);
        }
        NotificationHandler.getInstance().SendNotification(resourceList, 2);
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install Profile sub task completed ******");
    }
    
    @Override
    public void uninstallProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall Profile sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final HashMap<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, false, "InstallProfile");
        }
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        final Set finalResourceListToSendNotification = new HashSet();
        for (int j = 0; j < collectionList.size(); ++j) {
            final List clonedResourceList = new ArrayList(collectionToApplicableRes.get(collectionList.get(j)));
            new CollectionCommandHandler().removeInstallProfileCommandFromDevice(clonedResourceList, collectionList, "InstallProfile");
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(j), collResConstant, remark);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(j)), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            finalResourceListToSendNotification.addAll(clonedResourceList);
            SeqCmdUtils.getInstance().removeSeqInstallProfileCmd(resourceList, Arrays.asList(collectionList.get(j)));
        }
        NotificationHandler.getInstance().SendNotification(new ArrayList(finalResourceListToSendNotification), 2);
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "************** Uninstall profile sub task completed *************");
    }
    
    @Override
    public void installApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final Map<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, false, "RemoveApplication");
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, true, "InstallApplication");
        }
        new AndroidAppLicenseMgmtHandler().assignAppForDevices(collectionCommandTaskData.getTaskProperties());
        final DeviceCommandRepository cmdRepo = DeviceCommandRepository.getInstance();
        cmdRepo.addAndroidSyncAppCatalogCmd(resourceList);
        cmdRepo.addApplicationConfigurationCommand(resourceList);
        cmdRepo.addAppPermissionPolicyCommand(resourceList);
        NotificationHandler.getInstance().SendNotification(resourceList, 2);
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App sub task completed **********");
    }
    
    @Override
    public void uninstallApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall App sub task initiated : {0}", collectionCommandTaskData);
        final Long customerId = collectionCommandTaskData.getCustomerId();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final HashMap<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        if (GoogleForWorkSettings.isAFWSettingsConfigured(customerId)) {
            final Map<Long, JSONObject> portalAppDetails = AppsUtil.getInstance().getPortalAppDetails(collectionList);
            final JSONObject playStoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableAndroidDeviceAppPolicy")) {
                final AdvPlayStoreAppDistributionHandler dist = new AdvPlayStoreAppDistributionHandler();
                dist.initialize(customerId, playStoreDetails.getLong("BUSINESSSTORE_ID"));
                dist.removeAppsByDevices(resourceList, collectionList);
            }
            else {
                final PlayStoreAppDistributionRequestHandler dist2 = new PlayStoreAppDistributionRequestHandler();
                final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(playStoreDetails);
                dist2.removeAppsByDevices(new ArrayList(resourceList), portalAppDetails, ebs, collectionToApplicableRes);
                dist2.disAssociateAppsByUsers(new ArrayList(resourceList), portalAppDetails, ebs, playStoreDetails.getLong("BUSINESSSTORE_ID"), collectionToApplicableRes);
                dist2.updateAvailableProductSet(new ArrayList(resourceList), customerId);
            }
        }
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, false, "InstallApplication");
        }
        final int collResConstant = 18;
        final Set finalResourceSetToSendNotification = new HashSet();
        for (int j = 0; j < collectionList.size(); ++j) {
            final List clonedResourceList = new ArrayList(collectionToApplicableRes.get(collectionList.get(j)));
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(j), collResConstant);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(j)), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            finalResourceSetToSendNotification.addAll(clonedResourceList);
        }
        DeviceCommandRepository.getInstance().addAndroidSyncAppCatalogCmd(resourceList);
        final List finalResourceListToSendNotification = new ArrayList();
        finalResourceListToSendNotification.addAll(finalResourceSetToSendNotification);
        NotificationHandler.getInstance().SendNotification(finalResourceListToSendNotification, 2);
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall App sub task completed **********");
    }
    
    @Override
    public void blackListApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Blacklist App sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final int collResConstant = 18;
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoResDO(resourceList, collectionList, collResConstant, "--", Boolean.FALSE);
        final List profileOwnerList = ManagedDeviceHandler.getInstance().getProfileOwnerList(resourceList);
        if (profileOwnerList != null && !profileOwnerList.isEmpty()) {
            final Long commandID = DeviceCommandRepository.getInstance().addCommand("BlacklistAppInContainer");
            new DeviceBlacklistHandler().updateResourcetoBlacklistAppStatus(profileOwnerList, collectionList, 8, 0);
            new DeviceBlacklistHandler().updateResourcetoBlacklistAppStatus(profileOwnerList, collectionList, 1, 1);
            DeviceCommandRepository.getInstance().assignCommandToDevicesInChunck(profileOwnerList, commandID);
            NotificationHandler.getInstance().SendNotification(profileOwnerList, 2);
            resourceList.removeAll(profileOwnerList);
        }
        if (resourceList != null && !resourceList.isEmpty()) {
            Long commandID = DeviceCommandRepository.getInstance().addCommand("BlacklistAppInDevice");
            DeviceCommandRepository.getInstance().assignCommandToDevicesInChunck(resourceList, commandID);
            final List containerResList = this.getResourceListWithWorkContainerForBlacklist(resourceList, collectionList);
            if (containerResList.size() != 0) {
                commandID = DeviceCommandRepository.getInstance().addCommand("BlacklistAppInContainer");
                new DeviceBlacklistHandler().updateResourcetoBlacklistAppStatus(containerResList, collectionList, 1, 1);
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandID, containerResList);
            }
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
        }
    }
    
    @Override
    public void removeBlacklisting(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** remove Blacklist App sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final int collResConstant = 18;
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoResDO(resourceList, collectionList, collResConstant, "--", Boolean.FALSE);
        final List profileOwnerList = ManagedDeviceHandler.getInstance().getProfileOwnerList(resourceList);
        if (profileOwnerList != null && !profileOwnerList.isEmpty()) {
            final Long commandID = DeviceCommandRepository.getInstance().addCommand("RemoveBlacklistAppInContainer");
            new DeviceBlacklistHandler().updateResourcetoBlacklistAppStatus(profileOwnerList, collectionList, 8, 0);
            new DeviceBlacklistHandler().updateResourcetoBlacklistAppStatus(profileOwnerList, collectionList, 6, 1);
            DeviceCommandRepository.getInstance().assignCommandToDevicesInChunck(profileOwnerList, commandID);
            NotificationHandler.getInstance().SendNotification(profileOwnerList, 2);
            resourceList.removeAll(profileOwnerList);
        }
        if (resourceList != null && !resourceList.isEmpty()) {
            Long commandID = DeviceCommandRepository.getInstance().addCommand("RemoveBlacklistAppInDevice");
            DeviceCommandRepository.getInstance().assignCommandToDevicesInChunck(resourceList, commandID);
            final List containerResList = this.getResourceListWithWorkContainerForBlacklist(resourceList, collectionList);
            if (containerResList.size() != 0) {
                commandID = DeviceCommandRepository.getInstance().addCommand("RemoveBlacklistAppInContainer");
                new DeviceBlacklistHandler().updateResourcetoBlacklistAppStatus(containerResList, collectionList, 6, 1);
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandID, containerResList);
            }
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
        }
    }
    
    @Override
    public void installDataUsageProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install Data Profile sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, true, "InstallDataProfile");
        }
        for (int j = 0; j < collectionList.size(); ++j) {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionList.get(j), collResConstant, remark);
        }
        final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, collectionCommandTaskData.getCommandName());
        DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceList);
        NotificationHandler.getInstance().SendNotification(resourceList, 2);
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install Data Profile sub task completed ******");
    }
    
    @Override
    public void removeDataUsageProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall Data Profile sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final HashMap<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, false, "RemoveDataProfile");
        }
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        final Set finalResourceListToSendNotification = new HashSet();
        for (int j = 0; j < collectionList.size(); ++j) {
            final List clonedResourceList = new ArrayList(collectionToApplicableRes.get(collectionList.get(j)));
            new CollectionCommandHandler().removeInstallProfileCommandFromDevice(clonedResourceList, collectionList, "InstallDataProfile");
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(j), collResConstant, remark);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(j)), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            finalResourceListToSendNotification.addAll(clonedResourceList);
            SeqCmdUtils.getInstance().removeSeqInstallProfileCmd(resourceList, collectionList);
        }
        NotificationHandler.getInstance().SendNotification(new ArrayList(finalResourceListToSendNotification), 2);
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "************** Uninstall Data profile sub task completed *************");
    }
    
    @Override
    public void installAppConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App Config sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final Map<Long, List> collnToApplicableResource = collectionCommandTaskData.getCollectionToApplicableResource();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collnToApplicableResource != null && !collnToApplicableResource.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collnToApplicableResource, profileCollectionMap, true, "InstallApplicationConfiguration");
        }
        final Set finalResourceListToSendNotification = new HashSet();
        for (int i = 0; i < collectionList.size(); ++i) {
            final List clonedResourceList = new ArrayList(collnToApplicableResource.get(collectionList.get(i)));
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(i), collResConstant, remark);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(i)), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            finalResourceListToSendNotification.addAll(clonedResourceList);
        }
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App Config sub task completed ******");
        NotificationHandler.getInstance().SendNotification(new ArrayList(finalResourceListToSendNotification), 2);
    }
    
    @Override
    public void removeAppConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Remove app config sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final Map<Long, List> collnToApplicableResource = collectionCommandTaskData.getCollectionToApplicableResource();
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collnToApplicableResource != null && !collnToApplicableResource.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collnToApplicableResource, profileCollectionMap, false, "RemoveApplicationConfiguration");
        }
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        final Set finalResourceListToSendNotification = new HashSet();
        for (int i = 0; i < collectionList.size(); ++i) {
            final List clonedResourceList = collnToApplicableResource.get(collectionList.get(i));
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(i), collResConstant, remark);
            new CollectionCommandHandler().removeInstallProfileCommandFromDevice(clonedResourceList, Arrays.asList(collectionList.get(i)), "InstallApplicationConfiguration");
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(i)), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            finalResourceListToSendNotification.addAll(clonedResourceList);
        }
        NotificationHandler.getInstance().SendNotification(new ArrayList(finalResourceListToSendNotification), 2);
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "************** Remove app config sub task completed *************");
    }
    
    @Override
    public void installAnnouncement(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        new AndroidAnnouncementCommandTaskHandler().executeInstallCommandForDevice(collectionCommandTaskData);
    }
    
    @Override
    public void installScheduleConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "Installing schedule configuration for the commandTaskData", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, true, "InstallScheduleConfiguration");
        }
        for (int j = 0; j < collectionList.size(); ++j) {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionList.get(j), collResConstant, remark);
        }
        final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, collectionCommandTaskData.getCommandName());
        DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceList);
        NotificationHandler.getInstance().SendNotification(resourceList, 2);
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "Installing schedule components is complete");
    }
    
    @Override
    public void removeScheduleConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Remove ScheduleConfig ******* : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final Map<Long, List> collnToApplicableResource = collectionCommandTaskData.getCollectionToApplicableResource();
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collnToApplicableResource != null && !collnToApplicableResource.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collnToApplicableResource, profileCollectionMap, false, "RemoveScheduleConfiguration");
        }
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        for (int i = 0; i < collectionList.size(); ++i) {
            final List clonedResourceList = resourceList;
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(i), collResConstant, remark);
            new CollectionCommandHandler().removeInstallProfileCommandFromDevice(clonedResourceList, Arrays.asList(collectionList.get(i)), "InstallScheduleConfiguration");
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(i)), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            NotificationHandler.getInstance().SendNotification(clonedResourceList, 2);
        }
        AndroidCollectionCommandTask.profileDistributionLog.log(Level.INFO, "************** Remove Schedule Config *************");
    }
    
    private List getResourceListWithWorkContainerForBlacklist(final List resourceList, final List collectionList) {
        final List reslist = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppToCollection"));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdInstalledAppResourceRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria scopeCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)1, 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
        final Criteria resCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        selectQuery.setCriteria(collectionCriteria.and(scopeCriteria).and(resCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("MdInstalledAppResourceRel");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long resID = (Long)row.get("RESOURCE_ID");
                if (!reslist.contains(resID)) {
                    reslist.add(resID);
                }
            }
        }
        catch (final DataAccessException e) {
            AndroidCollectionCommandTask.profileDistributionLog.log(Level.WARNING, "unable to add blacklist command for container", (Throwable)e);
        }
        return reslist;
    }
    
    static {
        AndroidCollectionCommandTask.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
    }
}
