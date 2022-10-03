package com.adventnet.db.migration.task;

import java.util.Map;
import com.adventnet.ds.query.QueryConstants;
import java.util.LinkedHashMap;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import com.adventnet.db.migration.handler.NonMickeyTablesMigrationHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.db.adapter.Ansi92SQLGenerator;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.zoho.cp.LogicalConnection;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.adapter.mssql.MssqlSQLGenerator;
import com.adventnet.db.migration.adapter.DBMigrationRetryQueryQueue;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.sql.ResultSet;
import com.adventnet.db.adapter.mssql.MssqlDBAdapter;
import com.adventnet.ds.query.DataSet;
import java.util.Iterator;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.db.migration.handler.DBMigrationHandler;
import java.sql.SQLException;
import com.adventnet.persistence.Row;
import java.util.Locale;
import com.adventnet.ds.query.BulkLoad;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.ArchiveTable;
import java.sql.Statement;
import com.adventnet.db.migration.report.DBMigrationStatusUpdater;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.util.logging.Level;
import com.adventnet.persistence.template.TemplateUtil;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.migration.handler.DBMigrationHandlerFactory;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.util.CreateSchema;
import java.util.logging.Logger;
import java.util.concurrent.Callable;

public class DBMigrationTask implements Callable<Boolean>
{
    private Logger logger;
    private String tableName;
    private String moduleName;
    private CreateSchema createSchema;
    private TaskType taskType;
    private TableDefinition tableDef;
    private TableDefinition templateTableDef;
    private ExecutorService executerPool;
    private Queue<String> tablesCreatedUsingSchemaConf;
    private DBMigrationHandlerFactory handlerFactory;
    boolean waitForStatusUpdate;
    private String liveTableName;
    
    public DBMigrationTask(final String tabName, final TableDefinition tabDef, final TaskType type, final ExecutorService workerPool, final DBMigrationHandlerFactory handlerFactory) throws CloneNotSupportedException, MetaDataException {
        this.logger = null;
        this.tableName = null;
        this.moduleName = null;
        this.createSchema = null;
        this.taskType = null;
        this.tableDef = null;
        this.templateTableDef = null;
        this.executerPool = null;
        this.tablesCreatedUsingSchemaConf = null;
        this.handlerFactory = null;
        this.waitForStatusUpdate = false;
        this.liveTableName = null;
        this.executerPool = workerPool;
        this.initializeLogger(this.tableName = tabName);
        this.taskType = type;
        this.setTableDefinition(tabDef);
        this.handlerFactory = handlerFactory;
    }
    
    public DBMigrationTask(final String tabName, final String liveTableName, final TableDefinition tabDef, final TaskType type, final ExecutorService workerPool, final DBMigrationHandlerFactory handlerFactory) throws CloneNotSupportedException, MetaDataException {
        this.logger = null;
        this.tableName = null;
        this.moduleName = null;
        this.createSchema = null;
        this.taskType = null;
        this.tableDef = null;
        this.templateTableDef = null;
        this.executerPool = null;
        this.tablesCreatedUsingSchemaConf = null;
        this.handlerFactory = null;
        this.waitForStatusUpdate = false;
        this.liveTableName = null;
        this.executerPool = workerPool;
        this.tableName = tabName;
        this.liveTableName = liveTableName;
        this.initializeLogger(this.tableName);
        this.taskType = type;
        this.setTableDefinition(tabDef);
        this.handlerFactory = handlerFactory;
    }
    
    public DBMigrationTask(final String module, final CreateSchema cSchema, final TaskType type, final ExecutorService workerPool, final Queue<String> createdTableNames, final DBMigrationHandlerFactory handlerFactory) {
        this.logger = null;
        this.tableName = null;
        this.moduleName = null;
        this.createSchema = null;
        this.taskType = null;
        this.tableDef = null;
        this.templateTableDef = null;
        this.executerPool = null;
        this.tablesCreatedUsingSchemaConf = null;
        this.handlerFactory = null;
        this.waitForStatusUpdate = false;
        this.liveTableName = null;
        this.executerPool = workerPool;
        this.createSchema = cSchema;
        this.moduleName = module;
        this.initializeLogger(module + "_DatabaseSchema");
        this.taskType = type;
        this.tablesCreatedUsingSchemaConf = createdTableNames;
        this.handlerFactory = handlerFactory;
    }
    
    public DBMigrationTask(final String tabName, final TaskType type, final ExecutorService workerPool, final DBMigrationHandlerFactory handlerFactory) {
        this.logger = null;
        this.tableName = null;
        this.moduleName = null;
        this.createSchema = null;
        this.taskType = null;
        this.tableDef = null;
        this.templateTableDef = null;
        this.executerPool = null;
        this.tablesCreatedUsingSchemaConf = null;
        this.handlerFactory = null;
        this.waitForStatusUpdate = false;
        this.liveTableName = null;
        this.executerPool = workerPool;
        this.initializeLogger(this.tableName = tabName);
        this.taskType = type;
        this.handlerFactory = handlerFactory;
    }
    
    private void initializeLogger(final String loggerName) {
        this.logger = Logger.getLogger(loggerName);
    }
    
    private void setTableDefinition(final TableDefinition tabDef) throws CloneNotSupportedException, MetaDataException {
        try {
            if (tabDef.isTemplate()) {
                final String templateName = MetaDataUtil.getTemplateHandler(tabDef.getModuleName()).getTemplateName(this.tableName);
                final String instanceId = this.tableName.substring(templateName.length() + 1, this.tableName.length());
                this.templateTableDef = tabDef;
                this.logger.info("tableName ::: " + this.tableName + "\t templateName ::: " + templateName + "\t instanceId ::: " + instanceId);
                this.tableDef = TemplateUtil.createTableDefnForTemplateInstance(this.templateTableDef, instanceId);
                this.tableName = this.tableDef.getTableName();
                this.logger.fine("Template table definition ::: " + this.tableDef);
            }
            else {
                this.tableDef = tabDef;
            }
            this.moduleName = this.tableDef.getModuleName();
        }
        catch (final NullPointerException e) {
            this.logger.log(Level.SEVERE, "Exception while initializing TableDefinition " + this.tableName, e);
            throw e;
        }
    }
    
    @Override
    public Boolean call() throws Exception {
        Connection srcConnection = null;
        Connection destConnection = null;
        try {
            srcConnection = DBMigrationUtil.getSrcConnection();
            destConnection = DBMigrationUtil.getDestConnection();
            if (this.taskType instanceof MICKEY_TABLE) {
                this.migrateMickeyTable(srcConnection, destConnection);
            }
            else if (this.taskType instanceof DATABASE_SCHEMA_CONF) {
                this.migrateSchemaConfTables(srcConnection, destConnection);
            }
            else if (this.taskType instanceof NON_MICKEY_TABLE) {
                this.migrateNonMickeyTables(srcConnection, destConnection);
            }
            else {
                if (!(this.taskType instanceof ARCHIVE_TABLE)) {
                    this.logger.severe("Unknown TaskType " + this.taskType + " Specifide...");
                    throw new IllegalArgumentException("Unknown TaskType " + this.taskType + " Specifide...");
                }
                this.migrateArchiveTables(srcConnection, destConnection);
            }
            this.logger.info("..............Migration " + this.taskType + " completed...............");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occured while migrating table " + this.tableName, e);
            e.printStackTrace();
            this.executerPool.shutdownNow();
            final String awaitTime = this.handlerFactory.getConfiguration("shutdown.await.time", "30");
            this.executerPool.awaitTermination(Long.parseLong(awaitTime), TimeUnit.SECONDS);
            throw e;
        }
        finally {
            if (srcConnection != null) {
                srcConnection.close();
            }
            if (destConnection != null) {
                destConnection.close();
            }
        }
        return Boolean.TRUE;
    }
    
