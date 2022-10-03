package com.me.devicemanagement.onpremise.server.metrack;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class METrackerTrackingImpl implements MEDMTracker
{
    private Properties meTrackingTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public METrackerTrackingImpl() {
        this.meTrackingTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "METrackerTrackingImpl";
    }
    
    @Override
    public Properties getTrackerProperties() {
        try {
            this.cleanUPMETrackingTrackerProperties();
            this.updateLastPostFailedReason();
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getTrackerProperties", "Exception occurred : ", (Throwable)e);
        }
        return this.getMETrackingTrackerProperties();
    }
    
    private void cleanUPMETrackingTrackerProperties() {
        if (!this.meTrackingTrackerProperties.isEmpty()) {
            this.meTrackingTrackerProperties = new Properties();
        }
    }
    
    private Properties getMETrackingTrackerProperties() {
        return this.meTrackingTrackerProperties;
    }
    
    private void updateLastPostFailedReason() {
        try {
            final String metrackDirPath = METrackerDiffUtil.getInstance().getMETrackDir();
            final String lastPostFailedStatusDir = metrackDirPath + File.separator + "last_post_failed_status.properties";
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(lastPostFailedStatusDir)) {
                final Properties properties = FileAccessUtil.readProperties(lastPostFailedStatusDir);
                if (properties.containsKey("LastPostFailedStatus")) {
                    ((Hashtable<String, Object>)this.meTrackingTrackerProperties).put("Last_Post_Failed_Status", ((Hashtable<K, Object>)properties).get("LastPostFailedStatus"));
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "updateLastPostFailedReason", "Exception occurred : ", (Throwable)e);
        }
    }
}
