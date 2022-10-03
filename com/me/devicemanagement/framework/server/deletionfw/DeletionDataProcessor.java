package com.me.devicemanagement.framework.server.deletionfw;

import java.util.Iterator;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import java.util.List;
import java.util.Arrays;
import java.util.LinkedList;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class DeletionDataProcessor extends DCQueueDataProcessor
{
    private static final Logger LOGGER;
    
    @Override
    public void processData(final DCQueueData qData) {
        final long taskId = Long.parseLong(qData.queueData.toString().replaceAll("\n", ""));
        DeletionDataProcessor.LOGGER.log(Level.INFO, () -> "Processing queue data [" + dcQueueData.queueData + "]");
        DeleteDataDetails deleteDataDetails = null;
        if (OPERATION_TYPE.DEPENDENT_DATA_DELETION.equals(qData.queueDataType)) {
            deleteDataDetails = DeletionTaskUtil.getDependentDataToBeDeleted(taskId);
        }
        else {
            try {
                deleteDataDetails = DeletionTaskUtil.getDataToDeleteFromDB(taskId);
            }
            catch (final DataAccessException e) {
                this.logger.log(Level.SEVERE, () -> "Exception while fetching DeleteDataDetails for task [" + n + "]");
            }
        }
        if (deleteDataDetails != null) {
            this.processData(deleteDataDetails);
        }
    }
    
    public void processData(final DeleteDataDetails deleteDataDetails) {
        if (deleteDataDetails.operationType.equals(OPERATION_TYPE.DEPENDENT_DATA_DELETION)) {
            try {
                DeletionTaskUtil.deleteAllDependentDataForQueueDeletionTask(deleteDataDetails);
            }
            catch (final Exception e) {
                DeletionDataProcessor.LOGGER.log(Level.INFO, "Exception while deleting dependent data :", e);
            }
        }
        else {
            doDeletion(deleteDataDetails.operationType, deleteDataDetails);
        }
    }
    
    private static void doDeletion(final OPERATION_TYPE operationType, final DeleteDataDetails deleteDataDetails) {
        DeletionDataProcessor.LOGGER.log(Level.INFO, () -> "Inside doDeletion for task-id [" + deleteDataDetails2.taskID + "]");
        final long startTime = System.currentTimeMillis();
        boolean isFailed = false;
        try {
            DeletionTaskUtil.updateStartedStatusInDB(deleteDataDetails.taskID, System.currentTimeMillis());
            if (OPERATION_TYPE.DELETE_MULTIPLE_TABLE_WITH_SAME_CRITERIA.equals(operationType)) {
                DeletionDataProcessor.LOGGER.log(Level.INFO, "Task : DELETE_MULTIPLE_TABLE_WITH_SAME_CRITERIA");
                multipleDeleteWithSameCriteria(deleteDataDetails.tablesList, deleteDataDetails.criteria, deleteDataDetails.chunkThreshold, deleteDataDetails.isPersistenceDeletion);
            }
            else if (OPERATION_TYPE.DELETE_TABLE_WITH_CRITERIA.equals(operationType)) {
                DeletionDataProcessor.LOGGER.log(Level.INFO, () -> "Task : DELETE_TABLE_WITH_CRITERIA : " + deleteDataDetails3.tableName);
                final List<String> tableList = new LinkedList<String>();
                tableList.add(deleteDataDetails.tableName);
                multipleDeleteWithSameCriteria(tableList, deleteDataDetails.criteria, deleteDataDetails.chunkThreshold, deleteDataDetails.isPersistenceDeletion);
            }
            else if (OPERATION_TYPE.DELETE_BY_OPTIMAL_GROUPING.equals(operationType)) {
                DeletionDataProcessor.LOGGER.log(Level.INFO, () -> "Task : DELETE_BY_OPTIMAL_GROUPING : " + deleteDataDetails4.tableName);
                try {
                    if (deleteDataDetails.isPersistenceDeletion) {
                        DeletionFramework.persistenceDelete(deleteDataDetails.tableName, deleteDataDetails.criteria, deleteDataDetails.chunkThreshold, deleteDataDetails.groupByClauseList);
                    }
                    else {
                        DeletionFramework.delete(deleteDataDetails.tableName, deleteDataDetails.criteria, deleteDataDetails.chunkThreshold, deleteDataDetails.groupByClauseList);
                    }
                }
                catch (final Exception e) {
                    DeletionDataProcessor.LOGGER.log(Level.INFO, e, () -> "Exception in Task : DELETE_BY_OPTIMAL_GROUPING : " + deleteDataDetails5.tableName);
                }
            }
            else if (OPERATION_TYPE.DELETE_DUPLICATE_DATA_BY_GROUPING.equals(operationType)) {
                DeletionDataProcessor.LOGGER.log(Level.INFO, () -> "Task : DELETE_DUPLICATE_DATA_BY_GROUPING : " + deleteDataDetails6.tableName);
                try {
                    if (deleteDataDetails.isPersistenceDeletion) {
                        DeletionFramework.persistenceDelete(deleteDataDetails.tableName, deleteDataDetails.criteria, deleteDataDetails.chunkThreshold, deleteDataDetails.groupByClauseList, true, deleteDataDetails.nonDuplicateColumnList);
                    }
                    else {
                        DeletionFramework.delete(deleteDataDetails.tableName, deleteDataDetails.criteria, deleteDataDetails.chunkThreshold, deleteDataDetails.groupByClauseList, true, deleteDataDetails.nonDuplicateColumnList);
                    }
                }
                catch (final Exception e) {
                    DeletionDataProcessor.LOGGER.log(Level.INFO, e, () -> "Exception in Task : DELETE_DUPLICATE_DATA_BY_GROUPING : " + deleteDataDetails7.tableName);
                }
            }
            else if (OPERATION_TYPE.DELETE_WITH_DELETE_QUERY.equals(operationType)) {
                DeletionDataProcessor.LOGGER.log(Level.INFO, () -> "Task : DELETE_WITH_DELETE_QUERY : " + deleteDataDetails8.deleteQuery.getTableName());
                try {
                    if (deleteDataDetails.isPersistenceDeletion) {
                        DeletionFramework.persistenceDelete(deleteDataDetails.deleteQuery, deleteDataDetails.chunkThreshold);
                    }
                    else {
                        DeletionFramework.delete(deleteDataDetails.deleteQuery, deleteDataDetails.chunkThreshold);
                    }
                }
                catch (final Exception e) {
                    DeletionDataProcessor.LOGGER.log(Level.INFO, e, () -> "Exception in Task : DELETE_WITH_DELETE_QUERY : " + deleteDataDetails9.deleteQuery.getTableName());
                }
            }
            else if (OPERATION_TYPE.DELETE_WITH_SELECT_QUERY.equals(operationType)) {
                DeletionDataProcessor.LOGGER.log(Level.INFO, () -> "Task : DELETE_WITH_SELECT_QUERY : " + Arrays.toString(deleteDataDetails10.tablesList.toArray()));
                try {
                    if (deleteDataDetails.isPersistenceDeletion) {
                        DeletionFramework.persistenceDelete(deleteDataDetails.selectQuery, deleteDataDetails.tablesList, deleteDataDetails.chunkThreshold);
                    }
                    else {
                        DeletionFramework.delete(deleteDataDetails.selectQuery, deleteDataDetails.tablesList, deleteDataDetails.chunkThreshold);
                    }
                }
                catch (final Exception e) {
                    DeletionDataProcessor.LOGGER.log(Level.INFO, e, () -> "Exception in Task : DELETE_WITH_SELECT_QUERY : " + Arrays.toString(deleteDataDetails11.tablesList.toArray()));
                }
            }
            else {
                DeletionDataProcessor.LOGGER.log(Level.INFO, () -> "Invalid operation type" + operation_TYPE + " for taskID : " + deleteDataDetails12.taskID);
            }
            DeletionTaskUtil.updateCompletedStatusInDB(deleteDataDetails.taskID);
        }
        catch (final Exception e) {
            DeletionDataProcessor.LOGGER.log(Level.INFO, e, () -> "Exception in Task : " + deleteDataDetails13.taskID);
            isFailed = true;
            try {
                DeletionTaskUtil.updateFailedStatusInDB(deleteDataDetails.taskID);
            }
            catch (final DataAccessException ex) {
                DeletionDataProcessor.LOGGER.log(Level.INFO, (Throwable)ex, () -> "Exception while updating failure status in db for task-id [" + deleteDataDetails14.taskID + "]");
            }
        }
        finally {
            updateRetryCountWithoutException(deleteDataDetails.taskID, isFailed);
        }
        final long totalTime = System.currentTimeMillis() - startTime;
        DeletionDataProcessor.LOGGER.log(Level.INFO, () -> "multipleDeleteWithSameCriteria completed in " + n + " ms");
    }
    
    private static void updateRetryCountWithoutException(final long taskId, final boolean isFailed) {
        try {
            updateRetryCount(taskId, isFailed);
        }
        catch (final DataAccessException e) {
            DeletionDataProcessor.LOGGER.log(Level.SEVERE, (Throwable)e, () -> "Exception while updating retry count in DB for taskID [" + n + "]");
        }
    }
    
    private static void updateRetryCount(final long taskId, final boolean isFailed) throws DataAccessException {
        final DataObject retryDo = DataAccess.get("DeletionParameters", DeletionTaskUtil.getDeletionParamsCriteria(taskId, "RetryCount"));
        final Row retryRow = retryDo.getRow("DeletionParameters");
        if (retryRow != null) {
            final long retryCount = Long.parseLong((String)retryRow.get("PARAM_VALUE"));
            if (retryCount > DeletionFWProps.maxRetryCount && isFailed) {
                DeletionTaskUtil.updateAbortedStatusInDB(taskId);
            }
            retryRow.set("PARAM_VALUE", (Object)(retryCount + 1L));
            retryDo.updateRow(retryRow);
            DataAccess.update(retryDo);
        }
    }
    
    private static void multipleDeleteWithSameCriteria(final List<String> tablesList, final Criteria criteria, final int chunk, final boolean isPersistenceDeletion) throws DataAccessException {
        for (final String table : tablesList) {
            DeletionDataProcessor.LOGGER.log(Level.FINE, () -> "Table Name : " + s);
            final Criteria newCri = new Criteria(new Column(table, criteria.getColumn().getColumnName()), criteria.getValue(), criteria.getComparator());
            if (isPersistenceDeletion) {
                DeletionFramework.persistenceDelete(table, newCri, chunk);
            }
            else {
                DeletionFramework.delete(table, newCri, chunk);
            }
        }
    }
    
    static {
        LOGGER = DeletionTaskUtil.getDeletionFwLogger();
    }
}
