package io.netty.handler.ssl;

import io.netty.util.ResourceLeakDetectorFactory;
import javax.net.ssl.SSLSessionContext;
import java.security.Principal;
import javax.security.cert.X509Certificate;
import io.netty.util.internal.EmptyArrays;
import java.security.cert.Certificate;
import io.netty.internal.tcnative.SSLSession;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import io.netty.internal.tcnative.SSLSessionCache;

class OpenSslSessionCache implements SSLSessionCache
{
    private static final OpenSslSession[] EMPTY_SESSIONS;
    private static final int DEFAULT_CACHE_SIZE;
    private final OpenSslEngineMap engineMap;
    private final Map<OpenSslSessionId, NativeSslSession> sessions;
    private final AtomicInteger maximumCacheSize;
    private final AtomicInteger sessionTimeout;
    private int sessionCounter;
    
    OpenSslSessionCache(final OpenSslEngineMap engineMap) {
        this.sessions = new LinkedHashMap<OpenSslSessionId, NativeSslSession>() {
            private static final long serialVersionUID = -7773696788135734448L;
            
            @Override
            protected boolean removeEldestEntry(final Map.Entry<OpenSslSessionId, NativeSslSession> eldest) {
                final int maxSize = OpenSslSessionCache.this.maximumCacheSize.get();
                if (maxSize >= 0 && this.size() > maxSize) {
                    OpenSslSessionCache.this.removeSessionWithId(eldest.getKey());
                }
                return false;
            }
        };
        this.maximumCacheSize = new AtomicInteger(OpenSslSessionCache.DEFAULT_CACHE_SIZE);
        this.sessionTimeout = new AtomicInteger(300);
        this.engineMap = engineMap;
    }
    
    final void setSessionTimeout(final int seconds) {
        final int oldTimeout = this.sessionTimeout.getAndSet(seconds);
        if (oldTimeout > seconds) {
            this.clear();
        }
    }
    
    final int getSessionTimeout() {
        return this.sessionTimeout.get();
    }
    
    protected boolean sessionCreated(final NativeSslSession session) {
        return true;
    }
    
    protected void sessionRemoved(final NativeSslSession session) {
    }
    
    final void setSessionCacheSize(final int size) {
        final long oldSize = this.maximumCacheSize.getAndSet(size);
        if (oldSize > size || size == 0) {
            this.clear();
        }
    }
    
    final int getSessionCacheSize() {
        return this.maximumCacheSize.get();
    }
    
    private void expungeInvalidSessions() {
        if (this.sessions.isEmpty()) {
            return;
        }
        final long now = System.currentTimeMillis();
        final Iterator<Map.Entry<OpenSslSessionId, NativeSslSession>> iterator = this.sessions.entrySet().iterator();
        while (iterator.hasNext()) {
            final NativeSslSession session = (NativeSslSession)iterator.next().getValue();
            if (session.isValid(now)) {
                break;
            }
            iterator.remove();
            this.notifyRemovalAndFree(session);
        }
    }
    
    public final boolean sessionCreated(final long ssl, final long sslSession) {
        final ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
        if (engine == null) {
            return false;
        }
        final NativeSslSession session = new NativeSslSession(sslSession, engine.getPeerHost(), engine.getPeerPort(), this.getSessionTimeout() * 1000L);
        engine.setSessionId(session.sessionId());
        synchronized (this) {
            if (++this.sessionCounter == 255) {
                this.sessionCounter = 0;
                this.expungeInvalidSessions();
            }
            if (!this.sessionCreated(session)) {
                session.close();
                return false;
            }
            final NativeSslSession old = this.sessions.put(session.sessionId(), session);
            if (old != null) {
                this.notifyRemovalAndFree(old);
            }
        }
        return true;
    }
    
    public final long getSession(final long ssl, final byte[] sessionId) {
        final OpenSslSessionId id = new OpenSslSessionId(sessionId);
        final NativeSslSession session;
        synchronized (this) {
            session = this.sessions.get(id);
            if (session == null) {
                return -1L;
            }
            if (!session.isValid() || !session.upRef()) {
                this.removeSessionWithId(session.sessionId());
                return -1L;
            }
            if (session.shouldBeSingleUse()) {
                this.removeSessionWithId(session.sessionId());
            }
        }
        session.updateLastAccessedTime();
        return session.session();
    }
    
    void setSession(final long ssl, final String host, final int port) {
    }
    
    final synchronized void removeSessionWithId(final OpenSslSessionId id) {
        final NativeSslSession sslSession = this.sessions.remove(id);
        if (sslSession != null) {
            this.notifyRemovalAndFree(sslSession);
        }
    }
    
    final synchronized boolean containsSessionWithId(final OpenSslSessionId id) {
        return this.sessions.containsKey(id);
    }
    
    private void notifyRemovalAndFree(final NativeSslSession session) {
        this.sessionRemoved(session);
        session.free();
    }
    
    final synchronized OpenSslSession getSession(final OpenSslSessionId id) {
        final NativeSslSession session = this.sessions.get(id);
        if (session != null && !session.isValid()) {
            this.removeSessionWithId(session.sessionId());
            return null;
        }
        return session;
    }
    
