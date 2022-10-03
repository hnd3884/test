package com.sun.jndi.ldap.pool;

final class ConnectionsRef
{
    private final Connections conns;
    
    ConnectionsRef(final Connections conns) {
        this.conns = conns;
    }
    
    Connections getConnections() {
        return this.conns;
    }
}
