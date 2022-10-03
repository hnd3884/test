package io.netty.channel.local;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.net.SocketAddress;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.EventLoop;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.PreferHeapByteBufAllocator;
import java.util.ArrayDeque;
import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelConfig;
import java.util.Queue;
import io.netty.channel.ChannelConfig;
import io.netty.channel.AbstractServerChannel;

public class LocalServerChannel extends AbstractServerChannel
{
    private final ChannelConfig config;
    private final Queue<Object> inboundBuffer;
    private final Runnable shutdownHook;
    private volatile int state;
    private volatile LocalAddress localAddress;
    private volatile boolean acceptInProgress;
    
    public LocalServerChannel() {
        this.config = new DefaultChannelConfig(this);
        this.inboundBuffer = new ArrayDeque<Object>();
        this.shutdownHook = new Runnable() {
            @Override
            public void run() {
                LocalServerChannel.this.unsafe().close(LocalServerChannel.this.unsafe().voidPromise());
            }
        };
        this.config().setAllocator(new PreferHeapByteBufAllocator(this.config.getAllocator()));
    }
    
    @Override
    public ChannelConfig config() {
        return this.config;
    }
    
    @Override
    public LocalAddress localAddress() {
        return (LocalAddress)super.localAddress();
    }
    
    @Override
    public LocalAddress remoteAddress() {
        return (LocalAddress)super.remoteAddress();
    }
    
    @Override
    public boolean isOpen() {
        return this.state < 2;
    }
    
    @Override
    public boolean isActive() {
        return this.state == 1;
    }
    
    @Override
    protected boolean isCompatible(final EventLoop loop) {
        return loop instanceof SingleThreadEventLoop;
    }
    
    @Override
    protected SocketAddress localAddress0() {
        return this.localAddress;
    }
    
    @Override
    protected void doRegister() throws Exception {
        ((SingleThreadEventExecutor)this.eventLoop()).addShutdownHook(this.shutdownHook);
    }
    
    @Override
    protected void doBind(final SocketAddress localAddress) throws Exception {
        this.localAddress = LocalChannelRegistry.register(this, this.localAddress, localAddress);
        this.state = 1;
    }
    
    @Override
    protected void doClose() throws Exception {
        if (this.state <= 1) {
            if (this.localAddress != null) {
                LocalChannelRegistry.unregister(this.localAddress);
                this.localAddress = null;
            }
            this.state = 2;
        }
    }
    
    @Override
    protected void doDeregister() throws Exception {
        ((SingleThreadEventExecutor)this.eventLoop()).removeShutdownHook(this.shutdownHook);
    }
    
    @Override
    protected void doBeginRead() throws Exception {
        if (this.acceptInProgress) {
            return;
        }
        final Queue<Object> inboundBuffer = this.inboundBuffer;
        if (inboundBuffer.isEmpty()) {
            this.acceptInProgress = true;
            return;
        }
        this.readInbound();
    }
    
    LocalChannel serve(final LocalChannel peer) {
        final LocalChannel child = this.newLocalChannel(peer);
        if (this.eventLoop().inEventLoop()) {
            this.serve0(child);
        }
        else {
            this.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    LocalServerChannel.this.serve0(child);
                }
            });
        }
        return child;
    }
    
    private void readInbound() {
        final RecvByteBufAllocator.Handle handle = this.unsafe().recvBufAllocHandle();
        handle.reset(this.config());
        final ChannelPipeline pipeline = this.pipeline();
        do {
            final Object m = this.inboundBuffer.poll();
            if (m == null) {
                break;
            }
            pipeline.fireChannelRead(m);
        } while (handle.continueReading());
        pipeline.fireChannelReadComplete();
    }
    
    protected LocalChannel newLocalChannel(final LocalChannel peer) {
        return new LocalChannel(this, peer);
    }
    
    private void serve0(final LocalChannel child) {
        this.inboundBuffer.add(child);
        if (this.acceptInProgress) {
            this.acceptInProgress = false;
            this.readInbound();
        }
    }
}
