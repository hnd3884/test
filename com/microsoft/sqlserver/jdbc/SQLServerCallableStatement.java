package com.microsoft.sqlserver.jdbc;

import java.sql.SQLType;
import java.sql.RowId;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Comparator;
import java.sql.Array;
import java.sql.Ref;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.SQLXML;
import java.util.UUID;
import microsoft.sql.DateTimeOffset;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.io.Closeable;
import java.util.TreeMap;
import java.util.HashMap;

public class SQLServerCallableStatement extends SQLServerPreparedStatement implements ISQLServerCallableStatement
{
    private static final long serialVersionUID = 5044984771674532350L;
    private HashMap<String, Integer> parameterNames;
    private TreeMap<String, Integer> insensitiveParameterNames;
    int nOutParams;
    int nOutParamsAssigned;
    private int outParamIndex;
    private Parameter lastParamAccessed;
    private Closeable activeStream;
    Map<String, Integer> map;
    AtomicInteger ai;
    
    @Override
    String getClassNameInternal() {
        return "SQLServerCallableStatement";
    }
    
    SQLServerCallableStatement(final SQLServerConnection connection, final String sql, final int nRSType, final int nRSConcur, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        super(connection, sql, nRSType, nRSConcur, stmtColEncSetting);
        this.nOutParams = 0;
        this.nOutParamsAssigned = 0;
        this.outParamIndex = -1;
        this.map = new ConcurrentHashMap<String, Integer>();
        this.ai = new AtomicInteger(0);
    }
    
