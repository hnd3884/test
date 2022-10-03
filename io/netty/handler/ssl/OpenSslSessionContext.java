package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import java.util.Arrays;
import io.netty.internal.tcnative.SessionTicketKey;
import java.util.Iterator;
import java.util.Enumeration;
import javax.net.ssl.SSLSession;
import java.util.concurrent.locks.Lock;
import io.netty.util.internal.ObjectUtil;
import io.netty.internal.tcnative.SSLSessionCache;
import io.netty.internal.tcnative.SSLContext;
import javax.net.ssl.SSLSessionContext;

public abstract class OpenSslSessionContext implements SSLSessionContext
{
    private final OpenSslSessionStats stats;
    private final OpenSslKeyMaterialProvider provider;
    final ReferenceCountedOpenSslContext context;
    private final OpenSslSessionCache sessionCache;
    private final long mask;
    
    OpenSslSessionContext(final ReferenceCountedOpenSslContext context, final OpenSslKeyMaterialProvider provider, final long mask, final OpenSslSessionCache cache) {
        this.context = context;
        this.provider = provider;
        this.mask = mask;
        this.stats = new OpenSslSessionStats(context);
        this.sessionCache = cache;
        SSLContext.setSSLSessionCache(context.ctx, (SSLSessionCache)cache);
    }
    
    final boolean useKeyManager() {
        return this.provider != null;
    }
    
    @Override
    public void setSessionCacheSize(final int size) {
        ObjectUtil.checkPositiveOrZero(size, "size");
        this.sessionCache.setSessionCacheSize(size);
    }
    
    @Override
    public int getSessionCacheSize() {
        return this.sessionCache.getSessionCacheSize();
    }
    
    @Override
    public void setSessionTimeout(final int seconds) {
        ObjectUtil.checkPositiveOrZero(seconds, "seconds");
        final Lock writerLock = this.context.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.setSessionCacheTimeout(this.context.ctx, (long)seconds);
            this.sessionCache.setSessionTimeout(seconds);
        }
        finally {
            writerLock.unlock();
        }
    }
    
    @Override
    public int getSessionTimeout() {
        return this.sessionCache.getSessionTimeout();
    }
    
    @Override
    public SSLSession getSession(final byte[] bytes) {
        return this.sessionCache.getSession(new OpenSslSessionId(bytes));
    }
    
    @Override
    public Enumeration<byte[]> getIds() {
        return new Enumeration<byte[]>() {
            private final Iterator<OpenSslSessionId> ids = OpenSslSessionContext.this.sessionCache.getIds().iterator();
            
            @Override
            public boolean hasMoreElements() {
                return this.ids.hasNext();
            }
            
            @Override
            public byte[] nextElement() {
                return this.ids.next().cloneBytes();
            }
        };
    }
    
    @Deprecated
    public void setTicketKeys(final byte[] keys) {
        if (keys.length % 48 != 0) {
            throw new IllegalArgumentException("keys.length % 48 != 0");
        }
        final SessionTicketKey[] tickets = new SessionTicketKey[keys.length / 48];
        byte[] name;
        byte[] hmacKey;
        byte[] aesKey;
        for (int i = 0, a = 0; i < tickets.length; i += 16, aesKey = Arrays.copyOfRange(keys, a, 16), a += 16, tickets[i] = new SessionTicketKey(name, hmacKey, aesKey), ++i) {
            name = Arrays.copyOfRange(keys, a, 16);
            a += 16;
            hmacKey = Arrays.copyOfRange(keys, a, 16);
        }
        final Lock writerLock = this.context.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.clearOptions(this.context.ctx, SSL.SSL_OP_NO_TICKET);
            SSLContext.setSessionTicketKeys(this.context.ctx, tickets);
        }
        finally {
            writerLock.unlock();
        }
    }
    
    public void setTicketKeys(final OpenSslSessionTicketKey... keys) {
        ObjectUtil.checkNotNull(keys, "keys");
        final SessionTicketKey[] ticketKeys = new SessionTicketKey[keys.length];
        for (int i = 0; i < ticketKeys.length; ++i) {
            ticketKeys[i] = keys[i].key;
        }
        final Lock writerLock = this.context.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.clearOptions(this.context.ctx, SSL.SSL_OP_NO_TICKET);
            if (ticketKeys.length > 0) {
                SSLContext.setSessionTicketKeys(this.context.ctx, ticketKeys);
            }
        }
        finally {
            writerLock.unlock();
        }
    }
    
    public void setSessionCacheEnabled(final boolean enabled) {
        final long mode = enabled ? (this.mask | SSL.SSL_SESS_CACHE_NO_INTERNAL_LOOKUP | SSL.SSL_SESS_CACHE_NO_INTERNAL_STORE) : SSL.SSL_SESS_CACHE_OFF;
        final Lock writerLock = this.context.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.setSessionCacheMode(this.context.ctx, mode);
            if (!enabled) {
                this.sessionCache.clear();
            }
        }
        finally {
            writerLock.unlock();
        }
    }
    
    public boolean isSessionCacheEnabled() {
        final Lock readerLock = this.context.ctxLock.readLock();
        readerLock.lock();
        try {
            return (SSLContext.getSessionCacheMode(this.context.ctx) & this.mask) != 0x0L;
        }
        finally {
            readerLock.unlock();
        }
    }
    
    public OpenSslSessionStats stats() {
        return this.stats;
    }
    
    final void removeFromCache(final OpenSslSessionId id) {
        this.sessionCache.removeSessionWithId(id);
    }
    
    final boolean isInCache(final OpenSslSessionId id) {
        return this.sessionCache.containsSessionWithId(id);
    }
    
    void setSessionFromCache(final String host, final int port, final long ssl) {
        this.sessionCache.setSession(ssl, host, port);
    }
    
    final void destroy() {
        if (this.provider != null) {
            this.provider.destroy();
        }
        this.sessionCache.clear();
    }
}
