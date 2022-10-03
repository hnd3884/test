package io.netty.handler.ssl;

import javax.net.ssl.SSLSessionContext;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import io.netty.buffer.ByteBufAllocator;
import java.util.List;
import io.netty.util.internal.ObjectUtil;

public abstract class DelegatingSslContext extends SslContext
{
    private final SslContext ctx;
    
    protected DelegatingSslContext(final SslContext ctx) {
        this.ctx = ObjectUtil.checkNotNull(ctx, "ctx");
    }
    
    @Override
    public final boolean isClient() {
        return this.ctx.isClient();
    }
    
    @Override
    public final List<String> cipherSuites() {
        return this.ctx.cipherSuites();
    }
    
    @Override
    public final long sessionCacheSize() {
        return this.ctx.sessionCacheSize();
    }
    
    @Override
    public final long sessionTimeout() {
        return this.ctx.sessionTimeout();
    }
    
    @Override
    public final ApplicationProtocolNegotiator applicationProtocolNegotiator() {
        return this.ctx.applicationProtocolNegotiator();
    }
    
    @Override
    public final SSLEngine newEngine(final ByteBufAllocator alloc) {
        final SSLEngine engine = this.ctx.newEngine(alloc);
        this.initEngine(engine);
        return engine;
    }
    
    @Override
    public final SSLEngine newEngine(final ByteBufAllocator alloc, final String peerHost, final int peerPort) {
        final SSLEngine engine = this.ctx.newEngine(alloc, peerHost, peerPort);
        this.initEngine(engine);
        return engine;
    }
    
    @Override
    protected final SslHandler newHandler(final ByteBufAllocator alloc, final boolean startTls) {
        final SslHandler handler = this.ctx.newHandler(alloc, startTls);
        this.initHandler(handler);
        return handler;
    }
    
    @Override
    protected final SslHandler newHandler(final ByteBufAllocator alloc, final String peerHost, final int peerPort, final boolean startTls) {
        final SslHandler handler = this.ctx.newHandler(alloc, peerHost, peerPort, startTls);
        this.initHandler(handler);
        return handler;
    }
    
    @Override
    protected SslHandler newHandler(final ByteBufAllocator alloc, final boolean startTls, final Executor executor) {
        final SslHandler handler = this.ctx.newHandler(alloc, startTls, executor);
        this.initHandler(handler);
        return handler;
    }
    
    @Override
    protected SslHandler newHandler(final ByteBufAllocator alloc, final String peerHost, final int peerPort, final boolean startTls, final Executor executor) {
        final SslHandler handler = this.ctx.newHandler(alloc, peerHost, peerPort, startTls, executor);
        this.initHandler(handler);
        return handler;
    }
    
    @Override
    public final SSLSessionContext sessionContext() {
        return this.ctx.sessionContext();
    }
    
    protected abstract void initEngine(final SSLEngine p0);
    
    protected void initHandler(final SslHandler handler) {
        this.initEngine(handler.engine());
    }
}
