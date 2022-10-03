package com.adventnet.db.adapter.mssql;

import java.util.Hashtable;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.adapter.SQLGenerator;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import com.zoho.cp.LogicalConnection;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import java.sql.Timestamp;
import java.sql.Date;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;
import com.zoho.mickey.db.mssql.MssqlSQLModifier;
import com.adventnet.db.adapter.DBInitializer;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.ds.query.BulkLoadStatementGenerator;
import com.adventnet.db.adapter.BulkInsertObject;
import com.adventnet.ds.query.BulkLoad;
import com.adventnet.db.adapter.RestoreDBParams;
import com.adventnet.db.adapter.DCAdapter;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Collections;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.StringTokenizer;
import com.zoho.mickey.exception.DataBaseException;
import java.sql.BatchUpdateException;
import java.util.Locale;
import java.sql.Time;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import java.util.Map;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.adapter.ResultSetAdapter;
import java.util.HashMap;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.Iterator;
import java.util.List;
import java.sql.Statement;
import com.adventnet.mfw.ConsoleOut;
import java.util.Collection;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.util.Properties;
import java.util.logging.Logger;
import com.adventnet.db.adapter.Jdbc20DBAdapter;

public class MssqlDBAdapter extends Jdbc20DBAdapter
{
    static final transient Logger out;
    private static final String DEFAULT_MASTERKEY_PASSWORD = "Password123";
    private static final String DEFAULT_CERTIFICATE_NAME = "ZOHO_CERT";
    private static final String DEFAULT_CERTIFICATE_SUB = "Created by ZOHO";
    private static final String DEFAULT_SYMM_KEY_NAME = "ZOHO_SYMM_KEY";
    private static final String DEFAULT_SYMM_KEY_ALGO = "AES_256";
    private static final String DEFAULT_SYMM_KEY_ALTALGO = "TRIPLE_DES";
    private static final String DEFAULT_IDENTITY_VALUE = "ZOHO_Identity_Value";
    private static final String DEFAULT_KEY_SOURCE = "ZOHO_Key_Source";
    private MssqlSQLGenerator mssqlGen;
    private MssqlDBInitializer dbInitializer;
    private boolean supportFilteredUniqueIndex;
    private Mssql2005DBAdapter mssql2005Adapter;
    private static boolean supportsEncryption;
    private static boolean isTextDataTypeDeprecated;
    
    public MssqlDBAdapter() {
        this.mssqlGen = null;
        this.dbInitializer = null;
        this.mssql2005Adapter = new Mssql2005DBAdapter();
        this.dbInitializer = new MssqlDBInitializer();
    }
    
    public Mssql2005DBAdapter getSql2005DBAdapter() {
        return this.mssql2005Adapter;
    }
    
    @Override
    public void initialize(final Properties props) {
        super.initialize(props);
        this.mssqlGen = (MssqlSQLGenerator)this.sqlGen;
        String masterkeyPass = props.getProperty("masterkey.password");
        masterkeyPass = ((masterkeyPass != null) ? masterkeyPass : "Password123");
        String certificateName = props.getProperty("certificate.name");
        certificateName = ((certificateName != null) ? certificateName : "ZOHO_CERT");
        String certificateSub = props.getProperty("certificate.subject");
        certificateSub = ((certificateSub != null) ? certificateSub : "Created by ZOHO");
        String symmkeyName = props.getProperty("symmetrickey.name");
        symmkeyName = ((symmkeyName != null) ? symmkeyName : "ZOHO_SYMM_KEY");
        final String symmkeyAlgo = props.getProperty("symmetrickey.algo", "AES_256");
        final String symmkeyAlgo_alt = props.getProperty("symmetrickey.altalgo", "TRIPLE_DES");
        final String identityValue = props.getProperty("identity.value", "ZOHO_Identity_Value");
        final String keySource = props.getProperty("key.source", "ZOHO_Key_Source");
        final boolean rowGUIDEnable = Boolean.parseBoolean(props.getProperty("enable.rowguid"));
        final boolean treatCharAsNChar = Boolean.parseBoolean(props.getProperty("treat.char.as.nchar", "false"));
        this.mssqlGen.setRowGuidEnable(rowGUIDEnable);
        this.mssqlGen.setCertificateName(certificateName);
        this.mssqlGen.setSymmetricKeyName(symmkeyName);
        this.mssqlGen.setMasterkeyPassword(masterkeyPass);
        this.mssqlGen.setSymmetricKeyAlgorithm(symmkeyAlgo);
        this.mssqlGen.setSymmetricKeyAltAlgorithm(symmkeyAlgo_alt);
        this.mssqlGen.setCertificateSubject(certificateSub);
        this.mssqlGen.setIdentityValue(identityValue);
        this.mssqlGen.setKeySource(keySource);
        if (treatCharAsNChar) {
            this.mssqlGen.treatCharAsNChar();
        }
        this.mssqlGen.setIsDBMigration(this.isDBMigration);
    }
    
    public void setIsDBMigration(final boolean isMigration) {
        this.mssqlGen.setIsDBMigration(isMigration);
    }
    
