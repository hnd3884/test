package com.adventnet.db.adapter.postgres;

import com.zoho.mickey.db.postgres.PostgresSQLModifier;
import com.adventnet.ds.query.DataSet;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.Arrays;
import java.sql.BatchUpdateException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import org.postgresql.copy.CopyManager;
import com.zoho.cp.LogicalConnection;
import org.postgresql.core.BaseConnection;
import org.apache.commons.lang3.StringUtils;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.ds.query.BulkLoadStatementGenerator;
import com.zoho.framework.io.DataBufferStream;
import com.adventnet.db.adapter.BulkInsertObject;
import com.adventnet.ds.query.BulkLoad;
import java.util.TreeMap;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import java.io.FileInputStream;
import com.adventnet.persistence.ErrorCodes;
import com.zoho.mickey.exception.DataBaseException;
import com.adventnet.db.adapter.DBAdapter;
import com.zoho.mickey.tools.CreateDBUser;
import com.zoho.mickey.tools.postgres.CreatePostgresDBUser;
import java.sql.DriverManager;
import com.adventnet.persistence.PersistenceUtil;
import java.net.ConnectException;
import com.zoho.conf.AppResources;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.File;
import com.adventnet.db.adapter.DBInitializer;
import com.adventnet.mfw.ConsoleOut;
import java.io.IOException;
import com.adventnet.ds.query.Column;
import java.io.InputStream;
import java.sql.PreparedStatement;
import com.adventnet.db.adapter.ResultSetAdapter;
import java.util.Locale;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Map;
import com.adventnet.db.adapter.DCAdapter;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.AlterOperation;
import java.sql.Connection;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.Iterator;
import com.adventnet.ds.query.AlterTableQueryImpl;
import java.util.Collections;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.List;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import com.zoho.conf.Configuration;
import java.util.Collection;
import java.util.logging.Level;
import java.util.Properties;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Set;
import java.util.logging.Logger;
import com.adventnet.db.adapter.Jdbc20DBAdapter;

public class PostgresDBAdapter extends Jdbc20DBAdapter
{
    DefaultPostgresDBInitializer pgInitializer;
    private static final Logger OUT;
    private static final String SEPARATOR;
    private static final String DEFAULT_ENCRYPT_ALGO = "aes256";
    private static final String DEFAULT_ENCRYPT_S2KMODE = "1";
    protected Set<String> reservedKeyWords;
    private static String server_home;
    private static final byte[] NULL;
    private static final byte[] APPEND_HEX;
    private static final char[] HEXARRAY;
    
    public PostgresDBAdapter() {
        this.pgInitializer = null;
        this.reservedKeyWords = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    }
    
    private void initDBInitializer(final Properties props) {
        if (this.pgInitializer == null) {
            final String dbInitializerClassName = props.getProperty("dbinitializer", null);
            if (dbInitializerClassName != null && !dbInitializerClassName.equals("")) {
                PostgresDBAdapter.OUT.log(Level.INFO, "DBInitializer class name specified ::: {0}", dbInitializerClassName);
                try {
                    this.pgInitializer = (DefaultPostgresDBInitializer)Thread.currentThread().getContextClassLoader().loadClass(dbInitializerClassName).newInstance();
                }
                catch (final Exception ex) {
                    PostgresDBAdapter.OUT.log(Level.SEVERE, "Exception occured while initializing DBInitializer :::: {0} \n Exception : {1}", new Object[] { dbInitializerClassName, ex });
                }
            }
            else {
                this.pgInitializer = new DefaultPostgresDBInitializer();
            }
            this.pgInitializer.initialize(props);
        }
    }
    
    @Override
    public void initialize(final Properties props) {
        super.initialize(props);
        PostgresDBAdapter.isAutoQuoteEnabled = Boolean.valueOf(props.getProperty("AUTO_QUOTE_IDENTIFIERS", "false"));
        ((PostgresSQLGenerator)this.sqlGen).setAutoQuote(PostgresDBAdapter.isAutoQuoteEnabled);
        if (!PostgresDBAdapter.isAutoQuoteEnabled) {
            this.reservedKeyWords.addAll(this.getListFromIdentifierFile());
            ((PostgresSQLGenerator)this.sqlGen).addReservedKeyWords(this.reservedKeyWords);
        }
        final PostgresSQLGenerator pgsqlGen = (PostgresSQLGenerator)this.sqlGen;
        final String encryptAlgo = props.getProperty("encryption.algo", "aes256");
        pgsqlGen.setEncryptionAlgorithm(encryptAlgo);
        final String encryptS2kMode = props.getProperty("encryption.s2kmode", "1");
        pgsqlGen.setEncryptionS2kMode(encryptS2kMode);
        final String dbServiceName = props.getProperty("dbservicename", null);
        this.initDBInitializer(props);
        if (dbServiceName != null) {
            final DefaultPostgresDBInitializer pgInitializer = this.pgInitializer;
            DefaultPostgresDBInitializer.dbServiceName = dbServiceName;
        }
        PostgresConfUtil.setDBInitializer(this.pgInitializer);
        if (props.getProperty("db.default.authmode", "md5").equalsIgnoreCase("trust")) {
            throw new IllegalArgumentException("Default Auth Mode cannot be trust");
        }
        PostgresConfUtil.setDefaultAuthMode(props.getProperty("db.default.authmode", "md5"));
    }
    
    @Override
    protected void setDefaultDBHome() {
        Configuration.setString("db.home", PostgresDBAdapter.server_home + PostgresDBAdapter.SEPARATOR + "pgsql");
    }
    
    @Override
    @Deprecated
    protected ResultSet getTablesFromDB(final DatabaseMetaData metaData) throws SQLException {
        return this.getTablesFromDB(metaData, metaData.getUserName());
    }
    
