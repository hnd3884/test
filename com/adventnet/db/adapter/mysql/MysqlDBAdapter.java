package com.adventnet.db.adapter.mysql;

import com.zoho.mickey.db.mysql.MysqlSQLModifier;
import com.adventnet.ds.query.DataSet;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Arrays;
import java.util.Properties;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import com.zoho.framework.utils.OSCheckUtil;
import com.zoho.cp.LogicalConnection;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.commons.codec.binary.Hex;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import com.zoho.framework.io.DataBufferStream;
import com.adventnet.ds.query.BulkLoad;
import com.zoho.conf.Configuration;
import java.io.File;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.CreateTableLike;
import java.util.Iterator;
import com.adventnet.db.adapter.DCAdapter;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.ds.query.AlterTableQueryImpl;
import java.util.ArrayList;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Column;
import java.sql.PreparedStatement;
import com.zoho.mickey.exception.DataBaseException;
import java.util.StringTokenizer;
import java.sql.DatabaseMetaData;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import java.io.IOException;
import java.sql.DriverManager;
import com.adventnet.persistence.PersistenceUtil;
import java.net.ConnectException;
import com.zoho.conf.AppResources;
import com.adventnet.db.adapter.DBInitializer;
import java.util.Map;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.db.adapter.ResultSetAdapter;
import java.sql.ResultSet;
import java.sql.Connection;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.util.List;
import java.sql.Statement;
import java.util.logging.Logger;
import com.adventnet.db.adapter.Jdbc20DBAdapter;

public class MysqlDBAdapter extends Jdbc20DBAdapter
{
    static MySqlDBInitializer mysInitializer;
    private static final Logger OUT;
    private static String server_home;
    private static final byte[] NULL;
    private static final byte[] TRUE;
    private static final byte[] FALSE;
    private static final int[] MYSQL_ACCESS_DENIED_ERROR_CODES;
    protected boolean readOnlyMode;
    
    public MysqlDBAdapter() {
        this.readOnlyMode = Boolean.getBoolean("app.readonly.mode");
    }
    
    @Override
    public void dropTable(final Statement stmt, final String tableName, final boolean cascade, final List relatedTables) throws SQLException {
        try {
            final String dropSQL = this.sqlGen.getSQLForDrop(tableName, cascade);
            this.execute(stmt, dropSQL);
        }
        catch (final QueryConstructionException excp) {
            throw new SQLException(excp.getMessage());
        }
    }
    
    @Override
    public void createTable(final Statement stmt, final String createSQL, final List relatedTables) throws SQLException {
        this.execute(stmt, createSQL);
    }
    
    @Override
    protected String getUniqueKeyName(final String ukName) {
        return ukName;
    }
    
    @Override
    public void connectTo(final Connection connection, final String dbName) throws SQLException {
        connection.setCatalog(dbName);
    }
    
    @Override
    public void createDB(final Connection connection, final String dbName, final String userName, final String passWord) throws SQLException {
        final Statement stmt = connection.createStatement();
        try {
            this.execute(stmt, "CREATE DATABASE " + dbName);
        }
        finally {
            stmt.close();
        }
    }
    
    @Override
    public String getDefaultDB(final Connection connection) throws SQLException {
        final String url = connection.getMetaData().getURL();
        return url.substring(url.lastIndexOf("/") + 1, (url.indexOf(63) > 0) ? url.lastIndexOf(63) : url.length());
    }
    
    @Override
    public String getDBName(final Connection connection) throws SQLException {
        return connection.getCatalog();
    }
    
    @Override
    protected ResultSetAdapter getResultSetAdapter(final ResultSet rs) throws SQLException {
        final MysqlResultSetAdapter ra = new MysqlResultSetAdapter(rs);
        ra.setRangeHandled(true);
        return ra;
    }
    
    @Override
    public void disableForeignKeyChecks(final Statement stmt) throws SQLException {
        stmt.execute("set SESSION FOREIGN_KEY_CHECKS = 0");
    }
    
