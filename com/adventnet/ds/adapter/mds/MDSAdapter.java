package com.adventnet.ds.adapter.mds;

import java.util.Collection;
import com.zoho.mickey.db.SQLModifier;
import com.adventnet.db.adapter.DCAdapter;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.UpdateQuery;
import java.sql.ResultSet;
import com.adventnet.db.adapter.DBInitializer;
import com.adventnet.db.adapter.RestoreHandler;
import java.util.logging.Level;
import com.adventnet.db.adapter.BackupHandler;
import java.util.concurrent.ExecutorService;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.adapter.BulkInsertObject;
import com.adventnet.ds.query.BulkLoad;
import java.sql.DatabaseMetaData;
import java.util.Map;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.ds.query.ArchiveTable;
import com.adventnet.ds.query.CreateTableLike;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.zoho.mickey.exception.DataBaseException;
import java.net.ConnectException;
import java.io.IOException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.db.adapter.SQLGenerator;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Query;
import com.adventnet.db.adapter.ResultSetAdapter;
import java.sql.PreparedStatement;
import java.util.List;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Connection;
import com.adventnet.ds.adapter.DataSourceException;
import com.adventnet.ds.adapter.MDSContext;
import java.util.Properties;
import java.util.HashMap;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.DataSourceManager;
import java.util.logging.Logger;
import com.adventnet.db.adapter.DBAdapter;

public class MDSAdapter implements DBAdapter
{
    private static final Logger LOGGER;
    private static final int DEFAULT_FETCH_SIZE = 200;
    private String name;
    
    public MDSAdapter() {
        this.name = null;
    }
    
    public DBAdapter getDBAdapter() {
        try {
            final HashMap hashMap = DBThreadLocal.get();
            final String dsName = (hashMap != null) ? hashMap.get("dsName") : "default";
            DataSourceManager.getInstance();
            DBAdapter dbAdapter = (DBAdapter)DataSourceManager.getDSAdapter(dsName);
            if (dbAdapter == null) {
                final Properties dataSourceProps = PersistenceInitializer.getConfigurationProps("DataSourcePlugIn");
                final String adapterType = dataSourceProps.getProperty("DefaultDSAdapter");
                final Properties adapterProps = PersistenceInitializer.getConfigurationProps(adapterType);
                adapterProps.setProperty("DSName", dsName);
                dbAdapter = PersistenceInitializer.createDBAdapter(adapterProps);
                dbAdapter.initialize(adapterProps);
            }
            return dbAdapter;
        }
        catch (final Exception e) {
            throw new RuntimeException("Cannot able to resolve the appropriate DSAdapter for this request.");
        }
    }
    
    @Override
    public void cleanUp(final MDSContext context) throws DataSourceException {
        this.getDBAdapter().cleanUp(context);
    }
    
    @Override
    public void connectTo(final Connection connection, final String dbName) throws SQLException {
        this.getDBAdapter().connectTo(connection, dbName);
    }
    
    @Override
    public Connection createConnection(final String url, final String userName, final String password, final String driver) throws SQLException, ClassNotFoundException {
        return this.getDBAdapter().createConnection(url, userName, password, driver);
    }
    
    @Override
    public void createDB(final Connection connection, final String dbName, final String userName, final String passWord) throws SQLException {
        this.getDBAdapter().createDB(connection, dbName, userName, passWord);
    }
    
    @Override
    public void createTable(final Statement stmt, final TableDefinition tabDefn, final List relatedTables) throws SQLException {
        this.getDBAdapter().createTable(stmt, tabDefn, relatedTables);
    }
    
    @Override
    public void createTable(final Statement stmt, final TableDefinition tabDefn, final String createTableOptions, final List relatedTables) throws SQLException {
        this.getDBAdapter().createTable(stmt, tabDefn, createTableOptions, relatedTables);
    }
    
    @Override
    public void createTable(final Statement stmt, final String createSQL, final List relatedTables) throws SQLException {
        this.getDBAdapter().createTable(stmt, createSQL, relatedTables);
    }
    
    @Override
    public void createTables(final Statement stmt, final String schemaName, final List tabDefn, final List tablesPresent) throws SQLException {
        this.getDBAdapter().createTables(stmt, schemaName, tabDefn, tablesPresent);
    }
    
