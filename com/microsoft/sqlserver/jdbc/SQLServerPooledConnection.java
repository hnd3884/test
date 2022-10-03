package com.microsoft.sqlserver.jdbc;

import javax.sql.StatementEventListener;
import javax.sql.ConnectionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import javax.sql.ConnectionEventListener;
import java.util.Vector;
import java.io.Serializable;
import javax.sql.PooledConnection;

public class SQLServerPooledConnection implements PooledConnection, Serializable
{
    private static final long serialVersionUID = 3492921646187451164L;
    private final Vector<ConnectionEventListener> listeners;
    private SQLServerDataSource factoryDataSource;
    private SQLServerConnection physicalConnection;
    private SQLServerConnectionPoolProxy lastProxyConnection;
    private String factoryUser;
    private String factoryPassword;
    private Logger pcLogger;
    private final String traceID;
    private static final AtomicInteger basePooledConnectionID;
    
    SQLServerPooledConnection(final SQLServerDataSource ds, final String user, final String password) throws SQLException {
        this.listeners = new Vector<ConnectionEventListener>();
        this.pcLogger = SQLServerDataSource.dsLogger;
        this.factoryDataSource = ds;
        this.factoryUser = user;
        this.factoryPassword = password;
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + " Start create new connection for pool.");
        }
        this.physicalConnection = this.createNewConnection();
        final String nameL = this.getClass().getName();
        this.traceID = nameL.substring(1 + nameL.lastIndexOf(46)) + ":" + nextPooledConnectionID();
        if (this.pcLogger.isLoggable(Level.FINE)) {
            this.pcLogger.fine(this.toString() + " created by (" + ds.toString() + ") Physical connection " + this.safeCID() + ", End create new connection for pool");
        }
    }
    
    @Override
    public String toString() {
        return this.traceID;
    }
    
    private SQLServerConnection createNewConnection() throws SQLException {
        return this.factoryDataSource.getConnectionInternal(this.factoryUser, this.factoryPassword, this);
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + " user:(default).");
        }
        synchronized (this) {
            if (this.physicalConnection == null) {
                SQLServerException.makeFromDriverError(null, this, SQLServerException.getErrString("R_physicalConnectionIsClosed"), "", true);
            }
            this.physicalConnection.doSecurityCheck();
            if (this.pcLogger.isLoggable(Level.FINE)) {
                this.pcLogger.fine(this.toString() + " Physical connection, " + this.safeCID());
            }
            if (this.physicalConnection.needsReconnect()) {
                this.physicalConnection.close();
                this.physicalConnection = this.createNewConnection();
            }
            if (null != this.lastProxyConnection) {
                this.physicalConnection.resetPooledConnection();
                if (!this.lastProxyConnection.isClosed()) {
                    if (this.pcLogger.isLoggable(Level.FINE)) {
                        this.pcLogger.fine(this.toString() + "proxy " + this.lastProxyConnection.toString() + " is not closed before getting the connection.");
                    }
                    this.lastProxyConnection.internalClose();
                }
            }
            this.lastProxyConnection = new SQLServerConnectionPoolProxy(this.physicalConnection);
            if (this.pcLogger.isLoggable(Level.FINE) && !this.lastProxyConnection.isClosed()) {
                this.pcLogger.fine(this.toString() + " proxy " + this.lastProxyConnection.toString() + " is returned.");
            }
            return this.lastProxyConnection;
        }
    }
    
    void notifyEvent(final SQLServerException e) {
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + " Exception:" + e + this.safeCID());
        }
        if (null != e) {
            synchronized (this) {
                if (null != this.lastProxyConnection) {
                    this.lastProxyConnection.internalClose();
                    this.lastProxyConnection = null;
                }
            }
        }
        synchronized (this.listeners) {
            for (int i = 0; i < this.listeners.size(); ++i) {
                final ConnectionEventListener listener = this.listeners.elementAt(i);
                if (listener != null) {
                    final ConnectionEvent ev = new ConnectionEvent(this, e);
                    if (null == e) {
                        if (this.pcLogger.isLoggable(Level.FINER)) {
                            this.pcLogger.finer(this.toString() + " notifyEvent:connectionClosed " + this.safeCID());
                        }
                        listener.connectionClosed(ev);
                    }
                    else {
                        if (this.pcLogger.isLoggable(Level.FINER)) {
                            this.pcLogger.finer(this.toString() + " notifyEvent:connectionErrorOccurred " + this.safeCID());
                        }
                        listener.connectionErrorOccurred(ev);
                    }
                }
            }
        }
    }
    
    @Override
    public void addConnectionEventListener(final ConnectionEventListener listener) {
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + this.safeCID());
        }
        synchronized (this.listeners) {
            this.listeners.add(listener);
        }
    }
    
    @Override
    public void close() throws SQLException {
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + " Closing physical connection, " + this.safeCID());
        }
        synchronized (this) {
            if (null != this.lastProxyConnection) {
                this.lastProxyConnection.internalClose();
            }
            if (null != this.physicalConnection) {
                this.physicalConnection.DetachFromPool();
                this.physicalConnection.close();
            }
            this.physicalConnection = null;
        }
        synchronized (this.listeners) {
            this.listeners.clear();
        }
    }
    
    @Override
    public void removeConnectionEventListener(final ConnectionEventListener listener) {
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + this.safeCID());
        }
        synchronized (this.listeners) {
            this.listeners.remove(listener);
        }
    }
    
    @Override
    public void addStatementEventListener(final StatementEventListener listener) {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }
    
    @Override
    public void removeStatementEventListener(final StatementEventListener listener) {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }
    
    SQLServerConnection getPhysicalConnection() {
        return this.physicalConnection;
    }
    
    private static int nextPooledConnectionID() {
        return SQLServerPooledConnection.basePooledConnectionID.incrementAndGet();
    }
    
    private String safeCID() {
        if (null == this.physicalConnection) {
            return " ConnectionID:(null)";
        }
        return this.physicalConnection.toString();
    }
    
    static {
        basePooledConnectionID = new AtomicInteger(0);
    }
}
