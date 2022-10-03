package com.adventnet.db.migration.report;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.util.QueryUtil;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.adapter.ResultSetAdapter;
import java.util.logging.Level;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.DataSet;
import java.sql.Statement;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.util.Iterator;
import java.util.Set;
import java.sql.Connection;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.migration.handler.DBMigrationHandlerFactory;
import java.util.List;
import java.util.Queue;
import com.adventnet.db.migration.notifier.ProgressNotifier;
import java.util.Map;
import java.util.logging.Logger;

public class DBMigrationStatusUpdater
{
    private static final String DBM_PROCESS_STATS_TABLENAME = "DBMProcessStats";
    private static boolean isRetry;
    private static final Logger LOGGER;
    private static Map<String, DBMTableStatus> checkPointStatus;
    private static Map<String, DBMigrationStatusSummary> summaryMap;
    private static ProgressNotifier notifier;
    private static Queue<String> skippedTableNames;
    private static boolean genSummary;
    private static List<String> systemTables;
    
    public static void initialize(final DBMigrationHandlerFactory handlerFactory) throws MetaDataException, SQLException, QueryConstructionException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        DBMigrationStatusUpdater.notifier = handlerFactory.getProgressNotifier();
        final String configuration = handlerFactory.getConfiguration("migration.summary.generate");
        DBMigrationStatusUpdater.genSummary = handlerFactory.getConfigurationAsBoolean("migration.summary.generate");
        createStatsTable(handlerFactory);
        DBMigrationStatusUpdater.systemTables.add("DBMProcessStats");
        DBMigrationStatusUpdater.systemTables.add("DBMStatus");
    }
    
    public static boolean isRetry() {
        return DBMigrationStatusUpdater.isRetry;
    }
    
    public static boolean proceedLevel1(final String tableName) {
        final boolean level1Completed = getResumeCheckPoint(tableName).isLevel1Completed();
        if (level1Completed) {
            notifyLevelCompletion(tableName, true);
        }
        return !level1Completed;
    }
    
    public static boolean proceedLevel2(final String tableName, final boolean notifyIfCompleted) {
        final boolean tableSkipped = getResumeCheckPoint(tableName).isTableSkipped();
        final boolean level2Completed = getResumeCheckPoint(tableName).isLevel2Completed();
        if (tableSkipped) {
            DBMigrationStatusUpdater.LOGGER.warning("Table creation skipped for table " + tableName + ". Hence ignoring level2 process.");
        }
        if (level2Completed && notifyIfCompleted) {
            notifyLevelCompletion(tableName, false);
        }
        return !level2Completed;
    }
    
    public static boolean isConstraintCreated(final String tableName, final String constraintName) {
        return getResumeCheckPoint(tableName).getLevel1CompletedKeysList().contains(constraintName) || getResumeCheckPoint(tableName).getLevel2CompletedKeysList().contains(constraintName);
    }
    
    public static boolean setTaskStartTime(final String tableName) {
        final DBMTableStatus resumeCheckPoint = getResumeCheckPoint(tableName);
        DBMigrationStatusSummary dbMigrationStatusSummary = DBMigrationStatusUpdater.summaryMap.get(tableName);
        if (dbMigrationStatusSummary == null) {
            dbMigrationStatusSummary = new DBMigrationStatusSummary(tableName);
            DBMigrationStatusUpdater.summaryMap.put(tableName, dbMigrationStatusSummary);
        }
        dbMigrationStatusSummary.setTaskStartTime();
        return true;
    }
    
    public static void setTaskEndTime(final String tableName) {
        DBMigrationStatusUpdater.summaryMap.get(tableName).setTaskEndTime();
    }
    
    public static void setDataPopulationStartTime(final String tableName) {
        DBMigrationStatusUpdater.summaryMap.get(tableName).setDataPopulationStartTime();
    }
    
    public static void setDataPopulationEndTime(final String tableName) {
        DBMigrationStatusUpdater.summaryMap.get(tableName).setDataPopulationEndTime();
    }
    
    public static OperationStatus setTableCreationStartTime(final String tableName) {
        final DBMTableStatus resumeCheckPoint = getResumeCheckPoint(tableName);
        if (resumeCheckPoint.isTableSkipped()) {
            DBMigrationStatusUpdater.LOGGER.info("Tablecreation process skipped for table " + tableName);
            DBMigrationStatusUpdater.skippedTableNames.add(tableName);
            return OperationStatus.SKIPPED;
        }
        if (resumeCheckPoint.isLevel1Completed() || resumeCheckPoint.isTableCreated()) {
            DBMigrationStatusUpdater.LOGGER.info("Tablecreation process already completed for table " + tableName + " during previous migration.");
            DBMigrationStatusUpdater.notifier.startedProcessingTable(tableName);
            return OperationStatus.COMPLETED;
        }
        DBMigrationStatusUpdater.summaryMap.get(tableName).setTableCreationStartTime();
        DBMigrationStatusUpdater.notifier.startedProcessingTable(tableName);
        return OperationStatus.PROCEED;
    }
    
    public static Queue<String> getSkippedTableCreationList() {
        return DBMigrationStatusUpdater.skippedTableNames;
    }
    
    public static void setTableCreationEndTime(final String tableName, final Connection destConnection) throws QueryConstructionException, SQLException {
        getResumeCheckPoint(tableName).setTableCreated(true);
        DBMigrationStatusUpdater.summaryMap.get(tableName).setTableCreationEndTime();
        getResumeCheckPoint(tableName).addEntryInStatusTable(destConnection);
    }
    
    public static void markStartTimeForCreateConstraint(final String tableName, final String constraintName, final boolean isTrigger) {
        DBMigrationStatusUpdater.summaryMap.get(tableName).markStartTimeForCreateConstraint(constraintName, isTrigger);
    }
    
    public static void markEndTimeForCreateConstraint(final String tableName, final String constraintName, final boolean isLevel1, final Connection destConnection) throws QueryConstructionException, SQLException {
        DBMigrationStatusUpdater.summaryMap.get(tableName).markEndTimeForCreateConstraint(constraintName);
        getResumeCheckPoint(tableName).updateStatusDetails(constraintName, destConnection, isLevel1);
    }
    
    public static void skipConstraintCreation(final String tableName, final String constraintName, final boolean isLevel1, final Connection destConnection) throws QueryConstructionException, SQLException {
        String columnName = null;
        String valueStr = null;
        DBMigrationStatusUpdater.LOGGER.warning("Adding skipped Constraint creation entry for " + tableName + " constraint name: " + constraintName);
        final DBMTableStatus resumeCheckPoint = getResumeCheckPoint(tableName);
        resumeCheckPoint.appendConstraintKeyName(constraintName);
        columnName = "SKIPPED_CONSTRAINT_NAMES";
        valueStr = resumeCheckPoint.getSkippedConstraintKeys();
        resumeCheckPoint.updateStatusDetails(columnName, valueStr, destConnection);
    }
    
    public static void updateLevelSatusForDataBaseSchemaConf(final String tableName, final boolean isLevel1, final Connection destConnection) throws QueryConstructionException, SQLException {
        DBMigrationStatusUpdater.LOGGER.info("Updating " + (isLevel1 ? "LEVEL1_STATUS" : "LEVEL2_STATUS") + " for table " + tableName);
        final DBMTableStatus resumeCheckPoint = getResumeCheckPoint(tableName);
        if (isLevel1) {
            resumeCheckPoint.setLevel1Status(true);
        }
        else {
            resumeCheckPoint.setLevel2Status(true);
        }
        resumeCheckPoint.updateStatusDetails(isLevel1 ? "LEVEL1_STATUS" : "LEVEL2_STATUS", Boolean.TRUE, destConnection);
    }
    
    public static void updateLevelSatus(final String tableName, final boolean isLevel1, final Connection destConnection) throws QueryConstructionException, SQLException {
        DBMigrationStatusUpdater.LOGGER.info("Updating " + (isLevel1 ? "LEVEL1_STATUS" : "LEVEL2_STATUS") + " for table " + tableName);
        final DBMTableStatus resumeCheckPoint = getResumeCheckPoint(tableName);
        if (isLevel1) {
            resumeCheckPoint.setLevel1Status(true);
        }
        else {
            resumeCheckPoint.setLevel2Status(true);
        }
        resumeCheckPoint.updateStatusDetails(isLevel1 ? "LEVEL1_STATUS" : "LEVEL2_STATUS", Boolean.TRUE, destConnection);
        notifyLevelCompletion(tableName, isLevel1);
    }
    
    private static void notifyLevelCompletion(final String tableName, final boolean isLevel1) {
        if (isLevel1) {
            DBMigrationStatusUpdater.notifier.completedLevel1(tableName);
        }
        else {
            DBMigrationStatusUpdater.notifier.completedLevel2(tableName);
            DBMigrationStatusUpdater.notifier.completedProcessingTable(tableName);
        }
    }
    
    public static void generateSummaryReport() {
        if (DBMigrationStatusUpdater.genSummary) {
            final Set<String> keySet = DBMigrationStatusUpdater.summaryMap.keySet();
            for (final String key : keySet) {
                final DBMigrationStatusSummary get = DBMigrationStatusUpdater.summaryMap.get(key);
                DBMigrationStatusUpdater.LOGGER.info(get.toString());
            }
        }
    }
    
    protected static void createStatsTable(final DBMigrationHandlerFactory handlerFactory) throws MetaDataException, SQLException, QueryConstructionException {
        final boolean isDisableResume = handlerFactory.getConfigurationAsBoolean("migration.resume.disable");
        DBMigrationStatusUpdater.LOGGER.info("isResume disabled :: " + isDisableResume);
        Connection destConnection = null;
        Connection srcConnection = null;
        Statement statement = null;
        DataSet ds = null;
        try {
            destConnection = DBMigrationUtil.getDestConnection();
            final boolean tablePresentInDB = DBMigrationUtil.getDestDBAdapter().isTablePresentInDB(destConnection, null, "DBMStatus");
            if (tablePresentInDB) {
                DBMigrationStatusUpdater.LOGGER.info("Given destination database is already migrated one. Please try with new database ;)");
                throwException("\nGiven destination database is already migrated one. Please try with new database ;)");
            }
            final boolean isprocessStatTableExists = DBMigrationUtil.getDestDBAdapter().isTablePresentInDB(destConnection, null, "DBMProcessStats");
            if (isDisableResume || !isprocessStatTableExists) {
                final List tables = DBMigrationUtil.getDestDBAdapter().getTables(destConnection);
                final boolean isDBEmpty = tables.isEmpty();
                if (!isDBEmpty) {
                    if (!isprocessStatTableExists) {
                        DBMigrationStatusUpdater.notifier.printMessage("Destination DB is not empty!!! Please retry with new database.");
                        throw new IllegalArgumentException("Destination DB is not empty!!! Please retry with new database.");
                    }
                    DBMigrationStatusUpdater.notifier.printMessage("Forcefully reinitializing destination database.");
                    DBMigrationUtil.getDestDBAdapter().dropAllTables(destConnection, false);
                }
                final TableDefinition td = getStatusTableDef();
                DBMigrationUtil.getDestDBAdapter().createTable(destConnection.createStatement(), td, null);
                DBMigrationStatusUpdater.LOGGER.info("DBMProcessStats created");
                MetaDataUtil.addTableDefinition("Persistence", td);
            }
            else {
                DBMigrationStatusUpdater.LOGGER.info("Previous migration terminated abruptly, resuming DB migration.");
                DBMigrationStatusUpdater.notifier.printMessage("Previous migration terminated abruptly, resuming DB migration.");
                srcConnection = DBMigrationUtil.getSrcConnection();
                if (!DBMigrationUtil.getSrcDBAdapter().isTablePresentInDB(srcConnection, null, "DBMStatus")) {
                    throwException("\nIt seems that, the product server started after previous migration. Hence migration resume stopped. Please retry with new destination database.");
                }
                DBMigrationStatusUpdater.isRetry = true;
                MetaDataUtil.addTableDefinition("Persistence", getStatusTableDef());
                final SelectQuery sQuery = new SelectQueryImpl(Table.getTable("DBMProcessStats"));
                sQuery.addSelectColumn(Column.getColumn(null, "*"));
                DBMigrationStatusUpdater.LOGGER.info("Fetching checkpoints....");
                DBMigrationStatusUpdater.LOGGER.info("Fetching checkpoint query " + sQuery);
                statement = destConnection.createStatement();
                ds = executeQuery(DBMigrationUtil.getDestDBAdapter().getSQLGenerator().getSQLForSelect(sQuery), statement);
                generateResumeCheckPoint(ds);
            }
        }
        finally {
            if (ds != null) {
                ds.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (destConnection != null) {
                destConnection.close();
            }
            if (srcConnection != null) {
                srcConnection.close();
            }
        }
    }
    
    private static void throwException(final String errorMessage) {
        DBMigrationStatusUpdater.notifier.printMessage(errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }
    
    public static void migrationFailed() {
        DBMigrationStatusUpdater.isRetry = true;
    }
    
    private static DataSet executeQuery(final String sql, final Statement stmt) throws SQLException, QueryConstructionException {
        if (sql == null) {
            throw new QueryConstructionException("SQL String cannot be NULL");
        }
        DBMigrationStatusUpdater.LOGGER.log(Level.FINE, "Select Query to be executed: " + sql);
        final ResultSetAdapter rs = DBMigrationUtil.getDestDBAdapter().executeQuery(stmt, sql);
        return new DataSet(rs, stmt);
    }
    
    private static TableDefinition getStatusTableDef() throws MetaDataException {
        final TableDefinition td = new TableDefinition();
        final PrimaryKeyDefinition pk = new PrimaryKeyDefinition();
        td.setTableName("DBMProcessStats");
        final ColumnDefinition colDef = new ColumnDefinition();
        colDef.setColumnName("TABLE_NAME");
        colDef.setDataType("CHAR");
        colDef.setMaxLength(255);
        td.addColumnDefinition(colDef);
        pk.setTableName(td.getTableName());
        pk.setName(td.getTableName() + "_PK");
        pk.addColumnName("TABLE_NAME");
        td.setPrimaryKey(pk);
        final ColumnDefinition c1 = new ColumnDefinition();
        c1.setColumnName("LEVEL1_QUERY_KEYS");
        c1.setDataType("CHAR");
        c1.setMaxLength(-1);
        c1.setNullable(true);
        td.addColumnDefinition(c1);
        final ColumnDefinition c2 = new ColumnDefinition();
        c2.setColumnName("LEVEL1_STATUS");
        c2.setDataType("BOOLEAN");
        c2.setNullable(false);
        c2.setDefaultValue(false);
        td.addColumnDefinition(c2);
        final ColumnDefinition c3 = new ColumnDefinition();
        c3.setColumnName("LEVEL2_QUERY_KEYS");
        c3.setDataType("CHAR");
        c3.setMaxLength(-1);
        c3.setNullable(true);
        td.addColumnDefinition(c3);
        final ColumnDefinition c4 = new ColumnDefinition();
        c4.setColumnName("LEVEL2_STATUS");
        c4.setDataType("BOOLEAN");
        c4.setNullable(false);
        c4.setDefaultValue(false);
        td.addColumnDefinition(c4);
        final ColumnDefinition c5 = new ColumnDefinition();
        c5.setColumnName("SKIPPED_PROCESS_TABLE");
        c5.setDataType("BOOLEAN");
        c5.setNullable(false);
        c5.setDefaultValue(false);
        td.addColumnDefinition(c5);
        final ColumnDefinition c6 = new ColumnDefinition();
        c6.setColumnName("SKIPPED_CONSTRAINT_NAMES");
        c6.setDataType("CHAR");
        c6.setMaxLength(-1);
        c6.setNullable(true);
        td.addColumnDefinition(c6);
        td.setModuleName("Persistence");
        return td;
    }
    
    protected static void generateResumeCheckPoint(final DataSet ds) throws SQLException {
        DBMigrationStatusUpdater.notifier.printMessage("Locating migration resume checkpoint...");
        while (ds.next()) {
            final String tableName = ds.getAsString("TABLE_NAME");
            final DBMTableStatus status = new DBMTableStatus(tableName);
            status.setTableCreated(true);
            status.setEntryAddedInTable(true);
            status.setLevel1KeyNames(ds.getAsString("LEVEL1_QUERY_KEYS"));
            status.setLevel2KeyNames(ds.getAsString("LEVEL2_QUERY_KEYS"));
            status.setLevel1Status(ds.getAsBoolean("LEVEL1_STATUS"));
            status.setLevel2Status(ds.getAsBoolean("LEVEL2_STATUS"));
            status.setSkipTableCreation(ds.getAsBoolean("SKIPPED_PROCESS_TABLE"));
            DBMigrationStatusUpdater.checkPointStatus.put(tableName, status);
        }
    }
    
    public static DBMTableStatus getResumeCheckPoint(final String tableName) {
        DBMTableStatus status = DBMigrationStatusUpdater.checkPointStatus.get(tableName);
        if (status == null) {
            for (final String key : DBMigrationStatusUpdater.checkPointStatus.keySet()) {
                if (tableName.toLowerCase(Locale.ENGLISH).equals(key.toLowerCase(Locale.ENGLISH))) {
                    status = DBMigrationStatusUpdater.checkPointStatus.get(key);
                }
            }
            status = ((status == null) ? new DBMTableStatus(tableName) : status);
            DBMigrationStatusUpdater.checkPointStatus.put(tableName, status);
        }
        return status;
    }
    
    public static boolean isAlreadyMigrated(final String tableName) {
        final DBMTableStatus status = DBMigrationStatusUpdater.checkPointStatus.get(tableName);
        if (status != null) {
            if (status.isLevel1Completed() && status.isLevel2Completed()) {
                DBMigrationStatusUpdater.LOGGER.log(Level.WARNING, "Table [{0}] already migrated during previous migration try, hence skipping migration", tableName);
                return true;
            }
            DBMigrationStatusUpdater.LOGGER.log(Level.WARNING, "Previous migration for table [{0}] is incomplete, hence resuming migration.", tableName);
        }
        return false;
    }
    
    public static void skipTableCreation(final Collection<String> tableNames) throws QueryConstructionException, SQLException {
        Connection destConnection = null;
        try {
            destConnection = DBMigrationUtil.getDestConnection();
            for (final String tableName : tableNames) {
                skipTableCreation(tableName, destConnection);
            }
        }
        finally {
            if (destConnection != null) {
                destConnection.close();
            }
        }
    }
    
    public static void skipTableCreation(final String tableName, final Connection destConnection) throws QueryConstructionException, SQLException {
        DBMigrationStatusUpdater.LOGGER.warning("Adding skipped table entry for " + tableName);
        final DBMTableStatus resumeCheckPoint = getResumeCheckPoint(tableName);
        resumeCheckPoint.setSkipTableCreation(true);
        resumeCheckPoint.addEntryInStatusTable(destConnection);
        final List<String> columnNames = Arrays.asList("SKIPPED_PROCESS_TABLE", "LEVEL1_STATUS", "LEVEL2_STATUS");
        final Map<String, Object> columnVsValue = new HashMap<String, Object>();
        columnVsValue.put("SKIPPED_PROCESS_TABLE", Boolean.TRUE);
        columnVsValue.put("LEVEL1_STATUS", Boolean.TRUE);
        columnVsValue.put("LEVEL2_STATUS", Boolean.TRUE);
        resumeCheckPoint.updateStatusDetails(columnNames, columnVsValue, destConnection);
    }
    
    public static void updateMetaDataForSkippedOperations(final Connection destConnection) throws QueryConstructionException, SQLException {
        Statement statement = null;
        try {
            statement = destConnection.createStatement();
            final SelectQuery sQuery = new SelectQueryImpl(Table.getTable("DBMProcessStats"));
            sQuery.addSelectColumn(Column.getColumn("DBMProcessStats", "TABLE_NAME"));
            final Criteria criteria = new Criteria(Column.getColumn("DBMProcessStats", "SKIPPED_PROCESS_TABLE"), true, 0);
            final List<Table> tabList = new ArrayList<Table>();
            tabList.add(Table.getTable("DBMProcessStats"));
            QueryUtil.setTypeForCriteria(criteria, tabList);
            sQuery.setCriteria(criteria);
            final DerivedColumn skippedTables = new DerivedColumn("TABLE_NAME", sQuery);
            final String updateSQL = DBMigrationUtil.getDestDBAdapter().getSQLGenerator().getSQLForDelete("TableDetails", new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), skippedTables, 8));
            final int executeUpdate = statement.executeUpdate(updateSQL);
            DBMigrationStatusUpdater.LOGGER.info(updateSQL);
            DBMigrationStatusUpdater.LOGGER.warning("MetaData in TableDetails table updated for skipped tables. Updated rows :: " + executeUpdate);
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
    
    public static List<String> getSystemTableNames() {
        return DBMigrationStatusUpdater.systemTables;
    }
    
    public static void reinitializeStatus() {
        DBMTableStatus.initialize();
        DBMigrationStatusUpdater.checkPointStatus.clear();
        DBMigrationStatusUpdater.isRetry = false;
        DBMigrationStatusUpdater.summaryMap.clear();
        DBMigrationStatusUpdater.notifier = null;
        DBMigrationStatusUpdater.skippedTableNames = new ConcurrentLinkedQueue<String>();
        DBMigrationStatusUpdater.genSummary = false;
        DBMigrationStatusUpdater.systemTables = new ArrayList<String>();
    }
    
    static {
        DBMigrationStatusUpdater.isRetry = false;
        LOGGER = Logger.getLogger(DBMigrationStatusUpdater.class.getName());
        DBMigrationStatusUpdater.checkPointStatus = new ConcurrentHashMap<String, DBMTableStatus>();
        DBMigrationStatusUpdater.summaryMap = new ConcurrentHashMap<String, DBMigrationStatusSummary>();
        DBMigrationStatusUpdater.notifier = null;
        DBMigrationStatusUpdater.skippedTableNames = new ConcurrentLinkedQueue<String>();
        DBMigrationStatusUpdater.genSummary = false;
        DBMigrationStatusUpdater.systemTables = new ArrayList<String>();
    }
    
    public enum OperationStatus
    {
        SKIPPED, 
        PROCEED, 
        COMPLETED;
    }
}
