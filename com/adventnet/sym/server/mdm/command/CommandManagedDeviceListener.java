package com.adventnet.sym.server.mdm.command;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class CommandManagedDeviceListener extends ManagedDeviceListener
{
    public Logger logger;
    public Logger checkinLogger;
    
    public CommandManagedDeviceListener() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.checkinLogger = Logger.getLogger("MDMCheckinLogger");
    }
    
    @Override
    public void deviceRegistered(final DeviceEvent deviceEvent) {
        CommandManagedDeviceListener.mdmlogger.info("Entering CommandManagedDeviceListener:deviceRegistered");
        try {
            final DeviceDetails dd = new DeviceDetails(deviceEvent.resourceID);
            final List resourceList = new ArrayList();
            resourceList.add(deviceEvent.resourceID);
            if (deviceEvent.platformType == 4) {
                DeviceCommandRepository.getInstance().addSyncPrivacySettingsCommand(resourceList, 1);
            }
            final Long userId = new EnrollmentFacade().getUserIdForEnrollmentRequestToDevice(deviceEvent.resourceID);
            DeviceCommandRepository.getInstance().addDeviceScanCommand(dd, userId);
            if (deviceEvent.platformType == 2) {
                DeviceCommandRepository.getInstance().addSyncAgentSettingsCommandForAndroid(resourceList);
                DeviceCommandRepository.getInstance().addSystemAppCommand(deviceEvent.resourceID);
                DeviceCommandRepository.getInstance().addSmsPublicKeyDistributorCommand(deviceEvent.resourceID);
                DeviceCommandRepository.getInstance().addCapabilitiesInfoCommand(deviceEvent.resourceID);
            }
            NotificationHandler.getInstance().SendNotification(resourceList, deviceEvent.platformType);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exeception in command Listener during enrollment ", e);
        }
        CommandManagedDeviceListener.mdmlogger.info("Exiting CommandManagedDeviceListener:deviceRegistered");
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        CommandManagedDeviceListener.mdmlogger.info("Entering CommandManagedDeviceListener:deviceManaged");
        try {
            final List resourceList = new ArrayList();
            resourceList.add(deviceEvent.resourceID);
            if (deviceEvent.platformType == 2) {
                this.hideExcludeDeviceMessageBoxForNoManagedDevice();
                DeviceCommandRepository.getInstance().addSyncAgentSettingsCommandForAndroid(resourceList);
                DeviceCommandRepository.getInstance().addSyncDownloadSettingsCommand(resourceList, 1);
                DeviceCommandRepository.getInstance().addSecurityCommand(deviceEvent.resourceID, "UpdateUserInfo");
                NotificationHandler.getInstance().SendNotification(Arrays.asList(deviceEvent.resourceID), 2);
            }
            if (deviceEvent.platformType == 1) {
                final String sOSVersion = (String)DBUtil.getValueFromDB("MdOSDetailsTemp", "RESOURCE_ID", (Object)deviceEvent.resourceID, "OS_VERSION");
                if (!sOSVersion.startsWith("4.")) {
                    DeviceCommandRepository.getInstance().addDefaultAppCatalogCommand(deviceEvent.resourceID, "DefaultAppCatalogWebClips");
                }
            }
            if (deviceEvent.platformType == 3) {
                DeviceCommandRepository.getInstance().addDeviceClientSettingsCommand(deviceEvent.resourceID);
                if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(deviceEvent.resourceID, 10.0f)) {
                    DeviceCommandRepository.getInstance().addSecurityCommand(deviceEvent.resourceID, "UpdateUserInfo");
                    NotificationHandler.getInstance().SendNotification(Arrays.asList(deviceEvent.resourceID), 3);
                }
            }
            NotificationHandler.getInstance().SendNotification(resourceList, deviceEvent.platformType);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exeception in command Listener during enrollment ", e);
        }
        CommandManagedDeviceListener.mdmlogger.info("Exiting CommandManagedDeviceListener:deviceManaged");
    }
    
    @Override
    public void devicePreDelete(final DeviceEvent deviceEvent) {
        CommandManagedDeviceListener.mdmlogger.info("Entering CommandManagedDeviceListener:devicePreDelete");
        try {
            DeviceCommandRepository.getInstance().removeAllCommandsForResource(deviceEvent.resourceID, deviceEvent.udid);
            this.hideExcludeDeviceMessageBoxForNoManagedDevice();
            int managedstatus = deviceEvent.resourceJSON.optInt("MANAGED_STATUS");
            if (managedstatus == 0) {
                managedstatus = ManagedDeviceHandler.getInstance().getManagedDeviceStatus(deviceEvent.resourceID);
            }
            if (managedstatus == 2 || managedstatus == 5 || managedstatus == 9 || managedstatus == 11 || managedstatus == 10) {
                DeviceCommandRepository.getInstance().addRemoveDeviceCommand(deviceEvent.udid);
                final List resourceList = new ArrayList();
                resourceList.add(deviceEvent.resourceID);
                NotificationHandler.getInstance().SendNotification(resourceList, deviceEvent.platformType);
                String platformType = "iOS";
                switch (deviceEvent.platformType) {
                    case 2: {
                        platformType = "Android";
                        break;
                    }
                    case 3: {
                        platformType = "Windows";
                        break;
                    }
                }
                this.checkinLogger.log(Level.INFO, "{0} MessageType:RemoveDevice Erid:{1} Udid:{2}", new Object[] { platformType, deviceEvent.enrollmentRequestId, deviceEvent.udid });
                if (deviceEvent.platformType == 3) {
                    if (IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(deviceEvent.resourceID)) {
                        DeviceCommandRepository.getInstance().addNativeAppRemoveDeviceCommand(deviceEvent.udid);
                    }
                    NotificationHandler.getInstance().SendNotification(resourceList, 303);
                }
                else if (deviceEvent.platformType == 2 && ManagedDeviceHandler.getInstance().isPersonalProfileManaged(deviceEvent.resourceID)) {
                    NotificationHandler.getInstance().SendNotification(resourceList, 201);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exeception in command Listener during enrollment ", e);
        }
        CommandManagedDeviceListener.mdmlogger.info("Exiting CommandManagedDeviceListener:devicePreDelete");
    }
    
    @Override
    public void userAssigned(final DeviceEvent deviceEvent) {
        CommandManagedDeviceListener.mdmlogger.info("Entering CommandManagedDeviceListener:userAssigned");
        try {
            if (deviceEvent.platformType == 2) {
                final Long resourceId = deviceEvent.resourceID;
                DeviceCommandRepository.getInstance().addSecurityCommand(resourceId, "UpdateUserInfo");
                NotificationHandler.getInstance().SendNotification(Arrays.asList(resourceId), 2);
            }
            else if (deviceEvent.platformType == 3) {
                final Long resourceId = deviceEvent.resourceID;
                if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(resourceId, 10.0f)) {
                    DeviceCommandRepository.getInstance().addSecurityCommand(resourceId, "UpdateUserInfo");
                    NotificationHandler.getInstance().SendNotification(Arrays.asList(resourceId), 3);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exeception in command Listener during enrollment ", ex);
        }
        CommandManagedDeviceListener.mdmlogger.info("Exiting CommandManagedDeviceListener:userAssigned");
    }
    
    private void hideExcludeDeviceMessageBoxForNoManagedDevice() {
        try {
            final int unManagedDeviceCount = DBUtil.getRecordCount("ManagedDevice", "RESOURCE_ID", new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)4, 0));
            if (unManagedDeviceCount == 0) {
                MessageProvider.getInstance().hideMessage("ANDROID_REMOVED_DEVICES_BY_USER");
            }
            this.logger.log(Level.INFO, "unmanaged device count = {0}", unManagedDeviceCount);
        }
        catch (final Exception ex) {
            Logger.getLogger(CommandManagedDeviceListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
