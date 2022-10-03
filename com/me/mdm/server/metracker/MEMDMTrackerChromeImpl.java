package com.me.mdm.server.metracker;

import java.sql.Connection;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerChromeImpl extends MEMDMTrackerConstants
{
    private Properties mdmChromeTrackerProps;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerChromeImpl() {
        this.mdmChromeTrackerProps = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMTrackerChromeImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM Android impl starts");
            if (!this.mdmChromeTrackerProps.isEmpty()) {
                this.mdmChromeTrackerProps = new Properties();
            }
            this.addChromeDetails();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : ");
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MEMDMTrackerChromeImpl", "Exception : ", (Throwable)e);
        }
        return this.mdmChromeTrackerProps;
    }
    
    private void addChromeDetails() {
        final JSONObject chromeJSON = new JSONObject();
        final Connection connection = null;
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
            chromeJSON.put("Chrome_Configured", GoogleForWorkSettings.isGoogleForWorkSettingsConfigured(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT));
            if (GoogleForWorkSettings.isGoogleForWorkSettingsConfigured(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT)) {
                chromeJSON.put("Domain_Name", GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT).get("MANAGED_DOMAIN_NAME"));
            }
            this.mdmChromeTrackerProps.setProperty("Chrome_Details", chromeJSON.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addAgentMigrationDetails", "Exception : ", (Throwable)ex);
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e) {
                SyMLogger.error(this.logger, this.sourceClass, "addAgentMigrationDetails", "Exception : ", (Throwable)e);
            }
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e2) {
                SyMLogger.error(this.logger, this.sourceClass, "addAgentMigrationDetails", "Exception : ", (Throwable)e2);
            }
        }
    }
}
