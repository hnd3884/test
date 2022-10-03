package com.me.mdm.onpremise.server.enrollment;

import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.enroll.ERDeviceListener;

public class ManagedDeviceListenerOnPremise extends ERDeviceListener
{
    public void devicePreRegister(final DeviceEvent oldDeviceEvent) {
    }
    
    public void deviceRegistered(final DeviceEvent deviceEvent) {
    }
    
    public void deviceManaged(final DeviceEvent userEvent) {
        InvitationQRCodeEnrollmentHander.removeQRCode(userEvent.enrollmentRequestId);
    }
    
    public void devicePreDelete(final DeviceEvent userEvent) {
    }
    
    public void deviceDeleted(final DeviceEvent userEvent) {
    }
    
    public void deviceUnmanaged(final DeviceEvent userEvent) {
    }
    
    public void userAssigned(final DeviceEvent userEvent) {
    }
}
