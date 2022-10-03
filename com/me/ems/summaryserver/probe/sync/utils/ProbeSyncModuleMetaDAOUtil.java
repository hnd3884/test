package com.me.ems.summaryserver.probe.sync.utils;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;

public class ProbeSyncModuleMetaDAOUtil
{
    private static Logger logger;
    private static String sourceClass;
    
    public String getDeletionTable(final long moduleID) throws DataAccessException {
        final String sourceMethod = "getDeletionTable";
        String deletionTable = null;
        final DataObject probeSyncModuleMetaDO = this.getProbeSyncModuleMetaDOFromCache();
        if (probeSyncModuleMetaDO != null && !probeSyncModuleMetaDO.isEmpty()) {
            final Column moduleIDColumn = new Column("ProbeSyncModuleMeta", "SYNC_MODULE_ID");
            final Criteria moduleCri = new Criteria(moduleIDColumn, (Object)moduleID, 0);
            deletionTable = (String)probeSyncModuleMetaDO.getValue("ProbeSyncModuleMeta", "DELETION_AUDIT_TABLE", moduleCri);
            SyMLogger.debug(ProbeSyncModuleMetaDAOUtil.logger, ProbeSyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Returned Deletion table as : " + deletionTable + "for moduleID :" + moduleID);
        }
        else {
            SyMLogger.info(ProbeSyncModuleMetaDAOUtil.logger, ProbeSyncModuleMetaDAOUtil.sourceClass, sourceMethod, "ProbeSyncModuleMeta DO is Empty! Returning Deletion table as " + deletionTable);
        }
        return deletionTable;
    }
    
    public String getModuleSyncUrlPath(final long moduleID) throws DataAccessException {
        final String sourceMethod = "getModuleSyncUrlPath";
        String urlPath = null;
        final DataObject probeSyncModuleMetaDO = this.getProbeSyncModuleMetaDOFromCache();
        if (probeSyncModuleMetaDO != null && !probeSyncModuleMetaDO.isEmpty()) {
            final Column moduleIDColumn = new Column("ProbeSyncModuleMeta", "SYNC_MODULE_ID");
            final Criteria moduleCri = new Criteria(moduleIDColumn, (Object)moduleID, 0);
            urlPath = (String)probeSyncModuleMetaDO.getValue("ProbeSyncModuleMeta", "SYNC_URL_PATH", moduleCri);
            SyMLogger.debug(ProbeSyncModuleMetaDAOUtil.logger, ProbeSyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Returned url path as :" + urlPath + "for moduleID :" + moduleID);
        }
        else {
            SyMLogger.info(ProbeSyncModuleMetaDAOUtil.logger, ProbeSyncModuleMetaDAOUtil.sourceClass, sourceMethod, "ProbeSyncModuleMeta DO is Empty! Returning url path as " + urlPath);
        }
        return urlPath;
    }
    
    private DataObject getProbeSyncModuleMetaDOFromCache() {
        final String sourceMethod = "getProbeSyncModuleMetaDOFromCache";
        DataObject syncMetaDataCache = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("ProbeSyncModuleMeta", 2);
        if (syncMetaDataCache == null) {
            SyMLogger.info(ProbeSyncModuleMetaDAOUtil.logger, sourceMethod, sourceMethod, "setProbeSyncModuleMetaCache Cache becomes null, going to retrieve from DB");
            this.setProbeSyncModuleMetaCache();
            syncMetaDataCache = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("ProbeSyncModuleMeta", 2);
        }
        return syncMetaDataCache;
    }
    
    private void setProbeSyncModuleMetaCache() {
        final String sourceMethod = "setProbeSyncModuleMetaCache";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProbeSyncModuleMeta"));
            final Column allColumn = new Column("ProbeSyncModuleMeta", "*");
            selectQuery.addSelectColumn(allColumn);
            final Join moduleJoin = new Join("ProbeSyncModuleMeta", "SyncModuleMeta", new String[] { "SYNC_MODULE_ID" }, new String[] { "SYNC_MODULE_ID" }, 2);
            selectQuery.addJoin(moduleJoin);
            final Column enableCol = new Column("SyncModuleMeta", "IS_ENABLED");
            final Criteria enableCriteria = new Criteria(enableCol, (Object)true, 0);
            selectQuery.setCriteria(enableCriteria);
            final DataObject syncMetaDataDO = DataAccess.get(selectQuery);
            if (!syncMetaDataDO.isEmpty()) {
                SyMLogger.debug(ProbeSyncModuleMetaDAOUtil.logger, ProbeSyncModuleMetaDAOUtil.sourceClass, sourceMethod, "ProbeSyncModuleMeta to be set in cache : " + syncMetaDataDO);
                ApiFactoryProvider.getCacheAccessAPI().putCache("ProbeSyncModuleMeta", syncMetaDataDO, 2);
            }
            else {
                SyMLogger.info(ProbeSyncModuleMetaDAOUtil.logger, ProbeSyncModuleMetaDAOUtil.sourceClass, sourceMethod, "ProbeSyncModuleMeta DO is empty!");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(ProbeSyncModuleMetaDAOUtil.logger, ProbeSyncModuleMetaDAOUtil.sourceClass, sourceMethod, "Caught exception while setProbeSyncModuleMetaCache :", ex);
        }
    }
    
    static {
        ProbeSyncModuleMetaDAOUtil.logger = Logger.getLogger("ProbeSyncLogger");
        ProbeSyncModuleMetaDAOUtil.sourceClass = "ProbeSyncModuleMetaDAOUtil";
    }
}
