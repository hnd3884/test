package com.zoho.cp;

import java.sql.Timestamp;
import java.util.Date;
import com.zoho.mickey.ExceptionUtils;
import com.zoho.mickey.cp.ConnectionInfoFactory;
import com.zoho.mickey.cp.ClosedState;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import com.zoho.mickey.cp.BaseSQLExceptionHandler;
import java.util.Properties;
import com.zoho.mickey.cp.SQLExceptionHandler;
import java.util.logging.Logger;

public class ConnectionCreator
{
    private String connectionUrl;
    private String userName;
    private String password;
    private String driverClass;
    private String dbIp;
    private int transactionIsolation;
    private static final Logger LOGGER;
    private ExceptionSorter exceptionSorter;
    private SQLExceptionHandler exceptionHandler;
    private ConnectionAbortHandler abortHandler;
    private boolean isMysql;
    private MeteredSocketInterface socketFactory;
    
    public void initialize(final Properties dbProps) throws Exception {
        this.connectionUrl = dbProps.getProperty("url");
        this.userName = dbProps.getProperty("username");
        this.password = dbProps.getProperty("password", "");
        this.setIsMysql(this.driverClass = dbProps.getProperty("drivername"));
        this.dbIp = dbProps.getProperty("host");
        this.exceptionSorter = (ExceptionSorter)Class.forName(dbProps.getProperty("exceptionsorterclassname")).newInstance();
        this.exceptionHandler = (SQLExceptionHandler)Class.forName(dbProps.getProperty("sqlexceptionhandler", BaseSQLExceptionHandler.class.getName())).newInstance();
        if (null != dbProps.getProperty("aborthandlerclassname")) {
            this.abortHandler = (ConnectionAbortHandler)Class.forName(dbProps.getProperty("aborthandlerclassname")).newInstance();
        }
        this.transactionIsolation = getTransactionIsolation(dbProps.getProperty("transaction_isolation", "TRANSACTION_READ_COMMITTED"));
        final String socketFacoryClassName = dbProps.getProperty("socketFactory");
        ConnectionCreator.LOGGER.info("Socket Factory className--" + socketFacoryClassName);
        if (socketFacoryClassName != null) {
            this.socketFactory = (MeteredSocketInterface)Class.forName(socketFacoryClassName).newInstance();
        }
        Class.forName(this.driverClass).newInstance();
    }
    
    public Connection createConnection() throws SQLException {
        final Connection con = DriverManager.getConnection(this.connectionUrl, this.userName, this.password);
        con.setTransactionIsolation(this.transactionIsolation);
        return con;
    }
    
    public ConnectionInfo createConnectionInfo() throws SQLException {
        try {
            final ConnectionInfo conInfo = new ConnectionInfo();
            conInfo.conn = this.createConnection();
            if (this.socketFactory != null) {
                MeteredSocket socket = this.socketFactory.getSocketThreadLocal().get();
                if (socket == null) {
                    socket = new MeteredSocket((Socket)null);
                }
                conInfo.socket = socket;
            }
            return conInfo;
        }
        finally {
            if (this.socketFactory != null) {
                this.socketFactory.getSocketThreadLocal().set(null);
            }
        }
    }
    
    void handleException(final SQLException exc, final ConnectionDetail detail) throws SQLException {
        if (!this.exceptionSorter.isExceptionFatal(exc)) {
            this.exceptionHandler.handleSQLException(exc);
            return;
        }
        detail.setIsValid(false);
        ConnectionCreator.LOGGER.log(Level.INFO, "ClusterIP:" + this.getDBIp() + " connection detail is marked as invalid due to error" + detail);
        this.exceptionHandler.handleSQLException(exc);
    }
    
    public String getDBIp() {
        return this.dbIp;
    }
    
    Long fetchServerId(final Connection conn) throws SQLException {
        if (!this.isMysql()) {
            return 0L;
        }
        Long id = 0L;
        final Statement statement = conn.createStatement();
        ResultSet rs = null;
        try {
            rs = statement.executeQuery("select @@server_id");
            if (rs.next()) {
                id = rs.getLong(1);
                return id;
            }
        }
        finally {
            rs.close();
            statement.close();
        }
        return id;
    }
    
