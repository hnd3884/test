package com.sun.jndi.ldap.pool;

import com.sun.jndi.ldap.LdapPoolManager;
import java.util.Iterator;
import java.util.Collection;
import javax.naming.InterruptedNamingException;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.lang.ref.Reference;
import java.util.List;

final class Connections implements PoolCallback
{
    private static final boolean debug;
    private static final boolean trace;
    private static final int DEFAULT_SIZE = 10;
    private final int maxSize;
    private final int prefSize;
    private final List<ConnectionDesc> conns;
    private boolean closed;
    private Reference<Object> ref;
    
    Connections(final Object o, int min, final int prefSize, final int maxSize, final PooledConnectionFactory pooledConnectionFactory) throws NamingException {
        this.closed = false;
        this.maxSize = maxSize;
        if (maxSize > 0) {
            this.prefSize = Math.min(prefSize, maxSize);
            min = Math.min(min, maxSize);
        }
        else {
            this.prefSize = prefSize;
        }
        this.conns = new ArrayList<ConnectionDesc>((maxSize > 0) ? maxSize : 10);
        this.ref = new SoftReference<Object>(o);
        this.d("init size=", min);
        this.d("max size=", maxSize);
        this.d("preferred size=", prefSize);
        for (int i = 0; i < min; ++i) {
            final PooledConnection pooledConnection = pooledConnectionFactory.createPooledConnection(this);
            this.td("Create ", pooledConnection, pooledConnectionFactory);
            this.conns.add(new ConnectionDesc(pooledConnection));
        }
    }
    
    synchronized PooledConnection get(final long n, final PooledConnectionFactory pooledConnectionFactory) throws NamingException {
        final long n2 = (n > 0L) ? System.currentTimeMillis() : 0L;
        long n3 = n;
        this.d("get(): before");
        PooledConnection orCreateConnection;
        while ((orCreateConnection = this.getOrCreateConnection(pooledConnectionFactory)) == null) {
            if (n > 0L && n3 <= 0L) {
                throw new CommunicationException("Timeout exceeded while waiting for a connection: " + n + "ms");
            }
            try {
                this.d("get(): waiting");
                if (n3 > 0L) {
                    this.wait(n3);
                }
                else {
                    this.wait();
                }
            }
            catch (final InterruptedException ex) {
                throw new InterruptedNamingException("Interrupted while waiting for a connection");
            }
            if (n <= 0L) {
                continue;
            }
            n3 = n - (System.currentTimeMillis() - n2);
        }
        this.d("get(): after");
        return orCreateConnection;
    }
    
    private PooledConnection getOrCreateConnection(final PooledConnectionFactory pooledConnectionFactory) throws NamingException {
        final int size = this.conns.size();
        if (this.prefSize <= 0 || size >= this.prefSize) {
            for (int i = 0; i < size; ++i) {
                final PooledConnection tryUse;
                if ((tryUse = this.conns.get(i).tryUse()) != null) {
                    this.d("get(): use ", tryUse);
                    this.td("Use ", tryUse);
                    return tryUse;
                }
            }
        }
        if (this.maxSize > 0 && size >= this.maxSize) {
            return null;
        }
        final PooledConnection pooledConnection = pooledConnectionFactory.createPooledConnection(this);
        this.td("Create and use ", pooledConnection, pooledConnectionFactory);
        this.conns.add(new ConnectionDesc(pooledConnection, true));
        return pooledConnection;
    }
    
    @Override
    public synchronized boolean releasePooledConnection(final PooledConnection pooledConnection) {
        final ConnectionDesc connectionDesc;
        final int index = this.conns.indexOf(connectionDesc = new ConnectionDesc(pooledConnection));
        this.d("release(): ", pooledConnection);
        if (index >= 0) {
            if (this.closed || (this.prefSize > 0 && this.conns.size() > this.prefSize)) {
                this.d("release(): closing ", pooledConnection);
                this.td("Close ", pooledConnection);
                this.conns.remove(connectionDesc);
                pooledConnection.closeConnection();
            }
            else {
                this.d("release(): release ", pooledConnection);
                this.td("Release ", pooledConnection);
                this.conns.get(index).release();
            }
            this.notifyAll();
            this.d("release(): notify");
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized boolean removePooledConnection(final PooledConnection pooledConnection) {
        if (this.conns.remove(new ConnectionDesc(pooledConnection))) {
            this.d("remove(): ", pooledConnection);
            this.notifyAll();
            this.d("remove(): notify");
            this.td("Remove ", pooledConnection);
            if (this.conns.isEmpty()) {
                this.ref = null;
            }
            return true;
        }
        this.d("remove(): not found ", pooledConnection);
        return false;
    }
    
    boolean expire(final long n) {
        final ArrayList list;
        synchronized (this) {
            list = new ArrayList((Collection<? extends E>)this.conns);
        }
        final ArrayList list2 = new ArrayList();
        for (final ConnectionDesc connectionDesc : list) {
            this.d("expire(): ", connectionDesc);
            if (connectionDesc.expire(n)) {
                list2.add(connectionDesc);
                this.td("expire(): Expired ", connectionDesc);
            }
        }
        synchronized (this) {
            this.conns.removeAll(list2);
            return this.conns.isEmpty();
        }
    }
    
    synchronized void close() {
        this.expire(System.currentTimeMillis());
        this.closed = true;
    }
    
    String getStats() {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        long n4 = 0L;
        final int size;
        synchronized (this) {
            size = this.conns.size();
            for (int i = 0; i < size; ++i) {
                final ConnectionDesc connectionDesc = this.conns.get(i);
                n4 += connectionDesc.getUseCount();
                switch (connectionDesc.getState()) {
                    case 0: {
                        ++n2;
                        break;
                    }
                    case 1: {
                        ++n;
                        break;
                    }
                    case 2: {
                        ++n3;
                        break;
                    }
                }
            }
        }
        return "size=" + size + "; use=" + n4 + "; busy=" + n2 + "; idle=" + n + "; expired=" + n3;
    }
    
    private void d(final String s, final Object o) {
        if (Connections.debug) {
            this.d(s + o);
        }
    }
    
    private void d(final String s, final int n) {
        if (Connections.debug) {
            this.d(s + n);
        }
    }
    
    private void d(final String s) {
        if (Connections.debug) {
            System.err.println(this + "." + s + "; size: " + this.conns.size());
        }
    }
    
    private void td(final String s, final Object o, final Object o2) {
        if (Connections.trace) {
            this.td(s + o + "[" + o2 + "]");
        }
    }
    
    private void td(final String s, final Object o) {
        if (Connections.trace) {
            this.td(s + o);
        }
    }
    
    private void td(final String s) {
        if (Connections.trace) {
            System.err.println(s);
        }
    }
    
    static {
        debug = Pool.debug;
        trace = LdapPoolManager.trace;
    }
}
