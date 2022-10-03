package com.me.ems.summaryserver.probe.sync.utils;

import java.util.Collection;
import org.json.JSONArray;
import com.me.devicemanagement.framework.utils.JsonUtils;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.apache.commons.io.FilenameUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.me.ems.summaryserver.common.sync.utils.SyncMetaDataDAOUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.util.QueryUtil;
import com.me.ems.summaryserver.probe.sync.SyncData;
import java.util.List;
import java.util.logging.Logger;

public class SyncDataCreator
{
    private static String sourceClass;
    private static Logger logger;
    private static Logger dataCollectionLogger;
    private static final String SERVER_HOME;
    private static final String SYNC_FOLDER;
    
    public List<SyncData> getCSVWrittenSyncData(final long moduleID, final long sqlId, final long lastSuccessfulSyncTime, final long currSyncLockedTime) throws Exception {
        final SelectQuery selectQuery = QueryUtil.getSelectQuery(sqlId);
        this.applySyncCriteriaToSelectQuery(moduleID, selectQuery, lastSuccessfulSyncTime, currSyncLockedTime);
        return this.getResultantSyncData(moduleID, 1, sqlId, selectQuery, currSyncLockedTime);
    }
    
    public List<SyncData> getJSONWrittenSyncDeleteData(final long moduleID, final long lastSuccessfulSyncTime, final long currSyncLockedTime) throws Exception {
        final String sourceMethod = "getJSONWrittenSyncDeleteData";
        final ProbeSyncModuleMetaDAOUtil probeSyncModuleMetaDAOUtil = new ProbeSyncModuleMetaDAOUtil();
        final String deletionAuditTable = probeSyncModuleMetaDAOUtil.getDeletionTable(moduleID);
        if (deletionAuditTable == null) {
            SyMLogger.warning(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "DeletionAuditTable for moduleID : {0} is null, hence returning", moduleID);
            return null;
        }
        final SelectQuery deletionAuditQuery = (SelectQuery)new SelectQueryImpl(new Table(deletionAuditTable));
        final Join syncMetaJoin = new Join(deletionAuditTable, "SyncMetaData", new String[] { "PROBE_TABLE_NAME" }, new String[] { "PROBE_TABLE_NAME" }, 2);
        deletionAuditQuery.addJoin(syncMetaJoin);
        final Criteria timeCriteria = this.getSyncTimeCriteria(deletionAuditTable, "LAST_UPDATED_TIME", lastSuccessfulSyncTime, currSyncLockedTime);
        deletionAuditQuery.setCriteria(timeCriteria);
        deletionAuditQuery.addSelectColumn(new Column("SyncMetaData", "SS_TABLE_PK_COLUMN"));
        deletionAuditQuery.addSelectColumn(new Column("SyncMetaData", "SS_TABLE_NAME"));
        deletionAuditQuery.addSelectColumn(new Column(deletionAuditTable, "PROBE_TABLE_PK_VALUE"));
        final Column sortColumnName = Column.getColumn(deletionAuditTable, "PROBE_TABLE_NAME");
        deletionAuditQuery.addSelectColumn(sortColumnName);
        deletionAuditQuery.addSelectColumn(new Column(deletionAuditTable, "LAST_UPDATED_TIME"));
        final SortColumn sortColumn = new SortColumn(sortColumnName, true);
        deletionAuditQuery.addSortColumn(sortColumn);
        deletionAuditQuery.setRange(this.getDefaultRangeForSelectQuery());
        SyncUtil.getInstance().checkAndClearInvalidDeleteEntries(deletionAuditTable, deletionAuditQuery);
        return this.getResultantSyncData(moduleID, 2, -1L, deletionAuditQuery, currSyncLockedTime);
    }
    