    @Override
    public void dropTable(final Statement stmt, final String tableName, final boolean cascade, final List relatedTables) throws SQLException {
        this.getDBAdapter().dropTable(stmt, tableName, cascade, relatedTables);
    }
    
    @Override
    public boolean execute(final Statement stmt, final String sql) throws SQLException {
        return this.getDBAdapter().execute(stmt, sql);
    }
    
    @Override
    public ResultSetAdapter executeQuery(final PreparedStatement pstmt) throws SQLException {
        return this.getDBAdapter().executeQuery(pstmt);
    }
    
    @Override
    public DataSet executeQuery(final MDSContext context, final Query query) throws DataSourceException {
        return this.getDBAdapter().executeQuery(context, query);
    }
    
    @Override
    public ResultSetAdapter executeQuery(final Statement stmt, final String sql) throws SQLException {
        return this.getDBAdapter().executeQuery(stmt, sql);
    }
    
    @Override
    public int executeUpdate(final PreparedStatement pstmt) throws SQLException {
        return this.getDBAdapter().executeUpdate(pstmt);
    }
    
    @Override
    public int executeUpdate(final Statement stmt, final String sql) throws SQLException {
        return this.getDBAdapter().executeUpdate(stmt, sql);
    }
    
    @Override
    public SQLGenerator getSQLGenerator() {
        return this.getDBAdapter().getSQLGenerator();
    }
    
    @Override
    public List getTables(final Connection con) throws SQLException {
        return this.getDBAdapter().getTables(con);
    }
    
    @Override
    public void initForExecution(final MDSContext context) throws DataSourceException {
        this.getDBAdapter().initForExecution(context);
    }
    