    protected void migrateArchiveTables(final Connection srcConnection, final Connection destConnection) throws Exception {
        Statement statement = null;
        try {
            DBMigrationStatusUpdater.setTaskStartTime(this.tableName);
            boolean isResumeProcess = Boolean.FALSE;
            if (this.taskType == ARCHIVE_TABLE.LEVEL1) {
                this.handlerFactory.getProgressNotifier().startedLevel1(this.tableName);
                this.logger.log(Level.INFO, "Started processing create table ::: {0}", this.tableName);
                statement = destConnection.createStatement();
                if (!DBMigrationStatusUpdater.proceedLevel1(this.tableName)) {
                    this.logger.log(Level.WARNING, "LEVEL1 process for {0} is already completed during previous installation, hence ignoring.", this.tableName);
                    DBMigrationStatusUpdater.updateLevelSatus(this.tableName, true, destConnection);
                    return;
                }
                final DBMigrationStatusUpdater.OperationStatus status = DBMigrationStatusUpdater.setTableCreationStartTime(this.tableName);
                this.logger.log(Level.INFO, "Status returned {0}", status);
                if (DBMigrationStatusUpdater.OperationStatus.SKIPPED == status) {
                    this.logger.log(Level.WARNING, "{0} creation skipped.", new Object[] { this.tableName });
                    DBMigrationStatusUpdater.skipTableCreation(this.tableName, destConnection);
                    DBMigrationStatusUpdater.updateLevelSatus(this.tableName, true, destConnection);
                    return;
                }
                if (DBMigrationStatusUpdater.OperationStatus.COMPLETED == status) {
                    isResumeProcess = Boolean.TRUE;
                    this.logger.log(Level.WARNING, "{0} is already created during previous installation, hence ignoring table creation process", this.tableName);
                }
                else {
                    final boolean createTable = this.createMickeyTable(statement);
                    if (!createTable) {
                        this.handlerFactory.addToSkipList(this.tableName);
                        DBMigrationStatusUpdater.skipTableCreation(this.tableName, destConnection);
                        this.logger.warning("Table migration skipped for table " + this.tableName);
                        return;
                    }
                    DBMigrationStatusUpdater.setTableCreationEndTime(this.tableName, destConnection);
                }
                DBMigrationStatusUpdater.setDataPopulationStartTime(this.tableName);
                this.migrateArchiveTableData(srcConnection, destConnection, isResumeProcess);
                DBMigrationStatusUpdater.setDataPopulationEndTime(this.tableName);
                this.logger.info("Updating status for table [" + this.tableName + "] after completion of " + this.taskType);
                DBMigrationStatusUpdater.updateLevelSatus(this.tableName, true, destConnection);
            }
        }
        finally {
            if (statement != null) {
                statement.close();
            }
            if (!destConnection.getAutoCommit()) {
                destConnection.commit();
                destConnection.setAutoCommit(true);
            }
        }
    }
    
    protected void migrateArchiveTableData(final Connection srcConnection, final Connection destConnection, final boolean isResume) throws Exception {
        if (this.handlerFactory.ignoreTableData(this.tableName)) {
            this.logger.warning("Table data migration skipped using configuration.");
            return;
        }
        this.logger.log(Level.INFO, "Migrating table data from source DB to destination DB for the table {0}", this.tableName);
        final DBMigrationHandler rowHanlder = this.handlerFactory.getMigrationHandler(this.moduleName, this.tableName, DBMigrationHandlerFactory.HandlerLevel.ROW);
        this.logger.info("isRowHanlder defined for table " + this.tableName + " :::  " + (rowHanlder != null));
        if (rowHanlder == null && this.canUseMssqlBulkLoad(srcConnection) && !this.hasEncryptedColumm()) {
            this.migrateArchiveTableDataWithBulkCopy(srcConnection, destConnection, isResume);
            return;
        }
        final ArchiveTable archiveTable = new ArchiveTable(this.liveTableName, this.liveTableName, this.tableName, this.liveTableName, null, null);
        final SelectQuery sQuery = new SelectQueryImpl(archiveTable);
        final List<String> colNamesList = this.tableDef.getColumnNames();
        for (int i = 0; i < colNamesList.size(); ++i) {
            sQuery.addSelectColumn(new Column(this.liveTableName, colNamesList.get(i)));
        }
        for (final String columnName : MetaDataUtil.getTableDefinitionByName(this.liveTableName).getPrimaryKey().getColumnList()) {
            sQuery.addSortColumn(new SortColumn(Column.getColumn(this.liveTableName, columnName), true));
        }
        if (isResume) {
            final Long count = DBMigrationUtil.getDestDBAdapter().getTotalRowCount(destConnection, this.tableName);
            this.logger.info("Total no of rows populated during previous try :: " + count);
            if (count > 0L) {
                sQuery.setRange(new Range(count.intValue() + 1, -99));
            }
        }
        DataSet tableData = null;
        BulkLoad loadDatabuff = null;
        boolean auto_commit = false;
        try {
            final long approxRowCount = DBMigrationUtil.getDestDBAdapter().getApproxRowCount(this.tableName, destConnection.getMetaData());
            if (rowHanlder != null) {
                rowHanlder.preInvokeForFetchdata(sQuery);
            }
            RelationalAPI.getInstance().setDataType(sQuery);
            final String selectSQL = DBMigrationUtil.getSrcDBAdapter().getSQLGenerator().getSQLForSelect(sQuery);
            this.logger.log(Level.INFO, "SelectQuery ::: {0}", selectSQL);
            auto_commit = srcConnection.getAutoCommit();
            if (auto_commit) {
                srcConnection.setAutoCommit(false);
            }
            tableData = RelationalAPI.getInstance().executeQuery(sQuery, srcConnection, 0);
            this.logger.fine("Constructing prepared statement");
            long migratedRows = 0L;
            boolean isTableDataMigrated = Boolean.FALSE;
            loadDatabuff = new BulkLoad(this.liveTableName, this.tableName, destConnection, DBMigrationUtil.getDestDBAdapter());
            loadDatabuff.createTempTable(false);
            final int batchSizeForTable = this.handlerFactory.getBatchSizeForTable(this.tableName);
            loadDatabuff.setBatchSize(batchSizeForTable);
            loadDatabuff.setBufferSize(10);
            loadDatabuff.setDBName(DBMigrationUtil.getDestDBType().toString().toLowerCase(Locale.ENGLISH));
            List<String> colNames = this.getSelectColumnNames(sQuery);
            if (rowHanlder != null && rowHanlder.getSelectColumns(sQuery) != null) {
                loadDatabuff.setColumnNames(rowHanlder.getSelectColumns(sQuery));
                colNames = rowHanlder.getSelectColumns(sQuery);
            }
            while (tableData.next()) {
                isTableDataMigrated = Boolean.TRUE;
                Row row = null;
                final List<Column> cols = sQuery.getSelectColumns();
                for (int j = 0; j < cols.size(); ++j) {
                    final Column column = cols.get(j);
                    if (rowHanlder == null) {
                        loadDatabuff.setObject(column.getColumnIndex(), tableData.getValue(j + 1));
                    }
                    else {
                        if (row == null) {
                            row = new Row(this.tableDef.getTableName());
                        }
                        row.set(column.getColumnName(), tableData.getValue(column.getColumnName()));
                    }
                }
                if (rowHanlder != null) {
                    final Row returnedRow = rowHanlder.preInvokeForInsert(row);
                    if (returnedRow == null) {
                        this.logger.warning("Row skipped " + row);
                        continue;
                    }
                    row = returnedRow;
                    for (int k = 0; k < colNames.size(); ++k) {
                        loadDatabuff.setObject(k + 1, row.get(colNames.get(k)));
                    }
                }
                ++migratedRows;
                loadDatabuff.flush();
                this.handlerFactory.getProgressNotifier().migratedRows(this.tableName, approxRowCount, migratedRows);
            }
            this.logger.info(isTableDataMigrated ? ("Table data migrated to destination DB and row count is::" + migratedRows) : "No rows found");
            loadDatabuff.close();
        }
        catch (final SQLException e) {
            this.logger.log(Level.SEVERE, "Exception occured while migration data from source to destination ", e);
            throw e;
        }
        catch (final Throwable t) {
            if (loadDatabuff != null) {
                try {
                    loadDatabuff.forceClose();
                }
                catch (final Exception ex) {}
            }
            throw new Exception("Exception occured while migrating table data", t);
        }
        finally {
            if (tableData != null) {
                tableData.close();
            }
            if (auto_commit) {
                srcConnection.setAutoCommit(true);
            }
        }
    }
    
