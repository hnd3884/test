package com.me.ems.summaryserver.summary.sync.utils;

import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.DeleteQuery;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.me.ems.summaryserver.common.sync.utils.SyncFileAuditDAOUtil;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleAuditDAOUtil;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.FileReader;
import java.util.logging.Level;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.ems.summaryserver.common.sync.SyncException;
import com.me.ems.summaryserver.summary.sync.factory.SyncAPI;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.ems.summaryserver.summary.sync.DefaultSummarySyncImpl;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.List;
import java.util.Map;

public class SyncUtil
{
    @Deprecated
    public static Map<String, List<String>> conflictResolution;
    private static SyncUtil syncUtil;
    private static String sourceClass;
    private static Logger logger;
    private static Logger processingLogger;
    private static Logger accessLogger;
    private final HashMap processingTimeHash;
    
    public SyncUtil() {
        this.processingTimeHash = new HashMap();
    }
    
    public static SyncUtil getInstance() {
        if (SyncUtil.syncUtil == null) {
            SyncUtil.syncUtil = new SyncUtil();
        }
        return SyncUtil.syncUtil;
    }
    
    public void processCSVData(final DefaultSummarySyncImpl dataSync, final Long probeID, final long moduleID, final String filePath, final String tableName) throws SyncException {
        ProbeMgmtFactoryProvider.getSummaryServerSyncAPI().processCSVData(dataSync, probeID, moduleID, filePath, tableName);
    }
    
    public void processJSONDeletionData(final DefaultSummarySyncImpl dataSync, final Long probeID, final long moduleID, final String qDataStr) throws SyncException {
        ProbeMgmtFactoryProvider.getSummaryServerSyncAPI().processJSONDeletionData(dataSync, probeID, moduleID, qDataStr);
    }
    
    public String getTableName(final String fileName) {
        final String[] fileNameArray = fileName.split("-");
        return fileNameArray[2];
    }
    
