package io.netty.channel.socket.oio;

import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.buffer.ByteBufAllocator;
import java.net.NetworkInterface;
import java.net.InetAddress;
import io.netty.channel.socket.DatagramChannelConfig;

@Deprecated
public interface OioDatagramChannelConfig extends DatagramChannelConfig
{
    OioDatagramChannelConfig setSoTimeout(final int p0);
    
    int getSoTimeout();
    
    OioDatagramChannelConfig setSendBufferSize(final int p0);
    
    OioDatagramChannelConfig setReceiveBufferSize(final int p0);
    
    OioDatagramChannelConfig setTrafficClass(final int p0);
    
    OioDatagramChannelConfig setReuseAddress(final boolean p0);
    
    OioDatagramChannelConfig setBroadcast(final boolean p0);
    
    OioDatagramChannelConfig setLoopbackModeDisabled(final boolean p0);
    
    OioDatagramChannelConfig setTimeToLive(final int p0);
    
    OioDatagramChannelConfig setInterface(final InetAddress p0);
    
    OioDatagramChannelConfig setNetworkInterface(final NetworkInterface p0);
    
    OioDatagramChannelConfig setMaxMessagesPerRead(final int p0);
    
    OioDatagramChannelConfig setWriteSpinCount(final int p0);
    
    OioDatagramChannelConfig setConnectTimeoutMillis(final int p0);
    
    OioDatagramChannelConfig setAllocator(final ByteBufAllocator p0);
    
    OioDatagramChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator p0);
    
    OioDatagramChannelConfig setAutoRead(final boolean p0);
    
    OioDatagramChannelConfig setAutoClose(final boolean p0);
    
    OioDatagramChannelConfig setMessageSizeEstimator(final MessageSizeEstimator p0);
    
    OioDatagramChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark p0);
    
    OioDatagramChannelConfig setWriteBufferHighWaterMark(final int p0);
    
    OioDatagramChannelConfig setWriteBufferLowWaterMark(final int p0);
}
