package com.adventnet.db.migration.util;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import com.adventnet.persistence.PersistenceException;
import com.zoho.mickey.exception.PasswordException;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.adapter.mssql.MssqlDBAdapter;
import com.adventnet.db.adapter.WrappedDBAdapter;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.cp.WrappedDataSource;
import com.adventnet.ds.DataSourceManager;
import java.util.Iterator;
import java.util.Locale;
import com.adventnet.persistence.util.DCManager;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import java.sql.SQLException;
import java.sql.Connection;
import com.adventnet.db.migration.report.DBMigrationStatusUpdater;
import java.util.Map;
import java.util.Properties;
import java.util.List;
import com.adventnet.db.migration.test.SanityTestRunner;
import com.adventnet.db.migration.handler.DBMigrationHandlerFactory;
import com.adventnet.db.migration.adapter.DBMAdapter;
import java.util.logging.Logger;
import com.adventnet.db.adapter.DBAdapter;
import javax.sql.DataSource;

public class DBMigrationUtil
{
    private static DBType destDBType;
    private static DBType srcDBType;
    private static DataSource srcDataSource;
    private static DataSource destDataSource;
    private static DBAdapter srcDBAdapter;
    private static DBAdapter destDBAdapter;
    private static final Logger LOGGER;
    private static DBMAdapter dbmAdapter;
    private static boolean isMickeyInitialized;
    private static DBMigrationHandlerFactory handlerFactory;
    private static SanityTestRunner runner;
    private static boolean isDBMigrationRunning;
    private static List<String> nonMickeyTables;
    private static List<String> archiveTables;
    
    public static void addNonMickeyTable(final String tabName) {
        DBMigrationUtil.nonMickeyTables.add(tabName);
    }
    
    public static List<String> getNonMickeyTables() {
        return DBMigrationUtil.nonMickeyTables;
    }
    
    public static void addArchiveTable(final String tabName) {
        DBMigrationUtil.archiveTables.add(tabName);
    }
    
    public static List<String> getArchiveTables() {
        return DBMigrationUtil.archiveTables;
    }
    
    public static void migrateTables(final String destinationDB, final Properties destDBProperties) throws Throwable {
        System.setProperty("db.migration", "true");
        setDBMigrationRunning(true);
        final Properties destDBProps = new Properties();
        destDBProps.putAll(destDBProperties);
        final boolean isPreInitialized = DBMigrationUtil.isMickeyInitialized;
        DBMigrationUtil.LOGGER.warning("Preinitialized database server ::: " + isPreInitialized);
        try {
            initializeMickeyForMigration(destinationDB, destDBProps);
        }
        catch (final Exception e) {
            stopDataBase(destDBProps);
            final String message = e.getMessage();
            if (message != null && message.contains("database is already migrated one")) {
                return;
            }
            setDBMigrationRunning(false);
            throw e;
        }
        try {
            DBMigrationUtil.dbmAdapter.migrateDataBase(DBMigrationUtil.srcDataSource, getDestDataSource());
            runSanityTest();
            DBMigrationStatusUpdater.updateMetaDataForSkippedOperations(getDestConnection());
        }
        catch (final Throwable e2) {
            notifyMessage("\n\nException occured while migrating database. Refer logs for more details.");
            throw e2;
        }
        finally {
            DBMigrationUtil.LOGGER.info("Generating task execution summary.");
            notifyMessage("\nGenerating migration summary.");
            DBMigrationStatusUpdater.generateSummaryReport();
            if (!isPreInitialized) {
                stopDataBase(destDBProps);
            }
            else {
                DBMigrationUtil.LOGGER.warning("Preinitialized database server, hence ignoring stop database.");
            }
            setDBMigrationRunning(false);
        }
    }
    
    public static Connection getDestConnection() throws SQLException {
        try {
            return getDestDataSource().getConnection();
        }
        catch (final SQLException sqle) {
            notifyMessage("Exception occured while getting destination DB connction.");
            notifyMessage("Please make sure destination DB is reachable (or) respective JDBC driver present in lib directory\n");
            throw sqle;
        }
    }
    
    public static DBAdapter getDestDBAdapter() {
        return DBMigrationUtil.destDBAdapter;
    }
    
