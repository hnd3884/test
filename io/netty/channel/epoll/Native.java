package io.netty.channel.epoll;

import io.netty.channel.unix.Unix;
import io.netty.util.internal.ClassInitializerUtil;
import java.nio.channels.FileChannel;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.unix.PeerCredentials;
import java.nio.channels.Selector;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.channel.unix.Socket;
import io.netty.channel.unix.Errors;
import java.io.IOException;
import io.netty.channel.unix.FileDescriptor;
import io.netty.util.internal.logging.InternalLogger;

public final class Native
{
    private static final InternalLogger logger;
    public static final int EPOLLIN;
    public static final int EPOLLOUT;
    public static final int EPOLLRDHUP;
    public static final int EPOLLET;
    public static final int EPOLLERR;
    public static final boolean IS_SUPPORTING_SENDMMSG;
    static final boolean IS_SUPPORTING_RECVMMSG;
    static final boolean IS_SUPPORTING_UDP_SEGMENT;
    private static final int TFO_ENABLED_CLIENT_MASK = 1;
    private static final int TFO_ENABLED_SERVER_MASK = 2;
    private static final int TCP_FASTOPEN_MODE;
    static final boolean IS_SUPPORTING_TCP_FASTOPEN_CLIENT;
    static final boolean IS_SUPPORTING_TCP_FASTOPEN_SERVER;
    @Deprecated
    public static final boolean IS_SUPPORTING_TCP_FASTOPEN;
    public static final int TCP_MD5SIG_MAXKEYLEN;
    public static final String KERNEL_VERSION;
    
    private static native int registerUnix();
    
    public static FileDescriptor newEventFd() {
        return new FileDescriptor(eventFd());
    }
    
    public static FileDescriptor newTimerFd() {
        return new FileDescriptor(timerFd());
    }
    
    private static native boolean isSupportingUdpSegment();
    
    private static native int eventFd();
    
    private static native int timerFd();
    
    public static native void eventFdWrite(final int p0, final long p1);
    
    public static native void eventFdRead(final int p0);
    
    static native void timerFdRead(final int p0);
    
    static native void timerFdSetTime(final int p0, final int p1, final int p2) throws IOException;
    
    public static FileDescriptor newEpollCreate() {
        return new FileDescriptor(epollCreate());
    }
    
    private static native int epollCreate();
    
    @Deprecated
    public static int epollWait(final FileDescriptor epollFd, final EpollEventArray events, final FileDescriptor timerFd, int timeoutSec, int timeoutNs) throws IOException {
        if (timeoutSec == 0 && timeoutNs == 0) {
            return epollWait(epollFd, events, 0);
        }
        if (timeoutSec == Integer.MAX_VALUE) {
            timeoutSec = 0;
            timeoutNs = 0;
        }
        final int ready = epollWait0(epollFd.intValue(), events.memoryAddress(), events.length(), timerFd.intValue(), timeoutSec, timeoutNs);
        if (ready < 0) {
            throw Errors.newIOException("epoll_wait", ready);
        }
        return ready;
    }
    
    static int epollWait(final FileDescriptor epollFd, final EpollEventArray events, final boolean immediatePoll) throws IOException {
        return epollWait(epollFd, events, immediatePoll ? 0 : -1);
    }
    
    static int epollWait(final FileDescriptor epollFd, final EpollEventArray events, final int timeoutMillis) throws IOException {
        final int ready = epollWait(epollFd.intValue(), events.memoryAddress(), events.length(), timeoutMillis);
        if (ready < 0) {
            throw Errors.newIOException("epoll_wait", ready);
        }
        return ready;
    }
    
    public static int epollBusyWait(final FileDescriptor epollFd, final EpollEventArray events) throws IOException {
        final int ready = epollBusyWait0(epollFd.intValue(), events.memoryAddress(), events.length());
        if (ready < 0) {
            throw Errors.newIOException("epoll_wait", ready);
        }
        return ready;
    }
    
    private static native int epollWait0(final int p0, final long p1, final int p2, final int p3, final int p4, final int p5);
    
    private static native int epollWait(final int p0, final long p1, final int p2, final int p3);
    
    private static native int epollBusyWait0(final int p0, final long p1, final int p2);
    
    public static void epollCtlAdd(final int efd, final int fd, final int flags) throws IOException {
        final int res = epollCtlAdd0(efd, fd, flags);
        if (res < 0) {
            throw Errors.newIOException("epoll_ctl", res);
        }
    }
    
    private static native int epollCtlAdd0(final int p0, final int p1, final int p2);
    
    public static void epollCtlMod(final int efd, final int fd, final int flags) throws IOException {
        final int res = epollCtlMod0(efd, fd, flags);
        if (res < 0) {
            throw Errors.newIOException("epoll_ctl", res);
        }
    }
    
    private static native int epollCtlMod0(final int p0, final int p1, final int p2);
    
    public static void epollCtlDel(final int efd, final int fd) throws IOException {
        final int res = epollCtlDel0(efd, fd);
        if (res < 0) {
            throw Errors.newIOException("epoll_ctl", res);
        }
    }
    
    private static native int epollCtlDel0(final int p0, final int p1);
    
