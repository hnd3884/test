package org.apache.tomcat.dbcp.dbcp2;

import java.sql.RowIdLifetime;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

public class DelegatingDatabaseMetaData implements DatabaseMetaData
{
    private final DatabaseMetaData databaseMetaData;
    private final DelegatingConnection<?> connection;
    
    public DelegatingDatabaseMetaData(final DelegatingConnection<?> connection, final DatabaseMetaData databaseMetaData) {
        this.connection = connection;
        this.databaseMetaData = databaseMetaData;
    }
    
    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        try {
            return this.databaseMetaData.allProceduresAreCallable();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        try {
            return this.databaseMetaData.allTablesAreSelectable();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        try {
            return this.databaseMetaData.autoCommitFailureClosesAllResultSets();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        try {
            return this.databaseMetaData.dataDefinitionCausesTransactionCommit();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        try {
            return this.databaseMetaData.dataDefinitionIgnoredInTransactions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean deletesAreDetected(final int type) throws SQLException {
        try {
            return this.databaseMetaData.deletesAreDetected(type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        try {
            return this.databaseMetaData.doesMaxRowSizeIncludeBlobs();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        this.connection.checkOpen();
        try {
            return Jdbc41Bridge.generatedKeyAlwaysReturned(this.databaseMetaData);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public ResultSet getAttributes(final String catalog, final String schemaPattern, final String typeNamePattern, final String attributeNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getAttributes(catalog, schemaPattern, typeNamePattern, attributeNamePattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getBestRowIdentifier(final String catalog, final String schema, final String table, final int scope, final boolean nullable) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getBestRowIdentifier(catalog, schema, table, scope, nullable));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getCatalogs() throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getCatalogs());
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getCatalogSeparator() throws SQLException {
        try {
            return this.databaseMetaData.getCatalogSeparator();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getCatalogTerm() throws SQLException {
        try {
            return this.databaseMetaData.getCatalogTerm();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getClientInfoProperties());
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getColumnPrivileges(final String catalog, final String schema, final String table, final String columnNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getColumnPrivileges(catalog, schema, table, columnNamePattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }
    
    @Override
    public ResultSet getCrossReference(final String parentCatalog, final String parentSchema, final String parentTable, final String foreignCatalog, final String foreignSchema, final String foreignTable) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getCrossReference(parentCatalog, parentSchema, parentTable, foreignCatalog, foreignSchema, foreignTable));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        try {
            return this.databaseMetaData.getDatabaseMajorVersion();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        try {
            return this.databaseMetaData.getDatabaseMinorVersion();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public String getDatabaseProductName() throws SQLException {
        try {
            return this.databaseMetaData.getDatabaseProductName();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getDatabaseProductVersion() throws SQLException {
        try {
            return this.databaseMetaData.getDatabaseProductVersion();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        try {
            return this.databaseMetaData.getDefaultTransactionIsolation();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    public DatabaseMetaData getDelegate() {
        return this.databaseMetaData;
    }
    
    @Override
    public int getDriverMajorVersion() {
        return this.databaseMetaData.getDriverMajorVersion();
    }
    
    @Override
    public int getDriverMinorVersion() {
        return this.databaseMetaData.getDriverMinorVersion();
    }
    
    @Override
    public String getDriverName() throws SQLException {
        try {
            return this.databaseMetaData.getDriverName();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getDriverVersion() throws SQLException {
        try {
            return this.databaseMetaData.getDriverVersion();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getExportedKeys(final String catalog, final String schema, final String table) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getExportedKeys(catalog, schema, table));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getExtraNameCharacters() throws SQLException {
        try {
            return this.databaseMetaData.getExtraNameCharacters();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getFunctionColumns(final String catalog, final String schemaPattern, final String functionNamePattern, final String columnNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getFunctions(final String catalog, final String schemaPattern, final String functionNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getFunctions(catalog, schemaPattern, functionNamePattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getIdentifierQuoteString() throws SQLException {
        try {
            return this.databaseMetaData.getIdentifierQuoteString();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getImportedKeys(final String catalog, final String schema, final String table) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getImportedKeys(catalog, schema, table));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getIndexInfo(final String catalog, final String schema, final String table, final boolean unique, final boolean approximate) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getIndexInfo(catalog, schema, table, unique, approximate));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    public DatabaseMetaData getInnermostDelegate() {
        DatabaseMetaData m = this.databaseMetaData;
        while (m instanceof DelegatingDatabaseMetaData) {
            m = ((DelegatingDatabaseMetaData)m).getDelegate();
            if (this == m) {
                return null;
            }
        }
        return m;
    }
    
    @Override
    public int getJDBCMajorVersion() throws SQLException {
        try {
            return this.databaseMetaData.getJDBCMajorVersion();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getJDBCMinorVersion() throws SQLException {
        try {
            return this.databaseMetaData.getJDBCMinorVersion();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxBinaryLiteralLength();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxCatalogNameLength();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxCharLiteralLength();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxColumnNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnNameLength();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnsInGroupBy();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnsInIndex();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnsInOrderBy();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnsInSelect();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxColumnsInTable() throws SQLException {
        try {
            return this.databaseMetaData.getMaxColumnsInTable();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxConnections() throws SQLException {
        try {
            return this.databaseMetaData.getMaxConnections();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxCursorNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxCursorNameLength();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxIndexLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxIndexLength();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxProcedureNameLength();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxRowSize() throws SQLException {
        try {
            return this.databaseMetaData.getMaxRowSize();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxSchemaNameLength();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxStatementLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxStatementLength();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxStatements() throws SQLException {
        try {
            return this.databaseMetaData.getMaxStatements();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxTableNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxTableNameLength();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxTablesInSelect() throws SQLException {
        try {
            return this.databaseMetaData.getMaxTablesInSelect();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getMaxUserNameLength() throws SQLException {
        try {
            return this.databaseMetaData.getMaxUserNameLength();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public String getNumericFunctions() throws SQLException {
        try {
            return this.databaseMetaData.getNumericFunctions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getPrimaryKeys(final String catalog, final String schema, final String table) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getPrimaryKeys(catalog, schema, table));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getProcedureColumns(final String catalog, final String schemaPattern, final String procedureNamePattern, final String columnNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getProcedures(final String catalog, final String schemaPattern, final String procedureNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getProcedures(catalog, schemaPattern, procedureNamePattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getProcedureTerm() throws SQLException {
        try {
            return this.databaseMetaData.getProcedureTerm();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getPseudoColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, Jdbc41Bridge.getPseudoColumns(this.databaseMetaData, catalog, schemaPattern, tableNamePattern, columnNamePattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        try {
            return this.databaseMetaData.getResultSetHoldability();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        try {
            return this.databaseMetaData.getRowIdLifetime();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getSchemas() throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getSchemas());
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getSchemas(final String catalog, final String schemaPattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getSchemas(catalog, schemaPattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getSchemaTerm() throws SQLException {
        try {
            return this.databaseMetaData.getSchemaTerm();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getSearchStringEscape() throws SQLException {
        try {
            return this.databaseMetaData.getSearchStringEscape();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getSQLKeywords() throws SQLException {
        try {
            return this.databaseMetaData.getSQLKeywords();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public int getSQLStateType() throws SQLException {
        try {
            return this.databaseMetaData.getSQLStateType();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public String getStringFunctions() throws SQLException {
        try {
            return this.databaseMetaData.getStringFunctions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getSuperTables(final String catalog, final String schemaPattern, final String tableNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getSuperTables(catalog, schemaPattern, tableNamePattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getSuperTypes(final String catalog, final String schemaPattern, final String typeNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getSuperTypes(catalog, schemaPattern, typeNamePattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getSystemFunctions() throws SQLException {
        try {
            return this.databaseMetaData.getSystemFunctions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getTablePrivileges(final String catalog, final String schemaPattern, final String tableNamePattern) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getTablePrivileges(catalog, schemaPattern, tableNamePattern));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getTables(final String catalog, final String schemaPattern, final String tableNamePattern, final String[] types) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getTables(catalog, schemaPattern, tableNamePattern, types));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getTableTypes() throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getTableTypes());
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getTimeDateFunctions() throws SQLException {
        try {
            return this.databaseMetaData.getTimeDateFunctions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getTypeInfo() throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getTypeInfo());
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getUDTs(final String catalog, final String schemaPattern, final String typeNamePattern, final int[] types) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getUDTs(catalog, schemaPattern, typeNamePattern, types));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getURL() throws SQLException {
        try {
            return this.databaseMetaData.getURL();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public String getUserName() throws SQLException {
        try {
            return this.databaseMetaData.getUserName();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ResultSet getVersionColumns(final String catalog, final String schema, final String table) throws SQLException {
        this.connection.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this.connection, this.databaseMetaData.getVersionColumns(catalog, schema, table));
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    protected void handleException(final SQLException e) throws SQLException {
        if (this.connection == null) {
            throw e;
        }
        this.connection.handleException(e);
    }
    
    @Override
    public boolean insertsAreDetected(final int type) throws SQLException {
        try {
            return this.databaseMetaData.insertsAreDetected(type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean isCatalogAtStart() throws SQLException {
        try {
            return this.databaseMetaData.isCatalogAtStart();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        try {
            return this.databaseMetaData.isReadOnly();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(this.getClass()) || iface.isAssignableFrom(this.databaseMetaData.getClass()) || this.databaseMetaData.isWrapperFor(iface);
    }
    
    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        try {
            return this.databaseMetaData.locatorsUpdateCopy();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        try {
            return this.databaseMetaData.nullPlusNonNullIsNull();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        try {
            return this.databaseMetaData.nullsAreSortedAtEnd();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        try {
            return this.databaseMetaData.nullsAreSortedAtStart();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        try {
            return this.databaseMetaData.nullsAreSortedHigh();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        try {
            return this.databaseMetaData.nullsAreSortedLow();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean othersDeletesAreVisible(final int type) throws SQLException {
        try {
            return this.databaseMetaData.othersDeletesAreVisible(type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean othersInsertsAreVisible(final int type) throws SQLException {
        try {
            return this.databaseMetaData.othersInsertsAreVisible(type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean othersUpdatesAreVisible(final int type) throws SQLException {
        try {
            return this.databaseMetaData.othersUpdatesAreVisible(type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean ownDeletesAreVisible(final int type) throws SQLException {
        try {
            return this.databaseMetaData.ownDeletesAreVisible(type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean ownInsertsAreVisible(final int type) throws SQLException {
        try {
            return this.databaseMetaData.ownInsertsAreVisible(type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean ownUpdatesAreVisible(final int type) throws SQLException {
        try {
            return this.databaseMetaData.ownUpdatesAreVisible(type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesLowerCaseIdentifiers();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesLowerCaseQuotedIdentifiers();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesMixedCaseIdentifiers();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesMixedCaseQuotedIdentifiers();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesUpperCaseIdentifiers();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.storesUpperCaseQuotedIdentifiers();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        try {
            return this.databaseMetaData.supportsAlterTableWithAddColumn();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        try {
            return this.databaseMetaData.supportsAlterTableWithDropColumn();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        try {
            return this.databaseMetaData.supportsANSI92EntryLevelSQL();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        try {
            return this.databaseMetaData.supportsANSI92FullSQL();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        try {
            return this.databaseMetaData.supportsANSI92IntermediateSQL();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        try {
            return this.databaseMetaData.supportsBatchUpdates();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        try {
            return this.databaseMetaData.supportsCatalogsInDataManipulation();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsCatalogsInIndexDefinitions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsCatalogsInPrivilegeDefinitions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        try {
            return this.databaseMetaData.supportsCatalogsInProcedureCalls();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsCatalogsInTableDefinitions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        try {
            return this.databaseMetaData.supportsColumnAliasing();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsConvert() throws SQLException {
        try {
            return this.databaseMetaData.supportsConvert();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsConvert(final int fromType, final int toType) throws SQLException {
        try {
            return this.databaseMetaData.supportsConvert(fromType, toType);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        try {
            return this.databaseMetaData.supportsCoreSQLGrammar();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        try {
            return this.databaseMetaData.supportsCorrelatedSubqueries();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        try {
            return this.databaseMetaData.supportsDataDefinitionAndDataManipulationTransactions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        try {
            return this.databaseMetaData.supportsDataManipulationTransactionsOnly();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        try {
            return this.databaseMetaData.supportsDifferentTableCorrelationNames();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        try {
            return this.databaseMetaData.supportsExpressionsInOrderBy();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        try {
            return this.databaseMetaData.supportsExtendedSQLGrammar();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        try {
            return this.databaseMetaData.supportsFullOuterJoins();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        try {
            return this.databaseMetaData.supportsGetGeneratedKeys();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsGroupBy() throws SQLException {
        try {
            return this.databaseMetaData.supportsGroupBy();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        try {
            return this.databaseMetaData.supportsGroupByBeyondSelect();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        try {
            return this.databaseMetaData.supportsGroupByUnrelated();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        try {
            return this.databaseMetaData.supportsIntegrityEnhancementFacility();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        try {
            return this.databaseMetaData.supportsLikeEscapeClause();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        try {
            return this.databaseMetaData.supportsLimitedOuterJoins();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        try {
            return this.databaseMetaData.supportsMinimumSQLGrammar();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.supportsMixedCaseIdentifiers();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        try {
            return this.databaseMetaData.supportsMixedCaseQuotedIdentifiers();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        try {
            return this.databaseMetaData.supportsMultipleOpenResults();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        try {
            return this.databaseMetaData.supportsMultipleResultSets();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        try {
            return this.databaseMetaData.supportsMultipleTransactions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsNamedParameters() throws SQLException {
        try {
            return this.databaseMetaData.supportsNamedParameters();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        try {
            return this.databaseMetaData.supportsNonNullableColumns();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        try {
            return this.databaseMetaData.supportsOpenCursorsAcrossCommit();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        try {
            return this.databaseMetaData.supportsOpenCursorsAcrossRollback();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        try {
            return this.databaseMetaData.supportsOpenStatementsAcrossCommit();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        try {
            return this.databaseMetaData.supportsOpenStatementsAcrossRollback();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        try {
            return this.databaseMetaData.supportsOrderByUnrelated();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsOuterJoins() throws SQLException {
        try {
            return this.databaseMetaData.supportsOuterJoins();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        try {
            return this.databaseMetaData.supportsPositionedDelete();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        try {
            return this.databaseMetaData.supportsPositionedUpdate();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsResultSetConcurrency(final int type, final int concurrency) throws SQLException {
        try {
            return this.databaseMetaData.supportsResultSetConcurrency(type, concurrency);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsResultSetHoldability(final int holdability) throws SQLException {
        try {
            return this.databaseMetaData.supportsResultSetHoldability(holdability);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsResultSetType(final int type) throws SQLException {
        try {
            return this.databaseMetaData.supportsResultSetType(type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsSavepoints() throws SQLException {
        try {
            return this.databaseMetaData.supportsSavepoints();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        try {
            return this.databaseMetaData.supportsSchemasInDataManipulation();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsSchemasInIndexDefinitions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsSchemasInPrivilegeDefinitions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        try {
            return this.databaseMetaData.supportsSchemasInProcedureCalls();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        try {
            return this.databaseMetaData.supportsSchemasInTableDefinitions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        try {
            return this.databaseMetaData.supportsSelectForUpdate();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsStatementPooling() throws SQLException {
        try {
            return this.databaseMetaData.supportsStatementPooling();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        try {
            return this.databaseMetaData.supportsStoredFunctionsUsingCallSyntax();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        try {
            return this.databaseMetaData.supportsStoredProcedures();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        try {
            return this.databaseMetaData.supportsSubqueriesInComparisons();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        try {
            return this.databaseMetaData.supportsSubqueriesInExists();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        try {
            return this.databaseMetaData.supportsSubqueriesInIns();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        try {
            return this.databaseMetaData.supportsSubqueriesInQuantifieds();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        try {
            return this.databaseMetaData.supportsTableCorrelationNames();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsTransactionIsolationLevel(final int level) throws SQLException {
        try {
            return this.databaseMetaData.supportsTransactionIsolationLevel(level);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsTransactions() throws SQLException {
        try {
            return this.databaseMetaData.supportsTransactions();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsUnion() throws SQLException {
        try {
            return this.databaseMetaData.supportsUnion();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean supportsUnionAll() throws SQLException {
        try {
            return this.databaseMetaData.supportsUnionAll();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return iface.cast(this);
        }
        if (iface.isAssignableFrom(this.databaseMetaData.getClass())) {
            return iface.cast(this.databaseMetaData);
        }
        return this.databaseMetaData.unwrap(iface);
    }
    
    @Override
    public boolean updatesAreDetected(final int type) throws SQLException {
        try {
            return this.databaseMetaData.updatesAreDetected(type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        try {
            return this.databaseMetaData.usesLocalFilePerTable();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean usesLocalFiles() throws SQLException {
        try {
            return this.databaseMetaData.usesLocalFiles();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
}
