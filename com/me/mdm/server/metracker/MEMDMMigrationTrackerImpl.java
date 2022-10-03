package com.me.mdm.server.metracker;

import org.json.JSONObject;
import com.me.mdm.mdmmigration.APIServiceDataHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import java.util.logging.Logger;

public class MEMDMMigrationTrackerImpl extends MEMDMTrackerConstants
{
    private Logger logger;
    private String sourceClass;
    
    public MEMDMMigrationTrackerImpl() {
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMMigrationTrackerImpl";
    }
    
    public Properties getTrackerProperties() {
        SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDMP Migration implementation starts...");
        final Properties mdmTrackerProperties = new Properties();
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                final JSONObject trackingProperties = new APIServiceDataHandler().getMigrationTypeWiseCount(customerID);
                mdmTrackerProperties.setProperty("Migration_Tracking_Details", trackingProperties.toString());
                SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + mdmTrackerProperties);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getTrackerProperties", "Exception : ", (Throwable)e);
        }
        return mdmTrackerProperties;
    }
}
