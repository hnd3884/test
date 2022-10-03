package io.netty.channel.kqueue;

import java.net.InetAddress;
import io.netty.channel.unix.NativeInetAddress;
import java.net.Inet6Address;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.unix.IovArray;
import java.net.InetSocketAddress;
import io.netty.channel.unix.Errors;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.unix.PeerCredentials;
import java.io.IOException;
import io.netty.channel.unix.Socket;

final class BsdSocket extends Socket
{
    private static final int APPLE_SND_LOW_AT_MAX = 131072;
    private static final int FREEBSD_SND_LOW_AT_MAX = 32768;
    static final int BSD_SND_LOW_AT_MAX;
    private static final int UNSPECIFIED_SOURCE_INTERFACE = 0;
    
    BsdSocket(final int fd) {
        super(fd);
    }
    
    void setAcceptFilter(final AcceptFilter acceptFilter) throws IOException {
        setAcceptFilter(this.intValue(), acceptFilter.filterName(), acceptFilter.filterArgs());
    }
    
    void setTcpNoPush(final boolean tcpNoPush) throws IOException {
        setTcpNoPush(this.intValue(), tcpNoPush ? 1 : 0);
    }
    
    void setSndLowAt(final int lowAt) throws IOException {
        setSndLowAt(this.intValue(), lowAt);
    }
    
    boolean isTcpNoPush() throws IOException {
        return getTcpNoPush(this.intValue()) != 0;
    }
    
    int getSndLowAt() throws IOException {
        return getSndLowAt(this.intValue());
    }
    
    AcceptFilter getAcceptFilter() throws IOException {
        final String[] result = getAcceptFilter(this.intValue());
        return (result == null) ? AcceptFilter.PLATFORM_UNSUPPORTED : new AcceptFilter(result[0], result[1]);
    }
    
    PeerCredentials getPeerCredentials() throws IOException {
        return getPeerCredentials(this.intValue());
    }
    
    long sendFile(final DefaultFileRegion src, final long baseOffset, final long offset, final long length) throws IOException {
        src.open();
        final long res = sendFile(this.intValue(), src, baseOffset, offset, length);
        if (res >= 0L) {
            return res;
        }
        return Errors.ioResult("sendfile", (int)res);
    }
    
    int connectx(final InetSocketAddress source, final InetSocketAddress destination, final IovArray data, final boolean tcpFastOpen) throws IOException {
        ObjectUtil.checkNotNull(destination, "Destination InetSocketAddress cannot be null.");
        final int flags = tcpFastOpen ? Native.CONNECT_TCP_FASTOPEN : 0;
        boolean sourceIPv6;
        byte[] sourceAddress;
        int sourceScopeId;
        int sourcePort;
        if (source == null) {
            sourceIPv6 = false;
            sourceAddress = null;
            sourceScopeId = 0;
            sourcePort = 0;
        }
        else {
            final InetAddress sourceInetAddress = source.getAddress();
            sourceIPv6 = Socket.useIpv6(this, sourceInetAddress);
            if (sourceInetAddress instanceof Inet6Address) {
                sourceAddress = sourceInetAddress.getAddress();
                sourceScopeId = ((Inet6Address)sourceInetAddress).getScopeId();
            }
            else {
                sourceScopeId = 0;
                sourceAddress = NativeInetAddress.ipv4MappedIpv6Address(sourceInetAddress.getAddress());
            }
            sourcePort = source.getPort();
        }
        final InetAddress destinationInetAddress = destination.getAddress();
        final boolean destinationIPv6 = Socket.useIpv6(this, destinationInetAddress);
        byte[] destinationAddress;
        int destinationScopeId;
        if (destinationInetAddress instanceof Inet6Address) {
            destinationAddress = destinationInetAddress.getAddress();
            destinationScopeId = ((Inet6Address)destinationInetAddress).getScopeId();
        }
        else {
            destinationScopeId = 0;
            destinationAddress = NativeInetAddress.ipv4MappedIpv6Address(destinationInetAddress.getAddress());
        }
        final int destinationPort = destination.getPort();
        long iovAddress;
        int iovCount;
        int iovDataLength;
        if (data == null || data.count() == 0) {
            iovAddress = 0L;
            iovCount = 0;
            iovDataLength = 0;
        }
        else {
            iovAddress = data.memoryAddress(0);
            iovCount = data.count();
            final long size = data.size();
            if (size > 2147483647L) {
                throw new IOException("IovArray.size() too big: " + size + " bytes.");
            }
            iovDataLength = (int)size;
        }
        final int result = connectx(this.intValue(), 0, sourceIPv6, sourceAddress, sourceScopeId, sourcePort, destinationIPv6, destinationAddress, destinationScopeId, destinationPort, flags, iovAddress, iovCount, iovDataLength);
        if (result == Errors.ERRNO_EINPROGRESS_NEGATIVE) {
            return -iovDataLength;
        }
        if (result < 0) {
            return Errors.ioResult("connectx", result);
        }
        return result;
    }
    
    public static BsdSocket newSocketStream() {
        return new BsdSocket(Socket.newSocketStream0());
    }
    
    public static BsdSocket newSocketDgram() {
        return new BsdSocket(Socket.newSocketDgram0());
    }
    
    public static BsdSocket newSocketDomain() {
        return new BsdSocket(Socket.newSocketDomain0());
    }
    
    public static BsdSocket newSocketDomainDgram() {
        return new BsdSocket(Socket.newSocketDomainDgram0());
    }
    
    private static native long sendFile(final int p0, final DefaultFileRegion p1, final long p2, final long p3, final long p4) throws IOException;
    
    private static native int connectx(final int p0, final int p1, final boolean p2, final byte[] p3, final int p4, final int p5, final boolean p6, final byte[] p7, final int p8, final int p9, final int p10, final long p11, final int p12, final int p13);
    
    private static native String[] getAcceptFilter(final int p0) throws IOException;
    
    private static native int getTcpNoPush(final int p0) throws IOException;
    
    private static native int getSndLowAt(final int p0) throws IOException;
    
    private static native PeerCredentials getPeerCredentials(final int p0) throws IOException;
    
    private static native void setAcceptFilter(final int p0, final String p1, final String p2) throws IOException;
    
    private static native void setTcpNoPush(final int p0, final int p1) throws IOException;
    
    private static native void setSndLowAt(final int p0, final int p1) throws IOException;
    
    static {
        BSD_SND_LOW_AT_MAX = Math.min(131072, 32768);
    }
}