    protected void migrateArchiveTableDataWithBulkCopy(final Connection srcConnection, final Connection destConnection, final boolean isResume) throws Exception {
        final ArchiveTable archiveTable = new ArchiveTable(this.liveTableName, this.liveTableName, this.tableName, this.liveTableName, null, null);
        final SelectQuery sQuery = new SelectQueryImpl(archiveTable);
        final List<String> colNamesList = this.tableDef.getColumnNames();
        for (int i = 0; i < colNamesList.size(); ++i) {
            sQuery.addSelectColumn(new Column(this.liveTableName, colNamesList.get(i)));
        }
        for (final String columnName : MetaDataUtil.getTableDefinitionByName(this.liveTableName).getPrimaryKey().getColumnList()) {
            sQuery.addSortColumn(new SortColumn(Column.getColumn(this.liveTableName, columnName), true));
        }
        if (isResume) {
            final Long count = DBMigrationUtil.getDestDBAdapter().getTotalRowCount(destConnection, this.tableName);
            this.logger.info("Total no of rows populated during previous try :: " + count);
            if (count > 0L) {
                sQuery.setRange(new Range(count.intValue() + 1, -99));
            }
        }
        final MssqlDBAdapter mssqlDBAdapter = (MssqlDBAdapter)DBMigrationUtil.getDestDBAdapter();
        final boolean autoCommit = srcConnection.getAutoCommit();
        if (autoCommit) {
            srcConnection.setAutoCommit(false);
        }
        try (final Statement stmt = DBMigrationUtil.getSrcDBAdapter().createStatement(srcConnection, 0)) {
            final String selectSQL = RelationalAPI.getInstance().getSelectSQL(sQuery);
            this.logger.log(Level.INFO, "SelectQuery ::: {0}", selectSQL);
            try (final ResultSet rs = stmt.executeQuery(selectSQL)) {
                if (rs.isBeforeFirst()) {
                    try (final ResultSet extdResultSet = ExtdResultSetForBulkCopy.of(rs, this.tableDef)) {
                        mssqlDBAdapter.doBulkCopy(destConnection, this.tableName, extdResultSet);
                    }
                }
                else {
                    this.logger.log(Level.INFO, "No Data for [{0}]", this.tableName);
                }
            }
        }
        finally {
            if (autoCommit) {
                srcConnection.setAutoCommit(true);
            }
        }
    }
    
    protected void migrateMickeyTable(final Connection srcConnection, final Connection destConnection) throws Exception {
        Statement statement = null;
        try {
            DBMigrationStatusUpdater.setTaskStartTime(this.tableName);
            boolean isResumeProcess = Boolean.FALSE;
            if (this.taskType == MICKEY_TABLE.LEVEL1) {
                this.handlerFactory.getProgressNotifier().startedLevel1(this.tableName);
                this.logger.log(Level.INFO, "Started processing create table ::: {0}", this.tableName);
                statement = destConnection.createStatement();
                if (!DBMigrationStatusUpdater.proceedLevel1(this.tableName)) {
                    this.logger.log(Level.WARNING, "LEVEL1 process for {0} is already completed during previous installation, hence ignoring.", this.tableName);
                    DBMigrationStatusUpdater.updateLevelSatus(this.tableName, true, destConnection);
                    return;
                }
                final DBMigrationStatusUpdater.OperationStatus status = DBMigrationStatusUpdater.setTableCreationStartTime(this.tableName);
                this.logger.log(Level.INFO, "Status returned {0}", status);
                if (DBMigrationStatusUpdater.OperationStatus.SKIPPED == status) {
                    this.logger.log(Level.WARNING, "{0} creation skipped.", new Object[] { this.tableName });
                    DBMigrationStatusUpdater.skipTableCreation(this.tableName, destConnection);
                    DBMigrationStatusUpdater.updateLevelSatus(this.tableName, true, destConnection);
                    return;
                }
                if (DBMigrationStatusUpdater.OperationStatus.COMPLETED == status) {
                    isResumeProcess = Boolean.TRUE;
                    this.logger.log(Level.WARNING, "{0} is already created during previous installation, hence ignoring table creation process", this.tableName);
                }
                else {
                    final boolean createTable = this.createMickeyTable(statement);
                    if (!createTable) {
                        this.handlerFactory.addToSkipList(this.tableName);
                        DBMigrationStatusUpdater.skipTableCreation(this.tableName, destConnection);
                        this.logger.warning("Table migration skipped for table " + this.tableName);
                        return;
                    }
                    DBMigrationStatusUpdater.setTableCreationEndTime(this.tableName, destConnection);
                }
                DBMigrationStatusUpdater.setDataPopulationStartTime(this.tableName);
                this.migrateMickeyTableData(srcConnection, destConnection, isResumeProcess);
                DBMigrationStatusUpdater.setDataPopulationEndTime(this.tableName);
                if (!DBMigrationUtil.getDestDBType().equals("mysql")) {
                    this.createConstraintsInDestTable(destConnection);
                }
                this.logger.info("Updating status for table [" + this.tableName + "] after completion of " + this.taskType);
                DBMigrationStatusUpdater.updateLevelSatus(this.tableName, true, destConnection);
            }
            else if (this.taskType == MICKEY_TABLE.LEVEL2) {
                if (!DBMigrationStatusUpdater.proceedLevel2(this.tableName, true)) {
                    this.logger.log(Level.WARNING, "{0} is already created during previous installation, hence ignoring create FK in LEVEL2 processes", this.tableName);
                    return;
                }
                this.handlerFactory.getProgressNotifier().startedLevel2(this.tableName);
                this.logger.info("Started processing FK constraints for table :::: " + this.tableName);
                this.createAllFKConstraintsInTable(destConnection, this.handlerFactory.getMigrationHandler(this.moduleName, this.tableName, DBMigrationHandlerFactory.HandlerLevel.TABLE));
                if (!this.waitForStatusUpdate) {
                    this.logger.info("1 Updating status for table [" + this.tableName + "] after completion of " + this.taskType);
                    DBMigrationStatusUpdater.updateLevelSatus(this.tableName, false, destConnection);
                }
            }
            else if (this.taskType == MICKEY_TABLE.RETRY_FAILED_FK) {
                final Queue<ForeignKeyDefinition> queue = DBMigrationRetryQueryQueue.getRetryFKQueue().get(this.tableName);
                this.logger.info("Started processing retry FK constraints for table :::: " + this.tableName);
                for (final ForeignKeyDefinition foreignKeyDefinition : queue) {
                    this.logger.info("Retrying FK constraint creation :::: " + foreignKeyDefinition.getName());
                    this.createFKConstraint(foreignKeyDefinition, destConnection, null);
                }
                if (!DBMigrationRetryQueryQueue.hasAnyPendingFKTriggerRequest(this.tableName) && DBMigrationRetryQueryQueue.getFKIndexes(this.tableName).isEmpty()) {
                    this.logger.info("2 Updating status for table [" + this.tableName + "] after completion of " + this.taskType);
                    DBMigrationStatusUpdater.updateLevelSatus(this.tableName, false, destConnection);
                }
            }
            else if (this.taskType == MICKEY_TABLE.CREATE_FK_TRIGGER) {
                this.logger.info("Started processing FK trigger creations...");
                final String createTriggerSQL = ((MssqlSQLGenerator)DBMigrationUtil.getDestDBAdapter().getSQLGenerator()).getSQLForCreateTrigger(this.tableName, MetaDataUtil.getReferringForeignKeyDefinitions(this.tableName));
                DBMigrationStatusUpdater.markStartTimeForCreateConstraint(this.tableName, this.tableName + "_Trigger", true);
                statement = destConnection.createStatement();
                this.executeQuery(statement, createTriggerSQL);
                DBMigrationStatusUpdater.markEndTimeForCreateConstraint(this.tableName, this.tableName + "_Trigger", true, destConnection);
                final List<String> fkTriggerRequester = DBMigrationRetryQueryQueue.getFKTriggerRequesterNames(this.tableName);
                this.logger.info("fkTriggerRequester ::: " + fkTriggerRequester.toString());
                DBMigrationRetryQueryQueue.processedFKTriggerCreation(this.tableName);
                for (final String requesterName : fkTriggerRequester) {
                    DBMigrationRetryQueryQueue.servedFKTriggerRequest(requesterName);
                    if (!DBMigrationRetryQueryQueue.hasAnyPendingFKTriggerRequest(requesterName) && DBMigrationUtil.getDestDBType() == DBMigrationUtil.DBType.MSSQL && DBMigrationRetryQueryQueue.getFKIndexes(requesterName).isEmpty() && DBMigrationRetryQueryQueue.getRetryFKQueue().isEmpty()) {
                        this.logger.info("Updating status for requester table [" + this.tableName + "] after completion of " + this.taskType);
                        this.logger.info("3 Updateting FK trigger reqester [" + requesterName + "] status ");
                        DBMigrationStatusUpdater.updateLevelSatus(requesterName, false, destConnection);
                    }
                }
            }
            else if (this.taskType == MICKEY_TABLE.CREATE_FK_INDEX) {
                final List<IndexDefinition> fkIndexes = DBMigrationRetryQueryQueue.getFKIndexes(this.tableName);
                this.createAllIndexes(fkIndexes, destConnection, null);
                DBMigrationStatusUpdater.updateLevelSatus(this.tableName, false, destConnection);
            }
            DBMigrationStatusUpdater.setTaskEndTime(this.tableName);
        }
        finally {
            if (statement != null) {
                statement.close();
            }
            if (!destConnection.getAutoCommit()) {
                destConnection.commit();
                destConnection.setAutoCommit(true);
            }
        }
    }
    