    @Override
    public void initialize(final Properties props) {
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void setSQLGenerator(final SQLGenerator sqlGen) {
        this.getDBAdapter().setSQLGenerator(sqlGen);
    }
    
    @Override
    public void setValue(final PreparedStatement ps, final int columnIndex, final int sqlType, final Object value) throws SQLException {
        this.getDBAdapter().setValue(ps, columnIndex, sqlType, value);
    }
    
    @Override
    public List getTables(final Connection con, final String schemaName) throws SQLException {
        return this.getDBAdapter().getTables(con, schemaName);
    }
    
    @Override
    public String getDefaultDB(final Connection connection) throws SQLException {
        return this.getDBAdapter().getDefaultDB(connection);
    }
    
    @Override
    public String getDBName(final Connection connection) throws SQLException {
        final HashMap hashMap = DBThreadLocal.get();
        final String dbName = (hashMap != null) ? ((hashMap.get("dbName") != null) ? hashMap.get("dbName") : this.getDefaultDB(connection)) : this.getDBAdapter().getDBName(connection);
        return dbName;
    }
    
    @Override
    public void alterTable(final Connection connection, final AlterTableQuery alterTableQuery) throws SQLException {
        this.getDBAdapter().alterTable(connection, alterTableQuery);
    }
    
    @Override
    public void dropAllTables(final Connection connection, final boolean onlyProductTables) throws SQLException, MetaDataException {
        this.getDBAdapter().dropAllTables(connection, onlyProductTables);
    }
    
    @Override
    public void setUpDB(final String connectionURL, final String userName, final String password) throws IOException {
        this.getDBAdapter().setUpDB(connectionURL, userName, password);
    }
    
    @Override
    public void shutDownDB(final String connectionURL, final String userName, final String password) throws IOException, ConnectException {
        this.getDBAdapter().shutDownDB(connectionURL, userName, password);
    }
    
    @Override
    public boolean createDB(final String connectionURL, final String userName, final String password) throws IOException {
        return this.getDBAdapter().createDB(connectionURL, userName, password);
    }
    
    @Override
    public boolean validateVersion(final Connection con) {
        return this.getDBAdapter().validateVersion(con);
    }
    
    @Override
    public void checkDBStatus(final String url) {
    }
    
    @Override
    public void prepareDatabase(final Connection conn) throws DataBaseException {
        this.getDBAdapter().prepareDatabase(conn);
    }
    
    @Override
    public boolean abortBackup() throws Exception {
        return this.getDBAdapter().abortBackup();
    }
    
    @Override
    public Properties getDBProps() {
        return this.getDBAdapter().getDBProps();
    }
    
    @Override
    public boolean isActive(final Connection c) {
        return this.getDBAdapter().isActive(c);
    }
    
    @Override
    public boolean setPassword(final String userName, final String currentPassword, final Connection c) throws Exception {
        return this.getDBAdapter().setPassword(userName, currentPassword, c);
    }
    
    @Override
    public List getShutDownStrings() {
        return this.getDBAdapter().getShutDownStrings();
    }
    
    @Override
    public void handlePreExecute(final Connection conn, final List<String> tableNames, final List<String> columns) throws Exception {
        this.getDBAdapter().handlePreExecute(conn, tableNames, columns);
    }
    
    @Override
    public void handlePostExecute(final Connection conn, final List<String> tableNames, final List<String> columns) throws Exception {
        this.getDBAdapter().handlePostExecute(conn, tableNames, columns);
    }
    
    @Override
    public void migrateScharDatatype(final List<ColumnDefinition> columnDefinitions) throws SQLException {
        this.getDBAdapter().migrateScharDatatype(columnDefinitions);
    }
    
    @Override
    public boolean isTablePresentInDB(final Connection c, final String schemaName, final String tableName) throws SQLException {
        return this.getDBAdapter().isTablePresentInDB(c, schemaName, tableName);
    }
    
    @Override
    public List<AlterTableQuery> getAlterQueryForCopyAllConstraints(final CreateTableLike cloneTableDetails, final boolean copyOnlyFK) throws Exception {
        return this.getDBAdapter().getAlterQueryForCopyAllConstraints(cloneTableDetails, copyOnlyFK);
    }
    
    @Override
    public List<AlterTableQuery> getATQForRemoveAllConstraints(final ArchiveTable table) throws Exception {
        return this.getDBAdapter().getATQForRemoveAllConstraints(table);
    }
    
    @Override
    public int fileBackup(final String backupDir, final String backupFileName, final List<String> directoriesToBeArchived, final String versionHandlerName, final Properties prefProps) throws Exception {
        return this.getDBAdapter().fileBackup(backupDir, backupFileName, directoriesToBeArchived, versionHandlerName, prefProps);
    }
    
    @Override
    public String getIdentifiersFileName() {
        return this.getDBAdapter().getIdentifiersFileName();
    }
    
    @Override
    public SQLException handleSQLException(final SQLException sqle, final Connection connection, final boolean isWrite) throws SQLException {
        return this.getDBAdapter().handleSQLException(sqle, connection, isWrite);
    }
    
    @Override
    public boolean startDB(final String connectionURL, final String userName, final String password) throws Exception {
        return this.getDBAdapter().startDB(connectionURL, userName, password);
    }
    
    @Override
    public void stopDB(final String connectionURL, final String userName, final String password) throws Exception {
        this.getDBAdapter().stopDB(connectionURL, userName, password);
    }
    
    @Override
    public boolean isColumnModified(final ColumnDefinition oldColumnDefinition, final ColumnDefinition newColumnDefinition, final List<String> changedAttributes) {
        return this.getDBAdapter().isColumnModified(oldColumnDefinition, newColumnDefinition, changedAttributes);
    }
    
    @Override
    public boolean isIndexModified(final IndexDefinition oldIndexDefinition, final IndexDefinition newIndexDefinition, final List<String> changedAttributes) {
        return this.getDBAdapter().isIndexModified(oldIndexDefinition, newIndexDefinition, changedAttributes);
    }
    
    @Override
    public Map splitConnectionURL(final String databaseURL) {
        return this.getDBAdapter().splitConnectionURL(databaseURL);
    }
    
    @Override
    public long getApproxRowCount(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        return this.getDBAdapter().getApproxRowCount(tableName, metaData);
    }
    
    @Override
    public boolean isMaxLengthComparable(final String dataType) {
        return this.getDBAdapter().isMaxLengthComparable(dataType);
    }
    
    @Override
    public String getPKNameOfTheTable(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        return this.getDBAdapter().getPKNameOfTheTable(tableName, metaData);
    }
    
    @Override
    public void createDB(final Connection connection, final String dbName, final String userName, final String password, final boolean ignoreIfExists) throws SQLException {
        this.getDBAdapter().createDB(connection, dbName, userName, password, ignoreIfExists);
    }
    
    @Override
    public Statement createStatement(final Connection conn, final int fetchsize) throws SQLException {
        return this.getDBAdapter().createStatement(conn, fetchsize);
    }
    
    @Override
    public Statement createStatement(final Connection conn) throws SQLException {
        return this.getDBAdapter().createStatement(conn);
    }
    
    @Override
    public String getTableName(final String schemaString) {
        return this.getDBAdapter().getTableName(schemaString);
    }
    
    @Override
    public void loadFunctionTemplates() throws Exception {
        this.getDBAdapter().loadFunctionTemplates();
    }
    
    @Override
    public boolean isTableNotFoundException(final SQLException sqle) {
        return this.getDBAdapter().isTableNotFoundException(sqle);
    }
    
    @Override
    public int[] executeBatch(final Statement stmt) throws SQLException {
        return this.getDBAdapter().executeBatch(stmt);
    }
    
    @Override
    public int[] executeBatch(final PreparedStatement prstmt) throws SQLException {
        return this.getDBAdapter().executeBatch(prstmt);
    }
    
    @Override
    public void addBatch(final Object[] rowLvlByteValues, final BulkLoad bulk) throws IOException, SQLException {
        this.getDBAdapter().addBatch(rowLvlByteValues, bulk);
    }
    
    @Override
    public void execBulk(final BulkLoad bulk) throws SQLException, IOException, MetaDataException {
        this.getDBAdapter().execBulk(bulk);
    }
    
    @Override
    public BulkInsertObject createBulkInsertObject(final BulkLoad bulk) throws IOException, SQLException, QueryConstructionException, MetaDataException {
        return this.getDBAdapter().createBulkInsertObject(bulk);
    }
    
    @Override
    public void closeBulkInsertObject(final BulkLoad bulk) throws SQLException, IOException {
        this.getDBAdapter().closeBulkInsertObject(bulk);
    }
    
    @Override
    public ExecutorService getBulkThreadExecutor() {
        return this.getDBAdapter().getBulkThreadExecutor();
    }
    
    @Override
    public BackupHandler getBackupHandler() {
        MDSAdapter.LOGGER.log(Level.SEVERE, "Backup not supported for MDS");
        return null;
    }
    
    @Override
    public List<String> getAllDatabaseNames(final Connection c) {
        return this.getDBAdapter().getAllDatabaseNames(c);
    }
    
    @Override
    public RestoreHandler getRestoreHandler() {
        MDSAdapter.LOGGER.log(Level.SEVERE, "Restore not supported for MDS");
        return null;
    }
    
    @Override
    public DBInitializer getDBInitializer() {
        return this.getDBAdapter().getDBInitializer();
    }
    
    @Override
    public boolean isBundledDB() {
        return this.getDBAdapter().isBundledDB();
    }
    
    @Override
    public ResultSet getFKMetaData(final Connection connection, final String tableName) throws SQLException {
        return this.getDBAdapter().getFKMetaData(connection, tableName);
    }
    
    @Override
    public List<String> getColumnNamesFromDB(final String tableName, final String columnPattern, final DatabaseMetaData metaData) throws SQLException {
        return this.getDBAdapter().getColumnNamesFromDB(tableName, columnPattern, metaData);
    }
    
    @Override
    public String getDBSpecificExceptionSorterName() {
        return this.getDBAdapter().getDBSpecificExceptionSorterName();
    }
    
    @Override
    public PreparedStatement createInsertStatement(final String tableName, final Connection conn) throws SQLException, MetaDataException, QueryConstructionException {
        return this.getDBAdapter().createInsertStatement(tableName, conn);
    }
    
    @Override
    public PreparedStatement createUpdateStatement(final String tableName, final int[] changedIndexes, final Connection conn) throws SQLException, MetaDataException, QueryConstructionException {
        return this.getDBAdapter().createUpdateStatement(tableName, changedIndexes, conn);
    }
    
    @Override
    public PreparedStatement createUpdateStatement(final UpdateQuery query, final Connection conn) throws SQLException, MetaDataException, QueryConstructionException {
        return this.getDBAdapter().createUpdateStatement(query, conn);
    }
    
    @Override
    public boolean executeDelete(final String tableName, final Criteria cri, final Connection conn) throws SQLException, QueryConstructionException {
        return this.getDBAdapter().executeDelete(tableName, cri, conn);
    }
    
    @Override
    public boolean hasPermissionForBackup(final Connection conn) throws Exception {
        return this.getDBAdapter().hasPermissionForBackup(conn);
    }
    
    @Override
    public boolean hasPermissionForRestore(final Connection conn) throws Exception {
        return this.getDBAdapter().hasPermissionForRestore(conn);
    }
    
    @Override
    public long getSizeOfDB(final Connection conn) {
        return this.getDBAdapter().getSizeOfDB(conn);
    }
    
    @Override
    public String getDBSystemProperty(final Connection conn, final String property) throws SQLException, QueryConstructionException {
        return this.getDBAdapter().getDBSystemProperty(conn, property);
    }
    
    @Override
    public void setValue(final PreparedStatement ps, final int columnIndex, final Map<String, Integer> sqlTypes, final Map<String, Object> value, final String dcType) throws SQLException {
        this.getDBAdapter().setValue(ps, columnIndex, sqlTypes, value, dcType);
    }
    
    @Override
    public void logDatabaseDetails() {
        this.getDBAdapter().logDatabaseDetails();
    }
    
    @Override
    public List<String> getPKColumnNameOfTheTable(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        return this.getDBAdapter().getPKColumnNameOfTheTable(tableName, metaData);
    }
    
    @Override
    public long getTotalRowCount(final Connection connection, final String tableName) throws SQLException {
        return this.getDBAdapter().getTotalRowCount(connection, tableName);
    }
    
    @Override
    public void truncateTable(final Statement stmt, final String tableName) throws SQLException {
        this.getDBAdapter().truncateTable(stmt, tableName);
    }
    
    @Override
    public String getDBType() {
        return this.getDBAdapter().getDBType();
    }
    
    @Override
    public DCAdapter getDCAdapterForTable(final String tableName) {
        return this.getDBAdapter().getDCAdapterForTable(tableName);
    }
    
    @Override
    public DCAdapter getDCAdapter(final String dcType) {
        return this.getDBAdapter().getDCAdapter(dcType);
    }
    
    @Override
    public SQLModifier getSQLModifier() {
        return this.getDBAdapter().getSQLModifier();
    }
    
    @Override
    public List<String> getTableNamesLike(final Connection c, final String schemaName, final String tableNamePattern) throws SQLException {
        return this.getDBAdapter().getTableNamesLike(c, schemaName, tableNamePattern);
    }
    
    @Override
    public List<String> getColumnNames(final Connection c, final String schemaName, final String tableName) throws SQLException {
        return this.getDBAdapter().getColumnNames(c, schemaName, tableName);
    }
    
    @Override
    public String getDBSpecificAbortHandlerName() {
        return this.getDBAdapter().getDBSpecificAbortHandlerName();
    }
    
    @Override
    public int[] executeBatch(final Connection connection, final String query, final Collection<Object[]> args) throws SQLException {
        return this.getDBAdapter().executeBatch(connection, query, args);
    }
    
    @Override
    public boolean execute(final PreparedStatement stmt) throws SQLException {
        return this.getDBAdapter().execute(stmt);
    }
    
    @Override
    public PreparedStatement createPreparedStatement(final Connection conn, final String query, final Object... args) throws SQLException {
        return this.getDBAdapter().createPreparedStatement(conn, query, args);
    }
    
    @Override
    public PreparedStatement createPreparedStatement(final Connection conn, final int fetchsize, final String query, final Object... args) throws SQLException {
        return this.getDBAdapter().createPreparedStatement(conn, fetchsize, query, args);
    }
    
    @Override
    public boolean changePassword(final String userName, final String oldPassword, final String newPassword, final Connection c) throws SQLException {
        return this.getDBAdapter().changePassword(userName, oldPassword, newPassword, c);
    }
    
    @Override
    public boolean isReadOnly(final Connection connection) throws Exception {
        return this.getDBAdapter().isReadOnly(connection);
    }
    
    @Override
    public Statement createReadOnlyStatement(final Connection conn) throws SQLException {
        return this.getDBAdapter().createReadOnlyStatement(conn);
    }
    
    static {
        LOGGER = Logger.getLogger(MDSAdapter.class.getName());
    }
}
