package io.netty.channel.kqueue;

import io.netty.channel.unix.DatagramSocketAddress;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelPipeline;
import java.net.PortUnreachableException;
import io.netty.channel.unix.Errors;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.util.internal.StringUtil;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.channel.socket.DatagramPacket;
import java.nio.ByteBuffer;
import io.netty.channel.unix.IovArray;
import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import java.net.SocketAddress;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketException;
import java.net.NetworkInterface;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelFuture;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramChannel;

public final class KQueueDatagramChannel extends AbstractKQueueDatagramChannel implements DatagramChannel
{
    private static final String EXPECTED_TYPES;
    private volatile boolean connected;
    private final KQueueDatagramChannelConfig config;
    
    public KQueueDatagramChannel() {
        super(null, BsdSocket.newSocketDgram(), false);
        this.config = new KQueueDatagramChannelConfig(this);
    }
    
    public KQueueDatagramChannel(final int fd) {
        this(new BsdSocket(fd), true);
    }
    
    KQueueDatagramChannel(final BsdSocket socket, final boolean active) {
        super(null, socket, active);
        this.config = new KQueueDatagramChannelConfig(this);
    }
    
    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }
    
    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
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
    public ChannelFuture joinGroup(final InetAddress multicastAddress) {
        return this.joinGroup(multicastAddress, this.newPromise());
    }
    
    @Override
    public ChannelFuture joinGroup(final InetAddress multicastAddress, final ChannelPromise promise) {
        try {
            NetworkInterface iface = this.config().getNetworkInterface();
            if (iface == null) {
                iface = NetworkInterface.getByInetAddress(this.localAddress().getAddress());
            }
            return this.joinGroup(multicastAddress, iface, null, promise);
        }
        catch (final SocketException e) {
            promise.setFailure((Throwable)e);
            return promise;
        }
    }
    
    @Override
    public ChannelFuture joinGroup(final InetSocketAddress multicastAddress, final NetworkInterface networkInterface) {
        return this.joinGroup(multicastAddress, networkInterface, this.newPromise());
    }
    
    @Override
    public ChannelFuture joinGroup(final InetSocketAddress multicastAddress, final NetworkInterface networkInterface, final ChannelPromise promise) {
        return this.joinGroup(multicastAddress.getAddress(), networkInterface, null, promise);
    }
    
    @Override
    public ChannelFuture joinGroup(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source) {
        return this.joinGroup(multicastAddress, networkInterface, source, this.newPromise());
    }
    
    @Override
    public ChannelFuture joinGroup(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        promise.setFailure((Throwable)new UnsupportedOperationException("Multicast not supported"));
        return promise;
    }
    
    @Override
    public ChannelFuture leaveGroup(final InetAddress multicastAddress) {
        return this.leaveGroup(multicastAddress, this.newPromise());
    }
    
    @Override
    public ChannelFuture leaveGroup(final InetAddress multicastAddress, final ChannelPromise promise) {
        try {
            return this.leaveGroup(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), null, promise);
        }
        catch (final SocketException e) {
            promise.setFailure((Throwable)e);
            return promise;
        }
    }
    
    @Override
    public ChannelFuture leaveGroup(final InetSocketAddress multicastAddress, final NetworkInterface networkInterface) {
        return this.leaveGroup(multicastAddress, networkInterface, this.newPromise());
    }
    
    @Override
    public ChannelFuture leaveGroup(final InetSocketAddress multicastAddress, final NetworkInterface networkInterface, final ChannelPromise promise) {
        return this.leaveGroup(multicastAddress.getAddress(), networkInterface, null, promise);
    }
    
    @Override
    public ChannelFuture leaveGroup(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source) {
        return this.leaveGroup(multicastAddress, networkInterface, source, this.newPromise());
    }
    
    @Override
    public ChannelFuture leaveGroup(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress source, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        promise.setFailure((Throwable)new UnsupportedOperationException("Multicast not supported"));
        return promise;
    }
    
    @Override
    public ChannelFuture block(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress sourceToBlock) {
        return this.block(multicastAddress, networkInterface, sourceToBlock, this.newPromise());
    }
    
    @Override
    public ChannelFuture block(final InetAddress multicastAddress, final NetworkInterface networkInterface, final InetAddress sourceToBlock, final ChannelPromise promise) {
        ObjectUtil.checkNotNull(multicastAddress, "multicastAddress");
        ObjectUtil.checkNotNull(sourceToBlock, "sourceToBlock");
        ObjectUtil.checkNotNull(networkInterface, "networkInterface");
        promise.setFailure((Throwable)new UnsupportedOperationException("Multicast not supported"));
        return promise;
    }
    
    @Override
    public ChannelFuture block(final InetAddress multicastAddress, final InetAddress sourceToBlock) {
        return this.block(multicastAddress, sourceToBlock, this.newPromise());
    }
    
    @Override
    public ChannelFuture block(final InetAddress multicastAddress, final InetAddress sourceToBlock, final ChannelPromise promise) {
        try {
            return this.block(multicastAddress, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), sourceToBlock, promise);
        }
        catch (final Throwable e) {
            promise.setFailure(e);
            return promise;
        }
    }
    
    @Override
    protected AbstractKQueueUnsafe newUnsafe() {
        return new KQueueDatagramChannelUnsafe();
    }
    
    @Override
    protected void doBind(final SocketAddress localAddress) throws Exception {
        super.doBind(localAddress);
        this.active = true;
    }
    
    @Override
    protected boolean doWriteMessage(final Object msg) throws Exception {
        ByteBuf data;
        InetSocketAddress remoteAddress;
        if (msg instanceof AddressedEnvelope) {
            final AddressedEnvelope<ByteBuf, InetSocketAddress> envelope = (AddressedEnvelope<ByteBuf, InetSocketAddress>)msg;
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
                writtenBytes = this.socket.sendToAddress(memoryAddress, data.readerIndex(), data.writerIndex(), remoteAddress.getAddress(), remoteAddress.getPort());
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
                writtenBytes = this.socket.sendToAddresses(array.memoryAddress(0), cnt, remoteAddress.getAddress(), remoteAddress.getPort());
            }
        }
        else {
            final ByteBuffer nioData = data.internalNioBuffer(data.readerIndex(), data.readableBytes());
            if (remoteAddress == null) {
                writtenBytes = this.socket.write(nioData, nioData.position(), nioData.limit());
            }
            else {
                writtenBytes = this.socket.sendTo(nioData, nioData.position(), nioData.limit(), remoteAddress.getAddress(), remoteAddress.getPort());
            }
        }
        return writtenBytes > 0L;
    }
    
    @Override
    protected Object filterOutboundMessage(final Object msg) {
        if (msg instanceof DatagramPacket) {
            final DatagramPacket packet = (DatagramPacket)msg;
            final ByteBuf content = ((DefaultAddressedEnvelope<ByteBuf, A>)packet).content();
            return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DatagramPacket(this.newDirectBuffer(packet, content), ((DefaultAddressedEnvelope<M, InetSocketAddress>)packet).recipient()) : msg;
        }
        if (msg instanceof ByteBuf) {
            final ByteBuf buf = (ByteBuf)msg;
            return UnixChannelUtil.isBufferCopyNeededForWrite(buf) ? this.newDirectBuffer(buf) : buf;
        }
        if (msg instanceof AddressedEnvelope) {
            final AddressedEnvelope<Object, SocketAddress> e = (AddressedEnvelope<Object, SocketAddress>)msg;
            if (e.content() instanceof ByteBuf && (e.recipient() == null || e.recipient() instanceof InetSocketAddress)) {
                final ByteBuf content = e.content();
                return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DefaultAddressedEnvelope<Object, SocketAddress>(this.newDirectBuffer(e, content), e.recipient()) : e;
            }
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + KQueueDatagramChannel.EXPECTED_TYPES);
    }
    
    @Override
    public KQueueDatagramChannelConfig config() {
        return this.config;
    }
    
    @Override
    protected void doDisconnect() throws Exception {
        this.socket.disconnect();
        final boolean b = false;
        this.active = b;
        this.connected = b;
        this.resetCachedAddresses();
    }
    
    @Override
    protected boolean doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) throws Exception {
        return super.doConnect(remoteAddress, localAddress) && (this.connected = true);
    }
    
    @Override
    protected void doClose() throws Exception {
        super.doClose();
        this.connected = false;
    }
    
    static {
        EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(InetSocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
    }
    
    final class KQueueDatagramChannelUnsafe extends AbstractKQueueUnsafe
    {
        @Override
        void readReady(final KQueueRecvByteAllocatorHandle allocHandle) {
            assert KQueueDatagramChannel.this.eventLoop().inEventLoop();
            final DatagramChannelConfig config = KQueueDatagramChannel.this.config();
            if (KQueueDatagramChannel.this.shouldBreakReadReady(config)) {
                this.clearReadFilter0();
                return;
            }
            final ChannelPipeline pipeline = KQueueDatagramChannel.this.pipeline();
            final ByteBufAllocator allocator = config.getAllocator();
            allocHandle.reset(config);
            this.readReadyBefore();
            Throwable exception = null;
            try {
                ByteBuf byteBuf = null;
                try {
                    final boolean connected = KQueueDatagramChannel.this.isConnected();
                    do {
                        byteBuf = allocHandle.allocate(allocator);
                        allocHandle.attemptedBytesRead(byteBuf.writableBytes());
                        DatagramPacket packet;
                        if (connected) {
                            try {
                                allocHandle.lastBytesRead(KQueueDatagramChannel.this.doReadBytes(byteBuf));
                            }
                            catch (final Errors.NativeIoException e) {
                                if (e.expectedErr() == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
                                    final PortUnreachableException error = new PortUnreachableException(e.getMessage());
                                    error.initCause(e);
                                    throw error;
                                }
                                throw e;
                            }
                            if (allocHandle.lastBytesRead() <= 0) {
                                byteBuf.release();
                                byteBuf = null;
                                break;
                            }
                            packet = new DatagramPacket(byteBuf, (InetSocketAddress)this.localAddress(), (InetSocketAddress)this.remoteAddress());
                        }
                        else {
                            DatagramSocketAddress remoteAddress;
                            if (byteBuf.hasMemoryAddress()) {
                                remoteAddress = KQueueDatagramChannel.this.socket.recvFromAddress(byteBuf.memoryAddress(), byteBuf.writerIndex(), byteBuf.capacity());
                            }
                            else {
                                final ByteBuffer nioData = byteBuf.internalNioBuffer(byteBuf.writerIndex(), byteBuf.writableBytes());
                                remoteAddress = KQueueDatagramChannel.this.socket.recvFrom(nioData, nioData.position(), nioData.limit());
                            }
                            if (remoteAddress == null) {
                                allocHandle.lastBytesRead(-1);
                                byteBuf.release();
                                byteBuf = null;
                                break;
                            }
                            InetSocketAddress localAddress = remoteAddress.localAddress();
                            if (localAddress == null) {
                                localAddress = (InetSocketAddress)this.localAddress();
                            }
                            allocHandle.lastBytesRead(remoteAddress.receivedAmount());
                            byteBuf.writerIndex(byteBuf.writerIndex() + allocHandle.lastBytesRead());
                            packet = new DatagramPacket(byteBuf, localAddress, remoteAddress);
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
