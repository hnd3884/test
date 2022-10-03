package com.sun.jndi.ldap.pool;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

class ConnectionsWeakRef extends WeakReference<ConnectionsRef>
{
    private final Connections conns;
    
    ConnectionsWeakRef(final ConnectionsRef connectionsRef, final ReferenceQueue<? super ConnectionsRef> referenceQueue) {
        super(connectionsRef, referenceQueue);
        this.conns = connectionsRef.getConnections();
    }
    
    Connections getConnections() {
        return this.conns;
    }
}