    public static Connection getSrcConnection() throws SQLException {
        try {
            return DBMigrationUtil.srcDataSource.getConnection();
        }
        catch (final SQLException sqle) {
            notifyMessage("Exception occured while getting SourceDB connction.");
            notifyMessage("Please make sure source DB is reachable\n");
            throw sqle;
        }
    }
    
    public static DataSource getSrcDataSource() {
        return DBMigrationUtil.srcDataSource;
    }
    
    public static DBAdapter getSrcDBAdapter() {
        return DBMigrationUtil.srcDBAdapter;
    }
    
    public static DBMAdapter getMigrationAdapter() {
        return DBMigrationUtil.dbmAdapter;
    }
    
    public static DBType getDestDBType() {
        return DBMigrationUtil.destDBType;
    }
    
    public static DBType getSrcDBType() {
        return DBMigrationUtil.srcDBType;
    }
    
    private static void validateDestinationDB(final String destDBName) {
        if (DBMigrationUtil.destDBType == DBType.OTHERS) {
            notifyMessage("Invalid destination database [" + destDBName + "] type specified, type name should be either mysql/postgres/mssql/firebird");
        }
    }
    
    private static DBType getDBType(final String dbName) {
        return DBType.MYSQL.equals(dbName) ? DBType.MYSQL : (DBType.MSSQL.equals(dbName) ? DBType.MSSQL : (DBType.POSTGRES.equals(dbName) ? DBType.POSTGRES : (DBType.FIREBIRD.equals(dbName) ? DBType.FIREBIRD : DBType.OTHERS)));
    }
    
    private static String getSourceDBType() {
        String sourceDB = PersistenceInitializer.getConfigurationValue("DSAdapter");
        if (sourceDB != null && sourceDB.equalsIgnoreCase("mds")) {
            DBMigrationUtil.LOGGER.log(Level.INFO, "mds ::: {0}", PersistenceInitializer.getConfigurationProps("DataSourcePlugIn"));
            sourceDB = PersistenceInitializer.getConfigurationProps("DataSourcePlugIn").getProperty("DefaultDSAdapter");
        }
        return sourceDB;
    }
    
    private static void validateDCMigrationProps() {
        List<String> dcTypes = DCManager.getDCTypes();
        for (final String dcType : dcTypes) {
            final Properties p = DCManager.getProps(dcType + "." + getDestDBType().toString().toLowerCase(Locale.ENGLISH));
            if (p == null) {
                throw new IllegalArgumentException("dynamic-column-types.props is not defined for the dctype " + dcType + "." + getDestDBType().toString().toLowerCase(Locale.ENGLISH));
            }
            if (p.getProperty("dcmhandler") == null) {
                throw new IllegalArgumentException("dcmhandler property for the dctype " + dcType + " is not defined for the dbtype " + getDestDBType().toString().toLowerCase(Locale.ENGLISH));
            }
        }
        dcTypes = DCManager.getDCTypes();
        for (final String dcType : dcTypes) {
            final Properties p = DCManager.getProps(dcType + "." + getSrcDBType().toString().toLowerCase(Locale.ENGLISH));
            if (p == null) {
                throw new IllegalArgumentException("dynamic-column-types.props is not defined for the dctype " + dcType + "." + getSrcDBType().toString().toLowerCase(Locale.ENGLISH));
            }
            if (p.getProperty("dcmhandler") == null) {
                throw new IllegalArgumentException("dcmhandler property for the dctype " + dcType + " is not defined for the dbtype " + getSrcDBType().toString().toLowerCase(Locale.ENGLISH));
            }
        }
    }
    
