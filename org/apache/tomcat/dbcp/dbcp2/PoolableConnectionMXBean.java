package org.apache.tomcat.dbcp.dbcp2;

import java.sql.SQLException;

public interface PoolableConnectionMXBean
{
    boolean isClosed() throws SQLException;
    
    String getToString();
    
    boolean getAutoCommit() throws SQLException;
    
    void setAutoCommit(final boolean p0) throws SQLException;
    
    boolean getCacheState();
    
    void setCacheState(final boolean p0);
    
    String getCatalog() throws SQLException;
    
    void setCatalog(final String p0) throws SQLException;
    
    int getHoldability() throws SQLException;
    
    void setHoldability(final int p0) throws SQLException;
    
    boolean isReadOnly() throws SQLException;
    
    void setReadOnly(final boolean p0) throws SQLException;
    
    String getSchema() throws SQLException;
    
    void setSchema(final String p0) throws SQLException;
    
    int getTransactionIsolation() throws SQLException;
    
    void setTransactionIsolation(final int p0) throws SQLException;
    
    void clearCachedState();
    
    void clearWarnings() throws SQLException;
    
    void close() throws SQLException;
    
    void reallyClose() throws SQLException;
}
