package com.me.mdm.server.metracker;

import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import java.util.logging.Logger;

public class MEMDMTrackerBulkActionsImpl extends MEMDMTrackerConstants
{
    private Logger logger;
    private String sourceClass;
    Properties mdmTrackerProperties;
    
    public MEMDMTrackerBulkActionsImpl() {
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMTrackerBulkActionsImpl";
        this.mdmTrackerProperties = new Properties();
    }
    
    public Properties getTrackerProperties() {
        SyMLogger.info(this.logger, this.sourceClass, "getProperties::MEMDMTrackerBulkActionsImpl", "MDM Bulk Actions implementation starts...");
        try {
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.addGroupActionDetails();
            this.addDeviceActionDetails();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties::MEMDMTrackerBulkActionsImpl", "Details Summary : " + this.mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getTrackerProperties::MEMDMTrackerBulkActionsImpl ", "Exception : ", (Throwable)e);
        }
        return this.mdmTrackerProperties;
    }
    
    private void addGroupActionDetails() {
        try {
            final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("BULK_GROUP_ACTIONS_QUERY");
            this.mdmTrackerProperties.setProperty("GROUP_ACTION_DETAILS", MEMDMTrackerUtil.getGroupActionCount(selectQuery));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in adding bulk group action details", ex);
        }
    }
    
    private void addDeviceActionDetails() {
        try {
            final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("BULK_DEVICE_ACTIONS_QUERY");
            this.mdmTrackerProperties.setProperty("DEVICE_ACTION_DETAILS", MEMDMTrackerUtil.getDeviceActionCount(selectQuery));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in adding bulk device action details", ex);
        }
    }
}
