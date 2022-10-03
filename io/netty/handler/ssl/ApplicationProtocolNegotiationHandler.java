package io.netty.handler.ssl;

import io.netty.util.internal.logging.InternalLoggerFactory;
import javax.net.ssl.SSLException;
import io.netty.handler.codec.DecoderException;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class ApplicationProtocolNegotiationHandler extends ChannelInboundHandlerAdapter
{
    private static final InternalLogger logger;
    private final String fallbackProtocol;
    private final RecyclableArrayList bufferedMessages;
    private ChannelHandlerContext ctx;
    private boolean sslHandlerChecked;
    
    protected ApplicationProtocolNegotiationHandler(final String fallbackProtocol) {
        this.bufferedMessages = RecyclableArrayList.newInstance();
        this.fallbackProtocol = ObjectUtil.checkNotNull(fallbackProtocol, "fallbackProtocol");
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(this.ctx = ctx);
    }
    
    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        this.fireBufferedMessages();
        this.bufferedMessages.recycle();
        super.handlerRemoved(ctx);
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        this.bufferedMessages.add(msg);
        if (!this.sslHandlerChecked) {
            this.sslHandlerChecked = true;
            if (ctx.pipeline().get(SslHandler.class) == null) {
                this.removeSelfIfPresent(ctx);
            }
        }
    }
    
    private void fireBufferedMessages() {
        if (!this.bufferedMessages.isEmpty()) {
            for (int i = 0; i < this.bufferedMessages.size(); ++i) {
                this.ctx.fireChannelRead(this.bufferedMessages.get(i));
            }
            this.ctx.fireChannelReadComplete();
            this.bufferedMessages.clear();
        }
    }
    
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt instanceof SslHandshakeCompletionEvent) {
            final SslHandshakeCompletionEvent handshakeEvent = (SslHandshakeCompletionEvent)evt;
            try {
                if (handshakeEvent.isSuccess()) {
                    final SslHandler sslHandler = ctx.pipeline().get(SslHandler.class);
                    if (sslHandler == null) {
                        throw new IllegalStateException("cannot find an SslHandler in the pipeline (required for application-level protocol negotiation)");
                    }
                    final String protocol = sslHandler.applicationProtocol();
                    this.configurePipeline(ctx, (protocol != null) ? protocol : this.fallbackProtocol);
                }
            }
            catch (final Throwable cause) {
                this.exceptionCaught(ctx, cause);
            }
            finally {
                if (handshakeEvent.isSuccess()) {
                    this.removeSelfIfPresent(ctx);
                }
            }
        }
        if (evt instanceof ChannelInputShutdownEvent) {
            this.fireBufferedMessages();
        }
        ctx.fireUserEventTriggered(evt);
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        this.fireBufferedMessages();
        super.channelInactive(ctx);
    }
    
    private void removeSelfIfPresent(final ChannelHandlerContext ctx) {
        final ChannelPipeline pipeline = ctx.pipeline();
        if (pipeline.context(this) != null) {
            pipeline.remove(this);
        }
    }
    
    protected abstract void configurePipeline(final ChannelHandlerContext p0, final String p1) throws Exception;
    
    protected void handshakeFailure(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        ApplicationProtocolNegotiationHandler.logger.warn("{} TLS handshake failed:", ctx.channel(), cause);
        ctx.close();
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        final Throwable wrapped;
        if (cause instanceof DecoderException && (wrapped = cause.getCause()) instanceof SSLException) {
            try {
                this.handshakeFailure(ctx, wrapped);
                return;
            }
            finally {
                this.removeSelfIfPresent(ctx);
            }
        }
        ApplicationProtocolNegotiationHandler.logger.warn("{} Failed to select the application-level protocol:", ctx.channel(), cause);
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(ApplicationProtocolNegotiationHandler.class);
    }
}
