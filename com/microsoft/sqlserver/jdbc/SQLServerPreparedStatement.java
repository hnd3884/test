package com.microsoft.sqlserver.jdbc;

import java.sql.RowId;
import java.net.URL;
import java.sql.ParameterMetaData;
import java.sql.Array;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import java.util.Vector;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.BatchUpdateException;
import java.sql.Date;
import microsoft.sql.DateTimeOffset;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.SQLType;
import java.io.Reader;
import java.util.UUID;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.SQLXML;
import java.util.Calendar;
import java.sql.ResultSetMetaData;
import java.util.Map;
import java.sql.Statement;
import java.util.HashMap;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.text.MessageFormat;
import java.util.ArrayList;

public class SQLServerPreparedStatement extends SQLServerStatement implements ISQLServerPreparedStatement
{
    private static final long serialVersionUID = -6292257029445685221L;
    private static final int BATCH_STATEMENT_DELIMITER_TDS_71 = 128;
    private static final int BATCH_STATEMENT_DELIMITER_TDS_72 = 255;
    final int nBatchStatementDelimiter = 255;
    private String preparedTypeDefinitions;
    final String userSQL;
    final int[] userSQLParamPositions;
    private String preparedSQL;
    private boolean isExecutedAtLeastOnce;
    private SQLServerConnection.PreparedStatementHandle cachedPreparedStatementHandle;
    private SQLServerConnection.CityHash128Key sqlTextCacheKey;
    private ArrayList<String> parameterNames;
    final boolean bReturnValueSyntax;
    private boolean useFmtOnly;
    int outParamIndexAdjustment;
    ArrayList<Parameter[]> batchParamValues;
    private int prepStmtHandle;
    private SQLServerStatement internalStmt;
    private boolean useBulkCopyForBatchInsert;
    private boolean expectPrepStmtHandle;
    private boolean encryptionMetadataIsRetrieved;
    private String localUserSQL;
    private ArrayList<byte[]> enclaveCEKs;
    
    private void setPreparedStatementHandle(final int handle) {
        this.prepStmtHandle = handle;
    }
    
    private boolean getUseBulkCopyForBatchInsert() throws SQLServerException {
        this.checkClosed();
        return this.useBulkCopyForBatchInsert;
    }
    
    private void setUseBulkCopyForBatchInsert(final boolean useBulkCopyForBatchInsert) throws SQLServerException {
        this.checkClosed();
        this.useBulkCopyForBatchInsert = useBulkCopyForBatchInsert;
    }
    
    @Override
    public int getPreparedStatementHandle() throws SQLServerException {
        this.checkClosed();
        return this.prepStmtHandle;
    }
    
    private boolean hasPreparedStatementHandle() {
        return 0 < this.prepStmtHandle;
    }
    
    private boolean resetPrepStmtHandle(final boolean discardCurrentCacheItem) {
        final boolean statementPoolingUsed = null != this.cachedPreparedStatementHandle;
        if (statementPoolingUsed && discardCurrentCacheItem) {
            this.cachedPreparedStatementHandle.setIsExplicitlyDiscarded();
        }
        this.prepStmtHandle = 0;
        return statementPoolingUsed;
    }
    
    @Override
    String getClassNameInternal() {
        return "SQLServerPreparedStatement";
    }
    
