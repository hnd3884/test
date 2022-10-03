package com.me.ems.summaryserver.common.sync.utils;

import com.me.devicemanagement.framework.server.deletionfw.DeletionFramework;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class SyncModuleAuditDAOUtil
{
    private static Logger logger;
    private static String sourceClass;
    
    public DataObject getSyncModuleDOAfterSyncTime(final long probeID, final long moduleID, final long syncTime) throws DataAccessException {
        final String sourceMethod = "getAllSyncAfterSyncTime";
        DataObject moduleAuditDO;
        try {
            final Column syncTimeCol = Column.getColumn("SyncModuleAudit", "SYNC_TIME");
            Criteria criteria = new Criteria(syncTimeCol, (Object)syncTime, 5);
            final Column probeIDCol = Column.getColumn("SyncModuleAudit", "PROBE_ID");
            criteria = criteria.and(new Criteria(probeIDCol, (Object)probeID, 0));
            final Column moduleIDCol = Column.getColumn("SyncModuleAudit", "SYNC_MODULE_ID");
            criteria = criteria.and(new Criteria(moduleIDCol, (Object)moduleID, 0));
            moduleAuditDO = DataAccess.get("SyncModuleAudit", criteria);
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "Caught exception while getModuleSyncAuditRow :", (Throwable)ex);
            throw ex;
        }
        return moduleAuditDO;
    }
    
    public DataObject getSyncModuleAuditDO(final long probeID, final long moduleID, final long syncTime) throws DataAccessException {
        final String sourceMethod = "getSyncModuleAuditDO";
        DataObject moduleAuditDO;
        try {
            final Column syncTimeCol = Column.getColumn("SyncModuleAudit", "SYNC_TIME");
            Criteria criteria = new Criteria(syncTimeCol, (Object)syncTime, 0);
            final Column probeIDCol = Column.getColumn("SyncModuleAudit", "PROBE_ID");
            criteria = criteria.and(new Criteria(probeIDCol, (Object)probeID, 0));
            final Column moduleIDCol = Column.getColumn("SyncModuleAudit", "SYNC_MODULE_ID");
            criteria = criteria.and(new Criteria(moduleIDCol, (Object)moduleID, 0));
            moduleAuditDO = DataAccess.get("SyncModuleAudit", criteria);
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "Caught exception while getModuleSyncAuditRow :", (Throwable)ex);
            throw ex;
        }
        return moduleAuditDO;
    }
    
    public Row getSyncModuleAuditRow(final long probeID, final long moduleID, final long syncTime) {
        final String sourceMethod = "getSyncModuleAuditRow";
        Row syncModuleAuditRow = null;
        try {
            final DataObject moduleAuditDO = this.getSyncModuleAuditDO(probeID, moduleID, syncTime);
            if (moduleAuditDO != null && !moduleAuditDO.isEmpty()) {
                syncModuleAuditRow = moduleAuditDO.getFirstRow("SyncModuleAudit");
            }
            else {
                SyMLogger.info(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "SyncModuleAudit empty for ProbeID :" + probeID + ", moduleID : " + moduleID + ", syncTime :" + syncTime);
            }
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "Caught exception while getModuleSyncAuditRow :", (Throwable)ex);
        }
        return syncModuleAuditRow;
    }
    
    public long getModuleAuditID(final long probeID, final long moduleID, final long syncTime) {
        final String sourceMethod = "getModuleAuditID";
        long moduleAuditId = -1L;
        try {
            final Row moduleAuditRow = this.getSyncModuleAuditRow(probeID, moduleID, syncTime);
            if (moduleAuditRow != null) {
                moduleAuditId = (long)moduleAuditRow.get("MODULE_AUDIT_ID");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "Caught exception while getModuleAuditID :", ex);
        }
        return moduleAuditId;
    }
    
    public int getFilesSentFromProbe(final long probeID, final long moduleID, final long syncTime) {
        final String sourceMethod = "getFilesSentFromProbe";
        int filesSentFromProbe = 0;
        final Row syncModuleAuditRow = this.getSyncModuleAuditRow(probeID, moduleID, syncTime);
        if (syncModuleAuditRow != null) {
            filesSentFromProbe = (int)syncModuleAuditRow.get("FILES_POSTED_FROM_PROBE");
        }
        SyMLogger.debug(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "FilesSentFromProbe = " + filesSentFromProbe + " for probeID =" + probeID + ", moduleID = " + moduleID + ", syncTime =" + syncTime);
        return filesSentFromProbe;
    }
    
    public long incrementFilesSentFromProbeCount(final long probeID, final long moduleID, final long syncTime) throws DataAccessException {
        final String sourceMethod = "incrementFilesSentFromProbeCount";
        final DataObject moduleAuditDO = this.getSyncModuleAuditDO(probeID, moduleID, syncTime);
        int filesSentFromProbe = 1;
        if (moduleAuditDO.isEmpty()) {
            SyMLogger.info(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "DO Empty, creating new row in SyncModuleAudit for probeID =" + probeID + ", moduleID = " + moduleID + ", syncTime =" + syncTime);
            final Row moduleAuditRow = new Row("SyncModuleAudit");
            moduleAuditRow.set("SYNC_TIME", (Object)syncTime);
            moduleAuditRow.set("PROBE_ID", (Object)probeID);
            moduleAuditRow.set("SYNC_MODULE_ID", (Object)moduleID);
            moduleAuditRow.set("FILES_POSTED_FROM_PROBE", (Object)filesSentFromProbe);
            moduleAuditDO.addRow(moduleAuditRow);
        }
        else {
            final Row moduleAuditRow = moduleAuditDO.getFirstRow("SyncModuleAudit");
            filesSentFromProbe = (int)moduleAuditRow.get("FILES_POSTED_FROM_PROBE");
            ++filesSentFromProbe;
            moduleAuditRow.set("FILES_POSTED_FROM_PROBE", (Object)filesSentFromProbe);
            moduleAuditDO.updateRow(moduleAuditRow);
        }
        final DataObject resultantDO = DataAccess.update(moduleAuditDO);
        SyMLogger.debug(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "FILES_POSTED_FROM_PROBE updated in DB:- " + filesSentFromProbe + " for probeID = " + probeID + ", moduleID = " + moduleID + ", syncTime = " + syncTime);
        final long moduleAuditID = (long)resultantDO.getFirstValue("SyncModuleAudit", "MODULE_AUDIT_ID");
        return moduleAuditID;
    }
    
    public long addOrUpdateSyncModuleAudit(final long probeID, final long moduleID, final long syncTime, final int filesSentFromProbe) {
        final String sourceMethod = "updateSyncModuleAudit";
        long moduleAuditId = -1L;
        try {
            final DataObject moduleAuditDO = this.getSyncModuleAuditDO(probeID, moduleID, syncTime);
            if (moduleAuditDO.isEmpty()) {
                final Row moduleAuditRow = new Row("SyncModuleAudit");
                moduleAuditRow.set("SYNC_TIME", (Object)syncTime);
                moduleAuditRow.set("PROBE_ID", (Object)probeID);
                moduleAuditRow.set("SYNC_MODULE_ID", (Object)moduleID);
                moduleAuditRow.set("FILES_POSTED_FROM_PROBE", (Object)filesSentFromProbe);
                moduleAuditDO.addRow(moduleAuditRow);
            }
            else {
                final Row moduleAuditRow = moduleAuditDO.getFirstRow("SyncModuleAudit");
                moduleAuditRow.set("FILES_POSTED_FROM_PROBE", (Object)filesSentFromProbe);
                moduleAuditDO.updateRow(moduleAuditRow);
            }
            final DataObject resultantDO = DataAccess.update(moduleAuditDO);
            SyMLogger.debug(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "FILES_POSTED_FROM_PROBE updated in DB:- " + filesSentFromProbe + " for probeID = " + probeID + ", moduleID = " + moduleID + ", syncTime = " + syncTime);
            moduleAuditId = (long)resultantDO.getFirstValue("SyncModuleAudit", "MODULE_AUDIT_ID");
        }
        catch (final Exception ex) {
            SyMLogger.error(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "Caught exception while getModuleSyncAuditRow :", ex);
        }
        return moduleAuditId;
    }
    
    public int getModuleSyncStatus(final long probeID, final long moduleID, final long syncTime) {
        final String sourceMethod = "getModuleSyncStatus";
        int moduleSyncStatus = 6;
        final Row syncModuleAuditRow = this.getSyncModuleAuditRow(probeID, moduleID, syncTime);
        if (syncModuleAuditRow != null) {
            moduleSyncStatus = (int)syncModuleAuditRow.get("MODULE_SYNC_STATUS");
        }
        SyMLogger.debug(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "MODULE_SYNC_STATUS = " + moduleSyncStatus + " for probeID =" + probeID + ", moduleID = " + moduleID + ", syncTime =" + syncTime);
        return moduleSyncStatus;
    }
    
    public boolean updateModuleSyncStatus(final long probeID, final long moduleID, final long syncTime) {
        final String sourceMethod = "updateModuleSyncStatus";
        int moduleSyncStatus = 6;
        final SyncFileAuditDAOUtil syncFileAuditDAOUtil = new SyncFileAuditDAOUtil();
        boolean isUpdated = false;
        try {
            final DataObject moduleAuditDO = this.getSyncModuleAuditDO(probeID, moduleID, syncTime);
            if (!moduleAuditDO.isEmpty()) {
                final Row moduleAuditRow = moduleAuditDO.getFirstRow("SyncModuleAudit");
                final long moduleAuditID = (long)moduleAuditRow.get("MODULE_AUDIT_ID");
                final int filesPostedFromProbe = (int)moduleAuditRow.get("FILES_POSTED_FROM_PROBE");
                final DataObject fileAuditDO = syncFileAuditDAOUtil.getSyncFileAuditDO(moduleAuditID);
                int totalFileCount = 0;
                int successFileCount = 0;
                int inProgressFileCount = 0;
                int failedFileCount = 0;
                if (fileAuditDO != null && !fileAuditDO.isEmpty()) {
                    final Iterator fileAuditRows = fileAuditDO.getRows("SyncFileAudit");
                    while (fileAuditRows.hasNext()) {
                        final Row fileAuditRow = fileAuditRows.next();
                        final int fileStatus = (int)fileAuditRow.get("FILE_STATUS");
                        if (fileStatus == 950100) {
                            ++successFileCount;
                        }
                        else if (fileStatus == 950101) {
                            ++inProgressFileCount;
                        }
                        else {
                            ++failedFileCount;
                        }
                        ++totalFileCount;
                    }
                }
                if (inProgressFileCount > 0) {
                    moduleSyncStatus = 2;
                }
                else if (failedFileCount > 0) {
                    moduleSyncStatus = 4;
                }
                else if (filesPostedFromProbe == successFileCount) {
                    moduleSyncStatus = 1;
                }
                moduleAuditRow.set("FILES_PROCESSED_IN_SUMMARY", (Object)totalFileCount);
                moduleAuditRow.set("MODULE_SYNC_STATUS", (Object)moduleSyncStatus);
                moduleAuditDO.updateRow(moduleAuditRow);
                DataAccess.update(moduleAuditDO);
                isUpdated = true;
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "Caught exception while updateModuleSyncStatus :", ex);
        }
        return isUpdated;
    }
    
    public boolean updateModuleSyncStatus(final long moduleAuditID, final int status) {
        return this.updateSyncModuleAuditRow(moduleAuditID, "MODULE_SYNC_STATUS", status);
    }
    
    public boolean updateFilesPostedFromProbe(final long moduleAuditID, final int filesPostedFromProbe) {
        return this.updateSyncModuleAuditRow(moduleAuditID, "FILES_POSTED_FROM_PROBE", filesPostedFromProbe);
    }
    
    public boolean updateFilesProcessedInSummary(final long moduleAuditID, final int filesProcessedInSummary) {
        return this.updateSyncModuleAuditRow(moduleAuditID, "FILES_PROCESSED_IN_SUMMARY", filesProcessedInSummary);
    }
    
    private boolean updateSyncModuleAuditRow(final long moduleAuditID, final String columnName, final Object columnValue) {
        final String sourceMethod = "updateSyncModuleAuditRow";
        boolean isUpdated = false;
        try {
            final DataObject moduleAuditDO = this.getModuleAuditDO(moduleAuditID);
            if (!moduleAuditDO.isEmpty()) {
                final Row moduleAuditRow = moduleAuditDO.getFirstRow("SyncModuleAudit");
                moduleAuditRow.set(columnName, columnValue);
                moduleAuditDO.updateRow(moduleAuditRow);
                DataAccess.update(moduleAuditDO);
                isUpdated = true;
                SyMLogger.debug(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, columnName + " updated for moduleAuditID:" + moduleAuditID + " : " + columnValue);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "Caught exception while update SyncModuleAudit Row for :" + columnName, ex);
        }
        return isUpdated;
    }
    
    private DataObject getModuleAuditDO(final long moduleAuditID) {
        final String sourceMethod = "getModuleAuditDO";
        final Column moduleAuditCol = Column.getColumn("SyncModuleAudit", "MODULE_AUDIT_ID");
        final Criteria criteria = new Criteria(moduleAuditCol, (Object)moduleAuditID, 0);
        DataObject moduleAuditDO = null;
        try {
            moduleAuditDO = DataAccess.get("SyncModuleAudit", criteria);
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "Caught exception while getModuleAuditDO :", (Throwable)ex);
        }
        return moduleAuditDO;
    }
    
    public boolean cleanUpModuleAudit(final long deletionTime) {
        final String sourceMethod = "getModuleAuditDO";
        boolean isDeleted = false;
        final Column syncTimeCol = Column.getColumn("SyncModuleAudit", "SYNC_TIME");
        final Criteria criteria = new Criteria(syncTimeCol, (Object)deletionTime, 7);
        try {
            final SummarySyncParamsDAOUtil summarySyncParamsDAOUtil = new SummarySyncParamsDAOUtil();
            final int chunk = Integer.parseInt(summarySyncParamsDAOUtil.getSummarySyncParams("AUDIT_DELETION_CHUNK"));
            DeletionFramework.delete("SyncModuleAudit", criteria, chunk);
            isDeleted = true;
        }
        catch (final Exception ex) {
            SyMLogger.error(SyncModuleAuditDAOUtil.logger, SyncModuleAuditDAOUtil.sourceClass, sourceMethod, "Caught exception while cleanUpModuleAudit :", ex);
        }
        return isDeleted;
    }
    
    static {
        SyncModuleAuditDAOUtil.logger = Logger.getLogger("SummarySyncLogger");
        SyncModuleAuditDAOUtil.sourceClass = "SyncModuleAuditDAOUtil";
    }
}
