package com.microsoft.sqlserver.jdbc;

import java.sql.SQLFeatureNotSupportedException;
import java.util.UUID;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;

public final class SQLServerException extends SQLException
{
    private static final long serialVersionUID = -2195310557661496761L;
    static final String EXCEPTION_XOPEN_CONNECTION_CANT_ESTABLISH = "08001";
    static final String EXCEPTION_XOPEN_CONNECTION_DOES_NOT_EXIST = "08003";
    static final String EXCEPTION_XOPEN_CONNECTION_FAILURE = "08006";
    static final String LOG_CLIENT_CONNECTION_ID_PREFIX = " ClientConnectionId:";
    static final int LOGON_FAILED = 18456;
    static final int PASSWORD_EXPIRED = 18488;
    static final int USER_ACCOUNT_LOCKED = 18486;
    static Logger exLogger;
    static final int DRIVER_ERROR_NONE = 0;
    static final int DRIVER_ERROR_FROM_DATABASE = 2;
    static final int DRIVER_ERROR_IO_FAILED = 3;
    static final int DRIVER_ERROR_INVALID_TDS = 4;
    static final int DRIVER_ERROR_SSL_FAILED = 5;
    static final int DRIVER_ERROR_UNSUPPORTED_CONFIG = 6;
    static final int DRIVER_ERROR_INTERMITTENT_TLS_FAILED = 7;
    static final int ERROR_SOCKET_TIMEOUT = 8;
    static final int ERROR_QUERY_TIMEOUT = 9;
    static final int DATA_CLASSIFICATION_INVALID_VERSION = 10;
    static final int DATA_CLASSIFICATION_NOT_EXPECTED = 11;
    static final int DATA_CLASSIFICATION_INVALID_LABEL_INDEX = 12;
    static final int DATA_CLASSIFICATION_INVALID_INFORMATION_TYPE_INDEX = 13;
    private int driverErrorCode;
    private SQLServerError sqlServerError;
    
    final int getDriverErrorCode() {
        return this.driverErrorCode;
    }
    
    final void setDriverErrorCode(final int value) {
        this.driverErrorCode = value;
    }
    
    private void logException(final Object o, final String errText, final boolean bStack) {
        String id = "";
        if (o != null) {
            id = o.toString();
        }
        if (SQLServerException.exLogger.isLoggable(Level.FINE)) {
            SQLServerException.exLogger.fine("*** SQLException:" + id + " " + this.toString() + " " + errText);
        }
        if (bStack && SQLServerException.exLogger.isLoggable(Level.FINE)) {
            final StringBuilder sb = new StringBuilder(100);
            final StackTraceElement[] stackTrace;
            final StackTraceElement[] st = stackTrace = this.getStackTrace();
            for (final StackTraceElement aSt : stackTrace) {
                sb.append(aSt.toString());
            }
            final Throwable t = this.getCause();
            if (t != null) {
                sb.append("\n caused by ").append(t).append("\n");
                final StackTraceElement[] stackTrace2;
                final StackTraceElement[] tst = stackTrace2 = t.getStackTrace();
                for (final StackTraceElement aTst : stackTrace2) {
                    sb.append(aTst.toString());
                }
            }
            SQLServerException.exLogger.fine(sb.toString());
        }
        if (getErrString("R_queryTimedOut").equals(errText)) {
            this.setDriverErrorCode(9);
        }
    }
    
    static String getErrString(final String errCode) {
        return SQLServerResource.getResource(errCode);
    }
    
    SQLServerException(final String errText, final SQLState sqlState, final DriverError driverError, final Throwable cause) {
        this(errText, sqlState.getSQLStateCode(), driverError.getErrorCode(), cause);
    }
    
    SQLServerException(final String errText, final String errState, final int errNum, final Throwable cause) {
        super(errText, errState, errNum);
        this.driverErrorCode = 0;
        this.initCause(cause);
        this.logException(null, errText, true);
        if (Util.isActivityTraceOn()) {
            ActivityCorrelator.setCurrentActivityIdSentFlag();
        }
    }
    
    SQLServerException(final String errText, final Throwable cause) {
        super(errText);
        this.driverErrorCode = 0;
        this.initCause(cause);
        this.logException(null, errText, true);
        if (Util.isActivityTraceOn()) {
            ActivityCorrelator.setCurrentActivityIdSentFlag();
        }
    }
    
    SQLServerException(final Object obj, final String errText, final String errState, final int errNum, final boolean bStack) {
        super(errText, errState, errNum);
        this.driverErrorCode = 0;
        this.logException(obj, errText, bStack);
        if (Util.isActivityTraceOn()) {
            ActivityCorrelator.setCurrentActivityIdSentFlag();
        }
    }
    
