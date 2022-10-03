package com.microsoft.sqlserver.jdbc;

import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.time.temporal.TemporalAccessor;
import java.time.DateTimeException;
import microsoft.sql.DateTimeOffset;
import java.sql.Date;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.time.temporal.TemporalField;
import java.time.temporal.ChronoField;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.sql.Timestamp;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.io.StringReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Set;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.Properties;
import java.sql.Connection;
import java.util.concurrent.ScheduledFuture;
import java.util.Map;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Logger;
import java.io.Serializable;

public class SQLServerBulkCopy implements AutoCloseable, Serializable
{
    private static final long serialVersionUID = 1989903904654306244L;
    private static final String loggerClassName = "com.microsoft.sqlserver.jdbc.SQLServerBulkCopy";
    private static final Logger loggerExternal;
    private SQLServerConnection connection;
    private SQLServerBulkCopyOptions copyOptions;
    private List<ColumnMapping> columnMappings;
    private boolean ownsConnection;
    private String destinationTableName;
    private ISQLServerBulkData serverBulkData;
    private ResultSet sourceResultSet;
    private ResultSetMetaData sourceResultSetMetaData;
    private CekTable destCekTable;
    private SQLServerStatementColumnEncryptionSetting stmtColumnEncriptionSetting;
    private ResultSet destinationTableMetadata;
    private Map<Integer, BulkColumnMetaData> destColumnMetadata;
    private Map<Integer, BulkColumnMetaData> srcColumnMetadata;
    private int destColumnCount;
    private int srcColumnCount;
    private ScheduledFuture<?> timeout;
    private static final int sourceBulkRecordTemporalMaxPrecision = 50;
    
