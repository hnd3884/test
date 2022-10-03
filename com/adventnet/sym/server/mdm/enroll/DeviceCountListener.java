package com.adventnet.sym.server.mdm.enroll;

import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import com.me.devicemanagement.framework.server.general.InstallationTrackingAPI;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class DeviceCountListener extends ManagedDeviceListener
{
    Logger mdmlogger;
    public Logger logger;
    
    public DeviceCountListener() {
        this.mdmlogger = Logger.getLogger("MDMLogger");
        this.logger = Logger.getLogger("DeviceCountListener");
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        this.mdmlogger.info("Entering DeviceCountListener:deviceManaged");
        try {
            MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", deviceEvent.customerID);
            this.updateEntriesInFile();
            this.updateEvalTracking(deviceEvent.platformType);
            ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
            this.incrementSystemParamCount();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while updating added device details in install.conf", e);
        }
        this.mdmlogger.info("Exiting DeviceCountListener:deviceManaged");
    }
    
    private void incrementSystemParamCount() {
        try {
            this.logger.log(Level.INFO, "Inside incrementSystemParamCount()");
            final String systemParamValue = SyMUtil.getSyMParameter("MDM_DEVICE_HISTORY_COUNT");
            int count = 1;
            if (!MDMStringUtils.isEmpty(systemParamValue)) {
                try {
                    count = Integer.valueOf(systemParamValue);
                    ++count;
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, ex, () -> "Exception typecasting MDM_DEVICE_HISTORY_COUNT value [" + s + "]");
                }
            }
            this.logger.log(Level.INFO, "MDM_DEVICE_HISTORY_COUNT :{0}", count);
            SyMUtil.updateSyMParameter("MDM_DEVICE_HISTORY_COUNT", String.valueOf(count));
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception incrementSystemParamCount()", ex2);
        }
        try {
            final String firstEnrollmenttimeOnce = SyMUtil.getSyMParameter("FIRSTENROLLTIME");
            if (firstEnrollmenttimeOnce == null) {
                SyMUtil.updateSyMParameter("FIRSTENROLLTIME", String.valueOf(System.currentTimeMillis()));
                this.logger.log(Level.INFO, "This is the first device enrolling .Updating FIRST_DEVICE_ENROLLMENT_TIME");
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception incrementSystemParamCount()", ex2);
        }
        try {
            SyMUtil.updateSyMParameter("LATESTENROLLTIME", String.valueOf(System.currentTimeMillis()));
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception incrementSystemParamCount()", ex2);
        }
    }
    
    @Override
    public void deviceUnmanaged(final DeviceEvent deviceEvent) {
        this.mdmlogger.info("Entering DeviceCountListener:deviceUnmanaged");
        try {
            MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", deviceEvent.customerID);
            this.updateEntriesInFile();
            ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while updating removed device details in install.conf ", e);
        }
        this.mdmlogger.info("Exiting DeviceCountListener:deviceUnmanaged");
    }
    
    @Override
    public void deviceDeleted(final DeviceEvent deviceEvent) {
        this.mdmlogger.info("Entering DeviceCountListener:deviceDeleted");
        try {
            this.updateEntriesInFile();
            ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while updating removed device details in install.conf ", e);
        }
        this.mdmlogger.info("Exiting DeviceCountListener:deviceDeleted");
    }
    
    @Override
    public void deviceDeprovisioned(final DeviceEvent deviceEvent) {
        MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", deviceEvent.customerID);
    }
    
    private void updateEntriesInFile() {
        final Properties installProps = new Properties();
        final String mdmSummary = ApiFactoryProvider.getServiceAPI(false).getTrackingSummary();
        installProps.setProperty("mdm", mdmSummary);
        final InstallationTrackingAPI installationTracking = ApiFactoryProvider.installationTrackingAPIImpl();
        if (installationTracking != null) {
            installationTracking.writeInstallProps(installProps);
        }
        final Properties serverInfoProps = new Properties();
        serverInfoProps.setProperty("mdm.summary", mdmSummary);
        if (installationTracking != null) {
            installationTracking.writeServerInfoProps(serverInfoProps);
        }
    }
    
    private void updateEvalTracking(final int platformType) {
        final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
        if (evaluatorApi != null) {
            evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Total_Device_Added");
            switch (platformType) {
                case 1: {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Total_Ios_Device_Added");
                    break;
                }
                case 2: {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Total_Android_Device_Added");
                    break;
                }
                case 3: {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Total_Windows_Device_Added");
                    break;
                }
            }
        }
    }
}
