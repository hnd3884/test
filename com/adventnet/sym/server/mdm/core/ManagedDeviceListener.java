package com.adventnet.sym.server.mdm.core;

import java.util.logging.Logger;

public class ManagedDeviceListener
{
    public static Logger mdmlogger;
    
    public void devicePreRegister(final DeviceEvent oldDeviceEvent) {
    }
    
    public void deviceRegistered(final DeviceEvent deviceEvent) {
    }
    
    public void deviceManaged(final DeviceEvent userEvent) {
    }
    
    public void devicePreDelete(final DeviceEvent userEvent) {
    }
    
    public void deviceDeleted(final DeviceEvent userEvent) {
    }
    
    public void deviceUnmanaged(final DeviceEvent userEvent) {
    }
    
    public void userAssigned(final DeviceEvent userEvent) {
    }
    
    public void deviceDetailChanged(final DeviceEvent deviceEvent) {
    }
    
    public void deviceDeprovisioned(final DeviceEvent deviceEvent) {
    }
    
    public void devicePreUserAssigned(final DeviceEvent preDeviceEvent) {
    }
    
    public void devicePostScan(final DeviceEvent deviceEvent) {
    }
    
    static {
        ManagedDeviceListener.mdmlogger = Logger.getLogger("MDMEnrollment");
    }
}
