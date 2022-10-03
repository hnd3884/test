package com.adventnet.db.adapter;

import java.util.Collection;
import com.zoho.mickey.db.SQLModifier;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Query;
import java.util.concurrent.ExecutorService;
import java.sql.ResultSet;
import com.adventnet.ds.query.QueryConstructionException;
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
import com.adventnet.cp.WrappedPreparedStatement;
import java.sql.PreparedStatement;
import com.adventnet.cp.WrappedConnection;
import com.adventnet.ds.query.AlterTableQuery;
import java.sql.Connection;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.sql.SQLException;
import com.adventnet.cp.WrappedStatement;
import java.util.List;
import java.sql.Statement;
import com.adventnet.ds.adapter.DataSourceException;
import com.adventnet.ds.adapter.MDSContext;
import java.util.Properties;
import java.util.Arrays;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.cp.MultiDSUtil;
import com.adventnet.cp.ClientFilter;
import java.util.logging.Logger;

public class WrappedDBAdapter implements DBAdapter
{
    static Logger OUT;
    private int i;
    private DBAdapter[] dbAdapters;
    
    public WrappedDBAdapter() {
        this.dbAdapters = new DBAdapter[3];
        this.i = 0;
        for (int i = 0; i < 3; ++i) {
            this.dbAdapters[i] = null;
        }
    }
    
    public void addDBAdapter(final DBAdapter adapter) {
        this.dbAdapters[this.i++] = adapter;
    }
    
    public DBAdapter getDBAdapter(final int i) {
        return this.dbAdapters[i];
    }
    
    private int getIndexOfReadDBAdapter() {
        final Integer readDBIndex = ClientFilter.getThreadLocalDB();
        if (readDBIndex != null && MultiDSUtil.isMultiDataSourceEnabled()) {
            return readDBIndex;
        }
        return 0;
    }
    
    private DBAdapter getReadDBAdapter() {
        return this.dbAdapters[this.getIndexOfReadDBAdapter()];
    }
    
    private DBAdapter getDefaultDBAdapter() {
        return this.dbAdapters[0];
    }
    
    private int checkResult(final int[] r) {
        for (int i = 1; i < PersistenceInitializer.getDatabases().size(); ++i) {
            if (r[i] != r[0]) {
                WrappedDBAdapter.OUT.log(Level.INFO, "Default database result is: " + r[0] + " while " + PersistenceInitializer.getDatabases().get(i) + " result is: " + r[i]);
            }
        }
        return r[0];
    }
    
    private boolean compareBooleanResult(final boolean[] r) {
        for (int i = 1; i < PersistenceInitializer.getDatabases().size(); ++i) {
            if (r[i] != r[0]) {
                WrappedDBAdapter.OUT.log(Level.INFO, "Default database result is: " + r[0] + " while " + PersistenceInitializer.getDatabases().get(i) + " result is: " + r[i]);
            }
        }
        return r[0];
    }
    
    private int[] checkBatchResult(final int[][] r) {
        for (int i = 1; i < PersistenceInitializer.getDatabases().size(); ++i) {
            if (!Arrays.equals(r[i], r[0])) {
                WrappedDBAdapter.OUT.log(Level.INFO, "Default database result is: " + Arrays.toString(r[0]) + " while " + PersistenceInitializer.getDatabases().get(i) + " result is: " + Arrays.toString(r[i]));
            }
        }
        return r[0];
    }
    
