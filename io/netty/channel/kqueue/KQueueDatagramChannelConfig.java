package io.netty.channel.kqueue;

import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.MessageSizeEstimator;
import java.io.IOException;
import io.netty.channel.ChannelException;
import java.net.NetworkInterface;
import java.net.InetAddress;
import io.netty.channel.unix.UnixChannelOption;
import io.netty.channel.ChannelOption;
import java.util.Map;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.DatagramChannelConfig;

public final class KQueueDatagramChannelConfig extends KQueueChannelConfig implements DatagramChannelConfig
{
    private static final RecvByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR;
    private boolean activeOnOpen;
    
    KQueueDatagramChannelConfig(final KQueueDatagramChannel channel) {
        super(channel);
        this.setRecvByteBufAllocator(KQueueDatagramChannelConfig.DEFAULT_RCVBUF_ALLOCATOR);
    }
    
    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_BROADCAST, ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.SO_REUSEADDR, ChannelOption.IP_MULTICAST_LOOP_DISABLED, ChannelOption.IP_MULTICAST_ADDR, ChannelOption.IP_MULTICAST_IF, ChannelOption.IP_MULTICAST_TTL, ChannelOption.IP_TOS, ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, UnixChannelOption.SO_REUSEPORT);
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
        if (option == UnixChannelOption.SO_REUSEPORT) {
            return (T)Boolean.valueOf(this.isReusePort());
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
        else {
            if (option != UnixChannelOption.SO_REUSEPORT) {
                return super.setOption(option, value);
            }
            this.setReusePort((boolean)value);
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
    
    public boolean isReusePort() {
        try {
            return ((KQueueDatagramChannel)this.channel).socket.isReusePort();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public KQueueDatagramChannelConfig setReusePort(final boolean reusePort) {
        try {
            ((KQueueDatagramChannel)this.channel).socket.setReusePort(reusePort);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public KQueueDatagramChannelConfig setRcvAllocTransportProvidesGuess(final boolean transportProvidesGuess) {
        super.setRcvAllocTransportProvidesGuess(transportProvidesGuess);
        return this;
    }
    
    @Override
    public KQueueDatagramChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueDatagramChannelConfig setWriteBufferLowWaterMark(final int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueDatagramChannelConfig setWriteBufferHighWaterMark(final int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }
    
    @Override
    public KQueueDatagramChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }
    
    @Override
    public KQueueDatagramChannelConfig setAutoClose(final boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }
    
    @Override
    public KQueueDatagramChannelConfig setAutoRead(final boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }
    
    @Override
    public KQueueDatagramChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }
    
    @Override
    public KQueueDatagramChannelConfig setWriteSpinCount(final int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }
    
    @Override
    public KQueueDatagramChannelConfig setAllocator(final ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }
    
    @Override
    public KQueueDatagramChannelConfig setConnectTimeoutMillis(final int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueDatagramChannelConfig setMaxMessagesPerRead(final int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }
    
    @Override
    public int getSendBufferSize() {
        try {
            return ((KQueueDatagramChannel)this.channel).socket.getSendBufferSize();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public KQueueDatagramChannelConfig setSendBufferSize(final int sendBufferSize) {
        try {
            ((KQueueDatagramChannel)this.channel).socket.setSendBufferSize(sendBufferSize);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public int getReceiveBufferSize() {
        try {
            return ((KQueueDatagramChannel)this.channel).socket.getReceiveBufferSize();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public KQueueDatagramChannelConfig setReceiveBufferSize(final int receiveBufferSize) {
        try {
            ((KQueueDatagramChannel)this.channel).socket.setReceiveBufferSize(receiveBufferSize);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public int getTrafficClass() {
        try {
            return ((KQueueDatagramChannel)this.channel).socket.getTrafficClass();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public KQueueDatagramChannelConfig setTrafficClass(final int trafficClass) {
        try {
            ((KQueueDatagramChannel)this.channel).socket.setTrafficClass(trafficClass);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public boolean isReuseAddress() {
        try {
            return ((KQueueDatagramChannel)this.channel).socket.isReuseAddress();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public KQueueDatagramChannelConfig setReuseAddress(final boolean reuseAddress) {
        try {
            ((KQueueDatagramChannel)this.channel).socket.setReuseAddress(reuseAddress);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public boolean isBroadcast() {
        try {
            return ((KQueueDatagramChannel)this.channel).socket.isBroadcast();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public KQueueDatagramChannelConfig setBroadcast(final boolean broadcast) {
        try {
            ((KQueueDatagramChannel)this.channel).socket.setBroadcast(broadcast);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public boolean isLoopbackModeDisabled() {
        return false;
    }
    
    @Override
    public DatagramChannelConfig setLoopbackModeDisabled(final boolean loopbackModeDisabled) {
        throw new UnsupportedOperationException("Multicast not supported");
    }
    
    @Override
    public int getTimeToLive() {
        return -1;
    }
    
    @Override
    public KQueueDatagramChannelConfig setTimeToLive(final int ttl) {
        throw new UnsupportedOperationException("Multicast not supported");
    }
    
    @Override
    public InetAddress getInterface() {
        return null;
    }
    
    @Override
    public KQueueDatagramChannelConfig setInterface(final InetAddress interfaceAddress) {
        throw new UnsupportedOperationException("Multicast not supported");
    }
    
    @Override
    public NetworkInterface getNetworkInterface() {
        return null;
    }
    
    @Override
    public KQueueDatagramChannelConfig setNetworkInterface(final NetworkInterface networkInterface) {
        throw new UnsupportedOperationException("Multicast not supported");
    }
    
    @Override
    public KQueueDatagramChannelConfig setMaxMessagesPerWrite(final int maxMessagesPerWrite) {
        super.setMaxMessagesPerWrite(maxMessagesPerWrite);
        return this;
    }
    
    static {
        DEFAULT_RCVBUF_ALLOCATOR = new FixedRecvByteBufAllocator(2048);
    }
}
