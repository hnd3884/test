package io.netty.bootstrap;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import java.util.HashMap;
import java.util.Collections;
import io.netty.util.concurrent.EventExecutor;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelFuture;
import java.net.InetAddress;
import io.netty.util.internal.SocketUtils;
import java.net.InetSocketAddress;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashMap;
import io.netty.channel.ChannelHandler;
import java.net.SocketAddress;
import io.netty.channel.EventLoopGroup;
import io.netty.util.AttributeKey;
import io.netty.channel.ChannelOption;
import java.util.Map;
import io.netty.channel.Channel;

public abstract class AbstractBootstrap<B extends AbstractBootstrap<B, C>, C extends Channel> implements Cloneable
{
    private static final Map.Entry<ChannelOption<?>, Object>[] EMPTY_OPTION_ARRAY;
    private static final Map.Entry<AttributeKey<?>, Object>[] EMPTY_ATTRIBUTE_ARRAY;
    volatile EventLoopGroup group;
    private volatile ChannelFactory<? extends C> channelFactory;
    private volatile SocketAddress localAddress;
    private final Map<ChannelOption<?>, Object> options;
    private final Map<AttributeKey<?>, Object> attrs;
    private volatile ChannelHandler handler;
    
    AbstractBootstrap() {
        this.options = new LinkedHashMap<ChannelOption<?>, Object>();
        this.attrs = new ConcurrentHashMap<AttributeKey<?>, Object>();
    }
    
    AbstractBootstrap(final AbstractBootstrap<B, C> bootstrap) {
        this.options = new LinkedHashMap<ChannelOption<?>, Object>();
        this.attrs = new ConcurrentHashMap<AttributeKey<?>, Object>();
        this.group = bootstrap.group;
        this.channelFactory = bootstrap.channelFactory;
        this.handler = bootstrap.handler;
        this.localAddress = bootstrap.localAddress;
        synchronized (bootstrap.options) {
            this.options.putAll(bootstrap.options);
        }
        this.attrs.putAll(bootstrap.attrs);
    }
    
    public B group(final EventLoopGroup group) {
        ObjectUtil.checkNotNull(group, "group");
        if (this.group != null) {
            throw new IllegalStateException("group set already");
        }
        this.group = group;
        return this.self();
    }
    
    private B self() {
        return (B)this;
    }
    
    public B channel(final Class<? extends C> channelClass) {
        return this.channelFactory((io.netty.channel.ChannelFactory<? extends C>)new ReflectiveChannelFactory<C>(ObjectUtil.checkNotNull(channelClass, "channelClass")));
    }
    
    @Deprecated
    public B channelFactory(final ChannelFactory<? extends C> channelFactory) {
        ObjectUtil.checkNotNull(channelFactory, "channelFactory");
        if (this.channelFactory != null) {
            throw new IllegalStateException("channelFactory set already");
        }
        this.channelFactory = channelFactory;
        return this.self();
    }
    
    public B channelFactory(final io.netty.channel.ChannelFactory<? extends C> channelFactory) {
        return this.channelFactory((ChannelFactory<? extends C>)channelFactory);
    }
    
    public B localAddress(final SocketAddress localAddress) {
        this.localAddress = localAddress;
        return this.self();
    }
    
    public B localAddress(final int inetPort) {
        return this.localAddress(new InetSocketAddress(inetPort));
    }
    
    public B localAddress(final String inetHost, final int inetPort) {
        return this.localAddress(SocketUtils.socketAddress(inetHost, inetPort));
    }
    
    public B localAddress(final InetAddress inetHost, final int inetPort) {
        return this.localAddress(new InetSocketAddress(inetHost, inetPort));
    }
    
    public <T> B option(final ChannelOption<T> option, final T value) {
        ObjectUtil.checkNotNull(option, "option");
        synchronized (this.options) {
            if (value == null) {
                this.options.remove(option);
            }
            else {
                this.options.put(option, value);
            }
        }
        return this.self();
    }
    
    public <T> B attr(final AttributeKey<T> key, final T value) {
        ObjectUtil.checkNotNull(key, "key");
        if (value == null) {
            this.attrs.remove(key);
        }
        else {
            this.attrs.put(key, value);
        }
        return this.self();
    }
    
    public B validate() {
        if (this.group == null) {
            throw new IllegalStateException("group not set");
        }
        if (this.channelFactory == null) {
            throw new IllegalStateException("channel or channelFactory not set");
        }
        return this.self();
    }
    
    public abstract B clone();
    
    public ChannelFuture register() {
        this.validate();
        return this.initAndRegister();
    }
    
    public ChannelFuture bind() {
        this.validate();
        final SocketAddress localAddress = this.localAddress;
        if (localAddress == null) {
            throw new IllegalStateException("localAddress not set");
        }
        return this.doBind(localAddress);
    }
    
    public ChannelFuture bind(final int inetPort) {
        return this.bind(new InetSocketAddress(inetPort));
    }
    
    public ChannelFuture bind(final String inetHost, final int inetPort) {
        return this.bind(SocketUtils.socketAddress(inetHost, inetPort));
    }
    
    public ChannelFuture bind(final InetAddress inetHost, final int inetPort) {
        return this.bind(new InetSocketAddress(inetHost, inetPort));
    }
    
    public ChannelFuture bind(final SocketAddress localAddress) {
        this.validate();
        return this.doBind(ObjectUtil.checkNotNull(localAddress, "localAddress"));
    }
    
