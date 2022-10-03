package com.microsoft.sqlserver.jdbc;

import java.util.HashMap;
import java.sql.RowIdLifetime;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.util.Properties;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.SQLTimeoutException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.LinkedHashMap;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.io.Serializable;
import java.sql.DatabaseMetaData;

public final class SQLServerDatabaseMetaData implements DatabaseMetaData, Serializable
{
    private static final long serialVersionUID = -116977606028371577L;
    private SQLServerConnection connection;
    static final String urlprefix = "jdbc:sqlserver://";
    private static final Logger logger;
    private static final Logger loggerExternal;
    private static final AtomicInteger baseID;
    private final String traceID;
    static final int MAXLOBSIZE = Integer.MAX_VALUE;
    static final int uniqueidentifierSize = 36;
    EnumMap<CallableHandles, HandleAssociation> handleMap;
    private static final String ASC_OR_DESC = "ASC_OR_DESC";
    private static final String ATTR_NAME = "ATTR_NAME";
    private static final String ATTR_TYPE_NAME = "ATTR_TYPE_NAME";
    private static final String ATTR_SIZE = "ATTR_SIZE";
    private static final String ATTR_DEF = "ATTR_DEF";
    private static final String BASE_TYPE = "BASE_TYPE";
    private static final String BUFFER_LENGTH = "BUFFER_LENGTH";
    private static final String CARDINALITY = "CARDINALITY";
    private static final String CHAR_OCTET_LENGTH = "CHAR_OCTET_LENGTH";
    private static final String CLASS_NAME = "CLASS_NAME";
    private static final String COLUMN_DEF = "COLUMN_DEF";
    private static final String COLUMN_NAME = "COLUMN_NAME";
    private static final String COLUMN_SIZE = "COLUMN_SIZE";
    private static final String COLUMN_TYPE = "COLUMN_TYPE";
    private static final String DATA_TYPE = "DATA_TYPE";
    private static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
    private static final String DEFERRABILITY = "DEFERRABILITY";
    private static final String DELETE_RULE = "DELETE_RULE";
    private static final String FILTER_CONDITION = "FILTER_CONDITION";
    private static final String FK_NAME = "FK_NAME";
    private static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
    private static final String FKTABLE_CAT = "FKTABLE_CAT";
    private static final String FKTABLE_NAME = "FKTABLE_NAME";
    private static final String FKTABLE_SCHEM = "FKTABLE_SCHEM";
    private static final String GRANTEE = "GRANTEE";
    private static final String GRANTOR = "GRANTOR";
    private static final String INDEX_NAME = "INDEX_NAME";
    private static final String INDEX_QUALIFIER = "INDEX_QUALIFIER";
    private static final String IS_GRANTABLE = "IS_GRANTABLE";
    private static final String IS_NULLABLE = "IS_NULLABLE";
    private static final String KEY_SEQ = "KEY_SEQ";
    private static final String LENGTH = "LENGTH";
    private static final String NON_UNIQUE = "NON_UNIQUE";
    private static final String NULLABLE = "NULLABLE";
    private static final String NUM_INPUT_PARAMS = "NUM_INPUT_PARAMS";
    private static final String NUM_OUTPUT_PARAMS = "NUM_OUTPUT_PARAMS";
    private static final String NUM_PREC_RADIX = "NUM_PREC_RADIX";
    private static final String NUM_RESULT_SETS = "NUM_RESULT_SETS";
    private static final String ORDINAL_POSITION = "ORDINAL_POSITION";
    private static final String PAGES = "PAGES";
    private static final String PK_NAME = "PK_NAME";
    private static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
    private static final String PKTABLE_CAT = "PKTABLE_CAT";
    private static final String PKTABLE_NAME = "PKTABLE_NAME";
    private static final String PKTABLE_SCHEM = "PKTABLE_SCHEM";
    private static final String PRECISION = "PRECISION";
    private static final String PRIVILEGE = "PRIVILEGE";
    private static final String PROCEDURE_CAT = "PROCEDURE_CAT";
    private static final String PROCEDURE_NAME = "PROCEDURE_NAME";
    private static final String PROCEDURE_SCHEM = "PROCEDURE_SCHEM";
    private static final String PROCEDURE_TYPE = "PROCEDURE_TYPE";
    private static final String PSEUDO_COLUMN = "PSEUDO_COLUMN";
    private static final String RADIX = "RADIX";
    private static final String REMARKS = "REMARKS";
    private static final String SCALE = "SCALE";
    private static final String SCOPE = "SCOPE";
    private static final String SCOPE_CATALOG = "SCOPE_CATALOG";
    private static final String SCOPE_SCHEMA = "SCOPE_SCHEMA";
    private static final String SCOPE_TABLE = "SCOPE_TABLE";
    private static final String SOURCE_DATA_TYPE = "SOURCE_DATA_TYPE";
    private static final String SQL_DATA_TYPE = "SQL_DATA_TYPE";
    private static final String SQL_DATETIME_SUB = "SQL_DATETIME_SUB";
    private static final String SS_DATA_TYPE = "SS_DATA_TYPE";
    private static final String SUPERTABLE_NAME = "SUPERTABLE_NAME";
    private static final String SUPERTYPE_CAT = "SUPERTYPE_CAT";
    private static final String SUPERTYPE_NAME = "SUPERTYPE_NAME";
    private static final String SUPERTYPE_SCHEM = "SUPERTYPE_SCHEM";
    private static final String TABLE_CAT = "TABLE_CAT";
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String TABLE_SCHEM = "TABLE_SCHEM";
    private static final String TABLE_TYPE = "TABLE_TYPE";
    private static final String TYPE = "TYPE";
    private static final String TYPE_CAT = "TYPE_CAT";
    private static final String TYPE_NAME = "TYPE_NAME";
    private static final String TYPE_SCHEM = "TYPE_SCHEM";
    private static final String UPDATE_RULE = "UPDATE_RULE";
    private static final String FUNCTION_CAT = "FUNCTION_CAT";
    private static final String FUNCTION_NAME = "FUNCTION_NAME";
    private static final String FUNCTION_SCHEM = "FUNCTION_SCHEM";
    private static final String FUNCTION_TYPE = "FUNCTION_TYPE";
    private static final String SS_IS_SPARSE = "SS_IS_SPARSE";
    private static final String SS_IS_COLUMN_SET = "SS_IS_COLUMN_SET";
    private static final String IS_GENERATEDCOLUMN = "IS_GENERATEDCOLUMN";
    private static final String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";
    private static final String SQL_KEYWORDS;
    private static LinkedHashMap<Integer, String> getColumnsDWColumns;
    private static LinkedHashMap<Integer, String> getImportedKeysDWColumns;
    private static final String[] getColumnPrivilegesColumnNames;
    private static final String[] getTablesColumnNames;
    static final char LEFT_BRACKET = '[';
    static final char RIGHT_BRACKET = ']';
    static final char ESCAPE = '\\';
    static final char PERCENT = '%';
    static final char UNDERSCORE = '_';
    static final char[] DOUBLE_RIGHT_BRACKET;
    private static final String[] getFunctionsColumnNames;
    private static final String[] getFunctionsColumnsColumnNames;
    private static final String[] getBestRowIdentifierColumnNames;
    private static final String[] getIndexInfoColumnNames;
    private static final String[] getPrimaryKeysColumnNames;
    private static final String[] getProcedureColumnsColumnNames;
    private static final String[] getProceduresColumnNames;
    private static final String[] getTablePrivilegesColumnNames;
    private static final String[] getVersionColumnsColumnNames;
    
    private static int nextInstanceID() {
        return SQLServerDatabaseMetaData.baseID.incrementAndGet();
    }
    
    @Override
    public final String toString() {
        return this.traceID;
    }
    
