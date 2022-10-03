package com.adventnet.db.adapter;

import java.util.Hashtable;
import com.adventnet.persistence.util.DCManager;
import com.adventnet.ds.query.UpdateQuery;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.sql.Time;
import java.math.BigDecimal;
import com.adventnet.ds.query.BulkLoadStatementGenerator;
import com.zoho.framework.io.DataBufferStream;
import com.adventnet.ds.query.BulkLoad;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import com.zoho.framework.utils.archive.ZipUtils;
import com.adventnet.db.persistence.metadata.util.TemplateMetaHandler;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.ArchiveTable;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.ds.query.CreateTableLike;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.zoho.mickey.exception.DataBaseException;
import java.util.Enumeration;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.ErrorCodes;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import java.net.ConnectException;
import java.io.IOException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.mfw.ConsoleOut;
import java.util.Collection;
import com.adventnet.db.archive.TableArchiverUtil;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.Date;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.adapter.DataSourceException;
import com.adventnet.ds.adapter.MDSContext;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import java.sql.BatchUpdateException;
import java.util.Locale;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import com.adventnet.ds.DataSourceManager;
import java.util.logging.Level;
import com.zoho.conf.Configuration;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import com.zoho.mickey.db.SQLModifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.List;
import javax.sql.DataSource;
import java.util.logging.Logger;

public class Jdbc20DBAdapter implements DBAdapter
{
    private static final Logger OUT;
    private static final int DEFAULT_FETCH_SIZE = 200;
    protected SQLGenerator sqlGen;
    protected DataSource datasource;
    protected String CONNECTION_KEY;
    boolean checkForDBAlive;
    protected boolean isDBMigration;
    protected boolean isDBMigDestDB;
    protected Boolean isBundledDB;
    List shutDownStrings;
    protected static boolean isAutoQuoteEnabled;
    protected Properties dbProps;
    protected BackupHandler backupHandler;
    private static String server_home;
    protected RestoreHandler restoreHandler;
    private Map<String, DCAdapter> dcTypeVsAdapter;
    protected static final int UNDEFINED_ERROR_CODE = -9999;
    protected Map<String, HashSet<String>> convertibleDataTypes;
    private SQLModifier sqlModifier;
    private String name;
    private ExecutorService bulkExecutorThreadPool;
    protected boolean readOnlyMode;
    private static final int BUFFER = 2048;
    
    protected void initializeConvertibleDataTypes() {
        HashSet<String> set = new HashSet<String>();
        set.add("TINYINT");
        set.add("INTEGER");
        set.add("CHAR");
        set.add("NCHAR");
        set.add("SCHAR");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("BOOLEAN", set);
        set = new HashSet<String>();
        set.add("INTEGER");
        set.add("BIGINT");
        set.add("FLOAT");
        set.add("DOUBLE");
        set.add("DECIMAL");
        set.add("CHAR");
        set.add("NCHAR");
        set.add("SCHAR");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("TINYINT", set);
        set = new HashSet<String>();
        set.add("BIGINT");
        set.add("FLOAT");
        set.add("DOUBLE");
        set.add("DECIMAL");
        set.add("CHAR");
        set.add("NCHAR");
        set.add("SCHAR");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("INTEGER", set);
        set = new HashSet<String>();
        set.add("DOUBLE");
        set.add("CHAR");
        set.add("NCHAR");
        set.add("SCHAR");
        set.add("DATETIME");
        set.add("TIMESTAMP");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("BIGINT", set);
        set = new HashSet<String>();
        set.add("DOUBLE");
        set.add("CHAR");
        set.add("NCHAR");
        set.add("SCHAR");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("FLOAT", set);
        set = new HashSet<String>();
        set.add("CHAR");
        set.add("NCHAR");
        set.add("SCHAR");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("DOUBLE", set);
        set = new HashSet<String>();
        set.add("CHAR");
        set.add("NCHAR");
        set.add("SCHAR");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("DECIMAL", set);
        set = new HashSet<String>();
        set.add("NCHAR");
        set.add("SCHAR");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("CHAR", set);
        set = new HashSet<String>();
        set.add("CHAR");
        set.add("SCHAR");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("NCHAR", set);
        set = new HashSet<String>();
        set.add("CHAR");
        set.add("NCHAR");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("SCHAR", set);
        set = new HashSet<String>();
        set.add("DATETIME");
        set.add("TIMESTAMP");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("DATE", set);
        set = new HashSet<String>();
        set.add("BIGINT");
        set.add("TIMESTAMP");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("DATETIME", set);
        set = new HashSet<String>();
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("TIME", set);
        set = new HashSet<String>();
        set.add("BIGINT");
        set.add("DATETIME");
        set.add("BLOB");
        set.add("SBLOB");
        this.convertibleDataTypes.put("TIMESTAMP", set);
        set = new HashSet<String>();
        set.add("SBLOB");
        this.convertibleDataTypes.put("BLOB", set);
        set = new HashSet<String>();
        set.add("BLOB");
        this.convertibleDataTypes.put("SBLOB", set);
    }
    
    public Jdbc20DBAdapter() {
        this.sqlGen = null;
        this.datasource = null;
        this.CONNECTION_KEY = "db_connection";
        this.checkForDBAlive = false;
        this.isDBMigration = false;
        this.isDBMigDestDB = false;
        this.isBundledDB = null;
        this.shutDownStrings = null;
        this.dbProps = null;
        this.backupHandler = null;
        this.restoreHandler = null;
        this.dcTypeVsAdapter = null;
        this.convertibleDataTypes = new HashMap<String, HashSet<String>>();
        this.sqlModifier = null;
        this.name = null;
        this.bulkExecutorThreadPool = null;
        this.readOnlyMode = Boolean.getBoolean("app.readonly.mode");
    }
    
    public Jdbc20DBAdapter(final SQLGenerator sqlGen) {
        this.sqlGen = null;
        this.datasource = null;
        this.CONNECTION_KEY = "db_connection";
        this.checkForDBAlive = false;
        this.isDBMigration = false;
        this.isDBMigDestDB = false;
        this.isBundledDB = null;
        this.shutDownStrings = null;
        this.dbProps = null;
        this.backupHandler = null;
        this.restoreHandler = null;
        this.dcTypeVsAdapter = null;
        this.convertibleDataTypes = new HashMap<String, HashSet<String>>();
        this.sqlModifier = null;
        this.name = null;
        this.bulkExecutorThreadPool = null;
        this.readOnlyMode = Boolean.getBoolean("app.readonly.mode");
        this.sqlGen = sqlGen;
    }
    
    @Override
    public void initialize(final Properties props) {
        if (null == this.bulkExecutorThreadPool) {
            this.bulkExecutorThreadPool = Executors.newCachedThreadPool();
        }
        if (Configuration.getString("db.home") == null || Configuration.getString("db.home").equals("")) {
            this.setDefaultDBHome();
        }
        try {
            this.dbProps = props;
            this.initDCAdapters();
            this.initializeConvertibleDataTypes();
            this.name = props.getProperty("DSName");
            if (this.sqlGen == null) {
                final String sqlGenClass = props.getProperty("SQLGeneratorName");
                (this.sqlGen = (SQLGenerator)Thread.currentThread().getContextClassLoader().loadClass(sqlGenClass).newInstance()).initDCSQLGenerators();
            }
            if (this.sqlModifier == null) {
                final String sqlModifierName = (props.getProperty("sqlmodifier") != null) ? props.getProperty("sqlmodifier") : this.getDBSpecificSQLModifierName();
                this.sqlModifier = (SQLModifier)Thread.currentThread().getContextClassLoader().loadClass(sqlModifierName).newInstance();
            }
            else {
                Jdbc20DBAdapter.OUT.log(Level.WARNING, "SQLModifier has been already initialized for this JVM.");
            }
            ((Ansi92SQLGenerator)this.sqlGen).initializeSQLModifier(this.sqlModifier);
            this.loadFunctionTemplates();
            this.CONNECTION_KEY += this.name;
            this.isDBMigration = Boolean.valueOf(props.getProperty("db.migration", "false"));
            this.isDBMigDestDB = Boolean.valueOf(props.getProperty("db.migration.dest", "false"));
            Jdbc20DBAdapter.OUT.info("isDBMigration running :: " + this.isDBMigration);
            Jdbc20DBAdapter.OUT.info("isDBMigration dest DB :: " + this.isDBMigDestDB);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            Jdbc20DBAdapter.OUT.log(Level.SEVERE, "Error while trying to instantiate SQLGenerator :: {0}", ex);
        }
        try {
            if (this.name != null) {
                this.datasource = DataSourceManager.getDataSource(this.name);
            }
        }
        catch (final Exception e) {
            Jdbc20DBAdapter.OUT.log(Level.SEVERE, "Exception occured while initializing datasource.");
            Jdbc20DBAdapter.OUT.log(Level.FINE, "{0}", e);
        }
    }
    
    @Override
    public ExecutorService getBulkThreadExecutor() {
        return this.bulkExecutorThreadPool;
    }
    
