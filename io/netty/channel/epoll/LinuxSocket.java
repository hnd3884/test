package io.netty.channel.epoll;

import io.netty.channel.ChannelException;
import io.netty.channel.unix.Errors;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.unix.PeerCredentials;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import io.netty.util.internal.PlatformDependent;
import java.util.Enumeration;
import io.netty.util.internal.SocketUtils;
import java.net.NetworkInterface;
import io.netty.channel.unix.NativeInetAddress;
import java.io.IOException;
import io.netty.channel.socket.InternetProtocolFamily;
import java.net.InetAddress;
import io.netty.channel.unix.Socket;

final class LinuxSocket extends Socket
{
    static final InetAddress INET6_ANY;
    private static final InetAddress INET_ANY;
    private static final long MAX_UINT32_T = 4294967295L;
    
    LinuxSocket(final int fd) {
        super(fd);
    }
    
    InternetProtocolFamily family() {
        return this.ipv6 ? InternetProtocolFamily.IPv6 : InternetProtocolFamily.IPv4;
    }
    
    int sendmmsg(final NativeDatagramPacketArray.NativeDatagramPacket[] msgs, final int offset, final int len) throws IOException {
        return Native.sendmmsg(this.intValue(), this.ipv6, msgs, offset, len);
    }
    
    int recvmmsg(final NativeDatagramPacketArray.NativeDatagramPacket[] msgs, final int offset, final int len) throws IOException {
        return Native.recvmmsg(this.intValue(), this.ipv6, msgs, offset, len);
    }
    
    int recvmsg(final NativeDatagramPacketArray.NativeDatagramPacket msg) throws IOException {
        return Native.recvmsg(this.intValue(), this.ipv6, msg);
    }
    
    void setTimeToLive(final int ttl) throws IOException {
        setTimeToLive(this.intValue(), ttl);
    }
    
    void setInterface(final InetAddress address) throws IOException {
        final NativeInetAddress a = NativeInetAddress.newInstance(address);
        setInterface(this.intValue(), this.ipv6, a.address(), a.scopeId(), interfaceIndex(address));
    }
    
    void setNetworkInterface(final NetworkInterface netInterface) throws IOException {
        final InetAddress address = deriveInetAddress(netInterface, this.family() == InternetProtocolFamily.IPv6);
        if (address.equals((this.family() == InternetProtocolFamily.IPv4) ? LinuxSocket.INET_ANY : LinuxSocket.INET6_ANY)) {
            throw new IOException("NetworkInterface does not support " + this.family());
        }
        final NativeInetAddress nativeAddress = NativeInetAddress.newInstance(address);
        setInterface(this.intValue(), this.ipv6, nativeAddress.address(), nativeAddress.scopeId(), interfaceIndex(netInterface));
    }
    
    InetAddress getInterface() throws IOException {
        final NetworkInterface inf = this.getNetworkInterface();
        if (inf != null) {
            final Enumeration<InetAddress> addresses = SocketUtils.addressesFromNetworkInterface(inf);
            if (addresses.hasMoreElements()) {
                return addresses.nextElement();
            }
        }
        return null;
    }
    
    NetworkInterface getNetworkInterface() throws IOException {
        final int ret = getInterface(this.intValue(), this.ipv6);
        if (this.ipv6) {
            return (PlatformDependent.javaVersion() >= 7) ? NetworkInterface.getByIndex(ret) : null;
        }
        final InetAddress address = inetAddress(ret);
        return (address != null) ? NetworkInterface.getByInetAddress(address) : null;
    }
    
    private static InetAddress inetAddress(final int value) {
        final byte[] var1 = { (byte)(value >>> 24 & 0xFF), (byte)(value >>> 16 & 0xFF), (byte)(value >>> 8 & 0xFF), (byte)(value & 0xFF) };
        try {
            return InetAddress.getByAddress(var1);
        }
        catch (final UnknownHostException ignore) {
            return null;
        }
    }
    
