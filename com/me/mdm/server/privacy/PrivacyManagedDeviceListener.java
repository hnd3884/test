package com.me.mdm.server.privacy;

import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class PrivacyManagedDeviceListener extends ManagedDeviceListener
{
    @Override
    public void userAssigned(final DeviceEvent userEvent) {
        this.renameDevice(userEvent.enrollmentRequestId, userEvent.resourceID);
    }
    
    @Override
    public void deviceRegistered(final DeviceEvent userEvent) {
        this.renameDevice(userEvent.enrollmentRequestId, userEvent.resourceID);
    }
    
    private void renameDevice(final Long enrollmentRequestId, final Long resourceID) {
        PrivacyDynamicDeviceNameHandler.getInstance().renameDeviceInExtn(enrollmentRequestId, resourceID);
    }
}
