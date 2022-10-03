package com.me.devicemanagement.framework.server.deletionfw;

import org.json.JSONObject;
import java.util.Iterator;
import java.util.Set;
import javax.transaction.SystemException;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DeleteQuery;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class DeletionFramework
{
    private static final Logger LOGGER;
    
    public static long asyncPersistenceDependentDataDeletion(final String tableName, final Criteria criteria) throws DataAccessException, DependentDeletionFailedException, DeletionQueueFailedException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "asyncPersistenceDependentDataDeletion called for table [" + s + "] and criteria [" + criteria2 + "]");
        return DeletionTaskUtil.checkForAndDeleteDependentData(tableName, criteria, null, true, true);
    }
    
    public static long asyncDependentDataDeletion(final String tableName, final Criteria criteria) throws DataAccessException, DependentDeletionFailedException, DeletionQueueFailedException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "asyncDependentDataDeletion called for table [" + s + "] and criteria [" + criteria2 + "]");
        return DeletionTaskUtil.checkForAndDeleteDependentData(tableName, criteria, null, false, true);
    }
    
    public static long doDependentDataDeletion(final String tableName, final Criteria criteria) throws DataAccessException, DependentDeletionFailedException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "doDependentDataDeletion called for table [" + s + "] and criteria [" + criteria2 + "]");
        try {
            return DeletionTaskUtil.checkForAndDeleteDependentData(tableName, criteria, null, false, false);
        }
        catch (final DeletionQueueFailedException e) {
            DeletionFramework.LOGGER.log(Level.SEVERE, "Impossible error occurred", e);
            throw new DataAccessException("Impossible DeletionQueueFailedException occurred", (Throwable)e);
        }
    }
    
    public static void asyncPersistenceDelete(final List<String> tables, final Criteria criteria) throws DataAccessException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "asyncPersistenceDelete called for tables [" + list + "] and criteria [" + criteria2 + "]");
        checkParentDependencyRemovedTables(tables);
        DeletionTaskUtil.asyncDelete(tables, criteria, true);
    }
    
    public static void asyncDelete(final List<String> tables, final Criteria criteria) throws DataAccessException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "asyncDelete called for tables [" + list + "] and criteria [" + criteria2 + "]");
        checkParentDependencyRemovedTables(tables);
        DeletionTaskUtil.asyncDelete(tables, criteria, false);
    }
    
    public static int persistenceDelete(final DeleteQuery deleteQuery, final int chunk) throws DataAccessException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "persistenceDelete called with query [" + deleteQuery2 + "] and chunk [" + n + "]");
        checkParentDependencyRemovedTables(deleteQuery.getTableName());
        return DeletionTaskUtil.deleteByQuery(deleteQuery, chunk, true);
    }
    
    public static int delete(final DeleteQuery deleteQuery, final int chunk) throws DataAccessException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "Deletion called with query [" + deleteQuery2 + "] and chunk [" + n + "]");
        checkParentDependencyRemovedTables(deleteQuery.getTableName());
        return DeletionTaskUtil.deleteByQuery(deleteQuery, chunk, false);
    }
    
    public static int persistenceDelete(final SelectQuery selectQuery, final List<String> tableList, final int chunk) throws DataAccessException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "persistenceDelete called with select query [" + selectQuery2 + "] , tables [" + list + "] and chunk [" + n + "]");
        checkTablesHasParentDependencyRemovedTables(selectQuery.getTableList());
        return DeletionTaskUtil.deleteBySelectQuery(selectQuery, tableList, chunk, true);
    }
    
    public static int delete(final SelectQuery selectQuery, final List<String> tableList, final int chunk) throws DataAccessException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "Deletion called with select query [" + selectQuery2 + "] , tables [" + list + "] and chunk [" + n + "]");
        checkTablesHasParentDependencyRemovedTables(selectQuery.getTableList());
        return DeletionTaskUtil.deleteBySelectQuery(selectQuery, tableList, chunk, false);
    }
    
    public static long delete(final String tableName, final Criteria criteria, final int chunk) throws DataAccessException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "Deletion called for table [" + s + "] , criteria [" + criteria2 + "] and chunk [" + n + "]");
        checkParentDependencyRemovedTables(tableName);
        return DeletionTaskUtil.doChunkDeletion(tableName, criteria, chunk, false);
    }
    
    public static long persistenceDelete(final String tableName, final Criteria criteria, final int chunk) throws DataAccessException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "persistenceDelete called for table [" + s + "] , criteria [" + criteria2 + "] and chunk [" + n + "]");
        checkParentDependencyRemovedTables(tableName);
        return DeletionTaskUtil.doChunkDeletion(tableName, criteria, chunk, true);
    }
    
    public static int delete(final String tableName, final Criteria criteria, final int chunk, final List<GroupByClause> groupByClauseList) throws SQLException, DataAccessException, QueryConstructionException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "Deletion called for table [" + s + "] , criteria [" + criteria2 + "] , chunk [" + n + "], groupBy [" + list + "]");
        checkParentDependencyRemovedTables(tableName);
        return DeletionTaskUtil.doChunkDeletionByGrouping(tableName, criteria, chunk, groupByClauseList, false);
    }
    
    public static int persistenceDelete(final String tableName, final Criteria criteria, final int chunk, final List<GroupByClause> groupByClauseList) throws SQLException, DataAccessException, QueryConstructionException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "persistenceDelete called for table [" + s + "] , criteria [" + criteria2 + "] , chunk [" + n + "], groupBy [" + list + "]");
        checkParentDependencyRemovedTables(tableName);
        return DeletionTaskUtil.doChunkDeletionByGrouping(tableName, criteria, chunk, groupByClauseList, true);
    }
    
    public static int delete(final String tableName, final Criteria criteria, final int chunk, final List<GroupByClause> groupByClauseList, final boolean deleteOnlyDuplicate, final List<Column> uniqueColList) throws SQLException, QueryConstructionException, DataAccessException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "Deletion called for table [" + s + "] , criteria [" + criteria2 + "] , chunk [" + n + "], groupBy [" + list + "] , deleteOnlyDuplicate [" + b + "], uniqueCols [" + list2 + "]");
        checkParentDependencyRemovedTables(tableName);
        return DeletionTaskUtil.doChunkDeletionByGrouping(tableName, criteria, chunk, groupByClauseList, deleteOnlyDuplicate, uniqueColList, false);
    }
    
    public static int persistenceDelete(final String tableName, final Criteria criteria, final int chunk, final List<GroupByClause> groupByClauseList, final boolean deleteOnlyDuplicate, final List<Column> uniqueColList) throws SQLException, QueryConstructionException, DataAccessException {
        DeletionFramework.LOGGER.log(Level.FINE, () -> "persistenceDelete called for table [" + s + "] , criteria [" + criteria2 + "] , chunk [" + n + "], groupBy [" + list + "] , deleteOnlyDuplicate [" + b + "], uniqueCols [" + list2 + "]");
        checkParentDependencyRemovedTables(tableName);
        return DeletionTaskUtil.doChunkDeletionByGrouping(tableName, criteria, chunk, groupByClauseList, deleteOnlyDuplicate, uniqueColList, true);
    }
    
    public static void transactionCommitted() throws SystemException {
        DeletionTaskUtil.transactionCommitted();
    }
    
    public static void transactionRollback() throws SystemException {
        DeletionTaskUtil.transactionRollback();
    }
    
    public static boolean handleDependentDataWaitingForDeletion(final String tableName, final long... pkValue) {
        return DeletionTaskUtil.handleDependentDataWaitingForDeletion(tableName, pkValue);
    }
    
    public static boolean isDeleteQueryContainParentDependencyRemovedTables(String sqlQuery) throws DataAccessException {
        if (sqlQuery == null || sqlQuery.isEmpty()) {
            return false;
        }
        sqlQuery = sqlQuery.replaceAll("\\s{2,}", " ").trim().toLowerCase();
        final String DELETE_FROM = "delete from";
        if (sqlQuery.startsWith("delete from")) {
            String tableName = sqlQuery.substring("delete from".length()).trim();
            tableName = tableName.substring(0, tableName.indexOf(" "));
            if (tableName.startsWith("\"")) {
                tableName = tableName.substring(1, tableName.length() - 1);
            }
            return DeletionTaskUtil.getParentDependencyTables().containsKey(tableName);
        }
        return false;
    }
    
    public static boolean isDeleteQueryContainParentDependencyRemovedTables(final DeleteQuery query) throws DataAccessException {
        return query != null && DeletionTaskUtil.getParentDependencyTables().containsKey(query.getTableName().toLowerCase());
    }
    
    public static boolean isParentDependencyRemovedTables(final String tableName) throws DataAccessException {
        return DeletionTaskUtil.getParentDependencyTables().containsKey(tableName.toLowerCase());
    }
    
    public static boolean isParentDependencyRemovedTables(final List<String> tableNames) throws DataAccessException {
        final Set<String> parentTables = DeletionTaskUtil.getParentDependencyTables().keySet();
        for (final String tableName : tableNames) {
            if (parentTables.contains(tableName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isTablesHasParentDependencyRemovedTables(final List<Table> tables) throws DataAccessException {
        final Set<String> parentTables = DeletionTaskUtil.getParentDependencyTables().keySet();
        for (final Table table : tables) {
            if (parentTables.contains(table.getTableName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    private static void checkParentDependencyRemovedTables(final String tables) throws DataAccessException {
        if (isParentDependencyRemovedTables(tables)) {
            throwParentDependencyRemovedDeletionException(tables);
        }
    }
    
    private static void checkParentDependencyRemovedTables(final List<String> tables) throws DataAccessException {
        if (isParentDependencyRemovedTables(tables)) {
            throwParentDependencyRemovedDeletionException(tables.toString());
        }
    }
    
    private static void checkTablesHasParentDependencyRemovedTables(final List<Table> tables) throws DataAccessException {
        if (isTablesHasParentDependencyRemovedTables(tables)) {
            throwParentDependencyRemovedDeletionException(tables.toString());
        }
    }
    
    private static void throwParentDependencyRemovedDeletionException(final String tablesString) throws DataAccessException {
        DeletionFramework.LOGGER.log(Level.SEVERE, () -> "Parent dependency Removed tables are being tried to deleted [" + s + "]");
        throw new DataAccessException("Some of the tables are parent dependency removed tables. Delete using DependentDataDeletion method. Tables = " + tablesString);
    }
    
    public static JSONObject deleteQueryToJSON(final DeleteQuery deleteQuery) {
        return DeletionTaskUtil.deleteQueryToJSON(deleteQuery);
    }
    
    public static DeleteQuery jsonToDeleteQuery(final JSONObject json) {
        return DeletionTaskUtil.jsonToDeleteQuery(json);
    }
    
    static {
        LOGGER = DeletionTaskUtil.getDeletionFwLogger();
    }
}
