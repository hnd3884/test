package com.adventnet.db.schema.analyze;

import java.util.Hashtable;
import com.zoho.conf.Configuration;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import com.adventnet.ds.query.DataSet;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import com.zoho.db.model.DataBaseBuilder;
import java.sql.Connection;
import com.adventnet.persistence.PersistenceException;
import com.zoho.mickey.exception.PasswordException;
import com.adventnet.persistence.PersistenceUtil;
import java.util.Locale;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.parser.DataDictionaryParser;
import java.net.URL;
import com.adventnet.persistence.SchemaBrowserUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.DataSourceManager;
import org.json.JSONArray;
import java.util.Map;
import java.util.Properties;
import com.adventnet.db.adapter.DBAdapter;
import javax.sql.DataSource;
import com.zoho.db.model.DataBase;
import java.util.logging.Logger;

public class SchemaAnalyzerUtil
{
    private static final Logger LOGGER;
    private static boolean isMickeyInitialized;
    private static SchemaComparatorConfiguration config;
    private static SchemaComparatorObject comparatorObj;
    private static DataBase destDB;
    private static DataBase srcDB;
    private static String serverPath;
    private static DataSource dataSource;
    private static DBAdapter dbAdapter;
    private static DBType srcDBType;
    private static DBType destDBType;
    
    public static void generateDiffForMetaDataVsDB() throws Exception {
        generateDiffForMetaDataVsDB(getConfiguration());
    }
    
    public static void generateDiffForMetaDataVsDB(final SchemaComparatorConfiguration configObj) throws Exception {
        Map<String, JSONArray> diff = null;
        try {
            diff = compare(null, null, SchemaComparator.ComparatorType.METADATA_VS_DATABASE, configObj);
            writeDiffToFile(diff);
        }
        catch (final Throwable exp) {
            SchemaAnalyzerUtil.LOGGER.severe("Exception occurred while generating diff:: " + exp.getMessage());
            exp.printStackTrace();
            throw exp;
        }
    }
    
    private static void initializeDataSource() {
        SchemaAnalyzerUtil.dataSource = DataSourceManager.getDataSource("default");
        SchemaAnalyzerUtil.dbAdapter = (DBAdapter)DataSourceManager.getDSAdapter("default");
        if (SchemaAnalyzerUtil.dataSource == null) {
            SchemaAnalyzerUtil.dataSource = DataSourceManager.getDataSource("RelationalAPI");
            SchemaAnalyzerUtil.dbAdapter = (DBAdapter)DataSourceManager.getDSAdapter("RelationalAPI");
        }
    }
    
    private static void addDestDBServerProps(final String destDB, final Properties props) throws Exception {
        final boolean startDestDB = getConfiguration().getConfigurationValueAsBoolean("start.dest." + destDB + ".server");
        props.setProperty("StartDBServer", String.valueOf(startDestDB));
        if (startDestDB) {
            final String dbHome = getConfiguration().getConfigurationValue("dest.db." + destDB + ".dir");
            if (dbHome == null) {
                throw new IllegalArgumentException("start.dest." + destDB + ".server configuration requires dest.db." + destDB + ".dir (data directory path)");
            }
            props.setProperty("db.home", dbHome);
        }
        props.setProperty("is.dest.db", "true");
    }
    
    public static void generateDiffForDBVsDB(final String destDB, final Properties destDBProps) throws Exception {
        generateDiffForDBVsDB(destDB, destDBProps, getConfiguration());
    }
    
    public static void generateDiffForDBVsDB(final String destDB, final Properties destDBProps, final SchemaComparatorConfiguration configObj) throws Exception {
        Map<String, JSONArray> diff = null;
        try {
            diff = compare(destDB, destDBProps, SchemaComparator.ComparatorType.DATABASE_VS_DATABASE, configObj);
            writeDiffToFile(diff);
        }
        catch (final Throwable exp) {
            SchemaAnalyzerUtil.LOGGER.severe("Exception occurred while generating diff:: " + exp.getMessage());
            exp.printStackTrace();
            throw exp;
        }
    }
    
