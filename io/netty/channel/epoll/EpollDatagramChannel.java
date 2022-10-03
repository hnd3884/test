package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelConfig;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.RecyclableArrayList;
import java.net.PortUnreachableException;
import io.netty.channel.unix.Errors;
import java.nio.ByteBuffer;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.internal.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.unix.SegmentedDatagramPacket;
import io.netty.channel.ChannelOutboundBuffer;
import java.net.Inet4Address;
import java.net.SocketAddress;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.NetworkInterface;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelFuture;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import io.netty.channel.Channel;
import io.netty.channel.unix.Socket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.socket.DatagramChannel;

public final class EpollDatagramChannel extends AbstractEpollChannel implements DatagramChannel
{
    private static final ChannelMetadata METADATA;
    private static final String EXPECTED_TYPES;
    private final EpollDatagramChannelConfig config;
    private volatile boolean connected;
    
    public static boolean isSegmentedDatagramPacketSupported() {
        return Epoll.isAvailable() && Native.IS_SUPPORTING_SENDMMSG && Native.IS_SUPPORTING_UDP_SEGMENT;
    }
    
    public EpollDatagramChannel() {
        this((InternetProtocolFamily)null);
    }
    
    public EpollDatagramChannel(final InternetProtocolFamily family) {
        this((family == null) ? LinuxSocket.newSocketDgram(Socket.isIPv6Preferred()) : LinuxSocket.newSocketDgram(family == InternetProtocolFamily.IPv6), false);
    }
    
    public EpollDatagramChannel(final int fd) {
        this(new LinuxSocket(fd), true);
    }
    
