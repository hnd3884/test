package io.netty.channel.unix;

import io.netty.channel.ChannelException;
import io.netty.util.NetUtil;
import io.netty.util.CharsetUtil;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.PortUnreachableException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class Socket extends FileDescriptor
{
    @Deprecated
    public static final int UDS_SUN_PATH_SIZE = 100;
    protected final boolean ipv6;
    private static final AtomicBoolean INITIALIZED;
    
    public Socket(final int fd) {
        super(fd);
        this.ipv6 = isIPv6(fd);
    }
    
    private boolean useIpv6(final InetAddress address) {
        return useIpv6(this, address);
    }
    
    protected static boolean useIpv6(final Socket socket, final InetAddress address) {
        return socket.ipv6 || address instanceof Inet6Address;
    }
    
    public final void shutdown() throws IOException {
        this.shutdown(true, true);
    }
    
    public final void shutdown(final boolean read, final boolean write) throws IOException {
        while (true) {
            final int oldState = this.state;
            if (FileDescriptor.isClosed(oldState)) {
                throw new ClosedChannelException();
            }
            int newState = oldState;
            if (read && !FileDescriptor.isInputShutdown(newState)) {
                newState = FileDescriptor.inputShutdown(newState);
            }
            if (write && !FileDescriptor.isOutputShutdown(newState)) {
                newState = FileDescriptor.outputShutdown(newState);
            }
            if (newState == oldState) {
                return;
            }
            if (this.casState(oldState, newState)) {
                final int res = shutdown(this.fd, read, write);
                if (res < 0) {
                    Errors.ioResult("shutdown", res);
                }
            }
        }
    }
    
    public final boolean isShutdown() {
        final int state = this.state;
        return FileDescriptor.isInputShutdown(state) && FileDescriptor.isOutputShutdown(state);
    }
    
    public final boolean isInputShutdown() {
        return FileDescriptor.isInputShutdown(this.state);
    }
    
    public final boolean isOutputShutdown() {
        return FileDescriptor.isOutputShutdown(this.state);
    }
    
    public final int sendTo(final ByteBuffer buf, final int pos, final int limit, final InetAddress addr, final int port) throws IOException {
        return this.sendTo(buf, pos, limit, addr, port, false);
    }
    
    public final int sendTo(final ByteBuffer buf, final int pos, final int limit, final InetAddress addr, final int port, final boolean fastOpen) throws IOException {
        byte[] address;
        int scopeId;
        if (addr instanceof Inet6Address) {
            address = addr.getAddress();
            scopeId = ((Inet6Address)addr).getScopeId();
        }
        else {
            scopeId = 0;
            address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
        }
        final int flags = fastOpen ? msgFastopen() : 0;
        final int res = sendTo(this.fd, this.useIpv6(addr), buf, pos, limit, address, scopeId, port, flags);
        if (res >= 0) {
            return res;
        }
        if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE && fastOpen) {
            return 0;
        }
        if (res == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
            throw new PortUnreachableException("sendTo failed");
        }
        return Errors.ioResult("sendTo", res);
    }
    
    public final int sendToDomainSocket(final ByteBuffer buf, final int pos, final int limit, final byte[] path) throws IOException {
        final int res = sendToDomainSocket(this.fd, buf, pos, limit, path);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("sendToDomainSocket", res);
    }
    
    public final int sendToAddress(final long memoryAddress, final int pos, final int limit, final InetAddress addr, final int port) throws IOException {
        return this.sendToAddress(memoryAddress, pos, limit, addr, port, false);
    }
    
    public final int sendToAddress(final long memoryAddress, final int pos, final int limit, final InetAddress addr, final int port, final boolean fastOpen) throws IOException {
        byte[] address;
        int scopeId;
        if (addr instanceof Inet6Address) {
            address = addr.getAddress();
            scopeId = ((Inet6Address)addr).getScopeId();
        }
        else {
            scopeId = 0;
            address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
        }
        final int flags = fastOpen ? msgFastopen() : 0;
        final int res = sendToAddress(this.fd, this.useIpv6(addr), memoryAddress, pos, limit, address, scopeId, port, flags);
        if (res >= 0) {
            return res;
        }
        if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE && fastOpen) {
            return 0;
        }
        if (res == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
            throw new PortUnreachableException("sendToAddress failed");
        }
        return Errors.ioResult("sendToAddress", res);
    }
    
    public final int sendToAddressDomainSocket(final long memoryAddress, final int pos, final int limit, final byte[] path) throws IOException {
        final int res = sendToAddressDomainSocket(this.fd, memoryAddress, pos, limit, path);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("sendToAddressDomainSocket", res);
    }
    
    public final int sendToAddresses(final long memoryAddress, final int length, final InetAddress addr, final int port) throws IOException {
        return this.sendToAddresses(memoryAddress, length, addr, port, false);
    }
    
    public final int sendToAddresses(final long memoryAddress, final int length, final InetAddress addr, final int port, final boolean fastOpen) throws IOException {
        byte[] address;
        int scopeId;
        if (addr instanceof Inet6Address) {
            address = addr.getAddress();
            scopeId = ((Inet6Address)addr).getScopeId();
        }
        else {
            scopeId = 0;
            address = NativeInetAddress.ipv4MappedIpv6Address(addr.getAddress());
        }
        final int flags = fastOpen ? msgFastopen() : 0;
        final int res = sendToAddresses(this.fd, this.useIpv6(addr), memoryAddress, length, address, scopeId, port, flags);
        if (res >= 0) {
            return res;
        }
        if (res == Errors.ERRNO_EINPROGRESS_NEGATIVE && fastOpen) {
            return 0;
        }
        if (res == Errors.ERROR_ECONNREFUSED_NEGATIVE) {
            throw new PortUnreachableException("sendToAddresses failed");
        }
        return Errors.ioResult("sendToAddresses", res);
    }
    
    public final int sendToAddressesDomainSocket(final long memoryAddress, final int length, final byte[] path) throws IOException {
        final int res = sendToAddressesDomainSocket(this.fd, memoryAddress, length, path);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("sendToAddressesDomainSocket", res);
    }
    
    public final DatagramSocketAddress recvFrom(final ByteBuffer buf, final int pos, final int limit) throws IOException {
        return recvFrom(this.fd, buf, pos, limit);
    }
    
    public final DatagramSocketAddress recvFromAddress(final long memoryAddress, final int pos, final int limit) throws IOException {
        return recvFromAddress(this.fd, memoryAddress, pos, limit);
    }
    
    public final DomainDatagramSocketAddress recvFromDomainSocket(final ByteBuffer buf, final int pos, final int limit) throws IOException {
        return recvFromDomainSocket(this.fd, buf, pos, limit);
    }
    
    public final DomainDatagramSocketAddress recvFromAddressDomainSocket(final long memoryAddress, final int pos, final int limit) throws IOException {
        return recvFromAddressDomainSocket(this.fd, memoryAddress, pos, limit);
    }
    
    public final int recvFd() throws IOException {
        final int res = recvFd(this.fd);
        if (res > 0) {
            return res;
        }
        if (res == 0) {
            return -1;
        }
        if (res == Errors.ERRNO_EAGAIN_NEGATIVE || res == Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
            return 0;
        }
        throw Errors.newIOException("recvFd", res);
    }
    
    public final int sendFd(final int fdToSend) throws IOException {
        final int res = sendFd(this.fd, fdToSend);
        if (res >= 0) {
            return res;
        }
        if (res == Errors.ERRNO_EAGAIN_NEGATIVE || res == Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
            return -1;
        }
        throw Errors.newIOException("sendFd", res);
    }
    
    public final boolean connect(final SocketAddress socketAddress) throws IOException {
        int res;
        if (socketAddress instanceof InetSocketAddress) {
            final InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
            final InetAddress inetAddress = inetSocketAddress.getAddress();
            final NativeInetAddress address = NativeInetAddress.newInstance(inetAddress);
            res = connect(this.fd, this.useIpv6(inetAddress), address.address, address.scopeId, inetSocketAddress.getPort());
        }
        else {
            if (!(socketAddress instanceof DomainSocketAddress)) {
                throw new Error("Unexpected SocketAddress implementation " + socketAddress);
            }
            final DomainSocketAddress unixDomainSocketAddress = (DomainSocketAddress)socketAddress;
            res = connectDomainSocket(this.fd, unixDomainSocketAddress.path().getBytes(CharsetUtil.UTF_8));
        }
        return res >= 0 || Errors.handleConnectErrno("connect", res);
    }
    
    public final boolean finishConnect() throws IOException {
        final int res = finishConnect(this.fd);
        return res >= 0 || Errors.handleConnectErrno("finishConnect", res);
    }
    
    public final void disconnect() throws IOException {
        final int res = disconnect(this.fd, this.ipv6);
        if (res < 0) {
            Errors.handleConnectErrno("disconnect", res);
        }
    }
    
    public final void bind(final SocketAddress socketAddress) throws IOException {
        if (socketAddress instanceof InetSocketAddress) {
            final InetSocketAddress addr = (InetSocketAddress)socketAddress;
            final InetAddress inetAddress = addr.getAddress();
            final NativeInetAddress address = NativeInetAddress.newInstance(inetAddress);
            final int res = bind(this.fd, this.useIpv6(inetAddress), address.address, address.scopeId, addr.getPort());
            if (res < 0) {
                throw Errors.newIOException("bind", res);
            }
        }
        else {
            if (!(socketAddress instanceof DomainSocketAddress)) {
                throw new Error("Unexpected SocketAddress implementation " + socketAddress);
            }
            final DomainSocketAddress addr2 = (DomainSocketAddress)socketAddress;
            final int res2 = bindDomainSocket(this.fd, addr2.path().getBytes(CharsetUtil.UTF_8));
            if (res2 < 0) {
                throw Errors.newIOException("bind", res2);
            }
        }
    }
    
    public final void listen(final int backlog) throws IOException {
        final int res = listen(this.fd, backlog);
        if (res < 0) {
            throw Errors.newIOException("listen", res);
        }
    }
    
    public final int accept(final byte[] addr) throws IOException {
        final int res = accept(this.fd, addr);
        if (res >= 0) {
            return res;
        }
        if (res == Errors.ERRNO_EAGAIN_NEGATIVE || res == Errors.ERRNO_EWOULDBLOCK_NEGATIVE) {
            return -1;
        }
        throw Errors.newIOException("accept", res);
    }
    
    public final InetSocketAddress remoteAddress() {
        final byte[] addr = remoteAddress(this.fd);
        return (addr == null) ? null : NativeInetAddress.address(addr, 0, addr.length);
    }
    
    public final InetSocketAddress localAddress() {
        final byte[] addr = localAddress(this.fd);
        return (addr == null) ? null : NativeInetAddress.address(addr, 0, addr.length);
    }
    
    public final int getReceiveBufferSize() throws IOException {
        return getReceiveBufferSize(this.fd);
    }
    
    public final int getSendBufferSize() throws IOException {
        return getSendBufferSize(this.fd);
    }
    
    public final boolean isKeepAlive() throws IOException {
        return isKeepAlive(this.fd) != 0;
    }
    
    public final boolean isTcpNoDelay() throws IOException {
        return isTcpNoDelay(this.fd) != 0;
    }
    
    public final boolean isReuseAddress() throws IOException {
        return isReuseAddress(this.fd) != 0;
    }
    
    public final boolean isReusePort() throws IOException {
        return isReusePort(this.fd) != 0;
    }
    
    public final boolean isBroadcast() throws IOException {
        return isBroadcast(this.fd) != 0;
    }
    
    public final int getSoLinger() throws IOException {
        return getSoLinger(this.fd);
    }
    
    public final int getSoError() throws IOException {
        return getSoError(this.fd);
    }
    
    public final int getTrafficClass() throws IOException {
        return getTrafficClass(this.fd, this.ipv6);
    }
    
    public final void setKeepAlive(final boolean keepAlive) throws IOException {
        setKeepAlive(this.fd, keepAlive ? 1 : 0);
    }
    
    public final void setReceiveBufferSize(final int receiveBufferSize) throws IOException {
        setReceiveBufferSize(this.fd, receiveBufferSize);
    }
    
    public final void setSendBufferSize(final int sendBufferSize) throws IOException {
        setSendBufferSize(this.fd, sendBufferSize);
    }
    
    public final void setTcpNoDelay(final boolean tcpNoDelay) throws IOException {
        setTcpNoDelay(this.fd, tcpNoDelay ? 1 : 0);
    }
    
    public final void setSoLinger(final int soLinger) throws IOException {
        setSoLinger(this.fd, soLinger);
    }
    
    public final void setReuseAddress(final boolean reuseAddress) throws IOException {
        setReuseAddress(this.fd, reuseAddress ? 1 : 0);
    }
    
    public final void setReusePort(final boolean reusePort) throws IOException {
        setReusePort(this.fd, reusePort ? 1 : 0);
    }
    
    public final void setBroadcast(final boolean broadcast) throws IOException {
        setBroadcast(this.fd, broadcast ? 1 : 0);
    }
    
    public final void setTrafficClass(final int trafficClass) throws IOException {
        setTrafficClass(this.fd, this.ipv6, trafficClass);
    }
    
    public static native boolean isIPv6Preferred();
    
    private static native boolean isIPv6(final int p0);
    
    @Override
    public String toString() {
        return "Socket{fd=" + this.fd + '}';
    }
    
    public static Socket newSocketStream() {
        return new Socket(newSocketStream0());
    }
    
    public static Socket newSocketDgram() {
        return new Socket(newSocketDgram0());
    }
    
    public static Socket newSocketDomain() {
        return new Socket(newSocketDomain0());
    }
    
    public static Socket newSocketDomainDgram() {
        return new Socket(newSocketDomainDgram0());
    }
    
    public static void initialize() {
        if (Socket.INITIALIZED.compareAndSet(false, true)) {
            initialize(NetUtil.isIpV4StackPreferred());
        }
    }
    
    protected static int newSocketStream0() {
        return newSocketStream0(isIPv6Preferred());
    }
    
    protected static int newSocketStream0(final boolean ipv6) {
        final int res = newSocketStreamFd(ipv6);
        if (res < 0) {
            throw new ChannelException(Errors.newIOException("newSocketStream", res));
        }
        return res;
    }
    
    protected static int newSocketDgram0() {
        return newSocketDgram0(isIPv6Preferred());
    }
    
    protected static int newSocketDgram0(final boolean ipv6) {
        final int res = newSocketDgramFd(ipv6);
        if (res < 0) {
            throw new ChannelException(Errors.newIOException("newSocketDgram", res));
        }
        return res;
    }
    
    protected static int newSocketDomain0() {
        final int res = newSocketDomainFd();
        if (res < 0) {
            throw new ChannelException(Errors.newIOException("newSocketDomain", res));
        }
        return res;
    }
    
    protected static int newSocketDomainDgram0() {
        final int res = newSocketDomainDgramFd();
        if (res < 0) {
            throw new ChannelException(Errors.newIOException("newSocketDomainDgram", res));
        }
        return res;
    }
    
    private static native int shutdown(final int p0, final boolean p1, final boolean p2);
    
    private static native int connect(final int p0, final boolean p1, final byte[] p2, final int p3, final int p4);
    
    private static native int connectDomainSocket(final int p0, final byte[] p1);
    
    private static native int finishConnect(final int p0);
    
    private static native int disconnect(final int p0, final boolean p1);
    
    private static native int bind(final int p0, final boolean p1, final byte[] p2, final int p3, final int p4);
    
    private static native int bindDomainSocket(final int p0, final byte[] p1);
    
    private static native int listen(final int p0, final int p1);
    
    private static native int accept(final int p0, final byte[] p1);
    
    private static native byte[] remoteAddress(final int p0);
    
    private static native byte[] localAddress(final int p0);
    
    private static native int sendTo(final int p0, final boolean p1, final ByteBuffer p2, final int p3, final int p4, final byte[] p5, final int p6, final int p7, final int p8);
    
    private static native int sendToAddress(final int p0, final boolean p1, final long p2, final int p3, final int p4, final byte[] p5, final int p6, final int p7, final int p8);
    
    private static native int sendToAddresses(final int p0, final boolean p1, final long p2, final int p3, final byte[] p4, final int p5, final int p6, final int p7);
    
    private static native int sendToDomainSocket(final int p0, final ByteBuffer p1, final int p2, final int p3, final byte[] p4);
    
    private static native int sendToAddressDomainSocket(final int p0, final long p1, final int p2, final int p3, final byte[] p4);
    
    private static native int sendToAddressesDomainSocket(final int p0, final long p1, final int p2, final byte[] p3);
    
    private static native DatagramSocketAddress recvFrom(final int p0, final ByteBuffer p1, final int p2, final int p3) throws IOException;
    
    private static native DatagramSocketAddress recvFromAddress(final int p0, final long p1, final int p2, final int p3) throws IOException;
    
    private static native DomainDatagramSocketAddress recvFromDomainSocket(final int p0, final ByteBuffer p1, final int p2, final int p3) throws IOException;
    
    private static native DomainDatagramSocketAddress recvFromAddressDomainSocket(final int p0, final long p1, final int p2, final int p3) throws IOException;
    
    private static native int recvFd(final int p0);
    
    private static native int sendFd(final int p0, final int p1);
    
    private static native int msgFastopen();
    
    private static native int newSocketStreamFd(final boolean p0);
    
    private static native int newSocketDgramFd(final boolean p0);
    
    private static native int newSocketDomainFd();
    
    private static native int newSocketDomainDgramFd();
    
    private static native int isReuseAddress(final int p0) throws IOException;
    
    private static native int isReusePort(final int p0) throws IOException;
    
    private static native int getReceiveBufferSize(final int p0) throws IOException;
    
    private static native int getSendBufferSize(final int p0) throws IOException;
    
    private static native int isKeepAlive(final int p0) throws IOException;
    
    private static native int isTcpNoDelay(final int p0) throws IOException;
    
    private static native int isBroadcast(final int p0) throws IOException;
    
    private static native int getSoLinger(final int p0) throws IOException;
    
    private static native int getSoError(final int p0) throws IOException;
    
    private static native int getTrafficClass(final int p0, final boolean p1) throws IOException;
    
    private static native void setReuseAddress(final int p0, final int p1) throws IOException;
    
    private static native void setReusePort(final int p0, final int p1) throws IOException;
    
    private static native void setKeepAlive(final int p0, final int p1) throws IOException;
    
    private static native void setReceiveBufferSize(final int p0, final int p1) throws IOException;
    
    private static native void setSendBufferSize(final int p0, final int p1) throws IOException;
    
    private static native void setTcpNoDelay(final int p0, final int p1) throws IOException;
    
    private static native void setSoLinger(final int p0, final int p1) throws IOException;
    
    private static native void setBroadcast(final int p0, final int p1) throws IOException;
    
    private static native void setTrafficClass(final int p0, final boolean p1, final int p2) throws IOException;
    
    private static native void initialize(final boolean p0);
    
    static {
        INITIALIZED = new AtomicBoolean();
    }
}