    protected boolean createMickeyTable(final Statement statement) throws Exception {
        final String moduleName = this.tableDef.getModuleName();
        final DBMigrationHandler handler = this.handlerFactory.getMigrationHandler(moduleName, this.tableName, DBMigrationHandlerFactory.HandlerLevel.TABLE);
        if (handler != null) {
            final boolean proceed = handler.processTable(this.tableName);
            this.logger.log(Level.INFO, "process table returns ::: {0} for table [{1}] in the module [{2}]", new Object[] { proceed, this.tableName, moduleName });
            if (!proceed) {
                if (this.handlerFactory.canSkippableTable(this.tableName)) {
                    this.logger.log(Level.INFO, "Skipping table migration for the table {0}", this.tableName);
                    return false;
                }
                throw new IllegalArgumentException("Permission denied. Tables which is defined in the data-dictionary.xml cannot be skipped.");
            }
            else {
                this.logger.log(Level.INFO, "Creating table structure in destination table for table {0}", this.tableName);
                handler.preInvokeForCreateTable(this.tableName);
            }
        }
        this.logger.info("Creating table structure in destination DB");
        TableDefinition td = null;
        if (this.taskType == ARCHIVE_TABLE.LEVEL1) {
            (this.tableDef = this.cloneTableWithoutConstraint()).setTableName(this.tableName);
            td = this.tableDef;
        }
        else if (DBMigrationUtil.getDestDBType().equals("mysql")) {
            td = (TableDefinition)this.tableDef.cloneWithoutFK();
        }
        else {
            td = this.cloneTableWithoutConstraint();
        }
        DBMigrationUtil.getDestDBAdapter().createTable(statement, td, null);
        this.logger.info("Table structure created successfully in destination DB");
        if (handler != null) {
            handler.postInvokeForCreateTable(this.tableName);
        }
        return true;
    }
    
    private List<String> getSelectColumnNames(final SelectQuery sQuery) {
        final List<String> colNames = new ArrayList<String>();
        final List<Column> cols = sQuery.getSelectColumns();
        for (final Column col : cols) {
            colNames.add(col.getColumnName());
        }
        return colNames;
    }
    
    protected void migrateMickeyTableDataWithBulkCopy(final Connection srcConnection, final Connection destConnection, final boolean isResume) throws Exception {
        final SelectQuery sQuery = new SelectQueryImpl(Table.getTable(this.tableName));
        sQuery.addSelectColumn(Column.getColumn(null, "*"));
        for (final String columnName : this.tableDef.getPrimaryKey().getColumnList()) {
            sQuery.addSortColumn(new SortColumn(Column.getColumn(this.tableName, columnName), true));
        }
        if (isResume) {
            final Long count = DBMigrationUtil.getDestDBAdapter().getTotalRowCount(destConnection, this.tableName);
            this.logger.info("Total no of rows populated during previous try :: " + count);
            if (count > 0L) {
                sQuery.setRange(new Range(count.intValue() + 1, -99));
            }
        }
        final MssqlDBAdapter mssqlDBAdapter = (MssqlDBAdapter)DBMigrationUtil.getDestDBAdapter();
        final boolean autoCommit = srcConnection.getAutoCommit();
        if (autoCommit) {
            srcConnection.setAutoCommit(false);
        }
        try (final Statement stmt = DBMigrationUtil.getSrcDBAdapter().createStatement(srcConnection, 0)) {
            final String selectSQL = RelationalAPI.getInstance().getSelectSQL(sQuery);
            this.logger.log(Level.INFO, "SelectQuery ::: {0}", selectSQL);
            try (final ResultSet rs = stmt.executeQuery(selectSQL)) {
                if (rs.isBeforeFirst()) {
                    try (final ResultSet extdResultSet = ExtdResultSetForBulkCopy.of(rs, this.tableDef)) {
                        mssqlDBAdapter.doBulkCopy(destConnection, this.tableName, extdResultSet);
                    }
                }
                else {
                    this.logger.log(Level.INFO, "No Data for [{0}]", this.tableName);
                }
            }
        }
        finally {
            if (autoCommit) {
                srcConnection.setAutoCommit(true);
            }
        }
    }
    
