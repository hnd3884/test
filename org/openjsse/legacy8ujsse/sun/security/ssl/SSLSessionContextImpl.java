package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import java.util.Locale;
import java.util.Enumeration;
import javax.net.ssl.SSLSession;
import sun.security.util.Cache;
import javax.net.ssl.SSLSessionContext;

final class SSLSessionContextImpl implements SSLSessionContext
{
    private static final int DEFAULT_MAX_CACHE_SIZE = 20480;
    private Cache<SessionId, SSLSessionImpl> sessionCache;
    private Cache<String, SSLSessionImpl> sessionHostPortCache;
    private int cacheLimit;
    private int timeout;
    
    SSLSessionContextImpl() {
        this.cacheLimit = this.getDefaultCacheLimit();
        this.timeout = 86400;
        this.sessionCache = Cache.newSoftMemoryCache(this.cacheLimit, this.timeout);
        this.sessionHostPortCache = Cache.newSoftMemoryCache(this.cacheLimit, this.timeout);
    }
    
    @Override
    public SSLSession getSession(final byte[] sessionId) {
        if (sessionId == null) {
            throw new NullPointerException("session id cannot be null");
        }
        final SSLSessionImpl sess = this.sessionCache.get(new SessionId(sessionId));
        if (!this.isTimedout(sess)) {
            return sess;
        }
        return null;
    }
    
    @Override
    public Enumeration<byte[]> getIds() {
        final SessionCacheVisitor scVisitor = new SessionCacheVisitor();
        this.sessionCache.accept(scVisitor);
        return scVisitor.getSessionIds();
    }
    
    @Override
    public void setSessionTimeout(final int seconds) throws IllegalArgumentException {
        if (seconds < 0) {
            throw new IllegalArgumentException();
        }
        if (this.timeout != seconds) {
            this.sessionCache.setTimeout(seconds);
            this.sessionHostPortCache.setTimeout(seconds);
            this.timeout = seconds;
        }
    }
    
    @Override
    public int getSessionTimeout() {
        return this.timeout;
    }
    
    @Override
    public void setSessionCacheSize(final int size) throws IllegalArgumentException {
        if (size < 0) {
            throw new IllegalArgumentException();
        }
        if (this.cacheLimit != size) {
            this.sessionCache.setCapacity(size);
            this.sessionHostPortCache.setCapacity(size);
            this.cacheLimit = size;
        }
    }
    
    @Override
    public int getSessionCacheSize() {
        return this.cacheLimit;
    }
    
    SSLSessionImpl get(final byte[] id) {
        return (SSLSessionImpl)this.getSession(id);
    }
    
    SSLSessionImpl get(final String hostname, final int port) {
        if (hostname == null && port == -1) {
            return null;
        }
        final SSLSessionImpl sess = this.sessionHostPortCache.get(this.getKey(hostname, port));
        if (!this.isTimedout(sess)) {
            return sess;
        }
        return null;
    }
    
    private String getKey(final String hostname, final int port) {
        return (hostname + ":" + String.valueOf(port)).toLowerCase(Locale.ENGLISH);
    }
    
    void put(final SSLSessionImpl s) {
        this.sessionCache.put(s.getSessionId(), s);
        if (s.getPeerHost() != null && s.getPeerPort() != -1) {
            this.sessionHostPortCache.put(this.getKey(s.getPeerHost(), s.getPeerPort()), s);
        }
        s.setContext(this);
    }
    
    void remove(final SessionId key) {
        final SSLSessionImpl s = this.sessionCache.get(key);
        if (s != null) {
            this.sessionCache.remove(key);
            this.sessionHostPortCache.remove(this.getKey(s.getPeerHost(), s.getPeerPort()));
        }
    }
    
    private int getDefaultCacheLimit() {
        try {
            final int defaultCacheLimit = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("javax.net.ssl.sessionCacheSize", 20480));
            if (defaultCacheLimit >= 0) {
                return defaultCacheLimit;
            }
        }
        catch (final Exception ex) {}
        return 20480;
    }
    
    boolean isTimedout(final SSLSession sess) {
        if (this.timeout == 0) {
            return false;
        }
        if (sess != null && sess.getCreationTime() + this.timeout * 1000L <= System.currentTimeMillis()) {
            sess.invalidate();
            return true;
        }
        return false;
    }
    
    final class SessionCacheVisitor implements Cache.CacheVisitor<SessionId, SSLSessionImpl>
    {
        Vector<byte[]> ids;
        
        SessionCacheVisitor() {
            this.ids = null;
        }
        
        @Override
        public void visit(final Map<SessionId, SSLSessionImpl> map) {
            this.ids = new Vector<byte[]>(map.size());
            for (final SessionId key : map.keySet()) {
                final SSLSessionImpl value = map.get(key);
                if (!SSLSessionContextImpl.this.isTimedout(value)) {
                    this.ids.addElement(key.getId());
                }
            }
        }
        
        public Enumeration<byte[]> getSessionIds() {
            return (this.ids != null) ? this.ids.elements() : new Vector<byte[]>().elements();
        }
    }
}
