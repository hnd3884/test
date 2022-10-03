package com.me.ems.summaryserver.summary.sync.utils;

import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.DeleteQuery;
import java.util.Collection;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.SQLException;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Arrays;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.ems.summaryserver.common.sync.SyncException;
import com.adventnet.persistence.Row;
import com.me.ems.summaryserver.common.sync.utils.ConflictResolutionMetaDataDAOUtil;
import java.util.HashMap;
import java.util.logging.Logger;

public class SyncDataProcessor
{
    private static final String sourceClass = "SyncDataProcessor";
    private static Logger logger;
    
    public HashMap<String, HashMap> getConflictResolutionMetaData(final String tableName, final long moduleID) throws SyncException, DataAccessException {
        final HashMap<String, HashMap> conflictMetaDataHash = new HashMap<String, HashMap>();
        final ConflictResolutionMetaDataDAOUtil conflictMetaUtil = new ConflictResolutionMetaDataDAOUtil();
        final DataObject conflictResolutionMeta = conflictMetaUtil.getConflictResolutionMetaData(moduleID, tableName);
        if (conflictResolutionMeta != null && !conflictResolutionMeta.isEmpty()) {
            final Iterator rows = conflictResolutionMeta.getRows("ConflictResolutionMetaData");
            while (rows.hasNext()) {
                final Row row = rows.next();
                final String ssFieldName = (String)row.get("SS_FIELD_NAME");
                final HashMap<String, Object> rowEntry = new HashMap<String, Object>();
                rowEntry.put("SS_RESOLUTION_KEY_FIELD", row.get("SS_RESOLUTION_KEY_FIELD"));
                rowEntry.put("SS_RESOLUTION_VALUE_FIELD", row.get("SS_RESOLUTION_VALUE_FIELD"));
                rowEntry.put("CREATE_MAPPING", row.get("CREATE_MAPPING"));
                rowEntry.put("UNIQUE_FIELDS", row.get("UNIQUE_FIELDS"));
                conflictMetaDataHash.put(ssFieldName, rowEntry);
            }
            return conflictMetaDataHash;
        }
        throw new SyncException(950501, tableName, "ConflictResolutionMeta Data not found");
    }
    
    private ArrayList<String> mapResolutionValue(final Long probeID, final String mappingTableName, final String mappingKeyField, final String mappingValueField, final ArrayList<String> pkColValues) throws DataAccessException {
        final Iterator iterator = pkColValues.iterator();
        final ArrayList<String> summaryPKValues = new ArrayList<String>();
        while (iterator.hasNext()) {
            final String probeValue = iterator.next();
            final Object valueFromDB = ApiFactoryProvider.getCacheAccessAPI().getCache(mappingTableName + "_" + probeValue, 2);
            if (valueFromDB != null) {
                final String summaryValue = String.valueOf(valueFromDB);
                summaryPKValues.add(summaryValue);
            }
        }
        return summaryPKValues;
    }
    
    public String getMappingTableName(final HashMap conflictFieldMeta) {
        final String keyField = conflictFieldMeta.get("SS_RESOLUTION_KEY_FIELD");
        final String[] keyFieldArr = keyField.split("\\.");
        return keyFieldArr[0];
    }
    
    public String getMappingKeyField(final HashMap conflictFieldMeta) {
        final String keyField = conflictFieldMeta.get("SS_RESOLUTION_KEY_FIELD");
        final String[] keyFieldArr = keyField.split("\\.");
        return keyFieldArr[1];
    }
    
    public String getMappingValueField(final HashMap conflictFieldMeta) {
        final String valueField = conflictFieldMeta.get("SS_RESOLUTION_VALUE_FIELD");
        final String[] valueFieldArr = valueField.split("\\.");
        return valueFieldArr[1];
    }
    
    public List<String> getUniqueFields(final HashMap conflictFieldMeta) {
        final String unique = conflictFieldMeta.get("UNIQUE_FIELDS");
        if (unique != null) {
            final String[] uniqueFields = unique.split(",");
            return Arrays.asList(uniqueFields);
        }
        return null;
    }
    
    public DataObject createMapping(final String mappingTableName, final String mappingKeyField, final String mappingValueField, final String probeValue, final Object summaryValue, final Long probeID, DataObject mappingDO) throws DataAccessException {
        try {
            mappingDO = ((mappingDO == null) ? SyMUtil.getPersistence().constructDataObject() : mappingDO);
            final Row mappingRow = new Row(mappingTableName);
            mappingRow.set(mappingKeyField, (Object)probeValue);
            mappingRow.set("PROBE_ID", (Object)probeID);
            mappingRow.set(mappingValueField, summaryValue);
            mappingDO.addRow(mappingRow);
        }
        catch (final DataAccessException e) {
            throw e;
        }
        return mappingDO;
    }
    
