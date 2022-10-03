package com.adventnet.persistence;

import java.util.Hashtable;
import com.zoho.mickey.exception.PasswordException;
import java.sql.ResultSet;
import java.sql.Statement;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.Collection;
import com.adventnet.mfw.service.ServiceUtil;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.mfw.ConfPopulator;
import java.sql.SQLException;
import com.adventnet.db.adapter.AbstractRestoreHandler;
import com.zoho.framework.utils.crypto.EnDecrypt;
import com.zoho.framework.utils.crypto.EnDecryptUtil;
import com.adventnet.persistence.internal.SequenceGeneratorRepository;
import com.adventnet.persistence.xml.Xml2DoConverter;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilderFactory;
import com.adventnet.persistence.internal.Operation;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.db.persistence.metadata.MetaDataAccess;
import java.net.URL;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.xml.DynamicValueHandlerRepositry;
import com.adventnet.db.persistence.metadata.DataDictionary;
import java.sql.Connection;
import com.adventnet.ds.DSUtil;
import com.zoho.mickey.api.SQLStringAPI;
import com.adventnet.ds.adapter.DataSourceAdapter;
import com.adventnet.ds.DataSourceManager;
import com.adventnet.ds.adapter.mds.MDSDataSourcePlugIn;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.cp.MultiDSUtil;
import com.adventnet.db.persistence.metadata.parser.DataDictionaryParser;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.adventnet.persistence.util.DCManager;
import com.adventnet.mfw.ConsoleOut;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import com.adventnet.db.archive.StorageAdapter;
import java.util.logging.Level;
import com.adventnet.db.archive.ArchiveAdapter;
import java.util.Iterator;
import com.adventnet.db.adapter.WrappedDBAdapter;
import com.adventnet.db.adapter.SQLGenerator;
import com.zoho.framework.utils.crypto.CryptoUtil;
import java.io.File;
import com.zoho.conf.Configuration;
import java.util.Map;
import javax.sql.DataSource;
import com.adventnet.db.adapter.DBAdapter;
import com.adventnet.ds.DataSourcePlugIn;
import com.adventnet.db.util.CreateSchema;
import java.util.Properties;
import java.util.List;
import java.util.HashMap;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Logger;

public class PersistenceInitializer
{
    private static final Logger OUT;
    private static RelationalAPI relationalapi;
    private static boolean isInitialized;
    private static HashMap<String, String> confNameVsValue;
    private static HashMap<String, List<String>> confNameVsList;
    private static HashMap<String, Properties> confNameVsProps;
    private static Properties dbProps;
    private static DataObject defaultDSDO;
    private static CreateSchema createSchema;
    private static DataSourcePlugIn dsPlugIn;
    private static boolean onSAS;
    private static boolean isMDS;
    private static DBAdapter dbadapter;
    private static DataSource ds;
    private static boolean coldStart;
    private static String server_home;
    private static List<String> databases;
    private static int dbConfigCount;
    public static final List<String> ERRORCODETABLENAMES;
    static PersistencePreprocessor preprocess;
    private static Map moduleNameVsModuleID;
    
    private PersistenceInitializer() {
    }
    
    public static Properties getDBProps() throws Exception {
        final Properties props = new Properties();
        String url = Configuration.getString("db.url");
        if (url.indexOf("${product.home}") >= 0) {
            String replaceStr = new File(PersistenceInitializer.server_home).getCanonicalPath();
            replaceStr = replaceStr.replaceAll("\\\\", "/");
            url = url.replaceAll("\\$\\{product.home\\}", replaceStr);
        }
        props.setProperty("url", url);
        props.setProperty("username", Configuration.getString("db.username", ""));
        props.setProperty("password", CryptoUtil.decrypt(Configuration.getString("db.password", "")));
        props.setProperty("drivername", Configuration.getString("db.drivername"));
        props.setProperty("minsize", Configuration.getString("db.minsize", "1"));
        props.setProperty("maxsize", Configuration.getString("db.maxsize", "5"));
        props.setProperty("transaction_isolation", Configuration.getString("db.transaction.isolation", "TRANSACTION_READ_COMMITTED"));
        props.setProperty("additional_properties", Configuration.getString("db.additional.properties", ""));
        props.setProperty("port", Configuration.getString("db.port", "3306"));
        props.setProperty("schemaname", Configuration.getString("db.schemaname", "jbossdb"));
        props.setProperty("dbname", Configuration.getString("db.name", "mysql"));
        props.setProperty("organizationname", Configuration.getString("db.organizationname", "sas.zohocrm.com"));
        props.setProperty("exceptionsorterclassname", Configuration.getString("db.exception.sorter.classname", "com.adventnet.sas.provisioning.db.DBExceptionSorter"));
        props.setProperty("criteriaproviderclassname", Configuration.getString("db.criteria.provider.classname", "com.adventnet.sas.signup.SASCriteriaProvider"));
        props.setProperty("run_scheduler_lb", Boolean.toString(Configuration.getBoolean("db.run.scheduler.lb", "true")));
        props.setProperty("scheduler_lb_interval", Configuration.getString("db.scheduler.lb.interval", "600"));
        props.setProperty("readonlyslave", Boolean.toString(Configuration.getBoolean("db.readonly.slave", "false")));
        props.setProperty("useRedisForSASThreadLocal", Configuration.getString("db.use.redis.for.sasthreadlocal", ""));
        props.setProperty("ukmodules", Configuration.getString("db.ukmodules", ""));
        props.setProperty("ignoreMysqlExceptionForAudit", Configuration.getString("db.ignore.mysqlexception.foraudit", ""));
        props.setProperty("nonPropagatedTables", Configuration.getString("db.nonpropagated.tables", ""));
        props.setProperty("nonPropagatedModules", Configuration.getString("db.nonpropagated.modules", ""));
        props.setProperty("dbadapter", Configuration.getString("db.adapter", ""));
        props.setProperty("sqlgenerator", Configuration.getString("db.sqlgenerator", ""));
        return props;
    }
    
    public static List<String> getDatabases() {
        return PersistenceInitializer.databases;
    }
    
    public static int getMWSRConfigCount() {
        return PersistenceInitializer.dbConfigCount;
    }
    
    public static Properties getDBProps(final DataObject dsDO) throws Exception {
        final Properties props = new Properties();
        final Row dsRow = dsDO.getRow("DataSource");
        final Row dsAdapterRow = dsDO.getRow("DBAdapter");
        final String url = (String)dsAdapterRow.get(4);
        props.setProperty("url", url);
        final String userName = (String)dsAdapterRow.get(5);
        props.setProperty("username", userName);
        String password = (String)dsAdapterRow.get(6);
        if (password == null) {
            password = "";
        }
        props.setProperty("password", password);
        final String driverName = (String)dsAdapterRow.get(3);
        props.setProperty("drivername", driverName);
        Integer minSize = (Integer)dsAdapterRow.get(8);
        if (minSize == null || minSize < 1) {
            minSize = new Integer(1);
        }
        props.setProperty("minsize", minSize.toString());
        Integer maxSize = (Integer)dsAdapterRow.get(9);
        if (maxSize == null || maxSize < 1) {
            maxSize = new Integer(5);
        }
        props.setProperty("maxsize", maxSize.toString());
        final String transIsolationLevel = (String)((dsAdapterRow.get(7) != null) ? dsAdapterRow.get(7) : "TRANSACTION_READ_COMMITTED");
        props.setProperty("transaction_isolation", transIsolationLevel);
        final String sqlGenClass = (String)dsAdapterRow.get(2);
        props.setProperty("sqlgenerator", sqlGenClass);
        final String dsName = (String)dsRow.get(2);
        props.setProperty("DSName", dsName);
        final String dbadapterClass = (String)dsRow.get(3);
        props.setProperty("dbadapter", dbadapterClass);
        final Object isActive = dsRow.get("ISACTIVE");
        if (isActive != null) {
            props.setProperty("isactive", isActive.toString());
        }
        return props;
    }
    
    public static DBAdapter createDBAdapter(final Properties props) throws Exception {
        final String dbAdapterClass = props.getProperty("dbadapter");
        final String sqlGenClass = props.getProperty("sqlgenerator");
        final DBAdapter dbadap = (DBAdapter)Thread.currentThread().getContextClassLoader().loadClass(dbAdapterClass).newInstance();
        if (sqlGenClass != null && !sqlGenClass.equals("")) {
            final SQLGenerator sqlGen = (SQLGenerator)Thread.currentThread().getContextClassLoader().loadClass(sqlGenClass).newInstance();
            dbadap.setSQLGenerator(sqlGen);
            sqlGen.setKey(getConfigurationValue("ECTag"));
        }
        return dbadap;
    }
    
    public static DBAdapter createMultiDBAdapters() throws Exception {
        final DBAdapter wDBAdapter = new WrappedDBAdapter();
        for (final String db : PersistenceInitializer.databases) {
            final Properties adapterProps = getConfigurationProps(db);
            adapterProps.setProperty("DSName", "RelationalAPI");
            ((WrappedDBAdapter)wDBAdapter).addDBAdapter(createDBAdapter(adapterProps));
        }
        return wDBAdapter;
    }
    
    public static ArchiveAdapter createArchiveAdapter(final Properties props) throws Exception {
        final String arcAdapterClass = (props == null) ? "com.adventnet.db.archive.DefaultArchiveAdapter" : props.getProperty("archiveadapter", "com.adventnet.db.archive.DefaultArchiveAdapter");
        final String storageAdapterClass = (props != null) ? props.getProperty("storageadapter") : null;
        PersistenceInitializer.OUT.log(Level.INFO, "Archive Adapter class ::: {0}", arcAdapterClass);
        PersistenceInitializer.OUT.log(Level.INFO, "Storage Adapter class ::: {0}", storageAdapterClass);
        final ArchiveAdapter arcAdap = (ArchiveAdapter)Thread.currentThread().getContextClassLoader().loadClass(arcAdapterClass).newInstance();
        if (storageAdapterClass != null && !storageAdapterClass.equals("")) {
            final StorageAdapter storegeAdapter = (StorageAdapter)Thread.currentThread().getContextClassLoader().loadClass(storageAdapterClass).newInstance();
            arcAdap.setStorageAdapter(storegeAdapter);
        }
        return arcAdap;
    }
    