    @Override
    public void initialize(final Properties props) {
        for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
            this.dbAdapters[i].initialize(props);
        }
    }
    
    @Override
    public void initForExecution(final MDSContext context) throws DataSourceException {
        this.getDefaultDBAdapter().initForExecution(context);
    }
    
    @Override
    public void cleanUp(final MDSContext context) throws DataSourceException {
        this.getDefaultDBAdapter().cleanUp(context);
    }
    
    @Override
    public void dropTable(final Statement stmt, final String tableName, final boolean cascade, final List relatedTables) throws SQLException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                this.dbAdapters[i].dropTable(((WrappedStatement)stmt).getStatement(i), tableName, cascade, relatedTables);
            }
        }
        else {
            this.getDefaultDBAdapter().dropTable(((WrappedStatement)stmt).getStatement(0), tableName, cascade, relatedTables);
        }
    }
    
    @Override
    public void createTable(final Statement stmt, final String createSQL, final List relatedTables) throws SQLException {
        try {
            this.getDefaultDBAdapter().createTable(stmt, createSQL, relatedTables);
        }
        catch (final SQLException e) {
            final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e);
            throw sqle;
        }
    }
    
    @Override
    public void createTables(final Statement stmt, final String schemaName, final List tabDefn, final List tablesPresent) throws SQLException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    this.dbAdapters[i].createTables(((WrappedStatement)stmt).getStatement(i), schemaName, tabDefn, tablesPresent);
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
        }
        else {
            try {
                this.getDefaultDBAdapter().createTables(((WrappedStatement)stmt).getStatement(0), schemaName, tabDefn, tablesPresent);
            }
            catch (final SQLException e2) {
                final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
                WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
                throw sqle2;
            }
        }
    }
    
    @Override
    public void createTable(final Statement stmt, final TableDefinition tabDefn, final List relatedTables) throws SQLException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    this.dbAdapters[i].createTable(((WrappedStatement)stmt).getStatement(i), tabDefn, relatedTables);
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
        }
        else {
            try {
                this.getDefaultDBAdapter().createTable(((WrappedStatement)stmt).getStatement(0), tabDefn, relatedTables);
            }
            catch (final SQLException e2) {
                final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
                WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
                throw sqle2;
            }
        }
    }
    
    @Override
    public void createTable(final Statement stmt, final TableDefinition tabDefn, final String createTableOptions, final List relatedTables) throws SQLException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    this.dbAdapters[i].createTable(((WrappedStatement)stmt).getStatement(i), tabDefn, createTableOptions, relatedTables);
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
        }
        else {
            try {
                this.getDefaultDBAdapter().createTable(((WrappedStatement)stmt).getStatement(0), tabDefn, createTableOptions, relatedTables);
            }
            catch (final SQLException e2) {
                final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
                WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
                throw sqle2;
            }
        }
    }
    
    @Override
    public void alterTable(final Connection connection, final AlterTableQuery alterTableQuery) throws SQLException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    this.dbAdapters[i].alterTable(((WrappedConnection)connection).getConnection(i), alterTableQuery);
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
        }
        else {
            try {
                this.getDefaultDBAdapter().alterTable(((WrappedConnection)connection).getConnection(0), alterTableQuery);
            }
            catch (final SQLException e2) {
                final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
                WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
                throw sqle2;
            }
        }
    }
    
    @Override
    public void setValue(final PreparedStatement ps, final int columnIndex, final int sqlType, final Object value) throws SQLException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                this.dbAdapters[i].setValue(((WrappedPreparedStatement)ps).getPreparedStatement(i), columnIndex, sqlType, value);
            }
        }
        else {
            this.getDefaultDBAdapter().setValue(((WrappedPreparedStatement)ps).getPreparedStatement(0), columnIndex, sqlType, value);
        }
    }
    
    @Override
    public void connectTo(final Connection connection, final String dbName) throws SQLException {
        this.getDefaultDBAdapter().connectTo(((WrappedConnection)connection).getConnection(0), dbName);
    }
    
    @Override
    public void createDB(final Connection connection, final String dbName, final String userName, final String passWord) throws SQLException {
        this.getDefaultDBAdapter().createDB(connection, dbName, userName, passWord);
    }
    
    @Override
    public String getDefaultDB(final Connection connection) throws SQLException {
        return this.getDefaultDBAdapter().getDefaultDB(connection);
    }
    
    @Override
    public String getDBName(final Connection connection) throws SQLException {
        return this.getReadDBAdapter().getDBName(connection);
    }
    
    @Override
    public void dropAllTables(final Connection connection, final boolean onlyProductTables) throws SQLException, MetaDataException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    this.dbAdapters[i].dropAllTables(((WrappedConnection)connection).getConnection(i), onlyProductTables);
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
        }
        else {
            try {
                this.getDefaultDBAdapter().dropAllTables(((WrappedConnection)connection).getConnection(0), onlyProductTables);
            }
            catch (final SQLException e2) {
                final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
                WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
                throw sqle2;
            }
        }
    }
    
    @Override
    public void setUpDB(final String connectionURL, final String userName, final String password) throws IOException, ConnectException {
        this.getDefaultDBAdapter().setUpDB(connectionURL, userName, password);
    }
    
    @Override
    public boolean createDB(final String connectionURL, final String userName, final String password) throws IOException {
        return this.getDefaultDBAdapter().createDB(connectionURL, userName, password);
    }
    
    @Override
    public void shutDownDB(final String connectionURL, final String userName, final String password) throws IOException {
        this.getDefaultDBAdapter().shutDownDB(connectionURL, userName, password);
    }
    
    @Override
    public boolean validateVersion(final Connection con) {
        final WrappedConnection wc = (WrappedConnection)con;
        boolean result = true;
        for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
            result = (result && this.dbAdapters[i].validateVersion(wc.getConnection(i)));
        }
        return result;
    }
    
    @Override
    public void checkDBStatus(final String url) {
        this.getDefaultDBAdapter().checkDBStatus(url);
    }
    
    @Override
    public SQLException handleSQLException(final SQLException sqle, final Connection connection, final boolean isWrite) throws SQLException {
        return this.getDefaultDBAdapter().handleSQLException(sqle, null, false);
    }
    
    @Override
    public int fileBackup(final String backupDir, final String backupFileName, final List<String> directoriesToBeArchived, final String versionHandlerName, final Properties prefProps) throws Exception {
        return this.getDefaultDBAdapter().fileBackup(backupDir, backupFileName, directoriesToBeArchived, versionHandlerName, prefProps);
    }
    
    @Override
    public boolean abortBackup() throws Exception {
        return this.getDefaultDBAdapter().abortBackup();
    }
    
    @Override
    public Properties getDBProps() {
        return this.getDefaultDBAdapter().getDBProps();
    }
    
    @Override
    public boolean isActive(final Connection c) {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            final WrappedConnection wc = (WrappedConnection)c;
            boolean result = true;
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                result = (result && this.dbAdapters[i].isActive(wc.getConnection(i)));
            }
            return result;
        }
        return this.getDefaultDBAdapter().isActive(((WrappedConnection)c).getConnection(0));
    }
    
    @Override
    public boolean setPassword(final String userName, final String currentPassword, final Connection c) throws Exception {
        return this.getDefaultDBAdapter().setPassword(userName, currentPassword, c);
    }
    
    @Override
    public List getShutDownStrings() {
        return this.getDefaultDBAdapter().getShutDownStrings();
    }
    
    @Override
    public void handlePreExecute(final Connection conn, final List<String> tableNames, final List<String> columns) throws Exception {
        final WrappedConnection wc = (WrappedConnection)conn;
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                this.dbAdapters[i].handlePreExecute(wc.getConnection(i), tableNames, columns);
            }
        }
        else {
            this.getDefaultDBAdapter().handlePreExecute(wc.getConnection(0), tableNames, columns);
        }
    }
    
    @Override
    public void handlePostExecute(final Connection conn, final List<String> tableNames, final List<String> columns) throws Exception {
        final WrappedConnection wc = (WrappedConnection)conn;
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                this.dbAdapters[i].handlePostExecute(wc.getConnection(i), tableNames, columns);
            }
        }
        else {
            this.getDefaultDBAdapter().handlePostExecute(wc.getConnection(0), tableNames, columns);
        }
    }
    
    @Override
    public void prepareDatabase(final Connection conn) throws DataBaseException {
        final WrappedConnection wc = (WrappedConnection)conn;
        for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
            this.dbAdapters[i].prepareDatabase(wc.getConnection(i));
        }
    }
    
    @Override
    public void migrateScharDatatype(final List<ColumnDefinition> columnDefinitions) throws SQLException {
        this.getDefaultDBAdapter().migrateScharDatatype(columnDefinitions);
    }
    
    @Override
    public boolean isTablePresentInDB(final Connection c, final String schemaName, final String tableName) throws SQLException {
        return this.getDefaultDBAdapter().isTablePresentInDB(c, schemaName, tableName);
    }
    
    @Override
    public List<AlterTableQuery> getAlterQueryForCopyAllConstraints(final CreateTableLike cloneTableDetails, final boolean copyOnlyFK) throws Exception {
        return this.getReadDBAdapter().getAlterQueryForCopyAllConstraints(cloneTableDetails, copyOnlyFK);
    }
    
    @Override
    public List<AlterTableQuery> getATQForRemoveAllConstraints(final ArchiveTable table) throws Exception {
        return this.getReadDBAdapter().getATQForRemoveAllConstraints(table);
    }
    
    @Override
    public String getIdentifiersFileName() {
        return this.getDefaultDBAdapter().getIdentifiersFileName();
    }
    
    @Override
    public boolean startDB(final String connectionURL, final String userName, final String password) throws Exception {
        return this.getDefaultDBAdapter().startDB(connectionURL, userName, password);
    }
    
    @Override
    public void stopDB(final String connectionURL, final String userName, final String password) throws Exception {
        this.getDefaultDBAdapter().stopDB(connectionURL, userName, password);
    }
    
    @Override
    public boolean isColumnModified(final ColumnDefinition oldColumnDefinition, final ColumnDefinition newColumnDefinition, final List<String> changedAttributes) {
        return this.getReadDBAdapter().isColumnModified(oldColumnDefinition, newColumnDefinition, changedAttributes);
    }
    
    @Override
    public boolean isIndexModified(final IndexDefinition oldIndexDefinition, final IndexDefinition newIndexDefinition, final List<String> changedAttributes) {
        return this.getReadDBAdapter().isIndexModified(oldIndexDefinition, newIndexDefinition, changedAttributes);
    }
    
    @Override
    public Map splitConnectionURL(final String databaseURL) {
        return this.getDefaultDBAdapter().splitConnectionURL(databaseURL);
    }
    
    @Override
    public long getApproxRowCount(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        return this.getReadDBAdapter().getApproxRowCount(tableName, metaData);
    }
    
    @Override
    public List<String> getColumnNamesFromDB(final String tableName, final String columnPattern, final DatabaseMetaData metaData) throws SQLException {
        return this.getReadDBAdapter().getColumnNamesFromDB(tableName, columnPattern, metaData);
    }
    
    @Override
    public boolean isMaxLengthComparable(final String dataType) {
        return this.getReadDBAdapter().isMaxLengthComparable(dataType);
    }
    
    @Override
    public String getPKNameOfTheTable(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        return this.getReadDBAdapter().getPKNameOfTheTable(tableName, metaData);
    }
    
    @Override
    public void createDB(final Connection connection, final String dbName, final String userName, final String password, final boolean ignoreIfExists) throws SQLException {
        this.getDefaultDBAdapter().createDB(connection, dbName, userName, password, ignoreIfExists);
    }
    
    @Override
    public Statement createStatement(final Connection conn, final int fetchsize) throws SQLException {
        return this.getReadDBAdapter().createStatement(conn, fetchsize);
    }
    
    @Override
    public Statement createStatement(final Connection conn) throws SQLException {
        final Statement stmt = new WrappedStatement();
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            final WrappedConnection wc = (WrappedConnection)conn;
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                ((WrappedStatement)stmt).addStatement(this.dbAdapters[i].createStatement(wc.getConnection(i)));
            }
        }
        else {
            ((WrappedStatement)stmt).addStatement(this.getDefaultDBAdapter().createStatement(conn));
        }
        return stmt;
    }
    
    @Override
    public String getTableName(final String schemaString) {
        return this.getReadDBAdapter().getTableName(schemaString);
    }
    
    @Override
    public void loadFunctionTemplates() throws Exception {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                this.dbAdapters[i].loadFunctionTemplates();
            }
        }
        else {
            this.getDefaultDBAdapter().loadFunctionTemplates();
        }
    }
    
    @Override
    public boolean isTableNotFoundException(final SQLException sqle) {
        return this.getDefaultDBAdapter().isTableNotFoundException(sqle);
    }
    
    @Override
    public int[] executeBatch(final Statement stmt) throws SQLException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            final int[][] result = new int[3][];
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    result[i] = this.dbAdapters[i].executeBatch(((WrappedStatement)stmt).getStatement(i));
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
            return this.checkBatchResult(result);
        }
        try {
            return this.getDefaultDBAdapter().executeBatch(((WrappedStatement)stmt).getStatement(0));
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
            throw sqle2;
        }
    }
    
    @Override
    public int[] executeBatch(final PreparedStatement pstmt) throws SQLException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            final int[][] result = new int[3][];
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    result[i] = this.dbAdapters[i].executeBatch(((WrappedPreparedStatement)pstmt).getPreparedStatement(i));
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
            return this.checkBatchResult(result);
        }
        try {
            return this.getDefaultDBAdapter().executeBatch(((WrappedPreparedStatement)pstmt).getPreparedStatement(0));
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
            throw sqle2;
        }
    }
    
    @Override
    public void execBulk(final BulkLoad bulk) throws SQLException, IOException, MetaDataException {
        try {
            this.getDefaultDBAdapter().execBulk(bulk);
        }
        catch (final SQLException e) {
            final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e);
            throw sqle;
        }
    }
    
    @Override
    public void addBatch(final Object[] rowLvlByteValues, final BulkLoad bulk) throws IOException, SQLException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                this.dbAdapters[i].addBatch(rowLvlByteValues, bulk);
            }
        }
        else {
            this.getDefaultDBAdapter().addBatch(rowLvlByteValues, bulk);
        }
    }
    
    @Override
    public BulkInsertObject createBulkInsertObject(final BulkLoad bulk) throws IOException, SQLException, QueryConstructionException, MetaDataException {
        return this.getDefaultDBAdapter().createBulkInsertObject(bulk);
    }
    
    @Override
    public void closeBulkInsertObject(final BulkLoad bulk) throws SQLException, IOException {
        this.getDefaultDBAdapter().closeBulkInsertObject(bulk);
    }
    
    @Override
    public ResultSet getFKMetaData(final Connection connection, final String tableName) throws SQLException {
        return this.getReadDBAdapter().getFKMetaData(connection, tableName);
    }
    
    @Override
    public ExecutorService getBulkThreadExecutor() {
        return this.getDefaultDBAdapter().getBulkThreadExecutor();
    }
    
    @Override
    public BackupHandler getBackupHandler() {
        return this.getDefaultDBAdapter().getBackupHandler();
    }
    
    @Override
    public RestoreHandler getRestoreHandler() {
        return this.getDefaultDBAdapter().getRestoreHandler();
    }
    
    @Override
    public List<String> getAllDatabaseNames(final Connection c) {
        return this.getReadDBAdapter().getAllDatabaseNames(c);
    }
    
    @Override
    public DBInitializer getDBInitializer() {
        return this.getDefaultDBAdapter().getDBInitializer();
    }
    
    @Override
    public boolean isBundledDB() {
        return this.getDefaultDBAdapter().isBundledDB();
    }
    
    @Override
    public String getDBSpecificExceptionSorterName() {
        return this.getReadDBAdapter().getDBSpecificExceptionSorterName();
    }
    
    @Override
    public DataSet executeQuery(final MDSContext context, final Query query) throws DataSourceException {
        return this.getDefaultDBAdapter().executeQuery(context, query);
    }
    
    @Override
    public String getName() {
        return this.getDefaultDBAdapter().getName();
    }
    
    @Override
    public Connection createConnection(final String url, final String userName, final String password, final String driver) throws SQLException, ClassNotFoundException {
        return this.getDefaultDBAdapter().createConnection(url, userName, password, driver);
    }
    
    @Override
    public ResultSetAdapter executeQuery(final Statement stmt, final String sql) throws SQLException {
        try {
            return this.getReadDBAdapter().executeQuery(stmt, sql);
        }
        catch (final SQLException e) {
            final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadDBAdapter()) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadDBAdapter()), e);
            throw sqle;
        }
    }
    
    @Override
    public ResultSetAdapter executeQuery(final PreparedStatement pstmt) throws SQLException {
        try {
            return this.getReadDBAdapter().executeQuery(pstmt);
        }
        catch (final SQLException e) {
            final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadDBAdapter()) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadDBAdapter()), e);
            throw sqle;
        }
    }
    
    @Override
    public int executeUpdate(final Statement stmt, final String sql) throws SQLException {
        final String[] split = sql.split(" ", 2);
        final String first_word = split[0];
        if (first_word.equals("insert") || first_word.equals("update") || first_word.equals("delete")) {
            if (MultiDSUtil.isMultiDataSourceEnabled()) {
                final int[] result = new int[3];
                for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                    try {
                        result[i] = this.dbAdapters[i].executeUpdate(((WrappedStatement)stmt).getStatement(i), sql);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
                return this.checkResult(result);
            }
            try {
                return this.getDefaultDBAdapter().executeUpdate(((WrappedStatement)stmt).getStatement(0), sql);
            }
            catch (final SQLException e2) {
                final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
                WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
                throw sqle2;
            }
        }
        try {
            return this.getReadDBAdapter().executeUpdate(((WrappedStatement)stmt).getStatement(this.getIndexOfReadDBAdapter()), sql);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadDBAdapter()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadDBAdapter()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public int executeUpdate(final PreparedStatement pstmt) throws SQLException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            final int[] result = new int[3];
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    result[i] = this.dbAdapters[i].executeUpdate(((WrappedPreparedStatement)pstmt).getPreparedStatement(i));
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
            return this.checkResult(result);
        }
        try {
            return this.getDefaultDBAdapter().executeUpdate(((WrappedPreparedStatement)pstmt).getPreparedStatement(0));
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
            throw sqle2;
        }
    }
    
    @Override
    public boolean execute(final Statement stmt, final String sql) throws SQLException {
        final String[] split = sql.split(" ", 2);
        final String first_word = split[0];
        if (first_word.equals("insert") || first_word.equals("update") || first_word.equals("delete")) {
            if (MultiDSUtil.isMultiDataSourceEnabled()) {
                final boolean[] result = new boolean[3];
                for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                    try {
                        result[i] = this.dbAdapters[i].execute(((WrappedStatement)stmt).getStatement(i), sql);
                    }
                    catch (final SQLException e) {
                        final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                        WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                        throw sqle;
                    }
                }
                return this.compareBooleanResult(result);
            }
            try {
                return this.getDefaultDBAdapter().execute(((WrappedStatement)stmt).getStatement(0), sql);
            }
            catch (final SQLException e2) {
                final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
                WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
                throw sqle2;
            }
        }
        try {
            return this.getReadDBAdapter().execute(((WrappedStatement)stmt).getStatement(this.getIndexOfReadDBAdapter()), sql);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(this.getIndexOfReadDBAdapter()) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(this.getIndexOfReadDBAdapter()), e2);
            throw sqle2;
        }
    }
    
    @Override
    public List getTables(final Connection con) throws SQLException {
        return this.getReadDBAdapter().getTables(con);
    }
    
    @Override
    public List getTables(final Connection con, final String schemaName) throws SQLException {
        return this.getReadDBAdapter().getTables(con, schemaName);
    }
    
    @Override
    public void setSQLGenerator(final SQLGenerator sqlGen) {
        this.getReadDBAdapter().setSQLGenerator(sqlGen);
    }
    
    @Override
    public SQLGenerator getSQLGenerator() {
        return this.getReadDBAdapter().getSQLGenerator();
    }
    
    @Override
    public PreparedStatement createInsertStatement(final String tableName, final Connection conn) throws SQLException, MetaDataException, QueryConstructionException {
        final PreparedStatement pstmt = new WrappedPreparedStatement();
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.dbAdapters[i].createInsertStatement(tableName, ((WrappedConnection)conn).getConnection(i)));
            }
        }
        else {
            ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.getDefaultDBAdapter().createInsertStatement(tableName, ((WrappedConnection)conn).getConnection(0)));
        }
        return pstmt;
    }
    
    @Override
    public PreparedStatement createUpdateStatement(final String tableName, final int[] changedIndexes, final Connection conn) throws SQLException, MetaDataException, QueryConstructionException {
        final PreparedStatement pstmt = new WrappedPreparedStatement();
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.dbAdapters[i].createUpdateStatement(tableName, changedIndexes, ((WrappedConnection)conn).getConnection(i)));
            }
        }
        else {
            ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.getDefaultDBAdapter().createUpdateStatement(tableName, changedIndexes, ((WrappedConnection)conn).getConnection(0)));
        }
        return pstmt;
    }
    
    @Override
    public PreparedStatement createUpdateStatement(final UpdateQuery query, final Connection conn) throws SQLException, MetaDataException, QueryConstructionException {
        final PreparedStatement pstmt = new WrappedPreparedStatement();
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.dbAdapters[i].createUpdateStatement(query, ((WrappedConnection)conn).getConnection(i)));
            }
        }
        else {
            ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.getDefaultDBAdapter().createUpdateStatement(query, ((WrappedConnection)conn).getConnection(0)));
        }
        return pstmt;
    }
    
    @Override
    public boolean executeDelete(final String tableName, final Criteria cri, final Connection conn) throws SQLException, QueryConstructionException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            final boolean[] result = new boolean[3];
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    result[i] = this.dbAdapters[i].executeDelete(tableName, cri, ((WrappedConnection)conn).getConnection(i));
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
            return this.compareBooleanResult(result);
        }
        try {
            return this.getDefaultDBAdapter().executeDelete(tableName, cri, ((WrappedConnection)conn).getConnection(0));
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
            throw sqle2;
        }
    }
    
    @Override
    public boolean hasPermissionForBackup(final Connection conn) throws Exception {
        return this.getDefaultDBAdapter().hasPermissionForBackup(conn);
    }
    
    @Override
    public boolean hasPermissionForRestore(final Connection conn) throws Exception {
        return this.getDefaultDBAdapter().hasPermissionForRestore(conn);
    }
    
    @Override
    public long getSizeOfDB(final Connection conn) {
        return this.getDefaultDBAdapter().getSizeOfDB(conn);
    }
    
    @Override
    public String getDBSystemProperty(final Connection conn, final String property) throws SQLException, QueryConstructionException {
        return this.getDefaultDBAdapter().getDBSystemProperty(conn, property);
    }
    
    @Override
    public void setValue(final PreparedStatement ps, final int columnIndex, final Map<String, Integer> sqlTypes, final Map<String, Object> value, final String dcType) throws SQLException {
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    this.dbAdapters[i].setValue(((WrappedPreparedStatement)ps).getPreparedStatement(i), columnIndex, sqlTypes, value, dcType);
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
        }
        else {
            try {
                this.getDefaultDBAdapter().setValue(((WrappedPreparedStatement)ps).getPreparedStatement(0), columnIndex, sqlTypes, value, dcType);
            }
            catch (final SQLException e2) {
                final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
                WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
                throw sqle2;
            }
        }
    }
    
    @Override
    public void logDatabaseDetails() {
        for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
            this.dbAdapters[i].logDatabaseDetails();
        }
    }
    
    @Override
    public List<String> getPKColumnNameOfTheTable(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        return this.getReadDBAdapter().getPKColumnNameOfTheTable(tableName, metaData);
    }
    
    @Override
    public long getTotalRowCount(final Connection connection, final String tableName) throws SQLException {
        return this.getDefaultDBAdapter().getTotalRowCount(connection, tableName);
    }
    
    @Override
    public void truncateTable(final Statement stmt, final String tableName) throws SQLException {
        this.getDefaultDBAdapter().truncateTable(stmt, tableName);
    }
    
    @Override
    public String getDBType() {
        return this.getDefaultDBAdapter().getDBType();
    }
    
    @Override
    public DCAdapter getDCAdapterForTable(final String tableName) {
        return this.getDefaultDBAdapter().getDCAdapterForTable(tableName);
    }
    
    @Override
    public DCAdapter getDCAdapter(final String dcType) {
        return this.getDefaultDBAdapter().getDCAdapter(dcType);
    }
    
    @Override
    public SQLModifier getSQLModifier() {
        return this.getDefaultDBAdapter().getSQLModifier();
    }
    
    @Override
    public List<String> getTableNamesLike(final Connection c, final String schemaName, final String tableNamePattern) throws SQLException {
        return this.getDefaultDBAdapter().getTableNamesLike(c, schemaName, tableNamePattern);
    }
    
    @Override
    public List<String> getColumnNames(final Connection c, final String schemaName, final String tableName) throws SQLException {
        return this.getDefaultDBAdapter().getColumnNames(c, schemaName, tableName);
    }
    
    @Override
    public String getDBSpecificAbortHandlerName() {
        return this.getDefaultDBAdapter().getDBSpecificAbortHandlerName();
    }
    
    @Override
    public boolean changePassword(final String userName, final String oldPassword, final String newPassword, final Connection c) throws SQLException {
        return this.getDefaultDBAdapter().changePassword(userName, oldPassword, newPassword, c);
    }
    
    @Override
    public boolean isReadOnly(final Connection connection) throws Exception {
        return this.getDefaultDBAdapter().isReadOnly(connection);
    }
    
    @Override
    public boolean execute(final PreparedStatement stmt) throws SQLException {
        final WrappedPreparedStatement wps = (WrappedPreparedStatement)stmt;
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            final boolean[] result = new boolean[3];
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    result[i] = this.dbAdapters[i].execute(wps.getPreparedStatement(i));
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
            return this.compareBooleanResult(result);
        }
        try {
            return this.getDefaultDBAdapter().execute(wps.getPreparedStatement(0));
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
            throw sqle2;
        }
    }
    
    @Override
    public int[] executeBatch(final Connection connection, final String query, final Collection<Object[]> args) throws SQLException {
        final WrappedConnection wc = (WrappedConnection)connection;
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            final int[][] result = new int[3][];
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                try {
                    result[i] = this.dbAdapters[i].executeBatch(wc.getConnection(i), query, args);
                }
                catch (final SQLException e) {
                    final SQLException sqle = new SQLException(PersistenceInitializer.getDatabases().get(i) + " " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
                    WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(i), e);
                    throw sqle;
                }
            }
            return this.checkBatchResult(result);
        }
        try {
            return this.getDefaultDBAdapter().executeBatch(wc.getConnection(0), query, args);
        }
        catch (final SQLException e2) {
            final SQLException sqle2 = new SQLException(PersistenceInitializer.getDatabases().get(0) + " " + e2.getMessage(), e2.getSQLState(), e2.getErrorCode(), e2);
            WrappedDBAdapter.OUT.log(Level.SEVERE, "Exception while executing in " + PersistenceInitializer.getDatabases().get(0), e2);
            throw sqle2;
        }
    }
    
    @Override
    public PreparedStatement createPreparedStatement(final Connection conn, final String query, final Object... args) throws SQLException {
        final WrappedConnection wc = (WrappedConnection)conn;
        final WrappedPreparedStatement pstmt = new WrappedPreparedStatement();
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                pstmt.addPreparedStatement(this.dbAdapters[i].createPreparedStatement(wc.getConnection(i), query, args));
            }
        }
        else {
            pstmt.addPreparedStatement(this.getDefaultDBAdapter().createPreparedStatement(wc.getConnection(0), query, args));
        }
        return pstmt;
    }
    
    @Override
    public PreparedStatement createPreparedStatement(final Connection conn, final int fetchsize, final String query, final Object... args) throws SQLException {
        final WrappedConnection wc = (WrappedConnection)conn;
        final WrappedPreparedStatement pstmt = new WrappedPreparedStatement();
        if (MultiDSUtil.isMultiDataSourceEnabled()) {
            for (int i = 0; i < PersistenceInitializer.getDatabases().size(); ++i) {
                pstmt.addPreparedStatement(this.dbAdapters[i].createPreparedStatement(wc.getConnection(i), fetchsize, query, args));
            }
        }
        else {
            pstmt.addPreparedStatement(this.getDefaultDBAdapter().createPreparedStatement(wc.getConnection(0), fetchsize, query, args));
        }
        return pstmt;
    }
    
    @Override
    public Statement createReadOnlyStatement(final Connection conn) throws SQLException {
        return this.getReadDBAdapter().createReadOnlyStatement(conn);
    }
    
    static {
        WrappedDBAdapter.OUT = Logger.getLogger(WrappedDBAdapter.class.getName());
    }
}
