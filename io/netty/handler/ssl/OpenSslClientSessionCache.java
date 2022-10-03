package io.netty.handler.ssl;

import io.netty.util.AsciiString;
import io.netty.internal.tcnative.SSL;
import java.util.HashMap;
import java.util.Map;

final class OpenSslClientSessionCache extends OpenSslSessionCache
{
    private final Map<HostPort, NativeSslSession> sessions;
    
    OpenSslClientSessionCache(final OpenSslEngineMap engineMap) {
        super(engineMap);
        this.sessions = new HashMap<HostPort, NativeSslSession>();
    }
    
    @Override
    protected boolean sessionCreated(final NativeSslSession session) {
        assert Thread.holdsLock(this);
        final HostPort hostPort = keyFor(session.getPeerHost(), session.getPeerPort());
        if (hostPort == null || this.sessions.containsKey(hostPort)) {
            return false;
        }
        this.sessions.put(hostPort, session);
        return true;
    }
    
    @Override
    protected void sessionRemoved(final NativeSslSession session) {
        assert Thread.holdsLock(this);
        final HostPort hostPort = keyFor(session.getPeerHost(), session.getPeerPort());
        if (hostPort == null) {
            return;
        }
        this.sessions.remove(hostPort);
    }
    
    @Override
    void setSession(final long ssl, final String host, final int port) {
        final HostPort hostPort = keyFor(host, port);
        if (hostPort == null) {
            return;
        }
        final NativeSslSession session;
        final boolean reused;
        synchronized (this) {
            session = this.sessions.get(hostPort);
            if (session == null) {
                return;
            }
            if (!session.isValid()) {
                this.removeSessionWithId(session.sessionId());
                return;
            }
            reused = SSL.setSession(ssl, session.session());
        }
        if (reused) {
            if (session.shouldBeSingleUse()) {
                session.invalidate();
            }
            session.updateLastAccessedTime();
        }
    }
    
    private static HostPort keyFor(final String host, final int port) {
        if (host == null && port < 1) {
            return null;
        }
        return new HostPort(host, port);
    }
    
    @Override
    synchronized void clear() {
        super.clear();
        this.sessions.clear();
    }
    
    private static final class HostPort
    {
        private final int hash;
        private final String host;
        private final int port;
        
        HostPort(final String host, final int port) {
            this.host = host;
            this.port = port;
            this.hash = 31 * AsciiString.hashCode(host) + port;
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof HostPort)) {
                return false;
            }
            final HostPort other = (HostPort)obj;
            return this.port == other.port && this.host.equalsIgnoreCase(other.host);
        }
        
        @Override
        public String toString() {
            return "HostPort{host='" + this.host + '\'' + ", port=" + this.port + '}';
        }
    }
}