    private boolean hasEncryptedColumm() {
        for (final ColumnDefinition cd : this.tableDef.getColumnList()) {
            if (cd.isEncryptedColumn()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean canUseMssqlBulkLoad(final Connection connection) {
        return DBMigrationUtil.getDestDBAdapter() instanceof MssqlDBAdapter && ((LogicalConnection)connection).getPhysicalConnection().getClass().getName().equalsIgnoreCase("com.microsoft.sqlserver.jdbc.SQLServerConnection");
    }
    
    protected void migrateMickeyTableData(final Connection srcConnection, final Connection destConnection, final boolean isResume) throws Exception {
        if (this.handlerFactory.ignoreTableData(this.tableName)) {
            this.logger.warning("Table data migration skipped using configuration.");
            return;
        }
        this.logger.log(Level.INFO, "Migrating table data from source DB to destination DB for the table {0}", this.tableName);
        final DBMigrationHandler rowHanlder = this.handlerFactory.getMigrationHandler(this.moduleName, this.tableDef.getTableName(), DBMigrationHandlerFactory.HandlerLevel.ROW);
        this.logger.info("isRowHanlder defined for table " + this.tableName + " :::  " + (rowHanlder != null));
        if (rowHanlder == null && this.canUseMssqlBulkLoad(srcConnection) && !this.hasEncryptedColumm()) {
            this.migrateMickeyTableDataWithBulkCopy(srcConnection, destConnection, isResume);
            return;
        }
        SelectQuery sQuery = new SelectQueryImpl(Table.getTable(this.tableDef.getTableName()));
        sQuery.addSelectColumn(Column.getColumn(null, "*"));
        for (final String columnName : this.tableDef.getPrimaryKey().getColumnList()) {
            sQuery.addSortColumn(new SortColumn(Column.getColumn(this.tableDef.getTableName(), columnName), true));
        }
        if (isResume) {
            final Long count = DBMigrationUtil.getDestDBAdapter().getTotalRowCount(destConnection, this.tableName);
            this.logger.info("Total no of rows populated during previous try :: " + count);
            if (count > 0L) {
                sQuery.setRange(new Range(count.intValue() + 1, -99));
            }
        }
        DataSet tableData = null;
        BulkLoad loadDatabuff = null;
        boolean auto_commit = false;
        try {
            final long approxRowCount = DBMigrationUtil.getDestDBAdapter().getApproxRowCount(this.tableName, destConnection.getMetaData());
            sQuery = (SelectQuery)RelationalAPI.getInstance().getModifiedQuery(sQuery);
            if (rowHanlder != null) {
                rowHanlder.preInvokeForFetchdata(sQuery);
            }
            final String selectSQL = RelationalAPI.getInstance().getSelectSQL(sQuery);
            this.logger.log(Level.INFO, "SelectQuery ::: {0}", selectSQL);
            auto_commit = srcConnection.getAutoCommit();
            if (auto_commit) {
                srcConnection.setAutoCommit(false);
            }
            tableData = RelationalAPI.getInstance().executeQuery(sQuery, srcConnection, 0);
            this.logger.fine("Constructing prepared statement");
            long migratedRows = 0L;
            boolean isTableDataMigrated = Boolean.FALSE;
            loadDatabuff = new BulkLoad(this.tableName, destConnection, DBMigrationUtil.getDestDBAdapter());
            loadDatabuff.createTempTable(false);
            final int batchSizeForTable = this.handlerFactory.getBatchSizeForTable(this.tableName);
            loadDatabuff.setBatchSize(batchSizeForTable);
            loadDatabuff.setBufferSize(10);
            loadDatabuff.setDBName(DBMigrationUtil.getDestDBType().toString().toLowerCase(Locale.ENGLISH));
            List<String> colNames = this.getSelectColumnNames(sQuery);
            if (rowHanlder != null && rowHanlder.getSelectColumns(sQuery) != null) {
                loadDatabuff.setColumnNames(rowHanlder.getSelectColumns(sQuery));
                colNames = rowHanlder.getSelectColumns(sQuery);
            }
            while (tableData.next()) {
                isTableDataMigrated = Boolean.TRUE;
                Row row = null;
                final List<Column> cols = sQuery.getSelectColumns();
                for (int i = 0; i < cols.size(); ++i) {
                    final Column column = cols.get(i);
                    if (rowHanlder == null) {
                        loadDatabuff.setObject(column.getColumnIndex(), tableData.getValue(i + 1));
                    }
                    else {
                        if (row == null) {
                            row = new Row(this.tableDef.getTableName());
                        }
                        row.set(column.getColumnName(), tableData.getValue(column.getColumnName()));
                    }
                }
                if (rowHanlder != null) {
                    final Row returnedRow = rowHanlder.preInvokeForInsert(row);
                    if (returnedRow == null) {
                        this.logger.warning("Row skipped " + row);
                        continue;
                    }
                    row = returnedRow;
                    for (int j = 0; j < colNames.size(); ++j) {
                        loadDatabuff.setObject(j + 1, row.get(colNames.get(j)));
                    }
                }
                ++migratedRows;
                loadDatabuff.flush();
                this.handlerFactory.getProgressNotifier().migratedRows(this.tableName, approxRowCount, migratedRows);
            }
            this.logger.info(isTableDataMigrated ? ("Table data migrated to destination DB and row count is::" + migratedRows) : "No rows found");
            loadDatabuff.close();
        }
        catch (final SQLException e) {
            this.logger.log(Level.SEVERE, "Exception occured while migration data from source to destination ", e);
            throw e;
        }
        catch (final Throwable t) {
            if (loadDatabuff != null) {
                try {
                    loadDatabuff.forceClose();
                }
                catch (final Exception ex) {}
            }
            throw new Exception("Exception occured while migrating table data", t);
        }
        finally {
            if (tableData != null) {
                tableData.close();
            }
            if (auto_commit) {
                srcConnection.setAutoCommit(true);
            }
        }
    }
    
    public void createConstraintsInDestTable(final Connection destConn) throws Exception {
        this.createAllIndexes(this.tableDef.getIndexes(), destConn, this.handlerFactory.getMigrationHandler(this.moduleName, this.tableName, DBMigrationHandlerFactory.HandlerLevel.TABLE));
        this.createPrimaryKey(this.tableDef.getPrimaryKey(), destConn, this.handlerFactory.getMigrationHandler(this.moduleName, this.tableName, DBMigrationHandlerFactory.HandlerLevel.TABLE));
        this.createAllUniqueKeys(this.tableDef.getUniqueKeys(), destConn, this.handlerFactory.getMigrationHandler(this.moduleName, this.tableName, DBMigrationHandlerFactory.HandlerLevel.TABLE));
    }
    
    private void createAllIndexes(final List<IndexDefinition> idxDefList, final Connection destConn, final DBMigrationHandler handler) throws QueryConstructionException, SQLException {
        if (idxDefList != null && !idxDefList.isEmpty()) {
            for (final IndexDefinition idxDef : idxDefList) {
                if (DBMigrationStatusUpdater.isConstraintCreated(this.tableName, idxDef.getName())) {
                    this.logger.log(Level.WARNING, "Index {0} is already created during previous installation, hence ignoring this index", idxDef.getName());
                }
                else {
                    this.logger.log(Level.INFO, "Creating Index[{0}] in {1}", new Object[] { idxDef.getName(), this.tableName });
                    DBMigrationStatusUpdater.markStartTimeForCreateConstraint(this.tableName, idxDef.getName(), false);
                    final AlterTableQuery addIdxQuery = new AlterTableQueryImpl(this.tableName);
                    addIdxQuery.addIndex(idxDef);
                    DBMigrationUtil.getDestDBAdapter().alterTable(destConn, addIdxQuery);
                    DBMigrationStatusUpdater.markEndTimeForCreateConstraint(this.tableName, idxDef.getName(), true, destConn);
                }
            }
        }
    }
    
    private void createPrimaryKey(final PrimaryKeyDefinition pkDef, final Connection destConn, final DBMigrationHandler handler) throws SQLException, QueryConstructionException {
        this.logger.log(Level.INFO, "Creating PK[{0}] in {1}", new Object[] { pkDef.getName(), this.tableName });
        if (DBMigrationStatusUpdater.isConstraintCreated(this.tableName, pkDef.getName())) {
            this.logger.log(Level.WARNING, "PK {0} is already created during previous installation, hence ignoring this PK", pkDef.getName());
            return;
        }
        DBMigrationStatusUpdater.markStartTimeForCreateConstraint(this.tableName, pkDef.getName(), false);
        final AlterTableQuery addPK = new AlterTableQueryImpl(this.tableName, 9);
        addPK.setConstraintName(pkDef.getName());
        addPK.setPKColumns(pkDef.getColumnList());
        DBMigrationUtil.getDestDBAdapter().alterTable(destConn, addPK);
        DBMigrationStatusUpdater.markEndTimeForCreateConstraint(this.tableName, pkDef.getName(), true, destConn);
    }
    
    private void createAllUniqueKeys(final List<UniqueKeyDefinition> ukList, final Connection destConn, final DBMigrationHandler handler) throws SQLException, QueryConstructionException {
        if (ukList != null && !ukList.isEmpty()) {
            for (final UniqueKeyDefinition ukDef : ukList) {
                this.logger.log(Level.INFO, "Creating UK[{0}] in {1}", new Object[] { ukDef.getName(), this.tableName });
                if (DBMigrationStatusUpdater.isConstraintCreated(this.tableName, ukDef.getName())) {
                    this.logger.log(Level.WARNING, "UK {0} is already created during previous installation, hence ignoring this UK", ukDef.getName());
                }
                else {
                    DBMigrationStatusUpdater.markStartTimeForCreateConstraint(this.tableName, ukDef.getName(), false);
                    final AlterTableQuery addUk = new AlterTableQueryImpl(this.tableName);
                    addUk.addUniqueKey(ukDef);
                    DBMigrationUtil.getDestDBAdapter().alterTable(destConn, addUk);
                    DBMigrationStatusUpdater.markEndTimeForCreateConstraint(this.tableName, ukDef.getName(), true, destConn);
                }
            }
        }
    }
    
    private void createAllFKConstraintsInTable(final Connection destConn, final DBMigrationHandler handler) throws Exception {
        final List<ForeignKeyDefinition> fkList = this.tableDef.getForeignKeyList();
        if (fkList != null && !fkList.isEmpty()) {
            for (final ForeignKeyDefinition fkDef : fkList) {
                if (DBMigrationStatusUpdater.isConstraintCreated(this.tableName, fkDef.getName())) {
                    this.logger.log(Level.WARNING, "FK {0} is already created during previous installation, hence ignoring this FK", fkDef.getName());
                }
                else {
                    this.createFKConstraint(fkDef, destConn, handler);
                }
            }
        }
    }
    
    private void createFKConstraint(final ForeignKeyDefinition fkDef, final Connection destConn, final DBMigrationHandler handler) throws Exception {
        final AlterTableQuery addFk = new AlterTableQueryImpl(this.tableName);
        addFk.addForeignKey(fkDef);
        if (DBMigrationUtil.getDestDBType() == DBMigrationUtil.DBType.MSSQL) {
            final AlterOperation operation = addFk.getAlterOperations().get(0);
            operation.setIgnoreFKIndexCreation(true);
            if (fkDef.getConstraints() == 1 || fkDef.getConstraints() == 2) {
                operation.setDisableTriggerCreation(true);
                final List<ForeignKeyDefinition> refFKs = MetaDataUtil.getReferringForeignKeyDefinitions(fkDef.getMasterTableName());
                if (refFKs != null && !refFKs.isEmpty()) {
                    if (this.taskType != MICKEY_TABLE.RETRY_FAILED_FK) {
                        DBMigrationRetryQueryQueue.addToFKTriggerQueue(fkDef.getMasterTableName(), this.tableName);
                    }
                    this.waitForStatusUpdate = true;
                    this.logger.info(fkDef.getName() + " trigger creation postponed. Adding FK trigger creation request for table [" + fkDef.getMasterTableName() + "]");
                }
            }
            this.logger.info("FKIndex creation process added in a retry queue for " + fkDef.getName());
            if (this.taskType != MICKEY_TABLE.RETRY_FAILED_FK) {
                DBMigrationRetryQueryQueue.addToFKIndexQueue(this.tableName, ((Ansi92SQLGenerator)DBMigrationUtil.getDestDBAdapter().getSQLGenerator()).getIndexDefForFK(fkDef));
            }
        }
        try {
            DBMigrationStatusUpdater.markStartTimeForCreateConstraint(this.tableName, fkDef.getName(), false);
            DBMigrationUtil.getDestDBAdapter().alterTable(destConn, addFk);
            DBMigrationStatusUpdater.markEndTimeForCreateConstraint(this.tableName, fkDef.getName(), false, destConn);
        }
        catch (final SQLException sqle) {
            this.logger.warning("Exception occured while creating FK consraint" + fkDef);
            this.logger.severe("Failed alter SQL " + DBMigrationUtil.getDestDBAdapter().getSQLGenerator().getSQLForAlterTable(addFk));
            if (handler == null) {
                throw sqle;
            }
            if (DBMigrationHandler.Operation.RETRY == handler.handleException(addFk, sqle, destConn)) {
                DBMigrationRetryQueryQueue.addToFKRetryQuery(this.tableName, fkDef);
                this.waitForStatusUpdate = true;
            }
        }
    }
    
    protected TableDefinition cloneTableWithoutConstraint() throws CloneNotSupportedException, MetaDataException, DataAccessException, SQLException, QueryConstructionException {
        this.logger.log(Level.INFO, "Cloning table structure :::: {0}", this.tableDef.getTableName());
        final TableDefinition newTableDef = new TableDefinition();
        newTableDef.setTableName(this.tableDef.getTableName());
        for (final Object colDefObj : this.tableDef.getColumnList()) {
            final ColumnDefinition colDef = (ColumnDefinition)((ColumnDefinition)colDefObj).clone();
            if (colDef.isKey()) {
                colDef.setNullable(false);
            }
            newTableDef.addColumnDefinition(colDef);
        }
        this.logger.info("Returning cloned table structure");
        return newTableDef;
    }
    
    private void migrateSchemaConfTables(final Connection srcConnection, final Connection destConnection) throws Exception {
        String newModuleName = null;
        boolean isResumeProcess = Boolean.FALSE;
        if (this.taskType == DATABASE_SCHEMA_CONF.LEVEL1) {
            this.logger.info("Going to read DatabaseSchema.conf for module " + this.moduleName);
            newModuleName = this.createSchema.readDataBaseSchema(this.moduleName, DBMigrationUtil.getDestDBType().toString().toLowerCase(Locale.ENGLISH));
            if (newModuleName == null) {
                return;
            }
            this.logger.info("Going to create schemas in DatabaseSchema.conf for module " + this.moduleName);
            this.handlerFactory.getProgressNotifier().printMessage("Going to create schemas in DatabaseSchema.conf for module " + this.moduleName);
            this.moduleName = newModuleName;
            final String summaryObjID = this.moduleName + "_dbschema_conf";
            DBMigrationStatusUpdater.setTaskStartTime(summaryObjID);
            if (!DBMigrationStatusUpdater.proceedLevel1(summaryObjID)) {
                this.logger.log(Level.WARNING, "LEVEL1 process for {0} is already completed during previous installation, hence ignoring.", summaryObjID);
                DBMigrationStatusUpdater.updateLevelSatus(summaryObjID, true, destConnection);
                return;
            }
            final DBMigrationStatusUpdater.OperationStatus status = DBMigrationStatusUpdater.setTableCreationStartTime(summaryObjID);
            if (DBMigrationStatusUpdater.OperationStatus.COMPLETED == status) {
                isResumeProcess = Boolean.TRUE;
            }
            this.handlerFactory.getProgressNotifier().startedLevel1(summaryObjID);
            this.createSchema.createSchemas(this.moduleName, true, false, false, destConnection, null);
            DBMigrationStatusUpdater.setTableCreationEndTime(summaryObjID, destConnection);
            final List<String> schemaConfCreatedTableNames = this.createSchema.getSchemaConfCreatedTableNames(this.moduleName);
            if (schemaConfCreatedTableNames != null) {
                DBMigrationStatusUpdater.setDataPopulationStartTime(summaryObjID);
                for (final String processedTabName : schemaConfCreatedTableNames) {
                    DBMigrationStatusUpdater.setTaskStartTime(this.tableName = DBMigrationUtil.getDestDBAdapter().getTableName(processedTabName));
                    DBMigrationStatusUpdater.setTableCreationStartTime(this.tableName);
                    DBMigrationStatusUpdater.setTableCreationEndTime(this.tableName, destConnection);
                    this.tableDef = MetaDataUtil.getTableDefinitionByName(this.tableName);
                    DBMigrationStatusUpdater.setDataPopulationStartTime(this.tableName);
                    if (this.tableDef != null) {
                        this.migrateMickeyTableData(srcConnection, destConnection, isResumeProcess);
                    }
                    else {
                        this.migrateNonMickeyTableData(srcConnection, destConnection, this.handlerFactory.getNonMickeyMigrationHandler(processedTabName, DBMigrationHandlerFactory.HandlerLevel.TABLE), isResumeProcess);
                        DBMigrationUtil.addNonMickeyTable(this.tableName);
                    }
                    DBMigrationStatusUpdater.setDataPopulationEndTime(this.tableName);
                    DBMigrationStatusUpdater.updateLevelSatusForDataBaseSchemaConf(this.tableName, true, destConnection);
                    DBMigrationStatusUpdater.updateLevelSatusForDataBaseSchemaConf(this.tableName, false, destConnection);
                    DBMigrationStatusUpdater.setTaskEndTime(this.tableName);
                    this.tablesCreatedUsingSchemaConf.add(this.tableName);
                    this.tableName = null;
                }
                DBMigrationStatusUpdater.setDataPopulationEndTime(summaryObjID);
            }
            this.logger.info("Going to all constraints in DatabaseSchema.conf for module " + this.moduleName);
            this.createSchema.createSchemas(this.moduleName, false, true, false, destConnection, null);
            this.logger.info("Updating status for table [" + this.tableName + "] after completion of " + this.taskType);
            DBMigrationStatusUpdater.updateLevelSatusForDataBaseSchemaConf(summaryObjID, true, destConnection);
            DBMigrationStatusUpdater.setTaskEndTime(summaryObjID);
        }
        else if (this.taskType == DATABASE_SCHEMA_CONF.LEVEL2) {
            this.moduleName = this.createSchema.getModuleNameFromDataBaseSchemaConf(this.moduleName);
            if (this.moduleName != null) {
                final String summaryObjID = this.moduleName + "_dbschema_conf";
                if (!DBMigrationStatusUpdater.proceedLevel2(summaryObjID, false)) {
                    return;
                }
                this.handlerFactory.getProgressNotifier().startedLevel2(summaryObjID);
                DBMigrationStatusUpdater.setTaskStartTime(summaryObjID);
                this.createSchema.createSchemas(this.moduleName, false, false, true, destConnection, null);
                this.logger.info("Updating status for table [" + summaryObjID + "] after completion of " + this.taskType);
                DBMigrationStatusUpdater.updateLevelSatusForDataBaseSchemaConf(summaryObjID, false, destConnection);
                DBMigrationStatusUpdater.setTaskEndTime(summaryObjID);
            }
        }
    }
    
    private void migrateNonMickeyTables(final Connection srcConnection, final Connection destConnection) throws Exception {
        Statement statement = null;
        boolean isResumeProcess = Boolean.FALSE;
        try {
            DBMigrationStatusUpdater.setTaskStartTime(this.tableName);
            final NonMickeyTablesMigrationHandler nonMickeyTableHandler = this.handlerFactory.getNonMickeyMigrationHandler(this.tableName, DBMigrationHandlerFactory.HandlerLevel.TABLE);
            statement = destConnection.createStatement();
            if (this.taskType == NON_MICKEY_TABLE.LEVEL1) {
                if (!nonMickeyTableHandler.processTable(this.tableName)) {
                    this.logger.log(Level.WARNING, "{0} creation skipped by handler.", new Object[] { this.tableName });
                    DBMigrationStatusUpdater.updateLevelSatus(this.tableName, true, destConnection);
                    DBMigrationStatusUpdater.skipTableCreation(this.tableName, destConnection);
                    return;
                }
                final DBMigrationStatusUpdater.OperationStatus status = DBMigrationStatusUpdater.setTableCreationStartTime(this.tableName);
                if (DBMigrationStatusUpdater.OperationStatus.SKIPPED == status) {
                    this.logger.log(Level.WARNING, "{0} creation skipped.", new Object[] { this.tableName });
                    DBMigrationStatusUpdater.updateLevelSatus(this.tableName, true, destConnection);
                    DBMigrationStatusUpdater.skipTableCreation(this.tableName, destConnection);
                    return;
                }
                if (DBMigrationStatusUpdater.OperationStatus.COMPLETED == status) {
                    isResumeProcess = Boolean.TRUE;
                    this.logger.log(Level.WARNING, "{0} is already created during previous migration, hence ignoring table creation process", this.tableName);
                    return;
                }
                this.handlerFactory.getProgressNotifier().startedLevel1(this.tableName);
                final String sqlForCreateTable = nonMickeyTableHandler.getSQLForCreateTable(this.tableName);
                if (sqlForCreateTable == null) {
                    throw new IllegalArgumentException("Invalid create table query returned [" + sqlForCreateTable + "] for NON_MICKEY table " + this.tableName);
                }
                nonMickeyTableHandler.preInvokeForCreateTable(this.tableName, sqlForCreateTable);
                DBMigrationUtil.getDestDBAdapter().execute(statement, sqlForCreateTable);
                DBMigrationStatusUpdater.setTableCreationEndTime(this.tableName, destConnection);
                nonMickeyTableHandler.postInvokeForCreateTable(this.tableName);
                DBMigrationStatusUpdater.setDataPopulationStartTime(this.tableName);
                this.migrateNonMickeyTableData(srcConnection, destConnection, nonMickeyTableHandler, isResumeProcess);
                DBMigrationStatusUpdater.setDataPopulationEndTime(this.tableName);
                this.createAllConstratintsForNonMickeyTable(nonMickeyTableHandler, statement);
                this.logger.info("Updating status for table [" + this.tableName + "] after completion of " + this.taskType);
                DBMigrationStatusUpdater.updateLevelSatus(this.tableName, true, destConnection);
                DBMigrationUtil.addNonMickeyTable(this.tableName);
            }
            else if (this.taskType == NON_MICKEY_TABLE.LEVEL2) {
                if (!DBMigrationStatusUpdater.proceedLevel2(this.tableName, false)) {
                    return;
                }
                this.handlerFactory.getProgressNotifier().startedLevel2(this.tableName);
                this.createAllFKConstratintsForNonMickeyTable(nonMickeyTableHandler, statement);
                this.logger.info("Updating status for table [" + this.tableName + "] after completion of " + this.taskType);
                DBMigrationStatusUpdater.updateLevelSatus(this.tableName, false, destConnection);
            }
            DBMigrationStatusUpdater.setTaskEndTime(this.tableName);
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
    
    protected void migrateNonMickeyTableData(final Connection srcConnection, final Connection destConnection, final NonMickeyTablesMigrationHandler nonMickeyTableHandler, final boolean isResume) throws Exception {
        if (this.handlerFactory.ignoreTableData(this.tableName)) {
            this.logger.warning("Table data migration skipped using configuration for the table:: " + this.tableName);
            return;
        }
        boolean auto_commit = false;
        boolean isTableDataMigrated = Boolean.FALSE;
        BulkLoad loadDatabuff = null;
        if (nonMickeyTableHandler != null) {
            this.logger.info("NonMickeyTableHandler " + nonMickeyTableHandler.getHandlerName() + " is defined for the table: " + this.tableName);
        }
        try {
            final List<String> columnNames = nonMickeyTableHandler.getSelectColumns(this.tableName);
            String sqlForSelect = null;
            if (nonMickeyTableHandler != null) {
                sqlForSelect = nonMickeyTableHandler.getSQLForSelect(this.tableName, columnNames, isResume);
                this.logger.info("sqlForSelect ::: " + sqlForSelect);
            }
            if (sqlForSelect == null) {
                throw new IllegalArgumentException("Invalid select query returned [" + sqlForSelect + "] for NON_MICKEY table " + this.tableName);
            }
            if (this.canUseMssqlBulkLoad(srcConnection)) {
                this.migrateNonMickeyTableDataWithBulkCopy(srcConnection, destConnection, sqlForSelect);
                return;
            }
            DataSet tableRecords = null;
            final PreparedStatement ps = null;
            final Statement srcStatement = null;
            long migratedRows = 0L;
            try {
                final long approxRowCount = DBMigrationUtil.getDestDBAdapter().getApproxRowCount(this.tableName, destConnection.getMetaData());
                auto_commit = srcConnection.getAutoCommit();
                if (auto_commit) {
                    srcConnection.setAutoCommit(false);
                }
                tableRecords = RelationalAPI.getInstance().executeQuery(sqlForSelect, srcConnection, 0);
                loadDatabuff = new BulkLoad(this.tableName, destConnection, DBMigrationUtil.getDestDBAdapter());
                loadDatabuff.createTempTable(false);
                final int batchSizeForTable = this.handlerFactory.getBatchSizeForTable(this.tableName);
                loadDatabuff.setBatchSize(batchSizeForTable);
                loadDatabuff.setBufferSize(10);
                loadDatabuff.setDBName(DBMigrationUtil.getDestDBType().toString().toLowerCase(Locale.ENGLISH));
                loadDatabuff.setColumnNames(columnNames);
                while (tableRecords.next()) {
                    isTableDataMigrated = Boolean.TRUE;
                    for (int i = 0; i < columnNames.size(); ++i) {
                        loadDatabuff.setObject(columnNames.get(i), tableRecords.getValue(columnNames.get(i)));
                    }
                    ++migratedRows;
                    loadDatabuff.flush();
                    this.handlerFactory.getProgressNotifier().migratedRows(this.tableName, approxRowCount, migratedRows);
                }
                this.logger.info(isTableDataMigrated ? ("Non Mickey Table " + this.tableName + " data migrated to destination DB") : ("No rows found for NonMickey table " + this.tableName));
                loadDatabuff.close();
            }
            finally {
                if (tableRecords != null) {
                    tableRecords.close();
                }
                if (srcStatement != null) {
                    srcStatement.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (auto_commit) {
                    srcConnection.setAutoCommit(true);
                }
            }
        }
        catch (final Exception e) {
            if (loadDatabuff != null) {
                try {
                    loadDatabuff.forceClose();
                }
                catch (final Exception ex) {}
            }
            this.logger.severe("Exception ocurred while migrating non mickey table: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Exception occured while migrating table data", e);
        }
    }
    
    protected void migrateNonMickeyTableDataWithBulkCopy(final Connection srcConnection, final Connection destConnection, final String selectSQL) throws Exception {
        final List<String> destDataTypes = new ArrayList<String>();
        final DatabaseMetaData dbmd = destConnection.getMetaData();
        try (final ResultSet rs = dbmd.getColumns(null, null, this.tableName, null)) {
            while (rs.next()) {
                destDataTypes.add(rs.getString("TYPE_NAME"));
            }
        }
        final boolean autoCommit = srcConnection.getAutoCommit();
        if (autoCommit) {
            srcConnection.setAutoCommit(false);
        }
        try (final Statement stmt = DBMigrationUtil.getSrcDBAdapter().createStatement(srcConnection, 0);
             final ResultSet rs2 = stmt.executeQuery(selectSQL);
             final ResultSet extdResultSet = ExtdResultSetForBulkCopy.of(rs2, destDataTypes)) {
            final MssqlDBAdapter mssqlDBAdapter = (MssqlDBAdapter)DBMigrationUtil.getDestDBAdapter();
            mssqlDBAdapter.doBulkCopy(destConnection, this.tableName, extdResultSet);
        }
        finally {
            if (autoCommit) {
                srcConnection.setAutoCommit(true);
            }
        }
    }
    
    private void createAllConstratintsForNonMickeyTable(final NonMickeyTablesMigrationHandler nonMickeyTableHandler, final Statement destStatement) throws SQLException {
        final String sqlForCreateIndex = nonMickeyTableHandler.getSQLForCreateIndex(this.tableName);
        this.logger.info("Creating indexes for table " + this.tableName);
        if (sqlForCreateIndex != null) {
            nonMickeyTableHandler.preInvokeForCreateIndex(this.tableName, sqlForCreateIndex);
            destStatement.execute(sqlForCreateIndex);
            nonMickeyTableHandler.postInvokeForCreateIndex(this.tableName, sqlForCreateIndex);
        }
        this.logger.info("Creating PK for table " + this.tableName);
        final String sqlForCreatePrimaryKey = nonMickeyTableHandler.getSQLForCreatePrimaryKey(this.tableName);
        if (sqlForCreatePrimaryKey != null) {
            nonMickeyTableHandler.preInvokeForCreatePK(this.tableName, sqlForCreatePrimaryKey);
            destStatement.execute(sqlForCreatePrimaryKey);
            nonMickeyTableHandler.postInvokeForCreatePK(this.tableName, sqlForCreatePrimaryKey);
        }
        this.logger.info("Creating UKs for table " + this.tableName);
        final String sqlForCreateUniqueKey = nonMickeyTableHandler.getSQLForCreateUniqueKey(this.tableName);
        if (sqlForCreateUniqueKey != null) {
            nonMickeyTableHandler.preInvokeForCreateUK(this.tableName, sqlForCreateUniqueKey);
            destStatement.execute(sqlForCreateUniqueKey);
            nonMickeyTableHandler.postInvokeForCreateUK(this.tableName, sqlForCreateUniqueKey);
        }
    }
    
    private void createAllFKConstratintsForNonMickeyTable(final NonMickeyTablesMigrationHandler nonMickeyTableHandler, final Statement destStatement) throws SQLException {
        final String sqlForCreateFK = nonMickeyTableHandler.getSQLForCreateForeignKey(this.tableName);
        this.logger.info("Creating FK for table " + this.tableName);
        if (sqlForCreateFK != null) {
            nonMickeyTableHandler.preInvokeForCreateFK(this.tableName, sqlForCreateFK);
            destStatement.execute(sqlForCreateFK);
            nonMickeyTableHandler.postInvokeForCreateFK(this.tableName, sqlForCreateFK);
        }
    }
    
    protected void executeQuery(final Statement stmt, final String sqlString) throws SQLException {
        this.logger.info("Going to execute query ::: " + sqlString);
        DBMigrationUtil.getDestDBAdapter().execute(stmt, sqlString);
    }
    
    private PreparedStatement getPreparedStatemetForTable(final TableDefinition tableDef, final Connection destConnection) throws QueryConstructionException, SQLException {
        final Map<Column, Object> columnValueMap = new LinkedHashMap<Column, Object>();
        for (final String columnName : tableDef.getColumnNames()) {
            columnValueMap.put(Column.getColumn(tableDef.getTableName(), columnName), QueryConstants.PREPARED_STMT_CONST);
        }
        final String insertSQL = DBMigrationUtil.getDestDBAdapter().getSQLGenerator().getSQLForInsert(tableDef.getTableName(), columnValueMap);
        this.logger.log(Level.FINE, "Constructed insert sql ::: {0}", insertSQL);
        return destConnection.prepareStatement(insertSQL);
    }
    
    public enum MICKEY_TABLE implements TaskType
    {
        LEVEL1, 
        LEVEL2, 
        CREATE_FK_TRIGGER, 
        CREATE_FK_INDEX, 
        RETRY_FAILED_FK;
        
        @Override
        public int getOrdinal() {
            return 0;
        }
    }
    
    public enum NON_MICKEY_TABLE implements TaskType
    {
        LEVEL1, 
        LEVEL2;
        
        @Override
        public int getOrdinal() {
            return 0;
        }
    }
    
    public enum ARCHIVE_TABLE implements TaskType
    {
        LEVEL1;
        
        @Override
        public int getOrdinal() {
            return 0;
        }
    }
    
    public enum DATABASE_SCHEMA_CONF implements TaskType
    {
        LEVEL1, 
        LEVEL2;
        
        @Override
        public int getOrdinal() {
            return 0;
        }
    }
}
