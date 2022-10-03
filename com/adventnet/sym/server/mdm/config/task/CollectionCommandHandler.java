package com.adventnet.sym.server.mdm.config.task;

import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.List;

public class CollectionCommandHandler
{
    public void initCollectionCommandTask(final CollectionCommandTaskData collnCommandTaskData) throws Exception {
        final CollectionCommandTaskData collectionCommandTaskData = collnCommandTaskData;
        final int platform = collectionCommandTaskData.getPlatform();
        final String commandName = collectionCommandTaskData.getCommandName();
        if (commandName != null && !commandName.isEmpty()) {
            final CollectionCommandTask collectionCommandTask = this.getCollectionCommandTaskForPlatform(platform);
            if (commandName.equalsIgnoreCase("InstallProfile")) {
                collectionCommandTask.installProfile(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("RemoveProfile")) {
                collectionCommandTask.uninstallProfile(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("InstallApplication")) {
                collectionCommandTask.installApp(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("RemoveApplication")) {
                collectionCommandTask.uninstallApp(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("BlacklistAppInDevice")) {
                collectionCommandTask.blackListApp(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("RemoveBlacklistAppInDevice")) {
                collectionCommandTask.removeBlacklisting(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("ManageApplication")) {
                new IOSCollectionCommandTask().manageApp(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("InstallDataProfile")) {
                collectionCommandTask.installDataUsageProfile(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("RemoveDataProfile")) {
                collectionCommandTask.removeDataUsageProfile(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("InstallApplicationConfiguration")) {
                collectionCommandTask.installAppConfiguration(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("RemoveApplicationConfiguration")) {
                collectionCommandTask.removeAppConfiguration(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("SyncAnnouncement")) {
                collectionCommandTask.installAnnouncement(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("InstallScheduleConfiguration")) {
                collectionCommandTask.installScheduleConfiguration(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("RemoveScheduleConfiguration")) {
                collectionCommandTask.removeScheduleConfiguration(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("InstallAppUpdatePolicy")) {
                collectionCommandTask.installAppUpdatePolicy(collectionCommandTaskData);
            }
            else if (commandName.equalsIgnoreCase("RemoveAppUpdatePolicy")) {
                collectionCommandTask.removeAppUpdatePolicy(collectionCommandTaskData);
            }
        }
    }
    
    private CollectionCommandTask getCollectionCommandTaskForPlatform(final int platform) {
        CollectionCommandTask collectionCommandTask = null;
        if (platform == 2) {
            collectionCommandTask = new AndroidCollectionCommandTask();
        }
        else if (platform == 4) {
            collectionCommandTask = new ChromeCollectionDeviceCommandTask();
        }
        else if (platform == 1) {
            collectionCommandTask = new IOSCollectionCommandTask();
        }
        else if (platform == 3) {
            collectionCommandTask = new WindowsCollectionCommandTask();
        }
        return collectionCommandTask;
    }
    
    public void removeInstallProfileCommandFromDevice(final List resourceIDs, final List collectionIDs, final String requestCommandName) {
        for (int collectionIndex = 0; collectionIndex < collectionIDs.size(); ++collectionIndex) {
            for (int resourceIndex = 0; resourceIndex < resourceIDs.size(); ++resourceIndex) {
                final String commandName = requestCommandName + ";Collection=" + collectionIDs.get(collectionIndex);
                final Long resourceID = resourceIDs.get(resourceIndex);
                final String deviceUDID = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID);
                DeviceCommandRepository.getInstance().clearCommandFromDevice(deviceUDID, resourceID, commandName);
            }
        }
    }
}
