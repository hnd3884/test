package io.netty.channel;

import java.net.SocketAddress;

public class ChannelOutboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelOutboundHandler
{
    @ChannelHandlerMask.Skip
    @Override
    public void bind(final ChannelHandlerContext ctx, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void connect(final ChannelHandlerContext ctx, final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void disconnect(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        ctx.close(promise);
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void deregister(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void read(final ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        ctx.write(msg, promise);
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void flush(final ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
