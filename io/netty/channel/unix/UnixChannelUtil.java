package io.netty.channel.unix;

import java.net.UnknownHostException;
import java.net.InetAddress;
import io.netty.util.internal.PlatformDependent;
import java.net.InetSocketAddress;
import io.netty.buffer.ByteBuf;

public final class UnixChannelUtil
{
    private UnixChannelUtil() {
    }
    
    public static boolean isBufferCopyNeededForWrite(final ByteBuf byteBuf) {
        return isBufferCopyNeededForWrite(byteBuf, Limits.IOV_MAX);
    }
    
    static boolean isBufferCopyNeededForWrite(final ByteBuf byteBuf, final int iovMax) {
        return !byteBuf.hasMemoryAddress() && (!byteBuf.isDirect() || byteBuf.nioBufferCount() > iovMax);
    }
    
    public static InetSocketAddress computeRemoteAddr(final InetSocketAddress remoteAddr, final InetSocketAddress osRemoteAddr) {
        if (osRemoteAddr != null) {
            if (PlatformDependent.javaVersion() >= 7) {
                try {
                    return new InetSocketAddress(InetAddress.getByAddress(remoteAddr.getHostString(), osRemoteAddr.getAddress().getAddress()), osRemoteAddr.getPort());
                }
                catch (final UnknownHostException ex) {}
            }
            return osRemoteAddr;
        }
        return remoteAddr;
    }
}
