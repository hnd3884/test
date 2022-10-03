package com.me.devicemanagement.framework.server.deletionfw;

import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.function.Supplier;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.Map;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.SortColumn;
import java.util.ArrayList;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Join;
import org.json.JSONObject;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.WritableDataObject;
import java.util.Arrays;
import java.util.HashSet;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.util.Collection;
import org.json.JSONArray;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import java.util.Iterator;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import java.util.Queue;
import java.util.logging.Logger;

class DeletionTaskUtil
{
    private static final Logger LOGGER;
    private static final ThreadLocal<Queue<Long>> TRANSACTION_PROPERTIES;
    private static boolean isPostgres;
    
    public static Logger getDeletionFwLogger() {
        return Logger.getLogger("DeletionFwLogger");
    }
    
    public static void transactionCommitted() throws SystemException {
        try {
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Transaction committed, so invoking async tasks");
            if (!DeletionTaskUtil.TRANSACTION_PROPERTIES.get().isEmpty()) {
                final TransactionManager transactionManager = SyMUtil.getUserTransaction();
                if (transactionManager != null) {
                    final Transaction transaction = transactionManager.getTransaction();
                    final int status = transactionManager.getStatus();
                    if (transaction == null || status == 3 || status == 6) {
                        addAllTaskToQueue();
                    }
                    else {
                        DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Failed Invoked DeletionTaskUtil.transactionCommitted before the transaction is committed");
                    }
                }
                else {
                    DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Transaction manager has null value so adding all data in queue");
                    addAllTaskToQueue();
                }
            }
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while fetching transaction information in transactionCommitted method", e);
            addAllTaskToQueue();
        }
    }
    
    public static void transactionRollback() {
        try {
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Removing all tasks in transaction");
            removeAllTask();
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while removing tasks from transaction thread local", e);
        }
    }
    
    private static void addAllTaskToQueue() {
        try {
            if (DeletionTaskUtil.TRANSACTION_PROPERTIES.get() != null) {
                for (final Long taskId : DeletionTaskUtil.TRANSACTION_PROPERTIES.get()) {
                    try {
                        addToQueue(taskId, OPERATION_TYPE.DEPENDENT_DATA_DELETION);
                    }
                    catch (final DeletionQueueFailedException ex) {
                        DeletionTaskUtil.LOGGER.log(Level.SEVERE, ex, () -> "Exception while adding data [" + n + "] to queue");
                    }
                    DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Invoked async task  taskID [" + n2 + "]");
                }
            }
            else {
                DeletionTaskUtil.LOGGER.log(Level.SEVERE, "No data in Thread Local to delete");
            }
        }
        finally {
            DeletionTaskUtil.TRANSACTION_PROPERTIES.remove();
        }
    }
    
    private static void removeAllTask() {
        DeletionTaskUtil.TRANSACTION_PROPERTIES.remove();
    }
    
