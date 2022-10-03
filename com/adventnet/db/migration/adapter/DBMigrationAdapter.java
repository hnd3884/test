package com.adventnet.db.migration.adapter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.concurrent.ExecutionException;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.archive.TableArchiverUtil;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.migration.task.DBMigrationTask;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.Collections;
import java.util.Set;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.sql.Connection;
import com.adventnet.db.migration.report.DBMigrationStatusUpdater;
import com.adventnet.db.migration.handler.DCMigrationHandler;
import com.adventnet.db.migration.handler.DBMigrationPrePostHandler;
import javax.sql.DataSource;
import com.adventnet.ds.DataSourceManager;
import java.util.Locale;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.Map;
import com.adventnet.persistence.SchemaBrowserUtil;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
import java.util.concurrent.Executors;
import com.adventnet.db.util.CreateSchema;
import com.adventnet.db.migration.fkgraph.HierarchyProcessor;
import com.adventnet.db.migration.handler.DBMigrationHandlerFactory;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class DBMigrationAdapter implements DBMAdapter
{
    private static final Logger LOGGER;
    ExecutorService migrationWorkerPool;
    DBMigrationHandlerFactory handlerFactory;
    HierarchyProcessor hierarchyProcessor;
    CreateSchema createSchema;
    
    public DBMigrationAdapter() {
        this.migrationWorkerPool = null;
        this.handlerFactory = null;
        this.hierarchyProcessor = null;
        this.createSchema = null;
    }
    
    @Override
    public void initialize(final DBMigrationHandlerFactory handlerFactory) throws Exception {
        this.handlerFactory = handlerFactory;
        this.migrationWorkerPool = Executors.newFixedThreadPool(handlerFactory.getWorkerPoolSize());
    }
    
    private List<Long> getTableIDsOfDC(final String givendcType, final Statement stmt) throws DataAccessException, MetaDataException, QueryConstructionException, SQLException {
        final List<Long> tableIds = new ArrayList<Long>();
        final Map<String, Map<String, List<ColumnDefinition>>> map = SchemaBrowserUtil.getAllDynamicColumnDetails();
        final Map<String, List<ColumnDefinition>> childMap = map.get(givendcType);
        final List<String> tableNames = new ArrayList<String>();
        if (childMap.size() > 0) {
            for (final String tabName : childMap.keySet()) {
                tableNames.add(tabName);
            }
            final Criteria criteria = new Criteria(new Column("TableDetails", "TABLE_NAME"), tableNames.toArray(), 8);
            final DataObject dobj = DataAccess.get("TableDetails", criteria);
            final Iterator itr = dobj.getRows("TableDetails");
            Row row = null;
            while (itr.hasNext()) {
                row = itr.next();
                tableIds.add((Long)row.get(1));
            }
        }
        DBMigrationAdapter.LOGGER.info("fetched table id:: " + tableIds);
        return tableIds;
    }
    
    private void flushDestinationDBConnections() throws Exception {
        DataSourceManager.flushDataSource(DBMigrationUtil.getDestDBType().toString().toLowerCase(Locale.ENGLISH));
    }
    
    @Override
    public void migrateDataBase(final DataSource srcDataSource, final DataSource destDataSource) throws Exception {
        try {
            for (final DBMigrationPrePostHandler preHandler : DBMigrationHandlerFactory.getPrePostHandlers()) {
                preHandler.preHandle();
            }
            Connection destConn = null;
            Statement stmt = null;
            try {
                destConn = DBMigrationUtil.getDestConnection();
                stmt = destConn.createStatement();
                Map<String, DCMigrationHandler> dcmHandlers = DBMigrationHandlerFactory.getDCMHandlers();
                for (final String dcType : dcmHandlers.keySet()) {
                    final DCMigrationHandler preHandler2 = dcmHandlers.get(dcType);
                    preHandler2.preInvokeForDynamicColumns(DBMigrationUtil.getDestDBAdapter(), destConn, this.getTableIDsOfDC(dcType, stmt));
                }
                this.createSchema = new CreateSchema(destDataSource);
                final List<String> createSchemaConfTable = this.createSchemaConfTable(this.createSchema);
                DBMigrationAdapter.LOGGER.info("Tables handled by database_schema.conf ::: " + createSchemaConfTable);
                DBMigrationAdapter.LOGGER.info("List of table created using database_schema.conf :::: " + createSchemaConfTable);
                this.migrateTablesExcept(createSchemaConfTable);
                this.handlerFactory.getProgressNotifier().printMessage("Going to create FKs for tables in DatabaseSchema.conf");
                this.createSchemaConfTableConstraints(this.createSchema);
                this.shutdownWorkerPool(false);
                dcmHandlers = DBMigrationHandlerFactory.getDCMHandlers();
                for (final String dcType2 : dcmHandlers.keySet()) {
                    final DCMigrationHandler postHandler = dcmHandlers.get(dcType2);
                    postHandler.postInvokeForDynamicColumns(DBMigrationUtil.getDestDBAdapter(), destConn, this.getTableIDsOfDC(dcType2, stmt));
                }
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                }
                if (destConn != null) {
                    destConn.close();
                }
            }
            this.flushDestinationDBConnections();
            for (final DBMigrationPrePostHandler postHandler2 : DBMigrationHandlerFactory.getPrePostHandlers()) {
                postHandler2.postHandle();
            }
        }
        catch (final Exception e) {
            this.handlerFactory.getProgressNotifier().printMessage("\nStopping all running tasks forcibly");
            DBMigrationStatusUpdater.migrationFailed();
            this.handlerFactory.getProgressNotifier().migrationStopped();
            this.shutdownWorkerPool(true);
            throw e;
        }
    }
    
    private List<String> removeProcessedTables(final List<String> allTableNameFromSourceDB, final List<String> tableNamesToBeIgnored) {
        final Set<String> tableNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        tableNames.addAll(allTableNameFromSourceDB);
        tableNames.removeAll(tableNamesToBeIgnored);
        final List<String> tablesToBeMigrated = new ArrayList<String>();
        tablesToBeMigrated.addAll(tableNames);
        return tablesToBeMigrated;
    }
    
    @Override
    public void migrateTablesExcept(final List<String> tableNamesToBeIgnored) throws Exception {
        List<String> allTableNameFromSourceDB = this.getAllTableNameFromSourceDB();
        this.removeSkippedTableNames(allTableNameFromSourceDB);
        allTableNameFromSourceDB = this.removeProcessedTables(allTableNameFromSourceDB, tableNamesToBeIgnored);
        this.migrateTables(allTableNameFromSourceDB, true);
    }
    
    @Override
    public void migrateTables(final List<String> tableNames) throws Exception {
        try {
            this.migrateTables(tableNames, false);
            this.shutdownWorkerPool(false);
            this.flushDestinationDBConnections();
            for (final DBMigrationPrePostHandler postHandler : DBMigrationHandlerFactory.getPrePostHandlers()) {
                postHandler.postHandle();
            }
        }
        catch (final Exception e) {
            this.handlerFactory.getProgressNotifier().printMessage("\nStopping all running tasks forcibly");
            DBMigrationStatusUpdater.migrationFailed();
            this.handlerFactory.getProgressNotifier().migrationStopped();
            this.shutdownWorkerPool(true);
            throw e;
        }
    }
    
    protected void migrateTables(List<String> tableNames, final boolean skipPreHandlers) throws Exception {
        if (!skipPreHandlers) {
            for (final DBMigrationPrePostHandler preHandler : DBMigrationHandlerFactory.getPrePostHandlers()) {
                preHandler.preHandle();
            }
        }
        tableNames = Collections.unmodifiableList((List<? extends String>)tableNames);
        this.handlerFactory.getProgressNotifier().migrationStarted();
        this.handlerFactory.getProgressNotifier().initialize(tableNames, tableNames.size());
        DBMigrationRetryQueryQueue.initialize();
        DBMigrationAdapter.LOGGER.info("List of table name to be migrated..." + tableNames);
        this.handlerFactory.getProgressNotifier().startedLevel1Process();
        this.createTableAndMigrateData(tableNames);
        this.waitForTaskCompletion();
        this.handlerFactory.getProgressNotifier().completedLevel1Process();
        this.handlerFactory.getProgressNotifier().startedLevel2Process();
        this.createAllFKConstraints(tableNames);
        this.waitForTaskCompletion();
        this.executeAllRetryQueriesOfMickeyTable();
        this.waitForTaskCompletion();
        this.handlerFactory.getProgressNotifier().completedLevel2Process();
        this.handlerFactory.getProgressNotifier().printMessage("\nTotal no of FKs retries attempted ::: " + DBMigrationRetryQueryQueue.getTotalRetryFKCount());
    }
    
    public static List<String> tablesToBeMigrated() throws SQLException {
        Connection con = null;
        List<String> allTableNames = null;
        try {
            con = DBMigrationUtil.getDestConnection();
            allTableNames = DBMigrationUtil.getDestDBAdapter().getTables(con);
            for (final String sysTable : DBMigrationStatusUpdater.getSystemTableNames()) {
                allTableNames.remove(sysTable);
                allTableNames.remove(sysTable.toLowerCase(Locale.ENGLISH));
            }
        }
        catch (final Exception exp) {
            DBMigrationAdapter.LOGGER.severe("Exception while fetching tableNames from destination database. " + exp.getMessage());
        }
        finally {
            if (con != null) {
                con.close();
            }
        }
        return allTableNames;
    }
    
    public void createArchiveTable(final String tableName, final TableDefinition tableDef) throws Exception {
        DBMigrationAdapter.LOGGER.info("Going to create Archive table ::: " + tableName);
        this.submitTaskInWorkerPool(new DBMigrationTask(tableName, tableDef.getTableName(), (TableDefinition)tableDef.cloneWithoutFK(), DBMigrationTask.ARCHIVE_TABLE.LEVEL1, this.migrationWorkerPool, this.handlerFactory));
    }
    
    @Override
    public void createMickeyTable(final String tableName, final TableDefinition tableDef) throws Exception {
        DBMigrationAdapter.LOGGER.info("Going to create table ::: " + tableName);
        this.submitTaskInWorkerPool(new DBMigrationTask(tableName, tableDef, DBMigrationTask.MICKEY_TABLE.LEVEL1, this.migrationWorkerPool, this.handlerFactory));
    }
    
    @Override
    public void createMickeyTableConstraints(final String tableName, final TableDefinition tableDef) throws Exception {
        DBMigrationAdapter.LOGGER.info("Going to create constraint for table ::: " + tableName);
        this.submitTaskInWorkerPool(new DBMigrationTask(tableName, tableDef, DBMigrationTask.MICKEY_TABLE.LEVEL2, this.migrationWorkerPool, this.handlerFactory));
    }
    
    @Override
    public void createNonMickeyTable(final String tableName) {
        this.submitTaskInWorkerPool(new DBMigrationTask(tableName, DBMigrationTask.NON_MICKEY_TABLE.LEVEL1, this.migrationWorkerPool, this.handlerFactory));
    }
    
    @Override
    public void createNonMickeyTableConstraints(final String tableName) {
        DBMigrationAdapter.LOGGER.info("Trying to create constraints for non mickey tables.");
        this.submitTaskInWorkerPool(new DBMigrationTask(tableName, DBMigrationTask.NON_MICKEY_TABLE.LEVEL2, this.migrationWorkerPool, this.handlerFactory));
    }
    
    @Override
    public List<String> createSchemaConfTable(final CreateSchema createSchema) throws Exception {
        final Set<String> moduleNames = MetaDataUtil.getAllModuleNames();
        final Queue<String> createdTableList = new ConcurrentLinkedQueue<String>();
        for (final String moduleName : moduleNames) {
            this.createSchemaConfTable(moduleName, createSchema, createdTableList);
        }
        this.waitForTaskCompletion();
        return new ArrayList<String>(createdTableList);
    }
    
    protected void createSchemaConfTable(final String moduleName, final CreateSchema createSchema, final Queue<String> createdTableNames) {
        DBMigrationAdapter.LOGGER.info("Processing DatabaseSchema.conf for module " + moduleName);
        this.submitTaskInWorkerPool(new DBMigrationTask(moduleName, createSchema, DBMigrationTask.DATABASE_SCHEMA_CONF.LEVEL1, this.migrationWorkerPool, createdTableNames, this.handlerFactory));
    }
    
    @Override
    public void createSchemaConfTableConstraints(final CreateSchema createSchema) throws Exception {
        final Set<String> moduleNames = MetaDataUtil.getAllModuleNames();
        for (final String moduleName : moduleNames) {
            DBMigrationAdapter.LOGGER.info("Processing DatabaseSchema.conf for module " + moduleName);
            this.createSchemaConfTableConstraints(moduleName, createSchema);
        }
        this.waitForTaskCompletion();
    }
    
    @Override
    public void createSchemaConfTableConstraints(final String moduleName, final CreateSchema createSchema) {
        this.submitTaskInWorkerPool(new DBMigrationTask(moduleName, createSchema, DBMigrationTask.DATABASE_SCHEMA_CONF.LEVEL2, this.migrationWorkerPool, null, this.handlerFactory));
    }
    
    @Override
    public boolean isSkippedTable(final String tableName) {
        return this.handlerFactory.getSkippedTableList().contains(tableName);
    }
    
    protected void createTableAndMigrateData(final List<String> tableNameList) throws Exception {
        final int i = 1;
        for (final String tabName : tableNameList) {
            final String tableName = this.getDefinedTableName(tabName);
            final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
            DBMigrationAdapter.LOGGER.info("isTableDefinition identified ::: " + (tableDef != null));
            if (tableDef != null) {
                if (!tableDef.isTemplate() || (tableDef.isTemplate() && !tableName.equalsIgnoreCase(tableDef.getTableName()))) {
                    this.createMickeyTable(tableName, tableDef);
                }
                else {
                    DBMigrationAdapter.LOGGER.info("Template table physically not created, hence ignoring template table [" + tabName + "].");
                }
            }
            else if (TableArchiverUtil.isArchiveTableExists(tableName)) {
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(TableArchiverUtil.getActualTable(tableName));
                if (td == null) {
                    throw new MetaDataException("Original table can not be found for the archive table:: " + tableName);
                }
                this.createArchiveTable(tableName, td);
                DBMigrationUtil.addArchiveTable(tableName);
            }
            else if (this.handlerFactory.processNonMickeyTables() && tableName != null && tableName.equalsIgnoreCase(tabName)) {
                DBMigrationAdapter.LOGGER.info("Couldn't find TableDefinition for table [" + tabName + "], hence adding table name to non mickey table list.");
                this.createNonMickeyTable(tabName);
            }
            else {
                DBMigrationAdapter.LOGGER.severe("Ignoring NON_MICKEY_TABLE " + tabName + " from migration.");
                this.handlerFactory.getProgressNotifier().completedLevel1(tableName);
            }
        }
    }
    
    protected void createAllFKConstraints(final List<String> tableNameList) throws Exception {
        final Set<String> submittedNames = new TreeSet<String>();
        Boolean fkHierarchy = this.handlerFactory.getConfigurationAsBoolean("create.order.fk.hierarchy");
        final String dbSpecificFKGraphProp = "create.order." + DBMigrationUtil.getDestDBType().toString().toLowerCase(Locale.ENGLISH) + ".fk.hierarchy";
        if (this.handlerFactory.getConfiguration(dbSpecificFKGraphProp) != null) {
            fkHierarchy = this.handlerFactory.getConfigurationAsBoolean(dbSpecificFKGraphProp);
        }
        if (fkHierarchy) {
            this.hierarchyProcessor = new HierarchyProcessor(tableNameList, DBMigrationUtil.getSrcDataSource(), DBMigrationUtil.getSrcDBAdapter());
            DBMigrationAdapter.LOGGER.info("create.order.fk.hierarchy is true");
            this.handlerFactory.getProgressNotifier().printMessage("Foreign key creation happening based on tables hierarchy. This may take some time. Please wait.\n");
            DBMigrationAdapter.LOGGER.info("FK creation will be processed based on the tables hierarchy order.");
            final Collection<Set<String>> hierarchyMatrix = this.hierarchyProcessor.getLevels();
            for (final Set<String> nextMatrixRowElements : hierarchyMatrix) {
                this.processFKContraints(nextMatrixRowElements, submittedNames);
                this.waitForTaskCompletion();
            }
            final List<String> unProcessedNames = new ArrayList<String>(tableNameList);
            unProcessedNames.removeAll(submittedNames);
            this.processFKContraints(unProcessedNames, submittedNames);
        }
        else {
            this.processFKContraints(tableNameList, submittedNames);
        }
    }
    
    protected void processFKContraints(final Collection<String> tableNames, final Set<String> submittedNames) throws Exception {
        final List<String> archivedTableNames = DBMigrationUtil.getArchiveTables();
        for (final String tabName : tableNames) {
            final String tableName = this.getDefinedTableName(tabName);
            if (!submittedNames.contains(tableName)) {
                final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
                submittedNames.add(tableName);
                if (tableDef != null && (!tableDef.isTemplate() || !tableName.equals(tableDef.getTableName()))) {
                    this.createMickeyTableConstraints(tableName, tableDef);
                }
                else {
                    if (archivedTableNames.contains(tableName)) {
                        continue;
                    }
                    if (this.handlerFactory.processNonMickeyTables()) {
                        this.createNonMickeyTableConstraints(tableName);
                    }
                    else {
                        DBMigrationAdapter.LOGGER.severe("Ignoring NON_MICKEY_TABLE " + tabName + " from FK creation.");
                        this.handlerFactory.getProgressNotifier().completedLevel2(tableName);
                    }
                }
            }
        }
    }
    
    protected void executeAllRetryQueriesOfMickeyTable() throws InterruptedException, CloneNotSupportedException, MetaDataException, ExecutionException {
        final Set<String> tableNamesHasFKRetryQuries = DBMigrationRetryQueryQueue.getTableNamesHasFKRetryQuries();
        DBMigrationAdapter.LOGGER.info("List of table names which needs FK triggers ::: " + tableNamesHasFKRetryQuries);
        for (final String tableName : tableNamesHasFKRetryQuries) {
            DBMigrationAdapter.LOGGER.info("Going to create trigger for table ::: " + tableName);
            this.submitTaskInWorkerPoolAndWait(new DBMigrationTask(tableName, MetaDataUtil.getTableDefinitionByName(tableName), DBMigrationTask.MICKEY_TABLE.CREATE_FK_TRIGGER, this.migrationWorkerPool, this.handlerFactory));
        }
        this.waitForTaskCompletion();
        final Map<String, Queue<ForeignKeyDefinition>> retryFKQueue = DBMigrationRetryQueryQueue.getRetryFKQueue();
        for (final String tableName2 : retryFKQueue.keySet()) {
            this.submitTaskInWorkerPoolAndWait(new DBMigrationTask(tableName2, MetaDataUtil.getTableDefinitionByName(tableName2), DBMigrationTask.MICKEY_TABLE.RETRY_FAILED_FK, this.migrationWorkerPool, this.handlerFactory));
        }
        final Set<String> tableNamesHasFKIndexRetryQuries = DBMigrationRetryQueryQueue.getTableNamesHasFKIndexRetryQuries();
        for (final String tableName3 : tableNamesHasFKIndexRetryQuries) {
            this.submitTaskInWorkerPool(new DBMigrationTask(tableName3, DBMigrationTask.MICKEY_TABLE.CREATE_FK_INDEX, this.migrationWorkerPool, this.handlerFactory));
        }
        this.waitForTaskCompletion();
    }
    
    protected void removeSkippedTableNames(final List<String> tableNameList) throws QueryConstructionException, SQLException {
        final Set<String> tableSkipList = this.handlerFactory.getSkippedTableList();
        if (!tableSkipList.isEmpty()) {
            DBMigrationAdapter.LOGGER.info("Skipping tables [" + tableSkipList + "] from DBMigration process");
            final List<String> tablesToBeRemoved = new ArrayList<String>();
            for (final String tabName : tableNameList) {
                final Iterator<String> itr = tableSkipList.iterator();
                while (itr.hasNext()) {
                    final String tabPattern = itr.next().trim();
                    final Pattern pattern = Pattern.compile(tabPattern, 2);
                    if (pattern.matcher(tabName).matches()) {
                        tablesToBeRemoved.add(tabName);
                        break;
                    }
                }
            }
            tableNameList.removeAll(tablesToBeRemoved);
            this.handlerFactory.addToSkipList(tablesToBeRemoved);
        }
    }
    
    protected void submitTaskInWorkerPool(final DBMigrationTask task) {
        this.migrationWorkerPool.submit((Callable<Object>)task);
    }
    
    protected Boolean submitTaskInWorkerPoolAndWait(final DBMigrationTask task) throws InterruptedException, ExecutionException {
        return this.migrationWorkerPool.submit((Callable<Boolean>)task).get();
    }
    
    @Override
    public void waitForTaskCompletion() throws InterruptedException {
        DBMigrationAdapter.LOGGER.info("Waiting for Task completion");
        int threshold = 0;
        while (((ThreadPoolExecutor)this.migrationWorkerPool).getActiveCount() != 0 || ((ThreadPoolExecutor)this.migrationWorkerPool).getQueue().size() != 0) {
            this.checkForPoolShutdown();
            if (threshold % 30 == 0) {
                DBMigrationAdapter.LOGGER.info("Total no of active workers ::: " + ((ThreadPoolExecutor)this.migrationWorkerPool).getActiveCount());
                DBMigrationAdapter.LOGGER.info("Total no of pending tasks  ::: " + ((ThreadPoolExecutor)this.migrationWorkerPool).getQueue().size());
                threshold = 0;
            }
            ++threshold;
            Thread.sleep(1000L);
        }
        this.checkForPoolShutdown();
    }
    
    private void checkForPoolShutdown() throws InterruptedException {
        if (this.migrationWorkerPool.isShutdown()) {
            final String awaitTime = this.handlerFactory.getConfiguration("shutdown.await.time", "30");
            this.migrationWorkerPool.awaitTermination(Long.parseLong(awaitTime), TimeUnit.SECONDS);
            throw new InterruptedException("WorkerPOOL execution terminated forcibly...");
        }
    }
    
    @Override
    public void shutdownWorkerPool(final boolean forcibly) throws Exception {
        if (!forcibly) {
            this.migrationWorkerPool.shutdown();
            this.waitForWorkerPoolShutdown();
        }
        else {
            this.migrationWorkerPool.shutdownNow();
        }
    }
    
    protected void waitForWorkerPoolShutdown() throws InterruptedException {
        while (!this.migrationWorkerPool.isTerminated()) {
            Thread.sleep(1000L);
        }
    }
    
    private String getDefinedTableName(final String tableName) {
        try {
            return MetaDataUtil.getDefinedTableName(tableName);
        }
        catch (final MetaDataException mde) {
            return tableName;
        }
    }
    
    private List<String> getAllTableNameFromSourceDB() throws MetaDataException, DataAccessException, SQLException {
        Connection conn = null;
        try {
            conn = DBMigrationUtil.getSrcConnection();
            final List<String> tableNameList = DBMigrationUtil.getSrcDBAdapter().getTables(conn);
            return tableNameList;
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(DBMigrationAdapter.class.getName());
    }
}
