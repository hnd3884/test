package com.adventnet.sym.server.mdm;

import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.Map;
import java.util.HashMap;
import com.me.mdm.server.doc.DocMgmt;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Iterator;
import com.me.mdm.core.enrollment.AdminDeviceHandler;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.enrollment.ios.IOSUpgradeMobileConfigCommandHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MDMAgentUpgradeTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public MDMAgentUpgradeTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void executeTask(final Properties taskProps) {
        try {
            boolean isNotify = true;
            if (taskProps != null && taskProps.getProperty("isNotify") != null && taskProps.getProperty("isNotify").equals("false")) {
                isNotify = false;
            }
            this.sendAndroidAgentUpgradeCommand(isNotify);
            this.sendIosAgentUpgradeCommand();
            this.sendRenewMobileConfigCommand();
            this.checkWindowsAgentNewVersionAvailable();
            this.sendDefaultMDMAppConfigurationCommand();
            this.addAndroidAdminAgentUpgradeCommand();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while upgrading android agent", ex);
        }
    }
    
    private void sendRenewMobileConfigCommand() throws Exception {
        final String value = MDMUtil.getSyMParameter("REGENERATE_MOBILE_CONFIG");
        if (value != null && !value.isEmpty() && Boolean.parseBoolean(value)) {
            Logger.getLogger("MDMIosEnrollmentClientCertificateLogger").log(Level.INFO, "MDMAgentUpgradeTask: Adding upgrade mobile config for eligible devices.");
            IOSUpgradeMobileConfigCommandHandler.getInstance().addIosUpgradeMobileConfigCommand(null, false, false);
            MDMUtil.deleteSyMParameter("REGENERATE_MOBILE_CONFIG");
        }
    }
    
    private void sendAndroidAgentUpgradeCommand(final boolean isNotify) throws Exception {
        final List<Long> resList = ManagedDeviceHandler.getInstance().getYetToUpgradeAndroidManagedDevices();
        this.logger.log(Level.INFO, "Android devices ready to upgrade : {0}", resList.toString());
        if (!resList.isEmpty()) {
            DeviceCommandRepository.getInstance().addAgentUpgradeCommand(resList, 1);
            if (isNotify) {
                NotificationHandler.getInstance().SendNotification(resList, 2);
            }
        }
    }
    
    private void addAndroidAdminAgentUpgradeCommand() {
        final List udidList = new AdminDeviceHandler().getDevicesYetToNotifyForUpgrade();
        for (final String udid : udidList) {
            DeviceCommandRepository.getInstance().addAdminAgentUpgradeCommand(udid, 3);
        }
    }
    
    private void sendDefaultMDMAppConfigurationCommand() {
        try {
            final String iOSValue = MDMUtil.getSyMParameter("DiscoveryServletsAdded");
            if (!MDMStringUtils.isEmpty(iOSValue) && Boolean.valueOf(iOSValue)) {
                final List resList = ManagedDeviceHandler.getInstance().getIOSManagedDeviceResourceIDs();
                DeviceCommandRepository.getInstance().addMDMDefaultAppConfiguration(resList);
                NotificationHandler.getInstance().SendNotification(resList, 1);
                MDMUtil.deleteSyMParameter("DiscoveryServletsAdded");
                DocMgmt.logger.log(Level.INFO, "sending new servlet/app configurations to following ios Devices : {0}", resList.toString());
            }
            final String androidValue = MDMUtil.getSyMParameter("ANDROID_FEATURE_ENABLED");
            if (!MDMStringUtils.isEmpty(androidValue) && Boolean.valueOf(androidValue)) {
                final List androidList = ManagedDeviceHandler.getInstance().getAndroidManagedDeviceResourceIDs();
                final Map cmdData = new HashMap();
                cmdData.put("RresourceList", androidList);
                cmdData.put("CommandUUID", "SyncAgentSettings");
                cmdData.put("CMDRepType", 1);
                DeviceCommandRepository.getInstance().addOrUpdateCommandOnPPM(cmdData);
                NotificationHandler.getInstance().SendNotification(androidList, 2);
                MDMUtil.deleteSyMParameter("ANDROID_FEATURE_ENABLED");
                DocMgmt.logger.log(Level.INFO, "sending sync agent command to following android Devices : {0}", androidList.toString());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception in sendDefaultMDMAppConfigurationCommand");
        }
    }
    
    private void sendIosAgentUpgradeCommand() throws Exception {
        final List resList = ManagedDeviceHandler.getInstance().getYetToUpgradeIOSManagedDevices();
        if (!resList.isEmpty()) {
            DeviceCommandRepository.getInstance().addAgentUpgradeCommand(resList, 2);
        }
    }
    
    private void checkWindowsAgentNewVersionAvailable() throws Exception {
        if (!Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"))) {
            MDMAppMgmtHandler.getInstance().latestWindowsAppHandling();
        }
    }
}