    public Long createNewConnAndFetchServerId() throws SQLException {
        if (!this.isMysql()) {
            return 0L;
        }
        final Connection conn = this.createConnection();
        try {
            final Long id = this.fetchServerId(conn);
            return id;
        }
        finally {
            conn.close();
        }
    }
    
    private static int getTransactionIsolation(final String transactionIsolation) {
        if (transactionIsolation.equals("TRANSACTION_NONE")) {
            return 0;
        }
        if (transactionIsolation.equals("TRANSACTION_READ_COMMITTED")) {
            return 2;
        }
        if (transactionIsolation.equals("TRANSACTION_READ_UNCOMMITTED")) {
            return 1;
        }
        if (transactionIsolation.equals("TRANSACTION_REPEATABLE_READ")) {
            return 4;
        }
        if (transactionIsolation.equals("TRANSACTION_SERIALIZABLE")) {
            return 8;
        }
        throw new IllegalArgumentException("Setting Isolation level to unknown state: " + transactionIsolation);
    }
    
    boolean isMysql() {
        return this.isMysql;
    }
    
    private void setIsMysql(final String driverName) {
        this.isMysql = driverName.trim().equals("org.gjt.mm.mysql.Driver");
    }
    
    public ConnectionAbortHandler getAbortHandler() {
        return this.abortHandler;
    }
    
    public SQLExceptionHandler getExceptionHandler() {
        return this.exceptionHandler;
    }
    
    static {
        LOGGER = Logger.getLogger(ConnectionCreator.class.getName());
    }
    
    public static class ConnectionInfo
    {
        Connection conn;
        MeteredSocket socket;
        String stackTrace;
        long dateTime;
        String invokedBy;
        long threadId;
        ClosedState closedState;
        
        ConnectionInfo() {
            if (ConnectionInfoFactory.isTracking()) {
                this.stackTrace = ExceptionUtils.getStackTraceForCurrentThread();
                this.dateTime = new Date().getTime();
                this.closedState = ClosedState.NONE;
                String traceInt = null;
                try {
                    if (this.stackTrace != null && this.stackTrace.contains("RelationalAPI.getConnection(")) {
                        traceInt = this.stackTrace.substring(this.stackTrace.indexOf("RelationalAPI.getConnection("));
                    }
                    else if (this.stackTrace != null && this.stackTrace.contains("TxDataSource.getConnection(")) {
                        traceInt = this.stackTrace.substring(this.stackTrace.indexOf("TxDataSource.getConnection("));
                    }
                }
                catch (final Exception e) {
                    ConnectionCreator.LOGGER.log(Level.WARNING, "Unable to find the root cause. Refer trace");
                    traceInt = null;
                }
                if (traceInt != null) {
                    traceInt = traceInt.substring(traceInt.indexOf("at ") + 3).trim();
                    this.invokedBy = traceInt.substring(0, traceInt.indexOf(")") + 1).trim();
                    this.stackTrace = traceInt;
                }
                else {
                    this.invokedBy = "Unknown";
                }
                this.threadId = Thread.currentThread().getId();
            }
        }
        
        public void setPhysicalConnection(final Connection connection) {
            this.conn = connection;
        }
        
        public String getStackTrace() {
            return this.stackTrace;
        }
        
        public long getDateTime() {
            return this.dateTime;
        }
        
        public String getInvokedBy() {
            return this.invokedBy;
        }
        
        public long getThreadId() {
            return this.threadId;
        }
        
        public ClosedState getClosedState() {
            return this.closedState;
        }
        
        public void setClosedState(final ClosedState closedState) {
            this.closedState = closedState;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("PhysicalConnection :: ").append(this.conn);
            sb.append("\n<threadid>");
            sb.append(this.threadId);
            sb.append("</threadid>");
            sb.append("\n<creationtime>");
            sb.append(new Timestamp(this.dateTime));
            sb.append("</creationtime>");
            sb.append("\n<isclosed>");
            sb.append(this.closedState.name());
            sb.append("</isclosed>");
            sb.append("\n<origin>");
            sb.append(this.invokedBy);
            sb.append("</origin>");
            sb.append("\n<trace>");
            sb.append(this.stackTrace);
            sb.append("</trace>");
            return sb.toString();
        }
    }
}
