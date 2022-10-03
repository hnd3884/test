package com.adventnet.db.migration.test;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Callable;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.DataDictionary;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import com.adventnet.db.migration.notifier.ProgressNotifier;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.Collection;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.HashMap;
import com.adventnet.db.migration.handler.DBMigrationHandlerFactory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import java.util.Map;

public class SanityTestRunner
{
    Map<String, String> diffMap;
    private static final Logger LOGGER;
    ExecutorService runnerPool;
    private List<String> archiveTables;
    
    public SanityTestRunner(final DBMigrationHandlerFactory.SanityTestConf sanityTestConf) throws Exception {
        this.diffMap = new HashMap<String, String>();
        this.runnerPool = Executors.newFixedThreadPool(10);
        this.archiveTables = new ArrayList<String>();
        try {
            final List<String> moduleNames = sanityTestConf.getModuleNames();
            for (final String moduleName : moduleNames) {
                final Map<String, String> runSanityTestForModule = this.runSanityTestForModule(moduleName, sanityTestConf.getExcludeTablePatterns());
                this.diffMap.putAll(runSanityTestForModule);
            }
            final List<String> tablePatterns = sanityTestConf.getTablePatterns();
            final List<TableDefinition> tableDefinitions = new ArrayList<TableDefinition>();
            for (final String tabName : tablePatterns) {
                final TableDefinition tableDefinitionByName = MetaDataUtil.getTableDefinitionByName(tabName);
                if (tableDefinitionByName != null) {
                    tableDefinitions.add(tableDefinitionByName);
                }
                else {
                    final List<TableDefinition> tabDefs = MetaDataUtil.getTableDefinitions();
                    for (final TableDefinition tableDefinition : tabDefs) {
                        if (!moduleNames.contains(tableDefinition.getModuleName()) && tableDefinition.getTableName().matches(tabName)) {
                            tableDefinitions.add(tableDefinition);
                        }
                    }
                }
            }
            ProgressNotifier newProgressNotifier = DBMigrationUtil.getHandlerFactory().getNewProgressNotifier();
            newProgressNotifier.printMessage("Running sanity test for defined tables...");
            final Map<String, String> tablesDiff = this.runSanityTestForModule(tableDefinitions, newProgressNotifier);
            this.diffMap.putAll(tablesDiff);
            newProgressNotifier = DBMigrationUtil.getHandlerFactory().getNewProgressNotifier();
            newProgressNotifier.printMessage("Running sanity test for Non Mickey tables...");
            final List<String> nonMickeyTables = new ArrayList<String>();
            final List<String> nonMickeyTableAndArchiveTableList = new ArrayList<String>(DBMigrationUtil.getNonMickeyTables());
            nonMickeyTableAndArchiveTableList.addAll(this.archiveTables = DBMigrationUtil.getArchiveTables());
            Map<String, String> nonMickeyTablesDiff = new HashMap<String, String>();
            for (final String tabName2 : tablePatterns) {
                if (nonMickeyTableAndArchiveTableList.contains(tabName2)) {
                    nonMickeyTables.add(tabName2);
                }
                else {
                    for (final String nonMickeyTabName : nonMickeyTableAndArchiveTableList) {
                        final Pattern pattern = Pattern.compile(tabName2, 2);
                        if (pattern.matcher(nonMickeyTabName).matches()) {
                            nonMickeyTables.add(nonMickeyTabName);
                        }
                    }
                }
            }
            nonMickeyTablesDiff = this.runSanityTestForNonMickeyTables(nonMickeyTables, newProgressNotifier, sanityTestConf.getExcludeTablePatterns());
            this.diffMap.putAll(nonMickeyTablesDiff);
            printDiff(this.diffMap, newProgressNotifier);
            newProgressNotifier.printMessage("\nSanity test status :: " + (this.diffMap.isEmpty() ? "PASSED\n" : "FAILED\n"));
        }
        catch (final Exception e) {
            e.printStackTrace();
            final ProgressNotifier newProgressNotifier2 = DBMigrationUtil.getHandlerFactory().getNewProgressNotifier();
            newProgressNotifier2.printMessage("\nSanity test status :: FAILED\n");
            newProgressNotifier2.printMessage("Exception occured while running Sanity test.");
            throw e;
        }
    }
    
