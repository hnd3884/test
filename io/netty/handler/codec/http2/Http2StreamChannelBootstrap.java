package io.netty.handler.codec.http2;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.util.internal.StringUtil;
import io.netty.util.concurrent.EventExecutor;
import java.nio.channels.ClosedChannelException;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.netty.channel.ChannelOption;
import java.util.Map;
import io.netty.util.internal.logging.InternalLogger;

public final class Http2StreamChannelBootstrap
{
    private static final InternalLogger logger;
    private static final Map.Entry<ChannelOption<?>, Object>[] EMPTY_OPTION_ARRAY;
    private static final Map.Entry<AttributeKey<?>, Object>[] EMPTY_ATTRIBUTE_ARRAY;
    private final Map<ChannelOption<?>, Object> options;
    private final Map<AttributeKey<?>, Object> attrs;
    private final Channel channel;
    private volatile ChannelHandler handler;
    private volatile ChannelHandlerContext multiplexCtx;
    
    public Http2StreamChannelBootstrap(final Channel channel) {
        this.options = new LinkedHashMap<ChannelOption<?>, Object>();
        this.attrs = new ConcurrentHashMap<AttributeKey<?>, Object>();
        this.channel = ObjectUtil.checkNotNull(channel, "channel");
    }
    
    public <T> Http2StreamChannelBootstrap option(final ChannelOption<T> option, final T value) {
        ObjectUtil.checkNotNull(option, "option");
        synchronized (this.options) {
            if (value == null) {
                this.options.remove(option);
            }
            else {
                this.options.put(option, value);
            }
        }
        return this;
    }
    
    public <T> Http2StreamChannelBootstrap attr(final AttributeKey<T> key, final T value) {
        ObjectUtil.checkNotNull(key, "key");
        if (value == null) {
            this.attrs.remove(key);
        }
        else {
            this.attrs.put(key, value);
        }
        return this;
    }
    
    public Http2StreamChannelBootstrap handler(final ChannelHandler handler) {
        this.handler = ObjectUtil.checkNotNull(handler, "handler");
        return this;
    }
    
    public Future<Http2StreamChannel> open() {
        return this.open(this.channel.eventLoop().newPromise());
    }
    
    public Future<Http2StreamChannel> open(final Promise<Http2StreamChannel> promise) {
        try {
            final ChannelHandlerContext ctx = this.findCtx();
            final EventExecutor executor = ctx.executor();
            if (executor.inEventLoop()) {
                this.open0(ctx, promise);
            }
            else {
                final ChannelHandlerContext finalCtx = ctx;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (Http2StreamChannelBootstrap.this.channel.isActive()) {
                            Http2StreamChannelBootstrap.this.open0(finalCtx, promise);
                        }
                        else {
                            promise.setFailure(new ClosedChannelException());
                        }
                    }
                });
            }
        }
        catch (final Throwable cause) {
            promise.setFailure(cause);
        }
        return promise;
    }
    
    private ChannelHandlerContext findCtx() throws ClosedChannelException {
        ChannelHandlerContext ctx = this.multiplexCtx;
        if (ctx != null && !ctx.isRemoved()) {
            return ctx;
        }
        final ChannelPipeline pipeline = this.channel.pipeline();
        ctx = pipeline.context(Http2MultiplexCodec.class);
        if (ctx == null) {
            ctx = pipeline.context(Http2MultiplexHandler.class);
        }
        if (ctx != null) {
            return this.multiplexCtx = ctx;
        }
        if (this.channel.isActive()) {
            throw new IllegalStateException(StringUtil.simpleClassName(Http2MultiplexCodec.class) + " or " + StringUtil.simpleClassName(Http2MultiplexHandler.class) + " must be in the ChannelPipeline of Channel " + this.channel);
        }
        throw new ClosedChannelException();
    }
    
    @Deprecated
    public void open0(final ChannelHandlerContext ctx, final Promise<Http2StreamChannel> promise) {
        assert ctx.executor().inEventLoop();
        if (!promise.setUncancellable()) {
            return;
        }
        Http2StreamChannel streamChannel;
        try {
            if (ctx.handler() instanceof Http2MultiplexCodec) {
                streamChannel = ((Http2MultiplexCodec)ctx.handler()).newOutboundStream();
            }
            else {
                streamChannel = ((Http2MultiplexHandler)ctx.handler()).newOutboundStream();
            }
        }
        catch (final Exception e) {
            promise.setFailure(e);
            return;
        }
        try {
            this.init(streamChannel);
        }
        catch (final Exception e) {
            streamChannel.unsafe().closeForcibly();
            promise.setFailure(e);
            return;
        }
        final ChannelFuture future = ctx.channel().eventLoop().register(streamChannel);
        future.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) {
                if (future.isSuccess()) {
                    promise.setSuccess(streamChannel);
                }
                else if (future.isCancelled()) {
                    promise.cancel(false);
                }
                else {
                    if (streamChannel.isRegistered()) {
                        streamChannel.close();
                    }
                    else {
                        streamChannel.unsafe().closeForcibly();
                    }
                    promise.setFailure(future.cause());
                }
            }
        });
    }
    
    private void init(final Channel channel) {
        final ChannelPipeline p = channel.pipeline();
        final ChannelHandler handler = this.handler;
        if (handler != null) {
            p.addLast(handler);
        }
        final Map.Entry<ChannelOption<?>, Object>[] optionArray;
        synchronized (this.options) {
            optionArray = this.options.entrySet().toArray(Http2StreamChannelBootstrap.EMPTY_OPTION_ARRAY);
        }
        setChannelOptions(channel, optionArray);
        setAttributes(channel, this.attrs.entrySet().toArray(Http2StreamChannelBootstrap.EMPTY_ATTRIBUTE_ARRAY));
    }
    
    private static void setChannelOptions(final Channel channel, final Map.Entry<ChannelOption<?>, Object>[] options) {
        for (final Map.Entry<ChannelOption<?>, Object> e : options) {
            setChannelOption(channel, e.getKey(), e.getValue());
        }
    }
    
    private static void setChannelOption(final Channel channel, final ChannelOption<?> option, final Object value) {
        try {
            final ChannelOption<Object> opt = (ChannelOption<Object>)option;
            if (!channel.config().setOption(opt, value)) {
                Http2StreamChannelBootstrap.logger.warn("Unknown channel option '{}' for channel '{}'", option, channel);
            }
        }
        catch (final Throwable t) {
            Http2StreamChannelBootstrap.logger.warn("Failed to set channel option '{}' with value '{}' for channel '{}'", option, value, channel, t);
        }
    }
    
    private static void setAttributes(final Channel channel, final Map.Entry<AttributeKey<?>, Object>[] options) {
        for (final Map.Entry<AttributeKey<?>, Object> e : options) {
            final AttributeKey<Object> key = e.getKey();
            channel.attr(key).set(e.getValue());
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(Http2StreamChannelBootstrap.class);
        EMPTY_OPTION_ARRAY = new Map.Entry[0];
        EMPTY_ATTRIBUTE_ARRAY = new Map.Entry[0];
    }
}