    SQLServerException(final Object obj, String errText, final String errState, final SQLServerError sqlServerError, final boolean bStack) {
        super(errText, errState, sqlServerError.getErrorNumber());
        this.driverErrorCode = 0;
        this.sqlServerError = sqlServerError;
        errText = "Msg " + sqlServerError.getErrorNumber() + ", Level " + sqlServerError.getErrorSeverity() + ", State " + sqlServerError.getErrorState() + ", " + errText;
        this.logException(obj, errText, bStack);
    }
    
    static void makeFromDriverError(final SQLServerConnection con, final Object obj, final String errText, final String state, final boolean bStack) throws SQLServerException {
        String stateCode = "";
        if (state != null) {
            stateCode = state;
        }
        if (con == null || !con.xopenStates) {
            stateCode = mapFromXopen(state);
        }
        final SQLServerException theException = new SQLServerException(obj, checkAndAppendClientConnId(errText, con), stateCode, 0, bStack);
        if (null != state && state.equals("08006") && null != con) {
            con.notifyPooledConnection(theException);
            con.close();
        }
        throw theException;
    }
    
    static void makeFromDatabaseError(final SQLServerConnection con, final Object obj, final String errText, final SQLServerError sqlServerError, final boolean bStack) throws SQLServerException {
        final String state = generateStateCode(con, sqlServerError.getErrorNumber(), sqlServerError.getErrorState());
        final SQLServerException theException = new SQLServerException(obj, checkAndAppendClientConnId(errText, con), state, sqlServerError, bStack);
        theException.setDriverErrorCode(2);
        if (sqlServerError.getErrorSeverity() >= 20 && null != con) {
            con.notifyPooledConnection(theException);
            con.close();
        }
        throw theException;
    }
    
    static void ConvertConnectExceptionToSQLServerException(final String hostName, final int portNumber, final SQLServerConnection conn, final Exception ex) throws SQLServerException {
        final Exception connectException = ex;
        if (connectException != null) {
            final MessageFormat formDetail = new MessageFormat(getErrString("R_tcpOpenFailed"));
            final Object[] msgArgsDetail = { connectException.getMessage() };
            final MessageFormat form = new MessageFormat(getErrString("R_tcpipConnectionFailed"));
            final Object[] msgArgs = { hostName, Integer.toString(portNumber), formDetail.format(msgArgsDetail) };
            final String s = form.format(msgArgs);
            makeFromDriverError(conn, conn, s, "08001", false);
        }
    }
    
    static String mapFromXopen(final String state) {
        if (null == state) {
            return null;
        }
        switch (state) {
            case "07009": {
                return "S1093";
            }
            case "08001": {
                return "08S01";
            }
            case "08006": {
                return "08S01";
            }
            default: {
                return "";
            }
        }
    }
    
    static String generateStateCode(final SQLServerConnection con, final int errNum, final Integer databaseState) {
        final boolean xopenStates = con != null && con.xopenStates;
        if (xopenStates) {
            switch (errNum) {
                case 4060: {
                    return "08001";
                }
                case 18456: {
                    return "08001";
                }
                case 2714: {
                    return "42S01";
                }
                case 208: {
                    return "42S02";
                }
                case 207: {
                    return "42S22";
                }
                default: {
                    return "42000";
                }
            }
        }
        else {
            switch (errNum) {
                case 8152: {
                    return "22001";
                }
                case 515:
                case 547: {
                    return "23000";
                }
                case 2601: {
                    return "23000";
                }
                case 2714: {
                    return "S0001";
                }
                case 208: {
                    return "S0002";
                }
                case 1205: {
                    return "40001";
                }
                case 2627: {
                    return "23000";
                }
                default: {
                    final String dbState = databaseState.toString();
                    final StringBuilder trailingZeroes = new StringBuilder("S");
                    for (int i = 0; i < 4 - dbState.length(); ++i) {
                        trailingZeroes.append("0");
                    }
                    return trailingZeroes.append(dbState).toString();
                }
            }
        }
    }
    
    static String checkAndAppendClientConnId(final String errMsg, final SQLServerConnection conn) throws SQLServerException {
        if (null == conn || !conn.attachConnId()) {
            return errMsg;
        }
        final UUID clientConnId = conn.getClientConIdInternal();
        assert null != clientConnId;
        final StringBuilder sb = new StringBuilder(errMsg);
        sb.append(" ClientConnectionId:");
        sb.append(clientConnId.toString());
        return sb.toString();
    }
    
    static void throwNotSupportedException(final SQLServerConnection con, final Object obj) throws SQLServerException {
        makeFromDriverError(con, obj, getErrString("R_notSupported"), null, false);
    }
    
    static void throwFeatureNotSupportedException() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException(getErrString("R_notSupported"));
    }
    
    public SQLServerError getSQLServerError() {
        return this.sqlServerError;
    }
    
    static {
        SQLServerException.exLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerException");
    }
}
