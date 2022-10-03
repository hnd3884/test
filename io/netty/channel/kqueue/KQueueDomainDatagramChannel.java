package io.netty.channel.kqueue;

import io.netty.channel.unix.DomainDatagramSocketAddress;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelPipeline;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.channel.unix.DomainDatagramChannelConfig;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import java.io.IOException;
import io.netty.channel.unix.PeerCredentials;
import io.netty.util.internal.StringUtil;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.channel.unix.DomainDatagramPacket;
import java.nio.ByteBuffer;
import io.netty.channel.unix.IovArray;
import io.netty.util.CharsetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import java.net.SocketAddress;
import io.netty.channel.Channel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.DomainDatagramChannel;

public final class KQueueDomainDatagramChannel extends AbstractKQueueDatagramChannel implements DomainDatagramChannel
{
    private static final String EXPECTED_TYPES;
    private volatile boolean connected;
    private volatile DomainSocketAddress local;
    private volatile DomainSocketAddress remote;
    private final KQueueDomainDatagramChannelConfig config;
    
    public KQueueDomainDatagramChannel() {
        this(BsdSocket.newSocketDomainDgram(), false);
    }
    
    public KQueueDomainDatagramChannel(final int fd) {
        this(new BsdSocket(fd), true);
    }
    
    private KQueueDomainDatagramChannel(final BsdSocket socket, final boolean active) {
        super(null, socket, active);
        this.config = new KQueueDomainDatagramChannelConfig(this);
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig config() {
        return this.config;
    }
    
    @Override
    protected void doBind(final SocketAddress localAddress) throws Exception {
        super.doBind(localAddress);
        this.local = (DomainSocketAddress)localAddress;
        this.active = true;
    }
    
    @Override
    protected void doClose() throws Exception {
        super.doClose();
        final boolean b = false;
        this.active = b;
        this.connected = b;
        this.local = null;
        this.remote = null;
    }
    
    @Override
    protected boolean doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) throws Exception {
        if (super.doConnect(remoteAddress, localAddress)) {
            if (localAddress != null) {
                this.local = (DomainSocketAddress)localAddress;
            }
            this.remote = (DomainSocketAddress)remoteAddress;
            return this.connected = true;
        }
        return false;
    }
    
    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }
    
    @Override
    protected boolean doWriteMessage(final Object msg) throws Exception {
        ByteBuf data;
        DomainSocketAddress remoteAddress;
        if (msg instanceof AddressedEnvelope) {
            final AddressedEnvelope<ByteBuf, DomainSocketAddress> envelope = (AddressedEnvelope<ByteBuf, DomainSocketAddress>)msg;
            data = envelope.content();
            remoteAddress = envelope.recipient();
        }
        else {
            data = (ByteBuf)msg;
            remoteAddress = null;
        }
        final int dataLen = data.readableBytes();
        if (dataLen == 0) {
            return true;
        }
        long writtenBytes;
        if (data.hasMemoryAddress()) {
            final long memoryAddress = data.memoryAddress();
            if (remoteAddress == null) {
                writtenBytes = this.socket.writeAddress(memoryAddress, data.readerIndex(), data.writerIndex());
            }
            else {
                writtenBytes = this.socket.sendToAddressDomainSocket(memoryAddress, data.readerIndex(), data.writerIndex(), remoteAddress.path().getBytes(CharsetUtil.UTF_8));
            }
        }
        else if (data.nioBufferCount() > 1) {
            final IovArray array = ((KQueueEventLoop)this.eventLoop()).cleanArray();
            array.add(data, data.readerIndex(), data.readableBytes());
            final int cnt = array.count();
            assert cnt != 0;
            if (remoteAddress == null) {
                writtenBytes = this.socket.writevAddresses(array.memoryAddress(0), cnt);
            }
            else {
                writtenBytes = this.socket.sendToAddressesDomainSocket(array.memoryAddress(0), cnt, remoteAddress.path().getBytes(CharsetUtil.UTF_8));
            }
        }
        else {
            final ByteBuffer nioData = data.internalNioBuffer(data.readerIndex(), data.readableBytes());
            if (remoteAddress == null) {
                writtenBytes = this.socket.write(nioData, nioData.position(), nioData.limit());
            }
            else {
                writtenBytes = this.socket.sendToDomainSocket(nioData, nioData.position(), nioData.limit(), remoteAddress.path().getBytes(CharsetUtil.UTF_8));
            }
        }
        return writtenBytes > 0L;
    }
    
    @Override
    protected Object filterOutboundMessage(final Object msg) {
        if (msg instanceof DomainDatagramPacket) {
            final DomainDatagramPacket packet = (DomainDatagramPacket)msg;
            final ByteBuf content = ((DefaultAddressedEnvelope<ByteBuf, A>)packet).content();
            return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DomainDatagramPacket(this.newDirectBuffer(packet, content), ((DefaultAddressedEnvelope<M, DomainSocketAddress>)packet).recipient()) : msg;
        }
        if (msg instanceof ByteBuf) {
            final ByteBuf buf = (ByteBuf)msg;
            return UnixChannelUtil.isBufferCopyNeededForWrite(buf) ? this.newDirectBuffer(buf) : buf;
        }
        if (msg instanceof AddressedEnvelope) {
            final AddressedEnvelope<Object, SocketAddress> e = (AddressedEnvelope<Object, SocketAddress>)msg;
            if (e.content() instanceof ByteBuf && (e.recipient() == null || e.recipient() instanceof DomainSocketAddress)) {
                final ByteBuf content = e.content();
                return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DefaultAddressedEnvelope<Object, SocketAddress>(this.newDirectBuffer(e, content), e.recipient()) : e;
            }
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + KQueueDomainDatagramChannel.EXPECTED_TYPES);
    }
    
    @Override
    public boolean isActive() {
        return this.socket.isOpen() && ((this.config.getActiveOnOpen() && this.isRegistered()) || this.active);
    }
    
    @Override
    public boolean isConnected() {
        return this.connected;
    }
    
    @Override
    public DomainSocketAddress localAddress() {
        return (DomainSocketAddress)super.localAddress();
    }
    
    @Override
    protected DomainSocketAddress localAddress0() {
        return this.local;
    }
    
    @Override
    protected AbstractKQueueUnsafe newUnsafe() {
        return new KQueueDomainDatagramChannelUnsafe();
    }
    
    public PeerCredentials peerCredentials() throws IOException {
        return this.socket.getPeerCredentials();
    }
    
    @Override
    public DomainSocketAddress remoteAddress() {
        return (DomainSocketAddress)super.remoteAddress();
    }
    
    @Override
    protected DomainSocketAddress remoteAddress0() {
        return this.remote;
    }
    
    static {
        EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DomainDatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DomainSocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
    }
    
    final class KQueueDomainDatagramChannelUnsafe extends AbstractKQueueUnsafe
    {
        @Override
        void readReady(final KQueueRecvByteAllocatorHandle allocHandle) {
            assert KQueueDomainDatagramChannel.this.eventLoop().inEventLoop();
            final DomainDatagramChannelConfig config = KQueueDomainDatagramChannel.this.config();
            if (KQueueDomainDatagramChannel.this.shouldBreakReadReady(config)) {
                this.clearReadFilter0();
                return;
            }
            final ChannelPipeline pipeline = KQueueDomainDatagramChannel.this.pipeline();
            final ByteBufAllocator allocator = config.getAllocator();
            allocHandle.reset(config);
            this.readReadyBefore();
            Throwable exception = null;
            try {
                ByteBuf byteBuf = null;
                try {
                    final boolean connected = KQueueDomainDatagramChannel.this.isConnected();
                    do {
                        byteBuf = allocHandle.allocate(allocator);
                        allocHandle.attemptedBytesRead(byteBuf.writableBytes());
                        DomainDatagramPacket packet;
                        if (connected) {
                            allocHandle.lastBytesRead(KQueueDomainDatagramChannel.this.doReadBytes(byteBuf));
                            if (allocHandle.lastBytesRead() <= 0) {
                                byteBuf.release();
                                break;
                            }
                            packet = new DomainDatagramPacket(byteBuf, (DomainSocketAddress)this.localAddress(), (DomainSocketAddress)this.remoteAddress());
                        }
                        else {
                            DomainDatagramSocketAddress remoteAddress;
                            if (byteBuf.hasMemoryAddress()) {
                                remoteAddress = KQueueDomainDatagramChannel.this.socket.recvFromAddressDomainSocket(byteBuf.memoryAddress(), byteBuf.writerIndex(), byteBuf.capacity());
                            }
                            else {
                                final ByteBuffer nioData = byteBuf.internalNioBuffer(byteBuf.writerIndex(), byteBuf.writableBytes());
                                remoteAddress = KQueueDomainDatagramChannel.this.socket.recvFromDomainSocket(nioData, nioData.position(), nioData.limit());
                            }
                            if (remoteAddress == null) {
                                allocHandle.lastBytesRead(-1);
                                byteBuf.release();
                                break;
                            }
                            DomainSocketAddress localAddress = remoteAddress.localAddress();
                            if (localAddress == null) {
                                localAddress = (DomainSocketAddress)this.localAddress();
                            }
                            allocHandle.lastBytesRead(remoteAddress.receivedAmount());
                            byteBuf.writerIndex(byteBuf.writerIndex() + allocHandle.lastBytesRead());
                            packet = new DomainDatagramPacket(byteBuf, localAddress, remoteAddress);
                        }
                        allocHandle.incMessagesRead(1);
                        this.readPending = false;
                        pipeline.fireChannelRead((Object)packet);
                        byteBuf = null;
                    } while (allocHandle.continueReading(UncheckedBooleanSupplier.TRUE_SUPPLIER));
                }
                catch (final Throwable t) {
                    if (byteBuf != null) {
                        byteBuf.release();
                    }
                    exception = t;
                }
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (exception != null) {
                    pipeline.fireExceptionCaught(exception);
                }
            }
            finally {
                this.readReadyFinally(config);
            }
        }
    }
}
