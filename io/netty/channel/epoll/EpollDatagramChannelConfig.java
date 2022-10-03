package io.netty.channel.epoll;

import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import io.netty.channel.ChannelException;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.MessageSizeEstimator;
import java.net.NetworkInterface;
import java.net.InetAddress;
import io.netty.channel.ChannelOption;
import java.util.Map;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.DatagramChannelConfig;

public final class EpollDatagramChannelConfig extends EpollChannelConfig implements DatagramChannelConfig
{
    private static final RecvByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR;
    private boolean activeOnOpen;
    private volatile int maxDatagramSize;
    private volatile boolean gro;
    
    EpollDatagramChannelConfig(final EpollDatagramChannel channel) {
        super(channel);
        this.setRecvByteBufAllocator(EpollDatagramChannelConfig.DEFAULT_RCVBUF_ALLOCATOR);
    }
    
    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_BROADCAST, ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.SO_REUSEADDR, ChannelOption.IP_MULTICAST_LOOP_DISABLED, ChannelOption.IP_MULTICAST_ADDR, ChannelOption.IP_MULTICAST_IF, ChannelOption.IP_MULTICAST_TTL, ChannelOption.IP_TOS, ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, EpollChannelOption.SO_REUSEPORT, EpollChannelOption.IP_FREEBIND, EpollChannelOption.IP_TRANSPARENT, EpollChannelOption.IP_RECVORIGDSTADDR, EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE, EpollChannelOption.UDP_GRO);
    }
    
    @Override
    public <T> T getOption(final ChannelOption<T> option) {
        if (option == ChannelOption.SO_BROADCAST) {
            return (T)Boolean.valueOf(this.isBroadcast());
        }
        if (option == ChannelOption.SO_RCVBUF) {
            return (T)Integer.valueOf(this.getReceiveBufferSize());
        }
        if (option == ChannelOption.SO_SNDBUF) {
            return (T)Integer.valueOf(this.getSendBufferSize());
        }
        if (option == ChannelOption.SO_REUSEADDR) {
            return (T)Boolean.valueOf(this.isReuseAddress());
        }
        if (option == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
            return (T)Boolean.valueOf(this.isLoopbackModeDisabled());
        }
        if (option == ChannelOption.IP_MULTICAST_ADDR) {
            return (T)this.getInterface();
        }
        if (option == ChannelOption.IP_MULTICAST_IF) {
            return (T)this.getNetworkInterface();
        }
        if (option == ChannelOption.IP_MULTICAST_TTL) {
            return (T)Integer.valueOf(this.getTimeToLive());
        }
        if (option == ChannelOption.IP_TOS) {
            return (T)Integer.valueOf(this.getTrafficClass());
        }
        if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
            return (T)Boolean.valueOf(this.activeOnOpen);
        }
        if (option == EpollChannelOption.SO_REUSEPORT) {
            return (T)Boolean.valueOf(this.isReusePort());
        }
        if (option == EpollChannelOption.IP_TRANSPARENT) {
            return (T)Boolean.valueOf(this.isIpTransparent());
        }
        if (option == EpollChannelOption.IP_FREEBIND) {
            return (T)Boolean.valueOf(this.isFreeBind());
        }
        if (option == EpollChannelOption.IP_RECVORIGDSTADDR) {
            return (T)Boolean.valueOf(this.isIpRecvOrigDestAddr());
        }
        if (option == EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE) {
            return (T)Integer.valueOf(this.getMaxDatagramPayloadSize());
        }
        if (option == EpollChannelOption.UDP_GRO) {
            return (T)Boolean.valueOf(this.isUdpGro());
        }
        return super.getOption(option);
    }
    
    @Override
    public <T> boolean setOption(final ChannelOption<T> option, final T value) {
        this.validate(option, value);
        if (option == ChannelOption.SO_BROADCAST) {
            this.setBroadcast((boolean)value);
        }
        else if (option == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((int)value);
        }
        else if (option == ChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((int)value);
        }
        else if (option == ChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((boolean)value);
        }
        else if (option == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
            this.setLoopbackModeDisabled((boolean)value);
        }
        else if (option == ChannelOption.IP_MULTICAST_ADDR) {
            this.setInterface((InetAddress)value);
        }
        else if (option == ChannelOption.IP_MULTICAST_IF) {
            this.setNetworkInterface((NetworkInterface)value);
        }
        else if (option == ChannelOption.IP_MULTICAST_TTL) {
            this.setTimeToLive((int)value);
        }
        else if (option == ChannelOption.IP_TOS) {
            this.setTrafficClass((int)value);
        }
        else if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
            this.setActiveOnOpen((boolean)value);
        }
        else if (option == EpollChannelOption.SO_REUSEPORT) {
            this.setReusePort((boolean)value);
        }
        else if (option == EpollChannelOption.IP_FREEBIND) {
            this.setFreeBind((boolean)value);
        }
        else if (option == EpollChannelOption.IP_TRANSPARENT) {
            this.setIpTransparent((boolean)value);
        }
        else if (option == EpollChannelOption.IP_RECVORIGDSTADDR) {
            this.setIpRecvOrigDestAddr((boolean)value);
        }
        else if (option == EpollChannelOption.MAX_DATAGRAM_PAYLOAD_SIZE) {
            this.setMaxDatagramPayloadSize((int)value);
        }
        else {
            if (option != EpollChannelOption.UDP_GRO) {
                return super.setOption(option, value);
            }
            this.setUdpGro((boolean)value);
        }
        return true;
    }
    
    private void setActiveOnOpen(final boolean activeOnOpen) {
        if (this.channel.isRegistered()) {
            throw new IllegalStateException("Can only changed before channel was registered");
        }
        this.activeOnOpen = activeOnOpen;
    }
    
    boolean getActiveOnOpen() {
        return this.activeOnOpen;
    }
    
    @Override
    public EpollDatagramChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollDatagramChannelConfig setWriteBufferLowWaterMark(final int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollDatagramChannelConfig setWriteBufferHighWaterMark(final int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }
    
    @Override
    public EpollDatagramChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }
    
    @Override
    public EpollDatagramChannelConfig setAutoClose(final boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }
    
    @Override
    public EpollDatagramChannelConfig setAutoRead(final boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }
    
    @Override
    public EpollDatagramChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }
    
    @Override
    public EpollDatagramChannelConfig setWriteSpinCount(final int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }
    
    @Override
    public EpollDatagramChannelConfig setAllocator(final ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }
    
    @Override
    public EpollDatagramChannelConfig setConnectTimeoutMillis(final int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollDatagramChannelConfig setMaxMessagesPerRead(final int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }
    
    @Override
    public int getSendBufferSize() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getSendBufferSize();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollDatagramChannelConfig setSendBufferSize(final int sendBufferSize) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setSendBufferSize(sendBufferSize);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public int getReceiveBufferSize() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getReceiveBufferSize();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollDatagramChannelConfig setReceiveBufferSize(final int receiveBufferSize) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setReceiveBufferSize(receiveBufferSize);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public int getTrafficClass() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getTrafficClass();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollDatagramChannelConfig setTrafficClass(final int trafficClass) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setTrafficClass(trafficClass);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public boolean isReuseAddress() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isReuseAddress();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollDatagramChannelConfig setReuseAddress(final boolean reuseAddress) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setReuseAddress(reuseAddress);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public boolean isBroadcast() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isBroadcast();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollDatagramChannelConfig setBroadcast(final boolean broadcast) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setBroadcast(broadcast);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public boolean isLoopbackModeDisabled() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isLoopbackModeDisabled();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public DatagramChannelConfig setLoopbackModeDisabled(final boolean loopbackModeDisabled) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setLoopbackModeDisabled(loopbackModeDisabled);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public int getTimeToLive() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getTimeToLive();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollDatagramChannelConfig setTimeToLive(final int ttl) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setTimeToLive(ttl);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public InetAddress getInterface() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getInterface();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollDatagramChannelConfig setInterface(final InetAddress interfaceAddress) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setInterface(interfaceAddress);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public NetworkInterface getNetworkInterface() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.getNetworkInterface();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollDatagramChannelConfig setNetworkInterface(final NetworkInterface networkInterface) {
        try {
            final EpollDatagramChannel datagramChannel = (EpollDatagramChannel)this.channel;
            datagramChannel.socket.setNetworkInterface(networkInterface);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollDatagramChannelConfig setEpollMode(final EpollMode mode) {
        super.setEpollMode(mode);
        return this;
    }
    
    public boolean isReusePort() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isReusePort();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public EpollDatagramChannelConfig setReusePort(final boolean reusePort) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setReusePort(reusePort);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public boolean isIpTransparent() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isIpTransparent();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public EpollDatagramChannelConfig setIpTransparent(final boolean ipTransparent) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setIpTransparent(ipTransparent);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public boolean isFreeBind() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isIpFreeBind();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public EpollDatagramChannelConfig setFreeBind(final boolean freeBind) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setIpFreeBind(freeBind);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public boolean isIpRecvOrigDestAddr() {
        try {
            return ((EpollDatagramChannel)this.channel).socket.isIpRecvOrigDestAddr();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public EpollDatagramChannelConfig setIpRecvOrigDestAddr(final boolean ipTransparent) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setIpRecvOrigDestAddr(ipTransparent);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public EpollDatagramChannelConfig setMaxDatagramPayloadSize(final int maxDatagramSize) {
        this.maxDatagramSize = ObjectUtil.checkPositiveOrZero(maxDatagramSize, "maxDatagramSize");
        return this;
    }
    
    public int getMaxDatagramPayloadSize() {
        return this.maxDatagramSize;
    }
    
    public EpollDatagramChannelConfig setUdpGro(final boolean gro) {
        try {
            ((EpollDatagramChannel)this.channel).socket.setUdpGro(gro);
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
        this.gro = gro;
        return this;
    }
    
    public boolean isUdpGro() {
        return this.gro;
    }
    
    @Override
    public EpollDatagramChannelConfig setMaxMessagesPerWrite(final int maxMessagesPerWrite) {
        super.setMaxMessagesPerWrite(maxMessagesPerWrite);
        return this;
    }
    
    static {
        DEFAULT_RCVBUF_ALLOCATOR = new FixedRecvByteBufAllocator(2048);
    }
}