    @Override
    public void registerOutParameter(final int index, final int sqlType) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { index, sqlType });
        }
        this.checkClosed();
        if (index < 1 || index > this.inOutParam.length) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
            final Object[] msgArgs = { index };
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), "7009", false);
        }
        if (2012 == sqlType) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_featureNotSupported"));
            final Object[] msgArgs = { "REF_CURSOR" };
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), null, false);
        }
        JDBCType jdbcType = JDBCType.of(sqlType);
        this.discardLastExecutionResults();
        if (jdbcType.isUnsupported()) {
            jdbcType = JDBCType.BINARY;
        }
        final Parameter param = this.inOutParam[index - 1];
        assert null != param;
        if (!param.isOutput()) {
            ++this.nOutParams;
        }
        param.registerForOutput(jdbcType, this.connection);
        switch (sqlType) {
            case -151: {
                param.setOutScale(3);
                break;
            }
            case -155:
            case 92:
            case 93: {
                param.setOutScale(7);
                break;
            }
        }
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    private Parameter getOutParameter(final int i) throws SQLServerException {
        this.processResults();
        if (this.inOutParam[i - 1] == this.lastParamAccessed || this.inOutParam[i - 1].isValueGotten()) {
            return this.inOutParam[i - 1];
        }
        while (this.outParamIndex != i - 1) {
            this.skipOutParameters(1, false);
        }
        return this.inOutParam[i - 1];
    }
    
    @Override
    void startResults() {
        super.startResults();
        this.outParamIndex = -1;
        this.nOutParamsAssigned = 0;
        this.lastParamAccessed = null;
        assert null == this.activeStream;
    }
    
    @Override
    void processBatch() throws SQLServerException {
        this.processResults();
        assert this.nOutParams >= 0;
        if (this.nOutParams > 0) {
            this.processOutParameters();
            this.processBatchRemainder();
        }
    }
    
    final void processOutParameters() throws SQLServerException {
        assert this.nOutParams > 0;
        assert null != this.inOutParam;
        this.closeActiveStream();
        if (this.outParamIndex >= 0) {
            for (int index = 0; index < this.inOutParam.length; ++index) {
                if (index != this.outParamIndex && this.inOutParam[index].isValueGotten()) {
                    assert this.inOutParam[index].isOutput();
                    this.inOutParam[index].resetOutputValue();
                }
            }
        }
        assert this.nOutParamsAssigned <= this.nOutParams;
        if (this.nOutParamsAssigned < this.nOutParams) {
            this.skipOutParameters(this.nOutParams - this.nOutParamsAssigned, true);
        }
        if (this.outParamIndex >= 0) {
            this.inOutParam[this.outParamIndex].skipValue(this.resultsReader(), true);
            this.inOutParam[this.outParamIndex].resetOutputValue();
            this.outParamIndex = -1;
        }
    }
    
    private void processBatchRemainder() throws SQLServerException {
        final class ExecDoneHandler extends TDSTokenHandler
        {
            ExecDoneHandler() {
                super("ExecDoneHandler");
            }
            
            @Override
            boolean onDone(final TDSReader tdsReader) throws SQLServerException {
                final StreamDone doneToken = new StreamDone();
                doneToken.setFromTDS(tdsReader);
                if (doneToken.wasRPCInBatch()) {
                    SQLServerCallableStatement.this.startResults();
                    return false;
                }
                return true;
            }
        }
        final ExecDoneHandler execDoneHandler = new ExecDoneHandler();
        TDSParser.parse(this.resultsReader(), execDoneHandler);
    }
    
    private void skipOutParameters(final int numParamsToSkip, final boolean discardValues) throws SQLServerException {
        final class OutParamHandler extends TDSTokenHandler
        {
            final StreamRetValue srv;
            private boolean foundParam;
            
            final boolean foundParam() {
                return this.foundParam;
            }
            
            OutParamHandler() {
                super("OutParamHandler");
                this.srv = new StreamRetValue();
            }
            
            final void reset() {
                this.foundParam = false;
            }
            
            @Override
            boolean onRetValue(final TDSReader tdsReader) throws SQLServerException {
                this.srv.setFromTDS(tdsReader);
                this.foundParam = true;
                return false;
            }
        }
        final OutParamHandler outParamHandler = new OutParamHandler();
        assert numParamsToSkip <= this.nOutParams - this.nOutParamsAssigned;
        for (int paramsSkipped = 0; paramsSkipped < numParamsToSkip; ++paramsSkipped) {
            if (-1 != this.outParamIndex) {
                this.inOutParam[this.outParamIndex].skipValue(this.resultsReader(), discardValues);
                if (discardValues) {
                    this.inOutParam[this.outParamIndex].resetOutputValue();
                }
            }
            outParamHandler.reset();
            TDSParser.parse(this.resultsReader(), outParamHandler);
            if (!outParamHandler.foundParam()) {
                if (discardValues) {
                    break;
                }
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_valueNotSetForParameter"));
                final Object[] msgArgs = { this.outParamIndex + 1 };
                SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), null, false);
            }
            this.outParamIndex = outParamHandler.srv.getOrdinalOrLength();
            this.outParamIndex -= this.outParamIndexAdjustment;
            if (this.outParamIndex < 0 || this.outParamIndex >= this.inOutParam.length || !this.inOutParam[this.outParamIndex].isOutput()) {
                this.getStatementLogger().info(this.toString() + " Unexpected outParamIndex: " + this.outParamIndex + "; adjustment: " + this.outParamIndexAdjustment);
                this.connection.throwInvalidTDS();
            }
            ++this.nOutParamsAssigned;
        }
    }
    
    @Override
    public void registerOutParameter(final int index, final int sqlType, final String typeName) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { index, sqlType, typeName });
        }
        this.checkClosed();
        this.registerOutParameter(index, sqlType);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final int index, final int sqlType, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { index, sqlType, scale });
        }
        this.checkClosed();
        this.registerOutParameter(index, sqlType);
        this.inOutParam[index - 1].setOutScale(scale);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final int index, final int sqlType, final int precision, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { index, sqlType, scale, precision });
        }
        this.checkClosed();
        this.registerOutParameter(index, sqlType);
        this.inOutParam[index - 1].setValueLength(precision);
        this.inOutParam[index - 1].setOutScale(scale);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    private Parameter getterGetParam(final int index) throws SQLServerException {
        this.checkClosed();
        if (index < 1 || index > this.inOutParam.length) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidOutputParameter"));
            final Object[] msgArgs = { index };
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), "07009", false);
        }
        if (!this.inOutParam[index - 1].isOutput()) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_outputParameterNotRegisteredForOutput"));
            final Object[] msgArgs = { index };
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), "07009", true);
        }
        if (!this.wasExecuted()) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_statementMustBeExecuted"), "07009", false);
        }
        this.resultsReader().getCommand().checkForInterrupt();
        this.closeActiveStream();
        if (this.getStatementLogger().isLoggable(Level.FINER)) {
            this.getStatementLogger().finer(this.toString() + " Getting Param:" + index);
        }
        return this.lastParamAccessed = this.getOutParameter(index);
    }
    
    private Object getValue(final int parameterIndex, final JDBCType jdbcType) throws SQLServerException {
        return this.getterGetParam(parameterIndex).getValue(jdbcType, null, null, this.resultsReader());
    }
    
    private Object getValue(final int parameterIndex, final JDBCType jdbcType, final Calendar cal) throws SQLServerException {
        return this.getterGetParam(parameterIndex).getValue(jdbcType, null, cal, this.resultsReader());
    }
    
    private Object getStream(final int parameterIndex, final StreamType streamType) throws SQLServerException {
        final Object value = this.getterGetParam(parameterIndex).getValue(streamType.getJDBCType(), new InputStreamGetterArgs(streamType, this.getIsResponseBufferingAdaptive(), this.getIsResponseBufferingAdaptive(), this.toString()), null, this.resultsReader());
        this.activeStream = (Closeable)value;
        return value;
    }
    
    private Object getSQLXMLInternal(final int parameterIndex) throws SQLServerException {
        final SQLServerSQLXML value = (SQLServerSQLXML)this.getterGetParam(parameterIndex).getValue(JDBCType.SQLXML, new InputStreamGetterArgs(StreamType.SQLXML, this.getIsResponseBufferingAdaptive(), this.getIsResponseBufferingAdaptive(), this.toString()), null, this.resultsReader());
        if (null != value) {
            this.activeStream = value.getStream();
        }
        return value;
    }
    
    @Override
    public int getInt(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getInt", index);
        this.checkClosed();
        final Integer value = (Integer)this.getValue(index, JDBCType.INTEGER);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getInt", value);
        return (null != value) ? value : 0;
    }
    
    @Override
    public int getInt(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getInt", parameterName);
        this.checkClosed();
        final Integer value = (Integer)this.getValue(this.findColumn(parameterName), JDBCType.INTEGER);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getInt", value);
        return (null != value) ? value : 0;
    }
    
    @Override
    public String getString(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getString", index);
        this.checkClosed();
        String value = null;
        final Object objectValue = this.getValue(index, JDBCType.CHAR);
        if (null != objectValue) {
            value = objectValue.toString();
        }
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getString", value);
        return value;
    }
    
    @Override
    public String getString(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getString", parameterName);
        this.checkClosed();
        String value = null;
        final Object objectValue = this.getValue(this.findColumn(parameterName), JDBCType.CHAR);
        if (null != objectValue) {
            value = objectValue.toString();
        }
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getString", value);
        return value;
    }
    
    @Override
    public final String getNString(final int parameterIndex) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getNString", parameterIndex);
        this.checkClosed();
        final String value = (String)this.getValue(parameterIndex, JDBCType.NCHAR);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getNString", value);
        return value;
    }
    
    @Override
    public final String getNString(final String parameterName) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getNString", parameterName);
        this.checkClosed();
        final String value = (String)this.getValue(this.findColumn(parameterName), JDBCType.NCHAR);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getNString", value);
        return value;
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int parameterIndex, final int scale) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", new Object[] { parameterIndex, scale });
        }
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(parameterIndex, JDBCType.DECIMAL);
        if (null != value) {
            value = value.setScale(scale, 1);
        }
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final String parameterName, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", new Object[] { parameterName, scale });
        }
        this.checkClosed();
        BigDecimal value = (BigDecimal)this.getValue(this.findColumn(parameterName), JDBCType.DECIMAL);
        if (null != value) {
            value = value.setScale(scale, 1);
        }
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }
    
    @Override
    public boolean getBoolean(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBoolean", index);
        this.checkClosed();
        final Boolean value = (Boolean)this.getValue(index, JDBCType.BIT);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBoolean", value);
        return null != value && value;
    }
    
    @Override
    public boolean getBoolean(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBoolean", parameterName);
        this.checkClosed();
        final Boolean value = (Boolean)this.getValue(this.findColumn(parameterName), JDBCType.BIT);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBoolean", value);
        return null != value && value;
    }
    
    @Override
    public byte getByte(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getByte", index);
        this.checkClosed();
        final Short shortValue = (Short)this.getValue(index, JDBCType.TINYINT);
        final byte byteValue = (byte)((null != shortValue) ? shortValue.byteValue() : 0);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getByte", byteValue);
        return byteValue;
    }
    
    @Override
    public byte getByte(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getByte", parameterName);
        this.checkClosed();
        final Short shortValue = (Short)this.getValue(this.findColumn(parameterName), JDBCType.TINYINT);
        final byte byteValue = (byte)((null != shortValue) ? shortValue.byteValue() : 0);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getByte", byteValue);
        return byteValue;
    }
    
    @Override
    public byte[] getBytes(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBytes", index);
        this.checkClosed();
        final byte[] value = (byte[])this.getValue(index, JDBCType.BINARY);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBytes", value);
        return value;
    }
    
    @Override
    public byte[] getBytes(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBytes", parameterName);
        this.checkClosed();
        final byte[] value = (byte[])this.getValue(this.findColumn(parameterName), JDBCType.BINARY);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBytes", value);
        return value;
    }
    
    @Override
    public Date getDate(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDate", index);
        this.checkClosed();
        final Date value = (Date)this.getValue(index, JDBCType.DATE);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }
    
    @Override
    public Date getDate(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDate", parameterName);
        this.checkClosed();
        final Date value = (Date)this.getValue(this.findColumn(parameterName), JDBCType.DATE);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }
    
    @Override
    public Date getDate(final int index, final Calendar cal) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDate", new Object[] { index, cal });
        }
        this.checkClosed();
        final Date value = (Date)this.getValue(index, JDBCType.DATE, cal);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }
    
    @Override
    public Date getDate(final String parameterName, final Calendar cal) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDate", new Object[] { parameterName, cal });
        }
        this.checkClosed();
        final Date value = (Date)this.getValue(this.findColumn(parameterName), JDBCType.DATE, cal);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDate", value);
        return value;
    }
    
    @Override
    public double getDouble(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDouble", index);
        this.checkClosed();
        final Double value = (Double)this.getValue(index, JDBCType.DOUBLE);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDouble", value);
        return (null != value) ? value : 0.0;
    }
    
    @Override
    public double getDouble(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDouble", parameterName);
        this.checkClosed();
        final Double value = (Double)this.getValue(this.findColumn(parameterName), JDBCType.DOUBLE);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDouble", value);
        return (null != value) ? value : 0.0;
    }
    
    @Override
    public float getFloat(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getFloat", index);
        this.checkClosed();
        final Float value = (Float)this.getValue(index, JDBCType.REAL);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return (null != value) ? value : 0.0f;
    }
    
    @Override
    public float getFloat(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getFloat", parameterName);
        this.checkClosed();
        final Float value = (Float)this.getValue(this.findColumn(parameterName), JDBCType.REAL);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getFloat", value);
        return (null != value) ? value : 0.0f;
    }
    
    @Override
    public long getLong(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getLong", index);
        this.checkClosed();
        final Long value = (Long)this.getValue(index, JDBCType.BIGINT);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getLong", value);
        return (null != value) ? value : 0L;
    }
    
    @Override
    public long getLong(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getLong", parameterName);
        this.checkClosed();
        final Long value = (Long)this.getValue(this.findColumn(parameterName), JDBCType.BIGINT);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getLong", value);
        return (null != value) ? value : 0L;
    }
    
    @Override
    public Object getObject(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getObject", index);
        this.checkClosed();
        final Object value = this.getValue(index, (null != this.getterGetParam(index).getJdbcTypeSetByUser()) ? this.getterGetParam(index).getJdbcTypeSetByUser() : this.getterGetParam(index).getJdbcType());
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }
    
    @Override
    public <T> T getObject(final int index, final Class<T> type) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getObject", index);
        this.checkClosed();
        Object returnValue;
        if (type == String.class) {
            returnValue = this.getString(index);
        }
        else if (type == Byte.class) {
            final byte byteValue = this.getByte(index);
            returnValue = (this.wasNull() ? null : Byte.valueOf(byteValue));
        }
        else if (type == Short.class) {
            final short shortValue = this.getShort(index);
            returnValue = (this.wasNull() ? null : Short.valueOf(shortValue));
        }
        else if (type == Integer.class) {
            final int intValue = this.getInt(index);
            returnValue = (this.wasNull() ? null : Integer.valueOf(intValue));
        }
        else if (type == Long.class) {
            final long longValue = this.getLong(index);
            returnValue = (this.wasNull() ? null : Long.valueOf(longValue));
        }
        else if (type == BigDecimal.class) {
            returnValue = this.getBigDecimal(index);
        }
        else if (type == Boolean.class) {
            final boolean booleanValue = this.getBoolean(index);
            returnValue = (this.wasNull() ? null : Boolean.valueOf(booleanValue));
        }
        else if (type == Date.class) {
            returnValue = this.getDate(index);
        }
        else if (type == Time.class) {
            returnValue = this.getTime(index);
        }
        else if (type == Timestamp.class) {
            returnValue = this.getTimestamp(index);
        }
        else if (type == DateTimeOffset.class) {
            returnValue = this.getDateTimeOffset(index);
        }
        else if (type == UUID.class) {
            final byte[] guid = this.getBytes(index);
            returnValue = ((null != guid) ? Util.readGUIDtoUUID(guid) : null);
        }
        else if (type == SQLXML.class) {
            returnValue = this.getSQLXML(index);
        }
        else if (type == Blob.class) {
            returnValue = this.getBlob(index);
        }
        else if (type == Clob.class) {
            returnValue = this.getClob(index);
        }
        else if (type == NClob.class) {
            returnValue = this.getNClob(index);
        }
        else if (type == byte[].class) {
            returnValue = this.getBytes(index);
        }
        else if (type == Float.class) {
            final float floatValue = this.getFloat(index);
            returnValue = (this.wasNull() ? null : Float.valueOf(floatValue));
        }
        else {
            if (type != Double.class) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionTo"));
                final Object[] msgArgs = { type };
                throw new SQLServerException(form.format(msgArgs), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
            }
            final double doubleValue = this.getDouble(index);
            returnValue = (this.wasNull() ? null : Double.valueOf(doubleValue));
        }
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getObject", index);
        return type.cast(returnValue);
    }
    
    @Override
    public Object getObject(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getObject", parameterName);
        this.checkClosed();
        final int parameterIndex = this.findColumn(parameterName);
        final Object value = this.getValue(parameterIndex, (null != this.getterGetParam(parameterIndex).getJdbcTypeSetByUser()) ? this.getterGetParam(parameterIndex).getJdbcTypeSetByUser() : this.getterGetParam(parameterIndex).getJdbcType());
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }
    
    @Override
    public <T> T getObject(final String parameterName, final Class<T> type) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getObject", parameterName);
        this.checkClosed();
        final int parameterIndex = this.findColumn(parameterName);
        final T value = this.getObject(parameterIndex, type);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getObject", value);
        return value;
    }
    
    @Override
    public short getShort(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getShort", index);
        this.checkClosed();
        final Short value = (Short)this.getValue(index, JDBCType.SMALLINT);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getShort", value);
        return (short)((null != value) ? ((short)value) : 0);
    }
    
    @Override
    public short getShort(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getShort", parameterName);
        this.checkClosed();
        final Short value = (Short)this.getValue(this.findColumn(parameterName), JDBCType.SMALLINT);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getShort", value);
        return (short)((null != value) ? ((short)value) : 0);
    }
    
    @Override
    public Time getTime(final int index) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getTime", index);
        this.checkClosed();
        final Time value = (Time)this.getValue(index, JDBCType.TIME);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }
    
    @Override
    public Time getTime(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getTime", parameterName);
        this.checkClosed();
        final Time value = (Time)this.getValue(this.findColumn(parameterName), JDBCType.TIME);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }
    
    @Override
    public Time getTime(final int index, final Calendar cal) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getTime", new Object[] { index, cal });
        }
        this.checkClosed();
        final Time value = (Time)this.getValue(index, JDBCType.TIME, cal);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }
    
    @Override
    public Time getTime(final String parameterName, final Calendar cal) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getTime", new Object[] { parameterName, cal });
        }
        this.checkClosed();
        final Time value = (Time)this.getValue(this.findColumn(parameterName), JDBCType.TIME, cal);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getTime", value);
        return value;
    }
    
    @Override
    public Timestamp getTimestamp(final int index) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", index);
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(index, JDBCType.TIMESTAMP);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getTimestamp", value);
        return value;
    }
    
    @Override
    public Timestamp getTimestamp(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", parameterName);
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(parameterName), JDBCType.TIMESTAMP);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getTimestamp", value);
        return value;
    }
    
    @Override
    public Timestamp getTimestamp(final int index, final Calendar cal) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", new Object[] { index, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(index, JDBCType.TIMESTAMP, cal);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getTimestamp", value);
        return value;
    }
    
    @Override
    public Timestamp getTimestamp(final String name, final Calendar cal) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getTimestamp", new Object[] { name, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(name), JDBCType.TIMESTAMP, cal);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getTimestamp", value);
        return value;
    }
    
    @Override
    public Timestamp getDateTime(final int index) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDateTime", index);
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(index, JDBCType.DATETIME);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getDateTime(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDateTime", parameterName);
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(parameterName), JDBCType.DATETIME);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getDateTime(final int index, final Calendar cal) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDateTime", new Object[] { index, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(index, JDBCType.DATETIME, cal);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getDateTime(final String name, final Calendar cal) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDateTime", new Object[] { name, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(name), JDBCType.DATETIME, cal);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getSmallDateTime(final int index) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", index);
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(index, JDBCType.SMALLDATETIME);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getSmallDateTime(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", parameterName);
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(parameterName), JDBCType.SMALLDATETIME);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getSmallDateTime(final int index, final Calendar cal) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", new Object[] { index, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(index, JDBCType.SMALLDATETIME, cal);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }
    
    @Override
    public Timestamp getSmallDateTime(final String name, final Calendar cal) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getSmallDateTime", new Object[] { name, cal });
        }
        this.checkClosed();
        final Timestamp value = (Timestamp)this.getValue(this.findColumn(name), JDBCType.SMALLDATETIME, cal);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getSmallDateTime", value);
        return value;
    }
    
    @Override
    public DateTimeOffset getDateTimeOffset(final int index) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDateTimeOffset", index);
        }
        this.checkClosed();
        if (!this.connection.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        final DateTimeOffset value = (DateTimeOffset)this.getValue(index, JDBCType.DATETIMEOFFSET);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDateTimeOffset", value);
        return value;
    }
    
    @Override
    public DateTimeOffset getDateTimeOffset(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getDateTimeOffset", parameterName);
        this.checkClosed();
        if (!this.connection.isKatmaiOrLater()) {
            throw new SQLServerException(SQLServerException.getErrString("R_notSupported"), SQLState.DATA_EXCEPTION_NOT_SPECIFIC, DriverError.NOT_SET, null);
        }
        final DateTimeOffset value = (DateTimeOffset)this.getValue(this.findColumn(parameterName), JDBCType.DATETIMEOFFSET);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getDateTimeOffset", value);
        return value;
    }
    
    @Override
    public boolean wasNull() throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "wasNull");
        this.checkClosed();
        boolean bWasNull = false;
        if (null != this.lastParamAccessed) {
            bWasNull = this.lastParamAccessed.isNull();
        }
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "wasNull", bWasNull);
        return bWasNull;
    }
    
    @Override
    public final InputStream getAsciiStream(final int parameterIndex) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getAsciiStream", parameterIndex);
        this.checkClosed();
        final InputStream value = (InputStream)this.getStream(parameterIndex, StreamType.ASCII);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getAsciiStream", value);
        return value;
    }
    
    @Override
    public final InputStream getAsciiStream(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getAsciiStream", parameterName);
        this.checkClosed();
        final InputStream value = (InputStream)this.getStream(this.findColumn(parameterName), StreamType.ASCII);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getAsciiStream", value);
        return value;
    }
    
    @Override
    public BigDecimal getBigDecimal(final int parameterIndex) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", parameterIndex);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(parameterIndex, JDBCType.DECIMAL);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }
    
    @Override
    public BigDecimal getBigDecimal(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBigDecimal", parameterName);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(this.findColumn(parameterName), JDBCType.DECIMAL);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBigDecimal", value);
        return value;
    }
    
    @Override
    public BigDecimal getMoney(final int parameterIndex) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getMoney", parameterIndex);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(parameterIndex, JDBCType.MONEY);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getMoney", value);
        return value;
    }
    
    @Override
    public BigDecimal getMoney(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getMoney", parameterName);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(this.findColumn(parameterName), JDBCType.MONEY);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getMoney", value);
        return value;
    }
    
    @Override
    public BigDecimal getSmallMoney(final int parameterIndex) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getSmallMoney", parameterIndex);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(parameterIndex, JDBCType.SMALLMONEY);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getSmallMoney", value);
        return value;
    }
    
    @Override
    public BigDecimal getSmallMoney(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getSmallMoney", parameterName);
        this.checkClosed();
        final BigDecimal value = (BigDecimal)this.getValue(this.findColumn(parameterName), JDBCType.SMALLMONEY);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getSmallMoney", value);
        return value;
    }
    
    @Override
    public final InputStream getBinaryStream(final int parameterIndex) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBinaryStream", parameterIndex);
        this.checkClosed();
        final InputStream value = (InputStream)this.getStream(parameterIndex, StreamType.BINARY);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBinaryStream", value);
        return value;
    }
    
    @Override
    public final InputStream getBinaryStream(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBinaryStream", parameterName);
        this.checkClosed();
        final InputStream value = (InputStream)this.getStream(this.findColumn(parameterName), StreamType.BINARY);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBinaryStream", value);
        return value;
    }
    
    @Override
    public Blob getBlob(final int parameterIndex) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBlob", parameterIndex);
        this.checkClosed();
        final Blob value = (Blob)this.getValue(parameterIndex, JDBCType.BLOB);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBlob", value);
        return value;
    }
    
    @Override
    public Blob getBlob(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getBlob", parameterName);
        this.checkClosed();
        final Blob value = (Blob)this.getValue(this.findColumn(parameterName), JDBCType.BLOB);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getBlob", value);
        return value;
    }
    
    @Override
    public final Reader getCharacterStream(final int parameterIndex) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getCharacterStream", parameterIndex);
        this.checkClosed();
        final Reader reader = (Reader)this.getStream(parameterIndex, StreamType.CHARACTER);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getCharacterStream", reader);
        return reader;
    }
    
    @Override
    public final Reader getCharacterStream(final String parameterName) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getCharacterStream", parameterName);
        this.checkClosed();
        final Reader reader = (Reader)this.getStream(this.findColumn(parameterName), StreamType.CHARACTER);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getCharacterSream", reader);
        return reader;
    }
    
    @Override
    public final Reader getNCharacterStream(final int parameterIndex) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getNCharacterStream", parameterIndex);
        this.checkClosed();
        final Reader reader = (Reader)this.getStream(parameterIndex, StreamType.NCHARACTER);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getNCharacterStream", reader);
        return reader;
    }
    
    @Override
    public final Reader getNCharacterStream(final String parameterName) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getNCharacterStream", parameterName);
        this.checkClosed();
        final Reader reader = (Reader)this.getStream(this.findColumn(parameterName), StreamType.NCHARACTER);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getNCharacterStream", reader);
        return reader;
    }
    
    void closeActiveStream() throws SQLServerException {
        if (null != this.activeStream) {
            try {
                this.activeStream.close();
            }
            catch (final IOException e) {
                SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
            }
            finally {
                this.activeStream = null;
            }
        }
    }
    
    @Override
    public Clob getClob(final int parameterIndex) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getClob", parameterIndex);
        this.checkClosed();
        final Clob clob = (Clob)this.getValue(parameterIndex, JDBCType.CLOB);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getClob", clob);
        return clob;
    }
    
    @Override
    public Clob getClob(final String parameterName) throws SQLServerException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getClob", parameterName);
        this.checkClosed();
        final Clob clob = (Clob)this.getValue(this.findColumn(parameterName), JDBCType.CLOB);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getClob", clob);
        return clob;
    }
    
    @Override
    public NClob getNClob(final int parameterIndex) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getNClob", parameterIndex);
        this.checkClosed();
        final NClob nClob = (NClob)this.getValue(parameterIndex, JDBCType.NCLOB);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getNClob", nClob);
        return nClob;
    }
    
    @Override
    public NClob getNClob(final String parameterName) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getNClob", parameterName);
        this.checkClosed();
        final NClob nClob = (NClob)this.getValue(this.findColumn(parameterName), JDBCType.NCLOB);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getNClob", nClob);
        return nClob;
    }
    
    @Override
    public Object getObject(final int parameterIndex, final Map<String, Class<?>> map) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }
    
    @Override
    public Object getObject(final String parameterName, final Map<String, Class<?>> m) throws SQLException {
        this.checkClosed();
        return this.getObject(this.findColumn(parameterName), m);
    }
    
    @Override
    public Ref getRef(final int parameterIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }
    
    @Override
    public Ref getRef(final String parameterName) throws SQLException {
        this.checkClosed();
        return this.getRef(this.findColumn(parameterName));
    }
    
    @Override
    public Array getArray(final int parameterIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }
    
    @Override
    public Array getArray(final String parameterName) throws SQLException {
        this.checkClosed();
        return this.getArray(this.findColumn(parameterName));
    }
    
    private int findColumn(final String columnName) throws SQLServerException {
        if (null == this.parameterNames) {
            try (final SQLServerStatement s = (SQLServerStatement)this.connection.createStatement()) {
                final ThreePartName threePartName = ThreePartName.parse(this.procedureName);
                final StringBuilder metaQuery = new StringBuilder("exec sp_sproc_columns ");
                if (null != threePartName.getDatabasePart()) {
                    metaQuery.append("@procedure_qualifier=");
                    metaQuery.append(threePartName.getDatabasePart());
                    metaQuery.append(", ");
                }
                if (null != threePartName.getOwnerPart()) {
                    metaQuery.append("@procedure_owner=");
                    metaQuery.append(threePartName.getOwnerPart());
                    metaQuery.append(", ");
                }
                if (null != threePartName.getProcedurePart()) {
                    metaQuery.append("@procedure_name=");
                    metaQuery.append(threePartName.getProcedurePart());
                    metaQuery.append(" , @ODBCVer=3, @fUsePattern=0");
                }
                else {
                    final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_parameterNotDefinedForProcedure"));
                    final Object[] msgArgs = { columnName, "" };
                    SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), "07009", false);
                }
                try (final ResultSet rs = s.executeQueryInternal(metaQuery.toString())) {
                    this.parameterNames = new HashMap<String, Integer>();
                    this.insensitiveParameterNames = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
                    int columnIndex = 0;
                    while (rs.next()) {
                        final String p = rs.getString(4).trim();
                        this.parameterNames.put(p, columnIndex);
                        this.insensitiveParameterNames.put(p, columnIndex++);
                    }
                }
            }
            catch (final SQLException e) {
                SQLServerException.makeFromDriverError(this.connection, this, e.toString(), null, false);
            }
        }
        int l = 0;
        if (null != this.parameterNames) {
            l = this.parameterNames.size();
        }
        if (l == 0) {
            this.map.putIfAbsent(columnName, this.ai.incrementAndGet());
            return this.map.get(columnName);
        }
        final String columnNameWithSign = columnName.startsWith("@") ? columnName : ("@" + columnName);
        Integer matchPos = this.parameterNames.get(columnNameWithSign);
        if (null == matchPos) {
            matchPos = this.insensitiveParameterNames.get(columnNameWithSign);
        }
        if (null == matchPos) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_parameterNotDefinedForProcedure"));
            final Object[] msgArgs = { columnName, this.procedureName };
            SQLServerException.makeFromDriverError(this.connection, this, form.format(msgArgs), "07009", false);
        }
        if (this.bReturnValueSyntax) {
            return matchPos + 1;
        }
        return matchPos;
    }
    
    @Override
    public void setTimestamp(final String parameterName, final Timestamp value, final Calendar calendar) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setTimeStamp", new Object[] { parameterName, value, calendar });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIMESTAMP, value, JavaType.TIMESTAMP, calendar, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTimeStamp");
    }
    
    @Override
    public void setTimestamp(final String parameterName, final Timestamp value, final Calendar calendar, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setTimeStamp", new Object[] { parameterName, value, calendar, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIMESTAMP, value, JavaType.TIMESTAMP, calendar, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTimeStamp");
    }
    
    @Override
    public void setTime(final String parameterName, final Time value, final Calendar calendar) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[] { parameterName, value, calendar });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIME, value, JavaType.TIME, calendar, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }
    
    @Override
    public void setTime(final String parameterName, final Time value, final Calendar calendar, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[] { parameterName, value, calendar, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIME, value, JavaType.TIME, calendar, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }
    
    @Override
    public void setDate(final String parameterName, final Date value, final Calendar calendar) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[] { parameterName, value, calendar });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATE, value, JavaType.DATE, calendar, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }
    
    @Override
    public void setDate(final String parameterName, final Date value, final Calendar calendar, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[] { parameterName, value, calendar, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATE, value, JavaType.DATE, calendar, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }
    
    @Override
    public final void setCharacterStream(final String parameterName, final Reader reader) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[] { parameterName, reader });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.CHARACTER, reader, JavaType.READER, -1L);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }
    
    @Override
    public final void setCharacterStream(final String parameterName, final Reader value, final int length) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[] { parameterName, value, length });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.CHARACTER, value, JavaType.READER, length);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }
    
    @Override
    public final void setCharacterStream(final String parameterName, final Reader reader, final long length) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[] { parameterName, reader, length });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.CHARACTER, reader, JavaType.READER, length);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }
    
    @Override
    public final void setNCharacterStream(final String parameterName, final Reader value) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setNCharacterStream", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.NCHARACTER, value, JavaType.READER, -1L);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNCharacterStream");
    }
    
    @Override
    public final void setNCharacterStream(final String parameterName, final Reader value, final long length) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setNCharacterStream", new Object[] { parameterName, value, length });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.NCHARACTER, value, JavaType.READER, length);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNCharacterStream");
    }
    
    @Override
    public final void setClob(final String parameterName, final Clob value) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.CLOB, value, JavaType.CLOB, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }
    
    @Override
    public final void setClob(final String parameterName, final Reader reader) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[] { parameterName, reader });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.CHARACTER, reader, JavaType.READER, -1L);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }
    
    @Override
    public final void setClob(final String parameterName, final Reader value, final long length) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[] { parameterName, value, length });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.CHARACTER, value, JavaType.READER, length);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }
    
    @Override
    public final void setNClob(final String parameterName, final NClob value) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.NCLOB, value, JavaType.NCLOB, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }
    
    @Override
    public final void setNClob(final String parameterName, final Reader reader) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[] { parameterName, reader });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.NCHARACTER, reader, JavaType.READER, -1L);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }
    
    @Override
    public final void setNClob(final String parameterName, final Reader reader, final long length) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[] { parameterName, reader, length });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.NCHARACTER, reader, JavaType.READER, length);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }
    
    @Override
    public final void setNString(final String parameterName, final String value) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setNString", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.NVARCHAR, value, JavaType.STRING, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNString");
    }
    
    @Override
    public final void setNString(final String parameterName, final String value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setNString", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.NVARCHAR, value, JavaType.STRING, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNString");
    }
    
    @Override
    public void setObject(final String parameterName, final Object value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setObjectNoType(this.findColumn(parameterName), value, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public void setObject(final String parameterName, final Object value, final int sqlType) throws SQLServerException {
        String tvpName = null;
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { parameterName, value, sqlType });
        }
        this.checkClosed();
        if (-153 == sqlType) {
            tvpName = this.getTVPNameIfNull(this.findColumn(parameterName), null);
            this.setObject(this.setterGetParam(this.findColumn(parameterName)), value, JavaType.TVP, JDBCType.TVP, null, null, false, this.findColumn(parameterName), tvpName);
        }
        else {
            this.setObject(this.setterGetParam(this.findColumn(parameterName)), value, JavaType.of(value), JDBCType.of(sqlType), null, null, false, this.findColumn(parameterName), tvpName);
        }
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public void setObject(final String parameterName, final Object value, final int sqlType, final int decimals) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { parameterName, value, sqlType, decimals });
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(this.findColumn(parameterName)), value, JavaType.of(value), JDBCType.of(sqlType), decimals, null, false, this.findColumn(parameterName), null);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public void setObject(final String parameterName, final Object value, final int sqlType, final int decimals, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { parameterName, value, sqlType, decimals, forceEncrypt });
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(this.findColumn(parameterName)), value, JavaType.of(value), JDBCType.of(sqlType), (2 == sqlType || 3 == sqlType) ? Integer.valueOf(decimals) : null, null, forceEncrypt, this.findColumn(parameterName), null);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public final void setObject(final String parameterName, final Object value, final int targetSqlType, final Integer precision, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { parameterName, value, targetSqlType, precision, scale });
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(this.findColumn(parameterName)), value, JavaType.of(value), JDBCType.of(targetSqlType), (2 == targetSqlType || 3 == targetSqlType || InputStream.class.isInstance(value) || Reader.class.isInstance(value)) ? Integer.valueOf(scale) : null, precision, false, this.findColumn(parameterName), null);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public final void setAsciiStream(final String parameterName, final InputStream value) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.ASCII, value, JavaType.INPUTSTREAM, -1L);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }
    
    @Override
    public final void setAsciiStream(final String parameterName, final InputStream value, final int length) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[] { parameterName, value, length });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.ASCII, value, JavaType.INPUTSTREAM, length);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }
    
    @Override
    public final void setAsciiStream(final String parameterName, final InputStream value, final long length) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[] { parameterName, value, length });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.ASCII, value, JavaType.INPUTSTREAM, length);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }
    
    @Override
    public final void setBinaryStream(final String parameterName, final InputStream value) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.BINARY, value, JavaType.INPUTSTREAM, -1L);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }
    
    @Override
    public final void setBinaryStream(final String parameterName, final InputStream value, final int length) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[] { parameterName, value, length });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.BINARY, value, JavaType.INPUTSTREAM, length);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }
    
    @Override
    public final void setBinaryStream(final String parameterName, final InputStream value, final long length) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[] { parameterName, value, length });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.BINARY, value, JavaType.INPUTSTREAM, length);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }
    
    @Override
    public final void setBlob(final String parameterName, final Blob inputStream) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[] { parameterName, inputStream });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BLOB, inputStream, JavaType.BLOB, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }
    
    @Override
    public final void setBlob(final String parameterName, final InputStream value) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.BINARY, value, JavaType.INPUTSTREAM, -1L);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }
    
    @Override
    public final void setBlob(final String parameterName, final InputStream inputStream, final long length) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[] { parameterName, inputStream, length });
        }
        this.checkClosed();
        this.setStream(this.findColumn(parameterName), StreamType.BINARY, inputStream, JavaType.INPUTSTREAM, length);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }
    
    @Override
    public void setTimestamp(final String parameterName, final Timestamp value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIMESTAMP, value, JavaType.TIMESTAMP, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }
    
    @Override
    public void setTimestamp(final String parameterName, final Timestamp value, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIMESTAMP, value, JavaType.TIMESTAMP, null, scale, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }
    
    @Override
    public void setTimestamp(final String parameterName, final Timestamp value, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIMESTAMP, value, JavaType.TIMESTAMP, null, scale, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }
    
    @Override
    public void setDateTimeOffset(final String parameterName, final DateTimeOffset value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATETIMEOFFSET, value, JavaType.DATETIMEOFFSET, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }
    
    @Override
    public void setDateTimeOffset(final String parameterName, final DateTimeOffset value, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATETIMEOFFSET, value, JavaType.DATETIMEOFFSET, null, scale, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }
    
    @Override
    public void setDateTimeOffset(final String parameterName, final DateTimeOffset value, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATETIMEOFFSET, value, JavaType.DATETIMEOFFSET, null, scale, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }
    
    @Override
    public void setDate(final String parameterName, final Date value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATE, value, JavaType.DATE, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }
    
    @Override
    public void setTime(final String parameterName, final Time value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIME, value, JavaType.TIME, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }
    
    @Override
    public void setTime(final String parameterName, final Time value, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIME, value, JavaType.TIME, null, scale, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }
    
    @Override
    public void setTime(final String parameterName, final Time value, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TIME, value, JavaType.TIME, null, scale, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }
    
    @Override
    public void setDateTime(final String parameterName, final Timestamp value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setDateTime", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATETIME, value, JavaType.TIMESTAMP, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDateTime");
    }
    
    @Override
    public void setDateTime(final String parameterName, final Timestamp value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setDateTime", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DATETIME, value, JavaType.TIMESTAMP, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDateTime");
    }
    
    @Override
    public void setSmallDateTime(final String parameterName, final Timestamp value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setSmallDateTime", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLDATETIME, value, JavaType.TIMESTAMP, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setSmallDateTime");
    }
    
    @Override
    public void setSmallDateTime(final String parameterName, final Timestamp value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setSmallDateTime", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLDATETIME, value, JavaType.TIMESTAMP, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setSmallDateTime");
    }
    
    @Override
    public void setUniqueIdentifier(final String parameterName, final String guid) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setUniqueIdentifier", new Object[] { parameterName, guid });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.GUID, guid, JavaType.STRING, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setUniqueIdentifier");
    }
    
    @Override
    public void setUniqueIdentifier(final String parameterName, final String guid, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setUniqueIdentifier", new Object[] { parameterName, guid, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.GUID, guid, JavaType.STRING, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setUniqueIdentifier");
    }
    
    @Override
    public void setBytes(final String parameterName, final byte[] value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBytes", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BINARY, value, JavaType.BYTEARRAY, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBytes");
    }
    
    @Override
    public void setBytes(final String parameterName, final byte[] value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBytes", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BINARY, value, JavaType.BYTEARRAY, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBytes");
    }
    
    @Override
    public void setByte(final String parameterName, final byte value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setByte", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TINYINT, value, JavaType.BYTE, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setByte");
    }
    
    @Override
    public void setByte(final String parameterName, final byte value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setByte", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TINYINT, value, JavaType.BYTE, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setByte");
    }
    
    @Override
    public void setString(final String parameterName, final String value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setString", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.VARCHAR, value, JavaType.STRING, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setString");
    }
    
    @Override
    public void setString(final String parameterName, final String value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setString", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.VARCHAR, value, JavaType.STRING, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setString");
    }
    
    @Override
    public void setMoney(final String parameterName, final BigDecimal value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setMoney", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.MONEY, value, JavaType.BIGDECIMAL, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setMoney");
    }
    
    @Override
    public void setMoney(final String parameterName, final BigDecimal value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setMoney", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.MONEY, value, JavaType.BIGDECIMAL, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setMoney");
    }
    
    @Override
    public void setSmallMoney(final String parameterName, final BigDecimal value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setSmallMoney", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLMONEY, value, JavaType.BIGDECIMAL, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setSmallMoney");
    }
    
    @Override
    public void setSmallMoney(final String parameterName, final BigDecimal value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setSmallMoney", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLMONEY, value, JavaType.BIGDECIMAL, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setSmallMoney");
    }
    
    @Override
    public void setBigDecimal(final String parameterName, final BigDecimal value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DECIMAL, value, JavaType.BIGDECIMAL, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }
    
    @Override
    public void setBigDecimal(final String parameterName, final BigDecimal value, final int precision, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[] { parameterName, value, precision, scale });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DECIMAL, value, JavaType.BIGDECIMAL, precision, scale, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }
    
    @Override
    public void setBigDecimal(final String parameterName, final BigDecimal value, final int precision, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[] { parameterName, value, precision, scale, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DECIMAL, value, JavaType.BIGDECIMAL, precision, scale, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }
    
    @Override
    public void setDouble(final String parameterName, final double value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setDouble", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DOUBLE, value, JavaType.DOUBLE, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDouble");
    }
    
    @Override
    public void setDouble(final String parameterName, final double value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setDouble", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.DOUBLE, value, JavaType.DOUBLE, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setDouble");
    }
    
    @Override
    public void setFloat(final String parameterName, final float value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setFloat", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.REAL, value, JavaType.FLOAT, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setFloat");
    }
    
    @Override
    public void setFloat(final String parameterName, final float value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setFloat", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.REAL, value, JavaType.FLOAT, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setFloat");
    }
    
    @Override
    public void setInt(final String parameterName, final int value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setInt", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.INTEGER, value, JavaType.INTEGER, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setInt");
    }
    
    @Override
    public void setInt(final String parameterName, final int value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setInt", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.INTEGER, value, JavaType.INTEGER, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setInt");
    }
    
    @Override
    public void setLong(final String parameterName, final long value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setLong", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BIGINT, value, JavaType.LONG, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setLong");
    }
    
    @Override
    public void setLong(final String parameterName, final long value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setLong", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BIGINT, value, JavaType.LONG, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setLong");
    }
    
    @Override
    public void setShort(final String parameterName, final short value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setShort", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLINT, value, JavaType.SHORT, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setShort");
    }
    
    @Override
    public void setShort(final String parameterName, final short value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setShort", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.SMALLINT, value, JavaType.SHORT, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setShort");
    }
    
    @Override
    public void setBoolean(final String parameterName, final boolean value) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBoolean", new Object[] { parameterName, value });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BIT, value, JavaType.BOOLEAN, false);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBoolean");
    }
    
    @Override
    public void setBoolean(final String parameterName, final boolean value, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setBoolean", new Object[] { parameterName, value, forceEncrypt });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.BIT, value, JavaType.BOOLEAN, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setBoolean");
    }
    
    @Override
    public void setNull(final String parameterName, final int nType) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setNull", new Object[] { parameterName, nType });
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(this.findColumn(parameterName)), null, JavaType.OBJECT, JDBCType.of(nType), null, null, false, this.findColumn(parameterName), null);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNull");
    }
    
    @Override
    public void setNull(final String parameterName, final int nType, final String sTypeName) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setNull", new Object[] { parameterName, nType, sTypeName });
        }
        this.checkClosed();
        this.setObject(this.setterGetParam(this.findColumn(parameterName)), null, JavaType.OBJECT, JDBCType.of(nType), null, null, false, this.findColumn(parameterName), sTypeName);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setNull");
    }
    
    @Override
    public void setURL(final String parameterName, final URL url) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setURL", parameterName);
        this.checkClosed();
        this.setURL(this.findColumn(parameterName), url);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setURL");
    }
    
    @Override
    public final void setStructured(final String parameterName, String tvpName, final SQLServerDataTable tvpDataTable) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(this.findColumn(parameterName), tvpName);
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[] { parameterName, tvpName, tvpDataTable });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TVP, tvpDataTable, JavaType.TVP, tvpName);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }
    
    @Override
    public final void setStructured(final String parameterName, String tvpName, final ResultSet tvpResultSet) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(this.findColumn(parameterName), tvpName);
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[] { parameterName, tvpName, tvpResultSet });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TVP, tvpResultSet, JavaType.TVP, tvpName);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }
    
    @Override
    public final void setStructured(final String parameterName, String tvpName, final ISQLServerDataRecord tvpDataRecord) throws SQLServerException {
        tvpName = this.getTVPNameIfNull(this.findColumn(parameterName), tvpName);
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setStructured", new Object[] { parameterName, tvpName, tvpDataRecord });
        }
        this.checkClosed();
        this.setValue(this.findColumn(parameterName), JDBCType.TVP, tvpDataRecord, JavaType.TVP, tvpName);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setStructured");
    }
    
    @Override
    public URL getURL(final int parameterIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }
    
    @Override
    public URL getURL(final String parameterName) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }
    
    @Override
    public final void setSQLXML(final String parameterName, final SQLXML xmlObject) throws SQLException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setSQLXML", new Object[] { parameterName, xmlObject });
        }
        this.checkClosed();
        this.setSQLXMLInternal(this.findColumn(parameterName), xmlObject);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setSQLXML");
    }
    
    @Override
    public final SQLXML getSQLXML(final int parameterIndex) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getSQLXML", parameterIndex);
        this.checkClosed();
        final SQLServerSQLXML value = (SQLServerSQLXML)this.getSQLXMLInternal(parameterIndex);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getSQLXML", value);
        return value;
    }
    
    @Override
    public final SQLXML getSQLXML(final String parameterName) throws SQLException {
        SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "getSQLXML", parameterName);
        this.checkClosed();
        final SQLServerSQLXML value = (SQLServerSQLXML)this.getSQLXMLInternal(this.findColumn(parameterName));
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "getSQLXML", value);
        return value;
    }
    
    @Override
    public final void setRowId(final String parameterName, final RowId value) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
    }
    
    @Override
    public final RowId getRowId(final int parameterIndex) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }
    
    @Override
    public final RowId getRowId(final String parameterName) throws SQLException {
        SQLServerException.throwNotSupportedException(this.connection, this);
        return null;
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final int sqlType, final String typeName) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterName, sqlType, typeName });
        }
        this.checkClosed();
        this.registerOutParameter(this.findColumn(parameterName), sqlType, typeName);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final int sqlType, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterName, sqlType, scale });
        }
        this.checkClosed();
        this.registerOutParameter(this.findColumn(parameterName), sqlType, scale);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final int sqlType, final int precision, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterName, sqlType, scale });
        }
        this.checkClosed();
        this.registerOutParameter(this.findColumn(parameterName), sqlType, precision, scale);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final int sqlType) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterName, sqlType });
        }
        this.checkClosed();
        this.registerOutParameter(this.findColumn(parameterName), sqlType);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final SQLType sqlType) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterIndex, sqlType });
        }
        this.registerOutParameter(parameterIndex, sqlType.getVendorTypeNumber());
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final SQLType sqlType, final String typeName) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterIndex, sqlType, typeName });
        }
        this.registerOutParameter(parameterIndex, sqlType.getVendorTypeNumber(), typeName);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final SQLType sqlType, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterIndex, sqlType, scale });
        }
        this.registerOutParameter(parameterIndex, sqlType.getVendorTypeNumber(), scale);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final SQLType sqlType, final int precision, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterIndex, sqlType, scale });
        }
        this.registerOutParameter(parameterIndex, sqlType.getVendorTypeNumber(), precision, scale);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void setObject(final String parameterName, final Object value, final SQLType jdbcType) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { parameterName, value, jdbcType });
        }
        this.setObject(parameterName, value, jdbcType.getVendorTypeNumber());
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public void setObject(final String parameterName, final Object value, final SQLType jdbcType, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { parameterName, value, jdbcType, scale });
        }
        this.setObject(parameterName, value, jdbcType.getVendorTypeNumber(), scale);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public void setObject(final String parameterName, final Object value, final SQLType jdbcType, final int scale, final boolean forceEncrypt) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[] { parameterName, value, jdbcType, scale, forceEncrypt });
        }
        this.setObject(parameterName, value, jdbcType.getVendorTypeNumber(), scale, forceEncrypt);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final SQLType sqlType, final String typeName) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterName, sqlType, typeName });
        }
        this.registerOutParameter(parameterName, sqlType.getVendorTypeNumber(), typeName);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final SQLType sqlType, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterName, sqlType, scale });
        }
        this.registerOutParameter(parameterName, sqlType.getVendorTypeNumber(), scale);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final SQLType sqlType, final int precision, final int scale) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterName, sqlType, scale });
        }
        this.registerOutParameter(parameterName, sqlType.getVendorTypeNumber(), precision, scale);
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final SQLType sqlType) throws SQLServerException {
        if (SQLServerCallableStatement.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerCallableStatement.loggerExternal.entering(this.getClassNameLogging(), "registerOutParameter", new Object[] { parameterName, sqlType });
        }
        this.registerOutParameter(parameterName, sqlType.getVendorTypeNumber());
        SQLServerCallableStatement.loggerExternal.exiting(this.getClassNameLogging(), "registerOutParameter");
    }
}