    public static Map<String, JSONArray> compare() throws Exception {
        return compare(null, null, SchemaComparator.ComparatorType.METADATA_VS_DATABASE, getConfiguration());
    }
    
    public static Map<String, JSONArray> compare(final SchemaComparatorConfiguration configObj) throws Exception {
        return compare(null, null, SchemaComparator.ComparatorType.METADATA_VS_DATABASE, configObj);
    }
    
    public static Map<String, JSONArray> compare(final String destinationDBType, final Properties destdbProps) throws Exception {
        return compare(destinationDBType, destdbProps, SchemaComparator.ComparatorType.DATABASE_VS_DATABASE, getConfiguration());
    }
    
    public static Map<String, JSONArray> compare(final String destinationDBType, final Properties destdbProps, final SchemaComparatorConfiguration configObj) throws Exception {
        return compare(destinationDBType, destdbProps, SchemaComparator.ComparatorType.DATABASE_VS_DATABASE, configObj);
    }
    
    private static DBType initSrcDBType() {
        String sourceDB = PersistenceInitializer.getConfigurationValue("DBName");
        if (sourceDB != null && sourceDB.equalsIgnoreCase("mds")) {
            SchemaAnalyzerUtil.LOGGER.log(Level.INFO, "mds ::: {0}", PersistenceInitializer.getConfigurationProps("DataSourcePlugIn"));
            sourceDB = PersistenceInitializer.getConfigurationProps("DataSourcePlugIn").getProperty("DefaultDSAdapter");
        }
        setSrcDBType(sourceDB);
        return SchemaAnalyzerUtil.srcDBType;
    }
    
