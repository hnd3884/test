package com.adventnet.db.migration.handler;

import java.util.Hashtable;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.net.URL;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.parser.DataDictionaryParser;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Comparator;
import java.util.TreeSet;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.migration.report.DBMigrationStatusUpdater;
import java.util.Arrays;
import com.adventnet.db.migration.test.SanityTestHandler;
import com.adventnet.persistence.util.DCManager;
import java.util.Iterator;
import java.util.Locale;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import com.adventnet.db.migration.notifier.ProgressNotifier;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.logging.Logger;

public class DBMigrationHandlerFactory
{
    private static final Logger LOGGER;
    private static final List<HandlerResponsibility> HANDLER_CHAIN;
    private static final List<HandlerResponsibility> NONMICKEY_HANDLER_CHAIN;
    private static final List<DBMigrationPrePostHandler> PRE_POST_HANDLERS;
    private static final Set<String> TABLE_SKIP_LIST;
    private static final Set<String> IGNORE_TABLEDATA_LIST;
    private static final Map<String, Integer> TABLENAME_VS_BATCHSIZE;
    private static int workerPoolSize;
    private static ProgressNotifier notifier;
    private static final Properties PROP;
    private static boolean createDB;
    private SanityTestConf sanityTestConf;
    private boolean ignoreNonMickeyTables;
    private NonMickeyTablesMigrationHandler defaultNonMickeyHandler;
    private static final Map<String, DCMigrationHandler> DCTYPEVSMIGRATIONHANDLER;
    private static Set<String> tableNames;
    
    public static void reinitialize() {
        DBMigrationHandlerFactory.HANDLER_CHAIN.clear();
        DBMigrationHandlerFactory.NONMICKEY_HANDLER_CHAIN.clear();
        DBMigrationHandlerFactory.PRE_POST_HANDLERS.clear();
        DBMigrationHandlerFactory.TABLE_SKIP_LIST.clear();
        DBMigrationHandlerFactory.IGNORE_TABLEDATA_LIST.clear();
        DBMigrationHandlerFactory.TABLENAME_VS_BATCHSIZE.clear();
        DBMigrationHandlerFactory.workerPoolSize = 10;
        DBMigrationHandlerFactory.notifier = null;
        DBMigrationHandlerFactory.PROP.clear();
        DBMigrationHandlerFactory.createDB = true;
    }
    
    private void overrideWithExtendedProperties() throws FileNotFoundException, IOException {
        FileInputStream fis = null;
        final String extendedPropertyFiles = DBMigrationHandlerFactory.PROP.getProperty("include_if_exists");
        if (extendedPropertyFiles != null) {
            final String path = System.getProperty("server.home") + File.separator + "conf" + File.separator;
            for (final String extendedProperty : extendedPropertyFiles.split(",")) {
                final File extendedPropertyFile = new File(path + extendedProperty.trim());
                if (extendedPropertyFile.exists()) {
                    try {
                        fis = new FileInputStream(extendedPropertyFile);
                        DBMigrationHandlerFactory.PROP.load(fis);
                    }
                    finally {
                        if (fis != null) {
                            fis.close();
                        }
                    }
                }
            }
        }
    }
    
