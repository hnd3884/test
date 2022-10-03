package com.me.ems.summaryserver.common.sync.utils;

import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class SyncModuleMetaDAOUtil
{
    private static Logger logger;
    private static String sourceClass;
    
    public long getModuleID(final String moduleName) {
        final String sourceMethod = "getModuleID";
        long moduleID = -1L;
        try {
            final DataObject syncModuleMetaDO = this.getSyncModuleMetaDOFromCache();
            if (syncModuleMetaDO != null && !syncModuleMetaDO.isEmpty()) {
                final Column moduleNameColumn = new Column("SyncModuleMeta", "SYNC_MODULE_NAME");
                final Criteria moduleCri = new Criteria(moduleNameColumn, (Object)moduleName, 0, false);
                moduleID = Long.parseLong(String.valueOf(syncModuleMetaDO.getValue("SyncModuleMeta", "SYNC_MODULE_ID", moduleCri)));
            }
            else {
                SyMLogger.info(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "SyncModuleMeta DO is empty! Returning moduleID as " + moduleID);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Exception while getModuleID for " + moduleName, e);
        }
        return moduleID;
    }
    
    public String getModuleName(final long moduleID) {
        final String sourceMethod = "getModuleName";
        String moduleName = null;
        try {
            final DataObject syncModuleMetaDO = this.getSyncModuleMetaDOFromCache();
            if (syncModuleMetaDO != null && !syncModuleMetaDO.isEmpty()) {
                final Column moduleNameColumn = new Column("SyncModuleMeta", "SYNC_MODULE_ID");
                final Criteria moduleCri = new Criteria(moduleNameColumn, (Object)moduleID, 0);
                moduleName = String.valueOf(syncModuleMetaDO.getValue("SyncModuleMeta", "SYNC_MODULE_NAME", moduleCri));
            }
            else {
                SyMLogger.info(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "SyncModuleMeta DO is empty! Returning moduleName as " + moduleName);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Exception while getModuleName for " + moduleID, e);
        }
        return moduleName;
    }
    
    public int getTotalSyncModule() {
        final String sourceMethod = "getTotalSyncModule";
        int totalModuleCount = -1;
        try {
            final DataObject syncModuleMetaDO = this.getSyncModuleMetaDOFromCache();
            if (syncModuleMetaDO != null && !syncModuleMetaDO.isEmpty()) {
                totalModuleCount = syncModuleMetaDO.size("SyncModuleMeta");
                SyMLogger.debug(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Total Enabled SyncModule in DB : " + totalModuleCount);
            }
            else {
                SyMLogger.info(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "SyncModuleMeta DO is empty! Returning total sync module as " + totalModuleCount);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Exception while getTotalSyncModule", e);
        }
        return totalModuleCount;
    }
    
    public int getBatchLimit(final long moduleID) {
        final String sourceMethod = "getBatchLimit";
        int batchLimit = 1000;
        try {
            final DataObject syncModuleMetaDO = this.getSyncModuleMetaDOFromCache();
            if (syncModuleMetaDO != null && !syncModuleMetaDO.isEmpty()) {
                final Column moduleNameColumn = new Column("SyncModuleMeta", "SYNC_MODULE_ID");
                final Criteria moduleCri = new Criteria(moduleNameColumn, (Object)moduleID, 0);
                batchLimit = Integer.parseInt(String.valueOf(syncModuleMetaDO.getValue("SyncModuleMeta", "BATCH_SELECTION_LIMIT", moduleCri)));
                SyMLogger.debug(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "BatchLimit for moduleID :" + moduleID + " in DB: " + batchLimit);
                batchLimit = ((batchLimit > 0) ? batchLimit : 1000);
                SyMLogger.debug(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Returned BatchLimit for moduleID :" + moduleID + " in DB: " + batchLimit);
            }
            else {
                SyMLogger.info(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "SyncModuleMeta DO is empty! Returning BatchLimit as " + batchLimit);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Exception while getBatchLimit for " + moduleID, e);
        }
        return batchLimit;
    }
    
    public int getRecordLimit(final long moduleID) {
        final String sourceMethod = "getRecordLimit";
        int recordLimit = 10000;
        try {
            final DataObject syncModuleMetaDO = this.getSyncModuleMetaDOFromCache();
            if (syncModuleMetaDO != null && !syncModuleMetaDO.isEmpty()) {
                final Column moduleNameColumn = new Column("SyncModuleMeta", "SYNC_MODULE_ID");
                final Criteria moduleCri = new Criteria(moduleNameColumn, (Object)moduleID, 0);
                recordLimit = Integer.parseInt(String.valueOf(syncModuleMetaDO.getValue("SyncModuleMeta", "RECORD_LIMIT", moduleCri)));
                SyMLogger.debug(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "RecordLimit for moduleID :" + moduleID + " in DB: " + recordLimit);
                final int batchLimit = this.getBatchLimit(moduleID);
                if (batchLimit % recordLimit != 0) {
                    int multiples = 10;
                    do {
                        recordLimit = batchLimit * multiples;
                    } while (--multiples > 0 && !this.isRecordLimitInRange(recordLimit));
                }
                recordLimit = (this.isRecordLimitInRange(recordLimit) ? recordLimit : (recordLimit - batchLimit % recordLimit));
                SyMLogger.debug(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Returned RecordLimit for moduleID :" + moduleID + ": " + recordLimit);
            }
            else {
                SyMLogger.info(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "SyncModuleMeta DO is empty! Returning RecordLimit as " + recordLimit);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Exception while getRecordLimit for " + moduleID, e);
        }
        return recordLimit;
    }
    
    private boolean isRecordLimitInRange(final int recordLimit) {
        final SummarySyncParamsDAOUtil summarySyncParamsDAOUtil = new SummarySyncParamsDAOUtil();
        final int minRecordLimit = Integer.parseInt(summarySyncParamsDAOUtil.getSummarySyncParams("MIN_RECORD_LIMIT"));
        final int maxRecordLimit = Integer.parseInt(summarySyncParamsDAOUtil.getSummarySyncParams("MAX_RECORD_LIMIT"));
        return recordLimit >= minRecordLimit && recordLimit <= maxRecordLimit;
    }
    
    public String getSyncSchedulerName(final long moduleID) {
        final String sourceMethod = "getSyncSchedulerName";
        String syncSchedulerName = null;
        try {
            final DataObject syncModuleMetaDO = this.getSyncModuleMetaDOFromCache();
            if (syncModuleMetaDO != null && !syncModuleMetaDO.isEmpty()) {
                final Column moduleNameColumn = new Column("SyncModuleMeta", "SYNC_MODULE_ID");
                final Criteria moduleCri = new Criteria(moduleNameColumn, (Object)moduleID, 0);
                syncSchedulerName = String.valueOf(syncModuleMetaDO.getValue("SyncModuleMeta", "SYNC_SCHEDULER_NAME", moduleCri));
            }
            else {
                SyMLogger.info(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "SyncModuleMeta DO is empty! Returning SYNC_SCHEDULER_NAME as " + syncSchedulerName);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Exception while retrieving sync scheduler name for " + moduleID, e);
        }
        return syncSchedulerName;
    }
    
    public List<Long> getAllModuleIDs() {
        final String sourceMethod = "getAllModuleIDs";
        final List<Long> moduleIDs = new ArrayList<Long>();
        try {
            final DataObject syncModuleMetaDO = this.getSyncModuleMetaDOFromCache();
            if (syncModuleMetaDO != null && !syncModuleMetaDO.isEmpty()) {
                final Iterator syncModuleRows = syncModuleMetaDO.getRows("SyncModuleMeta");
                while (syncModuleRows.hasNext()) {
                    final Row syncModuleRow = syncModuleRows.next();
                    final long moduleID = (long)syncModuleRow.get("SYNC_MODULE_ID");
                    moduleIDs.add(moduleID);
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Exception while getAllModuleIDs ", e);
        }
        return moduleIDs;
    }
    
    public List<Long> getAllParentModuleIDs(final long moduleID) {
        final String sourceMethod = "getAllParentModuleIDs";
        final List<Long> parentModuleIDs = new ArrayList<Long>();
        try {
            final DataObject syncModuleMetaDO = this.getSyncModuleMetaDOFromCache();
            if (syncModuleMetaDO != null && !syncModuleMetaDO.isEmpty()) {
                final Column moduleNameColumn = new Column("SyncModuleMeta", "SYNC_MODULE_ID");
                final Criteria moduleCri = new Criteria(moduleNameColumn, (Object)moduleID, 0);
                final int syncOrder = Integer.parseInt(String.valueOf(syncModuleMetaDO.getValue("SyncModuleMeta", "SYNC_MODULE_ORDER", moduleCri)));
                SyMLogger.debug(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "SYNC_MODULE_ORDER for moduleID :" + moduleID + " in DB: " + syncOrder);
                final Column syncOrderCol = new Column("SyncModuleMeta", "SYNC_MODULE_ORDER");
                final Criteria syncOrderCriteria = new Criteria(syncOrderCol, (Object)syncOrder, 7);
                final DataObject resultanttDO = syncModuleMetaDO.getDataObject("SyncModuleMeta", syncOrderCriteria);
                if (!resultanttDO.isEmpty()) {
                    final Iterator syncModuleRows = resultanttDO.getRows("SyncModuleMeta");
                    while (syncModuleRows.hasNext()) {
                        final Row syncModuleRow = syncModuleRows.next();
                        final long parentModuleID = (long)syncModuleRow.get("SYNC_MODULE_ID");
                        parentModuleIDs.add(parentModuleID);
                    }
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Exception while getAllParentModuleIDs for " + moduleID, e);
        }
        return parentModuleIDs;
    }
    
    public List<Long> getAllChildModuleIDs(final long moduleID) {
        final String sourceMethod = "getAllParentModuleIDs";
        final List<Long> childModuleIDs = new ArrayList<Long>();
        try {
            final DataObject syncModuleMetaDO = this.getSyncModuleMetaDOFromCache();
            if (syncModuleMetaDO != null && !syncModuleMetaDO.isEmpty()) {
                final Column moduleNameColumn = new Column("SyncModuleMeta", "SYNC_MODULE_ID");
                final Criteria moduleCri = new Criteria(moduleNameColumn, (Object)moduleID, 0);
                final int syncOrder = Integer.parseInt(String.valueOf(syncModuleMetaDO.getValue("SyncModuleMeta", "SYNC_MODULE_ORDER", moduleCri)));
                SyMLogger.debug(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "SYNC_MODULE_ORDER for moduleID :" + moduleID + " in DB: " + syncOrder);
                final Column syncOrderCol = new Column("SyncModuleMeta", "SYNC_MODULE_ORDER");
                final Criteria syncOrderCriteria = new Criteria(syncOrderCol, (Object)syncOrder, 5);
                final DataObject resultanttDO = syncModuleMetaDO.getDataObject("SyncModuleMeta", syncOrderCriteria);
                if (!resultanttDO.isEmpty()) {
                    final Iterator syncModuleRows = resultanttDO.getRows("SyncModuleMeta");
                    while (syncModuleRows.hasNext()) {
                        final Row syncModuleRow = syncModuleRows.next();
                        final long childModuleID = (long)syncModuleRow.get("SYNC_MODULE_ID");
                        childModuleIDs.add(childModuleID);
                    }
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Exception while getAllParentModuleIDs for " + moduleID, e);
        }
        return childModuleIDs;
    }
    
    private DataObject getSyncModuleMetaDOFromCache() {
        final String sourceMethod = "getSyncModuleMetaDO";
        DataObject syncMetaDataCache = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("SyncModuleMeta", 2);
        if (syncMetaDataCache == null) {
            SyMLogger.debug(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "SyncModuleMeta Cache becomes null, going to retrieve from DB");
            this.setSyncModuleMetaCacheDO();
            syncMetaDataCache = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("SyncModuleMeta", 2);
        }
        return syncMetaDataCache;
    }
    
    private void setSyncModuleMetaCacheDO() {
        final String sourceMethod = "setSyncModuleMetaCacheDO";
        try {
            final Column enableColumn = new Column("SyncModuleMeta", "IS_ENABLED");
            final Criteria enableCriteria = new Criteria(enableColumn, (Object)true, 0);
            final DataObject syncModuleMeta = DataAccess.get("SyncModuleMeta", enableCriteria);
            if (!syncModuleMeta.isEmpty()) {
                SyMLogger.debug(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "SyncModuleMeta DO to be set in cache: " + syncModuleMeta);
                ApiFactoryProvider.getCacheAccessAPI().putCache("SyncModuleMeta", syncModuleMeta, 2);
            }
            else {
                SyMLogger.info(SyncModuleMetaDAOUtil.logger, SyncModuleMetaDAOUtil.sourceClass, sourceMethod, "SyncModuleMeta DO is empty!");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SyncModuleMetaDAOUtil.logger, sourceMethod, sourceMethod, "Caught exception while updating SyncModuleMetaCache:", ex);
        }
    }
    
    static {
        SyncModuleMetaDAOUtil.logger = Logger.getLogger("SummarySyncLogger");
        SyncModuleMetaDAOUtil.sourceClass = "SyncModuleAuditDAOUtil";
    }
}
