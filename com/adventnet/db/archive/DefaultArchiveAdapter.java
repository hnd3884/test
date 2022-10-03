package com.adventnet.db.archive;

import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.PersistenceUtil;
import javax.transaction.Transaction;
import javax.transaction.InvalidTransactionException;
import javax.transaction.SystemException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.SelectQueryImpl;
import java.sql.Statement;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.Collections;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.Table;
import java.util.Collection;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.CreateTableLike;
import com.adventnet.persistence.QueryConstructor;
import java.util.HashMap;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.ArchiveTable;
import java.util.Map;
import java.sql.Connection;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.db.api.RelationalAPI;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import com.adventnet.db.adapter.DBAdapter;
import java.util.logging.Logger;

public class DefaultArchiveAdapter implements ArchiveAdapter
{
    static final Logger LOGGER;
    private static DBAdapter dbAdapter;
    private StorageAdapter storageAdap;
    private List<String> currentlyRunningTables;
    private Set<String> archiveEnabledTableNames;
    
    public DefaultArchiveAdapter() {
        this.storageAdap = null;
        this.currentlyRunningTables = new ArrayList<String>();
        this.archiveEnabledTableNames = new TreeSet<String>();
    }
    
    public DBAdapter getDBAdapter() {
        if (DefaultArchiveAdapter.dbAdapter == null) {
            DefaultArchiveAdapter.dbAdapter = RelationalAPI.getInstance().getDBAdapter();
        }
        return DefaultArchiveAdapter.dbAdapter;
    }
    
    @Override
    public void moveArchivedTable(final List<String> tableNamesToBeMoved) throws Exception {
        if (this.storageAdap != null) {
            this.storageAdap.moveArchiveTables(tableNamesToBeMoved);
        }
    }
    
    @Override
    public void cleanUpArchiveTables(final List<String> tableNamesToBeDeleted) throws Exception {
        if (this.storageAdap != null) {
            this.storageAdap.cleanUpTables(tableNamesToBeDeleted);
        }
    }
    
