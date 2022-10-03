package io.netty.channel;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;

public abstract class SimpleUserEventChannelHandler<I> extends ChannelInboundHandlerAdapter
{
    private final TypeParameterMatcher matcher;
    private final boolean autoRelease;
    
    protected SimpleUserEventChannelHandler() {
        this(true);
    }
    
    protected SimpleUserEventChannelHandler(final boolean autoRelease) {
        this.matcher = TypeParameterMatcher.find(this, SimpleUserEventChannelHandler.class, "I");
        this.autoRelease = autoRelease;
    }
    
    protected SimpleUserEventChannelHandler(final Class<? extends I> eventType) {
        this(eventType, true);
    }
    
    protected SimpleUserEventChannelHandler(final Class<? extends I> eventType, final boolean autoRelease) {
        this.matcher = TypeParameterMatcher.get(eventType);
        this.autoRelease = autoRelease;
    }
    
    protected boolean acceptEvent(final Object evt) throws Exception {
        return this.matcher.match(evt);
    }
    
    @Override
    public final void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        boolean release = true;
        try {
            if (this.acceptEvent(evt)) {
                final I ievt = (I)evt;
                this.eventReceived(ctx, ievt);
            }
            else {
                release = false;
                ctx.fireUserEventTriggered(evt);
            }
        }
        finally {
            if (this.autoRelease && release) {
                ReferenceCountUtil.release(evt);
            }
        }
    }
    
    protected abstract void eventReceived(final ChannelHandlerContext p0, final I p1) throws Exception;
}
