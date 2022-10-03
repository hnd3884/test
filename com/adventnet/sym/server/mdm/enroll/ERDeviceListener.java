package com.adventnet.sym.server.mdm.enroll;

import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class ERDeviceListener extends ManagedDeviceListener
{
    @Override
    public void devicePreRegister(final DeviceEvent oldDeviceEvent) {
    }
    
    @Override
    public void deviceRegistered(final DeviceEvent deviceEvent) {
    }
    
    @Override
    public void deviceManaged(final DeviceEvent userEvent) {
    }
    
    @Override
    public void devicePreDelete(final DeviceEvent userEvent) {
    }
    
    @Override
    public void deviceDeleted(final DeviceEvent userEvent) {
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent userEvent) {
    }
    
    @Override
    public void userAssigned(final DeviceEvent userEvent) {
    }
}
