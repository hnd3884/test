package io.netty.handler.codec.http2;

import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.Channel;
import java.util.ArrayDeque;
import io.netty.channel.ChannelHandlerContext;
import java.util.Queue;
import io.netty.channel.ChannelHandler;

@Deprecated
public class Http2MultiplexCodec extends Http2FrameCodec
{
    private final ChannelHandler inboundStreamHandler;
    private final ChannelHandler upgradeStreamHandler;
    private final Queue<AbstractHttp2StreamChannel> readCompletePendingQueue;
    private boolean parentReadInProgress;
    private int idCount;
    volatile ChannelHandlerContext ctx;
    
    Http2MultiplexCodec(final Http2ConnectionEncoder encoder, final Http2ConnectionDecoder decoder, final Http2Settings initialSettings, final ChannelHandler inboundStreamHandler, final ChannelHandler upgradeStreamHandler, final boolean decoupleCloseAndGoAway) {
        super(encoder, decoder, initialSettings, decoupleCloseAndGoAway);
        this.readCompletePendingQueue = new MaxCapacityQueue<AbstractHttp2StreamChannel>(new ArrayDeque<AbstractHttp2StreamChannel>(8), 100);
        this.inboundStreamHandler = inboundStreamHandler;
        this.upgradeStreamHandler = upgradeStreamHandler;
    }
    
