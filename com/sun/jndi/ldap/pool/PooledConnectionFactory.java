package com.sun.jndi.ldap.pool;

import javax.naming.NamingException;

public interface PooledConnectionFactory
{
    PooledConnection createPooledConnection(final PoolCallback p0) throws NamingException;
}
