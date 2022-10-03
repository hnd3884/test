package com.me.ems.summaryserver.summary.sync.utils;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class SummarySyncModuleDataDAOUtil
{
    private static Logger logger;
    private static String sourceClass;
    
    public long getLastSyncTime(final long probeID, final long moduleID) {
        final String sourceMethod = "getLastSyncTime";
        long lastSyncTime = -1L;
        try {
            lastSyncTime = (long)this.getSummarySyncModuleData(probeID, moduleID, "LAST_SYNC_TIME");
        }
        catch (final Exception ex) {
            SyMLogger.error(SummarySyncModuleDataDAOUtil.logger, SummarySyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while retrieving LAST_SYNC_TIME from SummarySyncModuleData for moduleId = " + moduleID, ex);
        }
        return lastSyncTime;
    }
    
    public boolean addOrUpdateLastSyncTime(final long probeID, final long moduleID, final long syncTime) {
        return this.addOrUpdateSummarySyncModuleData(probeID, moduleID, "LAST_SYNC_TIME", syncTime);
    }
    
    public long getLastSuccessfulSyncTime(final long probeID, final long moduleID) {
        final String sourceMethod = "getLastSuccessfulSyncTime";
        long lastSuccessfulSyncTime = -1L;
        try {
            lastSuccessfulSyncTime = (long)this.getSummarySyncModuleData(probeID, moduleID, "LAST_SUCCESSFUL_SYNC_TIME");
        }
        catch (final Exception ex) {
            SyMLogger.error(SummarySyncModuleDataDAOUtil.logger, SummarySyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while retrieving LAST_SUCCESSFUL_SYNC_TIME from SummarySyncModuleData for moduleId = " + moduleID, ex);
        }
        return lastSuccessfulSyncTime;
    }
    
    public boolean addOrUpdateLastSuccessfulSyncTime(final long probeID, final long moduleID, final long syncTime) {
        return this.addOrUpdateSummarySyncModuleData(probeID, moduleID, "LAST_SUCCESSFUL_SYNC_TIME", syncTime);
    }
    
    public boolean addOrUpdateSummarySyncModuleData(final long probeID, final long moduleID, final String columnName, final Object columnValue) {
        final String sourceMethod = "addOrUpdateSummarySyncModuleData";
        boolean isUpdated = true;
        try {
            final DataObject summarySyncModuleDataDO = this.getSummarySyncModuleDataDO(probeID, moduleID);
            if (summarySyncModuleDataDO.isEmpty()) {
                final Row syncModuleDataRow = new Row("SummarySyncModuleData");
                syncModuleDataRow.set("PROBE_ID", (Object)probeID);
                syncModuleDataRow.set("SYNC_MODULE_ID", (Object)moduleID);
                syncModuleDataRow.set(columnName, columnValue);
                summarySyncModuleDataDO.addRow(syncModuleDataRow);
                DataAccess.add(summarySyncModuleDataDO);
                SyMLogger.info(SummarySyncModuleDataDAOUtil.logger, SummarySyncModuleDataDAOUtil.sourceClass, sourceMethod, columnName + " added in SummarySyncModuleData for moduleId = " + moduleID + " : " + columnValue);
            }
            else {
                final Row syncModuleDataRow = summarySyncModuleDataDO.getFirstRow("SummarySyncModuleData");
                syncModuleDataRow.set(columnName, columnValue);
                summarySyncModuleDataDO.updateRow(syncModuleDataRow);
                DataAccess.update(summarySyncModuleDataDO);
                SyMLogger.info(SummarySyncModuleDataDAOUtil.logger, SummarySyncModuleDataDAOUtil.sourceClass, sourceMethod, columnName + " updated in SummarySyncModuleData for moduleId = " + moduleID + " : " + columnValue);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SummarySyncModuleDataDAOUtil.logger, SummarySyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while updating SummarySyncModuleData for moduleId = " + moduleID + " and " + columnName + " with " + columnValue, ex);
            isUpdated = false;
        }
        return isUpdated;
    }
    
    private Object getSummarySyncModuleData(final long probeID, final long moduleID, final String columnName) {
        final String sourceMethod = "getSummarySyncModuleData";
        Object columnValue = null;
        try {
            final DataObject probeSyncModuleDataDO = this.getSummarySyncModuleDataDO(probeID, moduleID);
            if (!probeSyncModuleDataDO.isEmpty()) {
                columnValue = probeSyncModuleDataDO.getFirstValue("SummarySyncModuleData", columnName);
            }
            SyMLogger.debug(SummarySyncModuleDataDAOUtil.logger, SummarySyncModuleDataDAOUtil.sourceClass, sourceMethod, columnName + " value returned from SummarySyncModuleData for moduleId = " + moduleID + " : " + columnValue);
        }
        catch (final Exception ex) {
            SyMLogger.error(SummarySyncModuleDataDAOUtil.logger, SummarySyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while retrieving SummarySyncModuleData for moduleId = " + moduleID + " and " + columnName, ex);
        }
        return columnValue;
    }
    
    private DataObject getSummarySyncModuleDataDO(final long probeID, final long moduleID) {
        final String sourceMethod = "getSummarySyncModuleDataDO";
        DataObject summarySyncModuleData = null;
        try {
            final Column probeIDColumn = new Column("SummarySyncModuleData", "PROBE_ID");
            final Criteria probeCri = new Criteria(probeIDColumn, (Object)probeID, 0);
            final Column moduleIDColumn = new Column("SummarySyncModuleData", "SYNC_MODULE_ID");
            final Criteria moduleCri = new Criteria(moduleIDColumn, (Object)moduleID, 0);
            final Criteria criteria = probeCri.and(moduleCri);
            summarySyncModuleData = DataAccess.get("SummarySyncModuleData", criteria);
            if (summarySyncModuleData.isEmpty()) {
                SyMLogger.info(SummarySyncModuleDataDAOUtil.logger, SummarySyncModuleDataDAOUtil.sourceClass, sourceMethod, "SummarySyncModuleData DO is Empty! for moduleID = " + moduleID);
                SyMLogger.info(SummarySyncModuleDataDAOUtil.logger, SummarySyncModuleDataDAOUtil.sourceClass, sourceMethod, "Going to create new SummarySyncModuleData row for moduleID = " + moduleID);
                final Row syncModuleDataRow = new Row("SummarySyncModuleData");
                syncModuleDataRow.set("PROBE_ID", (Object)probeID);
                syncModuleDataRow.set("SYNC_MODULE_ID", (Object)moduleID);
                summarySyncModuleData.addRow(syncModuleDataRow);
                summarySyncModuleData = DataAccess.add(summarySyncModuleData);
            }
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(SummarySyncModuleDataDAOUtil.logger, SummarySyncModuleDataDAOUtil.sourceClass, sourceMethod, "Exception while retrieving SummarySyncModuleData DO for moduleId = " + moduleID, (Throwable)ex);
        }
        return summarySyncModuleData;
    }
    
    static {
        SummarySyncModuleDataDAOUtil.logger = Logger.getLogger("SummarySyncLogger");
        SummarySyncModuleDataDAOUtil.sourceClass = "SummarySyncModuleDataDAOUtil";
    }
}