    @Override
    public void onHttpClientUpgrade() throws Http2Exception {
        if (this.upgradeStreamHandler == null) {
            throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, "Client is misconfigured for upgrade requests", new Object[0]);
        }
        super.onHttpClientUpgrade();
    }
    
    public final void handlerAdded0(final ChannelHandlerContext ctx) throws Exception {
        if (ctx.executor() != ctx.channel().eventLoop()) {
            throw new IllegalStateException("EventExecutor must be EventLoop of Channel");
        }
        this.ctx = ctx;
    }
    
    public final void handlerRemoved0(final ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved0(ctx);
        this.readCompletePendingQueue.clear();
    }
    
    @Override
    final void onHttp2Frame(final ChannelHandlerContext ctx, final Http2Frame frame) {
        if (frame instanceof Http2StreamFrame) {
            final Http2StreamFrame streamFrame = (Http2StreamFrame)frame;
            final AbstractHttp2StreamChannel channel = (AbstractHttp2StreamChannel)((DefaultHttp2FrameStream)streamFrame.stream()).attachment;
            channel.fireChildRead(streamFrame);
            return;
        }
        if (frame instanceof Http2GoAwayFrame) {
            this.onHttp2GoAwayFrame(ctx, (Http2GoAwayFrame)frame);
        }
        ctx.fireChannelRead((Object)frame);
    }
    
    @Override
    final void onHttp2StreamStateChanged(final ChannelHandlerContext ctx, final DefaultHttp2FrameStream stream) {
        switch (stream.state()) {
            case HALF_CLOSED_LOCAL: {
                if (stream.id() != 1) {
                    break;
                }
            }
            case HALF_CLOSED_REMOTE:
            case OPEN: {
                if (stream.attachment != null) {
                    break;
                }
                Http2MultiplexCodecStreamChannel streamChannel;
                if (stream.id() == 1 && !this.connection().isServer()) {
                    assert this.upgradeStreamHandler != null;
                    streamChannel = new Http2MultiplexCodecStreamChannel(stream, this.upgradeStreamHandler);
                    streamChannel.closeOutbound();
                }
                else {
                    streamChannel = new Http2MultiplexCodecStreamChannel(stream, this.inboundStreamHandler);
                }
                final ChannelFuture future = ctx.channel().eventLoop().register(streamChannel);
                if (future.isDone()) {
                    Http2MultiplexHandler.registerDone(future);
                    break;
                }
                future.addListener((GenericFutureListener<? extends Future<? super Void>>)Http2MultiplexHandler.CHILD_CHANNEL_REGISTRATION_LISTENER);
                break;
            }
            case CLOSED: {
                final AbstractHttp2StreamChannel channel = (AbstractHttp2StreamChannel)stream.attachment;
                if (channel != null) {
                    channel.streamClosed();
                    break;
                }
                break;
            }
        }
    }
    
    final Http2StreamChannel newOutboundStream() {
        return new Http2MultiplexCodecStreamChannel(this.newStream(), null);
    }
    
    @Override
    final void onHttp2FrameStreamException(final ChannelHandlerContext ctx, final Http2FrameStreamException cause) {
        final Http2FrameStream stream = cause.stream();
        final AbstractHttp2StreamChannel channel = (AbstractHttp2StreamChannel)((DefaultHttp2FrameStream)stream).attachment;
        try {
            channel.pipeline().fireExceptionCaught(cause.getCause());
        }
        finally {
            channel.unsafe().closeForcibly();
        }
    }
    
    private void onHttp2GoAwayFrame(final ChannelHandlerContext ctx, final Http2GoAwayFrame goAwayFrame) {
        if (goAwayFrame.lastStreamId() == Integer.MAX_VALUE) {
            return;
        }
        try {
            this.forEachActiveStream(new Http2FrameStreamVisitor() {
                @Override
                public boolean visit(final Http2FrameStream stream) {
                    final int streamId = stream.id();
                    final AbstractHttp2StreamChannel channel = (AbstractHttp2StreamChannel)((DefaultHttp2FrameStream)stream).attachment;
                    if (streamId > goAwayFrame.lastStreamId() && Http2MultiplexCodec.this.connection().local().isValidStreamId(streamId)) {
                        channel.pipeline().fireUserEventTriggered((Object)goAwayFrame.retainedDuplicate());
                    }
                    return true;
                }
            });
        }
        catch (final Http2Exception e) {
            ctx.fireExceptionCaught((Throwable)e);
            ctx.close();
        }
    }
    
    @Override
    public final void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        this.processPendingReadCompleteQueue();
        this.channelReadComplete0(ctx);
    }
    
    private void processPendingReadCompleteQueue() {
        this.parentReadInProgress = true;
        try {
            while (true) {
                final AbstractHttp2StreamChannel childChannel = this.readCompletePendingQueue.poll();
                if (childChannel == null) {
                    break;
                }
                childChannel.fireChildReadComplete();
            }
        }
        finally {
            this.parentReadInProgress = false;
            this.readCompletePendingQueue.clear();
            this.flush0(this.ctx);
        }
    }
    
    @Override
    public final void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        this.parentReadInProgress = true;
        super.channelRead(ctx, msg);
    }
    
    @Override
    public final void channelWritabilityChanged(final ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isWritable()) {
            this.forEachActiveStream(AbstractHttp2StreamChannel.WRITABLE_VISITOR);
        }
        super.channelWritabilityChanged(ctx);
    }
    
    final void flush0(final ChannelHandlerContext ctx) {
        this.flush(ctx);
    }
    
    private final class Http2MultiplexCodecStreamChannel extends AbstractHttp2StreamChannel
    {
        Http2MultiplexCodecStreamChannel(final DefaultHttp2FrameStream stream, final ChannelHandler inboundHandler) {
            super(stream, ++Http2MultiplexCodec.this.idCount, inboundHandler);
        }
        
        @Override
        protected boolean isParentReadInProgress() {
            return Http2MultiplexCodec.this.parentReadInProgress;
        }
        
        @Override
        protected void addChannelToReadCompletePendingQueue() {
            while (!Http2MultiplexCodec.this.readCompletePendingQueue.offer(this)) {
                Http2MultiplexCodec.this.processPendingReadCompleteQueue();
            }
        }
        
        @Override
        protected ChannelHandlerContext parentContext() {
            return Http2MultiplexCodec.this.ctx;
        }
        
        @Override
        protected ChannelFuture write0(final ChannelHandlerContext ctx, final Object msg) {
            final ChannelPromise promise = ctx.newPromise();
            Http2MultiplexCodec.this.write(ctx, msg, promise);
            return promise;
        }
        
        @Override
        protected void flush0(final ChannelHandlerContext ctx) {
            Http2MultiplexCodec.this.flush0(ctx);
        }
    }
}
