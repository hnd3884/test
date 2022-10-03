package io.netty.handler.ssl.ocsp;

import javax.net.ssl.SSLHandshakeException;
import io.netty.channel.ChannelHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class OcspClientHandler extends ChannelInboundHandlerAdapter
{
    private final ReferenceCountedOpenSslEngine engine;
    
    protected OcspClientHandler(final ReferenceCountedOpenSslEngine engine) {
        this.engine = ObjectUtil.checkNotNull(engine, "engine");
    }
    
    protected abstract boolean verify(final ChannelHandlerContext p0, final ReferenceCountedOpenSslEngine p1) throws Exception;
    
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt instanceof SslHandshakeCompletionEvent) {
            ctx.pipeline().remove(this);
            final SslHandshakeCompletionEvent event = (SslHandshakeCompletionEvent)evt;
            if (event.isSuccess() && !this.verify(ctx, this.engine)) {
                throw new SSLHandshakeException("Bad OCSP response");
            }
        }
        ctx.fireUserEventTriggered(evt);
    }
}