    private ChannelFuture doBind(final SocketAddress localAddress) {
        final ChannelFuture regFuture = this.initAndRegister();
        final Channel channel = regFuture.channel();
        if (regFuture.cause() != null) {
            return regFuture;
        }
        if (regFuture.isDone()) {
            final ChannelPromise promise = channel.newPromise();
            doBind0(regFuture, channel, localAddress, promise);
            return promise;
        }
        final PendingRegistrationPromise promise2 = new PendingRegistrationPromise(channel);
        regFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                final Throwable cause = future.cause();
                if (cause != null) {
                    promise2.setFailure(cause);
                }
                else {
                    promise2.registered();
                    doBind0(regFuture, channel, localAddress, promise2);
                }
            }
        });
        return promise2;
    }
    
    final ChannelFuture initAndRegister() {
        Channel channel = null;
        try {
            channel = (Channel)this.channelFactory.newChannel();
            this.init(channel);
        }
        catch (final Throwable t) {
            if (channel != null) {
                channel.unsafe().closeForcibly();
                return new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE).setFailure(t);
            }
            return new DefaultChannelPromise(new FailedChannel(), GlobalEventExecutor.INSTANCE).setFailure(t);
        }
        final ChannelFuture regFuture = this.config().group().register(channel);
        if (regFuture.cause() != null) {
            if (channel.isRegistered()) {
                channel.close();
            }
            else {
                channel.unsafe().closeForcibly();
            }
        }
        return regFuture;
    }
    
    abstract void init(final Channel p0) throws Exception;
    
    private static void doBind0(final ChannelFuture regFuture, final Channel channel, final SocketAddress localAddress, final ChannelPromise promise) {
        channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                if (regFuture.isSuccess()) {
                    channel.bind(localAddress, promise).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE_ON_FAILURE);
                }
                else {
                    promise.setFailure(regFuture.cause());
                }
            }
        });
    }
    
    public B handler(final ChannelHandler handler) {
        this.handler = ObjectUtil.checkNotNull(handler, "handler");
        return this.self();
    }
    
    @Deprecated
    public final EventLoopGroup group() {
        return this.group;
    }
    
    public abstract AbstractBootstrapConfig<B, C> config();
    
    final Map.Entry<ChannelOption<?>, Object>[] newOptionsArray() {
        return newOptionsArray(this.options);
    }
    
    static Map.Entry<ChannelOption<?>, Object>[] newOptionsArray(final Map<ChannelOption<?>, Object> options) {
        synchronized (options) {
            return (Map.Entry[])new LinkedHashMap(options).entrySet().toArray(AbstractBootstrap.EMPTY_OPTION_ARRAY);
        }
    }
    
    final Map.Entry<AttributeKey<?>, Object>[] newAttributesArray() {
        return newAttributesArray(this.attrs0());
    }
    
    static Map.Entry<AttributeKey<?>, Object>[] newAttributesArray(final Map<AttributeKey<?>, Object> attributes) {
        return attributes.entrySet().toArray(AbstractBootstrap.EMPTY_ATTRIBUTE_ARRAY);
    }
    
    final Map<ChannelOption<?>, Object> options0() {
        return this.options;
    }
    
    final Map<AttributeKey<?>, Object> attrs0() {
        return this.attrs;
    }
    
    final SocketAddress localAddress() {
        return this.localAddress;
    }
    
    final ChannelFactory<? extends C> channelFactory() {
        return this.channelFactory;
    }
    
    final ChannelHandler handler() {
        return this.handler;
    }
    
    final Map<ChannelOption<?>, Object> options() {
        synchronized (this.options) {
            return copiedMap(this.options);
        }
    }
    
    final Map<AttributeKey<?>, Object> attrs() {
        return copiedMap(this.attrs);
    }
    
    static <K, V> Map<K, V> copiedMap(final Map<K, V> map) {
        if (map.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap((Map<? extends K, ? extends V>)new HashMap<K, V>((Map<? extends K, ? extends V>)map));
    }
    
    static void setAttributes(final Channel channel, final Map.Entry<AttributeKey<?>, Object>[] attrs) {
        for (final Map.Entry<AttributeKey<?>, Object> e : attrs) {
            final AttributeKey<Object> key = e.getKey();
            channel.attr(key).set(e.getValue());
        }
    }
    
    static void setChannelOptions(final Channel channel, final Map.Entry<ChannelOption<?>, Object>[] options, final InternalLogger logger) {
        for (final Map.Entry<ChannelOption<?>, Object> e : options) {
            setChannelOption(channel, e.getKey(), e.getValue(), logger);
        }
    }
    
    private static void setChannelOption(final Channel channel, final ChannelOption<?> option, final Object value, final InternalLogger logger) {
        try {
            if (!channel.config().setOption(option, value)) {
                logger.warn("Unknown channel option '{}' for channel '{}'", option, channel);
            }
        }
        catch (final Throwable t) {
            logger.warn("Failed to set channel option '{}' with value '{}' for channel '{}'", option, value, channel, t);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder().append(StringUtil.simpleClassName(this)).append('(').append(this.config()).append(')');
        return buf.toString();
    }
    
    static {
        EMPTY_OPTION_ARRAY = new Map.Entry[0];
        EMPTY_ATTRIBUTE_ARRAY = new Map.Entry[0];
    }
    
    static final class PendingRegistrationPromise extends DefaultChannelPromise
    {
        private volatile boolean registered;
        
        PendingRegistrationPromise(final Channel channel) {
            super(channel);
        }
        
        void registered() {
            this.registered = true;
        }
        
        @Override
        protected EventExecutor executor() {
            if (this.registered) {
                return super.executor();
            }
            return GlobalEventExecutor.INSTANCE;
        }
    }
}
