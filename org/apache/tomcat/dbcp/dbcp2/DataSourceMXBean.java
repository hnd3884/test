package org.apache.tomcat.dbcp.dbcp2;

import java.sql.SQLException;

public interface DataSourceMXBean
{
    boolean getAbandonedUsageTracking();
    
    boolean getCacheState();
    
    String[] getConnectionInitSqlsAsArray();
    
    Boolean getDefaultAutoCommit();
    
    String getDefaultCatalog();
    
    Boolean getDefaultReadOnly();
    
    String getDefaultSchema();
    
    int getDefaultTransactionIsolation();
    
    String[] getDisconnectionSqlCodesAsArray();
    
    String getDriverClassName();
    
    boolean getFastFailValidation();
    
    int getInitialSize();
    
    boolean getLifo();
    
    boolean getLogAbandoned();
    
    boolean getLogExpiredConnections();
    
    long getMaxConnLifetimeMillis();
    
    int getMaxIdle();
    
    int getMaxOpenPreparedStatements();
    
    int getMaxTotal();
    
    long getMaxWaitMillis();
    
    long getMinEvictableIdleTimeMillis();
    
    int getMinIdle();
    
    int getNumActive();
    
    int getNumIdle();
    
    int getNumTestsPerEvictionRun();
    
    boolean getRemoveAbandonedOnBorrow();
    
    boolean getRemoveAbandonedOnMaintenance();
    
    int getRemoveAbandonedTimeout();
    
    long getSoftMinEvictableIdleTimeMillis();
    
    boolean getTestOnBorrow();
    
    boolean getTestOnCreate();
    
    boolean getTestWhileIdle();
    
    long getTimeBetweenEvictionRunsMillis();
    
    String getUrl();
    
    String getUsername();
    
    String getValidationQuery();
    
    int getValidationQueryTimeout();
    
    boolean isAccessToUnderlyingConnectionAllowed();
    
    boolean isClearStatementPoolOnReturn();
    
    boolean isClosed();
    
    boolean isPoolPreparedStatements();
    
    void restart() throws SQLException;
    
    void start() throws SQLException;
}