    protected static Map<String, JSONArray> compare(final String destinationDBType, final Properties destdbProps, final SchemaComparator.ComparatorType operation, final SchemaComparatorConfiguration configObj) throws Exception {
        DBAdapter destDBAdapter = null;
        final SchemaAnalyzer analyzer = new SchemaAnalyzer();
        try {
            SchemaAnalyzerUtil.config = configObj;
            System.setProperty("gen.db.password", "false");
            if (operation == SchemaComparator.ComparatorType.DATABASE_VS_DATABASE) {
                addDestDBServerProps(destinationDBType, destdbProps);
                setDestDBType(destinationDBType);
                SchemaAnalyzerUtil.LOGGER.log(Level.INFO, " Latest Db Props :{0}", destdbProps);
            }
            SchemaAnalyzerUtil.LOGGER.log(Level.INFO, "initialize mickey in schema analyser");
            initializeSetup();
            initializeDataSource();
            final DBType srcDBType = initSrcDBType();
            SchemaAnalyzerUtil.LOGGER.log(Level.INFO, "sourceDB ::: {0}", srcDBType);
            SchemaAnalyzerUtil.LOGGER.log(Level.INFO, "destDB ::: {0}", destinationDBType);
            final SchemaComparatorHandler handler = SchemaAnalyzerUtil.config.getSchemaComparatorHandler();
            for (final SchemaComparatorPrePostHandler prePostHandler : SchemaAnalyzerUtil.config.getPrePostHandlers()) {
                prePostHandler.preHandle();
            }
            if (operation.equals(SchemaComparator.ComparatorType.DATABASE_VS_DATABASE)) {
                SchemaAnalyzerUtil.LOGGER.log(Level.INFO, "Destination DB initialize begin");
                final DBType destDBType = getDBType(destinationDBType);
                updateEncryptedPassword(destdbProps);
                destdbProps.setProperty("DSName", destinationDBType);
                destdbProps.putAll(PersistenceInitializer.getConfigurationProps(destinationDBType));
                PersistenceInitializer.startDB(null, destdbProps, null);
                ((Hashtable<String, String>)destdbProps).put("shutdown.db", "true");
                final DataSource destDataSource = DataSourceManager.getDataSource(destinationDBType);
                destDBAdapter = (DBAdapter)DataSourceManager.getDSAdapter(destinationDBType);
                SchemaAnalyzerUtil.LOGGER.log(Level.INFO, "Db props", destdbProps);
                SchemaAnalyzerUtil.comparatorObj = SchemaComparatorBuilder.config().setSrcDataSource(SchemaAnalyzerUtil.dataSource).setDestDataSource(destDataSource).setSrcDBAdapter(SchemaAnalyzerUtil.dbAdapter).setDestDBAdapter(destDBAdapter).withSrcDBType(srcDBType).withDestDBType(destDBType).usingSchemaComparatorHandler(handler).withComparatorType(SchemaComparator.ComparatorType.DATABASE_VS_DATABASE).build();
            }
            else {
                SchemaAnalyzerUtil.comparatorObj = SchemaComparatorBuilder.config().setSrcDataSource(SchemaAnalyzerUtil.dataSource).setSrcDBAdapter(SchemaAnalyzerUtil.dbAdapter).withSrcDBType(srcDBType).usingSchemaComparatorHandler(handler).withComparatorType(SchemaComparator.ComparatorType.METADATA_VS_DATABASE).build();
            }
            final Map<String, JSONArray> diff = analyzer.analyzeSchema(SchemaAnalyzerUtil.comparatorObj);
            for (final SchemaComparatorPrePostHandler prePostHandler : SchemaAnalyzerUtil.config.getPrePostHandlers()) {
                prePostHandler.postHandle();
            }
            return diff;
        }
        finally {
            try {
                PersistenceInitializer.stopDB();
                if (destdbProps != null && destdbProps.getProperty("StartDBServer") != null && destdbProps.getProperty("StartDBServer").equals("true")) {
                    PersistenceInitializer.stopDB(destdbProps, destDBAdapter);
                }
            }
            catch (final Throwable t) {
                SchemaAnalyzerUtil.LOGGER.severe("Exception occurred :: " + t.getMessage());
                t.printStackTrace();
            }
        }
    }
    
    public static void writeDiffToFile(final Map<String, JSONArray> diff) throws Exception {
        BufferedWriter buf = null;
        final BufferedWriter diffbuf = null;
        try {
            String dirPath = getConfiguration().getConfigurationValue("diff.file.dir.path");
            if (dirPath == null) {
                dirPath = SchemaAnalyzerUtil.serverPath + File.separator + "logs" + File.separator + "db_difftool";
            }
            final File diffFile = new File(dirPath + File.separator + "diff.txt");
            if (diff.size() > 0) {
                SchemaAnalyzerUtil.LOGGER.info("Going to write diff.txt in the location:: " + dirPath + File.separator + "diff.txt");
                diffFile.getParentFile().mkdirs();
                diffFile.createNewFile();
                buf = new BufferedWriter(new FileWriter(diffFile));
                final String format = "%-20s %-25s %-25s %-35s %-20s %-20s \n";
                final Set<String> set = diff.keySet();
                for (final Object key : set) {
                    buf.write("\n");
                    buf.write("Table Name: " + key.toString());
                    buf.write("\n");
                    buf.write(String.format(format, "ModuleName", "TableName", "Name", "DiffType", "Source", "Destination"));
                    final JSONArray jsonArr = diff.get(key);
                    for (int i = 0; i < jsonArr.length(); ++i) {
                        final JSONObject obj = (JSONObject)jsonArr.get(i);
                        buf.write(String.format(format, obj.getString("modulename"), obj.getString("tablename"), obj.getString("name"), obj.getString("difftype"), obj.get("source"), obj.get("destination")));
                    }
                    buf.flush();
                }
            }
            else if (diffFile.exists()) {
                diffFile.delete();
            }
        }
        finally {
            if (buf != null) {
                buf.close();
            }
            if (diffbuf != null) {
                diffbuf.close();
            }
        }
    }
    
