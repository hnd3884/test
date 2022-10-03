package com.adventnet.sym.server.mdm.core;

import java.util.logging.Level;
import com.me.mdm.core.enrollment.settings.DeviceLimitLicenseAlertHandler;
import java.util.logging.Logger;

public class LicensePercentManagedDeviceListener extends ManagedDeviceListener
{
    private Logger logger;
    
    public LicensePercentManagedDeviceListener() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        try {
            DeviceLimitLicenseAlertHandler.getInstance().checkAndSendLicenseAlertMail(deviceEvent);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in LicensePercentManagedDeviceListener.deviceManaged()", e);
        }
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent deviceEvent) {
        try {
            DeviceLimitLicenseAlertHandler.getInstance().checkAndSendLicenseAlertMail(deviceEvent);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in LicensePercentManagedDeviceListener.deviceUnmanaged()", e);
        }
    }
    
    @Override
    public void deviceDeprovisioned(final DeviceEvent deviceEvent) {
        try {
            DeviceLimitLicenseAlertHandler.getInstance().checkAndSendLicenseAlertMail(deviceEvent);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in LicensePercentManagedDeviceListener.deviceDeprovisioned()", e);
        }
    }
}