    @Override
    @Deprecated
    protected ResultSet getTablesFromDB(final DatabaseMetaData metaData) throws SQLException {
        return this.getTablesFromDB(metaData, null);
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
                final List tableNames = this.getTables(connection, this.getCurrentSchema(connection));
                final List<String> templist = new ArrayList<String>();
                templist.addAll(tableNames);
                List<String> orderedTables = this.getOrderedTables(connection, tableNames);
                templist.removeAll(orderedTables);
                orderedTables.addAll(templist);
                List tablesNotDropped = null;
                do {
                    tablesNotDropped = new ArrayList();
                    final int size = orderedTables.size();
                    if (size == 0) {
                        System.out.println("No Tables found in the specified DB.");
                    }
                    int tablesDropped = 0;
                    for (final String tableName : orderedTables) {
                        final StringBuilder dropSQL = new StringBuilder();
                        try {
                            dropSQL.append(this.sqlGen.getSQLForDrop(tableName, true));
                            this.mssqlGen.appendIfExistsForDropTable(tableName, dropSQL);
                            this.execute(stmt, dropSQL.toString());
                            ++tablesDropped;
                            this.printInConsole("Dropped table " + tableName, tablesDropped % 50 == 0);
                        }
                        catch (final SQLException sqle) {
                            tablesNotDropped.add(tableName);
                        }
                    }
                    ConsoleOut.println("");
                    orderedTables = new ArrayList<String>(tablesNotDropped);
                } while (!orderedTables.isEmpty());
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
    
    private List getOrderedTables(final Connection connection, final List tables) throws SQLException {
        final DatabaseMetaData dbMeta = connection.getMetaData();
        final HashMap relationsMap = new HashMap();
        for (int i = 0; i < tables.size(); ++i) {
            final String tableName = tables.get(i);
            ResultSet rs = null;
            try {
                rs = dbMeta.getExportedKeys(null, null, tableName);
                while (rs.next()) {
                    final String fkTable = rs.getString("FKTABLE_NAME");
                    List relTables = relationsMap.get(tableName);
                    if (relTables == null) {
                        relTables = new ArrayList();
                    }
                    if (!relTables.contains(fkTable)) {
                        relTables.add(fkTable);
                    }
                    relationsMap.put(tableName, relTables);
                }
            }
            catch (final SQLException e) {
                e.printStackTrace();
            }
            finally {
                if (rs != null) {
                    try {
                        rs.close();
                    }
                    catch (final SQLException ex) {}
                }
            }
        }
        final List orderedTables = this.constuctList(relationsMap);
        return orderedTables;
    }
    
    private List constuctList(final HashMap relationsMap) {
        final List orderedTables = new ArrayList();
        final int numTables = relationsMap.size();
        for (final String mainTable : relationsMap.keySet()) {
            final List relTables = relationsMap.get(mainTable);
            final int mainIndex = orderedTables.indexOf(mainTable);
            for (int i = 0; i < relTables.size(); ++i) {
                final String relTable = relTables.get(i);
                final int relIndex = orderedTables.indexOf(relTable);
                if (relIndex == -1 && mainIndex == -1) {
                    orderedTables.add(relTable);
                }
                else if (mainIndex != -1) {
                    if (relIndex == -1) {
                        orderedTables.add(mainIndex, relTable);
                    }
                    else if (relIndex != -1 && relIndex > mainIndex) {
                        orderedTables.remove(relIndex);
                        orderedTables.add(mainIndex, relTable);
                    }
                }
            }
            if (mainIndex == -1) {
                orderedTables.add(mainTable);
            }
        }
        return orderedTables;
    }
    
    @Override
    protected ResultSet getTablesFromDB(final DatabaseMetaData metaData, final String schemaName) throws SQLException {
        return metaData.getTables(null, schemaName, "%", new String[] { "TABLE" });
    }
    
    @Override
    protected String getUniqueKeyName(final String ukName) {
        return "";
    }
    
    @Override
    protected ResultSetAdapter getResultSetAdapter(final ResultSet rs) throws SQLException {
        return new MssqlResultSetAdapter(rs);
    }
    
    @Override
    public void dropTable(final Statement stmt, final String tableName, final boolean cascade, final List relatedTables) throws SQLException {
        try {
            final String dropSQL = this.sqlGen.getSQLForDrop(tableName, cascade);
            this.execute(stmt, dropSQL);
            List<ForeignKeyDefinition> fkList = null;
            try {
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                if (td == null) {
                    MssqlDBAdapter.out.log(Level.SEVERE, "Unknown table name {0} specified. Dropping triggers ignored. ", tableName);
                }
                else {
                    fkList = td.getForeignKeyList();
                }
            }
            catch (final MetaDataException mde) {
                throw new RuntimeException("Exception occured while getting foreignkeys for the table :: [" + tableName + "]");
            }
            if (fkList != null && fkList.size() > 0) {
                MssqlDBAdapter.out.log(Level.FINE, "Going to drop the FK as for the tableName :: [{0}]", tableName);
                this.dropTriggers(stmt, fkList);
            }
        }
        catch (final QueryConstructionException excp) {
            final SQLException sqle = new SQLException(excp.getMessage());
            sqle.initCause(excp);
            throw sqle;
        }
    }
    
    @Override
    public void createTable(final Statement stmt, final String createSQL, final List relatedTables) throws SQLException {
        this.execute(stmt, createSQL);
    }
    
    @Override
    public void createTable(final Statement stmt, final TableDefinition tabDefn, final String createTableOptions, final List relatedTables) throws SQLException {
        super.createTable(stmt, tabDefn, createTableOptions, relatedTables);
        final List fkList = tabDefn.getForeignKeyList();
        if (!tabDefn.creatable()) {
            MssqlDBAdapter.out.log(Level.INFO, "Not creating the FK/UK triggers for the table :: [{0}], since it is specified as CREATETABLE false.", tabDefn.getTableName());
            return;
        }
        if (fkList != null) {
            this.createTriggers(stmt, fkList, null);
        }
        final List uniqueKeys = tabDefn.getUniqueKeys();
        if (!this.supportFilteredUniqueIndex && uniqueKeys != null) {
            this.mssql2005Adapter.createTriggersForUK(stmt, tabDefn, uniqueKeys, true);
        }
    }
    
    @Override
    public void createTables(final Statement stmt, final String schemaName, final List tableDefnList, final List tablesPresent) throws SQLException {
        super.createTables(stmt, schemaName, tableDefnList, tablesPresent);
        for (int size = tableDefnList.size(), i = 0; i < size; ++i) {
            final TableDefinition tDef = tableDefnList.get(i);
            if (!tDef.creatable()) {
                MssqlDBAdapter.out.log(Level.INFO, "createTables :: Not creating the FK triggers for the table :: [{0}], since it is specified as CREATETABLE false.", tDef.getTableName());
            }
            else {
                final List fkList = tDef.getForeignKeyList();
                if (fkList != null && !tDef.isTemplate()) {
                    this.createTriggers(stmt, fkList, null);
                }
            }
        }
    }
    
    private void dropUniqueIndexForUK(final Statement stmt, final TableDefinition tabDefn, final List<UniqueKeyDefinition> uniqueKeys) throws SQLException {
        for (final UniqueKeyDefinition ukd : uniqueKeys) {
            final String tableName = tabDefn.getTableName();
            try {
                final String dropIdxSql = this.sqlGen.getSQLForDropIndex(tableName, ukd.getName());
                MssqlDBAdapter.out.log(Level.FINER, "Dropping index for {0} in table {1} : {2}", new Object[] { ukd.getName(), tableName, dropIdxSql });
                this.execute(stmt, dropIdxSql);
            }
            catch (final QueryConstructionException qce) {
                MssqlDBAdapter.out.log(Level.WARNING, "Error while getting drop index sql for Unique Constraint {0} in table {1}", new Object[] { ukd.getName(), tableName });
            }
        }
    }
    
    private void createUniqueIndex(final Statement stmt, final TableDefinition tabDefn, final List<UniqueKeyDefinition> uniqueKeys, final List<ColumnDefinition> modifiedColDef, final Map<String, String> oldColVsNewCol) throws SQLException {
        for (final UniqueKeyDefinition ukDef : uniqueKeys) {
            this.createUniqueIndex(stmt, tabDefn, ukDef, modifiedColDef, oldColVsNewCol);
        }
    }
    
    public void createUniqueIndex(final Statement stmt, final TableDefinition tableDefinition, final UniqueKeyDefinition ukDef) throws SQLException {
        this.createUniqueIndex(stmt, tableDefinition, ukDef, null, null);
    }
    
    private void createUniqueIndex(final Statement stmt, final TableDefinition tabDefn, final UniqueKeyDefinition ukDef, final List<ColumnDefinition> modifiedColumnDef, final Map<String, String> oldColVsNewCol) throws SQLException {
        final String indexName = ukDef.getName();
        final String indexStr = ((MssqlSQLGenerator)this.sqlGen).getSQLForCreateUniqueIndex(indexName, tabDefn, ukDef, modifiedColumnDef, oldColVsNewCol);
        MssqlDBAdapter.out.log(Level.FINE, "Create unique index for unique Key of table {0} SQL constructed is {1}", new Object[] { tabDefn.getTableName(), indexStr });
        this.execute(stmt, indexStr);
    }
    
    public boolean isUniqueIndexCreated(final Statement statement, String tableName, String uniqueKeyName) throws SQLException {
        if (this.supportFilteredUniqueIndex) {
            tableName = this.sqlGen.escapeSpecialCharacters(tableName, 1);
            uniqueKeyName = this.sqlGen.escapeSpecialCharacters(uniqueKeyName, 1);
            final String checkIndexExistsSQL = "SELECT OBJECT_NAME(object_id) as table_name, name as index_name, is_unique_constraint, is_unique, is_primary_key FROM sys.indexes WHERE is_unique = 1 AND is_unique_constraint != 1 AND object_id = OBJECT_ID('" + tableName + "') AND name = '" + uniqueKeyName + "'";
            try (final ResultSet rs = statement.executeQuery(checkIndexExistsSQL)) {
                return rs.next();
            }
        }
        return false;
    }
    
    private List getReferringFKDefs(final String tableName) {
        try {
            final List<ForeignKeyDefinition> refFKs = MetaDataUtil.getReferringForeignKeyDefinitions(tableName);
            final List<ForeignKeyDefinition> fks = new ArrayList<ForeignKeyDefinition>();
            if (refFKs != null && !refFKs.isEmpty()) {
                fks.addAll(refFKs);
            }
            return fks;
        }
        catch (final MetaDataException mde) {
            throw new RuntimeException("Exception occurred while fetching the referring FKDefinitions for the tableName :: [" + tableName + "]", mde);
        }
    }
    
    private void dropTrigger(final Statement stmt, final ForeignKeyDefinition removedFK) throws SQLException {
        MssqlDBAdapter.out.log(Level.FINE, "Entered dropTrigger for the removedFK :: [{0}]", removedFK);
        final List<ForeignKeyDefinition> removedFKs = new ArrayList<ForeignKeyDefinition>();
        removedFKs.add(removedFK);
        this.dropTriggers(stmt, removedFKs);
    }
    
    private void dropTriggers(final Statement stmt, final List<ForeignKeyDefinition> removedFKs) throws SQLException {
        final List<String> masterTables = new ArrayList<String>();
        for (final ForeignKeyDefinition removedFK : removedFKs) {
            if ((removedFK.getConstraints() == 1 || removedFK.getConstraints() == 2) && !masterTables.contains(removedFK.getMasterTableName())) {
                masterTables.add(removedFK.getMasterTableName());
            }
        }
        for (final String masterTableName : masterTables) {
            final List<ForeignKeyDefinition> refFKs = this.getReferringFKDefs(masterTableName);
            if (!refFKs.removeAll(removedFKs)) {
                throw new RuntimeException("Exception occurred while dropTrigger for the removedFK :: [" + removedFKs + "]. No such ForeignKey found in the referring FKs list [" + refFKs + "] for this master table name");
            }
            final String triggerName = this.mssqlGen.getTriggerName(masterTableName);
            final String dropTriggerSQL = this.mssqlGen.getSQLForDeleteTrigger(triggerName);
            MssqlDBAdapter.out.log(Level.FINE, "Drop trigger SQL is {0}", dropTriggerSQL);
            this.execute(stmt, dropTriggerSQL);
            if (refFKs.size() <= 0) {
                continue;
            }
            final String createTriggerSQL = this.mssqlGen.getSQLForCreateTrigger(masterTableName, refFKs);
            MssqlDBAdapter.out.log(Level.FINE, "Create trigger SQL constructed is {0}", createTriggerSQL);
            this.execute(stmt, createTriggerSQL);
        }
    }
    
    private void createTrigger(final Statement stmt, final ForeignKeyDefinition addedFK, final Map<String, String> oldVsNewTableName) throws SQLException {
        if (addedFK.getConstraints() == 1 || addedFK.getConstraints() == 2) {
            final String masterTableName = addedFK.getMasterTableName();
            final List refFKs = this.getReferringFKDefs(masterTableName);
            if (!refFKs.contains(addedFK)) {
                refFKs.add(addedFK);
            }
            final String dropTriggerSQL = this.mssqlGen.getSQLForDeleteTrigger(this.mssqlGen.getTriggerName(masterTableName));
            MssqlDBAdapter.out.log(Level.FINE, "Drop trigger SQL is : {0}", dropTriggerSQL);
            this.execute(stmt, dropTriggerSQL);
            final String createTriggerSQL = this.mssqlGen.getSQLForCreateTrigger(masterTableName, refFKs, oldVsNewTableName);
            MssqlDBAdapter.out.log(Level.FINE, "Create trigger SQL constructed is : {0}", createTriggerSQL);
            this.execute(stmt, createTriggerSQL);
        }
    }
    
    private void reCreateTrigger(final Statement stmt, final String oldTableName, final String newTableName) throws SQLException, MetaDataException {
        TableDefinition td = MetaDataUtil.getTableDefinitionByName(oldTableName);
        if (td == null && oldTableName.startsWith("__")) {
            td = MetaDataUtil.getTableDefinitionByName(oldTableName.substring(2, oldTableName.length()));
        }
        final List<ForeignKeyDefinition> fks = td.getForeignKeyList();
        if (fks != null) {
            final Map<String, String> oldVsNewTableName = new HashMap<String, String>();
            oldVsNewTableName.put(td.getTableName(), newTableName);
            this.createTriggers(stmt, fks, oldVsNewTableName);
        }
        final List refFKs = this.getReferringFKDefs(td.getTableName());
        if (!refFKs.isEmpty()) {
            final String dropTriggerSQL = this.mssqlGen.getSQLForDeleteTrigger(this.mssqlGen.getTriggerName(oldTableName));
            MssqlDBAdapter.out.log(Level.FINE, "Drop trigger SQL is : {0}", dropTriggerSQL);
            this.execute(stmt, dropTriggerSQL);
            final String createTriggerSQL = this.mssqlGen.getSQLForCreateTrigger(newTableName, refFKs);
            MssqlDBAdapter.out.log(Level.FINE, "Create trigger SQL constructed is : {0}", createTriggerSQL);
            this.execute(stmt, createTriggerSQL);
        }
    }
    
    public void createTriggers(final Statement stmt, final List<ForeignKeyDefinition> addedFKs, final Map<String, String> oldVsNewTableName) throws SQLException {
        for (final ForeignKeyDefinition addedFK : addedFKs) {
            this.createTrigger(stmt, addedFK, oldVsNewTableName);
        }
    }
    
    @Override
    public void setValue(final PreparedStatement ps, final int columnIndex, final int sqlType, Object value) throws SQLException {
        if (value == null) {
            ps.setObject(columnIndex, null, sqlType);
        }
        else if (sqlType == 2004 && value != null) {
            if (value instanceof InputStream) {
                try {
                    final InputStream stream = (InputStream)value;
                    ps.setBinaryStream(columnIndex, stream, stream.available());
                    return;
                }
                catch (final IOException ioe) {
                    throw new SQLException(ioe.getMessage());
                }
            }
            if (value instanceof String) {
                ps.setBytes(columnIndex, ((String)value).getBytes());
            }
            else {
                ps.setBytes(columnIndex, (byte[])value);
            }
        }
        else if (sqlType == 92 && value != null) {
            if (value instanceof Time || value instanceof String) {
                value = "1900-01-01 " + value;
            }
            ps.setObject(columnIndex, value);
        }
        else {
            super.setValue(ps, columnIndex, sqlType, value);
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
            this.execute(stmt, "CREATE DATABASE " + dbName);
        }
        finally {
            stmt.close();
        }
    }
    
    @Override
    public String getDefaultDB(final Connection connection) throws SQLException {
        final String url = connection.getMetaData().getURL();
        String defaultDBName;
        if (url.toUpperCase(Locale.ENGLISH).contains("DATABASENAME")) {
            final int index = url.toUpperCase(Locale.ENGLISH).indexOf("DATABASENAME");
            final String dbName = url.substring(index + 13, url.length());
            defaultDBName = ((dbName.indexOf(";") != -1) ? dbName.substring(0, dbName.indexOf(";")) : dbName);
        }
        else {
            final Map map = this.splitConnectionURL(url);
            defaultDBName = map.get("DBName");
        }
        return defaultDBName;
    }
    
    @Override
    public String getDBName(final Connection connection) throws SQLException {
        return connection.getCatalog();
    }
    
    @Override
    protected int getBatchFailureIndex(final BatchUpdateException bue) {
        final int[] updateCounts = bue.getUpdateCounts();
        if (updateCounts != null) {
            return updateCounts.length;
        }
        return -1;
    }
    
    @Override
    public boolean isActive(final Connection c) {
        Statement s = null;
        ResultSet rs = null;
        try {
            s = c.createStatement();
            rs = s.executeQuery("SELECT 1");
            rs.next();
            return true;
        }
        catch (final Exception e) {
            return false;
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (s != null) {
                    s.close();
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    public boolean isEncryptionSupported() {
        return MssqlDBAdapter.supportsEncryption;
    }
    
    public boolean isFilteredUniqueIndexSupported() {
        return this.supportFilteredUniqueIndex;
    }
    
    public boolean isTextDataTypeDeprecated() {
        return MssqlDBAdapter.isTextDataTypeDeprecated;
    }
    
    @Override
    public void prepareDatabase(final Connection conn) throws DataBaseException {
        this.prepareDatabase(conn, true);
    }
    
    public void prepareDatabase(final Connection conn, final boolean createKey) throws DataBaseException {
        Statement stmt = null;
        try {
            boolean versionGrt2000 = true;
            final DatabaseMetaData metaData = conn.getMetaData();
            final int version = metaData.getDatabaseMajorVersion();
            this.supportFilteredUniqueIndex = (version > 9);
            if (this.supportFilteredUniqueIndex) {
                ((MssqlSQLGenerator)this.sqlGen).setSupportFilteredUniqueIndex(true);
            }
            versionGrt2000 = (version > 8);
            if (versionGrt2000) {
                MssqlDBAdapter.supportsEncryption = versionGrt2000;
                MssqlDBAdapter.isTextDataTypeDeprecated = versionGrt2000;
                ((MssqlSQLGenerator)this.sqlGen).deprecateTextDataType();
                ((MssqlSQLGenerator)this.sqlGen).setSupportsEncryption(MssqlDBAdapter.supportsEncryption);
            }
            if (MssqlDBAdapter.supportsEncryption) {
                stmt = conn.createStatement();
                final boolean created = this.createMasterKey(stmt, createKey);
                this.createCertificate(stmt, createKey);
                this.createSymmetricKey(stmt, createKey);
                if (created) {
                    MssqlDBAdapter.out.log(Level.INFO, "Master Key Created");
                    try {
                        stmt.execute(this.mssqlGen.getSQLForOpenSymmetricKey());
                    }
                    catch (final SQLException e) {
                        throw new DataBaseException(1011, e);
                    }
                    this.testMasterKey(stmt);
                }
                else {
                    try {
                        MssqlDBAdapter.out.log(Level.INFO, "Master Key is already Created");
                        stmt.execute(this.mssqlGen.getSQLForOpenSymmetricKey());
                        this.testMasterKey(stmt);
                    }
                    catch (final SQLException e) {
                        MssqlDBAdapter.out.log(Level.INFO, "Master Key is already created but not associated to Service Master Key");
                        try {
                            stmt.execute(this.mssqlGen.getSQLForOpenMasterKey());
                        }
                        catch (final SQLException e2) {
                            throw new DataBaseException(1004, e2);
                        }
                        try {
                            stmt.execute(this.mssqlGen.getSQLToAssociateMKWithSMK());
                        }
                        catch (final SQLException e3) {
                            throw new DataBaseException(1005, e3);
                        }
                        try {
                            stmt.execute(this.mssqlGen.getSQLForOpenSymmetricKey());
                        }
                        catch (final SQLException e4) {
                            throw new DataBaseException(1011, e4);
                        }
                        this.testMasterKey(stmt);
                    }
                }
            }
        }
        catch (final SQLException e5) {
            MssqlDBAdapter.out.log(Level.SEVERE, "Error during database preparation", e5);
            throw new DataBaseException(e5);
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (final SQLException e6) {
                    throw new DataBaseException(1017, e6);
                }
            }
        }
    }
    
    private void testMasterKey(final Statement stmt) throws SQLException, DataBaseException {
        ResultSet rs = null;
        try {
            if (!stmt.executeQuery(this.mssqlGen.getSelectSQLForOpenSymmetricKey()).next()) {
                throw new DataBaseException(1011, "Opening of symmetric key failed");
            }
            rs = stmt.executeQuery(this.mssqlGen.getSelectSQLForEncryptDecrytData());
            if (rs.next() && rs.getBoolean(1)) {
                stmt.execute(this.mssqlGen.getSQLForCloseSymmetricKey());
                return;
            }
            throw new SQLException("data is not encrypted or decrypted properly");
        }
        catch (final SQLException e) {
            throw e;
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
    
    private boolean createMasterKey(final Statement stmt, final boolean createKey) throws SQLException, DataBaseException {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(this.mssqlGen.getSelectSQLForDefaultSymmetricKeys());
            final boolean isAlreadyCreated = rs.next();
            if (!isAlreadyCreated && createKey) {
                try {
                    MssqlDBAdapter.out.log(Level.INFO, "Going to create the master key");
                    stmt.execute(this.mssqlGen.getSQLForCreateMasterKey());
                    return true;
                }
                catch (final SQLException e) {
                    throw new DataBaseException(1003, e);
                }
            }
            if (!isAlreadyCreated && !createKey) {
                throw new DataBaseException(1002, "Master key has not been created!!");
            }
            return false;
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
    
    private void createSymmetricKey(final Statement stmt, final boolean createKey) throws SQLException, DataBaseException {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(this.mssqlGen.getSelectSQLForSymmetricKey());
            final boolean isAlreadyCreated = rs.next();
            if (!isAlreadyCreated && createKey) {
                try {
                    stmt.execute(this.mssqlGen.getSQLForCreateSymmetricKey(true));
                    return;
                }
                catch (final SQLException e) {
                    throw new DataBaseException(1010, e);
                }
            }
            if (!isAlreadyCreated && !createKey) {
                throw new DataBaseException(1009, "Symmetric key has not been created!!");
            }
        }
        catch (final SQLException sqe) {
            try {
                stmt.execute(this.mssqlGen.getSQLForCreateSymmetricKey(false));
            }
            catch (final SQLException e) {
                throw new DataBaseException(1010, e);
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
    
    private void createCertificate(final Statement stmt, final boolean createCertificate) throws SQLException, DataBaseException {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(this.mssqlGen.getSelectSQLForCertificate());
            final boolean isAlreadyCreated = rs.next();
            if (!isAlreadyCreated && createCertificate) {
                try {
                    stmt.execute(this.mssqlGen.getSQLForCreateCertificate());
                    return;
                }
                catch (final SQLException e) {
                    throw new DataBaseException(1007, e);
                }
            }
            if (!isAlreadyCreated && !createCertificate) {
                throw new DataBaseException(1006, "Certificate has not been created!!");
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
    
    @Override
    public void handlePreExecute(final Connection conn, final List<String> tableNames, final List<String> columnNames) throws Exception {
        MssqlDBAdapter.out.log(Level.FINE, "PreExecute called for tables {0} and columns {1}", new Object[] { tableNames, columnNames });
        if (MssqlDBAdapter.supportsEncryption && columnNames != null && columnNames.size() > 0) {
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                stmt.execute(this.mssqlGen.getSQLForOpenSymmetricKey());
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
    }
    
    @Override
    public void handlePostExecute(final Connection conn, final List<String> tableNames, final List<String> columnNames) throws Exception {
        MssqlDBAdapter.out.log(Level.FINE, "PostExecute called for tables {0} and columns {1}", new Object[] { tableNames, columnNames });
        if (MssqlDBAdapter.supportsEncryption && columnNames != null && columnNames.size() > 0) {
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                stmt.execute(this.mssqlGen.getSQLForCloseSymmetricKey());
            }
            finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
    }
    
    @Override
    public Map splitConnectionURL(final String databaseURL) {
        final Map mapProperties = this.getURLProps(databaseURL);
        final Properties props = new Properties();
        StringTokenizer stk = new StringTokenizer(databaseURL, "//", false);
        stk.nextToken();
        String tok = stk.nextToken();
        stk = new StringTokenizer(tok, ";", false);
        tok = stk.nextToken();
        String hostName = null;
        String portStr = "1433";
        if (tok.indexOf(":") < 0) {
            hostName = tok;
        }
        else {
            final StringTokenizer stk2 = new StringTokenizer(tok, ":", false);
            hostName = stk2.nextToken();
            portStr = stk2.nextToken();
        }
        int port = 1433;
        try {
            port = Integer.parseInt(portStr);
        }
        catch (final Exception e) {
            e.printStackTrace();
            System.out.println("Exception occured while finding the port, hence taking the default port 1433.");
        }
        while (stk.hasMoreTokens()) {
            final String[] s = stk.nextToken().split("=");
            final String value = (s.length == 2) ? s[1] : "";
            props.setProperty(s[0], value);
        }
        props.setProperty("Server", hostName);
        props.setProperty("Port", port + "");
        if (databaseURL.startsWith("jdbc:jtds:sqlserver")) {
            final String urlWithoutProps = mapProperties.get("urlWithoutProps");
            final String[] s2 = urlWithoutProps.split("/");
            ((Hashtable<String, String>)props).put("DBName", s2[s2.length - 1]);
        }
        if (props.containsKey("databaseName")) {
            ((Hashtable<String, String>)props).put("DBName", props.getProperty("databaseName"));
        }
        mapProperties.put("properties", props);
        mapProperties.putAll(props);
        return mapProperties;
    }
    
    @Override
    protected String getDelimiterForURLProps() {
        return ";";
    }
    
    @Override
    protected String getInitCharForURLProps() {
        return this.getDelimiterForURLProps();
    }
    
    @Override
    public boolean validateVersion(final Connection c) {
        try {
            String collation_string = null;
            boolean isCS = true;
            Statement s = null;
            ResultSet rs = null;
            try {
                s = c.createStatement();
                rs = s.executeQuery("SELECT convert(varchar(MAX),DATABASEPROPERTYEX('" + this.getDBName(c) + "', 'Collation')) AS 'Database Level Collation'");
                while (rs.next()) {
                    collation_string = rs.getString(1);
                }
                rs.close();
                if (collation_string == null || collation_string.equals("")) {
                    rs = s.executeQuery("SELECT convert(varchar(MAX),SERVERPROPERTY('Collation')) AS 'Server Level Collation'");
                    while (rs.next()) {
                        collation_string = rs.getString(1);
                    }
                    rs.close();
                }
            }
            finally {
                if (s != null) {
                    s.close();
                }
            }
            isCS = (collation_string.indexOf("_CS_") >= 0);
            ((MssqlSQLGenerator)this.sqlGen).setCollation(collation_string);
            final String toggle_collation_string = this.getToggleCollation(c, collation_string);
            ((MssqlSQLGenerator)this.sqlGen).setToggleCollation(toggle_collation_string);
            return super.validateVersion(c);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private String getToggleCollation(final Connection conn, String collationStr) throws SQLException {
        MssqlDBAdapter.out.log(Level.INFO, "Incoming collation string is {0}", collationStr);
        Statement stmt = null;
        ResultSet rs = null;
        String toggle_collation_string = collationStr;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from ::fn_helpcollations() where name = '" + collationStr + "'");
            if (rs.next()) {
                final String description = rs.getString(2);
                final String[] splitDes = description.split(",");
                final String caseSensitivity = splitDes[1].trim();
                final boolean isCaseInSensitiveCollation = caseSensitivity.equalsIgnoreCase("case-insensitive");
                ((MssqlSQLGenerator)this.sqlGen).setIsDBCaseSensitive(!isCaseInSensitiveCollation);
                if (isCaseInSensitiveCollation) {
                    collationStr = collationStr.replaceAll("_CI_", "_CS_");
                    collationStr = collationStr.replaceAll("_A(I|S)", "\\_A%");
                }
                else {
                    final boolean isBinaryCollation = collationStr.indexOf("_BIN") > 0;
                    if (isBinaryCollation) {
                        collationStr = collationStr.replaceAll("_BIN[2]*", "\\_CI\\_%");
                    }
                    else {
                        collationStr = collationStr.replaceAll("_CS_", "_CI_");
                        collationStr = collationStr.replaceAll("_A(I|S)", "\\_A%");
                    }
                }
            }
            MssqlDBAdapter.out.log(Level.INFO, "Collation String constructed is {0}", collationStr);
            rs.close();
            rs = stmt.executeQuery("select name from ::fn_helpcollations() where name like '" + collationStr + "'");
            if (rs.next()) {
                toggle_collation_string = rs.getString(1);
            }
            else {
                MssqlDBAdapter.out.log(Level.WARNING, "Could not find the toggling collation for {0}", collationStr);
            }
        }
        catch (final Exception exc) {
            MssqlDBAdapter.out.log(Level.WARNING, "Exception while finding the toggle collation string for collation {0}", collationStr);
            exc.printStackTrace();
        }
        finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
        MssqlDBAdapter.out.log(Level.INFO, "The Toggle collation string is {0}", toggle_collation_string);
        return toggle_collation_string;
    }
    
    private void postAlterTable(final Connection con, final Statement statement, final AlterTableQuery atq) throws SQLException, MetaDataException, QueryConstructionException {
        TableDefinition tDef = null;
        try {
            tDef = MetaDataUtil.getTableDefinitionByName(atq.getTableName());
        }
        catch (final MetaDataException ex) {
            final SQLException sqe = new SQLException(ex.getMessage());
            sqe.initCause(ex);
            throw sqe;
        }
        final List<UniqueKeyDefinition> ukDefs = new ArrayList<UniqueKeyDefinition>();
        final Map<String, String> oldColNameVsNewColName = new HashMap<String, String>();
        for (final AlterOperation alterOper : atq.getAlterOperations()) {
            final Object alterObject = alterOper.getAlterObject();
            final int operation = alterOper.getOperationType();
            if ((operation == 6 || operation == 14) && !alterOper.isDisableTriggerCreation()) {
                final ForeignKeyDefinition fkDefn = (ForeignKeyDefinition)alterObject;
                this.createTrigger(statement, fkDefn, null);
            }
            else if (operation == 4) {
                final UniqueKeyDefinition ukDef = (UniqueKeyDefinition)alterOper.getAlterObject();
                if (this.mssqlGen.isTriggerForUK(alterOper, atq)) {
                    ukDefs.add(ukDef);
                }
                else {
                    if (!this.supportFilteredUniqueIndex || !((MssqlSQLGenerator)this.sqlGen).isAddColumnAndAddUK(alterOper, atq)) {
                        continue;
                    }
                    final String sql = ((MssqlSQLGenerator)this.sqlGen).getSQLForCreateUniqueIndex(atq, alterOper, tDef, ukDef);
                    this.execute(statement, sql);
                }
            }
            else if (operation == 1) {
                final ColumnDefinition colDef = (ColumnDefinition)alterOper.getAlterObject();
                if (colDef.getUniqueValueGeneration() == null || !alterOper.fillUVHValues()) {
                    continue;
                }
                final ColumnDefinition newCol = new ColumnDefinition();
                newCol.setColumnName("_" + colDef.getColumnName() + "_");
                newCol.setTableName(atq.getTableName());
                newCol.setNullable(colDef.isNullable());
                newCol.setDataType(colDef.getDataType());
                statement.execute(this.mssqlGen.getSQLForAddUVHColumn(newCol));
                statement.execute(this.mssqlGen.getSQLForUpdateColumn(atq.getTableName(), colDef.getColumnName(), newCol.getColumnName()));
                final AlterTableQuery aq = new AlterTableQueryImpl(atq.getTableName());
                aq.removeColumn(newCol.getColumnName());
                final String dropQuery = this.mssqlGen.getSQLForAlterTable(aq);
                statement.execute(dropQuery);
            }
            else if (operation == 2) {
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(atq.getTableName());
                final ColumnDefinition colDef2 = (ColumnDefinition)alterOper.getAlterObject();
                if (this.supportFilteredUniqueIndex || td.getUniqueKeys() == null) {
                    continue;
                }
                for (final UniqueKeyDefinition uk : td.getUniqueKeys()) {
                    if (uk.getColumns().contains(colDef2.getColumnName()) && colDef2.isNullable() && uk.getColumns().size() == 1) {
                        ukDefs.add(uk);
                    }
                }
            }
            else if (this.supportFilteredUniqueIndex && operation == 12) {
                final String oldColumnName = ((String[])alterObject)[0];
                final String newColumnName = ((String[])alterObject)[1];
                final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(atq.getTableName());
                this.checkForColumnExistsInUKForColumnRename(oldColumnName, tableDefinition, ukDefs);
                oldColNameVsNewColName.put(oldColumnName, newColumnName);
            }
            else {
                if (operation != 13) {
                    continue;
                }
                final String newTableName = (String)alterOper.getAlterObject();
                this.reCreateTrigger(statement, atq.getTableName(), newTableName);
            }
        }
        if (0 != ukDefs.size()) {
            if (this.supportFilteredUniqueIndex) {
                this.createUniqueIndex(statement, tDef, ukDefs, Collections.emptyList(), oldColNameVsNewColName);
            }
            else {
                this.mssql2005Adapter.createTriggersForUK(statement, tDef, ukDefs, false);
            }
        }
    }
    
    private void preAlterTable(final Connection con, final Statement statement, final AlterTableQuery atq) throws Exception {
        final String tableName = atq.getTableName();
        final List<UniqueKeyDefinition> ukDefs = new ArrayList<UniqueKeyDefinition>();
        final List<String> modEncryptedColQueries = new ArrayList<String>();
        TableDefinition tDef = null;
        try {
            tDef = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException ex) {
            final SQLException sqle = new SQLException(ex.getMessage());
            sqle.initCause(ex);
            throw sqle;
        }
        final List<AlterOperation> alterOperations = atq.getAlterOperations();
        for (int aoSize = atq.getAlterOperations().size(), i = 0; i < aoSize; ++i) {
            AlterOperation alterOper;
            final AlterOperation ao = alterOper = alterOperations.get(i);
            if (atq.isRevert()) {
                alterOper = this.mssqlGen.getAlterOperationForRevert(ao);
            }
            final Object alterObject = alterOper.getAlterObject();
            final int operation = alterOper.getOperationType();
            String colName = null;
            if (operation == 3 || operation == 2) {
                if (operation == 3) {
                    colName = (String)alterObject;
                }
                else {
                    final ColumnDefinition colDef = (ColumnDefinition)alterObject;
                    colName = colDef.getColumnName();
                }
                final String defConstraintName = this.getDefValConstraintName(tableName, colName, statement);
                ao.setDefaultValueConstraintName(defConstraintName);
            }
            else if (this.supportFilteredUniqueIndex && operation == 12) {
                final String oldColumnName = ((String[])alterObject)[0];
                this.checkForColumnExistsInUKForColumnRename(oldColumnName, tDef, ukDefs);
            }
            else if (operation == 7 || operation == 14) {
                String fkName;
                if (operation == 7) {
                    fkName = (String)alterObject;
                }
                else {
                    fkName = ((ForeignKeyDefinition)alterObject).getName();
                }
                ForeignKeyDefinition removedFKDefn = null;
                try {
                    removedFKDefn = MetaDataUtil.getTableDefinitionByName(tableName).getForeignKeyDefinitionByName(fkName);
                    final List<String> indexNames = this.getAllIndexNamesInTable(tableName, con);
                    MssqlDBAdapter.out.log(Level.INFO, "indexNames :: {0} for the table :: [{1}]", new Object[] { indexNames, tableName });
                    final String fkIdxName = this.mssqlGen.getIndexName(removedFKDefn.getName());
                    if (!indexNames.contains(fkIdxName)) {
                        ao.handleIndexForFK = false;
                    }
                }
                catch (final MetaDataException e) {
                    final SQLException sqle2 = new SQLException(e.getMessage());
                    sqle2.initCause(e);
                    throw sqle2;
                }
                this.dropTrigger(statement, removedFKDefn);
            }
            else if (operation == 5 && this.mssqlGen.isTriggerForUK(alterOper, atq)) {
                ukDefs.add(tDef.getUniqueKeyDefinitionByName((String)alterOper.getAlterObject()));
            }
            if (this.mssqlGen.isEncrptedModifyColumn(ao) && !this.mssqlGen.isNewDataTypeBinary(ao)) {
                if (this.getTotalRowCount(con, tableName) > 0L) {
                    this.handleEncryptedModifyColumn(modEncryptedColQueries, ao, i);
                }
                else {
                    final ColumnDefinition colDef = (ColumnDefinition)ao.getAlterObject();
                    final AlterTableQuery dropAndAddColQuery = new AlterTableQueryImpl(tableName);
                    dropAndAddColQuery.removeColumn(colDef.getColumnName());
                    dropAndAddColQuery.getAlterOperations().get(0).setDefaultValueConstraintName(ao.getDefaultValueConstraintName());
                    dropAndAddColQuery.addColumn(colDef);
                    modEncryptedColQueries.add(this.mssqlGen.getSQLForAlterTable(dropAndAddColQuery));
                }
            }
            else if (this.mssqlGen.isNewDataTypeBinary(ao)) {
                final ColumnDefinition colDef = (ColumnDefinition)ao.getAlterObject();
                final ColumnDefinition sourceColumnDefn = tDef.getColumnDefinitionByName(colDef.getColumnName());
                if (!sourceColumnDefn.getDataType().equals("BLOB")) {
                    if (!sourceColumnDefn.getDataType().equals("SBLOB")) {
                        if (sourceColumnDefn.getDataType().equals("CHAR") || sourceColumnDefn.getDataType().equals("SCHAR") || sourceColumnDefn.getDataType().equals("NCHAR")) {
                            this.handleBinaryModifyColumn(con, colDef);
                        }
                    }
                }
            }
        }
        if (0 != ukDefs.size()) {
            if (this.supportFilteredUniqueIndex) {
                this.dropUniqueIndexForUK(statement, tDef, ukDefs);
            }
            else {
                this.mssql2005Adapter.dropTriggerForUK(statement, tDef, ukDefs);
            }
        }
        for (final String query : modEncryptedColQueries) {
            MssqlDBAdapter.out.log(Level.INFO, "Going to execute {0}", query);
            statement.execute(query);
        }
    }
    
    private void checkForColumnExistsInUKForColumnRename(final String columnName, final TableDefinition tableDefinition, final List<UniqueKeyDefinition> uniqueKeyDefinitions) {
        final ColumnDefinition colDef = tableDefinition.getColumnDefinitionByName(columnName);
        if (colDef.isNullable() && tableDefinition.getUniqueKeys() != null) {
            for (final UniqueKeyDefinition ukd : tableDefinition.getUniqueKeys()) {
                if (ukd.getColumns().contains(columnName)) {
                    uniqueKeyDefinitions.add(ukd);
                }
            }
        }
    }
    
    private void handleEncryptedModifyColumn(final List<String> queries, final AlterOperation modifyOperation, final int index) throws QueryConstructionException {
        try {
            final String tableName = modifyOperation.getTableName();
            ColumnDefinition modColDef = (ColumnDefinition)modifyOperation.getAlterObject();
            modColDef = (ColumnDefinition)modColDef.clone();
            final String oldColName = modColDef.getColumnName();
            final String newColName = "MOD__" + index;
            modColDef.setColumnName(newColName);
            modColDef.setNullable(true);
            modColDef.setUnique(false);
            AlterTableQueryImpl aq = new AlterTableQueryImpl(tableName);
            aq.addColumn(modColDef);
            queries.add(this.sqlGen.getSQLForAlterTable(aq));
            queries.add(this.mssqlGen.getSQLForOpenSymmetricKey());
            final ColumnDefinition oldColDef = this.mssqlGen.getColumnIgnoreException(tableName, oldColName);
            queries.add(this.mssqlGen.getSQLForColumnValueCopy(oldColDef, modColDef));
            queries.add(this.mssqlGen.getSQLForCloseSymmetricKey());
            aq = new AlterTableQueryImpl(tableName);
            aq.removeColumn(oldColName);
            aq.getAlterOperations().get(0).setDefaultValueConstraintName(modifyOperation.getDefaultValueConstraintName());
            queries.add(this.sqlGen.getSQLForAlterTable(aq));
            aq = new AlterTableQueryImpl(tableName);
            aq.renameColumn(newColName, oldColName);
            queries.add(this.sqlGen.getSQLForAlterTable(aq));
        }
        catch (final Exception e) {
            throw new QueryConstructionException("MODIFY_COLUMN_ENCRYPT_EXCEPTION", e);
        }
    }
    
    private void handleBinaryModifyColumn(final Connection con, ColumnDefinition modColDef) throws QueryConstructionException {
        try (final Statement stmt = con.createStatement()) {
            final String tableName = modColDef.getTableName();
            final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
            modColDef = (ColumnDefinition)modColDef.clone();
            final String oldColName = modColDef.getColumnName();
            final String newColName = "MOD_TEMP";
            modColDef.setColumnName(newColName);
            modColDef.setNullable(true);
            modColDef.setUnique(false);
            AlterTableQueryImpl aq = new AlterTableQueryImpl(tableName);
            aq.addColumn(modColDef);
            stmt.execute(this.sqlGen.getSQLForAlterTable(aq));
            stmt.execute(this.mssqlGen.getSQLForOpenSymmetricKey());
            this.updateColumnsWithBinaryValue(con, modColDef, tableDefinition.getColumnDefinitionByName(oldColName));
            stmt.execute(this.mssqlGen.getSQLForCloseSymmetricKey());
            aq = new AlterTableQueryImpl(tableName);
            aq.removeColumn(oldColName);
            stmt.execute(this.sqlGen.getSQLForAlterTable(aq));
            aq = new AlterTableQueryImpl(tableName);
            aq.renameColumn(newColName, oldColName);
            stmt.execute(this.sqlGen.getSQLForAlterTable(aq));
        }
        catch (final Exception e) {
            throw new QueryConstructionException("MODIFY_BINARY_COLUMN_EXCEPTION", e);
        }
    }
    
    void updateColumnsWithBinaryValue(final Connection con, final ColumnDefinition dstColDefn, final ColumnDefinition srcColDefn) throws SQLException, DataAccessException, MetaDataException, QueryConstructionException, CloneNotSupportedException {
        final String tableName = dstColDefn.getTableName();
        final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
        final String sColName = srcColDefn.getColumnName();
        final List<String> pkColumns = tableDefinition.getPrimaryKey().getColumnList();
        final SelectQuery query = new SelectQueryImpl(new Table(tableName));
        final Column srcColumn = new Column(tableName, sColName);
        query.addSelectColumn(srcColumn);
        for (final String pkColumn : pkColumns) {
            query.addSelectColumn(new Column(tableName, pkColumn));
        }
        QueryUtil.setDataType(query);
        final String selectSQL = this.mssqlGen.getSQLForSelect(query);
        try (final Statement stmt = con.createStatement();
             final ResultSet rs = stmt.executeQuery(selectSQL);
             final DataSet ds = new DataSet(new ResultSetAdapter(rs), query, RelationalAPI.getSelectColumns(query), stmt)) {
            final String updateSql = this.mssqlGen.getUpdateSQLForColumn(tableName, dstColDefn, pkColumns);
            try (final PreparedStatement ps = con.prepareStatement(updateSql)) {
                while (ds.next()) {
                    this.setValue(ps, 1, 2004, ds.getValue(1));
                    for (int i = 2; i < pkColumns.size() + 2; ++i) {
                        final ColumnDefinition pkColumnDefn = tableDefinition.getColumnDefinitionByName(pkColumns.get(i - 2));
                        this.setValue(ps, i, pkColumnDefn.getSQLType(), ds.getValue(i));
                    }
                    ps.addBatch();
                }
                this.executeBatch(ps);
            }
        }
    }
    
    private List<String> getAllIndexNamesInTable(final String tableName, final Connection conn) throws SQLException {
        final DatabaseMetaData meta = conn.getMetaData();
        final List<String> indexNames = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = meta.getIndexInfo(null, null, tableName, false, true);
            while (rs.next()) {
                final String indexName = rs.getString("INDEX_NAME");
                if (indexName == null) {
                    continue;
                }
                indexNames.add(indexName);
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
        return indexNames;
    }
    
    @Override
    public void alterTable(final Connection connection, AlterTableQuery alterTableQuery) throws SQLException {
        try {
            int operationType = alterTableQuery.getAlterOperations().get(0).getOperationType();
            DCAdapter dcAdapter = null;
            if (operationType == 19 || operationType == 20 || operationType == 22 || operationType == 21) {
                dcAdapter = this.getDCAdapterForTable(alterTableQuery.getTableName());
                if (dcAdapter == null) {
                    throw new MetaDataException("No dynamic column handler defined");
                }
            }
            this.validateAlterTableQuery(connection, alterTableQuery);
            switch (alterTableQuery.getAlterOperations().get(0).getOperationType()) {
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
                    String alterSQL = null;
                    boolean isCommitRequired = false;
                    boolean isExecuted = false;
                    alterTableQuery = this.mssqlGen.getModifiedATQ(alterTableQuery);
                    Statement statement = null;
                    try {
                        if (connection.getAutoCommit()) {
                            isCommitRequired = true;
                            connection.setAutoCommit(false);
                        }
                        statement = connection.createStatement();
                        this.preAlterTable(connection, statement, alterTableQuery);
                        alterSQL = this.mssqlGen.getSQLForAlterTable(alterTableQuery);
                        final List<AlterOperation> alterOperationList = alterTableQuery.getAlterOperations();
                        for (final AlterOperation ao : alterOperationList) {
                            operationType = ao.getOperationType();
                            if (operationType == 8 || operationType == 7 || operationType == 17 || operationType == 14) {
                                String constraintNameMeta;
                                if (operationType == 8 || operationType == 7) {
                                    constraintNameMeta = (String)ao.getAlterObject();
                                }
                                else if (operationType == 14) {
                                    constraintNameMeta = ((ForeignKeyDefinition)ao.getAlterObject()).getName();
                                }
                                else {
                                    constraintNameMeta = ((Object[])ao.getAlterObject())[0].toString();
                                }
                                if (constraintNameMeta.length() <= 29) {
                                    continue;
                                }
                                final String constraintNameInDB = this.getConstraintNameFromDB(connection, ao);
                                MssqlDBAdapter.out.log(Level.INFO, "Constraint name [{0}] is replaced to actual constraint name as created in DB [{1}]", new Object[] { constraintNameMeta, constraintNameMeta });
                                alterSQL = alterSQL.replaceFirst(this.mssqlGen.getConstraintName(constraintNameMeta), this.mssqlGen.getConstraintName(constraintNameInDB));
                            }
                        }
                        MssqlDBAdapter.out.log(Level.FINE, "Alter query going to execute ::: [{0}]", alterSQL);
                        if (alterSQL != null && alterSQL.trim().length() > 0) {
                            statement.execute(alterSQL);
                        }
                        this.postAlterTable(connection, statement, alterTableQuery);
                        if (isCommitRequired) {
                            connection.commit();
                            connection.setAutoCommit(true);
                        }
                        isExecuted = true;
                    }
                    catch (final Exception e) {
                        MssqlDBAdapter.out.log(Level.SEVERE, "Problem while executing ALTER SQL in DB. \n" + e.getMessage(), e);
                        throw e;
                    }
                    finally {
                        if (isCommitRequired && !isExecuted) {
                            connection.rollback();
                        }
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    break;
                }
            }
        }
        catch (final MetaDataException mde) {
            throw new IllegalArgumentException(mde);
        }
        catch (final Exception e2) {
            final SQLException sqle = new SQLException(e2.getMessage());
            sqle.initCause(e2);
            throw sqle;
        }
    }
    
    private String getDefValConstraintName(final String tableName, final String columnName, final Statement s) throws SQLException {
        final String sql = this.mssqlGen.getSQLToFetchDefValConstraintName(tableName, columnName);
        String defConstraintName = null;
        ResultSet rs = null;
        try {
            MssqlDBAdapter.out.log(Level.FINE, "alterTable :: Going to execute the SQL :: [{0}]", sql);
            rs = s.executeQuery(sql);
            while (rs.next()) {
                defConstraintName = rs.getString(2);
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
        return defConstraintName;
    }
    
    private String getConstraintNameFromDB(final Connection connection, final AlterOperation alterOperation) throws SQLException {
        final int operation = alterOperation.getOperationType();
        final String tableName = alterOperation.getTableName();
        PreparedStatement selectStatement = null;
        Statement statement = null;
        ResultSet rs = null;
        String constraintName = null;
        try {
            switch (operation) {
                case 8:
                case 17: {
                    final String sql = this.mssqlGen.getSQLForGetConstraintName(alterOperation);
                    selectStatement = connection.prepareStatement(sql);
                    selectStatement.setString(1, tableName);
                    rs = selectStatement.executeQuery();
                    while (rs.next()) {
                        constraintName = rs.getString(1);
                    }
                    MssqlDBAdapter.out.log(Level.INFO, "Returning constraint name :: {0}", constraintName);
                    return constraintName;
                }
                case 7:
                case 14: {
                    String fkConsName = null;
                    if (operation == 14) {
                        fkConsName = ((ForeignKeyDefinition)alterOperation.getAlterObject()).getName();
                    }
                    else {
                        fkConsName = (String)alterOperation.getAlterObject();
                    }
                    List<String> fkColumnList = null;
                    try {
                        fkColumnList = MetaDataUtil.getForeignKeyDefinitionByName(fkConsName).getFkColumns();
                    }
                    catch (final MetaDataException ex) {
                        throw new SQLException("Exception occured while fetching [" + fkConsName + "] FKColumns");
                    }
                    String sql = this.mssqlGen.getSQLForGetColIDFromSysObjectTable(tableName, fkColumnList);
                    MssqlDBAdapter.out.log(Level.INFO, "Going to execute :: {0}", sql);
                    statement = connection.createStatement();
                    rs = statement.executeQuery(sql);
                    final Map<String, Integer> columnVsColId = new HashMap<String, Integer>();
                    while (rs.next()) {
                        final String columnName = rs.getString("name");
                        final int colId = rs.getInt("colid");
                        columnVsColId.put(columnName, colId);
                    }
                    this.safeClose(rs);
                    sql = this.mssqlGen.getSQLForGetConstraintName(alterOperation);
                    selectStatement = connection.prepareStatement(sql);
                    for (int index = 1; index <= fkColumnList.size(); ++index) {
                        final String columnName2 = fkColumnList.get(index - 1);
                        selectStatement.setInt(index, columnVsColId.get(columnName2));
                    }
                    rs = selectStatement.executeQuery();
                    while (rs.next()) {
                        constraintName = rs.getString(1);
                    }
                    MssqlDBAdapter.out.log(Level.INFO, "Returning constraint name :: {0}", constraintName);
                    return constraintName;
                }
                default: {
                    throw new SQLException("Unnown Operation[" + operation + "] specified to get original constraint name");
                }
            }
        }
        finally {
            this.safeClose(rs);
            this.safeClose(selectStatement);
            this.safeClose(statement);
        }
    }
    
    @Override
    public boolean abortBackup() throws Exception {
        throw new RuntimeException("Abort Backup has to be implemented db specifically");
    }
    
    public int restoreDB(final RestoreDBParams rdbp) throws Exception {
        throw new RuntimeException("Restore DB has to be implemented db specifically");
    }
    
    @Override
    public int fileBackup(final String backupDir, final String backupFileName, final List<String> directoriesToBeArchived, final String versionHandlerName, final Properties prefProps) throws Exception {
        throw new RuntimeException("BackUp dir has to be implemented db specifically");
    }
    
    @Override
    protected String getErrorCodeTableName() {
        return "MsSQLErrorCode";
    }
    
    @Override
    protected boolean isMigrateSCHARRequired(final ColumnDefinition colDef) {
        return colDef.getMaxLength() <= 4000;
    }
    
    @Override
    public boolean isColumnModified(final ColumnDefinition oldColumnDefinition, final ColumnDefinition newColumnDefinition, final List<String> changedAttributes) {
        if (changedAttributes.contains("data-type") && this.mssqlGen.isCharTreatedAsNChar() && ((oldColumnDefinition.getDataType().equals("CHAR") && newColumnDefinition.getDataType().equals("NCHAR")) || (oldColumnDefinition.getDataType().equals("NCHAR") && newColumnDefinition.getDataType().equals("CHAR")))) {
            MssqlDBAdapter.out.log(Level.INFO, "CHAR/NCHAR to NCHAR/CHAR data-type change is skipped.");
            changedAttributes.remove("data-type");
        }
        return changedAttributes.contains("data-type") || super.isColumnModified(oldColumnDefinition, newColumnDefinition, changedAttributes);
    }
    
    @Override
    public String getTableName(final String schemaString) {
        String retStr = super.getTableName(schemaString);
        if (schemaString != null) {
            retStr = retStr.replace('[', ' ').replace(']', ' ').trim();
        }
        return retStr;
    }
    
    @Override
    public boolean isTableNotFoundException(final SQLException sqle) {
        return (sqle.getErrorCode() == 208 || sqle.getErrorCode() == -9999) && "S0002".equals(sqle.getSQLState());
    }
    
    @Override
    public BulkInsertObject createBulkInsertObject(final BulkLoad bulk) throws IOException, SQLException, QueryConstructionException, MetaDataException {
        final BulkInsertObject bio = new BulkInsertObject();
        if (MetaDataUtil.getTableDefinitionByName(bulk.getTableName()) != null) {
            super.loadColumnDetails(bulk, bio, null);
        }
        else {
            super.loadColumnDetailsFromDB(bulk, bio, bulk.getConnection().getMetaData());
        }
        final String sql = BulkLoadStatementGenerator.getBulkSQL(bulk, bio, this.getSQLGenerator());
        final PreparedStatement ps = bulk.getConnection().prepareStatement(sql);
        bio.setBulkObject(ps);
        bio.setSQL(sql);
        return bio;
    }
    
    @Override
    public void closeBulkInsertObject(final BulkLoad bulk) throws SQLException, IOException {
        if (!bulk.timerHaltInvoked && bulk.counter > 0L) {
            ((PreparedStatement)bulk.getBulkInsertObject().getBulkObject()).executeBatch();
            bulk.counter = 0L;
        }
        ((PreparedStatement)bulk.getBulkInsertObject().getBulkObject()).close();
        this.postExecuteHandle(bulk);
    }
    
    @Override
    public void addBatch(final Object[] rowLvlByteValues, final BulkLoad bulk) throws IOException, SQLException {
        if (bulk.counter == 1L) {
            this.preExecuteHandle(bulk);
        }
        int sqlType = 0;
        try {
            int skipCount = 0;
            for (int index = 1; index <= rowLvlByteValues.length; ++index) {
                final String colDataType = bulk.getBulkInsertObject().getColTypeNames().get(index - 1);
                if (DataTypeUtil.isUDT(colDataType) && !DataTypeManager.getDataTypeDefinition(colDataType).getMeta().processInput()) {
                    ++skipCount;
                }
                else {
                    final Object objArrValue = rowLvlByteValues[index - 1];
                    if (DataTypeUtil.isUDT(colDataType)) {
                        final DataTypeDefinition dataDef = DataTypeManager.getDataTypeDefinition(colDataType);
                        if (dataDef.getDTAdapter("mssql") == null) {
                            throw new IllegalArgumentException("DTAdapter not defined for type :: " + colDataType);
                        }
                        sqlType = dataDef.getDTAdapter("mssql").getJavaSQLType();
                    }
                    else {
                        sqlType = bulk.getBulkInsertObject().getColTypes().get(index - 1);
                    }
                    this.setValue((PreparedStatement)bulk.getBulkInsertObject().getBulkObject(), index - skipCount, sqlType, objArrValue);
                }
            }
            ((PreparedStatement)bulk.getBulkInsertObject().getBulkObject()).addBatch();
            if (bulk.counter >= bulk.psBatchSize) {
                ((PreparedStatement)bulk.getBulkInsertObject().getBulkObject()).executeBatch();
                bulk.counter = 0L;
            }
        }
        catch (final SQLException e) {
            e.printStackTrace();
            throw e;
        }
        catch (final Exception e2) {
            e2.printStackTrace();
            bulk.getBulkInsertObject().setError(e2);
        }
    }
    
    @Override
    public void execBulk(final BulkLoad bulk) throws SQLException, IOException {
        bulk.getBulkInsertObject().setIsReadyToWrite(Boolean.TRUE);
    }
    
    private void preExecuteHandle(final BulkLoad bulk) throws SQLException {
        final List<String> tableNames = new ArrayList<String>();
        tableNames.add(bulk.getTableName());
        try {
            this.handlePreExecute(bulk.getConnection(), tableNames, bulk.getBulkInsertObject().getColNames());
            bulk.setCloseKey(Boolean.TRUE);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void postExecuteHandle(final BulkLoad bulk) throws SQLException {
        if (bulk.canCloseKey()) {
            final List<String> tableNames = new ArrayList<String>();
            tableNames.add(bulk.getTableName());
            try {
                this.handlePostExecute(bulk.getConnection(), tableNames, bulk.getBulkInsertObject().getColNames());
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            MssqlDBAdapter.out.log(Level.INFO, "Bulk Load Operation Completed!!!");
        }
    }
    
    @Override
    protected String getBackupHandlerClassName() {
        return MssqlBackupHandler.class.getName();
    }
    
    @Override
    protected String getRestoreHandlerClassName() {
        return MssqlRestoreHandler.class.getName();
    }
    
    @Override
    public SQLException handleSQLException(final SQLException sqle, final Connection conn, final boolean isWrite) throws SQLException {
        for (Throwable cause = sqle; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException && "08S01".equalsIgnoreCase(((SQLException)cause).getSQLState())) {
                final SQLException sqlcause = (SQLException)cause;
                ConsoleOut.println("SQLServer DB Server is not running in the specified port");
                ConsoleOut.println("Check that the hostname and port are correct and SQLserver is accepting connections");
            }
        }
        return super.handleSQLException(sqle, conn, isWrite);
    }
    
    @Override
    public DBInitializer getDBInitializer() {
        return this.dbInitializer;
    }
    
    @Override
    public String getDBSpecificExceptionSorterName() {
        return "com.adventnet.db.adapter.mssql.MssqlExceptionSorter";
    }
    
    protected void checkSanityOfBackupFile(final Connection conn, final String file) throws SQLException {
        final String query = "RESTORE VERIFYONLY\tFROM DISK=?";
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, file)) {
            MssqlDBAdapter.out.log(Level.FINE, ps.toString());
            this.execute(ps);
        }
    }
    
    protected void alterDatabase(final Connection conn, final String state) throws SQLException {
        final String query = "ALTER DATABASE " + this.dbInitializer.getDBName() + " SET " + state;
        MssqlDBAdapter.out.log(Level.FINE, query);
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, new Object[0])) {
            this.execute(ps);
        }
    }
    
    @Override
    public boolean hasPermissionForBackup(final Connection conn) throws Exception {
        return this.isDBBackupOperator(conn) || this.isOwner(conn) || this.isSysAdmin(conn);
    }
    
    @Override
    public boolean hasPermissionForRestore(final Connection conn) throws Exception {
        return this.isDBCreator(conn) || this.isSysAdmin(conn);
    }
    
    protected boolean hasPermissionForAlterDatabase(final Connection conn) throws SQLException, QueryConstructionException {
        return this.isOwner(conn) || this.isDBCreator(conn) || this.isSysAdmin(conn);
    }
    
    private boolean isOwner(final Connection conn) throws SQLException, QueryConstructionException {
        return this.isMemberRole(conn, "db_owner");
    }
    
    private boolean isDBBackupOperator(final Connection conn) throws SQLException, QueryConstructionException {
        return this.isMemberRole(conn, "db_backupoperator");
    }
    
    private boolean isDBCreator(final Connection conn) throws SQLException, QueryConstructionException {
        return this.isServerRole(conn, "dbcreator");
    }
    
    private boolean isSysAdmin(final Connection conn) throws SQLException, QueryConstructionException {
        return this.isServerRole(conn, "sysadmin");
    }
    
    private boolean isMemberRole(final Connection conn, final String role) throws SQLException, QueryConstructionException {
        boolean result = false;
        final String query = "IF IS_MEMBER ('" + role + "') = 1 SELECT 'true' ELSE SELECT 'false'";
        MssqlDBAdapter.out.log(Level.FINE, query);
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, new Object[0]);
             final ResultSetAdapter rs = this.executeQuery(ps)) {
            if (rs.next()) {
                result = rs.getBoolean(1);
            }
            else {
                MssqlDBAdapter.out.log(Level.WARNING, "No information is obtained for " + role);
            }
        }
        MssqlDBAdapter.out.log(Level.FINE, "Permission for " + role + " is : " + result);
        return result;
    }
    
    private boolean isServerRole(final Connection conn, final String role) throws SQLException, QueryConstructionException {
        boolean result = false;
        final String query = "IF IS_SRVROLEMEMBER ('" + role + "') = 1 SELECT 'true' ELSE SELECT 'false';";
        MssqlDBAdapter.out.log(Level.FINE, query);
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, new Object[0]);
             final ResultSetAdapter rs = this.executeQuery(ps)) {
            if (rs.next()) {
                result = rs.getBoolean(1);
            }
            else {
                MssqlDBAdapter.out.log(Level.WARNING, "No information is obtained for " + role);
            }
        }
        MssqlDBAdapter.out.log(Level.FINE, "Permission for " + role + " is : " + result);
        return result;
    }
    
    @Override
    public String getDBSystemProperty(final Connection conn, final String property) throws SQLException, QueryConstructionException {
        String result = null;
        final String query = "SELECT CONVERT(VARCHAR, SERVERPROPERTY('" + property + "'))";
        MssqlDBAdapter.out.log(Level.FINE, query);
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, new Object[0]);
             final ResultSetAdapter rs = this.executeQuery(ps)) {
            if (rs.next()) {
                result = rs.getString(1);
            }
            else {
                MssqlDBAdapter.out.log(Level.WARNING, "No information is obtained for " + property);
            }
        }
        return result;
    }
    
    protected String getMssqlDBProperty(final Connection conn, final String property) throws SQLException, QueryConstructionException {
        final String query = "SELECT CONVERT(VARCHAR, DATABASEPROPERTYEX('" + this.dbInitializer.getDBName() + "', '" + property + "'))";
        MssqlDBAdapter.out.log(Level.FINE, query);
        String result = null;
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, new Object[0]);
             final ResultSetAdapter rs = this.executeQuery(ps)) {
            rs.next();
            result = rs.getString(1);
        }
        return result;
    }
    
    @Override
    public long getSizeOfDB(final Connection conn) {
        String size = "-1";
        final String query = "EXEC sp_spaceused";
        MssqlDBAdapter.out.log(Level.FINE, query);
        try (final PreparedStatement ps = this.createPreparedStatement(conn, query, new Object[0]);
             final ResultSetAdapter rs = this.executeQuery(ps)) {
            if (rs.next()) {
                size = rs.getString(2);
            }
            else {
                MssqlDBAdapter.out.log(Level.WARNING, "The size of the database cannot be obtained");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return this.convertIntoBytes(size);
    }
    
    private long convertIntoBytes(final String sizeOfDB) {
        if (sizeOfDB.contains(" ")) {
            final String[] string = sizeOfDB.split(" ");
            double size = Double.parseDouble(string[0]);
            if (string[1].equalsIgnoreCase("TB")) {
                size *= Math.pow(1024.0, 4.0);
            }
            else if (string[1].equalsIgnoreCase("GB")) {
                size *= Math.pow(1024.0, 3.0);
            }
            else if (string[1].equalsIgnoreCase("MB")) {
                size *= Math.pow(1024.0, 2.0);
            }
            else if (string[1].equalsIgnoreCase("KB")) {
                size *= 1024.0;
            }
            return (long)size;
        }
        return Long.parseLong(sizeOfDB);
    }
    
    @Override
    public void logDatabaseDetails() {
        try {
            final Map<String, String> map = this.dbInitializer.getDBFilesLocation();
            final StringBuilder sb = new StringBuilder();
            sb.append("There are " + map.size() + " files. [");
            for (final String key : map.keySet()) {
                if (map.get(key).equalsIgnoreCase("ROWS")) {
                    sb.append("Data file : " + key);
                }
                else {
                    sb.append("Log file : " + key);
                }
                sb.append(",  ");
            }
            sb.append("]");
            MssqlDBAdapter.out.log(Level.INFO, "DB Version      :: " + this.dbInitializer.getVersion());
            MssqlDBAdapter.out.log(Level.INFO, "DB Architecture :: " + this.dbInitializer.getDBArchitecture());
            MssqlDBAdapter.out.log(Level.INFO, sb.toString());
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getDBSpecificSQLModifierName() throws Exception {
        return MssqlSQLModifier.class.getName();
    }
    
    @Override
    public String getDBType() {
        return "mssql";
    }
    
    @Override
    public String getDBSpecificAbortHandlerName() {
        return MssqlJtdsAbortHandler.class.getName();
    }
    
    @Override
    public boolean changePassword(final String userName, final String oldPassword, final String newPassword, final Connection c) throws SQLException {
        throw new SQLException("ChangePassword can be invoked for the database which has been bundled with the product!!");
    }
    
    public List<String> getDependentTableNamesFromTrigger(final Connection conn, final String triggerName) throws SQLException {
        final String sql = this.mssqlGen.getSQLToGetDependentTableNamesFromTrigger(triggerName);
        final List<String> tableNames = new ArrayList<String>();
        try (final Statement stmt = conn.createStatement();
             final ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableNames.add(rs.getString(1));
            }
        }
        return tableNames;
    }
    
    public void doBulkCopy(final Connection connection, final String tableName, final ResultSet resultSet) throws SQLException {
        final SQLServerBulkCopyOptions options = new SQLServerBulkCopyOptions();
        options.setBulkCopyTimeout(60000);
        options.setKeepIdentity(true);
        options.setBatchSize(50);
        this.doBulkCopy(connection, tableName, resultSet, options);
    }
    
    @Override
    protected PreparedStatement setPreparedStatement(final PreparedStatement statement, final Object... args) throws SQLException {
        for (int i = 0; i < args.length; ++i) {
            final int index = i + 1;
            if (args[i] instanceof Integer) {
                statement.setInt(index, (int)args[i]);
            }
            else if (args[i] instanceof Float) {
                statement.setFloat(index, (float)args[i]);
            }
            else if (args[i] instanceof Long) {
                statement.setLong(index, (long)args[i]);
            }
            else if (args[i] instanceof Boolean) {
                statement.setBoolean(index, (boolean)args[i]);
            }
            else if (args[i] instanceof Double) {
                statement.setDouble(index, (double)args[i]);
            }
            else if (args[i] instanceof Date) {
                statement.setDate(index, (Date)args[i]);
            }
            else if (args[i] instanceof Time) {
                statement.setString(index, args[i].toString());
            }
            else if (args[i] instanceof Timestamp) {
                statement.setTimestamp(index, (Timestamp)args[i]);
            }
            else if (args[i] instanceof InputStream) {
                statement.setBinaryStream(index, (InputStream)args[i]);
            }
            else {
                statement.setObject(index, args[i]);
            }
        }
        return statement;
    }
    
    public void doBulkCopy(final Connection connection, final String tableName, final ResultSet resultSet, final SQLServerBulkCopyOptions options) throws SQLException {
        final SQLServerConnection sqlServerConnection = (SQLServerConnection)((LogicalConnection)connection).getPhysicalConnection();
        try (final SQLServerBulkCopy bulkCopy = new SQLServerBulkCopy((Connection)sqlServerConnection)) {
            bulkCopy.setBulkCopyOptions(options);
            bulkCopy.setDestinationTableName(this.sqlGen.getDBSpecificTableName(tableName));
            bulkCopy.writeToServer(resultSet);
        }
    }
    
    static {
        out = Logger.getLogger(MssqlDBAdapter.class.getName());
        MssqlDBAdapter.supportsEncryption = false;
        MssqlDBAdapter.isTextDataTypeDeprecated = false;
    }
    
    public class Mssql2005DBAdapter
    {
        public void createTriggersForUK(final Statement stmt, final TableDefinition tabDefn, final List<UniqueKeyDefinition> uniqueKeys, final boolean validate) throws SQLException {
            for (final UniqueKeyDefinition ukDef : uniqueKeys) {
                this.createTriggersForUK(stmt, tabDefn, ukDef, validate);
            }
        }
        
        public void createTriggersForUK(final Statement stmt, final TableDefinition tabDefn, final UniqueKeyDefinition ukDef, final boolean validate) throws SQLException {
            final String tableName = tabDefn.getTableName();
            final String triggerName = tableName + "_" + ukDef.getName();
            final String dropTriggerSql = ((MssqlSQLGenerator)MssqlDBAdapter.this.sqlGen).getSQLForDeleteTrigger(triggerName);
            MssqlDBAdapter.out.log(Level.FINER, "Drop trigger SQL is {0}", dropTriggerSql);
            MssqlDBAdapter.this.execute(stmt, dropTriggerSql);
            final String triggerStr = ((MssqlSQLGenerator)MssqlDBAdapter.this.sqlGen).getSql2005Generator().getSQLCreateTriggerForUK(triggerName, tabDefn, ukDef, validate);
            if (triggerStr != null) {
                MssqlDBAdapter.out.log(Level.FINER, "Create trigger for unique Key of table {0} SQL constructed is {1}", new Object[] { tabDefn.getTableName(), triggerStr });
                MssqlDBAdapter.this.execute(stmt, triggerStr);
                this.createIndexUKDefinition(tabDefn, ukDef, stmt);
            }
        }
        
        private void createIndexUKDefinition(final TableDefinition tabDef, final UniqueKeyDefinition ukd, final Statement statement) throws SQLException {
            final IndexDefinition idx = new IndexDefinition();
            idx.setName(ukd.getName());
            final List columns = ukd.getColumns();
            for (int i = 0; i < columns.size(); ++i) {
                ColumnDefinition cd = tabDef.getColumnDefinitionByName(columns.get(i));
                if (cd == null) {
                    cd = new ColumnDefinition();
                    cd.setColumnName(columns.get(i));
                }
                idx.addIndexColumnDefinition(new IndexColumnDefinition(cd));
            }
            try {
                final String indexDefStr = MssqlDBAdapter.this.sqlGen.getSQLForIndex(tabDef.getTableName(), idx);
                MssqlDBAdapter.out.log(Level.FINER, "Creating a new index for {0} in table {1} : {2}", new Object[] { ukd.getName(), tabDef.getTableName(), indexDefStr });
                MssqlDBAdapter.this.execute(statement, indexDefStr);
            }
            catch (final QueryConstructionException qce) {
                MssqlDBAdapter.out.log(Level.WARNING, "Error during index creation for Unique Constraint {0} in table {1}", new Object[] { ukd.getName(), tabDef.getTableName() });
            }
        }
        
        public void dropTriggerForUK(final Statement stmt, final TableDefinition tabDefn, final List uniqueKeys) throws SQLException {
            for (int ukSize = uniqueKeys.size(), i = 0; i < ukSize; ++i) {
                final UniqueKeyDefinition ukd = uniqueKeys.get(i);
                final String tableName = tabDefn.getTableName();
                try {
                    final String dropIdxSql = MssqlDBAdapter.this.sqlGen.getSQLForDropIndex(tableName, ukd.getName());
                    MssqlDBAdapter.out.log(Level.FINER, "Dropping for {0} in table {1} : {2}", new Object[] { ukd.getName(), tableName, dropIdxSql });
                    MssqlDBAdapter.this.execute(stmt, dropIdxSql);
                }
                catch (final QueryConstructionException qce) {
                    MssqlDBAdapter.out.log(Level.WARNING, "Error during index creation for Unique Constraint {0} in table {1}", new Object[] { ukd.getName(), tableName });
                }
                final String triggerName = tableName + "_" + ukd.getName();
                final String dropTriggerSql = ((MssqlSQLGenerator)MssqlDBAdapter.this.sqlGen).getSQLForDeleteTrigger(triggerName);
                MssqlDBAdapter.out.log(Level.FINER, "Drop trigger SQL is {0}", dropTriggerSql);
                MssqlDBAdapter.this.execute(stmt, dropTriggerSql);
            }
        }
    }
}