    public static void initializeMickeyForMigration(final String destinationDB, final Properties dbProps) throws Exception {
        setDBMigrationRunning(true);
        System.setProperty("gen.db.password", "false");
        if (DBMigrationUtil.isMickeyInitialized) {
            DBMigrationUtil.LOGGER.warning("Mickey server already initialized. Hence ignoring startup request.");
            DBMigrationUtil.dbmAdapter.initialize(DBMigrationUtil.handlerFactory);
            return;
        }
        DBMigrationUtil.destDBType = getDBType(destinationDB);
        validateDestinationDB(destinationDB);
        PersistenceInitializer.initializeDB(System.getProperty("server.conf"));
        final String sourceDB = getSourceDBType();
        DBMigrationUtil.LOGGER.log(Level.INFO, "sourceDB ::: {0}", sourceDB);
        DBMigrationUtil.LOGGER.log(Level.INFO, "destDB ::: {0}", destinationDB);
        PersistenceInitializer.initializeMickey(false);
        DBMigrationUtil.srcDBType = getDBType(sourceDB);
        DBMigrationUtil.srcDataSource = DataSourceManager.getDataSource("default");
        DBMigrationUtil.srcDBAdapter = (DBAdapter)DataSourceManager.getDSAdapter("default");
        if (DBMigrationUtil.srcDataSource == null) {
            DBMigrationUtil.srcDataSource = DataSourceManager.getDataSource("RelationalAPI");
            DBMigrationUtil.srcDBAdapter = (DBAdapter)DataSourceManager.getDSAdapter("RelationalAPI");
        }
        updateEncryptedPassword(dbProps);
        addDSProps(dbProps, destinationDB);
        addDestDBProps(dbProps, destinationDB);
        PersistenceInitializer.startDB(null, dbProps, null);
        ((Hashtable<String, String>)dbProps).put("shutdown.db", "true");
        DBMigrationUtil.destDataSource = DataSourceManager.getDataSource(destinationDB);
        DBMigrationUtil.destDBAdapter = (DBAdapter)DataSourceManager.getDSAdapter(destinationDB);
        validateDCMigrationProps();
        DBMigrationStatusUpdater.initialize(getHandlerFactory());
        getHandlerFactory().getProgressNotifier().printMessage("Migrating data from " + getSrcDBType() + " --to--> " + getDestDBType());
        final String adapterClassName = getHandlerFactory().getConfiguration("dbmigration.adapter.class");
        (DBMigrationUtil.dbmAdapter = (DBMAdapter)Class.forName((adapterClassName != null) ? adapterClassName.trim() : "com.adventnet.db.migration.adapter.DBMigrationAdapter").newInstance()).initialize(getHandlerFactory());
        DBMigrationUtil.isMickeyInitialized = true;
    }
    
    private static void initializeDBMigrationAdapter() throws Exception {
        final String adapterClassName = getHandlerFactory().getConfiguration("dbmigration.adapter.class");
        (DBMigrationUtil.dbmAdapter = (DBMAdapter)Class.forName((adapterClassName != null) ? adapterClassName.trim() : "com.adventnet.db.migration.adapter.DBMigrationAdapter").newInstance()).initialize(getHandlerFactory());
    }
    
    public static void dbMigrationForMWSR() throws Exception {
        final Iterator<String> databases = PersistenceInitializer.getDatabases().iterator();
        final String default_db = PersistenceInitializer.getConfigurationValue("DSAdapter");
        DBMigrationUtil.srcDBType = getDBType(default_db);
        DBMigrationUtil.srcDataSource = ((WrappedDataSource)RelationalAPI.getInstance().getDataSource()).getDataSource(0);
        DBMigrationUtil.srcDBAdapter = ((WrappedDBAdapter)RelationalAPI.getInstance().getDBAdapter()).getDBAdapter(0);
        while (databases.hasNext()) {
            final String destDB = databases.next();
            try {
                if (default_db.equals(destDB)) {
                    continue;
                }
                DBMigrationUtil.destDBType = getDBType(destDB);
                DBMigrationUtil.destDBAdapter = ((WrappedDBAdapter)RelationalAPI.getInstance().getDBAdapter()).getDBAdapter(PersistenceInitializer.getDatabases().indexOf(destDB));
                if (destDB.equals("mssql")) {
                    ((MssqlDBAdapter)DBMigrationUtil.destDBAdapter).setIsDBMigration(true);
                }
                DBMigrationUtil.destDataSource = ((WrappedDataSource)RelationalAPI.getInstance().getDataSource()).getDataSource(PersistenceInitializer.getDatabases().indexOf(destDB));
                if (DBMigrationUtil.destDBAdapter.isTablePresentInDB(getDestConnection(), null, "DBMStatus")) {
                    continue;
                }
                DBMigrationStatusUpdater.initialize(getHandlerFactory());
                getHandlerFactory().getProgressNotifier().printMessage("Migrating data from " + default_db + " --to--> " + destDB);
                initializeDBMigrationAdapter();
                DBMigrationUtil.dbmAdapter.migrateDataBase(DBMigrationUtil.srcDataSource, getDestDataSource());
                runSanityTest();
                DBMigrationStatusUpdater.updateMetaDataForSkippedOperations(getDestConnection());
                if (destDB.equals("mssql")) {
                    ((MssqlDBAdapter)DBMigrationUtil.destDBAdapter).setIsDBMigration(false);
                }
                MetaDataUtil.removeTableDefinition("DBMProcessStats");
                DBMigrationUtil.handlerFactory = null;
                DBMigrationUtil.destDBType = null;
                DBMigrationUtil.destDBAdapter = null;
                DBMigrationUtil.dbmAdapter = null;
                DBMigrationUtil.destDataSource = null;
                DBMigrationStatusUpdater.reinitializeStatus();
                DBMigrationHandlerFactory.reinitialize();
            }
            catch (final Exception e) {
                DBMigrationUtil.LOGGER.log(Level.SEVERE, "Exception occured while migrating to " + destDB + ".\nPlease analyze the problem or remove " + destDB + " configurations in database_params.conf and try again.");
                ConsoleOut.println("Exception occured while migrating to " + destDB + ".\nPlease analyze the problem or remove " + destDB + " configurations in database_params.conf and try again.");
                throw e;
            }
        }
    }
    
