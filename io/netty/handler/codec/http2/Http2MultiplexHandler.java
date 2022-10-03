package io.netty.handler.codec.http2;

import io.netty.channel.ServerChannel;
import javax.net.ssl.SSLException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import io.netty.channel.ChannelHandlerContext;
import java.util.Queue;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelFutureListener;

public final class Http2MultiplexHandler extends Http2ChannelDuplexHandler
{
    static final ChannelFutureListener CHILD_CHANNEL_REGISTRATION_LISTENER;
    private final ChannelHandler inboundStreamHandler;
    private final ChannelHandler upgradeStreamHandler;
    private final Queue<AbstractHttp2StreamChannel> readCompletePendingQueue;
    private boolean parentReadInProgress;
    private int idCount;
    private volatile ChannelHandlerContext ctx;
    
    public Http2MultiplexHandler(final ChannelHandler inboundStreamHandler) {
        this(inboundStreamHandler, null);
    }
    
    public Http2MultiplexHandler(final ChannelHandler inboundStreamHandler, final ChannelHandler upgradeStreamHandler) {
        this.readCompletePendingQueue = new MaxCapacityQueue<AbstractHttp2StreamChannel>(new ArrayDeque<AbstractHttp2StreamChannel>(8), 100);
        this.inboundStreamHandler = ObjectUtil.checkNotNull(inboundStreamHandler, "inboundStreamHandler");
        this.upgradeStreamHandler = upgradeStreamHandler;
    }
    
    static void registerDone(final ChannelFuture future) {
        if (!future.isSuccess()) {
            final Channel childChannel = future.channel();
            if (childChannel.isRegistered()) {
                childChannel.close();
            }
            else {
                childChannel.unsafe().closeForcibly();
            }
        }
    }
    
    @Override
    protected void handlerAdded0(final ChannelHandlerContext ctx) {
        if (ctx.executor() != ctx.channel().eventLoop()) {
            throw new IllegalStateException("EventExecutor must be EventLoop of Channel");
        }
        this.ctx = ctx;
    }
    
    @Override
    protected void handlerRemoved0(final ChannelHandlerContext ctx) {
        this.readCompletePendingQueue.clear();
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        this.parentReadInProgress = true;
        if (!(msg instanceof Http2StreamFrame)) {
            if (msg instanceof Http2GoAwayFrame) {
                this.onHttp2GoAwayFrame(ctx, (Http2GoAwayFrame)msg);
            }
            ctx.fireChannelRead(msg);
            return;
        }
        if (msg instanceof Http2WindowUpdateFrame) {
            return;
        }
        final Http2StreamFrame streamFrame = (Http2StreamFrame)msg;
        final Http2FrameCodec.DefaultHttp2FrameStream s = (Http2FrameCodec.DefaultHttp2FrameStream)streamFrame.stream();
        final AbstractHttp2StreamChannel channel = (AbstractHttp2StreamChannel)s.attachment;
        if (msg instanceof Http2ResetFrame) {
            channel.pipeline().fireUserEventTriggered(msg);
        }
        else {
            channel.fireChildRead(streamFrame);
        }
    }
    
