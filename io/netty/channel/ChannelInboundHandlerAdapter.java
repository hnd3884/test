package io.netty.channel;

public class ChannelInboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelInboundHandler
{
    @ChannelHandlerMask.Skip
    @Override
    public void channelRegistered(final ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelUnregistered();
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelActive();
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        ctx.fireChannelRead(msg);
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void channelWritabilityChanged(final ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelWritabilityChanged();
    }
    
    @ChannelHandlerMask.Skip
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
