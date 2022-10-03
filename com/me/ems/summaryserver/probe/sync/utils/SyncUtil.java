package com.me.ems.summaryserver.probe.sync.utils;

import java.util.HashSet;
import com.me.devicemanagement.framework.server.scheduler.SchedulerConstants;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import java.util.HashMap;
import java.util.LinkedList;
import com.me.ems.summaryserver.common.sync.utils.SyncFileAuditDAOUtil;
import com.me.devicemanagement.framework.server.deletionfw.DeletionFramework;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.Range;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.List;
import com.me.ems.summaryserver.probe.sync.SyncData;
import java.util.Collection;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleAuditDAOUtil;
import com.me.ems.summaryserver.common.sync.SyncConstants;
import com.me.ems.summaryserver.probe.sync.factory.SyncAPI;
import java.util.Map;
import com.me.ems.summaryserver.common.probeadministration.ProbeDetailsAPI;
import com.me.ems.summaryserver.common.sync.utils.SummarySyncParamsDAOUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONArray;
import com.me.devicemanagement.framework.utils.JsonUtils;
import java.io.File;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.message.MickeyMessageListener;
import com.adventnet.persistence.Row;
import com.me.ems.summaryserver.common.sync.utils.SyncMetaDataDAOUtil;
import java.util.logging.Level;
import java.util.Set;
import org.json.JSONObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class SyncUtil
{
    private static final String sourceClass = "SyncUtil";
    protected static Logger logger;
    protected static Logger dataPostLogger;
    protected static Logger dataCollectorLogger;
    protected static Logger syncStatusLogger;
    private static SyncUtil syncUtil;
    private final ConcurrentHashMap<String, String> longRunningSyncMap;
    private final ConcurrentHashMap<String, String> probeSyncFailureMap;
    private final ConcurrentHashMap<String, String> longSummaryProcessingMap;
    private final ConcurrentHashMap<String, String> longWaitingStatusMap;
    private final ConcurrentHashMap<String, String> parentSyncFailureMap;
    private static JSONObject syncSettingsJSON;
    private static final Set<String> deleteInsertTableList;
    private int ssDownCount;
    
    public SyncUtil() {
        this.longRunningSyncMap = new ConcurrentHashMap<String, String>();
        this.probeSyncFailureMap = new ConcurrentHashMap<String, String>();
        this.longSummaryProcessingMap = new ConcurrentHashMap<String, String>();
        this.longWaitingStatusMap = new ConcurrentHashMap<String, String>();
        this.parentSyncFailureMap = new ConcurrentHashMap<String, String>();
        this.ssDownCount = 0;
    }
    
    public static SyncUtil getInstance() {
        if (SyncUtil.syncUtil == null) {
            SyncUtil.syncUtil = new SyncUtil();
        }
        return SyncUtil.syncUtil;
    }
    
    public static void setSyncObjects() {
        final String sourceMethod = "setSyncObjects";
        SyncUtil.logger.log(Level.INFO, "Going to set probe tables...");
        try {
            final SyncMetaDataDAOUtil syncMetaDataDAOUtil = new SyncMetaDataDAOUtil();
            final DataObject syncMetaDataDO = syncMetaDataDAOUtil.getSyncMetaDataDO();
            if (syncMetaDataDO != null && !syncMetaDataDO.isEmpty()) {
                final Iterator syncMetaDataRows = syncMetaDataDO.getRows("SyncMetaData");
                while (syncMetaDataRows.hasNext()) {
                    final Row syncMetaDataRow = syncMetaDataRows.next();
                    final String tableName = String.valueOf(syncMetaDataRow.get("PROBE_TABLE_NAME")).toLowerCase();
                    final String moduleID = String.valueOf(syncMetaDataRow.get("MODULE_ID"));
                    final String pkColumnName = String.valueOf(syncMetaDataRow.get("PROBE_TABLE_PK_COLUMN"));
                    final String ssTable = String.valueOf(syncMetaDataRow.get("SS_TABLE_NAME"));
                    final String queryID = String.valueOf(syncMetaDataRow.get("QUERY_ID"));
                    MickeyMessageListener.checkTableSet.add(tableName);
                    final String lastUpdatedTimeFromRedis = ApiFactoryProvider.getRedisHashMap().get(tableName, 2);
                    final String pkColumnFromRedis = ApiFactoryProvider.getRedisHashMap().get("PK_COLUMN", tableName, 2);
                    final String moduleIDFromRedis = ApiFactoryProvider.getRedisHashMap().get("MODULE_ID", tableName, 2);
                    if (lastUpdatedTimeFromRedis != null) {
                        SyncUtil.logger.log(Level.INFO, "Entry already available in Redis. Table: {0},  Last Updated Time: {1}", new Object[] { tableName, lastUpdatedTimeFromRedis });
                    }
                    else {
                        ApiFactoryProvider.getRedisHashMap().put(tableName, "0", 2);
                        SyncUtil.logger.log(Level.INFO, "Entry newly added in Redis for Last Updated Time. Table: {0}" + tableName);
                    }
                    if (pkColumnFromRedis != null) {
                        SyncUtil.logger.log(Level.INFO, "Entry already available in Redis. Table: {0},  PK_COLUMN: {1}", new Object[] { tableName, pkColumnFromRedis });
                    }
                    else {
                        ApiFactoryProvider.getRedisHashMap().put("PK_COLUMN", tableName, pkColumnName, 2);
                        SyncUtil.logger.log(Level.INFO, "Entry newly added in Redis for PK_COLUMN. Table: {0}, PK_COLUMN: {1}", new Object[] { tableName, pkColumnName });
                    }
                    if (moduleIDFromRedis != null) {
                        SyncUtil.logger.log(Level.INFO, "Entry already available in Redis. Table: {0},  MODULE_ID: {1}", new Object[] { tableName, moduleIDFromRedis });
                    }
                    else {
                        ApiFactoryProvider.getRedisHashMap().put("MODULE_ID", tableName, moduleID, 2);
                        SyncUtil.logger.log(Level.INFO, "Entry newly added in Redis for MODULE_ID. Table: {0},  MODULE_ID: {1}", new Object[] { tableName, moduleID });
                    }
                    try {
                        final String queryId = String.valueOf(DBUtil.getValueFromDB("CustomViewConfiguration", "CVNAME", queryID, "QUERYID"));
                        final String ssFileNameFromRedis = ApiFactoryProvider.getRedisHashMap().get("SQL_ID" + queryId, 2);
                        if (ssFileNameFromRedis != null) {
                            SyncUtil.logger.log(Level.INFO, "Entry already available in Redis. SS Table: {0}, SQL ID: {1}, MODULE_ID: {2},  ssFileNameFromRedis : {3}", new Object[] { tableName, queryId, moduleID, ssFileNameFromRedis });
                        }
                        else {
                            ApiFactoryProvider.getRedisHashMap().put("SQL_ID" + queryId, ssTable, 2);
                            SyncUtil.logger.log(Level.INFO, "Entry newly added in Redis for  SS Table: {0}, SQL ID: {1}, MODULE_ID: {2}", new Object[] { tableName, queryId, moduleID });
                        }
                    }
                    catch (final Exception e) {
                        SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while updating SQL ID in redis", e);
                    }
                }
            }
            loadSyncSettingsJSONFile();
            populateDeleteInsertTables();
            SyncUtil.logger.log(Level.INFO, "Tables Set for Checking: {0}", MickeyMessageListener.checkTableSet.toString());
        }
        catch (final Exception e2) {
            SyncUtil.logger.log(Level.SEVERE, "Exception in setting tables Hash Set ", e2);
        }
    }
    
    private static void loadSyncSettingsJSONFile() {
        final String sourceMethod = "loadSyncSettingsJSONFile";
        try {
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String filePath = serverHome + File.separator + "conf" + File.separator + "DMSummaryServer" + File.separator + "syncSettings.json";
            if (new File(filePath).exists()) {
                final File syncSettingsFile = new File(filePath);
                SyncUtil.syncSettingsJSON = JsonUtils.loadJsonFile(syncSettingsFile);
            }
            else {
                SyMLogger.debug(SyncUtil.logger, "SyncUtil", sourceMethod, "{0} file does not exists", filePath);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while reading sync settings json", e);
        }
    }
    
    private static void populateDeleteInsertTables() {
        final String sourceMethod = "populateDeleteInsertTables";
        try {
            if (SyncUtil.syncSettingsJSON != null) {
                final JSONArray deleteInsertTables = SyncUtil.syncSettingsJSON.getJSONArray("deleteInsertTables");
                for (int index = 0; index < deleteInsertTables.length(); ++index) {
                    SyncUtil.deleteInsertTableList.add(deleteInsertTables.getString(index));
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while loading syncSettingsJSON", e);
        }
    }
    
    public String getPKValue(final String xmlStr, final String tableName, final String tablePKColumn) {
        StringBuilder deletionValue = null;
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document document = db.parse(new InputSource(new StringReader(xmlStr)));
            int numberOfPKColumns = 1;
            String[] pkCols = new String[5];
            if (tablePKColumn.contains("*")) {
                pkCols = tablePKColumn.split("\\*");
                numberOfPKColumns = pkCols.length;
            }
            else {
                pkCols[0] = tablePKColumn;
            }
            final NodeList nList = document.getElementsByTagName(tableName + "_PK");
            for (int index = 0, len = nList.getLength(); index < len; ++index) {
                final Element elm = (Element)nList.item(index);
                for (int subIndex = 0; subIndex < numberOfPKColumns; ++subIndex) {
                    final String currentId = elm.getAttribute(pkCols[subIndex]);
                    SyncUtil.logger.log(Level.FINE, "Current Id Deleted for column name: " + pkCols[subIndex] + " " + currentId);
                    if (subIndex == 0) {
                        deletionValue = new StringBuilder();
                    }
                    if (subIndex + 1 == numberOfPKColumns) {
                        deletionValue.append(currentId);
                        SyncUtil.logger.log(Level.FINE, "Deletion value: " + (Object)deletionValue);
                    }
                    else {
                        deletionValue.append(currentId).append("*");
                    }
                }
            }
        }
        catch (final Exception e) {
            SyncUtil.logger.log(Level.SEVERE, "Exception Details: ", e);
        }
        return (deletionValue != null) ? deletionValue.toString() : null;
    }
    
    public void addDeletionAudit(final String probeTableName, final String probeTablePKColumn, String probeTablePKValue, final String lastUpdatedTime, final Long moduleID) {
        try {
            final ProbeSyncModuleMetaDAOUtil probeSyncModuleMetaDAOUtil = new ProbeSyncModuleMetaDAOUtil();
            final String deletionAuditTable = probeSyncModuleMetaDAOUtil.getDeletionTable(moduleID);
            if (deletionAuditTable == null) {
                SyncUtil.logger.log(Level.WARNING, "DeletionAuditTable for moduleID : {0} is null, hence returning", moduleID);
                return;
            }
            final boolean isValidDeleteValue = !this.isValueExistInOtherJoinTables(probeTableName, probeTablePKValue);
            final SyncMetaDataDAOUtil syncMetaDataDAOUtil = new SyncMetaDataDAOUtil();
            final Row syncMetaDataRow = syncMetaDataDAOUtil.getSyncMetaDataRow(moduleID, "PROBE_TABLE_NAME", probeTableName);
            if (syncMetaDataRow != null) {
                final String ssTablePKCol = (String)syncMetaDataRow.get("SS_TABLE_PK_COLUMN");
                if (!probeTablePKColumn.equals(ssTablePKCol) && ("PROBE_ID*" + probeTablePKColumn).equals(ssTablePKCol)) {
                    final long probeID = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID();
                    probeTablePKValue = probeID + "*" + probeTablePKValue;
                }
            }
            if (isValidDeleteValue) {
                final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
                final Row row = new Row(deletionAuditTable);
                row.set("PROBE_TABLE_NAME", (Object)probeTableName);
                row.set("PROBE_TABLE_PK_VALUE", (Object)probeTablePKValue);
                row.set("LAST_UPDATED_TIME", (Object)lastUpdatedTime);
                dataObject.addRow(row);
                SyMUtil.getPersistence().add(dataObject);
                SyncUtil.logger.log(Level.FINE, "Added the deletion audit: {0}", probeTableName);
            }
            else {
                SyncUtil.logger.log(Level.INFO, "Skipped the deletion audit: {0} as pk value {1} exist in other join table", new Object[] { probeTableName, probeTablePKValue });
            }
        }
        catch (final Exception e) {
            SyncUtil.logger.log(Level.SEVERE, "Exception while adding the deletion audit for table: {0}, {1}", new Object[] { probeTableName, e });
        }
    }
    
    private boolean isValueExistInOtherJoinTables(final String probeTableName, final String pkColumnValue) {
        boolean isValueExist = Boolean.FALSE;
        try {
            final DataObject syncMetaDataDO = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("SyncMetaData", 2);
            if (syncMetaDataDO != null) {
                final Criteria probeTableNameCri = new Criteria(new Column("SyncMetaData", "PROBE_TABLE_NAME"), (Object)probeTableName, 0, false);
                final Row ssTableNameRow = syncMetaDataDO.getRow("SyncMetaData", probeTableNameCri);
                final String ssTableName = (String)ssTableNameRow.get("SS_TABLE_NAME");
                final Criteria ssTableNameCri = new Criteria(new Column("SyncMetaData", "SS_TABLE_NAME"), (Object)ssTableName, 0, false);
                final Iterator probeTableList = syncMetaDataDO.getRows("SyncMetaData", ssTableNameCri);
                while (probeTableList.hasNext()) {
                    final Row joinTableRow = probeTableList.next();
                    final String joinTableName = (String)joinTableRow.get("PROBE_TABLE_NAME");
                    if (!joinTableName.equalsIgnoreCase(probeTableName)) {
                        final String joinTableTimeField = (String)joinTableRow.get("TIME_FIELD");
                        final String joinTablePKCol = (String)joinTableRow.get("PROBE_TABLE_PK_COLUMN");
                        Criteria valueCriteria = null;
                        if (joinTablePKCol.contains("*")) {
                            final String[] joinTablePKColNames = joinTablePKCol.split("\\*");
                            final String[] pkColumnValues = pkColumnValue.split("\\*");
                            for (int index = 0, valueIndex = 0; index < joinTablePKColNames.length; ++index, ++valueIndex) {
                                if (joinTablePKColNames.length != pkColumnValues.length && joinTablePKColNames[index].equalsIgnoreCase("PROBE_ID")) {
                                    --valueIndex;
                                }
                                else {
                                    final Criteria compositePKCri = new Criteria(new Column(joinTableName, joinTablePKColNames[index]), (Object)pkColumnValues[valueIndex], 0, false);
                                    valueCriteria = ((valueCriteria == null) ? compositePKCri : valueCriteria.and(compositePKCri));
                                }
                            }
                        }
                        else {
                            valueCriteria = new Criteria(new Column(joinTableName, joinTablePKCol), (Object)pkColumnValue, 0, false);
                        }
                        final DataObject joinTableValuesDO = DataAccess.get(joinTableName, valueCriteria);
                        if (joinTableValuesDO.isEmpty()) {
                            continue;
                        }
                        isValueExist = Boolean.TRUE;
                        final Iterator iterator = joinTableValuesDO.getRows(joinTableName);
                        while (iterator.hasNext()) {
                            final Row row = iterator.next();
                            row.set(joinTableTimeField, (Object)System.currentTimeMillis());
                            joinTableValuesDO.updateRow(row);
                        }
                        SyMUtil.getPersistence().update(joinTableValuesDO);
                    }
                }
            }
        }
        catch (final Exception e) {
            SyncUtil.logger.log(Level.INFO, "Exception while isJoinTableExistForSSSync method", e);
        }
        return isValueExist;
    }
    
    public boolean isSummaryServerLiveForSync() {
        final String sourceMethod = "isSummaryServerLiveForSync";
        boolean status = false;
        try {
            final ProbeDetailsAPI probeDetailsAPI = ProbeMgmtFactoryProvider.getProbeDetailsAPI();
            int summaryServerStatus = probeDetailsAPI.getSummaryServerLiveStatus();
            if (summaryServerStatus == 1) {
                this.ssDownCount = 0;
                status = true;
            }
            else {
                SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Summary server is {0}! Going to check on demand", summaryServerStatus);
                probeDetailsAPI.checkAndUpdateSummaryServerLiveStatus();
                summaryServerStatus = probeDetailsAPI.getSummaryServerLiveStatus();
                status = (summaryServerStatus == 1);
            }
            SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Is Summary server live after on demand check : {0}", status);
            if (!status) {
                ++this.ssDownCount;
                final SummarySyncParamsDAOUtil syncParamsDAOUtil = new SummarySyncParamsDAOUtil();
                final int skipThreshold = syncParamsDAOUtil.getSkipThreshold();
                if (this.ssDownCount <= skipThreshold) {
                    SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Summary Server down count : {0} Skipping current sync!", this.ssDownCount);
                }
                else {
                    SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Threshold reached - {0}", skipThreshold);
                    SyMLogger.info(SyncUtil.syncStatusLogger, "SyncUtil", sourceMethod, "Going to disable sync scheduler!! Sync will be enabled once Summary Server live {0}", "");
                    this.disableSyncScheduler("DISABLED_ON_SS_DOWN");
                }
            }
            else {
                this.ssDownCount = 0;
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while checking summary server live status", e);
        }
        return status;
    }
    
    public boolean IsPreviousSyncCompleted(final long moduleID, final long syncTime) {
        final String sourceMethod = "IsPreviousSyncCompleted";
        boolean status = true;
        if (syncTime != 0L) {
            SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "currSyncLockedTime : {0} != {1}", new Object[] { syncTime, 0L });
            status = this.isThresholdExceedsForLongRunningSync(moduleID, syncTime);
        }
        SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Is Previous Sync completed ? {0}", status);
        return status;
    }
    
    public long getCurrSyncLockTime(final Map<String, Object> syncParams) {
        long currSyncLockedTime = System.currentTimeMillis();
        try {
            final SummarySyncParamsDAOUtil summarySyncParamsDAOUtil = new SummarySyncParamsDAOUtil();
            final int syncBeforeTimeMin = summarySyncParamsDAOUtil.getSyncBeforeMinutes();
            final long syncBeforeTimeMillis = syncBeforeTimeMin * 60L * 1000L;
            currSyncLockedTime -= syncBeforeTimeMillis;
            final long lastSuccessfulSyncTime = syncParams.get("LAST_SUCCESSFUL_SYNC_TIME");
            final long splitCurrSyncLockedTime = this.getCurrSyncLockTimeWithSplitSyncLimit(currSyncLockedTime, lastSuccessfulSyncTime);
            if (splitCurrSyncLockedTime != -1L && splitCurrSyncLockedTime > lastSuccessfulSyncTime) {
                currSyncLockedTime = splitCurrSyncLockedTime;
                syncParams.put("IS_SPLIT_SYNC", 1);
            }
            SyncUtil.logger.log(Level.FINE, "CurrSyncLockTime value returned as " + currSyncLockedTime);
        }
        catch (final Exception e) {
            SyncUtil.logger.log(Level.SEVERE, "Exception while getting SYNC_BEFORE_MINUTES", e);
        }
        return currSyncLockedTime;
    }
    
    private long getCurrSyncLockTimeWithSplitSyncLimit(final long currSyncLockedTime, long lastSuccessfulSyncTime) {
        final String sourceMethod = "getCurrSyncLockTimeWithSplitSyncLimit";
        long newCurrSyncLockedTime = -1L;
        if (lastSuccessfulSyncTime == 0L) {
            final String installationTime = SyMUtil.getInstallationProperty("it");
            if (installationTime == null || installationTime.equalsIgnoreCase("")) {
                SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "lastSuccessfulSyncTime = 0 and installation time is {0} Going to continue without split sync check!", installationTime);
                return -1L;
            }
            lastSuccessfulSyncTime = Long.parseLong(SyMUtil.getInstallationProperty("it"));
        }
        final SummarySyncParamsDAOUtil summarySyncParamsDAOUtil = new SummarySyncParamsDAOUtil();
        final String splitSync = summarySyncParamsDAOUtil.getSummarySyncParams("ENABLE_SPLIT_SYNC");
        if (splitSync != null && splitSync.equalsIgnoreCase("true")) {
            final String splitLimitStr = summarySyncParamsDAOUtil.getSummarySyncParams("SPLIT_SYNC_MINUTES");
            SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Split Sync enabled : true, with split sync minutes ", splitLimitStr);
            if (splitLimitStr != null) {
                final long splitLimit = Long.parseLong(splitLimitStr) * 60L * 1000L;
                final long diffTime = currSyncLockedTime - lastSuccessfulSyncTime;
                if (diffTime > splitLimit) {
                    newCurrSyncLockedTime = lastSuccessfulSyncTime + splitLimit;
                    SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "currSyncLockedTime from changed from : {0}  to {1}:", new Object[] { currSyncLockedTime, newCurrSyncLockedTime });
                }
            }
        }
        return newCurrSyncLockedTime;
    }
    
    public boolean performRegularSync(final long moduleID, final SyncAPI syncAPI) {
        return this.performSync(SyncConstants.SyncType.REGULAR_SYNC, moduleID, syncAPI);
    }
    
    public boolean performSync(final SyncConstants.SyncType syncType, final long moduleID, final SyncAPI syncAPI) {
        return this.performSync(syncType, moduleID, syncAPI, 1);
    }
    
    private boolean performSync(final SyncConstants.SyncType syncType, final long moduleID, final SyncAPI syncAPI, final int splitCount) {
        final String sourceMethod = "performSync";
        boolean isProbeSyncSuccess = false;
        final long startTime = System.currentTimeMillis();
        try {
            SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "*********************** {0} - on moduleID {1} (Split - {2}) Starts ********************", new Object[] { syncType.getValue(), moduleID, splitCount });
            final boolean syncPreCheckStatus = syncAPI.syncPreChecks();
            if (syncPreCheckStatus) {
                final boolean modulePreCheckStatus = syncAPI.performPreChecks(moduleID);
                if (modulePreCheckStatus) {
                    final Map<String, Object> syncParams = syncAPI.getSyncParameters(moduleID);
                    if (syncParams != null) {
                        switch (syncType) {
                            case FULL_SYNC: {
                                isProbeSyncSuccess = this.doFullSync(moduleID, syncAPI, syncParams);
                                break;
                            }
                            default: {
                                final int lastSyncStatus = syncAPI.getLastSyncStatus(moduleID, syncParams);
                                isProbeSyncSuccess = this.doSyncOperation(moduleID, syncAPI, syncParams, lastSyncStatus);
                                break;
                            }
                        }
                        syncAPI.performPostChecks(moduleID, isProbeSyncSuccess);
                        isProbeSyncSuccess = syncAPI.syncPostChecks(moduleID, syncParams, isProbeSyncSuccess);
                        this.checkForSplitSync(syncType, moduleID, syncAPI, syncParams, splitCount);
                    }
                    else {
                        SyMLogger.warning(SyncUtil.logger, "SyncUtil", sourceMethod, "syncParams returns null, {0} cannot be performed", syncType.getValue());
                    }
                }
                else {
                    SyMLogger.warning(SyncUtil.logger, "SyncUtil", sourceMethod, "modulePreChecks returns false, {0} cannot be performed", syncType.getValue());
                }
            }
            else {
                SyMLogger.warning(SyncUtil.logger, "SyncUtil", sourceMethod, "syncPreChecks returns false, {0} cannot be performed", syncType.getValue());
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while perform sync : ", e);
        }
        finally {
            final long endTime = System.currentTimeMillis();
            final long totalTimeTaken = endTime - startTime;
            SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Total time taken on {0} for moduleID {1} : {2}", new Object[] { syncType.getValue(), moduleID, totalTimeTaken });
            SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "*********************** {0} - on moduleID {1} (Split - {2}) Ends ********************", new Object[] { syncType.getValue(), moduleID, splitCount });
        }
        return isProbeSyncSuccess;
    }
    
    private void checkForSplitSync(final SyncConstants.SyncType syncType, final long moduleID, final SyncAPI syncAPI, final Map<String, Object> syncParams, int splitCount) {
        final String sourceMethod = "checkForSplitSync";
        final int isSplitSync = syncParams.getOrDefault("IS_SPLIT_SYNC", 0);
        final int isEmptySync = syncParams.getOrDefault("IS_EMPTY_SYNC", 0);
        final long currSyncLockedTime = syncParams.get("CURR_SYNC_LOCKED_TIME");
        final SummarySyncParamsDAOUtil summarySyncParamsDAOUtil = new SummarySyncParamsDAOUtil();
        final int syncBeforeTimeMin = summarySyncParamsDAOUtil.getSyncBeforeMinutes();
        final long syncBeforeTimeMillis = syncBeforeTimeMin * 60L * 1000L;
        final long buffer = 300000L;
        final long actualCurrSyncLockTime = System.currentTimeMillis() - (syncBeforeTimeMillis + buffer);
        if (isSplitSync == 1 && isEmptySync == 1 && currSyncLockedTime < actualCurrSyncLockTime) {
            final int maxSplitPerSync = 10;
            if (splitCount < maxSplitPerSync) {
                ++splitCount;
                SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "IS_SPLIT_SYNC = 1 && IS_EMPTY_SYNC = 1 && splitCount = {0}, Going tp continue with next split in the same sync", splitCount);
                this.performSync(syncType, moduleID, syncAPI, splitCount);
            }
        }
    }
    
    private boolean doSyncOperation(final long moduleID, final SyncAPI syncAPI, Map<String, Object> syncParams, int lastSyncStatus) throws Exception {
        final String sourceMethod = "doSyncOperation";
        boolean isProbeSyncSuccess = false;
        final Long probeID = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID();
        final long lastSyncTime = syncParams.get("LAST_SYNC_TIME");
        final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
        final Row moduleAuditRow = syncModuleAuditDAOUtil.getSyncModuleAuditRow(probeID, moduleID, lastSyncTime);
        switch (lastSyncStatus) {
            case 1:
            case 6: {
                isProbeSyncSuccess = this.doCurrentSync(moduleID, syncAPI, syncParams);
                break;
            }
            case 3: {
                final com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus failureStatus = this.getSyncFailureStatus(lastSyncStatus, moduleID, syncParams);
                if (failureStatus == com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus.THRESHOLD_NOT_REACHED) {
                    syncParams.put("SYNC_RETRY_COUNT", failureStatus.getFailedCount());
                    isProbeSyncSuccess = this.doCurrentSync(moduleID, syncAPI, syncParams);
                    break;
                }
                if (failureStatus == com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus.CRITICAL_LIMIT_REACHED) {
                    syncParams.put("SYNC_RETRY_COUNT", failureStatus.getFailedCount());
                    isProbeSyncSuccess = this.doFullSync(moduleID, syncAPI, syncParams);
                    break;
                }
                isProbeSyncSuccess = false;
                this.disableSyncScheduler(moduleID, "DISABLED_ON_PROBE_FAILURE");
                break;
            }
            case 5: {
                if (moduleAuditRow == null) {
                    SyMLogger.warning(SyncUtil.logger, "SyncUtil", sourceMethod, "Status not received for empty audit sync as module audit row is {0}!", moduleAuditRow);
                    isProbeSyncSuccess = this.doCurrentSync(moduleID, syncAPI, syncParams);
                    break;
                }
                lastSyncStatus = this.fetchLastSyncStatusFromSummaryServer(moduleID, syncAPI, syncParams);
                SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Last Sync Status fetched from Summary Server : {0}", lastSyncStatus);
                if (lastSyncStatus != 5) {
                    syncParams = syncAPI.getSyncParameters(moduleID);
                    isProbeSyncSuccess = this.doSyncOperation(moduleID, syncAPI, syncParams, lastSyncStatus);
                    break;
                }
                final com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus failureStatus = this.getSyncFailureStatus(lastSyncStatus, moduleID, syncParams);
                if (failureStatus == com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus.THRESHOLD_NOT_REACHED) {
                    isProbeSyncSuccess = false;
                    break;
                }
                if (failureStatus == com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus.CRITICAL_LIMIT_REACHED) {
                    syncParams.put("SYNC_RETRY_COUNT", failureStatus.getFailedCount());
                    isProbeSyncSuccess = this.doCurrentSync(moduleID, syncAPI, syncParams);
                    break;
                }
                isProbeSyncSuccess = false;
                this.disableSyncScheduler(moduleID, "DISABLED_ON_NO_STATUS_FROM_SS");
                break;
            }
            case 2: {
                if (moduleAuditRow == null) {
                    SyMLogger.warning(SyncUtil.logger, "SyncUtil", sourceMethod, "Status not received for empty audit sync as module audit row is {0}!", moduleAuditRow);
                    isProbeSyncSuccess = this.doCurrentSync(moduleID, syncAPI, syncParams);
                    break;
                }
                lastSyncStatus = this.fetchLastSyncStatusFromSummaryServer(moduleID, syncAPI, syncParams);
                SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Last Sync Status fetched from Summary Server : {0}", lastSyncStatus);
                if (lastSyncStatus != 2) {
                    syncParams = syncAPI.getSyncParameters(moduleID);
                    isProbeSyncSuccess = this.doSyncOperation(moduleID, syncAPI, syncParams, lastSyncStatus);
                    break;
                }
                final com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus failureStatus = this.getSyncFailureStatus(lastSyncStatus, moduleID, syncParams);
                if (failureStatus == com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus.THRESHOLD_NOT_REACHED) {
                    isProbeSyncSuccess = false;
                    break;
                }
                if (failureStatus == com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus.CRITICAL_LIMIT_REACHED) {
                    syncParams.put("SYNC_RETRY_COUNT", failureStatus.getFailedCount());
                    isProbeSyncSuccess = this.doCurrentSync(moduleID, syncAPI, syncParams);
                    break;
                }
                isProbeSyncSuccess = false;
                this.disableSyncScheduler(moduleID, "DISABLED_ON_SS_LONG_PROCESSING");
                break;
            }
            case 4: {
                isProbeSyncSuccess = this.doSyncRetry(moduleID, syncAPI, syncParams);
                break;
            }
        }
        return isProbeSyncSuccess;
    }
    
    private boolean doCurrentSync(final long moduleID, final SyncAPI syncAPI, final Map<String, Object> syncParams) throws Exception {
        final String sourceMethod = "performCurrentSync";
        SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Inside PerformCurrentSync for {0} : {1}", new Object[] { moduleID, syncParams });
        final List<SyncData> syncData = syncAPI.getAndWriteAddOrUpdateData(moduleID, syncParams);
        final List<SyncData> deleteData = syncAPI.getAndWriteDeleteData(moduleID, syncParams);
        syncData.addAll(deleteData);
        boolean isProbeSyncSuccess;
        if (syncData.size() > 0) {
            final Map<String, Boolean> syncFileStatus = syncAPI.postSyncDataToSummaryServer(moduleID, syncParams, syncData);
            isProbeSyncSuccess = syncAPI.addAuditEntryAndUpdateSyncStatus(moduleID, syncParams, syncFileStatus);
        }
        else {
            isProbeSyncSuccess = true;
            syncParams.put("IS_EMPTY_SYNC", 1);
        }
        SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "is Probe Sync Success : {0}", isProbeSyncSuccess);
        return isProbeSyncSuccess;
    }
    
    private List<String> getToBeProcessedTableNameList(final long moduleID, final long lastSuccessfulSyncTime) throws Exception {
        final String sourceMethod = "getToBeProcessedTableNameList";
        final SyncMetaDataDAOUtil syncMetaDataDAOUtil = new SyncMetaDataDAOUtil();
        final List<String> tableNameList = syncMetaDataDAOUtil.getSyncTableNames(moduleID);
        final List<String> toBeProcessedTables = new ArrayList<String>();
        for (final String tableName : tableNameList) {
            String lastUpdatedTime = ApiFactoryProvider.getRedisHashMap().get(tableName.toLowerCase(), 2);
            if (lastUpdatedTime != null) {
                SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Regular Sync Entry set for: {0} with last updated time: ", new Object[] { tableName, lastUpdatedTime });
            }
            else {
                ApiFactoryProvider.getRedisHashMap().put(tableName.toLowerCase(), "0", 2);
                SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Summary Server Table Entry newly added for: {0}", tableName);
                lastUpdatedTime = "0";
            }
            if (Long.parseLong(lastUpdatedTime) >= lastSuccessfulSyncTime) {
                toBeProcessedTables.add(tableName);
            }
        }
        SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Regular Sync HashMap is in order. Proceed to fetch necessary sql ids.. for moduleID", moduleID);
        return toBeProcessedTables;
    }
    
    public LinkedHashSet<Long> getUpdatedDataSQLIds(final long moduleID, final long lastSuccessfulSyncTime) throws Exception {
        final String sourceMethod = "getUpdatedDataSQLIds";
        final List<String> tableNameList = this.getToBeProcessedTableNameList(moduleID, lastSuccessfulSyncTime);
        final LinkedHashSet<Long> sqlIds = new LinkedHashSet<Long>();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("CustomViewConfiguration"));
        final Join join = new Join("CustomViewConfiguration", "SyncMetaData", new String[] { "CVNAME" }, new String[] { "QUERY_ID" }, 2);
        query.addJoin(join);
        query.addSelectColumn(new Column("CustomViewConfiguration", "*"));
        Criteria criteria = new Criteria(new Column("SyncMetaData", "PROBE_TABLE_NAME"), (Object)tableNameList.toArray(), 8);
        criteria = criteria.and(new Criteria(new Column("SyncMetaData", "MODULE_ID"), (Object)moduleID, 0));
        query.setCriteria(criteria);
        final Column syncOrderCol = new Column("SyncMetaData", "SYNC_ORDER");
        final SortColumn sortColumn = new SortColumn(syncOrderCol, true);
        query.addSortColumn(sortColumn);
        final DataObject dataObject = SyMUtil.getPersistence().get(query);
        final Iterator itr = dataObject.getRows("CustomViewConfiguration");
        while (itr.hasNext()) {
            final Row row = itr.next();
            sqlIds.add((Long)row.get("QUERYID"));
        }
        SyMLogger.debug(SyncUtil.logger, "SyncUtil", sourceMethod, "Updated SQLIds - {0}", sqlIds);
        return sqlIds;
    }
    
    public void checkAndClearInvalidDeleteEntries(final String deletionAuditTable, final SelectQuery selectQuery) {
        final String sourceMethod = "checkAndClearInvalidDeleteEntries";
        final RelationalAPI relationalAPI = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            final SelectQuery deletionAuditQuery = (SelectQuery)selectQuery.clone();
            final Column probeTableColumn = new Column(deletionAuditTable, "PROBE_TABLE_NAME");
            for (final String deleteInsertTableName : SyncUtil.deleteInsertTableList) {
                final Criteria deleteInsertTableCriteria = new Criteria(probeTableColumn, (Object)deleteInsertTableName, 0);
                deletionAuditQuery.setCriteria(deleteInsertTableCriteria);
                conn = relationalAPI.getConnection();
                int currIndex = 1;
                final int batchLimit = 1000;
                boolean rowsToBeFetched = false;
                do {
                    final Range range = new Range(currIndex, batchLimit);
                    deletionAuditQuery.setRange(range);
                    ds = relationalAPI.executeQuery((Query)deletionAuditQuery, conn);
                    String pkColumnName = null;
                    String[] pkColumnNames = new String[5];
                    int numberofPKCols = 1;
                    int rowsProcessed = 0;
                    boolean isFirstRow = true;
                    Criteria actualTableCriteria = null;
                    if (ds.next()) {
                        SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "DeletionAudit table entry exist for deleteInsert table : {0}", deleteInsertTableName);
                        do {
                            if (isFirstRow) {
                                pkColumnName = (String)ds.getValue("SS_TABLE_PK_COLUMN");
                                if (pkColumnName.contains("*")) {
                                    pkColumnNames = pkColumnName.split("\\*");
                                    numberofPKCols = pkColumnNames.length;
                                }
                                isFirstRow = false;
                            }
                            final String pkColumnValue = (String)ds.getValue("PROBE_TABLE_PK_VALUE");
                            Criteria rowCriteria = null;
                            if (numberofPKCols > 1) {
                                final String[] pkColumnValues = pkColumnValue.split("\\*");
                                int index;
                                for (int startIndex = index = (pkColumnNames[0].equals("PROBE_ID") ? 1 : 0); index < numberofPKCols; ++index) {
                                    final Criteria subCriteria = new Criteria(new Column(deleteInsertTableName, pkColumnNames[index]), (Object)pkColumnValues[index], 0, true);
                                    rowCriteria = ((rowCriteria == null) ? subCriteria : rowCriteria.and(subCriteria));
                                }
                            }
                            else {
                                rowCriteria = new Criteria(new Column(deleteInsertTableName, pkColumnName), (Object)pkColumnValue, 0, true);
                            }
                            actualTableCriteria = ((actualTableCriteria == null) ? rowCriteria : actualTableCriteria.or(rowCriteria));
                            ++rowsProcessed;
                        } while (ds.next());
                        if (actualTableCriteria != null) {
                            final DataObject actualTableDO = DataAccess.get(deleteInsertTableName, actualTableCriteria);
                            if (!actualTableDO.isEmpty()) {
                                final Criteria invalidDeleteCriteria = this.getInvalidEntryCriteria(actualTableDO, deleteInsertTableName, pkColumnNames, deletionAuditTable);
                                if (invalidDeleteCriteria != null) {
                                    SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "DeletionAudit table, Invalid entry exist for deleteInsert table : {0} ", deleteInsertTableName);
                                    SyMLogger.debug(SyncUtil.logger, "SyncUtil", sourceMethod, "invalidDeleteCriteria : {0}", invalidDeleteCriteria);
                                    this.cleanupInvalidEntriesInDeletionAuditTable(deletionAuditTable, invalidDeleteCriteria);
                                }
                            }
                        }
                    }
                    rowsToBeFetched = (rowsProcessed == batchLimit);
                    currIndex += batchLimit;
                } while (rowsToBeFetched);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while checkAndClearInvalidDeleteEntries", e);
            try {
                if (ds != null) {
                    ds.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final SQLException e2) {
                SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while closing dataset or connection", e2);
            }
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final SQLException e3) {
                SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while closing dataset or connection", e3);
            }
        }
    }
    
    private Criteria getInvalidEntryCriteria(final DataObject actualTableDO, final String probeTableName, final String[] pkColumnNames, final String deletionAuditTable) {
        final String sourceMethod = "getInvalidEntryCriteria";
        Criteria invalidDeleteCriteria = null;
        try {
            final String probeID = String.valueOf(ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID());
            final Iterator actualTableDORows = actualTableDO.getRows(probeTableName);
            final int numberofPKCols = pkColumnNames.length;
            while (actualTableDORows.hasNext()) {
                final int startIndex = pkColumnNames[0].equals("PROBE_ID") ? 1 : 0;
                String currPKColumnValue = pkColumnNames[0].equals("PROBE_ID") ? ((numberofPKCols > 1) ? (probeID + "*") : probeID) : null;
                final Row actualRow = actualTableDORows.next();
                for (int index = startIndex; index < numberofPKCols; ++index) {
                    final String pkValue = String.valueOf(actualRow.get(pkColumnNames[index]));
                    currPKColumnValue = ((currPKColumnValue == null) ? pkValue : (currPKColumnValue + pkValue));
                    if (index < numberofPKCols - 1) {
                        currPKColumnValue += "*";
                    }
                }
                Criteria criteria = new Criteria(new Column(deletionAuditTable, "PROBE_TABLE_NAME"), (Object)probeTableName, 0);
                criteria = criteria.and(new Column(deletionAuditTable, "PROBE_TABLE_PK_VALUE"), (Object)currPKColumnValue, 0);
                invalidDeleteCriteria = ((invalidDeleteCriteria == null) ? criteria : invalidDeleteCriteria.or(criteria));
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while deleting invalid deleteinserttable data entries", e);
        }
        return invalidDeleteCriteria;
    }
    
    public void cleanupInvalidEntriesInDeletionAuditTable(final String deletionAuditTable, final Criteria invalidDeleteCriteria) {
        final String sourceMethod = "cleanupInvalidEntriesInDeletionAuditTable";
        try {
            SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Deletion of Invalid delete entries from table : {0}", deletionAuditTable);
            final SummarySyncParamsDAOUtil summarySyncParamsDAOUtil = new SummarySyncParamsDAOUtil();
            final int chunk = Integer.parseInt(summarySyncParamsDAOUtil.getSummarySyncParams("AUDIT_DELETION_CHUNK"));
            DeletionFramework.delete(deletionAuditTable, invalidDeleteCriteria, chunk);
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while deleting invalid deleteinserttable data entries", e);
        }
    }
    
    private boolean doFullSync(final long moduleID, final SyncAPI syncAPI, final Map<String, Object> syncParams) throws Exception {
        final String sourceMethod = "performFullSync";
        final long lastSuccessfulSyncTime = 0L;
        syncParams.put("LAST_SUCCESSFUL_SYNC_TIME", lastSuccessfulSyncTime);
        long currSyncLockedTime = syncParams.get("CURR_SYNC_LOCKED_TIME");
        final long splitCurrSyncLockedTime = this.getCurrSyncLockTimeWithSplitSyncLimit(currSyncLockedTime, lastSuccessfulSyncTime);
        if (splitCurrSyncLockedTime != -1L) {
            currSyncLockedTime = splitCurrSyncLockedTime;
            syncParams.put("IS_SPLIT_SYNC", 1);
            syncParams.put("CURR_SYNC_LOCKED_TIME", currSyncLockedTime);
        }
        SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Inside doFullSync for {0} : {1}", new Object[] { moduleID, syncParams });
        return this.doCurrentSync(moduleID, syncAPI, syncParams);
    }
    
    private boolean doSyncRetry(final long moduleID, final SyncAPI syncAPI, final Map<String, Object> syncParams) throws Exception {
        final String sourceMethod = "performSyncRetry";
        SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Inside PerformSyncRetry for {0} : {1}", new Object[] { moduleID, syncParams });
        final long lastSuccessfulSyncTime = syncParams.get("LAST_SUCCESSFUL_SYNC_TIME");
        final long currSyncLockedTime = syncParams.get("CURR_SYNC_LOCKED_TIME");
        final long lastSyncTime = syncParams.get("LAST_SYNC_TIME");
        SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Performing Sync retry!Updated currSyncLockedtime from {0} to lastSyncTime  : {1}", new Object[] { currSyncLockedTime, lastSyncTime });
        boolean isProbeSyncSuccess = false;
        boolean performCurrentSync = false;
        final List<Long> retryFileList = new ArrayList<Long>();
        final Long probeID = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID();
        final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
        final DataObject syncAuditDO = syncModuleAuditDAOUtil.getSyncModuleDOAfterSyncTime(probeID, moduleID, lastSuccessfulSyncTime);
        if (syncAuditDO != null && !syncAuditDO.isEmpty()) {
            final Iterator syncAuditRows = syncAuditDO.getRows("SyncModuleAudit");
            final SyncFileAuditDAOUtil syncFileAuditDAOUtil = new SyncFileAuditDAOUtil();
            while (syncAuditRows.hasNext()) {
                final Row syncAuditRow = syncAuditRows.next();
                final long moduleAuditId = (long)syncAuditRow.get("MODULE_AUDIT_ID");
                final DataObject fileAuditDO = syncFileAuditDAOUtil.getSyncFileAuditDO(moduleAuditId);
                if (fileAuditDO != null && !fileAuditDO.isEmpty()) {
                    final Iterator fileAuditRows = fileAuditDO.getRows("SyncFileAudit");
                    while (fileAuditRows.hasNext()) {
                        final Row fileAuditRow = fileAuditRows.next();
                        final long fileAuditID = (long)fileAuditRow.get("FILE_AUDIT_ID");
                        final String fileName = (String)fileAuditRow.get("FILE_NAME");
                        final int fileStatus = (int)fileAuditRow.get("FILE_STATUS");
                        switch (fileStatus) {
                            case 950204: {
                                SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "File skipped with status : {0} for fileName {1}: ", new Object[] { fileStatus, fileName });
                                performCurrentSync = true;
                                continue;
                            }
                            case 950100:
                            case 950203:
                            case 950303: {
                                SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "File skipped with status : {0} for fileName {1}: ", new Object[] { fileStatus, fileName });
                                continue;
                            }
                            default: {
                                SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "File added to retry with status : : {0} for fileName {1}: ", new Object[] { fileStatus, fileName });
                                retryFileList.add(fileAuditID);
                                continue;
                            }
                        }
                    }
                }
            }
        }
        if (retryFileList.size() > 0) {
            isProbeSyncSuccess = this.doSyncFileRetry(moduleID, syncAPI, syncParams, retryFileList);
        }
        else if (performCurrentSync) {
            syncParams.put("SYNC_RETRY_COUNT", 1);
            isProbeSyncSuccess = this.doCurrentSync(moduleID, syncAPI, syncParams);
        }
        else {
            isProbeSyncSuccess = true;
        }
        return isProbeSyncSuccess;
    }
    
    private boolean doSyncFileRetry(final long moduleID, final SyncAPI syncAPI, final Map<String, Object> syncParams, final List<Long> retryFileList) throws Exception {
        final String sourceMethod = "performSyncFileRetry";
        boolean isProbeSyncSucess = true;
        final LinkedList<SyncData> retrySyncDataObjects = new LinkedList<SyncData>();
        final long syncTime = syncParams.get("LAST_SYNC_TIME");
        boolean doCurrentSync = false;
        boolean doFullSync = false;
        int maxRetryCount = 0;
        final Column fileAuditIDCol = Column.getColumn("SyncFileAudit", "FILE_AUDIT_ID");
        final Criteria criteria = new Criteria(fileAuditIDCol, (Object)retryFileList.toArray(), 8);
        final DataObject fileAuditDO = DataAccess.get("SyncFileAudit", criteria);
        if (!fileAuditDO.isEmpty()) {
            final long failedParentModuleID = this.checkForParentModuleSync(moduleID, syncTime);
            final com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus status = this.getSyncFailureStatus(4, moduleID, syncParams);
            final SummarySyncParamsDAOUtil syncParamsDAOUtil = new SummarySyncParamsDAOUtil();
            final int skipThreshold = syncParamsDAOUtil.getSkipThreshold();
            final Iterator fileAuditRows = fileAuditDO.getRows("SyncFileAudit");
            final SyncDataCreator syncDataCreator = new SyncDataCreator();
        Label_0446:
            while (fileAuditRows.hasNext()) {
                final Row fileAuditRow = fileAuditRows.next();
                final String fileName = (String)fileAuditRow.get("FILE_NAME");
                final int fileStatus = (int)fileAuditRow.get("FILE_STATUS");
                final int retryCount = (int)fileAuditRow.get("RETRY_COUNT");
                if (retryCount > maxRetryCount) {
                    maxRetryCount = retryCount;
                }
                switch (fileStatus) {
                    case 950503:
                    case 950601:
                    case 950602:
                    case 950801: {
                        if (failedParentModuleID != -1L && status != com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus.THRESHOLD_NOT_REACHED) {
                            this.disableSyncScheduler(moduleID, "DISABLED_ON_PARENT_MODULE_FAILURE_" + failedParentModuleID);
                            return false;
                        }
                        if (retryCount < skipThreshold) {
                            if (retryCount == 0) {
                                final SyncData syncData = syncDataCreator.getSyncDataObjectForRetryFile(moduleID, syncTime, fileName);
                                retrySyncDataObjects.add(syncData);
                                continue;
                            }
                            doCurrentSync = true;
                            break Label_0446;
                        }
                        else {
                            if (retryCount == skipThreshold) {
                                doFullSync = true;
                                break Label_0446;
                            }
                            SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "File Retry threshold exceeded for {0}", fileName);
                            SyMLogger.info(SyncUtil.syncStatusLogger, "SyncUtil", sourceMethod, "Going to disable sync scheduler!! Need manual troubleshoot for module ", moduleID);
                            this.disableSyncScheduler(moduleID, "DISABLED_ON_SS_FAILURE");
                            return false;
                        }
                        break;
                    }
                    default: {
                        SyMLogger.warning(SyncUtil.logger, "SyncUtil", sourceMethod, "Need to handle SyncStatusCode : {0} for fileName : {1}", new Object[] { fileStatus, fileName });
                        continue;
                    }
                }
            }
            if (doCurrentSync) {
                syncParams.put("SYNC_RETRY_COUNT", maxRetryCount + 1);
                return this.doCurrentSync(moduleID, syncAPI, syncParams);
            }
            if (doFullSync) {
                syncParams.put("SYNC_RETRY_COUNT", skipThreshold + 1);
                return this.doFullSync(moduleID, syncAPI, syncParams);
            }
            if (retrySyncDataObjects.size() <= 0) {
                SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Empty SyncData objects!Updating sync as success for ", moduleID);
                return true;
            }
            syncParams.put("CURR_SYNC_LOCKED_TIME", syncTime);
            final SyncMetaDataDAOUtil syncMetaDataDAOUtil = new SyncMetaDataDAOUtil();
            final DataObject syncMetaDO = syncMetaDataDAOUtil.getSyncMetaDataDO(moduleID);
            final Criteria moduleCri = new Criteria(Column.getColumn("SyncMetaData", "MODULE_ID"), (Object)moduleID, 0);
            syncMetaDO.getDataObject("SyncMetaData", moduleCri);
            final Iterator iterator = syncMetaDO.getRows("SyncMetaData");
            final HashMap<String, Integer> orderMap = new HashMap<String, Integer>();
            while (iterator.hasNext()) {
                final Row syncMetaRow = iterator.next();
                final String key = (String)syncMetaRow.get("SS_TABLE_NAME");
                final Integer value = (Integer)syncMetaRow.get("SYNC_ORDER");
                orderMap.put(key, value);
            }
            this.reOrderSyncData(orderMap, retrySyncDataObjects);
            final Map<String, Boolean> syncFileStatus = syncAPI.postSyncDataToSummaryServer(moduleID, syncParams, retrySyncDataObjects);
            isProbeSyncSucess = syncAPI.addAuditEntryAndUpdateSyncStatus(moduleID, syncParams, syncFileStatus);
            SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "is Probe Sync Success : {0}", isProbeSyncSucess);
        }
        return isProbeSyncSucess;
    }
    
    private long checkForParentModuleSync(final long moduleID, final long syncTime) {
        final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
        final ProbeSyncModuleDataDAOUtil probeSyncModuleDataDAOUtil = new ProbeSyncModuleDataDAOUtil();
        final List<Long> parentModuleIDs = syncModuleMetaDAOUtil.getAllParentModuleIDs(moduleID);
        for (final long parentModuleID : parentModuleIDs) {
            final long lastSuccessfulSyncTime = probeSyncModuleDataDAOUtil.getLastSuccessfulSyncTime(parentModuleID);
            if (lastSuccessfulSyncTime < syncTime) {
                return parentModuleID;
            }
        }
        return -1L;
    }
    
    private void reOrderSyncData(final HashMap<String, Integer> orderMap, final List<SyncData> retrySyncDataObjects) {
        retrySyncDataObjects.sort((s1, s2) -> {
            final String t1 = s1.tableName;
            final String t2 = s2.tableName;
            return hashMap.get(t1).compareTo(hashMap.get(t2));
        });
    }
    
    private int fetchLastSyncStatusFromSummaryServer(final long moduleID, final SyncAPI syncAPI, final Map<String, Object> syncParams) {
        final String sourceMethod = "";
        int lastSyncStatus = 5;
        final long lastSyncTime = syncParams.get("LAST_SYNC_TIME");
        final ProbeSyncStatusUpdater probeSyncStatusUpdater = new ProbeSyncStatusUpdater();
        final boolean isUpdated = probeSyncStatusUpdater.getAndUpdateSyncStatusFromSummaryServer(moduleID, lastSyncTime);
        if (isUpdated) {
            final long probeID = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID();
            final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
            lastSyncStatus = syncModuleAuditDAOUtil.getModuleSyncStatus(probeID, moduleID, lastSyncTime);
        }
        else {
            SyMLogger.warning(SyncUtil.logger, "SyncUtil", sourceMethod, "Fetch sync status from Summary server failed for {0}", moduleID);
        }
        return lastSyncStatus;
    }
    
    private com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus getSyncFailureStatus(final int syncStatus, final long moduleID, final Map<String, Object> syncParams) {
        final String sourceMethod = "getSyncFailureStatus";
        com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus status = com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus.THRESHOLD_NOT_REACHED;
        final long currSyncLockedTime = syncParams.get("CURR_SYNC_LOCKED_TIME");
        final long lastSyncTime = syncParams.get("LAST_SYNC_TIME");
        final String key = String.valueOf(moduleID);
        int failureCount = 0;
        try {
            final SummarySyncParamsDAOUtil syncParamsDAOUtil = new SummarySyncParamsDAOUtil();
            final int skipThreshold = syncParamsDAOUtil.getSkipThreshold();
            switch (syncStatus) {
                case 4: {
                    SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Parent Module sync failure - LastSyncTime : {0}, currSyncLockedTime : {1}", new Object[] { lastSyncTime, currSyncLockedTime });
                    String value = this.parentSyncFailureMap.get(key);
                    if (value != null && value.startsWith(String.valueOf(lastSyncTime))) {
                        failureCount = Integer.parseInt(value.split("_")[1]);
                    }
                    ++failureCount;
                    value = lastSyncTime + "_" + failureCount;
                    this.parentSyncFailureMap.put(key, value);
                    SyMLogger.debug(SyncUtil.logger, "SyncUtil", sourceMethod, "parentSyncFailureMap details : {0}", this.parentSyncFailureMap);
                    break;
                }
                case 3: {
                    SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Probe sync failure - LastSyncTime : {0}, currSyncLockedTime : {1}", new Object[] { lastSyncTime, currSyncLockedTime });
                    String value = this.probeSyncFailureMap.get(key);
                    if (value != null && value.startsWith(String.valueOf(lastSyncTime))) {
                        failureCount = Integer.parseInt(value.split("_")[1]);
                    }
                    ++failureCount;
                    value = lastSyncTime + "_" + failureCount;
                    this.probeSyncFailureMap.put(key, value);
                    SyMLogger.debug(SyncUtil.logger, "SyncUtil", sourceMethod, "probeSyncFailure details : {0}", this.probeSyncFailureMap);
                    break;
                }
                case 5: {
                    SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Long waiting for Summary server status - LastSyncTime : {0}, currSyncLockedTime : {1}", new Object[] { lastSyncTime, currSyncLockedTime });
                    String value = this.longWaitingStatusMap.get(key);
                    if (value != null && value.startsWith(String.valueOf(lastSyncTime))) {
                        failureCount = Integer.parseInt(value.split("_")[1]);
                    }
                    ++failureCount;
                    value = lastSyncTime + "_" + failureCount;
                    this.longWaitingStatusMap.put(key, value);
                    SyMLogger.debug(SyncUtil.logger, "SyncUtil", sourceMethod, "longWaitingStatusMap details : {0}", this.longWaitingStatusMap);
                    break;
                }
                case 2: {
                    SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Long Summary server processing - LastSyncTime : {0}, currSyncLockedTime : {1}", new Object[] { lastSyncTime, currSyncLockedTime });
                    String value = this.longSummaryProcessingMap.get(key);
                    if (value != null && value.startsWith(String.valueOf(lastSyncTime))) {
                        failureCount = Integer.parseInt(value.split("_")[1]);
                    }
                    ++failureCount;
                    value = lastSyncTime + "_" + failureCount;
                    this.longSummaryProcessingMap.put(key, value);
                    SyMLogger.debug(SyncUtil.logger, "SyncUtil", sourceMethod, "longSummaryProcessingMap details : {0}", this.longSummaryProcessingMap);
                    break;
                }
            }
            if (failureCount < skipThreshold) {
                status = com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus.THRESHOLD_NOT_REACHED;
            }
            else if (failureCount == skipThreshold) {
                status = com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus.CRITICAL_LIMIT_REACHED;
            }
            else {
                status = com.me.ems.summaryserver.probe.sync.SyncConstants.SyncFailureStatus.CRITICAL_LIMIT_EXCEEDED;
            }
            status.setFailedCount(failureCount);
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception in getSyncFailureStatus :", e);
        }
        return status;
    }
    
    private boolean isThresholdExceedsForLongRunningSync(final long moduleID, final long syncTime) {
        final String sourceMethod = "isThresholdExceedsForLongRunningSync";
        boolean status = true;
        final String key = String.valueOf(moduleID);
        String value = this.longRunningSyncMap.get(key);
        int skipCount = 0;
        if (value != null && value.startsWith(String.valueOf(syncTime))) {
            skipCount = Integer.parseInt(value.split("_")[1]);
        }
        ++skipCount;
        value = syncTime + "_" + skipCount;
        final SummarySyncParamsDAOUtil syncParamsDAOUtil = new SummarySyncParamsDAOUtil();
        final int skipThreshold = syncParamsDAOUtil.getSkipThreshold();
        if (skipCount <= skipThreshold) {
            status = false;
            this.longRunningSyncMap.put(key, value);
            SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Long running sync : {0}", syncTime);
        }
        else {
            SyMLogger.info(SyncUtil.logger, "SyncUtil", sourceMethod, "Long running sync : {0}, reached threshold - {1} , Going to return isThresholdExceedsForLongRunningSync as true!", new Object[] { syncTime, skipThreshold });
        }
        SyMLogger.debug(SyncUtil.logger, "SyncUtil", sourceMethod, "LongRunningSync details : {0}", this.longRunningSyncMap);
        return status;
    }
    
    public void disableSyncScheduler(final String reasonForDisable) {
        final String sourceMethod = "disableSyncScheduler";
        try {
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            final List<Long> moduleIDs = syncModuleMetaDAOUtil.getAllModuleIDs();
            for (final long moduleID : moduleIDs) {
                this.disableSyncScheduler(moduleID, reasonForDisable);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while disabling sync scheduler", e);
        }
    }
    
    public void disableSyncScheduler(final long moduleID, final String disableReason) {
        final String sourceMethod = "disableSyncScheduler";
        try {
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            final String schedulerName = syncModuleMetaDAOUtil.getSyncSchedulerName(moduleID);
            final String syncState = ApiFactoryProvider.getRedisHashMap().get(moduleID + "_SYNC", 2);
            SyMLogger.debug(SyncUtil.syncStatusLogger, "SyncUtil", sourceMethod, "Sync State for {0} : {1}", new Object[] { moduleID, syncState });
            switch (disableReason) {
                case "DISABLED_ON_SS_DOWN": {
                    if (syncState.equalsIgnoreCase("ENABLED")) {
                        ApiFactoryProvider.getSchedulerAPI().setSchedulerState(SchedulerConstants.DISABLE, schedulerName);
                        this.updateSyncState(moduleID, disableReason);
                        break;
                    }
                    break;
                }
                default: {
                    ApiFactoryProvider.getSchedulerAPI().setSchedulerState(SchedulerConstants.DISABLE, schedulerName);
                    this.updateSyncState(moduleID, disableReason);
                    break;
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while disabling sync scheduler", e);
        }
        SyMLogger.info(SyncUtil.syncStatusLogger, "SyncUtil", sourceMethod, "Sync Disabled for {0} : {1} ", new Object[] { moduleID, disableReason });
    }
    
    public void checkAndEnableSyncScheduler(final long moduleID, final String reasonForEnable) {
        final String sourceMethod = "enableSyncScheduler";
        try {
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            final String syncState = ApiFactoryProvider.getRedisHashMap().get(moduleID + "_SYNC", 2);
            SyMLogger.debug(SyncUtil.syncStatusLogger, "SyncUtil", sourceMethod, "Sync State for {0} : {1}", new Object[] { moduleID, syncState });
            if (syncState == null) {
                SyMLogger.info(SyncUtil.syncStatusLogger, "SyncUtil", sourceMethod, "Sync State for {0}  : null. Going to update as Sync running", moduleID);
                this.enableSyncScheduler(moduleID, "ENABLED");
            }
            else {
                switch (reasonForEnable) {
                    case "ENABLE_ON_SS_LIVE": {
                        if (syncState.equals("DISABLED_ON_SS_DOWN")) {
                            this.enableSyncScheduler(moduleID, "ENABLE_ON_SS_LIVE");
                            break;
                        }
                        break;
                    }
                    case "ENABLE_ON_SS_STATUS": {
                        if (syncState.equals("DISABLED_ON_NO_STATUS_FROM_SS") || syncState.equals("DISABLED_ON_SS_LONG_PROCESSING")) {
                            this.enableSyncScheduler(moduleID, "ENABLE_ON_SS_STATUS");
                        }
                        final List<Long> childModuleIDs = syncModuleMetaDAOUtil.getAllChildModuleIDs(moduleID);
                        for (final Long childModule : childModuleIDs) {
                            final String childSyncState = ApiFactoryProvider.getRedisHashMap().get(childModule + "_SYNC", 2);
                            if (childSyncState.equals("DISABLED_ON_PARENT_MODULE_FAILURE_" + moduleID)) {
                                this.enableSyncScheduler(moduleID, "ENABLE_ON_SS_STATUS");
                            }
                        }
                        break;
                    }
                    case "ENABLE_ON_MAINTENANCE_RETRY": {
                        this.longRunningSyncMap.clear();
                        this.probeSyncFailureMap.clear();
                        this.longSummaryProcessingMap.clear();
                        this.longWaitingStatusMap.clear();
                        this.probeSyncFailureMap.clear();
                        if (!syncState.equals("ENABLED")) {
                            this.enableSyncScheduler(moduleID, "ENABLE_ON_MAINTENANCE_RETRY");
                            break;
                        }
                        break;
                    }
                    default: {
                        this.enableSyncScheduler(moduleID, reasonForEnable);
                        break;
                    }
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while enabling sync scheduler for moduleID " + moduleID, e);
        }
    }
    
    public void checkAndEnableSyncScheduler(final String reasonForEnable) {
        final String sourceMethod = "checkAndEnableSyncScheduler";
        try {
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            final List<Long> moduleIDs = syncModuleMetaDAOUtil.getAllModuleIDs();
            for (final long moduleID : moduleIDs) {
                this.checkAndEnableSyncScheduler(moduleID, reasonForEnable);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while enabling sync scheduler", e);
        }
    }
    
    private void enableSyncScheduler(final long moduleID, final String enableReason) {
        final String sourceMethod = "enableSyncScheduler";
        try {
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            final String schedulerName = syncModuleMetaDAOUtil.getSyncSchedulerName(moduleID);
            ApiFactoryProvider.getSchedulerAPI().setSchedulerState(SchedulerConstants.ENABLE, schedulerName);
            SyMLogger.info(SyncUtil.syncStatusLogger, "SyncUtil", sourceMethod, "Sync Enabled for {0} : {1}", new Object[] { moduleID, enableReason });
            this.updateSyncState(moduleID, "ENABLED");
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, " Exception in enableSyncScheduler :", e);
        }
    }
    
    private void updateSyncState(final long moduleID, final String syncState) {
        ApiFactoryProvider.getRedisHashMap().put(moduleID + "_SYNC", syncState, 2);
        final SummarySyncParamsDAOUtil summarySyncParamsDAOUtil = new SummarySyncParamsDAOUtil();
        summarySyncParamsDAOUtil.updateSummarySyncParams(moduleID + "_SYNC", syncState);
    }
    
    public void cleanUpSyncFolderData(final long moduleID, final long syncTime) {
        final String sourceMethod = "cleanUpSyncFolderData";
        final SyncDataCreator syncDataCreator = new SyncDataCreator();
        final String syncFolderPath = syncDataCreator.getFolderPathForCurrSync(moduleID, syncTime);
        try {
            ApiFactoryProvider.getFileAccessAPI().forceDeleteDirectory(syncFolderPath);
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception while cleanUpSyncFolderData", e);
        }
    }
    
    public void cleanUpDeletionAuditTable(final long moduleID, final long syncTime) {
        final String sourceMethod = "cleanUpDeletionAuditTable";
        final ProbeSyncModuleMetaDAOUtil probeSyncModuleMetaDAOUtil = new ProbeSyncModuleMetaDAOUtil();
        try {
            final String deletionAuditTable = probeSyncModuleMetaDAOUtil.getDeletionTable(moduleID);
            if (deletionAuditTable != null) {
                final Criteria criteria = new Criteria(new Column(deletionAuditTable, "LAST_UPDATED_TIME"), (Object)syncTime, 6);
                DataAccess.delete(deletionAuditTable, criteria);
            }
            else {
                SyMLogger.warning(SyncUtil.logger, "SyncUtil", sourceMethod, "DeletionAuditTable for moduleID :{0} is null", moduleID);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncUtil.logger, "SyncUtil", sourceMethod, "Exception in cleaning up of Deletion audit table:", e);
        }
    }
    
    static {
        SyncUtil.logger = Logger.getLogger("ProbeSyncLogger");
        SyncUtil.dataPostLogger = Logger.getLogger("ProbeDataPostLogger");
        SyncUtil.dataCollectorLogger = Logger.getLogger("ProbeDataCollectionLogger");
        SyncUtil.syncStatusLogger = Logger.getLogger("ProbeSyncStatusLogger");
        SyncUtil.syncUtil = null;
        SyncUtil.syncSettingsJSON = null;
        deleteInsertTableList = new HashSet<String>();
    }
}