    public static String getDBParamsFilePath() {
        final String customPath = Configuration.getString("databaseparams.file");
        if (customPath != null) {
            PersistenceInitializer.OUT.log(Level.INFO, "Reading {0} ", customPath);
            return customPath;
        }
        final String dbName = getConfigurationValue("DBName");
        final String path = PersistenceInitializer.server_home + File.separator + "conf" + File.separator;
        final String dbSpecificFilePath = path + dbName + "_database_params.conf";
        final String defaultFilePath = path + "database_params.conf";
        if (new File(dbSpecificFilePath).exists()) {
            PersistenceInitializer.OUT.log(Level.INFO, "Reading {0} ", dbSpecificFilePath);
            return dbSpecificFilePath;
        }
        PersistenceInitializer.OUT.log(Level.INFO, "Reading {0} ", defaultFilePath);
        return defaultFilePath;
    }
    
    public static Properties getDBProps(final String propFileName) throws Exception {
        if (!new File(propFileName).exists()) {
            return PersistenceInitializer.dbProps;
        }
        final FileInputStream fis = new FileInputStream(new File(propFileName));
        final Properties props = new Properties();
        props.load(fis);
        fis.close();
        String url = props.getProperty("url");
        if (url.indexOf("${product.home}") >= 0) {
            String replaceStr = new File(PersistenceInitializer.server_home).getCanonicalPath();
            replaceStr = replaceStr.replaceAll("\\\\", "/");
            url = url.replaceAll("\\$\\{product.home\\}", replaceStr);
        }
        props.setProperty("url", url);
        final String userName = props.getProperty("username");
        String password = props.getProperty("password");
        final String superuserPassword = props.getProperty("superuser_pass");
        password = getDBPassword(props);
        password = ((password == null) ? "" : password);
        props.setProperty("password", password);
        if (superuserPassword != null) {
            props.setProperty("superuser_pass", PersistenceUtil.getDBPasswordProvider().getPassword(superuserPassword));
        }
        props.setProperty("drivername", Configuration.getString("drivername", props.getProperty("drivername")));
        final int minSize = (props.getProperty("minsize") != null) ? Integer.parseInt(props.getProperty("minsize")) : 1;
        if (minSize == 1) {
            props.setProperty("minsize", String.valueOf(minSize));
        }
        final int maxSize = (props.getProperty("maxsize") != null) ? Integer.parseInt(props.getProperty("maxsize")) : 5;
        if (maxSize == 5) {
            props.setProperty("maxsize", String.valueOf(maxSize));
        }
        final int transIsolationLevel = (props.getProperty("transaction_isolation") != null) ? getIsolationLevel(props.getProperty("transaction_isolation")) : 2;
        return props;
    }
    
    public static DataObject getDataSourceDO(final Properties props) throws Exception {
        final DataObject dsDO = new WritableDataObject();
        final Row dsRow = new Row("DataSource");
        dsRow.set(2, "default");
        dsRow.set(3, props.getProperty("dbadapter"));
        dsRow.set(4, "Default DataSource.");
        dsDO.addRow(dsRow);
        final Row dsAdapterRow = new Row("DBAdapter");
        dsAdapterRow.set(1, dsRow.get(1));
        dsAdapterRow.set(2, props.getProperty("sqlgenerator"));
        dsAdapterRow.set(3, props.getProperty("drivername"));
        dsAdapterRow.set(4, props.getProperty("url"));
        dsAdapterRow.set(5, props.getProperty("username"));
        dsAdapterRow.set(6, props.getProperty("password"));
        dsAdapterRow.set(9, new Integer(props.getProperty("maxsize")));
        dsAdapterRow.set(8, new Integer(props.getProperty("minsize")));
        dsAdapterRow.set(7, new Integer(getIsolationLevel(props.getProperty("transaction_isolation"))));
        dsDO.addRow(dsAdapterRow);
        return dsDO;
    }
    
    public static synchronized void initialize(final String confFileDir) throws Exception {
        loadPersistenceConfigurations();
        initializeDBAndPers(confFileDir);
    }
    
    public static void initialize(final ConfigurationParser parser) throws Exception {
        PersistenceInitializer.dbProps = parser.getDBProperties();
        initialize((String)null);
    }
    
    public static void initializeDBAndDBProps(final ConfigurationParser parser) throws Exception {
        PersistenceInitializer.dbProps = parser.getDBProperties();
        initializeDB(null);
    }
    
    public static void initializeDBAndPers(final String confFileDir) throws Exception {
        if (PersistenceInitializer.isInitialized) {
            return;
        }
        try {
            initializeDB(confFileDir);
        }
        catch (final Exception e) {
            final Exception newException = new Exception("Exception while initializing DB.");
            newException.initCause(e);
            throw newException;
        }
        try {
            initializePersistence();
        }
        catch (final Exception e) {
            final Exception newException = new Exception("Exception while initializing Persistence Module.");
            newException.initCause(e);
            throw newException;
        }
        PersistenceInitializer.isInitialized = true;
        PersistenceInitializer.ds = null;
        PersistenceInitializer.dbadapter = null;
    }
    
    public static synchronized void initializeRelationalAPI(final String confFileDir) throws Exception {
        loadPersistenceConfigurations();
        if (!(PersistenceInitializer.onSAS = getConfigurationValue("onSAS").equalsIgnoreCase("true"))) {
            FileInputStream fis = null;
            final Properties props = new Properties();
            try {
                final File file = new File(getDBParamsFilePath());
                if (file.exists()) {
                    fis = new FileInputStream(file);
                    props.load(fis);
                    fis.close();
                }
            }
            catch (final FileNotFoundException e) {
                throw e;
            }
            finally {
                if (fis != null) {
                    fis.close();
                }
            }
            PersistenceInitializer.databases.clear();
            if (getConfigurationValue("EnableMWSR") != null && getConfigurationValue("EnableMWSR").equals("true")) {
                if (getConfigurationValue("DSAdapter").equals("mds")) {
                    printErrorMsgAndHalt("MDS cannot be enabled when MWSR is enabled.");
                }
                PersistenceInitializer.databases.add(getConfigurationValue("DSAdapter"));
                if (Boolean.getBoolean("development.mode")) {
                    if (props.containsKey("postgres.url")) {
                        if (getConfigurationValue("DSAdapter").equals("postgres")) {
                            printErrorMsgAndHalt("Postgres is a default database. So the paramaters for that database will be taken from properties url,username and so on. There should not be any additional properties for postgres like 'postgres.url', 'postgres.username' and so on.");
                        }
                        else {
                            PersistenceInitializer.databases.add("postgres");
                        }
                    }
                    if (props.containsKey("mysql.url")) {
                        if (getConfigurationValue("DSAdapter").equals("mysql")) {
                            printErrorMsgAndHalt("Mysql is a default database. So the paramaters for that database will be taken from properties url,username and so on. There should not be any additional properties for mysql like 'mysql.url', 'mysql.username' and so on.");
                        }
                        else {
                            PersistenceInitializer.databases.add("mysql");
                        }
                    }
                    if (props.containsKey("mssql.url")) {
                        if (getConfigurationValue("DSAdapter").equals("mssql")) {
                            printErrorMsgAndHalt("Mssql is a default database. So the paramaters for that database will be taken from properties url,username and so on. There should not be any additional properties for mssql like 'mssql.url', 'mssql.username' and so on.");
                        }
                        else {
                            PersistenceInitializer.databases.add("mssql");
                        }
                    }
                }
                else {
                    PersistenceInitializer.OUT.log(Level.SEVERE, "Development mode should be set while using MWSR");
                    ConsoleOut.println("Development mode should be set while using MWSR");
                    System.exit(1);
                }
            }
            else if (getConfigurationValue("DSAdapter").equals("mds")) {
                PersistenceInitializer.databases.add(getConfigurationProps("DataSourcePlugIn").getProperty("DefaultDSAdapter"));
            }
            else {
                PersistenceInitializer.databases.add(getConfigurationValue("DSAdapter"));
            }
            DCManager.initialize();
        }
        PersistenceInitializer.OUT.log(Level.INFO, "Loading UDTs from directory :: {0}", PersistenceInitializer.server_home + File.separator + "conf" + File.separator + "udt");
        DataTypeManager.initialize(null);
        PersistenceInitializer.dbConfigCount = PersistenceInitializer.databases.size();
        DataDictionaryParser.setValueForOnSAS(PersistenceInitializer.onSAS);
        DataDictionaryParser.setTablesWithoutUVGColsInPK(getConfigurationList("TablesWithoutUVGColsInPK"));
        loadDBParams();
        String dataSourcePlugInClass = null;
        final Properties dataSourceProps = getConfigurationProps("DataSourcePlugIn");
        final Properties backupRestoreProps = getConfigurationProps("BackupRestore");
        final String config = getConfigurationValue("MonitoringConfiguration");
        final String adapterType = getConfigurationValue("DSAdapter");
        MultiDSUtil.setDefaultDB(adapterType);
        final Properties adapterProps = getConfigurationProps(adapterType);
        adapterProps.setProperty("DSName", "RelationalAPI");
        PersistenceInitializer.dbProps.putAll(adapterProps);
        if (getConfigurationValue("EnableMWSR") != null && getConfigurationValue("EnableMWSR").equals("true")) {
            dataSourcePlugInClass = getConfigurationValue("MWSRDataSourcePlugIn");
        }
        else {
            dataSourcePlugInClass = getConfigurationValue("DataSourcePlugIn");
        }
        if (getConfigurationValue("EnableMWSR") != null && getConfigurationValue("EnableMWSR").equals("true") && !DBMigrationUtil.isDBMigrationRunning()) {
            if (Boolean.getBoolean("development.mode")) {
                PersistenceInitializer.dbadapter = createMultiDBAdapters();
            }
            else {
                PersistenceInitializer.OUT.log(Level.SEVERE, "Development mode should be set to true while using MWSR");
                ConsoleOut.println("Development mode should be set to true while using MWSR");
                System.exit(1);
            }
        }
        else {
            PersistenceInitializer.dbadapter = createDBAdapter(adapterProps);
        }
        if (PersistenceInitializer.dbProps.getProperty("exceptionsorterclassname") == null) {
            PersistenceInitializer.dbProps.setProperty("exceptionsorterclassname", PersistenceInitializer.dbadapter.getDBSpecificExceptionSorterName());
        }
        if (PersistenceInitializer.dbProps.getProperty("aborthandlerclassname") == null && PersistenceInitializer.dbadapter.getDBSpecificAbortHandlerName() != null) {
            PersistenceInitializer.dbProps.setProperty("aborthandlerclassname", PersistenceInitializer.dbadapter.getDBSpecificAbortHandlerName());
        }
        final Map map = PersistenceInitializer.dbadapter.splitConnectionURL(PersistenceInitializer.dbProps.getProperty("url"));
        if (PersistenceInitializer.dbProps.getProperty("host") == null || PersistenceInitializer.dbProps.getProperty("host") == "") {
            PersistenceInitializer.dbProps.setProperty("host", map.get("Server"));
        }
        if (dataSourceProps != null) {
            PersistenceInitializer.dbProps.putAll(dataSourceProps);
        }
        if (backupRestoreProps != null) {
            PersistenceInitializer.dbProps.putAll(backupRestoreProps);
        }
        PersistenceInitializer.dbProps.setProperty("DSAdapter", adapterType);
        PersistenceInitializer.dsPlugIn = createDataSourcePlugIn(dataSourcePlugInClass, PersistenceInitializer.dbProps);
        PersistenceInitializer.isMDS = (PersistenceInitializer.dsPlugIn instanceof MDSDataSourcePlugIn);
        DataAccess.setTransactionManager(PersistenceInitializer.dsPlugIn.getTxManager());
        PersistenceInitializer.ds = PersistenceInitializer.dsPlugIn.getDataSource();
        String url = PersistenceInitializer.dbProps.getProperty("url");
        if (url.contains("$port")) {
            url = getTemplatesReplacedUrl(PersistenceInitializer.dbProps);
        }
        final ArchiveAdapter archiveAdap = createArchiveAdapter(getConfigurationProps("archive"));
        final Properties confProps = new Properties();
        final String isStream = (PersistenceInitializer.confNameVsValue.get("StreamingResultSet") != null) ? PersistenceInitializer.confNameVsValue.get("StreamingResultSet") : "true";
        confProps.setProperty("StreamingResultSet", isStream);
        PersistenceInitializer.relationalapi = new RelationalAPI(PersistenceInitializer.dbadapter, PersistenceInitializer.ds, archiveAdap, confProps);
        final String dbchClassName = getConfigurationValue("DBCrashHandler");
        if (dbchClassName != null) {
            PersistenceInitializer.relationalapi.setDBCrashHandlerClassName(dbchClassName);
            PersistenceInitializer.OUT.log(Level.SEVERE, "DBCrashHandler [{0}] has been set to RelationalAPI.", dbchClassName);
        }
        DataSourceManager.addDataSource("RelationalAPI", PersistenceInitializer.ds, PersistenceInitializer.dbadapter);
        PersistenceInitializer.dbadapter.initialize(PersistenceInitializer.dbProps);
        new SQLStringAPI(PersistenceInitializer.dbadapter.getSQLModifier());
        if (DSUtil.setDSPlugIn(PersistenceInitializer.dsPlugIn)) {
            PersistenceInitializer.OUT.log(Level.INFO, "DataSourcePlugIn is initialized successfully");
            return;
        }
        throw new Exception("DataSourcePlugIn is already initialized");
    }
    
