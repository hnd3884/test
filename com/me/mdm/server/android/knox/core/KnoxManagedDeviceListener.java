package com.me.mdm.server.android.knox.core;

import com.me.mdm.server.android.knox.KnoxUtil;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class KnoxManagedDeviceListener extends ManagedDeviceListener
{
    @Override
    public void deviceManaged(final DeviceEvent userEvent) {
        KnoxManagedDeviceListener.mdmlogger.info("Entering KnoxManagedDeviceListener:deviceManaged");
        try {
            final List reList = Arrays.asList(userEvent.resourceID);
            if (userEvent.platformType == 2 && (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)userEvent.resourceID, "AGENT_TYPE") == 3) {
                DeviceCommandRepository.getInstance().addSecurityCommand(userEvent.resourceID, "GetKnoxAvailabilityEnrollment");
                NotificationHandler.getInstance().SendNotification(reList, 2);
                Logger.getLogger("MDMLogger").log(Level.INFO, "Device added Notification for KNOXManagedDeviceListener executed successfully");
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, null, ex);
        }
        KnoxManagedDeviceListener.mdmlogger.info("Exiting KnoxManagedDeviceListener:deviceManaged");
    }
    
    @Override
    public void deviceDeleted(final DeviceEvent userEvent) {
        KnoxManagedDeviceListener.mdmlogger.info("Entering KnoxManagedDeviceListener:deviceUnmanaged");
        try {
            KnoxUtil.getInstance().handleUnmangedKnoxDevice(userEvent.resourceID);
            Logger.getLogger("MDMLogger").log(Level.INFO, "KNOXManagedDeviceListener : Device deleted So associated License is removed");
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, null, e);
        }
        KnoxManagedDeviceListener.mdmlogger.info("Exiting KnoxManagedDeviceListener:deviceUnmanaged");
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent userEvent) {
        KnoxManagedDeviceListener.mdmlogger.info("Entering KnoxManagedDeviceListener:deviceUnmanaged");
        try {
            KnoxUtil.getInstance().handleUnmangedKnoxDevice(userEvent.resourceID);
            Logger.getLogger("MDMLogger").log(Level.INFO, "KNOXManagedDeviceListener : Device unmanaged So associated License is removed");
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, null, e);
        }
        KnoxManagedDeviceListener.mdmlogger.info("Exiting KnoxManagedDeviceListener:deviceUnmanaged");
    }
}
