package com.sun.jndi.ldap.pool;

public interface PoolCallback
{
    boolean releasePooledConnection(final PooledConnection p0);
    
    boolean removePooledConnection(final PooledConnection p0);
}
