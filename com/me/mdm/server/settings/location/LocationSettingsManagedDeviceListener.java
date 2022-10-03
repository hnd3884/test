package com.me.mdm.server.settings.location;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class LocationSettingsManagedDeviceListener extends ManagedDeviceListener
{
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        LocationSettingsManagedDeviceListener.mdmlogger.info("Entering LocationSettingsManagedDeviceListener:deviceManaged");
        try {
            final Long resourceId = deviceEvent.resourceID;
            final Long customerId = deviceEvent.customerID;
            final List<Long> groupId = MDMEnrollmentRequestHandler.getInstance().getGroupEnrollmentId(deviceEvent.enrollmentRequestId);
            final Boolean isLocationEnable = LocationSettingsDataHandler.getInstance().isLocationTrackingEnabled(customerId);
            boolean status = isLocationEnable;
            final boolean isResourceIncluded = LocationSettingsDataHandler.getInstance().isResourceIncluded(customerId);
            if (groupId == null || groupId.isEmpty()) {
                final List deviceIdList = new ArrayList();
                deviceIdList.add(resourceId);
                if (isLocationEnable) {
                    status = !isResourceIncluded;
                }
                LocationSettingsDataHandler.getInstance().addOrUpdateLocationDeviceStatus(resourceId, status);
                if (deviceEvent.platformType != 1) {
                    DeviceCommandRepository.getInstance().addSyncAgentSettingsCommand(deviceIdList, 2);
                    NotificationHandler.getInstance().SendNotification(deviceIdList, deviceEvent.platformType);
                }
                final boolean isIOSNativeAppRegistered = IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(deviceEvent.resourceID);
                if (deviceEvent.platformType == 1 && isIOSNativeAppRegistered) {
                    DeviceCommandRepository.getInstance().addSyncAgentSettingsCommand(deviceIdList, 2);
                }
            }
            else {
                if (isLocationEnable) {
                    final int groupInclusionStatus = LocationSettingsDataHandler.getInstance().getGroupInclusionStatus(customerId, groupId);
                    if (groupInclusionStatus != -1) {
                        status = (groupInclusionStatus != 0);
                    }
                    else {
                        status = !isResourceIncluded;
                    }
                }
                LocationSettingsDataHandler.getInstance().addOrUpdateLocationDeviceStatus(resourceId, status);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(LocationSettingsManagedDeviceListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        LocationSettingsManagedDeviceListener.mdmlogger.info("Exiting LocationSettingsManagedDeviceListener:deviceManaged");
    }
}
