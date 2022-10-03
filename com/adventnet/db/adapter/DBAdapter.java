package com.adventnet.db.adapter;

import java.util.Collection;
import com.zoho.mickey.db.SQLModifier;
import java.util.concurrent.ExecutorService;
import java.sql.ResultSet;
import com.adventnet.ds.query.BulkLoad;
import com.adventnet.ds.query.UpdateQuery;
import java.sql.DatabaseMetaData;
import java.util.Map;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.ds.query.ArchiveTable;
import com.adventnet.ds.query.CreateTableLike;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.zoho.mickey.exception.DataBaseException;
import java.util.Properties;
import java.net.ConnectException;
import java.io.IOException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.List;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Criteria;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Connection;
import com.adventnet.ds.adapter.DataSourceAdapter;

public interface DBAdapter extends DataSourceAdapter
{
    public static final String JDBC_URL_PROPS_SEPARATOR = "jdbcurl_props_separator";
    public static final String URL_WITHOUT_PROPS = "urlWithoutProps";
    public static final String PROPS_DELIMITER = "url_props_delimiter";
    public static final String URL_PROPS = "urlProps";
    
    Connection createConnection(final String p0, final String p1, final String p2, final String p3) throws SQLException, ClassNotFoundException;
    
    ResultSetAdapter executeQuery(final Statement p0, final String p1) throws SQLException;
    
    ResultSetAdapter executeQuery(final PreparedStatement p0) throws SQLException;
    
    int executeUpdate(final Statement p0, final String p1) throws SQLException;
    
    boolean executeDelete(final String p0, final Criteria p1, final Connection p2) throws SQLException, QueryConstructionException;
    
    int executeUpdate(final PreparedStatement p0) throws SQLException;
    
    boolean execute(final Statement p0, final String p1) throws SQLException;
    
    boolean execute(final PreparedStatement p0) throws SQLException;
    
    @Deprecated
    List getTables(final Connection p0) throws SQLException;
    
    List getTables(final Connection p0, final String p1) throws SQLException;
    
    void dropTable(final Statement p0, final String p1, final boolean p2, final List p3) throws SQLException;
    
    void createTable(final Statement p0, final String p1, final List p2) throws SQLException;
    
    void createTables(final Statement p0, final String p1, final List p2, final List p3) throws SQLException;
    
    void createTable(final Statement p0, final TableDefinition p1, final List p2) throws SQLException;
    
    void createTable(final Statement p0, final TableDefinition p1, final String p2, final List p3) throws SQLException;
    
    void alterTable(final Connection p0, final AlterTableQuery p1) throws SQLException;
    
    void setSQLGenerator(final SQLGenerator p0);
    
    SQLGenerator getSQLGenerator();
    
    void setValue(final PreparedStatement p0, final int p1, final int p2, final Object p3) throws SQLException;
    
    void connectTo(final Connection p0, final String p1) throws SQLException;
    
    void createDB(final Connection p0, final String p1, final String p2, final String p3) throws SQLException;
    
    String getDefaultDB(final Connection p0) throws SQLException;
    
    String getDBName(final Connection p0) throws SQLException;
    
    void dropAllTables(final Connection p0, final boolean p1) throws SQLException, MetaDataException;
    
    void setUpDB(final String p0, final String p1, final String p2) throws IOException, ConnectException;
    
    boolean createDB(final String p0, final String p1, final String p2) throws IOException;
    
    void shutDownDB(final String p0, final String p1, final String p2) throws IOException;
    
    boolean validateVersion(final Connection p0);
    
    void checkDBStatus(final String p0);
    
    SQLException handleSQLException(final SQLException p0, final Connection p1, final boolean p2) throws SQLException;
    
    int fileBackup(final String p0, final String p1, final List<String> p2, final String p3, final Properties p4) throws Exception;
    
    boolean abortBackup() throws Exception;
    
    Properties getDBProps();
    
    boolean isActive(final Connection p0);
    
    boolean setPassword(final String p0, final String p1, final Connection p2) throws Exception;
    
    List getShutDownStrings();
    
    void handlePreExecute(final Connection p0, final List<String> p1, final List<String> p2) throws Exception;
    
    void handlePostExecute(final Connection p0, final List<String> p1, final List<String> p2) throws Exception;
    
    void prepareDatabase(final Connection p0) throws DataBaseException;
    
    void migrateScharDatatype(final List<ColumnDefinition> p0) throws SQLException;
    
    boolean isTablePresentInDB(final Connection p0, final String p1, final String p2) throws SQLException;
    
    List<AlterTableQuery> getAlterQueryForCopyAllConstraints(final CreateTableLike p0, final boolean p1) throws Exception;
    
    List<AlterTableQuery> getATQForRemoveAllConstraints(final ArchiveTable p0) throws Exception;
    
