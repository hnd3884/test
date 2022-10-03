package com.me.mdm.server.metracker;

import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerIntegrationImpl extends MEMDMTrackerConstants
{
    private Properties integrationTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerIntegrationImpl() {
        this.integrationTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMTrackerIntegrationImpl";
    }
    
    public Properties getTrackerProperties() {
        SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM integration starts...");
        return this.getIntegrationTrackerProperties();
    }
    
    private Properties getIntegrationTrackerProperties() {
        this.updateIntegrationProperties();
        return this.integrationTrackerProperties;
    }
    
    private void updateIntegrationProperties() {
        try {
            if (!this.integrationTrackerProperties.isEmpty()) {
                this.integrationTrackerProperties = new Properties();
            }
            this.addSpiceworksIntegrationDetails();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.integrationTrackerProperties);
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM integration ends");
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "updateIntegrationProperties", "Exception : ", (Throwable)e);
        }
    }
    
    private void addSpiceworksIntegrationDetails() {
        final JSONObject spiceworksJSON = new JSONObject();
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final MEMDMTrackParamManager mdmTrackParamManager = MEMDMTrackParamManager.getInstance();
            spiceworksJSON.put("SPICEWORKS_PLUGIN_DEVICE_LIST_PAGE", (Object)mdmTrackParamManager.getTrackParamValueFromDB(customerId, "Integration_Module", "SPICEWORKS_PLUGIN_DEVICE_LIST_PAGE", "0"));
            spiceworksJSON.put("SPICEWORKS_PLUGIN_ADMIN_PAGE", (Object)mdmTrackParamManager.getTrackParamValueFromDB(customerId, "Integration_Module", "SPICEWORKS_PLUGIN_ADMIN_PAGE", "0"));
            spiceworksJSON.put("SPICEWORKS_PLUGIN_REMOTE_LOCK", (Object)mdmTrackParamManager.getTrackParamValueFromDB(customerId, "Integration_Module", "SPICEWORKS_PLUGIN_REMOTE_LOCK", "0"));
            spiceworksJSON.put("SPICEWORKS_PLUGIN_CORPORATE_WIPE", (Object)mdmTrackParamManager.getTrackParamValueFromDB(customerId, "Integration_Module", "SPICEWORKS_PLUGIN_CORPORATE_WIPE", "0"));
            spiceworksJSON.put("SPICEWORKS_PLUGIN_COMPLETE_WIPE", (Object)mdmTrackParamManager.getTrackParamValueFromDB(customerId, "Integration_Module", "SPICEWORKS_PLUGIN_COMPLETE_WIPE", "0"));
            spiceworksJSON.put("SPICEWORKS_PLUGIN_CLEAR_PASSCODE", (Object)mdmTrackParamManager.getTrackParamValueFromDB(customerId, "Integration_Module", "SPICEWORKS_PLUGIN_CLEAR_PASSCODE", "0"));
            spiceworksJSON.put("SPICEWORKS_PLUGIN_REMOTE_ALARM", (Object)mdmTrackParamManager.getTrackParamValueFromDB(customerId, "Integration_Module", "SPICEWORKS_PLUGIN_REMOTE_ALARM", "0"));
            spiceworksJSON.put("SPICEWORKS_PLUGIN_RESET_PASSCODE", (Object)mdmTrackParamManager.getTrackParamValueFromDB(customerId, "Integration_Module", "SPICEWORKS_PLUGIN_RESET_PASSCODE", "0"));
            spiceworksJSON.put("SPICEWORKS_PLUGIN_REMOTE_CONTROL", (Object)mdmTrackParamManager.getTrackParamValueFromDB(customerId, "Integration_Module", "SPICEWORKS_PLUGIN_REMOTE_CONTROL", "0"));
            this.integrationTrackerProperties.setProperty("SPICEWORKS_INTEGRATION_DETAILS", spiceworksJSON.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addSpiceworksIntegrationDetails", "Exception : ", (Throwable)ex);
        }
    }
}