    @Override
    protected ResultSet getTablesFromDB(final DatabaseMetaData metaData, final String schemaName) throws SQLException {
        return metaData.getTables(null, null, "%", new String[] { "TABLE" });
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
    
    public void dropTable(final Statement stmt, final String tableName, final boolean ifExists, final boolean cascade, final List relatedTables) throws SQLException {
        try {
            final String dropSQL = this.sqlGen.getSQLForDrop(tableName, ifExists, cascade);
            this.execute(stmt, dropSQL);
        }
        catch (final QueryConstructionException excp) {
            throw new SQLException(excp.getMessage());
        }
    }
    
    @Override
    public void createTable(final Statement stmt, final String createSQL, final List relatedTables) throws SQLException {
        PostgresDBAdapter.OUT.log(Level.FINER, "In PostgresDBAdapter createSQL : {0}", createSQL);
        this.execute(stmt, createSQL);
    }
    
    @Override
    public void createTable(final Statement stmt, final TableDefinition tabDefn, final String createTableOptions, final List relatedTables) throws SQLException {
        if (!tabDefn.creatable()) {
            PostgresDBAdapter.OUT.log(Level.INFO, "Not creating the table :: [{0}], since it is specified as CREATETABLE false.", tabDefn.getTableName());
            return;
        }
        try {
            final String createSQL = this.sqlGen.getSQLForCreateTable(tabDefn, createTableOptions);
            this.createTable(stmt, createSQL, relatedTables);
            if (tabDefn.getPhysicalColumns() != null && !tabDefn.getPhysicalColumns().isEmpty()) {
                final List<ColumnDefinition> dyCols = new ArrayList<ColumnDefinition>(tabDefn.getDynamicColumnList().size());
                for (int i = 0; i < tabDefn.getDynamicColumnList().size(); ++i) {
                    dyCols.add(new ColumnDefinition());
                }
                Collections.copy((List<? super Object>)dyCols, (List<?>)tabDefn.getDynamicColumnList());
                for (final ColumnDefinition cd : dyCols) {
                    tabDefn.removeColumnDefinition(cd.getColumnName());
                }
                for (final ColumnDefinition cd : dyCols) {
                    final AlterTableQuery atq = new AlterTableQueryImpl(tabDefn.getTableName());
                    atq.addDynamicColumn(cd);
                    atq.setTableDefinition(tabDefn);
                    this.alterTable(stmt.getConnection(), atq);
                    tabDefn.addColumnDefinition(cd);
                }
            }
        }
        catch (final QueryConstructionException excp) {
            throw new SQLException(excp.getMessage());
        }
    }
    
    @Override
    protected String getUniqueKeyName(final String ukName) {
        return ukName;
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
                case 18: {
                    this.handleTableAttributesChange(connection, alterTableQuery.getAlterOperations().get(0));
                    break;
                }
                default: {
                    final StringBuilder alterSQL = new StringBuilder();
                    alterSQL.append(this.sqlGen.getSQLForAlterTable(alterTableQuery));
                    final Map columnVsConstraintName = this.getCitextColumnVsConstraintName(alterTableQuery.getTableName(), connection);
                    final String alterCheckConstraintSql = ((PostgresSQLGenerator)this.sqlGen).getSQLForAlterMaxSizeCheckConstraint(alterTableQuery, columnVsConstraintName);
                    PostgresDBAdapter.OUT.log(Level.FINE, "checkConstraint change query :: {0}", alterCheckConstraintSql);
                    alterSQL.append(alterCheckConstraintSql);
                    this.checkForDuplicateUK(connection, alterTableQuery, alterSQL);
                    PostgresDBAdapter.OUT.log(Level.FINE, "Going to exceute alter sql  :: {0}", alterSQL);
                    statement = connection.createStatement();
                    statement.execute(alterSQL.toString());
                    if (isCommitRequired) {
                        connection.commit();
                        connection.setAutoCommit(true);
                    }
                    isExececuted = true;
                    break;
                }
            }
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
    
    protected void checkForDuplicateUK(final Connection c, final AlterTableQuery atq, final StringBuilder alterSQL) throws SQLException, MetaDataException, QueryConstructionException {
        for (final AlterOperation ao : atq.getAlterOperations()) {
            if (ao.getOperationType() == 5) {
                final String ukName = (String)ao.getAlterObject();
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(atq.getTableName());
                final List<UniqueKeyDefinition> uks = td.getUniqueKeys();
                if (uks == null) {
                    continue;
                }
                final UniqueKeyDefinition actualUK = td.getUniqueKeyDefinitionByName(ukName);
                if (actualUK == null) {
                    continue;
                }
                for (final UniqueKeyDefinition uk : uks) {
                    if (uk.getColumns().equals(actualUK.getColumns()) && !uk.getName().equals(ukName)) {
                        final DatabaseMetaData metaData = c.getMetaData();
                        String dbSpecificTableName = this.sqlGen.getDBSpecificTableName(atq.getTableName());
                        if (dbSpecificTableName.equals(atq.getTableName())) {
                            dbSpecificTableName = dbSpecificTableName.toLowerCase(Locale.ENGLISH);
                        }
                        boolean isUKPresentInDB = false;
                        ResultSet rs = null;
                        try {
                            rs = metaData.getIndexInfo(null, null, dbSpecificTableName, true, true);
                            while (rs.next()) {
                                final String constrintName = rs.getString("INDEX_NAME");
                                if (constrintName.equalsIgnoreCase(uk.getName())) {
                                    isUKPresentInDB = true;
                                    break;
                                }
                            }
                        }
                        finally {
                            if (rs != null) {
                                rs.close();
                            }
                        }
                        if (!isUKPresentInDB) {
                            PostgresDBAdapter.OUT.log(Level.INFO, "Creating duplicate UK(\"{0}\"), which not exists in DB now, as this UK not got created during table creation(Hint : duplicate UK constraint).", uk.getName());
                            final AlterTableQuery newAtq = new AlterTableQueryImpl(atq.getTableName());
                            newAtq.addUniqueKey(uk);
                            alterSQL.append(";");
                            alterSQL.append(this.sqlGen.getSQLForAlterTable(newAtq));
                            break;
                        }
                        continue;
                    }
                }
            }
        }
    }
    
    @Override
    public void connectTo(final Connection connection, final String dbName) throws SQLException {
        connection.setCatalog(dbName);
    }
    
    @Override
    public void createDB(final Connection connection, final String dbName, final String userName, final String passWord) throws SQLException {
        final Statement stmt = connection.createStatement();
        try {
            this.execute(stmt, "CREATE DATABASE \"" + dbName + "\"");
        }
        finally {
            stmt.close();
        }
    }
    
    @Override
    public String getDefaultDB(final Connection connection) throws SQLException {
        final String url = connection.getMetaData().getURL();
        return url.substring(url.lastIndexOf("/") + 1, url.length());
    }
    
    @Override
    public String getDBName(final Connection connection) throws SQLException {
        return connection.getCatalog();
    }
    
    @Override
    protected ResultSetAdapter getResultSetAdapter(final ResultSet rs) throws SQLException {
        final PostgresResultSetAdapter ra = new PostgresResultSetAdapter(rs);
        ra.setRangeHandled(true);
        return ra;
    }
    
    @Override
    public Map splitConnectionURL(final String connectionURL) {
        final Map properties = super.splitConnectionURL(connectionURL);
        if (properties.get("Port") == null) {
            properties.put("Port", new Integer(5432));
        }
        return properties;
    }
    
    @Override
    public void setValue(final PreparedStatement ps, final int columnIndex, final int sqlType, final Object value) throws SQLException {
        try {
            if (sqlType == 2004) {
                if (value != null && value instanceof InputStream) {
                    final InputStream stream = (InputStream)value;
                    ps.setBinaryStream(columnIndex, stream, stream.available());
                }
                else if (value instanceof String) {
                    ps.setBytes(columnIndex, ((String)value).getBytes());
                }
                else {
                    ps.setBytes(columnIndex, (byte[])value);
                }
            }
            else if (value != null && !(value instanceof Column)) {
                ps.setObject(columnIndex, value, sqlType);
            }
            else {
                super.setValue(ps, columnIndex, sqlType, value);
            }
        }
        catch (final IOException ioe) {
            final SQLException sqle = new SQLException(ioe.getMessage());
            sqle.initCause(ioe);
            throw sqle;
        }
    }
    
    @Override
    public void dropAllTables(final Connection connection, final boolean onlyProductTables) throws SQLException, MetaDataException {
        if (onlyProductTables) {
            super.dropAllTables(connection, onlyProductTables);
        }
        else {
            List tables = new ArrayList(this.getTables(connection));
            if (tables.size() > 0) {
                List tablesNotDropped = null;
                Statement stmt = null;
                try {
                    stmt = connection.createStatement();
                    do {
                        tablesNotDropped = new ArrayList();
                        for (int size = tables.size(), k = 0; k < size; ++k) {
                            final String tableName = tables.get(k);
                            String dropSQL = null;
                            try {
                                dropSQL = this.sqlGen.getSQLForDrop(tableName, true);
                                this.execute(stmt, dropSQL);
                                this.printInConsole("Dropped table " + tableName, k % 50 == 0 && k != 0);
                            }
                            catch (final SQLException sqle) {
                                tablesNotDropped.add(tableName);
                            }
                        }
                        ConsoleOut.println("");
                        tables = new ArrayList(tablesNotDropped);
                    } while (tables.size() != 0);
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
            else {
                PostgresDBAdapter.OUT.info("No Tables found in the specified DB.");
            }
        }
    }
    
    @Override
    public DBInitializer getDBInitializer() {
        return this.pgInitializer;
    }
    
    @Override
    public boolean startDB(final String connectionURL, final String userName, final String password) throws Exception {
        final Map properties = this.splitConnectionURL(connectionURL);
        final String host = properties.get("Server");
        final Integer port = properties.get("Port");
        this.pgInitializer.checkForPgIsReadybinary();
        return this.getDBInitializer().startDBServer(port, host, userName, password);
    }
    
    @Override
    public void setUpDB(final String connectionURL, final String userName, final String password) throws IOException {
        final Map properties = this.splitConnectionURL(connectionURL);
        final String host = properties.get("Server");
        Integer port = properties.get("Port");
        final String dbName = properties.get("DBName");
        final String postgresConfFilePath = this.buildString(this.pgInitializer.dbhome, File.separator, "data", File.separator, "postgresql.conf");
        final String dbParamsFile = PersistenceInitializer.getDBParamsFilePath();
        if (this.pgInitializer.hasDBService()) {
            final int portInPostgresConf = this.getPortFromPostgresConf(postgresConfFilePath);
            if (port != portInPostgresConf) {
                throw new IOException("The port [" + port + "] specified in " + dbParamsFile + " is different from " + postgresConfFilePath + " port [" + portInPostgresConf + "]. Please correct the port");
            }
        }
        this.pgInitializer.checkForPgIsReadybinary();
        this.pgInitializer = (DefaultPostgresDBInitializer)this.getDBInitializer();
        final boolean serverRunning = this.pgInitializer.checkServerStatus(host, port, userName);
        if (serverRunning) {
            try {
                this.pgInitializer.validateDataDirectory(host, port, userName, password);
                PostgresDBAdapter.OUT.log(Level.INFO, "DB server already running in :: {0}", this.pgInitializer.dbhome);
                try {
                    this.createVersionFile(port, host, userName, password, "postgres");
                }
                catch (final IOException ioe) {
                    PostgresDBAdapter.OUT.log(Level.INFO, "Problem while creating version file");
                }
                return;
            }
            catch (final Exception e) {
                if (AppResources.getString("useAvailableDBPort", "false").equals("false")) {
                    throw new ConnectException("Unable to start Postgres server on port " + port + ", since another instance of postgres is running in this port.");
                }
                port = DBInitializer.checkAndChangeDBPort(PersistenceInitializer.getDBParamsFilePath(), -1);
                Configuration.setString("generate.dbparams", "true");
                this.startAndCheckDBServer(connectionURL, userName, password, port);
                try {
                    this.createVersionFile(port, host, userName, password, "postgres");
                }
                catch (final IOException ioe2) {
                    PostgresDBAdapter.OUT.log(Level.INFO, "Problem while creating version file");
                }
                return;
            }
        }
        this.startAndCheckDBServer(connectionURL, userName, password, port);
        try {
            final String newPassword = PersistenceInitializer.getDBProps(PersistenceInitializer.getDBParamsFilePath()).getProperty("password");
            this.createVersionFile(port, host, userName, newPassword, "postgres");
        }
        catch (final Exception ioe3) {
            PostgresDBAdapter.OUT.log(Level.INFO, "Problem while creating version file");
        }
    }
    
    protected void startAndCheckDBServer(final String connectionURL, final String userName, String password, final int port) throws IOException {
        final Map properties = this.splitConnectionURL(connectionURL);
        final String host = properties.get("Server");
        this.pgInitializer.startDBServer(port, host, userName, password);
        if (PostgresConfUtil.isDefaultModeTrust("127.0.0.1/32", "all")) {
            throw new IllegalArgumentException("Auth Mode cannot be trust. Kindly change and restart");
        }
        final boolean generate = Boolean.parseBoolean(Configuration.getString("gen.db.password", "true"));
        final String generatedPassword = PersistenceUtil.generateRandomPassword();
        final String superUserKey = "superuser_pass";
        String superUserPassword = this.getDBProps().getProperty(superUserKey);
        try {
            String url = connectionURL;
            final Map connectionURLProps = this.splitConnectionURL(connectionURL);
            url = url.replaceAll(connectionURLProps.get("DBName"), "template1");
            if (superUserPassword != null && !superUserPassword.isEmpty()) {
                superUserPassword = PersistenceUtil.getDBPasswordProvider(this.getDBType()).getPassword(superUserPassword);
                try (final Connection connection = DriverManager.getConnection(url, "postgres", superUserPassword)) {
                    if (this.isRole(connection, "rolcanlogin", userName)) {
                        PostgresDBAdapter.OUT.log(Level.SEVERE, "Role {0} already exists", userName);
                        if (userName.equalsIgnoreCase(password) && generate) {
                            try (final Connection connection2 = DriverManager.getConnection(url, userName, password)) {
                                this.changePassword(userName, password, generatedPassword, connection2);
                            }
                            Configuration.setString("generate.dbparams", "true");
                            password = generatedPassword;
                            this.dbProps.setProperty("password", password);
                            final String encryptedPassword = PersistenceUtil.getDBPasswordProvider(this.getDBType()).getEncryptedPassword(generatedPassword);
                            if (!PersistenceUtil.updatePasswordInDBConf(encryptedPassword)) {
                                throw new IOException("Exception occurred while writing new password to database_params.conf");
                            }
                        }
                    }
                    else {
                        String newPassword = password;
                        if (generate && userName.equalsIgnoreCase(password)) {
                            newPassword = PersistenceUtil.generateRandomPassword();
                        }
                        String dbUserCreatorClassName = null;
                        final Properties postgresProps = PersistenceInitializer.getConfigurationProps("postgres");
                        if (postgresProps != null) {
                            dbUserCreatorClassName = postgresProps.getProperty("dbusercreator");
                        }
                        if (dbUserCreatorClassName == null) {
                            dbUserCreatorClassName = CreatePostgresDBUser.class.getName();
                        }
                        final CreateDBUser createDBUser = (CreateDBUser)Thread.currentThread().getContextClassLoader().loadClass(dbUserCreatorClassName).newInstance();
                        createDBUser.createUser((DBAdapter)this, url, "postgres", superUserPassword, userName, newPassword, "default");
                        if (generate) {
                            Configuration.setString("generate.dbparams", "true");
                            final String encryptedPassword2 = PersistenceUtil.getDBPasswordProvider(this.getDBType()).getEncryptedPassword(newPassword);
                            if (!PersistenceUtil.updatePasswordInDBConf(encryptedPassword2)) {
                                throw new IOException("Exception occurred while writing new password to database_params.conf");
                            }
                        }
                        password = newPassword;
                        this.dbProps.setProperty("password", password);
                    }
                }
            }
            else if (userName.equalsIgnoreCase(password) && generate) {
                try (final Connection connection = DriverManager.getConnection(url, userName, password)) {
                    this.changePassword(userName, password, generatedPassword, connection);
                }
                Configuration.setString("generate.dbparams", "true");
                password = generatedPassword;
                this.dbProps.setProperty("password", password);
                final String encryptedPassword3 = PersistenceUtil.getDBPasswordProvider(this.getDBType()).getEncryptedPassword(generatedPassword);
                if (!PersistenceUtil.updatePasswordInDBConf(encryptedPassword3)) {
                    throw new IOException("Exception occurred while writing new password to database_params.conf");
                }
            }
        }
        catch (final Exception e) {
            throw new IOException(e.getMessage(), e);
        }
        final boolean dbReadyToAcceptConnection = this.pgInitializer.isDBReadyToAcceptConnection(port, host, userName, password);
        PostgresDBAdapter.OUT.log(Level.INFO, "isDBReadyToAcceptConnection returns ::: {0}", dbReadyToAcceptConnection);
        if (!dbReadyToAcceptConnection) {
            throw new IOException("Problem while starting database. Please check pgsql/data/pg_log/ for more details.");
        }
        PostgresDBAdapter.OUT.info("Database server sucessfully started.");
    }
    
    @Override
    public boolean createDB(final String connectionURL, final String userName, final String password) throws IOException {
        boolean createDB = false;
        final Map properties = this.splitConnectionURL(connectionURL);
        final String host = properties.get("Server");
        final Integer port = properties.get("Port");
        final String dbName = properties.get("DBName");
        createDB = this.pgInitializer.createDB(port, host, userName, password, dbName);
        PostgresDBAdapter.OUT.log(Level.INFO, "createDB returns ::: {0}", createDB);
        return createDB;
    }
    
    @Override
    public void shutDownDB(final String connectionURL, final String userName, final String password) throws IOException {
        final Map properties = this.splitConnectionURL(connectionURL);
        final Integer port = properties.get("Port");
        final String host = properties.get("Server");
        if (this.pgInitializer != null) {
            this.pgInitializer.stopDBServer(port, host, userName, password);
        }
    }
    
    @Override
    public boolean validateVersion(final Connection con) {
        try {
            final DatabaseMetaData dbm = con.getMetaData();
            PostgresDBAdapter.OUT.log(Level.INFO, "Postgres version ::: {0}", dbm.getDatabaseProductVersion());
            final Float dbVer = new Float(dbm.getDatabaseMajorVersion() + "." + dbm.getDatabaseMinorVersion());
            if (dbVer >= new Float("10")) {
                if (this.isBundledDB() && this.isSuperUser(con)) {
                    PostgresDBAdapter.OUT.log(Level.SEVERE, "The given user is a super user. Try connecting with a normal user.");
                    ConsoleOut.println("The given user is a super user. Try connecting with a normal user.");
                    return false;
                }
                return super.validateVersion(con);
            }
            else {
                if (dbVer >= new Float("9.4")) {
                    return super.validateVersion(con);
                }
                PostgresDBAdapter.OUT.log(Level.SEVERE, "Trying to connect to an incompatible version, PostgreSQL " + dbm.getDatabaseProductVersion() + " Only PostgreSQL versions 9.4 and above are supported");
                ConsoleOut.println("Trying to connect to an incompatible version, PostgreSQL " + dbm.getDatabaseProductVersion() + " Only PostgreSQL versions 9.4 and above are supported");
            }
        }
        catch (final SQLException | QueryConstructionException ex) {
            ex.printStackTrace();
        }
        return false;
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
                catch (final Exception e2) {
                    PostgresDBAdapter.OUT.fine("DO Nothing");
                }
            }
        }
    }
    
    private String buildString(final String... args) {
        final StringBuilder buffer = new StringBuilder();
        for (final String str : args) {
            buffer.append(str);
        }
        return buffer.toString();
    }
    
    @Override
    public void migrateScharDatatype(final List<ColumnDefinition> columnDefinitions) throws SQLException {
    }
    
    @Override
    public String getIdentifiersFileName() {
        return "pgsql_identifiers.txt";
    }
    
    @Override
    public void prepareDatabase(final Connection conn) throws DataBaseException {
        this.checkAndCreatePostgresExtensions(conn);
    }
    
    public void checkAndCreatePostgresExtensions(final Connection conn) throws DataBaseException {
        PostgresDBAdapter.OUT.info("Going to create postgres extensions if not exists ...");
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String createExtnSQL = null;
            String isExists = "select 'exists' from pg_extension where extname = 'pgcrypto'";
            if (!statement.executeQuery(isExists).next()) {
                createExtnSQL = "CREATE EXTENSION IF NOT EXISTS \"pgcrypto\" SCHEMA pg_catalog ";
                statement.execute(createExtnSQL);
            }
            isExists = "select 'exists' from pg_extension where extname = 'citext'";
            if (!statement.executeQuery(isExists).next()) {
                createExtnSQL = "CREATE EXTENSION IF NOT EXISTS \"citext\" SCHEMA pg_catalog ";
                statement.execute(createExtnSQL);
            }
            isExists = "select 'exists' from pg_extension where extname = 'pgadmin'";
            final DatabaseMetaData databaseMetaData = conn.getMetaData();
            if (databaseMetaData.getDatabaseMajorVersion() >= 10 && this.isBundledDB() && !statement.executeQuery(isExists).next()) {
                createExtnSQL = "CREATE EXTENSION IF NOT EXISTS \"pgadmin\" SCHEMA pg_catalog ";
                statement.execute(createExtnSQL);
            }
        }
        catch (final SQLException sqle) {
            PostgresDBAdapter.OUT.severe("Unable to create extensions in postgres.");
            sqle.printStackTrace();
            throw new DataBaseException(1013, sqle);
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final SQLException ex) {
                PostgresDBAdapter.OUT.log(Level.FINE, "ex :: {0}", ex);
            }
        }
    }
    
    @Override
    protected String getErrorCodeTableName() {
        return "PgSQLErrorCode";
    }
    
    @Override
    protected int getErrorCode(final SQLException sqle) {
        final Map<Object, ErrorCodes.AdventNetErrorCode> errorCodeMap = this.getErrorCodeMap();
        if (errorCodeMap == null) {
            return -9999;
        }
        ErrorCodes.AdventNetErrorCode ec = errorCodeMap.get(sqle.getSQLState());
        if (ec != null && "ER_NO_REFERENCED_ROW".equals(ec.getErrorString())) {
            final String message = sqle.getMessage();
            if (message != null && message.indexOf("update or delete") >= 0) {
                ec = ErrorCodes.getAdventNetErrorCode("ER_ROW_IS_REFERENCED");
            }
        }
        return (ec == null) ? -9999 : ec.getErrorCode();
    }
    
    private int getPortFromPostgresConf(final String postgresConfFilePath) throws IOException {
        final File postgresConf = new File(postgresConfFilePath);
        int portInConfFile = 5432;
        FileInputStream fis = null;
        try {
            if (!postgresConf.exists()) {
                throw new IOException(postgresConfFilePath + " doesn't exist. Please check whether the Postgres data directory is initialized");
            }
            fis = new FileInputStream(postgresConf);
            final Properties props = new Properties();
            props.load(fis);
            String portStr = props.getProperty("port");
            if (portStr == null) {
                PostgresDBAdapter.OUT.log(Level.INFO, "port not specified in {0} returning default port 5432", postgresConfFilePath);
            }
            else {
                final int hashIndex = portStr.indexOf("#");
                if (hashIndex != -1) {
                    portStr = portStr.substring(0, hashIndex);
                }
                try {
                    portInConfFile = Integer.parseInt(portStr.trim());
                }
                catch (final NumberFormatException nfe) {
                    PostgresDBAdapter.OUT.log(Level.SEVERE, "Invalid port specified in {0}", postgresConfFilePath);
                    PostgresDBAdapter.OUT.info(nfe.toString());
                }
            }
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        return portInConfFile;
    }
    
    @Override
    public boolean isColumnModified(final ColumnDefinition oldColumnDefinition, final ColumnDefinition newColumnDefinition, final List<String> changedAttributes) {
        return (changedAttributes.contains("data-type") && (!oldColumnDefinition.getDataType().equals("CHAR") || !newColumnDefinition.getDataType().equals("NCHAR")) && (!oldColumnDefinition.getDataType().equals("NCHAR") || !newColumnDefinition.getDataType().equals("CHAR"))) || (changedAttributes.contains("max-size") && (newColumnDefinition.getDataType().equals("CHAR") || newColumnDefinition.getDataType().equals("NCHAR"))) || super.isColumnModified(oldColumnDefinition, newColumnDefinition, changedAttributes);
    }
    
    @Override
    public boolean isIndexModified(final IndexDefinition oldIndexDefinition, final IndexDefinition newIndexDefinition, final List<String> changedAttributes) {
        if (changedAttributes == null || changedAttributes.isEmpty()) {
            return false;
        }
        boolean isChanged = changedAttributes.stream().anyMatch(changedAttribute -> changedAttribute.startsWith("index-column"));
        if (isChanged) {
            return true;
        }
        isChanged = changedAttributes.stream().anyMatch(changedAttribute -> changedAttribute.startsWith("isAscending of"));
        if (isChanged) {
            return true;
        }
        isChanged = changedAttributes.stream().anyMatch(changedAttribute -> changedAttribute.startsWith("isNullsFirst of"));
        if (isChanged) {
            final List<IndexColumnDefinition> oldIcdList = oldIndexDefinition.getColumnDefnitions();
            final List<IndexColumnDefinition> newIcdList = newIndexDefinition.getColumnDefnitions();
            for (int i = 0; i < oldIcdList.size(); ++i) {
                final Boolean isOrderAscInOldIndex = oldIcdList.get(i).isAscending();
                final Boolean isOrderAscInNewIndex = newIcdList.get(i).isAscending();
                Boolean isNullsFirstInOldIndex = oldIcdList.get(i).isNullsFirst();
                Boolean isNullsFirstInNewIndex = newIcdList.get(i).isNullsFirst();
                if (isOrderAscInOldIndex && isNullsFirstInOldIndex == null) {
                    isNullsFirstInOldIndex = false;
                }
                else if (!isOrderAscInOldIndex && isNullsFirstInOldIndex == null) {
                    isNullsFirstInOldIndex = true;
                }
                if (isOrderAscInNewIndex && isNullsFirstInNewIndex == null) {
                    isNullsFirstInNewIndex = false;
                }
                else if (!isOrderAscInNewIndex && isNullsFirstInNewIndex == null) {
                    isNullsFirstInNewIndex = true;
                }
                if (isNullsFirstInOldIndex != isNullsFirstInNewIndex) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public long getApproxRowCount(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        return super.getApproxRowCount((PostgresDBAdapter.isAutoQuoteEnabled || (!PostgresDBAdapter.isAutoQuoteEnabled && this.reservedKeyWords.contains(tableName))) ? tableName : tableName.toLowerCase(Locale.ENGLISH), metaData);
    }
    
    @Override
    public String getPKNameOfTheTable(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        return super.getPKNameOfTheTable((PostgresDBAdapter.isAutoQuoteEnabled || (!PostgresDBAdapter.isAutoQuoteEnabled && this.reservedKeyWords.contains(tableName))) ? tableName : tableName.toLowerCase(Locale.ENGLISH), metaData);
    }
    
    public Map<String, String> getCitextColumnVsMaxSize(final String tableName, final Connection connection) throws SQLException {
        PreparedStatement prepareStatement = null;
        ResultSet rs = null;
        try {
            prepareStatement = connection.prepareStatement(((PostgresSQLGenerator)this.sqlGen).getSQLForGetCitextColumnLength());
            prepareStatement.setString(1, tableName);
            final Map<String, String> columnNameVsMaxSize = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
            rs = prepareStatement.executeQuery();
            while (rs.next()) {
                columnNameVsMaxSize.put(rs.getString("columnname"), rs.getString("maxsize"));
            }
            return Collections.unmodifiableMap((Map<? extends String, ? extends String>)columnNameVsMaxSize);
        }
        finally {
            if (rs != null) {
                rs.close();
            }
            if (prepareStatement != null) {
                prepareStatement.close();
            }
        }
    }
    
    public Map<String, String> getCitextColumnVsConstraintName(final String tableName, final Connection connection) throws SQLException {
        PreparedStatement prepareStatement = null;
        ResultSet rs = null;
        try {
            prepareStatement = connection.prepareStatement(((PostgresSQLGenerator)this.sqlGen).getSQLForGetCitextColumnLength());
            prepareStatement.setString(1, tableName);
            final Map<String, String> columnNameVsconstraintName = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
            rs = prepareStatement.executeQuery();
            while (rs.next()) {
                columnNameVsconstraintName.put(rs.getString("columnname"), rs.getString("constraintname"));
            }
            return Collections.unmodifiableMap((Map<? extends String, ? extends String>)columnNameVsconstraintName);
        }
        finally {
            if (rs != null) {
                rs.close();
            }
            if (prepareStatement != null) {
                prepareStatement.close();
            }
        }
    }
    
    @Override
    public void createDB(final Connection connection, final String dbName, final String userName, final String password, final boolean ignoreIfExists) throws SQLException {
        ResultSet catalogs = null;
        Statement stmt = null;
        try {
            boolean isDBExists = false;
            stmt = connection.createStatement();
            catalogs = stmt.executeQuery("select datname from pg_database");
            while (catalogs.next()) {
                if (dbName.equals(catalogs.getString("datname"))) {
                    isDBExists = true;
                    break;
                }
            }
            if (!isDBExists) {
                this.createDB(connection, dbName, userName, password);
                PostgresDBAdapter.OUT.info("DataBase " + dbName + " successfully created...");
            }
            else {
                PostgresDBAdapter.OUT.severe("DataBase " + dbName + " already exists...");
            }
        }
        catch (final SQLException sqle) {
            if (ignoreIfExists && "42P04".equals(sqle.getSQLState())) {
                PostgresDBAdapter.OUT.warning("DataBase " + dbName + " already exists...");
                return;
            }
            throw sqle;
        }
        finally {
            if (catalogs != null) {
                catalogs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
    }
    
    @Override
    public boolean isTableNotFoundException(final SQLException sqle) {
        return (sqle.getErrorCode() == 0 || sqle.getErrorCode() == -9999) && "42P01".equals(sqle.getSQLState());
    }
    
    @Override
    public BulkInsertObject createBulkInsertObject(final BulkLoad bulk) throws IOException, SQLException, QueryConstructionException, MetaDataException {
        final DataBufferStream dbs = new DataBufferStream(bulk.getBuffersize());
        final BulkInsertObject bio = new BulkInsertObject(dbs);
        if (MetaDataUtil.getTableDefinitionByName(bulk.getTableName()) != null) {
            this.loadColumnDetails(bulk, bio, null);
            this.checkForEncryptedColumns(bulk, bio);
        }
        else {
            this.loadColumnDetailsFromDB(bulk, bio, bulk.getConnection().getMetaData());
        }
        bio.setSQL(BulkLoadStatementGenerator.getBulkSQL(bulk, bio, this.getSQLGenerator()));
        return bio;
    }
    
    private void checkForEncryptedColumns(final BulkLoad bulk, final BulkInsertObject bio) {
        for (String localColTypeName : bio.getColTypeNames()) {
            final String colTypeName = localColTypeName;
            if (DataTypeUtil.isEDT(localColTypeName)) {
                localColTypeName = DataTypeManager.getDataTypeDefinition(localColTypeName).getBaseType();
            }
            if (localColTypeName.equalsIgnoreCase("SCHAR") || localColTypeName.equalsIgnoreCase("SBLOB")) {
                bulk.containsEncryptedColumns = true;
                break;
            }
        }
    }
    
    @Override
    public void loadColumnDetails(final BulkLoad bulk, final BulkInsertObject bio, final String schema) throws SQLException, MetaDataException {
        super.loadColumnDetails(bulk, bio, schema);
    }
    
    @Override
    public void loadColumnDetailsFromDB(final BulkLoad bulk, final BulkInsertObject bio, final DatabaseMetaData metaData) throws SQLException {
        ResultSet rs = null;
        int index = 1;
        int[] recomputedIndices = null;
        try {
            rs = metaData.getColumns(null, null, bulk.getTableName().toLowerCase(Locale.ENGLISH), null);
            final List<Integer> columnTypesDB = new ArrayList<Integer>();
            final List<String> columnNamesDB = new ArrayList<String>();
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
    
    public String escapeString(String str) {
        try {
            str = StringUtils.replaceEach(str, new String[] { "\t", "\n", "\r", "\\", "\u0000" }, new String[] { "\\t", "\\n", "\\r", "\\\\", "" });
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return str;
    }
    
    @Override
    public void addBatch(final Object[] rowLvlByteValues, final BulkLoad bulk) throws IOException, SQLException {
        String colDataType = "";
        boolean dataExist = Boolean.FALSE;
        boolean prevAppend = Boolean.FALSE;
        final DataBufferStream dataBufferStream = (DataBufferStream)bulk.getBulkInsertObject().getBulkObject();
        for (int index = 0; index < rowLvlByteValues.length; ++index) {
            final Object value = rowLvlByteValues[index];
            colDataType = bulk.getBulkInsertObject().getColTypeNames().get(index);
            if (DataTypeUtil.isEDT(colDataType)) {
                colDataType = DataTypeManager.getDataTypeDefinition(colDataType).getBaseType();
            }
            if (DataTypeUtil.isUDT(colDataType) && !DataTypeManager.getDataTypeDefinition(colDataType).getMeta().processInput()) {
                if (dataExist) {
                    prevAppend = Boolean.TRUE;
                }
                dataExist = Boolean.FALSE;
            }
            else {
                if ((index != 0 && dataExist) || prevAppend) {
                    dataBufferStream.getOutputStream().write(9);
                }
                if (null == value) {
                    dataBufferStream.getOutputStream().write(PostgresDBAdapter.NULL);
                }
                else if (value instanceof String && (colDataType.equalsIgnoreCase("SCHAR") || colDataType.equalsIgnoreCase("NCHAR") || colDataType.equalsIgnoreCase("CHAR") || colDataType.equalsIgnoreCase("VARCHAR") || colDataType.equalsIgnoreCase("TEXT") || colDataType.equalsIgnoreCase("NTEXT") || colDataType.equalsIgnoreCase("LONGTEXT") || colDataType.equalsIgnoreCase("CITEXT"))) {
                    if (colDataType.equalsIgnoreCase("SCHAR")) {
                        this.getBytesForObjectAndEscape(((String)value).getBytes(), dataBufferStream);
                    }
                    else {
                        dataBufferStream.getOutputStream().write(this.escapeString((String)value).getBytes());
                    }
                }
                else if (colDataType.equals("DCJSON")) {
                    dataBufferStream.getOutputStream().write(this.escapeString(value.toString()).getBytes());
                }
                else if (bulk.getBulkInsertObject().getColTypes().get(index) == 2004) {
                    this.getBytesForObjectAndEscape(value, dataBufferStream);
                }
                else if (DataTypeUtil.isUDT(colDataType)) {
                    dataBufferStream.getOutputStream().write(DataTypeManager.getDataTypeDefinition(colDataType).getDTAdapter(this.getDBType()).getBytes(value));
                }
                else {
                    dataBufferStream.getOutputStream().write(super.getBytesForObject(value));
                }
                dataExist = Boolean.TRUE;
            }
        }
        dataBufferStream.getOutputStream().write(13);
    }
    
    @Override
    public void execBulk(final BulkLoad bulk) throws SQLException, IOException, MetaDataException {
        String updateSql = "";
        Statement state = null;
        try {
            if (!bulk.getBulkInsertObject().exceptionCaused.get() && bulk.createTempTable) {
                final String createSQL = this.sqlGen.createTempTableSQL(bulk.isArchivedTable() ? bulk.getArchivedTableName() : bulk.getTableName());
                state = bulk.getConnection().createStatement();
                this.dropTable(state, (bulk.isArchivedTable() ? bulk.getArchivedTableName() : bulk.getTableName()) + "_temp", true, false, null);
                this.createTable(state, createSQL, null);
            }
        }
        catch (final Throwable e) {
            PostgresDBAdapter.OUT.log(Level.FINE, "Exception occured ??? [{0}]", bulk.getBulkInsertObject().exceptionCaused.get());
            if (!bulk.getBulkInsertObject().exceptionCaused.get()) {
                e.printStackTrace();
            }
            return;
        }
        finally {
            try {
                if (null != state) {
                    state.close();
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
                bulk.getBulkInsertObject().setError(e2);
            }
        }
        try {
            bulk.getBulkInsertObject().setIsReadyToWrite(Boolean.TRUE);
            PostgresDBAdapter.OUT.fine("Exec Bulk has been started!!!");
            final CopyManager copy = new CopyManager((BaseConnection)((LogicalConnection)bulk.getConnection()).getPhysicalConnection());
            final long copiedRows = copy.copyIn(bulk.getBulkInsertObject().getSQL(), ((DataBufferStream)bulk.getBulkInsertObject().getBulkObject()).getInputStream());
            if (bulk.getBulkInsertObject().exceptionCaused.get()) {
                PostgresDBAdapter.OUT.info("An exception occured!!! Hence terminating stream for table " + bulk.getTableName() + "!!!");
                return;
            }
            PostgresDBAdapter.OUT.log(Level.INFO, "Bulk Load Operation Completed for the table:: " + bulk.getTableName() + " and the row count is :: " + copiedRows);
        }
        catch (final Throwable e) {
            e.printStackTrace();
            final Exception exp = new Exception(e);
            bulk.getBulkInsertObject().setError(exp);
            PostgresDBAdapter.OUT.fine("Constructed Exception >>>");
            return;
        }
        try {
            if (!bulk.getBulkInsertObject().exceptionCaused.get()) {
                if (bulk.containsEncryptedColumns) {
                    updateSql = BulkLoadStatementGenerator.getBulkUpdateSQL(bulk, bulk.getBulkInsertObject(), this.getSQLGenerator());
                    PostgresDBAdapter.OUT.info("Going to execute bulk update sql for table: " + bulk.getTableName() + " is " + updateSql);
                    this.executeBulkUpdate(bulk.getConnection(), updateSql);
                }
                if (bulk.createTempTable) {
                    this.executeInsertFromTempTable(bulk.getConnection(), bulk.isArchivedTable() ? bulk.getArchivedTableName() : bulk.getTableName());
                }
            }
        }
        catch (final Throwable e) {
            e.printStackTrace();
            final Exception exp = new Exception(e.getMessage());
            bulk.getBulkInsertObject().setError(exp);
        }
    }
    
    private int executeBulkUpdate(final Connection conn, final String sql) throws SQLException {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            statement.executeUpdate(sql);
        }
        catch (final SQLException e) {
            PostgresDBAdapter.OUT.log(Level.INFO, "Exception while executing BULK UPDATE:: [{0}]", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        finally {
            try {
                if (null != statement) {
                    statement.close();
                }
            }
            catch (final SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        return 1;
    }
    
    private boolean executeInsertFromTempTable(final Connection conn, final String tableName) throws SQLException {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            final String executeInsertFromTemp = this.sqlGen.insertSQLForTemp(tableName);
            this.execute(statement, executeInsertFromTemp);
        }
        finally {
            if (null != statement) {
                statement.close();
            }
        }
        return true;
    }
    
    private void getBytesForObjectAndEscape(final Object value, final DataBufferStream dbs) throws SQLException, IOException {
        dbs.getOutputStream().write(PostgresDBAdapter.APPEND_HEX);
        if (value instanceof InputStream) {
            streamBytea((InputStream)value, dbs.getOutputStream());
        }
        if (value instanceof byte[]) {
            streamBytea((byte[])value, dbs.getOutputStream());
        }
    }
    
    private static void streamBytea(final InputStream stream, final OutputStream encodingWriter) throws IOException {
        byte[] bytes = new byte[3072];
        int read = -1;
        while (true) {
            if (stream.available() > bytes.length) {
                read = stream.read(bytes, 0, bytes.length);
            }
            else {
                bytes = new byte[stream.available()];
                read = stream.read(bytes, 0, stream.available());
            }
            if (read == -1) {
                break;
            }
            streamBytea(bytes, encodingWriter);
        }
    }
    
    private static void streamBytea(final byte[] bytes, final OutputStream encodingWriter) throws IOException {
        encodingWriter.write(getEncodedBytes(bytes));
    }
    
    private static byte[] getEncodedBytes(final byte[] bytes) {
        int v = 0;
        final char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; ++j) {
            v = (bytes[j] & 0xFF);
            hexChars[j * 2] = PostgresDBAdapter.HEXARRAY[v >>> 4];
            hexChars[j * 2 + 1] = PostgresDBAdapter.HEXARRAY[v & 0xF];
        }
        byte[] ret;
        try {
            ret = new String(hexChars).getBytes("ISO8859-1");
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        return ret;
    }
    
    @Override
    public boolean isBundledDB() {
        if (this.isBundledDB == null) {
            boolean isBundled = false;
            if (!this.isLoopbackAddress()) {
                PostgresDBAdapter.OUT.log(Level.INFO, "isBundled :: {0}", isBundled);
            }
            else if (!new File(Configuration.getString("db.home") + File.separator + "bin").exists()) {
                PostgresDBAdapter.OUT.log(Level.INFO, "psql not found at :: {0}", new File(Configuration.getString("db.home") + File.separator + "bin"));
                PostgresDBAdapter.OUT.log(Level.INFO, "isBundled :: {0}", isBundled);
            }
            else {
                try {
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
                    final Process p = this.pgInitializer.executeCommand(port, host, username, password, "postgres", "SHOW data_directory");
                    p.waitFor();
                    String line = null;
                    final String dataDirectory = new File(Configuration.getString("db.home") + File.separator + "data").getCanonicalPath();
                    try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                        while ((line = bufferedReader.readLine()) != null && line.indexOf("\\") == -1 && line.indexOf("/") == -1) {}
                        if (line != null) {
                            PostgresDBAdapter.OUT.log(Level.INFO, "DB Home from DB:: {0}", new File(line.trim()).getCanonicalPath());
                        }
                        PostgresDBAdapter.OUT.log(Level.INFO, "DB Home from Props :: {0}", dataDirectory);
                    }
                    isBundled = (line != null && dataDirectory.equals(new File(line.trim()).getCanonicalPath()));
                    try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                        while ((line = bufferedReader.readLine()) != null) {
                            PostgresDBAdapter.OUT.log(Level.WARNING, "ErrorStream :: {0}", line);
                        }
                    }
                    PostgresDBAdapter.OUT.log(Level.INFO, "isBundled :: {0}", isBundled);
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            this.isBundledDB = isBundled;
        }
        return this.isBundledDB;
    }
    
    @Override
    protected String getBackupHandlerClassName() {
        return PostgresBackupHandler.class.getName();
    }
    
    @Override
    protected String getRestoreHandlerClassName() {
        return PostgresRestoreHandler.class.getName();
    }
    
    @Override
    public ResultSet getFKMetaData(final Connection connection, final String tableName) throws SQLException {
        return connection.getMetaData().getImportedKeys(connection.getCatalog(), this.getCurrentSchema(connection), (tableName != null) ? (this.reservedKeyWords.contains(tableName) ? tableName : tableName.toLowerCase(Locale.ENGLISH)) : tableName);
    }
    
    @Override
    public SQLException handleSQLException(final SQLException sqle, final Connection connection, final boolean isWrite) throws SQLException {
        for (Throwable cause = sqle; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException && "08004".equalsIgnoreCase(((SQLException)cause).getSQLState())) {
                final SQLException sqlcause = (SQLException)cause;
                ConsoleOut.println("Postgres DB Server is not running in the specified port");
                ConsoleOut.println("Check that the hostname and port are correct and Postgres server is accepting connections");
            }
        }
        return super.handleSQLException(this.maskMessage(sqle), connection, isWrite);
    }
    
    private SQLException maskMessage(final SQLException sqle) {
        if (sqle == null || AppResources.getString("development.mode", "false").equalsIgnoreCase("true")) {
            return sqle;
        }
        if (sqle instanceof BatchUpdateException) {
            final String maskedString = ((PostgresSQLGenerator)this.sqlGen).maskSensitiveMessages(sqle.getMessage());
            final BatchUpdateException maskedSqlException = new BatchUpdateException(maskedString, sqle.getSQLState(), sqle.getErrorCode(), ((BatchUpdateException)sqle).getUpdateCounts());
            maskedSqlException.initCause(sqle.getCause());
            maskedSqlException.setNextException(this.maskMessage(sqle.getNextException()));
            return maskedSqlException;
        }
        if (sqle.getNextException() instanceof SQLException) {
            final String maskedString = ((PostgresSQLGenerator)this.sqlGen).maskSensitiveMessages(sqle.getMessage());
            final SQLException maskedSqlException2 = new SQLException(maskedString, sqle.getSQLState(), sqle.getErrorCode(), sqle.getCause());
            maskedSqlException2.setNextException(this.maskMessage(sqle.getNextException()));
            return maskedSqlException2;
        }
        return sqle;
    }
    
    @Override
    public List<String> getColumnNamesFromDB(final String tableName, final String columnPattern, final DatabaseMetaData metaData) throws SQLException {
        return super.getColumnNamesFromDB(tableName.toLowerCase(Locale.ENGLISH), columnPattern, metaData);
    }
    
    @Override
    public String getDBSpecificExceptionSorterName() {
        return "com.adventnet.db.adapter.postgres.PostgresExceptionSorter";
    }
    
    protected List<String> getListFromIdentifierFile() {
        final String dbKeyWords = "all, analyse, analyze, and, any, array, as, asc, asymmetric, both, case, cast, check, collate, column, constraint, create, current_catalog, current_date, current_role, current_time, current_timestamp, current_user, default, deferrable, desc, distinct, do, else, end, except, false, fetch, for, foreign, from, grant, group, having, in, initially, intersect, into, lateral, leading, limit, localtime, localtimestamp, not, null, offset, on, only, or, order, placing, primary, references, returning, select, session_user, some, symmetric, table, then, to, trailing, true, union, unique, user, using, variadic, when, where, window, with";
        final List<String> dbreservedKeyWords = Arrays.asList(dbKeyWords.split(","));
        final List<String> keyWordList = new ArrayList<String>();
        BufferedReader br = null;
        String fileName = null;
        try {
            for (int i = 0; i < dbreservedKeyWords.size(); ++i) {
                keyWordList.add(dbreservedKeyWords.get(i).trim());
            }
            fileName = this.getIdentifiersFileName();
            final File identifierFile = new File(PostgresDBAdapter.server_home + File.separator + "conf" + File.separator + fileName);
            if (identifierFile.exists()) {
                br = new BufferedReader(new FileReader(identifierFile));
                String keyWord;
                while ((keyWord = br.readLine()) != null) {
                    if (!keyWord.startsWith("#")) {
                        keyWordList.add(keyWord.trim());
                    }
                }
            }
            else {
                PostgresDBAdapter.OUT.log(Level.WARNING, "Identifier [{0}] file not exist for this database", fileName);
            }
        }
        catch (final IOException ex) {
            PostgresDBAdapter.OUT.log(Level.WARNING, "{0} file not exist", fileName);
        }
        catch (final UnsupportedOperationException usoe) {
            PostgresDBAdapter.OUT.warning("AUTO_QUOTE_IDENTIFIERS not supported for this DB...");
        }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
            }
            catch (final IOException ex2) {
                PostgresDBAdapter.OUT.log(Level.FINE, "Exception while closing input stream", ex2);
            }
        }
        return keyWordList;
    }
    
    public void createVersionFile(final int port, final String host, final String userName, final String passwd, final String dbName) throws IOException {
        final File versionFile = new File(Configuration.getString("db.home") + File.separator + "bin" + File.separator + "PG_ARCH");
        if (versionFile.exists()) {
            PostgresDBAdapter.OUT.log(Level.INFO, "Postgres Architecture file already exists");
            return;
        }
        final String versionQuery = "SELECT version()";
        BufferedReader ipBuf = null;
        try {
            final List<String> otherArgs = new ArrayList<String>();
            otherArgs.add("-t");
            final Process p = this.pgInitializer.executeCommand(port, host, userName, passwd, dbName, versionQuery, otherArgs);
            ipBuf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String version = ipBuf.readLine();
            PostgresDBAdapter.OUT.log(Level.INFO, "version :: {0}", version);
            if (version == null) {
                final BufferedReader erBuf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                PostgresDBAdapter.OUT.log(Level.SEVERE, "Error while executing version command :: {0}", erBuf.readLine());
                throw new IOException("Error while executing version command");
            }
            version = version.substring(version.lastIndexOf(",") + 1).replace("-bit", "").trim();
            if (!versionFile.exists()) {
                versionFile.createNewFile();
            }
            final FileWriter fw = new FileWriter(versionFile);
            final BufferedWriter bw = new BufferedWriter(fw);
            bw.write(version + "\n");
            bw.close();
            PostgresDBAdapter.OUT.log(Level.INFO, "Postgres Architecture file created successfully at :: {0}", versionFile.getCanonicalPath());
        }
        finally {
            if (ipBuf != null) {
                try {
                    ipBuf.close();
                }
                catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public boolean hasPermissionForBackup(final Connection conn) throws Exception {
        return this.hasPermissionForBackupAndRestore(conn);
    }
    
    @Override
    public boolean hasPermissionForRestore(final Connection conn) throws Exception {
        return this.hasPermissionForBackupAndRestore(conn);
    }
    
    private boolean hasPermissionForBackupAndRestore(final Connection conn) throws SQLException, QueryConstructionException {
        boolean result = true;
        if (!this.isSuperUser(conn)) {
            if (!this.isDBRoleInherited(conn)) {
                PostgresDBAdapter.OUT.log(Level.SEVERE, "User dosent have superuser permission & also dosent inherit parents properties, so backup cannot be initiated");
                result = false;
            }
            else {
                PostgresDBAdapter.OUT.log(Level.INFO, "Database has superuser permission to perform backup");
                result = true;
            }
        }
        else {
            PostgresDBAdapter.OUT.log(Level.WARNING, "User dosent have superuser permission, but assuming its parent has permission and performing backup");
            result = true;
        }
        return result;
    }
    
    private boolean isDBRoleInherited(final Connection conn) throws SQLException, QueryConstructionException {
        return this.isRole(conn, "rolinherit", null);
    }
    
    protected boolean isSuperUser(final Connection conn) throws SQLException, QueryConstructionException {
        return this.isRole(conn, "rolsuper", null);
    }
    
    public boolean isRole(final Connection conn, final String roleType, String roleName) throws SQLException {
        String query = "SELECT " + roleType + " FROM pg_roles WHERE rolname = ";
        if (roleName == null || roleName.isEmpty()) {
            query += "current_user";
            roleName = null;
        }
        else {
            query += "?";
        }
        try (final PreparedStatement ps = conn.prepareStatement(query)) {
            if (roleName != null) {
                ps.setString(1, roleName);
            }
            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        }
        return false;
    }
    
    @Override
    public long getSizeOfDB(final Connection conn) {
        long size = -1L;
        final DataSet ds = null;
        final String query = "SELECT pg_database_size('" + this.getDBProps().getProperty("DBName") + "')";
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, new Object[0]);
             final ResultSetAdapter rs = this.executeQuery(ps)) {
            if (rs.next()) {
                size = rs.getLong(1);
            }
            else {
                PostgresDBAdapter.OUT.log(Level.WARNING, "The size of the database cannot be obtained");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return size;
    }
    
    @Override
    public String getDBSystemProperty(final Connection conn, final String property) throws SQLException, QueryConstructionException {
        String value = null;
        if (this.isSuperUser(conn) || this.hasPrivilegeForConfig(conn)) {
            final String query = "SHOW " + property;
            PostgresDBAdapter.OUT.log(Level.FINE, query);
            try (final PreparedStatement ps = this.createPreparedStatement(conn, query, new Object[0]);
                 final ResultSetAdapter rs = this.executeQuery(ps)) {
                if (rs.next()) {
                    value = rs.getString(1);
                }
                else {
                    PostgresDBAdapter.OUT.log(Level.WARNING, "Could not fetch the result for the property : " + property + ".");
                }
            }
        }
        else {
            PostgresDBAdapter.OUT.log(Level.SEVERE, "Could not obtain data directory information because of the lack of super user permission");
        }
        return value;
    }
    
    protected boolean hasPrivilegeForConfig(final Connection conn) throws SQLException, QueryConstructionException {
        final DatabaseMetaData databaseMetaData = conn.getMetaData();
        if (databaseMetaData.getDatabaseMajorVersion() >= 10) {
            final String sql = "select rolname from pg_roles join pg_auth_members on pg_roles.oid = pg_auth_members.roleid where pg_auth_members.member = (select oid from pg_roles c where c.rolname = current_user)";
            try (final PreparedStatement ps = this.createPreparedStatement(conn, sql, new Object[0]);
                 final ResultSetAdapter rs = this.executeQuery(ps)) {
                while (rs.next()) {
                    final String role = rs.getString(1);
                    if (role.equalsIgnoreCase("pg_read_all_settings") || role.equalsIgnoreCase("pg_monitor")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public String getDBType() {
        return "postgres";
    }
    
    @Override
    public List<String> getPKColumnNameOfTheTable(final String tableName, final DatabaseMetaData metaData) throws SQLException {
        return super.getPKColumnNameOfTheTable(tableName.toLowerCase(Locale.ENGLISH), metaData);
    }
    
    public String getDBSpecificSQLModifierName() throws Exception {
        return PostgresSQLModifier.class.getName();
    }
    
    @Override
    public String getDBSpecificAbortHandlerName() {
        return PostgresAbortHandler.class.getName();
    }
    
    @Override
    public boolean changePassword(final String userName, final String oldPassword, final String newPassword, final Connection c) throws SQLException {
        if (!this.isBundledDB()) {
            throw new SQLException("ChangePassword can be invoked for the database which has been bundled with the product!!");
        }
        if (oldPassword != null && oldPassword.equals(newPassword)) {
            PostgresDBAdapter.OUT.log(Level.WARNING, "old and new password is same for the user {0}", userName);
            return false;
        }
        PostgresDBAdapter.OUT.log(Level.WARNING, "Changing default password for user {0}", userName);
        String version;
        try {
            version = this.getDBInitializer().getVersion();
        }
        catch (final Exception e1) {
            throw new SQLException("Exception while getting postgres database version: " + e1);
        }
        PostgresDBAdapter.OUT.info("Going to change database password for the user: " + userName);
        PostgresDBAdapter.OUT.fine("postgres version is " + version);
        version = version.substring(0, version.lastIndexOf("."));
        if (Float.valueOf(version) < 10.0f) {
            final String query = "ALTER USER " + userName + " WITH ENCRYPTED PASSWORD '" + newPassword + "'";
            try (final Statement stmt = c.createStatement()) {
                PostgresDBAdapter.OUT.log(Level.FINE, "Going to execute statement :: [{0}]", new Object[] { query.replace(newPassword, "*********") });
                stmt.executeUpdate(query);
                PostgresDBAdapter.OUT.log(Level.FINE, "Executed statement :: [{0}]", new Object[] { query.replace(newPassword, "*********") });
            }
            catch (final SQLException e2) {
                PostgresDBAdapter.OUT.severe("Problem while setting new password.");
                throw e2;
            }
        }
        else {
            final String sql = "SELECT change_password('" + newPassword + "'::TEXT)";
            PostgresDBAdapter.OUT.log(Level.FINE, "Going to execute statement :: [{0}]", new Object[] { sql.replace(newPassword, "*********") });
            try (final Statement statement = c.createStatement();
                 final ResultSet resultSet = statement.executeQuery(sql)) {
                resultSet.next();
                if (!resultSet.getBoolean(1)) {
                    PostgresDBAdapter.OUT.severe("Password is not changed in database.");
                    throw new SQLException("Password is not changed in database.");
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean isReadOnly(final Connection connection) throws Exception {
        if (connection == null) {
            throw new SQLException("provided connection object is invalid");
        }
        final String sql = "SELECT pg_is_in_recovery()";
        try (final PreparedStatement ps = this.createPreparedStatement(connection, sql, new Object[0]);
             final ResultSetAdapter rs = this.executeQuery(ps)) {
            rs.next();
            return rs.getBoolean(1);
        }
        catch (final Exception e) {
            PostgresDBAdapter.OUT.log(Level.SEVERE, "Exception occurred while obtaining mode of the database");
            throw e;
        }
    }
    
    @Override
    public List<String> getTableNamesLike(final Connection con, final String schemaName, final String tableNameLike) throws SQLException {
        List<String> tableNames = super.getTableNamesLike(con, schemaName, tableNameLike);
        if (tableNames == null || tableNames.isEmpty()) {
            tableNames = super.getTableNamesLike(con, schemaName, tableNameLike.toLowerCase(Locale.ENGLISH));
        }
        return tableNames;
    }
    
    @Override
    public List<String> getColumnNames(final Connection con, final String schemaName, final String tableName) throws SQLException {
        List<String> columnNames = super.getColumnNames(con, schemaName, tableName);
        if (columnNames == null || columnNames.isEmpty()) {
            columnNames = super.getColumnNames(con, schemaName, tableName.toLowerCase(Locale.ENGLISH));
        }
        return columnNames;
    }
    
    static {
        OUT = Logger.getLogger(PostgresDBAdapter.class.getName());
        SEPARATOR = File.separator;
        PostgresDBAdapter.server_home = Configuration.getString("server.home", "app.home");
        NULL = "\\N".getBytes();
        APPEND_HEX = "\\\\x".getBytes();
        HEXARRAY = "0123456789ABCDEF".toCharArray();
    }
}