    public SQLServerDatabaseMetaData(final SQLServerConnection con) {
        this.handleMap = new EnumMap<CallableHandles, HandleAssociation>(CallableHandles.class);
        this.traceID = " SQLServerDatabaseMetaData:" + nextInstanceID();
        this.connection = con;
        if (SQLServerDatabaseMetaData.logger.isLoggable(Level.FINE)) {
            SQLServerDatabaseMetaData.logger.fine(this.toString() + " created by (" + this.connection.toString() + ")");
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        final boolean f = iface.isInstance(this);
        return f;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        T t;
        try {
            t = iface.cast(this);
        }
        catch (final ClassCastException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        return t;
    }
    
    private void checkClosed() throws SQLServerException {
        if (this.connection.isClosed()) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_connectionIsClosed"), "08003", false);
        }
    }
    
    private SQLServerResultSet getResultSetFromInternalQueries(final String catalog, final String query) throws SQLException, SQLTimeoutException {
        this.checkClosed();
        String orgCat = null;
        orgCat = this.switchCatalogs(catalog);
        SQLServerResultSet rs = null;
        try {
            final SQLServerStatement statement = (SQLServerStatement)this.connection.createStatement();
            statement.closeOnCompletion();
            rs = statement.executeQueryInternal(query);
        }
        finally {
            if (null != orgCat) {
                this.connection.setCatalog(orgCat);
            }
        }
        return rs;
    }
    
    private CallableStatement getCallableStatementHandle(final CallableHandles request, final String catalog) throws SQLServerException {
        CallableStatement CS = null;
        HandleAssociation hassoc = this.handleMap.get(request);
        try {
            if (null == hassoc) {
                CS = request.prepare(this.connection);
                hassoc = new HandleAssociation();
                hassoc.addToMap(catalog, CS);
            }
            else {
                CS = hassoc.getMappedStatement(catalog);
                if (null == CS || CS.isClosed()) {
                    CS = request.prepare(this.connection);
                    hassoc.addToMap(catalog, CS);
                }
            }
            this.handleMap.put(request, hassoc);
        }
        catch (final SQLException e) {
            SQLServerException.makeFromDriverError(this.connection, CS, e.toString(), null, false);
        }
        return CS;
    }
    
    private SQLServerResultSet getResultSetFromStoredProc(final String catalog, final CallableHandles procedure, final String[] arguments) throws SQLServerException, SQLTimeoutException {
        this.checkClosed();
        assert null != arguments;
        String orgCat = null;
        orgCat = this.switchCatalogs(catalog);
        SQLServerResultSet rs = null;
        try {
            final SQLServerCallableStatement call = (SQLServerCallableStatement)this.getCallableStatementHandle(procedure, catalog);
            for (int i = 1; i <= arguments.length; ++i) {
                call.setString(i, arguments[i - 1]);
            }
            rs = (SQLServerResultSet)call.executeQueryInternal();
        }
        finally {
            if (null != orgCat) {
                this.connection.setCatalog(orgCat);
            }
        }
        return rs;
    }
    
    private SQLServerResultSet getResultSetWithProvidedColumnNames(final String catalog, final CallableHandles procedure, final String[] arguments, final String[] columnNames) throws SQLServerException, SQLTimeoutException {
        final SQLServerResultSet rs = this.getResultSetFromStoredProc(catalog, procedure, arguments);
        for (int i = 0; i < columnNames.length; ++i) {
            rs.setColumnName(1 + i, columnNames[i]);
        }
        return rs;
    }
    
    private String switchCatalogs(final String catalog) throws SQLServerException {
        if (null == catalog) {
            return null;
        }
        String sCurr = null;
        sCurr = this.connection.getCatalog().trim();
        final String sNew = catalog.trim();
        if (sCurr.equals(sNew)) {
            return null;
        }
        this.connection.setCatalog(sNew);
        if (null == sCurr || sCurr.length() == 0) {
            return null;
        }
        return sCurr;
    }
    
    @Override
    public boolean allProceduresAreCallable() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean allTablesAreSelectable() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public long getMaxLogicalLobSize() throws SQLException {
        this.checkClosed();
        return 2147483647L;
    }
    
    @Override
    public boolean supportsRefCursors() throws SQLException {
        this.checkClosed();
        return false;
    }
    
    public boolean supportsSharding() throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC43();
        this.checkClosed();
        return false;
    }
    
