package com.me.devicemanagement.framework.server.deletionfw;

import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Join;
import java.sql.SQLException;
import com.adventnet.persistence.WritableDataObject;
import java.util.List;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Collections;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataAccessException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DeletionTaskMetrack
{
    private static final Logger LOGGER;
    
    public static String getDeletionMeTrackData() {
        int deletionCount = 0;
        int successCount = 0;
        int failedCount = 0;
        int toDeleteCount = 0;
        JSONObject dependentDataDeletion = new JSONObject();
        try {
            final SelectQuery diSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeletionInfo"));
            diSelectQuery.addSelectColumn(Column.getColumn("DeletionInfo", "TASK_ID"));
            diSelectQuery.addSelectColumn(Column.getColumn("DeletionInfo", "COMPLETED_STATUS"));
            final DataObject dataObject = DataAccess.get(diSelectQuery);
            final Iterator diIterator = dataObject.getRows("DeletionInfo");
            while (diIterator.hasNext()) {
                ++deletionCount;
                final Row row = diIterator.next();
                final Long taskId = (Long)row.get("TASK_ID");
                try {
                    final Long completedStatus = (Long)row.get("COMPLETED_STATUS");
                    if (completedStatus == null) {
                        DeletionTaskMetrack.LOGGER.log(Level.INFO, "Completed status is null for task : " + taskId);
                    }
                    else if (COMPLETED_STATUS.SUCCESS.equals(completedStatus.intValue())) {
                        ++successCount;
                    }
                    else {
                        if (!COMPLETED_STATUS.FAILED.equals(completedStatus.intValue())) {
                            continue;
                        }
                        final DataObject dataObject2 = DataAccess.get("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(taskId, "RetryCount"));
                        if (dataObject2.isEmpty()) {
                            continue;
                        }
                        final Row row2 = dataObject2.getRow("DeletionParameters");
                        final Long retryCount = (Long)row2.get("PARAM_VALUE");
                        if (retryCount >= 3L) {
                            ++failedCount;
                        }
                        else {
                            ++toDeleteCount;
                        }
                    }
                }
                catch (final Exception e) {
                    DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Exception while getting completed status", e);
                }
            }
            dependentDataDeletion = getDependentDataDeletionMeTrackData();
        }
        catch (final Exception e2) {
            DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Exception in getting metrack data", e2);
        }
        final JSONObject deletionMetrackJson = new JSONObject();
        try {
            deletionMetrackJson.put("TotalBackgroundDeletion", deletionCount);
        }
        catch (final JSONException jx) {
            DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Exception while putting data in TotalBackgroundDeletion", (Throwable)jx);
        }
        try {
            deletionMetrackJson.put("SuccessCount", successCount);
        }
        catch (final JSONException jx) {
            DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Exception while putting data in SuccessCount", (Throwable)jx);
        }
        try {
            deletionMetrackJson.put("FailedAfterMaxRetry", failedCount);
        }
        catch (final JSONException jx) {
            DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Exception while putting data in FailedAfterMaxRetry", (Throwable)jx);
        }
        try {
            deletionMetrackJson.put("YetToDelete", toDeleteCount);
        }
        catch (final JSONException jx) {
            DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Exception while putting data in YetToDelete", (Throwable)jx);
        }
        try {
            deletionMetrackJson.put("Dependent", (Object)dependentDataDeletion);
        }
        catch (final JSONException jx) {
            DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Exception while putting data in Dependent", (Throwable)jx);
        }
        try {
            DeletionTaskMetrack.LOGGER.log(Level.INFO, "Background Deletion Metrack Readable Json data  : {0}", DeletionFWParam.replaceJsonKeys(deletionMetrackJson));
        }
        catch (final Exception e3) {
            DeletionTaskMetrack.LOGGER.log(Level.WARNING, "Exception while replacing json keys for Background Deletion Metrack Readable Json data:", e3);
        }
        DeletionTaskMetrack.LOGGER.log(Level.INFO, () -> "Background Deletion Metrack data : " + jsonObject);
        return deletionMetrackJson.toString();
    }
    
    private static JSONObject getDependentDataDeletionMeTrackData() {
        DeletionTaskMetrack.LOGGER.log(Level.INFO, "Initializing DependentDataDeletion MeTrack");
        final long startTime = System.currentTimeMillis();
        try {
            final HashMap<String, ProcessingType> normalTask = new LinkedHashMap<String, ProcessingType>();
            normalTask.put("CDD", ProcessingType.DESCENDING);
            normalTask.put("PDD", ProcessingType.DESCENDING);
            normalTask.put("TCRD", ProcessingType.DESCENDING);
            normalTask.put("TPRD", ProcessingType.DESCENDING);
            normalTask.put("DED", ProcessingType.DESCENDING);
            normalTask.put("CompletedStatus", ProcessingType.COUNT_MAP);
            final HashMap<String, ProcessingType> cleanupTask = new LinkedHashMap<String, ProcessingType>();
            cleanupTask.put("TDUR", ProcessingType.DESCENDING);
            cleanupTask.put("TCRD", ProcessingType.DESCENDING);
            cleanupTask.put("CompletedStatus", ProcessingType.COUNT_MAP);
            cleanupTask.put("EST", ProcessingType.ALL_LIST);
            final HashMap<String, ProcessingType> additionPreHandlingTask = new LinkedHashMap<String, ProcessingType>();
            additionPreHandlingTask.put("TDUR", ProcessingType.DESCENDING);
            additionPreHandlingTask.put("TCRD", ProcessingType.DESCENDING);
            additionPreHandlingTask.put("CompletedStatus", ProcessingType.COUNT_MAP);
            final HashMap<String, Object> normalTaskTrack = fetchTrackedData(normalTask, DeletionFWProps.metrackTopN, DeletionFWProps.metrackMDays, DeletionFWProps.metrackPCount, OPERATION_TYPE.DEPENDENT_DATA_DELETION);
            final HashMap<String, Object> cleanupTaskTrack = fetchTrackedData(cleanupTask, DeletionFWProps.metrackTopN, DeletionFWProps.metrackMDays, DeletionFWProps.metrackPCount, OPERATION_TYPE.INDEPENDENT_DATA_CLEANUP);
            final HashMap<String, Object> additionPreHandlingTaskTrack = fetchTrackedData(additionPreHandlingTask, DeletionFWProps.metrackTopN, DeletionFWProps.metrackMDays, DeletionFWProps.metrackPCount, OPERATION_TYPE.ADDITION_PRE_HANDLING);
            final JSONObject normalTrackJson = new JSONObject();
            final JSONObject cleanupTrackJson = new JSONObject();
            final JSONObject additionPreHandlingTrackJSON = new JSONObject();
            normalTrackJson.put("CDD", normalTaskTrack.get("CDD"));
            normalTrackJson.put("PDD", normalTaskTrack.get("PDD"));
            normalTrackJson.put("TCRD", normalTaskTrack.get("TCRD"));
            normalTrackJson.put("TPRD", normalTaskTrack.get("TPRD"));
            normalTrackJson.put("DED", normalTaskTrack.get("DED"));
            normalTrackJson.put("CS", normalTaskTrack.get("CompletedStatus"));
            cleanupTrackJson.put("TDUR", cleanupTaskTrack.get("TDUR"));
            cleanupTrackJson.put("TCRD", cleanupTaskTrack.get("TCRD"));
            cleanupTrackJson.put("CS", cleanupTaskTrack.get("CompletedStatus"));
            cleanupTrackJson.put("EST", cleanupTaskTrack.get("EST"));
            additionPreHandlingTrackJSON.put("TDUR", additionPreHandlingTaskTrack.get("TDUR"));
            additionPreHandlingTrackJSON.put("TCRD", additionPreHandlingTaskTrack.get("TCRD"));
            additionPreHandlingTrackJSON.put("CS", additionPreHandlingTaskTrack.get("CompletedStatus"));
            final Set<Long> normalIdsRequired = new HashSet<Long>();
            final Set<Long> cleanupIdsRequired = new HashSet<Long>();
            final Set<Long> addPreHandlingIdsRequired = new HashSet<Long>();
            normalIdsRequired.addAll(normalTaskTrack.get("CDD"));
            normalIdsRequired.addAll(normalTaskTrack.get("PDD"));
            normalIdsRequired.addAll(normalTaskTrack.get("TCRD"));
            normalIdsRequired.addAll(normalTaskTrack.get("TPRD"));
            normalIdsRequired.addAll(normalTaskTrack.get("DED"));
            cleanupIdsRequired.addAll(cleanupTaskTrack.get("TDUR"));
            cleanupIdsRequired.addAll(cleanupTaskTrack.get("TCRD"));
            cleanupIdsRequired.addAll(cleanupTaskTrack.get("EST"));
            addPreHandlingIdsRequired.addAll(additionPreHandlingTaskTrack.get("TDUR"));
            addPreHandlingIdsRequired.addAll(additionPreHandlingTaskTrack.get("TCRD"));
            final HashMap<Long, HashMap<String, Object>> requiredIdDetails = new HashMap<Long, HashMap<String, Object>>();
            requiredIdDetails.putAll(getCompleteDetail(OPERATION_TYPE.DEPENDENT_DATA_DELETION, normalIdsRequired));
            requiredIdDetails.putAll(getCompleteDetail(OPERATION_TYPE.INDEPENDENT_DATA_CLEANUP, cleanupIdsRequired));
            requiredIdDetails.putAll(getCompleteDetail(OPERATION_TYPE.ADDITION_PRE_HANDLING, addPreHandlingIdsRequired));
            JSONObject orphanCountJSON;
            try {
                orphanCountJSON = getOrphanRowCount();
            }
            catch (final Exception e) {
                DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Exception while fetching orphan json", e);
                orphanCountJSON = getExceptionJSON(e);
            }
            JSONObject orphanCleanupJSON;
            try {
                orphanCleanupJSON = getOrphanCleanupJSON();
            }
            catch (final Exception e2) {
                DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Exception while fetching orphan json", e2);
                orphanCleanupJSON = getExceptionJSON(e2);
            }
            final JSONObject metrackJson = new JSONObject();
            metrackJson.put("DT", (Object)normalTrackJson);
            metrackJson.put("CT", (Object)cleanupTrackJson);
            metrackJson.put("APT", (Object)additionPreHandlingTrackJSON);
            metrackJson.put("IDET", (Map)requiredIdDetails);
            metrackJson.put("MGD", System.currentTimeMillis() - startTime);
            metrackJson.put("OC", (Object)orphanCountJSON);
            metrackJson.put("OCL", (Object)orphanCleanupJSON);
            return metrackJson;
        }
        catch (final DataAccessException e3) {
            DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Exception in initDependentDataDeletionMeTrackData", (Throwable)e3);
            return getExceptionJSON((Exception)e3);
        }
        catch (final Exception e4) {
            DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Unexpected Exception in initDependentDataDeletionMeTrackData", e4);
            return getExceptionJSON(e4);
        }
    }
    
    private static HashMap<String, Object> fetchTrackedData(final HashMap<String, ProcessingType> paramNames, final int nValue, final int mDays, final int pValue, final OPERATION_TYPE operationType) throws DataAccessException {
        final HashMap<String, Object> result = new HashMap<String, Object>();
        final Column taskIDCol = new Column("DeletionParameters", "TASK_ID");
        final Column taskParamCol = new Column("DeletionParameters", "TASK_PARAM");
        final Set<Long> possibleIds = new HashSet<Long>();
        final long mDaysBefore = System.currentTimeMillis() - mDays * 86400000L;
        final Criteria trackableDataCriteria = new Criteria(Column.getColumn("DeletionInfo", "OPERATION_TYPE"), (Object)operationType.id, 0).and(new Criteria(Column.getColumn("DeletionInfo", "EXECUTION_START_TIME"), (Object)mDaysBefore, 5));
        final DataObject trackableDataDO = DataAccess.get("DeletionInfo", trackableDataCriteria);
        final Iterator diItr = trackableDataDO.getRows("DeletionInfo");
        while (diItr.hasNext()) {
            final Row row = diItr.next();
            possibleIds.add((Long)row.get("TASK_ID"));
        }
        final DataObject deletionParamsDO = DataAccess.get("DeletionParameters", new Criteria(taskIDCol, (Object)possibleIds.toArray(), 8));
        for (final String paramName : paramNames.keySet()) {
            final ProcessingType operator = paramNames.get(paramName);
            boolean isDeletionInfoIterator = false;
            Iterator diOrDpiterator;
            if (isParamIsInDeletionInfoTable(paramName)) {
                isDeletionInfoIterator = true;
                diOrDpiterator = trackableDataDO.getRows("DeletionInfo");
            }
            else {
                diOrDpiterator = deletionParamsDO.getRows("DeletionParameters", new Criteria(taskParamCol, (Object)paramName, 0));
            }
            if (operator.equals(ProcessingType.DESCENDING) || operator.equals(ProcessingType.ASCENDING)) {
                final HashMap<Long, Long> unsortedMap = new HashMap<Long, Long>();
                while (diOrDpiterator.hasNext()) {
                    final Row row2 = diOrDpiterator.next();
                    long paramValue;
                    if (isDeletionInfoIterator) {
                        paramValue = (long)row2.get(getDeletionInfoColumnName(paramName));
                    }
                    else {
                        paramValue = Long.parseLong((String)row2.get("PARAM_VALUE"));
                    }
                    final Long taskId = (Long)row2.get("TASK_ID");
                    unsortedMap.put(taskId, paramValue);
                }
                final Map<Long, Long> sortedMap = sortByValue(unsortedMap, operator.equals(ProcessingType.ASCENDING), nValue);
                result.put(paramName, sortedMap.keySet());
            }
            else if (operator.equals(ProcessingType.ALL_LIST)) {
                final SortedSet<Long> sortedSet = new TreeSet<Long>(Collections.reverseOrder());
                while (diOrDpiterator.hasNext()) {
                    final Row row2 = diOrDpiterator.next();
                    final Long taskId2 = (Long)row2.get("TASK_ID");
                    sortedSet.add(taskId2);
                }
                while (sortedSet.size() > pValue) {
                    sortedSet.remove(sortedSet.last());
                }
                result.put(paramName, sortedSet);
            }
            else {
                if (!operator.equals(ProcessingType.COUNT_MAP)) {
                    continue;
                }
                final HashMap<String, Integer> map = new HashMap<String, Integer>();
                while (diOrDpiterator.hasNext()) {
                    final Row row2 = diOrDpiterator.next();
                    String paramValue2;
                    if (isDeletionInfoIterator) {
                        paramValue2 = row2.get(getDeletionInfoColumnName(paramName)).toString();
                    }
                    else {
                        paramValue2 = row2.get("PARAM_VALUE").toString();
                    }
                    if (!map.containsKey(paramValue2)) {
                        map.put(paramValue2, 0);
                    }
                    map.put(paramValue2, map.get(paramValue2) + 1);
                }
                result.put(paramName, map);
            }
        }
        return result;
    }
    
    private static boolean isParamIsInDeletionInfoTable(final String param) {
        return param.equals("CompletedStatus") || param.equals("EST");
    }
    
    private static String getDeletionInfoColumnName(final String param) {
        if (param.equals("CompletedStatus")) {
            return "COMPLETED_STATUS";
        }
        if (param.equals("EST")) {
            return "EXECUTION_START_TIME";
        }
        return null;
    }
    
    private static HashMap<Long, Long> sortByValue(final HashMap<Long, Long> unsortedMap, final boolean isAscending, final Integer requiredCount) {
        final List<Map.Entry<Long, Long>> list = new LinkedList<Map.Entry<Long, Long>>(unsortedMap.entrySet());
        list.sort((o1, o2) -> {
            if (b) {
                return o1.getValue().compareTo(o2.getValue());
            }
            else {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        final HashMap<Long, Long> temp = new LinkedHashMap<Long, Long>();
        int i = 0;
        for (final Map.Entry<Long, Long> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
            ++i;
            if (requiredCount != null && i >= requiredCount) {
                break;
            }
        }
        return temp;
    }
    
    private static HashMap<Long, HashMap<String, Object>> getCompleteDetail(final OPERATION_TYPE operationType, final Set<Long> ids) throws DataAccessException {
        final HashMap<Long, HashMap<String, Object>> resultMap = new HashMap<Long, HashMap<String, Object>>();
        if (ids.size() == 0) {
            return resultMap;
        }
        final Column taskIDCol = new Column("DeletionParameters", "TASK_ID");
        final DataObject deletionInfoDO = DataAccess.get("DeletionInfo", new Criteria(Column.getColumn("DeletionInfo", "TASK_ID"), (Object)ids.toArray(), 8));
        final DataObject deletionParamsDO = DataAccess.get("DeletionParameters", new Criteria(taskIDCol, (Object)ids.toArray(), 8));
        for (final Long id : ids) {
            final Row deletionInfoRow = deletionInfoDO.getRow("DeletionInfo", DeletionTaskUtil.getDeletionInfoCriteria(id));
            final Iterator dpIterator = deletionParamsDO.getRows("DeletionParameters", new Criteria(taskIDCol, (Object)id, 0));
            final HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("CS", deletionInfoRow.get("COMPLETED_STATUS"));
            data.put("EST", deletionInfoRow.get("EXECUTION_START_TIME"));
            if (operationType.equals(OPERATION_TYPE.DEPENDENT_DATA_DELETION)) {
                while (dpIterator.hasNext()) {
                    final Row dpRow = dpIterator.next();
                    final String taskParam = (String)dpRow.get("TASK_PARAM");
                    final String paramValue = dpRow.get("PARAM_VALUE").toString();
                    final String s = taskParam;
                    switch (s) {
                        case "TPRD":
                        case "TCRD":
                        case "PDD":
                        case "CDD":
                        case "DED": {
                            data.put(taskParam, Long.parseLong(paramValue));
                            continue;
                        }
                        case "FS":
                        case "PD": {
                            data.put(taskParam, paramValue);
                            continue;
                        }
                        case "DD": {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(paramValue);
                            }
                            catch (final JSONException exception) {
                                DeletionTaskMetrack.LOGGER.log(Level.SEVERE, (Throwable)exception, () -> "Invalid JSON String, " + s4);
                                jsonObject = getExceptionJSON((Exception)exception);
                            }
                            data.put(taskParam, jsonObject);
                            continue;
                        }
                        case "StatusKey": {
                            data.put("DRC", paramValue.replaceAll("[^,]", "").length());
                            continue;
                        }
                    }
                }
            }
            else if (operationType.equals(OPERATION_TYPE.INDEPENDENT_DATA_CLEANUP)) {
                while (dpIterator.hasNext()) {
                    final Row dpRow = dpIterator.next();
                    final String taskParam = (String)dpRow.get("TASK_PARAM");
                    final String paramValue = dpRow.get("PARAM_VALUE").toString();
                    final String s2 = taskParam;
                    switch (s2) {
                        case "TCRD":
                        case "TDUR": {
                            data.put(taskParam, Long.parseLong(paramValue));
                            continue;
                        }
                        case "FS": {
                            data.put(taskParam, paramValue);
                            continue;
                        }
                        case "CUD": {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(paramValue);
                            }
                            catch (final JSONException exception) {
                                DeletionTaskMetrack.LOGGER.log(Level.SEVERE, (Throwable)exception, () -> "Invalid JSON String, " + s5);
                                jsonObject = getExceptionJSON((Exception)exception);
                            }
                            data.put(taskParam, jsonObject);
                            continue;
                        }
                        case "StatusKey": {
                            data.put("DRC", paramValue.replaceAll("[^,]", "").length());
                            continue;
                        }
                    }
                }
            }
            else if (operationType.equals(OPERATION_TYPE.ADDITION_PRE_HANDLING)) {
                while (dpIterator.hasNext()) {
                    final Row dpRow = dpIterator.next();
                    final String taskParam = (String)dpRow.get("TASK_PARAM");
                    final String paramValue = dpRow.get("PARAM_VALUE").toString();
                    final String s3 = taskParam;
                    switch (s3) {
                        case "TCRD":
                        case "TDUR": {
                            data.put(taskParam, Long.parseLong(paramValue));
                            continue;
                        }
                    }
                }
            }
            resultMap.put(id, data);
        }
        return resultMap;
    }
    
    private static JSONObject getOrphanRowCount() throws DataAccessException, SQLException, JSONException {
        final JSONObject result = new JSONObject();
        final DataObject deletionInfoDO = DataAccess.get("DeletionInfo", new Criteria(Column.getColumn("DeletionInfo", "OPERATION_TYPE"), (Object)OPERATION_TYPE.ORPHAN_COUNT_INFO.id, 0));
        long orphanTaskId;
        if (deletionInfoDO.isEmpty()) {
            orphanTaskId = DeletionTaskUtil.getUniqueTaskID(OPERATION_TYPE.ORPHAN_COUNT_INFO);
        }
        else {
            orphanTaskId = (long)deletionInfoDO.getRow("DeletionInfo").get("TASK_ID");
        }
        final DataObject deletionParamsDO = DataAccess.get("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(orphanTaskId));
        long lastModified;
        JSONObject orphanRowCount;
        long totalTime;
        if (deletionParamsDO.size("DeletionParameters") > 0) {
            lastModified = Long.parseLong((String)deletionParamsDO.getRow("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(orphanTaskId, "OCLM")).get("PARAM_VALUE"));
            long orphanCleanupLastDone = 0L;
            final DataObject deletionCleanupDO = DataAccess.get("DeletionInfo", new Criteria(Column.getColumn("DeletionInfo", "OPERATION_TYPE"), (Object)OPERATION_TYPE.ORPHAN_CLEANUP_INFO.id, 0));
            if (!deletionCleanupDO.isEmpty()) {
                final Row deletionCleanupRow = deletionCleanupDO.getRow("DeletionInfo");
                if (deletionCleanupRow != null && deletionCleanupRow.get("EXECUTION_START_TIME") != null) {
                    orphanCleanupLastDone = (long)deletionCleanupRow.get("EXECUTION_START_TIME");
                }
            }
            if (orphanCleanupLastDone > lastModified || lastModified <= System.currentTimeMillis() - DeletionFWProps.orphanCountUpdateDuration * 24L * 60L * 60L * 1000L) {
                DeletionTaskMetrack.LOGGER.log(Level.INFO, "Fetching current orphan data");
                lastModified = System.currentTimeMillis();
                orphanRowCount = new JSONObject((Map)getCurrentOrphanRowCount());
                String orphanJSON = orphanRowCount.toString();
                totalTime = System.currentTimeMillis() - lastModified;
                deletionParamsDO.updateRow(DeletionTaskUtil.getDeletionParamsRow(orphanTaskId, "OCLM", lastModified));
                deletionParamsDO.updateRow(DeletionTaskUtil.getDeletionParamsRow(orphanTaskId, "OCTT", totalTime));
                final int limit = DeletionFWProps.deletionParamsColumnSize;
                final List<String> orphanJSONList = new LinkedList<String>();
                while (orphanJSON.length() > limit) {
                    orphanJSONList.add(orphanJSON.substring(0, limit));
                    orphanJSON = orphanJSON.substring(limit);
                }
                if (orphanJSON.length() > 0) {
                    orphanJSONList.add(orphanJSON);
                }
                deletionParamsDO.updateRow(DeletionTaskUtil.getDeletionParamsRow(orphanTaskId, "OCMF", orphanJSONList.size()));
                int i = 0;
                for (final String str : orphanJSONList) {
                    final String paramName = "OCJ_" + i++;
                    if (deletionParamsDO.getRow("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(orphanTaskId, paramName)) != null) {
                        deletionParamsDO.updateRow(DeletionTaskUtil.getDeletionParamsRow(orphanTaskId, paramName, str));
                    }
                    else {
                        deletionParamsDO.addRow(DeletionTaskUtil.getDeletionParamsRow(orphanTaskId, paramName, str));
                    }
                }
                DataAccess.update(deletionParamsDO);
            }
            else {
                DeletionTaskMetrack.LOGGER.log(Level.INFO, "Returning old orphan data");
                totalTime = Long.parseLong((String)deletionParamsDO.getRow("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(orphanTaskId, "OCTT")).get("PARAM_VALUE"));
                final int maxFoldCount = Integer.parseInt((String)deletionParamsDO.getRow("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(orphanTaskId, "OCMF")).get("PARAM_VALUE"));
                final StringBuilder orphanStr = new StringBuilder();
                for (int j = 0; j < maxFoldCount; ++j) {
                    final String paramName2 = "OCJ_" + j;
                    final Row row = deletionParamsDO.getRow("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(orphanTaskId, paramName2));
                    if (row == null) {
                        DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Some orphan data is missing from DB.");
                    }
                    else {
                        orphanStr.append(row.get("PARAM_VALUE"));
                    }
                }
                try {
                    orphanRowCount = new JSONObject(orphanStr.toString());
                }
                catch (final JSONException ex) {
                    orphanRowCount = new JSONObject();
                    orphanRowCount.put("Invalid JSON", (Object)orphanStr.toString());
                }
                catch (final Exception ex2) {
                    orphanRowCount = getExceptionJSON(ex2);
                }
            }
        }
        else {
            DeletionTaskMetrack.LOGGER.log(Level.INFO, "Fetching orphan data for first time");
            lastModified = System.currentTimeMillis();
            orphanRowCount = new JSONObject((Map)getCurrentOrphanRowCount());
            String orphanJSON2 = orphanRowCount.toString();
            totalTime = System.currentTimeMillis() - lastModified;
            final DataObject updateDO = (DataObject)new WritableDataObject();
            updateDO.addRow(DeletionTaskUtil.getDeletionParamsRow(orphanTaskId, "OCLM", lastModified));
            updateDO.addRow(DeletionTaskUtil.getDeletionParamsRow(orphanTaskId, "OCTT", totalTime));
            final int limit2 = DeletionFWProps.deletionParamsColumnSize;
            final List<String> orphanJSONList2 = new LinkedList<String>();
            while (orphanJSON2.length() > limit2) {
                orphanJSONList2.add(orphanJSON2.substring(0, limit2));
                orphanJSON2 = orphanJSON2.substring(limit2);
            }
            if (orphanJSON2.length() > 0) {
                orphanJSONList2.add(orphanJSON2);
            }
            updateDO.addRow(DeletionTaskUtil.getDeletionParamsRow(orphanTaskId, "OCMF", orphanJSONList2.size()));
            int k = 0;
            for (final String str2 : orphanJSONList2) {
                final String paramName3 = "OCJ_" + k++;
                updateDO.addRow(DeletionTaskUtil.getDeletionParamsRow(orphanTaskId, paramName3, str2));
            }
            DataAccess.add(updateDO);
        }
        result.put("OCJ", (Object)orphanRowCount);
        result.put("OCTT", totalTime);
        result.put("OCLM", lastModified);
        return result;
    }
    
    private static HashMap<Long, HashMap<String, Long>> getCurrentOrphanRowCount() throws DataAccessException, SQLException {
        DeletionTaskMetrack.LOGGER.log(Level.INFO, "Fetching orphan row count");
        final HashMap<Long, HashMap<String, Long>> result = new HashMap<Long, HashMap<String, Long>>();
        final SelectQuery parentQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ParentDependencyInfo"));
        parentQuery.addSelectColumn(Column.getColumn("ParentDependencyInfo", "ID"));
        parentQuery.addSelectColumn(Column.getColumn("ParentDependencyInfo", "TABLE_NAME"));
        parentQuery.addSelectColumn(Column.getColumn("ParentDependencyInfo", "COLUMN_NAME"));
        final DataObject parentDO = DataAccess.get(parentQuery);
        final Iterator<Row> parentItr = parentDO.getRows("ParentDependencyInfo");
        while (parentItr.hasNext()) {
            final Row parentRow = parentItr.next();
            final long parentID = (long)parentRow.get("ID");
            final String parentTableName = (String)parentRow.get("TABLE_NAME");
            final String[] parentColumnNames = ((String)parentRow.get("COLUMN_NAME")).split(",");
            final List<String> childTablesToExclude = DeletionFWProps.orphanCleanupSkipMap.get(parentTableName.toLowerCase());
            Criteria nullCriteria = null;
            for (final String column : parentColumnNames) {
                if (nullCriteria == null) {
                    nullCriteria = new Criteria(Column.getColumn(parentTableName, column), (Object)null, 0);
                }
                else {
                    nullCriteria = nullCriteria.and(Column.getColumn(parentTableName, column), (Object)null, 0);
                }
            }
            final SelectQuery childQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ChildDependencyInfo"));
            childQuery.addSelectColumn(Column.getColumn("ChildDependencyInfo", "PARENT_ID"));
            childQuery.addSelectColumn(Column.getColumn("ChildDependencyInfo", "CHILD_TABLE_NAME"));
            childQuery.addSelectColumn(Column.getColumn("ChildDependencyInfo", "CHILD_COLUMN_NAME"));
            childQuery.setCriteria(new Criteria(Column.getColumn("ChildDependencyInfo", "PARENT_ID"), (Object)parentID, 0));
            final DataObject childDO = DataAccess.get(childQuery);
            final Iterator<Row> childItr = childDO.getRows("ChildDependencyInfo");
            while (childItr.hasNext()) {
                final Row childRow = childItr.next();
                final String childTableName = (String)childRow.get("CHILD_TABLE_NAME");
                final String[] childColumnNames = ((String)childRow.get("CHILD_COLUMN_NAME")).split(",");
                if (childTablesToExclude != null && childTablesToExclude.contains(childTableName.toLowerCase())) {
                    DeletionTaskMetrack.LOGGER.log(Level.INFO, () -> "---------------------Skipping cleanup for : [" + s + "->" + s2 + "]");
                }
                else {
                    final SelectQuery orphanQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(childTableName));
                    orphanQuery.addJoin(new Join(childTableName, parentTableName, childColumnNames, parentColumnNames, 1));
                    orphanQuery.setCriteria(nullCriteria);
                    final Column countColumn = Column.getColumn(childTableName, childColumnNames[0]).count();
                    countColumn.setColumnAlias("ORPHAN_COUNT");
                    orphanQuery.addSelectColumn(countColumn);
                    long count = 0L;
                    DeletionTaskMetrack.LOGGER.log(Level.FINE, () -> "Checking " + s3 + "->" + s4);
                    DMDataSetWrapper ds;
                    try {
                        ds = DeletionTaskUtil.executeQuery(orphanQuery);
                    }
                    catch (final Exception e) {
                        ds = null;
                        DeletionTaskMetrack.LOGGER.log(Level.SEVERE, e, () -> "Exception while executing orphan row count query for " + s5 + "->" + s6);
                    }
                    if (ds != null && ds.next()) {
                        count = (int)ds.getValue("ORPHAN_COUNT");
                    }
                    if (count == 0L) {
                        continue;
                    }
                    if (!result.containsKey(parentID)) {
                        result.put(parentID, new HashMap<String, Long>());
                    }
                    result.get(parentID).put(childTableName, count);
                }
            }
        }
        return result;
    }
    
    private static JSONObject getOrphanCleanupJSON() throws DataAccessException, JSONException {
        DeletionTaskMetrack.LOGGER.log(Level.INFO, "Fetching Orphan Cleanup JSON");
        final DataObject deletionInfoDO = DataAccess.get("DeletionInfo", new Criteria(Column.getColumn("DeletionInfo", "OPERATION_TYPE"), (Object)OPERATION_TYPE.ORPHAN_CLEANUP_INFO.id, 0));
        if (deletionInfoDO.isEmpty()) {
            return new JSONObject();
        }
        final JSONObject result = new JSONObject();
        final Long orphanCleanupTaskId = (Long)deletionInfoDO.getRow("DeletionInfo").get("TASK_ID");
        final Long lastExecutionTime = (Long)deletionInfoDO.getRow("DeletionInfo").get("EXECUTION_START_TIME");
        final DataObject deletionParamsDO = DataAccess.get("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(orphanCleanupTaskId));
        if (deletionParamsDO.isEmpty()) {
            result.put("Exception", (Object)"Deletion Params Table is empty");
            return result;
        }
        final long totalTime = Long.parseLong((String)deletionParamsDO.getRow("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(orphanCleanupTaskId, "OCLD")).get("PARAM_VALUE"));
        final int maxFoldCount = Integer.parseInt((String)deletionParamsDO.getRow("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(orphanCleanupTaskId, "OCLMF")).get("PARAM_VALUE"));
        final StringBuilder orphanStr = new StringBuilder();
        for (int i = 0; i < maxFoldCount; ++i) {
            final String paramName = "OCLJ_" + i;
            final Row row = deletionParamsDO.getRow("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(orphanCleanupTaskId, paramName));
            if (row == null) {
                DeletionTaskMetrack.LOGGER.log(Level.SEVERE, "Some orphan data is missing from DB.");
            }
            else {
                orphanStr.append(row.get("PARAM_VALUE"));
            }
        }
        JSONObject orphanCleanupJSON;
        try {
            orphanCleanupJSON = new JSONObject(orphanStr.toString());
        }
        catch (final JSONException pex) {
            orphanCleanupJSON = new JSONObject();
            orphanCleanupJSON.put("Invalid JSON", (Object)orphanStr.toString());
        }
        catch (final Exception e) {
            orphanCleanupJSON = getExceptionJSON(e);
        }
        result.put("OCLJ", (Object)orphanCleanupJSON);
        result.put("OCLD", totalTime);
        result.put("OCLET", (Object)lastExecutionTime);
        return result;
    }
    
    private static JSONObject getExceptionJSON(final String key, final Exception e) {
        final JSONObject json = new JSONObject();
        try {
            json.put(e.getClass().getSimpleName(), (Object)getExceptionAsMessage(e));
        }
        catch (final JSONException jx) {
            DeletionTaskMetrack.LOGGER.log(Level.SEVERE, e, () -> "Exception while getting exception json key=" + s);
        }
        return json;
    }
    
    private static JSONObject getExceptionJSON(final Exception e) {
        return getExceptionJSON(e.getClass().getSimpleName(), e);
    }
    
    private static String getExceptionAsMessage(final Exception e) {
        final String message = (e.getMessage() != null) ? e.getMessage() : e.toString();
        return (message.length() > 20) ? message.substring(0, 20) : message;
    }
    
    static {
        LOGGER = DeletionTaskUtil.getDeletionFwLogger();
    }
    
    private enum ProcessingType
    {
        ASCENDING(1), 
        DESCENDING(2), 
        COUNT_MAP(3), 
        ALL_LIST(4);
        
        final int id;
        
        private ProcessingType(final int id) {
            this.id = id;
        }
        
        public boolean equals(final ProcessingType type) {
            return this.id == type.id;
        }
    }
}