    private static void stopDataBase(final Properties destDBProps) throws Exception {
        if (Boolean.getBoolean("shutdown.db") || Boolean.valueOf(destDBProps.getProperty("shutdown.db", "false"))) {
            DBMigrationUtil.LOGGER.info("Shutting down source database.");
            PersistenceInitializer.stopDB();
            if (destDBProps.getProperty("StartDBServer", "false").equalsIgnoreCase("true")) {
                DBMigrationUtil.LOGGER.info("Shutting down destination database.");
                System.setProperty("stopscript", "true");
                PersistenceInitializer.stopDB(destDBProps, getDestDBAdapter());
            }
        }
    }
    
    private static void updateEncryptedPassword(final Properties props) throws PasswordException, PersistenceException {
        final String pass = props.getProperty("password");
        if (pass != null) {
            final String decryptedPass = PersistenceUtil.getDBPasswordProvider(getDestDBType().toString().toLowerCase(Locale.ENGLISH)).getPassword(props);
            ((Hashtable<String, String>)props).put("password", (decryptedPass != null) ? decryptedPass : pass);
        }
    }
    
    private static void addDSProps(final Properties prop, final String destDB) {
        prop.setProperty("DSName", destDB);
        prop.setProperty("db.migration", "true");
        prop.setProperty("db.migration.dest", "true");
        prop.putAll(PersistenceInitializer.getConfigurationProps(destDB));
    }
    
    private static void addDestDBProps(final Properties dbProps, final String destinationDB) throws Exception {
        final Boolean startDBServer = Boolean.valueOf(getHandlerFactory().getConfiguration("start.dest." + destinationDB + ".server", "false"));
        ((Hashtable<String, String>)dbProps).put("StartDBServer", Boolean.toString(startDBServer));
        DBMigrationUtil.LOGGER.info("Start destination database server ::: " + startDBServer);
        final String dbHome = getHandlerFactory().getConfiguration("dest.db." + destinationDB + ".dir");
        if (startDBServer) {
            if (dbHome == null) {
                notifyMessage("dest.db." + destinationDB + ".dir configuration not specified for destination database.");
                throw new IllegalArgumentException("start.dest." + destinationDB + ".server configuration requires dest.db." + destinationDB + ".dir");
            }
            ((Hashtable<String, String>)dbProps).put("db.home", dbHome);
        }
        ((Hashtable<String, String>)dbProps).put("create.db", Boolean.toString(getHandlerFactory().createDestDB()));
        final String dbName = getHandlerFactory().getConfiguration("dest.create.db.name");
        if (dbName != null) {
            ((Hashtable<String, String>)dbProps).put("dest.create.db.name", dbName);
        }
        dbProps.setProperty("is.dest.db", "true");
        dbProps.setProperty("dest.db.type", getDestDBType().toString().toLowerCase(Locale.ENGLISH));
    }
    
