package com.adventnet.sym.server.mdm.config.task;

import java.util.Hashtable;
import com.me.mdm.server.announcement.handler.IOSAnnouncementCommandTaskHandler;
import com.me.mdm.server.apps.blacklist.DeviceBlacklistHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Set;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.apps.businessstore.ios.IOSStoreHandler;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.HashSet;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.Map;
import java.util.List;
import com.adventnet.sym.server.mdm.apps.ios.AppleAppLicenseMgmtHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.logging.Level;
import com.me.mdm.server.command.ios.commandtask.IOSRemoveProfileCommandTaskHandler;
import com.me.mdm.server.command.ios.commandtask.IOSInstallProfileCommandTaskHandler;
import java.util.logging.Logger;

public class IOSCollectionCommandTask implements CollectionCommandTask
{
    public static Logger profileDistributionLog;
    private static Logger mdmLogger;
    
    @Override
    public void installProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        new IOSInstallProfileCommandTaskHandler().executeCommandForDevice(collectionCommandTaskData);
    }
    
    @Override
    public void uninstallProfile(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        new IOSRemoveProfileCommandTaskHandler().executeCommandForDevice(collectionCommandTaskData);
    }
    
    @Override
    public void installApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final Map<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, false, "RemoveApplication");
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, true, "InstallApplication");
        }
        ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(resourceList, profileCollectionMap, false, "ManageApplication");
        new AppleAppLicenseMgmtHandler().assignAppForDevices(collectionCommandTaskData.getTaskProperties());
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App sub task completed **********");
    }
    
    @Override
    public void uninstallApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall App sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final Long customerId = collectionCommandTaskData.getCustomerId();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final HashMap<Long, List> collectionToApplicableRes = collectionCommandTaskData.getCollectionToApplicableResource();
        final JSONObject appToDeviceLicenseDetails = new JSONObject();
        HashMap collectionToDirectLicenseRemovalDevices = null;
        if (collectionCommandTaskData.getTaskProperties().containsKey("collectionToDirectLicenseRemovalDevices")) {
            collectionToDirectLicenseRemovalDevices = ((Hashtable<K, HashMap>)collectionCommandTaskData.getTaskProperties()).get("collectionToDirectLicenseRemovalDevices");
        }
        for (int j = 0; j < collectionList.size(); ++j) {
            final Long collectionID = collectionList.get(j);
            final List collectionApplicableResource = collectionToApplicableRes.get(collectionID);
            final Long profileID = new ProfileHandler().getProfileIDFromCollectionID(collectionID);
            JSONObject bsToDeviceArray = appToDeviceLicenseDetails.optJSONObject(String.valueOf(profileID));
            if (bsToDeviceArray == null) {
                bsToDeviceArray = new JSONObject();
            }
            if (collectionApplicableResource != null || collectionToDirectLicenseRemovalDevices != null) {
                final List removalResList = new ArrayList();
                if (!collectionApplicableResource.isEmpty()) {
                    removalResList.addAll(collectionApplicableResource);
                }
                if (collectionToDirectLicenseRemovalDevices != null) {
                    final List directlyRemovalRes = collectionToDirectLicenseRemovalDevices.get(collectionID);
                    if (directlyRemovalRes != null && !directlyRemovalRes.isEmpty()) {
                        removalResList.addAll(directlyRemovalRes);
                    }
                }
                if (!removalResList.isEmpty()) {
                    bsToDeviceArray = new AppleAppLicenseMgmtHandler().getAppLicenseResourcesForAppFromAllStores(bsToDeviceArray, profileID, resourceList);
                    appToDeviceLicenseDetails.put(String.valueOf(profileID), (Object)bsToDeviceArray);
                }
            }
        }
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collectionToApplicableRes != null && !collectionToApplicableRes.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, false, "InstallApplication");
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collectionToApplicableRes, profileCollectionMap, false, "ManageApplication");
        }
        final int collResConstant = 18;
        final Set finalResourceSetToSendNotification = new HashSet();
        for (int i = 0; i < collectionList.size(); ++i) {
            final List clonedResourceList = new ArrayList(collectionToApplicableRes.get(collectionList.get(i)));
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(i), collResConstant);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(i)), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            finalResourceSetToSendNotification.addAll(clonedResourceList);
        }
        if (appToDeviceLicenseDetails != null && appToDeviceLicenseDetails.length() > 0) {
            new IOSStoreHandler(null, customerId).addLicenseRemovalTaskToQueue(appToDeviceLicenseDetails, customerId, null, Boolean.TRUE);
            IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "All source licenses removal task added in queue");
        }
        final List finalResourceListToSendNotification = new ArrayList();
        finalResourceListToSendNotification.addAll(finalResourceSetToSendNotification);
        NotificationHandler.getInstance().SendNotification(finalResourceListToSendNotification, 1);
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Uninstall App sub task completed **********");
    }
    
    @Override
    public void blackListApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** BlackList App sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final List applicableResList = ManagedDeviceHandler.getInstance().getSupervisedAnd9_3AboveMobileDevices(resourceList);
        resourceList.removeAll(applicableResList);
        final int collResConstant = 18;
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(applicableResList, collectionList, collResConstant, "--");
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionList, 8, "--");
        new DeviceBlacklistHandler().updateResourcetoBlacklistAppStatus(resourceList, collectionList, 5, 0);
        final Long commandID = DeviceCommandRepository.getInstance().addCommand("BlacklistAppInDevice");
        DeviceCommandRepository.getInstance().assignCommandToDevicesInChunck(applicableResList, commandID);
        NotificationHandler.getInstance().SendNotification(applicableResList, collectionCommandTaskData.getPlatform());
    }
    
    @Override
    public void removeBlacklisting(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Remove BlackList App sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final List applicableResList = ManagedDeviceHandler.getInstance().getSupervisedAnd9_3AboveMobileDevices(resourceList);
        resourceList.removeAll(applicableResList);
        final int collResConstant = 18;
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(applicableResList, collectionList, collResConstant, "--");
        final Long commandID = DeviceCommandRepository.getInstance().addCommand("RemoveBlacklistAppInDevice");
        DeviceCommandRepository.getInstance().assignCommandToDevicesInChunck(applicableResList, commandID);
        NotificationHandler.getInstance().SendNotification(applicableResList, collectionCommandTaskData.getPlatform());
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionList, 8, "--");
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
    public void installAppConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App Config sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final Map<Long, List> collnToApplicableResource = collectionCommandTaskData.getCollectionToApplicableResource();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collnToApplicableResource != null && !collnToApplicableResource.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collnToApplicableResource, profileCollectionMap, true, "InstallApplicationConfiguration");
        }
        for (int i = 0; i < collectionList.size(); ++i) {
            final List clonedResourceList = new ArrayList(collnToApplicableResource.get(collectionList.get(i)));
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(i), collResConstant, remark);
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(i)), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            NotificationHandler.getInstance().SendNotification(clonedResourceList, 1);
        }
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Install App Config sub task completed ******");
    }
    
    @Override
    public void removeAppConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Remove app config sub task initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final Map<Long, Long> profileCollectionMap = collectionCommandTaskData.getProfileCollectionMap();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        final Map<Long, List> collnToApplicableResource = collectionCommandTaskData.getCollectionToApplicableResource();
        if (profileCollectionMap != null && !profileCollectionMap.isEmpty() && collnToApplicableResource != null && !collnToApplicableResource.isEmpty()) {
            ProfileAssociateHandler.getInstance().removeCollectionCommandsForSameProfile(collnToApplicableResource, profileCollectionMap, false, "RemoveApplicationConfiguration");
        }
        final int collResConstant = 18;
        final String remark = "mdm.profile.distribution.waitingfordeviceinfo";
        for (int i = 0; i < collectionList.size(); ++i) {
            final List clonedResourceList = collnToApplicableResource.get(collectionList.get(i));
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(clonedResourceList, collectionList.get(i), collResConstant, remark);
            new CollectionCommandHandler().removeInstallProfileCommandFromDevice(clonedResourceList, Arrays.asList(collectionList.get(i)), "InstallApplicationConfiguration");
            final List commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionList.get(i)), collectionCommandTaskData.getCommandName());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, clonedResourceList);
            NotificationHandler.getInstance().SendNotification(clonedResourceList, 1);
        }
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "************** Remove app config sub task completed *************");
    }
    
    @Override
    public void installAnnouncement(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        new IOSAnnouncementCommandTaskHandler().executeInstallCommandForDevice(collectionCommandTaskData);
    }
    
    @Override
    public void installScheduleConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "No command Is added for resource as it is not agent delegated");
    }
    
    @Override
    public void removeScheduleConfiguration(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "No command Is added for resource as it is not agent delegated");
    }
    
    public void manageApp(final CollectionCommandTaskData collectionCommandTaskData) throws Exception {
        IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "******Manage App sub task  initiated : {0}", collectionCommandTaskData);
        final List resourceList = collectionCommandTaskData.getResourceList();
        final List collectionList = collectionCommandTaskData.getCollectionList();
        for (int j = 0; j < collectionList.size(); ++j) {
            DeviceCommandRepository.getInstance().addManageAppCommand(collectionList.get(j));
            final String commandUUID = "ManageApplication;Collection=" + Long.toString(collectionList.get(j));
            final String removeCommandUUID = "RemoveApplication;Collection=" + Long.toString(collectionList.get(j));
            final List commandList = new ArrayList();
            commandList.add(removeCommandUUID);
            final List resListWithInstallRemoveCmd = DeviceCommandRepository.getInstance().getCommandsAvailableDeviceList(commandList, resourceList);
            final List finalResList = new ArrayList(resourceList);
            finalResList.removeAll(resListWithInstallRemoveCmd);
            IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "Fianl resource list for which ManageAppCommand is going to be added {0}", finalResList);
            final Long commandID = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandID, finalResList);
            NotificationHandler.getInstance().SendNotification(finalResList, collectionCommandTaskData.getPlatform());
            IOSCollectionCommandTask.profileDistributionLog.log(Level.INFO, "****** Manage App sub task completed **********");
        }
    }
    
    static {
        IOSCollectionCommandTask.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
        IOSCollectionCommandTask.mdmLogger = Logger.getLogger("MDMLogger");
    }
}
