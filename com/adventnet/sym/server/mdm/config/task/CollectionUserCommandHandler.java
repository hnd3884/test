package com.adventnet.sym.server.mdm.config.task;

import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.List;

public class CollectionUserCommandHandler
{
    public void initCollectionCommandTask(final CollectionCommandTaskData collnCommandTaskData) throws Exception {
        final CollectionCommandTaskData collectionCommandTaskData = collnCommandTaskData;
        final int platform = collectionCommandTaskData.getPlatform();
        final String commandName = collectionCommandTaskData.getCommandName();
        if (commandName != null && !commandName.isEmpty()) {
            final CollectionCommandTask collectionCommandTask = this.getCollectionCommandTaskForPlatform(platform);
            if (collectionCommandTask != null) {
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
            }
        }
    }
    
    private CollectionCommandTask getCollectionCommandTaskForPlatform(final int platform) {
        CollectionCommandTask collectionCommandTask = null;
        if (platform == 4 || platform == 2) {
            collectionCommandTask = new ChromeCollectionUserCommandTask();
        }
        return collectionCommandTask;
    }
    
    protected void removeInstallProfileCommandFromDevice(final List resourceIDs, final List collectionIDs) {
        for (int collectionIndex = 0; collectionIndex < collectionIDs.size(); ++collectionIndex) {
            for (int resourceIndex = 0; resourceIndex < resourceIDs.size(); ++resourceIndex) {
                final String commandName = "InstallProfile;Collection=" + collectionIDs.get(collectionIndex);
                final Long resourceID = resourceIDs.get(resourceIndex);
                final String deviceUDID = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID);
                DeviceCommandRepository.getInstance().clearCommandFromDevice(deviceUDID, resourceID, commandName);
            }
        }
    }
}
