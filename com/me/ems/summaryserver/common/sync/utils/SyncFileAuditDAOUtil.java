package com.me.ems.summaryserver.common.sync.utils;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class SyncFileAuditDAOUtil
{
    private static Logger logger;
    private static String sourceClass;
    
    public int getFileAuditRowCount(final long moduleAuditID) {
        final String sourceMethod = "getModuleSyncFileCount";
        int filesProcessed = 0;
        try {
            final DataObject fileAuditDO = this.getSyncFileAuditDO(moduleAuditID);
            filesProcessed = fileAuditDO.size("SyncFileAudit");
            SyMLogger.debug(SyncFileAuditDAOUtil.logger, SyncFileAuditDAOUtil.sourceClass, sourceMethod, "Processed sync file count : " + filesProcessed + " for moduleAuditID : " + moduleAuditID);
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(SyncFileAuditDAOUtil.logger, SyncFileAuditDAOUtil.sourceClass, sourceMethod, "Exception while retrieving module SyncFile count from DB.", (Throwable)ex);
        }
        return filesProcessed;
    }
    
    public DataObject getSyncFileAuditDO(final long moduleAuditID) throws DataAccessException {
        final String sourceMethod = "getModuleSyncFileDO";
        final Column moduleAuditIDCol = Column.getColumn("SyncFileAudit", "MODULE_AUDIT_ID");
        final Criteria criteria = new Criteria(moduleAuditIDCol, (Object)moduleAuditID, 0);
        final DataObject fileAuditDO = DataAccess.get("SyncFileAudit", criteria);
        SyMLogger.debug(SyncFileAuditDAOUtil.logger, SyncFileAuditDAOUtil.sourceClass, sourceMethod, "SyncFileAudit DO : " + fileAuditDO + " for moduleAuditID : " + moduleAuditID);
        return fileAuditDO;
    }
    
    public boolean addOrUpdateSyncFileStatus(final long moduleAuditID, final String fileName, final int fileStatus) {
        final String sourceMethod = "updateSyncFileAudit";
        boolean isUpdated = true;
        try {
            final Column syncTimeCol = Column.getColumn("SyncFileAudit", "MODULE_AUDIT_ID");
            Criteria criteria = new Criteria(syncTimeCol, (Object)moduleAuditID, 0);
            final Column probeIDCol = Column.getColumn("SyncFileAudit", "FILE_NAME");
            criteria = criteria.and(new Criteria(probeIDCol, (Object)fileName, 0));
            final DataObject fileAuditDO = DataAccess.get("SyncFileAudit", criteria);
            if (fileAuditDO.isEmpty()) {
                final Row fileAuditRow = new Row("SyncFileAudit");
                fileAuditRow.set("MODULE_AUDIT_ID", (Object)moduleAuditID);
                fileAuditRow.set("FILE_NAME", (Object)fileName);
                fileAuditRow.set("FILE_STATUS", (Object)fileStatus);
                fileAuditDO.addRow(fileAuditRow);
                DataAccess.add(fileAuditDO);
                SyMLogger.debug(SyncFileAuditDAOUtil.logger, SyncFileAuditDAOUtil.sourceClass, sourceMethod, "FileStatus added in DB:- file Name: " + fileName + "  file Status: " + fileStatus);
            }
            else {
                final Row fileAuditRow = fileAuditDO.getFirstRow("SyncFileAudit");
                fileAuditRow.set("FILE_STATUS", (Object)fileStatus);
                if (fileStatus == 950002 || fileStatus == 950001) {
                    int retryCount = (int)fileAuditRow.get("RETRY_COUNT");
                    ++retryCount;
                    fileAuditRow.set("RETRY_COUNT", (Object)retryCount);
                }
                fileAuditDO.updateRow(fileAuditRow);
                DataAccess.update(fileAuditDO);
                SyMLogger.debug(SyncFileAuditDAOUtil.logger, SyncFileAuditDAOUtil.sourceClass, sourceMethod, "FileStatus updated in DB:- file Name: " + fileName + "  file Status: " + fileStatus);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SyncFileAuditDAOUtil.logger, SyncFileAuditDAOUtil.sourceClass, sourceMethod, "Exception while updating fileStatus : " + fileStatus + " for file name :" + fileName, ex);
            isUpdated = false;
        }
        return isUpdated;
    }
    
    public boolean addOrUpdateSyncFileStatus(final long moduleAuditID, final String fileName, final int fileStatus, final int retryCount) {
        final String sourceMethod = "updateSyncFileAudit";
        boolean isUpdated = true;
        try {
            final Column syncTimeCol = Column.getColumn("SyncFileAudit", "MODULE_AUDIT_ID");
            Criteria criteria = new Criteria(syncTimeCol, (Object)moduleAuditID, 0);
            final Column probeIDCol = Column.getColumn("SyncFileAudit", "FILE_NAME");
            criteria = criteria.and(new Criteria(probeIDCol, (Object)fileName, 0));
            final DataObject fileAuditDO = DataAccess.get("SyncFileAudit", criteria);
            if (fileAuditDO.isEmpty()) {
                final Row fileAuditRow = new Row("SyncFileAudit");
                fileAuditRow.set("MODULE_AUDIT_ID", (Object)moduleAuditID);
                fileAuditRow.set("FILE_NAME", (Object)fileName);
                fileAuditRow.set("FILE_STATUS", (Object)fileStatus);
                fileAuditRow.set("RETRY_COUNT", (Object)retryCount);
                fileAuditDO.addRow(fileAuditRow);
                DataAccess.add(fileAuditDO);
                SyMLogger.debug(SyncFileAuditDAOUtil.logger, SyncFileAuditDAOUtil.sourceClass, sourceMethod, "FileStatus added in DB:- file Name: " + fileName + "  file Status: " + fileStatus);
            }
            else {
                final Row fileAuditRow = fileAuditDO.getFirstRow("SyncFileAudit");
                fileAuditRow.set("FILE_STATUS", (Object)fileStatus);
                fileAuditRow.set("RETRY_COUNT", (Object)retryCount);
                fileAuditDO.updateRow(fileAuditRow);
                DataAccess.update(fileAuditDO);
                SyMLogger.debug(SyncFileAuditDAOUtil.logger, SyncFileAuditDAOUtil.sourceClass, sourceMethod, "FileStatus updated in DB:- file Name: " + fileName + "  file Status: " + fileStatus);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SyncFileAuditDAOUtil.logger, SyncFileAuditDAOUtil.sourceClass, sourceMethod, "Exception while updating fileStatus : " + fileStatus + " for file name :" + fileName, ex);
            isUpdated = false;
        }
        return isUpdated;
    }
    
    static {
        SyncFileAuditDAOUtil.logger = Logger.getLogger("SummarySyncLogger");
        SyncFileAuditDAOUtil.sourceClass = "SyncFileAuditDAOUtil";
    }
}
