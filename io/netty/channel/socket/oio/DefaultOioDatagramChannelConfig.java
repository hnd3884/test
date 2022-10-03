package io.netty.channel.socket.oio;

import io.netty.channel.ChannelConfig;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.RecvByteBufAllocator;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.io.IOException;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import java.util.Map;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.PreferHeapByteBufAllocator;
import java.net.DatagramSocket;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DefaultDatagramChannelConfig;

final class DefaultOioDatagramChannelConfig extends DefaultDatagramChannelConfig implements OioDatagramChannelConfig
{
    DefaultOioDatagramChannelConfig(final DatagramChannel channel, final DatagramSocket javaSocket) {
        super(channel, javaSocket);
        this.setAllocator(new PreferHeapByteBufAllocator(this.getAllocator()));
    }
    
    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_TIMEOUT);
    }
    
    @Override
    public <T> T getOption(final ChannelOption<T> option) {
        if (option == ChannelOption.SO_TIMEOUT) {
            return (T)Integer.valueOf(this.getSoTimeout());
        }
        return super.getOption(option);
    }
    
    @Override
    public <T> boolean setOption(final ChannelOption<T> option, final T value) {
        this.validate(option, value);
        if (option == ChannelOption.SO_TIMEOUT) {
            this.setSoTimeout((int)value);
            return true;
        }
        return super.setOption(option, value);
    }
    
    @Override
    public OioDatagramChannelConfig setSoTimeout(final int timeout) {
        try {
            this.javaSocket().setSoTimeout(timeout);
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
        return this;
    }
    
    @Override
    public int getSoTimeout() {
        try {
            return this.javaSocket().getSoTimeout();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public OioDatagramChannelConfig setBroadcast(final boolean broadcast) {
        super.setBroadcast(broadcast);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setInterface(final InetAddress interfaceAddress) {
        super.setInterface(interfaceAddress);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setLoopbackModeDisabled(final boolean loopbackModeDisabled) {
        super.setLoopbackModeDisabled(loopbackModeDisabled);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setNetworkInterface(final NetworkInterface networkInterface) {
        super.setNetworkInterface(networkInterface);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setReuseAddress(final boolean reuseAddress) {
        super.setReuseAddress(reuseAddress);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setReceiveBufferSize(final int receiveBufferSize) {
        super.setReceiveBufferSize(receiveBufferSize);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setSendBufferSize(final int sendBufferSize) {
        super.setSendBufferSize(sendBufferSize);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setTimeToLive(final int ttl) {
        super.setTimeToLive(ttl);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setTrafficClass(final int trafficClass) {
        super.setTrafficClass(trafficClass);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setWriteSpinCount(final int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setConnectTimeoutMillis(final int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setMaxMessagesPerRead(final int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setAllocator(final ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setAutoRead(final boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setAutoClose(final boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setWriteBufferHighWaterMark(final int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setWriteBufferLowWaterMark(final int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }
    
    @Override
    public OioDatagramChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
}
