package com.adventnet.sym.server.mdm.message;

import com.me.devicemanagement.framework.server.license.LicenseEvent;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.license.LicenseListener;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class LicenseMessageListener extends ManagedDeviceListener implements LicenseListener
{
    public static Logger logger;
    
    @Override
    public void deviceUnmanaged(final DeviceEvent userEvent) {
        LicenseMessageListener.mdmlogger.info("Entering LicenseMessageListener:deviceUnmanaged");
        MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", userEvent.customerID);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING", userEvent.customerID);
        LicenseMessageListener.mdmlogger.info("Exiting LicenseMessageListener:deviceUnmanaged");
    }
    
    @Override
    public void deviceManaged(final DeviceEvent userEvent) {
        LicenseMessageListener.mdmlogger.info("Entering LicenseMessageListener:deviceManaged");
        MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", userEvent.customerID);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING", userEvent.customerID);
        LicenseMessageListener.mdmlogger.info("Exiting LicenseMessageListener:deviceManaged");
    }
    
    @Override
    public void deviceDeleted(final DeviceEvent userEvent) {
        LicenseMessageListener.mdmlogger.info("Entering LicenseMessageListener:deviceDeleted");
        MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", userEvent.customerID);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING", userEvent.customerID);
        LicenseMessageListener.mdmlogger.info("Exiting LicenseMessageListener:deviceDeleted");
    }
    
    public void licenseChanged(final LicenseEvent licenseEvent) {
        LicenseMessageListener.mdmlogger.info("Entering LicenseMessageListener:licenseChanged");
        MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", null);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING", null);
        LicenseMessageListener.mdmlogger.info("Exiting LicenseMessageListener:licenseChanged");
    }
    
    @Override
    public void devicePreRegister(final DeviceEvent oldDeviceEvent) {
        LicenseMessageListener.mdmlogger.info("Entering LicenseMessageListener:devicePreRegister");
        MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", oldDeviceEvent.customerID);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING", oldDeviceEvent.customerID);
        LicenseMessageListener.mdmlogger.info("Exiting LicenseMessageListener:devicePreRegister");
    }
    
    @Override
    public void deviceRegistered(final DeviceEvent deviceEvent) {
        LicenseMessageListener.mdmlogger.info("Entering LicenseMessageListener:deviceRegistered");
        MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", deviceEvent.customerID);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING", deviceEvent.customerID);
        LicenseMessageListener.mdmlogger.info("Exiting LicenseMessageListener:deviceRegistered");
    }
    
    @Override
    public void deviceDeprovisioned(final DeviceEvent deviceEvent) {
        LicenseMessageListener.mdmlogger.info("Entering LicenseMessageListener:deviceDeprovisioned");
        MDMMessageHandler.getInstance().messageAction("LICENSE_LIMIT_REACHED", null);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED", null);
        MDMMessageHandler.getInstance().messageAction("UEM_CENTRAL_LICENSE_LIMIT_EXCEED_WARNING", null);
        LicenseMessageListener.mdmlogger.info("Exiting LicenseMessageListener:deviceDeprovisioned");
    }
    
    static {
        LicenseMessageListener.logger = Logger.getLogger("MDMLogger");
    }
}
