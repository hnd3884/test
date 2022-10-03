package com.me.mdm.onpremise.server.metracker;

import org.json.JSONObject;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import java.util.Map;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;
import com.me.mdm.server.metracker.MEMDMTrackerConstants;

public class MEMDMTrackerWPImpl extends MEMDMTrackerConstants implements MEDMTracker
{
    private Properties mdmWPTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerWPImpl() {
        this.mdmWPTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMWPImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM Windows implementation starts...");
            if (!this.mdmWPTrackerProperties.isEmpty()) {
                this.mdmWPTrackerProperties = new Properties();
            }
            this.mdmWPTrackerProperties.putAll(new com.me.mdm.server.metracker.MEMDMTrackerWPImpl().getTrackerProperties());
            this.addWindowsNotificationCounts();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.getMDMWPTrackerProperties());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "mdmWPTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.getMDMWPTrackerProperties();
    }
    
    private Properties getMDMWPTrackerProperties() {
        return this.mdmWPTrackerProperties;
    }
    
    private void addWindowsNotificationCounts() {
        try {
            final String totalNotification = METrackerUtil.getMETrackParams("Win_Total_Notification_Count").getProperty("Win_Total_Notification_Count");
            final String successNotification = METrackerUtil.getMETrackParams("Win_Success_Notification_Count").getProperty("Win_Success_Notification_Count");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("Win_Total_Notification_Count", (Object)totalNotification);
            jsonObject.put("Win_Success_Notification_Count", (Object)successNotification);
            final String jsonString = jsonObject.toString();
            this.mdmWPTrackerProperties.setProperty("Windows_Notification_Details", jsonString);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addWindowsNotificationCounts", "Exception :", (Throwable)e);
        }
    }
}
