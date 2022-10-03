package com.microsoft.sqlserver.jdbc;

import java.io.Serializable;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import javax.naming.Reference;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.sql.PooledConnection;
import javax.sql.ConnectionPoolDataSource;

public class SQLServerConnectionPoolDataSource extends SQLServerDataSource implements ConnectionPoolDataSource
{
    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        if (SQLServerConnectionPoolDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnectionPoolDataSource.loggerExternal.entering(this.getClassNameLogging(), "getPooledConnection");
        }
        final PooledConnection pcon = this.getPooledConnection(this.getUser(), this.getPassword());
        if (SQLServerConnectionPoolDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnectionPoolDataSource.loggerExternal.exiting(this.getClassNameLogging(), "getPooledConnection", pcon);
        }
        return pcon;
    }
    
    @Override
    public PooledConnection getPooledConnection(final String user, final String password) throws SQLException {
        if (SQLServerConnectionPoolDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnectionPoolDataSource.loggerExternal.entering(this.getClassNameLogging(), "getPooledConnection", new Object[] { user, "Password not traced" });
        }
        final SQLServerPooledConnection pc = new SQLServerPooledConnection(this, user, password);
        if (SQLServerConnectionPoolDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnectionPoolDataSource.loggerExternal.exiting(this.getClassNameLogging(), "getPooledConnection", pc);
        }
        return pc;
    }
    
    @Override
    public Reference getReference() {
        if (SQLServerConnectionPoolDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnectionPoolDataSource.loggerExternal.entering(this.getClassNameLogging(), "getReference");
        }
        final Reference ref = this.getReferenceInternal("com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource");
        if (SQLServerConnectionPoolDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerConnectionPoolDataSource.loggerExternal.exiting(this.getClassNameLogging(), "getReference", ref);
        }
        return ref;
    }
    
    private Object writeReplace() throws ObjectStreamException {
        return new SerializationProxy(this);
    }
    
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("");
    }
    
    private static class SerializationProxy implements Serializable
    {
        private final Reference ref;
        private static final long serialVersionUID = 654661379842314126L;
        
        SerializationProxy(final SQLServerConnectionPoolDataSource ds) {
            this.ref = ds.getReferenceInternal(null);
        }
        
        private Object readResolve() {
            final SQLServerConnectionPoolDataSource ds = new SQLServerConnectionPoolDataSource();
            ds.initializeFromReference(this.ref);
            return ds;
        }
    }
}