    public static void asyncDelete(final List<String> tables, final Criteria criteria, final boolean isPersistence) throws DataAccessException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Creating properties for invoking async task for Delete_Multiple_Table_Same_Criteria");
        final String statusKey = "Delete_Multiple_Table_Same_Criteria_" + System.currentTimeMillis();
        final DeleteDataDetails deleteDataDetails = new DeleteDataDetails();
        deleteDataDetails.tablesList = tables;
        deleteDataDetails.criteria = criteria;
        deleteDataDetails.isPersistenceDeletion = isPersistence;
        deleteDataDetails.chunkThreshold = DeletionFWProps.chunkThreshold;
        deleteDataDetails.taskID = getUniqueTaskID(OPERATION_TYPE.DELETE_MULTIPLE_TABLE_WITH_SAME_CRITERIA);
        try {
            writeInitialPropsInDB(deleteDataDetails.taskID, statusKey, deleteDataDetails);
            addToQueue(deleteDataDetails.taskID, OPERATION_TYPE.DELETE_MULTIPLE_TABLE_WITH_SAME_CRITERIA);
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while writing initial props in db", e);
            throw new DataAccessException("Exception while deleting asynchronously", (Throwable)e);
        }
    }
    
    static long checkForAndDeleteDependentData(final String tableName, final Criteria criteria, final String parentStatusKey, final boolean isPersistence, final boolean isChildDeletionAsynchronous) throws DependentDeletionFailedException, DataAccessException, DeletionQueueFailedException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Inside checkForAndDeleteDependentData for table ==" + s + "== with criteria " + criteria2.toString());
        if (!getParentDependencyTables().containsKey(tableName.toLowerCase())) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, () -> "Should not delete this table [" + s2 + "] using dependent data deletion method");
            throw new DependentDeletionFailedException("Should not delete this table [" + tableName + "] using dependent data deletion method");
        }
        final long startTime = System.currentTimeMillis();
        try {
            final DeleteDataDetails deleteDataDetails = new DeleteDataDetails();
            final HashMap<Long, String[]> columnNamesToStore = getColumnNamesToStore(tableName);
            final Set<String> allColumnNamesToStore = getAllColumnNamesToStore(columnNamesToStore);
            final List<HashMap<String, Long>> deletedData = deleteFromParentTable(tableName, criteria, allColumnNamesToStore, DeletionFWProps.chunkThreshold, isPersistence);
            deleteDataDetails.tableName = tableName;
            deleteDataDetails.totalParentRowsDeleted = deletedData.size();
            deleteDataDetails.parentDeletionStartTime = startTime;
            deleteDataDetails.parentDeletionDuration = System.currentTimeMillis() - startTime;
            deleteDataDetails.isPersistenceDeletion = isPersistence;
            deleteDataDetails.processType = DeleteDataDetails.ProcessType.DEPENDENT_DELETION;
            deleteDataDetails.isDeletionAsynchronous = isChildDeletionAsynchronous;
            deleteDataDetails.taskID = writeInitialPropsInDBForDependentDataDeletion(parentStatusKey, deleteDataDetails);
            if (deleteDataDetails.taskID == null) {
                DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Data deleted from parent table, but unable to store the deleted data, so the dependent fk data cant be deleted");
                throw new DependentDeletionFailedException("Data partially deleted");
            }
            deleteDataDetails.statusKey = getStatusKey(parentStatusKey, deleteDataDetails.taskID);
            deleteDataDetails.operationType = OPERATION_TYPE.DEPENDENT_DATA_DELETION;
            storeDeletedDataInDB(deleteDataDetails.taskID, deletedData, columnNamesToStore);
            if (!isChildDeletionAsynchronous) {
                new DeletionDataProcessor().processData(deleteDataDetails);
            }
            else {
                try {
                    if (SyMUtil.getUserTransaction() == null || SyMUtil.getUserTransaction().getStatus() == 6 || SyMUtil.getUserTransaction().getTransaction() == null) {
                        addToQueue(deleteDataDetails.taskID, OPERATION_TYPE.DEPENDENT_DATA_DELETION);
                    }
                    else {
                        DeletionTaskUtil.TRANSACTION_PROPERTIES.get().add(deleteDataDetails.taskID);
                    }
                }
                catch (final DeletionQueueFailedException e) {
                    DeletionTaskUtil.LOGGER.log(Level.SEVERE, e, () -> "Exception while adding data [" + deleteDataDetails2.taskID + "] to queue");
                    throw e;
                }
                catch (final Exception e2) {
                    DeletionTaskUtil.LOGGER.log(Level.SEVERE, e2, () -> "Exception while fetching transaction information");
                    addToQueue(deleteDataDetails.taskID, OPERATION_TYPE.DEPENDENT_DATA_DELETION);
                }
            }
            return deleteDataDetails.totalParentRowsDeleted;
        }
        catch (final DataAccessException | DeletionQueueFailedException | DependentDeletionFailedException e3) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Deletion Task execution Exception :", e3);
            throw e3;
        }
        finally {
            DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Time taken to delete data from parent table [" + s3 + "] is [" + (System.currentTimeMillis() - n) + "] milliseconds");
        }
    }
    
    private static String getStatusKey(final String parentStatusKey, final long taskId) {
        return (parentStatusKey == null) ? ("" + taskId) : (parentStatusKey + "," + taskId);
    }
    
    private static List<HashMap<String, Long>> deleteFromParentTable(final String tableName, final Criteria criteria, final Set<String> columnList, final int chunkThreshold, final boolean isPersistence) throws DataAccessException {
        final List<HashMap<String, Long>> deletedData = new LinkedList<HashMap<String, Long>>();
        final DataObject dataObject = DataAccess.get(tableName, criteria);
        final Iterator itr = dataObject.getRows(tableName);
        while (itr.hasNext()) {
            final Row row = itr.next();
            final HashMap<String, Long> data = new HashMap<String, Long>();
            for (final String columnName : columnList) {
                final Object value = row.get(columnName);
                if (value instanceof Integer) {
                    data.put(columnName, (long)value);
                }
                else {
                    if (!(value instanceof Long)) {
                        throw new DataAccessException("Incompatible Column DataType for Dependent data Deletion, Column Name : " + columnName + " , Datatype : " + value.getClass().getName());
                    }
                    data.put(columnName, (Long)value);
                }
            }
            deletedData.add(data);
        }
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Going to delete this data in table ==" + s + "==" + new JSONArray((Collection)list));
        final long totalRowsDeleted = doChunkDeletion(tableName, criteria, chunkThreshold, isPersistence);
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Total no. of rows deleted : " + n);
        return deletedData;
    }
    
    private static HashMap<Long, String[]> getColumnNamesToStore(final String tableName) throws DataAccessException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Getting column names to store for table [" + s + "]");
        final Criteria parentDepCriteria = new Criteria(new Column("ParentDependencyInfo", "TABLE_NAME"), (Object)tableName, 0, false);
        final DataObject parentDepDo = SyMUtil.getCachedPersistence().get("ParentDependencyInfo", parentDepCriteria);
        final HashMap<Long, String[]> columnNamesToStore = new HashMap<Long, String[]>();
        final Iterator parentItr = parentDepDo.getRows("ParentDependencyInfo");
        while (parentItr.hasNext()) {
            final Row parentRow = parentItr.next();
            final Long id = (Long)parentRow.get("ID");
            final String[] columns = ((String)parentRow.get("COLUMN_NAME")).split(",");
            columnNamesToStore.put(id, columns);
        }
        return columnNamesToStore;
    }
    
    private static Set<String> getAllColumnNamesToStore(final HashMap<Long, String[]> columnNameMap) {
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Getting the set of column names to store");
        final Set<String> allColumnNames = new HashSet<String>();
        for (final String[] columns : columnNameMap.values()) {
            allColumnNames.addAll(Arrays.asList(columns));
        }
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Need to store " + set);
        return allColumnNames;
    }
    
    private static void storeDeletedDataInDB(final Long taskID, final List<HashMap<String, Long>> deletedDataList, final HashMap<Long, String[]> columnNamesToStore) throws DataAccessException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Storing deleted data in DeletedDependentData");
        final DataObject dddDO = (DataObject)new WritableDataObject();
        final HashMap<Long, List<String>> parentIdAndValue = new HashMap<Long, List<String>>();
        for (final HashMap<String, Long> deletedData : deletedDataList) {
            for (final Long id : columnNamesToStore.keySet()) {
                final String[] columnNames = columnNamesToStore.get(id);
                final StringBuilder commaSeparatedValues = new StringBuilder();
                for (final String column : columnNames) {
                    commaSeparatedValues.append(deletedData.get(column)).append(",");
                }
                commaSeparatedValues.deleteCharAt(commaSeparatedValues.length() - 1);
                if (!parentIdAndValue.containsKey(id)) {
                    parentIdAndValue.put(id, new LinkedList<String>());
                }
                parentIdAndValue.get(id).add(commaSeparatedValues.toString());
            }
        }
        for (final Long parentId : parentIdAndValue.keySet()) {
            final Criteria ukCheckCriteria = getDependentDeletionCriteria(null, parentId, (String[])parentIdAndValue.get(parentId).toArray(new String[0]));
            final DataObject excludeDO = DataAccess.get("DeletedDependentData", ukCheckCriteria);
            final Iterator dddItr = excludeDO.getRows("DeletedDependentData");
            while (dddItr.hasNext()) {
                final Row dddRow = dddItr.next();
                final String commaSeparatedValues2 = dddRow.get("DELETED_DATA").toString();
                DeletionTaskUtil.LOGGER.log(Level.WARNING, () -> "Already the data exists for deleting : parentId=[" + n + "] and value=[" + s + "]");
                parentIdAndValue.get(parentId).remove(commaSeparatedValues2);
            }
            for (final String dependentData : parentIdAndValue.get(parentId)) {
                final Row dddRow2 = new Row("DeletedDependentData");
                dddRow2.set("TASK_ID", (Object)taskID);
                dddRow2.set("PARENT_ID", (Object)parentId);
                dddRow2.set("DELETED_DATA", (Object)dependentData);
                dddDO.addRow(dddRow2);
            }
        }
        DataAccess.add(dddDO);
    }
    
    public static void deleteAllDependentDataForQueueDeletionTask(final DeleteDataDetails deleteDataDetails) {
        final long startTime = System.currentTimeMillis();
        Long totalRowsDeleted = null;
        try {
            DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Task ID : " + deleteDataDetails2.taskID);
            final DataObject dpDO = DataAccess.get("DeletionParameters", getDeletionParamsCriteria(deleteDataDetails.taskID));
            final DataObject diDO = DataAccess.get("DeletionInfo", getDeletionInfoCriteria(deleteDataDetails.taskID));
            if (dpDO.isEmpty()) {
                DeletionTaskUtil.LOGGER.log(Level.SEVERE, "The transaction which initiated this deletion might be rollback. So skipping the child deletion");
                return;
            }
            final Row statusRow = diDO.getRow("DeletionInfo");
            if (!canDoDependentDeletion(statusRow)) {
                DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Already the data is deleted or cleanup is in progress. So skipping deletion. TaskID : " + deleteDataDetails3.taskID);
                return;
            }
            statusRow.set("COMPLETED_STATUS", (Object)COMPLETED_STATUS.DEPENDENT_DELETION_STARTED.id);
            diDO.updateRow(statusRow);
            DataAccess.update(diDO);
            final HashMap<Long, Long> deletedDetails = deleteAllDependentData(deleteDataDetails);
            totalRowsDeleted = deletedDetails.values().stream().reduce(0L, Long::sum);
            final long totalTime = System.currentTimeMillis() - startTime;
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Total Time taken(ms) : " + totalTime + " , Total rows Deleted : " + totalRowsDeleted);
            writeFinalPropsInDBForDependentDataDeletion(deleteDataDetails, true, null, totalRowsDeleted, totalTime, deletedDetails);
        }
        catch (final Exception e) {
            final long totalTime = System.currentTimeMillis() - startTime;
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while deleting dependent data after :" + totalTime, e);
            try {
                writeFinalPropsInDBForDependentDataDeletion(deleteDataDetails, false, e.getMessage(), totalRowsDeleted, totalTime, null);
            }
            catch (final DataAccessException dataAccessException) {
                DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while updating dependent data failure reason in db", (Throwable)dataAccessException);
            }
        }
    }
    
    private static HashMap<Long, Long> deleteAllDependentData(final DeleteDataDetails deleteDataDetails) throws DataAccessException, SQLException, QueryConstructionException {
        final HashMap<Long, Long> deletedData = new HashMap<Long, Long>();
        final SelectQuery dataToDeleteQuery = (SelectQuery)new SelectQueryImpl(new Table("DeletedDependentData"));
        final Column distinctParentIdCol = new Column("DeletedDependentData", "PARENT_ID").distinct();
        distinctParentIdCol.setColumnAlias("DISTINCT_PARENT_ID");
        dataToDeleteQuery.addSelectColumn(distinctParentIdCol);
        dataToDeleteQuery.setCriteria(new Criteria(new Column("DeletedDependentData", "TASK_ID"), (Object)deleteDataDetails.taskID, 0));
        final DMDataSetWrapper deletedDataSet = executeQuery(dataToDeleteQuery);
        while (deletedDataSet.next()) {
            final long parentID = (long)deletedDataSet.getValue("DISTINCT_PARENT_ID");
            final long deletedCount = deleteAllDependentData(parentID, deleteDataDetails);
            deletedData.put(parentID, deletedCount);
        }
        return deletedData;
    }
    
    private static long deleteAllDependentData(final Long parentID, final DeleteDataDetails deleteDataDetails) throws DataAccessException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Entered deleteAllDependentData with parentID = " + n + " and taskID = " + deleteDataDetails2.taskID);
        final Criteria parentDepCriteria = new Criteria(new Column("ParentDependencyInfo", "ID"), (Object)parentID, 0, false);
        final DataObject parentDepDo = SyMUtil.getCachedPersistence().get("ParentDependencyInfo", parentDepCriteria);
        if (parentDepDo.size("ParentDependencyInfo") == 0) {
            return 0L;
        }
        final String[] parentColumnNames = ((String)parentDepDo.getFirstRow("ParentDependencyInfo").get("COLUMN_NAME")).split(",");
        final int groupCount = deleteDataDetails.groupThreshold;
        final Criteria childDepCriteria = new Criteria(new Column("ChildDependencyInfo", "PARENT_ID"), (Object)parentID, 0);
        final DataObject childDepDo = SyMUtil.getCachedPersistence().get("ChildDependencyInfo", childDepCriteria);
        final HashMap<String, String[]> childColumnNames = new HashMap<String, String[]>();
        final HashMap<String, Boolean> isAnotherParent = new HashMap<String, Boolean>();
        final Iterator childTableItr = childDepDo.getRows("ChildDependencyInfo");
        while (childTableItr.hasNext()) {
            final Row childDepRow = childTableItr.next();
            childColumnNames.put((String)childDepRow.get("CHILD_TABLE_NAME"), ((String)childDepRow.get("CHILD_COLUMN_NAME")).split(","));
            isAnotherParent.put((String)childDepRow.get("CHILD_TABLE_NAME"), (boolean)childDepRow.get("IS_ANOTHER_PARENT"));
        }
        Criteria deletedDataCriteria = new Criteria(new Column("DeletedDependentData", "PARENT_ID"), (Object)parentID, 0);
        if (deleteDataDetails.processType == DeleteDataDetails.ProcessType.DEPENDENT_DELETION) {
            deletedDataCriteria = deletedDataCriteria.and(new Criteria(new Column("DeletedDependentData", "TASK_ID"), (Object)deleteDataDetails.taskID, 0));
        }
        else if (deleteDataDetails.processType == DeleteDataDetails.ProcessType.CLEANUP) {
            if (deleteDataDetails.excludeTaskIds != null && !deleteDataDetails.excludeTaskIds.isEmpty()) {
                deletedDataCriteria = deletedDataCriteria.and(new Criteria(new Column("DeletedDependentData", "TASK_ID"), (Object)deleteDataDetails.excludeTaskIds.toArray(), 9));
            }
        }
        else if (deleteDataDetails.processType == DeleteDataDetails.ProcessType.ADDITION_PRE_HANDLING) {
            if (deleteDataDetails.deletedData == null) {
                DeletionTaskUtil.LOGGER.log(Level.SEVERE, () -> "Data to be deleted is null for task [" + deleteDataDetails3.taskID + "]");
                throw new DataAccessException("Invalid Data to be Deleted");
            }
            deletedDataCriteria = deletedDataCriteria.and(new Criteria(new Column("DeletedDependentData", "DELETED_DATA"), (Object)deleteDataDetails.deletedData, 0));
        }
        final DataObject dddDO = DataAccess.get("DeletedDependentData", deletedDataCriteria);
        final long totalSize = dddDO.size("DeletedDependentData");
        long totalRowsDeleted = 0L;
        boolean isExceptionThrown = false;
        final List<List<Row>> groupedRows = new LinkedList<List<Row>>();
        List<Row> rows = null;
        int dddDORowIndex = 0;
        final Iterator dddItr = dddDO.getRows("DeletedDependentData");
        while (dddItr.hasNext()) {
            if (dddDORowIndex % groupCount == 0) {
                rows = new LinkedList<Row>();
                groupedRows.add(rows);
            }
            rows.add(dddItr.next());
            ++dddDORowIndex;
        }
        for (final List<Row> groupedRow : groupedRows) {
            DeletionTaskUtil.LOGGER.log(Level.FINE, "Number of Rows in the Group : " + groupedRow.size());
            final Iterator<String> childTableItr2 = childColumnNames.keySet().iterator();
            while (childTableItr2.hasNext()) {
                final String childTable = childTableItr2.next();
                final String[] childColumns = childColumnNames.get(childTable);
                if (!isTablePresent(childTable)) {
                    DeletionTaskUtil.LOGGER.log(Level.WARNING, () -> "Table not present : " + s + ". So skipping deletion");
                }
                else {
                    DeletionTaskUtil.LOGGER.log(Level.FINE, () -> "Deleting rows from " + s2);
                    try {
                        Criteria criteriaToDelete = null;
                        final List<Long> childDeletionData = new LinkedList<Long>();
                        final List<Long> childDeletedIds = new LinkedList<Long>();
                        for (final Row deletedRow : groupedRow) {
                            childDeletedIds.add((Long)deletedRow.get("ID"));
                            final String[] deletedData = ((String)deletedRow.get("DELETED_DATA")).split(",");
                            if (parentColumnNames.length == 1) {
                                childDeletionData.add(Long.parseLong(deletedData[0]));
                            }
                            else {
                                Criteria singleCriteria = null;
                                for (int j = 0; j < deletedData.length; ++j) {
                                    final Criteria innerCriteria = new Criteria(new Column(childTable, childColumns[j]), (Object)Long.parseLong(deletedData[j]), 0);
                                    if (singleCriteria == null) {
                                        singleCriteria = innerCriteria;
                                    }
                                    else {
                                        singleCriteria = singleCriteria.and(innerCriteria);
                                    }
                                }
                                if (criteriaToDelete == null) {
                                    criteriaToDelete = singleCriteria;
                                }
                                else {
                                    criteriaToDelete = criteriaToDelete.or(singleCriteria);
                                }
                            }
                        }
                        if (parentColumnNames.length == 1) {
                            criteriaToDelete = new Criteria(new Column(childTable, childColumns[0]), (Object)childDeletionData.toArray(), 8);
                        }
                        if (criteriaToDelete != null) {
                            if (isAnotherParent.get(childTable)) {
                                totalRowsDeleted += checkForAndDeleteDependentData(childTable, criteriaToDelete, deleteDataDetails.statusKey, deleteDataDetails.isPersistenceDeletion, deleteDataDetails.isDeletionAsynchronous);
                            }
                            else {
                                final long rowsDeleted = doChunkDeletion(childTable, criteriaToDelete, deleteDataDetails.chunkThreshold, deleteDataDetails.isPersistenceDeletion);
                                totalRowsDeleted += rowsDeleted;
                                if (rowsDeleted != 0L) {
                                    DeletionTaskUtil.LOGGER.log(Level.INFO, "Total Dependent rows deleted from " + childTable + " : " + rowsDeleted);
                                }
                            }
                        }
                        if (isExceptionThrown || childTableItr2.hasNext()) {
                            continue;
                        }
                        final DeleteQuery removeDeletedRowQuery = (DeleteQuery)new DeleteQueryImpl("DeletedDependentData");
                        removeDeletedRowQuery.setCriteria(new Criteria(new Column("DeletedDependentData", "ID"), (Object)childDeletedIds.toArray(), 8));
                        DataAccess.delete(removeDeletedRowQuery);
                    }
                    catch (final Exception e) {
                        DeletionTaskUtil.LOGGER.log(Level.SEVERE, () -> "Exception thrown while deleting for child table =" + s3 + " task id =" + deleteDataDetails4.taskID);
                        isExceptionThrown = true;
                    }
                }
            }
        }
        return totalRowsDeleted;
    }
    
    public static void doInDependentDataCleanup() throws DataAccessException {
        doInDependentDataCleanup(DeletionFWProps.chunkThreshold);
    }
    
    public static void doInDependentDataCleanup(final int chunkLimit) throws DataAccessException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Going to do Independent Data Cleanup with chunk limit of : " + n);
        final long startTime = System.currentTimeMillis();
        final JSONObject cleanupDetails = new JSONObject();
        long totalChildRowsDeleted = 0L;
        final DataObject dpDO = (DataObject)new WritableDataObject();
        Row failedReasonRow = null;
        Row totalChildDeletedRow = null;
        Row cleanupDetailsRow = null;
        Row durationRow = null;
        Row statusKeyRow = null;
        final Long taskID = getUniqueTaskID(OPERATION_TYPE.INDEPENDENT_DATA_CLEANUP);
        if (taskID == null) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Got Task id as Null, cant continue cleanup process.");
            return;
        }
        final DataObject diDO = DataAccess.get("DeletionInfo", getDeletionInfoCriteria(taskID));
        final Row deletionInfoRow = diDO.getRow("DeletionInfo");
        boolean isRowsInitialized = false;
        try {
            final SelectQuery dataToDeleteQuery = (SelectQuery)new SelectQueryImpl(new Table("DeletedDependentData"));
            dataToDeleteQuery.addJoin(new Join("DeletedDependentData", "DeletionParameters", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 1));
            dataToDeleteQuery.addSelectColumn(Column.getColumn("DeletedDependentData", "PARENT_ID"));
            dataToDeleteQuery.addSelectColumn(Column.getColumn("DeletedDependentData", "TASK_ID"));
            dataToDeleteQuery.setDistinct(true);
            final DMDataSetWrapper deletedDataSet = executeQuery(dataToDeleteQuery);
            final HashMap<Long, List<Long>> parentAndTaskIds = new HashMap<Long, List<Long>>();
            while (deletedDataSet.next()) {
                final long parentId = (long)deletedDataSet.getValue("PARENT_ID");
                final long excludeTaskId = (long)deletedDataSet.getValue("TASK_ID");
                if (!parentAndTaskIds.containsKey(parentId)) {
                    parentAndTaskIds.put(parentId, new LinkedList<Long>());
                }
                parentAndTaskIds.get(parentId).add(excludeTaskId);
            }
            if (parentAndTaskIds.isEmpty()) {
                DeletionTaskUtil.LOGGER.log(Level.INFO, "There is no dependent data to be cleaned");
                return;
            }
            deletionInfoRow.set("OPERATION_TYPE", (Object)OPERATION_TYPE.INDEPENDENT_DATA_CLEANUP.id);
            deletionInfoRow.set("COMPLETED_STATUS", (Object)COMPLETED_STATUS.STARTED.id);
            deletionInfoRow.set("EXECUTION_START_TIME", (Object)startTime);
            totalChildDeletedRow = getDeletionParamsRow(taskID, "TCRD", -1);
            cleanupDetailsRow = getDeletionParamsRow(taskID, "CUD", "{}");
            durationRow = getDeletionParamsRow(taskID, "TDUR", -1);
            failedReasonRow = getDeletionParamsRow(taskID, "FS", "-");
            statusKeyRow = getDeletionParamsRow(taskID, "StatusKey", taskID);
            isRowsInitialized = true;
            final DeleteDataDetails deleteDataDetails = new DeleteDataDetails();
            deleteDataDetails.taskID = taskID;
            deleteDataDetails.processType = DeleteDataDetails.ProcessType.CLEANUP;
            deleteDataDetails.statusKey = "" + taskID;
            deleteDataDetails.chunkThreshold = chunkLimit;
            deleteDataDetails.isPersistenceDeletion = false;
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Dependent Data cleanup is in progress");
            for (final Long parentId2 : parentAndTaskIds.keySet()) {
                final long unitStartTime = System.currentTimeMillis();
                final long rowsDeleted = deleteAllDependentData(parentId2, deleteDataDetails);
                totalChildRowsDeleted += rowsDeleted;
                final long duration = System.currentTimeMillis() - unitStartTime;
                final JSONObject data = new JSONObject();
                data.put("TCRD", rowsDeleted);
                data.put("CDD", duration);
                cleanupDetails.put(parentId2.toString(), (Object)data);
                DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Total Rows cleaned " + n2 + " : " + n3);
            }
            deletionInfoRow.set("COMPLETED_STATUS", (Object)COMPLETED_STATUS.SUCCESS.id);
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while cleaning", e);
            if (isRowsInitialized) {
                deletionInfoRow.set("COMPLETED_STATUS", (Object)COMPLETED_STATUS.FAILED.id);
                failedReasonRow.set("PARAM_VALUE", (Object)e.getMessage());
            }
            try {
                if (isRowsInitialized) {
                    durationRow.set("PARAM_VALUE", (Object)(System.currentTimeMillis() - startTime));
                    totalChildDeletedRow.set("PARAM_VALUE", (Object)totalChildRowsDeleted);
                    cleanupDetailsRow.set("PARAM_VALUE", (Object)cleanupDetails.toString());
                    diDO.updateRow(deletionInfoRow);
                    DataAccess.update(diDO);
                    dpDO.addRow(failedReasonRow);
                    dpDO.addRow(totalChildDeletedRow);
                    dpDO.addRow(cleanupDetailsRow);
                    dpDO.addRow(durationRow);
                    dpDO.addRow(statusKeyRow);
                    DataAccess.add(dpDO);
                }
            }
            catch (final DataAccessException ex) {
                DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while updating failed status of Deletion cleanup", (Throwable)ex);
            }
            catch (final Exception e) {
                DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while updating in DB", e);
            }
        }
        finally {
            try {
                if (isRowsInitialized) {
                    durationRow.set("PARAM_VALUE", (Object)(System.currentTimeMillis() - startTime));
                    totalChildDeletedRow.set("PARAM_VALUE", (Object)totalChildRowsDeleted);
                    cleanupDetailsRow.set("PARAM_VALUE", (Object)cleanupDetails.toString());
                    diDO.updateRow(deletionInfoRow);
                    DataAccess.update(diDO);
                    dpDO.addRow(failedReasonRow);
                    dpDO.addRow(totalChildDeletedRow);
                    dpDO.addRow(cleanupDetailsRow);
                    dpDO.addRow(durationRow);
                    dpDO.addRow(statusKeyRow);
                    DataAccess.add(dpDO);
                }
            }
            catch (final DataAccessException ex2) {
                DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while updating failed status of Deletion cleanup", (Throwable)ex2);
            }
            catch (final Exception e2) {
                DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while updating in DB", e2);
            }
        }
    }
    
    public static long doChunkDeletion(final String tableName, final Criteria criteria, final int chunk, final boolean isPersistence) throws DataAccessException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
        deleteQuery.setCriteria(criteria);
        setLimitForDeleteQuery(deleteQuery, tableName, chunk);
        long totalRowsDeleted = 0L;
        int nRowsDeleted;
        do {
            nRowsDeleted = doDeletion(deleteQuery, isPersistence);
            totalRowsDeleted += nRowsDeleted;
        } while (nRowsDeleted == chunk);
        return totalRowsDeleted;
    }
    
    public static int doChunkDeletionByGrouping(final String tableName, final Criteria criteria, final int range, final List<GroupByClause> groupByClauseList, final boolean isPersistence) throws SQLException, DataAccessException, QueryConstructionException {
        return doChunkDeletionByGrouping(tableName, criteria, range, groupByClauseList, false, null, isPersistence);
    }
    
    public static int doChunkDeletionByGrouping(final String tableName, final Criteria criteria, final int range, final List<GroupByClause> groupByClauseList, final boolean deleteOnlyDuplicate, final List<Column> uniqueColList, final boolean isPersistence) throws SQLException, QueryConstructionException, DataAccessException {
        int totalRowsDeleted = 0;
        GroupByClause groupByClause;
        if (groupByClauseList.size() == 1) {
            groupByClause = groupByClauseList.get(0);
        }
        else {
            groupByClause = getOptimalGroupByClause(tableName, criteria, range, groupByClauseList);
        }
        final Table table = new Table(tableName);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
        final Column countCol = new Column(tableName, "*").count();
        countCol.setColumnAlias("COUNT_COLUMN");
        countCol.setDataType("INTEGER");
        selectQuery.addSelectColumn(countCol);
        final List grpByColList = groupByClause.getGroupByColumns();
        final List<Column> newGrpByList = new ArrayList<Column>();
        for (final Object o : grpByColList) {
            Column col = (Column)o;
            if (col.getTableAlias() == null) {
                col = new Column(tableName, col.getColumnName());
            }
            selectQuery.addSelectColumn(col);
            newGrpByList.add(col);
        }
        final GroupByClause newGrpByClause = new GroupByClause((List)newGrpByList);
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        selectQuery.setGroupByClause(newGrpByClause);
        try {
            final DMDataSetWrapper dataSet = executeQuery(selectQuery);
            while (dataSet.next()) {
                Criteria subCriteria = createSubCriteria(dataSet, newGrpByClause);
                if (deleteOnlyDuplicate && uniqueColList != null) {
                    final Criteria duplicateCriteria = getDuplicateCriteria(tableName, subCriteria, uniqueColList);
                    subCriteria = subCriteria.and(duplicateCriteria);
                }
                if (subCriteria != null) {
                    totalRowsDeleted += (int)doChunkDeletion(tableName, subCriteria, range, isPersistence);
                }
            }
            return totalRowsDeleted;
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Exception in doChunkDeletion by grouping : ", e);
            throw e;
        }
    }
    
    private static Criteria getDuplicateCriteria(final String tableName, final Criteria criteria, final List<Column> uniqueColList) throws SQLException, QueryConstructionException, DataAccessException {
        Criteria duplicateCriteria = null;
        try {
            final Table table = new Table(tableName);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
            for (Column col : uniqueColList) {
                if (col.getTableAlias() == null) {
                    col = new Column(tableName, col.getColumnName());
                }
                final Column minCol = col.minimum();
                minCol.setColumnAlias("MINIMUM_COLUMN");
                minCol.setDataType("INTEGER");
                selectQuery.addSelectColumn(minCol);
            }
            if (criteria != null) {
                selectQuery.setCriteria(criteria);
            }
            final DMDataSetWrapper dataSet = executeQuery(selectQuery);
            if (dataSet != null) {
                while (dataSet.next()) {
                    for (int i = 0; i < uniqueColList.size(); ++i) {
                        Column column = uniqueColList.get(i);
                        if (column.getTableAlias() == null) {
                            column = new Column(tableName, column.getColumnName());
                        }
                        final String col2 = column.getColumnName();
                        final Object value = dataSet.getValue(i + 1);
                        if (duplicateCriteria == null) {
                            duplicateCriteria = new Criteria(new Column(tableName, col2), value, 1);
                        }
                        else {
                            duplicateCriteria = duplicateCriteria.and(new Criteria(new Column(tableName, col2), value, 1));
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Exception in getting duplicate criteria :", e);
            throw e;
        }
        return duplicateCriteria;
    }
    
    private static Criteria createSubCriteria(final DMDataSetWrapper dataSet, final GroupByClause groupByClause) {
        Criteria subCriteria = null;
        final List groupByCols = groupByClause.getGroupByColumns();
        for (final Object groupByCol : groupByCols) {
            final Column col = (Column)groupByCol;
            final Object value = dataSet.getValue(col.getColumnName());
            if (subCriteria == null) {
                subCriteria = new Criteria(col, value, 0);
            }
            else {
                subCriteria = subCriteria.and(new Criteria(col, value, 0));
            }
        }
        return subCriteria;
    }
    
    private static GroupByClause getOptimalGroupByClause(final String tableName, final Criteria criteria, final int range, final List groupByClauseList) throws SQLException, QueryConstructionException, DataAccessException {
        GroupByClause optimalGrpByClause = groupByClauseList.get(0);
        try {
            for (final Object o : groupByClauseList) {
                final GroupByClause groupByClause = (GroupByClause)o;
                final List groupByColumns = groupByClause.getGroupByColumns();
                final List<Column> newGrpByCols = new ArrayList<Column>();
                for (final Object groupByColumn : groupByColumns) {
                    Column col = (Column)groupByColumn;
                    if (col.getTableAlias() == null) {
                        col = new Column(tableName, col.getColumnName());
                    }
                    newGrpByCols.add(col);
                }
                final GroupByClause newGrpByClause = new GroupByClause((List)newGrpByCols);
                final Table table = new Table(tableName);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
                final Column countCol = new Column(tableName, "*").count();
                countCol.setColumnAlias("COUNT_COLUMN");
                countCol.setDataType("INTEGER");
                selectQuery.addSelectColumn(countCol);
                selectQuery.setCriteria(criteria);
                selectQuery.setGroupByClause(newGrpByClause);
                final DMDataSetWrapper dataSet = executeQuery(selectQuery);
                boolean isOptimal = true;
                while (dataSet.next()) {
                    final int count = (int)dataSet.getValue(1);
                    if (count < range - range * 0.2 || count > range + range * 0.2) {
                        isOptimal = false;
                    }
                }
                if (isOptimal) {
                    optimalGrpByClause = groupByClause;
                }
            }
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Exception in getting optimal group by clause : ", e);
            throw e;
        }
        return optimalGrpByClause;
    }
    
    public static int deleteByQuery(final DeleteQuery deleteQuery, final int range, final boolean isPersistence) throws DataAccessException {
        if (deleteQuery.getJoins() == null) {
            setLimitForDeleteQuery(deleteQuery, range);
            int totalRowsDeleted = 0;
            for (int rowsDeleted = doDeletion(deleteQuery, isPersistence); rowsDeleted == range; rowsDeleted += doDeletion(deleteQuery, isPersistence), totalRowsDeleted += rowsDeleted) {}
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Total Rows Deleted : " + totalRowsDeleted);
            return totalRowsDeleted;
        }
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "TableName: " + deleteQuery2.getTableName());
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "DeleteQuery with join and deleted all at once : " + deleteQuery3.getLimit());
        int totalRowsDeleted = doDeletion(deleteQuery, isPersistence);
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Total Rows Deleted : " + n);
        return totalRowsDeleted;
    }
    
    public static int deleteBySelectQuery(final SelectQuery selectQuery, final List<String> tableList, final int range, final boolean isPersistence) throws DataAccessException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Inside deleteBySelectQuery");
        int totalRowsDeleted = 0;
        for (final String tableName : tableList) {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
            deleteQuery.setCriteria(selectQuery.getCriteria());
            deleteQuery.addSortColumns(selectQuery.getSortColumns());
            final List<Join> joins = selectQuery.getJoins();
            if (joins != null) {
                for (final Join join : joins) {
                    deleteQuery.addJoin(join);
                }
            }
            if (selectQuery.getSortColumns() != null) {
                for (final SortColumn column : selectQuery.getSortColumns()) {
                    if (column.getTableAlias().equalsIgnoreCase(tableName)) {
                        deleteQuery.addSortColumn(column);
                    }
                }
            }
            totalRowsDeleted += deleteByQuery(deleteQuery, range, isPersistence);
        }
        return totalRowsDeleted;
    }
    
    public static void writeInitialPropsInDB(final long taskId, final String statusKey, final DeleteDataDetails deleteDataDetails) throws DataAccessException, SQLException, QueryConstructionException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Inside writeInitialPropsInDB(properties) method");
        final DataObject dpDO = (DataObject)new WritableDataObject();
        dpDO.addRow(getDeletionParamsRow(taskId, "StatusKey", statusKey));
        dpDO.addRow(getDeletionParamsRow(taskId, "CompletedStatus", COMPLETED_STATUS.NOT_STARTED.id));
        dpDO.addRow(getDeletionParamsRow(taskId, "RetryCount", 0));
        dpDO.addRow(getDeletionParamsRow(taskId, "ChunkThreshold", deleteDataDetails.chunkThreshold));
        dpDO.addRow(getDeletionParamsRow(taskId, "PD", deleteDataDetails.isPersistenceDeletion ? PERSISTENCE_DELETION.TRUE.value : PERSISTENCE_DELETION.FALSE.value));
        SelectQuery query = null;
        if (deleteDataDetails.tableName != null) {
            dpDO.addRow(getDeletionParamsRow(taskId, "TableName", deleteDataDetails.tableName));
        }
        if (deleteDataDetails.criteria != null) {
            query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeletionParameters"));
            query.setCriteria(deleteDataDetails.criteria);
        }
        if (deleteDataDetails.tablesList != null) {
            final List<String> tablesList = deleteDataDetails.tablesList;
            for (int i = 0; i < tablesList.size(); ++i) {
                dpDO.addRow(getDeletionParamsRow(taskId, "TableList_" + (i + 1), tablesList.get(i)));
            }
        }
        if (deleteDataDetails.nonDuplicateColumnList != null) {
            final List<Column> nonDupColList = deleteDataDetails.nonDuplicateColumnList;
            for (int i = 0; i < nonDupColList.size(); ++i) {
                final Column nonDupCol = nonDupColList.get(i);
                dpDO.addRow(getDeletionParamsRow(taskId, "NonDuplicateCol" + (i + 1), nonDupCol.getColumnName()));
            }
        }
        if (deleteDataDetails.groupByClauseList != null && deleteDataDetails.groupByClauseList.size() > 0) {
            if (query == null) {
                query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeletionParameters"));
            }
            GroupByClause optimalGroupByClause = deleteDataDetails.groupByClauseList.get(0);
            if (deleteDataDetails.groupByClauseList.size() > 1) {
                optimalGroupByClause = getOptimalGroupByClause(deleteDataDetails.tableName, deleteDataDetails.criteria, deleteDataDetails.chunkThreshold, deleteDataDetails.groupByClauseList);
            }
            query.setGroupByClause(optimalGroupByClause);
        }
        if (deleteDataDetails.selectQuery != null) {
            query = deleteDataDetails.selectQuery;
        }
        if (deleteDataDetails.deleteQuery != null) {
            dpDO.addRow(getDeletionParamsRow(taskId, "Query", deleteQueryToJSON(deleteDataDetails.deleteQuery)));
        }
        if (query != null) {
            dpDO.addRow(getDeletionParamsRow(taskId, "Query", QueryUtil.queryToJson((Query)query)));
        }
        DataAccess.add(dpDO);
    }
    
    public static JSONObject deleteQueryToJSON(final DeleteQuery deleteQuery) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(deleteQuery.getTableName()));
        query.setCriteria(deleteQuery.getCriteria());
        query.addSortColumns(deleteQuery.getSortColumns());
        for (final Join join : deleteQuery.getJoins()) {
            query.addJoin(join);
        }
        query.setRange(new Range(0, deleteQuery.getLimit()));
        return QueryUtil.queryToJson((Query)query);
    }
    
    public static DeleteQuery jsonToDeleteQuery(final JSONObject json) {
        final SelectQuery query = (SelectQuery)QueryUtil.jsonToQuery(json);
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(query.getTableList().get(0).getTableName());
        deleteQuery.setCriteria(query.getCriteria());
        deleteQuery.addSortColumns(query.getSortColumns());
        for (final Join join : query.getJoins()) {
            deleteQuery.addJoin(join);
        }
        deleteQuery.setLimit(query.getRange().getNumberOfObjects());
        return deleteQuery;
    }
    
    private static Long writeInitialPropsInDBForDependentDataDeletion(final String parentStatusKey, final DeleteDataDetails deleteDataDetails) throws DataAccessException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Inside writeInitialPropsInDBForDependentDataDeletion method");
        final Long taskID = getUniqueTaskID(OPERATION_TYPE.DEPENDENT_DATA_DELETION);
        if (taskID == null) {
            return null;
        }
        updateInDeletionInfoTable(taskID, null, COMPLETED_STATUS.PARENT_COMPLETED, System.currentTimeMillis());
        final DataObject dpDO = (DataObject)new WritableDataObject();
        dpDO.addRow(getDeletionParamsRow(taskID, "StatusKey", getStatusKey(parentStatusKey, taskID)));
        dpDO.addRow(getDeletionParamsRow(taskID, "TPRD", deleteDataDetails.totalParentRowsDeleted));
        dpDO.addRow(getDeletionParamsRow(taskID, "TCRD", -1));
        dpDO.addRow(getDeletionParamsRow(taskID, "PDD", deleteDataDetails.parentDeletionDuration));
        dpDO.addRow(getDeletionParamsRow(taskID, "CDD", -1));
        dpDO.addRow(getDeletionParamsRow(taskID, "DED", -1));
        dpDO.addRow(getDeletionParamsRow(taskID, "DD", "{}"));
        dpDO.addRow(getDeletionParamsRow(taskID, "FS", "-"));
        dpDO.addRow(getDeletionParamsRow(taskID, "ChunkThreshold", deleteDataDetails.chunkThreshold));
        dpDO.addRow(getDeletionParamsRow(taskID, "GC", deleteDataDetails.groupThreshold));
        dpDO.addRow(getDeletionParamsRow(taskID, "TableName", deleteDataDetails.tableName));
        dpDO.addRow(getDeletionParamsRow(taskID, "PD", deleteDataDetails.isPersistenceDeletion ? PERSISTENCE_DELETION.TRUE.value : PERSISTENCE_DELETION.FALSE.value));
        DataAccess.add(dpDO);
        return taskID;
    }
    
    private static void writeFinalPropsInDBForDependentDataDeletion(final DeleteDataDetails deleteDataDetails, final boolean isSuccess, final String failureReason, final Long childDeletedCount, final Long childDeletedDuration, final HashMap<Long, Long> deletedDetails) throws DataAccessException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Inside writeFinalPropsInDBForDependentDataDeletion method");
        final DataObject dpDO = (DataObject)new WritableDataObject();
        if (isSuccess) {
            updateInDeletionInfoTable(deleteDataDetails.taskID, null, COMPLETED_STATUS.SUCCESS, null);
        }
        else {
            updateInDeletionInfoTable(deleteDataDetails.taskID, null, COMPLETED_STATUS.FAILED, null);
            dpDO.updateBlindly(getDeletionParamsRow(deleteDataDetails.taskID, "FS", failureReason));
        }
        if (childDeletedCount != null) {
            dpDO.updateBlindly(getDeletionParamsRow(deleteDataDetails.taskID, "TCRD", childDeletedCount));
        }
        if (childDeletedDuration != null) {
            dpDO.updateBlindly(getDeletionParamsRow(deleteDataDetails.taskID, "CDD", childDeletedDuration));
        }
        dpDO.updateBlindly(getDeletionParamsRow(deleteDataDetails.taskID, "DED", System.currentTimeMillis() - deleteDataDetails.parentDeletionStartTime));
        if (deletedDetails != null) {
            dpDO.updateBlindly(getDeletionParamsRow(deleteDataDetails.taskID, "DD", new JSONObject((Map)deletedDetails).toString()));
        }
        DataAccess.update(dpDO);
    }
    
    public static Long getUniqueTaskID(final OPERATION_TYPE operationType) throws DataAccessException {
        final WritableDataObject idObject = new WritableDataObject();
        final Row row = new Row("DeletionInfo");
        row.set("OPERATION_TYPE", (Object)operationType.id);
        row.set("VERSION", (Object)1L);
        idObject.addRow(row);
        final DataObject updatedObject = DataAccess.add((DataObject)idObject);
        final Long taskId = (Long)updatedObject.getRow("DeletionInfo").get("TASK_ID");
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Fetched ID : " + n + " for operation : " + operation_TYPE.id);
        return taskId;
    }
    
    public static Row getDeletionParamsRow(final Long taskID, final String key, final Object value) {
        final Row row = new Row("DeletionParameters");
        row.set("TASK_ID", (Object)taskID);
        row.set("TASK_PARAM", (Object)key);
        row.set("PARAM_VALUE", (Object)value.toString());
        return row;
    }
    
    public static Row getDeletionInfoRow(final Long taskID, final int operationType, final int completedStatus, final long executionStartTime) {
        final Row row = new Row("DeletionInfo");
        row.set("TASK_ID", (Object)taskID);
        row.set("OPERATION_TYPE", (Object)operationType);
        row.set("COMPLETED_STATUS", (Object)completedStatus);
        row.set("EXECUTION_START_TIME", (Object)executionStartTime);
        row.set("VERSION", (Object)1L);
        return row;
    }
    
    public static void updateDeletionInfoRow(final Row row, final Long taskID, final OPERATION_TYPE operationType, final COMPLETED_STATUS completedStatus, final Long executionStartTime) {
        if (taskID != null) {
            row.set("TASK_ID", (Object)taskID);
        }
        if (operationType != null) {
            row.set("OPERATION_TYPE", (Object)operationType.id);
        }
        if (completedStatus != null) {
            row.set("COMPLETED_STATUS", (Object)completedStatus.id);
        }
        if (executionStartTime != null) {
            row.set("EXECUTION_START_TIME", (Object)executionStartTime);
        }
    }
    
    public static Criteria getDeletionInfoCriteria(final Long taskID) {
        return new Criteria(Column.getColumn("DeletionInfo", "TASK_ID"), (Object)taskID, 0).and(getDeletionInfoVersionCheckCriteria());
    }
    
    public static Criteria getDeletionInfoCriteria(final int compStatusQueryConstant, final COMPLETED_STATUS[] completedStatuses, final int oprTypeQueryConstant, final OPERATION_TYPE[] operationTypes) {
        final boolean isIn_compStatus = compStatusQueryConstant == 8 || compStatusQueryConstant == 0;
        final boolean isIn_OperType = oprTypeQueryConstant == 8 || oprTypeQueryConstant == 0;
        if ((completedStatuses == null || completedStatuses.length == 0) && (operationTypes == null || operationTypes.length == 0)) {
            return null;
        }
        Criteria result = getDeletionInfoVersionCheckCriteria();
        if (completedStatuses != null && completedStatuses.length != 0) {
            if (completedStatuses.length == 1) {
                result = result.and(new Criteria(Column.getColumn("DeletionInfo", "COMPLETED_STATUS"), (Object)completedStatuses[0].id, (int)(isIn_compStatus ? 0 : 1)));
            }
            else {
                final int[] statusIds = new int[completedStatuses.length];
                for (int i = 0; i < completedStatuses.length; ++i) {
                    statusIds[i] = completedStatuses[i].id;
                }
                result = result.and(new Criteria(Column.getColumn("DeletionInfo", "COMPLETED_STATUS"), (Object)statusIds, isIn_compStatus ? 8 : 9));
            }
        }
        if (operationTypes != null && operationTypes.length != 0) {
            if (operationTypes.length == 1) {
                result = result.and(new Criteria(Column.getColumn("DeletionInfo", "OPERATION_TYPE"), (Object)operationTypes[0].id, (int)(isIn_OperType ? 0 : 1)));
            }
            final int[] statusIds = new int[operationTypes.length];
            for (int i = 0; i < operationTypes.length; ++i) {
                statusIds[i] = operationTypes[i].id;
            }
            result = result.and(new Criteria(Column.getColumn("DeletionInfo", "OPERATION_TYPE"), (Object)statusIds, isIn_OperType ? 8 : 9));
        }
        return result;
    }
    
    public static Criteria getDeletionInfoCriteria(final COMPLETED_STATUS[] completedStatuses, final OPERATION_TYPE[] operationTypes) {
        return getDeletionInfoCriteria(8, completedStatuses, 8, operationTypes);
    }
    
    public static Criteria getDeletionInfoCriteria(final COMPLETED_STATUS... completedStatuses) {
        return getDeletionInfoCriteria(8, completedStatuses);
    }
    
    public static Criteria getDeletionInfoCriteria(final int queryConstant, final COMPLETED_STATUS... completedStatuses) {
        return getDeletionInfoCriteria(queryConstant, completedStatuses, -1, null);
    }
    
    public static Criteria getDeletionInfoCriteria(final OPERATION_TYPE... operationTypes) {
        return getDeletionInfoCriteria(8, operationTypes);
    }
    
    public static Criteria getDeletionInfoCriteria(final int queryConstant, final OPERATION_TYPE... operationTypes) {
        return getDeletionInfoCriteria(-1, null, queryConstant, operationTypes);
    }
    
    private static Criteria getDeletionInfoVersionCheckCriteria() {
        return new Criteria(Column.getColumn("DeletionInfo", "VERSION"), (Object)1L, 0);
    }
    
    public static Criteria getDeletionParamsCriteria(final Long taskID) {
        return new Criteria(Column.getColumn("DeletionParameters", "TASK_ID"), (Object)taskID, 0);
    }
    
    public static Criteria getDeletionParamsCriteria(final Long taskID, final String key) {
        return new Criteria(Column.getColumn("DeletionParameters", "TASK_ID"), (Object)taskID, 0).and(new Criteria(Column.getColumn("DeletionParameters", "TASK_PARAM"), (Object)key, 0));
    }
    
    public static Criteria getDeletionParamsCriteria(final String key, final Object value) {
        return new Criteria(Column.getColumn("DeletionParameters", "TASK_PARAM"), (Object)key, 0).and(new Criteria(Column.getColumn("DeletionParameters", "PARAM_VALUE"), value, 0, false));
    }
    
    public static Criteria getDependentDeletionCriteria(final Long taskId, final Long parentId, final String... values) {
        Criteria criteria = null;
        if (taskId != null) {
            criteria = new Criteria(Column.getColumn("DeletedDependentData", "TASK_ID"), (Object)taskId, 0);
        }
        if (parentId != null) {
            final Criteria parentIdCriteria = new Criteria(Column.getColumn("DeletedDependentData", "PARENT_ID"), (Object)parentId, 0);
            criteria = ((criteria == null) ? parentIdCriteria : criteria.and(parentIdCriteria));
        }
        if (values != null && values.length > 0) {
            final Criteria valueCriteria = new Criteria(Column.getColumn("DeletedDependentData", "DELETED_DATA"), (values.length == 1) ? values[0] : values, (values.length == 1) ? 0 : 8);
            criteria = ((criteria == null) ? valueCriteria : criteria.and(valueCriteria));
        }
        return criteria;
    }
    
    private static void updateInDeletionParamsDB(final long taskID, final String taskParam, final Object value) throws DataAccessException {
        final DataObject dpDO = (DataObject)new WritableDataObject();
        dpDO.updateBlindly(getDeletionParamsRow(taskID, taskParam, value));
        DataAccess.update(dpDO);
    }
    
    private static void updateInDeletionInfoTable(final long taskID, final OPERATION_TYPE operationType, final COMPLETED_STATUS completedStatus, final Long executionStartTime) throws DataAccessException {
        final DataObject diDO = DataAccess.get("DeletionInfo", getDeletionInfoCriteria(taskID));
        final Row row = diDO.getRow("DeletionInfo");
        updateDeletionInfoRow(row, taskID, operationType, completedStatus, executionStartTime);
        diDO.updateRow(row);
        DataAccess.update(diDO);
    }
    
    public static void updateStartedStatusInDB(final Long taskID, final long time) throws DataAccessException {
        updateInDeletionInfoTable(taskID, null, COMPLETED_STATUS.STARTED, time);
    }
    
    public static void updateCompletedStatusInDB(final Long taskID) throws DataAccessException {
        updateInDeletionInfoTable(taskID, null, COMPLETED_STATUS.SUCCESS, null);
    }
    
    public static void updateFailedStatusInDB(final Long taskID) throws DataAccessException {
        updateInDeletionInfoTable(taskID, null, COMPLETED_STATUS.FAILED, null);
    }
    
    public static void updateAbortedStatusInDB(final Long taskID) throws DataAccessException {
        updateInDeletionInfoTable(taskID, null, COMPLETED_STATUS.ABORTED, null);
    }
    
    public static List<DeleteDataDetails> getDataToDeleteFromDB() throws DataAccessException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Inside getPropertiesToDeleteFromDB() method");
        final List<DeleteDataDetails> propsList = new ArrayList<DeleteDataDetails>();
        final SelectQuery diSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeletionInfo"));
        diSelectQuery.addSelectColumn(Column.getColumn("DeletionInfo", "TASK_ID"));
        diSelectQuery.setCriteria(getDeletionInfoCriteria(8, new COMPLETED_STATUS[] { COMPLETED_STATUS.NOT_STARTED, COMPLETED_STATUS.FAILED, COMPLETED_STATUS.STARTED }, 9, new OPERATION_TYPE[] { OPERATION_TYPE.DEPENDENT_DATA_DELETION, OPERATION_TYPE.INDEPENDENT_DATA_CLEANUP }));
        final DataObject diDO = DataAccess.get(diSelectQuery);
        final Iterator diItr = diDO.getRows("DeletionInfo");
        while (diItr.hasNext()) {
            final Row row = diItr.next();
            final Long taskId = (Long)row.get("TASK_ID");
            propsList.add(getDataToDeleteFromDB(taskId));
        }
        return propsList;
    }
    
    public static DeleteDataDetails getDataToDeleteFromDB(final long taskId) throws DataAccessException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Inside getPropertiesToDeleteFromDB() method");
        final DataObject diDO = DataAccess.get("DeletionInfo", getDeletionInfoCriteria(taskId));
        final DataObject dpDO = DataAccess.get("DeletionParameters", getDeletionParamsCriteria(taskId));
        final Row diRow = diDO.getRow("DeletionInfo");
        final DeleteDataDetails deleteDataDetails = new DeleteDataDetails();
        final Long taskID = (Long)diRow.get("TASK_ID");
        final Long operationType = (Long)diRow.get("OPERATION_TYPE");
        JSONObject queryJSON = null;
        final List<String> taskTableList = new LinkedList<String>();
        final List<Column> taskNonDupColsList = new LinkedList<Column>();
        deleteDataDetails.taskID = taskID;
        if (operationType == OPERATION_TYPE.DEPENDENT_DATA_DELETION.id || operationType == OPERATION_TYPE.INDEPENDENT_DATA_CLEANUP.id) {
            return null;
        }
        deleteDataDetails.operationType = OPERATION_TYPE.getOperationType(operationType);
        final Iterator dpItr = dpDO.getRows("DeletionParameters");
        while (dpItr.hasNext()) {
            final Row dpRow = dpItr.next();
            final String paramName = (String)dpRow.get("TASK_PARAM");
            final Object value = dpRow.get("PARAM_VALUE");
            if (paramName.equals("StatusKey")) {
                deleteDataDetails.statusKey = (String)value;
            }
            else if (paramName.equals("ChunkThreshold")) {
                deleteDataDetails.chunkThreshold = Integer.parseInt((String)value);
            }
            else if (paramName.equals("TableName")) {
                deleteDataDetails.tableName = (String)value;
            }
            else if (paramName.equals("Query")) {
                try {
                    queryJSON = new JSONObject(value.toString());
                }
                catch (final Exception e) {
                    DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while parsing query json", e);
                }
            }
            else if (paramName.startsWith("TableList_")) {
                taskTableList.add((String)value);
            }
            else if (paramName.equals("PD")) {
                deleteDataDetails.isPersistenceDeletion = (value != null && PERSISTENCE_DELETION.TRUE.equals((String)value));
            }
            else {
                if (!paramName.startsWith("NonDuplicateCol")) {
                    continue;
                }
                final String nonDupCol = (String)value;
                final Column col = new Column((String)null, nonDupCol);
                taskNonDupColsList.add(col);
            }
        }
        if (operationType == OPERATION_TYPE.DELETE_WITH_SELECT_QUERY.id) {
            deleteDataDetails.selectQuery = (SelectQuery)QueryUtil.jsonToQuery(queryJSON);
        }
        else if (operationType == OPERATION_TYPE.DELETE_WITH_DELETE_QUERY.id) {
            deleteDataDetails.deleteQuery = jsonToDeleteQuery(queryJSON);
        }
        else if (queryJSON != null) {
            final SelectQuery selQuery = (SelectQuery)QueryUtil.jsonToQuery(queryJSON);
            deleteDataDetails.criteria = selQuery.getCriteria();
            if (selQuery.getGroupByClause() != null) {
                deleteDataDetails.groupByClauseList.add(selQuery.getGroupByClause());
            }
        }
        deleteDataDetails.tablesList = taskTableList;
        if (taskNonDupColsList.size() > 0) {
            deleteDataDetails.nonDuplicateColumnList = taskNonDupColsList;
        }
        return deleteDataDetails;
    }
    
    public static List<DeleteDataDetails> getDependentDataToBeDeleted() {
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Fetching Dependent Data to be deleted");
        final List<DeleteDataDetails> result = new LinkedList<DeleteDataDetails>();
        try {
            final DataObject diPossibleDo = DataAccess.get("DeletionInfo", getDeletionInfoCriteria(new COMPLETED_STATUS[] { COMPLETED_STATUS.PARENT_COMPLETED, COMPLETED_STATUS.DEPENDENT_DELETION_STARTED }, new OPERATION_TYPE[] { OPERATION_TYPE.DEPENDENT_DATA_DELETION }));
            final Iterator diItr = diPossibleDo.getRows("DeletionInfo");
            while (diItr.hasNext()) {
                final Row diRow = diItr.next();
                final long taskId = (long)diRow.get("TASK_ID");
                result.add(getDependentDataToBeDeleted(taskId));
            }
        }
        catch (final DataAccessException e) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception in getting properties from DB :", (Throwable)e);
        }
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Got " + list.size() + " values to be deleted");
        return result;
    }
    
    public static DeleteDataDetails getDependentDataToBeDeleted(final long taskID) {
        final DeleteDataDetails deleteDataDetails = new DeleteDataDetails();
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Fetching Dependent Data to be deleted");
        try {
            final DataObject diDO = DataAccess.get("DeletionInfo", getDeletionInfoCriteria(taskID));
            final Row infoRow = diDO.getRow("DeletionInfo");
            final DataObject dpDO = DataAccess.get("DeletionParameters", getDeletionParamsCriteria(taskID));
            deleteDataDetails.taskID = taskID;
            deleteDataDetails.processType = DeleteDataDetails.ProcessType.DEPENDENT_DELETION;
            deleteDataDetails.parentDeletionStartTime = (long)infoRow.get("EXECUTION_START_TIME");
            deleteDataDetails.operationType = OPERATION_TYPE.DEPENDENT_DATA_DELETION;
            final Iterator dpItr = dpDO.getRows("DeletionParameters");
            while (dpItr.hasNext()) {
                final Row dpRow = dpItr.next();
                final String taskParam = (String)dpRow.get("TASK_PARAM");
                final String paramValue = (String)dpRow.get("PARAM_VALUE");
                if (taskParam.equalsIgnoreCase("StatusKey")) {
                    deleteDataDetails.statusKey = paramValue;
                }
                else if (taskParam.equalsIgnoreCase("ChunkThreshold")) {
                    deleteDataDetails.chunkThreshold = Integer.parseInt(paramValue);
                }
                else if (taskParam.equalsIgnoreCase("TableName")) {
                    deleteDataDetails.tableName = paramValue;
                }
                else if (taskParam.equalsIgnoreCase("GC")) {
                    deleteDataDetails.groupThreshold = Integer.parseInt(paramValue);
                }
                else if (taskParam.equalsIgnoreCase("PDD")) {
                    deleteDataDetails.parentDeletionDuration = Long.parseLong(paramValue);
                }
                else if (taskParam.equalsIgnoreCase("TPRD")) {
                    deleteDataDetails.totalParentRowsDeleted = Long.parseLong(paramValue);
                }
                else {
                    if (!taskParam.equalsIgnoreCase("PD")) {
                        continue;
                    }
                    deleteDataDetails.isPersistenceDeletion = PERSISTENCE_DELETION.TRUE.equals(paramValue);
                }
            }
        }
        catch (final DataAccessException e) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception in getting properties from DB :", (Throwable)e);
        }
        return deleteDataDetails;
    }
    
    private static int doDeletion(final DeleteQuery query, final boolean isPersistence) throws DataAccessException {
        if (isPersistence) {
            return SyMUtil.getPersistence().delete(query);
        }
        return DataAccess.delete(query);
    }
    
    private static void doDeletion(final Criteria criteria, final boolean isPersistence) throws DataAccessException {
        if (isPersistence) {
            SyMUtil.getPersistence().delete(criteria);
        }
        else {
            DataAccess.delete(criteria);
        }
    }
    
    private static HashMap<Long, HashMap<String, Long>> removeOrphanRowWithLeftJoin(final long chunk) throws DataAccessException {
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Removing orphan rows by Left Joining parent table");
        final HashMap<Long, HashMap<String, Long>> result = new HashMap<Long, HashMap<String, Long>>();
        final SelectQuery parentQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ParentDependencyInfo"));
        parentQuery.addSelectColumn(Column.getColumn("ParentDependencyInfo", "ID"));
        parentQuery.addSelectColumn(Column.getColumn("ParentDependencyInfo", "TABLE_NAME"));
        parentQuery.addSelectColumn(Column.getColumn("ParentDependencyInfo", "COLUMN_NAME"));
        final DataObject parentDO = SyMUtil.getCachedPersistence().get(parentQuery);
        final Iterator<Row> parentItr = parentDO.getRows("ParentDependencyInfo");
        while (parentItr.hasNext()) {
            final Row parentRow = parentItr.next();
            final long parentID = (long)parentRow.get("ID");
            final String parentTableName = (String)parentRow.get("TABLE_NAME");
            final String[] parentColumnNames = ((String)parentRow.get("COLUMN_NAME")).split(",");
            final List<String> childTablesToExclude = DeletionFWProps.orphanCleanupSkipMap.get(parentTableName.toLowerCase());
            DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "---------------------Scanning for Parent table : " + s);
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
            final DataObject childDO = SyMUtil.getCachedPersistence().get(childQuery);
            final Iterator<Row> childItr = childDO.getRows("ChildDependencyInfo");
            while (childItr.hasNext()) {
                final Row childRow = childItr.next();
                final String childTableName = (String)childRow.get("CHILD_TABLE_NAME");
                final String[] childColumnNames = ((String)childRow.get("CHILD_COLUMN_NAME")).split(",");
                if (!isTablePresent(childTableName)) {
                    DeletionTaskUtil.LOGGER.log(Level.WARNING, () -> "Table not present : " + s2 + ". So skipping deletion");
                }
                else if (childTablesToExclude != null && childTablesToExclude.contains(childTableName.toLowerCase())) {
                    DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "---------------------Skipping cleanup for : [" + s3 + "->" + s4 + "]");
                }
                else {
                    DeletionTaskUtil.LOGGER.log(Level.FINE, () -> "---------------------Scanning for Child table : " + s5);
                    final SelectQuery orphanQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(childTableName));
                    orphanQuery.addJoin(new Join(childTableName, parentTableName, childColumnNames, parentColumnNames, 1));
                    orphanQuery.setCriteria(nullCriteria);
                    for (final String column2 : childColumnNames) {
                        orphanQuery.addSelectColumn(Column.getColumn(childTableName, column2));
                    }
                    long count = 0L;
                    try {
                        final DMDataSetWrapper ds = executeQuery(orphanQuery);
                        if (childColumnNames.length == 1) {
                            boolean isCompleted;
                            do {
                                final List<Object> primaryValues = new ArrayList<Object>();
                                int i = 1;
                                isCompleted = true;
                                while (ds.next()) {
                                    primaryValues.add(ds.getValue(childColumnNames[0]));
                                    if (i == chunk) {
                                        isCompleted = false;
                                        break;
                                    }
                                    ++i;
                                }
                                if (!primaryValues.isEmpty()) {
                                    DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Deleting orphan rows using IN Criteria. Table : " + s6 + " Count : " + list.size());
                                    final Criteria deleteCriteria = new Criteria(Column.getColumn(childTableName, childColumnNames[0]), (Object)primaryValues.toArray(), 8);
                                    final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(childTableName);
                                    deleteQuery.setCriteria(deleteCriteria);
                                    count += DataAccess.delete(deleteQuery);
                                }
                            } while (!isCompleted);
                        }
                        else {
                            boolean isCompleted;
                            do {
                                Criteria deleteCriteria2 = null;
                                int i = 1;
                                isCompleted = true;
                                while (ds.next()) {
                                    Criteria rowCriteria = null;
                                    for (final String column3 : childColumnNames) {
                                        final Criteria criteria = new Criteria(Column.getColumn(childTableName, column3), ds.getValue(column3), 0);
                                        if (rowCriteria == null) {
                                            rowCriteria = criteria;
                                        }
                                        else {
                                            rowCriteria = rowCriteria.and(criteria);
                                        }
                                    }
                                    if (deleteCriteria2 == null) {
                                        deleteCriteria2 = rowCriteria;
                                    }
                                    else {
                                        deleteCriteria2 = deleteCriteria2.or(rowCriteria);
                                    }
                                    if (i == chunk) {
                                        isCompleted = false;
                                        break;
                                    }
                                    ++i;
                                }
                                if (deleteCriteria2 != null) {
                                    DeletionTaskUtil.LOGGER.log(Level.INFO, "Deleting orphan rows using OR OR Criteria. Table : " + childTableName + " Count : " + (i - 1));
                                    final DeleteQuery deleteQuery2 = (DeleteQuery)new DeleteQueryImpl(childTableName);
                                    deleteQuery2.setCriteria(deleteCriteria2);
                                    count += DataAccess.delete(deleteQuery2);
                                }
                            } while (!isCompleted);
                        }
                    }
                    catch (final Exception e) {
                        DeletionTaskUtil.LOGGER.log(Level.SEVERE, e, () -> "Exception while executing orphan row delete count query for " + s7 + "->" + s8);
                    }
                    if (count == 0L) {
                        continue;
                    }
                    DeletionTaskUtil.LOGGER.log(Level.FINE, "Total orphan rows deleted for " + parentTableName + "->" + childTableName + " is " + count);
                    if (!result.containsKey(parentID)) {
                        result.put(parentID, new HashMap<String, Long>());
                    }
                    result.get(parentID).put(childTableName, count);
                }
            }
        }
        return result;
    }
    
    public static boolean doOrphanRowCleanup(final boolean forceCleanup) {
        Long orphanCleanupTaskId = null;
        try {
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Inside doOrphanRowCleanup");
            final long startTime = System.currentTimeMillis();
            final DataObject diDO = DataAccess.get("DeletionInfo", getDeletionInfoCriteria(OPERATION_TYPE.ORPHAN_CLEANUP_INFO));
            if (diDO.isEmpty()) {
                orphanCleanupTaskId = getUniqueTaskID(OPERATION_TYPE.ORPHAN_CLEANUP_INFO);
            }
            else {
                orphanCleanupTaskId = (long)diDO.getRow("DeletionInfo").get("TASK_ID");
                final Long lastExecutionTime = (Long)diDO.getRow("DeletionInfo").get("EXECUTION_START_TIME");
                DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Last Orphan row cleanup was done at " + n);
                if (lastExecutionTime != null && lastExecutionTime > System.currentTimeMillis() - DeletionFWProps.orphanCleanupDuration * 24L * 60L * 60L * 1000L) {
                    if (!forceCleanup) {
                        DeletionTaskUtil.LOGGER.log(Level.INFO, "Skipping Orphan row cleanup");
                        return false;
                    }
                    DeletionTaskUtil.LOGGER.log(Level.WARNING, "Orphan row cleanup is done before schedule because it is forced.");
                }
            }
            updateInDeletionInfoTable(orphanCleanupTaskId, null, COMPLETED_STATUS.STARTED, startTime);
            final HashMap<Long, HashMap<String, Long>> result = removeOrphanRowWithLeftJoin(DeletionFWProps.chunkThreshold);
            final JSONObject orphanJSON = new JSONObject((Map)result);
            String orphanJSONStr = orphanJSON.toString();
            final int limit = DeletionFWProps.deletionParamsColumnSize;
            final List<String> orphanJSONList = new LinkedList<String>();
            while (orphanJSONStr.length() > limit) {
                orphanJSONList.add(orphanJSONStr.substring(0, limit));
                orphanJSONStr = orphanJSONStr.substring(limit);
            }
            if (orphanJSONStr.length() > 0) {
                orphanJSONList.add(orphanJSONStr);
            }
            final long totalTime = System.currentTimeMillis() - startTime;
            final DataObject dpDO = DataAccess.get("DeletionParameters", getDeletionParamsCriteria(orphanCleanupTaskId));
            final Row durationRow = getDeletionParamsRow(orphanCleanupTaskId, "OCLD", totalTime);
            final Row foldCountRow = getDeletionParamsRow(orphanCleanupTaskId, "OCLMF", orphanJSONList.size());
            if (dpDO.getRow("DeletionParameters", getDeletionParamsCriteria(orphanCleanupTaskId, "OCLD")) == null) {
                dpDO.addRow(durationRow);
            }
            else {
                dpDO.updateRow(durationRow);
            }
            if (dpDO.getRow("DeletionParameters", getDeletionParamsCriteria(orphanCleanupTaskId, "OCLMF")) == null) {
                dpDO.addRow(foldCountRow);
            }
            else {
                dpDO.updateRow(durationRow);
            }
            int i = 0;
            for (final String str : orphanJSONList) {
                final String paramName = "OCLJ_" + i++;
                if (dpDO.getRow("DeletionParameters", getDeletionParamsCriteria(orphanCleanupTaskId, paramName)) != null) {
                    dpDO.updateRow(getDeletionParamsRow(orphanCleanupTaskId, paramName, str));
                }
                else {
                    dpDO.addRow(getDeletionParamsRow(orphanCleanupTaskId, paramName, str));
                }
            }
            DataAccess.update(dpDO);
            updateInDeletionInfoTable(orphanCleanupTaskId, null, COMPLETED_STATUS.SUCCESS, null);
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Orphan Cleanup Detail updated in DB");
            return true;
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception caught while cleanup", e);
            if (orphanCleanupTaskId != null) {
                try {
                    updateInDeletionInfoTable(orphanCleanupTaskId, null, COMPLETED_STATUS.FAILED, null);
                }
                catch (final DataAccessException ex) {
                    DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while updating failure status in db", e);
                }
            }
            return false;
        }
    }
    
    public static void addToQueue(final long taskId, final OPERATION_TYPE operationType) throws DeletionQueueFailedException {
        try {
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Adding props to Queue");
            final DCQueue queue = DCQueueHandler.getQueue("deletion-fw-data");
            final DCQueueData queueData = new DCQueueData();
            queueData.fileName = taskId + "-" + System.currentTimeMillis() + ".txt";
            queueData.postTime = System.currentTimeMillis();
            queueData.queueData = taskId;
            queueData.queueDataType = operationType.id;
            queueData.priority = true;
            queue.addToQueue(queueData);
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Add to Queue failed", e);
            throw new DeletionQueueFailedException("Queue addition for task-id=" + taskId + " failed", e);
        }
    }
    
    public static void doDeletionInfoDBCleanup() {
        DeletionTaskUtil.LOGGER.log(Level.INFO, "Inside doDeletionInfoDBCleanup");
        long historyCleanupTaskId;
        try {
            final DataObject diDO = DataAccess.get("DeletionInfo", getDeletionInfoCriteria(OPERATION_TYPE.HISTORY_CLEANUP_INFO));
            if (diDO.isEmpty()) {
                historyCleanupTaskId = getUniqueTaskID(OPERATION_TYPE.HISTORY_CLEANUP_INFO);
            }
            else {
                historyCleanupTaskId = (long)diDO.getRow("DeletionInfo").get("TASK_ID");
            }
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Exception while fetching historyCleanupTaskId", e);
            return;
        }
        try {
            updateStartedStatusInDB(historyCleanupTaskId, System.currentTimeMillis());
            final long dayToMilli = 86400000L;
            final long currentTime = System.currentTimeMillis();
            final long successTime = currentTime - DeletionFWProps.dbHistoryCleanupDuration_success * dayToMilli;
            final long failureTime = currentTime - DeletionFWProps.dbHistoryCleanupDuration_failed * dayToMilli;
            final long abortedTime = currentTime - DeletionFWProps.dbHistoryCleanupDuration_aborted * dayToMilli;
            final long otherTime = currentTime - DeletionFWProps.dbHistoryCleanupDuration_other * dayToMilli;
            Criteria criteria = null;
            if (DeletionFWProps.dbHistoryCleanupDuration_success >= 0) {
                final Criteria deleteCriteria_success = criteria = getDeletionInfoCriteria(COMPLETED_STATUS.SUCCESS).and(new Criteria(Column.getColumn("DeletionInfo", "EXECUTION_START_TIME"), (Object)successTime, 7)).and(new Criteria(Column.getColumn("DeletionInfo", "OPERATION_TYPE"), (Object)2000, 7));
            }
            if (DeletionFWProps.dbHistoryCleanupDuration_failed >= 0) {
                final Criteria deleteCriteria_failure = getDeletionInfoCriteria(COMPLETED_STATUS.FAILED).and(new Criteria(Column.getColumn("DeletionInfo", "EXECUTION_START_TIME"), (Object)failureTime, 7)).and(new Criteria(Column.getColumn("DeletionInfo", "OPERATION_TYPE"), (Object)2000, 7));
                criteria = ((criteria == null) ? deleteCriteria_failure : criteria.or(deleteCriteria_failure));
            }
            if (DeletionFWProps.dbHistoryCleanupDuration_aborted >= 0) {
                final Criteria deleteCriteria_aborted = getDeletionInfoCriteria(COMPLETED_STATUS.ABORTED).and(new Criteria(Column.getColumn("DeletionInfo", "EXECUTION_START_TIME"), (Object)abortedTime, 7)).and(new Criteria(Column.getColumn("DeletionInfo", "OPERATION_TYPE"), (Object)2000, 7));
                criteria = ((criteria == null) ? deleteCriteria_aborted : criteria.or(deleteCriteria_aborted));
            }
            if (DeletionFWProps.dbHistoryCleanupDuration_other >= 0) {
                final Criteria deleteCriteria_other = getDeletionInfoCriteria(COMPLETED_STATUS.PARENT_COMPLETED, COMPLETED_STATUS.DEPENDENT_DELETION_STARTED, COMPLETED_STATUS.NOT_STARTED).and(new Criteria(Column.getColumn("DeletionInfo", "EXECUTION_START_TIME"), (Object)otherTime, 7)).and(new Criteria(Column.getColumn("DeletionInfo", "OPERATION_TYPE"), (Object)2000, 7));
                criteria = ((criteria == null) ? deleteCriteria_other : criteria.or(deleteCriteria_other));
            }
            if (criteria != null) {
                final DeleteQuery query = (DeleteQuery)new DeleteQueryImpl("DeletionInfo");
                query.setCriteria(criteria);
                final long deletedRows = DataAccess.delete(query);
                DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "In doDeletionInfoDBCleanup Number of Rows Cleaned : " + n);
            }
            updateCompletedStatusInDB(historyCleanupTaskId);
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.INFO, "Exception in cleaning DB", e);
            try {
                updateFailedStatusInDB(historyCleanupTaskId);
            }
            catch (final DataAccessException ex) {
                DeletionTaskUtil.LOGGER.log(Level.INFO, "Exception while updating failure status in DB", (Throwable)ex);
            }
        }
    }
    
    public static HashMap<String, Long> getParentDependencyTables() throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ParentDependencyInfo"));
        query.addSelectColumn(Column.getColumn("ParentDependencyInfo", "TABLE_NAME"));
        query.addSelectColumn(Column.getColumn("ParentDependencyInfo", "ID"));
        final DataObject parentDO = SyMUtil.getCachedPersistence().get(query);
        final HashMap<String, Long> parentDependencyTables = new HashMap<String, Long>();
        final Iterator<Row> pItr = parentDO.getRows("ParentDependencyInfo");
        while (pItr.hasNext()) {
            final Row row = pItr.next();
            parentDependencyTables.put(((String)row.get("TABLE_NAME")).toLowerCase(), (Long)row.get("ID"));
        }
        return parentDependencyTables;
    }
    
    public static Set<String> getChildDependencyTables() throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ChildDependencyInfo"));
        final Column tableNames = Column.getColumn("ChildDependencyInfo", "CHILD_TABLE_NAME");
        query.addSelectColumn(tableNames);
        query.addSelectColumn(Column.getColumn("ChildDependencyInfo", "PARENT_ID"));
        final DataObject childDO = SyMUtil.getCachedPersistence().get(query);
        final Set<String> childDependencyTables = new HashSet<String>();
        final Iterator<Row> cItr = childDO.getRows("ChildDependencyInfo");
        while (cItr.hasNext()) {
            final Row row = cItr.next();
            childDependencyTables.add(((String)row.get("CHILD_TABLE_NAME")).toLowerCase());
        }
        return childDependencyTables;
    }
    
    private static boolean canDoDependentDeletion(final Row deletionInfoRow) {
        final int operationType = ((Long)deletionInfoRow.get("OPERATION_TYPE")).intValue();
        final int completedStatus = ((Long)deletionInfoRow.get("COMPLETED_STATUS")).intValue();
        if (!OPERATION_TYPE.DEPENDENT_DATA_DELETION.equals(operationType)) {
            return false;
        }
        if (COMPLETED_STATUS.PARENT_COMPLETED.equals(completedStatus)) {
            return true;
        }
        if (COMPLETED_STATUS.DEPENDENT_DELETION_STARTED.equals(completedStatus)) {
            final long startedTime = (long)deletionInfoRow.get("EXECUTION_START_TIME");
            return startedTime < System.currentTimeMillis() - 300000L;
        }
        return false;
    }
    
    public static DMDataSetWrapper executeQuery(final SelectQuery selectQuery) throws SQLException, QueryConstructionException, DataAccessException {
        try {
            return DMDataSetWrapper.executeQuery(selectQuery);
        }
        catch (final SQLException | QueryConstructionException e) {
            throw e;
        }
        catch (final Exception e) {
            throw new DataAccessException(e.getMessage(), (Throwable)e);
        }
    }
    
    public static boolean handleDependentDataWaitingForDeletion(final String tableName, final long... pkValue) {
        DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Inside handleDependentDataWaitingForDeletion for table [" + s + "]");
        final long startTime = System.currentTimeMillis();
        try {
            final HashMap<String, Long> parentDependencyTables = getParentDependencyTables();
            if (!parentDependencyTables.containsKey(tableName.toLowerCase())) {
                DeletionTaskUtil.LOGGER.log(Level.WARNING, () -> "Unnecessary call to this method. Table [" + s2 + "] is not parent dependency table");
                return true;
            }
            final long parentId = parentDependencyTables.get(tableName.toLowerCase());
            final StringBuilder commaSeparatedValues = new StringBuilder();
            for (final long value : pkValue) {
                commaSeparatedValues.append(value).append(",");
            }
            commaSeparatedValues.deleteCharAt(commaSeparatedValues.length() - 1);
            DeletionTaskUtil.LOGGER.log(Level.FINE, () -> "Value : " + (Object)sb);
            final Criteria checkCriteria = getDependentDeletionCriteria(null, parentId, commaSeparatedValues.toString());
            final DataObject dddDO = DataAccess.get("DeletedDependentData", checkCriteria);
            if (dddDO.isEmpty() || dddDO.size("DeletedDependentData") == 0) {
                DeletionTaskUtil.LOGGER.log(Level.INFO, "No handling required");
                return true;
            }
            final long addHandlingTaskId = getUniqueTaskID(OPERATION_TYPE.ADDITION_PRE_HANDLING);
            try {
                DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Started Addition Pre-handling for TaskId : " + n);
                updateInDeletionInfoTable(addHandlingTaskId, null, COMPLETED_STATUS.STARTED, System.currentTimeMillis());
                final Row rowToExecute = dddDO.getRow("DeletedDependentData");
                final long taskId = (long)rowToExecute.get("TASK_ID");
                final DeleteDataDetails deleteDataDetails = new DeleteDataDetails();
                deleteDataDetails.processType = DeleteDataDetails.ProcessType.ADDITION_PRE_HANDLING;
                deleteDataDetails.taskID = taskId;
                deleteDataDetails.deletedData = commaSeparatedValues.toString();
                deleteDataDetails.operationType = OPERATION_TYPE.ADDITION_PRE_HANDLING;
                deleteDataDetails.statusKey = getStatusKey(null, addHandlingTaskId);
                final DataObject deletionParamDO = (DataObject)new WritableDataObject();
                deletionParamDO.addRow(getDeletionParamsRow(addHandlingTaskId, "TableName", tableName));
                deletionParamDO.addRow(getDeletionParamsRow(addHandlingTaskId, "PHD", commaSeparatedValues));
                deletionParamDO.addRow(getDeletionParamsRow(addHandlingTaskId, "TCRD", -1));
                DataAccess.add(deletionParamDO);
                final long totalRowsDeleted = deleteAllDependentData(parentId, deleteDataDetails);
                DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Total rows deleted : " + n2);
                updateInDeletionParamsDB(addHandlingTaskId, "TCRD", totalRowsDeleted);
                updateInDeletionInfoTable(addHandlingTaskId, null, COMPLETED_STATUS.SUCCESS, null);
                return true;
            }
            catch (final Exception e) {
                DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while doing Pre-Handling task for Dependent Deletion", e);
                updateInDeletionInfoTable(addHandlingTaskId, null, COMPLETED_STATUS.FAILED, null);
                return false;
            }
            finally {
                updateInDeletionParamsDB(addHandlingTaskId, "TDUR", System.currentTimeMillis() - startTime);
            }
        }
        catch (final Exception e2) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while handleDependentDataWaitingForDeletion", e2);
            return false;
        }
        finally {
            final long totalTime = System.currentTimeMillis() - startTime;
            DeletionTaskUtil.LOGGER.log(Level.INFO, () -> "Total time taken for handling Dependent-Data waiting for deletion : " + n3 + "ms");
        }
    }
    
    private static boolean isTablePresent(final String tableName) {
        try {
            if (MetaDataUtil.getTableDefinitionByName(tableName) == null) {
                return false;
            }
        }
        catch (final MetaDataException e) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, (Throwable)e, () -> "Exception while fetching meta-data information for " + s);
            return false;
        }
        return true;
    }
    
    private static String getPrimaryKey(final String tableName) {
        try {
            final PrimaryKeyDefinition pkd = MetaDataUtil.getTableDefinitionByName(tableName).getPrimaryKey();
            if (pkd == null || pkd.getColumnList().size() == 0) {
                DeletionTaskUtil.LOGGER.severe(() -> "Primary key for table [" + s + "] is not available");
                return null;
            }
            DeletionTaskUtil.LOGGER.fine(() -> "Primary key for table [" + s2 + "] is [" + primaryKeyDefinition.getColumnList().get(0) + "]");
            return pkd.getColumnList().get(0);
        }
        catch (final MetaDataException e) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, (Throwable)e, () -> "Exception while fetching primary key information for " + s3);
            return null;
        }
    }
    
    private static void setLimitForDeleteQuery(final DeleteQuery deleteQuery, final int limit) {
        setLimitForDeleteQuery(deleteQuery, deleteQuery.getTableName(), limit);
    }
    
    private static void setLimitForDeleteQuery(final DeleteQuery deleteQuery, final String tableName, final int limit) {
        if (DeletionTaskUtil.isPostgres) {
            setLimitIfNotPresent(deleteQuery, limit);
        }
        else if (deleteQuery.getSortColumns() == null || deleteQuery.getSortColumns().isEmpty()) {
            final String primaryKey = getPrimaryKey(tableName);
            if (primaryKey != null) {
                deleteQuery.addSortColumn(new SortColumn(tableName, primaryKey, true));
                setLimitIfNotPresent(deleteQuery, limit);
            }
            else {
                DeletionTaskUtil.LOGGER.severe(() -> "Limit cant be applied because primary key for table [" + s + "] cant be fetched");
            }
        }
        else {
            DeletionTaskUtil.LOGGER.fine("Sort column already present");
            setLimitIfNotPresent(deleteQuery, limit);
        }
    }
    
    private static void setLimitIfNotPresent(final DeleteQuery deleteQuery, final int limit) {
        if (deleteQuery.getLimit() == -1) {
            deleteQuery.setLimit(limit);
        }
        else {
            DeletionTaskUtil.LOGGER.info("limit already present");
        }
    }
    
    static {
        LOGGER = getDeletionFwLogger();
        TRANSACTION_PROPERTIES = ThreadLocal.withInitial((Supplier<? extends Queue<Long>>)LinkedList::new);
        DeletionTaskUtil.isPostgres = false;
        try {
            DeletionTaskUtil.isPostgres = DBUtil.getActiveDBName().equalsIgnoreCase("postgres");
            DeletionTaskUtil.LOGGER.info(() -> "In Deletion Framework. Is Postgres used as DB [" + DeletionTaskUtil.isPostgres + "]");
        }
        catch (final Exception e) {
            DeletionTaskUtil.LOGGER.log(Level.SEVERE, "Exception while checking DB Server. So here after chunk deletion cant be performed", e);
        }
    }
}
