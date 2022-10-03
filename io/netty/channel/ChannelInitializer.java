package io.netty.channel;

import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import io.netty.util.internal.logging.InternalLogger;

@ChannelHandler.Sharable
public abstract class ChannelInitializer<C extends Channel> extends ChannelInboundHandlerAdapter
{
    private static final InternalLogger logger;
    private final Set<ChannelHandlerContext> initMap;
    
    public ChannelInitializer() {
        this.initMap = Collections.newSetFromMap(new ConcurrentHashMap<ChannelHandlerContext, Boolean>());
    }
    
    protected abstract void initChannel(final C p0) throws Exception;
    
    @Override
    public final void channelRegistered(final ChannelHandlerContext ctx) throws Exception {
        if (this.initChannel(ctx)) {
            ctx.pipeline().fireChannelRegistered();
            this.removeState(ctx);
        }
        else {
            ctx.fireChannelRegistered();
        }
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (ChannelInitializer.logger.isWarnEnabled()) {
            ChannelInitializer.logger.warn("Failed to initialize a channel. Closing: " + ctx.channel(), cause);
        }
        ctx.close();
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isRegistered() && this.initChannel(ctx)) {
            this.removeState(ctx);
        }
    }
    
    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        this.initMap.remove(ctx);
    }
    
    private boolean initChannel(final ChannelHandlerContext ctx) throws Exception {
        if (this.initMap.add(ctx)) {
            try {
                this.initChannel(ctx.channel());
            }
            catch (final Throwable cause) {
                this.exceptionCaught(ctx, cause);
            }
            finally {
                final ChannelPipeline pipeline = ctx.pipeline();
                if (pipeline.context(this) != null) {
                    pipeline.remove(this);
                }
            }
            return true;
        }
        return false;
    }
    
    private void removeState(final ChannelHandlerContext ctx) {
        if (ctx.isRemoved()) {
            this.initMap.remove(ctx);
        }
        else {
            ctx.executor().execute(new Runnable() {
                @Override
                public void run() {
                    ChannelInitializer.this.initMap.remove(ctx);
                }
            });
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);
    }
}
