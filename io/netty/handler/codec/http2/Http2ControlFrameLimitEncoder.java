package io.netty.handler.codec.http2;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.internal.logging.InternalLogger;

final class Http2ControlFrameLimitEncoder extends DecoratingHttp2ConnectionEncoder
{
    private static final InternalLogger logger;
    private final int maxOutstandingControlFrames;
    private final ChannelFutureListener outstandingControlFramesListener;
    private Http2LifecycleManager lifecycleManager;
    private int outstandingControlFrames;
    private boolean limitReached;
    
    Http2ControlFrameLimitEncoder(final Http2ConnectionEncoder delegate, final int maxOutstandingControlFrames) {
        super(delegate);
        this.outstandingControlFramesListener = new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) {
                Http2ControlFrameLimitEncoder.this.outstandingControlFrames--;
            }
        };
        this.maxOutstandingControlFrames = ObjectUtil.checkPositive(maxOutstandingControlFrames, "maxOutstandingControlFrames");
    }
    
    @Override
    public void lifecycleManager(final Http2LifecycleManager lifecycleManager) {
        super.lifecycleManager(this.lifecycleManager = lifecycleManager);
    }
    
    @Override
    public ChannelFuture writeSettingsAck(final ChannelHandlerContext ctx, final ChannelPromise promise) {
        final ChannelPromise newPromise = this.handleOutstandingControlFrames(ctx, promise);
        if (newPromise == null) {
            return promise;
        }
        return super.writeSettingsAck(ctx, newPromise);
    }
    
    @Override
    public ChannelFuture writePing(final ChannelHandlerContext ctx, final boolean ack, final long data, final ChannelPromise promise) {
        if (!ack) {
            return super.writePing(ctx, ack, data, promise);
        }
        final ChannelPromise newPromise = this.handleOutstandingControlFrames(ctx, promise);
        if (newPromise == null) {
            return promise;
        }
        return super.writePing(ctx, ack, data, newPromise);
    }
    
    @Override
    public ChannelFuture writeRstStream(final ChannelHandlerContext ctx, final int streamId, final long errorCode, final ChannelPromise promise) {
        final ChannelPromise newPromise = this.handleOutstandingControlFrames(ctx, promise);
        if (newPromise == null) {
            return promise;
        }
        return super.writeRstStream(ctx, streamId, errorCode, newPromise);
    }
    
    private ChannelPromise handleOutstandingControlFrames(final ChannelHandlerContext ctx, final ChannelPromise promise) {
        if (!this.limitReached) {
            if (this.outstandingControlFrames == this.maxOutstandingControlFrames) {
                ctx.flush();
            }
            if (this.outstandingControlFrames == this.maxOutstandingControlFrames) {
                this.limitReached = true;
                final Http2Exception exception = Http2Exception.connectionError(Http2Error.ENHANCE_YOUR_CALM, "Maximum number %d of outstanding control frames reached", this.maxOutstandingControlFrames);
                Http2ControlFrameLimitEncoder.logger.info("Maximum number {} of outstanding control frames reached. Closing channel {}", this.maxOutstandingControlFrames, ctx.channel(), exception);
                this.lifecycleManager.onError(ctx, true, exception);
                ctx.close();
            }
            ++this.outstandingControlFrames;
            return promise.unvoid().addListener((GenericFutureListener<? extends Future<? super Void>>)this.outstandingControlFramesListener);
        }
        return promise;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(Http2ControlFrameLimitEncoder.class);
    }
}