    private EpollDatagramChannel(final LinuxSocket fd, final boolean active) {
        super(null, fd, active);
        this.config = new EpollDatagramChannelConfig(this);
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
    public ChannelMetadata metadata() {
        return EpollDatagramChannel.METADATA;
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
        catch (final IOException e) {
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
        try {
            this.socket.joinGroup(multicastAddress, networkInterface, source);
            promise.setSuccess();
        }
        catch (final IOException e) {
            promise.setFailure((Throwable)e);
        }
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
        catch (final IOException e) {
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
        try {
            this.socket.leaveGroup(multicastAddress, networkInterface, source);
            promise.setSuccess();
        }
        catch (final IOException e) {
            promise.setFailure((Throwable)e);
        }
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
    protected AbstractEpollUnsafe newUnsafe() {
        return new EpollDatagramChannelUnsafe();
    }
    
    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        if (localAddress instanceof InetSocketAddress) {
            final InetSocketAddress socketAddress = (InetSocketAddress)localAddress;
            if (socketAddress.getAddress().isAnyLocalAddress() && socketAddress.getAddress() instanceof Inet4Address && this.socket.family() == InternetProtocolFamily.IPv6) {
                localAddress = new InetSocketAddress(LinuxSocket.INET6_ANY, socketAddress.getPort());
            }
        }
        super.doBind(localAddress);
        this.active = true;
    }
    
    @Override
    protected void doWrite(final ChannelOutboundBuffer in) throws Exception {
        int maxMessagesPerWrite = this.maxMessagesPerWrite();
        while (maxMessagesPerWrite > 0) {
            final Object msg = in.current();
            if (msg == null) {
                break;
            }
            try {
                if ((Native.IS_SUPPORTING_SENDMMSG && in.size() > 1) || in.current() instanceof SegmentedDatagramPacket) {
                    final NativeDatagramPacketArray array = this.cleanDatagramPacketArray();
                    array.add(in, this.isConnected(), maxMessagesPerWrite);
                    final int cnt = array.count();
                    if (cnt >= 1) {
                        final int offset = 0;
                        final NativeDatagramPacketArray.NativeDatagramPacket[] packets = array.packets();
                        final int send = this.socket.sendmmsg(packets, offset, cnt);
                        if (send == 0) {
                            break;
                        }
                        for (int i = 0; i < send; ++i) {
                            in.remove();
                        }
                        maxMessagesPerWrite -= send;
                        continue;
                    }
                }
                boolean done = false;
                for (int j = this.config().getWriteSpinCount(); j > 0; --j) {
                    if (this.doWriteMessage(msg)) {
                        done = true;
                        break;
                    }
                }
                if (!done) {
                    break;
                }
                in.remove();
                --maxMessagesPerWrite;
            }
            catch (final IOException e) {
                --maxMessagesPerWrite;
                in.remove(e);
            }
        }
        if (in.isEmpty()) {
            this.clearFlag(Native.EPOLLOUT);
        }
        else {
            this.setFlag(Native.EPOLLOUT);
        }
    }
    
    private boolean doWriteMessage(final Object msg) throws Exception {
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
        return dataLen == 0 || this.doWriteOrSendBytes(data, remoteAddress, false) > 0L;
    }
    
    @Override
    protected Object filterOutboundMessage(final Object msg) {
        if (msg instanceof SegmentedDatagramPacket) {
            if (!Native.IS_SUPPORTING_UDP_SEGMENT) {
                throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EpollDatagramChannel.EXPECTED_TYPES);
            }
            final SegmentedDatagramPacket packet = (SegmentedDatagramPacket)msg;
            final ByteBuf content = ((DefaultAddressedEnvelope<ByteBuf, A>)packet).content();
            return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? packet.replace(this.newDirectBuffer(packet, content)) : msg;
        }
        else {
            if (msg instanceof DatagramPacket) {
                final DatagramPacket packet2 = (DatagramPacket)msg;
                final ByteBuf content = ((DefaultAddressedEnvelope<ByteBuf, A>)packet2).content();
                return UnixChannelUtil.isBufferCopyNeededForWrite(content) ? new DatagramPacket(this.newDirectBuffer(packet2, content), ((DefaultAddressedEnvelope<M, InetSocketAddress>)packet2).recipient()) : msg;
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
            throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EpollDatagramChannel.EXPECTED_TYPES);
        }
    }
    
    @Override
    public EpollDatagramChannelConfig config() {
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
    
    private boolean connectedRead(final EpollRecvByteAllocatorHandle allocHandle, ByteBuf byteBuf, final int maxDatagramPacketSize) throws Exception {
        try {
            final int writable = (maxDatagramPacketSize != 0) ? Math.min(byteBuf.writableBytes(), maxDatagramPacketSize) : byteBuf.writableBytes();
            allocHandle.attemptedBytesRead(writable);
            final int writerIndex = byteBuf.writerIndex();
            int localReadAmount;
            if (byteBuf.hasMemoryAddress()) {
                localReadAmount = this.socket.readAddress(byteBuf.memoryAddress(), writerIndex, writerIndex + writable);
            }
            else {
                final ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, writable);
                localReadAmount = this.socket.read(buf, buf.position(), buf.limit());
            }
            if (localReadAmount <= 0) {
                allocHandle.lastBytesRead(localReadAmount);
                return false;
            }
            byteBuf.writerIndex(writerIndex + localReadAmount);
            allocHandle.lastBytesRead((maxDatagramPacketSize <= 0) ? localReadAmount : writable);
            final DatagramPacket packet = new DatagramPacket(byteBuf, this.localAddress(), this.remoteAddress());
            allocHandle.incMessagesRead(1);
            this.pipeline().fireChannelRead((Object)packet);
            byteBuf = null;
            return true;
        }
        finally {
            if (byteBuf != null) {
                byteBuf.release();
            }
        }
    }
    
    private IOException translateForConnected(final Errors.NativeIoException e) {
        if (e.expectedErr() == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
            final PortUnreachableException error = new PortUnreachableException(e.getMessage());
            error.initCause(e);
            return error;
        }
        return e;
    }
    
    private static void addDatagramPacketToOut(final DatagramPacket packet, final RecyclableArrayList out) {
        if (packet instanceof SegmentedDatagramPacket) {
            final SegmentedDatagramPacket segmentedDatagramPacket = (SegmentedDatagramPacket)packet;
            final ByteBuf content = ((DefaultAddressedEnvelope<ByteBuf, A>)segmentedDatagramPacket).content();
            final InetSocketAddress recipient = ((DefaultAddressedEnvelope<M, InetSocketAddress>)segmentedDatagramPacket).recipient();
            final InetSocketAddress sender = ((DefaultAddressedEnvelope<M, InetSocketAddress>)segmentedDatagramPacket).sender();
            final int segmentSize = segmentedDatagramPacket.segmentSize();
            do {
                out.add(new DatagramPacket(content.readRetainedSlice(Math.min(content.readableBytes(), segmentSize)), recipient, sender));
            } while (content.isReadable());
            segmentedDatagramPacket.release();
        }
        else {
            out.add(packet);
        }
    }
    
    private static void releaseAndRecycle(final ByteBuf byteBuf, final RecyclableArrayList packetList) {
        if (byteBuf != null) {
            byteBuf.release();
        }
        if (packetList != null) {
            for (int i = 0; i < packetList.size(); ++i) {
                ReferenceCountUtil.release(packetList.get(i));
            }
            packetList.recycle();
        }
    }
    
    private static void processPacket(final ChannelPipeline pipeline, final EpollRecvByteAllocatorHandle handle, final int bytesRead, final DatagramPacket packet) {
        handle.lastBytesRead(bytesRead);
        handle.incMessagesRead(1);
        pipeline.fireChannelRead((Object)packet);
    }
    
    private static void processPacketList(final ChannelPipeline pipeline, final EpollRecvByteAllocatorHandle handle, final int bytesRead, final RecyclableArrayList packetList) {
        final int messagesRead = packetList.size();
        handle.lastBytesRead(bytesRead);
        handle.incMessagesRead(messagesRead);
        for (int i = 0; i < messagesRead; ++i) {
            pipeline.fireChannelRead(packetList.set(i, Unpooled.EMPTY_BUFFER));
        }
    }
    
    private boolean recvmsg(final EpollRecvByteAllocatorHandle allocHandle, final NativeDatagramPacketArray array, ByteBuf byteBuf) throws IOException {
        RecyclableArrayList datagramPackets = null;
        try {
            final int writable = byteBuf.writableBytes();
            final boolean added = array.addWritable(byteBuf, byteBuf.writerIndex(), writable);
            assert added;
            allocHandle.attemptedBytesRead(writable);
            final NativeDatagramPacketArray.NativeDatagramPacket msg = array.packets()[0];
            final int bytesReceived = this.socket.recvmsg(msg);
            if (bytesReceived == 0) {
                allocHandle.lastBytesRead(-1);
                return false;
            }
            byteBuf.writerIndex(bytesReceived);
            final InetSocketAddress local = this.localAddress();
            final DatagramPacket packet = msg.newDatagramPacket(byteBuf, local);
            if (!(packet instanceof SegmentedDatagramPacket)) {
                processPacket(this.pipeline(), allocHandle, bytesReceived, packet);
                byteBuf = null;
            }
            else {
                datagramPackets = RecyclableArrayList.newInstance();
                addDatagramPacketToOut(packet, datagramPackets);
                byteBuf = null;
                processPacketList(this.pipeline(), allocHandle, bytesReceived, datagramPackets);
                datagramPackets.recycle();
                datagramPackets = null;
            }
            return true;
        }
        finally {
            releaseAndRecycle(byteBuf, datagramPackets);
        }
    }
    
    private boolean scatteringRead(final EpollRecvByteAllocatorHandle allocHandle, final NativeDatagramPacketArray array, ByteBuf byteBuf, final int datagramSize, final int numDatagram) throws IOException {
        RecyclableArrayList datagramPackets = null;
        try {
            int offset = byteBuf.writerIndex();
            for (int i = 0; i < numDatagram && array.addWritable(byteBuf, offset, datagramSize); ++i, offset += datagramSize) {}
            allocHandle.attemptedBytesRead(offset - byteBuf.writerIndex());
            final NativeDatagramPacketArray.NativeDatagramPacket[] packets = array.packets();
            final int received = this.socket.recvmmsg(packets, 0, array.count());
            if (received == 0) {
                allocHandle.lastBytesRead(-1);
                return false;
            }
            final int bytesReceived = received * datagramSize;
            byteBuf.writerIndex(bytesReceived);
            final InetSocketAddress local = this.localAddress();
            if (received == 1) {
                final DatagramPacket packet = packets[0].newDatagramPacket(byteBuf, local);
                if (!(packet instanceof SegmentedDatagramPacket)) {
                    processPacket(this.pipeline(), allocHandle, datagramSize, packet);
                    byteBuf = null;
                    return true;
                }
            }
            datagramPackets = RecyclableArrayList.newInstance();
            for (int j = 0; j < received; ++j) {
                final DatagramPacket packet2 = packets[j].newDatagramPacket(byteBuf.readRetainedSlice(datagramSize), local);
                addDatagramPacketToOut(packet2, datagramPackets);
            }
            byteBuf.release();
            byteBuf = null;
            processPacketList(this.pipeline(), allocHandle, bytesReceived, datagramPackets);
            datagramPackets.recycle();
            datagramPackets = null;
            return true;
        }
        finally {
            releaseAndRecycle(byteBuf, datagramPackets);
        }
    }
    
    private NativeDatagramPacketArray cleanDatagramPacketArray() {
        return ((EpollEventLoop)this.eventLoop()).cleanDatagramPacketArray();
    }
    
    static {
        METADATA = new ChannelMetadata(true);
        EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(InetSocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
    }
    
    final class EpollDatagramChannelUnsafe extends AbstractEpollUnsafe
    {
        @Override
        void epollInReady() {
            assert EpollDatagramChannel.this.eventLoop().inEventLoop();
            final EpollDatagramChannelConfig config = EpollDatagramChannel.this.config();
            if (EpollDatagramChannel.this.shouldBreakEpollInReady(config)) {
                this.clearEpollIn0();
                return;
            }
            final EpollRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            allocHandle.edgeTriggered(EpollDatagramChannel.this.isFlagSet(Native.EPOLLET));
            final ChannelPipeline pipeline = EpollDatagramChannel.this.pipeline();
            final ByteBufAllocator allocator = config.getAllocator();
            allocHandle.reset(config);
            this.epollInBefore();
            Throwable exception = null;
            try {
                try {
                    final boolean connected = EpollDatagramChannel.this.isConnected();
                    do {
                        final int datagramSize = EpollDatagramChannel.this.config().getMaxDatagramPayloadSize();
                        final ByteBuf byteBuf = allocHandle.allocate(allocator);
                        final int numDatagram = Native.IS_SUPPORTING_RECVMMSG ? ((datagramSize == 0) ? 1 : (byteBuf.writableBytes() / datagramSize)) : 0;
                        boolean read;
                        try {
                            if (numDatagram <= 1) {
                                if (!connected || config.isUdpGro()) {
                                    read = EpollDatagramChannel.this.recvmsg(allocHandle, EpollDatagramChannel.this.cleanDatagramPacketArray(), byteBuf);
                                }
                                else {
                                    read = EpollDatagramChannel.this.connectedRead(allocHandle, byteBuf, datagramSize);
                                }
                            }
                            else {
                                read = EpollDatagramChannel.this.scatteringRead(allocHandle, EpollDatagramChannel.this.cleanDatagramPacketArray(), byteBuf, datagramSize, numDatagram);
                            }
                        }
                        catch (final Errors.NativeIoException e) {
                            if (connected) {
                                throw EpollDatagramChannel.this.translateForConnected(e);
                            }
                            throw e;
                        }
                        if (!read) {
                            break;
                        }
                        this.readPending = false;
                    } while (allocHandle.continueReading(UncheckedBooleanSupplier.TRUE_SUPPLIER));
                }
                catch (final Throwable t) {
                    exception = t;
                }
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (exception != null) {
                    pipeline.fireExceptionCaught(exception);
                }
            }
            finally {
                this.epollInFinally(config);
            }
        }
    }
}