    public static int splice(final int fd, final long offIn, final int fdOut, final long offOut, final long len) throws IOException {
        final int res = splice0(fd, offIn, fdOut, offOut, len);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("splice", res);
    }
    
    private static native int splice0(final int p0, final long p1, final int p2, final long p3, final long p4);
    
    @Deprecated
    public static int sendmmsg(final int fd, final NativeDatagramPacketArray.NativeDatagramPacket[] msgs, final int offset, final int len) throws IOException {
        return sendmmsg(fd, Socket.isIPv6Preferred(), msgs, offset, len);
    }
    
    static int sendmmsg(final int fd, final boolean ipv6, final NativeDatagramPacketArray.NativeDatagramPacket[] msgs, final int offset, final int len) throws IOException {
        final int res = sendmmsg0(fd, ipv6, msgs, offset, len);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("sendmmsg", res);
    }
    
    private static native int sendmmsg0(final int p0, final boolean p1, final NativeDatagramPacketArray.NativeDatagramPacket[] p2, final int p3, final int p4);
    
    static int recvmmsg(final int fd, final boolean ipv6, final NativeDatagramPacketArray.NativeDatagramPacket[] msgs, final int offset, final int len) throws IOException {
        final int res = recvmmsg0(fd, ipv6, msgs, offset, len);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("recvmmsg", res);
    }
    
    private static native int recvmmsg0(final int p0, final boolean p1, final NativeDatagramPacketArray.NativeDatagramPacket[] p2, final int p3, final int p4);
    
    static int recvmsg(final int fd, final boolean ipv6, final NativeDatagramPacketArray.NativeDatagramPacket packet) throws IOException {
        final int res = recvmsg0(fd, ipv6, packet);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("recvmsg", res);
    }
    
    private static native int recvmsg0(final int p0, final boolean p1, final NativeDatagramPacketArray.NativeDatagramPacket p2);
    
    public static native int sizeofEpollEvent();
    
    public static native int offsetofEpollData();
    
    private static void loadNativeLibrary() {
        final String name = PlatformDependent.normalizedOs();
        if (!"linux".equals(name)) {
            throw new IllegalStateException("Only supported on Linux");
        }
        final String staticLibName = "netty_transport_native_epoll";
        final String sharedLibName = staticLibName + '_' + PlatformDependent.normalizedArch();
        final ClassLoader cl = PlatformDependent.getClassLoader(Native.class);
        try {
            NativeLibraryLoader.load(sharedLibName, cl);
        }
        catch (final UnsatisfiedLinkError e1) {
            try {
                NativeLibraryLoader.load(staticLibName, cl);
                Native.logger.debug("Failed to load {}", sharedLibName, e1);
            }
            catch (final UnsatisfiedLinkError e2) {
                ThrowableUtil.addSuppressed(e1, e2);
                throw e1;
            }
        }
    }
    
    private Native() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(Native.class);
        Selector selector = null;
        try {
            selector = Selector.open();
        }
        catch (final IOException ex) {}
        ClassInitializerUtil.tryLoadClasses(Native.class, PeerCredentials.class, DefaultFileRegion.class, FileChannel.class, java.io.FileDescriptor.class, NativeDatagramPacketArray.NativeDatagramPacket.class);
        try {
            offsetofEpollData();
        }
        catch (final UnsatisfiedLinkError ignore) {
            loadNativeLibrary();
        }
        finally {
            try {
                if (selector != null) {
                    selector.close();
                }
            }
            catch (final IOException ex2) {}
        }
        Unix.registerInternal(new Runnable() {
            @Override
            public void run() {
                registerUnix();
            }
        });
        EPOLLIN = NativeStaticallyReferencedJniMethods.epollin();
        EPOLLOUT = NativeStaticallyReferencedJniMethods.epollout();
        EPOLLRDHUP = NativeStaticallyReferencedJniMethods.epollrdhup();
        EPOLLET = NativeStaticallyReferencedJniMethods.epollet();
        EPOLLERR = NativeStaticallyReferencedJniMethods.epollerr();
        IS_SUPPORTING_SENDMMSG = NativeStaticallyReferencedJniMethods.isSupportingSendmmsg();
        IS_SUPPORTING_RECVMMSG = NativeStaticallyReferencedJniMethods.isSupportingRecvmmsg();
        IS_SUPPORTING_UDP_SEGMENT = isSupportingUdpSegment();
        TCP_FASTOPEN_MODE = NativeStaticallyReferencedJniMethods.tcpFastopenMode();
        IS_SUPPORTING_TCP_FASTOPEN_CLIENT = ((Native.TCP_FASTOPEN_MODE & 0x1) == 0x1);
        IS_SUPPORTING_TCP_FASTOPEN_SERVER = ((Native.TCP_FASTOPEN_MODE & 0x2) == 0x2);
        IS_SUPPORTING_TCP_FASTOPEN = (Native.IS_SUPPORTING_TCP_FASTOPEN_CLIENT || Native.IS_SUPPORTING_TCP_FASTOPEN_SERVER);
        TCP_MD5SIG_MAXKEYLEN = NativeStaticallyReferencedJniMethods.tcpMd5SigMaxKeyLen();
        KERNEL_VERSION = NativeStaticallyReferencedJniMethods.kernelVersion();
    }
}
