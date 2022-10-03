package com.me.ems.summaryserver.common.sync.utils;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SyncMetaDataDAOUtil
{
    private static Logger logger;
    private static String sourceClass;
    
    public List<String> getAllSyncTableNames() throws Exception {
        final String sourceMethod = "getAllSyncTableNames";
        final List<String> syncTableNames = new ArrayList<String>();
        final DataObject syncMetaDataDO = this.getSyncMetaDataDO();
        if (syncMetaDataDO != null && !syncMetaDataDO.isEmpty()) {
            final Iterator syncMetaDataRows = syncMetaDataDO.getRows("SyncMetaData");
            while (syncMetaDataRows.hasNext()) {
                final Row syncMetaRow = syncMetaDataRows.next();
                final String tableName = (String)syncMetaRow.get("PROBE_TABLE_NAME");
                syncTableNames.add(tableName);
            }
        }
        else {
            SyMLogger.info(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "SyncMetaData DO is empty!");
        }
        return syncTableNames;
    }
    
    public List<String> getSyncTableNames(final long moduleID) throws Exception {
        final String sourceMethod = "getSyncTableNames";
        final List<String> syncTableNames = new ArrayList<String>();
        final DataObject syncMetaDataDO = this.getSyncMetaDataDO();
        if (syncMetaDataDO != null && !syncMetaDataDO.isEmpty()) {
            final Column moduleIDColumn = new Column("SyncMetaData", "MODULE_ID");
            final Criteria moduleCri = new Criteria(moduleIDColumn, (Object)moduleID, 0);
            final Iterator syncMetaDataRows = syncMetaDataDO.getRows("SyncMetaData", moduleCri);
            while (syncMetaDataRows.hasNext()) {
                final Row syncMetaRow = syncMetaDataRows.next();
                final String tableName = (String)syncMetaRow.get("PROBE_TABLE_NAME");
                syncTableNames.add(tableName);
            }
        }
        else {
            SyMLogger.info(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "SyncMetaData DO is empty!");
        }
        return syncTableNames;
    }
    
    public DataObject getSyncMetaDataDO() {
        final String sourceMethod = "getSyncMetaDataDOFromCache";
        DataObject syncMetaDataCache = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("SyncMetaData", 2);
        if (syncMetaDataCache == null) {
            SyMLogger.info(SyncMetaDataDAOUtil.logger, sourceMethod, sourceMethod, "SyncMetaData Cache becomes null, going to retrieve from DB");
            this.setSyncMetaDataCacheDO();
            syncMetaDataCache = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("SyncMetaData", 2);
        }
        return syncMetaDataCache;
    }
    
    public DataObject getSyncMetaDataDO(final long moduleID) {
        final String sourceMethod = "getSyncMetaDataDO";
        final DataObject syncMetaDataCache = this.getSyncMetaDataDO();
        DataObject resultantDO = null;
        final Criteria moduleCri = new Criteria(Column.getColumn("SyncMetaData", "MODULE_ID"), (Object)moduleID, 0);
        try {
            resultantDO = syncMetaDataCache.getDataObject("SyncMetaData", moduleCri);
        }
        catch (final DataAccessException e) {
            SyMLogger.error(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "Caught exception while getSyncMetaDataDO :", (Throwable)e);
        }
        return resultantDO;
    }
    
    public Object getSyncMetaDataValue(final long moduleID, final String criteriaColumnName, final String criteriaValue, final String returnColumnName) throws Exception {
        final String sourceMethod = "getSyncMetaDataValue";
        Object value = null;
        final DataObject syncMetaDataDO = this.getSyncMetaDataDO();
        if (syncMetaDataDO != null && !syncMetaDataDO.isEmpty()) {
            final Column criteriaColumn = new Column("SyncMetaData", criteriaColumnName);
            Criteria criteria = new Criteria(criteriaColumn, (Object)criteriaValue, 0);
            final Column moduleIdCol = new Column("SyncMetaData", "MODULE_ID");
            final Criteria moduleCriteria = new Criteria(moduleIdCol, (Object)moduleID, 0);
            criteria = criteria.and(moduleCriteria);
            value = syncMetaDataDO.getValue("SyncMetaData", returnColumnName, criteria);
            SyMLogger.debug(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "SyncMetaData value : " + value + " for criteria : " + criteria);
        }
        else {
            SyMLogger.info(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "SyncMetaData DO is empty! Returning value as " + value);
        }
        return value;
    }
    
    public Row getSyncMetaDataRow(final Long moduleID, final String criteriaColumnName, final String criteriaValue) {
        final String sourceMethod = "getSyncMetaDataValue";
        Row syncMetaDataRow = null;
        try {
            final DataObject syncMetaDataDO = this.getSyncMetaDataDO();
            if (syncMetaDataDO != null && !syncMetaDataDO.isEmpty()) {
                final Column criteriaColumn = new Column("SyncMetaData", criteriaColumnName);
                Criteria criteria = new Criteria(criteriaColumn, (Object)criteriaValue, 0);
                final Column moduleIdCol = new Column("SyncMetaData", "MODULE_ID");
                final Criteria moduleCriteria = new Criteria(moduleIdCol, (Object)moduleID, 0);
                criteria = criteria.and(moduleCriteria);
                syncMetaDataRow = syncMetaDataDO.getRow("SyncMetaData", criteria);
                SyMLogger.debug(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "SyncMetaData Row : " + syncMetaDataRow + " for criteria : " + criteria);
            }
            else {
                SyMLogger.info(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "SyncMetaData DO is empty! Returning row as " + syncMetaDataRow);
            }
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "Caught exception while getSyncMetaDataRow :", (Throwable)ex);
        }
        return syncMetaDataRow;
    }
    
    private void setSyncMetaDataCacheDO() {
        final String sourceMethod = "setSyncMetaDataCacheDO";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SyncMetaData"));
            final Column allColumn = new Column("SyncMetaData", "*");
            selectQuery.addSelectColumn(allColumn);
            final Join moduleJoin = new Join("SyncMetaData", "SyncModuleMeta", new String[] { "MODULE_ID" }, new String[] { "SYNC_MODULE_ID" }, 2);
            selectQuery.addJoin(moduleJoin);
            final Column enableCol = new Column("SyncModuleMeta", "IS_ENABLED");
            final Criteria enableCriteria = new Criteria(enableCol, (Object)true, 0);
            selectQuery.setCriteria(enableCriteria);
            final DataObject syncMetaDataDO = DataAccess.get(selectQuery);
            if (!syncMetaDataDO.isEmpty()) {
                SyMLogger.debug(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "SyncMetaData to be set in cache : " + syncMetaDataDO);
                ApiFactoryProvider.getCacheAccessAPI().putCache("SyncMetaData", syncMetaDataDO, 2);
            }
            else {
                SyMLogger.info(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "SyncMetaData DO is empty!");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "Caught exception while setSyncMetaDataCacheDO :", ex);
        }
    }
    
    public boolean isConflictTable(final String tableName) throws Exception {
        final String sourceMethod = "isConflictTable";
        boolean isConflictData = false;
        final DataObject syncMetaDataDO = this.getSyncMetaDataDO();
        if (syncMetaDataDO != null) {
            final Column tableCol = Column.getColumn("SyncMetaData", "SS_TABLE_NAME");
            final Criteria criteria = new Criteria(tableCol, (Object)tableName, 0, false);
            final DataObject resultantDO = syncMetaDataDO.getDataObject("SyncMetaData", criteria);
            if (resultantDO.isEmpty()) {
                SyMLogger.warning(SyncMetaDataDAOUtil.logger, SyncMetaDataDAOUtil.sourceClass, sourceMethod, "SyncMetaData DO is empty! for " + tableName);
                throw new Exception("SyncMetaData DO empty for " + tableName);
            }
            isConflictData = (boolean)resultantDO.getFirstValue("SyncMetaData", "IS_CONFLICT_DATA");
        }
        return isConflictData;
    }
    
    static {
        SyncMetaDataDAOUtil.logger = Logger.getLogger("SummarySyncLogger");
        SyncMetaDataDAOUtil.sourceClass = "SyncMetaDataDAOUtil";
    }
}