    String getIdentifiersFileName();
    
    boolean startDB(final String p0, final String p1, final String p2) throws Exception;
    
    void stopDB(final String p0, final String p1, final String p2) throws Exception;
    
    boolean isColumnModified(final ColumnDefinition p0, final ColumnDefinition p1, final List<String> p2);
    
    boolean isIndexModified(final IndexDefinition p0, final IndexDefinition p1, final List<String> p2);
    
    Map splitConnectionURL(final String p0);
    
    long getApproxRowCount(final String p0, final DatabaseMetaData p1) throws SQLException;
    
    List<String> getColumnNamesFromDB(final String p0, final String p1, final DatabaseMetaData p2) throws SQLException;
    
    boolean isMaxLengthComparable(final String p0);
    
    String getPKNameOfTheTable(final String p0, final DatabaseMetaData p1) throws SQLException;
    
    List<String> getPKColumnNameOfTheTable(final String p0, final DatabaseMetaData p1) throws SQLException;
    
    void createDB(final Connection p0, final String p1, final String p2, final String p3, final boolean p4) throws SQLException;
    
    @Deprecated
    Statement createReadOnlyStatement(final Connection p0) throws SQLException;
    
    Statement createStatement(final Connection p0, final int p1) throws SQLException;
    
    Statement createStatement(final Connection p0) throws SQLException;
    
    String getTableName(final String p0);
    
    void loadFunctionTemplates() throws Exception;
    
    boolean isTableNotFoundException(final SQLException p0);
    
    PreparedStatement createInsertStatement(final String p0, final Connection p1) throws SQLException, MetaDataException, QueryConstructionException;
    
    PreparedStatement createUpdateStatement(final String p0, final int[] p1, final Connection p2) throws SQLException, MetaDataException, QueryConstructionException;
    
    PreparedStatement createUpdateStatement(final UpdateQuery p0, final Connection p1) throws SQLException, MetaDataException, QueryConstructionException;
    
    int[] executeBatch(final Statement p0) throws SQLException;
    
    int[] executeBatch(final PreparedStatement p0) throws SQLException;
    
    void execBulk(final BulkLoad p0) throws SQLException, IOException, MetaDataException;
    
    void addBatch(final Object[] p0, final BulkLoad p1) throws IOException, SQLException;
    
    BulkInsertObject createBulkInsertObject(final BulkLoad p0) throws IOException, SQLException, QueryConstructionException, MetaDataException;
    
    void closeBulkInsertObject(final BulkLoad p0) throws SQLException, IOException;
    
    ResultSet getFKMetaData(final Connection p0, final String p1) throws SQLException;
    
    ExecutorService getBulkThreadExecutor();
    
    BackupHandler getBackupHandler();
    
    RestoreHandler getRestoreHandler();
    
    List<String> getAllDatabaseNames(final Connection p0);
    
    DBInitializer getDBInitializer();
    
    boolean isBundledDB();
    
    String getDBSpecificExceptionSorterName();
    
    boolean hasPermissionForBackup(final Connection p0) throws Exception;
    
    boolean hasPermissionForRestore(final Connection p0) throws Exception;
    
    long getSizeOfDB(final Connection p0);
    
    String getDBSystemProperty(final Connection p0, final String p1) throws SQLException, QueryConstructionException;
    
    void setValue(final PreparedStatement p0, final int p1, final Map<String, Integer> p2, final Map<String, Object> p3, final String p4) throws SQLException;
    
    void logDatabaseDetails();
    
    long getTotalRowCount(final Connection p0, final String p1) throws SQLException;
    
    void truncateTable(final Statement p0, final String p1) throws SQLException;
    
    String getDBType();
    
    DCAdapter getDCAdapterForTable(final String p0);
    
    DCAdapter getDCAdapter(final String p0);
    
    SQLModifier getSQLModifier();
    
    List<String> getTableNamesLike(final Connection p0, final String p1, final String p2) throws SQLException;
    
    List<String> getColumnNames(final Connection p0, final String p1, final String p2) throws SQLException;
    
    String getDBSpecificAbortHandlerName();
    
    int[] executeBatch(final Connection p0, final String p1, final Collection<Object[]> p2) throws SQLException;
    
    PreparedStatement createPreparedStatement(final Connection p0, final String p1, final Object... p2) throws SQLException;
    
    PreparedStatement createPreparedStatement(final Connection p0, final int p1, final String p2, final Object... p3) throws SQLException;
    
    boolean changePassword(final String p0, final String p1, final String p2, final Connection p3) throws SQLException;
    
    default boolean isReadOnly(final Connection connection) throws Exception {
        throw new UnsupportedOperationException("read only is not handled");
    }
}
