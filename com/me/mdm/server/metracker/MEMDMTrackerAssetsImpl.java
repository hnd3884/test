package com.me.mdm.server.metracker;

import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerAssetsImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerAssetsImpl() {
        this.mdmTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMPAssetsImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDMP Assets implementation starts...");
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.addMDCustomColumnUsage();
            this.addTotalDiscoveredAppCount();
            this.addPlatformWiseModelInfo();
            this.addSecurityActionInfo();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmTrackerProperties;
    }
    
    private void addMDCustomColumnUsage() {
        try {
            final int deviceNameCount = DBUtil.getRecordActualCount("ManagedDeviceExtn", "MANAGED_DEVICE_ID", new Criteria(Column.getColumn("ManagedDeviceExtn", "IS_MODIFIED"), (Object)true, 0));
            final JSONObject json = new JSONObject();
            json.put((Object)"Device_Name", (Object)deviceNameCount);
            this.mdmTrackerProperties.setProperty("CustomColumn_Details", json.toJSONString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addMDCustomColumnCSVImportUsage", "Exception : ", (Throwable)e);
        }
    }
    
    private void addTotalDiscoveredAppCount() {
        try {
            final int appCount = DBUtil.getRecordCount("MdAppGroupDetails", "APP_GROUP_ID", (Criteria)null);
            this.mdmTrackerProperties.setProperty("TOTAL_DISCOVERED_APPS", String.valueOf(appCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addTotalDiscoveredAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addPlatformWiseModelInfo() {
        final org.json.JSONObject platformModelInfo = MEMDMTrackerUtil.getPlatformModelWiseInfo(null);
        this.mdmTrackerProperties.setProperty("PlatformModelDetails", platformModelInfo.toString());
    }
    
    private void addSecurityActionInfo() {
        final org.json.JSONObject securityActionDetailsJSON = MEMDMTrackerUtil.getSecurityActionsInfo();
        this.mdmTrackerProperties.setProperty("SecurityActionDetails", securityActionDetailsJSON.toString());
    }
}
