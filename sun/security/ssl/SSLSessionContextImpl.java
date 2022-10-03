package sun.security.ssl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
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
    private final Cache<SessionId, SSLSessionImpl> sessionCache;
    private final Cache<String, SSLSessionImpl> sessionHostPortCache;
    private int cacheLimit;
    private int timeout;
    
    SSLSessionContextImpl() {
        this.cacheLimit = getDefaultCacheLimit();
        this.timeout = 86400;
        this.sessionCache = Cache.newSoftMemoryCache(this.cacheLimit, this.timeout);
        this.sessionHostPortCache = Cache.newSoftMemoryCache(this.cacheLimit, this.timeout);
    }
    
    @Override
    public SSLSession getSession(final byte[] array) {
        if (array == null) {
            throw new NullPointerException("session id cannot be null");
        }
        final SSLSessionImpl sslSessionImpl = this.sessionCache.get(new SessionId(array));
        if (!this.isTimedout(sslSessionImpl)) {
            return sslSessionImpl;
        }
        return null;
    }
    
    @Override
    public Enumeration<byte[]> getIds() {
        final SessionCacheVisitor sessionCacheVisitor = new SessionCacheVisitor();
        this.sessionCache.accept(sessionCacheVisitor);
        return sessionCacheVisitor.getSessionIds();
    }
    
    @Override
    public void setSessionTimeout(final int timeout) throws IllegalArgumentException {
        if (timeout < 0) {
            throw new IllegalArgumentException();
        }
        if (this.timeout != timeout) {
            this.sessionCache.setTimeout(timeout);
            this.sessionHostPortCache.setTimeout(timeout);
            this.timeout = timeout;
        }
    }
    
    @Override
    public int getSessionTimeout() {
        return this.timeout;
    }
    
    @Override
    public void setSessionCacheSize(final int cacheLimit) throws IllegalArgumentException {
        if (cacheLimit < 0) {
            throw new IllegalArgumentException();
        }
        if (this.cacheLimit != cacheLimit) {
            this.sessionCache.setCapacity(cacheLimit);
            this.sessionHostPortCache.setCapacity(cacheLimit);
            this.cacheLimit = cacheLimit;
        }
    }
    
    @Override
    public int getSessionCacheSize() {
        return this.cacheLimit;
    }
    
    SSLSessionImpl get(final byte[] array) {
        return (SSLSessionImpl)this.getSession(array);
    }
    
    SSLSessionImpl get(final String s, final int n) {
        if (s == null && n == -1) {
            return null;
        }
        final SSLSessionImpl sslSessionImpl = this.sessionHostPortCache.get(getKey(s, n));
        if (!this.isTimedout(sslSessionImpl)) {
            return sslSessionImpl;
        }
        return null;
    }
    
    private static String getKey(final String s, final int n) {
        return (s + ":" + String.valueOf(n)).toLowerCase(Locale.ENGLISH);
    }
    
    void put(final SSLSessionImpl sslSessionImpl) {
        this.sessionCache.put(sslSessionImpl.getSessionId(), sslSessionImpl);
        if (sslSessionImpl.getPeerHost() != null && sslSessionImpl.getPeerPort() != -1) {
            this.sessionHostPortCache.put(getKey(sslSessionImpl.getPeerHost(), sslSessionImpl.getPeerPort()), sslSessionImpl);
        }
        sslSessionImpl.setContext(this);
    }
    
    void remove(final SessionId sessionId) {
        final SSLSessionImpl sslSessionImpl = this.sessionCache.get(sessionId);
        if (sslSessionImpl != null) {
            this.sessionCache.remove(sessionId);
            this.sessionHostPortCache.remove(getKey(sslSessionImpl.getPeerHost(), sslSessionImpl.getPeerPort()));
        }
    }
    
    private static int getDefaultCacheLimit() {
        try {
            final int intValue = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("javax.net.ssl.sessionCacheSize", 20480));
            if (intValue >= 0) {
                return intValue;
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("invalid System Property javax.net.ssl.sessionCacheSize, use the default session cache size (20480) instead", new Object[0]);
            }
        }
        catch (final Exception ex) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("the System Property javax.net.ssl.sessionCacheSize is not available, use the default value (20480) instead", new Object[0]);
            }
        }
        return 20480;
    }
    
    private boolean isTimedout(final SSLSession sslSession) {
        if (this.timeout == 0) {
            return false;
        }
        if (sslSession != null && sslSession.getCreationTime() + this.timeout * 1000L <= System.currentTimeMillis()) {
            sslSession.invalidate();
            return true;
        }
        return false;
    }
    
    private final class SessionCacheVisitor implements Cache.CacheVisitor<SessionId, SSLSessionImpl>
    {
        ArrayList<byte[]> ids;
        
        private SessionCacheVisitor() {
            this.ids = null;
        }
        
        @Override
        public void visit(final Map<SessionId, SSLSessionImpl> map) {
            this.ids = new ArrayList<byte[]>(map.size());
            for (final SessionId sessionId : map.keySet()) {
                if (!SSLSessionContextImpl.this.isTimedout(map.get(sessionId))) {
                    this.ids.add(sessionId.getId());
                }
            }
        }
        
        Enumeration<byte[]> getSessionIds() {
            return (this.ids != null) ? Collections.enumeration(this.ids) : Collections.emptyEnumeration();
        }
    }
}
