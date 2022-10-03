package io.netty.handler.address;

import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;

public abstract class DynamicAddressConnectHandler extends ChannelOutboundHandlerAdapter
{
    @Override
    public final void connect(final ChannelHandlerContext ctx, final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
        SocketAddress remote;
        SocketAddress local;
        try {
            remote = this.remoteAddress(remoteAddress, localAddress);
            local = this.localAddress(remoteAddress, localAddress);
        }
        catch (final Exception e) {
            promise.setFailure((Throwable)e);
            return;
        }
        ctx.connect(remote, local, promise).addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) {
                if (future.isSuccess()) {
                    future.channel().pipeline().remove(DynamicAddressConnectHandler.this);
                }
            }
        });
    }
    
    protected SocketAddress localAddress(final SocketAddress remoteAddress, final SocketAddress localAddress) throws Exception {
        return localAddress;
    }
    
    protected SocketAddress remoteAddress(final SocketAddress remoteAddress, final SocketAddress localAddress) throws Exception {
        return remoteAddress;
    }
}
