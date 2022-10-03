package com.adventnet.sym.server.mdm.config.task;

import java.util.Hashtable;
import com.me.mdm.server.apps.blacklist.DeviceBlacklistHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.adventnet.sym.server.mdm.apps.AppLicenseMgmtHandler;
import org.apache.commons.collections.ListUtils;
import com.me.mdm.server.profiles.windows.WindowsCustomProfileHandler;
import java.util.Properties;
import java.util.Map;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import java.util.HashMap;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
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

public class WindowsCollectionCommandTask implements CollectionCommandTask
{
    public static Logger profileDistributionLog;
    
    @Override
    public void installProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install Profile sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, true, "InstallProfile");
            SeqCmdUtils.getInstance().removeEarlierVersionSeqProfileCommand(resourceList, profileCollectionMap);
        }
        for (int j = 0; j < collectionList.size(); ++j) {
            final Long collectionID = collectionList.get(j);
            final Long profileID = collectionCommandTaskData.getProfileIdForCollection(collectionID);
            final List configIds = MDMConfigUtil.getConfigIds(collectionID);
            final List resourceListClone = new ArrayList(resourceList);
            List naList = MDMConfigNotApplicableHandler.getInstance(profileID, collectionID).invokeConfigNotApplicationListeners(configIds, resourceListClone, collectionCommandTaskData.getCustomerId());
            WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "Marked following devices {0} for collection {1} as not applicable for install", new Object[] { naList, collectionID });
            naList = new MDMCollectionNotApplicableHandler(collectionID).invokeCollectionNotApplicableListener(configIds, resourceListClone, collectionCommandTaskData.getCustomerId());
            WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "Marked following devices {0} for collection List{1} as not applicable for install", new Object[] { naList, collectionList.get(j) });
            final Map<String, List> devicesSeparatedMap = ManagedDeviceHandler.getInstance().getWindows8AndWindows81DevicesAsSeparateList(resourceListClone);
            final List windows81AboveDevices = devicesSeparatedMap.get("windows81AboveDevices");
            final List windows8Devices = devicesSeparatedMap.get("windows8Devices");
            collResConstant = 18;
            if (configIds.contains(607) && ProfileUtil.getInstance().isClientCertificateProfile(collectionList.get(j), 607, Arrays.asList("pfx"))) {
                final List win10Devices = ManagedDeviceHandler.getInstance().getDevicesEqualOrAboveOsVersion(windows81AboveDevices, "10");
                final List win81Devices = new ArrayList(windows81AboveDevices);
                win81Devices.removeAll(win10Devices);
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(win10Devices, collectionID, collResConstant, remark);
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(win81Devices, collectionID, collResConstant, "dc.db.mdm.collection.Cert_payload_not_compatible");
            }
            else {
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(windows81AboveDevices, collectionID, collResConstant, remark);
            }
            collResConstant = 12;
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(windows8Devices, collectionID, collResConstant, remark);
            if (!configIds.contains(608)) {
                final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionID), collectionCommandTaskData.getCommandName());
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceListClone);
            }
        }
        final List commandList2 = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, collectionCommandTaskData.getCommandName());
        final List clonedCommandList = new ArrayList(commandList2);
        final JSONObject seqCmdParams = new JSONObject();
        seqCmdParams.put("UserId", (Object)collectionCommandTaskData.getLoggedInUserId());
        seqCmdParams.put("commandName", (Object)collectionCommandTaskData.getCommandName());
        SeqCmdRepository.getInstance().executeSequentially(resourceList, commandList2, seqCmdParams);
        final Properties taskProps = collectionCommandTaskData.getTaskProperties();
        final boolean isGroup = ((Hashtable<K, Boolean>)taskProps).get("isGroup");
        if (isGroup) {
            final List groupList = ((Hashtable<K, List>)taskProps).get("groupList");
            final HashMap params = new HashMap();
            params.put("groupList", groupList);
            params.put("userID", collectionCommandTaskData.getLoggedInUserId());
            params.put("commandList", clonedCommandList);
            params.put("commandType", collectionCommandTaskData.getCommandName());
            new ProfileAssociateDataHandler().updateGroupCollectionsForSequentialCmds(params);
        }
        NotificationHandler.getInstance().SendNotification(resourceList, collectionCommandTaskData.getPlatform());
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install Profile sub task completed ******");
    }
    
    @Override
    public void uninstallProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall Profile sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final HashMap<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, false, "InstallProfile");
        }
        int collResConstant = 18;
        for (int j = 0; j < collectionList.size(); ++j) {
            final Long collectionId = collectionList.get(j);
            final List clonedResourceList = new ArrayList(collectionToApplicableRes.get(collectionId));
            final Long profileID = collectionCommandTaskData.getProfileIdForCollection(collectionId);
            final List configIds = MDMConfigUtil.getConfigIds(collectionId);
            List naList = MDMConfigNotApplicableHandler.getInstance(profileID, collectionId).invokeConfigNotApplicableRemoveListeners(configIds, clonedResourceList, new ArrayList<Long>());
            WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "Marked following devices {0} for collection {1} as not applicable for uninstall", new Object[] { naList, collectionId });
            naList = new MDMCollectionNotApplicableHandler(collectionId).invokeCollectionNotApplicableRemoveListener(configIds, clonedResourceList, collectionCommandTaskData.getCustomerId());
            WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "Marked following devices {0} for collection List{1} as not applicable for uninstall", new Object[] { naList, collectionList.get(j) });
            new CollectionCommandHandler().removeInstallProfileCommandFromDevice(clonedResourceList, Arrays.asList(collectionId), "InstallProfile");
            final Map<String, List> devicesSeparatedMap = ManagedDeviceHandler.getInstance().getWindows8AndWindows81DevicesAsSeparateList(clonedResourceList);
            final List windows81AboveDevices = devicesSeparatedMap.get("windows81AboveDevices");
            final List windows8Devices = devicesSeparatedMap.get("windows8Devices");
            if (new WindowsCustomProfileHandler().isRemoveProfileEmpty(collectionId)) {
                collResConstant = 6;
                final List combinedList = ListUtils.union(windows8Devices, windows81AboveDevices);
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(combinedList, collectionId, collResConstant, "dc.db.mdm.collection.Successfully_removed_the_policy");
                ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(combinedList, collectionCommandTaskData.getProfileIdForCollection(collectionId), collectionId);
                ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
            }
            else {
                collResConstant = 18;
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(windows81AboveDevices, collectionId, collResConstant);
                collResConstant = 12;
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(windows8Devices, collectionId, collResConstant);
                final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionId), collectionCommandTaskData.getCommandName());
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
                NotificationHandler.getInstance().SendNotification(clonedResourceList, collectionCommandTaskData.getPlatform());
                SeqCmdUtils.getInstance().removeSeqInstallProfileCmd(clonedResourceList, Arrays.asList(collectionId));
            }
        }
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "************** Uninstall profile sub task completed *************");
    }
    
    @Override
    public void installApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final Map<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, false, "RemoveApplication");
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, true, "InstallApplication");
        }
        else {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, true, "InstallApplication");
        }
        final Properties taskProps = collectionCommandTaskData.getTaskProperties();
        if (!collectionCommandTaskData.isAppSilentInstall()) {
            new AppLicenseMgmtHandler().assignAppForDevices(taskProps);
            final List devicesWithNotificationCaps = WpCompanyHubAppHandler.getInstance().getResourcesWithLocationSupportComptabileAgent(null);
            DeviceCommandRepository.getInstance().addSyncAppCatalogCommand(devicesWithNotificationCaps);
            DeviceCommandRepository.getInstance().addAppCatalogStatusSummaryCommand(devicesWithNotificationCaps);
            NotificationHandler.getInstance().SendNotification(devicesWithNotificationCaps, 303);
        }
        else {
            final List collectionCmdList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionCommandTaskData.getCollectionList(), "InstallApplication");
            final JSONObject seqCmdParams = new JSONObject();
            seqCmdParams.put("UserId", (Object)collectionCommandTaskData.getLoggedInUserId());
            seqCmdParams.put("commandName", (Object)collectionCommandTaskData.getCommandName());
            seqCmdParams.put("IsAppSilentInstall", true);
            seqCmdParams.put("isMSIJson", (Object)ProfileUtil.getInstance().getWindowsAppMSICommand(collectionCommandTaskData.getCollectionList(), collectionCmdList));
            seqCmdParams.put("collectionToApplicableResource", (Object)AppVersionDBUtil.getInstance().convertCollnToResListAsJSON(collectionToApplicableRes));
            AppsUtil.getInstance().setAppUpdateForResource(collectionToApplicableRes, collectionCommandTaskData.getCollectionList(), false);
            SeqCmdRepository.getInstance().executeSequentially(resourceList, collectionCmdList, seqCmdParams);
            NotificationHandler.getInstance().SendNotification(resourceList, collectionCommandTaskData.getPlatform());
        }
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App sub task completed **********");
    }
    
    @Override
    public void uninstallApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall App sub task initiated : {0}", collectionCommandTaskData);
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final HashMap<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, false, "InstallApplication");
        }
        int collResConstant = 18;
        for (int j = 0; j < collectionList.size(); ++j) {
            final List clonedResourceList = new ArrayList(collectionToApplicableRes.get(collectionList.get(j)));
            SeqCmdUtils.getInstance().removeSeqInstallAppCmd(clonedResourceList, Arrays.asList(collectionList.get(j)));
            final Map<String, List> devicesSeparatedMap = ManagedDeviceHandler.getInstance().getWindows8AndWindows81DevicesAsSeparateList(clonedResourceList);
            final List windows81AboveDevices = devicesSeparatedMap.get("windows81AboveDevices");
            final List windows8Devices = devicesSeparatedMap.get("windows8Devices");
            collResConstant = 18;
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(windows81AboveDevices, collectionList.get(j), collResConstant);
            collResConstant = 12;
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(windows8Devices, collectionList.get(j), collResConstant);
            final List devicesWithNotificationCaps = WpCompanyHubAppHandler.getInstance().getResourcesWithLocationSupportComptabileAgent(resourceList);
            DeviceCommandRepository.getInstance().addSyncAppCatalogCommand(devicesWithNotificationCaps);
            DeviceCommandRepository.getInstance().addAppCatalogStatusSummaryCommand(devicesWithNotificationCaps);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(j)), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            NotificationHandler.getInstance().SendNotification(clonedResourceList, 3);
            NotificationHandler.getInstance().SendNotification(devicesWithNotificationCaps, 303);
        }
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall App sub task completed **********");
    }
    
    @Override
    public void blackListApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** BlackListing App sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        for (int j = 0; j < collectionList.size(); ++j) {
            if (collectionList.size() != 0) {
                final List clonedList = new ArrayList(resourceList);
                final List windows81AndBelowDeviceList = ManagedDeviceHandler.getInstance().getWindows81AndBelowManagedDeviceResourceIDs(clonedList);
                clonedList.removeAll(windows81AndBelowDeviceList);
                String remarks = "mdm.applicable.10above";
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(windows81AndBelowDeviceList, collectionList, 8, remarks);
                new DeviceBlacklistHandler().updateResourcetoBlacklistAppStatus(windows81AndBelowDeviceList, collectionList, 5, 0);
                if (clonedList.size() != 0) {
                    remarks = "--";
                    final Long commandID = DeviceCommandRepository.getInstance().addCommand("BlacklistAppInDevice");
                    DeviceCommandRepository.getInstance().assignCommandToDevicesInChunck(clonedList, commandID);
                    NotificationHandler.getInstance().SendNotification(clonedList, 3);
                    MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedList, collectionList, 18, remarks);
                    new DeviceBlacklistHandler().updateResourcetoBlacklistAppStatus(clonedList, collectionList, 1, 0);
                }
            }
        }
    }
    
    @Override
    public void removeBlacklisting(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Remove BlackListing App sub task initiated : {0}", collectionCommandTaskData);
        final HashMap<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        for (int j = 0; j < collectionList.size(); ++j) {
            if (collectionList.size() != 0) {
                final List resourceList = collectionToApplicableRes.get(collectionList.get(j));
                final List windows81AndBelowDeviceList = ManagedDeviceHandler.getInstance().getWindows81AndBelowManagedDeviceResourceIDs(resourceList);
                resourceList.removeAll(windows81AndBelowDeviceList);
                String remarks = "mdm.applicable.10above";
                MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(windows81AndBelowDeviceList, collectionList, 8, remarks);
                new DeviceBlacklistHandler().updateResourcetoBlacklistAppStatus(windows81AndBelowDeviceList, collectionList, 5, 0);
                if (resourceList.size() != 0) {
                    remarks = "notification sent";
                    final Long commandID = DeviceCommandRepository.getInstance().addCommand("RemoveBlacklistAppInDevice");
                    DeviceCommandRepository.getInstance().assignCommandToDevicesInChunck(resourceList, commandID);
                    NotificationHandler.getInstance().SendNotification(resourceList, 3);
                    MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionList, 18, remarks);
                }
            }
        }
    }
    
    @Override
    public void installDataUsageProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionList, 8, "--");
    }
    
    @Override
    public void removeDataUsageProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionList, 8, "--");
    }
    
    @Override
    public void installAppConfiguration(final CollectionCommandTaskData collectionCommandTaskData) {
        throw new UnsupportedOperationException("App Configuration policy not supported for windows");
    }
    
    @Override
    public void removeAppConfiguration(final CollectionCommandTaskData collectionCommandTaskData) {
        throw new UnsupportedOperationException("App Configuration policy not supported for windows");
    }
    
    @Override
    public void installAnnouncement(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        throw new UnsupportedOperationException("Operation not supported");
    }
    
    @Override
    public void installScheduleConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "No command Is added for resource as it is not agent delegated");
    }
    
    @Override
    public void removeScheduleConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        WindowsCollectionCommandTask.profileDistributionLog.log(Level.INFO, "No command Is added for resource as it is not agent delegated");
    }
    
    static {
        WindowsCollectionCommandTask.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
    }
}
