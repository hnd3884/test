package com.me.mdm.server.apple.listeners.manageddevice;

import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.adep.DeviceConfiguredCommandHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.me.mdm.server.apple.useraccount.AppleMultiUserUtils;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class AppleSharediPadConfigurationManagedDeviceListener extends ManagedDeviceListener
{
    @Override
    public void deviceRegistered(final DeviceEvent deviceEvent) {
        final Long resourceID = deviceEvent.resourceID;
        final int devicePlatform = deviceEvent.platformType;
        if (devicePlatform == 1) {
            AppleSharediPadConfigurationManagedDeviceListener.mdmlogger.info("Entering AppleSharediPadConfigurationManagedDeviceListener:deviceRegistered :" + deviceEvent.resourceID + " " + deviceEvent.udid);
            final DataObject dataObject = AppleMultiUserUtils.getSharedIpadDO(resourceID);
            final boolean canAddSharedIPAdConfigCommand = AppleMultiUserUtils.isSharedIPadConfiguration(dataObject);
            boolean wakeupDevice = false;
            final List resourceIDList = new ArrayList();
            resourceIDList.add(resourceID);
            if (canAddSharedIPAdConfigCommand) {
                DeviceCommandRepository.getInstance().addSharedIPadConfiguration(resourceIDList);
                AppleSharediPadConfigurationManagedDeviceListener.mdmlogger.info("Added SharedDeviceConfigurationCommand for resource:" + deviceEvent.udid);
                wakeupDevice = true;
            }
            if (dataObject != null && !dataObject.isEmpty()) {
                DeviceConfiguredCommandHandler.getInstance().addDeviceConfiguredCommand(resourceID);
                wakeupDevice = true;
            }
            if (wakeupDevice) {
                try {
                    NotificationHandler.getInstance().SendNotification(resourceIDList, deviceEvent.platformType);
                }
                catch (final Exception e) {
                    AppleSharediPadConfigurationManagedDeviceListener.mdmlogger.log(Level.SEVERE, "EXCEPTION AppleSharediPadConfigurationManagedDeviceListener:deviceRegistered", e);
                }
            }
            AppleSharediPadConfigurationManagedDeviceListener.mdmlogger.info("Exiting AppleSharediPadConfigurationManagedDeviceListener:deviceRegistered");
        }
    }
}