    protected void setDefaultDBHome() {
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Connection createConnection(final String url, final String userName, final String password, final String driver) throws SQLException, ClassNotFoundException {
        Thread.currentThread().getContextClassLoader().loadClass(driver);
        final Connection conn = DriverManager.getConnection(url, userName, password);
        final StringBuilder buff = new StringBuilder();
        buff.append("Connection created ").append("\n\tURL : ").append(url).append("\n\tUSERNAME : ").append(userName).append("\n\tDRIVER : ").append(driver);
        Jdbc20DBAdapter.OUT.log(Level.FINE, buff.toString());
        return conn;
    }
    
    @Override
    public ResultSetAdapter executeQuery(final Statement stmt, final String sql) throws SQLException {
        try {
            final ResultSet rs = stmt.executeQuery(sql);
            Jdbc20DBAdapter.OUT.log(Level.FINE, "DBAdapter.executeQuery(Statement,String): Query executed is :\n{0}", sql);
            return this.getResultSetAdapter(rs);
        }
        catch (final SQLException sqle) {
            Jdbc20DBAdapter.OUT.log(Level.FINE, "DBAdapter.executeQuery(Statement,String): Exception while executing Query :\n{0}", sql);
            Jdbc20DBAdapter.OUT.log(Level.FINE, "", sqle);
            throw this.handleSQLException(sqle, stmt.getConnection(), false);
        }
    }
    
    @Override
    public ResultSetAdapter executeQuery(final PreparedStatement pstmt) throws SQLException {
        try {
            final ResultSet rs = pstmt.executeQuery();
            Jdbc20DBAdapter.OUT.log(Level.FINE, "DBAdapter.executeQuery(PreparedStatement): PreparedStatement executed  : {0}", pstmt.toString());
            return this.getResultSetAdapter(rs);
        }
        catch (final SQLException sqle) {
            Jdbc20DBAdapter.OUT.log(Level.FINE, "DBAdapter.executeQuery(PreparedStatement): Exception while executing PreparedStatement:{0} ", pstmt.toString());
            Jdbc20DBAdapter.OUT.log(Level.FINE, "", sqle);
            throw this.handleSQLException(sqle, pstmt.getConnection(), false);
        }
    }
    
    @Override
    public int executeUpdate(final Statement stmt, final String sql) throws SQLException {
        try {
            final int updateCount = stmt.executeUpdate(sql);
            Jdbc20DBAdapter.OUT.log(Level.FINE, "DBAdapter.executeUpdate(): Query executed is :{0}", sql);
            return updateCount;
        }
        catch (final SQLException sqle) {
            Jdbc20DBAdapter.OUT.log(Level.FINE, "DBAdapter.executeUpdate(): Exception while executing Query :{0} ", sql);
            Jdbc20DBAdapter.OUT.log(Level.FINE, "", sqle);
            throw this.handleSQLException(sqle, stmt.getConnection(), true);
        }
    }
    
    @Override
    public int executeUpdate(final PreparedStatement pstmt) throws SQLException {
        try {
            final int updateCount = pstmt.executeUpdate();
            Jdbc20DBAdapter.OUT.log(Level.FINE, "DBAdapter.executeUpdate(PreparedStatement): PreparedStatement executed  :{0}", pstmt);
            return updateCount;
        }
        catch (final SQLException sqle) {
            Jdbc20DBAdapter.OUT.log(Level.FINE, "Exception while executing PreparedStatement: {0}", new Object[] { pstmt });
            Jdbc20DBAdapter.OUT.log(Level.FINE, "", sqle);
            throw this.handleSQLException(sqle, pstmt.getConnection(), true);
        }
    }
    
    @Override
    public boolean execute(final Statement stmt, final String sql) throws SQLException {
        boolean executed = false;
        try {
            executed = stmt.execute(sql);
            Jdbc20DBAdapter.OUT.log(Level.FINE, "DBAdapter.execute(Statement,String): Query executed is :\n{0}", sql);
            return executed;
        }
        catch (final SQLException sqle) {
            Jdbc20DBAdapter.OUT.log(Level.FINE, "DBAdapter.execute(Statement,String): Exception while executing Query :\n{0}", new Object[] { sql });
            Jdbc20DBAdapter.OUT.log(Level.FINE, "", sqle);
            throw this.handleSQLException(sqle, stmt.getConnection(), true);
        }
    }
    
    @Override
    @Deprecated
    public List getTables(final Connection con) throws SQLException {
        return this.getTables(con, this.getCurrentSchema(con));
    }
    
    protected String getCurrentSchema(final Connection conn) throws SQLException {
        final String schemaQuery = this.sqlGen.getSchemaQuery();
        if (schemaQuery != null) {
            Statement statement = null;
            ResultSet rs = null;
            try {
                String schemaName = null;
                statement = conn.createStatement();
                rs = statement.executeQuery(schemaQuery);
                if (rs.next()) {
                    schemaName = rs.getString(1);
                }
                Jdbc20DBAdapter.OUT.fine("Current schema name :: " + schemaName);
                return schemaName;
            }
            finally {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
        }
        return null;
    }
    
    @Override
    public List getTables(final Connection con, final String schemaName) throws SQLException {
        final List<String> tableList = new ArrayList<String>();
        final DatabaseMetaData metaData = con.getMetaData();
        ResultSet rs = null;
        try {
            rs = this.getTablesFromDB(metaData, this.getDBName(con), schemaName);
            while (rs.next()) {
                tableList.add(rs.getString("TABLE_NAME"));
            }
            return tableList;
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
    
    @Override
    public List<String> getTableNamesLike(final Connection con, final String schemaName, final String tableNameLike) throws SQLException {
        final List<String> tableList = new ArrayList<String>();
        final DatabaseMetaData metaData = con.getMetaData();
        ResultSet rs = null;
        try {
            rs = this.getTablesFromDB(metaData, null, schemaName, tableNameLike);
            while (rs.next()) {
                tableList.add(rs.getString("TABLE_NAME"));
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
        return tableList;
    }
    
    @Override
    public List<String> getColumnNames(final Connection con, final String schemaName, final String tableName) throws SQLException {
        final List<String> tableList = new ArrayList<String>();
        final DatabaseMetaData metaData = con.getMetaData();
        ResultSet rs = null;
        try {
            rs = this.getColumnNamesFromDB(metaData, schemaName, tableName);
            while (rs.next()) {
                tableList.add(rs.getString("COLUMN_NAME"));
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
        return tableList;
    }
    
    @Override
    public void dropTable(final Statement stmt, final String tableName, final boolean cascade, final List relatedTable) throws SQLException {
        throw new UnsupportedOperationException("Jdbc20DBAdapter: Dropping table not supported.");
    }
    
    @Override
    public void truncateTable(final Statement stmt, final String tableName) throws SQLException {
        try {
            final String truncateSQL = this.sqlGen.getSQLForTruncate(tableName);
            this.execute(stmt, truncateSQL);
        }
        catch (final QueryConstructionException excp) {
            throw new SQLException(excp.getMessage());
        }
    }
    
    @Override
    public void setSQLGenerator(final SQLGenerator sqlGen) {
        (this.sqlGen = sqlGen).initDCSQLGenerators();
    }
    
    @Override
    public SQLGenerator getSQLGenerator() {
        return this.sqlGen;
    }
    
    @Override
    public void createTable(final Statement stmt, final String createSQL, final List relatedTables) throws SQLException {
        this.execute(stmt, createSQL);
    }
    
    @Override
    public void createTables(final Statement stmt, final String schemaName, final List tableDefnList, final List tablesPresent) throws SQLException {
        for (int size = tableDefnList.size(), i = 0; i < size; ++i) {
            final TableDefinition tDef = tableDefnList.get(i);
            final String tableName = tDef.getTableName();
            if (this.isTablePresentInDB(stmt.getConnection(), schemaName, tableName)) {
                Jdbc20DBAdapter.OUT.log(Level.FINE, "Table {0} already exists", tableName);
            }
            else if (tDef.isTemplate()) {
                Jdbc20DBAdapter.OUT.log(Level.INFO, "Table {0} is a template-table. Ignoring it", tableName);
            }
            else {
                try {
                    this.createTable(stmt, tDef, null);
                }
                catch (final SQLException x) {
                    Jdbc20DBAdapter.OUT.log(Level.SEVERE, "Exception while creating the table [{0}]", tableName);
                    throw x;
                }
            }
        }
    }
    
    @Override
    public boolean isTablePresentInDB(final Connection c, final String schemaName, final String tableName) throws SQLException {
        final boolean isExist = this.isTableExists(c, schemaName, tableName);
        if (!isExist) {
            return this.isTableExists(c, schemaName, tableName.toLowerCase(Locale.ENGLISH));
        }
        return isExist;
    }
    
    public boolean isTableExists(final Connection c, final String schemaName, final String tableName) throws SQLException {
        ResultSet tableSet = null;
        boolean isExist = false;
        final DatabaseMetaData metaData = c.getMetaData();
        try {
            tableSet = metaData.getTables(null, null, tableName, new String[] { "TABLE" });
            isExist = tableSet.next();
            Jdbc20DBAdapter.OUT.log(Level.FINE, "is table [{0}] already exists {1}", new Object[] { tableName, isExist });
        }
        finally {
            if (tableSet != null) {
                tableSet.close();
            }
        }
        return isExist;
    }
    
    protected int getBatchFailureIndex(final BatchUpdateException bue) {
        final int[] updateCounts = bue.getUpdateCounts();
        if (updateCounts != null) {
            for (int length = updateCounts.length, j = 0; j < length; ++j) {
                if (updateCounts[j] == -3) {
                    return j;
                }
            }
        }
        return -1;
    }
    
    @Override
    public void createTable(final Statement stmt, final TableDefinition tabDefn, final List relatedTables) throws SQLException {
        this.createTable(stmt, tabDefn, null, relatedTables);
    }
    
    @Override
    public void createTable(final Statement stmt, final TableDefinition tabDefn, final String createTableOptions, final List relatedTables) throws SQLException {
        if (!tabDefn.creatable()) {
            Jdbc20DBAdapter.OUT.log(Level.INFO, "Not creating the table :: [{0}], since it is specified as CREATETABLE false.", tabDefn.getTableName());
            return;
        }
        try {
            final String createSQL = this.sqlGen.getSQLForCreateTable(tabDefn, createTableOptions);
            this.createTable(stmt, createSQL, relatedTables);
        }
        catch (final QueryConstructionException excp) {
            throw new SQLException(excp.getMessage());
        }
    }
    
    protected void createIndex(final Statement stmt, final TableDefinition tDef) throws SQLException {
        try {
            final String tableName = tDef.getTableName();
            final List indexes = tDef.getIndexes();
            if (indexes != null) {
                for (int indSize = indexes.size(), j = 0; j < indSize; ++j) {
                    final IndexDefinition id = indexes.get(j);
                    if (!tDef.isUnique(id.getColumns())) {
                        final String indexSQL = this.sqlGen.getSQLForIndex(tDef.getTableName(), id);
                        Jdbc20DBAdapter.OUT.log(Level.FINER, "Index SQL for table {0} and index {1} is {2}", new Object[] { tableName, id.getName(), indexSQL });
                        stmt.executeUpdate(indexSQL);
                    }
                }
            }
        }
        catch (final QueryConstructionException excp) {
            throw new SQLException(excp.getMessage());
        }
    }
    
    @Deprecated
    protected ResultSet getTablesFromDB(final DatabaseMetaData metaData) throws SQLException {
        return this.getTablesFromDB(metaData, metaData.getUserName());
    }
    
    protected ResultSet getTablesFromDB(final DatabaseMetaData metaData, final String schemaName) throws SQLException {
        return this.getTablesFromDB(metaData, null, schemaName);
    }
    
    protected ResultSet getTablesFromDB(final DatabaseMetaData metaData, final String catalog, final String schamePattern) throws SQLException {
        return this.getTablesFromDB(metaData, catalog, schamePattern, null);
    }
    
    protected ResultSet getTablesFromDB(final DatabaseMetaData metaData, final String catalog, final String schamePattern, String tableName) throws SQLException {
        if (tableName == null) {
            tableName = "%";
        }
        else {
            tableName += "%";
        }
        return metaData.getTables(catalog, schamePattern, tableName, new String[] { "TABLE" });
    }
    
    protected ResultSetAdapter getResultSetAdapter(final ResultSet rs) throws SQLException {
        return new ResultSetAdapter(rs);
    }
    
    @Override
    public void initForExecution(final MDSContext context) throws DataSourceException {
        if (context == null) {
            throw new DataSourceException("Context passed for initForExecution is NULL");
        }
        Connection con = null;
        try {
            con = this.datasource.getConnection();
        }
        catch (final SQLException sqlExp) {
            throw new DataSourceException(sqlExp.getMessage(), sqlExp);
        }
        context.add(this.CONNECTION_KEY, con);
    }
    
    @Override
    public void cleanUp(final MDSContext context) throws DataSourceException {
        Jdbc20DBAdapter.OUT.log(Level.FINER, " MDSContext [{0}] :{1}", new Object[] { this.CONNECTION_KEY, context });
        if (context == null) {
            throw new DataSourceException("Context passed to cleanUp is NULL");
        }
        final Connection con = (Connection)this.getFromContext(context, this.CONNECTION_KEY);
        try {
            if (con != null) {
                con.close();
                Jdbc20DBAdapter.OUT.log(Level.FINER, " DEBUG Jdbc20DBAdapter Connection closed is  {0}", con);
            }
        }
        catch (final SQLException sqlExp) {
            throw new DataSourceException(sqlExp.getMessage(), sqlExp);
        }
    }
    
    private Object getFromContext(final MDSContext context, final Object key) {
        return context.get(key);
    }
    
    @Override
    public DataSet executeQuery(final MDSContext context, final Query query) throws DataSourceException {
        final Connection conn = (Connection)this.getFromContext(context, this.CONNECTION_KEY);
        return this.processQuery(conn, query);
    }
    
    protected DataSet processQuery(final Connection con, final Query query) throws DataSourceException {
        DataSet ds = null;
        try {
            QueryUtil.setDataType(query);
            final String sql = this.sqlGen.getSQLForSelect(query);
            final Statement stmt = con.createStatement();
            final ResultSetAdapter rsAdapter = this.executeQuery(stmt, sql);
            if (query instanceof SelectQuery) {
                final SelectQuery select = (SelectQuery)query;
                ds = new DataSet(rsAdapter, select, select.getSelectColumns(), stmt);
            }
        }
        catch (final SQLException excp) {
            throw new DataSourceException(excp.getMessage());
        }
        catch (final QueryConstructionException excp2) {
            throw new DataSourceException(excp2.getMessage());
        }
        return ds;
    }
    
    @Override
    public void setValue(final PreparedStatement ps, final int columnIndex, final int sqlType, Object value) throws SQLException {
        try {
            if (value == null) {
                ps.setNull(columnIndex, sqlType);
            }
            else if (!(value instanceof Column) && !(value instanceof CaseExpression)) {
                if (sqlType == 91) {
                    if (value instanceof Date) {
                        value = new java.sql.Date(((Date)value).getTime());
                    }
                    ps.setObject(columnIndex, value);
                }
                else {
                    ps.setObject(columnIndex, value);
                }
            }
        }
        catch (final SQLException sqle) {
            Jdbc20DBAdapter.OUT.log(Level.FINER, "Exception in setValue {0}", sqle);
            throw this.handleSQLException(sqle, ps.getConnection(), true);
        }
    }
    
    @Override
    public void connectTo(final Connection connection, final String dbName) throws SQLException {
        throw new UnsupportedOperationException("No specific implementation common to all DBs. Hence use respective DBAdapter");
    }
    
    @Override
    public void createDB(final Connection connection, final String dbName, final String userName, final String passWord) throws SQLException {
        throw new UnsupportedOperationException("No specific implementation common to all DBs. Hence use respective DBAdapter");
    }
    
    @Override
    public String getDefaultDB(final Connection connection) throws SQLException {
        throw new UnsupportedOperationException("No specific implementation common to all DBs. Hence use respective DBAdapter");
    }
    
    @Override
    public String getDBName(final Connection connection) throws SQLException {
        throw new UnsupportedOperationException("No specific implementation common to all DBs. Hence use respective DBAdapter");
    }
    
    protected String getUniqueKeyName(final String ukName) {
        throw new UnsupportedOperationException("This implementation cannot be common to all DBs. Hence use respective DBAdapter");
    }
    
    @Override
    public void alterTable(final Connection connection, final AlterTableQuery alterTableQuery) throws SQLException {
        try {
            this.validateAlterTableQuery(connection, alterTableQuery);
            final int operationType = alterTableQuery.getAlterOperations().get(0).getOperationType();
            DCAdapter dcAdapter = null;
            if (operationType == 19 || operationType == 20 || operationType == 21 || operationType == 22) {
                dcAdapter = this.getDCAdapterForTable(alterTableQuery.getTableName());
                if (dcAdapter == null) {
                    throw new MetaDataException("No dynamic column handler defined");
                }
            }
            switch (operationType) {
                case 19: {
                    dcAdapter.preAlterTable(connection, alterTableQuery);
                    dcAdapter.addDynamicColumn(connection, alterTableQuery);
                    break;
                }
                case 20: {
                    dcAdapter.preAlterTable(connection, alterTableQuery);
                    dcAdapter.deleteDynamicColumn(connection, alterTableQuery);
                    break;
                }
                case 21: {
                    dcAdapter.preAlterTable(connection, alterTableQuery);
                    dcAdapter.modifyDynamicColumn(connection, alterTableQuery);
                    break;
                }
                case 22: {
                    dcAdapter.preAlterTable(connection, alterTableQuery);
                    dcAdapter.renameDynamicColumn(connection, alterTableQuery);
                    break;
                }
                default: {
                    final String alterSQL = this.sqlGen.getSQLForAlterTable(alterTableQuery);
                    Statement statement = null;
                    try {
                        statement = connection.createStatement();
                        statement.execute(alterSQL);
                    }
                    finally {
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    break;
                }
            }
        }
        catch (final QueryConstructionException qce) {
            throw new IllegalArgumentException(qce);
        }
        catch (final MetaDataException mde) {
            throw new IllegalArgumentException(mde);
        }
    }
    
    protected void handleTableAttributesChange(final Connection con, final AlterOperation ao) throws MetaDataException, SQLException {
        if (ao.getOperationType() == 18) {
            final String tableName = ao.getTableName();
            final Properties tableProp = (Properties)ao.getAlterObject();
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            if (tableProp.containsKey("template")) {
                throw new IllegalArgumentException("\"template\" attribute change not permitted for table, \"" + tableName + "\".");
            }
            if (tableProp.containsKey("createtable")) {
                final boolean createTable = Boolean.valueOf(tableProp.getProperty("createtable"));
                Statement st = null;
                try {
                    st = con.createStatement();
                    if (createTable) {
                        Jdbc20DBAdapter.OUT.info("Going to create table [" + tableName + "] in DB, since createtable attribute is changed to true.");
                        td.setCreateTable(true);
                        this.createTable(st, td, null, null);
                    }
                    else {
                        Jdbc20DBAdapter.OUT.info("Going to drop table [" + tableName + "] from DB, since createtable attribute is changed to false.");
                        this.dropTable(st, tableName, false, null);
                    }
                }
                finally {
                    if (st != null) {
                        st.close();
                    }
                }
            }
            if (tableProp.containsKey("dc-type")) {
                final String oldDCType = td.getDynamicColumnType();
                final String newDCType = tableProp.getProperty("dc-type");
                if (oldDCType != null && !oldDCType.equals("nodc") && (newDCType.isEmpty() || newDCType.equals("nodc"))) {
                    final DCAdapter dcAdapter = this.getDCAdapter(oldDCType);
                    if (dcAdapter == null) {
                        throw new MetaDataException("No dynamic column handler defined");
                    }
                    Jdbc20DBAdapter.OUT.info("Going to delete All dynamic columns of \"" + tableName + "\" table.");
                    dcAdapter.deleteAllDynamicColumns(con, tableName);
                }
            }
        }
    }
    
    public void disableForeignKeyChecks(final Statement stmt) throws SQLException {
    }
    
    protected void dropTableDetailsFK(final Connection connection) throws SQLException {
        final AlterTableQuery atq = new AlterTableQueryImpl("TableDetails", 7);
        atq.setConstraintName("Appl_id");
        this.alterTable(connection, atq);
    }
    
    @Override
    public void dropAllTables(final Connection connection, final boolean onlyProductTables) throws SQLException, MetaDataException {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            this.disableForeignKeyChecks(stmt);
            List tableNames = onlyProductTables ? this.getAllProductTablesFromDB(connection) : this.getTables(connection, this.getDefaultDB(connection));
            if (onlyProductTables) {
                tableNames = PersistenceUtil.sortTables(tableNames);
                tableNames.addAll(TableArchiverUtil.getAllArchivedTables());
            }
            Jdbc20DBAdapter.OUT.log(Level.FINE, "tableNames :: {0}", tableNames);
            if (!tableNames.contains("TableDetails") && !tableNames.contains("tabledetails") && !tableNames.contains("TABLEDETAILS")) {
                ConsoleOut.println("\nNo Tables found in the specified DB.");
                Jdbc20DBAdapter.OUT.log(Level.INFO, "\nNo Tables found in the specified DB.");
                return;
            }
            int size = tableNames.size();
            if (size == 0) {
                ConsoleOut.println("No tables pertaining to this product exists in the DB.");
                Jdbc20DBAdapter.OUT.log(Level.INFO, "No tables pertaining to this product exists in the DB.");
            }
            else {
                tableNames.add(0, "TableDetails");
                for (int k = ++size - 1; k >= 0; --k) {
                    final String tableName = tableNames.get(k);
                    if (tableName.equals("TableDetails") && k != 0) {
                        this.dropTableDetailsFK(connection);
                    }
                    else {
                        String dropSQL = null;
                        try {
                            dropSQL = this.sqlGen.getSQLForDrop(tableName, true);
                            this.execute(stmt, dropSQL);
                            if (!tableName.equals("TableDetails")) {
                                final String deleteSQL = this.sqlGen.getSQLForDelete("TableDetails", new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), tableName, 0));
                                this.execute(stmt, deleteSQL);
                            }
                            this.printInConsole("Dropped table " + tableName, k % 50 == 0 && k != 0);
                        }
                        catch (final SQLException sqle) {
                            ConsoleOut.println("Exception occured while dropping table " + tableName);
                            ConsoleOut.println("SQL executed is: " + dropSQL);
                        }
                    }
                }
                ConsoleOut.println("");
            }
        }
        catch (final QueryConstructionException qce) {
            throw new IllegalArgumentException("Query Cannot be formed for fetching the tableList from TableDetails Table.");
        }
        catch (final DataAccessException dae) {
            throw new MetaDataException(dae.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (final Exception exc) {
                exc.printStackTrace();
            }
        }
    }
    
    protected void printInConsole(final String message, final boolean isNextLine) {
        if (this.isDBMigration && this.isDBMigDestDB) {
            if (isNextLine) {
                ConsoleOut.println(".");
            }
            else {
                ConsoleOut.print(".");
            }
        }
        else {
            ConsoleOut.println(message);
        }
    }
    
    @Override
    public void setUpDB(final String connectionURL, final String userName, final String password) throws IOException, ConnectException {
        Jdbc20DBAdapter.OUT.log(Level.FINE, "Start DBServer operation is not supported ");
    }
    
    @Override
    public void shutDownDB(final String connectionURL, final String userName, final String password) throws IOException {
        Jdbc20DBAdapter.OUT.log(Level.FINE, "Stop DBServer operation is not supported ");
    }
    
    @Override
    public boolean createDB(final String connectionURL, final String userName, final String password) throws IOException {
        return true;
    }
    
    @Override
    public boolean validateVersion(final Connection con) {
        for (final String dcType : this.dcTypeVsAdapter.keySet()) {
            this.dcTypeVsAdapter.get(dcType).validateVersion(con);
        }
        DataTypeManager.validateDataTypes(con, this.getDBType());
        return true;
    }
    
    protected Map<Object, ErrorCodes.AdventNetErrorCode> getErrorCodeMap() {
        return ErrorCodes.getErrorCodeMap(this.getErrorCodeTableName());
    }
    
    protected String getErrorCodeTableName() {
        throw new UnsupportedOperationException("getErrorCodeTableName should be implemented specific to the DBAdapter");
    }
    
    protected int getErrorCode(final SQLException sqle) {
        final Map<Object, ErrorCodes.AdventNetErrorCode> errorCodeMap = this.getErrorCodeMap();
        if (errorCodeMap == null) {
            return -9999;
        }
        final Object ec = errorCodeMap.get(sqle.getErrorCode());
        return (ec == null) ? -9999 : ((ErrorCodes.AdventNetErrorCode)ec).getErrorCode();
    }
    
    protected void safeClose(final ResultSet rs) {
        if (rs == null) {
            return;
        }
        try {
            rs.close();
        }
        catch (final SQLException sqle) {
            Jdbc20DBAdapter.OUT.log(Level.INFO, "Exception while closing resultset {0}", sqle);
        }
    }
    
    protected void safeClose(final Statement stmt) {
        if (stmt == null) {
            return;
        }
        try {
            stmt.close();
        }
        catch (final SQLException sqle) {
            Jdbc20DBAdapter.OUT.log(Level.INFO, "Exception while closing statement {0}", sqle);
        }
    }
    
    protected void safeClose(final Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        }
        catch (final SQLException sqle) {
            Jdbc20DBAdapter.OUT.log(Level.INFO, "Exception while closing connection {0}", sqle);
        }
    }
    
    @Override
    public Map splitConnectionURL(String databaseURL) {
        if (databaseURL == null) {
            return new HashMap();
        }
        Map mapProperties = null;
        if (!PersistenceInitializer.onSAS()) {
            mapProperties = this.getURLProps(databaseURL);
        }
        else {
            mapProperties = new HashMap();
        }
        String server = null;
        String fileName = null;
        int port = 0;
        final String DB_PROTOCOL = "jdbc:";
        if (databaseURL.startsWith(DB_PROTOCOL)) {
            databaseURL = databaseURL.substring(DB_PROTOCOL.length());
            final int nextIndex = databaseURL.indexOf(":");
            databaseURL = databaseURL.substring(nextIndex + 1);
        }
        databaseURL = databaseURL.trim();
        char hostSepChar;
        char portSepChar;
        if (databaseURL.startsWith("//")) {
            databaseURL = databaseURL.substring(2);
            hostSepChar = '/';
            portSepChar = ':';
        }
        else {
            hostSepChar = ':';
            portSepChar = '/';
        }
        final int sep = databaseURL.indexOf(hostSepChar);
        if (sep <= 0) {
            return mapProperties;
        }
        server = databaseURL.substring(0, sep);
        fileName = databaseURL.substring(sep + 1);
        if (fileName.indexOf("?") != -1) {
            fileName = fileName.substring(0, fileName.indexOf("?"));
        }
        final int portSep = server.indexOf(portSepChar);
        if (portSep > 0) {
            port = Integer.parseInt(server.substring(portSep + 1));
            server = server.substring(0, portSep);
            mapProperties.put("Port", new Integer(port));
        }
        mapProperties.put("Server", server);
        mapProperties.put("DBName", fileName);
        return mapProperties;
    }
    
    protected String getDelimiterForURLProps() {
        return "&";
    }
    
    protected String getInitCharForURLProps() {
        return "?";
    }
    
    protected Map getURLProps(final String databaseURL) {
        final Map mapProperties = new HashMap();
        final String[] arr = databaseURL.split(this.getInitCharForURLProps().equals("?") ? "\\?" : this.getInitCharForURLProps(), 2);
        mapProperties.put("jdbcurl_props_separator", this.getInitCharForURLProps());
        mapProperties.put("urlWithoutProps", arr[0]);
        final String delimiter = this.getDelimiterForURLProps();
        mapProperties.put("url_props_delimiter", delimiter);
        if (arr.length != 2) {
            return mapProperties;
        }
        final String[] props = arr[1].split(delimiter);
        final Properties p = new Properties();
        for (int i = 0; i < props.length; ++i) {
            final String[] pair = props[i].split("=");
            String value = null;
            if (pair.length != 2) {
                Jdbc20DBAdapter.OUT.log(Level.WARNING, "Value is empty for the property {0}", pair[0]);
                value = "";
            }
            value = pair[1];
            ((Hashtable<String, String>)p).put(pair[0], value);
        }
        mapProperties.put("urlProps", p);
        return mapProperties;
    }
    
    @Override
    public SQLException handleSQLException(final SQLException sqle, final Connection connection, final boolean isWrite) throws SQLException {
        if (this.readOnlyMode && isWrite) {
            Jdbc20DBAdapter.OUT.log(Level.FINEST, "Ignoring the SQLException from the DB server for a write operation, as connection is set to read-only ", sqle);
            return null;
        }
        final int advErrorCode = this.getErrorCode(sqle);
        SQLException newSqle = null;
        if (sqle instanceof BatchUpdateException) {
            newSqle = new BatchUpdateException(sqle.getMessage(), sqle.getSQLState(), advErrorCode, ((BatchUpdateException)sqle).getUpdateCounts());
        }
        else {
            newSqle = new SQLException(sqle.getMessage(), sqle.getSQLState(), advErrorCode);
        }
        newSqle.initCause(sqle);
        newSqle.setStackTrace(sqle.getStackTrace());
        if (sqle.getNextException() != null) {
            newSqle.setNextException(sqle.getNextException());
        }
        throw newSqle;
    }
    
    @Override
    public void checkDBStatus(final String url) {
    }
    
    @Override
    public boolean abortBackup() throws Exception {
        return this.getBackupHandler().abortBackup();
    }
    
    @Override
    public Properties getDBProps() {
        final Properties properties = this.dbProps;
        String url = properties.getProperty("url");
        if (url != null && url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
            if (url.contains("\\$port")) {
                url.replaceAll("\\$port", properties.getProperty("port"));
            }
        }
        final Map prop = this.splitConnectionURL(url);
        Jdbc20DBAdapter.OUT.log(Level.INFO, "splitConnectionURL :: {0}", prop);
        properties.putAll(prop);
        return properties;
    }
    
    @Override
    public boolean isActive(final Connection c) {
        throw new RuntimeException("Implementation should be DB Specific.");
    }
    
    @Deprecated
    @Override
    public boolean setPassword(final String userName, final String currentPassword, final Connection c) throws Exception {
        throw new RuntimeException("Implementation should be DB Specific.");
    }
    
    @Override
    public List getShutDownStrings() {
        if (this.shutDownStrings == null) {
            this.shutDownStrings = new ArrayList();
            final Properties dbProps = PersistenceInitializer.getDefaultDBProps();
            final Enumeration e = dbProps.propertyNames();
            while (e.hasMoreElements()) {
                final String key = e.nextElement();
                if (key.toLowerCase().startsWith("shutdown.string")) {
                    this.shutDownStrings.add(dbProps.getProperty(key));
                }
            }
            Jdbc20DBAdapter.OUT.log(Level.INFO, "shutDownStrings :: {0}", this.shutDownStrings);
            this.checkForDBAlive = (this.shutDownStrings.size() > 0);
        }
        return this.shutDownStrings;
    }
    
    @Override
    public void handlePreExecute(final Connection conn, final List<String> tableNames, final List<String> columnNames) throws Exception {
    }
    
    @Override
    public void handlePostExecute(final Connection conn, final List<String> tableNames, final List<String> columnNames) throws Exception {
    }
    
    @Override
    public void prepareDatabase(final Connection conn) throws DataBaseException {
    }
    
    protected void validateAlterTableQuery(final Connection connection, final AlterTableQuery atq) throws SQLException, QueryConstructionException {
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(atq.getTableName());
        }
        catch (final MetaDataException mde) {
            throw new QueryConstructionException("Exception occurred while fetching the tableDefinition for the tableName :: [" + atq.getTableName() + "]", mde);
        }
        for (final AlterOperation ao : atq.getAlterOperations()) {
            switch (ao.getOperationType()) {
                case 1:
                case 19: {
                    final ColumnDefinition addColDef = (ColumnDefinition)ao.getAlterObject();
                    final long totalRowCount = this.getTotalRowCount(connection, ao.getTableName());
                    if (totalRowCount > 0L) {
                        ao.setFillUVHValue(true);
                    }
                    if (!addColDef.isNullable() && addColDef.getDefaultValue() == null && totalRowCount > 0L) {
                        throw new QueryConstructionException("It is not possible to add a non-nullable column [" + addColDef.getColumnName() + "] without a default value to a table [" + ao.getTableName() + "] which contains some data");
                    }
                    continue;
                }
                case 2:
                case 21: {
                    final ColumnDefinition modColDef = (ColumnDefinition)ao.getAlterObject();
                    final String columnName = modColDef.getColumnName();
                    final ColumnDefinition existingColDef = td.getColumnDefinitionByName(columnName);
                    final String oldDataType = existingColDef.getDataType();
                    final long nullCount = this.getNullCount(connection, ao.getTableName(), columnName);
                    final long totalCount = this.getTotalRowCount(connection, ao.getTableName());
                    final long notNullCount = totalCount - nullCount;
                    if (notNullCount > 0L) {
                        final boolean isDataTypeChanged = !modColDef.getDataType().equals(oldDataType);
                        if (isDataTypeChanged && DataTypeManager.getDataTypes().contains(modColDef.getDataType())) {
                            throw new QueryConstructionException("Datatype [" + oldDataType + "] of the column [" + td.getTableName() + "." + columnName + "] cannot be changed to EDT datatype [" + modColDef.getDataType() + "] when the column is not empty.");
                        }
                        if (isDataTypeChanged && this.convertibleDataTypes.get(oldDataType) != null && !this.convertibleDataTypes.get(oldDataType).contains(modColDef.getDataType())) {
                            throw new QueryConstructionException("Datatype [" + oldDataType + "] of the column [" + td.getTableName() + "." + columnName + "] can be changed to [" + modColDef.getDataType() + "] only when the column is empty or when the new datatype is convertible. See the convertableDataTypes for " + oldDataType + " :: " + this.convertibleDataTypes.get(oldDataType));
                        }
                        if (!isDataTypeChanged && (oldDataType.equals("CHAR") || oldDataType.equals("NCHAR") || oldDataType.equals("DECIMAL"))) {
                            if (existingColDef.getMaxLength() > modColDef.getMaxLength() && modColDef.getMaxLength() > 0) {
                                if (!ao.isMaxSizeReductionIgnored()) {
                                    throw new QueryConstructionException("MaxLength of the column [" + td.getTableName() + "." + columnName + "] - [" + existingColDef.getMaxLength() + "] cannot be reduced to [" + modColDef.getMaxLength() + "] when it contains some data");
                                }
                                modColDef.setMaxLength(existingColDef.getMaxLength());
                            }
                            if (existingColDef.getMaxLength() == -1 && modColDef.getMaxLength() > -1) {
                                if (!ao.isMaxSizeReductionIgnored()) {
                                    throw new QueryConstructionException("MaxLength of the column [" + td.getTableName() + "." + columnName + "] cannot be increased to [" + modColDef.getMaxLength() + "] from -1, when it contains some data");
                                }
                                modColDef.setMaxLength(existingColDef.getMaxLength());
                            }
                        }
                    }
                    if (existingColDef.isNullable() && !modColDef.isNullable() && nullCount > 0L) {
                        throw new QueryConstructionException("This column [" + columnName + "] has some null values, hence it cannot be modified as not-null.");
                    }
                    continue;
                }
            }
        }
    }
    
    protected int getNullCount(final Connection connection, final String tableName, final String columnName) throws SQLException {
        Statement s = null;
        ResultSet rs = null;
        try {
            s = connection.createStatement();
            final String countSQL = this.sqlGen.getSQLForCount(tableName, new Criteria(new Column(tableName, columnName), null, 0));
            rs = s.executeQuery(countSQL);
            return rs.next() ? rs.getInt(1) : 0;
        }
        catch (final QueryConstructionException e) {
            throw new SQLException(e.getMessage(), e);
        }
        finally {
            this.safeClose(rs);
            this.safeClose(s);
        }
    }
    
    @Override
    public long getTotalRowCount(final Connection connection, final String tableName) throws SQLException {
        Statement s = null;
        ResultSet rs = null;
        try {
            s = connection.createStatement();
            final String countSQL = this.sqlGen.getSQLForCount(tableName, null);
            rs = s.executeQuery(countSQL);
            return rs.next() ? rs.getLong(1) : 0L;
        }
        catch (final QueryConstructionException e) {
            throw new SQLException(e.getMessage(), e);
        }
        finally {
            this.safeClose(rs);
            this.safeClose(s);
        }
    }
    
    @Override
    public void migrateScharDatatype(final List<ColumnDefinition> columnDefinitions) throws SQLException {
        final Connection conn = this.datasource.getConnection();
        try {
            for (final ColumnDefinition oldColDef : columnDefinitions) {
                final String tableName = oldColDef.getTableName();
                if (this.isMigrateSCHARRequired(oldColDef)) {
                    Jdbc20DBAdapter.OUT.log(Level.INFO, "Going to migrate SCHAR column in [{0}.{1}]", new Object[] { tableName, oldColDef.getColumnName() });
                    final AlterTableQueryImpl aq = new AlterTableQueryImpl(tableName);
                    aq.modifyColumn(oldColDef.getColumnName(), oldColDef);
                    this.alterTable(conn, aq);
                }
                else {
                    Jdbc20DBAdapter.OUT.log(Level.INFO, "SCHAR column datatype migration ignored for [{0}.{1}]", new Object[] { tableName, oldColDef.getColumnName() });
                }
            }
        }
        catch (final QueryConstructionException qce) {
            final SQLException sqle = new SQLException(qce.getMessage());
            sqle.initCause(qce);
            throw sqle;
        }
        finally {
            this.safeClose(conn);
        }
    }
    
    protected boolean isMigrateSCHARRequired(final ColumnDefinition colDef) {
        return false;
    }
    
    @Override
    public List<AlterTableQuery> getAlterQueryForCopyAllConstraints(final CreateTableLike cloneTableDetails, final boolean copyOnlyFK) throws Exception {
        final TableDefinition tabDef = cloneTableDetails.getCloneTableDefinition();
        if (tabDef == null) {
            throw new QueryConstructionException("Table definition cannot be null");
        }
        final List<AlterTableQuery> query = new ArrayList<AlterTableQuery>();
        final String tableName = tabDef.getTableName();
        final AlterTableQuery addQuery = new AlterTableQueryImpl(tableName);
        boolean isProcessed = false;
        final PrimaryKeyDefinition pkDef = tabDef.getPrimaryKey();
        final List<ForeignKeyDefinition> fkList = tabDef.getForeignKeyList();
        if (fkList != null && !fkList.isEmpty()) {
            isProcessed = true;
            for (final ForeignKeyDefinition fkDef : fkList) {
                addQuery.addForeignKey(fkDef);
            }
        }
        final List<UniqueKeyDefinition> ukList = tabDef.getUniqueKeys();
        if (ukList != null && !ukList.isEmpty()) {
            isProcessed = true;
            for (final UniqueKeyDefinition ukDef : ukList) {
                addQuery.addUniqueKey(ukDef);
            }
        }
        final List<IndexDefinition> idxDefList = tabDef.getIndexes();
        if (idxDefList != null && !idxDefList.isEmpty()) {
            isProcessed = true;
            for (final IndexDefinition idxDef : idxDefList) {
                addQuery.addIndex(idxDef);
            }
        }
        final AlterTableQuery addPK = new AlterTableQueryImpl(tableName, 9);
        addPK.setConstraintName(pkDef.getName());
        addPK.setPKColumns(pkDef.getColumnList());
        query.add(addPK);
        if (isProcessed) {
            query.add(addQuery);
        }
        return query;
    }
    
    @Override
    public List<AlterTableQuery> getATQForRemoveAllConstraints(final ArchiveTable table) throws MetaDataException, QueryConstructionException {
        final List<AlterTableQuery> query = new ArrayList<AlterTableQuery>();
        final AlterTableQuery removeQuery = new AlterTableQueryImpl(table.getArchiveTableName());
        boolean isProcessed = false;
        final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(table.getTableName());
        if (tabDef == null) {
            throw new QueryConstructionException("Unknown table name [" + table.getTableName() + "] specified");
        }
        final List<ForeignKeyDefinition> fkList = tabDef.getForeignKeyList();
        if (fkList != null && !fkList.isEmpty()) {
            isProcessed = true;
            for (final ForeignKeyDefinition fkDef : fkList) {
                removeQuery.removeForeignKey(fkDef.getName());
            }
        }
        final List<UniqueKeyDefinition> ukList = tabDef.getUniqueKeys();
        if (ukList != null && !ukList.isEmpty()) {
            isProcessed = true;
            for (final UniqueKeyDefinition ukDef : ukList) {
                removeQuery.removeUniqueKey(ukDef.getName());
            }
        }
        final List<IndexDefinition> idxDefList = tabDef.getIndexes();
        if (idxDefList != null && !idxDefList.isEmpty()) {
            isProcessed = true;
            for (final IndexDefinition idxDef : idxDefList) {
                removeQuery.dropIndex(idxDef.getName());
            }
        }
        final PrimaryKeyDefinition pkDef = tabDef.getPrimaryKey();
        final AlterTableQuery removePK = new AlterTableQueryImpl(table.getArchiveTableName(), 8);
        removePK.setConstraintName(pkDef.getName());
        if (isProcessed) {
            query.add(removeQuery);
        }
        query.add(removePK);
        return query;
    }
    
    @Deprecated
    protected List<String> getAllProductTablesFromDB(Connection connection) throws SQLException, QueryConstructionException, MetaDataException {
        Statement stmt = null;
        ResultSet rs = null;
        final List<String> tableList = new ArrayList<String>();
        boolean isNewConn = false;
        try {
            if (connection == null) {
                connection = RelationalAPI.getInstance().getConnection();
                isNewConn = true;
            }
            stmt = connection.createStatement();
            final SelectQuery sq = new SelectQueryImpl(Table.getTable("TableDetails"));
            sq.addSelectColumn(Column.getColumn("TableDetails", "TABLE_NAME"));
            sq.addSortColumn(new SortColumn(Column.getColumn("TableDetails", "TABLE_ORDER"), true));
            final String sqlString = this.sqlGen.getSQLForSelect(sq);
            rs = stmt.executeQuery(sqlString);
            while (rs.next()) {
                final String tableName = rs.getString(1);
                final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
                if (tableDef.isTemplate()) {
                    final TemplateMetaHandler tmh = MetaDataUtil.getTemplateHandler(tableDef.getModuleName());
                    tableList.addAll(tmh.getTemplateInstancesForReInitialize(tableName));
                }
                else {
                    if (!tableDef.creatable()) {
                        continue;
                    }
                    tableList.add(tableName);
                }
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (isNewConn && connection != null) {
                    connection.close();
                }
            }
            catch (final Exception exc) {
                exc.printStackTrace();
            }
        }
        return tableList;
    }
    
    protected void zip(final String path) throws Exception {
        final List<String> dirs = new ArrayList<String>();
        dirs.add(path);
        this.zip(path + ".zip", dirs);
    }
    
    protected void zip(final String zipFileName, final List<String> directoriesToBeArchived) throws Exception {
        this.zip(zipFileName, directoriesToBeArchived, false, null);
    }
    
    protected void zip(final String zipFileName, final List<String> directoriesToBeArchived, final boolean zipSubDirs, final Properties prefProps) throws Exception {
        ZipUtils.zip(zipFileName, (List)directoriesToBeArchived, zipSubDirs, prefProps);
    }
    
    protected void unZip(final String src, final String dst) throws Exception {
        ZipUtils.unZip(src, dst);
    }
    
    protected boolean isFileExistsInZip(final String zipNameWithFullPath, final String entryNameWithPackage) throws Exception {
        return ZipUtils.isFileExistsInZip(zipNameWithFullPath, entryNameWithPackage);
    }
    
    @Override
    public String getIdentifiersFileName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public DBInitializer getDBInitializer() {
        throw new UnsupportedOperationException("Not supported for this DB.");
    }
    
    @Override
    public boolean startDB(final String connectionURL, final String userName, final String password) throws Exception {
        final Map properties = this.splitConnectionURL(connectionURL);
        final String host = properties.get("Server");
        final Integer port = properties.get("Port");
        return this.getDBInitializer().startDBServer(port, host, userName, password);
    }
    
    @Override
    public void stopDB(final String connectionURL, final String userName, final String password) throws Exception {
        final Map properties = this.splitConnectionURL(connectionURL);
        final String host = properties.get("Server");
        final Integer port = properties.get("Port");
        this.getDBInitializer().stopDBServer(port, host, userName, password);
    }
    
    @Override
    public boolean isColumnModified(final ColumnDefinition oldColumnDefinition, final ColumnDefinition newColumnDefinition, final List<String> changedAttributes) {
        boolean isExecutable = true;
        if (changedAttributes.contains("data-type") && ((oldColumnDefinition.getDataType().equals("CHAR") && newColumnDefinition.getDataType().equals("NCHAR")) || (oldColumnDefinition.getDataType().equals("NCHAR") && newColumnDefinition.getDataType().equals("CHAR")))) {
            Jdbc20DBAdapter.OUT.log(Level.INFO, "CHAR/NCHAR to NCHAR/CHAR data-type change is skipped.");
            changedAttributes.remove("data-type");
        }
        if (changedAttributes.contains("max-size")) {
            final String newDataType = newColumnDefinition.getDataType();
            if (newDataType.equals("BOOLEAN") || newDataType.equals("DATE") || newDataType.equals("DATETIME") || newDataType.equals("TIME") || newDataType.equals("TIMESTAMP") || newDataType.equals("BLOB") || newDataType.equals("SBLOB") || newDataType.equals("TINYINT")) {
                Jdbc20DBAdapter.OUT.log(Level.INFO, "max-size change is skipped.");
                changedAttributes.remove("max-size");
            }
            else {
                try {
                    final String oldDBDataType = this.sqlGen.getDBDataType(oldColumnDefinition);
                    final String newDBDataType = this.sqlGen.getDBDataType(newColumnDefinition);
                    if (oldDBDataType.equals(newDBDataType)) {
                        changedAttributes.remove("max-size");
                    }
                }
                catch (final QueryConstructionException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        if (!changedAttributes.contains("data-type") && !changedAttributes.contains("max-size") && !changedAttributes.contains("precision") && !changedAttributes.contains("default-value") && !changedAttributes.contains("nullable") && !changedAttributes.contains("unique")) {
            isExecutable = false;
        }
        return isExecutable;
    }
    
    @Override
    public boolean isIndexModified(final IndexDefinition oldIndexDefinition, final IndexDefinition newIndexDefinition, final List<String> changedAttributes) {
        return Optional.ofNullable(changedAttributes).orElse(Collections.emptyList()).stream().anyMatch(changedAttribute -> changedAttribute.startsWith("index-column") || changedAttribute.startsWith("isAscending of"));
    }
    
    protected int getFetchSize() {
        return 200;
    }
    
    @Override
    public Statement createStatement(final Connection conn) throws SQLException {
        final Statement stmt = conn.createStatement();
        stmt.setFetchSize(200);
        return stmt;
    }
    
    @Override
    public boolean execute(final PreparedStatement stmt) throws SQLException {
        boolean executed = false;
        try {
            executed = stmt.execute();
            return executed;
        }
        catch (final SQLException sqle) {
            Jdbc20DBAdapter.OUT.log(Level.FINE, "DBAdapter.execute(Statement): Exception while executing Query :\n", sqle);
            throw this.handleSQLException(sqle, stmt.getConnection(), true);
        }
    }
    
    @Override
    public PreparedStatement createPreparedStatement(final Connection conn, final String query, final Object... args) throws SQLException {
        final PreparedStatement statement = conn.prepareStatement(query);
        statement.setFetchSize(200);
        return this.setPreparedStatement(statement, args);
    }
    
    protected PreparedStatement setPreparedStatement(final PreparedStatement statement, final Object... args) throws SQLException {
        for (int i = 0; i < args.length; ++i) {
            if (args[i] instanceof InputStream) {
                statement.setBinaryStream(i + 1, (InputStream)args[i]);
            }
            else {
                statement.setObject(i + 1, args[i]);
            }
        }
        return statement;
    }
    
    @Override
    public PreparedStatement createPreparedStatement(final Connection conn, int fetchsize, final String query, final Object... args) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query, 1003, 1007);
        ps = this.setPreparedStatement(ps, args);
        fetchsize = ((fetchsize <= 0) ? this.getFetchSize() : fetchsize);
        ps.setFetchSize(fetchsize);
        return ps;
    }
    
    @Override
    public int[] executeBatch(final Connection connection, final String query, final Collection<Object[]> args) throws SQLException {
        try (final PreparedStatement stmt = connection.prepareStatement(query)) {
            for (final Object[] a : args) {
                this.setPreparedStatement(stmt, a).addBatch();
            }
            return stmt.executeBatch();
        }
    }
    
    @Override
    public Statement createStatement(final Connection conn, int fetchsize) throws SQLException {
        final Statement stmt = conn.createStatement(1003, 1007);
        fetchsize = ((fetchsize <= 0) ? this.getFetchSize() : fetchsize);
        stmt.setFetchSize(fetchsize);
        return stmt;
    }
    
    @Override
    public long getApproxRowCount(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        ResultSet rs = null;
        long rowCount = -1L;
        try {
            rs = metaData.getIndexInfo(null, null, tableName, true, true);
            final String pkNameOfTheTable = this.getPKNameOfTheTable(tableName, metaData);
            while (rs.next()) {
                final String indexName = rs.getString("INDEX_NAME");
                if (indexName != null && rs.getString("INDEX_NAME").equals(pkNameOfTheTable)) {
                    rowCount = rs.getLong("CARDINALITY");
                    break;
                }
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
        return rowCount;
    }
    
    @Override
    public String getPKNameOfTheTable(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        ResultSet primaryKey = null;
        try {
            primaryKey = metaData.getPrimaryKeys(null, null, tableName);
            if (primaryKey.next()) {
                return primaryKey.getString("PK_NAME");
            }
            return null;
        }
        finally {
            if (primaryKey != null) {
                primaryKey.close();
            }
        }
    }
    
    @Override
    public List<String> getPKColumnNameOfTheTable(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        ResultSet primaryKey = null;
        try {
            final List<String> pkCol = new ArrayList<String>();
            primaryKey = metaData.getPrimaryKeys(null, null, tableName);
            while (primaryKey.next()) {
                pkCol.add(primaryKey.getString("COLUMN_NAME"));
            }
            return pkCol;
        }
        finally {
            if (primaryKey != null) {
                primaryKey.close();
            }
        }
    }
    
    @Override
    public boolean isMaxLengthComparable(final String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("datatype value can't be null.");
        }
        return "VARCHAR".equals(dataType.toUpperCase(Locale.ENGLISH));
    }
    
    @Override
    public void createDB(final Connection connection, final String dbName, final String userName, final String password, final boolean ignoreIfExists) throws SQLException {
        ResultSet catalogs = null;
        try {
            boolean isDBExists = false;
            catalogs = connection.getMetaData().getCatalogs();
            while (catalogs.next()) {
                if (dbName.equals(catalogs.getString("TABLE_CAT"))) {
                    Jdbc20DBAdapter.OUT.warning("DataBase already exists. Hence ignoring database creation.");
                    isDBExists = true;
                    break;
                }
            }
            if (!isDBExists) {
                this.createDB(connection, dbName, userName, password);
                Jdbc20DBAdapter.OUT.info("DataBase " + dbName + " successfully created...");
            }
        }
        finally {
            if (catalogs != null) {
                catalogs.close();
            }
        }
    }
    
    @Override
    public String getTableName(final String schemaString) {
        String retStr = null;
        if (schemaString != null) {
            final String[] split = schemaString.split("\\.");
            retStr = split[split.length - 1].trim();
        }
        return retStr;
    }
    
    @Override
    public void loadFunctionTemplates() throws Exception {
        if (!PersistenceInitializer.getConfigurationList("sql_function_pattern_file").isEmpty()) {
            throw new UnsupportedOperationException("Common Configuration for sql_funtion_pattern is not supported, Instead use dbspecific sql_function_pattern ");
        }
        final List<String> fileNames = PersistenceInitializer.getConfigurationList(PersistenceInitializer.getConfigurationValue("DBName"));
        File patternFile = null;
        FileInputStream fis = null;
        final Properties functionTemplates = new Properties();
        for (final String fileName : fileNames) {
            if (fileName != null && !fileName.trim().equals("")) {
                try {
                    patternFile = new File(Jdbc20DBAdapter.server_home + File.separator + fileName);
                    if (patternFile.exists()) {
                        Jdbc20DBAdapter.OUT.log(Level.INFO, "Loading the function patterns from the file :: [{0}]", patternFile);
                        fis = new FileInputStream(patternFile);
                        functionTemplates.load(fis);
                        this.sqlGen.setFunctionTemplates(functionTemplates);
                    }
                    else {
                        Jdbc20DBAdapter.OUT.log(Level.SEVERE, "File not found at the specified Path :: [{0}]", patternFile);
                        ConsoleOut.println("Function Patterns file has been set in persistence-configurations.xml but not found at the specified path::" + patternFile.getAbsolutePath() + ". Either remove the property or add file at the specified path");
                    }
                }
                catch (final Exception e) {
                    Jdbc20DBAdapter.OUT.log(Level.SEVERE, "Exception occurred while parsing the file :: [{0}]", patternFile);
                    throw e;
                }
                finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
                Jdbc20DBAdapter.OUT.log(Level.FINE, "function templates :: {0} ", functionTemplates);
            }
        }
    }
    
    @Override
    public BackupHandler getBackupHandler() {
        try {
            if (this.backupHandler == null) {
                String backupHandlerClass = null;
                if (this.dbProps == null) {
                    backupHandlerClass = this.getBackupHandlerClassName();
                }
                else {
                    backupHandlerClass = this.getDBProps().getProperty("backuphandler", this.getBackupHandlerClassName());
                }
                if (backupHandlerClass != null && !backupHandlerClass.equals("")) {
                    this.backupHandler = (BackupHandler)Thread.currentThread().getContextClassLoader().loadClass(backupHandlerClass).newInstance();
                    ((AbstractBackupHandler)this.backupHandler).initDBAdapter(this);
                    ((AbstractBackupHandler)this.backupHandler).initDBInitializer(this.getDBInitializer());
                    ((AbstractBackupHandler)this.backupHandler).initSQLGenerator(this.sqlGen);
                    ((AbstractBackupHandler)this.backupHandler).initDataSource(this.datasource);
                    ((AbstractBackupHandler)this.backupHandler).initialize(this.getDBProps());
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return this.backupHandler;
    }
    
    @Override
    public List<String> getAllDatabaseNames(final Connection c) {
        final List<String> dbNames = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = c.getMetaData().getCatalogs();
            while (rs.next()) {
                dbNames.add(rs.getString(1));
            }
        }
        catch (final SQLException sqle) {
            sqle.printStackTrace();
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final SQLException sqle2) {
                sqle2.printStackTrace();
            }
        }
        return dbNames;
    }
    
    @Override
    public boolean isTableNotFoundException(final SQLException sqle) {
        throw new UnsupportedOperationException("No default implementation.");
    }
    
    @Override
    public int[] executeBatch(final PreparedStatement prstmt) throws SQLException {
        if (prstmt == null) {
            throw new SQLException("preparedstatement is null");
        }
        try {
            return prstmt.executeBatch();
        }
        catch (final BatchUpdateException bae) {
            throw this.handleSQLException(bae, null, false);
        }
        catch (final SQLException sqle) {
            throw this.handleSQLException(sqle, null, false);
        }
    }
    
    @Override
    public int[] executeBatch(final Statement stmt) throws SQLException {
        if (stmt == null) {
            throw new SQLException("Statement is null");
        }
        try {
            return stmt.executeBatch();
        }
        catch (final BatchUpdateException bae) {
            throw this.handleSQLException(bae, null, false);
        }
        catch (final SQLException sqle) {
            throw this.handleSQLException(sqle, null, false);
        }
    }
    
    @Override
    public void execBulk(final BulkLoad bulk) throws SQLException, IOException, MetaDataException {
        Jdbc20DBAdapter.OUT.log(Level.FINE, "execBulk is not supported !!!");
    }
    
    @Override
    public void addBatch(final Object[] rowLvlByteValues, final BulkLoad bulk) throws IOException, SQLException {
        Jdbc20DBAdapter.OUT.log(Level.FINE, "addBatch is not supported !!!");
    }
    
    @Override
    public BulkInsertObject createBulkInsertObject(final BulkLoad bulk) throws IOException, SQLException, QueryConstructionException, MetaDataException {
        final DataBufferStream dbs = new DataBufferStream(bulk.getBuffersize());
        final BulkInsertObject bio = new BulkInsertObject(dbs);
        if (MetaDataUtil.getTableDefinitionByName(bulk.getTableName()) != null) {
            this.loadColumnDetails(bulk, bio, null);
        }
        else {
            this.loadColumnDetailsFromDB(bulk, bio, bulk.getConnection().getMetaData());
        }
        bio.setSQL(BulkLoadStatementGenerator.getBulkSQL(bulk, bio, this.getSQLGenerator()));
        return bio;
    }
    
    @Override
    public void closeBulkInsertObject(final BulkLoad bulk) throws SQLException, IOException {
        ((DataBufferStream)bulk.getBulkInsertObject().getBulkObject()).getOutputStream().flush();
        ((DataBufferStream)bulk.getBulkInsertObject().getBulkObject()).getOutputStream().close();
    }
    
    public void loadColumnDetails(final BulkLoad bulk, final BulkInsertObject bio, final String schema) throws SQLException, MetaDataException {
        try {
            if (null == MetaDataUtil.getTableDefinitionByName(bulk.getTableName())) {
                throw new MetaDataException("Table present is not Mickey Based!!!");
            }
            final List<String> newColumns = bulk.getColumnNames();
            final List<String> columnNames = new ArrayList<String>();
            final List<String> colTypeNames = new ArrayList<String>();
            final List<Integer> colTypes = new ArrayList<Integer>();
            final List<String> dynamicColumns = new ArrayList<String>();
            int indexVal = 1;
            int index = 0;
            final int[] recomputedIndices = new int[bulk.getColumnNames().size()];
            for (final String columnName : newColumns) {
                if (MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getColumnDefinitionByName(columnName) != null && MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getColumnDefinitionByName(columnName).isDynamic()) {
                    dynamicColumns.add(columnName);
                }
                else {
                    columnNames.add(columnName);
                    colTypeNames.add(MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getColumnDefinitionByName(columnName).getDataType());
                    colTypes.add(MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getColumnDefinitionByName(columnName).getSQLType());
                    recomputedIndices[index] = indexVal++;
                }
                ++index;
            }
            bio.setColNames(columnNames);
            bio.setColTypeNames(colTypeNames);
            bio.setColTypes(colTypes);
            if (!dynamicColumns.isEmpty()) {
                if (MetaDataUtil.getTableDefinitionByName(bulk.getTableName()) == null || MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getDynamicColumnType() == null) {
                    throw new MetaDataException("Dynamic Column Handler not defined for table :: " + bulk.getTableName());
                }
                this.getDCAdapterForTable(bulk.getTableName()).loadDynamicColumnDetails(bio, bulk, dynamicColumns, recomputedIndices, indexVal);
            }
            bulk.setRecomputedIndices(recomputedIndices);
            Jdbc20DBAdapter.OUT.log(Level.FINE, "Loaded datatype from MetaData!!!");
        }
        catch (final MetaDataException mde) {
            throw mde;
        }
    }
    
    public void loadColumnDetailsFromDB(final BulkLoad bulk, final BulkInsertObject bio, final DatabaseMetaData metaData) throws SQLException {
        ResultSet rs = null;
        int index = 1;
        int[] recomputedIndices = null;
        try {
            rs = metaData.getColumns(null, null, bulk.getTableName(), null);
            final List<String> columnNamesDB = new ArrayList<String>();
            final List<Integer> columnTypesDB = new ArrayList<Integer>();
            final List<String> columnTypeNamesDB = new ArrayList<String>();
            while (rs.next()) {
                columnTypesDB.add(rs.getInt("DATA_TYPE"));
                columnNamesDB.add(rs.getString("COLUMN_NAME"));
                columnTypeNamesDB.add(rs.getString("TYPE_NAME"));
            }
            if (bulk.getColumnNames() != null) {
                bio.setColNames(bulk.getColumnNames());
            }
            else {
                bio.setColNames(columnNamesDB);
                bulk.setColumnNames(columnNamesDB);
            }
            bio.setColTypeNames(columnTypeNamesDB);
            bio.setColTypes(columnTypesDB);
            recomputedIndices = new int[bulk.getColumnNames().size()];
            for (final String columnName : bio.getColNames()) {
                recomputedIndices[bulk.getColumnNames().indexOf(columnName)] = index++;
            }
            bulk.setRecomputedIndices(recomputedIndices);
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
    
    public byte[] getBytesForObject(final Object value) throws SQLException, IOException {
        if (value instanceof Integer) {
            return Integer.toString((int)value).getBytes();
        }
        if (value instanceof Long) {
            return Long.toString((long)value).getBytes();
        }
        if (value instanceof Short) {
            return Short.toString((short)value).getBytes();
        }
        if (value instanceof BigDecimal) {
            return Double.toString(((BigDecimal)value).doubleValue()).getBytes();
        }
        if (value instanceof Boolean) {
            return String.valueOf(value).getBytes();
        }
        if (value instanceof byte[]) {
            return (byte[])value;
        }
        if (value instanceof Date) {
            return ((Date)value).toString().getBytes();
        }
        if (value instanceof Time) {
            return ((Time)value).toString().getBytes();
        }
        if (value instanceof Double) {
            return Double.toString((double)value).getBytes();
        }
        if (value instanceof Float) {
            return Float.toString((float)value).getBytes();
        }
        if (value instanceof String) {
            return ((String)value).getBytes();
        }
        if (value instanceof Timestamp) {
            return ((Timestamp)value).toString().getBytes();
        }
        Jdbc20DBAdapter.OUT.log(Level.FINE, "Unsupported Type :: Input Value : [{0}], hence returning NULL", value);
        return "\\N".getBytes();
    }
    
    @Override
    public int fileBackup(final String backupDir, final String backupFileName, final List<String> directoriesToBeArchived, final String versionHandlerName, final Properties prefProps) throws Exception {
        return this.getBackupHandler().doFileBackup(backupDir, backupFileName, directoriesToBeArchived, versionHandlerName, prefProps).getBackupStatus().getValue();
    }
    
    public boolean getIsAutoQuoteEnabled() {
        return Jdbc20DBAdapter.isAutoQuoteEnabled;
    }
    
    @Override
    public RestoreHandler getRestoreHandler() {
        try {
            if (this.restoreHandler == null) {
                String restoreHandlerClass = null;
                if (this.dbProps == null) {
                    restoreHandlerClass = this.getRestoreHandlerClassName();
                }
                else {
                    restoreHandlerClass = this.getDBProps().getProperty("restorehandler", this.getRestoreHandlerClassName());
                }
                if (restoreHandlerClass != null && !restoreHandlerClass.equals("")) {
                    this.restoreHandler = (RestoreHandler)Thread.currentThread().getContextClassLoader().loadClass(restoreHandlerClass).newInstance();
                    ((AbstractRestoreHandler)this.restoreHandler).initDBAdapter(this);
                    ((AbstractRestoreHandler)this.restoreHandler).initDBInitializer(this.getDBInitializer());
                    ((AbstractRestoreHandler)this.restoreHandler).initSQLGenerator(this.sqlGen);
                    ((AbstractRestoreHandler)this.restoreHandler).initDataSource(this.datasource);
                    ((AbstractRestoreHandler)this.restoreHandler).initialize(this.getDBProps());
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return this.restoreHandler;
    }
    
    @Override
    public boolean isBundledDB() {
        return false;
    }
    
    public boolean isLoopbackAddress() {
        InetAddress specifiedAddress = null;
        try {
            specifiedAddress = InetAddress.getByName(this.getDBProps().getProperty("Server"));
        }
        catch (final UnknownHostException e1) {
            e1.printStackTrace();
        }
        return specifiedAddress == null || specifiedAddress.isLoopbackAddress();
    }
    
    protected String getBackupHandlerClassName() {
        throw new UnsupportedOperationException("No Specific Backup Handler");
    }
    
    protected String getRestoreHandlerClassName() {
        throw new UnsupportedOperationException("No Specific Restore Handler");
    }
    
    @Override
    public ResultSet getFKMetaData(final Connection connection, final String tableName) throws SQLException {
        return connection.getMetaData().getImportedKeys(connection.getCatalog(), this.getCurrentSchema(connection), tableName);
    }
    
    protected ResultSet getColumnNamesFromDB(final DatabaseMetaData metaData, final String schemaName, final String tableName) throws SQLException {
        return metaData.getColumns(null, schemaName, tableName, "%");
    }
    
    @Override
    public List<String> getColumnNamesFromDB(final String tableName, final String columnPattern, final DatabaseMetaData metaData) throws SQLException {
        ResultSet rs = null;
        try {
            final String escapedTableName = tableName.replace("_", metaData.getSearchStringEscape() + "_");
            rs = metaData.getColumns(null, null, escapedTableName, columnPattern);
            final List<String> columnNamesDB = new ArrayList<String>();
            while (rs.next()) {
                columnNamesDB.add(rs.getString(4));
            }
            return columnNamesDB;
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
    
    @Override
    public String getDBSpecificExceptionSorterName() {
        return "com.adventnet.db.adapter.BaseExceptionSorter";
    }
    
    @Override
    public PreparedStatement createInsertStatement(final String tableName, final Connection conn) throws MetaDataException, QueryConstructionException, SQLException {
        final String insertSQL = this.sqlGen.getSQLForBatchInsert(tableName);
        PreparedStatement ps = null;
        if (insertSQL != null) {
            ps = conn.prepareStatement(insertSQL);
        }
        return ps;
    }
    
    @Override
    public PreparedStatement createUpdateStatement(final String tableName, final int[] changedIndexes, final Connection conn) throws SQLException, MetaDataException, QueryConstructionException {
        final String updateSQL = this.sqlGen.getSQLForBatchUpdate(tableName, changedIndexes);
        PreparedStatement ps = null;
        if (updateSQL != null) {
            ps = conn.prepareStatement(updateSQL);
        }
        return ps;
    }
    
    @Override
    public PreparedStatement createUpdateStatement(final UpdateQuery query, final Connection conn) throws SQLException, MetaDataException, QueryConstructionException {
        final DCAdapter dcAdapter = this.getDCAdapterForTable(query.getTableName());
        UpdateQuery modifiedQuery = null;
        if (dcAdapter != null) {
            modifiedQuery = dcAdapter.getModifiedUpdateQuery(query);
        }
        else {
            modifiedQuery = query;
        }
        final String updateSQL = this.sqlGen.getSQLForBatchUpdate(modifiedQuery);
        PreparedStatement ps = null;
        if (updateSQL != null) {
            ps = conn.prepareStatement(updateSQL);
        }
        return ps;
    }
    
    @Override
    public boolean executeDelete(final String tableName, final Criteria cri, final Connection conn) throws SQLException, QueryConstructionException {
        String deleteSQL = null;
        Statement stmt = null;
        try {
            final List tabList = new ArrayList();
            tabList.add(Table.getTable(tableName));
            QueryUtil.setTypeForCriteria(cri, tabList);
            deleteSQL = this.sqlGen.getSQLForDelete(tableName, cri);
            if (deleteSQL != null) {
                Jdbc20DBAdapter.OUT.log(Level.FINEST, "Executing delete SQL: {0}", deleteSQL);
                stmt = conn.createStatement();
                return this.executeUpdate(stmt, deleteSQL) > 0;
            }
            return false;
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    @Override
    public boolean hasPermissionForBackup(final Connection conn) throws Exception {
        throw new UnsupportedOperationException("This method is not implemented for the current DB. This should be implemented DB specifically");
    }
    
    @Override
    public boolean hasPermissionForRestore(final Connection conn) throws Exception {
        throw new UnsupportedOperationException("This method is not implemented for the current DB. This should be implemented DB specifically");
    }
    
    @Override
    public long getSizeOfDB(final Connection conn) {
        throw new UnsupportedOperationException("This method is not implemented for the current DB. This should be implemented DB specifically");
    }
    
    @Override
    public String getDBSystemProperty(final Connection conn, final String property) throws SQLException, QueryConstructionException {
        throw new UnsupportedOperationException("This method is not implemented for the current DB. This should be implemented DB specifically");
    }
    
    protected void initDCAdapters() {
        this.dcTypeVsAdapter = new HashMap<String, DCAdapter>();
        final List<String> dcTypes = DCManager.getDCTypes();
        for (int i = 0; i < dcTypes.size(); ++i) {
            final Properties p = DCManager.getProps(dcTypes.get(i) + "." + this.getDBType());
            if (p != null && p.getProperty("dcadapter") != null) {
                DCAdapter dcAdapter = null;
                try {
                    dcAdapter = (DCAdapter)Thread.currentThread().getContextClassLoader().loadClass(p.getProperty("dcadapter")).newInstance();
                }
                catch (final ClassNotFoundException e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                    Jdbc20DBAdapter.OUT.log(Level.SEVERE, "Error while trying to instantiate DCAdapter for dc type :: {0} with exception {1}", new Object[] { dcTypes.get(i), ex });
                }
                dcAdapter.initDBAdapter(this);
                this.dcTypeVsAdapter.put(dcTypes.get(i), dcAdapter);
            }
        }
    }
    
    @Override
    public DCAdapter getDCAdapterForTable(final String tableName) {
        return this.getDCAdapter(this.getDCTypeForTable(tableName));
    }
    
    protected String getDCTypeForTable(final String tableName) {
        try {
            return MetaDataUtil.getTableDefinitionByName(tableName).getDynamicColumnType();
        }
        catch (final MetaDataException e) {
            throw new IllegalArgumentException("Problem while fetching dynamic column type for given table :: " + tableName, e);
        }
    }
    
    @Override
    public DCAdapter getDCAdapter(final String dcType) {
        if (dcType == null) {
            return null;
        }
        return this.dcTypeVsAdapter.get(dcType);
    }
    
    @Override
    public void setValue(final PreparedStatement ps, final int columnIndex, final Map<String, Integer> sqlTypes, final Map<String, Object> value, final String dcType) throws SQLException {
        if (dcType != null) {
            this.getDCAdapter(dcType).setValue(ps, columnIndex, sqlTypes, value);
        }
        else {
            this.setValue(ps, columnIndex, sqlTypes.get(sqlTypes.keySet().iterator().next()), value);
        }
    }
    
    @Override
    public void logDatabaseDetails() {
        final DBInitializer dbInitializer = this.getDBInitializer();
        try {
            Jdbc20DBAdapter.OUT.log(Level.INFO, "DB Version            :: " + dbInitializer.getVersion());
            Jdbc20DBAdapter.OUT.log(Level.INFO, "DB Architecture       :: " + dbInitializer.getDBArchitecture());
            Jdbc20DBAdapter.OUT.log(Level.INFO, "DB Data Directory     :: " + dbInitializer.getDBDataDirectory());
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String getDBType() {
        return "";
    }
    
    protected String getDBSpecificSQLModifierName() throws Exception {
        throw new UnsupportedOperationException("ANSI :: No Specific SQLModifier");
    }
    
    @Override
    public SQLModifier getSQLModifier() {
        return this.sqlModifier;
    }
    
    @Override
    public String getDBSpecificAbortHandlerName() {
        return null;
    }
    
    @Override
    public boolean changePassword(final String userName, final String oldPassword, final String newPassword, final Connection c) throws SQLException {
        throw new RuntimeException("Implementation should be DB Specific.");
    }
    
    @Override
    public boolean isReadOnly(final Connection connection) throws Exception {
        return false;
    }
    
    @Override
    public Statement createReadOnlyStatement(final Connection conn) throws SQLException {
        return this.createStatement(conn, 0);
    }
    
    static {
        OUT = Logger.getLogger(Jdbc20DBAdapter.class.getName());
        Jdbc20DBAdapter.isAutoQuoteEnabled = false;
        Jdbc20DBAdapter.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
}
