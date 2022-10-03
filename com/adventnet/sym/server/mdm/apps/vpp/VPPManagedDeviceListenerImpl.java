package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.logging.Level;
import java.util.List;
import com.adventnet.sym.server.mdm.apps.ios.AppleAppLicenseMgmtHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class VPPManagedDeviceListenerImpl extends ManagedDeviceListener
{
    public Logger logger;
    
    public VPPManagedDeviceListenerImpl() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    @Override
    public void devicePreRegister(final DeviceEvent oldDeviceEvent) {
        VPPManagedDeviceListenerImpl.mdmlogger.info("Entering VPPManagedDeviceListenerImpl:devicePreRegister");
        this.devicePreDelete(oldDeviceEvent);
        VPPManagedDeviceListenerImpl.mdmlogger.info("Exiting VPPManagedDeviceListenerImpl:devicePreRegister");
    }
    
    @Override
    public void devicePreDelete(final DeviceEvent deviceEvent) {
        VPPManagedDeviceListenerImpl.mdmlogger.info("Entering VPPManagedDeviceListenerImpl:devicePreDelete");
        final List<Long> deviceIdList = new ArrayList<Long>();
        deviceIdList.add(deviceEvent.resourceID);
        final Long customerId = deviceEvent.customerID;
        try {
            new AppleAppLicenseMgmtHandler().revokeAllAppLicenseForResources(deviceIdList, customerId);
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception in  devicePreDelete of VPPManagedDeviceListenerImpl {0}", ex);
        }
        VPPManagedDeviceListenerImpl.mdmlogger.info("Exiting VPPManagedDeviceListenerImpl:devicePreDelete");
    }
}