    @Override
    public ResultSet getCatalogs() throws SQLException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final String s = "SELECT name AS TABLE_CAT FROM sys.databases order by name";
        return this.getResultSetFromInternalQueries(null, s);
    }
    
    @Override
    public String getCatalogSeparator() throws SQLServerException {
        this.checkClosed();
        return ".";
    }
    
    @Override
    public String getCatalogTerm() throws SQLServerException {
        this.checkClosed();
        return "database";
    }
    
    @Override
    public ResultSet getColumnPrivileges(final String catalog, final String schema, final String table, String col) throws SQLServerException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        col = EscapeIDName(col);
        final String[] arguments = { table, schema, catalog, col };
        return this.getResultSetWithProvidedColumnNames(catalog, CallableHandles.SP_COLUMN_PRIVILEGES, arguments, SQLServerDatabaseMetaData.getColumnPrivilegesColumnNames);
    }
    
    @Override
    public ResultSet getTables(final String catalog, String schema, String table, final String[] types) throws SQLServerException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        table = EscapeIDName(table);
        schema = EscapeIDName(schema);
        final String[] arguments = { table, schema, catalog, null };
        if (null != types) {
            final StringBuilder tableTypes = new StringBuilder("'");
            for (int i = 0; i < types.length; ++i) {
                if (i > 0) {
                    tableTypes.append(",");
                }
                tableTypes.append("''").append(types[i]).append("''");
            }
            tableTypes.append("'");
            arguments[3] = tableTypes.toString();
        }
        return this.getResultSetWithProvidedColumnNames(catalog, CallableHandles.SP_TABLES, arguments, SQLServerDatabaseMetaData.getTablesColumnNames);
    }
    
    private static String EscapeIDName(final String inID) throws SQLServerException {
        if (null == inID) {
            return inID;
        }
        final StringBuilder outID = new StringBuilder(inID.length() + 2);
        for (int i = 0; i < inID.length(); ++i) {
            char ch = inID.charAt(i);
            if ('\\' == ch && ++i < inID.length()) {
                ch = inID.charAt(i);
                switch (ch) {
                    case '%':
                    case '[':
                    case '_': {
                        outID.append('[');
                        outID.append(ch);
                        outID.append(']');
                        break;
                    }
                    case '\\':
                    case ']': {
                        outID.append(ch);
                        break;
                    }
                    default: {
                        outID.append('\\');
                        outID.append(ch);
                        break;
                    }
                }
            }
            else {
                outID.append(ch);
            }
        }
        return outID.toString();
    }
    
    @Override
    public ResultSet getColumns(final String catalog, final String schema, final String table, final String col) throws SQLException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final String originalCatalog = this.switchCatalogs(catalog);
        if (!this.connection.isAzureDW()) {
            final String spColumnsSql = "DECLARE @mssqljdbc_temp_sp_columns_result TABLE(TABLE_QUALIFIER SYSNAME, TABLE_OWNER SYSNAME,TABLE_NAME SYSNAME, COLUMN_NAME SYSNAME, DATA_TYPE SMALLINT, TYPE_NAME SYSNAME, PRECISION INT,LENGTH INT, SCALE SMALLINT, RADIX SMALLINT, NULLABLE SMALLINT, REMARKS VARCHAR(254), COLUMN_DEF NVARCHAR(4000),SQL_DATA_TYPE SMALLINT, SQL_DATETIME_SUB SMALLINT, CHAR_OCTET_LENGTH INT, ORDINAL_POSITION INT,IS_NULLABLE VARCHAR(254), SS_IS_SPARSE SMALLINT, SS_IS_COLUMN_SET SMALLINT, SS_IS_COMPUTED SMALLINT,SS_IS_IDENTITY SMALLINT, SS_UDT_CATALOG_NAME NVARCHAR(128), SS_UDT_SCHEMA_NAME NVARCHAR(128),SS_UDT_ASSEMBLY_TYPE_NAME NVARCHAR(max), SS_XML_SCHEMACOLLECTION_CATALOG_NAME NVARCHAR(128),SS_XML_SCHEMACOLLECTION_SCHEMA_NAME NVARCHAR(128), SS_XML_SCHEMACOLLECTION_NAME NVARCHAR(128),SS_DATA_TYPE TINYINT);INSERT INTO @mssqljdbc_temp_sp_columns_result EXEC sp_columns_100 ?,?,?,?,?,?;SELECT TABLE_QUALIFIER AS TABLE_CAT, TABLE_OWNER AS TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE,TYPE_NAME, PRECISION AS COLUMN_SIZE, LENGTH AS BUFFER_LENGTH, SCALE AS DECIMAL_DIGITS, RADIX AS NUM_PREC_RADIX,NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE,NULL AS SCOPE_CATALOG, NULL AS SCOPE_SCHEMA, NULL AS SCOPE_TABLE, SS_DATA_TYPE AS SOURCE_DATA_TYPE,CASE SS_IS_IDENTITY WHEN 0 THEN 'NO' WHEN 1 THEN 'YES' WHEN '' THEN '' END AS IS_AUTOINCREMENT,CASE SS_IS_COMPUTED WHEN 0 THEN 'NO' WHEN 1 THEN 'YES' WHEN '' THEN '' END AS IS_GENERATEDCOLUMN, SS_IS_SPARSE, SS_IS_COLUMN_SET, SS_UDT_CATALOG_NAME, SS_UDT_SCHEMA_NAME, SS_UDT_ASSEMBLY_TYPE_NAME,SS_XML_SCHEMACOLLECTION_CATALOG_NAME, SS_XML_SCHEMACOLLECTION_SCHEMA_NAME, SS_XML_SCHEMACOLLECTION_NAME FROM @mssqljdbc_temp_sp_columns_result;";
            SQLServerResultSet rs = null;
            final PreparedStatement pstmt = this.connection.prepareStatement(spColumnsSql);
            pstmt.closeOnCompletion();
            try {
                pstmt.setString(1, (null != table && !table.isEmpty()) ? EscapeIDName(table) : "%");
                pstmt.setString(2, (null != schema && !schema.isEmpty()) ? EscapeIDName(schema) : "%");
                pstmt.setString(3, (null != catalog && !catalog.isEmpty()) ? catalog : this.connection.getCatalog());
                pstmt.setString(4, (null != col && !col.isEmpty()) ? EscapeIDName(col) : "%");
                pstmt.setInt(5, 2);
                pstmt.setInt(6, 3);
                rs = (SQLServerResultSet)pstmt.executeQuery();
                rs.getColumn(5).setFilter(new DataTypeFilter());
                rs.getColumn(7).setFilter(new ZeroFixupFilter());
                rs.getColumn(8).setFilter(new ZeroFixupFilter());
                rs.getColumn(16).setFilter(new ZeroFixupFilter());
            }
            catch (final SQLException e) {
                if (null != pstmt) {
                    try {
                        pstmt.close();
                    }
                    catch (final SQLServerException ignore) {
                        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER)) {
                            SQLServerDatabaseMetaData.loggerExternal.finer("getColumns() threw an exception when attempting to close PreparedStatement");
                        }
                    }
                }
                throw e;
            }
            finally {
                if (null != originalCatalog) {
                    this.connection.setCatalog(originalCatalog);
                }
            }
            return rs;
        }
        synchronized (SQLServerDatabaseMetaData.class) {
            if (null == SQLServerDatabaseMetaData.getColumnsDWColumns) {
                (SQLServerDatabaseMetaData.getColumnsDWColumns = new LinkedHashMap<Integer, String>()).put(1, "TABLE_CAT");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(2, "TABLE_SCHEM");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(3, "TABLE_NAME");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(4, "COLUMN_NAME");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(5, "DATA_TYPE");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(6, "TYPE_NAME");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(7, "COLUMN_SIZE");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(8, "BUFFER_LENGTH");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(9, "DECIMAL_DIGITS");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(10, "NUM_PREC_RADIX");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(11, "NULLABLE");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(12, "REMARKS");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(13, "COLUMN_DEF");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(14, "SQL_DATA_TYPE");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(15, "SQL_DATETIME_SUB");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(16, "CHAR_OCTET_LENGTH");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(17, "ORDINAL_POSITION");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(18, "IS_NULLABLE");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(-1, "SCOPE_CATALOG");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(-2, "SCOPE_SCHEMA");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(-3, "SCOPE_TABLE");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(29, "SOURCE_DATA_TYPE");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(22, "IS_AUTOINCREMENT");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(21, "IS_GENERATEDCOLUMN");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(19, "SS_IS_SPARSE");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(20, "SS_IS_COLUMN_SET");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(23, "SS_UDT_CATALOG_NAME");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(24, "SS_UDT_SCHEMA_NAME");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(25, "SS_UDT_ASSEMBLY_TYPE_NAME");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(26, "SS_XML_SCHEMACOLLECTION_CATALOG_NAME");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(27, "SS_XML_SCHEMACOLLECTION_SCHEMA_NAME");
                SQLServerDatabaseMetaData.getColumnsDWColumns.put(28, "SS_XML_SCHEMACOLLECTION_NAME");
            }
        }
        try (final PreparedStatement storedProcPstmt = this.connection.prepareStatement("EXEC sp_columns_100 ?,?,?,?,?,?;")) {
            storedProcPstmt.setString(1, (null != table && !table.isEmpty()) ? EscapeIDName(table) : "%");
            storedProcPstmt.setString(2, (null != schema && !schema.isEmpty()) ? EscapeIDName(schema) : "%");
            storedProcPstmt.setString(3, (null != catalog && !catalog.isEmpty()) ? catalog : this.connection.getCatalog());
            storedProcPstmt.setString(4, (null != col && !col.isEmpty()) ? EscapeIDName(col) : "%");
            storedProcPstmt.setInt(5, 2);
            storedProcPstmt.setInt(6, 3);
            SQLServerResultSet userRs = null;
            PreparedStatement resultPstmt = null;
            try (final ResultSet rs2 = storedProcPstmt.executeQuery()) {
                final StringBuilder azureDwSelectBuilder = new StringBuilder();
                boolean isFirstRow = true;
                while (rs2.next()) {
                    if (!isFirstRow) {
                        azureDwSelectBuilder.append(" UNION ALL ");
                    }
                    azureDwSelectBuilder.append(this.generateAzureDWSelect(rs2, SQLServerDatabaseMetaData.getColumnsDWColumns));
                    isFirstRow = false;
                }
                if (0 == azureDwSelectBuilder.length()) {
                    azureDwSelectBuilder.append(this.generateAzureDWEmptyRS(SQLServerDatabaseMetaData.getColumnsDWColumns));
                }
                resultPstmt = this.connection.prepareStatement(azureDwSelectBuilder.toString());
                userRs = (SQLServerResultSet)resultPstmt.executeQuery();
                resultPstmt.closeOnCompletion();
                userRs.getColumn(5).setFilter(new DataTypeFilter());
                userRs.getColumn(7).setFilter(new ZeroFixupFilter());
                userRs.getColumn(8).setFilter(new ZeroFixupFilter());
                userRs.getColumn(16).setFilter(new ZeroFixupFilter());
                userRs.getColumn(23).setFilter(new IntColumnIdentityFilter());
                userRs.getColumn(24).setFilter(new IntColumnIdentityFilter());
            }
            catch (final SQLException e) {
                if (null != resultPstmt) {
                    try {
                        resultPstmt.close();
                    }
                    catch (final SQLServerException ignore) {
                        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER)) {
                            SQLServerDatabaseMetaData.loggerExternal.finer("getColumns() threw an exception when attempting to close PreparedStatement");
                        }
                    }
                }
                throw e;
            }
            return userRs;
        }
    }
    
    private String generateAzureDWSelect(final ResultSet rs, final Map<Integer, String> columns) throws SQLException {
        final StringBuilder sb = new StringBuilder("SELECT ");
        for (final Map.Entry<Integer, String> p : columns.entrySet()) {
            if (p.getKey() < 0) {
                sb.append("NULL");
            }
            else {
                final Object o = rs.getObject(p.getKey());
                if (null == o) {
                    sb.append("NULL");
                }
                else if (o instanceof Number) {
                    sb.append(o.toString());
                }
                else {
                    sb.append("'").append(Util.escapeSingleQuotes(o.toString())).append("'");
                }
            }
            sb.append(" AS ").append(p.getValue()).append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    
    private String generateAzureDWEmptyRS(final Map<Integer, String> columns) throws SQLException {
        final StringBuilder sb = new StringBuilder("SELECT TOP 0 ");
        for (final Map.Entry<Integer, String> p : columns.entrySet()) {
            sb.append("NULL AS ").append(p.getValue()).append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    
    @Override
    public ResultSet getFunctions(final String catalog, final String schemaPattern, final String functionNamePattern) throws SQLException {
        this.checkClosed();
        if (null != catalog && catalog.length() == 0) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
            final Object[] msgArgs = { "catalog" };
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
        }
        final String[] arguments = { EscapeIDName(functionNamePattern), EscapeIDName(schemaPattern), catalog };
        return this.getResultSetWithProvidedColumnNames(catalog, CallableHandles.SP_STORED_PROCEDURES, arguments, SQLServerDatabaseMetaData.getFunctionsColumnNames);
    }
    
    @Override
    public ResultSet getFunctionColumns(final String catalog, final String schemaPattern, final String functionNamePattern, final String columnNamePattern) throws SQLException {
        this.checkClosed();
        if (null != catalog && catalog.length() == 0) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
            final Object[] msgArgs = { "catalog" };
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
        }
        final String[] arguments = { EscapeIDName(functionNamePattern), EscapeIDName(schemaPattern), catalog, EscapeIDName(columnNamePattern), "3" };
        final SQLServerResultSet rs = this.getResultSetWithProvidedColumnNames(catalog, CallableHandles.SP_SPROC_COLUMNS, arguments, SQLServerDatabaseMetaData.getFunctionsColumnsColumnNames);
        rs.getColumn(6).setFilter(new DataTypeFilter());
        if (this.connection.isKatmaiOrLater()) {
            rs.getColumn(8).setFilter(new ZeroFixupFilter());
            rs.getColumn(9).setFilter(new ZeroFixupFilter());
            rs.getColumn(17).setFilter(new ZeroFixupFilter());
        }
        return rs;
    }
    
    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        this.checkClosed();
        return this.getResultSetFromInternalQueries(null, "SELECT cast(NULL as char(1)) as NAME, cast(0 as int) as MAX_LEN, cast(NULL as char(1)) as DEFAULT_VALUE, cast(NULL as char(1)) as DESCRIPTION  where 0 = 1");
    }
    
    @Override
    public ResultSet getBestRowIdentifier(final String catalog, final String schema, final String table, final int scope, final boolean nullable) throws SQLServerException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final String[] arguments = { table, schema, catalog, "R", null, null, null };
        if (0 == scope) {
            arguments[4] = "C";
        }
        else {
            arguments[4] = "T";
        }
        if (nullable) {
            arguments[5] = "U";
        }
        else {
            arguments[5] = "O";
        }
        arguments[6] = "3";
        final SQLServerResultSet rs = this.getResultSetWithProvidedColumnNames(catalog, CallableHandles.SP_SPECIAL_COLUMNS, arguments, SQLServerDatabaseMetaData.getBestRowIdentifierColumnNames);
        rs.getColumn(3).setFilter(new DataTypeFilter());
        return rs;
    }
    
    @Override
    public ResultSet getCrossReference(final String cat1, final String schem1, final String tab1, final String cat2, final String schem2, final String tab2) throws SQLException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final String[] arguments = { tab1, schem1, cat1, tab2, schem2, cat2 };
        return this.executeSPFkeys(arguments);
    }
    
    @Override
    public String getDatabaseProductName() throws SQLServerException {
        this.checkClosed();
        return "Microsoft SQL Server";
    }
    
    @Override
    public String getDatabaseProductVersion() throws SQLServerException {
        this.checkClosed();
        return this.connection.sqlServerVersion;
    }
    
    @Override
    public int getDefaultTransactionIsolation() throws SQLServerException {
        this.checkClosed();
        return 2;
    }
    
    @Override
    public int getDriverMajorVersion() {
        return 8;
    }
    
    @Override
    public int getDriverMinorVersion() {
        return 2;
    }
    
    @Override
    public String getDriverName() throws SQLServerException {
        this.checkClosed();
        return "Microsoft JDBC Driver 8.2 for SQL Server";
    }
    
    @Override
    public String getDriverVersion() throws SQLServerException {
        final int n = this.getDriverMinorVersion();
        String s = this.getDriverMajorVersion() + ".";
        s = s + "" + n;
        s += ".";
        s += 2;
        s += ".";
        s += 0;
        return s;
    }
    
    @Override
    public ResultSet getExportedKeys(final String cat, final String schema, final String table) throws SQLException, SQLTimeoutException {
        return this.getCrossReference(cat, schema, table, null, null, null);
    }
    
    @Override
    public String getExtraNameCharacters() throws SQLServerException {
        this.checkClosed();
        return "$#@";
    }
    
    @Override
    public String getIdentifierQuoteString() throws SQLServerException {
        this.checkClosed();
        return "\"";
    }
    
    @Override
    public ResultSet getImportedKeys(final String cat, final String schema, final String table) throws SQLException, SQLTimeoutException {
        return this.getCrossReference(null, null, null, cat, schema, table);
    }
    
    private ResultSet executeSPFkeys(final String[] procParams) throws SQLException, SQLTimeoutException {
        if (!this.connection.isAzureDW()) {
            final String tempTableName = "@jdbc_temp_fkeys_result";
            final String sql = "DECLARE " + tempTableName + " table (PKTABLE_QUALIFIER sysname, PKTABLE_OWNER sysname, PKTABLE_NAME sysname, PKCOLUMN_NAME sysname, FKTABLE_QUALIFIER sysname, FKTABLE_OWNER sysname, FKTABLE_NAME sysname, FKCOLUMN_NAME sysname, KEY_SEQ smallint, UPDATE_RULE smallint, DELETE_RULE smallint, FK_NAME sysname, PK_NAME sysname, DEFERRABILITY smallint);INSERT INTO " + tempTableName + " EXEC sp_fkeys ?,?,?,?,?,?;SELECT  t.PKTABLE_QUALIFIER AS PKTABLE_CAT, t.PKTABLE_OWNER AS PKTABLE_SCHEM, t.PKTABLE_NAME, t.PKCOLUMN_NAME, t.FKTABLE_QUALIFIER AS FKTABLE_CAT, t.FKTABLE_OWNER AS FKTABLE_SCHEM, t.FKTABLE_NAME, t.FKCOLUMN_NAME, t.KEY_SEQ, CASE s.update_referential_action WHEN 1 THEN 0 WHEN 0 THEN 3 WHEN 2 THEN 2 WHEN 3 THEN 4 END as UPDATE_RULE, CASE s.delete_referential_action WHEN 1 THEN 0 WHEN 0 THEN 3 WHEN 2 THEN 2 WHEN 3 THEN 4 END as DELETE_RULE, t.FK_NAME, t.PK_NAME, t.DEFERRABILITY FROM " + tempTableName + " t LEFT JOIN sys.foreign_keys s ON t.FK_NAME = s.name COLLATE database_default AND schema_id(t.FKTABLE_OWNER) = s.schema_id";
            final SQLServerCallableStatement cstmt = (SQLServerCallableStatement)this.connection.prepareCall(sql);
            cstmt.closeOnCompletion();
            for (int i = 0; i < 6; ++i) {
                cstmt.setString(i + 1, procParams[i]);
            }
            String currentDB = null;
            if (null != procParams[2] && !procParams[2].isEmpty()) {
                currentDB = this.switchCatalogs(procParams[2]);
            }
            else if (null != procParams[5] && !procParams[5].isEmpty()) {
                currentDB = this.switchCatalogs(procParams[5]);
            }
            final ResultSet rs = cstmt.executeQuery();
            if (null != currentDB) {
                this.switchCatalogs(currentDB);
            }
            return rs;
        }
        ResultSet userRs = null;
        PreparedStatement pstmt = null;
        final StringBuilder azureDwSelectBuilder = new StringBuilder();
        synchronized (SQLServerDatabaseMetaData.class) {
            if (null == SQLServerDatabaseMetaData.getImportedKeysDWColumns) {
                (SQLServerDatabaseMetaData.getImportedKeysDWColumns = new LinkedHashMap<Integer, String>()).put(1, "PKTABLE_CAT");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(2, "PKTABLE_SCHEM");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(3, "PKTABLE_NAME");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(4, "PKCOLUMN_NAME");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(5, "FKTABLE_CAT");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(6, "FKTABLE_SCHEM");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(7, "FKTABLE_NAME");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(8, "FKCOLUMN_NAME");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(9, "KEY_SEQ");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(10, "UPDATE_RULE");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(11, "DELETE_RULE");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(12, "FK_NAME");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(13, "PK_NAME");
                SQLServerDatabaseMetaData.getImportedKeysDWColumns.put(14, "DEFERRABILITY");
            }
        }
        azureDwSelectBuilder.append(this.generateAzureDWEmptyRS(SQLServerDatabaseMetaData.getImportedKeysDWColumns));
        try {
            pstmt = this.connection.prepareStatement(azureDwSelectBuilder.toString());
            userRs = pstmt.executeQuery();
            pstmt.closeOnCompletion();
            return userRs;
        }
        catch (final SQLException e) {
            if (null != pstmt) {
                try {
                    pstmt.close();
                }
                catch (final SQLServerException ignore) {
                    if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER)) {
                        SQLServerDatabaseMetaData.loggerExternal.finer("executeSPFkeys() threw an exception when attempting to close PreparedStatement");
                    }
                }
            }
            throw e;
        }
    }
    
    @Override
    public ResultSet getIndexInfo(final String cat, final String schema, final String table, final boolean unique, final boolean approximate) throws SQLServerException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final String[] arguments = { table, schema, cat, "%", null, null };
        if (unique) {
            arguments[4] = "Y";
        }
        else {
            arguments[4] = "N";
        }
        if (approximate) {
            arguments[5] = "Q";
        }
        else {
            arguments[5] = "E";
        }
        return this.getResultSetWithProvidedColumnNames(cat, CallableHandles.SP_STATISTICS, arguments, SQLServerDatabaseMetaData.getIndexInfoColumnNames);
    }
    
    @Override
    public int getMaxBinaryLiteralLength() throws SQLServerException {
        this.checkClosed();
        return 0;
    }
    
    @Override
    public int getMaxCatalogNameLength() throws SQLServerException {
        this.checkClosed();
        return 128;
    }
    
    @Override
    public int getMaxCharLiteralLength() throws SQLServerException {
        this.checkClosed();
        return 0;
    }
    
    @Override
    public int getMaxColumnNameLength() throws SQLServerException {
        this.checkClosed();
        return 128;
    }
    
    @Override
    public int getMaxColumnsInGroupBy() throws SQLServerException {
        this.checkClosed();
        return 0;
    }
    
    @Override
    public int getMaxColumnsInIndex() throws SQLServerException {
        this.checkClosed();
        return 16;
    }
    
    @Override
    public int getMaxColumnsInOrderBy() throws SQLServerException {
        this.checkClosed();
        return 0;
    }
    
    @Override
    public int getMaxColumnsInSelect() throws SQLServerException {
        this.checkClosed();
        return 4096;
    }
    
    @Override
    public int getMaxColumnsInTable() throws SQLServerException {
        this.checkClosed();
        return 1024;
    }
    
    @Override
    public int getMaxConnections() throws SQLException, SQLTimeoutException {
        this.checkClosed();
        try (final SQLServerResultSet rs = this.getResultSetFromInternalQueries(null, "select maximum from sys.configurations where name = 'user connections'")) {
            if (!rs.next()) {
                final int n = 0;
                if (rs != null) {
                    rs.close();
                }
                return n;
            }
            return rs.getInt("maximum");
        }
        catch (final SQLServerException e) {
            try (final SQLServerResultSet rs2 = this.getResultSetFromInternalQueries(null, "sp_configure 'user connections'")) {
                if (!rs2.next()) {
                    final int n2 = 0;
                    if (rs2 != null) {
                        rs2.close();
                    }
                    return n2;
                }
                return rs2.getInt("maximum");
            }
            catch (final SQLServerException e2) {
                return 0;
            }
        }
    }
    
    @Override
    public int getMaxCursorNameLength() throws SQLServerException {
        this.checkClosed();
        return 0;
    }
    
    @Override
    public int getMaxIndexLength() throws SQLServerException {
        this.checkClosed();
        return 900;
    }
    
    @Override
    public int getMaxProcedureNameLength() throws SQLServerException {
        this.checkClosed();
        return 128;
    }
    
    @Override
    public int getMaxRowSize() throws SQLServerException {
        this.checkClosed();
        return 8060;
    }
    
    @Override
    public int getMaxSchemaNameLength() throws SQLServerException {
        this.checkClosed();
        return 128;
    }
    
    @Override
    public int getMaxStatementLength() throws SQLServerException {
        this.checkClosed();
        return 65536 * this.connection.getTDSPacketSize();
    }
    
    @Override
    public int getMaxStatements() throws SQLServerException {
        this.checkClosed();
        return 0;
    }
    
    @Override
    public int getMaxTableNameLength() throws SQLServerException {
        this.checkClosed();
        return 128;
    }
    
    @Override
    public int getMaxTablesInSelect() throws SQLServerException {
        this.checkClosed();
        return 256;
    }
    
    @Override
    public int getMaxUserNameLength() throws SQLServerException {
        this.checkClosed();
        return 128;
    }
    
    @Override
    public String getNumericFunctions() throws SQLServerException {
        this.checkClosed();
        return "ABS,ACOS,ASIN,ATAN,ATAN2,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MOD,PI,POWER,RADIANS,RAND,ROUND,SIGN,SIN,SQRT,TAN,TRUNCATE";
    }
    
    @Override
    public ResultSet getPrimaryKeys(final String cat, final String schema, final String table) throws SQLServerException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final String[] arguments = { table, schema, cat };
        return this.getResultSetWithProvidedColumnNames(cat, CallableHandles.SP_PKEYS, arguments, SQLServerDatabaseMetaData.getPrimaryKeysColumnNames);
    }
    
    @Override
    public ResultSet getProcedureColumns(final String catalog, final String schema, String proc, String col) throws SQLServerException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final String[] arguments = new String[5];
        proc = EscapeIDName(proc);
        arguments[0] = proc;
        arguments[1] = schema;
        arguments[2] = catalog;
        col = EscapeIDName(col);
        arguments[3] = col;
        arguments[4] = "3";
        final SQLServerResultSet rs = this.getResultSetWithProvidedColumnNames(catalog, CallableHandles.SP_SPROC_COLUMNS, arguments, SQLServerDatabaseMetaData.getProcedureColumnsColumnNames);
        rs.getColumn(6).setFilter(new DataTypeFilter());
        if (this.connection.isKatmaiOrLater()) {
            rs.getColumn(8).setFilter(new ZeroFixupFilter());
            rs.getColumn(9).setFilter(new ZeroFixupFilter());
            rs.getColumn(17).setFilter(new ZeroFixupFilter());
        }
        return rs;
    }
    
    @Override
    public ResultSet getProcedures(final String catalog, final String schema, final String proc) throws SQLServerException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final String[] arguments = { EscapeIDName(proc), schema, catalog };
        return this.getResultSetWithProvidedColumnNames(catalog, CallableHandles.SP_STORED_PROCEDURES, arguments, SQLServerDatabaseMetaData.getProceduresColumnNames);
    }
    
    @Override
    public String getProcedureTerm() throws SQLServerException {
        this.checkClosed();
        return "stored procedure";
    }
    
    @Override
    public ResultSet getPseudoColumns(final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        return this.getResultSetFromInternalQueries(catalog, "SELECT cast(NULL as char(1)) as TABLE_CAT, cast(NULL as char(1)) as TABLE_SCHEM, cast(NULL as char(1)) as TABLE_NAME, cast(NULL as char(1)) as COLUMN_NAME, cast(0 as int) as DATA_TYPE, cast(0 as int) as COLUMN_SIZE, cast(0 as int) as DECIMAL_DIGITS, cast(0 as int) as NUM_PREC_RADIX, cast(NULL as char(1)) as COLUMN_USAGE, cast(NULL as char(1)) as REMARKS, cast(0 as int) as CHAR_OCTET_LENGTH, cast(NULL as char(1)) as IS_NULLABLE where 0 = 1");
    }
    
    @Override
    public ResultSet getSchemas() throws SQLException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        return this.getSchemasInternal(null, null);
    }
    
    private ResultSet getSchemasInternal(String catalog, final String schemaPattern) throws SQLException, SQLTimeoutException {
        final String constSchemas = " ('dbo', 'guest','INFORMATION_SCHEMA','sys','db_owner', 'db_accessadmin', 'db_securityadmin', 'db_ddladmin'  ,'db_backupoperator','db_datareader','db_datawriter','db_denydatareader','db_denydatawriter') ";
        String schema = "sys.schemas";
        String schemaName = "sys.schemas.name";
        if (null != catalog && catalog.length() != 0) {
            final String catalogId = Util.escapeSQLId(catalog);
            schema = catalogId + "." + schema;
            schemaName = catalogId + "." + schemaName;
        }
        String s = "select " + schemaName + " 'TABLE_SCHEM',";
        if (null != catalog && catalog.length() == 0) {
            s += "null 'TABLE_CATALOG' ";
        }
        else {
            s = s + " CASE WHEN " + schemaName + "  IN " + constSchemas + " THEN null ELSE ";
            if (null != catalog && catalog.length() != 0) {
                s = s + "'" + catalog + "' ";
            }
            else {
                s += " DB_NAME() ";
            }
            s += " END 'TABLE_CATALOG' ";
        }
        s = s + "   from " + schema;
        if (null != catalog && catalog.length() == 0) {
            if (null != schemaPattern) {
                s = s + " where " + schemaName + " like ?  and ";
            }
            else {
                s += " where ";
            }
            s = s + schemaName + " in " + constSchemas;
        }
        else if (null != schemaPattern) {
            s = s + " where " + schemaName + " like ?  ";
        }
        s += " order by 2, 1";
        if (SQLServerDatabaseMetaData.logger.isLoggable(Level.FINE)) {
            SQLServerDatabaseMetaData.logger.fine(this.toString() + " schema query (" + s + ")");
        }
        SQLServerResultSet rs;
        if (null == schemaPattern) {
            catalog = null;
            rs = this.getResultSetFromInternalQueries(catalog, s);
        }
        else {
            final SQLServerPreparedStatement ps = (SQLServerPreparedStatement)this.connection.prepareStatement(s);
            ps.closeOnCompletion();
            ps.setString(1, schemaPattern);
            rs = (SQLServerResultSet)ps.executeQueryInternal();
        }
        return rs;
    }
    
    @Override
    public ResultSet getSchemas(final String catalog, final String schemaPattern) throws SQLException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        return this.getSchemasInternal(catalog, schemaPattern);
    }
    
    @Override
    public String getSchemaTerm() throws SQLServerException {
        this.checkClosed();
        return "schema";
    }
    
    @Override
    public String getSearchStringEscape() throws SQLServerException {
        this.checkClosed();
        return "\\";
    }
    
    @Override
    public String getSQLKeywords() throws SQLServerException {
        this.checkClosed();
        return SQLServerDatabaseMetaData.SQL_KEYWORDS;
    }
    
    private static String createSqlKeyWords() {
        return "ADD,ALL,ALTER,AND,ANY,AS,ASC,AUTHORIZATION,BACKUP,BEGIN,BETWEEN,BREAK,BROWSE,BULK,BY,CASCADE,CASE,CHECK,CHECKPOINT,CLOSE,CLUSTERED,COALESCE,COLLATE,COLUMN,COMMIT,COMPUTE,CONSTRAINT,CONTAINS,CONTAINSTABLE,CONTINUE,CONVERT,CREATE,CROSS,CURRENT,CURRENT_DATE,CURRENT_TIME,CURRENT_TIMESTAMP,CURRENT_USER,CURSOR,DATABASE,DBCC,DEALLOCATE,DECLARE,DEFAULT,DELETE,DENY,DESC,DISK,DISTINCT,DISTRIBUTED,DOUBLE,DROP,DUMP,ELSE,END,ERRLVL,ESCAPE,EXCEPT,EXEC,EXECUTE,EXISTS,EXIT,EXTERNAL,FETCH,FILE,FILLFACTOR,FOR,FOREIGN,FREETEXT,FREETEXTTABLE,FROM,FULL,FUNCTION,GOTO,GRANT,GROUP,HAVING,HOLDLOCK,IDENTITY,IDENTITY_INSERT,IDENTITYCOL,IF,IN,INDEX,INNER,INSERT,INTERSECT,INTO,IS,JOIN,KEY,KILL,LEFT,LIKE,LINENO,LOAD,MERGE,NATIONAL,NOCHECK,NONCLUSTERED,NOT,NULL,NULLIF,OF,OFF,OFFSETS,ON,OPEN,OPENDATASOURCE,OPENQUERY,OPENROWSET,OPENXML,OPTION,OR,ORDER,OUTER,OVER,PERCENT,PIVOT,PLAN,PRECISION,PRIMARY,PRINT,PROC,PROCEDURE,PUBLIC,RAISERROR,READ,READTEXT,RECONFIGURE,REFERENCES,REPLICATION,RESTORE,RESTRICT,RETURN,REVERT,REVOKE,RIGHT,ROLLBACK,ROWCOUNT,ROWGUIDCOL,RULE,SAVE,SCHEMA,SECURITYAUDIT,SELECT,SEMANTICKEYPHRASETABLE,SEMANTICSIMILARITYDETAILSTABLE,SEMANTICSIMILARITYTABLE,SESSION_USER,SET,SETUSER,SHUTDOWN,SOME,STATISTICS,SYSTEM_USER,TABLE,TABLESAMPLE,TEXTSIZE,THEN,TO,TOP,TRAN,TRANSACTION,TRIGGER,TRUNCATE,TRY_CONVERT,TSEQUAL,UNION,UNIQUE,UNPIVOT,UPDATE,UPDATETEXT,USE,USER,VALUES,VARYING,VIEW,WAITFOR,WHEN,WHERE,WHILE,WITH,WITHIN GROUP,WRITETEXT";
    }
    
    @Override
    public String getStringFunctions() throws SQLServerException {
        this.checkClosed();
        return "ASCII,CHAR,CONCAT,DIFFERENCE,INSERT,LCASE,LEFT,LENGTH,LOCATE,LTRIM,REPEAT,REPLACE,RIGHT,RTRIM,SOUNDEX,SPACE,SUBSTRING,UCASE";
    }
    
    @Override
    public String getSystemFunctions() throws SQLServerException {
        this.checkClosed();
        return "DATABASE,IFNULL,USER";
    }
    
    @Override
    public ResultSet getTablePrivileges(final String catalog, String schema, String table) throws SQLServerException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        table = EscapeIDName(table);
        schema = EscapeIDName(schema);
        final String[] arguments = { table, schema, catalog };
        return this.getResultSetWithProvidedColumnNames(catalog, CallableHandles.SP_TABLE_PRIVILEGES, arguments, SQLServerDatabaseMetaData.getTablePrivilegesColumnNames);
    }
    
    @Override
    public ResultSet getTableTypes() throws SQLException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final String s = "SELECT 'VIEW' 'TABLE_TYPE' UNION SELECT 'TABLE' UNION SELECT 'SYSTEM TABLE'";
        final SQLServerResultSet rs = this.getResultSetFromInternalQueries(null, s);
        return rs;
    }
    
    @Override
    public String getTimeDateFunctions() throws SQLServerException {
        this.checkClosed();
        return "CURDATE,CURTIME,DAYNAME,DAYOFMONTH,DAYOFWEEK,DAYOFYEAR,HOUR,MINUTE,MONTH,MONTHNAME,NOW,QUARTER,SECOND,TIMESTAMPADD,TIMESTAMPDIFF,WEEK,YEAR";
    }
    
    @Override
    public ResultSet getTypeInfo() throws SQLException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        SQLServerResultSet rs;
        if (this.connection.isKatmaiOrLater()) {
            rs = this.getResultSetFromInternalQueries(null, "sp_datatype_info_100 @ODBCVer=3");
        }
        else {
            rs = this.getResultSetFromInternalQueries(null, "sp_datatype_info @ODBCVer=3");
        }
        rs.setColumnName(11, "FIXED_PREC_SCALE");
        rs.getColumn(2).setFilter(new DataTypeFilter());
        return rs;
    }
    
    @Override
    public String getURL() throws SQLServerException {
        this.checkClosed();
        final StringBuilder url = new StringBuilder();
        final Properties props = this.connection.activeConnectionProperties;
        final DriverPropertyInfo[] info = SQLServerDriver.getPropertyInfoFromProperties(props);
        String serverName = null;
        String portNumber = null;
        String instanceName = null;
        int index = info.length;
        while (--index >= 0) {
            final String name = info[index].name;
            if (!name.equals(SQLServerDriverBooleanProperty.INTEGRATED_SECURITY.toString()) && !name.equals(SQLServerDriverStringProperty.USER.toString()) && !name.equals(SQLServerDriverStringProperty.PASSWORD.toString()) && !name.equals(SQLServerDriverStringProperty.KEY_STORE_SECRET.toString())) {
                final String val = info[index].value;
                if (0 == val.length()) {
                    continue;
                }
                if (name.equals(SQLServerDriverStringProperty.SERVER_NAME.toString())) {
                    serverName = val;
                }
                else if (name.equals(SQLServerDriverStringProperty.INSTANCE_NAME.toString())) {
                    instanceName = val;
                }
                else if (name.equals(SQLServerDriverIntProperty.PORT_NUMBER.toString())) {
                    portNumber = val;
                }
                else {
                    url.append(name);
                    url.append("=");
                    url.append(val);
                    url.append(";");
                }
            }
        }
        url.insert(0, ";");
        url.insert(0, portNumber);
        url.insert(0, ":");
        if (null != instanceName) {
            url.insert(0, instanceName);
            url.insert(0, "\\");
        }
        url.insert(0, serverName);
        url.insert(0, "jdbc:sqlserver://");
        return url.toString();
    }
    
    @Override
    public String getUserName() throws SQLServerException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        String result = "";
        try (final SQLServerStatement s = (SQLServerStatement)this.connection.createStatement();
             final SQLServerResultSet rs = s.executeQueryInternal("select system_user")) {
            final boolean next = rs.next();
            assert next;
            result = rs.getString(1);
        }
        return result;
    }
    
    @Override
    public ResultSet getVersionColumns(final String catalog, final String schema, final String table) throws SQLServerException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        final String[] arguments = { table, schema, catalog, "V", "T", "U", "3" };
        final SQLServerResultSet rs = this.getResultSetWithProvidedColumnNames(catalog, CallableHandles.SP_SPECIAL_COLUMNS, arguments, SQLServerDatabaseMetaData.getVersionColumnsColumnNames);
        rs.getColumn(3).setFilter(new DataTypeFilter());
        return rs;
    }
    
    @Override
    public boolean isCatalogAtStart() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean isReadOnly() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean nullPlusNonNullIsNull() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean nullsAreSortedAtEnd() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean nullsAreSortedAtStart() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean nullsAreSortedHigh() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean nullsAreSortedLow() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsANSI92FullSQL() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsColumnAliasing() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsConvert() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsConvert(final int fromType, final int toType) throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsCoreSQLGrammar() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsFullOuterJoins() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsGroupBy() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsGroupByUnrelated() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsLikeEscapeClause() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsLimitedOuterJoins() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsMultipleResultSets() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsMultipleTransactions() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsNonNullableColumns() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsOrderByUnrelated() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsOuterJoins() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsPositionedDelete() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsPositionedUpdate() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsSelectForUpdate() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsStoredProcedures() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsSubqueriesInExists() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsSubqueriesInIns() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsTableCorrelationNames() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsTransactionIsolationLevel(final int level) throws SQLServerException {
        this.checkClosed();
        switch (level) {
            case 1:
            case 2:
            case 4:
            case 8:
            case 4096: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean supportsTransactions() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsUnion() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsUnionAll() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean usesLocalFilePerTable() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean usesLocalFiles() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsResultSetType(final int type) throws SQLServerException {
        this.checkClosed();
        this.checkResultType(type);
        switch (type) {
            case 1003:
            case 1004:
            case 1005:
            case 1006:
            case 2003:
            case 2004: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean supportsResultSetConcurrency(final int type, final int concurrency) throws SQLServerException {
        this.checkClosed();
        this.checkResultType(type);
        this.checkConcurrencyType(concurrency);
        switch (type) {
            case 1003:
            case 1005:
            case 1006:
            case 2004: {
                return true;
            }
            case 1004:
            case 2003: {
                return 1007 == concurrency;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean ownUpdatesAreVisible(final int type) throws SQLServerException {
        this.checkClosed();
        this.checkResultType(type);
        return type == 1006 || 1003 == type || 1005 == type || 1005 == type || 2004 == type;
    }
    
    @Override
    public boolean ownDeletesAreVisible(final int type) throws SQLServerException {
        this.checkClosed();
        this.checkResultType(type);
        return type == 1006 || 1003 == type || 1005 == type || 1005 == type || 2004 == type;
    }
    
    @Override
    public boolean ownInsertsAreVisible(final int type) throws SQLServerException {
        this.checkClosed();
        this.checkResultType(type);
        return type == 1006 || 1003 == type || 1005 == type || 1005 == type || 2004 == type;
    }
    
    @Override
    public boolean othersUpdatesAreVisible(final int type) throws SQLServerException {
        this.checkClosed();
        this.checkResultType(type);
        return type == 1006 || 1003 == type || 1005 == type || 1005 == type || 2004 == type;
    }
    
    @Override
    public boolean othersDeletesAreVisible(final int type) throws SQLServerException {
        this.checkClosed();
        this.checkResultType(type);
        return type == 1006 || 1003 == type || 1005 == type || 1005 == type || 2004 == type;
    }
    
    @Override
    public boolean othersInsertsAreVisible(final int type) throws SQLServerException {
        this.checkClosed();
        this.checkResultType(type);
        return type == 1006 || 1003 == type || 2004 == type;
    }
    
    @Override
    public boolean updatesAreDetected(final int type) throws SQLServerException {
        this.checkClosed();
        this.checkResultType(type);
        return false;
    }
    
    @Override
    public boolean deletesAreDetected(final int type) throws SQLServerException {
        this.checkClosed();
        this.checkResultType(type);
        return 1005 == type;
    }
    
    private void checkResultType(final int type) throws SQLServerException {
        switch (type) {
            case 1003:
            case 1004:
            case 1005:
            case 1006:
            case 2003:
            case 2004: {
                return;
            }
            default: {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
                final Object[] msgArgs = { type };
                throw new SQLServerException(null, form.format(msgArgs), null, 0, true);
            }
        }
    }
    
    private void checkConcurrencyType(final int type) throws SQLServerException {
        switch (type) {
            case 1007:
            case 1008:
            case 1009:
            case 1010: {
                return;
            }
            default: {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
                final Object[] msgArgs = { type };
                throw new SQLServerException(null, form.format(msgArgs), null, 0, true);
            }
        }
    }
    
    @Override
    public boolean insertsAreDetected(final int type) throws SQLServerException {
        this.checkClosed();
        this.checkResultType(type);
        return false;
    }
    
    @Override
    public boolean supportsBatchUpdates() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public ResultSet getUDTs(final String catalog, final String schemaPattern, final String typeNamePattern, final int[] types) throws SQLException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        return this.getResultSetFromInternalQueries(catalog, "SELECT cast(NULL as char(1)) as TYPE_CAT, cast(NULL as char(1)) as TYPE_SCHEM, cast(NULL as char(1)) as TYPE_NAME, cast(NULL as char(1)) as CLASS_NAME, cast(0 as int) as DATA_TYPE, cast(NULL as char(1)) as REMARKS, cast(0 as smallint) as BASE_TYPE where 0 = 1");
    }
    
    @Override
    public Connection getConnection() throws SQLServerException {
        this.checkClosed();
        return this.connection.getConnection();
    }
    
    @Override
    public int getSQLStateType() throws SQLServerException {
        this.checkClosed();
        if (null != this.connection && this.connection.xopenStates) {
            return 1;
        }
        return 2;
    }
    
    @Override
    public int getDatabaseMajorVersion() throws SQLServerException {
        this.checkClosed();
        String s = this.connection.sqlServerVersion;
        final int p = s.indexOf(46);
        if (p > 0) {
            s = s.substring(0, p);
        }
        try {
            return Integer.parseInt(s);
        }
        catch (final NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public int getDatabaseMinorVersion() throws SQLServerException {
        this.checkClosed();
        String s = this.connection.sqlServerVersion;
        final int p = s.indexOf(46);
        final int q = s.indexOf(46, p + 1);
        if (p > 0 && q > 0) {
            s = s.substring(p + 1, q);
        }
        try {
            return Integer.parseInt(s);
        }
        catch (final NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public int getJDBCMajorVersion() throws SQLServerException {
        this.checkClosed();
        return 4;
    }
    
    @Override
    public int getJDBCMinorVersion() throws SQLServerException {
        this.checkClosed();
        return 2;
    }
    
    @Override
    public int getResultSetHoldability() throws SQLServerException {
        this.checkClosed();
        return 1;
    }
    
    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        this.checkClosed();
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }
    
    @Override
    public boolean supportsResultSetHoldability(final int holdability) throws SQLServerException {
        this.checkClosed();
        if (1 == holdability || 2 == holdability) {
            return true;
        }
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
        final Object[] msgArgs = { holdability };
        throw new SQLServerException(null, form.format(msgArgs), null, 0, true);
    }
    
    @Override
    public ResultSet getAttributes(final String catalog, final String schemaPattern, final String typeNamePattern, final String attributeNamePattern) throws SQLException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        return this.getResultSetFromInternalQueries(catalog, "SELECT cast(NULL as char(1)) as TYPE_CAT, cast(NULL as char(1)) as TYPE_SCHEM, cast(NULL as char(1)) as TYPE_NAME, cast(NULL as char(1)) as ATTR_NAME, cast(0 as int) as DATA_TYPE, cast(NULL as char(1)) as ATTR_TYPE_NAME, cast(0 as int) as ATTR_SIZE, cast(0 as int) as DECIMAL_DIGITS, cast(0 as int) as NUM_PREC_RADIX, cast(0 as int) as NULLABLE, cast(NULL as char(1)) as REMARKS, cast(NULL as char(1)) as ATTR_DEF, cast(0 as int) as SQL_DATA_TYPE, cast(0 as int) as SQL_DATETIME_SUB, cast(0 as int) as CHAR_OCTET_LENGTH, cast(0 as int) as ORDINAL_POSITION, cast(NULL as char(1)) as IS_NULLABLE, cast(NULL as char(1)) as SCOPE_CATALOG, cast(NULL as char(1)) as SCOPE_SCHEMA, cast(NULL as char(1)) as SCOPE_TABLE, cast(0 as smallint) as SOURCE_DATA_TYPE where 0 = 1");
    }
    
    @Override
    public ResultSet getSuperTables(final String catalog, final String schemaPattern, final String tableNamePattern) throws SQLException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        return this.getResultSetFromInternalQueries(catalog, "SELECT cast(NULL as char(1)) as TYPE_CAT, cast(NULL as char(1)) as TYPE_SCHEM, cast(NULL as char(1)) as TYPE_NAME, cast(NULL as char(1)) as SUPERTABLE_NAME where 0 = 1");
    }
    
    @Override
    public ResultSet getSuperTypes(final String catalog, final String schemaPattern, final String typeNamePattern) throws SQLException, SQLTimeoutException {
        if (SQLServerDatabaseMetaData.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerDatabaseMetaData.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        return this.getResultSetFromInternalQueries(catalog, "SELECT cast(NULL as char(1)) as TYPE_CAT, cast(NULL as char(1)) as TYPE_SCHEM, cast(NULL as char(1)) as TYPE_NAME, cast(NULL as char(1)) as SUPERTYPE_CAT, cast(NULL as char(1)) as SUPERTYPE_SCHEM, cast(NULL as char(1)) as SUPERTYPE_NAME where 0 = 1");
    }
    
    @Override
    public boolean supportsGetGeneratedKeys() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsMultipleOpenResults() throws SQLServerException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsNamedParameters() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsSavepoints() throws SQLServerException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean supportsStatementPooling() throws SQLException {
        this.checkClosed();
        return false;
    }
    
    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        this.checkClosed();
        return true;
    }
    
    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        this.checkClosed();
        return true;
    }
    
    static {
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerDatabaseMetaData");
        loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.DatabaseMetaData");
        baseID = new AtomicInteger(0);
        SQL_KEYWORDS = createSqlKeyWords();
        SQLServerDatabaseMetaData.getColumnsDWColumns = null;
        SQLServerDatabaseMetaData.getImportedKeysDWColumns = null;
        getColumnPrivilegesColumnNames = new String[] { "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "GRANTOR", "GRANTEE", "PRIVILEGE", "IS_GRANTABLE" };
        getTablesColumnNames = new String[] { "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE", "REMARKS" };
        DOUBLE_RIGHT_BRACKET = new char[] { ']', ']' };
        getFunctionsColumnNames = new String[] { "FUNCTION_CAT", "FUNCTION_SCHEM", "FUNCTION_NAME", "NUM_INPUT_PARAMS", "NUM_OUTPUT_PARAMS", "NUM_RESULT_SETS", "REMARKS", "FUNCTION_TYPE" };
        getFunctionsColumnsColumnNames = new String[] { "FUNCTION_CAT", "FUNCTION_SCHEM", "FUNCTION_NAME", "COLUMN_NAME", "COLUMN_TYPE", "DATA_TYPE", "TYPE_NAME", "PRECISION", "LENGTH", "SCALE", "RADIX", "NULLABLE", "REMARKS", "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE" };
        getBestRowIdentifierColumnNames = new String[] { "SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN" };
        getIndexInfoColumnNames = new String[] { "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "NON_UNIQUE", "INDEX_QUALIFIER", "INDEX_NAME", "TYPE", "ORDINAL_POSITION", "COLUMN_NAME", "ASC_OR_DESC", "CARDINALITY", "PAGES", "FILTER_CONDITION" };
        getPrimaryKeysColumnNames = new String[] { "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "KEY_SEQ", "PK_NAME" };
        getProcedureColumnsColumnNames = new String[] { "PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "COLUMN_NAME", "COLUMN_TYPE", "DATA_TYPE", "TYPE_NAME", "PRECISION", "LENGTH", "SCALE", "RADIX", "NULLABLE", "REMARKS", "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE" };
        getProceduresColumnNames = new String[] { "PROCEDURE_CAT", "PROCEDURE_SCHEM", "PROCEDURE_NAME", "NUM_INPUT_PARAMS", "NUM_OUTPUT_PARAMS", "NUM_RESULT_SETS", "REMARKS", "PROCEDURE_TYPE" };
        getTablePrivilegesColumnNames = new String[] { "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "GRANTOR", "GRANTEE", "PRIVILEGE", "IS_GRANTABLE" };
        getVersionColumnsColumnNames = new String[] { "SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN" };
    }
    
    enum CallableHandles
    {
        SP_COLUMNS("{ call sp_columns(?, ?, ?, ?, ?) }", "{ call sp_columns_100(?, ?, ?, ?, ?, ?) }"), 
        SP_COLUMN_PRIVILEGES("{ call sp_column_privileges(?, ?, ?, ?)}", "{ call sp_column_privileges(?, ?, ?, ?)}"), 
        SP_TABLES("{ call sp_tables(?, ?, ?, ?) }", "{ call sp_tables(?, ?, ?, ?) }"), 
        SP_SPECIAL_COLUMNS("{ call sp_special_columns (?, ?, ?, ?, ?, ?, ?)}", "{ call sp_special_columns_100 (?, ?, ?, ?, ?, ?, ?)}"), 
        SP_FKEYS("{ call sp_fkeys (?, ?, ?, ? , ? ,?)}", "{ call sp_fkeys (?, ?, ?, ? , ? ,?)}"), 
        SP_STATISTICS("{ call sp_statistics(?,?,?,?,?, ?) }", "{ call sp_statistics_100(?,?,?,?,?, ?) }"), 
        SP_SPROC_COLUMNS("{ call sp_sproc_columns(?, ?, ?,?,?) }", "{ call sp_sproc_columns_100(?, ?, ?,?,?) }"), 
        SP_STORED_PROCEDURES("{call sp_stored_procedures(?, ?, ?) }", "{call sp_stored_procedures(?, ?, ?) }"), 
        SP_TABLE_PRIVILEGES("{call sp_table_privileges(?,?,?) }", "{call sp_table_privileges(?,?,?) }"), 
        SP_PKEYS("{ call sp_pkeys (?, ?, ?)}", "{ call sp_pkeys (?, ?, ?)}");
        
        private final String preKatProc;
        private final String katProc;
        
        private CallableHandles(final String name, final String katName) {
            this.preKatProc = name;
            this.katProc = katName;
        }
        
        CallableStatement prepare(final SQLServerConnection conn) throws SQLServerException {
            return conn.prepareCall(conn.isKatmaiOrLater() ? this.katProc : this.preKatProc);
        }
    }
    
    final class HandleAssociation
    {
        Map<String, CallableStatement> statementMap;
        boolean nullCatalog;
        CallableStatement stmt;
        
        HandleAssociation() {
            this.nullCatalog = false;
            if (null == this.statementMap) {
                this.statementMap = new HashMap<String, CallableStatement>();
            }
        }
        
        final void addToMap(final String databaseName, final CallableStatement stmt) {
            if (null != databaseName) {
                this.nullCatalog = false;
                this.statementMap.put(databaseName, stmt);
            }
            else {
                this.nullCatalog = true;
                this.stmt = stmt;
            }
        }
        
        final CallableStatement getMappedStatement(final String databaseName) {
            if (null == databaseName) {
                return this.stmt;
            }
            if (null != this.statementMap && this.statementMap.containsKey(databaseName)) {
                return this.statementMap.get(databaseName);
            }
            return null;
        }
    }
}