    private static List<String> getModuleNames(final String confPath) throws Exception {
        final ArrayList<String> moduleNames = new ArrayList<String>();
        final DataObject module = Xml2DoConverter.transform(new File(confPath + File.separator + "module.xml").toURI().toURL());
        final SortColumn col = new SortColumn("Module", "MODULEORDER", true);
        module.sortRows("Module", col);
        final Iterator iterator = module.getRows("Module", (Criteria)null);
        while (iterator.hasNext()) {
            final Row curRow = iterator.next();
            moduleNames.add((String)curRow.get("MODULENAME"));
        }
        return moduleNames;
    }
    
    protected static void initializeMetaData() throws Exception {
        if (SchemaAnalyzerUtil.isMickeyInitialized) {
            SchemaAnalyzerUtil.LOGGER.log(Level.INFO, "Metadata has already been initialized!! hence ignoring!!");
            return;
        }
        if (!getConfiguration().isMetaDataToBeLoadedFromDDXml()) {
            PersistenceInitializer.initializeMickey(false);
        }
        else {
            URL url = null;
            final String confPath = SchemaAnalyzerUtil.serverPath + File.separator + "conf";
            SchemaBrowserUtil.setReady(false);
            if (!PersistenceInitializer.onSAS()) {
                final String libPath = System.getProperty("external.lib.path");
                if (libPath != null) {
                    url = new URL("jar:file:" + libPath + "/AdvPersistence.jar!/com/adventnet/db/persistence/metadata/conf/meta-dd.xml");
                    MetaDataUtil.addDataDictionaryConfiguration(DataDictionaryParser.getDataDictionary(url));
                    url = new URL("jar:file:" + libPath + "/AdvPersistence.jar!/com/adventnet/db/persistence/metadata/conf/data-dictionary.xml");
                    MetaDataUtil.addDataDictionaryConfiguration(DataDictionaryParser.getDataDictionary(url));
                }
                else {
                    url = DataDictionary.class.getResource("conf/meta-dd.xml");
                    MetaDataUtil.addDataDictionaryConfiguration(DataDictionaryParser.getDataDictionary(url));
                    url = DataDictionary.class.getResource("conf/data-dictionary.xml");
                    MetaDataUtil.addDataDictionaryConfiguration(DataDictionaryParser.getDataDictionary(url));
                }
            }
            final List<String> moduleNames = getModuleNames(confPath);
            for (int i = 0; i < moduleNames.size(); ++i) {
                if (!moduleNames.get(i).equalsIgnoreCase("Persistence")) {
                    final String moduleDir = confPath + File.separator + moduleNames.get(i);
                    PersistenceInitializer.loadDD(moduleNames.get(i), moduleDir, false);
                    PersistenceInitializer.loadPersonality(moduleNames.get(i), moduleDir);
                }
            }
            SchemaBrowserUtil.setReady(true);
            SchemaAnalyzerUtil.isMickeyInitialized = true;
        }
    }
    
    private static void initializeSetup() throws Exception {
        PersistenceInitializer.initializeDB(SchemaAnalyzerUtil.serverPath + File.separator + "conf");
        getConfiguration();
        initializeMetaData();
    }
    
    private static void updateEncryptedPassword(final Properties props) throws PasswordException, PersistenceException {
        final String pass = props.getProperty("password");
        if (pass != null) {
            final String decryptedPass = PersistenceUtil.getDBPasswordProvider(getDestDBType().toString().toLowerCase(Locale.ENGLISH)).getPassword(props);
            ((Hashtable<String, String>)props).put("password", (decryptedPass != null) ? decryptedPass : pass);
        }
    }
    
