package com.me.mdm.server.metracker;

import org.json.JSONObject;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import java.util.logging.Logger;

public class MEMDMTrackerMDMPGroupsImpl extends MEMDMTrackerConstants
{
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerMDMPGroupsImpl() {
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMTrackerMDMPGroupsImpl";
    }
    
    public Properties getTrackerProperties() {
        SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDMP Groups implementation starts...");
        final Properties mdmTrackerProperties = new Properties();
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final JSONObject trackingProperties = MDMGroupHandler.getInstance().getGroupTypeWiseCount(customerID);
            mdmTrackerProperties.setProperty("Groups_Tracking_Details", trackingProperties.toString());
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getTrackerProperties", "Exception : ", (Throwable)e);
        }
        return mdmTrackerProperties;
    }
}