    final List<OpenSslSessionId> getIds() {
        final OpenSslSession[] sessionsArray;
        synchronized (this) {
            sessionsArray = this.sessions.values().toArray(OpenSslSessionCache.EMPTY_SESSIONS);
        }
        final List<OpenSslSessionId> ids = new ArrayList<OpenSslSessionId>(sessionsArray.length);
        for (final OpenSslSession session : sessionsArray) {
            if (session.isValid()) {
                ids.add(session.sessionId());
            }
        }
        return ids;
    }
    
    synchronized void clear() {
        final Iterator<Map.Entry<OpenSslSessionId, NativeSslSession>> iterator = this.sessions.entrySet().iterator();
        while (iterator.hasNext()) {
            final NativeSslSession session = (NativeSslSession)iterator.next().getValue();
            iterator.remove();
            this.notifyRemovalAndFree(session);
        }
    }
    
    static {
        EMPTY_SESSIONS = new OpenSslSession[0];
        final int cacheSize = SystemPropertyUtil.getInt("javax.net.ssl.sessionCacheSize", 20480);
        if (cacheSize >= 0) {
            DEFAULT_CACHE_SIZE = cacheSize;
        }
        else {
            DEFAULT_CACHE_SIZE = 20480;
        }
    }
    
    static final class NativeSslSession implements OpenSslSession
    {
        static final ResourceLeakDetector<NativeSslSession> LEAK_DETECTOR;
        private final ResourceLeakTracker<NativeSslSession> leakTracker;
        private final long session;
        private final String peerHost;
        private final int peerPort;
        private final OpenSslSessionId id;
        private final long timeout;
        private final long creationTime;
        private volatile long lastAccessedTime;
        private volatile boolean valid;
        private boolean freed;
        
        NativeSslSession(final long session, final String peerHost, final int peerPort, final long timeout) {
            this.creationTime = System.currentTimeMillis();
            this.lastAccessedTime = this.creationTime;
            this.valid = true;
            this.session = session;
            this.peerHost = peerHost;
            this.peerPort = peerPort;
            this.timeout = timeout;
            this.id = new OpenSslSessionId(io.netty.internal.tcnative.SSLSession.getSessionId(session));
            this.leakTracker = NativeSslSession.LEAK_DETECTOR.track(this);
        }
        
        @Override
        public void setSessionId(final OpenSslSessionId id) {
            throw new UnsupportedOperationException();
        }
        
        boolean shouldBeSingleUse() {
            assert !this.freed;
            return io.netty.internal.tcnative.SSLSession.shouldBeSingleUse(this.session);
        }
        
        long session() {
            assert !this.freed;
            return this.session;
        }
        
        boolean upRef() {
            assert !this.freed;
            return io.netty.internal.tcnative.SSLSession.upRef(this.session);
        }
        
        synchronized void free() {
            this.close();
            io.netty.internal.tcnative.SSLSession.free(this.session);
        }
        
        void close() {
            assert !this.freed;
            this.freed = true;
            this.invalidate();
            if (this.leakTracker != null) {
                this.leakTracker.close(this);
            }
        }
        
        @Override
        public OpenSslSessionId sessionId() {
            return this.id;
        }
        
        boolean isValid(final long now) {
            return this.creationTime + this.timeout >= now && this.valid;
        }
        
        @Override
        public void setLocalCertificate(final Certificate[] localCertificate) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public OpenSslSessionContext getSessionContext() {
            return null;
        }
        
        @Override
        public void tryExpandApplicationBufferSize(final int packetLengthDataOnly) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void handshakeFinished(final byte[] id, final String cipher, final String protocol, final byte[] peerCertificate, final byte[][] peerCertificateChain, final long creationTime, final long timeout) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public byte[] getId() {
            return this.id.cloneBytes();
        }
        
        @Override
        public long getCreationTime() {
            return this.creationTime;
        }
        
        void updateLastAccessedTime() {
            this.lastAccessedTime = System.currentTimeMillis();
        }
        
        @Override
        public long getLastAccessedTime() {
            return this.lastAccessedTime;
        }
        
        @Override
        public void invalidate() {
            this.valid = false;
        }
        
        @Override
        public boolean isValid() {
            return this.isValid(System.currentTimeMillis());
        }
        
        @Override
        public void putValue(final String name, final Object value) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Object getValue(final String name) {
            return null;
        }
        
        @Override
        public void removeValue(final String name) {
        }
        
        @Override
        public String[] getValueNames() {
            return EmptyArrays.EMPTY_STRINGS;
        }
        
        @Override
        public Certificate[] getPeerCertificates() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Certificate[] getLocalCertificates() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public X509Certificate[] getPeerCertificateChain() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Principal getPeerPrincipal() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Principal getLocalPrincipal() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String getCipherSuite() {
            return null;
        }
        
        @Override
        public String getProtocol() {
            return null;
        }
        
        @Override
        public String getPeerHost() {
            return this.peerHost;
        }
        
        @Override
        public int getPeerPort() {
            return this.peerPort;
        }
        
        @Override
        public int getPacketBufferSize() {
            return ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE;
        }
        
        @Override
        public int getApplicationBufferSize() {
            return ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
        }
        
        @Override
        public int hashCode() {
            return this.id.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof OpenSslSession)) {
                return false;
            }
            final OpenSslSession session1 = (OpenSslSession)o;
            return this.id.equals(session1.sessionId());
        }
        
        static {
            LEAK_DETECTOR = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(NativeSslSession.class);
        }
    }
}
