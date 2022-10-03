package com.me.mdm.server.metracker;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerGroupsImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerGroupsImpl() {
        this.mdmTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMPGroupsImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDMP Groups implementation starts...");
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.addAndroidGroupCount();
            this.addIosGroupCount();
            this.addTotalGroupCount();
            this.addWindowsGroupCount();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmTrackerProperties;
    }
    
    private void addAndroidGroupCount() {
        try {
            final Criteria androidTypeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)4, 0);
            final int group = DBUtil.getRecordCount("CustomGroup", "RESOURCE_ID", androidTypeCri);
            this.mdmTrackerProperties.setProperty("Android_Group_Count", String.valueOf(group));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidGroupCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIosGroupCount() {
        try {
            final Criteria iosTypeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)3, 0);
            final int group = DBUtil.getRecordCount("CustomGroup", "RESOURCE_ID", iosTypeCri);
            this.mdmTrackerProperties.setProperty("iOS_Group_Count", String.valueOf(group));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosGroupCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addWindowsGroupCount() {
        try {
            final Criteria windowsTypeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)5, 0);
            final int group = DBUtil.getRecordCount("CustomGroup", "RESOURCE_ID", windowsTypeCri);
            this.mdmTrackerProperties.setProperty("Windows_Group_Count", String.valueOf(group));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosGroupCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addTotalGroupCount() {
        try {
            final Criteria allMDMGroupTypeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)new Integer[] { 5, 3, 4 }, 8);
            final int group = DBUtil.getRecordCount("CustomGroup", "RESOURCE_ID", allMDMGroupTypeCri);
            this.mdmTrackerProperties.setProperty("Total_Group_Count", String.valueOf(group));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addTotalGroupCount", "Exception : ", (Throwable)e);
        }
    }
}
