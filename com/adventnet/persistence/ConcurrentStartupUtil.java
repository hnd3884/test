package com.adventnet.persistence;

import com.adventnet.mfw.ConfPopulator;
import com.adventnet.mfw.modulestartup.ModuleStartStopProcessorUtil;
import java.util.TreeMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import com.zoho.conf.AppResources;
import com.adventnet.mfw.Starter;
import com.zoho.mickey.startup.MEServer;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.persistence.xml.Xml2DoConverter;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.mfw.ServerFailureException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.List;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Locale;
import com.adventnet.db.api.RelationalAPI;
import java.io.InputStream;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Iterator;
import java.io.File;
import com.zoho.conf.Configuration;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.logging.Level;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import com.adventnet.db.util.CreateSchema;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class ConcurrentStartupUtil
{
    private static final Logger LOGGER;
    private static final String TABLECREATION = "tableCreation";
    private static final String FKCREATION = "fkconstraint";
    private static final String MODULEPOPULATION = "modulePopulation";
    protected static ExecutorService creationThreadPool;
    private static ExecutorService dataPopulationThreadPool;
    private static CreateSchema createSchema;
    private static Set skippedTables;
    private static Set createdTables;
    private static Set completedModules;
    private static HashMap moduleVsModuleLevel;
    private static Map<Integer, Set> modulelevels;
    private static boolean isConcurrentModulesEnabled;
    private static boolean isConcurrentTableCreationEnabled;
    private static boolean isConcurrentFKCreationEnabled;
    private static boolean isCalledFromStartup;
    
    public static void initializeConcurrentStartupUtil() {
        ConcurrentStartupUtil.createSchema = new CreateSchema();
        initializeExecutor();
        ConcurrentStartupUtil.isCalledFromStartup = true;
    }
    
    public static void cleanup() {
        try {
            ConcurrentStartupUtil.LOGGER.log(Level.FINER, "Shutting down the executors");
            shutdownWorkerPool(false, ConcurrentStartupUtil.creationThreadPool);
            shutdownWorkerPool(false, ConcurrentStartupUtil.dataPopulationThreadPool);
        }
        catch (final InterruptedException exp) {
            exp.printStackTrace();
        }
    }
    
    private static void loadAllDBSchemas() throws IOException, PersistenceException, SQLException {
        final Set<String> modules = MetaDataUtil.getAllModuleNames();
        for (final String moduleName : modules) {
            final String moduleDir = new File(Configuration.getString("server.dir") + "/conf/" + moduleName).getCanonicalPath();
            final String schemaFileName = moduleDir + "/" + PersistenceInitializer.getConfigurationValue("DBName") + "/DatabaseSchema.conf";
            loadDBSchema(schemaFileName);
        }
    }
    
    private static void loadDBSchema(final String schemaFileName) throws PersistenceException, SQLException, IOException {
        final File schemaFile = new File(schemaFileName);
        if (schemaFile.exists()) {
            InputStream stream = null;
            try {
                ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Loading schemaFileName :: {0}", schemaFileName);
                stream = new FileInputStream(schemaFile);
                ConcurrentStartupUtil.createSchema.create(true, false, stream);
            }
            finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }
    }
    
    private static void loadCreatedTables() throws SQLException {
        ConcurrentStartupUtil.LOGGER.log(Level.FINE, "loading created tables");
        ResultSet tableSet = null;
        Connection con = null;
        try {
            con = RelationalAPI.getInstance().getConnection();
            final DatabaseMetaData metaData = con.getMetaData();
            tableSet = metaData.getTables(null, null, "%", new String[] { "TABLE" });
            while (tableSet.next()) {
                ConcurrentStartupUtil.createdTables.add(tableSet.getString(3).toLowerCase(Locale.ENGLISH));
            }
            ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Tables already present in db {0}", ConcurrentStartupUtil.createdTables);
        }
        finally {
            if (tableSet != null) {
                tableSet.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }
    
    private static void createTable(final TableDefinition td) throws CloneNotSupportedException {
        if (td != null && (ConcurrentStartupUtil.createdTables.contains(td.getTableName().toLowerCase(Locale.ENGLISH)) || td.isTemplate())) {
            ConcurrentStartupUtil.LOGGER.log(Level.FINE, "table {0} creation skipped", td.getTableName());
            ConcurrentStartupUtil.skippedTables.add(td.getTableName());
        }
        else {
            ConcurrentStartupUtil.creationThreadPool.submit((Callable<Object>)new ConcurrentStartupTask(td.getModuleName(), (TableDefinition)td.cloneWithoutFK(), td, "tableCreation"));
        }
    }
    
    private static void createTablesParallely() throws MetaDataException, CloneNotSupportedException, SQLException {
        loadCreatedTables();
        TableDefinition td = null;
        final Enumeration e = MetaDataUtil.getAllTableDefinitions();
        while (e.hasMoreElements()) {
            td = e.nextElement();
            createTable(td);
        }
    }
    
    private static void addFKConstraint(final TableDefinition td) throws QueryConstructionException, SQLException {
        final RelationalAPI api = RelationalAPI.getInstance();
        final List<ForeignKeyDefinition> fkList = td.getForeignKeyList();
        if (fkList != null && !fkList.isEmpty()) {
            for (final ForeignKeyDefinition fkDef : fkList) {
                final AlterTableQuery addFk = new AlterTableQueryImpl(td.getTableName());
                addFk.addForeignKey(fkDef);
                api.alterTable(addFk);
            }
        }
    }
    
    private static void createFK(final TableDefinition td) throws QueryConstructionException, SQLException {
        if (td != null && !ConcurrentStartupUtil.skippedTables.contains(td.getTableName()) && td.creatable()) {
            if (isConcurrentFKCreationEnabled()) {
                ConcurrentStartupUtil.creationThreadPool.submit((Callable<Object>)new ConcurrentStartupTask(td.getModuleName(), td, td, "fkconstraint"));
            }
            else {
                addFKConstraint(td);
            }
        }
    }
    
    private static void createFKConstraints() throws QueryConstructionException, SQLException, MetaDataException {
        TableDefinition td = null;
        final Enumeration e = MetaDataUtil.getAllTableDefinitions();
        ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Creating FK's for tables. Parallel FK Creation: {0}", new Object[] { isConcurrentFKCreationEnabled() });
        while (e.hasMoreElements()) {
            td = e.nextElement();
            createFK(td);
        }
    }
    
    public static boolean concurrentModuleCreation() throws ServerFailureException {
        ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Parallel Table Creation option is enabled");
        loadMetadata();
        validateModuleLevel();
        createAllTables();
        return isConcurrentModulePopulation() && populateModules();
    }
    
    protected static void createTables(final String moduleName) throws Exception {
        final DataDictionary dd = MetaDataUtil.getDataDictionary(moduleName);
        if (dd == null) {
            ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Skipping {0} module for parallelTableCreation", moduleName);
            return;
        }
        final List<TableDefinition> tableDefList = dd.getTableDefinitions();
        for (final TableDefinition td : tableDefList) {
            createTable(td);
        }
        waitForCreation();
        for (final TableDefinition td : tableDefList) {
            createFK(td);
        }
        waitForCreation();
    }
    
    private static void loadMetadata() throws ServerFailureException {
        try {
            final String moduleFile = Configuration.getString("server.home") + File.separator + "conf" + File.separator + "module.xml";
            final DataObject dobj = Xml2DoConverter.transform(moduleFile);
            ConcurrentStartupUtil.LOGGER.log(Level.FINER, "module Do: {0}", dobj);
            final Iterator it = dobj.getRows("Module");
            final long start = System.currentTimeMillis();
            while (it.hasNext()) {
                final Row r = it.next();
                final String moduleName = r.get("MODULENAME").toString();
                final String moduleDir = Configuration.getString("server.home") + File.separator + "conf" + File.separator + moduleName;
                ConcurrentStartupUtil.LOGGER.log(Level.FINEST, "DD loading for " + moduleDir);
                String ddFileName = moduleDir + "/dd-files.xml";
                File ddFile = new File(ddFileName);
                if (!ddFile.exists()) {
                    ddFileName = moduleDir + "/data-dictionary.xml";
                    ddFile = new File(ddFileName);
                }
                if (ddFile.exists()) {
                    PersistenceInitializer.loadDDintoMetada(moduleName, moduleDir, ddFile);
                }
            }
            final long end = System.currentTimeMillis();
            ConcurrentStartupUtil.LOGGER.log(Level.FINE, "meta data loading tinme" + (end - start));
        }
        catch (final Exception e) {
            throw new ServerFailureException(10010, "Metadata loading failed while Concurrent server startup", (Throwable)e);
        }
    }
    
    private static void createAllTables() throws ServerFailureException {
        try {
            final long start_tot = System.currentTimeMillis();
            final String outStr = "Creating Tables and schemas ::";
            ConsoleOut.print(outStr);
            ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Creating Schemas");
            long start = System.currentTimeMillis();
            loadAllDBSchemas();
            long end = System.currentTimeMillis();
            ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Schema creation time" + (end - start));
            ((MEServer)Starter.getServerInstance()).setSplashMessage("Creating Schemas...", 5);
            ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Creating Tables");
            start = System.currentTimeMillis();
            createTablesParallely();
            waitForCreation();
            end = System.currentTimeMillis();
            ConcurrentStartupUtil.LOGGER.log(Level.FINE, "table creation time" + (end - start));
            ((MEServer)Starter.getServerInstance()).setSplashMessage("Creating Tables...", 15);
            ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Creating Schema FK's");
            start = System.currentTimeMillis();
            ConcurrentStartupUtil.createSchema.createFKConstraints();
            end = System.currentTimeMillis();
            ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Schema FKcreation time" + (end - start));
            ((MEServer)Starter.getServerInstance()).setSplashMessage("Creating Constraints...", 20);
            ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Creating FK's");
            start = System.currentTimeMillis();
            createFKConstraints();
            waitForCreation();
            end = System.currentTimeMillis();
            ConcurrentStartupUtil.LOGGER.log(Level.FINE, "FKcreation time" + (end - start));
            ((MEServer)Starter.getServerInstance()).setSplashMessage("Creating Constraints...", 30);
            final long end_tot = System.currentTimeMillis();
            printStatus(outStr, "COMPLETED");
            ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Total creation time" + (end_tot - start_tot));
        }
        catch (final Exception e) {
            throw new ServerFailureException(10012, "Table Creation failed Concurrent server startup", (Throwable)e);
        }
        finally {
            try {
                shutdownWorkerPool(false, ConcurrentStartupUtil.creationThreadPool);
            }
            catch (final InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }
    
    private static boolean populateModules() throws ServerFailureException {
        try {
            final Map moduleLevelMap = getModulesVsLevelmap();
            final int progressRate = 30 / moduleLevelMap.size();
            int progress = 30;
            final long start = System.currentTimeMillis();
            long totalPopulationTime = 0L;
            for (final Object key : moduleLevelMap.keySet()) {
                final Integer level = (Integer)key;
                final Set<String> modules = moduleLevelMap.get(level);
                final long start2 = System.currentTimeMillis();
                for (final String moduleName : modules) {
                    ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Going to populate Module {0} Level {1}", new Object[] { level, moduleName });
                    populateModule(moduleName);
                    ConsoleOut.print(moduleName);
                    printStatus(moduleName, "POPULATED_PARALLELY");
                }
                waitForPopulation();
                addCompletedModule(modules);
                final long end1 = System.currentTimeMillis();
                ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Level {0} Population time {1} ", new Object[] { level, end1 - start2 });
                ((MEServer)Starter.getServerInstance()).setSplashMessage("Initializing modules of level " + level + "...", progress);
                progress += progressRate;
            }
            final long end2 = System.currentTimeMillis();
            totalPopulationTime = end2 - start;
            ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Total module population time Parallely {0}", new Object[] { totalPopulationTime });
            return true;
        }
        catch (final Exception e) {
            throw new ServerFailureException(10013, "Module population failed while concurrent server startup", (Throwable)e);
        }
    }
    
    private static void populateModule(final String moduleName) throws IOException {
        ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Population for module " + moduleName + " pulled inside the queue");
        ConcurrentStartupUtil.dataPopulationThreadPool.submit((Callable<Object>)new ConcurrentStartupTask(moduleName, "modulePopulation"));
    }
    
    private static final void initializeExecutor() {
        final int threadPoolSize = AppResources.getInteger("concurrent.tablecreation.threads", Integer.valueOf(15));
        ConcurrentStartupUtil.creationThreadPool = Executors.newFixedThreadPool(threadPoolSize);
        ConcurrentStartupUtil.dataPopulationThreadPool = Executors.newFixedThreadPool(threadPoolSize);
        ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Initialized table creation thread pool with size ::: {0} threads", threadPoolSize);
    }
    
    private static void waitForCreation() throws InterruptedException {
        waitForTaskCompletion(ConcurrentStartupUtil.creationThreadPool);
    }
    
    private static void waitForPopulation() throws InterruptedException {
        waitForTaskCompletion(ConcurrentStartupUtil.dataPopulationThreadPool);
    }
    
    private static final void waitForTaskCompletion(final ExecutorService pool) throws InterruptedException {
        ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Waiting for Task completion");
        int threshold = 0;
        while (((ThreadPoolExecutor)pool).getActiveCount() != 0 || ((ThreadPoolExecutor)pool).getQueue().size() != 0 || ((ThreadPoolExecutor)pool).getTaskCount() != ((ThreadPoolExecutor)pool).getCompletedTaskCount()) {
            checkForPoolShutdown(pool);
            if (threshold % 30 == 0) {
                ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Total no of active workers ::: " + ((ThreadPoolExecutor)pool).getActiveCount());
                ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Total no of pending tasks  ::: " + ((ThreadPoolExecutor)pool).getQueue().size());
                ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Total no of submitted tasks  ::: " + ((ThreadPoolExecutor)pool).getTaskCount());
                ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Total no of completed tasks  ::: " + ((ThreadPoolExecutor)pool).getCompletedTaskCount());
                threshold = 0;
            }
            ++threshold;
            Thread.sleep(100L);
        }
        checkForPoolShutdown(pool);
    }
    
    private static final void shutdownWorkerPool(final boolean forcibly, final ExecutorService executorPool) throws InterruptedException {
        if (!forcibly) {
            executorPool.shutdown();
            waitForWorkerPoolShutdown(executorPool);
        }
        else {
            executorPool.shutdownNow();
        }
    }
    
    private static void waitForWorkerPoolShutdown(final ExecutorService executorPool) throws InterruptedException {
        while (!executorPool.isTerminated()) {
            Thread.sleep(1000L);
        }
    }
    
    private static final void checkForPoolShutdown(final ExecutorService executorPool) throws InterruptedException {
        if (executorPool.isShutdown()) {
            throw new InterruptedException("WorkerPOOL execution terminated forcibly...");
        }
    }
    
    public static Map getParentModules() throws MetaDataException {
        final long start = System.currentTimeMillis();
        final Map<String, Set<String>> hierarchyMap = new HashMap<String, Set<String>>();
        for (final String module : MetaDataUtil.getAllDataDictionarNames()) {
            hierarchyMap.put(module, new HashSet<String>());
        }
        final List<TableDefinition> tables = MetaDataUtil.getTableDefinitions();
        for (final TableDefinition td : tables) {
            final String tableName = td.getTableName();
            final String moduleName = td.getModuleName();
            final List<String> refTables = MetaDataUtil.getSlaveTableNames(tableName);
            for (final String child : refTables) {
                final String childModule = MetaDataUtil.getTableDefinitionByName(child).getModuleName();
                if (!childModule.equals(moduleName)) {
                    hierarchyMap.get(childModule).add(moduleName);
                }
            }
        }
        final long end = System.currentTimeMillis();
        ConcurrentStartupUtil.LOGGER.log(Level.FINER, "FK Hierarchy Map construction. Map :{0}  Duration : {1}", new Object[] { hierarchyMap, end - start });
        return hierarchyMap;
    }
    
    private static Set getModuleNamesUptoLevel(final Integer level) {
        final Set<String> modules = new HashSet<String>();
        for (int i = 1; i < level; ++i) {
            modules.addAll(ConcurrentStartupUtil.modulelevels.get(i));
        }
        return modules;
    }
    
    private static void validateModuleLevel() throws ServerFailureException {
        try {
            final long start = System.currentTimeMillis();
            final Map hierarchyMap = getParentModules();
            final Set<String> moduleNames = hierarchyMap.keySet();
            for (final String moduleName : moduleNames) {
                if (!moduleName.equals("MetaPersistence")) {
                    if (moduleName.equals("Persistence")) {
                        continue;
                    }
                    final Set fkDependency = hierarchyMap.get(moduleName);
                    final Set userDefinedDependency = getModuleNamesUptoLevel(getModuleLevel(moduleName));
                    ConcurrentStartupUtil.LOGGER.log(Level.FINER, "module Name [ {0} ] FKDependency {1} userDefinedDependency {2}", new Object[] { moduleName, fkDependency, userDefinedDependency });
                    if (userDefinedDependency.containsAll(fkDependency)) {
                        ConcurrentStartupUtil.LOGGER.log(Level.FINER, "Module [ {0} ] level is definied correctly", new Object[] { moduleName });
                    }
                    else {
                        ConcurrentStartupUtil.LOGGER.log(Level.WARNING, "Module level specified is incorrect. Dependent modules for module [ {0} ] should be atleast {1} where specified is {2}", new Object[] { moduleName, fkDependency, userDefinedDependency });
                    }
                }
            }
            final long end = System.currentTimeMillis();
            ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Validating Module Level compelted. Duration: {0} ", new Object[] { end - start });
        }
        catch (final MetaDataException e) {
            e.printStackTrace();
            throw new ServerFailureException(10011, "Module level validationg failed during Concurrent server startup", (Throwable)e);
        }
    }
    
    private static void printStatus(final String str, final String msg) {
        String out = "";
        for (int i = str.length(); i < 50; ++i) {
            out += " ";
        }
        out = out + "[ " + msg + " ]";
        ConsoleOut.println(out);
    }
    
    public static boolean isModuleCompleted(final String moduleName) {
        return moduleName != null && ConcurrentStartupUtil.completedModules.contains(moduleName);
    }
    
    private static void addCompletedModule(final Set moduleNames) {
        ConcurrentStartupUtil.completedModules.addAll(moduleNames);
    }
    
    private static Set getCompletedModules() {
        return ConcurrentStartupUtil.completedModules;
    }
    
    public static void setModuleLevel(final String moduleName, final Integer level) {
        ConcurrentStartupUtil.moduleVsModuleLevel.put(moduleName, level);
        Set set = ConcurrentStartupUtil.modulelevels.get(level);
        if (set == null) {
            set = new HashSet();
        }
        set.add(moduleName);
        ConcurrentStartupUtil.modulelevels.put(level, set);
    }
    
    private static Set getModules(final Integer level) {
        return ConcurrentStartupUtil.modulelevels.get(level);
    }
    
    private static Map getModulesVsLevelmap() {
        return ConcurrentStartupUtil.modulelevels;
    }
    
    public static Integer getModuleLevel(final String moduleName) {
        final Integer level = ConcurrentStartupUtil.moduleVsModuleLevel.get(moduleName);
        if (level == null) {
            ConcurrentStartupUtil.LOGGER.log(Level.WARNING, "Unknown module {0} name specified", new Object[] { moduleName });
            return -1;
        }
        return level;
    }
    
    public static boolean isConcurrentTableCreationEnabled() {
        return ConcurrentStartupUtil.isConcurrentTableCreationEnabled;
    }
    
    public static boolean isConcurrentTableCreation() {
        final String dbName = PersistenceInitializer.getConfigurationValue("DBName");
        return dbName != null && !dbName.equalsIgnoreCase("mysql") && ConcurrentStartupUtil.isConcurrentTableCreationEnabled && ConcurrentStartupUtil.isCalledFromStartup;
    }
    
    public static boolean isConcurrentModulesEnabled() {
        return ConcurrentStartupUtil.isConcurrentModulesEnabled;
    }
    
    public static boolean isConcurrentModulePopulation() {
        final String dbName = PersistenceInitializer.getConfigurationValue("DBName");
        return dbName != null && dbName.equalsIgnoreCase("postgres") && isConcurrentTableCreation() && ConcurrentStartupUtil.isConcurrentModulesEnabled;
    }
    
    private static boolean isConcurrentFKCreationEnabled() {
        return PersistenceInitializer.getConfigurationValue("DBName").equalsIgnoreCase("postgres") && ConcurrentStartupUtil.isConcurrentFKCreationEnabled;
    }
    
    static {
        LOGGER = Logger.getLogger(ConcurrentStartupUtil.class.getName());
        ConcurrentStartupUtil.creationThreadPool = null;
        ConcurrentStartupUtil.dataPopulationThreadPool = null;
        ConcurrentStartupUtil.createSchema = null;
        ConcurrentStartupUtil.skippedTables = new HashSet();
        ConcurrentStartupUtil.createdTables = new HashSet();
        ConcurrentStartupUtil.completedModules = new HashSet();
        ConcurrentStartupUtil.moduleVsModuleLevel = new HashMap();
        ConcurrentStartupUtil.modulelevels = new TreeMap<Integer, Set>();
        ConcurrentStartupUtil.isConcurrentModulesEnabled = false;
        ConcurrentStartupUtil.isConcurrentTableCreationEnabled = false;
        ConcurrentStartupUtil.isConcurrentFKCreationEnabled = false;
        ConcurrentStartupUtil.isCalledFromStartup = false;
        ConcurrentStartupUtil.isConcurrentTableCreationEnabled = Boolean.getBoolean("concurrent.tablecreation");
        ConcurrentStartupUtil.isConcurrentFKCreationEnabled = Boolean.getBoolean("concurrent.fkcreation");
        ConcurrentStartupUtil.isConcurrentModulesEnabled = Boolean.getBoolean("concurrent.modulepopulation");
    }
    
    private static class ConcurrentStartupTask implements Callable<Boolean>
    {
        private String moduleName;
        private TableDefinition clone_td;
        private TableDefinition orig_td;
        private String operation;
        
        public ConcurrentStartupTask(final String moduleName, final TableDefinition clone_td, final TableDefinition orig_td, final String oper) {
            this.moduleName = moduleName;
            this.clone_td = clone_td;
            this.orig_td = orig_td;
            this.operation = oper;
        }
        
        public ConcurrentStartupTask(final String moduleName, final String oper) {
            this.moduleName = moduleName;
            this.operation = oper;
        }
        
        @Override
        public Boolean call() throws Exception {
            try {
                if (this.operation.equalsIgnoreCase("tableCreation")) {
                    ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Creatingg table module name: [ {0} ] tableName: [ {1} ] ", new Object[] { this.moduleName, this.clone_td.getTableName() });
                    RelationalAPI.getInstance().createTable(this.clone_td, null, null);
                }
                else if (this.operation.equalsIgnoreCase("fkconstraint")) {
                    ConcurrentStartupUtil.LOGGER.log(Level.FINE, "Creating FKConstraint module name: [ {0} ] tableName: [ {1} ] ", new Object[] { this.moduleName, this.clone_td.getTableName() });
                    this.createFKConstraint();
                }
                else if (this.operation.equalsIgnoreCase("modulePopulation")) {
                    ConcurrentStartupUtil.LOGGER.info("Population data for module: " + this.moduleName);
                    this.populate(this.moduleName);
                }
            }
            catch (final Exception exp) {
                ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Error while creating {0} for table {1} module {2}", new Object[] { this.operation, this.orig_td.getTableName(), this.moduleName });
                exp.printStackTrace();
                ConcurrentStartupUtil.creationThreadPool.shutdownNow();
                throw exp;
            }
            return true;
        }
        
        private void createFKConstraint() throws QueryConstructionException, SQLException {
            addFKConstraint(this.orig_td);
        }
        
        private void populate(final String moduleName) throws Exception {
            try {
                ModuleStartStopProcessorUtil.execute_preStartProcesses(moduleName);
                final long start = System.currentTimeMillis();
                PersistenceInitializer.addEntryInModuleTable(moduleName, null);
                final String moduleDir = new File(Configuration.getString("server.dir") + "/conf/" + moduleName).getCanonicalPath();
                PersistenceInitializer.loadDVHConf(moduleDir);
                PersistenceInitializer.loadPersonality(moduleName, moduleDir);
                try {
                    ConfPopulator.populate(moduleDir, moduleName);
                    final long end = System.currentTimeMillis();
                    ConcurrentStartupUtil.LOGGER.log(Level.INFO, "Concurrent Conf population Module Name :[ " + moduleName + " ] Duration :[ " + (end - start) + " ]");
                }
                catch (final Exception e) {
                    e.printStackTrace();
                    throw e;
                }
                ModuleStartStopProcessorUtil.execute_postStartProcesses(moduleName);
            }
            catch (final Exception e2) {
                final Exception newException = new Exception("Exception while Populating Module " + moduleName);
                newException.initCause(e2);
                ConcurrentStartupUtil.dataPopulationThreadPool.shutdownNow();
                throw newException;
            }
        }
    }
}
