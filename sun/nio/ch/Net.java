package sun.nio.ch;

import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import jdk.net.SocketFlow;
import java.net.SocketOption;
import java.io.FileDescriptor;
import java.security.AccessController;
import java.util.Enumeration;
import java.security.PrivilegedAction;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.io.IOException;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.ClosedChannelException;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.Inet6Address;
import java.net.Inet4Address;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.ProtocolFamily;

public class Net
{
    static final ProtocolFamily UNSPEC;
    private static final boolean exclusiveBind;
    private static final boolean fastLoopback;
    private static volatile boolean checkedIPv6;
    private static volatile boolean isIPv6Available;
    public static final int SHUT_RD = 0;
    public static final int SHUT_WR = 1;
    public static final int SHUT_RDWR = 2;
    public static final short POLLIN;
    public static final short POLLOUT;
    public static final short POLLERR;
    public static final short POLLHUP;
    public static final short POLLNVAL;
    public static final short POLLCONN;
    
    private Net() {
    }
    
    static boolean isIPv6Available() {
        if (!Net.checkedIPv6) {
            Net.isIPv6Available = isIPv6Available0();
            Net.checkedIPv6 = true;
        }
        return Net.isIPv6Available;
    }
    
    static boolean useExclusiveBind() {
        return Net.exclusiveBind;
    }
    
    static boolean canIPv6SocketJoinIPv4Group() {
        return canIPv6SocketJoinIPv4Group0();
    }
    
    static boolean canJoin6WithIPv4Group() {
        return canJoin6WithIPv4Group0();
    }
    
    public static InetSocketAddress checkAddress(final SocketAddress socketAddress) {
        if (socketAddress == null) {
            throw new NullPointerException();
        }
        if (!(socketAddress instanceof InetSocketAddress)) {
            throw new UnsupportedAddressTypeException();
        }
        final InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
        if (inetSocketAddress.isUnresolved()) {
            throw new UnresolvedAddressException();
        }
        final InetAddress address = inetSocketAddress.getAddress();
        if (!(address instanceof Inet4Address) && !(address instanceof Inet6Address)) {
            throw new IllegalArgumentException("Invalid address type");
        }
        return inetSocketAddress;
    }
    
    static InetSocketAddress asInetSocketAddress(final SocketAddress socketAddress) {
        if (!(socketAddress instanceof InetSocketAddress)) {
            throw new UnsupportedAddressTypeException();
        }
        return (InetSocketAddress)socketAddress;
    }
    
    static void translateToSocketException(final Exception ex) throws SocketException {
        if (ex instanceof SocketException) {
            throw (SocketException)ex;
        }
        Exception ex2 = ex;
        if (ex instanceof ClosedChannelException) {
            ex2 = new SocketException("Socket is closed");
        }
        else if (ex instanceof NotYetConnectedException) {
            ex2 = new SocketException("Socket is not connected");
        }
        else if (ex instanceof AlreadyBoundException) {
            ex2 = new SocketException("Already bound");
        }
        else if (ex instanceof NotYetBoundException) {
            ex2 = new SocketException("Socket is not bound yet");
        }
        else if (ex instanceof UnsupportedAddressTypeException) {
            ex2 = new SocketException("Unsupported address type");
        }
        else if (ex instanceof UnresolvedAddressException) {
            ex2 = new SocketException("Unresolved address");
        }
        if (ex2 != ex) {
            ex2.initCause(ex);
        }
        if (ex2 instanceof SocketException) {
            throw (SocketException)ex2;
        }
        if (ex2 instanceof RuntimeException) {
            throw (RuntimeException)ex2;
        }
        throw new Error("Untranslated exception", ex2);
    }
    
    static void translateException(final Exception ex, final boolean b) throws IOException {
        if (ex instanceof IOException) {
            throw (IOException)ex;
        }
        if (b && ex instanceof UnresolvedAddressException) {
            throw new UnknownHostException();
        }
        translateToSocketException(ex);
    }
    
    static void translateException(final Exception ex) throws IOException {
        translateException(ex, false);
    }
    