    public Long getMappingTableCacheValue(final String mappingTableName, final String mappingKeyField, final String mappingKeyValue, final String mappingValueField) throws Exception {
        final String sourceMethod = "getMappingTableCacheValue";
        Long mappingValue = null;
        final Object valueFromCache = ApiFactoryProvider.getCacheAccessAPI().getCache(mappingTableName + "_" + mappingKeyValue, 2);
        if (valueFromCache == null) {
            final Object valueFromDB = DBUtil.getValueFromDB(mappingTableName, mappingKeyField, mappingKeyValue, mappingValueField);
            if (valueFromDB != null) {
                mappingValue = (Long)valueFromDB;
                SyMLogger.info(SyncDataProcessor.logger, "SyncDataProcessor", sourceMethod, "Mapping value found from DB, and not in cache! Going to reset cache for " + mappingTableName);
                final DataObject tableDO = SyMUtil.getPersistence().get(mappingTableName, (Criteria)null);
                final Iterator iterator = tableDO.getRows(mappingTableName);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final String key = String.valueOf(row.get(mappingKeyField));
                    final Long value = (Long)row.get(mappingValueField);
                    ApiFactoryProvider.getCacheAccessAPI().putCache(mappingTableName + "_" + key, value, 2);
                }
            }
            else {
                SyMLogger.info(SyncDataProcessor.logger, "SyncDataProcessor", sourceMethod, "Mapping value not found from DB! Returning null");
            }
        }
        else {
            mappingValue = (Long)valueFromCache;
            SyMLogger.debug(SyncDataProcessor.logger, "SyncDataProcessor", sourceMethod, "Mapping value found from cache! Returning " + mappingValue);
        }
        return mappingValue;
    }
    
    public Long getTableValue(final String tableName, final String fieldName, final List<String> uniqueFields, final List<String> uniqueValues) {
        final String sourceMethod = "getTableValue";
        Long tableValue = null;
        Criteria criteria = null;
        try {
            for (int index = 0; index < uniqueFields.size(); ++index) {
                final Criteria subCri = new Criteria(new Column(tableName, (String)uniqueFields.get(index)), (Object)uniqueValues.get(index), 0);
                criteria = ((criteria == null) ? subCri : criteria.and(subCri));
            }
            final DataObject resultantDO = DataAccess.get(tableName, criteria);
            if (!resultantDO.isEmpty()) {
                tableValue = (Long)resultantDO.getFirstValue(tableName, fieldName);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncDataProcessor.logger, "SyncDataProcessor", sourceMethod, "Exception while getTableValue for " + tableName + "." + fieldName + ":" + uniqueValues, e);
        }
        return tableValue;
    }
    
    public void bulkLoadStagingTable(final String table, final String filename) throws Exception {
        ProbeMgmtFactoryProvider.getSummaryServerSyncAPI().bulkLoadStagingTable(table, filename);
    }
    
    public void bulkLoadStagingTable(final String tempTable, final String[] headers, final List<String[]> probeData) throws Exception {
        ProbeMgmtFactoryProvider.getSummaryServerSyncAPI().bulkLoadStagingTable(tempTable, headers, probeData);
    }
    
    public void truncateTable(final String table) throws SQLException {
        ProbeMgmtFactoryProvider.getSummaryServerSyncAPI().truncateTable(table);
    }
    
    public JSONObject convertDeletionStringToJSONObject(final String str) throws Exception {
        final String sourceMethod = "convertDeletionStringToJSONObject";
        try {
            if (str != null) {
                final JSONObject deletionData = new JSONObject(str);
                SyMLogger.info(SyncDataProcessor.logger, "SyncDataProcessor", sourceMethod, "Successfully converted the DeletionData to JSONObject");
                return deletionData;
            }
            SyMLogger.warning(SyncDataProcessor.logger, "SyncDataProcessor", sourceMethod, "Converted InventoryData is null");
            throw new Exception("Converted InputSource is null");
        }
        catch (final JSONException exp) {
            SyMLogger.warning(SyncDataProcessor.logger, "SyncDataProcessor", sourceMethod, "JSONException while converting data to JSONObject");
            throw exp;
        }
        catch (final Exception exp2) {
            SyMLogger.error(SyncDataProcessor.logger, "SyncDataProcessor", sourceMethod, "Exception while converting data to JSONObject", exp2);
            throw exp2;
        }
    }
    
    public Map<String, Object> extractFieldAndValuesFromJson(final JSONArray jsonArray) throws JSONException {
        final List<String> fieldNamesList = new ArrayList<String>();
        final List<ArrayList> fieldValuesMappingList = new ArrayList<ArrayList>();
        boolean isConflictResolve = false;
        for (int index = 0; index < jsonArray.length(); ++index) {
            final JSONObject jsonObj = (JSONObject)jsonArray.get(index);
            final String fieldName = this.getSSFieldName(jsonObj);
            fieldNamesList.add(fieldName);
            isConflictResolve = this.getConflictResolution(jsonObj);
            final ArrayList<String> fieldValues = this.getSSFieldValues(jsonObj);
            fieldValuesMappingList.add(fieldValues);
        }
        final Map result = new HashMap();
        result.put("fields", fieldNamesList);
        result.put("values", fieldValuesMappingList);
        result.put("Conflict", isConflictResolve);
        return result;
    }
    
    private String getSSFieldName(final JSONObject jsonObj) throws JSONException {
        return jsonObj.get("SS_Field").toString();
    }
    
    private ArrayList<String> getSSFieldValues(final JSONObject jsonObj) throws JSONException {
        String fieldValuesList = jsonObj.get("SS_FieldValues").toString();
        fieldValuesList = fieldValuesList.replace("[", "");
        fieldValuesList = fieldValuesList.replace("]", "");
        return new ArrayList<String>(Arrays.asList(fieldValuesList.split(",")));
    }
    
    private boolean getConflictResolution(final JSONObject jsonObj) throws JSONException {
        return jsonObj.has("IS_CONFLICT_DATA") && Boolean.parseBoolean(jsonObj.get("IS_CONFLICT_DATA").toString());
    }
    
    public int performDeletion(final ArrayList<DeleteQuery> deleteQueryList) throws Exception {
        final String sourceMethod = "performDeletion";
        int rowsDeleted = 0;
        try {
            for (final DeleteQuery deleteQuery : deleteQueryList) {
                rowsDeleted += SyMUtil.getPersistenceLite().delete(deleteQuery);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SyncDataProcessor.logger, "SyncDataProcessor", sourceMethod, "Exception while deletion: ", e);
            throw e;
        }
        return rowsDeleted;
    }
    
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
                SyMLogger.info(SyncDataProcessor.logger, "SyncDataProcessor", "getDeleteQuery", "Delete Query for Data Sync: " + deleteQuery);
                deleteQueryList.add(deleteQuery);
            }
        }
        return deleteQueryList;
    }
    
    public HashMap<String, HashMap> getDeleteDataFromJSON(final JSONObject deletionJSON) throws Exception {
        final HashMap<String, HashMap> deleteData = new HashMap<String, HashMap>();
        final Iterator<String> tables = deletionJSON.keys();
        while (tables.hasNext()) {
            final String tableName = tables.next();
            final HashMap<String, ArrayList<String>> tableData = new HashMap<String, ArrayList<String>>();
            final JSONArray tableJSON = (JSONArray)deletionJSON.get(tableName);
            for (int index = 0; index < tableJSON.length(); ++index) {
                final JSONObject fieldJSON = (JSONObject)tableJSON.get(index);
                final String ssFieldName = this.getSSFieldName(fieldJSON);
                final ArrayList<String> ssFieldValues = this.getSSFieldValues(fieldJSON);
                tableData.put(ssFieldName, ssFieldValues);
            }
            deleteData.put(tableName, tableData);
        }
        return deleteData;
    }
    
    public HashMap<String, ArrayList> getDeletionResolutionData(final Long probeID, final long moduleID, final HashMap<String, HashMap> conflictMetaData, final String tableName, final HashMap<String, ArrayList> tableDataMap) throws DataAccessException {
        final HashMap<String, ArrayList> summaryDataMap = new HashMap<String, ArrayList>();
        for (final Map.Entry entry : tableDataMap.entrySet()) {
            final String pkColName = entry.getKey();
            final ArrayList pkColValues = entry.getValue();
            if (conflictMetaData.containsKey(pkColName)) {
                final HashMap conflictFieldMeta = conflictMetaData.get(pkColName);
                final String mappingTableName = this.getMappingTableName(conflictFieldMeta);
                final String mappingKeyField = this.getMappingKeyField(conflictFieldMeta);
                final String mappingValueField = this.getMappingValueField(conflictFieldMeta);
                final ArrayList<String> resolvedData = this.mapResolutionValue(probeID, mappingTableName, mappingKeyField, mappingValueField, pkColValues);
                summaryDataMap.put(pkColName, resolvedData);
            }
        }
        return summaryDataMap;
    }
    
    static {
        SyncDataProcessor.logger = Logger.getLogger("SummarySyncLogger");
    }
}
