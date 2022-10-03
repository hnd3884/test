package com.adventnet.sym.server.mdm.core;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UnmanageDeviceListener extends ManagedDeviceListener
{
    public Logger logger;
    
    public UnmanageDeviceListener() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent deviceEvent) {
        this.logger.log(Level.INFO, "Entering UnmanageDeviceListener:deviceUnmanaged");
        ManagedDeviceHandler.getInstance().removeResourceAssociationsOnUnmanage(deviceEvent.resourceID);
        this.logger.log(Level.INFO, "Exiting UnmanageDeviceListener:deviceUnmanaged");
        this.logger.log(Level.INFO, "Removing Kiosk state for unmanaged device");
        ManagedDeviceHandler.getInstance().removeKioskStateForUnmanagedDevice(deviceEvent.resourceID);
    }
}
