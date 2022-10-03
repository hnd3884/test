package com.zoho.cp;

import java.util.concurrent.Executor;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.LinkedList;
import java.sql.Connection;

public class ConnectionDetail
{
    Connection physicalConnection;
    private LinkedList<LogicalConnection> children;
    private ConnectionPool connPool;
    private long usageCounter;
    private boolean isValid;
    private Long serverId;
    private MeteredSocket socket;
    String stackTrace;
    AtomicBoolean hasReturned;
    private static final Logger LOGGER;
    private ConnectionAbortHandler abortHandler;
    
    public ConnectionDetail(final Connection physicalConnection, final Long serverId, final ConnectionPool connPool, final MeteredSocket socket) {
        this.children = new LinkedList<LogicalConnection>();
        this.isValid = true;
        this.serverId = 0L;
        this.stackTrace = "";
        this.hasReturned = new AtomicBoolean(false);
        this.physicalConnection = physicalConnection;
        this.connPool = connPool;
        this.serverId = serverId;
        this.socket = socket;
    }
    
    public void addChild(final LogicalConnection child) {
        this.children.add(child);
    }
    
    public void closeAndRemoveChildren() {
        while (true) {
            final LogicalConnection connection = this.children.poll();
            if (connection == null) {
                break;
            }
            connection.setClosed();
        }
    }
    
    public void safeDestroy() {
        try {
            this.physicalConnection.close();
        }
        catch (final SQLException exc) {
            ConnectionDetail.LOGGER.log(Level.SEVERE, "Exception during connnection safe Destroy", exc);
        }
    }
    
    public void returnToPool() {
        if (this.isValid) {
            this.connPool.returnConnectionDetail(this);
        }
        else {
            this.safeDestroy();
            this.connPool.removeConnectionDetail(this);
        }
    }
    
    public void updateUsageCounter(final long usageCounter) {
        this.usageCounter = usageCounter;
    }
    
    public long getUsageCounter() {
        return this.usageCounter;
    }
    
    public void handleException(final SQLException exc) throws SQLException {
        this.connPool.handleException(exc, this);
    }
    
    public void setIsValid(final boolean isValid) {
        this.isValid = isValid;
    }
    
    public boolean isValid() {
        return this.isValid;
    }
    
    public Long getServerId() {
        return this.serverId;
    }
    
    @Override
    public String toString() {
        if (this.hasReturned.get()) {
            this.stackTrace = "";
        }
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{PhysicalConnection :: ").append(this.physicalConnection).append(", ");
        stringBuffer.append("isUsed :: ").append(!this.hasReturned.get()).append(", ");
        stringBuffer.append("isValid :: ").append(this.isValid).append(", ");
        stringBuffer.append("stackTrace :: ").append(this.stackTrace).append("}");
        return stringBuffer.toString();
    }
    
    public MeteredSocket getMeteredSocket() {
        return this.socket;
    }
    
    public void setAbortHandler(final ConnectionAbortHandler abortHandler) {
        this.abortHandler = abortHandler;
    }
    
    public void abort(final Executor executor) {
        if (this.abortHandler != null) {
            try {
                this.abortHandler.abort(this.physicalConnection, executor);
                this.setIsValid(false);
            }
            catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ConnectionDetail.class.getName());
    }
}
