package org.apache.tomcat.jdbc.pool;

import java.sql.SQLException;

public interface PooledConnectionMBean
{
    long getConnectionVersion();
    
    boolean isInitialized();
    
    boolean isMaxAgeExpired();
    
    boolean isSuspect();
    
    long getTimestamp();
    
    boolean isDiscarded();
    
    long getLastValidated();
    
    long getLastConnected();
    
    boolean isReleased();
    
    void clearWarnings();
    
    boolean isClosed() throws SQLException;
    
    boolean getAutoCommit() throws SQLException;
    
    String getCatalog() throws SQLException;
    
    int getHoldability() throws SQLException;
    
    boolean isReadOnly() throws SQLException;
    
    String getSchema() throws SQLException;
    
    int getTransactionIsolation() throws SQLException;
}
