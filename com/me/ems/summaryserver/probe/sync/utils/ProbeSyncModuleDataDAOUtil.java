package com.me.ems.summaryserver.probe.sync.utils;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class ProbeSyncModuleDataDAOUtil
{
    private static Logger logger;
    private static String sourceClass;
    
    public long getCurrSyncLockedTime(final long moduleID) {
        final String sourceMethod = "getCurrSyncLockedTime";
        long currSyncLockedTime = -1L;
        try {
            currSyncLockedTime = (long)this.getProbeSyncModuleData(moduleID, "CURR_SYNC_LOCKED_TIME");
        }
        catch (final Exception ex) {
            SyMLogger.error(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while retrieving CURR_SYNC_LOCKED_TIME for moduleId = " + moduleID, ex);
        }
        return currSyncLockedTime;
    }
    
    public boolean updateCurrSyncLockedTime(final long moduleID, final long currSyncLockedTime) {
        return this.addOrUpdateProbeSyncModuleData(moduleID, "CURR_SYNC_LOCKED_TIME", currSyncLockedTime);
    }
    
    public long getLastSyncTime(final long moduleID) {
        final String sourceMethod = "getLastSyncTime";
        long lastSyncTime = -1L;
        try {
            lastSyncTime = (long)this.getProbeSyncModuleData(moduleID, "LAST_SYNC_TIME");
        }
        catch (final Exception ex) {
            SyMLogger.error(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while retrieving LAST_SYNC_TIME for moduleId = " + moduleID, ex);
        }
        return lastSyncTime;
    }
    
    public boolean updateLastSyncTime(final long moduleID, final long currSyncLockedTime) {
        return this.addOrUpdateProbeSyncModuleData(moduleID, "LAST_SYNC_TIME", currSyncLockedTime);
    }
    
    public long getLastSuccessfulSyncTime(final long moduleID) {
        final String sourceMethod = "getLastSuccessfulSyncTime";
        long lastSuccessfulSyncTime = -1L;
        try {
            lastSuccessfulSyncTime = (long)this.getProbeSyncModuleData(moduleID, "LAST_SUCCESSFUL_SYNC_TIME");
        }
        catch (final Exception ex) {
            SyMLogger.error(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while retrieving LAST_SUCCESSFUL_SYNC_TIME for moduleId = " + moduleID, ex);
        }
        return lastSuccessfulSyncTime;
    }
    
    public boolean updateLastSuccessfulSyncTime(final long moduleID, final long currSyncLockedTime) {
        return this.addOrUpdateProbeSyncModuleData(moduleID, "LAST_SUCCESSFUL_SYNC_TIME", currSyncLockedTime);
    }
    
    public long getLastSummaryUpdateTime(final long moduleID) {
        final String sourceMethod = "getLastSummaryUpdateTime";
        long lastSummaryUpdateTime = -1L;
        try {
            lastSummaryUpdateTime = (long)this.getProbeSyncModuleData(moduleID, "LAST_UPDATE_FROM_SUMMARY");
        }
        catch (final Exception ex) {
            SyMLogger.error(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while retrieving LAST_UPDATE_FROM_SUMMARY for moduleId = " + moduleID, ex);
        }
        return lastSummaryUpdateTime;
    }
    
    public boolean updateLastSummaryUpdateTime(final long moduleID, final long currSyncLockedTime) {
        return this.addOrUpdateProbeSyncModuleData(moduleID, "LAST_UPDATE_FROM_SUMMARY", currSyncLockedTime);
    }
    
    public Object getProbeSyncModuleData(final long moduleID, final String columnName) {
        final String sourceMethod = "getProbeSyncModuleData";
        Object columnValue = null;
        try {
            final DataObject probeSyncModuleDataDO = this.getProbeSyncModuleDataDO(moduleID);
            if (!probeSyncModuleDataDO.isEmpty()) {
                columnValue = probeSyncModuleDataDO.getFirstValue("ProbeSyncModuleData", columnName);
            }
            SyMLogger.debug(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, columnName + " value returned from ProbeSyncModuleData for moduleId = " + moduleID + " : " + columnValue);
        }
        catch (final Exception ex) {
            SyMLogger.error(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while retrieving ProbeSyncModuleData for moduleId = " + moduleID + " and " + columnName, ex);
        }
        return columnValue;
    }
    
    public DataObject getProbeSyncModuleDataDO(final long moduleID) {
        final String sourceMethod = "getProbeSyncModuleDataDO";
        DataObject probeSyncModuleDataDO = null;
        try {
            final Column moduleIDColumn = new Column("ProbeSyncModuleData", "SYNC_MODULE_ID");
            final Criteria moduleCri = new Criteria(moduleIDColumn, (Object)moduleID, 0);
            probeSyncModuleDataDO = DataAccess.get("ProbeSyncModuleData", moduleCri);
            if (probeSyncModuleDataDO.isEmpty()) {
                SyMLogger.info(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, "ProbeSyncModuleData DO is Empty! for moduleID = " + moduleID);
                SyMLogger.info(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, "Going to create new ProbeSyncModuleData row for moduleID = " + moduleID);
                final Row syncModuleDataRow = new Row("ProbeSyncModuleData");
                syncModuleDataRow.set("SYNC_MODULE_ID", (Object)moduleID);
                probeSyncModuleDataDO.addRow(syncModuleDataRow);
                probeSyncModuleDataDO = DataAccess.add(probeSyncModuleDataDO);
            }
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while retrieving ProbeSyncModuleData DO for moduleId = " + moduleID, (Throwable)ex);
        }
        return probeSyncModuleDataDO;
    }
    
    public boolean addOrUpdateProbeSyncModuleData(final long moduleID, final String columnName, final Object columnValue) {
        final String sourceMethod = "addOrUpdateProbeSyncModuleData";
        boolean isUpdated = true;
        try {
            final DataObject probeSyncModuleDataDO = this.getProbeSyncModuleDataDO(moduleID);
            if (probeSyncModuleDataDO.isEmpty()) {
                final Row syncModuleDataRow = new Row("ProbeSyncModuleData");
                syncModuleDataRow.set("SYNC_MODULE_ID", (Object)moduleID);
                syncModuleDataRow.set(columnName, columnValue);
                probeSyncModuleDataDO.addRow(syncModuleDataRow);
                DataAccess.add(probeSyncModuleDataDO);
                SyMLogger.info(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, columnName + " added in ProbeSyncModuleData for moduleId = " + moduleID + " : " + columnValue);
            }
            else {
                final Row syncModuleDataRow = probeSyncModuleDataDO.getFirstRow("ProbeSyncModuleData");
                syncModuleDataRow.set(columnName, columnValue);
                probeSyncModuleDataDO.updateRow(syncModuleDataRow);
                DataAccess.update(probeSyncModuleDataDO);
                SyMLogger.info(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, columnName + " updated in ProbeSyncModuleData for moduleId = " + moduleID + " : " + columnValue);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(ProbeSyncModuleDataDAOUtil.logger, ProbeSyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while updating ProbeSyncModuleData for moduleId = " + moduleID + " and " + columnName + " with " + columnValue, ex);
            isUpdated = false;
        }
        return isUpdated;
    }
    
    static {
        ProbeSyncModuleDataDAOUtil.logger = Logger.getLogger("ProbeSyncLogger");
        ProbeSyncModuleDataDAOUtil.sourceClass = "ProbeSyncModuleDataDAOUtil";
    }
}