    public SQLServerBulkCopy(final Connection connection) throws SQLServerException {
        this.destCekTable = null;
        this.stmtColumnEncriptionSetting = SQLServerStatementColumnEncryptionSetting.UseConnectionSetting;
        SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "SQLServerBulkCopy", connection);
        if (null == connection || !(connection instanceof ISQLServerConnection)) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_invalidDestConnection"), null, false);
        }
        if (connection instanceof SQLServerConnection) {
            this.connection = (SQLServerConnection)connection;
        }
        else if (connection instanceof SQLServerConnectionPoolProxy) {
            this.connection = ((SQLServerConnectionPoolProxy)connection).getWrappedConnection();
        }
        else {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_invalidDestConnection"), null, false);
        }
        this.ownsConnection = false;
        this.copyOptions = new SQLServerBulkCopyOptions();
        this.initializeDefaults();
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "SQLServerBulkCopy");
    }
    
    public SQLServerBulkCopy(final String connectionUrl) throws SQLServerException {
        this.destCekTable = null;
        this.stmtColumnEncriptionSetting = SQLServerStatementColumnEncryptionSetting.UseConnectionSetting;
        SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "SQLServerBulkCopy", "connectionUrl not traced.");
        if (connectionUrl == null || "".equals(connectionUrl.trim())) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_nullConnection"), null, 0, false);
        }
        this.ownsConnection = true;
        final SQLServerDriver driver = new SQLServerDriver();
        this.connection = (SQLServerConnection)driver.connect(connectionUrl, null);
        if (null == this.connection) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_invalidConnection"), null, 0, false);
        }
        this.copyOptions = new SQLServerBulkCopyOptions();
        this.initializeDefaults();
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "SQLServerBulkCopy");
    }
    
    public void addColumnMapping(final int sourceColumn, final int destinationColumn) throws SQLServerException {
        if (SQLServerBulkCopy.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "addColumnMapping", new Object[] { sourceColumn, destinationColumn });
        }
        if (0 >= sourceColumn) {
            this.throwInvalidArgument("sourceColumn");
        }
        else if (0 >= destinationColumn) {
            this.throwInvalidArgument("destinationColumn");
        }
        this.columnMappings.add(new ColumnMapping(sourceColumn, destinationColumn));
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "addColumnMapping");
    }
    
    public void addColumnMapping(final int sourceColumn, final String destinationColumn) throws SQLServerException {
        if (SQLServerBulkCopy.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "addColumnMapping", new Object[] { sourceColumn, destinationColumn });
        }
        if (0 >= sourceColumn) {
            this.throwInvalidArgument("sourceColumn");
        }
        else if (null == destinationColumn || destinationColumn.isEmpty()) {
            this.throwInvalidArgument("destinationColumn");
        }
        this.columnMappings.add(new ColumnMapping(sourceColumn, destinationColumn.trim()));
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "addColumnMapping");
    }
    
    public void addColumnMapping(final String sourceColumn, final int destinationColumn) throws SQLServerException {
        if (SQLServerBulkCopy.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "addColumnMapping", new Object[] { sourceColumn, destinationColumn });
        }
        if (0 >= destinationColumn) {
            this.throwInvalidArgument("destinationColumn");
        }
        else if (null == sourceColumn || sourceColumn.isEmpty()) {
            this.throwInvalidArgument("sourceColumn");
        }
        this.columnMappings.add(new ColumnMapping(sourceColumn.trim(), destinationColumn));
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "addColumnMapping");
    }
    
    public void addColumnMapping(final String sourceColumn, final String destinationColumn) throws SQLServerException {
        if (SQLServerBulkCopy.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "addColumnMapping", new Object[] { sourceColumn, destinationColumn });
        }
        if (null == sourceColumn || sourceColumn.isEmpty()) {
            this.throwInvalidArgument("sourceColumn");
        }
        else if (null == destinationColumn || destinationColumn.isEmpty()) {
            this.throwInvalidArgument("destinationColumn");
        }
        this.columnMappings.add(new ColumnMapping(sourceColumn.trim(), destinationColumn.trim()));
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "addColumnMapping");
    }
    
    public void clearColumnMappings() {
        SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "clearColumnMappings");
        this.columnMappings.clear();
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "clearColumnMappings");
    }
    
    @Override
    public void close() {
        SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "close");
        if (this.ownsConnection) {
            try {
                this.connection.close();
            }
            catch (final SQLException ex) {}
        }
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "close");
    }
    
    public String getDestinationTableName() {
        return this.destinationTableName;
    }
    
    public void setDestinationTableName(final String tableName) throws SQLServerException {
        SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "setDestinationTableName", tableName);
        if (null == tableName || 0 == tableName.trim().length()) {
            this.throwInvalidArgument("tableName");
        }
        this.destinationTableName = tableName.trim();
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "setDestinationTableName");
    }
    
    public SQLServerBulkCopyOptions getBulkCopyOptions() {
        return this.copyOptions;
    }
    
    public void setBulkCopyOptions(final SQLServerBulkCopyOptions copyOptions) throws SQLServerException {
        SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "updateBulkCopyOptions", copyOptions);
        if (null != copyOptions) {
            if (!this.ownsConnection && copyOptions.isUseInternalTransaction()) {
                SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_invalidTransactionOption"), null, false);
            }
            this.copyOptions = copyOptions;
        }
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "updateBulkCopyOptions");
    }
    
    public void writeToServer(final ResultSet sourceData) throws SQLServerException {
        this.writeResultSet(sourceData, false);
    }
    
    public void writeToServer(final RowSet sourceData) throws SQLServerException {
        this.writeResultSet(sourceData, true);
    }
    
    private void writeResultSet(final ResultSet sourceData, final boolean isRowSet) throws SQLServerException {
        SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "writeToServer");
        if (null == sourceData) {
            this.throwInvalidArgument("sourceData");
        }
        try {
            if (isRowSet) {
                if (!sourceData.isBeforeFirst()) {
                    sourceData.beforeFirst();
                }
            }
            else if (sourceData.isClosed()) {
                SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_resultsetClosed"), null, false);
            }
        }
        catch (final SQLException e) {
            throw new SQLServerException(null, e.getMessage(), null, 0, false);
        }
        this.sourceResultSet = sourceData;
        this.serverBulkData = null;
        try {
            this.sourceResultSetMetaData = this.sourceResultSet.getMetaData();
        }
        catch (final SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), e);
        }
        this.writeToServer();
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "writeToServer");
    }
    
    public void writeToServer(final ISQLServerBulkData sourceData) throws SQLServerException {
        SQLServerBulkCopy.loggerExternal.entering("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "writeToServer");
        if (null == sourceData) {
            this.throwInvalidArgument("sourceData");
        }
        this.serverBulkData = sourceData;
        this.sourceResultSet = null;
        this.writeToServer();
        SQLServerBulkCopy.loggerExternal.exiting("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy", "writeToServer");
    }
    
    private void initializeDefaults() {
        this.columnMappings = new ArrayList<ColumnMapping>();
        this.destinationTableName = null;
        this.serverBulkData = null;
        this.sourceResultSet = null;
        this.sourceResultSetMetaData = null;
        this.srcColumnCount = 0;
        this.srcColumnMetadata = null;
        this.destColumnMetadata = null;
        this.destColumnCount = 0;
    }
    
    private void sendBulkLoadBCP() throws SQLServerException {
        final class InsertBulk extends TDSCommand
        {
            private static final long serialVersionUID = 6714118105257791547L;
            
            InsertBulk() {
                super("InsertBulk", 0, 0);
            }
            
            @Override
            final boolean doExecute() throws SQLServerException {
                final int timeoutSeconds = SQLServerBulkCopy.this.copyOptions.getBulkCopyTimeout();
                if (timeoutSeconds > 0) {
                    SQLServerBulkCopy.this.connection.checkClosed();
                    SQLServerBulkCopy.this.timeout = SQLServerBulkCopy.this.connection.getSharedTimer().schedule(new TDSTimeoutTask(this, SQLServerBulkCopy.this.connection), timeoutSeconds);
                }
                try {
                    while (SQLServerBulkCopy.this.doInsertBulk(this)) {}
                }
                catch (final SQLServerException topLevelException) {
                    Throwable rootCause;
                    for (rootCause = topLevelException; null != rootCause.getCause(); rootCause = rootCause.getCause()) {}
                    if (rootCause instanceof SQLException && SQLServerBulkCopy.this.timeout != null && SQLServerBulkCopy.this.timeout.isDone()) {
                        final SQLException sqlEx = (SQLException)rootCause;
                        if (sqlEx.getSQLState() != null && sqlEx.getSQLState().equals(SQLState.STATEMENT_CANCELED.getSQLStateCode())) {
                            if (SQLServerBulkCopy.this.copyOptions.isUseInternalTransaction()) {
                                SQLServerBulkCopy.this.connection.rollback();
                            }
                            throw new SQLServerException(SQLServerException.getErrString("R_queryTimedOut"), SQLState.STATEMENT_CANCELED, DriverError.NOT_SET, sqlEx);
                        }
                    }
                    throw topLevelException;
                }
                if (SQLServerBulkCopy.this.timeout != null) {
                    SQLServerBulkCopy.this.timeout.cancel(true);
                    SQLServerBulkCopy.this.timeout = null;
                }
                return true;
            }
        }
        this.connection.executeCommand(new InsertBulk());
    }
    
    private void writeColumnMetaDataColumnData(final TDSWriter tdsWriter, final int idx) throws SQLServerException {
        final byte[] userType = { 0, 0, 0, 0 };
        tdsWriter.writeBytes(userType);
        final int destColumnIndex = this.columnMappings.get(idx).destinationColumnOrdinal;
        final int srcColumnIndex = this.columnMappings.get(idx).sourceColumnOrdinal;
        final byte[] flags = this.destColumnMetadata.get(destColumnIndex).flags;
        if (null == this.srcColumnMetadata.get(srcColumnIndex).cryptoMeta && null == this.destColumnMetadata.get(destColumnIndex).cryptoMeta && this.copyOptions.isAllowEncryptedValueModifications() && 0x1 == (flags[1] >> 3 & 0x1)) {
            flags[1] -= 8;
        }
        tdsWriter.writeBytes(flags);
        int bulkJdbcType = this.srcColumnMetadata.get(srcColumnIndex).jdbcType;
        int bulkPrecision = this.srcColumnMetadata.get(srcColumnIndex).precision;
        int bulkScale = this.srcColumnMetadata.get(srcColumnIndex).scale;
        final boolean srcNullable = this.srcColumnMetadata.get(srcColumnIndex).isNullable;
        final SSType destSSType = this.destColumnMetadata.get(destColumnIndex).ssType;
        final int destPrecision = this.destColumnMetadata.get(destColumnIndex).precision;
        bulkPrecision = this.validateSourcePrecision(bulkPrecision, bulkJdbcType, destPrecision);
        SQLCollation collation = this.destColumnMetadata.get(destColumnIndex).collation;
        if (null == collation) {
            collation = this.connection.getDatabaseCollation();
        }
        boolean isStreaming;
        if (-15 == bulkJdbcType || -9 == bulkJdbcType || -16 == bulkJdbcType) {
            isStreaming = (4000 < bulkPrecision || 4000 < destPrecision);
        }
        else {
            isStreaming = (8000 < bulkPrecision || 8000 < destPrecision);
        }
        final CryptoMetadata destCryptoMeta = this.destColumnMetadata.get(destColumnIndex).cryptoMeta;
        if (this.sourceResultSet instanceof SQLServerResultSet && this.connection.isColumnEncryptionSettingEnabled() && null != destCryptoMeta) {
            bulkJdbcType = this.destColumnMetadata.get(destColumnIndex).jdbcType;
            bulkPrecision = destPrecision;
            bulkScale = this.destColumnMetadata.get(destColumnIndex).scale;
        }
        if ((null != this.destColumnMetadata.get(destColumnIndex).encryptionType && this.copyOptions.isAllowEncryptedValueModifications()) || null != this.destColumnMetadata.get(destColumnIndex).cryptoMeta) {
            tdsWriter.writeByte((byte)(-91));
            if (isStreaming) {
                tdsWriter.writeShort((short)(-1));
            }
            else {
                tdsWriter.writeShort((short)bulkPrecision);
            }
        }
        else if ((1 == bulkJdbcType || 12 == bulkJdbcType || -1 == bulkJdbcType) && (SSType.BINARY == destSSType || SSType.VARBINARY == destSSType || SSType.VARBINARYMAX == destSSType || SSType.IMAGE == destSSType)) {
            if (isStreaming) {
                tdsWriter.writeByte((byte)(-91));
            }
            else {
                tdsWriter.writeByte((byte)((SSType.BINARY == destSSType) ? 173 : 165));
            }
            tdsWriter.writeShort((short)bulkPrecision);
        }
        else {
            this.writeTypeInfo(tdsWriter, bulkJdbcType, bulkScale, bulkPrecision, destSSType, collation, isStreaming, srcNullable, false);
        }
        if (null != destCryptoMeta) {
            final int baseDestJDBCType = destCryptoMeta.baseTypeInfo.getSSType().getJDBCType().asJavaSqlType();
            final int baseDestPrecision = destCryptoMeta.baseTypeInfo.getPrecision();
            if (-15 == baseDestJDBCType || -9 == baseDestJDBCType || -16 == baseDestJDBCType) {
                isStreaming = (4000 < baseDestPrecision);
            }
            else {
                isStreaming = (8000 < baseDestPrecision);
            }
            tdsWriter.writeShort(destCryptoMeta.getOrdinal());
            tdsWriter.writeBytes(userType);
            this.writeTypeInfo(tdsWriter, baseDestJDBCType, destCryptoMeta.baseTypeInfo.getScale(), baseDestPrecision, destCryptoMeta.baseTypeInfo.getSSType(), collation, isStreaming, srcNullable, true);
            tdsWriter.writeByte(destCryptoMeta.cipherAlgorithmId);
            tdsWriter.writeByte(destCryptoMeta.encryptionType.getValue());
            tdsWriter.writeByte(destCryptoMeta.normalizationRuleVersion);
        }
        final int destColNameLen = this.columnMappings.get(idx).destinationColumnName.length();
        final String destColName = this.columnMappings.get(idx).destinationColumnName;
        final byte[] colName = new byte[2 * destColNameLen];
        for (int i = 0; i < destColNameLen; ++i) {
            final int c = destColName.charAt(i);
            colName[2 * i] = (byte)(c & 0xFF);
            colName[2 * i + 1] = (byte)(c >> 8 & 0xFF);
        }
        tdsWriter.writeByte((byte)destColNameLen);
        tdsWriter.writeBytes(colName);
    }
    
    private void writeTypeInfo(final TDSWriter tdsWriter, final int srcJdbcType, final int srcScale, final int srcPrecision, final SSType destSSType, final SQLCollation collation, final boolean isStreaming, final boolean srcNullable, final boolean isBaseType) throws SQLServerException {
        Label_1389: {
            switch (srcJdbcType) {
                case 4: {
                    if (!srcNullable) {
                        tdsWriter.writeByte(TDSType.INT4.byteValue());
                        break;
                    }
                    tdsWriter.writeByte(TDSType.INTN.byteValue());
                    tdsWriter.writeByte((byte)4);
                    break;
                }
                case -5: {
                    if (!srcNullable) {
                        tdsWriter.writeByte(TDSType.INT8.byteValue());
                        break;
                    }
                    tdsWriter.writeByte(TDSType.INTN.byteValue());
                    tdsWriter.writeByte((byte)8);
                    break;
                }
                case -7: {
                    if (!srcNullable) {
                        tdsWriter.writeByte(TDSType.BIT1.byteValue());
                        break;
                    }
                    tdsWriter.writeByte(TDSType.BITN.byteValue());
                    tdsWriter.writeByte((byte)1);
                    break;
                }
                case 5: {
                    if (!srcNullable) {
                        tdsWriter.writeByte(TDSType.INT2.byteValue());
                        break;
                    }
                    tdsWriter.writeByte(TDSType.INTN.byteValue());
                    tdsWriter.writeByte((byte)2);
                    break;
                }
                case -6: {
                    if (!srcNullable) {
                        tdsWriter.writeByte(TDSType.INT1.byteValue());
                        break;
                    }
                    tdsWriter.writeByte(TDSType.INTN.byteValue());
                    tdsWriter.writeByte((byte)1);
                    break;
                }
                case 6:
                case 8: {
                    if (!srcNullable) {
                        tdsWriter.writeByte(TDSType.FLOAT8.byteValue());
                        break;
                    }
                    tdsWriter.writeByte(TDSType.FLOATN.byteValue());
                    tdsWriter.writeByte((byte)8);
                    break;
                }
                case 7: {
                    if (!srcNullable) {
                        tdsWriter.writeByte(TDSType.FLOAT4.byteValue());
                        break;
                    }
                    tdsWriter.writeByte(TDSType.FLOATN.byteValue());
                    tdsWriter.writeByte((byte)4);
                    break;
                }
                case -148:
                case -146:
                case 2:
                case 3: {
                    if (!isBaseType || (SSType.MONEY != destSSType && SSType.SMALLMONEY != destSSType)) {
                        if (3 == srcJdbcType) {
                            tdsWriter.writeByte(TDSType.DECIMALN.byteValue());
                        }
                        else {
                            tdsWriter.writeByte(TDSType.NUMERICN.byteValue());
                        }
                        tdsWriter.writeByte((byte)17);
                        tdsWriter.writeByte((byte)srcPrecision);
                        tdsWriter.writeByte((byte)srcScale);
                        break;
                    }
                    tdsWriter.writeByte(TDSType.MONEYN.byteValue());
                    if (SSType.MONEY == destSSType) {
                        tdsWriter.writeByte((byte)8);
                        break;
                    }
                    tdsWriter.writeByte((byte)4);
                    break;
                }
                case -145:
                case 1: {
                    if (isBaseType && SSType.GUID == destSSType) {
                        tdsWriter.writeByte(TDSType.GUID.byteValue());
                        tdsWriter.writeByte((byte)16);
                        break;
                    }
                    if (this.unicodeConversionRequired(srcJdbcType, destSSType)) {
                        tdsWriter.writeByte(TDSType.NCHAR.byteValue());
                        tdsWriter.writeShort(isBaseType ? ((short)srcPrecision) : ((short)(2 * srcPrecision)));
                    }
                    else {
                        tdsWriter.writeByte(TDSType.BIGCHAR.byteValue());
                        tdsWriter.writeShort((short)srcPrecision);
                    }
                    collation.writeCollation(tdsWriter);
                    break;
                }
                case -15: {
                    tdsWriter.writeByte(TDSType.NCHAR.byteValue());
                    tdsWriter.writeShort(isBaseType ? ((short)srcPrecision) : ((short)(2 * srcPrecision)));
                    collation.writeCollation(tdsWriter);
                    break;
                }
                case -1:
                case 12: {
                    if (this.unicodeConversionRequired(srcJdbcType, destSSType)) {
                        tdsWriter.writeByte(TDSType.NVARCHAR.byteValue());
                        if (isStreaming) {
                            tdsWriter.writeShort((short)(-1));
                        }
                        else {
                            tdsWriter.writeShort(isBaseType ? ((short)srcPrecision) : ((short)(2 * srcPrecision)));
                        }
                    }
                    else {
                        tdsWriter.writeByte(TDSType.BIGVARCHAR.byteValue());
                        if (isStreaming) {
                            tdsWriter.writeShort((short)(-1));
                        }
                        else {
                            tdsWriter.writeShort((short)srcPrecision);
                        }
                    }
                    collation.writeCollation(tdsWriter);
                    break;
                }
                case -16:
                case -9: {
                    tdsWriter.writeByte(TDSType.NVARCHAR.byteValue());
                    if (isStreaming) {
                        tdsWriter.writeShort((short)(-1));
                    }
                    else {
                        tdsWriter.writeShort(isBaseType ? ((short)srcPrecision) : ((short)(2 * srcPrecision)));
                    }
                    collation.writeCollation(tdsWriter);
                    break;
                }
                case -2: {
                    tdsWriter.writeByte(TDSType.BIGBINARY.byteValue());
                    tdsWriter.writeShort((short)srcPrecision);
                    break;
                }
                case -4:
                case -3: {
                    tdsWriter.writeByte(TDSType.BIGVARBINARY.byteValue());
                    if (isStreaming) {
                        tdsWriter.writeShort((short)(-1));
                        break;
                    }
                    tdsWriter.writeShort((short)srcPrecision);
                    break;
                }
                case -151:
                case -150:
                case 93: {
                    if (!isBaseType && null != this.serverBulkData) {
                        tdsWriter.writeByte(TDSType.BIGVARCHAR.byteValue());
                        tdsWriter.writeShort((short)srcPrecision);
                        collation.writeCollation(tdsWriter);
                        break;
                    }
                    switch (destSSType) {
                        case SMALLDATETIME: {
                            if (!srcNullable) {
                                tdsWriter.writeByte(TDSType.DATETIME4.byteValue());
                                break Label_1389;
                            }
                            tdsWriter.writeByte(TDSType.DATETIMEN.byteValue());
                            tdsWriter.writeByte((byte)4);
                            break Label_1389;
                        }
                        case DATETIME: {
                            if (!srcNullable) {
                                tdsWriter.writeByte(TDSType.DATETIME8.byteValue());
                                break Label_1389;
                            }
                            tdsWriter.writeByte(TDSType.DATETIMEN.byteValue());
                            tdsWriter.writeByte((byte)8);
                            break Label_1389;
                        }
                        default: {
                            tdsWriter.writeByte(TDSType.DATETIME2N.byteValue());
                            tdsWriter.writeByte((byte)srcScale);
                            break Label_1389;
                        }
                    }
                    break;
                }
                case 91: {
                    if (!isBaseType && null != this.serverBulkData) {
                        tdsWriter.writeByte(TDSType.BIGVARCHAR.byteValue());
                        tdsWriter.writeShort((short)srcPrecision);
                        collation.writeCollation(tdsWriter);
                        break;
                    }
                    tdsWriter.writeByte(TDSType.DATEN.byteValue());
                    break;
                }
                case 92: {
                    if (!isBaseType && null != this.serverBulkData) {
                        tdsWriter.writeByte(TDSType.BIGVARCHAR.byteValue());
                        tdsWriter.writeShort((short)srcPrecision);
                        collation.writeCollation(tdsWriter);
                        break;
                    }
                    tdsWriter.writeByte(TDSType.TIMEN.byteValue());
                    tdsWriter.writeByte((byte)srcScale);
                    break;
                }
                case 2013:
                case 2014: {
                    tdsWriter.writeByte(TDSType.DATETIMEOFFSETN.byteValue());
                    tdsWriter.writeByte((byte)srcScale);
                    break;
                }
                case -155: {
                    if (!isBaseType && null != this.serverBulkData) {
                        tdsWriter.writeByte(TDSType.BIGVARCHAR.byteValue());
                        tdsWriter.writeShort((short)srcPrecision);
                        collation.writeCollation(tdsWriter);
                        break;
                    }
                    tdsWriter.writeByte(TDSType.DATETIMEOFFSETN.byteValue());
                    tdsWriter.writeByte((byte)srcScale);
                    break;
                }
                case -156: {
                    tdsWriter.writeByte(TDSType.SQL_VARIANT.byteValue());
                    tdsWriter.writeInt(8009);
                    break;
                }
                default: {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                    final String unsupportedDataType = JDBCType.of(srcJdbcType).toString().toLowerCase(Locale.ENGLISH);
                    throw new SQLServerException(form.format(new Object[] { unsupportedDataType }), null, 0, null);
                }
            }
        }
    }
    
    private void writeCekTable(final TDSWriter tdsWriter) throws SQLServerException {
        if (this.connection.getServerSupportsColumnEncryption()) {
            if (null != this.destCekTable && 0 < this.destCekTable.getSize()) {
                tdsWriter.writeShort((short)this.destCekTable.getSize());
                for (int cekIndx = 0; cekIndx < this.destCekTable.getSize(); ++cekIndx) {
                    tdsWriter.writeInt(this.destCekTable.getCekTableEntry(cekIndx).getColumnEncryptionKeyValues().get(0).databaseId);
                    tdsWriter.writeInt(this.destCekTable.getCekTableEntry(cekIndx).getColumnEncryptionKeyValues().get(0).cekId);
                    tdsWriter.writeInt(this.destCekTable.getCekTableEntry(cekIndx).getColumnEncryptionKeyValues().get(0).cekVersion);
                    tdsWriter.writeBytes(this.destCekTable.getCekTableEntry(cekIndx).getColumnEncryptionKeyValues().get(0).cekMdVersion);
                    tdsWriter.writeByte((byte)0);
                }
            }
            else {
                tdsWriter.writeShort((short)0);
            }
        }
    }
    
    private void writeColumnMetaData(final TDSWriter tdsWriter) throws SQLServerException {
        tdsWriter.writeByte((byte)(-127));
        final byte[] count = { (byte)(this.columnMappings.size() & 0xFF), (byte)(this.columnMappings.size() >> 8 & 0xFF) };
        tdsWriter.writeBytes(count);
        this.writeCekTable(tdsWriter);
        for (int i = 0; i < this.columnMappings.size(); ++i) {
            this.writeColumnMetaDataColumnData(tdsWriter, i);
        }
    }
    
    private void validateDataTypeConversions(final int srcColOrdinal, final int destColOrdinal) throws SQLServerException {
        final CryptoMetadata sourceCryptoMeta = this.srcColumnMetadata.get(srcColOrdinal).cryptoMeta;
        final CryptoMetadata destCryptoMeta = this.destColumnMetadata.get(destColOrdinal).cryptoMeta;
        final JDBCType srcJdbcType = (null != sourceCryptoMeta) ? sourceCryptoMeta.baseTypeInfo.getSSType().getJDBCType() : JDBCType.of(this.srcColumnMetadata.get(srcColOrdinal).jdbcType);
        final SSType destSSType = (null != destCryptoMeta) ? destCryptoMeta.baseTypeInfo.getSSType() : this.destColumnMetadata.get(destColOrdinal).ssType;
        if (!srcJdbcType.convertsTo(destSSType)) {
            DataTypes.throwConversionError(srcJdbcType.toString(), destSSType.toString());
        }
    }
    
    private String getDestTypeFromSrcType(final int srcColIndx, final int destColIndx, final TDSWriter tdsWriter) throws SQLServerException {
        final SSType destSSType = (null != this.destColumnMetadata.get(destColIndx).cryptoMeta) ? this.destColumnMetadata.get(destColIndx).cryptoMeta.baseTypeInfo.getSSType() : this.destColumnMetadata.get(destColIndx).ssType;
        int bulkJdbcType = this.srcColumnMetadata.get(srcColIndx).jdbcType;
        int bulkPrecision;
        final int srcPrecision = bulkPrecision = this.srcColumnMetadata.get(srcColIndx).precision;
        final int destPrecision = this.destColumnMetadata.get(destColIndx).precision;
        int bulkScale = this.srcColumnMetadata.get(srcColIndx).scale;
        final CryptoMetadata destCryptoMeta = this.destColumnMetadata.get(destColIndx).cryptoMeta;
        if (null != destCryptoMeta || (null == destCryptoMeta && this.copyOptions.isAllowEncryptedValueModifications())) {
            tdsWriter.setCryptoMetaData(this.destColumnMetadata.get(destColIndx).cryptoMeta);
            if (this.sourceResultSet instanceof SQLServerResultSet && this.connection.isColumnEncryptionSettingEnabled() && null != destCryptoMeta) {
                bulkJdbcType = this.destColumnMetadata.get(destColIndx).jdbcType;
                bulkPrecision = destPrecision;
                bulkScale = this.destColumnMetadata.get(destColIndx).scale;
            }
            if (8000 < destPrecision) {
                return "varbinary(max)";
            }
            return "varbinary(" + this.destColumnMetadata.get(destColIndx).precision + ")";
        }
        else {
            if (null != this.sourceResultSet && null != this.destColumnMetadata.get(destColIndx).encryptionType && this.copyOptions.isAllowEncryptedValueModifications()) {
                return "varbinary(" + bulkPrecision + ")";
            }
            bulkPrecision = this.validateSourcePrecision(srcPrecision, bulkJdbcType, destPrecision);
            boolean isStreaming;
            if (-15 == bulkJdbcType || -9 == bulkJdbcType || -16 == bulkJdbcType) {
                isStreaming = (4000 < srcPrecision || 4000 < destPrecision);
            }
            else {
                isStreaming = (8000 < srcPrecision || 8000 < destPrecision);
            }
            if (Util.isCharType(bulkJdbcType) && Util.isBinaryType(destSSType)) {
                if (isStreaming) {
                    return "varbinary(max)";
                }
                return destSSType.toString() + "(" + ((8000 < destPrecision) ? "max" : Integer.valueOf(destPrecision)) + ")";
            }
            else {
                switch (bulkJdbcType) {
                    case 4: {
                        return "int";
                    }
                    case 5: {
                        return "smallint";
                    }
                    case -5: {
                        return "bigint";
                    }
                    case -7: {
                        return "bit";
                    }
                    case -6: {
                        return "tinyint";
                    }
                    case 6:
                    case 8: {
                        return "float";
                    }
                    case 7: {
                        return "real";
                    }
                    case -148:
                    case -146:
                    case 3: {
                        return "decimal(" + bulkPrecision + ", " + bulkScale + ")";
                    }
                    case 2: {
                        return "numeric(" + bulkPrecision + ", " + bulkScale + ")";
                    }
                    case -145: {
                        return "char(" + bulkPrecision + ")";
                    }
                    case 1: {
                        if (this.unicodeConversionRequired(bulkJdbcType, destSSType)) {
                            return "nchar(" + bulkPrecision + ")";
                        }
                        return "char(" + bulkPrecision + ")";
                    }
                    case -15: {
                        return "NCHAR(" + bulkPrecision + ")";
                    }
                    case -1:
                    case 12: {
                        if (this.unicodeConversionRequired(bulkJdbcType, destSSType)) {
                            if (isStreaming) {
                                return "nvarchar(max)";
                            }
                            return "nvarchar(" + bulkPrecision + ")";
                        }
                        else {
                            if (isStreaming) {
                                return "varchar(max)";
                            }
                            return "varchar(" + bulkPrecision + ")";
                        }
                        break;
                    }
                    case -16:
                    case -9: {
                        if (isStreaming) {
                            return "NVARCHAR(MAX)";
                        }
                        return "NVARCHAR(" + bulkPrecision + ")";
                    }
                    case -2: {
                        return "binary(" + bulkPrecision + ")";
                    }
                    case -4:
                    case -3: {
                        if (isStreaming) {
                            return "varbinary(max)";
                        }
                        return "varbinary(" + bulkPrecision + ")";
                    }
                    case -151:
                    case -150:
                    case 93: {
                        switch (destSSType) {
                            case SMALLDATETIME: {
                                if (null != this.serverBulkData) {
                                    return "varchar(" + ((0 == bulkPrecision) ? 50 : bulkPrecision) + ")";
                                }
                                return "smalldatetime";
                            }
                            case DATETIME: {
                                if (null != this.serverBulkData) {
                                    return "varchar(" + ((0 == bulkPrecision) ? 50 : bulkPrecision) + ")";
                                }
                                return "datetime";
                            }
                            default: {
                                if (null != this.serverBulkData) {
                                    return "varchar(" + ((0 == bulkPrecision) ? destPrecision : bulkPrecision) + ")";
                                }
                                return "datetime2(" + bulkScale + ")";
                            }
                        }
                        break;
                    }
                    case 91: {
                        if (null != this.serverBulkData) {
                            return "varchar(" + ((0 == bulkPrecision) ? destPrecision : bulkPrecision) + ")";
                        }
                        return "date";
                    }
                    case 92: {
                        if (null != this.serverBulkData) {
                            return "varchar(" + ((0 == bulkPrecision) ? destPrecision : bulkPrecision) + ")";
                        }
                        return "time(" + bulkScale + ")";
                    }
                    case 2013:
                    case 2014: {
                        return "datetimeoffset(" + bulkScale + ")";
                    }
                    case -155: {
                        if (null != this.serverBulkData) {
                            return "varchar(" + ((0 == bulkPrecision) ? destPrecision : bulkPrecision) + ")";
                        }
                        return "datetimeoffset(" + bulkScale + ")";
                    }
                    case -156: {
                        return "sql_variant";
                    }
                    default: {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                        final Object[] msgArgs = { JDBCType.of(bulkJdbcType).toString().toLowerCase(Locale.ENGLISH) };
                        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
                        return null;
                    }
                }
            }
        }
    }
    
    private String createInsertBulkCommand(final TDSWriter tdsWriter) throws SQLServerException {
        final StringBuilder bulkCmd = new StringBuilder();
        final List<String> bulkOptions = new ArrayList<String>();
        String endColumn = " , ";
        bulkCmd.append("INSERT BULK ").append(this.destinationTableName).append(" (");
        for (int i = 0; i < this.columnMappings.size(); ++i) {
            if (i == this.columnMappings.size() - 1) {
                endColumn = " ) ";
            }
            final ColumnMapping colMapping = this.columnMappings.get(i);
            final String columnCollation = this.destColumnMetadata.get(this.columnMappings.get(i).destinationColumnOrdinal).collationName;
            String addCollate = "";
            final String destType = this.getDestTypeFromSrcType(colMapping.sourceColumnOrdinal, colMapping.destinationColumnOrdinal, tdsWriter).toUpperCase(Locale.ENGLISH);
            if (null != columnCollation && columnCollation.trim().length() > 0 && null != destType && (destType.toLowerCase(Locale.ENGLISH).trim().startsWith("char") || destType.toLowerCase(Locale.ENGLISH).trim().startsWith("varchar"))) {
                addCollate = " COLLATE " + columnCollation;
            }
            if (colMapping.destinationColumnName.contains("]")) {
                final String escapedColumnName = colMapping.destinationColumnName.replaceAll("]", "]]");
                bulkCmd.append("[").append(escapedColumnName).append("] ").append(destType).append(addCollate).append(endColumn);
            }
            else {
                bulkCmd.append("[").append(colMapping.destinationColumnName).append("] ").append(destType).append(addCollate).append(endColumn);
            }
        }
        if (this.copyOptions.isCheckConstraints()) {
            bulkOptions.add("CHECK_CONSTRAINTS");
        }
        if (this.copyOptions.isFireTriggers()) {
            bulkOptions.add("FIRE_TRIGGERS");
        }
        if (this.copyOptions.isKeepNulls()) {
            bulkOptions.add("KEEP_NULLS");
        }
        if (this.copyOptions.getBatchSize() > 0) {
            bulkOptions.add("ROWS_PER_BATCH = " + this.copyOptions.getBatchSize());
        }
        if (this.copyOptions.isTableLock()) {
            bulkOptions.add("TABLOCK");
        }
        if (this.copyOptions.isAllowEncryptedValueModifications()) {
            bulkOptions.add("ALLOW_ENCRYPTED_VALUE_MODIFICATIONS");
        }
        final Iterator<String> it = bulkOptions.iterator();
        if (it.hasNext()) {
            bulkCmd.append(" with (");
            while (it.hasNext()) {
                bulkCmd.append(it.next());
                if (it.hasNext()) {
                    bulkCmd.append(", ");
                }
            }
            bulkCmd.append(")");
        }
        if (SQLServerBulkCopy.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkCopy.loggerExternal.finer(this.toString() + " TDSCommand: " + (Object)bulkCmd);
        }
        return bulkCmd.toString();
    }
    
    private boolean doInsertBulk(final TDSCommand command) throws SQLServerException {
        if (this.copyOptions.isUseInternalTransaction()) {
            this.connection.setAutoCommit(false);
        }
        boolean insertRowByRow = false;
        if (null != this.sourceResultSet && this.sourceResultSet instanceof SQLServerResultSet) {
            final SQLServerStatement src_stmt = (SQLServerStatement)((SQLServerResultSet)this.sourceResultSet).getStatement();
            final int resultSetServerCursorId = ((SQLServerResultSet)this.sourceResultSet).getServerCursorId();
            if (this.connection.equals(src_stmt.getConnection()) && 0 != resultSetServerCursorId) {
                insertRowByRow = true;
            }
            if (((SQLServerResultSet)this.sourceResultSet).isForwardOnly()) {
                try {
                    this.sourceResultSet.setFetchSize(1);
                }
                catch (final SQLException e) {
                    SQLServerException.makeFromDriverError(this.connection, this.sourceResultSet, e.getMessage(), e.getSQLState(), true);
                }
            }
        }
        TDSWriter tdsWriter = null;
        boolean moreDataAvailable = false;
        try {
            if (!insertRowByRow) {
                tdsWriter = this.sendBulkCopyCommand(command);
            }
            try {
                moreDataAvailable = this.writeBatchData(tdsWriter, command, insertRowByRow);
            }
            finally {
                tdsWriter = command.getTDSWriter();
            }
        }
        finally {
            if (null == tdsWriter) {
                tdsWriter = command.getTDSWriter();
            }
            tdsWriter.setCryptoMetaData(null);
        }
        if (!insertRowByRow) {
            this.writePacketDataDone(tdsWriter);
            TDSParser.parse(command.startResponse(), command.getLogContext());
        }
        if (this.copyOptions.isUseInternalTransaction()) {
            this.connection.commit();
        }
        return moreDataAvailable;
    }
    
    private TDSWriter sendBulkCopyCommand(final TDSCommand command) throws SQLServerException {
        TDSWriter tdsWriter = command.startRequest((byte)1);
        final String bulkCmd = this.createInsertBulkCommand(tdsWriter);
        tdsWriter.sendEnclavePackage(null, null);
        tdsWriter.writeString(bulkCmd);
        TDSParser.parse(command.startResponse(), command.getLogContext());
        tdsWriter = command.startRequest((byte)7);
        this.writeColumnMetaData(tdsWriter);
        return tdsWriter;
    }
    
    private void writePacketDataDone(final TDSWriter tdsWriter) throws SQLServerException {
        tdsWriter.writeByte((byte)(-3));
        tdsWriter.writeLong(0L);
        tdsWriter.writeInt(0);
    }
    
    private void throwInvalidArgument(final String argument) throws SQLServerException {
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
        final Object[] msgArgs = { argument };
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
    }
    
    private void writeToServer() throws SQLServerException {
        if (this.connection.isClosed()) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_connectionIsClosed"), "08003", false);
        }
        final long start = System.currentTimeMillis();
        if (SQLServerBulkCopy.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkCopy.loggerExternal.finer(this.toString() + " Start writeToServer: " + start);
        }
        this.getDestinationMetadata();
        this.getSourceMetadata();
        this.validateColumnMappings();
        this.sendBulkLoadBCP();
        final long end = System.currentTimeMillis();
        if (SQLServerBulkCopy.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerBulkCopy.loggerExternal.finer(this.toString() + " End writeToServer: " + end);
            final int seconds = (int)((end - start) / 1000L);
            SQLServerBulkCopy.loggerExternal.finer(this.toString() + "Time elapsed: " + seconds + " seconds");
        }
    }
    
    private void validateStringBinaryLengths(final Object colValue, final int srcCol, final int destCol) throws SQLServerException {
        final int destPrecision = this.destColumnMetadata.get(destCol).precision;
        final int srcJdbcType = this.srcColumnMetadata.get(srcCol).jdbcType;
        final SSType destSSType = this.destColumnMetadata.get(destCol).ssType;
        if ((Util.isCharType(srcJdbcType) && Util.isCharType(destSSType)) || (Util.isBinaryType(srcJdbcType) && Util.isBinaryType(destSSType))) {
            int sourcePrecision;
            if (colValue instanceof String) {
                if (Util.isBinaryType(destSSType)) {
                    sourcePrecision = ((String)colValue).getBytes().length / 2;
                }
                else {
                    sourcePrecision = ((String)colValue).length();
                }
            }
            else {
                if (!(colValue instanceof byte[])) {
                    return;
                }
                sourcePrecision = ((byte[])colValue).length;
            }
            if (sourcePrecision > destPrecision) {
                final String srcType = JDBCType.of(srcJdbcType) + "(" + sourcePrecision + ")";
                final String destType = destSSType.toString() + "(" + destPrecision + ")";
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                final Object[] msgArgs = { srcType, destType };
                throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
            }
        }
    }
    
    private void getDestinationMetadata() throws SQLServerException {
        if (null == this.destinationTableName) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_invalidDestinationTable"), null, false);
        }
        final String escapedDestinationTableName = Util.escapeSingleQuotes(this.destinationTableName);
        SQLServerResultSet rs = null;
        SQLServerStatement stmt = null;
        String metaDataQuery = null;
        try {
            if (null != this.destinationTableMetadata) {
                rs = (SQLServerResultSet)this.destinationTableMetadata;
            }
            else {
                stmt = (SQLServerStatement)this.connection.createStatement(1003, 1007, this.connection.getHoldability(), this.stmtColumnEncriptionSetting);
                rs = stmt.executeQueryInternal("sp_executesql N'SET FMTONLY ON SELECT * FROM " + escapedDestinationTableName + " '");
            }
            this.destColumnCount = rs.getMetaData().getColumnCount();
            this.destColumnMetadata = new HashMap<Integer, BulkColumnMetaData>();
            this.destCekTable = rs.getCekTable();
            if (!this.connection.getServerSupportsColumnEncryption()) {
                metaDataQuery = "select collation_name from sys.columns where object_id=OBJECT_ID('" + escapedDestinationTableName + "') order by column_id ASC";
            }
            else {
                metaDataQuery = "select collation_name, encryption_type from sys.columns where object_id=OBJECT_ID('" + escapedDestinationTableName + "') order by column_id ASC";
            }
            try (final SQLServerStatement statementMoreMetadata = (SQLServerStatement)this.connection.createStatement();
                 final SQLServerResultSet rsMoreMetaData = statementMoreMetadata.executeQueryInternal(metaDataQuery)) {
                for (int i = 1; i <= this.destColumnCount; ++i) {
                    if (rsMoreMetaData.next()) {
                        String bulkCopyEncryptionType = null;
                        if (this.connection.getServerSupportsColumnEncryption()) {
                            bulkCopyEncryptionType = rsMoreMetaData.getString("encryption_type");
                        }
                        this.destColumnMetadata.put(i, new BulkColumnMetaData(rs.getColumn(i), rsMoreMetaData.getString("collation_name"), bulkCopyEncryptionType));
                    }
                    else {
                        this.destColumnMetadata.put(i, new BulkColumnMetaData(rs.getColumn(i)));
                    }
                }
            }
        }
        catch (final SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), e);
        }
        finally {
            if (null != rs) {
                rs.close();
            }
            if (null != stmt) {
                stmt.close();
            }
        }
    }
    
    private void getSourceMetadata() throws SQLServerException {
        this.srcColumnMetadata = new HashMap<Integer, BulkColumnMetaData>();
        if (null != this.sourceResultSet) {
            try {
                this.srcColumnCount = this.sourceResultSetMetaData.getColumnCount();
                for (int i = 1; i <= this.srcColumnCount; ++i) {
                    this.srcColumnMetadata.put(i, new BulkColumnMetaData(this.sourceResultSetMetaData.getColumnName(i), 0 != this.sourceResultSetMetaData.isNullable(i), this.sourceResultSetMetaData.getPrecision(i), this.sourceResultSetMetaData.getScale(i), this.sourceResultSetMetaData.getColumnType(i), null));
                }
                return;
            }
            catch (final SQLException e) {
                throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), e);
            }
        }
        if (null == this.serverBulkData) {
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), (Throwable)null);
        }
        final Set<Integer> columnOrdinals = this.serverBulkData.getColumnOrdinals();
        if (null == columnOrdinals || 0 == columnOrdinals.size()) {
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), (Throwable)null);
        }
        this.srcColumnCount = columnOrdinals.size();
        for (final Integer columnOrdinal : columnOrdinals) {
            final int currentColumn = columnOrdinal;
            this.srcColumnMetadata.put(currentColumn, new BulkColumnMetaData(this.serverBulkData.getColumnName(currentColumn), true, this.serverBulkData.getPrecision(currentColumn), this.serverBulkData.getScale(currentColumn), this.serverBulkData.getColumnType(currentColumn), (this.serverBulkData instanceof SQLServerBulkCSVFileRecord) ? ((SQLServerBulkCSVFileRecord)this.serverBulkData).getColumnDateTimeFormatter(currentColumn) : null));
        }
    }
    
    private int validateSourcePrecision(int srcPrecision, final int srcJdbcType, final int destPrecision) {
        if (1 > srcPrecision && Util.isCharType(srcJdbcType)) {
            srcPrecision = destPrecision;
        }
        return srcPrecision;
    }
    
    private void validateColumnMappings() throws SQLServerException {
        try {
            if (this.columnMappings.isEmpty()) {
                if (this.destColumnCount != this.srcColumnCount) {
                    throw new SQLServerException(SQLServerException.getErrString("R_schemaMismatch"), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                }
                for (int i = 1; i <= this.srcColumnCount; ++i) {
                    if (!this.destColumnMetadata.get(i).isIdentity || this.copyOptions.isKeepIdentity()) {
                        final ColumnMapping cm = new ColumnMapping(i, i);
                        cm.destinationColumnName = this.destColumnMetadata.get(i).columnName;
                        this.columnMappings.add(cm);
                    }
                }
                if (null != this.serverBulkData) {
                    final Set<Integer> columnOrdinals = this.serverBulkData.getColumnOrdinals();
                    final Iterator<Integer> columnsIterator = columnOrdinals.iterator();
                    int j = 1;
                    while (columnsIterator.hasNext()) {
                        final int currentOrdinal = columnsIterator.next();
                        if (j != currentOrdinal) {
                            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                            final Object[] msgArgs = { currentOrdinal };
                            throw new SQLServerException(form.format(msgArgs), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                        }
                        ++j;
                    }
                }
            }
            else {
                int numMappings = this.columnMappings.size();
                for (int k = 0; k < numMappings; ++k) {
                    final ColumnMapping cm2 = this.columnMappings.get(k);
                    if (-1 == cm2.destinationColumnOrdinal) {
                        boolean foundColumn = false;
                        for (int l = 1; l <= this.destColumnCount; ++l) {
                            if (this.destColumnMetadata.get(l).columnName.equals(cm2.destinationColumnName)) {
                                foundColumn = true;
                                cm2.destinationColumnOrdinal = l;
                                break;
                            }
                        }
                        if (!foundColumn) {
                            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                            final Object[] msgArgs2 = { cm2.destinationColumnName };
                            throw new SQLServerException(form2.format(msgArgs2), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                        }
                    }
                    else {
                        if (0 > cm2.destinationColumnOrdinal || this.destColumnCount < cm2.destinationColumnOrdinal) {
                            final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                            final Object[] msgArgs3 = { cm2.destinationColumnOrdinal };
                            throw new SQLServerException(form3.format(msgArgs3), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                        }
                        cm2.destinationColumnName = this.destColumnMetadata.get(cm2.destinationColumnOrdinal).columnName;
                    }
                }
                for (int k = 0; k < numMappings; ++k) {
                    final ColumnMapping cm2 = this.columnMappings.get(k);
                    if (-1 == cm2.sourceColumnOrdinal) {
                        boolean foundColumn = false;
                        if (null != this.sourceResultSet) {
                            for (int columns = this.sourceResultSetMetaData.getColumnCount(), m = 1; m <= columns; ++m) {
                                if (this.sourceResultSetMetaData.getColumnName(m).equals(cm2.sourceColumnName)) {
                                    foundColumn = true;
                                    cm2.sourceColumnOrdinal = m;
                                    break;
                                }
                            }
                        }
                        else {
                            final Set<Integer> columnOrdinals2 = this.serverBulkData.getColumnOrdinals();
                            for (final Integer currentColumn : columnOrdinals2) {
                                if (this.serverBulkData.getColumnName(currentColumn).equals(cm2.sourceColumnName)) {
                                    foundColumn = true;
                                    cm2.sourceColumnOrdinal = currentColumn;
                                    break;
                                }
                            }
                        }
                        if (!foundColumn) {
                            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                            final Object[] msgArgs2 = { cm2.sourceColumnName };
                            throw new SQLServerException(form2.format(msgArgs2), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                        }
                    }
                    else {
                        boolean columnOutOfRange = true;
                        if (null != this.sourceResultSet) {
                            final int columns = this.sourceResultSetMetaData.getColumnCount();
                            if (0 < cm2.sourceColumnOrdinal && columns >= cm2.sourceColumnOrdinal) {
                                columnOutOfRange = false;
                            }
                        }
                        else if (this.srcColumnMetadata.containsKey(cm2.sourceColumnOrdinal)) {
                            columnOutOfRange = false;
                        }
                        if (columnOutOfRange) {
                            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_invalidColumn"));
                            final Object[] msgArgs2 = { cm2.sourceColumnOrdinal };
                            throw new SQLServerException(form2.format(msgArgs2), SQLState.COL_NOT_FOUND, DriverError.NOT_SET, null);
                        }
                    }
                    if (this.destColumnMetadata.get(cm2.destinationColumnOrdinal).isIdentity && !this.copyOptions.isKeepIdentity()) {
                        this.columnMappings.remove(k);
                        --numMappings;
                        --k;
                    }
                }
            }
        }
        catch (final SQLException e) {
            if (e instanceof SQLServerException && null != e.getSQLState() && e.getSQLState().equals(SQLState.COL_NOT_FOUND.getSQLStateCode())) {
                throw (SQLServerException)e;
            }
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveColMeta"), e);
        }
        if (this.columnMappings.isEmpty()) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_BulkColumnMappingsIsEmpty"), null, 0, false);
        }
    }
    
    private void writeNullToTdsWriter(final TDSWriter tdsWriter, final int srcJdbcType, final boolean isStreaming) throws SQLServerException {
        switch (srcJdbcType) {
            case -16:
            case -15:
            case -9:
            case -4:
            case -3:
            case -2:
            case -1:
            case 1:
            case 12: {
                if (isStreaming) {
                    tdsWriter.writeLong(-1L);
                }
                else {
                    tdsWriter.writeByte((byte)(-1));
                    tdsWriter.writeByte((byte)(-1));
                }
                return;
            }
            case -155:
            case -7:
            case -6:
            case -5:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 91:
            case 92:
            case 93:
            case 2013:
            case 2014: {
                tdsWriter.writeByte((byte)0);
                return;
            }
            case -156: {
                tdsWriter.writeInt(0);
                return;
            }
            default: {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                final Object[] msgArgs = { JDBCType.of(srcJdbcType).toString().toLowerCase(Locale.ENGLISH) };
                SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
            }
        }
    }
    
    private void writeColumnToTdsWriter(final TDSWriter tdsWriter, int bulkPrecision, int bulkScale, int bulkJdbcType, final boolean bulkNullable, final int srcColOrdinal, final int destColOrdinal, final boolean isStreaming, final Object colValue) throws SQLServerException {
        final SSType destSSType = this.destColumnMetadata.get(destColOrdinal).ssType;
        bulkPrecision = this.validateSourcePrecision(bulkPrecision, bulkJdbcType, this.destColumnMetadata.get(destColOrdinal).precision);
        final CryptoMetadata sourceCryptoMeta = this.srcColumnMetadata.get(srcColOrdinal).cryptoMeta;
        if ((null != this.destColumnMetadata.get(destColOrdinal).encryptionType && this.copyOptions.isAllowEncryptedValueModifications()) || null != this.destColumnMetadata.get(destColOrdinal).cryptoMeta) {
            bulkJdbcType = -3;
        }
        else if (null != sourceCryptoMeta) {
            bulkJdbcType = this.destColumnMetadata.get(destColOrdinal).jdbcType;
            bulkScale = this.destColumnMetadata.get(destColOrdinal).scale;
        }
        else if (null != this.serverBulkData) {
            switch (bulkJdbcType) {
                case -155:
                case 91:
                case 92:
                case 93: {
                    bulkJdbcType = 12;
                    break;
                }
            }
        }
        try {
            switch (bulkJdbcType) {
                case 4: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)4);
                    }
                    tdsWriter.writeInt((int)colValue);
                    break;
                }
                case 5: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)2);
                    }
                    tdsWriter.writeShort(((Number)colValue).shortValue());
                    break;
                }
                case -5: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)8);
                    }
                    tdsWriter.writeLong((long)colValue);
                    break;
                }
                case -7: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)1);
                    }
                    tdsWriter.writeByte((byte)(((boolean)colValue) ? 1 : 0));
                    break;
                }
                case -6: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)1);
                    }
                    tdsWriter.writeByte((byte)(((Number)colValue).shortValue() & 0xFF));
                    break;
                }
                case 6: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)8);
                    }
                    tdsWriter.writeDouble((float)colValue);
                    break;
                }
                case 8: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)8);
                    }
                    tdsWriter.writeDouble((double)colValue);
                    break;
                }
                case 7: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkNullable) {
                        tdsWriter.writeByte((byte)4);
                    }
                    tdsWriter.writeReal((float)colValue);
                    break;
                }
                case -148:
                case -146:
                case 2:
                case 3: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (bulkPrecision < Util.getValueLengthBaseOnJavaType(colValue, JavaType.of(colValue), null, null, JDBCType.of(bulkJdbcType))) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueOutOfRange"));
                        final Object[] msgArgs = { SSType.DECIMAL };
                        throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_LENGTH_MISMATCH, DriverError.NOT_SET, null);
                    }
                    tdsWriter.writeBigDecimal((BigDecimal)colValue, bulkJdbcType, bulkPrecision, bulkScale);
                    break;
                }
                case -145:
                case -1:
                case 1:
                case 12: {
                    if (isStreaming) {
                        if (null == colValue) {
                            this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                            break;
                        }
                        tdsWriter.writeLong(-2L);
                        try {
                            Reader reader;
                            if (colValue instanceof Reader) {
                                reader = (Reader)colValue;
                            }
                            else {
                                reader = new StringReader(colValue.toString());
                            }
                            if (this.unicodeConversionRequired(bulkJdbcType, destSSType)) {
                                tdsWriter.writeReader(reader, -1L, true);
                            }
                            else if (SSType.BINARY == destSSType || SSType.VARBINARY == destSSType || SSType.VARBINARYMAX == destSSType || SSType.IMAGE == destSSType) {
                                tdsWriter.writeNonUnicodeReader(reader, -1L, true, null);
                            }
                            else {
                                final SQLCollation destCollation = this.destColumnMetadata.get(destColOrdinal).collation;
                                if (null != destCollation) {
                                    tdsWriter.writeNonUnicodeReader(reader, -1L, false, destCollation.getCharset());
                                }
                                else {
                                    tdsWriter.writeNonUnicodeReader(reader, -1L, false, null);
                                }
                            }
                            reader.close();
                            break;
                        }
                        catch (final IOException e) {
                            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                        }
                    }
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    final String colValueStr = colValue.toString();
                    if (this.unicodeConversionRequired(bulkJdbcType, destSSType)) {
                        final int stringLength = colValue.toString().length();
                        final byte[] typevarlen = { (byte)(2 * stringLength & 0xFF), (byte)(2 * stringLength >> 8 & 0xFF) };
                        tdsWriter.writeBytes(typevarlen);
                        tdsWriter.writeString(colValue.toString());
                    }
                    else if (SSType.BINARY == destSSType || SSType.VARBINARY == destSSType) {
                        byte[] bytes = null;
                        try {
                            bytes = ParameterUtils.HexToBin(colValueStr);
                        }
                        catch (final SQLServerException e2) {
                            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e2);
                        }
                        tdsWriter.writeShort((short)bytes.length);
                        tdsWriter.writeBytes(bytes);
                    }
                    else {
                        tdsWriter.writeShort((short)colValueStr.length());
                        final SQLCollation destCollation = this.destColumnMetadata.get(destColOrdinal).collation;
                        if (null != destCollation) {
                            tdsWriter.writeBytes(colValueStr.getBytes(this.destColumnMetadata.get(destColOrdinal).collation.getCharset()));
                        }
                        else {
                            tdsWriter.writeBytes(colValueStr.getBytes());
                        }
                    }
                    break;
                }
                case -16:
                case -15:
                case -9: {
                    if (isStreaming) {
                        if (null == colValue) {
                            this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                            break;
                        }
                        tdsWriter.writeLong(-2L);
                        try {
                            Reader reader;
                            if (colValue instanceof Reader) {
                                reader = (Reader)colValue;
                            }
                            else {
                                reader = new StringReader(colValue.toString());
                            }
                            tdsWriter.writeReader(reader, -1L, true);
                            reader.close();
                            break;
                        }
                        catch (final IOException e) {
                            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                        }
                    }
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    final int stringLength2 = colValue.toString().length();
                    final byte[] typevarlen2 = { (byte)(2 * stringLength2 & 0xFF), (byte)(2 * stringLength2 >> 8 & 0xFF) };
                    tdsWriter.writeBytes(typevarlen2);
                    tdsWriter.writeString(colValue.toString());
                    break;
                }
                case -4:
                case -3:
                case -2: {
                    if (isStreaming) {
                        if (null == colValue) {
                            this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                            break;
                        }
                        tdsWriter.writeLong(-2L);
                        try {
                            InputStream iStream;
                            if (colValue instanceof InputStream) {
                                iStream = (InputStream)colValue;
                            }
                            else if (colValue instanceof byte[]) {
                                iStream = new ByteArrayInputStream((byte[])colValue);
                            }
                            else {
                                iStream = new ByteArrayInputStream(ParameterUtils.HexToBin(colValue.toString()));
                            }
                            tdsWriter.writeStream(iStream, -1L, true);
                            iStream.close();
                            break;
                        }
                        catch (final IOException e) {
                            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                        }
                    }
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    byte[] srcBytes;
                    if (colValue instanceof byte[]) {
                        srcBytes = (byte[])colValue;
                    }
                    else {
                        try {
                            srcBytes = ParameterUtils.HexToBin(colValue.toString());
                        }
                        catch (final SQLServerException e3) {
                            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e3);
                        }
                    }
                    tdsWriter.writeShort((short)srcBytes.length);
                    tdsWriter.writeBytes(srcBytes);
                    break;
                }
                case -151:
                case -150:
                case 93: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    switch (destSSType) {
                        case SMALLDATETIME: {
                            if (bulkNullable) {
                                tdsWriter.writeByte((byte)4);
                            }
                            tdsWriter.writeSmalldatetime(colValue.toString());
                            break;
                        }
                        case DATETIME: {
                            if (bulkNullable) {
                                tdsWriter.writeByte((byte)8);
                            }
                            tdsWriter.writeDatetime(colValue.toString());
                            break;
                        }
                        default: {
                            if (bulkNullable) {
                                if (2 >= bulkScale) {
                                    tdsWriter.writeByte((byte)6);
                                }
                                else if (4 >= bulkScale) {
                                    tdsWriter.writeByte((byte)7);
                                }
                                else {
                                    tdsWriter.writeByte((byte)8);
                                }
                            }
                            final String timeStampValue = colValue.toString();
                            tdsWriter.writeTime(Timestamp.valueOf(timeStampValue), bulkScale);
                            tdsWriter.writeDate(timeStampValue.substring(0, timeStampValue.lastIndexOf(32)));
                            break;
                        }
                    }
                    break;
                }
                case 91: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    tdsWriter.writeByte((byte)3);
                    tdsWriter.writeDate(colValue.toString());
                    break;
                }
                case 92: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (2 >= bulkScale) {
                        tdsWriter.writeByte((byte)3);
                    }
                    else if (4 >= bulkScale) {
                        tdsWriter.writeByte((byte)4);
                    }
                    else {
                        tdsWriter.writeByte((byte)5);
                    }
                    tdsWriter.writeTime((Timestamp)colValue, bulkScale);
                    break;
                }
                case 2013: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (2 >= bulkScale) {
                        tdsWriter.writeByte((byte)8);
                    }
                    else if (4 >= bulkScale) {
                        tdsWriter.writeByte((byte)9);
                    }
                    else {
                        tdsWriter.writeByte((byte)10);
                    }
                    tdsWriter.writeOffsetTimeWithTimezone((OffsetTime)colValue, bulkScale);
                    break;
                }
                case 2014: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (2 >= bulkScale) {
                        tdsWriter.writeByte((byte)8);
                    }
                    else if (4 >= bulkScale) {
                        tdsWriter.writeByte((byte)9);
                    }
                    else {
                        tdsWriter.writeByte((byte)10);
                    }
                    tdsWriter.writeOffsetDateTimeWithTimezone((OffsetDateTime)colValue, bulkScale);
                    break;
                }
                case -155: {
                    if (null == colValue) {
                        this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
                        break;
                    }
                    if (2 >= bulkScale) {
                        tdsWriter.writeByte((byte)8);
                    }
                    else if (4 >= bulkScale) {
                        tdsWriter.writeByte((byte)9);
                    }
                    else {
                        tdsWriter.writeByte((byte)10);
                    }
                    tdsWriter.writeDateTimeOffset(colValue, bulkScale, destSSType);
                    break;
                }
                case -156: {
                    final boolean isShiloh = 8 >= this.connection.getServerMajorVersion();
                    if (isShiloh) {
                        final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_SQLVariantSupport"));
                        throw new SQLServerException(null, form2.format(new Object[0]), null, 0, false);
                    }
                    this.writeSqlVariant(tdsWriter, colValue, this.sourceResultSet, srcColOrdinal, destColOrdinal, bulkJdbcType, bulkScale, isStreaming);
                    break;
                }
                default: {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                    final Object[] msgArgs2 = { JDBCType.of(bulkJdbcType).toString().toLowerCase(Locale.ENGLISH) };
                    SQLServerException.makeFromDriverError(null, null, form2.format(msgArgs2), null, true);
                    break;
                }
            }
        }
        catch (final ClassCastException ex) {
            if (null == colValue) {
                this.throwInvalidArgument("colValue");
            }
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_errorConvertingValue"));
            final Object[] msgArgs2 = { colValue.getClass().getSimpleName(), JDBCType.of(bulkJdbcType) };
            throw new SQLServerException(form2.format(msgArgs2), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, ex);
        }
    }
    
    private void writeSqlVariant(final TDSWriter tdsWriter, Object colValue, final ResultSet sourceResultSet, final int srcColOrdinal, final int destColOrdinal, final int bulkJdbcType, final int bulkScale, final boolean isStreaming) throws SQLServerException {
        if (null == colValue) {
            this.writeNullToTdsWriter(tdsWriter, bulkJdbcType, isStreaming);
            return;
        }
        final SqlVariant variantType = ((SQLServerResultSet)sourceResultSet).getVariantInternalType(srcColOrdinal);
        final int baseType = variantType.getBaseType();
        if (TDSType.TIMEN == TDSType.valueOf(baseType)) {
            variantType.setIsBaseTypeTimeValue(true);
            ((SQLServerResultSet)sourceResultSet).setInternalVariantType(srcColOrdinal, variantType);
            colValue = ((SQLServerResultSet)sourceResultSet).getObject(srcColOrdinal);
        }
        switch (TDSType.valueOf(baseType)) {
            case INT8: {
                this.writeBulkCopySqlVariantHeader(10, TDSType.INT8.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeLong(Long.valueOf(colValue.toString()));
                break;
            }
            case INT4: {
                this.writeBulkCopySqlVariantHeader(6, TDSType.INT4.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeInt(Integer.valueOf(colValue.toString()));
                break;
            }
            case INT2: {
                this.writeBulkCopySqlVariantHeader(4, TDSType.INT2.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeShort(Short.valueOf(colValue.toString()));
                break;
            }
            case INT1: {
                this.writeBulkCopySqlVariantHeader(3, TDSType.INT1.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeByte(Byte.valueOf(colValue.toString()));
                break;
            }
            case FLOAT8: {
                this.writeBulkCopySqlVariantHeader(10, TDSType.FLOAT8.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeDouble(Double.valueOf(colValue.toString()));
                break;
            }
            case FLOAT4: {
                this.writeBulkCopySqlVariantHeader(6, TDSType.FLOAT4.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeReal(Float.valueOf(colValue.toString()));
                break;
            }
            case MONEY8: {
                this.writeBulkCopySqlVariantHeader(21, TDSType.DECIMALN.byteValue(), (byte)2, tdsWriter);
                tdsWriter.writeByte((byte)38);
                tdsWriter.writeByte((byte)4);
                tdsWriter.writeSqlVariantInternalBigDecimal((BigDecimal)colValue, bulkJdbcType);
                break;
            }
            case MONEY4: {
                this.writeBulkCopySqlVariantHeader(21, TDSType.DECIMALN.byteValue(), (byte)2, tdsWriter);
                tdsWriter.writeByte((byte)38);
                tdsWriter.writeByte((byte)4);
                tdsWriter.writeSqlVariantInternalBigDecimal((BigDecimal)colValue, bulkJdbcType);
                break;
            }
            case BIT1: {
                this.writeBulkCopySqlVariantHeader(3, TDSType.BIT1.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeByte((byte)(((boolean)colValue) ? 1 : 0));
                break;
            }
            case DATEN: {
                this.writeBulkCopySqlVariantHeader(5, TDSType.DATEN.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeDate(colValue.toString());
                break;
            }
            case TIMEN: {
                final int timeBulkScale = variantType.getScale();
                int timeHeaderLength = 8;
                if (2 >= timeBulkScale) {
                    timeHeaderLength = 6;
                }
                else if (4 >= timeBulkScale) {
                    timeHeaderLength = 7;
                }
                else {
                    timeHeaderLength = 8;
                }
                this.writeBulkCopySqlVariantHeader(timeHeaderLength, TDSType.TIMEN.byteValue(), (byte)1, tdsWriter);
                tdsWriter.writeByte((byte)timeBulkScale);
                tdsWriter.writeTime((Timestamp)colValue, timeBulkScale);
                break;
            }
            case DATETIME8: {
                this.writeBulkCopySqlVariantHeader(10, TDSType.DATETIME8.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeDatetime(colValue.toString());
                break;
            }
            case DATETIME4: {
                this.writeBulkCopySqlVariantHeader(10, TDSType.DATETIME8.byteValue(), (byte)0, tdsWriter);
                tdsWriter.writeDatetime(colValue.toString());
                break;
            }
            case DATETIME2N: {
                this.writeBulkCopySqlVariantHeader(10, TDSType.DATETIME2N.byteValue(), (byte)1, tdsWriter);
                tdsWriter.writeByte((byte)3);
                final String timeStampValue = colValue.toString();
                tdsWriter.writeTime(Timestamp.valueOf(timeStampValue), 3);
                tdsWriter.writeDate(timeStampValue.substring(0, timeStampValue.lastIndexOf(32)));
                break;
            }
            case BIGCHAR: {
                final int length = colValue.toString().length();
                this.writeBulkCopySqlVariantHeader(9 + length, TDSType.BIGCHAR.byteValue(), (byte)7, tdsWriter);
                tdsWriter.writeCollationForSqlVariant(variantType);
                tdsWriter.writeShort((short)length);
                final SQLCollation destCollation = this.destColumnMetadata.get(destColOrdinal).collation;
                if (null != destCollation) {
                    tdsWriter.writeBytes(colValue.toString().getBytes(this.destColumnMetadata.get(destColOrdinal).collation.getCharset()));
                    break;
                }
                tdsWriter.writeBytes(colValue.toString().getBytes());
                break;
            }
            case BIGVARCHAR: {
                final int length = colValue.toString().length();
                this.writeBulkCopySqlVariantHeader(9 + length, TDSType.BIGVARCHAR.byteValue(), (byte)7, tdsWriter);
                tdsWriter.writeCollationForSqlVariant(variantType);
                tdsWriter.writeShort((short)length);
                final SQLCollation destCollation = this.destColumnMetadata.get(destColOrdinal).collation;
                if (null != destCollation) {
                    tdsWriter.writeBytes(colValue.toString().getBytes(this.destColumnMetadata.get(destColOrdinal).collation.getCharset()));
                    break;
                }
                tdsWriter.writeBytes(colValue.toString().getBytes());
                break;
            }
            case NCHAR: {
                final int length = colValue.toString().length() * 2;
                this.writeBulkCopySqlVariantHeader(9 + length, TDSType.NCHAR.byteValue(), (byte)7, tdsWriter);
                tdsWriter.writeCollationForSqlVariant(variantType);
                final int stringLength = colValue.toString().length();
                final byte[] typevarlen = { (byte)(2 * stringLength & 0xFF), (byte)(2 * stringLength >> 8 & 0xFF) };
                tdsWriter.writeBytes(typevarlen);
                tdsWriter.writeString(colValue.toString());
                break;
            }
            case NVARCHAR: {
                final int length = colValue.toString().length() * 2;
                this.writeBulkCopySqlVariantHeader(9 + length, TDSType.NVARCHAR.byteValue(), (byte)7, tdsWriter);
                tdsWriter.writeCollationForSqlVariant(variantType);
                final int stringLength = colValue.toString().length();
                final byte[] typevarlen = { (byte)(2 * stringLength & 0xFF), (byte)(2 * stringLength >> 8 & 0xFF) };
                tdsWriter.writeBytes(typevarlen);
                tdsWriter.writeString(colValue.toString());
                break;
            }
            case GUID: {
                final int length = colValue.toString().length();
                this.writeBulkCopySqlVariantHeader(9 + length, TDSType.BIGCHAR.byteValue(), (byte)7, tdsWriter);
                final SQLCollation collation = (null != this.destColumnMetadata.get(srcColOrdinal).collation) ? this.destColumnMetadata.get(srcColOrdinal).collation : this.connection.getDatabaseCollation();
                variantType.setCollation(collation);
                tdsWriter.writeCollationForSqlVariant(variantType);
                tdsWriter.writeShort((short)length);
                final SQLCollation destCollation = this.destColumnMetadata.get(destColOrdinal).collation;
                if (null != destCollation) {
                    tdsWriter.writeBytes(colValue.toString().getBytes(this.destColumnMetadata.get(destColOrdinal).collation.getCharset()));
                    break;
                }
                tdsWriter.writeBytes(colValue.toString().getBytes());
                break;
            }
            case BIGBINARY: {
                final byte[] b = (byte[])colValue;
                final int length = b.length;
                this.writeBulkCopySqlVariantHeader(4 + length, TDSType.BIGVARBINARY.byteValue(), (byte)2, tdsWriter);
                tdsWriter.writeShort((short)variantType.getMaxLength());
                byte[] srcBytes;
                if (colValue instanceof byte[]) {
                    srcBytes = (byte[])colValue;
                }
                else {
                    try {
                        srcBytes = ParameterUtils.HexToBin(colValue.toString());
                    }
                    catch (final SQLServerException e) {
                        throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                    }
                }
                tdsWriter.writeBytes(srcBytes);
                break;
            }
            case BIGVARBINARY: {
                final byte[] b = (byte[])colValue;
                final int length = b.length;
                this.writeBulkCopySqlVariantHeader(4 + length, TDSType.BIGVARBINARY.byteValue(), (byte)2, tdsWriter);
                tdsWriter.writeShort((short)variantType.getMaxLength());
                byte[] srcBytes;
                if (colValue instanceof byte[]) {
                    srcBytes = (byte[])colValue;
                }
                else {
                    try {
                        srcBytes = ParameterUtils.HexToBin(colValue.toString());
                    }
                    catch (final SQLServerException e) {
                        throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
                    }
                }
                tdsWriter.writeBytes(srcBytes);
                break;
            }
            default: {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                final Object[] msgArgs = { JDBCType.of(bulkJdbcType).toString().toLowerCase(Locale.ENGLISH) };
                SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
                break;
            }
        }
    }
    
    private void writeBulkCopySqlVariantHeader(final int length, final byte tdsType, final byte probBytes, final TDSWriter tdsWriter) throws SQLServerException {
        tdsWriter.writeInt(length);
        tdsWriter.writeByte(tdsType);
        tdsWriter.writeByte(probBytes);
    }
    
    private Object readColumnFromResultSet(final int srcColOrdinal, int srcJdbcType, final boolean isStreaming, final boolean isDestEncrypted) throws SQLServerException {
        CryptoMetadata srcCryptoMeta = null;
        if (this.sourceResultSet instanceof SQLServerResultSet && null != (srcCryptoMeta = ((SQLServerResultSet)this.sourceResultSet).getterGetColumn(srcColOrdinal).getCryptoMetadata())) {
            srcJdbcType = srcCryptoMeta.baseTypeInfo.getSSType().getJDBCType().asJavaSqlType();
            final BulkColumnMetaData temp = this.srcColumnMetadata.get(srcColOrdinal);
            this.srcColumnMetadata.put(srcColOrdinal, new BulkColumnMetaData(temp, srcCryptoMeta));
        }
        try {
            switch (srcJdbcType) {
                case -7:
                case -6:
                case -5:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8: {
                    return this.sourceResultSet.getObject(srcColOrdinal);
                }
                case -148:
                case -146:
                case 2:
                case 3: {
                    return this.sourceResultSet.getBigDecimal(srcColOrdinal);
                }
                case -145:
                case -1:
                case 1:
                case 12: {
                    if (isStreaming && !isDestEncrypted && null == srcCryptoMeta) {
                        return this.sourceResultSet.getCharacterStream(srcColOrdinal);
                    }
                    return this.sourceResultSet.getString(srcColOrdinal);
                }
                case -16:
                case -15:
                case -9: {
                    if (isStreaming && !isDestEncrypted && null == srcCryptoMeta) {
                        return this.sourceResultSet.getNCharacterStream(srcColOrdinal);
                    }
                    return this.sourceResultSet.getObject(srcColOrdinal);
                }
                case -4:
                case -3:
                case -2: {
                    if (isStreaming && !isDestEncrypted && null == srcCryptoMeta) {
                        return this.sourceResultSet.getBinaryStream(srcColOrdinal);
                    }
                    return this.sourceResultSet.getBytes(srcColOrdinal);
                }
                case -151:
                case -150:
                case 92:
                case 93: {
                    return this.sourceResultSet.getTimestamp(srcColOrdinal);
                }
                case 91: {
                    return this.sourceResultSet.getDate(srcColOrdinal);
                }
                case -155: {
                    return ((SQLServerResultSet)this.sourceResultSet).getDateTimeOffset(srcColOrdinal);
                }
                case -156: {
                    return this.sourceResultSet.getObject(srcColOrdinal);
                }
                default: {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                    final Object[] msgArgs = { JDBCType.of(srcJdbcType).toString().toLowerCase(Locale.ENGLISH) };
                    SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
                    return null;
                }
            }
        }
        catch (final SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
        }
    }
    
    private void writeColumn(final TDSWriter tdsWriter, final int srcColOrdinal, final int destColOrdinal, Object colValue) throws SQLServerException {
        SSType destSSType = null;
        final int srcPrecision = this.srcColumnMetadata.get(srcColOrdinal).precision;
        final int srcScale = this.srcColumnMetadata.get(srcColOrdinal).scale;
        final int srcJdbcType = this.srcColumnMetadata.get(srcColOrdinal).jdbcType;
        final boolean srcNullable = this.srcColumnMetadata.get(srcColOrdinal).isNullable;
        final int destPrecision = this.destColumnMetadata.get(destColOrdinal).precision;
        boolean isStreaming;
        if (-15 == srcJdbcType || -9 == srcJdbcType || -16 == srcJdbcType) {
            isStreaming = (4000 < srcPrecision || 4000 < destPrecision);
        }
        else {
            isStreaming = (8000 < srcPrecision || 8000 < destPrecision);
        }
        final CryptoMetadata destCryptoMeta = this.destColumnMetadata.get(destColOrdinal).cryptoMeta;
        if (null != destCryptoMeta) {
            destSSType = destCryptoMeta.baseTypeInfo.getSSType();
        }
        if (null != this.sourceResultSet) {
            colValue = this.readColumnFromResultSet(srcColOrdinal, srcJdbcType, isStreaming, null != destCryptoMeta);
            this.validateStringBinaryLengths(colValue, srcColOrdinal, destColOrdinal);
            if (!this.copyOptions.isAllowEncryptedValueModifications() && (null == destCryptoMeta || null == colValue)) {
                this.validateDataTypeConversions(srcColOrdinal, destColOrdinal);
            }
        }
        else if (null != this.serverBulkData && null == destCryptoMeta) {
            this.validateStringBinaryLengths(colValue, srcColOrdinal, destColOrdinal);
        }
        else if (null != this.serverBulkData && null != destCryptoMeta) {
            if (91 == srcJdbcType || 92 == srcJdbcType || 93 == srcJdbcType || -155 == srcJdbcType || 2013 == srcJdbcType || 2014 == srcJdbcType) {
                colValue = this.getTemporalObjectFromCSV(colValue, srcJdbcType, srcColOrdinal);
            }
            else if (2 == srcJdbcType || 3 == srcJdbcType) {
                final int baseDestPrecision = destCryptoMeta.baseTypeInfo.getPrecision();
                final int baseDestScale = destCryptoMeta.baseTypeInfo.getScale();
                if (srcScale != baseDestScale || srcPrecision != baseDestPrecision) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                    final String src = JDBCType.of(srcJdbcType) + "(" + srcPrecision + "," + srcScale + ")";
                    final String dest = destSSType + "(" + baseDestPrecision + "," + baseDestScale + ")";
                    final Object[] msgArgs = { src, dest };
                    throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
                }
            }
        }
        final CryptoMetadata srcCryptoMeta = this.srcColumnMetadata.get(srcColOrdinal).cryptoMeta;
        if (null != destCryptoMeta && null != colValue) {
            JDBCType baseSrcJdbcType = (null != srcCryptoMeta) ? this.srcColumnMetadata.get(srcColOrdinal).cryptoMeta.baseTypeInfo.getSSType().getJDBCType() : JDBCType.of(srcJdbcType);
            if (JDBCType.TIMESTAMP == baseSrcJdbcType) {
                if (SSType.DATETIME == destSSType) {
                    baseSrcJdbcType = JDBCType.DATETIME;
                }
                else if (SSType.SMALLDATETIME == destSSType) {
                    baseSrcJdbcType = JDBCType.SMALLDATETIME;
                }
            }
            if ((SSType.MONEY != destSSType || JDBCType.DECIMAL != baseSrcJdbcType) && (SSType.SMALLMONEY != destSSType || JDBCType.DECIMAL != baseSrcJdbcType) && (SSType.GUID != destSSType || JDBCType.CHAR != baseSrcJdbcType) && (!Util.isCharType(destSSType) || !Util.isCharType(srcJdbcType)) && !(this.sourceResultSet instanceof SQLServerResultSet) && !baseSrcJdbcType.normalizationCheck(destSSType)) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionAE"));
                final Object[] msgArgs2 = { baseSrcJdbcType, destSSType };
                throw new SQLServerException(this, form.format(msgArgs2), null, 0, false);
            }
            if (baseSrcJdbcType == JDBCType.DATE || baseSrcJdbcType == JDBCType.TIMESTAMP || baseSrcJdbcType == JDBCType.TIME || baseSrcJdbcType == JDBCType.DATETIMEOFFSET || baseSrcJdbcType == JDBCType.DATETIME || baseSrcJdbcType == JDBCType.SMALLDATETIME) {
                colValue = this.getEncryptedTemporalBytes(tdsWriter, baseSrcJdbcType, colValue, srcColOrdinal, destCryptoMeta.baseTypeInfo.getScale());
            }
            else {
                final TypeInfo destTypeInfo = destCryptoMeta.getBaseTypeInfo();
                final JDBCType destJdbcType = destTypeInfo.getSSType().getJDBCType();
                if (!Util.isBinaryType(destJdbcType.getIntValue()) && colValue instanceof byte[]) {
                    final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                    final Object[] msgArgs = { baseSrcJdbcType, destJdbcType };
                    throw new SQLServerException(this, form2.format(msgArgs), null, 0, false);
                }
                colValue = SQLServerSecurityUtility.encryptWithKey(this.normalizedValue(destJdbcType, colValue, baseSrcJdbcType, destTypeInfo.getPrecision(), destTypeInfo.getScale()), destCryptoMeta, this.connection);
            }
        }
        this.writeColumnToTdsWriter(tdsWriter, srcPrecision, srcScale, srcJdbcType, srcNullable, srcColOrdinal, destColOrdinal, isStreaming, colValue);
    }
    
    protected Object getTemporalObjectFromCSVWithFormatter(final String valueStrUntrimmed, final int srcJdbcType, final int srcColOrdinal, final DateTimeFormatter dateTimeFormatter) throws SQLServerException {
        try {
            final TemporalAccessor ta = dateTimeFormatter.parse(valueStrUntrimmed);
            int taOffsetSec;
            int taNano;
            int taDay;
            int taMonth;
            int taYear;
            int taSec;
            int taHour;
            int taMin = taHour = (taSec = (taYear = (taMonth = (taDay = (taNano = (taOffsetSec = 0))))));
            if (ta.isSupported(ChronoField.NANO_OF_SECOND)) {
                taNano = ta.get(ChronoField.NANO_OF_SECOND);
            }
            if (ta.isSupported(ChronoField.OFFSET_SECONDS)) {
                taOffsetSec = ta.get(ChronoField.OFFSET_SECONDS);
            }
            if (ta.isSupported(ChronoField.HOUR_OF_DAY)) {
                taHour = ta.get(ChronoField.HOUR_OF_DAY);
            }
            if (ta.isSupported(ChronoField.MINUTE_OF_HOUR)) {
                taMin = ta.get(ChronoField.MINUTE_OF_HOUR);
            }
            if (ta.isSupported(ChronoField.SECOND_OF_MINUTE)) {
                taSec = ta.get(ChronoField.SECOND_OF_MINUTE);
            }
            if (ta.isSupported(ChronoField.DAY_OF_MONTH)) {
                taDay = ta.get(ChronoField.DAY_OF_MONTH);
            }
            if (ta.isSupported(ChronoField.MONTH_OF_YEAR)) {
                taMonth = ta.get(ChronoField.MONTH_OF_YEAR);
            }
            if (ta.isSupported(ChronoField.YEAR)) {
                taYear = ta.get(ChronoField.YEAR);
            }
            final Calendar cal = new GregorianCalendar(new SimpleTimeZone(taOffsetSec * 1000, ""));
            cal.clear();
            cal.set(11, taHour);
            cal.set(12, taMin);
            cal.set(13, taSec);
            cal.set(5, taDay);
            cal.set(2, taMonth - 1);
            cal.set(1, taYear);
            for (int fractionalSecondsLength = Integer.toString(taNano).length(), i = 0; i < 9 - fractionalSecondsLength; ++i) {
                taNano *= 10;
            }
            Timestamp ts = new Timestamp(cal.getTimeInMillis());
            ts.setNanos(taNano);
            switch (srcJdbcType) {
                case 93: {
                    return ts;
                }
                case 92: {
                    cal.set(this.connection.baseYear(), 0, 1);
                    ts = new Timestamp(cal.getTimeInMillis());
                    ts.setNanos(taNano);
                    return new Timestamp(ts.getTime());
                }
                case 91: {
                    return new Date(ts.getTime());
                }
                case -155: {
                    return DateTimeOffset.valueOf(ts, taOffsetSec / 60);
                }
                default: {
                    return valueStrUntrimmed;
                }
            }
        }
        catch (final DateTimeException | ArithmeticException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ParsingError"));
            final Object[] msgArgs = { JDBCType.of(srcJdbcType) };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
    }
    
    private Object getTemporalObjectFromCSV(final Object value, final int srcJdbcType, final int srcColOrdinal) throws SQLServerException {
        if (2013 == srcJdbcType) {
            final MessageFormat form1 = new MessageFormat(SQLServerException.getErrString("R_UnsupportedDataTypeAE"));
            final Object[] msgArgs1 = { "TIME_WITH_TIMEZONE" };
            throw new SQLServerException(this, form1.format(msgArgs1), null, 0, false);
        }
        if (2014 == srcJdbcType) {
            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_UnsupportedDataTypeAE"));
            final Object[] msgArgs2 = { "TIMESTAMP_WITH_TIMEZONE" };
            throw new SQLServerException(this, form2.format(msgArgs2), null, 0, false);
        }
        String valueStr = null;
        String valueStrUntrimmed = null;
        if (null != value && value instanceof String) {
            valueStrUntrimmed = (String)value;
            valueStr = valueStrUntrimmed.trim();
        }
        if (null == valueStr) {
            switch (srcJdbcType) {
                case -155:
                case 91:
                case 92:
                case 93: {
                    return null;
                }
            }
        }
        final DateTimeFormatter dateTimeFormatter = this.srcColumnMetadata.get(srcColOrdinal).dateTimeFormatter;
        if (null != dateTimeFormatter) {
            return this.getTemporalObjectFromCSVWithFormatter(valueStrUntrimmed, srcJdbcType, srcColOrdinal, dateTimeFormatter);
        }
        try {
            switch (srcJdbcType) {
                case 93: {
                    return Timestamp.valueOf(valueStr);
                }
                case 92: {
                    final String time = this.connection.baseYear() + "-01-01 " + valueStr;
                    final Timestamp ts = Timestamp.valueOf(time);
                    return ts;
                }
                case 91: {
                    return Date.valueOf(valueStr);
                }
                case -155: {
                    int endIndx = valueStr.indexOf(45, 0);
                    final int year = Integer.parseInt(valueStr.substring(0, endIndx));
                    int startIndx = ++endIndx;
                    endIndx = valueStr.indexOf(45, startIndx);
                    final int month = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                    startIndx = ++endIndx;
                    endIndx = valueStr.indexOf(32, startIndx);
                    final int day = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                    startIndx = ++endIndx;
                    endIndx = valueStr.indexOf(58, startIndx);
                    final int hour = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                    startIndx = ++endIndx;
                    endIndx = valueStr.indexOf(58, startIndx);
                    final int minute = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                    startIndx = ++endIndx;
                    endIndx = valueStr.indexOf(46, startIndx);
                    int totalOffset = 0;
                    int fractionalSeconds = 0;
                    boolean isNegativeOffset = false;
                    boolean hasTimeZone = false;
                    int fractionalSecondsLength = 0;
                    int seconds;
                    if (-1 != endIndx) {
                        seconds = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                        startIndx = ++endIndx;
                        endIndx = valueStr.indexOf(32, startIndx);
                        if (-1 != endIndx) {
                            fractionalSeconds = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                            fractionalSecondsLength = endIndx - startIndx;
                            hasTimeZone = true;
                        }
                        else {
                            fractionalSeconds = Integer.parseInt(valueStr.substring(startIndx));
                            fractionalSecondsLength = valueStr.length() - startIndx;
                        }
                    }
                    else {
                        endIndx = valueStr.indexOf(32, startIndx);
                        if (-1 != endIndx) {
                            hasTimeZone = true;
                            seconds = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                        }
                        else {
                            seconds = Integer.parseInt(valueStr.substring(startIndx));
                            ++endIndx;
                        }
                    }
                    if (hasTimeZone) {
                        startIndx = ++endIndx;
                        if ('+' == valueStr.charAt(startIndx)) {
                            ++startIndx;
                        }
                        else if ('-' == valueStr.charAt(startIndx)) {
                            isNegativeOffset = true;
                            ++startIndx;
                        }
                        endIndx = valueStr.indexOf(58, startIndx);
                        final int offsethour = Integer.parseInt(valueStr.substring(startIndx, endIndx));
                        startIndx = ++endIndx;
                        final int offsetMinute = Integer.parseInt(valueStr.substring(startIndx));
                        totalOffset = offsethour * 60 + offsetMinute;
                        if (isNegativeOffset) {
                            totalOffset = -totalOffset;
                        }
                    }
                    final Calendar cal = new GregorianCalendar(new SimpleTimeZone(totalOffset * 60 * 1000, ""), Locale.US);
                    cal.clear();
                    cal.set(11, hour);
                    cal.set(12, minute);
                    cal.set(13, seconds);
                    cal.set(5, day);
                    cal.set(2, month - 1);
                    cal.set(1, year);
                    for (int i = 0; i < 9 - fractionalSecondsLength; ++i) {
                        fractionalSeconds *= 10;
                    }
                    final Timestamp ts2 = new Timestamp(cal.getTimeInMillis());
                    ts2.setNanos(fractionalSeconds);
                    return DateTimeOffset.valueOf(ts2, totalOffset);
                }
            }
        }
        catch (final IndexOutOfBoundsException e) {
            final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_ParsingError"));
            final Object[] msgArgs3 = { JDBCType.of(srcJdbcType) };
            throw new SQLServerException(this, form3.format(msgArgs3), null, 0, false);
        }
        catch (final NumberFormatException e2) {
            final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_ParsingError"));
            final Object[] msgArgs3 = { JDBCType.of(srcJdbcType) };
            throw new SQLServerException(this, form3.format(msgArgs3), null, 0, false);
        }
        catch (final IllegalArgumentException e3) {
            final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_ParsingError"));
            final Object[] msgArgs3 = { JDBCType.of(srcJdbcType) };
            throw new SQLServerException(this, form3.format(msgArgs3), null, 0, false);
        }
        return value;
    }
    
    private byte[] getEncryptedTemporalBytes(final TDSWriter tdsWriter, final JDBCType srcTemporalJdbcType, final Object colValue, final int srcColOrdinal, final int scale) throws SQLServerException {
        switch (srcTemporalJdbcType) {
            case DATE: {
                final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault(), Locale.US);
                calendar.setLenient(true);
                calendar.clear();
                calendar.setTimeInMillis(((Date)colValue).getTime());
                return tdsWriter.writeEncryptedScaledTemporal(calendar, 0, 0, SSType.DATE, (short)0);
            }
            case TIME: {
                final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault(), Locale.US);
                calendar.setLenient(true);
                calendar.clear();
                final long utcMillis = ((Timestamp)colValue).getTime();
                calendar.setTimeInMillis(utcMillis);
                int subSecondNanos;
                if (colValue instanceof Timestamp) {
                    subSecondNanos = ((Timestamp)colValue).getNanos();
                }
                else {
                    subSecondNanos = 1000000 * (int)(utcMillis % 1000L);
                    if (subSecondNanos < 0) {
                        subSecondNanos += 1000000000;
                    }
                }
                return tdsWriter.writeEncryptedScaledTemporal(calendar, subSecondNanos, scale, SSType.TIME, (short)0);
            }
            case TIMESTAMP: {
                final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault(), Locale.US);
                calendar.setLenient(true);
                calendar.clear();
                final long utcMillis = ((Timestamp)colValue).getTime();
                calendar.setTimeInMillis(utcMillis);
                final int subSecondNanos = ((Timestamp)colValue).getNanos();
                return tdsWriter.writeEncryptedScaledTemporal(calendar, subSecondNanos, scale, SSType.DATETIME2, (short)0);
            }
            case DATETIME:
            case SMALLDATETIME: {
                final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault(), Locale.US);
                calendar.setLenient(true);
                calendar.clear();
                final long utcMillis = ((Timestamp)colValue).getTime();
                calendar.setTimeInMillis(utcMillis);
                final int subSecondNanos = ((Timestamp)colValue).getNanos();
                return tdsWriter.getEncryptedDateTimeAsBytes(calendar, subSecondNanos, srcTemporalJdbcType);
            }
            case DATETIMEOFFSET: {
                final DateTimeOffset dtoValue = (DateTimeOffset)colValue;
                final long utcMillis = dtoValue.getTimestamp().getTime();
                final int subSecondNanos = dtoValue.getTimestamp().getNanos();
                final int minutesOffset = dtoValue.getMinutesOffset();
                final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
                calendar.setLenient(true);
                calendar.clear();
                calendar.setTimeInMillis(utcMillis);
                return tdsWriter.writeEncryptedScaledTemporal(calendar, subSecondNanos, scale, SSType.DATETIMEOFFSET, (short)minutesOffset);
            }
            default: {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_UnsupportedDataTypeAE"));
                final Object[] msgArgs = { srcTemporalJdbcType };
                throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
            }
        }
    }
    
    private byte[] normalizedValue(final JDBCType destJdbcType, final Object value, final JDBCType srcJdbcType, final int destPrecision, final int destScale) throws SQLServerException {
        Long longValue = null;
        byte[] byteValue = null;
        try {
            switch (destJdbcType) {
                case BIT: {
                    longValue = (long)(((boolean)value) ? 1 : 0);
                    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(longValue).array();
                }
                case TINYINT:
                case SMALLINT: {
                    switch (srcJdbcType) {
                        case BIT: {
                            longValue = (long)(((boolean)value) ? 1 : 0);
                            break;
                        }
                        default: {
                            if (value instanceof Integer) {
                                final int intValue = (int)value;
                                final short shortValue = (short)intValue;
                                longValue = (long)shortValue;
                                break;
                            }
                            longValue = (long)(short)value;
                            break;
                        }
                    }
                    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(longValue).array();
                }
                case INTEGER: {
                    switch (srcJdbcType) {
                        case BIT: {
                            longValue = (long)(((boolean)value) ? 1 : 0);
                            break;
                        }
                        case TINYINT:
                        case SMALLINT: {
                            longValue = (long)(short)value;
                            break;
                        }
                        default: {
                            longValue = (long)(int)value;
                            break;
                        }
                    }
                    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(longValue).array();
                }
                case BIGINT: {
                    switch (srcJdbcType) {
                        case BIT: {
                            longValue = (long)(((boolean)value) ? 1 : 0);
                            break;
                        }
                        case TINYINT:
                        case SMALLINT: {
                            longValue = (long)(short)value;
                            break;
                        }
                        case INTEGER: {
                            longValue = (long)(int)value;
                            break;
                        }
                        default: {
                            longValue = (long)value;
                            break;
                        }
                    }
                    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(longValue).array();
                }
                case BINARY:
                case VARBINARY:
                case LONGVARBINARY: {
                    byte[] byteArrayValue;
                    if (value instanceof String) {
                        byteArrayValue = ParameterUtils.HexToBin((String)value);
                    }
                    else {
                        byteArrayValue = (byte[])value;
                    }
                    if (byteArrayValue.length > destPrecision) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                        final Object[] msgArgs = { srcJdbcType, destJdbcType };
                        throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
                    }
                    return byteArrayValue;
                }
                case GUID: {
                    return Util.asGuidByteArray(UUID.fromString((String)value));
                }
                case CHAR:
                case VARCHAR:
                case LONGVARCHAR: {
                    if (((String)value).length() > destPrecision) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                        final Object[] msgArgs = { srcJdbcType, destJdbcType };
                        throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
                    }
                    return ((String)value).getBytes(StandardCharsets.UTF_8);
                }
                case NCHAR:
                case NVARCHAR:
                case LONGNVARCHAR: {
                    if (((String)value).length() > destPrecision) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                        final Object[] msgArgs = { srcJdbcType, destJdbcType };
                        throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
                    }
                    return ((String)value).getBytes(StandardCharsets.UTF_16LE);
                }
                case REAL: {
                    final Float floatValue = (value instanceof String) ? Float.parseFloat((String)value) : value;
                    return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(floatValue).array();
                }
                case FLOAT:
                case DOUBLE: {
                    final Double doubleValue = (value instanceof String) ? Double.parseDouble((String)value) : value;
                    return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(doubleValue).array();
                }
                case NUMERIC:
                case DECIMAL: {
                    final int srcDataScale = ((BigDecimal)value).scale();
                    final int srcDataPrecision = ((BigDecimal)value).precision();
                    BigDecimal bigDataValue = (BigDecimal)value;
                    if (srcDataPrecision > destPrecision || srcDataScale > destScale) {
                        final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
                        final Object[] msgArgs2 = { srcJdbcType, destJdbcType };
                        throw new SQLServerException(this, form2.format(msgArgs2), null, 0, false);
                    }
                    if (srcDataScale < destScale) {
                        bigDataValue = bigDataValue.setScale(destScale);
                    }
                    byteValue = DDC.convertBigDecimalToBytes(bigDataValue, bigDataValue.scale());
                    final byte[] decimalbyteValue = new byte[16];
                    System.arraycopy(byteValue, 2, decimalbyteValue, 0, byteValue.length - 2);
                    return decimalbyteValue;
                }
                case SMALLMONEY:
                case MONEY: {
                    final BigDecimal bdValue = (BigDecimal)value;
                    Util.validateMoneyRange(bdValue, destJdbcType);
                    final int digitCount = bdValue.precision() - bdValue.scale() + 4;
                    final long moneyVal = ((BigDecimal)value).multiply(new BigDecimal(10000), new MathContext(digitCount, RoundingMode.HALF_UP)).longValue();
                    final ByteBuffer bbuf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
                    bbuf.putInt((int)(moneyVal >> 32)).array();
                    bbuf.putInt((int)moneyVal).array();
                    return bbuf.array();
                }
                default: {
                    final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_UnsupportedDataTypeAE"));
                    final Object[] msgArgs3 = { destJdbcType };
                    throw new SQLServerException(this, form3.format(msgArgs3), null, 0, false);
                }
            }
        }
        catch (final NumberFormatException ex) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
            final Object[] msgArgs = { srcJdbcType, destJdbcType };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
        catch (final IllegalArgumentException ex2) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
            final Object[] msgArgs = { srcJdbcType, destJdbcType };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
        catch (final ClassCastException ex3) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidDataForAE"));
            final Object[] msgArgs = { srcJdbcType, destJdbcType };
            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
        }
    }
    
    private boolean goToNextRow() throws SQLServerException {
        try {
            if (null != this.sourceResultSet) {
                return this.sourceResultSet.next();
            }
            return this.serverBulkData.next();
        }
        catch (final SQLException e) {
            throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), e);
        }
    }
    
    private boolean writeBatchData(TDSWriter tdsWriter, final TDSCommand command, final boolean insertRowByRow) throws SQLServerException {
        final int batchsize = this.copyOptions.getBatchSize();
        int row = 0;
        while (0 == batchsize || row < batchsize) {
            if (!this.goToNextRow()) {
                return false;
            }
            if (insertRowByRow) {
                ((SQLServerResultSet)this.sourceResultSet).getTDSReader().readPacket();
                tdsWriter = this.sendBulkCopyCommand(command);
            }
            tdsWriter.writeByte((byte)(-47));
            if (null != this.sourceResultSet) {
                for (final ColumnMapping columnMapping : this.columnMappings) {
                    this.writeColumn(tdsWriter, columnMapping.sourceColumnOrdinal, columnMapping.destinationColumnOrdinal, null);
                }
            }
            else {
                Object[] rowObjects;
                try {
                    rowObjects = this.serverBulkData.getRowData();
                }
                catch (final Exception ex) {
                    throw new SQLServerException(SQLServerException.getErrString("R_unableRetrieveSourceData"), ex);
                }
                for (final ColumnMapping columnMapping2 : this.columnMappings) {
                    this.writeColumn(tdsWriter, columnMapping2.sourceColumnOrdinal, columnMapping2.destinationColumnOrdinal, rowObjects[columnMapping2.sourceColumnOrdinal - 1]);
                }
            }
            ++row;
            if (!insertRowByRow) {
                continue;
            }
            this.writePacketDataDone(tdsWriter);
            tdsWriter.setCryptoMetaData(null);
            TDSParser.parse(command.startResponse(), command.getLogContext());
        }
        return true;
    }
    
    protected void setStmtColumnEncriptionSetting(final SQLServerStatementColumnEncryptionSetting stmtColumnEncriptionSetting) {
        this.stmtColumnEncriptionSetting = stmtColumnEncriptionSetting;
    }
    
    protected void setDestinationTableMetadata(final SQLServerResultSet rs) {
        this.destinationTableMetadata = rs;
    }
    
    private boolean unicodeConversionRequired(final int jdbcType, final SSType ssType) {
        return (1 == jdbcType || 12 == jdbcType || -16 == jdbcType) && (SSType.NCHAR == ssType || SSType.NVARCHAR == ssType || SSType.NVARCHARMAX == ssType);
    }
    
    static {
        loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.SQLServerBulkCopy");
    }
    
    private class ColumnMapping implements Serializable
    {
        private static final long serialVersionUID = 6428337550654423919L;
        String sourceColumnName;
        int sourceColumnOrdinal;
        String destinationColumnName;
        int destinationColumnOrdinal;
        
        ColumnMapping(final String source, final String dest) {
            this.sourceColumnName = null;
            this.sourceColumnOrdinal = -1;
            this.destinationColumnName = null;
            this.destinationColumnOrdinal = -1;
            this.sourceColumnName = source;
            this.destinationColumnName = dest;
        }
        
        ColumnMapping(final String source, final int dest) {
            this.sourceColumnName = null;
            this.sourceColumnOrdinal = -1;
            this.destinationColumnName = null;
            this.destinationColumnOrdinal = -1;
            this.sourceColumnName = source;
            this.destinationColumnOrdinal = dest;
        }
        
        ColumnMapping(final int source, final String dest) {
            this.sourceColumnName = null;
            this.sourceColumnOrdinal = -1;
            this.destinationColumnName = null;
            this.destinationColumnOrdinal = -1;
            this.sourceColumnOrdinal = source;
            this.destinationColumnName = dest;
        }
        
        ColumnMapping(final int source, final int dest) {
            this.sourceColumnName = null;
            this.sourceColumnOrdinal = -1;
            this.destinationColumnName = null;
            this.destinationColumnOrdinal = -1;
            this.sourceColumnOrdinal = source;
            this.destinationColumnOrdinal = dest;
        }
    }
    
    class BulkColumnMetaData
    {
        String columnName;
        SSType ssType;
        int jdbcType;
        int precision;
        int scale;
        SQLCollation collation;
        byte[] flags;
        boolean isIdentity;
        boolean isNullable;
        String collationName;
        CryptoMetadata cryptoMeta;
        DateTimeFormatter dateTimeFormatter;
        String encryptionType;
        
        BulkColumnMetaData(final Column column) throws SQLServerException {
            this.ssType = null;
            this.flags = new byte[2];
            this.isIdentity = false;
            this.cryptoMeta = null;
            this.dateTimeFormatter = null;
            this.encryptionType = null;
            this.cryptoMeta = column.getCryptoMetadata();
            final TypeInfo typeInfo = column.getTypeInfo();
            this.columnName = column.getColumnName();
            this.ssType = typeInfo.getSSType();
            this.flags = typeInfo.getFlags();
            this.isIdentity = typeInfo.isIdentity();
            this.isNullable = typeInfo.isNullable();
            this.precision = typeInfo.getPrecision();
            this.scale = typeInfo.getScale();
            this.collation = typeInfo.getSQLCollation();
            this.jdbcType = this.ssType.getJDBCType().getIntValue();
        }
        
        BulkColumnMetaData(final String colName, final boolean isNullable, final int precision, final int scale, final int jdbcType, final DateTimeFormatter dateTimeFormatter) throws SQLServerException {
            this.ssType = null;
            this.flags = new byte[2];
            this.isIdentity = false;
            this.cryptoMeta = null;
            this.dateTimeFormatter = null;
            this.encryptionType = null;
            this.columnName = colName;
            this.isNullable = isNullable;
            this.precision = precision;
            this.scale = scale;
            this.jdbcType = jdbcType;
            this.dateTimeFormatter = dateTimeFormatter;
        }
        
        BulkColumnMetaData(final SQLServerBulkCopy this$0, final Column column, final String collationName, final String encryptionType) throws SQLServerException {
            this(this$0, column);
            this.collationName = collationName;
            this.encryptionType = encryptionType;
        }
        
        BulkColumnMetaData(final BulkColumnMetaData bulkColumnMetaData, final CryptoMetadata cryptoMeta) {
            this.ssType = null;
            this.flags = new byte[2];
            this.isIdentity = false;
            this.cryptoMeta = null;
            this.dateTimeFormatter = null;
            this.encryptionType = null;
            this.columnName = bulkColumnMetaData.columnName;
            this.isNullable = bulkColumnMetaData.isNullable;
            this.precision = bulkColumnMetaData.precision;
            this.scale = bulkColumnMetaData.scale;
            this.jdbcType = bulkColumnMetaData.jdbcType;
            this.cryptoMeta = cryptoMeta;
        }
    }
}
