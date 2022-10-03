package io.netty.channel.kqueue;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.channel.ChannelConfig;
import java.io.File;
import java.net.SocketAddress;
import io.netty.channel.Channel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.unix.ServerDomainSocketChannel;

public final class KQueueServerDomainSocketChannel extends AbstractKQueueServerChannel implements ServerDomainSocketChannel
{
    private static final InternalLogger logger;
    private final KQueueServerChannelConfig config;
    private volatile DomainSocketAddress local;
    
    public KQueueServerDomainSocketChannel() {
        super(BsdSocket.newSocketDomain(), false);
        this.config = new KQueueServerChannelConfig(this);
    }
    
    public KQueueServerDomainSocketChannel(final int fd) {
        this(new BsdSocket(fd), false);
    }
    
    KQueueServerDomainSocketChannel(final BsdSocket socket, final boolean active) {
        super(socket, active);
        this.config = new KQueueServerChannelConfig(this);
    }
    
    protected Channel newChildChannel(final int fd, final byte[] addr, final int offset, final int len) throws Exception {
        return new KQueueDomainSocketChannel(this, new BsdSocket(fd));
    }
    
    @Override
    protected DomainSocketAddress localAddress0() {
        return this.local;
    }
    
    @Override
    protected void doBind(final SocketAddress localAddress) throws Exception {
        this.socket.bind(localAddress);
        this.socket.listen(this.config.getBacklog());
        this.local = (DomainSocketAddress)localAddress;
        this.active = true;
    }
    
    @Override
    protected void doClose() throws Exception {
        try {
            super.doClose();
        }
        finally {
            final DomainSocketAddress local = this.local;
            if (local != null) {
                final File socketFile = new File(local.path());
                final boolean success = socketFile.delete();
                if (!success && KQueueServerDomainSocketChannel.logger.isDebugEnabled()) {
                    KQueueServerDomainSocketChannel.logger.debug("Failed to delete a domain socket file: {}", local.path());
                }
            }
        }
    }
    
    @Override
    public KQueueServerChannelConfig config() {
        return this.config;
    }
    
    @Override
    public DomainSocketAddress remoteAddress() {
        return (DomainSocketAddress)super.remoteAddress();
    }
    
    @Override
    public DomainSocketAddress localAddress() {
        return (DomainSocketAddress)super.localAddress();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(KQueueServerDomainSocketChannel.class);
    }
}
