package com.me.devicemanagement.framework.server.deletionfw;

import java.util.Collections;
import java.util.HashMap;
import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;

public class DeletionFWParam
{
    public static final String OPERATION_TYPE = "OperationType";
    public static final String STATUS_KEY = "StatusKey";
    public static final String CHUNK_THRESHOLD = "ChunkThreshold";
    public static final String TABLE_NAME = "TableName";
    public static final String DELETE_DATA_DETAILS = "DeleteDataDetails";
    public static final String RETRY_COUNT = "RetryCount";
    public static final String COMPLETED_STATUS = "CompletedStatus";
    public static final String COMPLETED_STATUS_SHORT = "CS";
    public static final String QUERY = "Query";
    public static final String TABLE_LIST_PREFIX = "TableList_";
    public static final String NON_DUPLICATE_COLUMN_PREFIX = "NonDuplicateCol";
    public static final String EXECUTION_START_TIME = "EST";
    public static final String FAILURE_REASON = "FS";
    public static final String CLEANUP_DETAILS = "CUD";
    public static final String DELETION_DETAILS = "DD";
    public static final String DEPENDENT_EXECUTION_DELAY = "DED";
    public static final String TOTAL_DURATION = "TDUR";
    public static final String TOTAL_PARENT_ROWS_DELETED = "TPRD";
    public static final String TOTAL_CHILD_ROWS_DELETED = "TCRD";
    public static final String PARENT_DELETION_DURATION = "PDD";
    public static final String CHILD_DELETION_DURATION = "CDD";
    public static final String DELETION_GROUP_COUNT = "GC";
    public static final String PERSISTENCE_DELETION = "PD";
    public static final String ORPHAN_COUNT_JSON = "OCJ";
    public static final String ORPHAN_COUNT_LAST_MODIFIED = "OCLM";
    public static final String ORPHAN_COUNT_TIME_TAKEN = "OCTT";
    public static final String ORPHAN_COUNT_MAX_FOLD = "OCMF";
    public static final String ORPHAN_CLEANUP_DURATION = "OCLD";
    public static final String ORPHAN_CLEANUP_JSON = "OCLJ";
    public static final String ORPHAN_CLEANUP_MAX_FOLD = "OCLMF";
    public static final String ORPHAN_CLEANUP_LAST_EXECUTION_TIME = "OCLET";
    public static final String PRE_HANDLING_DATA = "PHD";
    public static final String CLEANUP_TASK = "CT";
    public static final String DELETION_TASK = "DT";
    public static final String ADDITION_PRE_HANDLING_TASK = "APT";
    public static final String ID_DETAILS = "IDET";
    public static final String METRACK_GENERATION_DURATION = "MGD";
    public static final String DELETION_RECURSIVE_COUNT = "DRC";
    public static final String ORPHAN_COUNT = "OC";
    public static final String ORPHAN_CLEANUP = "OCL";
    private static final Map<String, String> CONSTANT_NAME_MAP;
    
    public static Object replaceJsonKeys(final Object jsonObjectOrJsonArray) throws JSONException {
        if (jsonObjectOrJsonArray instanceof JSONObject) {
            final JSONObject jsonObject = (JSONObject)jsonObjectOrJsonArray;
            final JSONObject newJsonObject = new JSONObject();
            final Iterator<String> itr = jsonObject.keys();
            String key = null;
            while (itr.hasNext()) {
                key = itr.next();
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    value = replaceJsonKeys(value);
                }
                if (DeletionFWParam.CONSTANT_NAME_MAP.containsKey(key)) {
                    newJsonObject.put((String)DeletionFWParam.CONSTANT_NAME_MAP.get(key), value);
                }
                else {
                    newJsonObject.put(key, value);
                }
            }
            return newJsonObject;
        }
        if (jsonObjectOrJsonArray instanceof JSONArray) {
            final JSONArray jsonArray = (JSONArray)jsonObjectOrJsonArray;
            final JSONArray newJsonArray = new JSONArray();
            for (int i = 0; i < jsonArray.length(); ++i) {
                Object value2 = jsonArray.get(i);
                if (value2 instanceof JSONObject || value2 instanceof JSONArray) {
                    value2 = replaceJsonKeys(value2);
                }
                newJsonArray.put(i, value2);
            }
            return newJsonArray;
        }
        return jsonObjectOrJsonArray;
    }
    
    static {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("OperationType", "OPERATION_TYPE");
        map.put("StatusKey", "STATUS_KEY");
        map.put("ChunkThreshold", "CHUNK_THRESHOLD");
        map.put("TableName", "TABLE_NAME");
        map.put("DeleteDataDetails", "DELETE_DATA_DETAILS");
        map.put("RetryCount", "RETRY_COUNT");
        map.put("CompletedStatus", "COMPLETED_STATUS");
        map.put("CS", "COMPLETED_STATUS_SHORT");
        map.put("Query", "QUERY");
        map.put("TableList_", "TABLE_LIST_PREFIX");
        map.put("NonDuplicateCol", "NON_DUPLICATE_COLUMN_PREFIX");
        map.put("EST", "EXECUTION_START_TIME");
        map.put("FS", "FAILURE_REASON");
        map.put("CUD", "CLEANUP_DETAILS");
        map.put("DD", "DELETION_DETAILS");
        map.put("DED", "DEPENDENT_EXECUTION_DELAY");
        map.put("TDUR", "TOTAL_DURATION");
        map.put("TPRD", "TOTAL_PARENT_ROWS_DELETED");
        map.put("TCRD", "TOTAL_CHILD_ROWS_DELETED");
        map.put("PDD", "PARENT_DELETION_DURATION");
        map.put("CDD", "CHILD_DELETION_DURATION");
        map.put("SP", "DELETION_GROUP_COUNT_SINGLE_PK");
        map.put("CP", "DELETION_GROUP_COUNT_COMPOSITE_PK");
        map.put("PD", "PERSISTENCE_DELETION");
        map.put("OCJ", "ORPHAN_COUNT_JSON");
        map.put("OCLM", "ORPHAN_COUNT_LAST_MODIFIED");
        map.put("OCTT", "ORPHAN_COUNT_TIME_TAKEN");
        map.put("OCMF", "ORPHAN_COUNT_MAX_FOLD");
        map.put("OCLD", "ORPHAN_CLEANUP_DURATION");
        map.put("OCLJ", "ORPHAN_CLEANUP_JSON");
        map.put("OCLMF", "ORPHAN_CLEANUP_MAX_FOLD");
        map.put("OCLET", "ORPHAN_CLEANUP_LAST_EXECUTION_TIME");
        map.put("PHD", "PRE_HANDLING_DATA");
        map.put("CT", "CLEANUP_TASK");
        map.put("DT", "DELETION_TASK");
        map.put("APT", "ADDITION_PRE_HANDLING_TASK");
        map.put("IDET", "ID_DETAILS");
        map.put("MGD", "METRACK_GENERATION_DURATION");
        map.put("DRC", "DELETION_RECURSIVE_COUNT");
        map.put("OC", "ORPHAN_COUNT");
        map.put("OCL", "ORPHAN_CLEANUP");
        CONSTANT_NAME_MAP = Collections.unmodifiableMap((Map<? extends String, ? extends String>)map);
    }
}