    private void applySyncCriteriaToSelectQuery(final long moduleID, final SelectQuery selectQuery, final long lastSuccessfulSyncTime, final long currSyncLockedTime) throws Exception {
        final String sourceMethod = "applySyncCriteriaToSelectQuery";
        Criteria overallCriteria = null;
        int sortIndex = 0;
        final List<Table> probeTables = selectQuery.getTableList();
        for (final Table probeTable : probeTables) {
            final String probeTableName = probeTable.getTableName();
            final SyncMetaDataDAOUtil syncMetaDataDAOUtil = new SyncMetaDataDAOUtil();
            final Row syncMetaDataRow = syncMetaDataDAOUtil.getSyncMetaDataRow(moduleID, "PROBE_TABLE_NAME", probeTableName);
            if (syncMetaDataRow != null) {
                final Criteria timeFieldCriteria = this.getTimeCriteriaForProbeTable(syncMetaDataRow, probeTableName, lastSuccessfulSyncTime, currSyncLockedTime);
                if (timeFieldCriteria != null) {
                    overallCriteria = ((overallCriteria == null) ? timeFieldCriteria : overallCriteria.or(timeFieldCriteria));
                }
                final List<SortColumn> probeTableSortColumn = this.getSortColumnForProbeTable(syncMetaDataRow, probeTableName);
                if (probeTableSortColumn == null) {
                    continue;
                }
                for (final SortColumn sortColumn : probeTableSortColumn) {
                    selectQuery.addSortColumn(sortColumn, sortIndex++);
                }
            }
            else {
                SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Skipping  {0} , since it is not a SyncMetaData probe table for module {1}", new Object[] { probeTableName, moduleID });
            }
        }
        if (overallCriteria != null) {
            final Criteria existingCriteria = selectQuery.getCriteria();
            overallCriteria = ((existingCriteria != null) ? existingCriteria.and(overallCriteria) : overallCriteria);
            selectQuery.setCriteria(overallCriteria);
        }
    }
    
    private Criteria getTimeCriteriaForProbeTable(final Row syncMetaDataRow, final String probeTableName, final long lastSuccessfulSyncTime, final long currSyncLockedTime) throws Exception {
        final String sourceMethod = "getTimeCriteriaForProbeTable";
        String timeField = String.valueOf(syncMetaDataRow.get("TIME_FIELD"));
        if (timeField == null || timeField.isEmpty()) {
            timeField = "DB_UPDATED_TIME";
        }
        Criteria timeFieldCriteria = this.getSyncTimeCriteria(probeTableName, timeField, lastSuccessfulSyncTime, currSyncLockedTime);
        final String addedTimeField = "DB_ADDED_TIME";
        final TableDefinition tblDef = MetaDataUtil.getTableDefinitionByName(probeTableName);
        final List<String> columnNames = tblDef.getColumnNames();
        if (columnNames.contains(addedTimeField)) {
            final Criteria dbAddedTimeCri = this.getSyncTimeCriteria(probeTableName, addedTimeField, lastSuccessfulSyncTime, currSyncLockedTime);
            timeFieldCriteria = timeFieldCriteria.or(dbAddedTimeCri);
        }
        SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Time Criteria added for table: {0}", probeTableName);
        return timeFieldCriteria;
    }
    
    private Criteria getSyncTimeCriteria(final String tableName, final String timeFieldColumnName, final long lastSuccessfulSyncTime, final long currSyncLockedTime) {
        final Column timeFieldColumn = new Column(tableName, timeFieldColumnName);
        Criteria timeFieldCriteria = new Criteria(timeFieldColumn, (Object)lastSuccessfulSyncTime, 4);
        timeFieldCriteria = timeFieldCriteria.and(timeFieldColumn, (Object)currSyncLockedTime, 7);
        return timeFieldCriteria;
    }
    
    private List<SortColumn> getSortColumnForProbeTable(final Row syncMetaDataRow, final String probeTableName) throws Exception {
        List<SortColumn> sortColumnList = null;
        final String orderByField = String.valueOf(syncMetaDataRow.get("PROBE_TABLE_SORT_COLUMN"));
        if (orderByField != null && !orderByField.equals("-")) {
            sortColumnList = new ArrayList<SortColumn>();
            if (orderByField.contains("*")) {
                final String[] split;
                final String[] orderColumns = split = orderByField.split("\\*");
                for (final String orderColumn : split) {
                    final SortColumn sortColumn = new SortColumn(probeTableName, orderColumn, true);
                    sortColumnList.add(sortColumn);
                }
            }
            else {
                final SortColumn sortColumn2 = new SortColumn(probeTableName, orderByField, true);
                sortColumnList.add(sortColumn2);
            }
        }
        return sortColumnList;
    }
    