    public DBMigrationHandlerFactory() throws Exception {
        this.ignoreNonMickeyTables = false;
        this.defaultNonMickeyHandler = null;
        reinitialize();
        this.getTableAllNamesDefinedInDDFiles();
        final String path = System.getProperty("server.home") + File.separator + "conf" + File.separator;
        final File migrationConf = new File(path + "db_migration.conf");
        boolean isAcSQLConfigured = false;
        if (!migrationConf.exists()) {
            DBMigrationHandlerFactory.LOGGER.info("[" + migrationConf.getAbsolutePath() + "] file not found...");
        }
        else {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(migrationConf);
                DBMigrationHandlerFactory.PROP.load(fis);
            }
            finally {
                if (fis != null) {
                    fis.close();
                }
            }
            this.overrideWithExtendedProperties();
            DBMigrationHandlerFactory.LOGGER.info("loaded props:: " + DBMigrationHandlerFactory.PROP);
            final String handlers = DBMigrationHandlerFactory.PROP.getProperty("handlers");
            if (handlers != null) {
                final List<HandlerResponsibility> constChain = new ArrayList<HandlerResponsibility>();
                final List<HandlerResponsibility> tableChain = new ArrayList<HandlerResponsibility>();
                final List<HandlerResponsibility> moduleChain = new ArrayList<HandlerResponsibility>();
                final List<HandlerResponsibility> rowChain = new ArrayList<HandlerResponsibility>();
                for (String handlerName : handlers.split(",")) {
                    handlerName = handlerName.trim();
                    final String handlerClassName = DBMigrationHandlerFactory.PROP.getProperty(handlerName + ".class");
                    if (handlerClassName == null) {
                        throw new IllegalArgumentException("Handler name [" + handlerName + "] specified without class name");
                    }
                    final String tableNames = DBMigrationHandlerFactory.PROP.getProperty(handlerName + ".table");
                    if (tableNames != null) {
                        final DBMigrationHandler handler = (DBMigrationHandler)Class.forName(handlerClassName.trim()).newInstance();
                        handler.setHandlerName(handlerName);
                        final HandlerResponsibility chainNode = new HandlerResponsibility(handler, HandlerLevel.TABLE);
                        for (final String tableName : tableNames.split(",")) {
                            chainNode.addResponsibility(tableName.trim());
                        }
                        DBMigrationHandlerFactory.LOGGER.info(chainNode.toString());
                        tableChain.add(chainNode);
                    }
                    final String moduleNames = DBMigrationHandlerFactory.PROP.getProperty(handlerName + ".module");
                    if (moduleNames != null) {
                        final DBMigrationHandler handler2 = (DBMigrationHandler)Class.forName(handlerClassName.trim()).newInstance();
                        handler2.setHandlerName(handlerName);
                        final HandlerResponsibility chainNode2 = new HandlerResponsibility(handler2, HandlerLevel.MODULE);
                        for (final String moduleName : moduleNames.split(",")) {
                            chainNode2.addResponsibility(moduleName.trim());
                        }
                        DBMigrationHandlerFactory.LOGGER.info(chainNode2.toString());
                        moduleChain.add(chainNode2);
                    }
                    final String rowTables = DBMigrationHandlerFactory.PROP.getProperty(handlerName + ".row");
                    if (rowTables != null) {
                        final DBMigrationHandler handler3 = (DBMigrationHandler)Class.forName(handlerClassName.trim()).newInstance();
                        handler3.setHandlerName(handlerName);
                        final HandlerResponsibility chainNode3 = new HandlerResponsibility(handler3, HandlerLevel.ROW);
                        for (final String tableName2 : rowTables.split(",")) {
                            if (tableName2.equals("ACSQLString")) {
                                isAcSQLConfigured = true;
                            }
                            chainNode3.addResponsibility(tableName2.trim());
                        }
                        DBMigrationHandlerFactory.LOGGER.info(chainNode3.toString());
                        rowChain.add(chainNode3);
                    }
                }
                DBMigrationHandlerFactory.HANDLER_CHAIN.addAll(constChain);
                DBMigrationHandlerFactory.HANDLER_CHAIN.addAll(tableChain);
                DBMigrationHandlerFactory.HANDLER_CHAIN.addAll(moduleChain);
                DBMigrationHandlerFactory.HANDLER_CHAIN.addAll(rowChain);
            }
            this.ignoreNonMickeyTables = Boolean.valueOf(DBMigrationHandlerFactory.PROP.getProperty("nonmickey.table.ignore.all", "false"));
            DBMigrationHandlerFactory.LOGGER.warning("nonmickey.table.ignore.all :: " + this.ignoreNonMickeyTables);
            if (!this.ignoreNonMickeyTables) {
                final String nonMickeyHandlers = DBMigrationHandlerFactory.PROP.getProperty("nonmickey.handlers");
                if (nonMickeyHandlers != null) {
                    for (String handlerName2 : nonMickeyHandlers.split(",")) {
                        handlerName2 = handlerName2.trim();
                        final String handlerClassName2 = DBMigrationHandlerFactory.PROP.getProperty(handlerName2 + ".class");
                        if (handlerClassName2 == null) {
                            throw new IllegalArgumentException("Handler name [" + handlerName2 + "] specified without class name");
                        }
                        final String tableNames2 = DBMigrationHandlerFactory.PROP.getProperty(handlerName2 + ".table");
                        if (tableNames2 != null) {
                            DBMigrationHandlerFactory.LOGGER.log(Level.INFO, "Initializing non-mickey table handler class :: {0}", handlerClassName2);
                            final NonMickeyTablesMigrationHandler handler4 = (NonMickeyTablesMigrationHandler)Class.forName(handlerClassName2.trim()).newInstance();
                            handler4.setHandlerName(handlerName2);
                            handler4.initialize();
                            final HandlerResponsibility chainNode4 = new HandlerResponsibility(handler4, HandlerLevel.TABLE);
                            for (final String tableName3 : tableNames2.split(",")) {
                                chainNode4.addResponsibility(tableName3.trim());
                            }
                            DBMigrationHandlerFactory.LOGGER.info(chainNode4.toString());
                            DBMigrationHandlerFactory.NONMICKEY_HANDLER_CHAIN.add(chainNode4);
                        }
                        final String rowTables2 = DBMigrationHandlerFactory.PROP.getProperty(handlerName2 + ".row");
                        if (rowTables2 != null) {
                            DBMigrationHandlerFactory.LOGGER.log(Level.FINE, "Initializing non-mickey tableRow handler class :: {0}", handlerClassName2);
                            final NonMickeyTablesMigrationHandler handler5 = (NonMickeyTablesMigrationHandler)Class.forName(handlerClassName2.trim()).newInstance();
                            handler5.setHandlerName(handlerName2);
                            handler5.initialize();
                            final HandlerResponsibility chainNode5 = new HandlerResponsibility(handler5, HandlerLevel.ROW);
                            for (final String tableName4 : rowTables2.split(",")) {
                                chainNode5.addResponsibility(tableName4.trim());
                            }
                            DBMigrationHandlerFactory.LOGGER.info(chainNode5.toString());
                            DBMigrationHandlerFactory.NONMICKEY_HANDLER_CHAIN.add(chainNode5);
                        }
                    }
                }
            }
            final String ignoreList = DBMigrationHandlerFactory.PROP.getProperty("migrate.table.ignore");
            if (ignoreList != null) {
                for (final String tableName5 : ignoreList.split(",")) {
                    if (!DBMigrationHandlerFactory.TABLE_SKIP_LIST.contains(tableName5) && !tableName5.trim().isEmpty()) {
                        DBMigrationHandlerFactory.TABLE_SKIP_LIST.add(tableName5.trim());
                    }
                }
            }
            if (!DBMigrationHandlerFactory.TABLE_SKIP_LIST.isEmpty()) {
                final List<String> staticTables = new ArrayList<String>();
                for (final String tabName : DBMigrationHandlerFactory.TABLE_SKIP_LIST) {
                    if (!this.canSkippableTable(tabName)) {
                        staticTables.add(tabName);
                    }
                }
                if (!staticTables.isEmpty()) {
                    throw new IllegalArgumentException("Tables defined in the data-dictionary.xml/dd-files.xml cannot be skipped during DB migration. Following tables can't be skipped." + staticTables);
                }
            }
            DBMigrationHandlerFactory.LOGGER.info("tableSkipList ::: " + DBMigrationHandlerFactory.TABLE_SKIP_LIST);
            final String ignoreDataList = DBMigrationHandlerFactory.PROP.getProperty("migrate.table.ignore.data");
            if (ignoreDataList != null) {
                for (final String tableName6 : ignoreDataList.split(",")) {
                    if (!DBMigrationHandlerFactory.IGNORE_TABLEDATA_LIST.contains(tableName6) && !tableName6.trim().isEmpty()) {
                        DBMigrationHandlerFactory.IGNORE_TABLEDATA_LIST.add(tableName6.trim());
                    }
                }
            }
            DBMigrationHandlerFactory.IGNORE_TABLEDATA_LIST.add("DBCredentialsAudit");
            DBMigrationHandlerFactory.TABLENAME_VS_BATCHSIZE.put("default", (DBMigrationHandlerFactory.PROP.getProperty("table.default.batchsize") == null) ? 100 : Integer.parseInt(DBMigrationHandlerFactory.PROP.getProperty("table.default.batchsize")));
            for (final Object key : ((Hashtable<Object, V>)DBMigrationHandlerFactory.PROP).keySet()) {
                final String annotation = (String)key;
                if (annotation.matches("table\\..*\\.batchsize")) {
                    final String tabName2 = annotation.substring("table.".length(), annotation.indexOf(".batchsize"));
                    DBMigrationHandlerFactory.TABLENAME_VS_BATCHSIZE.put(tabName2.trim(), Integer.parseInt(DBMigrationHandlerFactory.PROP.getProperty(annotation)));
                }
            }
            DBMigrationHandlerFactory.LOGGER.info("tableNameVsBatchSize ::: " + DBMigrationHandlerFactory.TABLENAME_VS_BATCHSIZE);
            DBMigrationHandlerFactory.workerPoolSize = Integer.parseInt(DBMigrationHandlerFactory.PROP.getProperty("dbmigration.workerpool.size", "10"));
            final String dbSpecificWorkerPoolSize = DBMigrationHandlerFactory.PROP.getProperty("dbmigration." + DBMigrationUtil.getDestDBType().toString().toLowerCase(Locale.ENGLISH) + ".workerpool.size");
            if (dbSpecificWorkerPoolSize != null) {
                DBMigrationHandlerFactory.workerPoolSize = Integer.parseInt(dbSpecificWorkerPoolSize);
            }
            DBMigrationHandlerFactory.LOGGER.info("workerPoolSize ::: " + DBMigrationHandlerFactory.workerPoolSize);
            DBMigrationHandlerFactory.createDB = Boolean.valueOf(DBMigrationHandlerFactory.PROP.getProperty("create.dest.db", "true"));
        }
        DBMigrationHandlerFactory.TABLE_SKIP_LIST.add("DBMProcessStats");
        DBMigrationHandlerFactory.TABLE_SKIP_LIST.add("DBMStatus");
        if (!isAcSQLConfigured) {
            final DBMigrationHandler handler6 = (DBMigrationHandler)Class.forName("com.adventnet.db.migration.handler.ACSQLTableRowHandler").newInstance();
            handler6.setHandlerName("ACSQLStringTableRowHandler");
            final HandlerResponsibility chainNode6 = new HandlerResponsibility(handler6, HandlerLevel.ROW);
            chainNode6.addResponsibility("ACSQLString");
            DBMigrationHandlerFactory.HANDLER_CHAIN.add(chainNode6);
        }
        if (DBMigrationHandlerFactory.TABLENAME_VS_BATCHSIZE.get("default") == null) {
            DBMigrationHandlerFactory.TABLENAME_VS_BATCHSIZE.put("default", 100);
        }
        DBMigrationHandlerFactory.notifier = this.getNewProgressNotifier();
        final NonMickeyTablesMigrationHandler handler7 = new DefaultNonMickeyTablesHandler();
        handler7.setHandlerName("default");
        handler7.initialize();
        final HandlerResponsibility chainNode6 = new HandlerResponsibility(handler7, HandlerLevel.TABLE);
        chainNode6.addResponsibility(".*");
        DBMigrationHandlerFactory.LOGGER.info(chainNode6.toString());
        this.defaultNonMickeyHandler = handler7;
        this.loadSanityConfigurations();
        DBMigrationHandlerFactory.LOGGER.info("Adding Pre/Post handlers.");
        final String prePostHandlerClasses = DBMigrationHandlerFactory.PROP.getProperty("dbmigration.prepost.handlers");
        final List<String> handlerList = new ArrayList<String>();
        if (prePostHandlerClasses != null) {
            this.prePostHandlers(prePostHandlerClasses.split(","), handlerList, false);
        }
        this.prePostHandlers(new String[] { "com.adventnet.db.migration.handler.DBMServerCheckPreHandler" }, handlerList, true);
        this.initializeDCMHandlers();
    }
    
    protected void initializeDCMHandlers() {
        final List<String> dcTypes = DCManager.getDCTypes();
        for (int i = 0; i < dcTypes.size(); ++i) {
            final Properties p = DCManager.getProps(dcTypes.get(i) + "." + DBMigrationUtil.getDestDBType().toString().toLowerCase(Locale.ENGLISH));
            if (p != null && p.getProperty("dcmhandler") != null) {
                DCMigrationHandler dcmHandler = null;
                try {
                    dcmHandler = (DCMigrationHandler)Thread.currentThread().getContextClassLoader().loadClass(p.getProperty("dcmhandler")).newInstance();
                }
                catch (final ClassNotFoundException e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                    DBMigrationHandlerFactory.LOGGER.log(Level.SEVERE, "Error while trying to instantiate DCMHandler for dc type :: {0} with exception {1}", new Object[] { dcTypes.get(i), ex });
                }
                DBMigrationHandlerFactory.DCTYPEVSMIGRATIONHANDLER.put(dcTypes.get(i), dcmHandler);
            }
        }
    }
    
    private void prePostHandlers(final String[] classes, final List<String> existingClasses, final boolean append) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        for (String prePostClass : classes) {
            prePostClass = prePostClass.trim();
            if (!existingClasses.contains(prePostClass)) {
                existingClasses.add(prePostClass);
                if (append) {
                    getPrePostHandlers().add(0, (DBMigrationPrePostHandler)Class.forName(prePostClass).newInstance());
                }
                else {
                    getPrePostHandlers().add((DBMigrationPrePostHandler)Class.forName(prePostClass).newInstance());
                }
            }
        }
    }
    
    private void loadSanityConfigurations() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final String sanityHandlerClass = DBMigrationHandlerFactory.PROP.getProperty("sanity.test.handler.class");
        if (sanityHandlerClass != null && !sanityHandlerClass.trim().equals("")) {
            this.sanityTestConf = new SanityTestConf((SanityTestHandler)Class.forName(sanityHandlerClass.trim()).newInstance());
        }
        else {
            this.sanityTestConf = new SanityTestConf();
        }
        String confProp = DBMigrationHandlerFactory.PROP.getProperty("sanity.test.table");
        if (confProp != null) {
            for (final String tableName : confProp.split(",")) {
                if (!tableName.trim().isEmpty()) {
                    this.sanityTestConf.addTablePatterns(tableName.trim());
                }
            }
        }
        confProp = DBMigrationHandlerFactory.PROP.getProperty("sanity.test.table.exclude");
        if (confProp != null) {
            for (final String tableName : confProp.split(",")) {
                if (!tableName.trim().isEmpty()) {
                    this.sanityTestConf.addTableNameToExcludeList(tableName.trim());
                }
            }
        }
        confProp = DBMigrationHandlerFactory.PROP.getProperty("sanity.test.module");
        if (confProp != null) {
            for (final String moduleName : confProp.split(",")) {
                if (!moduleName.trim().equals("")) {
                    this.sanityTestConf.addModuleName(moduleName.trim());
                }
            }
        }
        confProp = DBMigrationHandlerFactory.PROP.getProperty("sanity.test.column.types");
        if (confProp != null) {
            for (final String type : confProp.split(",")) {
                if (!type.trim().equals("*")) {
                    this.sanityTestConf.addColumnTypes(type.trim());
                }
            }
        }
        for (final Object key : ((Hashtable<Object, V>)DBMigrationHandlerFactory.PROP).keySet()) {
            final String pattern = (String)key;
            if (pattern.matches("sanity\\.test\\..*\\.columns")) {
                final String tabName = pattern.substring("sanity.test.".length(), pattern.indexOf(".columns"));
                if (tabName.equals("<tablename>")) {
                    continue;
                }
                if (tabName.trim().equals("")) {
                    continue;
                }
                if (this.sanityTestConf.getTableColumnsTypes(tabName) != null && !this.sanityTestConf.getTableColumnsTypes(tabName).toString().trim().equals("")) {
                    throw new IllegalArgumentException("Tablename/Pattern [" + tabName + "] specified in both \"sanity.test.<tablename>.columns.type\" and \"sanity.test.<tablename>.columns\".");
                }
                this.sanityTestConf.addTableColumns(tabName.trim(), Arrays.asList(DBMigrationHandlerFactory.PROP.getProperty(pattern).split(", ")));
            }
            else {
                if (!pattern.matches("sanity\\.test\\..*\\.column.types")) {
                    continue;
                }
                final String tabName = pattern.substring("sanity.test.".length(), pattern.indexOf(".column.type"));
                if (tabName.equals("<tablename>") || tabName.trim().equals("")) {
                    continue;
                }
                if (tabName.trim().equals("*")) {
                    continue;
                }
                if (this.sanityTestConf.getTableColumns(tabName) != null && !this.sanityTestConf.getTableColumns(tabName).toString().trim().equals("")) {
                    throw new IllegalArgumentException("Tablename/Pattern [" + tabName + "] specified in both \"sanity.test.<tablename>.columns.type\" and \"sanity.test.<tablename>.columns\".");
                }
                this.sanityTestConf.addTableColumnTypes(tabName.trim(), Arrays.asList(DBMigrationHandlerFactory.PROP.getProperty(pattern).split(", ")));
            }
        }
        DBMigrationHandlerFactory.LOGGER.info("SanityTestConf ::: " + this.sanityTestConf);
    }
    
    public boolean processNonMickeyTables() {
        return !this.ignoreNonMickeyTables;
    }
    
    public SanityTestConf getSanityTestConf() {
        return this.sanityTestConf;
    }
    
    public int getBatchSizeForTable(final String tableName) {
        final Integer batchSize = DBMigrationHandlerFactory.TABLENAME_VS_BATCHSIZE.get(tableName);
        return (batchSize != null) ? batchSize : ((int)DBMigrationHandlerFactory.TABLENAME_VS_BATCHSIZE.get("default"));
    }
    
    public int getWorkerPoolSize() {
        return DBMigrationHandlerFactory.workerPoolSize;
    }
    
    public String getConfiguration(final String configurationName) {
        return DBMigrationHandlerFactory.PROP.getProperty(configurationName);
    }
    
    public String getConfiguration(final String configurationName, final String defaultValue) {
        final String retVal = this.getConfiguration(configurationName);
        return (retVal != null) ? retVal : defaultValue;
    }
    
    public boolean getConfigurationAsBoolean(final String configurationName) {
        final String property = DBMigrationHandlerFactory.PROP.getProperty(configurationName);
        return (property == null) ? Boolean.FALSE : Boolean.valueOf(property);
    }
    
    public ProgressNotifier getProgressNotifier() {
        return DBMigrationHandlerFactory.notifier;
    }
    
    public ProgressNotifier getNewProgressNotifier() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final String notifierClassName = this.getConfiguration("dbmigration.notifier");
        return (ProgressNotifier)Class.forName((notifierClassName != null) ? notifierClassName.trim() : "com.adventnet.db.migration.notifier.ConsoleProgressNotifier").newInstance();
    }
    
    public boolean createDestDB() {
        return DBMigrationHandlerFactory.createDB;
    }
    
    public DBMigrationHandler getMigrationHandler(final String moduleName, final String tableName, final HandlerLevel level) {
        for (final HandlerResponsibility handlerResponsibility : DBMigrationHandlerFactory.HANDLER_CHAIN) {
            if (handlerResponsibility.getHandlerInfoType().ordinal() >= level.ordinal()) {
                switch (handlerResponsibility.getHandlerInfoType()) {
                    case TABLE: {
                        if (handlerResponsibility.contains(tableName)) {
                            return handlerResponsibility.getMickeyHandler();
                        }
                        continue;
                    }
                    case MODULE: {
                        if (handlerResponsibility.contains(moduleName)) {
                            return handlerResponsibility.getMickeyHandler();
                        }
                        continue;
                    }
                    case ROW: {
                        if (handlerResponsibility.contains(tableName)) {
                            return handlerResponsibility.getMickeyHandler();
                        }
                        continue;
                    }
                }
            }
        }
        return null;
    }
    
    public NonMickeyTablesMigrationHandler getNonMickeyMigrationHandler(final String tableName, final HandlerLevel level) {
        for (final HandlerResponsibility handlerResponsibility : DBMigrationHandlerFactory.NONMICKEY_HANDLER_CHAIN) {
            if (handlerResponsibility.getHandlerInfoType() == level && handlerResponsibility.contains(tableName)) {
                return handlerResponsibility.getNonMickeyHandler();
            }
        }
        return this.defaultNonMickeyHandler;
    }
    
    public Set<String> getSkippedTableList() {
        return DBMigrationHandlerFactory.TABLE_SKIP_LIST;
    }
    
    public boolean ignoreTableData(final String tableName) {
        return DBMigrationHandlerFactory.IGNORE_TABLEDATA_LIST.contains(tableName);
    }
    
    public void addToSkipList(final String tableName) {
        DBMigrationHandlerFactory.TABLE_SKIP_LIST.add(tableName);
    }
    
    public void addToSkipList(final List<String> tableNames) throws QueryConstructionException, SQLException {
        DBMigrationHandlerFactory.TABLE_SKIP_LIST.addAll(tableNames);
        DBMigrationStatusUpdater.skipTableCreation(tableNames);
    }
    
    private Set<String> getTableAllNamesDefinedInDDFiles() throws Exception {
        if (DBMigrationHandlerFactory.tableNames == null) {
            DBMigrationHandlerFactory.tableNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            try {
                for (final Object moduleName : MetaDataUtil.getAllModuleNames()) {
                    DBMigrationHandlerFactory.LOGGER.fine("Processing DD files in module ::: " + moduleName);
                    final String serverHome = System.getProperty("server.home");
                    DataDictionary dd = null;
                    if (moduleName.equals("Persistence")) {
                        final URL url = DataDictionaryParser.class.getResource("/com/adventnet/db/persistence/metadata/conf/data-dictionary.xml");
                        dd = DataDictionaryParser.getDataDictionary(url);
                    }
                    else if (moduleName.equals("MetaPersistence")) {
                        final URL url = DataDictionaryParser.class.getResource("/com/adventnet/db/persistence/metadata/conf/meta-dd.xml");
                        dd = DataDictionaryParser.getDataDictionary(url);
                    }
                    else {
                        final String modulePath = serverHome + File.separator + "conf" + File.separator + (String)moduleName + File.separator;
                        File ddFile = new File(modulePath + "dd-files.xml");
                        if (!ddFile.exists()) {
                            ddFile = new File(modulePath + "data-dictionary.xml");
                            if (!ddFile.exists()) {
                                DBMigrationHandlerFactory.LOGGER.fine("DD file not exists in module " + moduleName);
                                continue;
                            }
                        }
                        DBMigrationHandlerFactory.LOGGER.info("dd url:: " + ddFile.getAbsolutePath());
                        dd = DataDictionaryParser.getDataDictionary(ddFile.toURL());
                    }
                    for (final Object td : dd.getTableDefinitions()) {
                        DBMigrationHandlerFactory.tableNames.add(((TableDefinition)td).getTableName());
                    }
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return DBMigrationHandlerFactory.tableNames;
    }
    
    public boolean canSkippableTable(final String tableName) {
        return !DBMigrationHandlerFactory.tableNames.contains(tableName);
    }
    
    public static List<DBMigrationPrePostHandler> getPrePostHandlers() {
        return DBMigrationHandlerFactory.PRE_POST_HANDLERS;
    }
    
    public static Map<String, DCMigrationHandler> getDCMHandlers() {
        return DBMigrationHandlerFactory.DCTYPEVSMIGRATIONHANDLER;
    }
    
    static {
        LOGGER = Logger.getLogger(DBMigrationHandlerFactory.class.getName());
        HANDLER_CHAIN = new ArrayList<HandlerResponsibility>();
        NONMICKEY_HANDLER_CHAIN = new ArrayList<HandlerResponsibility>();
        PRE_POST_HANDLERS = new ArrayList<DBMigrationPrePostHandler>();
        TABLE_SKIP_LIST = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        IGNORE_TABLEDATA_LIST = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        TABLENAME_VS_BATCHSIZE = new HashMap<String, Integer>();
        DBMigrationHandlerFactory.workerPoolSize = 10;
        DBMigrationHandlerFactory.notifier = null;
        PROP = new Properties();
        DBMigrationHandlerFactory.createDB = true;
        DCTYPEVSMIGRATIONHANDLER = new HashMap<String, DCMigrationHandler>();
        DBMigrationHandlerFactory.tableNames = null;
    }
    
    public enum HandlerLevel
    {
        TABLE, 
        MODULE, 
        ROW;
    }
    
    public enum SanityTestLevel
    {
        TABLE, 
        MODULE;
    }
    
    protected class HandlerResponsibility
    {
        private DBMigrationHandler handler;
        private NonMickeyTablesMigrationHandler nonMickeyHandler;
        private Set<String> responsibilityIndex;
        private HandlerLevel handlerInfoType;
        
        public HandlerResponsibility(final DBMigrationHandler handler, final HandlerLevel handlerInfoType) {
            this.handler = null;
            this.nonMickeyHandler = null;
            this.responsibilityIndex = new TreeSet<String>();
            this.handlerInfoType = null;
            this.handler = handler;
            this.handlerInfoType = handlerInfoType;
        }
        
        public HandlerResponsibility(final NonMickeyTablesMigrationHandler handler, final HandlerLevel handlerInfoType) {
            this.handler = null;
            this.nonMickeyHandler = null;
            this.responsibilityIndex = new TreeSet<String>();
            this.handlerInfoType = null;
            this.nonMickeyHandler = handler;
            this.handlerInfoType = handlerInfoType;
        }
        
        public void addResponsibility(final String responsibility) {
            this.responsibilityIndex.add(responsibility);
        }
        
        public boolean contains(final String keyPattern) {
            boolean contains = this.responsibilityIndex.contains(keyPattern);
            if (!contains) {
                for (final String key : this.responsibilityIndex) {
                    final Pattern pattern = Pattern.compile(key, 2);
                    if (pattern.matcher(keyPattern).matches()) {
                        contains = true;
                        break;
                    }
                }
            }
            return contains;
        }
        
        public DBMigrationHandler getMickeyHandler() {
            return this.handler;
        }
        
        public NonMickeyTablesMigrationHandler getNonMickeyHandler() {
            return this.nonMickeyHandler;
        }
        
        public HandlerLevel getHandlerInfoType() {
            return this.handlerInfoType;
        }
        
        @Override
        public String toString() {
            final String nl = System.getProperty("line.separator");
            final StringBuilder strBuff = new StringBuilder();
            strBuff.append("Handler Name\t:: ").append((this.handler != null) ? this.handler.getHandlerName() : this.nonMickeyHandler.getHandlerName()).append(nl);
            strBuff.append("Handler for\t:: ").append(this.handlerInfoType).append(nl);
            strBuff.append("Handler property\t:: ").append(this.responsibilityIndex).append(nl);
            return strBuff.toString();
        }
    }
    
    public class SanityTestConf
    {
        private List<String> moduleNames;
        private List<String> tablePatterns;
        private Map<String, List<String>> tablenameVsColumns;
        private Map<String, List<String>> tablenameVsColumnsTypes;
        private List<String> columnTypes;
        private SanityTestHandler sanityTestHandler;
        private List<String> excludeTables;
        
        @Override
        public String toString() {
            final String nl = System.getProperty("line.separator");
            final StringBuilder buff = new StringBuilder();
            buff.append("Module names ::: ").append(this.moduleNames).append(nl);
            buff.append("Table names  ::: ").append(this.tablePatterns).append(nl);
            buff.append("tablenameVsColumnsTypes ::: ").append(this.tablenameVsColumnsTypes).append(nl);
            buff.append("tablenameVsColumns \t:: ").append(this.tablenameVsColumns).append(nl);
            buff.append("columnTypes   ::: ").append(this.columnTypes).append(nl);
            buff.append("excludeTables  ::: ").append(this.excludeTables).append(nl);
            return buff.toString();
        }
        
        public SanityTestConf() {
            this.moduleNames = new ArrayList<String>();
            this.tablePatterns = new ArrayList<String>();
            this.tablenameVsColumns = new HashMap<String, List<String>>();
            this.tablenameVsColumnsTypes = new HashMap<String, List<String>>();
            this.columnTypes = new ArrayList<String>();
            this.excludeTables = new ArrayList<String>();
            this.moduleNames.add("Persistence");
            this.moduleNames.add("MetaPersistence");
        }
        
        public SanityTestConf(final SanityTestHandler handler) {
            this.moduleNames = new ArrayList<String>();
            this.tablePatterns = new ArrayList<String>();
            this.tablenameVsColumns = new HashMap<String, List<String>>();
            this.tablenameVsColumnsTypes = new HashMap<String, List<String>>();
            this.columnTypes = new ArrayList<String>();
            this.excludeTables = new ArrayList<String>();
            this.sanityTestHandler = handler;
        }
        
        public void invokeHandler(final SelectQuery sQuery) {
            if (this.sanityTestHandler != null) {
                this.sanityTestHandler.preInvokeForSelectSQL(sQuery);
            }
        }
        
        public boolean isDiffIgnorable(final String tableName, final JSONObject diffObj) {
            return this.sanityTestHandler != null && this.sanityTestHandler.isDiffIgnorable(tableName, diffObj);
        }
        
        public List<String> getModuleNames() {
            return this.moduleNames;
        }
        
        public void addModuleName(final String moduleName) {
            this.moduleNames.add(moduleName);
        }
        
        public List<String> getTablePatterns() {
            return this.tablePatterns;
        }
        
        public List<String> getExcludeTablePatterns() {
            return this.excludeTables;
        }
        
        public void addTablePatterns(final String tablePattern) {
            this.tablePatterns.add(tablePattern);
        }
        
        public void addTableNameToExcludeList(final String tablePattern) {
            this.excludeTables.add(tablePattern);
        }
        
        public List<String> getTableColumns(final String tableName) {
            List<String> list = this.tablenameVsColumns.get(tableName);
            if (list == null) {
                for (final String key : this.tablenameVsColumns.keySet()) {
                    if (tableName.matches(key)) {
                        list = this.tablenameVsColumns.get(tableName);
                        break;
                    }
                }
            }
            return list;
        }
        
        public void addTableColumns(final String tableName, final List<String> tableColumns) {
            if (!tableColumns.isEmpty()) {
                this.tablenameVsColumns.put(tableName, tableColumns);
            }
        }
        
        public List<String> getTableColumnsTypes(final String tableName) {
            List<String> list = this.tablenameVsColumnsTypes.get(tableName);
            if (list == null) {
                for (final String key : this.tablenameVsColumnsTypes.keySet()) {
                    if (tableName.matches(key)) {
                        list = this.tablenameVsColumnsTypes.get(tableName);
                        break;
                    }
                }
            }
            return (list != null) ? list : this.getColumnTypes();
        }
        
        public void addTableColumnTypes(final String tableName, final List<String> tableColumnTypes) {
            if (!tableColumnTypes.isEmpty()) {
                this.tablenameVsColumnsTypes.put(tableName, tableColumnTypes);
            }
        }
        
        public List<String> getColumnTypes() {
            return this.columnTypes;
        }
        
        protected void addColumnTypes(final String columnType) {
            if ((columnType.equals("*") && !this.columnTypes.isEmpty()) || this.columnTypes.contains("*")) {
                throw new IllegalArgumentException("Value of the property \"sanity.test.column.types\" should be either '*' or comma seperated columnNames.");
            }
            this.columnTypes.add(columnType);
        }
    }
}