    @Override
    public int startArchive(final ArchivePolicyInfo policy) throws Exception {
        DefaultArchiveAdapter.LOGGER.log(Level.INFO, "Defined policy :::: {0}", policy.toString());
        final Long policyId = policy.getArchivePolicyID();
        if (this.isArchiveNotificationEnabled(policy.getArchivePolicyID())) {
            TableArchiverUtil.getPolicy(policyId).getNotificationHandler().startedArchiving(true);
            TableArchiverUtil.getPolicy(policyId).getNotificationHandler().currentRunningPolicy(policy);
        }
        final String criteriaString = policy.getCriteriaString();
        final String mode = policy.getArchiveMode();
        final String liveTableName = policy.getTableName();
        final Long threshold = policy.getThreshold();
        final boolean proceed = this.checkThreshold(liveTableName, threshold);
        if (!proceed) {
            DefaultArchiveAdapter.LOGGER.info("Threshold level not reached...");
            if (this.isArchiveNotificationEnabled(policyId)) {
                TableArchiverUtil.getPolicy(policyId).getNotificationHandler().completedArchiving(true, 3, null);
            }
            return 3;
        }
        final Connection conn = RelationalAPI.getInstance().getConnection();
        ArchiveTableInfo arcTabInfo = null;
        try {
            final List<String> tableNamesToBeArchived = this.getAllRelatedTableNames(liveTableName);
            arcTabInfo = new ArchiveTableInfo(policy, tableNamesToBeArchived);
            final Map<String, ArchiveTable> tableNameVsArchiveTable = arcTabInfo.getArchiveMap();
            if (this.isArchiveNotificationEnabled(policyId)) {
                TableArchiverUtil.getPolicy(policyId).getNotificationHandler().tableNamesToBeArchived(tableNamesToBeArchived);
                TableArchiverUtil.getPolicy(policyId).getNotificationHandler().archiveMap(tableNameVsArchiveTable);
            }
            DefaultArchiveAdapter.LOGGER.info(tableNamesToBeArchived.toString());
            this.startedArchiving(liveTableName);
            DataObject archiveTableDetails = null;
            if (mode.equals("PULL")) {
                this.executeQueriesToPull(conn, liveTableName, criteriaString, tableNamesToBeArchived, tableNameVsArchiveTable, policyId);
                archiveTableDetails = arcTabInfo.updateDetails();
            }
            else {
                if (!mode.equals("PUSH")) {
                    throw new Exception("Invalid archive mode specified... ");
                }
                if (criteriaString == null || criteriaString.length() == 0) {
                    throw new IllegalArgumentException("Criteria string cannot be null for mode PUSH");
                }
                this.executeQueriesToPush(conn, liveTableName, criteriaString, tableNamesToBeArchived, tableNameVsArchiveTable, policyId);
                archiveTableDetails = arcTabInfo.updateDetails();
            }
            this.moveArchivedTable(arcTabInfo.getListOfArchivedTableNames());
            if (TableArchiverUtil.isArchiveRotationEnabled() && policy.getRotationCount() > 0) {
                DefaultArchiveAdapter.LOGGER.log(Level.INFO, "Archive Rotation enabled..going to delete tables :" + this.rotateTables(policy).toString());
                this.deleteArchiveTable(this.rotateTables(policy));
            }
            if (this.isArchiveNotificationEnabled(policyId)) {
                TableArchiverUtil.getPolicy(policyId).getNotificationHandler().completedArchiving(true, 1, archiveTableDetails);
            }
            return 1;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            if (this.isArchiveNotificationEnabled(policyId)) {
                TableArchiverUtil.getPolicy(policyId).getNotificationHandler().completedArchiving(false, 2, null);
            }
            return 2;
        }
        finally {
            this.completedArchiving(liveTableName);
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                DefaultArchiveAdapter.LOGGER.info("Error during connection close...");
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void deleteArchiveTable(final List<String> tablesToBeDeleted) throws Exception {
        if (tablesToBeDeleted.isEmpty()) {
            return;
        }
        if (this.storageAdap != null) {
            this.storageAdap.cleanUpTables(tablesToBeDeleted);
        }
        final List<String> queryList = new ArrayList<String>();
        final Connection conn = RelationalAPI.getInstance().getConnection();
        try {
            final Criteria cri = new Criteria(Column.getColumn("ArchiveTableDetails", "ARCHIVED_TABLENAME"), tablesToBeDeleted.toArray(), 8);
            queryList.add(this.getDBAdapter().getSQLGenerator().getSQLForDelete("ArchiveTableDetails", cri));
            for (final String tableName : tablesToBeDeleted) {
                queryList.add(this.getDBAdapter().getSQLGenerator().getSQLForDrop(tableName, false));
            }
            this.executeAllQueries(conn, queryList);
        }
        catch (final Exception ex) {
            DefaultArchiveAdapter.LOGGER.log(Level.INFO, "Error occured in rotating the archive tables..");
            ex.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    private void executeAndNotify(final Connection conn, final Object query, final String tableName, final Long policyId) throws Exception {
        final List queryList = new ArrayList();
        queryList.add(query);
        if (this.isArchiveNotificationEnabled(policyId)) {
            TableArchiverUtil.getPolicy(policyId).getNotificationHandler().processTable(tableName);
            this.executeAllQueries(conn, queryList);
            TableArchiverUtil.getPolicy(policyId).getNotificationHandler().finishedTable(tableName);
        }
        else {
            this.executeAllQueries(conn, queryList);
        }
    }
    
    private void executeQueriesToPush(final Connection conn, final String liveTableName, final String criteriaString, final List<String> tableNamesToBeArchived, final Map<String, ArchiveTable> tableNameVsArchiveTable, final Long policyId) throws Exception {
        final List<String> childTablesOfLiveTable = MetaDataUtil.getAllSlaveTableNames(liveTableName);
        final Criteria cri = this.formArchiveCriteria(liveTableName, criteriaString);
        final boolean isEmptyTableClone = cri == null;
        final ArchiveTable arcTable = tableNameVsArchiveTable.get(liveTableName);
        final Map<String, ArchiveTable> arcMap = new HashMap<String, ArchiveTable>();
        arcMap.put(liveTableName, arcTable);
        final List<String> tableList = new ArrayList<String>();
        tableList.add(liveTableName);
        SelectQuery sQueryToGetLiveData = null;
        if (!isEmptyTableClone) {
            sQueryToGetLiveData = QueryConstructor.get(tableList, cri);
        }
        final CreateTableLike cloneTableDetails = new CreateTableLike(liveTableName, sQueryToGetLiveData, tableNameVsArchiveTable, true);
        this.executeAndNotify(conn, this.getDBAdapter().getSQLGenerator().getSQLForCreateArchiveTable(cloneTableDetails, null, true), tableNameVsArchiveTable.get(liveTableName).getArchiveTableName(), policyId);
        this.executeAndNotify(conn, this.getDBAdapter().getSQLGenerator().getSQLForDelete(liveTableName, cri), liveTableName, policyId);
        for (final String childTableName : childTablesOfLiveTable) {
            final List<String> masterTablesOfSlave = MetaDataUtil.getMasterTableNames(childTableName);
            final List<String> parentTablesOfChild = new ArrayList<String>();
            for (final String masterTabName : masterTablesOfSlave) {
                if (childTablesOfLiveTable.contains(masterTabName) || liveTableName.equals(masterTabName)) {
                    parentTablesOfChild.add(masterTabName);
                }
            }
            final String cloneTableQuery = this.cloneTableStructureToPushData(childTableName, parentTablesOfChild, tableNameVsArchiveTable);
            this.executeAndNotify(conn, cloneTableQuery, childTableName, policyId);
        }
    }
    
    protected void executeQueriesToPull(final Connection conn, final String liveTableName, final String criteriaString, final List<String> tableNamesToBeArchived, final Map<String, ArchiveTable> tableNameVsArchiveTable, final Long policyId) throws Exception {
        List queryList = new ArrayList();
        final List<String> childTablesOfLiveTable = MetaDataUtil.getAllSlaveTableNames(liveTableName);
        queryList.addAll(this.getQueriesToRenameTables(tableNamesToBeArchived, tableNameVsArchiveTable));
        this.executeAllQueries(conn, queryList);
        final String archiveTableName = tableNameVsArchiveTable.get(liveTableName).getArchiveTableName();
        final Criteria cri = this.formArchiveCriteria(archiveTableName, criteriaString);
        final Table table = new Table(liveTableName, archiveTableName);
        final List tableList = new ArrayList();
        tableList.add(table);
        QueryUtil.setTypeForCriteria(cri, tableList);
        final boolean isEmptyTableClone = cri == null;
        DefaultArchiveAdapter.LOGGER.log(Level.INFO, "isOnlyTableClone ::: {0}", isEmptyTableClone);
        final List<String> tableName = new ArrayList<String>();
        tableName.add(liveTableName);
        SelectQuery sQueryToGetLiveData = null;
        if (!isEmptyTableClone) {
            sQueryToGetLiveData = QueryConstructor.get(tableName, cri, tableNameVsArchiveTable);
        }
        final CreateTableLike mainTableClone = new CreateTableLike(liveTableName, sQueryToGetLiveData, tableNameVsArchiveTable, true);
        this.executeAndNotify(conn, this.getDBAdapter().getSQLGenerator().getSQLForCreateArchiveTable(mainTableClone, null, false), tableNameVsArchiveTable.get(liveTableName).getArchiveTableName(), policyId);
        for (final String childTableName : childTablesOfLiveTable) {
            String cloneTableQuery = null;
            if (isEmptyTableClone) {
                cloneTableQuery = this.cloneTableStructureAndPullData(childTableName, Collections.EMPTY_LIST, tableNameVsArchiveTable, isEmptyTableClone);
            }
            else {
                final List<String> masterTablesOfSlave = MetaDataUtil.getMasterTableNames(childTableName);
                final List<String> parentTablesOfChild = new ArrayList<String>();
                for (final String masterTabName : masterTablesOfSlave) {
                    if (childTablesOfLiveTable.contains(masterTabName) || liveTableName.equals(masterTabName)) {
                        parentTablesOfChild.add(masterTabName);
                    }
                }
                cloneTableQuery = this.cloneTableStructureAndPullData(childTableName, parentTablesOfChild, tableNameVsArchiveTable, isEmptyTableClone);
            }
            this.executeAndNotify(conn, cloneTableQuery, childTableName, policyId);
        }
        queryList = new ArrayList();
        for (int index = childTablesOfLiveTable.size() - 1; index >= 0; --index) {
            final String childTable = childTablesOfLiveTable.get(index);
            queryList.addAll(this.getDBAdapter().getATQForRemoveAllConstraints(tableNameVsArchiveTable.get(childTable)));
        }
        queryList.addAll(this.getDBAdapter().getATQForRemoveAllConstraints(tableNameVsArchiveTable.get(liveTableName)));
        queryList.addAll(this.handleCopyConstraintQuery(liveTableName, tableNameVsArchiveTable, isEmptyTableClone));
        final Iterator<String> iterator3 = childTablesOfLiveTable.iterator();
        while (iterator3.hasNext()) {
            final String childTable = iterator3.next();
            queryList.addAll(this.handleCopyConstraintQuery(childTable, tableNameVsArchiveTable, isEmptyTableClone));
        }
        queryList.addAll(this.getQueriesToVisibleTables(tableNamesToBeArchived, tableNameVsArchiveTable));
        this.executeAllQueries(conn, queryList);
    }
    
    private List<AlterTableQuery> getQueriesToRenameTables(final List<String> tableNameList, final Map<String, ArchiveTable> tableNameVsArchiveTable) throws QueryConstructionException {
        final List<AlterTableQuery> qryList = new ArrayList<AlterTableQuery>();
        for (final String tableName : tableNameList) {
            final ArchiveTable table = tableNameVsArchiveTable.get(tableName);
            final AlterTableQuery renameQuery = new AlterTableQueryImpl(table.getTableName());
            renameQuery.renameTable(table.getArchiveTableName());
            qryList.add(renameQuery);
        }
        return qryList;
    }
    
    private List<String> getQueriesToVisibleTables(final List<String> tableNamesToBeRenamed, final Map<String, ArchiveTable> tableNameVsArchiveTable) throws QueryConstructionException {
        final List<String> qryList = new ArrayList<String>();
        for (final String tableName : tableNamesToBeRenamed) {
            final AlterTableQuery renameQuery = new AlterTableQueryImpl(tableNameVsArchiveTable.get(tableName).getInvisibleTableName());
            renameQuery.renameTable(tableName);
            qryList.add(this.getSQLStringForAlterQuery(renameQuery));
        }
        return qryList;
    }
    
    private String cloneTableStructureAndPullData(final String tableNameToBeCreated, final List<String> tableNamesToBeJoined, final Map<String, ArchiveTable> tableNameVsArchiveTable, final boolean isEmptyClone) throws Exception {
        final List<String> tableNames = new ArrayList<String>();
        SelectQuery selectSQL = null;
        final List<String> archivedTableNames = new ArrayList<String>();
        for (final String joinTableName : tableNamesToBeJoined) {
            archivedTableNames.add(tableNameVsArchiveTable.get(joinTableName).getArchiveTableAlias());
        }
        if (!isEmptyClone) {
            tableNames.addAll(tableNamesToBeJoined);
            tableNames.add(tableNameToBeCreated);
            selectSQL = QueryConstructor.get(tableNames, null, tableNameVsArchiveTable);
            final List<Column> colList = selectSQL.getSelectColumns();
            for (final Column column : colList) {
                if (archivedTableNames.contains(column.getTableAlias())) {
                    selectSQL.removeSelectColumn(column);
                }
            }
        }
        final CreateTableLike cloneTableDetails = new CreateTableLike(tableNameToBeCreated, selectSQL, tableNameVsArchiveTable, true);
        final String returnString = this.getDBAdapter().getSQLGenerator().getSQLForCreateArchiveTable(cloneTableDetails, null, false);
        return returnString;
    }
    
    private String cloneTableStructureToPushData(final String tableNameToBeQueried, final List<String> tableNamesToBeJoined, final Map<String, ArchiveTable> tableNameVsArchiveTable) throws Exception {
        final List<String> tableNames = new ArrayList<String>();
        tableNames.addAll(tableNamesToBeJoined);
        tableNames.add(tableNameToBeQueried);
        final SelectQuery selectSQL = QueryConstructor.get(tableNames, (Criteria)null);
        final List<Column> colList = selectSQL.getSelectColumns();
        for (final Column column : colList) {
            if (tableNamesToBeJoined.contains(column.getTableAlias())) {
                selectSQL.removeSelectColumn(column);
            }
        }
        final CreateTableLike cloneTableDetails = new CreateTableLike(tableNameToBeQueried, selectSQL, tableNameVsArchiveTable, true);
        final String returnString = this.getDBAdapter().getSQLGenerator().getSQLForCreateArchiveTable(cloneTableDetails, null, true);
        return returnString;
    }
    
    protected Criteria formArchiveCriteria(final String tableName, final String criteriaString) {
        if (criteriaString == null || criteriaString.length() == 0) {
            return null;
        }
        final String criStr = criteriaString.replace("$", tableName);
        return new Criteria(criStr);
    }
    
    protected void executeAllQueries(final Connection connection, final List queryList) throws SQLException, QueryConstructionException {
        boolean isCommitRequired = false;
        boolean isExecuted = false;
        if (connection.getAutoCommit()) {
            isCommitRequired = true;
            connection.setAutoCommit(false);
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            for (final Object sql : queryList) {
                DefaultArchiveAdapter.LOGGER.log(Level.INFO, "Going to execute ::: {0}", sql);
                if (sql instanceof String) {
                    if (sql == null || sql.toString().length() == 0) {
                        continue;
                    }
                    statement.execute(sql.toString());
                }
                else {
                    if (!(sql instanceof AlterTableQuery)) {
                        continue;
                    }
                    final String atqStr = this.getSQLStringForAlterQuery((AlterTableQuery)sql);
                    statement.execute(atqStr);
                }
            }
            if (isCommitRequired) {
                connection.commit();
                connection.setAutoCommit(true);
            }
            isExecuted = true;
        }
        catch (final SQLException sqle) {
            throw sqle;
        }
        finally {
            if (isCommitRequired && !isExecuted) {
                connection.rollback();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }
    
    protected String getSQLStringForAlterQuery(final AlterTableQuery atq) throws QueryConstructionException {
        return this.getDBAdapter().getSQLGenerator().getSQLForAlterTable(atq);
    }
    
    private List<AlterTableQuery> handleCopyConstraintQuery(final String tableName, final Map<String, ArchiveTable> tableNameVsArchiveTable, final boolean isEmptyTableClone) throws Exception {
        final CreateTableLike cloneTableDetails = new CreateTableLike(tableName, null, tableNameVsArchiveTable, true);
        final List<AlterTableQuery> queries = this.getDBAdapter().getAlterQueryForCopyAllConstraints(cloneTableDetails, isEmptyTableClone);
        return queries;
    }
    
    @Override
    public void setStorageAdapter(final StorageAdapter storageAdapter) throws Exception {
        this.storageAdap = storageAdapter;
    }
    
    private boolean checkThreshold(final String tableName, final Long threshold) throws SQLException, QueryConstructionException {
        if (threshold == null || threshold == 0L) {
            return true;
        }
        final SelectQuery query = new SelectQueryImpl(Table.getTable(tableName));
        final Column count = Column.getColumn(null, "*").count();
        count.setColumnAlias("ROW_COUNT");
        query.addSelectColumn(count);
        Connection connection = null;
        DataSet ds = null;
        Long rowCount = 0L;
        try {
            connection = RelationalAPI.getInstance().getConnection();
            ds = RelationalAPI.getInstance().executeQuery(query, connection);
            if (ds.next()) {
                rowCount = ds.getLong(count.getColumnAlias());
            }
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return rowCount >= threshold;
    }
    
    @Override
    public boolean isArchiveFailedTable(final SQLException sqle, final String tableName) {
        if (this.isArchiveRunning(tableName)) {
            DefaultArchiveAdapter.LOGGER.warning("Table Archiving is running");
            return false;
        }
        return this.getDBAdapter().isTableNotFoundException(sqle);
    }
    
    @Override
    public void restoreUnArchivedInvisibleTable(final String tableName, final Connection conn, final SQLException sqle) {
        try {
            if (this.isArchiveFailedTable(sqle, tableName) && this.getArchiveEnabledTableNames().contains(tableName)) {
                DefaultArchiveAdapter.LOGGER.warning("restoreUnArchivedInvisibleTable is running for " + tableName);
                this.restoreUnArchivedInvisibleTable(tableName, conn);
            }
        }
        catch (final Throwable ex) {
            DefaultArchiveAdapter.LOGGER.info("Error Occured in restoreUnArchivedInvisibleTable :: " + ex.getMessage());
        }
    }
    
    private void restoreUnArchivedInvisibleTable(final String tableName, final Connection conn) {
        if (this.isArchiveRunning(tableName)) {
            DefaultArchiveAdapter.LOGGER.warning("Table archive is running for the table " + tableName + ", hence ignoring restore process");
            return;
        }
        try {
            for (final String tabName : this.getAllRelatedTableNames(tableName)) {
                this.restoreTableIfNotExist(tabName, conn);
            }
        }
        catch (final MetaDataException e) {
            DefaultArchiveAdapter.LOGGER.fine("Exception occured while restoring unarchived invisible tablename :: " + e.getMessage());
        }
        catch (final DataAccessException e2) {
            DefaultArchiveAdapter.LOGGER.fine("Exception occured while restoring unarchived invisible tablename :: " + e2.getMessage());
        }
        catch (final SQLException e3) {
            DefaultArchiveAdapter.LOGGER.fine("Exception occured while restoring unarchived invisible tablename :: " + e3.getMessage());
        }
        catch (final QueryConstructionException e4) {
            DefaultArchiveAdapter.LOGGER.fine("Exception occured while restoring unarchived invisible tablename :: " + e4.getMessage());
        }
    }
    
    private void restoreTableIfNotExist(final String tabName, final Connection conn) throws SQLException, QueryConstructionException {
        DefaultArchiveAdapter.LOGGER.warning("Restoring table _" + tabName);
        final AlterTableQuery rename = new AlterTableQueryImpl("__" + tabName);
        rename.renameTable(tabName);
        Transaction suspendedTrans = null;
        try {
            suspendedTrans = DataAccess.getTransactionManager().getTransaction();
            if (suspendedTrans != null) {
                DataAccess.getTransactionManager().suspend();
            }
            Connection newConn = null;
            try {
                newConn = ((suspendedTrans != null) ? RelationalAPI.getInstance().getConnection() : conn);
                if (!this.getDBAdapter().isTablePresentInDB(newConn, null, tabName) && this.getDBAdapter().isTablePresentInDB(newConn, null, "__" + tabName)) {
                    DefaultArchiveAdapter.LOGGER.fine("isTransactionSuspended ? " + (suspendedTrans != null) + " :: isAutoCommit ? " + conn.getAutoCommit());
                    this.getDBAdapter().alterTable(newConn, rename);
                }
                else {
                    DefaultArchiveAdapter.LOGGER.warning("Table exists :: " + tabName);
                }
            }
            finally {
                if (suspendedTrans != null) {
                    try {
                        if (newConn != null) {
                            newConn.close();
                        }
                    }
                    catch (final SQLException sqle) {
                        sqle.printStackTrace();
                    }
                    DataAccess.getTransactionManager().resume(suspendedTrans);
                }
            }
        }
        catch (final SystemException e) {
            DefaultArchiveAdapter.LOGGER.warning("Restoring unarchived table failed " + tabName);
            e.printStackTrace();
        }
        catch (final InvalidTransactionException e2) {
            DefaultArchiveAdapter.LOGGER.warning("Restoring unarchived table failed " + tabName);
            e2.printStackTrace();
        }
        catch (final IllegalStateException e3) {
            DefaultArchiveAdapter.LOGGER.warning("Restoring unarchived table failed " + tabName);
            e3.printStackTrace();
        }
    }
    
    @Override
    public boolean isArchiveRunning(final String tableName) {
        return this.currentlyRunningTables.contains(tableName);
    }
    
    protected void startedArchiving(final String tableName) {
        this.currentlyRunningTables.add(tableName);
    }
    
    protected void completedArchiving(final String tableName) {
        this.currentlyRunningTables.remove(tableName);
    }
    
    protected List<String> getAllRelatedTableNames(final String liveTableName) throws MetaDataException, DataAccessException {
        List<String> tableNamesToBeArchived = new ArrayList<String>();
        tableNamesToBeArchived.add(liveTableName);
        tableNamesToBeArchived.addAll(MetaDataUtil.getAllSlaveTableNames(liveTableName));
        tableNamesToBeArchived = PersistenceUtil.sortTables(tableNamesToBeArchived);
        return tableNamesToBeArchived;
    }
    
    public Set<String> getArchiveEnabledTableNames() {
        this.addArchiveEnabledTableNames();
        DefaultArchiveAdapter.LOGGER.fine("Table archiving enabled tables :: " + this.archiveEnabledTableNames);
        return this.archiveEnabledTableNames;
    }
    
    private void addArchiveEnabledTableNames() {
        try {
            String tableName = "";
            final List<ArchivePolicyInfo> policyList = TableArchiverUtil.getAllPolicies();
            if (policyList != null) {
                for (final ArchivePolicyInfo policy : policyList) {
                    if ((tableName = policy.getTableName()) != null) {
                        this.archiveEnabledTableNames.addAll(this.getAllRelatedTableNames(tableName));
                    }
                }
            }
            else {
                DefaultArchiveAdapter.LOGGER.info("Policy list is not initialised in TableArchiverUtil");
            }
        }
        catch (final MetaDataException ex) {
            DefaultArchiveAdapter.LOGGER.info("Error Occured in getting Archive enabled table names.");
            ex.printStackTrace();
        }
        catch (final DataAccessException ex2) {
            DefaultArchiveAdapter.LOGGER.info("Error Occured in getting Archive enabled table names.");
            ex2.printStackTrace();
        }
    }
    
    @Override
    public boolean isArchiveNotificationEnabled(final Long policyID) throws Exception {
        return TableArchiverUtil.getPolicy(policyID) != null && TableArchiverUtil.getPolicy(policyID).getNotificationHandler() != null;
    }
    
    @Override
    public void restoreFromArchive(final String archiveTable, final Criteria criteriaString) throws Exception {
        SelectQuery sQuery = null;
        StringBuffer buff = null;
        Connection conn = null;
        final RelationalAPI relapi = RelationalAPI.getInstance();
        try {
            if (!TableArchiverUtil.isArchiveTableExists(archiveTable)) {
                throw new DataAccessException("Archive table specified is not present in the db..");
            }
            conn = relapi.getConnection();
            final String actualTable = TableArchiverUtil.getActualTable(archiveTable);
            final List<String> columnNamesDB_archiveTable = this.getDBAdapter().getColumnNamesFromDB(archiveTable, null, conn.getMetaData());
            final List<String> columnNamesDB_actualTable = this.getDBAdapter().getColumnNamesFromDB(actualTable, null, conn.getMetaData());
            if (!this.compareDbVsMetaDataColumnNames(columnNamesDB_archiveTable, columnNamesDB_actualTable)) {
                throw new DataAccessException("Restore Failed..Table schema of the archive and actual table are different");
            }
            final String dbSpecArchiveTable = archiveTable;
            final List<String> tableList = new ArrayList<String>();
            tableList.add(dbSpecArchiveTable);
            sQuery = QueryConstructor.get(tableList, criteriaString);
            buff = new StringBuffer();
            buff.append("INSERT INTO ");
            buff.append(actualTable);
            buff.append(" ");
            buff.append(this.getDBAdapter().getSQLGenerator().getSQLForSelect(sQuery));
            DefaultArchiveAdapter.LOGGER.log(Level.INFO, "Going to restore data from archive table [ {0} ]", archiveTable);
            List<String> queryList = new ArrayList<String>();
            queryList.add(buff.toString());
            this.executeAllQueries(conn, queryList);
            queryList = new ArrayList<String>();
            queryList.add(this.getDBAdapter().getSQLGenerator().getSQLForDelete(dbSpecArchiveTable, criteriaString));
            this.executeAllQueries(conn, queryList);
        }
        catch (final SQLException ex) {
            DefaultArchiveAdapter.LOGGER.log(Level.INFO, "Error Occured in restoring data");
            throw ex;
        }
        catch (final DataAccessException ex2) {
            DefaultArchiveAdapter.LOGGER.log(Level.INFO, "Error Occured in restoring data");
            throw ex2;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private boolean compareDbVsMetaDataColumnNames(final List<String> columnNamesDB, final List<String> columnNames) {
        if (columnNamesDB.size() != columnNames.size()) {
            return false;
        }
        for (final String Col : columnNames) {
            if (!this.checkCaseInsensitiveColumn(columnNamesDB, Col)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkCaseInsensitiveColumn(final List<String> columnNamesDB, final String col) {
        for (final String dbCol : columnNamesDB) {
            if (col.equalsIgnoreCase(dbCol)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public DataSet getArchiveData(final String archiveTable, final Criteria cri, final Connection conn) throws Exception {
        SelectQuery sQuery = null;
        try {
            if (this.storageAdap != null) {
                return this.storageAdap.getArchiveData(archiveTable, cri);
            }
            final List<String> tableList = new ArrayList<String>();
            tableList.add(archiveTable);
            sQuery = QueryConstructor.get(tableList, cri);
            return this.getArchiveData(sQuery, conn);
        }
        catch (final Exception ex) {
            DefaultArchiveAdapter.LOGGER.log(Level.INFO, "Error occured in getting archive data");
            ex.printStackTrace();
            return null;
        }
    }
    
    @Override
    public DataSet getArchiveData(final SelectQuery query, final Connection conn) throws Exception {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        DataSet ds = null;
        try {
            if (this.storageAdap != null) {
                return this.storageAdap.getArchiveData(query);
            }
            final List<String> tableList = new ArrayList<String>();
            final String sql = this.getDBAdapter().getSQLGenerator().getSQLForSelect(query);
            ds = relapi.executeQuery(sql, conn);
        }
        catch (final Exception ex) {
            DefaultArchiveAdapter.LOGGER.log(Level.INFO, "Error occured in getting archive data");
            ex.printStackTrace();
            ds = null;
        }
        return ds;
    }
    
    private List<String> rotateTables(final ArchivePolicyInfo policy) throws Exception {
        final List<String> tablesToBeDeleted = new ArrayList<String>();
        DataSet dataSet = null;
        Connection conn = null;
        try {
            final int count = TableArchiverUtil.getArchivedTableCount(policy.getArchivePolicyID());
            conn = RelationalAPI.getInstance().getConnection();
            final SelectQuery dateSql = new SelectQueryImpl(Table.getTable("ArchiveTableDetails"));
            final Column col = new Column("ArchiveTableDetails", "ARCHIVED_DATE");
            col.setColumnAlias("Archiveddate");
            dateSql.addSelectColumn(col.distinct());
            final Criteria c1 = new Criteria(Column.getColumn("ArchiveTableDetails", "ARCHIVE_POLICY_ID"), policy.getArchivePolicyID(), 0);
            dateSql.addSortColumn(new SortColumn(col, false));
            dateSql.setCriteria(c1);
            dateSql.setRange(new Range(policy.getRotationCount() + 1, count));
            final ArrayList<String> dateList = new ArrayList<String>();
            dataSet = RelationalAPI.getInstance().executeQuery(dateSql, conn);
            while (dataSet.next()) {
                dateList.add(dataSet.getAsString(1));
            }
            final SelectQuery tableSql = new SelectQueryImpl(Table.getTable("ArchiveTableDetails"));
            final Criteria c2 = new Criteria(Column.getColumn("ArchiveTableDetails", "ARCHIVE_POLICY_ID"), policy.getArchivePolicyID(), 0);
            tableSql.addSelectColumn(Column.getColumn(null, "*"));
            Criteria cri = new Criteria(Column.getColumn("ArchiveTableDetails", "ARCHIVED_DATE"), dateList.toArray(), 8);
            cri = cri.and(c2);
            tableSql.setCriteria(cri);
            final DataObject dobj = DataAccess.get(tableSql);
            final Iterator itr = dobj.getRows("ArchiveTableDetails");
            while (itr.hasNext()) {
                tablesToBeDeleted.add((String)itr.next().get(4));
            }
            return tablesToBeDeleted;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        finally {
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void restoreUnArchivedInvisibleTable(final Collection<Table> tableList, final Connection conn, final SQLException sqle) {
        if (tableList != null) {
            final List<String> restoredTables = new ArrayList<String>();
            for (final Table tableIns : tableList) {
                final String tableName = tableIns.getTableName();
                if (!restoredTables.contains(tableName)) {
                    this.restoreUnArchivedInvisibleTable(tableIns.getTableName(), conn, sqle);
                    restoredTables.add(tableName);
                }
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(DefaultArchiveAdapter.class.getName());
    }
}