    public static synchronized void initializeDB(final String confFileDir) throws Exception {
        initializeRelationalAPI(confFileDir);
        startDB();
        if (Configuration.getString("useAvailableDBPort", "false").equals("true")) {
            final ArchiveAdapter archiveAdap = createArchiveAdapter(getConfigurationProps("archive"));
            final Properties confProps = new Properties();
            final String isStream = (PersistenceInitializer.confNameVsValue.get("StreamingResultSet") != null) ? PersistenceInitializer.confNameVsValue.get("StreamingResultSet") : "true";
            confProps.setProperty("StreamingResultSet", isStream);
            PersistenceInitializer.relationalapi = new RelationalAPI(PersistenceInitializer.dbadapter, PersistenceInitializer.ds, archiveAdap, confProps);
            DataSourceManager.addDataSource("RelationalAPI", PersistenceInitializer.ds, PersistenceInitializer.dbadapter);
            PersistenceInitializer.dbadapter.initialize(PersistenceInitializer.dbProps);
        }
        DataAccess.setDataSource(PersistenceInitializer.ds);
        checkAndPrepareDatabase(PersistenceInitializer.dbadapter, PersistenceInitializer.ds);
        PersistenceInitializer.dbadapter.logDatabaseDetails();
    }
    
    public static void checkAndPrepareDatabase(final DBAdapter dbadapter, final DataSource ds) throws Exception {
        dbadapter.checkDBStatus(getTemplatesReplacedUrl(dbadapter.getDBProps()));
        Connection conn = null;
        try {
            conn = ds.getConnection();
            dbadapter.prepareDatabase(conn);
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
    
    private static void initializePersistence() throws Exception {
        try {
            final URL dynValURL = DataDictionary.class.getResource("conf/dynamic-value-handlers.xml");
            final DynamicValueHandlerRepositry dynValRep = new DynamicValueHandlerRepositry();
            dynValRep.parse(dynValURL);
            MetaDataUtil.loadSchemaTemplates("Persistence");
            final List<String> moduleNames = getModuleNames();
            final List<URL> listOfExtendedDDFiles = new ArrayList<URL>();
            for (final String moduleName : moduleNames) {
                final File confFilePath = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + moduleName + File.separator + "extended_dd.attr");
                if (confFilePath.exists() && confFilePath.isFile()) {
                    listOfExtendedDDFiles.add(confFilePath.toURI().toURL());
                }
            }
            MetaDataUtil.loadCustomAttributes(listOfExtendedDDFiles, true, true);
            final URL metaDDURL = DataDictionary.class.getResource("conf/meta-dd.xml");
            final DataDictionary metaDD = MetaDataAccess.loadDataDictionary(metaDDURL, false);
            Connection c = null;
            try {
                c = RelationalAPI.getInstance().getConnection();
                PersistenceInitializer.coldStart = !RelationalAPI.getInstance().getDBAdapter().isTablePresentInDB(c, null, "SeqGenState");
            }
            finally {
                try {
                    if (c != null) {
                        c.close();
                    }
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            String persDDName = null;
            if (isColdStart()) {
                final URL persDDURL = DataDictionary.class.getResource("conf/data-dictionary.xml");
                final DataDictionary persDD = MetaDataAccess.loadDataDictionary(persDDURL, false);
                persDDName = persDD.getName();
                if (ConcurrentStartupUtil.isConcurrentTableCreation()) {
                    PersistenceInitializer.OUT.log(Level.FINER, "Parallely creating persistence tables");
                    ConcurrentStartupUtil.createTables(metaDD.getName());
                    ConcurrentStartupUtil.createTables(persDD.getName());
                }
                else {
                    DataAccess.createTables(metaDD.getName());
                    DataAccess.createTables(persDD.getName());
                }
                initializeSeqGenerator();
                initializePreprocessor();
                if (null != PersistenceInitializer.preprocess) {
                    PersistenceInitializer.preprocess.preReady();
                }
                SchemaBrowserUtil.setReady(true);
                if (null != PersistenceInitializer.preprocess) {
                    PersistenceInitializer.preprocess.postReady();
                    PersistenceInitializer.preprocess.preMetaDataFetch();
                }
                final String[] names = MetaDataUtil.getAllDataDictionarNames();
                for (int i = 0; i < names.length; ++i) {
                    final DataDictionary dataDic = MetaDataUtil.getDataDictionary(names[i]);
                    DataAccess.getTransactionManager().begin();
                    try {
                        DataAccess.addDataDictionary(dataDic);
                        DataAccess.getTransactionManager().commit();
                    }
                    catch (final Exception e2) {
                        PersistenceInitializer.OUT.log(Level.INFO, "Error while populating Schema data during persistence initialization :: {0} ", new Object[] { e2 });
                        DataAccess.getTransactionManager().rollback();
                        throw e2;
                    }
                }
                if (null != PersistenceInitializer.preprocess) {
                    PersistenceInitializer.preprocess.postMetaDataFetch();
                    PersistenceInitializer.preprocess.prePersonalityFetch();
                }
                final URL persURL = DataDictionary.class.getResource("conf/personality-configuration.xml");
                final DataObject pcDO = PersonalityConfigurationUtil.initializePersonalityConfiguration(persDD.getName(), persURL);
                if (onSAS() && isColdStart()) {
                    final List<String> idxTabNames = new ArrayList<String>();
                    final Iterator idxTbIterator = pcDO.getRows("TableDetails");
                    while (idxTbIterator.hasNext()) {
                        final Row row = idxTbIterator.next();
                        final String tableName = (String)row.get("TABLE_NAME");
                        idxTabNames.add(tableName);
                    }
                    DataAccess.createTables(idxTabNames);
                }
                if (null != PersistenceInitializer.preprocess) {
                    PersistenceInitializer.preprocess.postPersonalityFetch();
                }
            }
            else {
                loadModule("Persistence");
                final URL persURL2 = DataDictionary.class.getResource("conf/personality-configuration.xml");
                PersistenceInitializer.OUT.log(Level.INFO, "persURL :: {0}", persURL2);
                PersonalityConfigurationUtil.initializePersonalityConfiguration("Persistence", persURL2);
                initializeSeqGenerator();
                initializePreprocessor();
            }
        }
        catch (final Exception e3) {
            e3.printStackTrace();
            PersistenceInitializer.OUT.log(Level.INFO, "transaction has rolled back since error has encountered");
            throw e3;
        }
        initializeCreateSchema();
        Operation.setOperationHandler("OperationHandler");
        loadErrorCodes(new File(PersistenceInitializer.server_home + File.separator + "conf" + File.separator + "Persistence" + File.separator + "error-codes.xml").getAbsolutePath());
    }
    
    public static List<String> getModuleNames() throws SAXException, IOException, ParserConfigurationException {
        final List<String> moduleNames = new ArrayList<String>();
        final File moduleXml = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "module.xml");
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(moduleXml);
        final NodeList nList = doc.getDocumentElement().getChildNodes();
        final TreeMap<Integer, String> moduleOrderVsModuleName = new TreeMap<Integer, String>();
        for (int temp = 0; temp < nList.getLength(); ++temp) {
            final Node nNode = nList.item(temp);
            if (nNode.getNodeType() == 1) {
                final Element eElement = (Element)nNode;
                moduleOrderVsModuleName.put(Integer.parseInt(eElement.getAttribute("moduleorder")), eElement.getAttribute("modulename"));
            }
        }
        for (final Integer key : moduleOrderVsModuleName.keySet()) {
            moduleNames.add(moduleOrderVsModuleName.get(key));
        }
        return moduleNames;
    }
    
    public static void loadErrorCodes(final String absoluteFilePath) throws DataAccessException {
        PersistenceInitializer.OUT.log(Level.INFO, "Going to loadErrorCodes :: [{0}]", absoluteFilePath);
        DataObject errorCodesDO = null;
        try {
            errorCodesDO = Xml2DoConverter.transform(absoluteFilePath);
            ((WritableDataObject)errorCodesDO).clearOperations();
            PersistenceInitializer.OUT.log(Level.FINE, "errorCodesDO :: {0}", errorCodesDO);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new DataAccessException(e);
        }
        final List<String> tableNames = errorCodesDO.getTableNames();
        if (tableNames.contains("AdventNetErrorCode")) {
            for (final String errCodeTbName : PersistenceInitializer.ERRORCODETABLENAMES) {
                if (!PersistenceInitializer.ERRORCODETABLENAMES.contains(errCodeTbName)) {
                    PersistenceInitializer.OUT.log(Level.WARNING, "Ignoring to process the tableName :: [{0}]", errCodeTbName);
                }
                else {
                    Map<Object, ErrorCodes.AdventNetErrorCode> errorCodeMap = ErrorCodes.errorCodesMap.get(errCodeTbName);
                    if (errorCodeMap == null) {
                        errorCodeMap = new HashMap<Object, ErrorCodes.AdventNetErrorCode>();
                        ErrorCodes.errorCodesMap.put(errCodeTbName, errorCodeMap);
                    }
                    final Iterator iterator = errorCodesDO.getRows(errCodeTbName);
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        final Integer advErrorCode = (Integer)errorCodesDO.getRow("AdventNetErrorCode", row).get(2);
                        final Object dbSpecErrCode = row.get("ERRORCODE");
                        ErrorCodes.AdventNetErrorCode ae = null;
                        if ("AdventNetErrorCode".equals(errCodeTbName)) {
                            ae = new ErrorCodes.AdventNetErrorCode();
                            ae.errorCode = advErrorCode;
                            ae.errorMessage = (String)errorCodesDO.getRow("AdventNetErrorCode", row).get(4);
                            ae.errorString = (String)errorCodesDO.getRow("AdventNetErrorCode", row).get(3);
                            if (ErrorCodes.advErrStr_Vs_ErrCode.get(ae.errorString) != null) {
                                PersistenceInitializer.OUT.log(Level.SEVERE, "Already an AdventNetErrorcode [{0}] has been defined, hence over-writing it", dbSpecErrCode);
                            }
                            ErrorCodes.advErrStr_Vs_ErrCode.put(ae.errorString, ae);
                        }
                        else {
                            ae = ErrorCodes.getErrorCodeMap("AdventNetErrorCode").get(advErrorCode);
                        }
                        if (errorCodeMap.containsKey(dbSpecErrCode)) {
                            PersistenceInitializer.OUT.log(Level.SEVERE, "Already an errorcode [{0}] has been defined, hence over-writing it", dbSpecErrCode);
                        }
                        errorCodeMap.put(dbSpecErrCode, ae);
                    }
                }
            }
        }
        else {
            PersistenceInitializer.OUT.log(Level.WARNING, "This dataObject :: {0} does not contain any AdventNetErrorCode row entries, hence it is ignored.", errorCodesDO);
        }
        PersistenceInitializer.OUT.log(Level.INFO, "ErrorCodes :: {0}", ErrorCodes.errorCodesMap);
    }
    
    static void initializeSeqGenerator() throws Exception {
        final Properties seqGenProps = getConfigurationProps("SeqGenTypeVsClass");
        SequenceGeneratorRepository.put("INTEGER", seqGenProps.getProperty("INTEGER"));
        SequenceGeneratorRepository.put("BIGINT", seqGenProps.getProperty("BIGINT"));
    }
    
    static void initializeCreateSchema() {
        PersistenceInitializer.createSchema = new CreateSchema();
    }
    
    static boolean initializePreprocessor() throws Exception {
        String preprocessor = getConfigurationValue("Preprocessor");
        if (preprocessor != null && !preprocessor.equals("")) {
            (PersistenceInitializer.preprocess = (PersistencePreprocessor)Thread.currentThread().getContextClassLoader().loadClass(preprocessor).newInstance()).initialize(PersistenceInitializer.coldStart);
            PersistenceInitializer.OUT.log(Level.INFO, "Preprocessor :: [{0}] has been instantiated and initialized.", preprocessor);
        }
        else {
            if (!onSAS()) {
                PersistenceInitializer.OUT.log(Level.INFO, "No Preprocessor found.");
                return false;
            }
            preprocessor = "com.adventnet.persistence.PersistencePreprocessorImpl";
            PersistenceInitializer.OUT.log(Level.INFO, "No Preprocessor found. so : using the default." + preprocessor);
            (PersistenceInitializer.preprocess = (PersistencePreprocessor)Thread.currentThread().getContextClassLoader().loadClass(preprocessor).newInstance()).initialize(PersistenceInitializer.coldStart);
        }
        return true;
    }
    
    private static void decryptPersistenceConfigurationParams() throws Exception {
        if (PersistenceInitializer.confNameVsProps.containsKey("mssql")) {
            final Properties decryptedProps = new Properties();
            final Properties sqlProps = PersistenceInitializer.confNameVsProps.get("mssql");
            for (final Object sqlProp : ((Hashtable<Object, V>)sqlProps).keySet()) {
                final String propValue = sqlProps.getProperty((String)sqlProp);
                if (PersistenceUtil.getEncryptedSqlServerProps().contains(sqlProp)) {
                    decryptedProps.setProperty((String)sqlProp, CryptoUtil.decrypt(propValue));
                }
                else {
                    decryptedProps.setProperty((String)sqlProp, propValue);
                }
            }
            PersistenceInitializer.confNameVsProps.put("mssql", decryptedProps);
        }
        for (final String propName : PersistenceInitializer.confNameVsValue.keySet()) {
            if (PersistenceUtil.getEncryptedConfigurations().contains(propName)) {
                final String propValue2 = PersistenceInitializer.confNameVsValue.get(propName);
                if (null == propValue2) {
                    continue;
                }
                PersistenceInitializer.confNameVsValue.put(propName, CryptoUtil.decrypt(propValue2.toString()));
            }
        }
    }
    
    public static void loadDBParams() throws Exception {
        if (!(PersistenceInitializer.onSAS = getConfigurationValue("onSAS").equalsIgnoreCase("true"))) {
            final String dbParamsFileName = getDBParamsFilePath();
            PersistenceInitializer.dbProps = getDBProps(dbParamsFileName);
        }
        else {
            PersistenceInitializer.dbProps = getDBProps();
        }
        PersistenceInitializer.dbProps.setProperty("DSName", "default");
        PersistenceInitializer.OUT.log(Level.INFO, "Driver Name :: {0}", PersistenceInitializer.dbProps.getProperty("drivername"));
        PersistenceInitializer.OUT.log(Level.INFO, "Connection URL :: {0}", PersistenceInitializer.dbProps.getProperty("url"));
        PersistenceInitializer.OUT.log(Level.INFO, "Username :: {0}", PersistenceInitializer.dbProps.getProperty("username"));
        PersistenceInitializer.OUT.log(Level.INFO, "Min Size :: {0}", PersistenceInitializer.dbProps.getProperty("minsize"));
        PersistenceInitializer.OUT.log(Level.INFO, "Max Size :: {0}", PersistenceInitializer.dbProps.getProperty("maxsize"));
        PersistenceInitializer.OUT.log(Level.INFO, "Transaction Isolation :: {0}", PersistenceInitializer.dbProps.getProperty("transaction_isolation"));
    }
    
    public static void loadPersistenceConfigurations() throws Exception {
        final String fileName = PersistenceInitializer.server_home + "/conf/Persistence/persistence-configurations.xml";
        final ConfigurationParser parser = new ConfigurationParser(fileName);
        loadPersistenceConfigurations(parser);
    }
    
    public static void loadPersistenceConfigurations(final ConfigurationParser parser) throws Exception {
        loadEnDecryptInstances();
        try {
            PersistenceInitializer.confNameVsValue.putAll(parser.getConfigurationValues());
            PersistenceInitializer.confNameVsProps.putAll(parser.getConfigurationProps());
            PersistenceInitializer.confNameVsList.putAll(parser.getConfigurationList());
            final Properties props = getConfigurationProps(getConfigurationValue("DBName"));
            if (!props.containsKey("dbadapter") || !props.containsKey("sqlgenerator")) {
                throw new IllegalArgumentException("dbadapter/sqlgenerator entries are missing. You may have mentioned replace=\"true\" in extended configuration file and missed dbadapter/sqlgenerator entries in it");
            }
            decryptPersistenceConfigurationParams();
            if (PersistenceInitializer.confNameVsValue.get("onSAS") == null) {
                PersistenceInitializer.confNameVsValue.put("onSAS", "false");
            }
            if (PersistenceInitializer.confNameVsValue.get("segmented") == null) {
                PersistenceInitializer.confNameVsValue.put("segmented", "false");
            }
            if (PersistenceInitializer.confNameVsValue.get("ReadOnly") == null) {
                PersistenceInitializer.confNameVsValue.put("ReadOnly", "false");
            }
            if (Configuration.getString("StartDBServer") == null || Configuration.getString("StartDBServer").equals("")) {
                Configuration.setString("StartDBServer", (String)PersistenceInitializer.confNameVsValue.get("StartDBServer"));
            }
        }
        catch (final Exception e) {
            PersistenceInitializer.OUT.log(Level.WARNING, "Problem while reading persistence-configuration.xml", e);
        }
    }
    
    private static void loadEnDecryptInstances() throws Exception {
        try {
            final String defaultCryptClass = "com.zoho.framework.utils.crypto.EnDecryptAES256Impl";
            final String defaultAlgo = "aes256";
            String cryptAlgo = PersistenceInitializer.confNameVsValue.get("cryptAlgo");
            cryptAlgo = ((cryptAlgo == null) ? defaultAlgo : cryptAlgo);
            if (cryptAlgo.equals("des")) {
                throw new IllegalArgumentException("DES Algorithm not supported");
            }
            final Properties cryptClasses = PersistenceInitializer.confNameVsProps.get("cryptClass");
            String cryptClass = (cryptClasses == null) ? defaultCryptClass : cryptClasses.getProperty(cryptAlgo);
            if (null == cryptClass) {
                cryptClass = defaultCryptClass;
                cryptAlgo = defaultAlgo;
            }
            PersistenceInitializer.confNameVsValue.put("cryptAlgo", cryptAlgo);
            EnDecryptUtil.initializeEnDecryption();
            final Class<?> c = Thread.currentThread().getContextClassLoader().loadClass(cryptClass);
            final EnDecrypt cryptInstance = (EnDecrypt)c.newInstance();
            CryptoUtil.setEnDecryptInstance(cryptInstance);
        }
        catch (final Exception e) {
            PersistenceInitializer.OUT.log(Level.WARNING, "Problem while initializing EnDecrypt instances", e);
        }
    }
    
    private static boolean reInitDSPlugin() throws Exception {
        if (Configuration.getString("generate.dbparams", "false").equals("true") || Configuration.getString("ha.dbparams.updated", "false").equals("true")) {
            final String dbParamsFileName = getDBParamsFilePath();
            final Properties props = getDBProps(dbParamsFileName);
            PersistenceInitializer.dbProps.putAll(props);
            PersistenceInitializer.dsPlugIn.initialize(PersistenceInitializer.dbProps);
            PersistenceInitializer.ds = PersistenceInitializer.dsPlugIn.getDataSource();
            DataSourceManager.removeDataSource("RelationalAPI");
            DataSourceManager.addDataSource("RelationalAPI", PersistenceInitializer.ds, PersistenceInitializer.dbadapter);
            PersistenceInitializer.dbadapter.initialize(PersistenceInitializer.dbProps);
            final ArchiveAdapter archiveAdap = createArchiveAdapter(getConfigurationProps("archive"));
            final Properties confProps = new Properties();
            final String isStream = (PersistenceInitializer.confNameVsValue.get("StreamingResultSet") != null) ? PersistenceInitializer.confNameVsValue.get("StreamingResultSet") : "true";
            confProps.setProperty("StreamingResultSet", isStream);
            PersistenceInitializer.relationalapi = new RelationalAPI(PersistenceInitializer.dbadapter, PersistenceInitializer.ds, archiveAdap, confProps);
            Configuration.setString("generate.dbparams", "false");
            Configuration.setString("ha.dbparams.updated", "false");
            return true;
        }
        return false;
    }
    
    public static void startDB() throws Exception {
        startDB(PersistenceInitializer.dbadapter, PersistenceInitializer.dbProps, PersistenceInitializer.ds);
    }
    
    public static void startDB(DBAdapter dbAdapter, final Properties dbProperties, DataSource dataSource) throws Exception {
        final String startDB = getConfigurationValue("StartDBServer");
        if ((startDB != null && startDB.equalsIgnoreCase("true") && "true".equalsIgnoreCase(Configuration.getString("StartDBServer")) && dbProperties.getProperty("StartDBServer") == null) || Boolean.valueOf(dbProperties.getProperty("StartDBServer", "false"))) {
            if (!AbstractRestoreHandler.doRestoreSanityTesting()) {
                throw new RuntimeException("Error while previous restore. DB is inconsistent or corrupted");
            }
            final String dbHome = dbProperties.getProperty("db.home", Configuration.getString("db.home"));
            final File dbFolder = new File(dbHome).getAbsoluteFile();
            if (!dbFolder.exists() || !dbFolder.isDirectory()) {
                throw new PersistenceException("No DB is bundled with this product.");
            }
            String url = getTemplatesReplacedUrl(dbProperties);
            final String userName = dbProperties.getProperty("username");
            String password = dbProperties.getProperty("password");
            if (password == null || password.trim().length() == 0) {
                throw new IllegalArgumentException("Database password cannot be null / empty!!");
            }
            try {
                if (dbAdapter == null) {
                    dbAdapter = createDBAdapter(dbProperties);
                    dbAdapter.initialize(dbProperties);
                }
                dbAdapter.setUpDB(url, userName, password);
                if (reInitDSPlugin()) {
                    dataSource = DataSourceManager.getDataSource(dbProperties.getProperty("DSName"));
                    password = dbAdapter.getDBProps().getProperty("password");
                    dbProperties.setProperty("password", password);
                }
                url = getTemplatesReplacedUrl(dbProperties);
                final boolean createDB = dbAdapter.createDB(url, userName, password);
                if (dataSource == null) {
                    DataSourceManager.addDataSource(dbProperties);
                    dataSource = DataSourceManager.getDataSource(dbProperties.getProperty("DSName"));
                    dbAdapter = (DBAdapter)DataSourceManager.getDSAdapter(dbProperties.getProperty("DSName"));
                }
                if (!createDB) {
                    throw new RuntimeException("Database creation failed. Stopping the Server. Please refer logs for more information");
                }
                if (Configuration.getString("check.db.version", "true").equals("true")) {
                    final Connection con = dataSource.getConnection();
                    final boolean versionChk = dbAdapter.validateVersion(con);
                    try {
                        if (null != con) {
                            con.close();
                        }
                    }
                    catch (final Exception e) {
                        e.printStackTrace();
                    }
                    if (!versionChk) {
                        throw new RuntimeException("Version Check failed");
                    }
                }
            }
            catch (final Exception iex) {
                final String message = iex.getMessage();
                PersistenceInitializer.OUT.log(Level.SEVERE, message, iex);
                try {
                    dbAdapter.shutDownDB(url, userName, password);
                }
                catch (final Exception exc) {
                    final String excmessage = exc.getMessage();
                    PersistenceInitializer.OUT.log(Level.SEVERE, excmessage, exc);
                }
                printErrorMsgAndHalt(message);
            }
        }
        else {
            createDestinationDataBase(dbProperties);
            if (dataSource == null && dbAdapter == null) {
                DataSourceManager.addDataSource(dbProperties);
                dataSource = DataSourceManager.getDataSource(dbProperties.getProperty("DSName"));
                dbAdapter = (DBAdapter)DataSourceManager.getDSAdapter(dbProperties.getProperty("DSName"));
            }
            dbAdapter.checkDBStatus(getTemplatesReplacedUrl(dbProperties));
            if (Configuration.getString("check.db.version", "true").equals("true")) {
                Connection con2 = null;
                boolean versionChk2 = true;
                try {
                    con2 = dataSource.getConnection();
                    versionChk2 = dbAdapter.validateVersion(con2);
                }
                catch (final SQLException sqle) {
                    dbAdapter.handleSQLException(sqle, con2, false);
                    throw sqle;
                }
                finally {
                    try {
                        if (con2 != null) {
                            con2.close();
                        }
                    }
                    catch (final Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if (!versionChk2) {
                    printErrorMsgAndHalt("Version Check failed");
                }
            }
            if (!AbstractRestoreHandler.doRestoreSanityTesting()) {
                throw new RuntimeException("Error while previous restore. DB is inconsistent or corrupted");
            }
        }
    }
    
    private static String getTemplatesReplacedUrl(final Properties dbProperties) {
        String url = dbProperties.getProperty("url");
        String propValue = dbProperties.getProperty("host", "localhost");
        if (propValue != null) {
            url = url.replaceAll("\\$host", propValue);
        }
        propValue = dbProperties.getProperty("port");
        if (propValue != null) {
            url = url.replaceAll("\\$port", propValue);
        }
        propValue = dbProperties.getProperty("schemaname");
        if (propValue != null) {
            url = url.replaceAll("\\$dbName", propValue);
        }
        PersistenceInitializer.OUT.info("Template replaced url :: " + url);
        return url;
    }
    
    public static void stopDB() throws Exception {
        if (RelationalAPI.getInstance() != null) {
            stopDB(PersistenceInitializer.dbProps, RelationalAPI.getInstance().getDBAdapter());
        }
        else {
            PersistenceInitializer.OUT.info("RelationalAPI is not initialized yet..call to stopDB() fails");
        }
    }
    
    public static void stopDB(final Properties dbProperties, final DBAdapter dbAdapter) throws Exception {
        if ((getConfigurationValue("StartDBServer").equalsIgnoreCase("true") && "true".equalsIgnoreCase(Configuration.getString("StartDBServer"))) || Boolean.valueOf(dbProperties.getProperty("StartDBServer", "false"))) {
            PersistenceInitializer.OUT.info("Going to stop the DBServer");
            final String url = getTemplatesReplacedUrl(dbProperties);
            final String userName = dbProperties.getProperty("username");
            final String password = (dbProperties.getProperty("password") != null) ? dbProperties.getProperty("password") : "";
            dbAdapter.shutDownDB(url, userName, password);
        }
    }
    
    private static void printErrorMsgAndHalt(final String msg) throws Exception {
        PersistenceInitializer.OUT.log(Level.SEVERE, "Problem while starting Database due to:" + msg);
        ConsoleOut.println(msg);
        throw new RuntimeException(msg);
    }
    
    public static boolean isColdStart() {
        return PersistenceInitializer.coldStart;
    }
    
    public static boolean onSAS() {
        return PersistenceInitializer.onSAS;
    }
    
    public static void loadModule(final String moduleName, final boolean loadDVH) throws Exception {
        final String confFileDir = new File(PersistenceInitializer.server_home + "/conf/" + moduleName).getCanonicalPath();
        loadModule(moduleName, loadDVH, confFileDir);
    }
    
    public static void loadModule(final String moduleName, final boolean loadDVH, final boolean loadPersonality) throws Exception {
        final String confFileDir = new File(PersistenceInitializer.server_home + "/conf/" + moduleName).getCanonicalPath();
        loadModule(moduleName, loadDVH, confFileDir, loadPersonality);
    }
    
    public static void loadModule(final String moduleName) throws Exception {
        loadModule(moduleName, true);
    }
    
    public static void loadModule(final String moduleName, final String moduleDir) throws Exception {
        loadModule(moduleName, true, moduleDir);
    }
    
    @Deprecated
    public static void loadModule(final String moduleName, final boolean loadDVH, final String moduleDir) throws Exception {
        loadModule(moduleName, loadDVH, moduleDir, true);
    }
    
    public static void loadModule(final String moduleName, final boolean loadDVH, final String moduleDir, final boolean loadPersonality) throws Exception {
        if (!onSAS()) {
            DataDictionary dd = null;
            if (MetaDataUtil.getDataDictionary(moduleName) == null && (moduleName.equals("Persistence") || SchemaBrowserUtil.isDDExists(moduleName))) {
                dd = SchemaBrowserUtil.getDataDictionary(moduleName);
                PersistenceInitializer.OUT.log(Level.INFO, "loaded the module dd :: {0}", moduleName);
            }
            if (dd != null) {
                MetaDataUtil.addDataDictionaryConfiguration(dd);
            }
        }
        if (isColdStart() && onSAS()) {
            addModule(moduleName, moduleDir, null);
        }
        else if (onSAS() && !isColdStart()) {
            loadDD(moduleName, moduleDir);
        }
        if (!isColdStart() || !onSAS()) {
            if (loadDVH) {
                loadDVHConf(moduleDir);
            }
            if (loadPersonality) {
                loadPersonality(moduleName, moduleDir);
            }
        }
    }
    
    public static void addModule(final String moduleName) throws Exception {
        final File module = new File(PersistenceInitializer.server_home + "/conf/" + moduleName);
        if (module.isDirectory()) {
            final String confFileDir = module.getCanonicalPath();
            addModule(moduleName, confFileDir, null);
            return;
        }
        throw new Exception(moduleName + " Module does not exsist in conf directory");
    }
    
    public static void addModule(final String moduleName, final String existingModule) throws Exception {
        final String confFileDir = new File(PersistenceInitializer.server_home + "/conf/" + moduleName).getCanonicalPath();
        addModule(moduleName, confFileDir, existingModule);
    }
    
    private static void addModule(final String moduleName, final String moduleDir, final String existingModule) throws Exception {
        addEntryInModuleTable(moduleName, existingModule);
        MetaDataUtil.loadSchemaTemplates(moduleName);
        if (!ConcurrentStartupUtil.isConcurrentTableCreation()) {
            loadDBSchema(moduleDir);
            loadDD(moduleName, moduleDir);
        }
        loadDVHConf(moduleDir);
        loadPersonality(moduleName, moduleDir);
        try {
            ConfPopulator.populate(moduleDir, moduleName);
        }
        catch (final Exception e) {
            PersistenceInitializer.OUT.log(Level.INFO, "Exception while populating Module {0} :: {1} ", new Object[] { moduleName, e });
            throw e;
        }
    }
    
    protected static void addEntryInModuleTable(final String moduleName, final String existingModule) throws Exception {
        if (getModuleId(moduleName) != null) {
            return;
        }
        if (existingModule == null) {
            final SelectQuery sq = new SelectQueryImpl(Table.getTable("Module"));
            sq.addSelectColumn(Column.getColumn(null, "*"));
            sq.addSortColumn(new SortColumn(Column.getColumn("Module", "MODULEORDER"), false));
            final DataObject moduleDO = DataAccess.get(sq);
            final Iterator iterator = moduleDO.getRows("Module", new Criteria(Column.getColumn("Module", "MODULENAME"), moduleName, 0));
            if (!iterator.hasNext()) {
                final Row row = new Row("Module");
                row.set("MODULENAME", moduleName);
                row.set("MODULEORDER", new Integer(new Integer(moduleDO.getRow("Module").get("MODULEORDER").toString()) + 10));
                moduleDO.addRow(row);
                DataAccess.update(moduleDO);
                PersistenceInitializer.moduleNameVsModuleID.put(moduleName, row.get(1));
            }
        }
        else {
            final SelectQuery sq = new SelectQueryImpl(Table.getTable("Module"));
            sq.addSelectColumn(Column.getColumn(null, "*"));
            final DataObject moduleDO = DataAccess.get(sq);
            final Iterator iterator = moduleDO.getRows("Module", new Criteria(Column.getColumn("Module", "MODULENAME"), moduleName, 0));
            if (!iterator.hasNext()) {
                final Row existingRow = moduleDO.getRow("Module", new Criteria(Column.getColumn("Module", "MODULENAME"), existingModule, 0));
                if (existingRow == null) {
                    throw new IllegalArgumentException("Module " + existingModule + " not found when adding [" + moduleName + "] after this.");
                }
                final UpdateQuery uq = new UpdateQueryImpl("Module");
                final Column c = Column.createOperation(com.adventnet.ds.query.Operation.operationType.ADD, Column.getColumn("Module", "MODULEORDER"), 10);
                c.setDataType("INTEGER");
                uq.setUpdateColumn("MODULEORDER", c);
                uq.setCriteria(new Criteria(Column.getColumn("Module", "MODULEORDER"), existingRow.get("MODULEORDER"), 5));
                DataAccess.update(uq);
                final DataObject newDO = new WritableDataObject();
                final Row row2 = new Row("Module");
                row2.set("MODULENAME", moduleName);
                row2.set("MODULEORDER", new Integer(new Integer(existingRow.get("MODULEORDER").toString()) + 10));
                newDO.addRow(row2);
                DataAccess.add(newDO);
                PersistenceInitializer.moduleNameVsModuleID.put(moduleName, row2.get(1));
            }
        }
    }
    
    private static String getDBSpecificSQL(final String sql) {
        final String dbName = getConfigurationValue("DBName");
        if (dbName.equalsIgnoreCase("postgres") || dbName.equalsIgnoreCase("firebird")) {
            return sql.replaceAll("`", "\"");
        }
        if (dbName.equalsIgnoreCase("oracle") || dbName.equalsIgnoreCase("mssql")) {
            return sql.replaceAll("`", "");
        }
        return sql;
    }
    
    public static void loadBeans(final String confFileDir) throws Exception {
        final String beanFileName = confFileDir + File.separator + "bean.xml";
        final File beanFile = new File(beanFileName);
        PersistenceInitializer.OUT.info("Loading the bean.xml File :: " + beanFile);
        if (beanFile.exists()) {
            final DataObject beanDO = Xml2DoConverter.transform(beanFile.toURL());
            BeanUtil.addBeans(beanDO);
        }
    }
    
    public static void loadServices(final String confFileDir) throws Exception {
        final String serviceFileName = confFileDir + File.separator + "service.xml";
        final File serviceFile = new File(serviceFileName);
        PersistenceInitializer.OUT.info("Loading the sevice.xml File :: " + serviceFile);
        if (serviceFile.exists()) {
            final DataObject serviceDO = Xml2DoConverter.transform(serviceFile.toURL());
            ServiceUtil.addServices(serviceDO);
        }
    }
    
    public static int getIsolationLevel(final String value) {
        if (value.trim().equalsIgnoreCase("TRANSACTION_READ_COMMITTED")) {
            return 2;
        }
        if (value.trim().equalsIgnoreCase("TRANSACTION_READ_UNCOMMITTED")) {
            return 1;
        }
        if (value.trim().equalsIgnoreCase("TRANSACTION_REPEATABLE_READ")) {
            return 4;
        }
        if (value.trim().equalsIgnoreCase("TRANSACTION_SERIALIZABLE")) {
            return 8;
        }
        return 2;
    }
    
    public static RelationalAPI getRelAPI() {
        return PersistenceInitializer.relationalapi;
    }
    
    public static void loadDBSchema(final String moduleDir) throws Exception {
        final String dbName = PersistenceInitializer.confNameVsValue.get("DBName");
        final String schemaFileName = moduleDir + "/" + dbName + "/DatabaseSchema.conf";
        final File schemaFile = new File(schemaFileName);
        if (schemaFile.exists()) {
            InputStream stream = null;
            try {
                PersistenceInitializer.OUT.info("schemaFileName :: " + schemaFileName);
                stream = new FileInputStream(schemaFile);
                PersistenceInitializer.createSchema.createSchemas(true, false, stream);
            }
            finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }
    }
    
    protected static DataDictionary loadDDintoMetada(final String moduleName, final String moduleDir, final File ddFile) throws Exception {
        return loadDDintoMetada(moduleName, moduleDir, ddFile.toURL());
    }
    
    public static DataDictionary loadDDintoMetada(final String moduleName, final String moduleDir, final URL ddURL) throws Exception {
        DataDictionary dataDictionary = null;
        DataAccess.getTransactionManager().begin();
        try {
            dataDictionary = MetaDataAccess.loadDataDictionary(ddURL, false);
            if (!moduleName.equals(dataDictionary.getName())) {
                throw new DataAccessException("Module Name :: {" + moduleName + "} and the DataDictionary Name :: {" + dataDictionary.getName() + "} should be same");
            }
            DataAccess.getTransactionManager().commit();
        }
        catch (final Exception e) {
            PersistenceInitializer.OUT.log(Level.INFO, "Exception while loading DataDictionary {0} :: {1} ", new Object[] { moduleName, e });
            DataAccess.getTransactionManager().rollback();
            throw e;
        }
        return dataDictionary;
    }
    
    public static DataDictionary loadDD(final String moduleName, final String moduleDir) throws Exception {
        return loadDD(moduleName, moduleDir, true);
    }
    
    public static DataDictionary loadDD(final String moduleName, final String moduleDir, final boolean createTable) throws Exception {
        DataDictionary dataDictionary = null;
        PersistenceInitializer.OUT.log(Level.FINEST, "DD loading for " + moduleDir);
        String ddFileName = moduleDir + "/dd-files.xml";
        File ddFile = new File(ddFileName);
        if (!ddFile.exists()) {
            ddFileName = moduleDir + "/data-dictionary.xml";
            ddFile = new File(ddFileName);
        }
        if (ddFile.exists()) {
            dataDictionary = loadDDintoMetada(moduleName, moduleDir, ddFile);
            if (createTable) {
                DataAccess.createTables(moduleName);
            }
        }
        return dataDictionary;
    }
    
    public static void loadPersonality(final String moduleName, final String moduleDir) throws Exception {
        final String pcFileName = moduleDir + "/personality-configuration.xml";
        final File pcFile = new File(pcFileName);
        if (pcFile.exists()) {
            final DataObject pcData = PersonalityConfigurationUtil.initializePersonalityConfiguration(moduleName, pcFile.toURL());
            if (onSAS()) {
                final List<String> idxTabNames = new ArrayList<String>();
                final Iterator<Row> idxTbIterator = pcData.getRows("TableDetails");
                while (idxTbIterator.hasNext()) {
                    final Row row = idxTbIterator.next();
                    final String tableName = (String)row.get("TABLE_NAME");
                    idxTabNames.add(tableName);
                }
                if (isColdStart()) {
                    DataAccess.createTables(idxTabNames);
                }
            }
        }
    }
    
    public static void loadDVHConf(final String moduleDir) throws Exception {
        PersistenceInitializer.OUT.log(Level.FINEST, "In loadDVHConf input conf dir {0}", moduleDir);
        final File file = new File(moduleDir + "/dynamic-value-handlers.xml");
        if (file.exists()) {
            PersistenceInitializer.OUT.log(Level.FINEST, "DynamicValueHandler File exists!");
            final URL url = file.toURL();
            final DynamicValueHandlerRepositry rep = new DynamicValueHandlerRepositry();
            rep.parse(url);
            PersistenceInitializer.OUT.log(Level.FINEST, "Successfully parsed the DynamicValueHandler.xml file");
        }
        else {
            PersistenceInitializer.OUT.log(Level.FINEST, "DynamicValueHandler File does not exist!");
        }
    }
    
    public static String getConfigurationValue(final String confName) {
        return PersistenceInitializer.confNameVsValue.get(confName);
    }
    
    public static Properties getConfigurationProps(final String confName) {
        final Properties props = PersistenceInitializer.confNameVsProps.get(confName);
        if (props != null) {
            final Properties retProps = new Properties();
            retProps.putAll(props);
            final String versionHandler = getConfigurationValue("VersionHandler");
            if (versionHandler != null) {
                retProps.setProperty("VersionHandler", versionHandler);
            }
            final String appendQuote = getConfigurationValue("AUTO_QUOTE_IDENTIFIERS");
            retProps.setProperty("AUTO_QUOTE_IDENTIFIERS", (appendQuote != null) ? appendQuote : "false");
            return retProps;
        }
        return null;
    }
    
    public static Properties getProps(final String confName) {
        final Properties props = PersistenceInitializer.confNameVsProps.get(confName);
        if (props != null) {
            final Properties retProps = new Properties();
            retProps.putAll(props);
            return retProps;
        }
        return null;
    }
    
    public static List getConfigurationList(final String confName) {
        final List list = PersistenceInitializer.confNameVsList.get(confName);
        if (list == null) {
            return new ArrayList();
        }
        return new ArrayList(list);
    }
    
    public static boolean isMDS() {
        return PersistenceInitializer.isMDS;
    }
    
    public static DataObject getDefaultDSDO() throws Exception {
        if (PersistenceInitializer.defaultDSDO == null) {
            final Properties databaseProps = new Properties();
            databaseProps.putAll(PersistenceInitializer.dbProps);
            final String defDSAdap = databaseProps.getProperty("DefaultDSAdapter");
            if (defDSAdap != null) {
                databaseProps.putAll(getConfigurationProps(defDSAdap));
            }
            PersistenceInitializer.defaultDSDO = getDataSourceDO(databaseProps);
        }
        return PersistenceInitializer.defaultDSDO;
    }
    
    public static Properties getDefaultDBProps() {
        final Properties props = new Properties();
        props.putAll(PersistenceInitializer.dbProps);
        final String defDSAdap = props.getProperty("DefaultDSAdapter");
        if (defDSAdap != null) {
            props.putAll(getConfigurationProps(defDSAdap));
        }
        return props;
    }
    
    public static DataSource createDataSource(final Properties props) throws Exception {
        final String className = PersistenceInitializer.dsPlugIn.getDataSourcePlugInImplClass();
        final DataSourcePlugIn dsPlug = (DataSourcePlugIn)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
        dsPlug.initialize(props);
        return dsPlug.getDataSource();
    }
    
    public static DataSourcePlugIn createDataSourcePlugIn(final String plugInClassName, final Properties dsProps) throws Exception {
        final DataSourcePlugIn tempDSPlugIn = (DataSourcePlugIn)Thread.currentThread().getContextClassLoader().loadClass(plugInClassName).newInstance();
        tempDSPlugIn.initialize(dsProps);
        return tempDSPlugIn;
    }
    
    public static DataSourcePlugIn createDataSourcePlugIn(final Properties props) throws Exception {
        final String className = PersistenceInitializer.dsPlugIn.getDataSourcePlugInImplClass();
        final DataSourcePlugIn dsPlug = (DataSourcePlugIn)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
        dsPlug.initialize(props);
        return dsPlug;
    }
    
    public static void removeModule(final String moduleName) throws Exception {
        removeModule(moduleName, true);
    }
    
    public static void removeModule(final String moduleName, final boolean validate) throws Exception {
        final DataDictionary dd = MetaDataUtil.getDataDictionary(moduleName);
        if (dd == null) {
            throw new DataAccessException("No such module with the name : " + moduleName);
        }
        if (validate) {
            final List tableDefns = dd.getTableDefinitions();
            for (int size = tableDefns.size(), i = 0; i < size; ++i) {
                final TableDefinition tabDef = tableDefns.get(i);
                final String masterTableName = tabDef.getTableName();
                final List slaveTableNames = MetaDataUtil.getSlaveTableNames(masterTableName);
                for (int j = 0; j < slaveTableNames.size(); ++j) {
                    final String slaveTableName = slaveTableNames.get(j);
                    final TableDefinition slaveTableDef = MetaDataUtil.getTableDefinitionByName(slaveTableName);
                    final String slaveModuleName = slaveTableDef.getModuleName();
                    if (!slaveModuleName.equals(moduleName)) {
                        throw new IllegalArgumentException("Module [" + moduleName + "] cannot be removed because table [" + masterTableName + "] was refered by [" + slaveTableName + "] in module [" + slaveModuleName + "]");
                    }
                }
            }
        }
        DataAccess.getTransactionManager().begin();
        try {
            PersonalityConfigurationUtil.removePersonalityConfiguration(moduleName, PersistenceInitializer.onSAS);
            final String confFileDir = new File(Configuration.getString("server.home") + File.separator + "conf" + File.separator + moduleName).getCanonicalPath();
            final String confFileName = "conf-files.xml";
            final File confFile = new File(confFileDir + File.separator + confFileName);
            if (confFile.exists()) {
                final DataObject dataObject = Xml2DoConverter.transform(confFile.getAbsolutePath());
                if (!dataObject.isEmpty()) {
                    DataAccess.fillGeneratedValues(dataObject);
                    dataObject.sortRows("ConfFile", new SortColumn(Column.getColumn("ConfFile", "FILEID"), false));
                    final Iterator iterator = dataObject.getRows("ConfFile");
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        String fileToBeDeleted = (String)row.get("URL");
                        fileToBeDeleted = PersistenceInitializer.server_home + fileToBeDeleted.substring(fileToBeDeleted.indexOf("/conf"));
                        removeXml(fileToBeDeleted, moduleName);
                    }
                }
            }
            DataAccess.delete(new Criteria(Column.getColumn("Module", "MODULENAME"), moduleName, 0));
            DataAccess.delete(new Criteria(Column.getColumn("ConfFile", "URL"), "*/conf/" + moduleName + "/*", 2));
            DataAccess.getTransactionManager().commit();
            DataAccess.dropTables(moduleName);
        }
        catch (final Exception e) {
            PersistenceInitializer.OUT.log(Level.INFO, "Exception while removing Module {0} :: {1} ", new Object[] { moduleName, e });
            DataAccess.getTransactionManager().rollback();
            throw e;
        }
        if (PersistenceInitializer.moduleNameVsModuleID != null) {
            PersistenceInitializer.moduleNameVsModuleID.remove(moduleName);
        }
    }
    
    @Deprecated
    public static HashMap getPatternValues(final String urlLocation) throws Exception {
        return SchemaBrowserUtil.getPatternValues(urlLocation);
    }
    
    public static void removeXml(final String fileToBeDeleted) throws Exception {
        removeXml(fileToBeDeleted, null);
    }
    
    private static void removeXml(final String fileToBeDeleted, final String moduleName) throws Exception {
        DataDictionary dd = null;
        if (moduleName != null) {
            dd = MetaDataUtil.getDataDictionary(moduleName);
        }
        final String realFile = fileToBeDeleted.substring(fileToBeDeleted.indexOf("/conf"));
        final Map patternValues = getPatternValues(realFile);
        final DataObject data = Xml2DoConverter.transform(new File(new File(fileToBeDeleted).getAbsolutePath()).toURL(), true, patternValues);
        final WritableDataObject newDO = (WritableDataObject)data.clone();
        newDO.clearOperations();
        final List tableNames = PersistenceUtil.sortTables(newDO.getTableNames());
        for (int i = tableNames.size() - 1; i >= 0; --i) {
            final String tableName = tableNames.get(i);
            if (dd == null || dd.getTableDefinitionByName(tableName) == null) {
                final Iterator iterator = data.getRows(tableName);
                while (iterator.hasNext()) {
                    newDO.deleteRow(iterator.next());
                }
            }
        }
        DataAccess.update(newDO);
        DataAccess.delete("ConfFile", new Criteria(Column.getColumn("ConfFile", "URL"), "*" + realFile, 2));
    }
    
    public static Object getData(final String selectSQL, final int fetchIndex) throws Exception {
        Connection c = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            c = PersistenceInitializer.ds.getConnection();
            stmt = c.createStatement();
            rs = stmt.executeQuery(selectSQL);
            if (rs.next()) {
                final Object ret = rs.getObject(fetchIndex);
                return ret;
            }
            return null;
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final Exception ex) {}
            }
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (final Exception ex2) {}
            }
            if (c != null) {
                try {
                    c.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static Long getModuleId(final String moduleName) throws DataAccessException {
        if (PersistenceInitializer.moduleNameVsModuleID == null) {
            PersistenceInitializer.moduleNameVsModuleID = new HashMap();
            final DataObject moduleDO = DataAccess.get("Module", (Criteria)null);
            final Iterator iterator = moduleDO.getRows("Module");
            while (iterator.hasNext()) {
                final Row moduleRow = iterator.next();
                PersistenceInitializer.moduleNameVsModuleID.put(moduleRow.get("MODULENAME"), moduleRow.get(1));
            }
        }
        return PersistenceInitializer.moduleNameVsModuleID.get(moduleName);
    }
    
    public static void initializeMickey(final boolean loadDVH) throws Exception {
        initializeMickey(loadDVH, true);
    }
    
    public static void initializeMickey(final boolean loadDVH, final boolean loadPersonality) throws Exception {
        final String confFileDir = PersistenceInitializer.server_home + File.separator + "conf" + File.separator;
        initializePersistence();
        final ArrayList<String> moduleNames = (ArrayList)getAllModulesFromDB();
        for (final String moduleName : moduleNames) {
            loadModule(moduleName, loadDVH, loadPersonality);
        }
    }
    
    private static List<String> getAllModulesFromDB() {
        final SelectQuery sq = new SelectQueryImpl(Table.getTable("Module"));
        sq.addSelectColumn(Column.getColumn(null, "*"));
        sq.addSortColumn(new SortColumn(Column.getColumn("Module", "MODULEORDER"), true));
        final ArrayList<String> moduleNames = new ArrayList<String>();
        try {
            final DataObject moduleDO = DataAccess.get(sq);
            final Iterator iterator = moduleDO.getRows("Module", (Criteria)null);
            while (iterator.hasNext()) {
                final Row curRow = iterator.next();
                moduleNames.add((String)curRow.get("MODULENAME"));
            }
        }
        catch (final DataAccessException dae) {
            dae.printStackTrace();
        }
        return moduleNames;
    }
    
    protected static void createDestinationDataBase(final Properties props) throws Exception {
        PersistenceInitializer.OUT.fine("Destination DB props :: " + props);
        if (props.getProperty("create.db", "false").equalsIgnoreCase("false")) {
            PersistenceInitializer.OUT.info("create.db is :: " + props.getProperty("create.db"));
            return;
        }
        final Properties prop = new Properties();
        prop.putAll(props);
        DataSource dataSource = null;
        Connection conn = null;
        final String dummyDsName = "Dummy";
        boolean isDSAdded = Boolean.FALSE;
        try {
            final DBAdapter adapter = createDBAdapter(prop);
            final Map urlProps = adapter.splitConnectionURL(props.getProperty("url"));
            PersistenceInitializer.OUT.fine("Connection properties :: " + urlProps);
            final String defaultDBName = urlProps.get("DBName");
            final String databaseURL = props.getProperty("url");
            String defaultURL = "";
            if (databaseURL.contains(";databaseName=" + defaultDBName)) {
                defaultURL = databaseURL.replaceFirst(";databaseName=" + defaultDBName, "");
            }
            else {
                final StringBuilder dburlBuilder = new StringBuilder(databaseURL);
                final int ind = databaseURL.contains("?") ? databaseURL.indexOf("?") : (databaseURL.contains(";") ? databaseURL.indexOf(";") : databaseURL.length());
                if (props.getProperty("dest.db.type") != null && props.getProperty("dest.db.type").equals("postgres")) {
                    dburlBuilder.replace(databaseURL.lastIndexOf("/") + 1, ind, "postgres");
                }
                else {
                    dburlBuilder.replace(databaseURL.lastIndexOf("/") + 1, ind, "");
                }
                defaultURL = dburlBuilder.toString();
            }
            ((Hashtable<String, String>)prop).put("url", defaultURL);
            ((Hashtable<String, String>)prop).put("DSName", dummyDsName);
            PersistenceInitializer.OUT.fine("DefaultDB props :: " + prop);
            DataSourceManager.addDataSource(prop);
            isDSAdded = Boolean.TRUE;
            dataSource = DataSourceManager.getDataSource(prop.getProperty("DSName"));
            conn = dataSource.getConnection();
            final String dbName = prop.getProperty("dest.create.db.name", urlProps.get("DBName"));
            PersistenceInitializer.OUT.info("Going to create new database " + dbName + " in destination database.");
            adapter.createDB(conn, dbName, prop.getProperty("username"), prop.getProperty("password"), true);
            if (!dbName.equals(urlProps.get("DBName"))) {
                final String replacedUrl = props.getProperty("url").replace(urlProps.get("DBName"), dbName);
                ((Hashtable<String, String>)props).put("url", replacedUrl);
            }
        }
        catch (final Exception e) {
            PersistenceInitializer.OUT.severe("Exception occured while creating destination DB :( ");
            PersistenceInitializer.OUT.severe(e.getMessage());
            throw e;
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
            if (isDSAdded) {
                PersistenceInitializer.OUT.warning("Removing datasource ::: " + dummyDsName);
                DataSourceManager.removeDataSource(dummyDsName);
            }
        }
    }
    
    public static void resetMickey(final boolean unloadPersonality) throws Exception {
        resetMickey(Configuration.getString("server.home"), unloadPersonality);
    }
    
    public static void resetMickey(final String productHome, final boolean unloadPersonality) throws Exception {
        final String conf_dir = productHome + File.separator + "conf";
        final String existingModuleXML = conf_dir + File.separator + "module.xml";
        final DataObject moduleDO = Xml2DoConverter.transform(existingModuleXML);
        final Iterator<Row> iterator = moduleDO.getRows("Module");
        final List<String> metaNames = new ArrayList<String>();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            metaNames.add((String)row.get(3));
        }
        if (unloadPersonality) {
            PersistenceInitializer.OUT.log(Level.INFO, "Unloading All Personalities");
            PersonalityConfigurationUtil.unloadAllPersonalities();
        }
        for (int i = metaNames.size() - 1; i >= 0; --i) {
            final String module_dir = conf_dir + File.separator + metaNames.get(i);
            final File module = new File(module_dir);
            final File ddfiles = new File(module_dir + File.separator + "dd-files.xml");
            final File ddxml = new File(module_dir + File.separator + "data-dictionary.xml");
            if (metaNames.get(i).equals("Persistence") || (module.isDirectory() && (ddfiles.exists() || ddxml.exists()))) {
                PersistenceInitializer.OUT.log(Level.INFO, "Unloading Module :: [{0}]", metaNames.get(i));
                MetaDataUtil.removeDataDictionaryConfiguration(metaNames.get(i));
            }
        }
        PersistenceInitializer.OUT.log(Level.INFO, "Unloading Module :: MetaPersistence");
        MetaDataUtil.removeDataDictionaryConfiguration("MetaPersistence");
        PersistenceInitializer.OUT.log(Level.INFO, "Unloading DataTypes");
        DataTypeManager.unload();
        PersistenceInitializer.OUT.log(Level.INFO, "Unloading custom attributes");
        MetaDataUtil.removeCustomAttributeConfigurations();
    }
    
    private static String getDBPassword(final Properties dbProps) throws PasswordException, PersistenceException {
        final Properties props = new Properties();
        props.putAll(dbProps);
        final String password = PersistenceUtil.getDBPasswordProvider().getPassword(props);
        return password;
    }
    
    static {
        OUT = Logger.getLogger(PersistenceInitializer.class.getName());
        PersistenceInitializer.relationalapi = null;
        PersistenceInitializer.isInitialized = false;
        PersistenceInitializer.confNameVsValue = new HashMap<String, String>();
        PersistenceInitializer.confNameVsList = new HashMap<String, List<String>>();
        PersistenceInitializer.confNameVsProps = new HashMap<String, Properties>();
        PersistenceInitializer.dbProps = null;
        PersistenceInitializer.defaultDSDO = null;
        PersistenceInitializer.createSchema = null;
        PersistenceInitializer.dsPlugIn = null;
        PersistenceInitializer.onSAS = false;
        PersistenceInitializer.isMDS = false;
        PersistenceInitializer.dbadapter = null;
        PersistenceInitializer.ds = null;
        PersistenceInitializer.coldStart = false;
        PersistenceInitializer.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        PersistenceInitializer.databases = new ArrayList<String>();
        PersistenceInitializer.dbConfigCount = 0;
        (ERRORCODETABLENAMES = new ArrayList<String>()).add("AdventNetErrorCode");
        PersistenceInitializer.ERRORCODETABLENAMES.add("MySQLErrorCode");
        PersistenceInitializer.ERRORCODETABLENAMES.add("PgSQLErrorCode");
        PersistenceInitializer.ERRORCODETABLENAMES.add("MsSQLErrorCode");
        PersistenceInitializer.ERRORCODETABLENAMES.add("OracleErrorCode");
        PersistenceInitializer.preprocess = null;
        PersistenceInitializer.moduleNameVsModuleID = null;
    }
}