    @Override
    public void dropAllTables(final Connection connection, final boolean onlyProductTables) throws SQLException, MetaDataException {
        if (onlyProductTables) {
            super.dropAllTables(connection, onlyProductTables);
        }
        else {
            Statement stmt = null;
            try {
                stmt = connection.createStatement();
                this.disableForeignKeyChecks(stmt);
                final List tableNames = this.getTables(connection, this.getDefaultDB(connection));
                final int size = tableNames.size();
                if (size == 0) {
                    System.out.println("No Tables found in the specified DB.");
                }
                for (int k = 0; k < size; ++k) {
                    final String tableName = tableNames.get(k);
                    String dropSQL = null;
                    try {
                        dropSQL = this.sqlGen.getSQLForDrop(tableName, true);
                        this.execute(stmt, dropSQL);
                        this.printInConsole("Dropped table " + tableName, k % 50 == 0 && k != 0);
                    }
                    catch (final SQLException sqle) {
                        System.err.println("Exception occured while dropping table " + tableName);
                        System.err.println("SQL executed is: " + dropSQL);
                        throw sqle;
                    }
                }
                ConsoleOut.println("");
            }
            catch (final Exception e) {
                e.printStackTrace();
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
                catch (final Exception exc) {
                    exc.printStackTrace();
                }
            }
            finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
                catch (final Exception exc2) {
                    exc2.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public Map splitConnectionURL(final String connectionURL) {
        final Map properties = super.splitConnectionURL(connectionURL);
        if (properties.get("Port") == null) {
            properties.put("Port", new Integer(3306));
        }
        return properties;
    }
    
    @Override
    public DBInitializer getDBInitializer() {
        if (MysqlDBAdapter.mysInitializer == null) {
            MysqlDBAdapter.mysInitializer = new MySqlDBInitializer();
        }
        return MysqlDBAdapter.mysInitializer;
    }
    
    @Override
    public void setUpDB(String connectionURL, final String userName, String password) throws IOException, ConnectException {
        final Map properties = this.splitConnectionURL(connectionURL);
        final String host = properties.get("Server");
        final Integer port = properties.get("Port");
        this.getDBInitializer();
        final int retryStartDB = new Integer(AppResources.getString("retry.startdb", "1"));
        if (AppResources.getString("isDBRunning", "false").equals("false")) {
            int i;
            for (i = 0, i = 0; i < retryStartDB && !MysqlDBAdapter.mysInitializer.startDBServer(port, host, userName, password); ++i) {}
            if (i == retryStartDB) {
                throw new ConnectException("Trying to start MySQL server failed ");
            }
            try {
                if (userName.equals(password) && Boolean.parseBoolean(System.getProperty("gen.db.password", "true"))) {
                    connectionURL = connectionURL.replaceAll(properties.get("DBName"), "mysql");
                    final String generatedPassword = PersistenceUtil.generateRandomPassword();
                    try (final Connection connection = DriverManager.getConnection(connectionURL, userName, password)) {
                        this.changePassword(userName, password, generatedPassword, connection);
                    }
                    System.setProperty("generate.dbparams", "true");
                    password = generatedPassword;
                    this.dbProps.setProperty("password", password);
                    final String encryptedPassword = PersistenceUtil.getDBPasswordProvider(this.getDBType()).getEncryptedPassword(generatedPassword);
                    if (!PersistenceUtil.updatePasswordInDBConf(encryptedPassword)) {
                        throw new IOException("Exception occurred while writing new password to database_params.conf");
                    }
                }
            }
            catch (final Exception e) {
                throw new IOException(e.getMessage(), e);
            }
        }
        else {
            System.out.println("MysqlDB not started since isDBRunning is not set to false.");
        }
    }
    
    @Override
    public boolean createDB(final String connectionURL, final String userName, final String password) throws IOException {
        boolean createDB = false;
        final Map properties = this.splitConnectionURL(connectionURL);
        final String host = properties.get("Server");
        final Integer port = properties.get("Port");
        final String dbName = properties.get("DBName");
        if (MysqlDBAdapter.mysInitializer != null) {
            createDB = MysqlDBAdapter.mysInitializer.createDB(port, host, userName, password, dbName);
        }
        return createDB;
    }
    
    @Override
    public void shutDownDB(final String connectionURL, final String userName, final String password) throws IOException {
        final Map properties = this.splitConnectionURL(connectionURL);
        final Integer port = properties.get("Port");
        final String host = properties.get("Server");
        if (MysqlDBAdapter.mysInitializer != null) {
            MysqlDBAdapter.mysInitializer.stopDBServer(port, host, userName, password);
        }
    }
    
    @Override
    public boolean isIndexModified(final IndexDefinition oldIndexDefinition, final IndexDefinition newIndexDefinition, final List<String> changedAttributes) {
        if (changedAttributes == null || changedAttributes.isEmpty()) {
            return false;
        }
        if (changedAttributes.stream().anyMatch(changedAttribute -> changedAttribute.startsWith("index-column"))) {
            return true;
        }
        if (changedAttributes.stream().anyMatch(changedAttribute -> changedAttribute.startsWith("size of"))) {
            final List<IndexColumnDefinition> oldIcdList = oldIndexDefinition.getColumnDefnitions();
            final List<IndexColumnDefinition> newIcdList = newIndexDefinition.getColumnDefnitions();
            for (int i = 0; i < oldIcdList.size(); ++i) {
                if (oldIcdList.get(i).getSize() != newIcdList.get(i).getSize()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean validateVersion(final Connection con) {
        boolean hasCollation = true;
        boolean versionChk = false;
        final String chkToken = null;
        try {
            final DatabaseMetaData dbm = con.getMetaData();
            final int majorVersion = dbm.getDatabaseMajorVersion();
            final int minorVersion = dbm.getDatabaseMinorVersion();
            MysqlDBAdapter.OUT.log(Level.INFO, "getDatabaseMajorVersion :: [{0}] and getDatabaseMinorVersion :: [{1}]", new Object[] { majorVersion, minorVersion });
            if (majorVersion >= 4) {
                if (majorVersion == 4 && minorVersion == 0) {
                    hasCollation = false;
                }
                versionChk = super.validateVersion(con);
            }
            else {
                ConsoleOut.println("Trying to connect to a incompatible MySQL " + dbm.getDatabaseProductVersion() + " Only MySQL versions 4.0 and above are supported");
            }
        }
        catch (final SQLException sqex) {
            sqex.printStackTrace();
        }
        ((MysqlSQLGenerator)this.sqlGen).setSupportsCollation(hasCollation);
        if (!hasCollation) {
            return versionChk;
        }
        return versionChk;
    }
    
    @Override
    public void prepareDatabase(final Connection con) throws DataBaseException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            String sql = "SHOW CREATE DATABASE `" + this.getDBName(con) + "`";
            rs = stmt.executeQuery(sql);
            String charset = null;
            String createDBStr = null;
            while (rs.next()) {
                createDBStr = rs.getString(2);
                final StringTokenizer stk = new StringTokenizer(createDBStr.substring(createDBStr.indexOf("CHARACTER SET")), "CHARACTER SET", false);
                if (stk.hasMoreTokens()) {
                    charset = stk.nextToken();
                }
            }
            System.out.println("DEFAULT CHARACTER SET IS [" + charset + "]");
            rs.close();
            String collation_name = null;
            if (createDBStr.contains("COLLATE")) {
                createDBStr = createDBStr.substring(createDBStr.indexOf("COLLATE"));
                final String tmp = createDBStr.replace("COLLATE ", "");
                collation_name = tmp.substring(0, tmp.indexOf(32));
                MysqlDBAdapter.OUT.log(Level.INFO, "DATABASE COLLATION IS [{0}]", collation_name);
            }
            String default_collation_name = null;
            sql = "SHOW COLLATION LIKE '%" + charset + "%'";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("Default").equalsIgnoreCase("Yes")) {
                    default_collation_name = rs.getString("Collation");
                    break;
                }
            }
            if (collation_name == null) {
                collation_name = default_collation_name;
            }
            MysqlDBAdapter.OUT.log(Level.INFO, "DEFAULT COLLATION IS [{0}]", default_collation_name);
            final boolean isCS = collation_name.indexOf("_ci") < 0;
            final MysqlSQLGenerator mysqlSQLGenerator = (MysqlSQLGenerator)this.sqlGen;
            MysqlSQLGenerator.collationName = collation_name;
            ((MysqlSQLGenerator)this.sqlGen).setCollation(charset + (isCS ? ((charset.indexOf("_") >= 0) ? "_ci" : "_general_ci") : "_bin"));
            ((MysqlSQLGenerator)this.sqlGen).setCollationForEncryptedCols(collation_name);
            ((MysqlSQLGenerator)this.sqlGen).setIsDBCaseSensitive(isCS);
            ((MysqlSQLGenerator)this.sqlGen).setDefaultCharacterSet(charset);
        }
        catch (final SQLException sqex) {
            throw new DataBaseException(1014, sqex);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public boolean isActive(final Connection c) {
        Statement s = null;
        try {
            s = c.createStatement();
            return s.execute("SELECT 1");
        }
        catch (final Exception e) {
            return false;
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    @Override
    public void setValue(final PreparedStatement ps, final int columnIndex, final int sqlType, final Object value) throws SQLException {
        try {
            if (value == null) {
                ps.setNull(columnIndex, sqlType);
            }
            else if (!(value instanceof Column) && !(value instanceof CaseExpression)) {
                if (sqlType == 16 && value instanceof String) {
                    ps.setObject(columnIndex, Boolean.valueOf((String)value));
                }
                else {
                    ps.setObject(columnIndex, value);
                }
            }
        }
        catch (final SQLException sqle) {
            MysqlDBAdapter.OUT.log(Level.FINER, "Exception in setValue {0}", sqle);
            throw this.handleSQLException(sqle, ps.getConnection(), true);
        }
    }
    
    @Override
    public void alterTable(final Connection connection, final AlterTableQuery alterTableQuery) throws SQLException {
        boolean isCommitRequired = false;
        boolean isExececuted = false;
        Statement statement = null;
        try {
            final int operationType = alterTableQuery.getAlterOperations().get(0).getOperationType();
            DCAdapter dcAdapter = null;
            if (operationType != 19 && operationType != 20 && operationType != 22 && operationType != 21 && operationType != 18) {
                if (!connection.getAutoCommit()) {
                    throw new SQLException("Cannot excute AlterTableQuery in the transaction block");
                }
                connection.setAutoCommit(false);
                isCommitRequired = true;
            }
            else if (operationType != 18) {
                dcAdapter = this.getDCAdapterForTable(alterTableQuery.getTableName());
                if (dcAdapter == null) {
                    throw new MetaDataException("No dynamic column handler defined");
                }
            }
            this.validateAlterTableQuery(connection, alterTableQuery);
            statement = connection.createStatement();
            final List<String> alterSQLs = new ArrayList<String>();
            for (final AlterOperation ao : alterTableQuery.getAlterOperations()) {
                AlterTableQuery alterQuery = new AlterTableQueryImpl(ao.getTableName());
                switch (ao.getOperationType()) {
                    case 1: {
                        final ColumnDefinition coldDef = (ColumnDefinition)ao.getAlterObject();
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.addColumn(coldDef);
                        alterQuery.getAlterOperations().get(0).setFillUVHValue(ao.fillUVHValues());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        if (coldDef.getUniqueValueGeneration() != null && ao.fillUVHValues()) {
                            alterQuery = new AlterTableQueryImpl(ao.getTableName());
                            alterQuery.modifyColumn(coldDef.getColumnName(), coldDef);
                            alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                            alterQuery = new AlterTableQueryImpl(ao.getTableName());
                            alterQuery.removeUniqueKey(coldDef.getColumnName());
                            alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                            continue;
                        }
                        continue;
                    }
                    case 2: {
                        final ColumnDefinition cd = (ColumnDefinition)ao.getAlterObject();
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.modifyColumn(cd.getColumnName(), cd);
                        final List<String> updateSqls = this.sqlGen.getUpdateSQLForModifyColumnDataEncryption(alterQuery);
                        String updateSQL = null;
                        if (!updateSqls.isEmpty()) {
                            updateSQL = updateSqls.get(0);
                        }
                        if (updateSQL != null && updateSQL.contains("AES_DECRYPT")) {
                            alterSQLs.add(updateSQL);
                            updateSQL = null;
                        }
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        if (updateSQL != null) {
                            alterSQLs.add(updateSQL);
                            continue;
                        }
                        continue;
                    }
                    case 3: {
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.removeColumn(ao.getAlterObject().toString());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 4: {
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.addUniqueKey((UniqueKeyDefinition)ao.getAlterObject());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 5: {
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.removeUniqueKey(ao.getAlterObject().toString());
                        alterQuery.getAlterOperations().get(0).setActualConstraintName(ao.getActualConstraintName());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 6: {
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.addForeignKey((ForeignKeyDefinition)ao.getAlterObject());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 7: {
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.removeForeignKey(ao.getAlterObject().toString());
                        alterQuery.getAlterOperations().get(0).setActualConstraintName(ao.getActualConstraintName());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 8: {
                        alterQuery = new AlterTableQueryImpl(ao.getTableName(), 8);
                        alterQuery.setConstraintName(ao.getAlterObject().toString());
                        alterQuery.getAlterOperations().get(0).setActualConstraintName(ao.getActualConstraintName());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 9: {
                        alterQuery = new AlterTableQueryImpl(ao.getTableName(), 9);
                        final PrimaryKeyDefinition pkDef = (PrimaryKeyDefinition)ao.getAlterObject();
                        alterQuery.setConstraintName(pkDef.getName());
                        alterQuery.setPKColumns(pkDef.getColumnList());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 10: {
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.addIndex((IndexDefinition)ao.getAlterObject());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 11: {
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.dropIndex(ao.getAlterObject().toString());
                        alterQuery.getAlterOperations().get(0).setActualConstraintName(ao.getActualConstraintName());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 12: {
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        final String[] renameColumns = (String[])ao.getAlterObject();
                        alterQuery.renameColumn(renameColumns[0], renameColumns[1]);
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 13: {
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.renameTable(ao.getAlterObject().toString());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 14: {
                        final ForeignKeyDefinition fk = (ForeignKeyDefinition)ao.getAlterObject();
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.removeForeignKey(fk.getName());
                        alterQuery.getAlterOperations().get(0).setActualConstraintName(ao.getActualConstraintName());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.addForeignKey(fk);
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 15: {
                        final UniqueKeyDefinition uk = (UniqueKeyDefinition)ao.getAlterObject();
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.removeUniqueKey(uk.getName());
                        alterQuery.getAlterOperations().get(0).setActualConstraintName(ao.getActualConstraintName());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.addUniqueKey(uk);
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 16: {
                        final IndexDefinition idx = (IndexDefinition)ao.getAlterObject();
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.dropIndex(idx.getName());
                        alterQuery.getAlterOperations().get(0).setActualConstraintName(ao.getActualConstraintName());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        alterQuery = new AlterTableQueryImpl(ao.getTableName());
                        alterQuery.addIndex(idx);
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 17: {
                        final Object[] obj = (Object[])ao.getAlterObject();
                        alterQuery = new AlterTableQueryImpl(ao.getTableName(), 8);
                        alterQuery.setConstraintName(obj[0].toString());
                        alterQuery.getAlterOperations().get(0).setActualConstraintName(ao.getActualConstraintName());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        alterQuery = new AlterTableQueryImpl(ao.getTableName(), 9);
                        final PrimaryKeyDefinition pk = (PrimaryKeyDefinition)obj[1];
                        alterQuery.setConstraintName(pk.getName());
                        alterQuery.setPKColumns(pk.getColumnList());
                        alterSQLs.add(this.sqlGen.getSQLForAlterTable(alterQuery));
                        continue;
                    }
                    case 19: {
                        dcAdapter.preAlterTable(connection, alterTableQuery);
                        dcAdapter.addDynamicColumn(connection, alterTableQuery);
                        continue;
                    }
                    case 20: {
                        dcAdapter.preAlterTable(connection, alterTableQuery);
                        dcAdapter.deleteDynamicColumn(connection, alterTableQuery);
                        continue;
                    }
                    case 21: {
                        dcAdapter.preAlterTable(connection, alterTableQuery);
                        dcAdapter.modifyDynamicColumn(connection, alterTableQuery);
                        continue;
                    }
                    case 22: {
                        dcAdapter.preAlterTable(connection, alterTableQuery);
                        dcAdapter.renameDynamicColumn(connection, alterTableQuery);
                        continue;
                    }
                    case 18: {
                        this.handleTableAttributesChange(connection, ao);
                        continue;
                    }
                    default: {
                        throw new IllegalArgumentException("Unknown Operation type specified in AlterOperation");
                    }
                }
            }
            for (final String alterSQL : alterSQLs) {
                MysqlDBAdapter.OUT.log(Level.FINE, "Going to execute :: " + alterSQL);
                this.execute(statement, alterSQL);
            }
            if (isCommitRequired) {
                connection.commit();
                connection.setAutoCommit(true);
            }
            isExececuted = true;
        }
        catch (final MetaDataException mde) {
            throw new IllegalArgumentException(mde);
        }
        catch (final Exception e) {
            final SQLException sqle = new SQLException(e.getMessage());
            sqle.initCause(e);
            throw sqle;
        }
        finally {
            if (statement != null) {
                statement.close();
            }
            if (!isExececuted && isCommitRequired) {
                connection.rollback();
            }
        }
    }
    
    @Override
    public List<AlterTableQuery> getAlterQueryForCopyAllConstraints(final CreateTableLike cloneTableDetails, final boolean copyOnlyFK) throws Exception {
        if (!copyOnlyFK) {
            return super.getAlterQueryForCopyAllConstraints(cloneTableDetails, copyOnlyFK);
        }
        boolean processed = false;
        final TableDefinition tabDef = cloneTableDetails.getCloneTableDefinition();
        if (tabDef == null) {
            throw new QueryConstructionException("Table definition cannot be null");
        }
        final String tableName = tabDef.getTableName();
        final List<AlterTableQuery> query = new ArrayList<AlterTableQuery>();
        final List<ForeignKeyDefinition> fkDefList = tabDef.getForeignKeyList();
        final AlterTableQuery altQuery = new AlterTableQueryImpl(tableName);
        for (final ForeignKeyDefinition foreignKeyDefinition : fkDefList) {
            processed = true;
            altQuery.dropIndex(foreignKeyDefinition.getName() + "_IDX");
        }
        if (processed) {
            query.add(altQuery);
        }
        final AlterTableQuery addQuery = new AlterTableQueryImpl(tableName);
        final List<ForeignKeyDefinition> fkList = tabDef.getForeignKeyList();
        if (fkList != null) {
            for (final ForeignKeyDefinition fkDef : fkList) {
                processed = true;
                addQuery.addForeignKey(fkDef);
            }
        }
        if (processed) {
            query.add(addQuery);
        }
        MysqlDBAdapter.OUT.fine("query returned ::: " + query);
        return query;
    }
    
    @Override
    protected void setDefaultDBHome() {
        Configuration.setString("db.home", MysqlDBAdapter.server_home + File.separator + "mysql");
    }
    
    @Override
    protected String getErrorCodeTableName() {
        return "MySQLErrorCode";
    }
    
    @Override
    protected boolean isMigrateSCHARRequired(final ColumnDefinition colDef) {
        return colDef.getMaxLength() <= 255;
    }
    
    @Override
    protected int getFetchSize() {
        return Integer.MIN_VALUE;
    }
    
    @Override
    public void createDB(final Connection connection, final String dbName, final String userName, final String password, final boolean ignoreIfExists) throws SQLException {
        try {
            super.createDB(connection, dbName, userName, password, ignoreIfExists);
        }
        catch (final SQLException sqle) {
            if (ignoreIfExists && sqle.getErrorCode() == 1007) {
                MysqlDBAdapter.OUT.warning("DataBase " + dbName + " already exists...");
                return;
            }
            throw sqle;
        }
    }
    
    @Override
    public boolean isTableNotFoundException(final SQLException sqle) {
        return (sqle.getErrorCode() == 1146 || sqle.getErrorCode() == -9999) && "42S02".equals(sqle.getSQLState());
    }
    
    @Override
    public void addBatch(final Object[] rowLvlByteValues, final BulkLoad bulk) throws IOException, SQLException {
        String colDataType = "";
        final DataBufferStream dataBufferStream = (DataBufferStream)bulk.getBulkInsertObject().getBulkObject();
        for (int index = 0; index < rowLvlByteValues.length; ++index) {
            Object value = rowLvlByteValues[index];
            colDataType = bulk.getBulkInsertObject().getColTypeNames().get(index);
            if (DataTypeUtil.isEDT(colDataType)) {
                colDataType = DataTypeManager.getDataTypeDefinition(colDataType).getBaseType();
            }
            if (!DataTypeUtil.isUDT(colDataType) || DataTypeManager.getDataTypeDefinition(colDataType).getMeta().processInput()) {
                if (null == value) {
                    dataBufferStream.getOutputStream().write(MysqlDBAdapter.NULL);
                }
                else if (colDataType.equalsIgnoreCase("NCHAR") || colDataType.equalsIgnoreCase("SCHAR") || colDataType.equalsIgnoreCase("CHAR") || colDataType.equalsIgnoreCase("VARCHAR") || colDataType.equalsIgnoreCase("TEXT") || colDataType.equalsIgnoreCase("NTEXT") || colDataType.equalsIgnoreCase("LONGTEXT")) {
                    dataBufferStream.getOutputStream().write(Hex.encodeHexString(super.getBytesForObject(value)).getBytes());
                }
                else if (colDataType.equalsIgnoreCase("BLOB") || colDataType.equalsIgnoreCase("LONGBLOB") || colDataType.equalsIgnoreCase("SBLOB")) {
                    if (value instanceof InputStream) {
                        streamBytea((InputStream)value, dataBufferStream.getOutputStream());
                    }
                    if (value instanceof byte[]) {
                        streamBytea((byte[])value, dataBufferStream.getOutputStream());
                    }
                    if (value instanceof String) {
                        streamBytea(((String)value).getBytes(), dataBufferStream.getOutputStream());
                    }
                }
                else if (colDataType.equalsIgnoreCase("BOOLEAN") || colDataType.equalsIgnoreCase("TINYINT")) {
                    String defaultValue = "";
                    if (value instanceof Short) {
                        defaultValue = Short.toString((short)value);
                        value = defaultValue.getBytes();
                        if (defaultValue.length() > 1) {
                            MysqlDBAdapter.OUT.fine("Writing TinyInt Value ! Value not a boolean.." + value);
                        }
                        dataBufferStream.getOutputStream().write((byte[])value);
                    }
                    else {
                        if (value instanceof String) {
                            defaultValue = String.valueOf(value);
                        }
                        else if (value instanceof Boolean) {
                            defaultValue = Boolean.toString((boolean)value);
                        }
                        else if (value instanceof byte[]) {
                            defaultValue = new String((byte[])value);
                        }
                        if (defaultValue.equalsIgnoreCase("true") || defaultValue.equalsIgnoreCase("1") || defaultValue.equalsIgnoreCase("t") || defaultValue.equalsIgnoreCase("y") || defaultValue.equalsIgnoreCase("yes")) {
                            value = MysqlDBAdapter.TRUE;
                        }
                        else if (defaultValue.equalsIgnoreCase("false") || defaultValue.equalsIgnoreCase("0") || defaultValue.equalsIgnoreCase("f") || defaultValue.equalsIgnoreCase("n") || defaultValue.equalsIgnoreCase("no")) {
                            value = MysqlDBAdapter.FALSE;
                        }
                        else {
                            value = super.getBytesForObject(value);
                        }
                        dataBufferStream.getOutputStream().write((byte[])value);
                    }
                }
                else if (DataTypeUtil.isUDT(colDataType)) {
                    dataBufferStream.getOutputStream().write(DataTypeManager.getDataTypeDefinition(colDataType).getDTAdapter(this.getDBType()).getBytes(value));
                }
                else {
                    dataBufferStream.getOutputStream().write(super.getBytesForObject(value));
                }
                if (index != rowLvlByteValues.length - 1) {
                    dataBufferStream.getOutputStream().write(9);
                }
            }
        }
        dataBufferStream.getOutputStream().write(13);
    }
    
    private static void streamBytea(final InputStream stream, final OutputStream encodingWriter) throws IOException {
        final byte[] bytes = new byte[3072];
        int read = -1;
        while ((read = stream.read(bytes, 0, bytes.length)) != -1) {
            encodingWriter.write(Hex.encodeHexString(bytes).getBytes());
        }
    }
    
    private static void streamBytea(final byte[] bytes, final OutputStream encodingWriter) throws IOException {
        encodingWriter.write(Hex.encodeHexString(bytes).getBytes());
    }
    
    @Override
    public void execBulk(final BulkLoad bulk) throws SQLException, IOException, MetaDataException {
        Statement statement = null;
        try {
            bulk.getBulkInsertObject().setIsReadyToWrite(Boolean.TRUE);
            MysqlDBAdapter.OUT.fine("Exec Bulk has been started!!!");
            statement = ((LogicalConnection)bulk.getConnection()).getPhysicalConnection().createStatement();
            ((com.mysql.jdbc.Statement)statement).setLocalInfileInputStream(((DataBufferStream)bulk.getBulkInsertObject().getBulkObject()).getInputStream());
            statement.execute(bulk.getBulkInsertObject().getSQL());
            MysqlDBAdapter.OUT.fine("Exec Bulk has been terminated!!!");
            MysqlDBAdapter.OUT.log(Level.INFO, "Bulk Load Operation Completed!!!");
        }
        catch (final Throwable e) {
            e.printStackTrace();
            final SQLException sqe = new SQLException(e.getMessage());
            sqe.initCause(e);
            bulk.getBulkInsertObject().setError(sqe);
            throw sqe;
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    @Override
    public boolean isBundledDB() {
        if (this.isBundledDB == null) {
            boolean isBundled = false;
            if (!this.isLoopbackAddress()) {
                MysqlDBAdapter.OUT.log(Level.INFO, "isBundled :: {0}", isBundled);
            }
            else if (!new File(Configuration.getString("db.home") + File.separator + "bin").exists()) {
                MysqlDBAdapter.OUT.log(Level.INFO, "mysql not found at :: {0}", new File(Configuration.getString("db.home") + File.separator + "bin"));
                MysqlDBAdapter.OUT.log(Level.INFO, "isBundled :: {0}", isBundled);
            }
            else {
                try {
                    final List<String> commandList = new ArrayList<String>();
                    final String dbhome = Configuration.getString("db.home").replace("/", File.separator);
                    final boolean isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
                    final String mysqlPath = dbhome + File.separator + "bin" + File.separator + (isWindows ? "mysql.exe" : "mysql");
                    if (!new File(mysqlPath).exists()) {
                        MysqlDBAdapter.OUT.log(Level.INFO, "mysql binary path :: {0}", mysqlPath);
                        throw new IOException("mysql binary does not exists. Make sure whether the Mysql DB bundled with the product.");
                    }
                    final Properties dbProps = this.getDBProps();
                    final String username = dbProps.getProperty("username");
                    final String password = dbProps.getProperty("password", "");
                    String url = dbProps.getProperty("url");
                    if (url.contains("?")) {
                        url = url.substring(0, url.indexOf("?"));
                    }
                    final Map properties = this.splitConnectionURL(url);
                    final String host = properties.get("Server");
                    final Integer port = properties.get("Port");
                    final String tmpHome = dbhome + File.separator + "tmp";
                    commandList.add(mysqlPath);
                    final String myCnf = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "my.cnf";
                    if (new File(myCnf).exists()) {
                        commandList.add("--defaults-file=" + myCnf);
                    }
                    else {
                        commandList.add("--no-defaults");
                    }
                    commandList.add("--port=" + port);
                    if (!isWindows) {
                        if (!new File(tmpHome + File.separator + "mysql.sock").exists()) {
                            commandList.add("--host=" + InetAddress.getByName(host).getHostAddress());
                        }
                        else {
                            if (!host.equals("localhost")) {
                                commandList.add("-h");
                                commandList.add(host);
                            }
                            commandList.add("--socket=" + new File(tmpHome + File.separator + "mysql.sock").getCanonicalPath());
                        }
                    }
                    commandList.add("--user=" + username);
                    commandList.add("--password=" + password);
                    commandList.add("--default-character-set=utf8");
                    commandList.add("-e");
                    commandList.add("SHOW VARIABLES LIKE 'datadir'");
                    final int size = commandList.size();
                    final String[] commandToExecute = commandList.toArray(new String[size]);
                    final Process p = Runtime.getRuntime().exec(commandToExecute);
                    commandList.set(commandList.indexOf("--password=" + password), "--password=*******");
                    MysqlDBAdapter.OUT.log(Level.FINE, "Command to be executed {0} ", commandList);
                    String dataDir = null;
                    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.indexOf("datadir") != -1) {
                            line = line.replace("|", "");
                            line = line.replace("\t", "");
                            line = line.replace("datadir", "");
                            dataDir = line.trim();
                            break;
                        }
                    }
                    final String dataDirectory = new File(Configuration.getString("db.home") + File.separator + "data").getCanonicalPath();
                    if (dataDir != null) {
                        MysqlDBAdapter.OUT.log(Level.INFO, "DB Home from DB:: {0}", new File(dataDir.trim()).getCanonicalPath());
                    }
                    MysqlDBAdapter.OUT.log(Level.INFO, "DB Home from Props :: {0}", dataDirectory);
                    isBundled = (dataDir != null && dataDirectory.equals(new File(dataDir.trim()).getCanonicalPath()));
                }
                catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            this.isBundledDB = isBundled;
        }
        return this.isBundledDB;
    }
    
    @Override
    protected String getBackupHandlerClassName() {
        return MysqlBackupHandler.class.getName();
    }
    
    @Override
    protected String getRestoreHandlerClassName() {
        return MysqlRestoreHandler.class.getName();
    }
    
    @Override
    public SQLException handleSQLException(final SQLException sqle, final Connection connection, final boolean isWrite) throws SQLException {
        final int errorCode = this.getErrorCode(sqle);
        if (this.readOnlyMode && (Arrays.binarySearch(MysqlDBAdapter.MYSQL_ACCESS_DENIED_ERROR_CODES, errorCode) >= 0 || connection.isReadOnly()) && isWrite) {
            MysqlDBAdapter.OUT.log(Level.FINEST, "Ignoring the AccessDenied exception from the mysql server ", sqle);
        }
        else {
            for (Throwable cause = sqle; cause != null; cause = cause.getCause()) {
                if (cause instanceof SQLException && "08S01".equalsIgnoreCase(((SQLException)cause).getSQLState())) {
                    final SQLException sqlcause = (SQLException)cause;
                    ConsoleOut.println("Mysql DB Server is not running in the specified port.");
                    ConsoleOut.println("Check that the hostname and port are correct and Mysql server is accepting connections");
                }
            }
        }
        return super.handleSQLException(sqle, connection, isWrite);
    }
    
    @Override
    public String getDBSpecificExceptionSorterName() {
        return "com.adventnet.db.adapter.mysql.MysqlExceptionSorter";
    }
    
    @Override
    protected void validateAlterTableQuery(final Connection connection, final AlterTableQuery atq) throws SQLException, QueryConstructionException {
        super.validateAlterTableQuery(connection, atq);
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(atq.getTableName());
        }
        catch (final MetaDataException mde) {
            throw new QueryConstructionException("Exception occurred while fetching the tableDefinition for the tableName :: [" + atq.getTableName() + "]", mde);
        }
        for (final AlterOperation ao : atq.getAlterOperations()) {
            final Object alterObject = ao.getAlterObject();
            switch (ao.getOperationType()) {
                case 12:
                case 22: {
                    final String[] names = (String[])alterObject;
                    final String existingColumnName = names[0];
                    final String newColumnName = names[1];
                    for (final ForeignKeyDefinition fk : td.getForeignKeyList()) {
                        if (fk.getFkColumns().contains(existingColumnName)) {
                            throw new QueryConstructionException("This column cannot be renamed since it is participated in foreign-key :: " + fk);
                        }
                    }
                    try {
                        final List<ForeignKeyDefinition> fks = MetaDataUtil.getReferringForeignKeyDefinitions(atq.getTableName());
                        if (fks == null) {
                            continue;
                        }
                        for (final ForeignKeyDefinition fkDef : fks) {
                            if (fkDef.getFkRefColumns().contains(existingColumnName)) {
                                throw new QueryConstructionException("This column cannot be renamed since it is being referred by other tables :: " + fks);
                            }
                        }
                    }
                    catch (final MetaDataException mde2) {
                        throw new QueryConstructionException("Exception occurred while validating for renaming column from :: [" + existingColumnName + "] to [" + newColumnName + "]", mde2);
                    }
                    continue;
                }
            }
        }
    }
    
    @Override
    public boolean hasPermissionForBackup(final Connection conn) throws Exception {
        final String[] previlages = { "Select_priv", "Lock_tables_priv", "Event_priv" };
        final int count = previlages.length;
        String selectStatement = "";
        for (final String prev : previlages) {
            selectStatement = selectStatement + prev + ',';
        }
        selectStatement = selectStatement.subSequence(0, selectStatement.lastIndexOf(44)).toString();
        boolean[] userTable = new boolean[count];
        boolean[] dbTable = new boolean[count];
        userTable = this.checkInUserTable(conn, selectStatement, count);
        dbTable = this.checkInDBTable(conn, selectStatement, count);
        boolean result = true;
        for (int i = 0; i < count; ++i) {
            result = (result && (userTable[i] || dbTable[i]));
        }
        return result;
    }
    
    @Override
    public boolean hasPermissionForRestore(final Connection conn) throws Exception {
        final String[] previlages = { "Insert_priv", "Drop_priv", "Create_priv" };
        final int count = previlages.length;
        String selectStatement = "";
        for (final String prev : previlages) {
            selectStatement = selectStatement + prev + ',';
        }
        selectStatement = selectStatement.subSequence(0, selectStatement.lastIndexOf(44)).toString();
        boolean[] userTable = new boolean[count];
        boolean[] dbTable = new boolean[count];
        userTable = this.checkInUserTable(conn, selectStatement, count);
        dbTable = this.checkInDBTable(conn, selectStatement, count);
        boolean result = true;
        for (int i = 0; i < count; ++i) {
            result = (result && (userTable[i] || dbTable[i]));
        }
        return result;
    }
    
    private boolean[] checkInDBTable(final Connection conn, final String selectStatement, final int count) throws SQLException, QueryConstructionException {
        final boolean[] result = new boolean[count];
        final String query = "SELECT " + selectStatement + " FROM mysql.db WHERE db = ? and User = (SELECT LEFT(CURRENT_USER(),LOCATE('@',CURRENT_USER()) - 1))";
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, this.getDBProps().getProperty("DBName"));
             final ResultSetAdapter rs = this.executeQuery(ps)) {
            MysqlDBAdapter.OUT.log(Level.INFO, ps.toString());
            if (rs.next()) {
                for (int i = 0; i < count; ++i) {
                    result[i] = rs.getBoolean(i + 1);
                }
            }
        }
        return result;
    }
    
    private boolean[] checkInUserTable(final Connection conn, final String selectStatement, final int count) throws SQLException, QueryConstructionException {
        final boolean[] result = new boolean[count];
        final DataSet ds = null;
        final String query = "SELECT " + selectStatement + " FROM mysql.user WHERE User = (SELECT LEFT(CURRENT_USER(),LOCATE('@',CURRENT_USER()) - 1));";
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, new Object[0]);
             final ResultSetAdapter rs = this.executeQuery(ps)) {
            if (rs.next()) {
                for (int i = 0; i < count; ++i) {
                    result[i] = rs.getBoolean(i + 1);
                }
            }
        }
        return result;
    }
    
    @Override
    public long getSizeOfDB(final Connection conn) {
        long size = -1L;
        final String query = "SELECT SUM(data_length + index_length) FROM information_schema.tables WHERE table_schema = ?";
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, this.getDBProps().getProperty("DBName"));
             final ResultSetAdapter rs = this.executeQuery(ps)) {
            MysqlDBAdapter.OUT.log(Level.FINE, ps.toString());
            if (rs.next()) {
                final String databaseSize = rs.getString(1);
                if (databaseSize != null && !databaseSize.equals("")) {
                    size = Long.parseLong(databaseSize);
                }
                else {
                    MysqlDBAdapter.OUT.log(Level.WARNING, "The database must be empty. So size of the database cannot be obtained.");
                }
            }
            else {
                MysqlDBAdapter.OUT.log(Level.WARNING, "The size of the database cannot be obtained");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return size;
    }
    
    @Override
    public String getDBSystemProperty(final Connection conn, final String property) throws SQLException, QueryConstructionException {
        String result = null;
        final String query = "SHOW VARIABLES WHERE variable_name = ?";
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, property);
             final ResultSetAdapter rs = this.executeQuery(ps)) {
            MysqlDBAdapter.OUT.log(Level.FINE, ps.toString());
            if (rs.next()) {
                result = rs.getString(2);
            }
        }
        return result;
    }
    
    @Override
    protected String getDBSpecificSQLModifierName() throws Exception {
        return MysqlSQLModifier.class.getName();
    }
    
    @Override
    public String getDBType() {
        return "mysql";
    }
    
    @Override
    public boolean changePassword(final String userName, final String oldPassword, final String newPassword, final Connection c) throws SQLException {
        if (!this.isBundledDB()) {
            throw new SQLException("ChangePassword can be invoked for the database which has been bundled with the product!!");
        }
        if (oldPassword.equals(newPassword)) {
            MysqlDBAdapter.OUT.log(Level.WARNING, "old and new password is same for the user {0}", userName);
            return false;
        }
        MysqlDBAdapter.OUT.info("Going to change database password for the user: " + userName);
        final String query = "update mysql.user set Password = password('" + newPassword + "')";
        final String query2 = "flush privileges";
        Statement stmt = null;
        boolean result = false;
        try {
            ((MySqlDBInitializer)this.getDBInitializer()).invalidateCommandTemplate();
            stmt = c.createStatement();
            stmt.executeUpdate(query);
            MysqlDBAdapter.OUT.log(Level.FINE, "Executed statement :: [{0}]", new Object[] { query.replace(newPassword, "***********") });
            stmt.executeUpdate(query2);
            MysqlDBAdapter.OUT.log(Level.FINE, "Executed statement :: [{0}]", new Object[] { query2 });
            result = true;
        }
        catch (final SQLException e) {
            result = false;
            throw e;
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return result;
    }
    
    @Override
    public boolean startDB(final String connectionURL, final String userName, final String password) throws Exception {
        final Map properties = this.splitConnectionURL(connectionURL);
        final String host = properties.get("Server");
        final Integer port = properties.get("Port");
        return this.getDBInitializer().startDBServer(port, host, userName, password);
    }
    
    static {
        MysqlDBAdapter.mysInitializer = null;
        OUT = Logger.getLogger(MysqlDBAdapter.class.getName());
        MysqlDBAdapter.server_home = Configuration.getString("server.home", "app.home");
        NULL = "\\N".getBytes();
        TRUE = "1".getBytes();
        FALSE = "0".getBytes();
        MYSQL_ACCESS_DENIED_ERROR_CODES = new int[] { 1044, 1045, 1142, 1143, 1227, 1370, 1698 };
    }
}
