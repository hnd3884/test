package io.netty.channel.kqueue;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.unix.DomainSocketChannelConfig;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelConfig;
import java.io.IOException;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.ChannelOutboundBuffer;
import java.net.SocketAddress;
import io.netty.channel.Channel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.DomainSocketChannel;

public final class KQueueDomainSocketChannel extends AbstractKQueueStreamChannel implements DomainSocketChannel
{
    private final KQueueDomainSocketChannelConfig config;
    private volatile DomainSocketAddress local;
    private volatile DomainSocketAddress remote;
    
    public KQueueDomainSocketChannel() {
        super(null, BsdSocket.newSocketDomain(), false);
        this.config = new KQueueDomainSocketChannelConfig(this);
    }
    
    public KQueueDomainSocketChannel(final int fd) {
        this(null, new BsdSocket(fd));
    }
    
    KQueueDomainSocketChannel(final Channel parent, final BsdSocket fd) {
        super(parent, fd, true);
        this.config = new KQueueDomainSocketChannelConfig(this);
    }
    
    @Override
    protected AbstractKQueueUnsafe newUnsafe() {
        return new KQueueDomainUnsafe();
    }
    
    @Override
    protected DomainSocketAddress localAddress0() {
        return this.local;
    }
    
    @Override
    protected DomainSocketAddress remoteAddress0() {
        return this.remote;
    }
    
    @Override
    protected void doBind(final SocketAddress localAddress) throws Exception {
        this.socket.bind(localAddress);
        this.local = (DomainSocketAddress)localAddress;
    }
    
    @Override
    public KQueueDomainSocketChannelConfig config() {
        return this.config;
    }
    
    @Override
    protected boolean doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) throws Exception {
        if (super.doConnect(remoteAddress, localAddress)) {
            this.local = (DomainSocketAddress)localAddress;
            this.remote = (DomainSocketAddress)remoteAddress;
            return true;
        }
        return false;
    }
    
    @Override
    public DomainSocketAddress remoteAddress() {
        return (DomainSocketAddress)super.remoteAddress();
    }
    
    @Override
    public DomainSocketAddress localAddress() {
        return (DomainSocketAddress)super.localAddress();
    }
    
    @Override
    protected int doWriteSingle(final ChannelOutboundBuffer in) throws Exception {
        final Object msg = in.current();
        if (msg instanceof FileDescriptor && this.socket.sendFd(((FileDescriptor)msg).intValue()) > 0) {
            in.remove();
            return 1;
        }
        return super.doWriteSingle(in);
    }
    
    @Override
    protected Object filterOutboundMessage(final Object msg) {
        if (msg instanceof FileDescriptor) {
            return msg;
        }
        return super.filterOutboundMessage(msg);
    }
    
    public PeerCredentials peerCredentials() throws IOException {
        return this.socket.getPeerCredentials();
    }
    
    private final class KQueueDomainUnsafe extends KQueueStreamUnsafe
    {
        @Override
        void readReady(final KQueueRecvByteAllocatorHandle allocHandle) {
            switch (KQueueDomainSocketChannel.this.config().getReadMode()) {
                case BYTES: {
                    super.readReady(allocHandle);
                    break;
                }
                case FILE_DESCRIPTORS: {
                    this.readReadyFd();
                    break;
                }
                default: {
                    throw new Error();
                }
            }
        }
        
        private void readReadyFd() {
            if (KQueueDomainSocketChannel.this.socket.isInputShutdown()) {
                super.clearReadFilter0();
                return;
            }
            final ChannelConfig config = KQueueDomainSocketChannel.this.config();
            final KQueueRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            final ChannelPipeline pipeline = KQueueDomainSocketChannel.this.pipeline();
            allocHandle.reset(config);
            this.readReadyBefore();
            try {
            Label_0153:
                do {
                    final int recvFd = KQueueDomainSocketChannel.this.socket.recvFd();
                    switch (recvFd) {
                        case 0: {
                            allocHandle.lastBytesRead(0);
                            break Label_0153;
                        }
                        case -1: {
                            allocHandle.lastBytesRead(-1);
                            this.close(this.voidPromise());
                            return;
                        }
                        default: {
                            allocHandle.lastBytesRead(1);
                            allocHandle.incMessagesRead(1);
                            this.readPending = false;
                            pipeline.fireChannelRead((Object)new FileDescriptor(recvFd));
                            continue;
                        }
                    }
                } while (allocHandle.continueReading());
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
            }
            catch (final Throwable t) {
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                pipeline.fireExceptionCaught(t);
            }
            finally {
                this.readReadyFinally(config);
            }
        }
    }
}