    public String getValidFilePath(String filePath) {
        final String serverHome = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome()).getAbsolutePath();
        filePath = serverHome + filePath;
        filePath = filePath.replace("..", "");
        filePath = filePath.replace("bin", "");
        filePath = filePath.replace("\\\\", "\\");
        return filePath;
    }
    
    public String getFileNameWithoutExt(final File file) {
        final String fileNameWithOutExt = FilenameUtils.removeExtension(file.getName());
        return fileNameWithOutExt;
    }
    
    public void addToProcessingLogHashMap(final String tableName, final String key, final String value) {
        final HashMap fileTimeHash = this.getProcessingHashMap(tableName);
        fileTimeHash.put(key, value);
    }
    
    private HashMap getProcessingHashMap(final String tableName) {
        if (this.processingTimeHash.get(tableName) == null) {
            final HashMap fileProcessingHash = new HashMap();
            fileProcessingHash.put("TableName", tableName);
            this.processingTimeHash.put(tableName, fileProcessingHash);
        }
        return this.processingTimeHash.get(tableName);
    }
    
    public void addProcessingLog(final String tableName, final Long probeID, final long moduleID, final int statusCode, final String overAllProcessingTime) {
        this.addToProcessingLogHashMap(tableName, "SyncStatus", String.valueOf(statusCode));
        this.addToProcessingLogHashMap(tableName, "OverAllProcessingTime", overAllProcessingTime);
        this.addToProcessingLogHashMap(tableName, "ProbeName", ProbeMgmtFactoryProvider.getProbeDetailsAPI().getProbeName(probeID));
        final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
        final String moduleName = syncModuleMetaDAOUtil.getModuleName(moduleID);
        this.addToProcessingLogHashMap(tableName, "Module", moduleName);
        getInstance().doProcessingLogEntries(this.getProcessingHashMap(tableName));
        this.processingTimeHash.remove(tableName);
    }
    
    private void doProcessingLogEntries(final HashMap fileProcessingTimeHash) {
        try {
            final int len1 = 9;
            final int len2 = 15;
            final int len3 = 20;
            final int len4 = 8;
            final int len5 = 6;
            final int len6 = 12;
            final int preProcessLen = 14;
            final int conflictResolveLen = 19;
            final int len7 = 12;
            final int postProcessLen = 15;
            final int len8 = 9;
            final int gapLength1 = 25;
            final int gapLength2 = 1;
            final String fileName = fileProcessingTimeHash.getOrDefault("TableName", "-");
            final String probeName = fileProcessingTimeHash.getOrDefault("ProbeName", "-");
            final String module = fileProcessingTimeHash.getOrDefault("Module", "-");
            final String recordCount = fileProcessingTimeHash.getOrDefault("RecordCount", "0");
            final String syncStatus = fileProcessingTimeHash.getOrDefault("SyncStatus", "-");
            final String preProcessingTime = fileProcessingTimeHash.getOrDefault("PreProcessTime", "0");
            final String conflictResolveTime = fileProcessingTimeHash.getOrDefault("ConflictResolveTime", "0");
            final String bulkLoadTime = fileProcessingTimeHash.getOrDefault("BulkLoadTime", "0");
            final String cacheAndDOUpdateTime = fileProcessingTimeHash.getOrDefault("CacheAndDoUpdateTime", "0");
            final String postProcessingTime = fileProcessingTimeHash.getOrDefault("PostProcessTime", "0");
            final String overAllProcessingTime = fileProcessingTimeHash.getOrDefault("OverAllProcessingTime", "0");
            this.doProcessingFileHandling();
            String printstr = fileName;
            printstr = this.fillGaps(printstr, len1 - fileName.length() + gapLength1);
            printstr += probeName;
            printstr = this.fillGaps(printstr, len2 - probeName.length() + gapLength2);
            printstr += module;
            printstr = this.fillGaps(printstr, len3 - module.length() + gapLength2);
            printstr += recordCount;
            printstr = this.fillGaps(printstr, len4 - recordCount.length() + gapLength2);
            printstr += syncStatus;
            printstr = this.fillGaps(printstr, len5 - syncStatus.length() + gapLength2);
            printstr += preProcessingTime;
            printstr = this.fillGaps(printstr, preProcessLen - preProcessingTime.length() + gapLength2);
            printstr += conflictResolveTime;
            printstr = this.fillGaps(printstr, conflictResolveLen - conflictResolveTime.length() + gapLength2);
            printstr += bulkLoadTime;
            printstr = this.fillGaps(printstr, len6 - bulkLoadTime.length() + gapLength2);
            printstr += cacheAndDOUpdateTime;
            printstr = this.fillGaps(printstr, len7 - cacheAndDOUpdateTime.length() + gapLength2);
            printstr += postProcessingTime;
            printstr = this.fillGaps(printstr, postProcessLen - postProcessingTime.length() + gapLength2);
            printstr += overAllProcessingTime;
            printstr = this.fillGaps(printstr, len8 - overAllProcessingTime.length() + gapLength2);
            SyncUtil.processingLogger.log(Level.INFO, printstr);
        }
        catch (final Exception ex) {
            SyncUtil.logger.log(Level.WARNING, "Exception occurred while entering processing log entries ", ex);
        }
    }
    
    private String fillGaps(String strVal, final int gapLength) {
        try {
            for (int i = 0; i < gapLength; ++i) {
                strVal += " ";
            }
        }
        catch (final Exception ex) {
            SyncUtil.logger.log(Level.WARNING, "Exception occurred while filling gaps for access logs.. ", ex);
        }
        return strVal;
    }
    
    private void doProcessingFileHandling() {
        final String sourceMethod = "doProcessingFileHandling";
        try {
            boolean bRet = false;
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String str = serverHome + "\\logs\\SummarySyncProcessing_Log0.txt";
            final File file = new File(str);
            if (file.exists()) {
                final FileReader fr = new FileReader(file);
                final LineNumberReader ln = new LineNumberReader(fr);
                int count = 0;
                while (ln.readLine() != null) {
                    if (count >= 2) {
                        bRet = true;
                        break;
                    }
                    ++count;
                }
                ln.close();
                fr.close();
            }
            else {
                bRet = false;
            }
            if (!bRet) {
                final String line1 = "TableName                         ProbeName       Module               RowCount Status PreProcess(ms) ConflictResolve(ms) BulkLoad(ms) DOUpdate(ms) PostProcess(ms) Total(ms)";
                final String line2 = "---------                         ---------       ------               -------- ------ -------------- ------------------- ------------ ------------ --------------- ---------";
                SyncUtil.processingLogger.log(Level.INFO, line1);
                SyncUtil.processingLogger.log(Level.INFO, line2);
            }
        }
        catch (final Exception ex) {
            SyncUtil.logger.log(Level.WARNING, "Exception in processing log file handling ", ex);
        }
    }
    
    public void doAccessLogEntries(final String ip, final String probeName, final String moduleID, final String fileName) {
        try {
            final int len1 = 6;
            final int len2 = 8;
            final int len3 = 20;
            final int len4 = 8;
            final int gapLen1 = 12;
            final int gapLen2 = 6;
            String printstr = probeName;
            printstr = this.fillGaps(printstr, len2 - probeName.length() + gapLen1);
            printstr += moduleID;
            printstr = this.fillGaps(printstr, len3 - moduleID.length() + gapLen1);
            printstr += ip;
            printstr = this.fillGaps(printstr, len1 - ip.length() + gapLen1);
            printstr += fileName;
            printstr = this.fillGaps(printstr, len4 - fileName.length() + gapLen2);
            SyncUtil.accessLogger.log(Level.INFO, printstr);
        }
        catch (final Exception ex) {
            SyncUtil.logger.log(Level.WARNING, "Exception occurred while entering access log entries ", ex);
        }
    }
    
    public void updateAuditOnSyncFileReceivedFromProbe(final long probeID, final long moduleID, final long syncTime, final String fileName) {
        try {
            final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
            final Long moduleAuditID = syncModuleAuditDAOUtil.incrementFilesSentFromProbeCount(probeID, moduleID, syncTime);
            final SyncFileAuditDAOUtil syncFileAuditDAOUtil = new SyncFileAuditDAOUtil();
            syncFileAuditDAOUtil.addOrUpdateSyncFileStatus(moduleAuditID, fileName, 950002);
            final SummarySyncModuleDataDAOUtil summarySyncModuleDataDAOUtil = new SummarySyncModuleDataDAOUtil();
            summarySyncModuleDataDAOUtil.addOrUpdateLastSyncTime(probeID, moduleID, syncTime);
        }
        catch (final DataAccessException e) {
            SyncUtil.logger.log(Level.SEVERE, "Exception while updateAuditOnSyncFileReceivedFromProbe", (Throwable)e);
        }
    }
    
    public void updateSyncFileAudit(final long probeID, final long moduleID, final long syncTime, final String syncFileName, final int statusCode) {
        final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
        final long moduleAuditID = syncModuleAuditDAOUtil.getModuleAuditID(probeID, moduleID, syncTime);
        if (moduleAuditID != -1L) {
            final SyncFileAuditDAOUtil syncFileAuditDAOUtil = new SyncFileAuditDAOUtil();
            syncFileAuditDAOUtil.addOrUpdateSyncFileStatus(moduleAuditID, syncFileName, statusCode);
        }
    }
    
    public void updateStatusOnLastFile(final long probeID, final long moduleID, final long syncTime) {
        final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
        syncModuleAuditDAOUtil.updateModuleSyncStatus(probeID, moduleID, syncTime);
        final SummarySyncStatusUpdater statusUpdater = new SummarySyncStatusUpdater();
        statusUpdater.postModuleSyncStatus(probeID, moduleID, syncTime);
        final int syncStatus = syncModuleAuditDAOUtil.getModuleSyncStatus(probeID, moduleID, syncTime);
        if (syncStatus == 1) {
            final SummarySyncModuleDataDAOUtil summarySyncModuleDataDAOUtil = new SummarySyncModuleDataDAOUtil();
            summarySyncModuleDataDAOUtil.addOrUpdateLastSuccessfulSyncTime(probeID, moduleID, syncTime);
        }
    }
    
    @Deprecated
    public List<DeleteQuery> getDeleteQuery(final Long moduleID, final String table, final List pkColumns, final List<ArrayList> ids) {
        final List<DeleteQuery> deleteQueryList = new ArrayList<DeleteQuery>();
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(table);
        Criteria overallDeleteCriteria = new Criteria();
        Criteria deleteCriteria = new Criteria();
        final int primaryKeyCnt = pkColumns.size();
        if (!ids.isEmpty()) {
            final int idsCount = ids.get(0).size();
            int currIndex = 0;
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            for (int batchDeleteLimit = syncModuleMetaDAOUtil.getBatchLimit(moduleID); currIndex < idsCount; currIndex += batchDeleteLimit) {
                final int startIndex = currIndex;
                final int endIndex = Math.min(currIndex + batchDeleteLimit, idsCount);
                final List<ArrayList> batchIdsToDelete = new ArrayList<ArrayList>();
                for (final ArrayList arrayList : ids) {
                    arrayList.subList(startIndex, endIndex);
                    batchIdsToDelete.add(arrayList);
                }
                if (primaryKeyCnt == 1) {
                    deleteCriteria = new Criteria(new Column(table, pkColumns.get(0).toString()), (Object)batchIdsToDelete.get(0).toArray(), 8);
                    deleteQuery.setCriteria(deleteCriteria);
                }
                else {
                    for (int itr = 0; itr < ids.get(0).size(); ++itr) {
                        for (int index = 0; index < primaryKeyCnt; ++index) {
                            if (index == 0) {
                                deleteCriteria = new Criteria(new Column(table, pkColumns.get(index).toString()), batchIdsToDelete.get(index).get(itr), 0);
                            }
                            else {
                                deleteCriteria = deleteCriteria.and(new Criteria(Column.getColumn(table, pkColumns.get(index).toString()), batchIdsToDelete.get(index).get(itr), 0));
                            }
                        }
                        if (itr != 0) {
                            overallDeleteCriteria = overallDeleteCriteria.or(deleteCriteria);
                        }
                        else {
                            overallDeleteCriteria = deleteCriteria;
                        }
                    }
                    deleteQuery.setCriteria(overallDeleteCriteria);
                }
                SyMLogger.info(SyncUtil.logger, SyncUtil.sourceClass, "getDeleteQuery", "Delete Query for Data Sync: " + deleteQuery);
                deleteQueryList.add(deleteQuery);
            }
        }
        return deleteQueryList;
    }
    
    @Deprecated
    public void setConflictResolution(final String key, final List fieldNames) {
        SyncUtil.conflictResolution.put(key, fieldNames);
    }
    
    @Deprecated
    public List<DeleteQuery> resolveConflicts(final String key, final List fieldNamesList, final List fieldValuesMappingList) {
        try {
            final List resolvedFieldValuesMappingList = new ArrayList();
            final DataObject syncMetaDataDO = (DataObject)ApiFactoryProvider.getCacheAccessAPI().getCache("SyncMetaData", 2);
            if (syncMetaDataDO != null) {
                final Column col = Column.getColumn("SyncMetaData", "SS_TABLE_NAME");
                final Criteria criteria = new Criteria(col, (Object)key, 0, false);
                final Row row = syncMetaDataDO.getRow("SyncMetaData", criteria);
                if (row != null) {
                    final String conflictResolve = row.get("IS_CONFLICT_DATA").toString();
                    if (conflictResolve != null && conflictResolve.equalsIgnoreCase("true")) {
                        for (int index = 0; index < fieldNamesList.size(); ++index) {
                            resolvedFieldValuesMappingList.add(index, this.getFromMappingTable(fieldNamesList.get(index).toString(), fieldValuesMappingList.get(index)));
                        }
                        SyncUtil.logger.log(Level.INFO, "Conflict resolved and returning the update fieldValuesMappingList");
                        return this.getDeleteQuery(1L, key, fieldNamesList, resolvedFieldValuesMappingList);
                    }
                    SyncUtil.logger.log(Level.INFO, "Conflict not resolved and returning the same fieldValuesMappingList");
                }
                else {
                    SyncUtil.logger.log(Level.INFO, "Conflict not resolved and returning the same fieldValuesMappingList");
                }
            }
        }
        catch (final Exception e) {
            SyncUtil.logger.log(Level.SEVERE, "Exception in resolveConflicts: ", e);
        }
        return this.getDeleteQuery(1L, key, fieldNamesList, fieldValuesMappingList);
    }
    
    @Deprecated
    private List getFromMappingTable(final String fieldName, final List fieldValues) {
        final List resolvedList = new ArrayList();
        final List resolutionFields = SyncUtil.conflictResolution.get(fieldName);
        if (resolutionFields == null) {
            return fieldValues;
        }
        final String table = resolutionFields.get(0).toString().split("\\.")[0];
        final String summaryColName = resolutionFields.get(0).toString().split("\\.")[1];
        final String probeColName = resolutionFields.get(1).toString().split("\\.")[1];
        try {
            final Column column = Column.getColumn(table, probeColName);
            final Criteria criteria = new Criteria(column, (Object)fieldValues.toArray(), 8, false);
            final DataObject resultDO = DataAccess.get(table, criteria);
            final Iterator itr = resultDO.getRows(table);
            while (itr.hasNext()) {
                final Row row = itr.next();
                resolvedList.add(row.get(summaryColName));
            }
            SyncUtil.logger.log(Level.INFO, "UnResolved list: " + fieldValues);
            SyncUtil.logger.log(Level.INFO, "Resolved list: " + resolvedList);
            return resolvedList;
        }
        catch (final Exception e) {
            SyncUtil.logger.log(Level.INFO, "Error while fetching from Mapping table ... ");
            return null;
        }
    }
    
    static {
        SyncUtil.conflictResolution = new HashMap<String, List<String>>();
        SyncUtil.syncUtil = null;
        SyncUtil.sourceClass = "SyncUtil";
        SyncUtil.logger = Logger.getLogger("SummarySyncLogger");
        SyncUtil.processingLogger = Logger.getLogger("SummaryProcessingLogger");
        SyncUtil.accessLogger = Logger.getLogger("SummarySyncAccessLogger");
    }
}
