package io.netty.channel.socket.nio;

import java.util.Map;
import io.netty.channel.ChannelOption;
import java.net.ServerSocket;
import io.netty.channel.socket.DefaultServerSocketChannelConfig;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import io.netty.channel.Channel;
import java.io.IOException;
import io.netty.channel.ChannelException;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.internal.logging.InternalLogger;
import java.nio.channels.spi.SelectorProvider;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.nio.AbstractNioMessageChannel;

public class NioServerSocketChannel extends AbstractNioMessageChannel implements ServerSocketChannel
{
    private static final ChannelMetadata METADATA;
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER;
    private static final InternalLogger logger;
    private final ServerSocketChannelConfig config;
    
    private static java.nio.channels.ServerSocketChannel newSocket(final SelectorProvider provider) {
        try {
            return provider.openServerSocketChannel();
        }
        catch (final IOException e) {
            throw new ChannelException("Failed to open a server socket.", e);
        }
    }
    
    public NioServerSocketChannel() {
        this(newSocket(NioServerSocketChannel.DEFAULT_SELECTOR_PROVIDER));
    }
    
    public NioServerSocketChannel(final SelectorProvider provider) {
        this(newSocket(provider));
    }
    
    public NioServerSocketChannel(final java.nio.channels.ServerSocketChannel channel) {
        super(null, channel, 16);
        this.config = new NioServerSocketChannelConfig(this, this.javaChannel().socket());
    }
    
    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }
    
    @Override
    public ChannelMetadata metadata() {
        return NioServerSocketChannel.METADATA;
    }
    
    @Override
    public ServerSocketChannelConfig config() {
        return this.config;
    }
    
    @Override
    public boolean isActive() {
        return this.isOpen() && this.javaChannel().socket().isBound();
    }
    
    @Override
    public InetSocketAddress remoteAddress() {
        return null;
    }
    
    @Override
    protected java.nio.channels.ServerSocketChannel javaChannel() {
        return (java.nio.channels.ServerSocketChannel)super.javaChannel();
    }
    
    @Override
    protected SocketAddress localAddress0() {
        return SocketUtils.localSocketAddress(this.javaChannel().socket());
    }
    
    @SuppressJava6Requirement(reason = "Usage guarded by java version check")
    @Override
    protected void doBind(final SocketAddress localAddress) throws Exception {
        if (PlatformDependent.javaVersion() >= 7) {
            this.javaChannel().bind(localAddress, this.config.getBacklog());
        }
        else {
            this.javaChannel().socket().bind(localAddress, this.config.getBacklog());
        }
    }
    
    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }
    
    @Override
    protected int doReadMessages(final List<Object> buf) throws Exception {
        final SocketChannel ch = SocketUtils.accept(this.javaChannel());
        try {
            if (ch != null) {
                buf.add(new NioSocketChannel(this, ch));
                return 1;
            }
        }
        catch (final Throwable t) {
            NioServerSocketChannel.logger.warn("Failed to create a new channel from an accepted socket.", t);
            try {
                ch.close();
            }
            catch (final Throwable t2) {
                NioServerSocketChannel.logger.warn("Failed to close a socket.", t2);
            }
        }
        return 0;
    }
    
    @Override
    protected boolean doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void doFinishConnect() throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }
    
    @Override
    protected void doDisconnect() throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected boolean doWriteMessage(final Object msg, final ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected final Object filterOutboundMessage(final Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected boolean closeOnReadError(final Throwable cause) {
        return super.closeOnReadError(cause);
    }
    
    static {
        METADATA = new ChannelMetadata(false, 16);
        DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
        logger = InternalLoggerFactory.getInstance(NioServerSocketChannel.class);
    }
    
    private final class NioServerSocketChannelConfig extends DefaultServerSocketChannelConfig
    {
        private NioServerSocketChannelConfig(final NioServerSocketChannel channel, final ServerSocket javaSocket) {
            super(channel, javaSocket);
        }
        
        @Override
        protected void autoReadCleared() {
            AbstractNioChannel.this.clearReadPending();
        }
        
        @Override
        public <T> boolean setOption(final ChannelOption<T> option, final T value) {
            if (PlatformDependent.javaVersion() >= 7 && option instanceof NioChannelOption) {
                return NioChannelOption.setOption(this.jdkChannel(), (NioChannelOption)option, value);
            }
            return super.setOption(option, value);
        }
        
        @Override
        public <T> T getOption(final ChannelOption<T> option) {
            if (PlatformDependent.javaVersion() >= 7 && option instanceof NioChannelOption) {
                return NioChannelOption.getOption(this.jdkChannel(), (NioChannelOption<T>)(NioChannelOption)option);
            }
            return super.getOption(option);
        }
        
        @Override
        public Map<ChannelOption<?>, Object> getOptions() {
            if (PlatformDependent.javaVersion() >= 7) {
                return this.getOptions(super.getOptions(), (ChannelOption<?>[])NioChannelOption.getOptions(this.jdkChannel()));
            }
            return super.getOptions();
        }
        
        private java.nio.channels.ServerSocketChannel jdkChannel() {
            return ((NioServerSocketChannel)this.channel).javaChannel();
        }
    }
}
