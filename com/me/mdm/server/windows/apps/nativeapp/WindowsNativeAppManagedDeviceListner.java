package com.me.mdm.server.windows.apps.nativeapp;

import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Properties;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class WindowsNativeAppManagedDeviceListner extends ManagedDeviceListener
{
    public Logger logger;
    
    public WindowsNativeAppManagedDeviceListner() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        WindowsNativeAppManagedDeviceListner.mdmlogger.info("Entering WindowsNativeAppManagedDeviceListener:deviceManaged");
        if (deviceEvent.platformType == 3) {
            try {
                if (WpAppSettingsHandler.getInstance().isBstoreOrCSCConfigured(deviceEvent.customerID)) {
                    MessageProvider.getInstance().hideMessage("WIN_APP_MGMT_NOT_CONFIGURED", deviceEvent.customerID);
                    if (WpAppSettingsHandler.getInstance().isBstoreConfigured(deviceEvent.customerID)) {
                        MessageProvider.getInstance().hideMessage("BUSINESS_STORE_PROMO", deviceEvent.customerID);
                    }
                    else {
                        MessageProvider.getInstance().unhideMessage("BUSINESS_STORE_PROMO", deviceEvent.customerID);
                    }
                }
                else {
                    MessageProvider.getInstance().unhideMessage("WIN_APP_MGMT_NOT_CONFIGURED", deviceEvent.customerID);
                    MessageProvider.getInstance().unhideMessage("BUSINESS_STORE_PROMO", deviceEvent.customerID);
                }
                final Boolean isAutoDistribute = WindowsNativeAppHandler.getInstance().isWindowsNativeAgentEnable(deviceEvent.customerID);
                if (isAutoDistribute) {
                    final List resourceList = new ArrayList();
                    resourceList.add(deviceEvent.resourceID);
                    DeviceCommandRepository.getInstance().addSyncAgentSettingsCommand(resourceList, 2);
                    final boolean isAppExists = AppsUtil.getInstance().isAppExistsInPackage("ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2", 3, deviceEvent.customerID);
                    if (isAppExists) {
                        Properties properties = new Properties();
                        properties = WindowsNativeAppHandler.getInstance().getWindowsNativeAppProfileDetails(deviceEvent.customerID, resourceList);
                        ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
                        IosNativeAppHandler.getInstance().addorUpdateIOSAgentInstallationStatus(deviceEvent.resourceID, 2);
                        MessageProvider.getInstance().hideMessage("WP_APP_NOT_PURCHASED", deviceEvent.customerID);
                    }
                    else {
                        MessageProvider.getInstance().unhideMessage("WP_APP_NOT_PURCHASED", deviceEvent.customerID);
                    }
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, e, () -> "Cloud not distribute native app " + deviceEvent2);
            }
        }
        WindowsNativeAppManagedDeviceListner.mdmlogger.info("Exiting WindowsNativeAppManagedDeviceListener:deviceManaged");
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent deviceEvent) {
        this.checkAndHideWindowsMessages(deviceEvent);
    }
    
    @Override
    public void deviceDeleted(final DeviceEvent userEvent) {
        this.checkAndHideWindowsMessages(userEvent);
    }
    
    private void checkAndHideWindowsMessages(final DeviceEvent deviceEvent) {
        if (deviceEvent.platformType == 3 && ManagedDeviceHandler.getInstance().getWindowsManagedDeviceCount() <= 0) {
            MessageProvider.getInstance().hideMessage("WIN_APP_MGMT_NOT_CONFIGURED", deviceEvent.customerID);
            MessageProvider.getInstance().hideMessage("BUSINESS_STORE_PROMO", deviceEvent.customerID);
            MessageProvider.getInstance().hideMessage("WP_APP_NOT_PURCHASED", deviceEvent.customerID);
        }
    }
}