    private List<SyncData> getResultantSyncData(final long moduleID, final int fileType, final long sqlId, final SelectQuery selectQuery, final long currSyncLockedTime) throws Exception {
        final String sourceMethod = "getResultantSyncData";
        final List<SyncData> syncDataList = new ArrayList<SyncData>();
        final RelationalAPI relationalAPI = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        int currIndex = 1;
        int fileCount = 1;
        boolean rowsToBeFetched = false;
        try {
            conn = relationalAPI.getConnection();
            final Range defaultRange = this.getDefaultRangeForSelectQuery();
            final Range queryRange = selectQuery.getRange();
            final boolean isCustomRange = !queryRange.equals((Object)defaultRange);
            SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "The Summary Server Table query is: {0}", selectQuery);
            final String currSyncFolder = this.getFolderPathForCurrSync(moduleID, currSyncLockedTime);
            String fileName = this.getSyncFileName(fileType, sqlId, fileCount++);
            final String tableName = this.getTableNameFromSyncFileName(fileName);
            String filePath = currSyncFolder + fileName;
            if (isCustomRange) {
                SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Query running with custom range, batch not applied : {0}", queryRange);
                ds = relationalAPI.executeQuery((Query)selectQuery, conn);
                if (ds.next()) {
                    SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Returning the data set successfully...for {0}", fileName);
                    final int rowsWritten = this.writeDataSetIntoFile(moduleID, fileType, ds, filePath);
                    if (rowsWritten > 0) {
                        SyMLogger.info(SyncDataCreator.dataCollectionLogger, SyncDataCreator.sourceClass, sourceMethod, "Written file successfully: {0} Rows written : {1}", new Object[] { fileName, rowsWritten });
                        final SyncData syncData = this.getSyncDataObject(currSyncLockedTime, fileName, filePath, tableName, fileType, sqlId, moduleID);
                        syncDataList.add(syncData);
                    }
                }
            }
            else {
                final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
                final int batchLimit = syncModuleMetaDAOUtil.getBatchLimit(moduleID);
                final int recordLimit = syncModuleMetaDAOUtil.getRecordLimit(moduleID);
                int totalRowsWritten = 0;
                do {
                    this.addRangeToSelectQuery(selectQuery, currIndex, batchLimit);
                    SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "select query with range : {0}, {1}", new Object[] { currIndex, batchLimit });
                    ds = relationalAPI.executeQuery((Query)selectQuery, conn);
                    if (ds.next()) {
                        SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Returning the data set successfully...for {0} ", fileName);
                        final boolean isRecordLimitReached = (fileType == 1) ? (totalRowsWritten > 0 && totalRowsWritten == recordLimit) : (totalRowsWritten > 0);
                        if (isRecordLimitReached) {
                            SyMLogger.info(SyncDataCreator.dataCollectionLogger, SyncDataCreator.sourceClass, sourceMethod, "Written file successfully: {0} , Rows written : ", new Object[] { fileName, totalRowsWritten });
                            final SyncData syncData2 = this.getSyncDataObject(currSyncLockedTime, fileName, filePath, tableName, fileType, sqlId, moduleID);
                            syncDataList.add(syncData2);
                            fileName = this.getSyncFileName(fileType, sqlId, fileCount++);
                            filePath = currSyncFolder + fileName;
                            totalRowsWritten = 0;
                        }
                        final int rowsWritten2 = this.writeDataSetIntoFile(moduleID, fileType, ds, filePath);
                        totalRowsWritten += rowsWritten2;
                        SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "DS Fetched size: {0}, Batch limit : {1}", new Object[] { rowsWritten2, batchLimit });
                        rowsToBeFetched = (rowsWritten2 == batchLimit);
                        if (rowsWritten2 > 0 && !rowsToBeFetched) {
                            SyMLogger.info(SyncDataCreator.dataCollectionLogger, SyncDataCreator.sourceClass, sourceMethod, "Written file successfully: {0}, Rows written : ", new Object[] { fileName, totalRowsWritten });
                            final SyncData syncData3 = this.getSyncDataObject(currSyncLockedTime, fileName, filePath, tableName, fileType, sqlId, moduleID);
                            syncDataList.add(syncData3);
                        }
                        else {
                            currIndex += batchLimit;
                        }
                    }
                    else {
                        rowsToBeFetched = false;
                    }
                } while (rowsToBeFetched);
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
            catch (final SQLException e) {
                SyMLogger.error(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Exception while closing dataset or connection", e);
            }
        }
        return syncDataList;
    }
    
    public String getFolderPathForCurrSync(final long moduleID, final long currSyncLockedTime) {
        final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
        return SyncDataCreator.SYNC_FOLDER + File.separator + syncModuleMetaDAOUtil.getModuleName(moduleID) + File.separator + currSyncLockedTime + File.separator;
    }
    
    private String getSyncFileName(final int fileType, final long sqlId, final int fileCount) {
        String fileName = null;
        switch (fileType) {
            case 1: {
                fileName = ApiFactoryProvider.getRedisHashMap().get("SQL_ID" + sqlId, 2) + "--" + fileCount + ".csv";
                break;
            }
            case 2: {
                fileName = "Deletion_Info--" + fileCount + ".json";
                break;
            }
        }
        return fileName;
    }
    
    private String getTableNameFromSyncFileName(final String fileName) {
        return FilenameUtils.removeExtension(fileName).split("--")[0];
    }
    
    private Range getDefaultRangeForSelectQuery() {
        return new Range(0, 25);
    }
    
    private void addRangeToSelectQuery(final SelectQuery selectQuery, final int currIndex, final int batchLimit) {
        final String sourceMethod = "addRangeToSelectQuery";
        final Range currentRange = selectQuery.getRange();
        final Range defaultRange = this.getDefaultRangeForSelectQuery();
        if (currIndex != 1 || currentRange.equals((Object)defaultRange)) {
            final Range newRange = new Range(currIndex, batchLimit);
            selectQuery.setRange(newRange);
        }
        else {
            SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Query with custom range, hence batch range not applied {0}", currentRange);
        }
    }
    
    private int writeDataSetIntoFile(final long moduleID, final int fileType, final DataSet dataSet, final String fileName) throws Exception {
        switch (fileType) {
            case 1: {
                return this.convertDataSet2CSV(dataSet, fileName);
            }
            case 2: {
                return this.convertDataSet2JSON(moduleID, dataSet, fileName);
            }
            default: {
                return 0;
            }
        }
    }
    
    private int convertDataSet2CSV(final DataSet ds, final String csvFileName) throws Exception {
        final String sourceMethod = "convertDataSet2CSV";
        PrintWriter pw = null;
        int rowsWritten = 0;
        try {
            final int numberOfCols = ds.getColumnCount();
            SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "File Name : {0}", csvFileName);
            SyMLogger.debug(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "The total number of columns in the result: {0}", numberOfCols);
            final File csvFile = new File(csvFileName);
            if (csvFile.exists()) {
                pw = new PrintWriter(new BufferedWriter(new FileWriter(csvFileName, true)));
            }
            else {
                final boolean fileCreated = ApiFactoryProvider.getFileAccessAPI().createNewFile(csvFileName);
                if (!fileCreated) {
                    throw new Exception("New CSV File creation failed! for " + csvFileName);
                }
                pw = new PrintWriter(new BufferedWriter(new FileWriter(csvFileName)));
                for (int index = 1; index <= numberOfCols; ++index) {
                    final String columnName = ds.getColumnName(index);
                    SyMLogger.debug(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "column_{0} : {1}", new Object[] { index, columnName });
                    pw.write(columnName);
                    if (index < numberOfCols) {
                        pw.write(",");
                    }
                }
                pw.write("\n");
            }
            do {
                for (int index2 = 1; index2 <= numberOfCols; ++index2) {
                    final String currFieldValue = String.valueOf(ds.getValue(index2));
                    final String fieldValue = this.getNormalizedCurrFieldValue(currFieldValue);
                    SyMLogger.debug(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "currFieldValue : {0}, fieldValue : {1}", new Object[] { currFieldValue, fieldValue });
                    pw.write(fieldValue);
                    if (index2 < numberOfCols) {
                        pw.write(",");
                    }
                }
                pw.write("\n");
                ++rowsWritten;
            } while (ds.next());
        }
        finally {
            if (pw != null) {
                pw.close();
            }
        }
        return rowsWritten;
    }
    
    private String getNormalizedCurrFieldValue(final String currFieldValue) {
        String fieldValue = currFieldValue;
        if (currFieldValue == null || currFieldValue.equalsIgnoreCase("null")) {
            fieldValue = "*null*";
        }
        if (currFieldValue != null && currFieldValue.contains("\"")) {
            fieldValue = currFieldValue.replace("\"", "\"\"");
        }
        fieldValue = "\"" + fieldValue + "\"";
        return fieldValue;
    }
    
    private int convertDataSet2JSON(final long moduleID, final DataSet ds, final String jsonFileName) throws Exception {
        final String sourceMethod = "convertDataSet2JSON";
        PrintWriter pw = null;
        int rowsProcessed = 0;
        final int rowsWritten = 0;
        try {
            final int numberOfCols = ds.getColumnCount();
            SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "File Name : {0}", jsonFileName);
            SyMLogger.debug(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "The total number of columns in the result: {0}", numberOfCols);
            final File jsonFile = new File(jsonFileName);
            final JSONObject deleteJSON = new JSONObject();
            JSONObject existingJSON = null;
            if (jsonFile.exists()) {
                existingJSON = JsonUtils.loadJsonFile(jsonFile);
            }
            else {
                ApiFactoryProvider.getFileAccessAPI().createNewFile(jsonFileName);
            }
            String prevTable = "";
            ArrayList<ArrayList<Long>> pkValues = new ArrayList<ArrayList<Long>>();
            int numberOfPKColumns = 1;
            String[] pkCols = new String[5];
            String[] pkColValues = new String[5];
            String currSSTable;
            do {
                final String currSSPKColumn = String.valueOf(ds.getValue(1));
                currSSTable = String.valueOf(ds.getValue(2));
                final String currPKColumnValue = String.valueOf(ds.getValue(3));
                final String currTable = String.valueOf(ds.getValue(4));
                final String lastUpdatedTime = String.valueOf(ds.getValue(5));
                SyMLogger.debug(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "currSSPKColumn: {0}", currSSPKColumn);
                SyMLogger.debug(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "currSSTable: {0}", currSSTable);
                SyMLogger.debug(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "currPKColumnValue: {0}", currPKColumnValue);
                SyMLogger.debug(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "currTable: {0}", currTable);
                if (!currSSTable.equals(prevTable)) {
                    if (!prevTable.equals("")) {
                        final JSONArray prevTableArray = new JSONArray();
                        JSONArray existingPrevTableArray = null;
                        if (existingJSON != null && existingJSON.has(prevTable)) {
                            existingPrevTableArray = existingJSON.getJSONArray(prevTable);
                        }
                        for (int index = 0; index < numberOfPKColumns; ++index) {
                            final JSONObject currFieldObj = new JSONObject();
                            final String field = pkCols[index];
                            final ArrayList<Object> values = new ArrayList<Object>();
                            if (existingPrevTableArray != null) {
                                final JSONObject existingFieldObject = existingPrevTableArray.getJSONObject(index);
                                final JSONArray existingValues = existingFieldObject.getJSONArray("SS_FieldValues");
                                final List<Object> existingList = new ArrayList<Object>();
                                for (int i = 0; i < existingValues.length(); ++i) {
                                    existingList.add(existingValues);
                                }
                                if (!existingList.isEmpty()) {
                                    values.addAll(existingList);
                                }
                            }
                            values.addAll(pkValues.get(index));
                            currFieldObj.put("SS_Field", (Object)field);
                            currFieldObj.put("SS_FieldValues", (Collection)values);
                            prevTableArray.put((Object)currFieldObj);
                        }
                        deleteJSON.put(prevTable, (Object)prevTableArray);
                    }
                    pkValues = new ArrayList<ArrayList<Long>>();
                    pkCols = new String[5];
                    pkColValues = new String[5];
                    SyMLogger.debug(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Different table delete audit entry.So new params are set.Table name: {0}, {1}", new Object[] { currTable, currSSPKColumn });
                    if (currSSPKColumn.contains("*")) {
                        pkCols = currSSPKColumn.split("\\*");
                        numberOfPKColumns = pkCols.length;
                        pkValues = new ArrayList<ArrayList<Long>>();
                        for (int index2 = 0; index2 < numberOfPKColumns; ++index2) {
                            pkValues.add(index2, new ArrayList<Long>());
                        }
                    }
                    else {
                        pkValues.add(new ArrayList<Long>());
                        pkCols[0] = currSSPKColumn;
                        numberOfPKColumns = 1;
                    }
                }
                else {
                    SyMLogger.debug(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Same table delete audit entry...{0}", currSSTable);
                }
                if (currPKColumnValue.contains("*")) {
                    pkColValues = currPKColumnValue.split("\\*");
                }
                else {
                    pkColValues[0] = currPKColumnValue;
                }
                for (int index2 = 0; index2 < numberOfPKColumns; ++index2) {
                    pkValues.get(index2).add(Long.parseLong(pkColValues[index2]));
                }
                ++rowsProcessed;
                prevTable = currSSTable;
            } while (ds.next());
            SyMLogger.debug(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Final table entries...{0}", currSSTable);
            if (!pkValues.isEmpty() && !pkValues.get(0).isEmpty()) {
                final JSONArray currTableArray = new JSONArray();
                JSONArray existingCurrTableArray = null;
                if (existingJSON != null && existingJSON.has(currSSTable)) {
                    existingCurrTableArray = existingJSON.getJSONArray(currSSTable);
                }
                for (int index = 0; index < numberOfPKColumns; ++index) {
                    final JSONObject currFieldObj = new JSONObject();
                    final String field = pkCols[index];
                    final ArrayList<Object> values = new ArrayList<Object>();
                    if (existingCurrTableArray != null) {
                        final JSONObject existingFieldObject = existingCurrTableArray.getJSONObject(index);
                        final JSONArray existingValues = existingFieldObject.getJSONArray("SS_FieldValues");
                        final List<Object> existingList = new ArrayList<Object>();
                        for (int i = 0; i < existingValues.length(); ++i) {
                            existingList.add(existingValues);
                        }
                        if (!existingList.isEmpty()) {
                            values.addAll(existingList);
                        }
                    }
                    values.addAll(pkValues.get(index));
                    currFieldObj.put("SS_Field", (Object)field);
                    currFieldObj.put("SS_FieldValues", (Collection)values);
                    currTableArray.put((Object)currFieldObj);
                }
                deleteJSON.put(currSSTable, (Object)currTableArray);
            }
            if (deleteJSON.length() > 0) {
                pw = new PrintWriter(new BufferedWriter(new FileWriter(jsonFileName)));
                pw.write(deleteJSON.toString());
                SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Deletion JSON File created is: {0}", deleteJSON);
                SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Rows written : {0}", rowsWritten);
            }
            else {
                SyMLogger.info(SyncDataCreator.logger, SyncDataCreator.sourceClass, sourceMethod, "Empty JSON - {0}, All entries rejected on check for valid!", moduleID);
            }
        }
        finally {
            if (pw != null) {
                pw.close();
            }
        }
        return rowsProcessed;
    }
    
    private SyncData getSyncDataObject(final long syncTime, final String fileName, final String filePath, final String tableName, final int fileType, final long sqlId, final long moduleID) {
        final SyncData syncData = new SyncData();
        syncData.syncType = 1;
        syncData.syncTime = syncTime;
        syncData.fileName = fileName;
        syncData.tableName = tableName;
        syncData.fileLocation = filePath;
        syncData.fileType = fileType;
        syncData.sqlId = sqlId;
        syncData.moduleID = moduleID;
        return syncData;
    }
    
    public SyncData getSyncDataObjectForRetryFile(final long moduleID, final long syncTime, final String fileName) {
        final SyncData syncData = new SyncData();
        syncData.syncType = 1;
        syncData.syncTime = syncTime;
        syncData.fileName = fileName;
        syncData.tableName = this.getTableNameFromSyncFileName(fileName);
        syncData.moduleID = moduleID;
        syncData.fileType = (fileName.endsWith(".json") ? 2 : 1);
        final String currSyncFolder = this.getFolderPathForCurrSync(moduleID, syncTime);
        syncData.fileLocation = currSyncFolder + fileName;
        return syncData;
    }
    
    static {
        SyncDataCreator.sourceClass = "SyncDataCreator";
        SyncDataCreator.logger = Logger.getLogger("ProbeSyncLogger");
        SyncDataCreator.dataCollectionLogger = Logger.getLogger("ProbeDataCollectionLogger");
        SERVER_HOME = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        SYNC_FOLDER = SyncDataCreator.SERVER_HOME + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "sync";
    }
}
