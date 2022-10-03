package com.me.ems.summaryserver.common.sync.utils;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class SummarySyncParamsDAOUtil
{
    private static Logger logger;
    private static String sourceClass;
    
    public boolean isCompressedFilePost() {
        final String sourceMethod = "isCompressedFilePost";
        boolean isCompress = true;
        try {
            final String value = this.getSummarySyncParams("IS_COMPRESSED_FILE_POST");
            if (value != null) {
                isCompress = Boolean.parseBoolean(value);
            }
            else {
                SyMLogger.info(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "value null, Returning isCompressedFilePost as true");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "Exception while retrieving SummarySyncParams is compressed post from DB.", e);
        }
        return isCompress;
    }
    
    public int getSkipThreshold() {
        final String sourceMethod = "getSkipThreshold";
        int skipThreshold = 0;
        try {
            final String value = this.getSummarySyncParams("SKIP_SYNC_THRESHOLD");
            if (value != null) {
                skipThreshold = Integer.parseInt(value);
            }
            else {
                SyMLogger.warning(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "value null, Returning skip sync threshold as -1");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "Exception while retrieving SummarySyncParams skip threshold from DB.", e);
        }
        return skipThreshold;
    }
    
    public int getSyncBeforeMinutes() {
        final String sourceMethod = "getSyncBeforeMinutes";
        int syncBeforeMinutes = 5;
        try {
            final String value = this.getSummarySyncParams("SYNC_BEFORE_MINUTES");
            if (value != null) {
                syncBeforeMinutes = Integer.parseInt(value);
            }
            else {
                SyMLogger.warning(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "value null, Returning sync before minutes as 0");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "Exception while retrieving SummarySyncParams SYNC_BEFORE_MINUTES from DB.", e);
        }
        return syncBeforeMinutes;
    }
    
    public String getSummarySyncParams(final String paramKey) {
        final String sourceMethod = "getSummarySyncParams";
        String paramValue = null;
        try {
            final DataObject summarySyncParamsDO = this.getSummarySyncParamsDOFromCache();
            if (summarySyncParamsDO != null) {
                final Column col = Column.getColumn("SummarySyncParams", "PARAM_NAME");
                final Criteria criteria = new Criteria(col, (Object)paramKey, 0);
                final DataObject resultantDO = summarySyncParamsDO.getDataObject("SummarySyncParams", criteria);
                if (!resultantDO.isEmpty()) {
                    paramValue = (String)resultantDO.getFirstValue("SummarySyncParams", "PARAM_VALUE");
                    SyMLogger.debug(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "Value returned from SummarySyncParams for " + paramKey + " : " + paramValue);
                }
                else {
                    SyMLogger.info(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "SummarySyncParams DO is Empty for " + paramKey);
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "Exception while retrieving Sync Parameter :" + paramKey + " from DB.", ex);
        }
        return paramValue;
    }
    
    public DataObject getSummarySyncParamsDOFromCache() {
        final String sourceMethod = "getSummarySyncParamsDOFromCache";
        DataObject summarySyncParamsCache = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("SummarySyncParams", 2);
        if (summarySyncParamsCache == null) {
            SyMLogger.info(SummarySyncParamsDAOUtil.logger, sourceMethod, sourceMethod, "SummarySyncParams Cache becomes null, going to retrieve from DB");
            this.setSummarySyncParamsCacheDO();
            summarySyncParamsCache = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("SummarySyncParams", 2);
        }
        return summarySyncParamsCache;
    }
    
    public boolean updateSummarySyncParams(final String param, final String value) {
        final String sourceMethod = "updateSummarySyncParams";
        boolean isUpdated = true;
        try {
            final Column col = Column.getColumn("SummarySyncParams", "PARAM_NAME");
            final Criteria criteria = new Criteria(col, (Object)param, 0);
            final DataObject syncParamDO = DataAccess.get("SummarySyncParams", criteria);
            if (syncParamDO.isEmpty()) {
                final Row syncParamRow = new Row("SummarySyncParams");
                syncParamRow.set("PARAM_NAME", (Object)param);
                syncParamRow.set("PARAM_VALUE", (Object)value);
                syncParamDO.addRow(syncParamRow);
                DataAccess.add(syncParamDO);
                SyMLogger.debug(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "Parameter added in DB:- param name: " + param + "  param value: " + value);
            }
            else {
                final Row syncParamRow = syncParamDO.getFirstRow("SummarySyncParams");
                syncParamRow.set("PARAM_VALUE", (Object)value);
                syncParamDO.updateRow(syncParamRow);
                DataAccess.update(syncParamDO);
                SyMLogger.debug(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "Parameter updated in DB:- param name: " + param + "  param value: " + value);
            }
            this.setSummarySyncParamsCacheDO();
        }
        catch (final Exception ex) {
            SyMLogger.error(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "Caught exception while updating Parameter:" + param + " in DB.", ex);
            isUpdated = false;
        }
        return isUpdated;
    }
    
    private void setSummarySyncParamsCacheDO() {
        final String sourceMethod = "setSummarySyncParamsCacheDO";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SummarySyncParams"));
            final Column allColumn = new Column("SummarySyncParams", "*");
            selectQuery.addSelectColumn(allColumn);
            final DataObject summarySyncParamsDO = DataAccess.get(selectQuery);
            if (!summarySyncParamsDO.isEmpty()) {
                SyMLogger.debug(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "SummarySyncParams to be set in cache : " + summarySyncParamsDO);
                ApiFactoryProvider.getCacheAccessAPI().putCache("SummarySyncParams", summarySyncParamsDO, 2);
            }
            else {
                SyMLogger.info(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "SummarySyncParams DO is empty!");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SummarySyncParamsDAOUtil.logger, SummarySyncParamsDAOUtil.sourceClass, sourceMethod, "Caught exception while updating cache for SummarySyncParams :", ex);
        }
    }
    
    static {
        SummarySyncParamsDAOUtil.logger = Logger.getLogger("SummarySyncLogger");
        SummarySyncParamsDAOUtil.sourceClass = "SummarySyncParamsDAOUtil";
    }
}