    @Override
    public void channelWritabilityChanged(final ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isWritable()) {
            this.forEachActiveStream(AbstractHttp2StreamChannel.WRITABLE_VISITOR);
        }
        ctx.fireChannelWritabilityChanged();
    }
    
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt instanceof Http2FrameStreamEvent) {
            final Http2FrameStreamEvent event = (Http2FrameStreamEvent)evt;
            final Http2FrameCodec.DefaultHttp2FrameStream stream = (Http2FrameCodec.DefaultHttp2FrameStream)event.stream();
            if (event.type() == Http2FrameStreamEvent.Type.State) {
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
                        AbstractHttp2StreamChannel ch;
                        if (stream.id() == 1 && !isServer(ctx)) {
                            if (this.upgradeStreamHandler == null) {
                                throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, "Client is misconfigured for upgrade requests", new Object[0]);
                            }
                            ch = new Http2MultiplexHandlerStreamChannel(stream, this.upgradeStreamHandler);
                            ch.closeOutbound();
                        }
                        else {
                            ch = new Http2MultiplexHandlerStreamChannel(stream, this.inboundStreamHandler);
                        }
                        final ChannelFuture future = ctx.channel().eventLoop().register(ch);
                        if (future.isDone()) {
                            registerDone(future);
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
            return;
        }
        ctx.fireUserEventTriggered(evt);
    }
    
    Http2StreamChannel newOutboundStream() {
        return new Http2MultiplexHandlerStreamChannel((Http2FrameCodec.DefaultHttp2FrameStream)this.newStream(), null);
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (cause instanceof Http2FrameStreamException) {
            final Http2FrameStreamException exception = (Http2FrameStreamException)cause;
            final Http2FrameStream stream = exception.stream();
            final AbstractHttp2StreamChannel childChannel = (AbstractHttp2StreamChannel)((Http2FrameCodec.DefaultHttp2FrameStream)stream).attachment;
            try {
                childChannel.pipeline().fireExceptionCaught(cause.getCause());
            }
            finally {
                childChannel.unsafe().closeForcibly();
            }
            return;
        }
        if (cause.getCause() instanceof SSLException) {
            this.forEachActiveStream(new Http2FrameStreamVisitor() {
                @Override
                public boolean visit(final Http2FrameStream stream) {
                    final AbstractHttp2StreamChannel childChannel = (AbstractHttp2StreamChannel)((Http2FrameCodec.DefaultHttp2FrameStream)stream).attachment;
                    childChannel.pipeline().fireExceptionCaught(cause);
                    return true;
                }
            });
        }
        ctx.fireExceptionCaught(cause);
    }
    
    private static boolean isServer(final ChannelHandlerContext ctx) {
        return ctx.channel().parent() instanceof ServerChannel;
    }
    
    private void onHttp2GoAwayFrame(final ChannelHandlerContext ctx, final Http2GoAwayFrame goAwayFrame) {
        if (goAwayFrame.lastStreamId() == Integer.MAX_VALUE) {
            return;
        }
        try {
            final boolean server = isServer(ctx);
            this.forEachActiveStream(new Http2FrameStreamVisitor() {
                @Override
                public boolean visit(final Http2FrameStream stream) {
                    final int streamId = stream.id();
                    if (streamId > goAwayFrame.lastStreamId() && Http2CodecUtil.isStreamIdValid(streamId, server)) {
                        final AbstractHttp2StreamChannel childChannel = (AbstractHttp2StreamChannel)((Http2FrameCodec.DefaultHttp2FrameStream)stream).attachment;
                        childChannel.pipeline().fireUserEventTriggered((Object)goAwayFrame.retainedDuplicate());
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
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        this.processPendingReadCompleteQueue();
        ctx.fireChannelReadComplete();
    }
    
    private void processPendingReadCompleteQueue() {
        this.parentReadInProgress = true;
        AbstractHttp2StreamChannel childChannel = this.readCompletePendingQueue.poll();
        if (childChannel != null) {
            try {
                do {
                    childChannel.fireChildReadComplete();
                    childChannel = this.readCompletePendingQueue.poll();
                } while (childChannel != null);
            }
            finally {
                this.parentReadInProgress = false;
                this.readCompletePendingQueue.clear();
                this.ctx.flush();
            }
        }
        else {
            this.parentReadInProgress = false;
        }
    }
    
    static {
        CHILD_CHANNEL_REGISTRATION_LISTENER = new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) {
                Http2MultiplexHandler.registerDone(future);
            }
        };
    }
    
    private final class Http2MultiplexHandlerStreamChannel extends AbstractHttp2StreamChannel
    {
        Http2MultiplexHandlerStreamChannel(final Http2FrameCodec.DefaultHttp2FrameStream stream, final ChannelHandler inboundHandler) {
            super(stream, ++Http2MultiplexHandler.this.idCount, inboundHandler);
        }
        
        @Override
        protected boolean isParentReadInProgress() {
            return Http2MultiplexHandler.this.parentReadInProgress;
        }
        
        @Override
        protected void addChannelToReadCompletePendingQueue() {
            while (!Http2MultiplexHandler.this.readCompletePendingQueue.offer(this)) {
                Http2MultiplexHandler.this.processPendingReadCompleteQueue();
            }
        }
        
        @Override
        protected ChannelHandlerContext parentContext() {
            return Http2MultiplexHandler.this.ctx;
        }
    }
}
