package com.sun.jndi.ldap.pool;

import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import com.sun.jndi.ldap.LdapPoolManager;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.ArrayList;
import javax.naming.NamingException;
import java.util.WeakHashMap;
import java.util.Map;
import java.lang.ref.Reference;
import java.util.Collection;
import java.lang.ref.ReferenceQueue;

public final class Pool
{
    static final boolean debug;
    private static final ReferenceQueue<ConnectionsRef> queue;
    private static final Collection<Reference<ConnectionsRef>> weakRefs;
    private final int maxSize;
    private final int prefSize;
    private final int initSize;
    private final Map<Object, ConnectionsRef> map;
    
    public Pool(final int initSize, final int prefSize, final int maxSize) {
        this.map = new WeakHashMap<Object, ConnectionsRef>();
        this.prefSize = prefSize;
        this.maxSize = maxSize;
        this.initSize = initSize;
    }
    
    public PooledConnection getPooledConnection(final Object o, final long n, final PooledConnectionFactory pooledConnectionFactory) throws NamingException {
        this.d("get(): ", o);
        if (Pool.debug) {
            synchronized (this.map) {
                this.d("size: ", this.map.size());
            }
        }
        expungeStaleConnections();
        Connections connections;
        synchronized (this.map) {
            connections = this.getConnections(o);
            if (connections == null) {
                this.d("get(): creating new connections list for ", o);
                connections = new Connections(o, this.initSize, this.prefSize, this.maxSize, pooledConnectionFactory);
                final ConnectionsRef connectionsRef = new ConnectionsRef(connections);
                this.map.put(o, connectionsRef);
                Pool.weakRefs.add(new ConnectionsWeakRef(connectionsRef, Pool.queue));
            }
            this.d("get(): size after: ", this.map.size());
        }
        return connections.get(n, pooledConnectionFactory);
    }
    
    private Connections getConnections(final Object o) {
        final ConnectionsRef connectionsRef = this.map.get(o);
        return (connectionsRef != null) ? connectionsRef.getConnections() : null;
    }
    
    public void expire(final long n) {
        final ArrayList list;
        synchronized (this.map) {
            list = new ArrayList((Collection<? extends E>)this.map.values());
        }
        final ArrayList list2 = new ArrayList();
        for (final ConnectionsRef connectionsRef : list) {
            final Connections connections = connectionsRef.getConnections();
            if (connections.expire(n)) {
                this.d("expire(): removing ", connections);
                list2.add(connectionsRef);
            }
        }
        synchronized (this.map) {
            this.map.values().removeAll(list2);
        }
        expungeStaleConnections();
    }
    
    private static void expungeStaleConnections() {
        ConnectionsWeakRef connectionsWeakRef;
        while ((connectionsWeakRef = (ConnectionsWeakRef)Pool.queue.poll()) != null) {
            final Connections connections = connectionsWeakRef.getConnections();
            if (Pool.debug) {
                System.err.println("weak reference cleanup: Closing Connections:" + connections);
            }
            connections.close();
            Pool.weakRefs.remove(connectionsWeakRef);
            connectionsWeakRef.clear();
        }
    }
    
    public void showStats(final PrintStream printStream) {
        printStream.println("===== Pool start ======================");
        printStream.println("maximum pool size: " + this.maxSize);
        printStream.println("preferred pool size: " + this.prefSize);
        printStream.println("initial pool size: " + this.initSize);
        synchronized (this.map) {
            printStream.println("current pool size: " + this.map.size());
            for (final Map.Entry entry : this.map.entrySet()) {
                printStream.println("   " + entry.getKey() + ":" + ((ConnectionsRef)entry.getValue()).getConnections().getStats());
            }
        }
        printStream.println("====== Pool end =====================");
    }
    
    @Override
    public String toString() {
        synchronized (this.map) {
            return super.toString() + " " + this.map.toString();
        }
    }
    
    private void d(final String s, final int n) {
        if (Pool.debug) {
            System.err.println(this + "." + s + n);
        }
    }
    
    private void d(final String s, final Object o) {
        if (Pool.debug) {
            System.err.println(this + "." + s + o);
        }
    }
    
    static {
        debug = LdapPoolManager.debug;
        queue = new ReferenceQueue<ConnectionsRef>();
        weakRefs = Collections.synchronizedList(new LinkedList<Object>());
    }
}