    public static DBMigrationHandlerFactory getHandlerFactory() throws Exception {
        if (!DBMigrationUtil.isMickeyInitialized && DBMigrationUtil.handlerFactory == null) {
            DBMigrationUtil.handlerFactory = new DBMigrationHandlerFactory();
        }
        return DBMigrationUtil.handlerFactory;
    }
    
    private static void notifyMessage(final String message) {
        try {
            getHandlerFactory().getProgressNotifier().printMessage(message);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void runSanityTest() throws Exception {
        Connection srcConnection = null;
        Connection destConnection = null;
        try {
            final String format = "%-70s:%5d";
            notifyMessage("Running sanity test.");
            DBMigrationUtil.runner = new SanityTestRunner(DBMigrationUtil.handlerFactory.getSanityTestConf());
            destConnection = getDestConnection();
            srcConnection = getSrcConnection();
            final Set<String> srcDBTables = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            final Set<String> destDBTables = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            srcDBTables.addAll(getSrcDBAdapter().getTables(srcConnection));
            destDBTables.addAll(getDestDBAdapter().getTables(destConnection));
            final int systemTabInSrcDB = getNoOfSystemTablesAvailable(srcDBTables);
            DBMigrationUtil.LOGGER.info(String.format(format, "Total no of tables in source DB excluding DBM tables [" + srcDBTables.size() + "-" + systemTabInSrcDB + "] ", srcDBTables.size() - systemTabInSrcDB));
            final int systemTabInDstDB = getNoOfSystemTablesAvailable(destDBTables);
            DBMigrationUtil.LOGGER.info(String.format(format, "Total no of tables in Destination DB excluding DBM tables [" + destDBTables.size() + "-" + systemTabInDstDB + "] ", destDBTables.size() - systemTabInDstDB));
            DBMigrationUtil.LOGGER.info(String.format(format, "Total no of tables skipped during migration excluding system tables  ", srcDBTables.size() - systemTabInSrcDB - (destDBTables.size() - systemTabInDstDB)));
        }
        catch (final Exception e) {
            notifyMessage("\nException occured while running sanity test. Please refer logs for more details.");
        }
        finally {
            if (srcConnection != null) {
                srcConnection.close();
            }
            if (destConnection != null) {
                destConnection.close();
            }
        }
    }
    
    private static int getNoOfSystemTablesAvailable(final Set<String> tableNames) {
        int tableCount = 0;
        for (final String tableName : DBMigrationStatusUpdater.getSystemTableNames()) {
            if (tableNames.contains(tableName)) {
                ++tableCount;
            }
        }
        return tableCount;
    }
    
    public static Map<String, String> getSanityDiff() {
        return (DBMigrationUtil.runner != null) ? DBMigrationUtil.runner.getSanityDiff() : null;
    }
    
    public static boolean isDBMigrationRunning() {
        return DBMigrationUtil.isDBMigrationRunning;
    }
    
    protected static void setDBMigrationRunning(final boolean isDBMigrationRunning) {
        DBMigrationUtil.isDBMigrationRunning = isDBMigrationRunning;
    }
    
    public static DataSource getDestDataSource() {
        return DBMigrationUtil.destDataSource;
    }
    
    static {
        DBMigrationUtil.destDBType = DBType.OTHERS;
        DBMigrationUtil.srcDBType = DBType.OTHERS;
        DBMigrationUtil.srcDataSource = null;
        DBMigrationUtil.destDataSource = null;
        DBMigrationUtil.srcDBAdapter = null;
        DBMigrationUtil.destDBAdapter = null;
        LOGGER = Logger.getLogger(DBMigrationUtil.class.getName());
        DBMigrationUtil.dbmAdapter = null;
        DBMigrationUtil.isMickeyInitialized = false;
        DBMigrationUtil.handlerFactory = null;
        DBMigrationUtil.isDBMigrationRunning = false;
        DBMigrationUtil.nonMickeyTables = new ArrayList<String>();
        DBMigrationUtil.archiveTables = new ArrayList<String>();
    }
    
    public enum DBType
    {
        OTHERS("others"), 
        MYSQL("mysql"), 
        POSTGRES("postgres"), 
        MSSQL("mssql"), 
        FIREBIRD("firebird");
        
        String dbName;
        
        private DBType(final String name) {
            this.dbName = null;
            this.dbName = name;
        }
        
        public boolean equals(final String name) {
            return this.dbName.equalsIgnoreCase(name);
        }
    }
}
