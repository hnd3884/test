package com.me.mdm.server.metracker;

import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEDCTrackerMDMEvaluatorImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEDCTrackerMDMEvaluatorImpl() {
        this.mdmTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMEvaluatorImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM Evaluator implementation starts...");
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.updateServiceRestartInfo();
            this.getPageVisitData();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmTrackerProperties;
    }
    
    private void updateServiceRestartInfo() {
        final String natAddress = "";
        try {
            final Properties natProp = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            if (natProp != null && !natProp.isEmpty()) {
                this.getServiceRestartInfo();
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "updateServiceRestartInfo", "Exception : ", (Throwable)e);
        }
    }
    
    private void getServiceRestartInfo() {
        final String isServiceRestartedStr = MDMUtil.getSyMParameter("SERVICE_RESTARTED");
        final Boolean isServiceRestarted = isServiceRestartedStr == null || isServiceRestartedStr.isEmpty() || Boolean.parseBoolean(isServiceRestartedStr);
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType.equalsIgnoreCase("T")) {
            SyMLogger.info(this.logger, this.sourceClass, "getServiceRestartInfo", "isServiceRestarted is " + isServiceRestarted + "!!");
            this.mdmTrackerProperties.setProperty("IS_Server_Restarted_After_NAT_Config", String.valueOf(isServiceRestarted));
        }
    }
    
    private void getPageVisitData() {
        this.mdmTrackerProperties.setProperty("MDM_Evaluator_Summary", ApiFactoryProvider.getEvaluatorAPI().getJSONFromFileForModule("MDM_Module").toString());
        this.mdmTrackerProperties.setProperty("MDM_Inv_Summary", ApiFactoryProvider.getEvaluatorAPI().getJSONFromFileForModule("Inv_Module").toString());
        this.mdmTrackerProperties.setProperty("MDM_Enrollment_Evaluator_Summary", ApiFactoryProvider.getEvaluatorAPI().getJSONFromFileForModule("Enrollment_Module").toString());
        this.mdmTrackerProperties.setProperty("MDM_App_Management_Evaluator_Summary", ApiFactoryProvider.getEvaluatorAPI().getJSONFromFileForModule("App_Management_Module").toString());
        this.mdmTrackerProperties.setProperty("MDM_EAS_Management_Evaluator_Summary", ApiFactoryProvider.getEvaluatorAPI().getJSONFromFileForModule("EAS_Management_Module").toString());
        this.mdmTrackerProperties.setProperty("MDM_Remote_Evaluator_Summary", ApiFactoryProvider.getEvaluatorAPI().getJSONFromFileForModule("Remote_Module").toString());
    }
}