    public Map<String, String> runSanityTestForNonMickeyTables(final List<String> nonMickeyTableNames, final ProgressNotifier notifier, final List<String> excludeTablePatterns) throws Exception {
        final Map<String, String> diffMap = new ConcurrentHashMap<String, String>();
        notifier.initialize(new ArrayList<String>(), nonMickeyTableNames.size());
        for (final String tabName : nonMickeyTableNames) {
            if (!this.isTableToBeExcluded(tabName, excludeTablePatterns)) {
                this.runSanityTestForTable(tabName, notifier, diffMap);
            }
            else {
                notifier.startedProcessingTable(tabName);
                SanityTestRunner.LOGGER.warning("Table [" + tabName + "] is skipped by configuration, hence skipping sanity test.");
                notifier.completedProcessingTable(tabName);
            }
        }
        this.waitForTaskCompletion();
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)diffMap);
    }
    
    public Map<String, String> getSanityDiff() {
        return this.diffMap;
    }
    
    public Map<String, String> runSanityTestForModule(final String moduleName, final List<String> excludeTablePatterns) throws Exception {
        final ProgressNotifier notifier = DBMigrationUtil.getHandlerFactory().getNewProgressNotifier();
        notifier.printMessage("Running sanity test for module " + moduleName);
        final DataDictionary dataDictionary = MetaDataUtil.getDataDictionary(moduleName);
        if (dataDictionary == null) {
            SanityTestRunner.LOGGER.warning("data-dictionary is not found for the module " + moduleName + ". Hence skipped.");
            return new ConcurrentHashMap<String, String>();
        }
        if (excludeTablePatterns.isEmpty()) {
            return this.runSanityTestForModule(dataDictionary.getTableDefinitions(), notifier);
        }
        final List<TableDefinition> tablesToBeTested = new ArrayList<TableDefinition>();
        final List<TableDefinition> tabDefs = dataDictionary.getTableDefinitions();
        for (final TableDefinition tabDef : tabDefs) {
            if (!this.isTableToBeExcluded(tabDef.getTableName(), excludeTablePatterns)) {
                tablesToBeTested.add(tabDef);
            }
        }
        return this.runSanityTestForModule(tablesToBeTested, notifier);
    }
    
    private boolean isTableToBeExcluded(final String tableName, final List<String> excludeTablePatterns) {
        if (excludeTablePatterns.contains(tableName)) {
            return true;
        }
        for (final String pattern : excludeTablePatterns) {
            if (tableName.matches(pattern)) {
                return true;
            }
        }
        return false;
    }
    
    public Map<String, String> runSanityTestForModule(final List<TableDefinition> tableDefinitions, final ProgressNotifier notifier) throws Exception {
        final Map<String, String> moduleDiffMap = new ConcurrentHashMap<String, String>();
        notifier.initialize(new ArrayList<String>(), tableDefinitions.size());
        for (final TableDefinition tableDefinition : tableDefinitions) {
            if (!DBMigrationUtil.getHandlerFactory().getSkippedTableList().contains(tableDefinition.getTableName()) && tableDefinition.creatable() && !tableDefinition.isTemplate()) {
                this.runSanityTestForTable(tableDefinition.getTableName(), notifier, moduleDiffMap);
            }
            else {
                notifier.startedProcessingTable(tableDefinition.getTableName());
                SanityTestRunner.LOGGER.warning("Table [" + tableDefinition.getTableName() + "] is skipped by configuration, hence skipping sanity test.");
                notifier.completedProcessingTable(tableDefinition.getTableName());
            }
        }
        this.waitForTaskCompletion();
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)moduleDiffMap);
    }
    
    public void runSanityTestForTable(final String tableName, final ProgressNotifier notifier, final Map<String, String> diffMap) throws MetaDataException {
        this.submitTaskInWorkerPool(new SanityTestTask(tableName, notifier, diffMap, this.runnerPool, this.archiveTables.contains(tableName)));
    }
    
    protected void submitTaskInWorkerPool(final SanityTestTask task) {
        this.runnerPool.submit((Callable<Object>)task);
    }
    
    public void waitForTaskCompletion() throws InterruptedException {
        SanityTestRunner.LOGGER.info("Waiting for Task completion");
        while (((ThreadPoolExecutor)this.runnerPool).getActiveCount() != 0) {
            if (this.runnerPool.isShutdown()) {
                throw new InterruptedException("Runner pool execution terminated forcibly...");
            }
            SanityTestRunner.LOGGER.info("Total no of active workers ::: " + ((ThreadPoolExecutor)this.runnerPool).getActiveCount());
            Thread.sleep(1000L);
        }
        if (this.runnerPool.isShutdown()) {
            throw new InterruptedException("Runner pool execution terminated forcibly...");
        }
    }
    
    public void shutdownWorkerPool(final boolean forcibly) throws Exception {
        if (!forcibly) {
            this.runnerPool.shutdown();
            this.waitForWorkerPoolShutdown();
        }
        else {
            this.runnerPool.shutdownNow();
        }
    }
    
    protected void waitForWorkerPoolShutdown() throws InterruptedException {
        while (!this.runnerPool.isTerminated()) {
            Thread.sleep(1000L);
        }
    }
    
    private static void printDiff(final Map<String, String> diffMap, final ProgressNotifier notifier) {
        if (!diffMap.isEmpty()) {
            notifier.printMessage("Please refer logs for sanity test diff report.");
            SanityTestRunner.LOGGER.severe("Sanity test diff");
            for (final String key : diffMap.keySet()) {
                SanityTestRunner.LOGGER.info(key + " :: " + diffMap.get(key));
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(SanityTestRunner.class.getName());
    }
}
