package com.me.ems.summaryserver.common.sync.utils;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class ConflictResolutionMetaDataDAOUtil
{
    private static Logger logger;
    private static String sourceClass;
    
    public DataObject getConflictResolutionMetaDataDOFromCache() {
        final String sourceMethod = "getConflictResolutionMetaDataDOFromCache";
        DataObject conflictResolutionMetaDO = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("ConflictResolutionMetaData", 2);
        if (conflictResolutionMetaDO == null) {
            SyMLogger.info(ConflictResolutionMetaDataDAOUtil.logger, sourceMethod, sourceMethod, "ConflictResolutionMetaData Cache becomes null, going to retrieve from DB");
            this.setConflictResolutionMetaDataDO();
            conflictResolutionMetaDO = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("ConflictResolutionMetaData", 2);
        }
        return conflictResolutionMetaDO;
    }
    
    private void setConflictResolutionMetaDataDO() {
        final String sourceMethod = "setSyncMetaDataCacheDO";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ConflictResolutionMetaData"));
            final Column allColumn = new Column("ConflictResolutionMetaData", "*");
            selectQuery.addSelectColumn(allColumn);
            final Join moduleJoin = new Join("ConflictResolutionMetaData", "SyncModuleMeta", new String[] { "MODULE_ID" }, new String[] { "SYNC_MODULE_ID" }, 2);
            selectQuery.addJoin(moduleJoin);
            final Column enableCol = new Column("SyncModuleMeta", "IS_ENABLED");
            final Criteria enableCriteria = new Criteria(enableCol, (Object)true, 0);
            selectQuery.setCriteria(enableCriteria);
            final DataObject conflictResolutionMetaDO = DataAccess.get(selectQuery);
            if (!conflictResolutionMetaDO.isEmpty()) {
                SyMLogger.debug(ConflictResolutionMetaDataDAOUtil.logger, ConflictResolutionMetaDataDAOUtil.sourceClass, sourceMethod, "ConflictResolutionMetaData to be set in cache : " + conflictResolutionMetaDO);
                ApiFactoryProvider.getCacheAccessAPI().putCache("ConflictResolutionMetaData", conflictResolutionMetaDO, 2);
            }
            else {
                SyMLogger.info(ConflictResolutionMetaDataDAOUtil.logger, ConflictResolutionMetaDataDAOUtil.sourceClass, sourceMethod, "ConflictResolutionMetaData DO is empty!");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(ConflictResolutionMetaDataDAOUtil.logger, ConflictResolutionMetaDataDAOUtil.sourceClass, sourceMethod, "Caught exception while setConflictResolutionMetaDataDO :", ex);
        }
    }
    
    public DataObject getConflictResolutionMetaData(final long moduleID, final String tableName) {
        final String sourceMethod = "getConflictResolutionMetaData";
        final DataObject conflictResolutionMetaCache = this.getConflictResolutionMetaDataDOFromCache();
        DataObject resultantDO = null;
        try {
            if (conflictResolutionMetaCache != null) {
                Criteria criteria = new Criteria(new Column("ConflictResolutionMetaData", "SS_TABLE_NAME"), (Object)tableName, 0, false);
                criteria = criteria.and(new Criteria(new Column("ConflictResolutionMetaData", "MODULE_ID"), (Object)moduleID, 0));
                resultantDO = conflictResolutionMetaCache.getDataObject("ConflictResolutionMetaData", criteria);
                if (resultantDO.isEmpty()) {
                    SyMLogger.info(ConflictResolutionMetaDataDAOUtil.logger, ConflictResolutionMetaDataDAOUtil.sourceClass, sourceMethod, "ConflictResolutionMetaData DO is empty! for table : " + tableName + "and moduleID :" + moduleID);
                }
            }
        }
        catch (final DataAccessException ex) {
            SyMLogger.error(ConflictResolutionMetaDataDAOUtil.logger, ConflictResolutionMetaDataDAOUtil.sourceClass, sourceMethod, "Caught exception while getConflictResolutionMetaData :", (Throwable)ex);
        }
        return resultantDO;
    }
    
    static {
        ConflictResolutionMetaDataDAOUtil.logger = Logger.getLogger("SummarySyncLogger");
        ConflictResolutionMetaDataDAOUtil.sourceClass = "ConflictResolutionMetaDataDAOUtil";
    }
}