    SQLServerPreparedStatement(final SQLServerConnection conn, final String sql, final int nRSType, final int nRSConcur, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        super(conn, nRSType, nRSConcur, stmtColEncSetting);
        this.isExecutedAtLeastOnce = false;
        this.useFmtOnly = this.connection.getUseFmtOnly();
        this.prepStmtHandle = 0;
        this.internalStmt = null;
        this.expectPrepStmtHandle = false;
        this.encryptionMetadataIsRetrieved = false;
        if (null == sql) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_NullValue"));
            final Object[] msgArgs1 = { "Statement SQL" };
            throw new SQLServerException(form.format(msgArgs1), (Throwable)null);
        }
        this.stmtPoolable = true;
        this.sqlTextCacheKey = new SQLServerConnection.CityHash128Key(sql);
        ParsedSQLCacheItem parsedSQL = SQLServerConnection.getCachedParsedSQL(this.sqlTextCacheKey);
        if (null != parsedSQL) {
            if (null != this.connection && this.connection.isStatementPoolingEnabled()) {
                this.isExecutedAtLeastOnce = true;
            }
        }
        else {
            parsedSQL = SQLServerConnection.parseAndCacheSQL(this.sqlTextCacheKey, sql);
        }
        this.procedureName = parsedSQL.procedureName;
        this.bReturnValueSyntax = parsedSQL.bReturnValueSyntax;
        this.userSQL = parsedSQL.processedSQL;
        this.userSQLParamPositions = parsedSQL.parameterPositions;
        this.initParams(this.userSQLParamPositions.length);
        this.useBulkCopyForBatchInsert = conn.getUseBulkCopyForBatchInsert();
    }
    
    private void closePreparedHandle() {
        if (!this.hasPreparedStatementHandle()) {
            return;
        }
        if (this.connection.isSessionUnAvailable()) {
            if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
                SQLServerPreparedStatement.loggerExternal.finer(this + ": Not closing PreparedHandle:" + this.prepStmtHandle + "; connection is already closed.");
            }
        }
        else {
            this.isExecutedAtLeastOnce = false;
            final int handleToClose = this.prepStmtHandle;
            final class PreparedHandleClose extends UninterruptableTDSCommand
            {
                private static final long serialVersionUID = -8944096664249990764L;
                
                PreparedHandleClose() {
                    super("closePreparedHandle");
                }
                
                @Override
                final boolean doExecute() throws SQLServerException {
                    final TDSWriter tdsWriter = this.startRequest((byte)3);
                    tdsWriter.writeShort((short)(-1));
                    tdsWriter.writeShort((short)(SQLServerPreparedStatement.this.executedSqlDirectly ? 15 : 6));
                    tdsWriter.writeByte((byte)0);
                    tdsWriter.writeByte((byte)0);
                    tdsWriter.sendEnclavePackage(null, null);
                    tdsWriter.writeRPCInt(null, handleToClose, false);
                    TDSParser.parse(this.startResponse(), this.getLogContext());
                    return true;
                }
            }
            if (this.resetPrepStmtHandle(false)) {
                this.connection.returnCachedPreparedStatementHandle(this.cachedPreparedStatementHandle);
            }
            else if (this.connection.isPreparedStatementUnprepareBatchingEnabled()) {
                this.connection.enqueueUnprepareStatementHandle(this.connection.new PreparedStatementHandle(null, handleToClose, this.executedSqlDirectly, true));
            }
            else {
                if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
                    SQLServerPreparedStatement.loggerExternal.finer(this + ": Closing PreparedHandle:" + handleToClose);
                }
                try {
                    this.executeCommand(new PreparedHandleClose());
                }
                catch (final SQLServerException e) {
                    if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
                        SQLServerPreparedStatement.loggerExternal.log(Level.FINER, this + ": Error (ignored) closing PreparedHandle:" + handleToClose, e);
                    }
                }
                if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
                    SQLServerPreparedStatement.loggerExternal.finer(this + ": Closed PreparedHandle:" + handleToClose);
                }
            }
            this.connection.unprepareUnreferencedPreparedStatementHandles(false);
        }
    }
    
    @Override
    final void closeInternal() {
        super.closeInternal();
        this.closePreparedHandle();
        try {
            if (null != this.internalStmt) {
                this.internalStmt.close();
            }
        }
        catch (final SQLServerException e) {
            if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
                SQLServerPreparedStatement.loggerExternal.finer("Ignored error closing internal statement: " + e.getErrorCode() + " " + e.getMessage());
            }
        }
        finally {
            this.internalStmt = null;
        }
        this.batchParamValues = null;
    }
    
    final void initParams(final int nParams) {
        this.inOutParam = new Parameter[nParams];
        for (int i = 0; i < nParams; ++i) {
            this.inOutParam[i] = new Parameter(Util.shouldHonorAEForParameters(this.stmtColumnEncriptionSetting, this.connection));
        }
    }
    
    @Override
    public final void clearParameters() throws SQLServerException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "clearParameters");
        this.checkClosed();
        this.encryptionMetadataIsRetrieved = false;
        if (this.inOutParam == null) {
            return;
        }
        for (int i = 0; i < this.inOutParam.length; ++i) {
            this.inOutParam[i].clearInputValue();
        }
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "clearParameters");
    }
    
    private boolean buildPreparedStrings(final Parameter[] params, final boolean renewDefinition) throws SQLServerException {
        final String newTypeDefinitions = this.buildParamTypeDefinitions(params, renewDefinition);
        if (null != this.preparedTypeDefinitions && newTypeDefinitions.equalsIgnoreCase(this.preparedTypeDefinitions)) {
            return false;
        }
        this.preparedTypeDefinitions = newTypeDefinitions;
        this.preparedSQL = this.connection.replaceParameterMarkers(this.userSQL, this.userSQLParamPositions, params, this.bReturnValueSyntax);
        if (this.bRequestedGeneratedKeys) {
            this.preparedSQL += " select SCOPE_IDENTITY() AS GENERATED_KEYS";
        }
        return true;
    }
    
    private String buildParamTypeDefinitions(final Parameter[] params, final boolean renewDefinition) throws SQLServerException {
        final StringBuilder sb = new StringBuilder();
        final int nCols = params.length;
        final char[] cParamName = new char[10];
        this.parameterNames = new ArrayList<String>();
        for (int i = 0; i < nCols; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            for (int l = SQLServerConnection.makeParamName(i, cParamName, 0), j = 0; j < l; ++j) {
                sb.append(cParamName[j]);
            }
            sb.append(' ');
            this.parameterNames.add(i, new String(cParamName).trim());
            params[i].renewDefinition = renewDefinition;
            final String typeDefinition = params[i].getTypeDefinition(this.connection, this.resultsReader());
            if (null == typeDefinition) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueNotSetForParameter"));
                final Object[] msgArgs = { i + 1 };
                SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), null, false);
            }
            sb.append(typeDefinition);
            if (params[i].isOutput()) {
                sb.append(" OUTPUT");
            }
        }
        return sb.toString();
    }
    
    @Override
    public ResultSet executeQuery() throws SQLServerException, SQLTimeoutException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "executeQuery");
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerPreparedStatement.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        this.executeStatement(new PrepStmtExecCmd(this, 1));
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "executeQuery");
        return this.resultSet;
    }
    
    final ResultSet executeQueryInternal() throws SQLServerException, SQLTimeoutException {
        this.checkClosed();
        this.executeStatement(new PrepStmtExecCmd(this, 5));
        return this.resultSet;
    }
    
    @Override
    public int executeUpdate() throws SQLServerException, SQLTimeoutException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "executeUpdate");
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerPreparedStatement.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        this.executeStatement(new PrepStmtExecCmd(this, 2));
        if (this.updateCount < -2147483648L || this.updateCount > 2147483647L) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_updateCountOutofRange"), null, true);
        }
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "executeUpdate", this.updateCount);
        return (int)this.updateCount;
    }
    
    @Override
    public long executeLargeUpdate() throws SQLServerException, SQLTimeoutException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "executeLargeUpdate");
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerPreparedStatement.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        this.executeStatement(new PrepStmtExecCmd(this, 2));
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "executeLargeUpdate", this.updateCount);
        return this.updateCount;
    }
    
    @Override
    public boolean execute() throws SQLServerException, SQLTimeoutException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "execute");
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerPreparedStatement.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        this.executeStatement(new PrepStmtExecCmd(this, 3));
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "execute", null != this.resultSet);
        return null != this.resultSet;
    }
    
    final void doExecutePreparedStatement(final PrepStmtExecCmd command) throws SQLServerException {
        this.resetForReexecute();
        this.setMaxRowsAndMaxFieldSize();
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerPreparedStatement.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        final boolean hasExistingTypeDefinitions = this.preparedTypeDefinitions != null;
        boolean hasNewTypeDefinitions = true;
        if (!this.encryptionMetadataIsRetrieved) {
            hasNewTypeDefinitions = this.buildPreparedStrings(this.inOutParam, false);
        }
        if (this.connection.isAEv2() && !this.isInternalEncryptionQuery) {
            this.enclaveCEKs = this.connection.initEnclaveParameters(this.preparedSQL, this.preparedTypeDefinitions, this.inOutParam, this.parameterNames);
            this.encryptionMetadataIsRetrieved = true;
            this.setMaxRowsAndMaxFieldSize();
            hasNewTypeDefinitions = this.buildPreparedStrings(this.inOutParam, true);
        }
        if (Util.shouldHonorAEForParameters(this.stmtColumnEncriptionSetting, this.connection) && 0 < this.inOutParam.length && !this.isInternalEncryptionQuery) {
            if (!this.encryptionMetadataIsRetrieved) {
                this.getParameterEncryptionMetadata(this.inOutParam);
                this.encryptionMetadataIsRetrieved = true;
                this.setMaxRowsAndMaxFieldSize();
            }
            hasNewTypeDefinitions = this.buildPreparedStrings(this.inOutParam, true);
        }
        boolean needsPrepare = true;
        int attempt = 1;
        while (attempt <= 2) {
            try {
                if (this.reuseCachedHandle(hasNewTypeDefinitions, 1 < attempt)) {
                    hasNewTypeDefinitions = false;
                }
                final TDSWriter tdsWriter = command.startRequest((byte)3);
                needsPrepare = this.doPrepExec(tdsWriter, this.inOutParam, hasNewTypeDefinitions, hasExistingTypeDefinitions);
                this.ensureExecuteResultsReader(command.startResponse(this.getIsResponseBufferingAdaptive()));
                this.startResults();
                this.getNextResult(true);
            }
            catch (final SQLException e) {
                if (this.retryBasedOnFailedReuseOfCachedHandle(e, attempt, needsPrepare, false)) {
                    ++attempt;
                    continue;
                }
                throw e;
            }
            break;
        }
        if (1 == this.executeMethod && null == this.resultSet) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_noResultset"), null, true);
        }
        else if (2 == this.executeMethod && null != this.resultSet) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_resultsetGeneratedForUpdate"), null, false);
        }
    }
    
    private boolean retryBasedOnFailedReuseOfCachedHandle(final SQLException e, final int attempt, final boolean needsPrepare, final boolean isBatch) {
        return (!needsPrepare || isBatch) && 1 == attempt && (586 == e.getErrorCode() || 8179 == e.getErrorCode()) && this.connection.isStatementPoolingEnabled();
    }
    
    @Override
    boolean consumeExecOutParam(final TDSReader tdsReader) throws SQLServerException {
        if (this.expectPrepStmtHandle || this.expectCursorOutParams) {
            final class PrepStmtExecOutParamHandler extends StmtExecOutParamHandler
            {
                @Override
                boolean onRetValue(final TDSReader tdsReader) throws SQLServerException {
                    if (!SQLServerPreparedStatement.this.expectPrepStmtHandle) {
                        return super.onRetValue(tdsReader);
                    }
                    SQLServerPreparedStatement.this.expectPrepStmtHandle = false;
                    final Parameter param = new Parameter(Util.shouldHonorAEForParameters(SQLServerPreparedStatement.this.stmtColumnEncriptionSetting, SQLServerPreparedStatement.this.connection));
                    param.skipRetValStatus(tdsReader);
                    SQLServerPreparedStatement.this.setPreparedStatementHandle(param.getInt(tdsReader));
                    if (null == SQLServerPreparedStatement.this.cachedPreparedStatementHandle && !SQLServerPreparedStatement.this.isCursorable(SQLServerPreparedStatement.this.executeMethod)) {
                        SQLServerPreparedStatement.this.cachedPreparedStatementHandle = SQLServerPreparedStatement.this.connection.registerCachedPreparedStatementHandle(new SQLServerConnection.CityHash128Key(SQLServerPreparedStatement.this.preparedSQL, SQLServerPreparedStatement.this.preparedTypeDefinitions), SQLServerPreparedStatement.this.prepStmtHandle, SQLServerPreparedStatement.this.executedSqlDirectly);
                    }
                    param.skipValue(tdsReader, true);
                    if (SQLServerPreparedStatement.this.getStatementLogger().isLoggable(Level.FINER)) {
                        SQLServerPreparedStatement.this.getStatementLogger().finer(this.toString() + ": Setting PreparedHandle:" + SQLServerPreparedStatement.this.prepStmtHandle);
                    }
                    return true;
                }
            }
            TDSParser.parse(tdsReader, new PrepStmtExecOutParamHandler());
            return true;
        }
        return false;
    }
    
    void sendParamsByRPC(final TDSWriter tdsWriter, final Parameter[] params) throws SQLServerException {
        for (int index = 0; index < params.length; ++index) {
            if (JDBCType.TVP == params[index].getJdbcType()) {
                final char[] cParamName = new char[10];
                final int paramNameLen = SQLServerConnection.makeParamName(index, cParamName, 0);
                tdsWriter.writeByte((byte)paramNameLen);
                tdsWriter.writeString(new String(cParamName, 0, paramNameLen));
            }
            params[index].sendByRPC(tdsWriter, this.connection);
        }
    }
    
    private void buildServerCursorPrepExecParams(final TDSWriter tdsWriter) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_cursorprepexec: PreparedHandle:" + this.getPreparedStatementHandle() + ", SQL:" + this.preparedSQL);
        }
        this.expectPrepStmtHandle = true;
        this.executedSqlDirectly = false;
        this.expectCursorOutParams = true;
        this.outParamIndexAdjustment = 7;
        tdsWriter.writeShort((short)(-1));
        tdsWriter.writeShort((short)5);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(this.preparedSQL, this.enclaveCEKs);
        tdsWriter.writeRPCInt(null, this.getPreparedStatementHandle(), true);
        this.resetPrepStmtHandle(false);
        tdsWriter.writeRPCInt(null, 0, true);
        tdsWriter.writeRPCStringUnicode((this.preparedTypeDefinitions.length() > 0) ? this.preparedTypeDefinitions : null);
        tdsWriter.writeRPCStringUnicode(this.preparedSQL);
        tdsWriter.writeRPCInt(null, this.getResultSetScrollOpt() & ~((0 == this.preparedTypeDefinitions.length()) ? 4096 : 0), false);
        tdsWriter.writeRPCInt(null, this.getResultSetCCOpt(), false);
        tdsWriter.writeRPCInt(null, 0, true);
    }
    
    private void buildPrepExecParams(final TDSWriter tdsWriter) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_prepexec: PreparedHandle:" + this.getPreparedStatementHandle() + ", SQL:" + this.preparedSQL);
        }
        this.expectPrepStmtHandle = true;
        this.executedSqlDirectly = true;
        this.expectCursorOutParams = false;
        this.outParamIndexAdjustment = 3;
        tdsWriter.writeShort((short)(-1));
        tdsWriter.writeShort((short)13);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(this.preparedSQL, this.enclaveCEKs);
        tdsWriter.writeRPCInt(null, this.getPreparedStatementHandle(), true);
        this.resetPrepStmtHandle(false);
        tdsWriter.writeRPCStringUnicode((this.preparedTypeDefinitions.length() > 0) ? this.preparedTypeDefinitions : null);
        tdsWriter.writeRPCStringUnicode(this.preparedSQL);
    }
    
    private void buildExecSQLParams(final TDSWriter tdsWriter) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_executesql: SQL:" + this.preparedSQL);
        }
        this.expectPrepStmtHandle = false;
        this.executedSqlDirectly = true;
        this.expectCursorOutParams = false;
        this.outParamIndexAdjustment = 2;
        tdsWriter.writeShort((short)(-1));
        tdsWriter.writeShort((short)10);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(this.preparedSQL, this.enclaveCEKs);
        this.resetPrepStmtHandle(false);
        tdsWriter.writeRPCStringUnicode(this.preparedSQL);
        if (this.preparedTypeDefinitions.length() > 0) {
            tdsWriter.writeRPCStringUnicode(this.preparedTypeDefinitions);
        }
    }
    
    private void buildServerCursorExecParams(final TDSWriter tdsWriter) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_cursorexecute: PreparedHandle:" + this.getPreparedStatementHandle() + ", SQL:" + this.preparedSQL);
        }
        this.expectPrepStmtHandle = false;
        this.executedSqlDirectly = false;
        this.expectCursorOutParams = true;
        this.outParamIndexAdjustment = 5;
        tdsWriter.writeShort((short)(-1));
        tdsWriter.writeShort((short)4);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(this.preparedSQL, this.enclaveCEKs);
        assert this.hasPreparedStatementHandle();
        tdsWriter.writeRPCInt(null, this.getPreparedStatementHandle(), false);
        tdsWriter.writeRPCInt(null, 0, true);
        tdsWriter.writeRPCInt(null, this.getResultSetScrollOpt() & 0xFFFFEFFF, false);
        tdsWriter.writeRPCInt(null, this.getResultSetCCOpt(), false);
        tdsWriter.writeRPCInt(null, 0, true);
    }
    
    private void buildExecParams(final TDSWriter tdsWriter) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_execute: PreparedHandle:" + this.getPreparedStatementHandle() + ", SQL:" + this.preparedSQL);
        }
        this.expectPrepStmtHandle = false;
        this.executedSqlDirectly = true;
        this.expectCursorOutParams = false;
        this.outParamIndexAdjustment = 1;
        tdsWriter.writeShort((short)(-1));
        tdsWriter.writeShort((short)12);
        tdsWriter.writeByte((byte)0);
        tdsWriter.writeByte((byte)0);
        tdsWriter.sendEnclavePackage(this.preparedSQL, this.enclaveCEKs);
        assert this.hasPreparedStatementHandle();
        tdsWriter.writeRPCInt(null, this.getPreparedStatementHandle(), false);
    }
    
    private void getParameterEncryptionMetadata(final Parameter[] params) throws SQLServerException {
        assert this.connection != null : "Connection should not be null";
        try (final Statement stmt = this.connection.prepareCall("exec sp_describe_parameter_encryption ?,?")) {
            if (this.getStatementLogger().isLoggable(Level.FINE)) {
                this.getStatementLogger().fine("Calling stored procedure sp_describe_parameter_encryption to get parameter encryption information.");
            }
            ((SQLServerCallableStatement)stmt).isInternalEncryptionQuery = true;
            ((SQLServerCallableStatement)stmt).setNString(1, this.preparedSQL);
            ((SQLServerCallableStatement)stmt).setNString(2, this.preparedTypeDefinitions);
            try (final ResultSet rs = ((SQLServerCallableStatement)stmt).executeQueryInternal()) {
                if (null == rs) {
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                    return;
                }
                final Map<Integer, CekTableEntry> cekList = new HashMap<Integer, CekTableEntry>();
                CekTableEntry cekEntry = null;
                while (rs.next()) {
                    final int currentOrdinal = rs.getInt(DescribeParameterEncryptionResultSet1.KeyOrdinal.value());
                    if (!cekList.containsKey(currentOrdinal)) {
                        cekEntry = new CekTableEntry(currentOrdinal);
                        cekList.put(cekEntry.ordinal, cekEntry);
                    }
                    else {
                        cekEntry = cekList.get(currentOrdinal);
                    }
                    cekEntry.add(rs.getBytes(DescribeParameterEncryptionResultSet1.EncryptedKey.value()), rs.getInt(DescribeParameterEncryptionResultSet1.DbId.value()), rs.getInt(DescribeParameterEncryptionResultSet1.KeyId.value()), rs.getInt(DescribeParameterEncryptionResultSet1.KeyVersion.value()), rs.getBytes(DescribeParameterEncryptionResultSet1.KeyMdVersion.value()), rs.getString(DescribeParameterEncryptionResultSet1.KeyPath.value()), rs.getString(DescribeParameterEncryptionResultSet1.ProviderName.value()), rs.getString(DescribeParameterEncryptionResultSet1.KeyEncryptionAlgorithm.value()));
                }
                if (this.getStatementLogger().isLoggable(Level.FINE)) {
                    this.getStatementLogger().fine("Matadata of CEKs is retrieved.");
                }
                if (!stmt.getMoreResults()) {
                    throw new SQLServerException(this, SQLServerException.getErrString("R_UnexpectedDescribeParamFormat"), null, 0, false);
                }
                int paramCount = 0;
                try (final ResultSet secondRs = stmt.getResultSet()) {
                    while (secondRs.next()) {
                        ++paramCount;
                        final String paramName = secondRs.getString(DescribeParameterEncryptionResultSet2.ParameterName.value());
                        final int paramIndex = this.parameterNames.indexOf(paramName);
                        final int cekOrdinal = secondRs.getInt(DescribeParameterEncryptionResultSet2.ColumnEncryptionKeyOrdinal.value());
                        cekEntry = cekList.get(cekOrdinal);
                        if (null != cekEntry && cekList.size() < cekOrdinal) {
                            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_InvalidEncryptionKeyOrdinal"));
                            final Object[] msgArgs = { cekOrdinal, cekEntry.getSize() };
                            throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
                        }
                        final SQLServerEncryptionType encType = SQLServerEncryptionType.of((byte)secondRs.getInt(DescribeParameterEncryptionResultSet2.ColumnEncrytionType.value()));
                        if (SQLServerEncryptionType.PlainText != encType) {
                            SQLServerSecurityUtility.decryptSymmetricKey(params[paramIndex].cryptoMeta = new CryptoMetadata(cekEntry, (short)cekOrdinal, (byte)secondRs.getInt(DescribeParameterEncryptionResultSet2.ColumnEncryptionAlgorithm.value()), null, encType.value, (byte)secondRs.getInt(DescribeParameterEncryptionResultSet2.NormalizationRuleVersion.value())), this.connection);
                        }
                        else {
                            if (!params[paramIndex].getForceEncryption()) {
                                continue;
                            }
                            final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_ForceEncryptionTrue_HonorAETrue_UnencryptedColumn"));
                            final Object[] msgArgs2 = { this.userSQL, paramIndex + 1 };
                            SQLServerException.makeFromDriverError(this.connection, this, form2.format(msgArgs2), null, true);
                        }
                    }
                    if (this.getStatementLogger().isLoggable(Level.FINE)) {
                        this.getStatementLogger().fine("Parameter encryption metadata is set.");
                    }
                }
                if (paramCount != params.length) {
                    final MessageFormat form3 = new MessageFormat(SQLServerException.getErrString("R_MissingParamEncryptionMetadata"));
                    final Object[] msgArgs3 = { this.userSQL };
                    throw new SQLServerException(this, form3.format(msgArgs3), null, 0, false);
                }
            }
        }
        catch (final SQLException e) {
            if (e instanceof SQLServerException) {
                throw (SQLServerException)e;
            }
            throw new SQLServerException(SQLServerException.getErrString("R_UnableRetrieveParameterMetadata"), null, 0, e);
        }
        this.connection.resetCurrentCommand();
    }
    
    private boolean reuseCachedHandle(final boolean hasNewTypeDefinitions, final boolean discardCurrentCacheItem) {
        if (this.isCursorable(this.executeMethod)) {
            return false;
        }
        if (discardCurrentCacheItem || hasNewTypeDefinitions) {
            if (null != this.cachedPreparedStatementHandle && (discardCurrentCacheItem || (this.hasPreparedStatementHandle() && this.prepStmtHandle == this.cachedPreparedStatementHandle.getHandle()))) {
                this.cachedPreparedStatementHandle.removeReference();
            }
            this.resetPrepStmtHandle(discardCurrentCacheItem);
            this.cachedPreparedStatementHandle = null;
            if (discardCurrentCacheItem) {
                return false;
            }
        }
        if (null == this.cachedPreparedStatementHandle) {
            final SQLServerConnection.PreparedStatementHandle cachedHandle = this.connection.getCachedPreparedStatementHandle(new SQLServerConnection.CityHash128Key(this.preparedSQL, this.preparedTypeDefinitions));
            if (null != cachedHandle && (!this.connection.isColumnEncryptionSettingEnabled() || (this.connection.isColumnEncryptionSettingEnabled() && this.encryptionMetadataIsRetrieved)) && cachedHandle.tryAddReference()) {
                this.setPreparedStatementHandle(cachedHandle.getHandle());
                this.cachedPreparedStatementHandle = cachedHandle;
                return true;
            }
        }
        return false;
    }
    
    private boolean doPrepExec(final TDSWriter tdsWriter, final Parameter[] params, final boolean hasNewTypeDefinitions, final boolean hasExistingTypeDefinitions) throws SQLServerException {
        final boolean needsPrepare = (hasNewTypeDefinitions && hasExistingTypeDefinitions) || !this.hasPreparedStatementHandle();
        if (this.isCursorable(this.executeMethod)) {
            if (needsPrepare) {
                this.buildServerCursorPrepExecParams(tdsWriter);
            }
            else {
                this.buildServerCursorExecParams(tdsWriter);
            }
        }
        else if (needsPrepare && !this.connection.getEnablePrepareOnFirstPreparedStatementCall() && !this.isExecutedAtLeastOnce) {
            this.buildExecSQLParams(tdsWriter);
            this.isExecutedAtLeastOnce = true;
        }
        else if (needsPrepare) {
            this.buildPrepExecParams(tdsWriter);
        }
        else {
            this.buildExecParams(tdsWriter);
        }
        this.sendParamsByRPC(tdsWriter, params);
        return needsPrepare;
    }
    
    @Override
    public final ResultSetMetaData getMetaData() throws SQLServerException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "getMetaData");
        this.checkClosed();
        boolean rsclosed = false;
        ResultSetMetaData rsmd = null;
        try {
            if (this.resultSet != null) {
                this.resultSet.checkClosed();
            }
        }
        catch (final SQLServerException e) {
            rsclosed = true;
        }
        if (this.resultSet == null || rsclosed) {
            final SQLServerResultSet emptyResultSet = (SQLServerResultSet)this.buildExecuteMetaData();
            if (null != emptyResultSet) {
                rsmd = emptyResultSet.getMetaData();
            }
        }
        else if (this.resultSet != null) {
            rsmd = this.resultSet.getMetaData();
        }
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "getMetaData", rsmd);
        return rsmd;
    }
    
    private ResultSet buildExecuteMetaData() throws SQLServerException {
        String fmtSQL = this.userSQL;
        ResultSet emptyResultSet = null;
        try {
            fmtSQL = SQLServerStatement.replaceMarkerWithNull(fmtSQL);
            this.internalStmt = (SQLServerStatement)this.connection.createStatement();
            emptyResultSet = this.internalStmt.executeQueryInternal("set fmtonly on " + fmtSQL + "\nset fmtonly off");
        }
        catch (final SQLException sqle) {
            if (!sqle.getMessage().equals(SQLServerException.getErrString("R_noResultset"))) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_processingError"));
                final Object[] msgArgs = { sqle.getMessage() };
                SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), null, true);
            }
        }
        return emptyResultSet;
    }
    
    final Parameter setterGetParam(final int index) throws SQLServerException {
        if (index < 1 || index > this.inOutParam.length) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
            final Object[] msgArgs = { index };
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), "07009", false);
        }
        return this.inOutParam[index - 1];
    }
    
    final void setValue(final int parameterIndex, final JDBCType jdbcType, final Object value, final JavaType javaType, final String tvpName) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(jdbcType, value, javaType, null, null, null, null, this.connection, false, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, tvpName);
    }
    
    final void setValue(final int parameterIndex, final JDBCType jdbcType, final Object value, final JavaType javaType, final boolean forceEncrypt) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(jdbcType, value, javaType, null, null, null, null, this.connection, forceEncrypt, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, null);
    }
    
    final void setValue(final int parameterIndex, final JDBCType jdbcType, final Object value, final JavaType javaType, final Integer precision, final Integer scale, final boolean forceEncrypt) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(jdbcType, value, javaType, null, null, precision, scale, this.connection, forceEncrypt, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, null);
    }
    
    final void setValue(final int parameterIndex, final JDBCType jdbcType, final Object value, final JavaType javaType, final Calendar cal, final boolean forceEncrypt) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(jdbcType, value, javaType, null, cal, null, null, this.connection, forceEncrypt, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, null);
    }
    
    final void setStream(final int parameterIndex, final StreamType streamType, final Object streamValue, final JavaType javaType, final long length) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(streamType.getJDBCType(), streamValue, javaType, new StreamSetterArgs(streamType, length), null, null, null, this.connection, false, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, null);
    }
    
    final void setSQLXMLInternal(final int parameterIndex, final SQLXML value) throws SQLServerException {
        this.setterGetParam(parameterIndex).setValue(JDBCType.SQLXML, value, JavaType.SQLXML, new StreamSetterArgs(StreamType.SQLXML, -1L), null, null, null, this.connection, false, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, null);
    }
    
    @Override
    public final void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[] { parameterIndex, x });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.ASCII, x, JavaType.INPUTSTREAM, -1L);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }
    
    @Override
    public final void setAsciiStream(final int n, final InputStream x, final int length) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[] { n, x, length });
        }
        this.checkClosed();
        this.setStream(n, StreamType.ASCII, x, JavaType.INPUTSTREAM, length);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }
    
    @Override
    public final void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[] { parameterIndex, x, length });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.ASCII, x, JavaType.INPUTSTREAM, length);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }
    
    @Override
    public final void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[] { parameterIndex, x });
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }
    
    @Override
    public final void setBigDecimal(final int parameterIndex, final BigDecimal x, final int precision, final int scale) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[] { parameterIndex, x, precision, scale });
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }
    
    @Override
    public final void setBigDecimal(final int parameterIndex, final BigDecimal x, final int precision, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[] { parameterIndex, x, precision, scale, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.DECIMAL, x, JavaType.BIGDECIMAL, precision, scale, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }
    
    @Override
    public final void setMoney(final int n, final BigDecimal x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setMoney", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.MONEY, x, JavaType.BIGDECIMAL, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setMoney");
    }
    
    @Override
    public final void setMoney(final int n, final BigDecimal x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setMoney", new Object[] { n, x, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.MONEY, x, JavaType.BIGDECIMAL, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setMoney");
    }
    
    @Override
    public final void setSmallMoney(final int n, final BigDecimal x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setSmallMoney", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.SMALLMONEY, x, JavaType.BIGDECIMAL, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setSmallMoney");
    }
    
    @Override
    public final void setSmallMoney(final int n, final BigDecimal x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setSmallMoney", new Object[] { n, x, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.SMALLMONEY, x, JavaType.BIGDECIMAL, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setSmallMoney");
    }
    
    @Override
    public final void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBinaryStreaml", new Object[] { parameterIndex, x });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.BINARY, x, JavaType.INPUTSTREAM, -1L);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }
    
    @Override
    public final void setBinaryStream(final int n, final InputStream x, final int length) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[] { n, x, length });
        }
        this.checkClosed();
        this.setStream(n, StreamType.BINARY, x, JavaType.INPUTSTREAM, length);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }
    
    @Override
    public final void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[] { parameterIndex, x, length });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.BINARY, x, JavaType.INPUTSTREAM, length);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }
    
    @Override
    public final void setBoolean(final int n, final boolean x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBoolean", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BIT, x, JavaType.BOOLEAN, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBoolean");
    }
    
    @Override
    public final void setBoolean(final int n, final boolean x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBoolean", new Object[] { n, x, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BIT, x, JavaType.BOOLEAN, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBoolean");
    }
    
    @Override
    public final void setByte(final int n, final byte x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setByte", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TINYINT, x, JavaType.BYTE, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setByte");
    }
    
    @Override
    public final void setByte(final int n, final byte x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setByte", new Object[] { n, x, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TINYINT, x, JavaType.BYTE, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setByte");
    }
    
    @Override
    public final void setBytes(final int n, final byte[] x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBytes", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BINARY, x, JavaType.BYTEARRAY, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBytes");
    }
    
    @Override
    public final void setBytes(final int n, final byte[] x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBytes", new Object[] { n, x, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BINARY, x, JavaType.BYTEARRAY, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBytes");
    }
    
    @Override
    public final void setUniqueIdentifier(final int index, final String guid) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setUniqueIdentifier", new Object[] { index, guid });
        }
        this.checkClosed();
        this.setValue(index, JDBCType.GUID, guid, JavaType.STRING, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setUniqueIdentifier");
    }
    
    @Override
    public final void setUniqueIdentifier(final int index, final String guid, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setUniqueIdentifier", new Object[] { index, guid, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(index, JDBCType.GUID, guid, JavaType.STRING, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setUniqueIdentifier");
    }
    
    @Override
    public final void setDouble(final int n, final double x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setDouble", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DOUBLE, x, JavaType.DOUBLE, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDouble");
    }
    
    @Override
    public final void setDouble(final int n, final double x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setDouble", new Object[] { n, x, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DOUBLE, x, JavaType.DOUBLE, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDouble");
    }
    
    @Override
    public final void setFloat(final int n, final float x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setFloat", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.REAL, x, JavaType.FLOAT, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setFloat");
    }
    
    @Override
    public final void setFloat(final int n, final float x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setFloat", new Object[] { n, x, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.REAL, x, JavaType.FLOAT, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setFloat");
    }
    
    @Override
    public final void setGeometry(final int n, final Geometry x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setGeometry", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.GEOMETRY, x, JavaType.STRING, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setGeometry");
    }
    
    @Override
    public final void setGeography(final int n, final Geography x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setGeography", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.GEOGRAPHY, x, JavaType.STRING, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setGeography");
    }
    
    @Override
    public final void setInt(final int n, final int value) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setInt", new Object[] { n, value });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.INTEGER, value, JavaType.INTEGER, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setInt");
    }
    
    @Override
    public final void setInt(final int n, final int value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setInt", new Object[] { n, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.INTEGER, value, JavaType.INTEGER, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setInt");
    }
    
    @Override
    public final void setLong(final int n, final long x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setLong", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BIGINT, x, JavaType.LONG, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setLong");
    }
    
    @Override
    public final void setLong(final int n, final long x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setLong", new Object[] { n, x, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.BIGINT, x, JavaType.LONG, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setLong");
    }
    
    @Override
    public final void setNull(final int index, final int jdbcType) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setNull", new Object[] { index, jdbcType });
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(index), null, JavaType.OBJECT, JDBCType.of(jdbcType), null, null, false, index, null);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNull");
    }
    
    final void setObjectNoType(final int index, final Object obj, final boolean forceEncrypt) throws SQLServerException {
        final Parameter param = this.setterGetParam(index);
        JDBCType targetJDBCType = param.getJdbcType();
        String tvpName = null;
        if (null == obj) {
            if (JDBCType.UNKNOWN == targetJDBCType) {
                targetJDBCType = JDBCType.CHAR;
            }
            this.setObject(param, null, JavaType.OBJECT, targetJDBCType, null, null, forceEncrypt, index, null);
        }
        else {
            JavaType javaType = JavaType.of(obj);
            if (JavaType.TVP == javaType) {
                tvpName = this.getTVPNameIfNull(index, null);
                if (null == tvpName && obj instanceof ResultSet) {
                    throw new SQLServerException(SQLServerException.getErrString("R_TVPnotWorkWithSetObjectResultSet"), (Throwable)null);
                }
            }
            targetJDBCType = javaType.getJDBCType(SSType.UNKNOWN, targetJDBCType);
            if (JDBCType.UNKNOWN == targetJDBCType && obj instanceof UUID) {
                javaType = JavaType.STRING;
                targetJDBCType = JDBCType.GUID;
            }
            this.setObject(param, obj, javaType, targetJDBCType, null, null, forceEncrypt, index, tvpName);
        }
    }
    
    @Override
    public final void setObject(final int index, final Object obj) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { index, obj });
        }
        this.checkClosed();
        this.setObjectNoType(index, obj, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public final void setObject(final int n, final Object obj, final int jdbcType) throws SQLServerException {
        String tvpName = null;
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { n, obj, jdbcType });
        }
        this.checkClosed();
        if (-153 == jdbcType) {
            tvpName = this.getTVPNameIfNull(n, null);
        }
        this.setObject(this.setterGetParam(n), obj, JavaType.of(obj), JDBCType.of(jdbcType), null, null, false, n, tvpName);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public final void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scaleOrLength) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { parameterIndex, x, targetSqlType, scaleOrLength });
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(parameterIndex), x, JavaType.of(x), JDBCType.of(targetSqlType), (2 == targetSqlType || 3 == targetSqlType || 93 == targetSqlType || 92 == targetSqlType || -155 == targetSqlType || InputStream.class.isInstance(x) || Reader.class.isInstance(x)) ? Integer.valueOf(scaleOrLength) : null, null, false, parameterIndex, null);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public final void setObject(final int parameterIndex, final Object x, final int targetSqlType, final Integer precision, final int scale) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { parameterIndex, x, targetSqlType, precision, scale });
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(parameterIndex), x, JavaType.of(x), JDBCType.of(targetSqlType), (2 == targetSqlType || 3 == targetSqlType || InputStream.class.isInstance(x) || Reader.class.isInstance(x)) ? Integer.valueOf(scale) : null, precision, false, parameterIndex, null);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public final void setObject(final int parameterIndex, final Object x, final int targetSqlType, final Integer precision, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { parameterIndex, x, targetSqlType, precision, scale, forceEncrypt });
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(parameterIndex), x, JavaType.of(x), JDBCType.of(targetSqlType), (2 == targetSqlType || 3 == targetSqlType || InputStream.class.isInstance(x) || Reader.class.isInstance(x)) ? Integer.valueOf(scale) : null, precision, forceEncrypt, parameterIndex, null);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    final void setObject(final Parameter param, final Object obj, final JavaType javaType, JDBCType jdbcType, final Integer scale, final Integer precision, final boolean forceEncrypt, final int parameterIndex, final String tvpName) throws SQLServerException {
        assert JDBCType.UNKNOWN != jdbcType;
        if (null != obj || JavaType.TVP == javaType) {
            final JDBCType objectJDBCType = javaType.getJDBCType(SSType.UNKNOWN, jdbcType);
            if (!objectJDBCType.convertsTo(jdbcType)) {
                DataTypes.throwConversionError(objectJDBCType.toString(), jdbcType.toString());
            }
            StreamSetterArgs streamSetterArgs = null;
            switch (javaType) {
                case READER: {
                    streamSetterArgs = new StreamSetterArgs(StreamType.CHARACTER, -1L);
                    break;
                }
                case INPUTSTREAM: {
                    streamSetterArgs = new StreamSetterArgs(jdbcType.isTextual() ? StreamType.CHARACTER : StreamType.BINARY, -1L);
                    break;
                }
                case SQLXML: {
                    streamSetterArgs = new StreamSetterArgs(StreamType.SQLXML, -1L);
                    break;
                }
            }
            param.setValue(jdbcType, obj, javaType, streamSetterArgs, null, precision, scale, this.connection, forceEncrypt, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, tvpName);
        }
        else {
            assert JavaType.OBJECT == javaType;
            if (jdbcType.isUnsupported()) {
                jdbcType = JDBCType.BINARY;
            }
            param.setValue(jdbcType, null, JavaType.OBJECT, null, null, precision, scale, this.connection, false, this.stmtColumnEncriptionSetting, parameterIndex, this.userSQL, tvpName);
        }
    }
    
    @Override
    public final void setObject(final int index, final Object obj, final SQLType jdbcType) throws SQLServerException {
        this.setObject(index, obj, jdbcType.getVendorTypeNumber());
    }
    
    @Override
    public final void setObject(final int parameterIndex, final Object x, final SQLType targetSqlType, final int scaleOrLength) throws SQLServerException {
        this.setObject(parameterIndex, x, targetSqlType.getVendorTypeNumber(), scaleOrLength);
    }
    
    @Override
    public final void setObject(final int parameterIndex, final Object x, final SQLType targetSqlType, final Integer precision, final Integer scale) throws SQLServerException {
        this.setObject(parameterIndex, x, targetSqlType.getVendorTypeNumber(), precision, scale);
    }
    
    @Override
    public final void setObject(final int parameterIndex, final Object x, final SQLType targetSqlType, final Integer precision, final Integer scale, final boolean forceEncrypt) throws SQLServerException {
        this.setObject(parameterIndex, x, targetSqlType.getVendorTypeNumber(), precision, scale, forceEncrypt);
    }
    
    @Override
    public final void setShort(final int index, final short x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setShort", new Object[] { index, x });
        }
        this.checkClosed();
        this.setValue(index, JDBCType.SMALLINT, x, JavaType.SHORT, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setShort");
    }
    
    @Override
    public final void setShort(final int index, final short x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setShort", new Object[] { index, x, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(index, JDBCType.SMALLINT, x, JavaType.SHORT, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setShort");
    }
    
    @Override
    public final void setString(final int index, final String str) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setString", new Object[] { index, str });
        }
        this.checkClosed();
        this.setValue(index, JDBCType.VARCHAR, str, JavaType.STRING, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setString");
    }
    
    @Override
    public final void setString(final int index, final String str, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setString", new Object[] { index, str, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(index, JDBCType.VARCHAR, str, JavaType.STRING, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setString");
    }
    
    @Override
    public final void setNString(final int parameterIndex, final String value) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setNString", new Object[] { parameterIndex, value });
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.NVARCHAR, value, JavaType.STRING, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNString");
    }
    
    @Override
    public final void setNString(final int parameterIndex, final String value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setNString", new Object[] { parameterIndex, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.NVARCHAR, value, JavaType.STRING, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNString");
    }
    
    @Override
    public final void setTime(final int n, final Time x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIME, x, JavaType.TIME, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }
    
    @Override
    public final void setTime(final int n, final Time x, final int scale) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[] { n, x, scale });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIME, x, JavaType.TIME, null, scale, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }
    
    @Override
    public final void setTime(final int n, final Time x, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[] { n, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIME, x, JavaType.TIME, null, scale, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }
    
    @Override
    public final void setTimestamp(final int n, final Timestamp x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }
    
    @Override
    public final void setTimestamp(final int n, final Timestamp x, final int scale) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[] { n, x, scale });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }
    
    @Override
    public final void setTimestamp(final int n, final Timestamp x, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[] { n, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }
    
    @Override
    public final void setDateTimeOffset(final int n, final DateTimeOffset x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }
    
    @Override
    public final void setDateTimeOffset(final int n, final DateTimeOffset x, final int scale) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[] { n, x, scale });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }
    
    @Override
    public final void setDateTimeOffset(final int n, final DateTimeOffset x, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[] { n, x, scale, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATETIMEOFFSET, x, JavaType.DATETIMEOFFSET, null, scale, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }
    
    @Override
    public final void setDate(final int n, final Date x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATE, x, JavaType.DATE, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }
    
    @Override
    public final void setDateTime(final int n, final Timestamp x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setDateTime", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATETIME, x, JavaType.TIMESTAMP, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDateTime");
    }
    
    @Override
    public final void setDateTime(final int n, final Timestamp x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setDateTime", new Object[] { n, x, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATETIME, x, JavaType.TIMESTAMP, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDateTime");
    }
    
    @Override
    public final void setSmallDateTime(final int n, final Timestamp x) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setSmallDateTime", new Object[] { n, x });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setSmallDateTime");
    }
    
    @Override
    public final void setSmallDateTime(final int n, final Timestamp x, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setSmallDateTime", new Object[] { n, x, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.SMALLDATETIME, x, JavaType.TIMESTAMP, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setSmallDateTime");
    }
    
    @Override
    public final void setStructured(final int n, String tvpName, final SQLServerDataTable tvpDataTable) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(n, tvpName);
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[] { n, tvpName, tvpDataTable });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TVP, tvpDataTable, JavaType.TVP, tvpName);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }
    
    @Override
    public final void setStructured(final int n, String tvpName, final ResultSet tvpResultSet) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(n, tvpName);
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[] { n, tvpName, tvpResultSet });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TVP, tvpResultSet, JavaType.TVP, tvpName);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }
    
    @Override
    public final void setStructured(final int n, String tvpName, final ISQLServerDataRecord tvpBulkRecord) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(n, tvpName);
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[] { n, tvpName, tvpBulkRecord });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TVP, tvpBulkRecord, JavaType.TVP, tvpName);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }
    
    String getTVPNameIfNull(final int n, String tvpName) throws SQLServerException {
        if ((null == tvpName || 0 == tvpName.length()) && null != this.procedureName) {
            final SQLServerParameterMetaData pmd = (SQLServerParameterMetaData)this.getParameterMetaData();
            pmd.isTVP = true;
            if (!pmd.procedureIsFound) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_StoredProcedureNotFound"));
                final Object[] msgArgs = { this.procedureName };
                SQLServerException.makeFromDriverError(this.connection, pmd, form.format(msgArgs), null, false);
            }
            try {
                final String tvpNameWithoutSchema = pmd.getParameterTypeName(n);
                final String tvpSchema = pmd.getTVPSchemaFromStoredProcedure(n);
                if (null != tvpSchema) {
                    tvpName = "[" + tvpSchema + "].[" + tvpNameWithoutSchema + "]";
                }
                else {
                    tvpName = tvpNameWithoutSchema;
                }
            }
            catch (final SQLException e) {
                throw new SQLServerException(SQLServerException.getErrString("R_metaDataErrorForParameter"), null, 0, e);
            }
        }
        return tvpName;
    }
    
    @Deprecated
    @Override
    public final void setUnicodeStream(final int n, final InputStream x, final int length) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }
    
    @Override
    public final void addBatch() throws SQLServerException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "addBatch");
        this.checkClosed();
        if (this.batchParamValues == null) {
            this.batchParamValues = new ArrayList<Parameter[]>();
        }
        final int numParams = this.inOutParam.length;
        final Parameter[] paramValues = new Parameter[numParams];
        for (int i = 0; i < numParams; ++i) {
            paramValues[i] = this.inOutParam[i].cloneForBatch();
        }
        this.batchParamValues.add(paramValues);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "addBatch");
    }
    
    @Override
    public final void clearBatch() throws SQLServerException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "clearBatch");
        this.checkClosed();
        this.batchParamValues = null;
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "clearBatch");
    }
    
    @Override
    public int[] executeBatch() throws SQLServerException, BatchUpdateException, SQLTimeoutException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "executeBatch");
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerPreparedStatement.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        this.discardLastExecutionResults();
        this.localUserSQL = this.userSQL;
        try {
            if (this.useBulkCopyForBatchInsert && this.connection.isAzureDW() && this.isInsert(this.localUserSQL)) {
                if (null == this.batchParamValues) {
                    final int[] updateCounts = new int[0];
                    SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "executeBatch", updateCounts);
                    return updateCounts;
                }
                for (final Parameter[] array : this.batchParamValues) {
                    final Parameter[] paramValues = array;
                    for (final Parameter paramValue : array) {
                        if (paramValue.isOutput()) {
                            throw new BatchUpdateException(SQLServerException.getErrString("R_outParamsNotPermittedinBatch"), null, 0, (int[])null);
                        }
                    }
                }
                final String tableName = this.parseUserSQLForTableNameDW(false, false, false, false);
                final ArrayList<String> columnList = this.parseUserSQLForColumnListDW();
                final ArrayList<String> valueList = this.parseUserSQLForValueListDW(false);
                this.checkAdditionalQuery();
                try (final SQLServerStatement stmt = (SQLServerStatement)this.connection.createStatement(1003, 1007, this.connection.getHoldability(), this.stmtColumnEncriptionSetting);
                     final SQLServerResultSet rs = stmt.executeQueryInternal("sp_executesql N'SET FMTONLY ON SELECT * FROM " + Util.escapeSingleQuotes(tableName) + " '")) {
                    if (null != columnList && columnList.size() > 0) {
                        if (columnList.size() != valueList.size()) {
                            throw new IllegalArgumentException("Number of provided columns does not match the table definition.");
                        }
                    }
                    else if (rs.getColumnCount() != valueList.size()) {
                        throw new IllegalArgumentException("Number of provided columns does not match the table definition.");
                    }
                    final SQLServerBulkBatchInsertRecord batchRecord = new SQLServerBulkBatchInsertRecord(this.batchParamValues, columnList, valueList, null);
                    for (int i = 1; i <= rs.getColumnCount(); ++i) {
                        final Column c = rs.getColumn(i);
                        final CryptoMetadata cryptoMetadata = c.getCryptoMetadata();
                        final TypeInfo ti = c.getTypeInfo();
                        this.checkValidColumns(ti);
                        int jdbctype;
                        if (null != cryptoMetadata) {
                            jdbctype = cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType().getIntValue();
                        }
                        else {
                            jdbctype = ti.getSSType().getJDBCType().getIntValue();
                        }
                        batchRecord.addColumnMetadata(i, c.getColumnName(), jdbctype, ti.getPrecision(), ti.getScale());
                    }
                    final SQLServerBulkCopy bcOperation = new SQLServerBulkCopy(this.connection);
                    final SQLServerBulkCopyOptions option = new SQLServerBulkCopyOptions();
                    option.setBulkCopyTimeout(this.queryTimeout);
                    bcOperation.setBulkCopyOptions(option);
                    bcOperation.setDestinationTableName(tableName);
                    bcOperation.setStmtColumnEncriptionSetting(this.getStmtColumnEncriptionSetting());
                    bcOperation.setDestinationTableMetadata(rs);
                    bcOperation.writeToServer(batchRecord);
                    bcOperation.close();
                    final int[] updateCounts = new int[this.batchParamValues.size()];
                    for (int j = 0; j < this.batchParamValues.size(); ++j) {
                        updateCounts[j] = 1;
                    }
                    this.batchParamValues = null;
                    SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "executeBatch", updateCounts);
                    return updateCounts;
                }
            }
        }
        catch (final SQLException e) {
            throw new BatchUpdateException(e.getMessage(), null, 0, (int[])null);
        }
        catch (final IllegalArgumentException e2) {
            if (this.getStatementLogger().isLoggable(Level.FINE)) {
                this.getStatementLogger().fine("Parsing user's Batch Insert SQL Query failed: " + e2.getMessage());
                this.getStatementLogger().fine("Falling back to the original implementation for Batch Insert.");
            }
        }
        int[] updateCounts;
        if (null == this.batchParamValues) {
            updateCounts = new int[0];
        }
        else {
            try {
                for (final Parameter[] array3 : this.batchParamValues) {
                    final Parameter[] paramValues = array3;
                    for (final Parameter paramValue : array3) {
                        if (paramValue.isOutput()) {
                            throw new BatchUpdateException(SQLServerException.getErrString("R_outParamsNotPermittedinBatch"), null, 0, (int[])null);
                        }
                    }
                }
                final PrepStmtBatchExecCmd batchCommand = new PrepStmtBatchExecCmd(this);
                this.executeStatement(batchCommand);
                updateCounts = new int[batchCommand.updateCounts.length];
                for (int k = 0; k < batchCommand.updateCounts.length; ++k) {
                    updateCounts[k] = (int)batchCommand.updateCounts[k];
                }
                if (null != batchCommand.batchException) {
                    throw new BatchUpdateException(batchCommand.batchException.getMessage(), batchCommand.batchException.getSQLState(), batchCommand.batchException.getErrorCode(), updateCounts);
                }
            }
            finally {
                this.batchParamValues = null;
            }
        }
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "executeBatch", updateCounts);
        return updateCounts;
    }
    
    @Override
    public long[] executeLargeBatch() throws SQLServerException, BatchUpdateException, SQLTimeoutException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "executeLargeBatch");
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerPreparedStatement.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        this.checkClosed();
        this.discardLastExecutionResults();
        this.localUserSQL = this.userSQL;
        try {
            if (this.useBulkCopyForBatchInsert && this.connection.isAzureDW() && this.isInsert(this.localUserSQL)) {
                if (null == this.batchParamValues) {
                    final long[] updateCounts = new long[0];
                    SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "executeLargeBatch", updateCounts);
                    return updateCounts;
                }
                for (final Parameter[] array : this.batchParamValues) {
                    final Parameter[] paramValues = array;
                    for (final Parameter paramValue : array) {
                        if (paramValue.isOutput()) {
                            throw new BatchUpdateException(SQLServerException.getErrString("R_outParamsNotPermittedinBatch"), null, 0, (int[])null);
                        }
                    }
                }
                final String tableName = this.parseUserSQLForTableNameDW(false, false, false, false);
                final ArrayList<String> columnList = this.parseUserSQLForColumnListDW();
                final ArrayList<String> valueList = this.parseUserSQLForValueListDW(false);
                this.checkAdditionalQuery();
                try (final SQLServerStatement stmt = (SQLServerStatement)this.connection.createStatement(1003, 1007, this.connection.getHoldability(), this.stmtColumnEncriptionSetting);
                     final SQLServerResultSet rs = stmt.executeQueryInternal("sp_executesql N'SET FMTONLY ON SELECT * FROM " + Util.escapeSingleQuotes(tableName) + " '")) {
                    if (null != columnList && columnList.size() > 0) {
                        if (columnList.size() != valueList.size()) {
                            throw new IllegalArgumentException("Number of provided columns does not match the table definition.");
                        }
                    }
                    else if (rs.getColumnCount() != valueList.size()) {
                        throw new IllegalArgumentException("Number of provided columns does not match the table definition.");
                    }
                    final SQLServerBulkBatchInsertRecord batchRecord = new SQLServerBulkBatchInsertRecord(this.batchParamValues, columnList, valueList, null);
                    for (int i = 1; i <= rs.getColumnCount(); ++i) {
                        final Column c = rs.getColumn(i);
                        final CryptoMetadata cryptoMetadata = c.getCryptoMetadata();
                        final TypeInfo ti = c.getTypeInfo();
                        this.checkValidColumns(ti);
                        int jdbctype;
                        if (null != cryptoMetadata) {
                            jdbctype = cryptoMetadata.getBaseTypeInfo().getSSType().getJDBCType().getIntValue();
                        }
                        else {
                            jdbctype = ti.getSSType().getJDBCType().getIntValue();
                        }
                        batchRecord.addColumnMetadata(i, c.getColumnName(), jdbctype, ti.getPrecision(), ti.getScale());
                    }
                    final SQLServerBulkCopy bcOperation = new SQLServerBulkCopy(this.connection);
                    final SQLServerBulkCopyOptions option = new SQLServerBulkCopyOptions();
                    option.setBulkCopyTimeout(this.queryTimeout);
                    bcOperation.setBulkCopyOptions(option);
                    bcOperation.setDestinationTableName(tableName);
                    bcOperation.setStmtColumnEncriptionSetting(this.getStmtColumnEncriptionSetting());
                    bcOperation.setDestinationTableMetadata(rs);
                    bcOperation.writeToServer(batchRecord);
                    bcOperation.close();
                    final long[] updateCounts = new long[this.batchParamValues.size()];
                    for (int j = 0; j < this.batchParamValues.size(); ++j) {
                        updateCounts[j] = 1L;
                    }
                    this.batchParamValues = null;
                    SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "executeLargeBatch", updateCounts);
                    return updateCounts;
                }
            }
        }
        catch (final SQLException e) {
            throw new BatchUpdateException(e.getMessage(), null, 0, (int[])null);
        }
        catch (final IllegalArgumentException e2) {
            if (this.getStatementLogger().isLoggable(Level.FINE)) {
                this.getStatementLogger().fine("Parsing user's Batch Insert SQL Query failed: " + e2.getMessage());
                this.getStatementLogger().fine("Falling back to the original implementation for Batch Insert.");
            }
        }
        long[] updateCounts;
        if (null == this.batchParamValues) {
            updateCounts = new long[0];
        }
        else {
            try {
                for (final Parameter[] array3 : this.batchParamValues) {
                    final Parameter[] paramValues = array3;
                    for (final Parameter paramValue : array3) {
                        if (paramValue.isOutput()) {
                            throw new BatchUpdateException(SQLServerException.getErrString("R_outParamsNotPermittedinBatch"), null, 0, (int[])null);
                        }
                    }
                }
                final PrepStmtBatchExecCmd batchCommand = new PrepStmtBatchExecCmd(this);
                this.executeStatement(batchCommand);
                updateCounts = new long[batchCommand.updateCounts.length];
                System.arraycopy(batchCommand.updateCounts, 0, updateCounts, 0, batchCommand.updateCounts.length);
                if (null != batchCommand.batchException) {
                    DriverJDBCVersion.throwBatchUpdateException(batchCommand.batchException, updateCounts);
                }
            }
            finally {
                this.batchParamValues = null;
            }
        }
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "executeLargeBatch", updateCounts);
        return updateCounts;
    }
    
    private void checkValidColumns(final TypeInfo ti) throws SQLServerException {
        final int jdbctype = ti.getSSType().getJDBCType().getIntValue();
        switch (jdbctype) {
            case -155:
            case -151:
            case -150:
            case -148:
            case -146:
            case 91:
            case 92: {
                final String typeName = ti.getSSTypeName();
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupportedDW"));
                throw new IllegalArgumentException(form.format(new Object[] { typeName }));
            }
            case -145:
            case -16:
            case -15:
            case -9:
            case -7:
            case -6:
            case -5:
            case -4:
            case -3:
            case -2:
            case -1:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 7:
            case 8:
            case 12: {
                final String typeName = ti.getSSTypeName();
                if ("geometry".equalsIgnoreCase(typeName) || "geography".equalsIgnoreCase(typeName)) {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                    throw new IllegalArgumentException(form.format(new Object[] { typeName }));
                }
                return;
            }
            case -156:
            case 93:
            case 2013:
            case 2014: {
                return;
            }
            default: {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_BulkTypeNotSupported"));
                final String unsupportedDataType = JDBCType.of(jdbctype).toString();
                throw new IllegalArgumentException(form.format(new Object[] { unsupportedDataType }));
            }
        }
    }
    
    private void checkAdditionalQuery() {
        while (this.checkAndRemoveCommentsAndSpace(true)) {}
        if (this.localUserSQL.length() > 0) {
            throw new IllegalArgumentException("Multiple queries are not allowed.");
        }
    }
    
    private String parseUserSQLForTableNameDW(final boolean hasInsertBeenFound, final boolean hasIntoBeenFound, final boolean hasTableBeenFound, final boolean isExpectingTableName) {
        while (this.checkAndRemoveCommentsAndSpace(false)) {}
        final StringBuilder sb = new StringBuilder();
        if (hasTableBeenFound && !isExpectingTableName) {
            if (this.checkSQLLength(1) && ".".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
                sb.append(".");
                this.localUserSQL = this.localUserSQL.substring(1);
                return sb.toString() + this.parseUserSQLForTableNameDW(true, true, true, true);
            }
            return "";
        }
        else {
            if (!hasInsertBeenFound && this.checkSQLLength(6) && "insert".equalsIgnoreCase(this.localUserSQL.substring(0, 6))) {
                this.localUserSQL = this.localUserSQL.substring(6);
                return this.parseUserSQLForTableNameDW(true, hasIntoBeenFound, hasTableBeenFound, isExpectingTableName);
            }
            if (!hasIntoBeenFound && this.checkSQLLength(6) && "into".equalsIgnoreCase(this.localUserSQL.substring(0, 4))) {
                if (Character.isWhitespace(this.localUserSQL.charAt(4)) || (this.localUserSQL.charAt(4) == '/' && this.localUserSQL.charAt(5) == '*')) {
                    this.localUserSQL = this.localUserSQL.substring(4);
                    return this.parseUserSQLForTableNameDW(hasInsertBeenFound, true, hasTableBeenFound, isExpectingTableName);
                }
                return this.parseUserSQLForTableNameDW(hasInsertBeenFound, true, hasTableBeenFound, isExpectingTableName);
            }
            else if (this.checkSQLLength(1) && "[".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
                int tempint = this.localUserSQL.indexOf("]", 1);
                if (tempint < 0) {
                    throw new IllegalArgumentException("Invalid SQL Query.");
                }
                while (tempint >= 0 && this.checkSQLLength(tempint + 2) && this.localUserSQL.charAt(tempint + 1) == ']') {
                    tempint = this.localUserSQL.indexOf("]", tempint + 2);
                }
                sb.append(this.localUserSQL.substring(0, tempint + 1));
                this.localUserSQL = this.localUserSQL.substring(tempint + 1);
                return sb.toString() + this.parseUserSQLForTableNameDW(true, true, true, false);
            }
            else {
                if (!this.checkSQLLength(1) || !"\"".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
                    while (this.localUserSQL.length() > 0) {
                        if (this.localUserSQL.charAt(0) == '.' || Character.isWhitespace(this.localUserSQL.charAt(0)) || this.checkAndRemoveCommentsAndSpace(false)) {
                            return sb.toString() + this.parseUserSQLForTableNameDW(true, true, true, false);
                        }
                        if (this.localUserSQL.charAt(0) == ';') {
                            throw new IllegalArgumentException("End of query detected before VALUES have been found.");
                        }
                        sb.append(this.localUserSQL.charAt(0));
                        this.localUserSQL = this.localUserSQL.substring(1);
                    }
                    throw new IllegalArgumentException("Invalid SQL Query.");
                }
                int tempint = this.localUserSQL.indexOf("\"", 1);
                if (tempint < 0) {
                    throw new IllegalArgumentException("Invalid SQL Query.");
                }
                while (tempint >= 0 && this.checkSQLLength(tempint + 2) && this.localUserSQL.charAt(tempint + 1) == '\"') {
                    tempint = this.localUserSQL.indexOf("\"", tempint + 2);
                }
                sb.append(this.localUserSQL.substring(0, tempint + 1));
                this.localUserSQL = this.localUserSQL.substring(tempint + 1);
                return sb.toString() + this.parseUserSQLForTableNameDW(true, true, true, false);
            }
        }
    }
    
    private ArrayList<String> parseUserSQLForColumnListDW() {
        while (this.checkAndRemoveCommentsAndSpace(false)) {}
        if (this.checkSQLLength(1) && "(".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
            this.localUserSQL = this.localUserSQL.substring(1);
            return this.parseUserSQLForColumnListDWHelper(new ArrayList<String>());
        }
        return null;
    }
    
    private ArrayList<String> parseUserSQLForColumnListDWHelper(final ArrayList<String> listOfColumns) {
        while (this.checkAndRemoveCommentsAndSpace(false)) {}
        final StringBuilder sb = new StringBuilder();
        while (this.localUserSQL.length() > 0) {
            while (this.checkAndRemoveCommentsAndSpace(false)) {}
            if (this.checkSQLLength(1) && this.localUserSQL.charAt(0) == ')') {
                this.localUserSQL = this.localUserSQL.substring(1);
                return listOfColumns;
            }
            if (this.localUserSQL.charAt(0) == ',') {
                this.localUserSQL = this.localUserSQL.substring(1);
                while (this.checkAndRemoveCommentsAndSpace(false)) {}
            }
            if (this.localUserSQL.charAt(0) == '[') {
                int tempint = this.localUserSQL.indexOf("]", 1);
                if (tempint < 0) {
                    throw new IllegalArgumentException("Invalid SQL Query.");
                }
                while (tempint >= 0 && this.checkSQLLength(tempint + 2) && this.localUserSQL.charAt(tempint + 1) == ']') {
                    this.localUserSQL = this.localUserSQL.substring(0, tempint) + this.localUserSQL.substring(tempint + 1);
                    tempint = this.localUserSQL.indexOf("]", tempint + 1);
                }
                final String tempstr = this.localUserSQL.substring(1, tempint);
                this.localUserSQL = this.localUserSQL.substring(tempint + 1);
                listOfColumns.add(tempstr);
            }
            else if (this.localUserSQL.charAt(0) == '\"') {
                int tempint = this.localUserSQL.indexOf("\"", 1);
                if (tempint < 0) {
                    throw new IllegalArgumentException("Invalid SQL Query.");
                }
                while (tempint >= 0 && this.checkSQLLength(tempint + 2) && this.localUserSQL.charAt(tempint + 1) == '\"') {
                    this.localUserSQL = this.localUserSQL.substring(0, tempint) + this.localUserSQL.substring(tempint + 1);
                    tempint = this.localUserSQL.indexOf("\"", tempint + 1);
                }
                final String tempstr = this.localUserSQL.substring(1, tempint);
                this.localUserSQL = this.localUserSQL.substring(tempint + 1);
                listOfColumns.add(tempstr);
            }
            else {
                while (this.localUserSQL.length() > 0) {
                    if (this.checkAndRemoveCommentsAndSpace(false)) {
                        continue;
                    }
                    if (this.localUserSQL.charAt(0) == ',') {
                        this.localUserSQL = this.localUserSQL.substring(1);
                        listOfColumns.add(sb.toString());
                        sb.setLength(0);
                        break;
                    }
                    if (this.localUserSQL.charAt(0) == ')') {
                        this.localUserSQL = this.localUserSQL.substring(1);
                        listOfColumns.add(sb.toString());
                        return listOfColumns;
                    }
                    sb.append(this.localUserSQL.charAt(0));
                    this.localUserSQL = this.localUserSQL.substring(1);
                    this.localUserSQL = this.localUserSQL.trim();
                }
            }
        }
        throw new IllegalArgumentException("Invalid SQL Query.");
    }
    
    private ArrayList<String> parseUserSQLForValueListDW(final boolean hasValuesBeenFound) {
        if (this.checkAndRemoveCommentsAndSpace(false)) {}
        if (!hasValuesBeenFound) {
            if (this.checkSQLLength(6) && "VALUES".equalsIgnoreCase(this.localUserSQL.substring(0, 6))) {
                this.localUserSQL = this.localUserSQL.substring(6);
                while (this.checkAndRemoveCommentsAndSpace(false)) {}
                if (this.checkSQLLength(1) && "(".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
                    this.localUserSQL = this.localUserSQL.substring(1);
                    return this.parseUserSQLForValueListDWHelper(new ArrayList<String>());
                }
            }
        }
        else {
            while (this.checkAndRemoveCommentsAndSpace(false)) {}
            if (this.checkSQLLength(1) && "(".equalsIgnoreCase(this.localUserSQL.substring(0, 1))) {
                this.localUserSQL = this.localUserSQL.substring(1);
                return this.parseUserSQLForValueListDWHelper(new ArrayList<String>());
            }
        }
        throw new IllegalArgumentException("Invalid SQL Query.");
    }
    
    private ArrayList<String> parseUserSQLForValueListDWHelper(final ArrayList<String> listOfValues) {
        while (this.checkAndRemoveCommentsAndSpace(false)) {}
        final StringBuilder sb = new StringBuilder();
        while (this.localUserSQL.length() > 0) {
            if (this.checkAndRemoveCommentsAndSpace(false)) {
                continue;
            }
            if (this.localUserSQL.charAt(0) == ',' || this.localUserSQL.charAt(0) == ')') {
                if (this.localUserSQL.charAt(0) != ',') {
                    this.localUserSQL = this.localUserSQL.substring(1);
                    listOfValues.add(sb.toString());
                    return listOfValues;
                }
                this.localUserSQL = this.localUserSQL.substring(1);
                if (!"?".equals(sb.toString())) {
                    throw new IllegalArgumentException("Only fully parameterized queries are allowed for using Bulk Copy API for batch insert at the moment.");
                }
                listOfValues.add(sb.toString());
                sb.setLength(0);
            }
            else {
                sb.append(this.localUserSQL.charAt(0));
                this.localUserSQL = this.localUserSQL.substring(1);
                this.localUserSQL = this.localUserSQL.trim();
            }
        }
        throw new IllegalArgumentException("Invalid SQL Query.");
    }
    
    private boolean checkAndRemoveCommentsAndSpace(final boolean checkForSemicolon) {
        this.localUserSQL = this.localUserSQL.trim();
        while (checkForSemicolon && null != this.localUserSQL && this.localUserSQL.length() > 0 && this.localUserSQL.charAt(0) == ';') {
            this.localUserSQL = this.localUserSQL.substring(1);
        }
        if (null == this.localUserSQL || this.localUserSQL.length() < 2) {
            return false;
        }
        if ("/*".equalsIgnoreCase(this.localUserSQL.substring(0, 2))) {
            final int temp = this.localUserSQL.indexOf("*/") + 2;
            if (temp <= 0) {
                this.localUserSQL = "";
                return false;
            }
            this.localUserSQL = this.localUserSQL.substring(temp);
            return true;
        }
        else {
            if (!"--".equalsIgnoreCase(this.localUserSQL.substring(0, 2))) {
                return false;
            }
            final int temp = this.localUserSQL.indexOf("\n") + 1;
            if (temp <= 0) {
                this.localUserSQL = "";
                return false;
            }
            this.localUserSQL = this.localUserSQL.substring(temp);
            return true;
        }
    }
    
    private boolean checkSQLLength(final int length) {
        if (null == this.localUserSQL || this.localUserSQL.length() < length) {
            throw new IllegalArgumentException("Invalid SQL Query.");
        }
        return true;
    }
    
    final void doExecutePreparedStatementBatch(final PrepStmtBatchExecCmd batchCommand) throws SQLServerException {
        this.executeMethod = 4;
        batchCommand.batchException = null;
        final int numBatches = this.batchParamValues.size();
        batchCommand.updateCounts = new long[numBatches];
        for (int i = 0; i < numBatches; ++i) {
            batchCommand.updateCounts[i] = -3L;
        }
        int numBatchesPrepared = 0;
        int numBatchesExecuted = 0;
        final Vector<CryptoMetadata> cryptoMetaBatch = new Vector<CryptoMetadata>();
        if (this.isSelect(this.userSQL)) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_selectNotPermittedinBatch"), null, true);
        }
        this.connection.setMaxRows(0);
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
            SQLServerPreparedStatement.loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }
        final Parameter[] batchParam = new Parameter[this.inOutParam.length];
        TDSWriter tdsWriter = null;
        while (numBatchesExecuted < numBatches) {
            final Parameter[] paramValues = this.batchParamValues.get(numBatchesPrepared);
            assert paramValues.length == batchParam.length;
            System.arraycopy(paramValues, 0, batchParam, 0, paramValues.length);
            final boolean hasExistingTypeDefinitions = this.preparedTypeDefinitions != null;
            boolean hasNewTypeDefinitions = this.buildPreparedStrings(batchParam, false);
            if (0 == numBatchesExecuted && !this.isInternalEncryptionQuery && this.connection.isAEv2()) {
                this.enclaveCEKs = this.connection.initEnclaveParameters(this.preparedSQL, this.preparedTypeDefinitions, batchParam, this.parameterNames);
                this.buildPreparedStrings(batchParam, this.encryptionMetadataIsRetrieved = true);
                for (final Parameter aBatchParam : batchParam) {
                    cryptoMetaBatch.add(aBatchParam.cryptoMeta);
                }
            }
            if (0 == numBatchesExecuted && Util.shouldHonorAEForParameters(this.stmtColumnEncriptionSetting, this.connection) && 0 < batchParam.length && !this.isInternalEncryptionQuery && !this.encryptionMetadataIsRetrieved) {
                this.getParameterEncryptionMetadata(batchParam);
                this.buildPreparedStrings(batchParam, true);
                for (final Parameter aBatchParam : batchParam) {
                    cryptoMetaBatch.add(aBatchParam.cryptoMeta);
                }
            }
            if (0 < numBatchesExecuted) {
                for (int j = 0; j < cryptoMetaBatch.size(); ++j) {
                    batchParam[j].cryptoMeta = cryptoMetaBatch.get(j);
                }
            }
            boolean needsPrepare = true;
            for (int attempt = 1; attempt <= 2; ++attempt) {
                try {
                    if (this.reuseCachedHandle(hasNewTypeDefinitions, 1 < attempt)) {
                        hasNewTypeDefinitions = false;
                    }
                    if (numBatchesExecuted < numBatchesPrepared) {
                        tdsWriter.writeByte((byte)(-1));
                    }
                    else {
                        this.resetForReexecute();
                        tdsWriter = batchCommand.startRequest((byte)3);
                    }
                    ++numBatchesPrepared;
                    needsPrepare = this.doPrepExec(tdsWriter, batchParam, hasNewTypeDefinitions, hasExistingTypeDefinitions);
                    if (needsPrepare || numBatchesPrepared == numBatches) {
                        this.ensureExecuteResultsReader(batchCommand.startResponse(this.getIsResponseBufferingAdaptive()));
                        boolean retry = false;
                        while (numBatchesExecuted < numBatchesPrepared) {
                            this.startResults();
                            try {
                                if (!this.getNextResult(true)) {
                                    return;
                                }
                                if (null != this.resultSet) {
                                    SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_resultsetGeneratedForUpdate"), null, false);
                                }
                            }
                            catch (final SQLServerException e) {
                                if (this.connection.isSessionUnAvailable() || this.connection.rolledBackTransaction()) {
                                    throw e;
                                }
                                if (this.retryBasedOnFailedReuseOfCachedHandle(e, attempt, needsPrepare, true)) {
                                    numBatchesPrepared = numBatchesExecuted;
                                    retry = true;
                                    break;
                                }
                                this.updateCount = -3L;
                                if (null == batchCommand.batchException) {
                                    batchCommand.batchException = e;
                                }
                            }
                            batchCommand.updateCounts[numBatchesExecuted] = ((-1L == this.updateCount) ? -2L : this.updateCount);
                            this.processBatch();
                            ++numBatchesExecuted;
                        }
                        if (retry) {
                            continue;
                        }
                        assert numBatchesExecuted == numBatchesPrepared;
                    }
                    break;
                }
                catch (final SQLException e2) {
                    if (this.retryBasedOnFailedReuseOfCachedHandle(e2, attempt, needsPrepare, true) && this.connection.isStatementPoolingEnabled()) {
                        numBatchesPrepared = numBatchesExecuted;
                    }
                    else {
                        if (null == batchCommand.batchException) {
                            throw e2;
                        }
                        numBatchesExecuted = numBatchesPrepared;
                        ++attempt;
                    }
                }
            }
        }
    }
    
    @Override
    public final void setUseFmtOnly(final boolean useFmtOnly) throws SQLServerException {
        this.checkClosed();
        this.useFmtOnly = useFmtOnly;
    }
    
    @Override
    public final boolean getUseFmtOnly() throws SQLServerException {
        this.checkClosed();
        return this.useFmtOnly;
    }
    
    @Override
    public final void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[] { parameterIndex, reader });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.CHARACTER, reader, JavaType.READER, -1L);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }
    
    @Override
    public final void setCharacterStream(final int n, final Reader reader, final int length) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[] { n, reader, length });
        }
        this.checkClosed();
        this.setStream(n, StreamType.CHARACTER, reader, JavaType.READER, length);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }
    
    @Override
    public final void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[] { parameterIndex, reader, length });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.CHARACTER, reader, JavaType.READER, length);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }
    
    @Override
    public final void setNCharacterStream(final int parameterIndex, final Reader value) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setNCharacterStream", new Object[] { parameterIndex, value });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.NCHARACTER, value, JavaType.READER, -1L);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNCharacterStream");
    }
    
    @Override
    public final void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setNCharacterStream", new Object[] { parameterIndex, value, length });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.NCHARACTER, value, JavaType.READER, length);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNCharacterStream");
    }
    
    @Override
    public final void setRef(final int i, final Ref x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }
    
    @Override
    public final void setBlob(final int i, final Blob x) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[] { i, x });
        }
        this.checkClosed();
        this.setValue(i, JDBCType.BLOB, x, JavaType.BLOB, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }
    
    @Override
    public final void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[] { parameterIndex, inputStream });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, -1L);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }
    
    @Override
    public final void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[] { parameterIndex, inputStream, length });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, length);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }
    
    @Override
    public final void setClob(final int parameterIndex, final Clob clobValue) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[] { parameterIndex, clobValue });
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.CLOB, clobValue, JavaType.CLOB, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }
    
    @Override
    public final void setClob(final int parameterIndex, final Reader reader) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[] { parameterIndex, reader });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.CHARACTER, reader, JavaType.READER, -1L);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }
    
    @Override
    public final void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[] { parameterIndex, reader, length });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.CHARACTER, reader, JavaType.READER, length);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }
    
    @Override
    public final void setNClob(final int parameterIndex, final NClob value) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[] { parameterIndex, value });
        }
        this.checkClosed();
        this.setValue(parameterIndex, JDBCType.NCLOB, value, JavaType.NCLOB, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }
    
    @Override
    public final void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[] { parameterIndex, reader });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.NCHARACTER, reader, JavaType.READER, -1L);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }
    
    @Override
    public final void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[] { parameterIndex, reader, length });
        }
        this.checkClosed();
        this.setStream(parameterIndex, StreamType.NCHARACTER, reader, JavaType.READER, length);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }
    
    @Override
    public final void setArray(final int i, final Array x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }
    
    @Override
    public final void setDate(final int n, final Date x, final Calendar cal) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[] { n, x, cal });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATE, x, JavaType.DATE, cal, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }
    
    @Override
    public final void setDate(final int n, final Date x, final Calendar cal, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[] { n, x, cal, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.DATE, x, JavaType.DATE, cal, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }
    
    @Override
    public final void setTime(final int n, final Time x, final Calendar cal) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[] { n, x, cal });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIME, x, JavaType.TIME, cal, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }
    
    @Override
    public final void setTime(final int n, final Time x, final Calendar cal, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[] { n, x, cal, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIME, x, JavaType.TIME, cal, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }
    
    @Override
    public final void setTimestamp(final int n, final Timestamp x, final Calendar cal) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[] { n, x, cal });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, cal, false);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }
    
    @Override
    public final void setTimestamp(final int n, final Timestamp x, final Calendar cal, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[] { n, x, cal, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(n, JDBCType.TIMESTAMP, x, JavaType.TIMESTAMP, cal, forceEncrypt);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }
    
    @Override
    public final void setNull(final int paramIndex, final int sqlType, final String typeName) throws SQLServerException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setNull", new Object[] { paramIndex, sqlType, typeName });
        }
        this.checkClosed();
        if (-153 == sqlType) {
            this.setObject(this.setterGetParam(paramIndex), null, JavaType.TVP, JDBCType.of(sqlType), null, null, false, paramIndex, typeName);
        }
        else {
            this.setObject(this.setterGetParam(paramIndex), null, JavaType.OBJECT, JDBCType.of(sqlType), null, null, false, paramIndex, typeName);
        }
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNull");
    }
    
    @Override
    public final ParameterMetaData getParameterMetaData(final boolean forceRefresh) throws SQLServerException {
        SQLServerParameterMetaData pmd = this.connection.getCachedParameterMetadata(this.sqlTextCacheKey);
        if (!forceRefresh && null != pmd) {
            return pmd;
        }
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "getParameterMetaData");
        this.checkClosed();
        pmd = new SQLServerParameterMetaData(this, this.userSQL);
        this.connection.registerCachedParameterMetadata(this.sqlTextCacheKey, pmd);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "getParameterMetaData", pmd);
        return pmd;
    }
    
    @Override
    public final ParameterMetaData getParameterMetaData() throws SQLServerException {
        return this.getParameterMetaData(false);
    }
    
    @Override
    public final void setURL(final int parameterIndex, final URL x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }
    
    @Override
    public final void setRowId(final int parameterIndex, final RowId x) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }
    
    @Override
    public final void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
        if (SQLServerPreparedStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "setSQLXML", new Object[] { parameterIndex, xmlObject });
        }
        this.checkClosed();
        this.setSQLXMLInternal(parameterIndex, xmlObject);
        SQLServerPreparedStatement.loggerExternal.exiting(this.getClassNameLogging(), "setSQLXML");
    }
    
    @Override
    public final int executeUpdate(final String sql) throws SQLServerException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "executeUpdate", sql);
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        final Object[] msgArgs = { "executeUpdate()" };
        throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
    }
    
    @Override
    public final boolean execute(final String sql) throws SQLServerException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "execute", sql);
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        final Object[] msgArgs = { "execute()" };
        throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
    }
    
    @Override
    public final ResultSet executeQuery(final String sql) throws SQLServerException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "executeQuery", sql);
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        final Object[] msgArgs = { "executeQuery()" };
        throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
    }
    
    @Override
    public void addBatch(final String sql) throws SQLServerException {
        SQLServerPreparedStatement.loggerExternal.entering(this.getClassNameLogging(), "addBatch", sql);
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        final Object[] msgArgs = { "addBatch()" };
        throw new SQLServerException(this, form.format(msgArgs), null, 0, false);
    }
    
    private final class PrepStmtExecCmd extends TDSCommand
    {
        private static final long serialVersionUID = 4098801171124750861L;
        private final SQLServerPreparedStatement stmt;
        
        PrepStmtExecCmd(final SQLServerPreparedStatement stmt, final int executeMethod) {
            super(stmt.toString() + " executeXXX", SQLServerPreparedStatement.this.queryTimeout, SQLServerPreparedStatement.this.cancelQueryTimeoutSeconds);
            this.stmt = stmt;
            stmt.executeMethod = executeMethod;
        }
        
        @Override
        final boolean doExecute() throws SQLServerException {
            this.stmt.doExecutePreparedStatement(this);
            return false;
        }
        
        @Override
        final void processResponse(final TDSReader tdsReader) throws SQLServerException {
            SQLServerPreparedStatement.this.ensureExecuteResultsReader(tdsReader);
            SQLServerPreparedStatement.this.processExecuteResults();
        }
    }
    
    private final class PrepStmtBatchExecCmd extends TDSCommand
    {
        private static final long serialVersionUID = 5225705304799552318L;
        private final SQLServerPreparedStatement stmt;
        SQLServerException batchException;
        long[] updateCounts;
        
        PrepStmtBatchExecCmd(final SQLServerPreparedStatement stmt) {
            super(stmt.toString() + " executeBatch", SQLServerPreparedStatement.this.queryTimeout, SQLServerPreparedStatement.this.cancelQueryTimeoutSeconds);
            this.stmt = stmt;
        }
        
        @Override
        final boolean doExecute() throws SQLServerException {
            this.stmt.doExecutePreparedStatementBatch(this);
            return true;
        }
        
        @Override
        final void processResponse(final TDSReader tdsReader) throws SQLServerException {
            SQLServerPreparedStatement.this.ensureExecuteResultsReader(tdsReader);
            SQLServerPreparedStatement.this.processExecuteResults();
        }
    }
}
