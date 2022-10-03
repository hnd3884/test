package com.sun.jndi.ldap;

import javax.naming.NamingException;
import com.sun.jndi.ldap.pool.PooledConnection;
import com.sun.jndi.ldap.pool.PoolCallback;
import java.io.OutputStream;
import com.sun.jndi.ldap.pool.PooledConnectionFactory;

final class LdapClientFactory implements PooledConnectionFactory
{
    private final String host;
    private final int port;
    private final String socketFactory;
    private final int connTimeout;
    private final int readTimeout;
    private final OutputStream trace;
    
    LdapClientFactory(final String host, final int port, final String socketFactory, final int connTimeout, final int readTimeout, final OutputStream trace) {
        this.host = host;
        this.port = port;
        this.socketFactory = socketFactory;
        this.connTimeout = connTimeout;
        this.readTimeout = readTimeout;
        this.trace = trace;
    }
    
    @Override
    public PooledConnection createPooledConnection(final PoolCallback poolCallback) throws NamingException {
        return new LdapClient(this.host, this.port, this.socketFactory, this.connTimeout, this.readTimeout, this.trace, poolCallback);
    }
    
    @Override
    public String toString() {
        return this.host + ":" + this.port;
    }
}
