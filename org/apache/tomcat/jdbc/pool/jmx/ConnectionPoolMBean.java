package org.apache.tomcat.jdbc.pool.jmx;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;

public interface ConnectionPoolMBean extends PoolConfiguration
{
    int getSize();
    
    int getIdle();
    
    int getActive();
    
    int getNumIdle();
    
    int getNumActive();
    
    int getWaitCount();
    
    long getBorrowedCount();
    
    long getReturnedCount();
    
    long getCreatedCount();
    
    long getReleasedCount();
    
    long getReconnectedCount();
    
    long getRemoveAbandonedCount();
    
    long getReleasedIdleCount();
    
    void checkIdle();
    
    void checkAbandoned();
    
    void testIdle();
    
    void purge();
    
    void purgeOnReturn();
    
    void resetStats();
}