    public static DBType getDBType(final String dbName) {
        return DBType.MYSQL.equals(dbName) ? DBType.MYSQL : (DBType.MSSQL.equals(dbName) ? DBType.MSSQL : (DBType.POSTGRES.equals(dbName) ? DBType.POSTGRES : (DBType.FIREBIRD.equals(dbName) ? DBType.FIREBIRD : DBType.OTHERS)));
    }
    
    public static SchemaComparatorConfiguration getConfiguration() throws Exception {
        if (SchemaAnalyzerUtil.config == null) {
            SchemaAnalyzerUtil.config = new SchemaComparatorConfiguration();
        }
        return SchemaAnalyzerUtil.config;
    }
    
    public static DataBase getDatabaseInstance(final DBAdapter adapter, final Connection connection, final DataSource dataSource) throws Exception {
        final String srcSchema = getSchema(adapter.getSQLGenerator().getSchemaQuery(), connection);
        return ((DataBaseBuilder)((DataBaseBuilder)DataBaseBuilder.dataBase().withSchema(srcSchema)).usingDataSource(dataSource)).scan();
    }
    
    protected static String getSchema(final String schemaQuery, final Connection connection) throws SQLException, QueryConstructionException {
        if (schemaQuery == null) {
            return null;
        }
        ResultSet executeQuery = null;
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            executeQuery = stmt.executeQuery(schemaQuery);
            if (executeQuery.next()) {
                return executeQuery.getString(1);
            }
        }
        finally {
            if (executeQuery != null) {
                executeQuery.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
        return null;
    }
    
    public static Connection getSrcConnection() throws SQLException {
        return SchemaAnalyzerUtil.comparatorObj.getSrcDataSource().getConnection();
    }
    
    public static Connection getDestConnection() throws SQLException {
        return SchemaAnalyzerUtil.comparatorObj.getDestDataSource().getConnection();
    }
    
    public static DBAdapter getSrcDBAdapter() {
        return SchemaAnalyzerUtil.comparatorObj.getSrcDBAdapter();
    }
    
    public static DBAdapter getDestDBAdapter() {
        return SchemaAnalyzerUtil.comparatorObj.getDestDBAdapter();
    }
    
    public static DataBase getSrcDB() {
        return SchemaAnalyzerUtil.srcDB;
    }
    
    protected static void setSrcDB(final DataBase srcDb) {
        SchemaAnalyzerUtil.srcDB = srcDb;
    }
    
    public static DataBase getDestDB() {
        return SchemaAnalyzerUtil.destDB;
    }
    
    protected static void setDestDB(final DataBase destDb) {
        SchemaAnalyzerUtil.destDB = destDb;
    }
    
    public static Connection getConnection() throws SQLException {
        return SchemaAnalyzerUtil.comparatorObj.getDataSource().getConnection();
    }
    
    public static DBAdapter getDBAdapter() {
        return SchemaAnalyzerUtil.comparatorObj.getDBAdapter();
    }
    
    public static DataBase getDataBase() {
        return SchemaAnalyzerUtil.srcDB;
    }
    
    protected static void setDataBase(final DataBase db) {
        SchemaAnalyzerUtil.srcDB = db;
    }
    
    private static String getActualTableName(final String tabName, final String schemaName, final Connection conn) throws SQLException {
        final String sql = "select tablename from pg_tables where (tablename like lower(?) or tablename like ?) and schemaname=?";
        try (final DataSet ds = RelationalAPI.getInstance().executeQuery(conn, sql, tabName, tabName, schemaName)) {
            if (ds.next()) {
                return ds.getAsString(1);
            }
            return tabName;
        }
    }
    
    public static boolean isDiffExists(final List<String> tableNames) throws Exception {
        return isDiffExists(tableNames, null, false, SchemaComparator.ComparatorType.METADATA_VS_DATABASE);
    }
    
    public static boolean isDiffExists(final List<String> tableNames, final SchemaComparator.ComparatorType type) throws Exception {
        return isDiffExists(tableNames, null, false, type);
    }
    
    public static boolean isDiffExists(final List<String> tableNames, final SchemaComparatorConfiguration confObj, final boolean exitOnFirstDiff, final SchemaComparator.ComparatorType type) throws Exception {
        boolean isDiffExists = false;
        initializeDataSource();
        if (confObj == null) {
            SchemaAnalyzerUtil.config = getConfiguration();
        }
        else {
            SchemaAnalyzerUtil.config = confObj;
        }
        final DBType dbType = initSrcDBType();
        SchemaAnalyzerUtil.LOGGER.log(Level.INFO, "sourceDB ::: {0}", dbType);
        try (final Connection conn = SchemaAnalyzerUtil.dataSource.getConnection()) {
            final String schema = getSchema(SchemaAnalyzerUtil.dbAdapter.getSQLGenerator().getSchemaQuery(), conn);
            SchemaAnalyzerUtil.LOGGER.log(Level.INFO, "Schema ::: {0}", schema);
            final List<String> actualTableNames = new ArrayList<String>();
            if (tableNames != null && tableNames.size() > 0) {
                for (final String tableName : tableNames) {
                    if (dbType.equals("postgres")) {
                        actualTableNames.add(getActualTableName(tableName, schema, conn));
                    }
                    else {
                        actualTableNames.add(tableName);
                    }
                }
            }
            else {
                final Set<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                set.addAll(RelationalAPI.getInstance().getTables(schema));
                final List<TableDefinition> tds = MetaDataUtil.getTableDefinitions();
                for (int i = 0; i < tds.size(); ++i) {
                    if (!set.contains(tds.get(i).getTableName())) {
                        if (dbType.equals("postgres")) {
                            set.add(getActualTableName(tds.get(i).getTableName(), schema, conn));
                        }
                        else {
                            set.add(tds.get(i).getTableName());
                        }
                    }
                }
                actualTableNames.addAll(set);
            }
            SchemaAnalyzerUtil.comparatorObj = SchemaComparatorBuilder.config().setSrcDataSource(SchemaAnalyzerUtil.dataSource).setSrcDBAdapter(SchemaAnalyzerUtil.dbAdapter).withSrcDBType(dbType).WithTableNames(actualTableNames).usingSchemaComparatorHandler(getConfiguration().getSchemaComparatorHandler()).withComparatorType(type).exitOnFirstDiff(exitOnFirstDiff).build();
            final SchemaAnalyzer sa = new SchemaAnalyzer();
            final Map<String, JSONArray> diffMap = sa.analyzeSchema(SchemaAnalyzerUtil.comparatorObj);
            if (!diffMap.isEmpty()) {
                isDiffExists = true;
            }
        }
        return isDiffExists;
    }
    
    public static Map<String, JSONArray> getTableNameVsDiffMap() {
        if (SchemaAnalyzerUtil.comparatorObj == null) {
            return null;
        }
        return SchemaAnalyzerUtil.comparatorObj.getTableVsDiffMap();
    }
    
    public static void setSrcDBType(final String dbName) {
        SchemaAnalyzerUtil.srcDBType = getDBType(dbName);
    }
    
    public static void setDestDBType(final String dbName) {
        SchemaAnalyzerUtil.destDBType = getDBType(dbName);
    }
    
    public static DBType getSrcDBType() {
        return SchemaAnalyzerUtil.srcDBType;
    }
    
    public static DBType getDestDBType() {
        return SchemaAnalyzerUtil.destDBType;
    }
    
    static {
        LOGGER = Logger.getLogger(SchemaAnalyzerUtil.class.getName());
        SchemaAnalyzerUtil.isMickeyInitialized = false;
        SchemaAnalyzerUtil.config = null;
        SchemaAnalyzerUtil.comparatorObj = null;
        SchemaAnalyzerUtil.serverPath = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        SchemaAnalyzerUtil.srcDBType = DBType.OTHERS;
        SchemaAnalyzerUtil.destDBType = DBType.OTHERS;
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
