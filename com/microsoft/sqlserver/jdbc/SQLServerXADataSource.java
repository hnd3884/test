package com.microsoft.sqlserver.jdbc;

import java.io.Serializable;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import javax.naming.Reference;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.sql.XAConnection;
import java.util.logging.Logger;
import javax.sql.XADataSource;

public final class SQLServerXADataSource extends SQLServerConnectionPoolDataSource implements XADataSource
{
    static Logger xaLogger;
    
    @Override
    public XAConnection getXAConnection(final String user, final String password) throws SQLException {
        if (SQLServerXADataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerXADataSource.loggerExternal.entering(this.getClassNameLogging(), "getXAConnection", new Object[] { user, "Password not traced" });
        }
        final SQLServerXAConnection pooledXAConnection = new SQLServerXAConnection(this, user, password);
        if (SQLServerXADataSource.xaLogger.isLoggable(Level.FINER)) {
            SQLServerXADataSource.xaLogger.finer(this.toString() + " user:" + user + pooledXAConnection.toString());
        }
        if (SQLServerXADataSource.xaLogger.isLoggable(Level.FINER)) {
            SQLServerXADataSource.xaLogger.finer(this.toString() + " Start get physical connection.");
        }
        final SQLServerConnection physicalConnection = pooledXAConnection.getPhysicalConnection();
        if (SQLServerXADataSource.xaLogger.isLoggable(Level.FINE)) {
            SQLServerXADataSource.xaLogger.fine(this.toString() + " End get physical connection, " + physicalConnection.toString());
        }
        if (SQLServerXADataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerXADataSource.loggerExternal.exiting(this.getClassNameLogging(), "getXAConnection", pooledXAConnection);
        }
        return pooledXAConnection;
    }
    
    @Override
    public XAConnection getXAConnection() throws SQLException {
        if (SQLServerXADataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerXADataSource.loggerExternal.entering(this.getClassNameLogging(), "getXAConnection");
        }
        return this.getXAConnection(this.getUser(), this.getPassword());
    }
    
    @Override
    public Reference getReference() {
        if (SQLServerXADataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerXADataSource.loggerExternal.entering(this.getClassNameLogging(), "getReference");
        }
        final Reference ref = this.getReferenceInternal("com.microsoft.sqlserver.jdbc.SQLServerXADataSource");
        if (SQLServerXADataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerXADataSource.loggerExternal.exiting(this.getClassNameLogging(), "getReference", ref);
        }
        return ref;
    }
    
    private Object writeReplace() throws ObjectStreamException {
        return new SerializationProxy(this);
    }
    
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("");
    }
    
    static {
        SQLServerXADataSource.xaLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.XA");
    }
    
    private static class SerializationProxy implements Serializable
    {
        private final Reference ref;
        private static final long serialVersionUID = 454661379842314126L;
        
        SerializationProxy(final SQLServerXADataSource ds) {
            this.ref = ds.getReferenceInternal(null);
        }
        
        private Object readResolve() {
            final SQLServerXADataSource ds = new SQLServerXADataSource();
            ds.initializeFromReference(this.ref);
            return ds;
        }
    }
}