    void joinGroup(final InetAddress group, final NetworkInterface netInterface, final InetAddress source) throws IOException {
        final NativeInetAddress g = NativeInetAddress.newInstance(group);
        final boolean isIpv6 = group instanceof Inet6Address;
        final NativeInetAddress i = NativeInetAddress.newInstance(deriveInetAddress(netInterface, isIpv6));
        if (source != null) {
            if (source.getClass() != group.getClass()) {
                throw new IllegalArgumentException("Source address is different type to group");
            }
            final NativeInetAddress s = NativeInetAddress.newInstance(source);
            joinSsmGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), interfaceIndex(netInterface), s.address());
        }
        else {
            joinGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), interfaceIndex(netInterface));
        }
    }
    
    void leaveGroup(final InetAddress group, final NetworkInterface netInterface, final InetAddress source) throws IOException {
        final NativeInetAddress g = NativeInetAddress.newInstance(group);
        final boolean isIpv6 = group instanceof Inet6Address;
        final NativeInetAddress i = NativeInetAddress.newInstance(deriveInetAddress(netInterface, isIpv6));
        if (source != null) {
            if (source.getClass() != group.getClass()) {
                throw new IllegalArgumentException("Source address is different type to group");
            }
            final NativeInetAddress s = NativeInetAddress.newInstance(source);
            leaveSsmGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), interfaceIndex(netInterface), s.address());
        }
        else {
            leaveGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), interfaceIndex(netInterface));
        }
    }
    
    private static int interfaceIndex(final NetworkInterface networkInterface) {
        return (PlatformDependent.javaVersion() >= 7) ? networkInterface.getIndex() : -1;
    }
    
    private static int interfaceIndex(final InetAddress address) throws IOException {
        if (PlatformDependent.javaVersion() >= 7) {
            final NetworkInterface iface = NetworkInterface.getByInetAddress(address);
            if (iface != null) {
                return iface.getIndex();
            }
        }
        return -1;
    }
    
    void setTcpDeferAccept(final int deferAccept) throws IOException {
        setTcpDeferAccept(this.intValue(), deferAccept);
    }
    
    void setTcpQuickAck(final boolean quickAck) throws IOException {
        setTcpQuickAck(this.intValue(), quickAck ? 1 : 0);
    }
    
    void setTcpCork(final boolean tcpCork) throws IOException {
        setTcpCork(this.intValue(), tcpCork ? 1 : 0);
    }
    
    void setSoBusyPoll(final int loopMicros) throws IOException {
        setSoBusyPoll(this.intValue(), loopMicros);
    }
    
    void setTcpNotSentLowAt(final long tcpNotSentLowAt) throws IOException {
        if (tcpNotSentLowAt < 0L || tcpNotSentLowAt > 4294967295L) {
            throw new IllegalArgumentException("tcpNotSentLowAt must be a uint32_t");
        }
        setTcpNotSentLowAt(this.intValue(), (int)tcpNotSentLowAt);
    }
    
    void setTcpFastOpen(final int tcpFastopenBacklog) throws IOException {
        setTcpFastOpen(this.intValue(), tcpFastopenBacklog);
    }
    
    void setTcpKeepIdle(final int seconds) throws IOException {
        setTcpKeepIdle(this.intValue(), seconds);
    }
    
    void setTcpKeepIntvl(final int seconds) throws IOException {
        setTcpKeepIntvl(this.intValue(), seconds);
    }
    
    void setTcpKeepCnt(final int probes) throws IOException {
        setTcpKeepCnt(this.intValue(), probes);
    }
    
    void setTcpUserTimeout(final int milliseconds) throws IOException {
        setTcpUserTimeout(this.intValue(), milliseconds);
    }
    
    void setIpFreeBind(final boolean enabled) throws IOException {
        setIpFreeBind(this.intValue(), enabled ? 1 : 0);
    }
    
    void setIpTransparent(final boolean enabled) throws IOException {
        setIpTransparent(this.intValue(), enabled ? 1 : 0);
    }
    
    void setIpRecvOrigDestAddr(final boolean enabled) throws IOException {
        setIpRecvOrigDestAddr(this.intValue(), enabled ? 1 : 0);
    }
    
    int getTimeToLive() throws IOException {
        return getTimeToLive(this.intValue());
    }
    
    void getTcpInfo(final EpollTcpInfo info) throws IOException {
        getTcpInfo(this.intValue(), info.info);
    }
    
    void setTcpMd5Sig(final InetAddress address, final byte[] key) throws IOException {
        final NativeInetAddress a = NativeInetAddress.newInstance(address);
        setTcpMd5Sig(this.intValue(), this.ipv6, a.address(), a.scopeId(), key);
    }
    
    boolean isTcpCork() throws IOException {
        return isTcpCork(this.intValue()) != 0;
    }
    
    int getSoBusyPoll() throws IOException {
        return getSoBusyPoll(this.intValue());
    }
    
    int getTcpDeferAccept() throws IOException {
        return getTcpDeferAccept(this.intValue());
    }
    
    boolean isTcpQuickAck() throws IOException {
        return isTcpQuickAck(this.intValue()) != 0;
    }
    
    long getTcpNotSentLowAt() throws IOException {
        return (long)getTcpNotSentLowAt(this.intValue()) & 0xFFFFFFFFL;
    }
    
    int getTcpKeepIdle() throws IOException {
        return getTcpKeepIdle(this.intValue());
    }
    
    int getTcpKeepIntvl() throws IOException {
        return getTcpKeepIntvl(this.intValue());
    }
    
    int getTcpKeepCnt() throws IOException {
        return getTcpKeepCnt(this.intValue());
    }
    
    int getTcpUserTimeout() throws IOException {
        return getTcpUserTimeout(this.intValue());
    }
    
    boolean isIpFreeBind() throws IOException {
        return isIpFreeBind(this.intValue()) != 0;
    }
    
    boolean isIpTransparent() throws IOException {
        return isIpTransparent(this.intValue()) != 0;
    }
    
    boolean isIpRecvOrigDestAddr() throws IOException {
        return isIpRecvOrigDestAddr(this.intValue()) != 0;
    }
    
    PeerCredentials getPeerCredentials() throws IOException {
        return getPeerCredentials(this.intValue());
    }
    
    boolean isLoopbackModeDisabled() throws IOException {
        return getIpMulticastLoop(this.intValue(), this.ipv6) == 0;
    }
    
    void setLoopbackModeDisabled(final boolean loopbackModeDisabled) throws IOException {
        setIpMulticastLoop(this.intValue(), this.ipv6, loopbackModeDisabled ? 0 : 1);
    }
    
    boolean isUdpGro() throws IOException {
        return isUdpGro(this.intValue()) != 0;
    }
    
    void setUdpGro(final boolean gro) throws IOException {
        setUdpGro(this.intValue(), gro ? 1 : 0);
    }
    
    long sendFile(final DefaultFileRegion src, final long baseOffset, final long offset, final long length) throws IOException {
        src.open();
        final long res = sendFile(this.intValue(), src, baseOffset, offset, length);
        if (res >= 0L) {
            return res;
        }
        return Errors.ioResult("sendfile", (int)res);
    }
    
    private static InetAddress deriveInetAddress(final NetworkInterface netInterface, final boolean ipv6) {
        final InetAddress ipAny = ipv6 ? LinuxSocket.INET6_ANY : LinuxSocket.INET_ANY;
        if (netInterface != null) {
            final Enumeration<InetAddress> ias = netInterface.getInetAddresses();
            while (ias.hasMoreElements()) {
                final InetAddress ia = ias.nextElement();
                final boolean isV6 = ia instanceof Inet6Address;
                if (isV6 == ipv6) {
                    return ia;
                }
            }
        }
        return ipAny;
    }
    
    public static LinuxSocket newSocketStream(final boolean ipv6) {
        return new LinuxSocket(Socket.newSocketStream0(ipv6));
    }
    
    public static LinuxSocket newSocketStream() {
        return newSocketStream(Socket.isIPv6Preferred());
    }
    
    public static LinuxSocket newSocketDgram(final boolean ipv6) {
        return new LinuxSocket(Socket.newSocketDgram0(ipv6));
    }
    
    public static LinuxSocket newSocketDgram() {
        return newSocketDgram(Socket.isIPv6Preferred());
    }
    
    public static LinuxSocket newSocketDomain() {
        return new LinuxSocket(Socket.newSocketDomain0());
    }
    
    public static LinuxSocket newSocketDomainDgram() {
        return new LinuxSocket(Socket.newSocketDomainDgram0());
    }
    
    private static InetAddress unsafeInetAddrByName(final String inetName) {
        try {
            return InetAddress.getByName(inetName);
        }
        catch (final UnknownHostException uhe) {
            throw new ChannelException(uhe);
        }
    }
    
    private static native void joinGroup(final int p0, final boolean p1, final byte[] p2, final byte[] p3, final int p4, final int p5) throws IOException;
    
    private static native void joinSsmGroup(final int p0, final boolean p1, final byte[] p2, final byte[] p3, final int p4, final int p5, final byte[] p6) throws IOException;
    
    private static native void leaveGroup(final int p0, final boolean p1, final byte[] p2, final byte[] p3, final int p4, final int p5) throws IOException;
    
    private static native void leaveSsmGroup(final int p0, final boolean p1, final byte[] p2, final byte[] p3, final int p4, final int p5, final byte[] p6) throws IOException;
    
    private static native long sendFile(final int p0, final DefaultFileRegion p1, final long p2, final long p3, final long p4) throws IOException;
    
    private static native int getTcpDeferAccept(final int p0) throws IOException;
    
    private static native int isTcpQuickAck(final int p0) throws IOException;
    
    private static native int isTcpCork(final int p0) throws IOException;
    
    private static native int getSoBusyPoll(final int p0) throws IOException;
    
    private static native int getTcpNotSentLowAt(final int p0) throws IOException;
    
    private static native int getTcpKeepIdle(final int p0) throws IOException;
    
    private static native int getTcpKeepIntvl(final int p0) throws IOException;
    
    private static native int getTcpKeepCnt(final int p0) throws IOException;
    
    private static native int getTcpUserTimeout(final int p0) throws IOException;
    
    private static native int getTimeToLive(final int p0) throws IOException;
    
    private static native int isIpFreeBind(final int p0) throws IOException;
    
    private static native int isIpTransparent(final int p0) throws IOException;
    
    private static native int isIpRecvOrigDestAddr(final int p0) throws IOException;
    
    private static native void getTcpInfo(final int p0, final long[] p1) throws IOException;
    
    private static native PeerCredentials getPeerCredentials(final int p0) throws IOException;
    
    private static native void setTcpDeferAccept(final int p0, final int p1) throws IOException;
    
    private static native void setTcpQuickAck(final int p0, final int p1) throws IOException;
    
    private static native void setTcpCork(final int p0, final int p1) throws IOException;
    
    private static native void setSoBusyPoll(final int p0, final int p1) throws IOException;
    
    private static native void setTcpNotSentLowAt(final int p0, final int p1) throws IOException;
    
    private static native void setTcpFastOpen(final int p0, final int p1) throws IOException;
    
    private static native void setTcpKeepIdle(final int p0, final int p1) throws IOException;
    
    private static native void setTcpKeepIntvl(final int p0, final int p1) throws IOException;
    
    private static native void setTcpKeepCnt(final int p0, final int p1) throws IOException;
    
    private static native void setTcpUserTimeout(final int p0, final int p1) throws IOException;
    
    private static native void setIpFreeBind(final int p0, final int p1) throws IOException;
    
    private static native void setIpTransparent(final int p0, final int p1) throws IOException;
    
    private static native void setIpRecvOrigDestAddr(final int p0, final int p1) throws IOException;
    
    private static native void setTcpMd5Sig(final int p0, final boolean p1, final byte[] p2, final int p3, final byte[] p4) throws IOException;
    
    private static native void setInterface(final int p0, final boolean p1, final byte[] p2, final int p3, final int p4) throws IOException;
    
    private static native int getInterface(final int p0, final boolean p1);
    
    private static native int getIpMulticastLoop(final int p0, final boolean p1) throws IOException;
    
    private static native void setIpMulticastLoop(final int p0, final boolean p1, final int p2) throws IOException;
    
    private static native void setTimeToLive(final int p0, final int p1) throws IOException;
    
    private static native int isUdpGro(final int p0) throws IOException;
    
    private static native void setUdpGro(final int p0, final int p1) throws IOException;
    
    static {
        INET6_ANY = unsafeInetAddrByName("::");
        INET_ANY = unsafeInetAddrByName("0.0.0.0");
    }
}