    static InetSocketAddress getRevealedLocalAddress(InetSocketAddress loopbackAddress) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (loopbackAddress == null || securityManager == null) {
            return loopbackAddress;
        }
        try {
            securityManager.checkConnect(loopbackAddress.getAddress().getHostAddress(), -1);
        }
        catch (final SecurityException ex) {
            loopbackAddress = getLoopbackAddress(loopbackAddress.getPort());
        }
        return loopbackAddress;
    }
    
    static String getRevealedLocalAddressAsString(final InetSocketAddress inetSocketAddress) {
        return (System.getSecurityManager() == null) ? inetSocketAddress.toString() : getLoopbackAddress(inetSocketAddress.getPort()).toString();
    }
    
    private static InetSocketAddress getLoopbackAddress(final int n) {
        return new InetSocketAddress(InetAddress.getLoopbackAddress(), n);
    }
    
    static Inet4Address anyInet4Address(final NetworkInterface networkInterface) {
        return AccessController.doPrivileged((PrivilegedAction<Inet4Address>)new PrivilegedAction<Inet4Address>() {
            @Override
            public Inet4Address run() {
                final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    final InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address) {
                        return (Inet4Address)inetAddress;
                    }
                }
                return null;
            }
        });
    }
    
    static int inet4AsInt(final InetAddress inetAddress) {
        if (inetAddress instanceof Inet4Address) {
            final byte[] address = inetAddress.getAddress();
            return (address[3] & 0xFF) | (address[2] << 8 & 0xFF00) | (address[1] << 16 & 0xFF0000) | (address[0] << 24 & 0xFF000000);
        }
        throw new AssertionError((Object)"Should not reach here");
    }
    
    static InetAddress inet4FromInt(final int n) {
        final byte[] array = { (byte)(n >>> 24 & 0xFF), (byte)(n >>> 16 & 0xFF), (byte)(n >>> 8 & 0xFF), (byte)(n & 0xFF) };
        try {
            return InetAddress.getByAddress(array);
        }
        catch (final UnknownHostException ex) {
            throw new AssertionError((Object)"Should not reach here");
        }
    }
    
    static byte[] inet6AsByteArray(final InetAddress inetAddress) {
        if (inetAddress instanceof Inet6Address) {
            return inetAddress.getAddress();
        }
        if (inetAddress instanceof Inet4Address) {
            final byte[] address = inetAddress.getAddress();
            return new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, address[0], address[1], address[2], address[3] };
        }
        throw new AssertionError((Object)"Should not reach here");
    }
    
    static void setSocketOption(final FileDescriptor fileDescriptor, final ProtocolFamily protocolFamily, final SocketOption<?> socketOption, Object o) throws IOException {
        if (o == null) {
            throw new IllegalArgumentException("Invalid option value");
        }
        final Class<Integer> type = socketOption.type();
        if (type == SocketFlow.class) {
            ExtendedOptionsImpl.checkSetOptionPermission(socketOption);
            ExtendedOptionsImpl.checkValueType(o, SocketFlow.class);
            ExtendedOptionsImpl.setFlowOption(fileDescriptor, (SocketFlow)o);
            return;
        }
        if (socketOption == ExtendedSocketOptions.TCP_KEEPINTERVAL) {
            ExtendedOptionsImpl.checkSetOptionPermission(socketOption);
            ExtendedOptionsImpl.checkValueType(o, Integer.class);
            ExtendedOptionsImpl.setTcpKeepAliveIntvl(fileDescriptor, (int)o);
            return;
        }
        if (socketOption == ExtendedSocketOptions.TCP_KEEPIDLE) {
            ExtendedOptionsImpl.checkSetOptionPermission(socketOption);
            ExtendedOptionsImpl.checkValueType(o, Integer.class);
            ExtendedOptionsImpl.setTcpKeepAliveTime(fileDescriptor, (int)o);
            return;
        }
        if (socketOption == ExtendedSocketOptions.TCP_KEEPCOUNT) {
            ExtendedOptionsImpl.checkSetOptionPermission(socketOption);
            ExtendedOptionsImpl.checkValueType(o, Integer.class);
            ExtendedOptionsImpl.setTcpKeepAliveProbes(fileDescriptor, (int)o);
            return;
        }
        if (type != Integer.class && type != Boolean.class) {
            throw new AssertionError((Object)"Should not reach here");
        }
        if ((socketOption == StandardSocketOptions.SO_RCVBUF || socketOption == StandardSocketOptions.SO_SNDBUF) && (int)o < 0) {
            throw new IllegalArgumentException("Invalid send/receive buffer size");
        }
        if (socketOption == StandardSocketOptions.SO_LINGER) {
            final int intValue = (int)o;
            if (intValue < 0) {
                o = -1;
            }
            if (intValue > 65535) {
                o = 65535;
            }
        }
        if (socketOption == StandardSocketOptions.IP_TOS) {
            final int intValue2 = (int)o;
            if (intValue2 < 0 || intValue2 > 255) {
                throw new IllegalArgumentException("Invalid IP_TOS value");
            }
        }
        if (socketOption == StandardSocketOptions.IP_MULTICAST_TTL) {
            final int intValue3 = (int)o;
            if (intValue3 < 0 || intValue3 > 255) {
                throw new IllegalArgumentException("Invalid TTL/hop value");
            }
        }
        final OptionKey option = SocketOptionRegistry.findOption(socketOption, protocolFamily);
        if (option == null) {
            throw new AssertionError((Object)"Option not found");
        }
        int n;
        if (type == Integer.class) {
            n = (int)o;
        }
        else {
            n = (((boolean)o) ? 1 : 0);
        }
        setIntOption0(fileDescriptor, protocolFamily == Net.UNSPEC, option.level(), option.name(), n, protocolFamily == StandardProtocolFamily.INET6);
    }
    
    static Object getSocketOption(final FileDescriptor fileDescriptor, final ProtocolFamily protocolFamily, final SocketOption<?> socketOption) throws IOException {
        final Class<Integer> type = socketOption.type();
        if (type == SocketFlow.class) {
            ExtendedOptionsImpl.checkGetOptionPermission(socketOption);
            final SocketFlow create = SocketFlow.create();
            ExtendedOptionsImpl.getFlowOption(fileDescriptor, create);
            return create;
        }
        if (socketOption == ExtendedSocketOptions.TCP_KEEPINTERVAL) {
            ExtendedOptionsImpl.checkGetOptionPermission(socketOption);
            return ExtendedOptionsImpl.getTcpKeepAliveIntvl(fileDescriptor);
        }
        if (socketOption == ExtendedSocketOptions.TCP_KEEPIDLE) {
            ExtendedOptionsImpl.checkGetOptionPermission(socketOption);
            return ExtendedOptionsImpl.getTcpKeepAliveTime(fileDescriptor);
        }
        if (socketOption == ExtendedSocketOptions.TCP_KEEPCOUNT) {
            ExtendedOptionsImpl.checkGetOptionPermission(socketOption);
            return ExtendedOptionsImpl.getTcpKeepAliveProbes(fileDescriptor);
        }
        if (type != Integer.class && type != Boolean.class) {
            throw new AssertionError((Object)"Should not reach here");
        }
        final OptionKey option = SocketOptionRegistry.findOption(socketOption, protocolFamily);
        if (option == null) {
            throw new AssertionError((Object)"Option not found");
        }
        final int intOption0 = getIntOption0(fileDescriptor, protocolFamily == Net.UNSPEC, option.level(), option.name());
        if (type == Integer.class) {
            return intOption0;
        }
        return (intOption0 == 0) ? Boolean.FALSE : Boolean.TRUE;
    }
    
    public static boolean isFastTcpLoopbackRequested() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("jdk.net.useFastTcpLoopback");
            }
        });
        return "".equals(s) || Boolean.parseBoolean(s);
    }
    
    private static native boolean isIPv6Available0();
    
    private static native int isExclusiveBindAvailable();
    
    private static native boolean canIPv6SocketJoinIPv4Group0();
    
    private static native boolean canJoin6WithIPv4Group0();
    
    static FileDescriptor socket(final boolean b) throws IOException {
        return socket(Net.UNSPEC, b);
    }
    
    static FileDescriptor socket(final ProtocolFamily protocolFamily, final boolean b) throws IOException {
        return IOUtil.newFD(socket0(isIPv6Available() && protocolFamily != StandardProtocolFamily.INET, b, false, Net.fastLoopback));
    }
    
    static FileDescriptor serverSocket(final boolean b) {
        return IOUtil.newFD(socket0(isIPv6Available(), b, true, Net.fastLoopback));
    }
    
    private static native int socket0(final boolean p0, final boolean p1, final boolean p2, final boolean p3);
    
    public static void bind(final FileDescriptor fileDescriptor, final InetAddress inetAddress, final int n) throws IOException {
        bind(Net.UNSPEC, fileDescriptor, inetAddress, n);
    }
    
    static void bind(final ProtocolFamily protocolFamily, final FileDescriptor fileDescriptor, final InetAddress inetAddress, final int n) throws IOException {
        bind0(fileDescriptor, isIPv6Available() && protocolFamily != StandardProtocolFamily.INET, Net.exclusiveBind, inetAddress, n);
    }
    
    private static native void bind0(final FileDescriptor p0, final boolean p1, final boolean p2, final InetAddress p3, final int p4) throws IOException;
    
    static native void listen(final FileDescriptor p0, final int p1) throws IOException;
    
    static int connect(final FileDescriptor fileDescriptor, final InetAddress inetAddress, final int n) throws IOException {
        return connect(Net.UNSPEC, fileDescriptor, inetAddress, n);
    }
    
    static int connect(final ProtocolFamily protocolFamily, final FileDescriptor fileDescriptor, final InetAddress inetAddress, final int n) throws IOException {
        return connect0(isIPv6Available() && protocolFamily != StandardProtocolFamily.INET, fileDescriptor, inetAddress, n);
    }
    
    private static native int connect0(final boolean p0, final FileDescriptor p1, final InetAddress p2, final int p3) throws IOException;
    
    static native void shutdown(final FileDescriptor p0, final int p1) throws IOException;
    
    private static native int localPort(final FileDescriptor p0) throws IOException;
    
    private static native InetAddress localInetAddress(final FileDescriptor p0) throws IOException;
    
    public static InetSocketAddress localAddress(final FileDescriptor fileDescriptor) throws IOException {
        return new InetSocketAddress(localInetAddress(fileDescriptor), localPort(fileDescriptor));
    }
    
    private static native int remotePort(final FileDescriptor p0) throws IOException;
    
    private static native InetAddress remoteInetAddress(final FileDescriptor p0) throws IOException;
    
    static InetSocketAddress remoteAddress(final FileDescriptor fileDescriptor) throws IOException {
        return new InetSocketAddress(remoteInetAddress(fileDescriptor), remotePort(fileDescriptor));
    }
    
    private static native int getIntOption0(final FileDescriptor p0, final boolean p1, final int p2, final int p3) throws IOException;
    
    private static native void setIntOption0(final FileDescriptor p0, final boolean p1, final int p2, final int p3, final int p4, final boolean p5) throws IOException;
    
    static native int poll(final FileDescriptor p0, final int p1, final long p2) throws IOException;
    
    static int join4(final FileDescriptor fileDescriptor, final int n, final int n2, final int n3) throws IOException {
        return joinOrDrop4(true, fileDescriptor, n, n2, n3);
    }
    
    static void drop4(final FileDescriptor fileDescriptor, final int n, final int n2, final int n3) throws IOException {
        joinOrDrop4(false, fileDescriptor, n, n2, n3);
    }
    
    private static native int joinOrDrop4(final boolean p0, final FileDescriptor p1, final int p2, final int p3, final int p4) throws IOException;
    
    static int block4(final FileDescriptor fileDescriptor, final int n, final int n2, final int n3) throws IOException {
        return blockOrUnblock4(true, fileDescriptor, n, n2, n3);
    }
    
    static void unblock4(final FileDescriptor fileDescriptor, final int n, final int n2, final int n3) throws IOException {
        blockOrUnblock4(false, fileDescriptor, n, n2, n3);
    }
    
    private static native int blockOrUnblock4(final boolean p0, final FileDescriptor p1, final int p2, final int p3, final int p4) throws IOException;
    
    static int join6(final FileDescriptor fileDescriptor, final byte[] array, final int n, final byte[] array2) throws IOException {
        return joinOrDrop6(true, fileDescriptor, array, n, array2);
    }
    
    static void drop6(final FileDescriptor fileDescriptor, final byte[] array, final int n, final byte[] array2) throws IOException {
        joinOrDrop6(false, fileDescriptor, array, n, array2);
    }
    
    private static native int joinOrDrop6(final boolean p0, final FileDescriptor p1, final byte[] p2, final int p3, final byte[] p4) throws IOException;
    
    static int block6(final FileDescriptor fileDescriptor, final byte[] array, final int n, final byte[] array2) throws IOException {
        return blockOrUnblock6(true, fileDescriptor, array, n, array2);
    }
    
    static void unblock6(final FileDescriptor fileDescriptor, final byte[] array, final int n, final byte[] array2) throws IOException {
        blockOrUnblock6(false, fileDescriptor, array, n, array2);
    }
    
    static native int blockOrUnblock6(final boolean p0, final FileDescriptor p1, final byte[] p2, final int p3, final byte[] p4) throws IOException;
    
    static native void setInterface4(final FileDescriptor p0, final int p1) throws IOException;
    
    static native int getInterface4(final FileDescriptor p0) throws IOException;
    
    static native void setInterface6(final FileDescriptor p0, final int p1) throws IOException;
    
    static native int getInterface6(final FileDescriptor p0) throws IOException;
    
    private static native void initIDs();
    
    static native short pollinValue();
    
    static native short polloutValue();
    
    static native short pollerrValue();
    
    static native short pollhupValue();
    
    static native short pollnvalValue();
    
    static native short pollconnValue();
    
    static {
        UNSPEC = new ProtocolFamily() {
            @Override
            public String name() {
                return "UNSPEC";
            }
        };
        Net.checkedIPv6 = false;
        IOUtil.load();
        initIDs();
        POLLIN = pollinValue();
        POLLOUT = polloutValue();
        POLLERR = pollerrValue();
        POLLHUP = pollhupValue();
        POLLNVAL = pollnvalValue();
        POLLCONN = pollconnValue();
        final int exclusiveBindAvailable = isExclusiveBindAvailable();
        if (exclusiveBindAvailable >= 0) {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty("sun.net.useExclusiveBind");
                }
            });
            if (s != null) {
                exclusiveBind = (s.length() == 0 || Boolean.parseBoolean(s));
            }
            else if (exclusiveBindAvailable == 1) {
                exclusiveBind = true;
            }
            else {
                exclusiveBind = false;
            }
        }
        else {
            exclusiveBind = false;
        }
        fastLoopback = isFastTcpLoopbackRequested();
    }
}
