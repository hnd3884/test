package sun.nio.ch;

import java.util.Collections;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import java.util.HashSet;
import java.nio.channels.NetworkChannel;
import java.nio.channels.MulticastChannel;
import java.net.Inet6Address;
import java.nio.channels.MembershipKey;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.NotYetConnectedException;
import java.net.PortUnreachableException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.StandardSocketOptions;
import java.net.SocketOption;
import java.nio.channels.ClosedChannelException;
import java.io.IOException;
import java.net.StandardProtocolFamily;
import sun.net.ResourceManager;
import java.nio.channels.spi.SelectorProvider;
import java.net.SocketAddress;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.ProtocolFamily;
import java.io.FileDescriptor;
import java.nio.channels.DatagramChannel;

class DatagramChannelImpl extends DatagramChannel implements SelChImpl
{
    private static NativeDispatcher nd;
    private final FileDescriptor fd;
    private final int fdVal;
    private final ProtocolFamily family;
    private volatile long readerThread;
    private volatile long writerThread;
    private InetAddress cachedSenderInetAddress;
    private int cachedSenderPort;
    private final Object readLock;
    private final Object writeLock;
    private final Object stateLock;
    private static final int ST_UNINITIALIZED = -1;
    private static final int ST_UNCONNECTED = 0;
    private static final int ST_CONNECTED = 1;
    private static final int ST_KILLED = 2;
    private int state;
    private InetSocketAddress localAddress;
    private InetSocketAddress remoteAddress;
    private DatagramSocket socket;
    private MembershipRegistry registry;
    private boolean reuseAddressEmulated;
    private boolean isReuseAddress;
    private SocketAddress sender;
    
    public DatagramChannelImpl(final SelectorProvider selectorProvider) throws IOException {
        super(selectorProvider);
        this.readerThread = 0L;
        this.writerThread = 0L;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.stateLock = new Object();
        this.state = -1;
        ResourceManager.beforeUdpCreate();
        try {
            this.family = (Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET);
            this.fd = Net.socket(this.family, false);
            this.fdVal = IOUtil.fdVal(this.fd);
            this.state = 0;
        }
        catch (final IOException ex) {
            ResourceManager.afterUdpClose();
            throw ex;
        }
    }
    
    public DatagramChannelImpl(final SelectorProvider selectorProvider, final ProtocolFamily family) throws IOException {
        super(selectorProvider);
        this.readerThread = 0L;
        this.writerThread = 0L;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.stateLock = new Object();
        this.state = -1;
        if (family != StandardProtocolFamily.INET && family != StandardProtocolFamily.INET6) {
            if (family == null) {
                throw new NullPointerException("'family' is null");
            }
            throw new UnsupportedOperationException("Protocol family not supported");
        }
        else {
            if (family == StandardProtocolFamily.INET6 && !Net.isIPv6Available()) {
                throw new UnsupportedOperationException("IPv6 not available");
            }
            ResourceManager.beforeUdpCreate();
            try {
                this.family = family;
                this.fd = Net.socket(family, false);
                this.fdVal = IOUtil.fdVal(this.fd);
                this.state = 0;
            }
            catch (final IOException ex) {
                ResourceManager.afterUdpClose();
                throw ex;
            }
        }
    }
    
    public DatagramChannelImpl(final SelectorProvider selectorProvider, final FileDescriptor fd) throws IOException {
        super(selectorProvider);
        this.readerThread = 0L;
        this.writerThread = 0L;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.stateLock = new Object();
        this.state = -1;
        ResourceManager.beforeUdpCreate();
        this.family = (Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET);
        this.fd = fd;
        this.fdVal = IOUtil.fdVal(fd);
        this.state = 0;
        this.localAddress = Net.localAddress(fd);
    }
    
    @Override
    public DatagramSocket socket() {
        synchronized (this.stateLock) {
            if (this.socket == null) {
                this.socket = DatagramSocketAdaptor.create(this);
            }
            return this.socket;
        }
    }
    
    @Override
    public SocketAddress getLocalAddress() throws IOException {
        synchronized (this.stateLock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            return Net.getRevealedLocalAddress(this.localAddress);
        }
    }
    
    @Override
    public SocketAddress getRemoteAddress() throws IOException {
        synchronized (this.stateLock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            return this.remoteAddress;
        }
    }
    
    @Override
    public <T> DatagramChannel setOption(final SocketOption<T> socketOption, final T t) throws IOException {
        if (socketOption == null) {
            throw new NullPointerException();
        }
        if (!this.supportedOptions().contains(socketOption)) {
            throw new UnsupportedOperationException("'" + socketOption + "' not supported");
        }
        synchronized (this.stateLock) {
            this.ensureOpen();
            if (socketOption == StandardSocketOptions.IP_TOS || socketOption == StandardSocketOptions.IP_MULTICAST_TTL || socketOption == StandardSocketOptions.IP_MULTICAST_LOOP) {
                Net.setSocketOption(this.fd, this.family, socketOption, t);
                return this;
            }
            if (socketOption != StandardSocketOptions.IP_MULTICAST_IF) {
                if (socketOption == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind() && this.localAddress != null) {
                    this.reuseAddressEmulated = true;
                    this.isReuseAddress = (boolean)t;
                }
                Net.setSocketOption(this.fd, Net.UNSPEC, socketOption, t);
                return this;
            }
            if (t == null) {
                throw new IllegalArgumentException("Cannot set IP_MULTICAST_IF to 'null'");
            }
            final NetworkInterface networkInterface = (NetworkInterface)t;
            if (this.family == StandardProtocolFamily.INET6) {
                final int index = networkInterface.getIndex();
                if (index == -1) {
                    throw new IOException("Network interface cannot be identified");
                }
                Net.setInterface6(this.fd, index);
            }
            else {
                final Inet4Address anyInet4Address = Net.anyInet4Address(networkInterface);
                if (anyInet4Address == null) {
                    throw new IOException("Network interface not configured for IPv4");
                }
                Net.setInterface4(this.fd, Net.inet4AsInt(anyInet4Address));
            }
            return this;
        }
    }
    
    @Override
    public <T> T getOption(final SocketOption<T> socketOption) throws IOException {
        if (socketOption == null) {
            throw new NullPointerException();
        }
        if (!this.supportedOptions().contains(socketOption)) {
            throw new UnsupportedOperationException("'" + socketOption + "' not supported");
        }
        synchronized (this.stateLock) {
            this.ensureOpen();
            if (socketOption == StandardSocketOptions.IP_TOS || socketOption == StandardSocketOptions.IP_MULTICAST_TTL || socketOption == StandardSocketOptions.IP_MULTICAST_LOOP) {
                return (T)Net.getSocketOption(this.fd, this.family, socketOption);
            }
            if (socketOption == StandardSocketOptions.IP_MULTICAST_IF) {
                if (this.family == StandardProtocolFamily.INET) {
                    final int interface4 = Net.getInterface4(this.fd);
                    if (interface4 == 0) {
                        return null;
                    }
                    final NetworkInterface byInetAddress = NetworkInterface.getByInetAddress(Net.inet4FromInt(interface4));
                    if (byInetAddress == null) {
                        throw new IOException("Unable to map address to interface");
                    }
                    return (T)byInetAddress;
                }
                else {
                    final int interface5 = Net.getInterface6(this.fd);
                    if (interface5 == 0) {
                        return null;
                    }
                    final NetworkInterface byIndex = NetworkInterface.getByIndex(interface5);
                    if (byIndex == null) {
                        throw new IOException("Unable to map index to interface");
                    }
                    return (T)byIndex;
                }
            }
            else {
                if (socketOption == StandardSocketOptions.SO_REUSEADDR && this.reuseAddressEmulated) {
                    return (T)Boolean.valueOf(this.isReuseAddress);
                }
                return (T)Net.getSocketOption(this.fd, Net.UNSPEC, socketOption);
            }
        }
    }
    
    @Override
    public final Set<SocketOption<?>> supportedOptions() {
        return DefaultOptionsHolder.defaultOptions;
    }
    
    private void ensureOpen() throws ClosedChannelException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
    }
    
    @Override
    public SocketAddress receive(final ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer.isReadOnly()) {
            throw new IllegalArgumentException("Read-only buffer");
        }
        if (byteBuffer == null) {
            throw new NullPointerException();
        }
        synchronized (this.readLock) {
            this.ensureOpen();
            if (this.localAddress() == null) {
                this.bind(null);
            }
            int n = 0;
            ByteBuffer temporaryDirectBuffer = null;
            try {
                this.begin();
                if (!this.isOpen()) {
                    return null;
                }
                final SecurityManager securityManager = System.getSecurityManager();
                this.readerThread = NativeThread.current();
                if (this.isConnected() || securityManager == null) {
                    do {
                        n = this.receive(this.fd, byteBuffer);
                    } while (n == -3 && this.isOpen());
                    if (n == -2) {
                        return null;
                    }
                }
                else {
                    temporaryDirectBuffer = Util.getTemporaryDirectBuffer(byteBuffer.remaining());
                    while (true) {
                        n = this.receive(this.fd, temporaryDirectBuffer);
                        if (n != -3 || !this.isOpen()) {
                            if (n == -2) {
                                return null;
                            }
                            final InetSocketAddress inetSocketAddress = (InetSocketAddress)this.sender;
                            try {
                                securityManager.checkAccept(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
                            }
                            catch (final SecurityException ex) {
                                temporaryDirectBuffer.clear();
                                n = 0;
                                continue;
                            }
                            temporaryDirectBuffer.flip();
                            byteBuffer.put(temporaryDirectBuffer);
                            break;
                        }
                    }
                }
                return this.sender;
            }
            finally {
                if (temporaryDirectBuffer != null) {
                    Util.releaseTemporaryDirectBuffer(temporaryDirectBuffer);
                }
                this.readerThread = 0L;
                this.end(n > 0 || n == -2);
                assert IOStatus.check(n);
            }
        }
    }
    
    private int receive(final FileDescriptor fileDescriptor, final ByteBuffer byteBuffer) throws IOException {
        final int position = byteBuffer.position();
        final int limit = byteBuffer.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        if (byteBuffer instanceof DirectBuffer && n > 0) {
            return this.receiveIntoNativeBuffer(fileDescriptor, byteBuffer, n, position);
        }
        final int max = Math.max(n, 1);
        final ByteBuffer temporaryDirectBuffer = Util.getTemporaryDirectBuffer(max);
        try {
            final int receiveIntoNativeBuffer = this.receiveIntoNativeBuffer(fileDescriptor, temporaryDirectBuffer, max, 0);
            temporaryDirectBuffer.flip();
            if (receiveIntoNativeBuffer > 0 && n > 0) {
                byteBuffer.put(temporaryDirectBuffer);
            }
            return receiveIntoNativeBuffer;
        }
        finally {
            Util.releaseTemporaryDirectBuffer(temporaryDirectBuffer);
        }
    }
    
    private int receiveIntoNativeBuffer(final FileDescriptor fileDescriptor, final ByteBuffer byteBuffer, final int n, final int n2) throws IOException {
        final int receive0 = this.receive0(fileDescriptor, ((DirectBuffer)byteBuffer).address() + n2, n, this.isConnected());
        if (receive0 > 0) {
            byteBuffer.position(n2 + receive0);
        }
        return receive0;
    }
    
    @Override
    public int send(final ByteBuffer byteBuffer, final SocketAddress socketAddress) throws IOException {
        if (byteBuffer == null) {
            throw new NullPointerException();
        }
        synchronized (this.writeLock) {
            this.ensureOpen();
            final InetSocketAddress checkAddress = Net.checkAddress(socketAddress);
            final InetAddress address = checkAddress.getAddress();
            if (address == null) {
                throw new IOException("Target address not resolved");
            }
            synchronized (this.stateLock) {
                if (!this.isConnected()) {
                    if (socketAddress == null) {
                        throw new NullPointerException();
                    }
                    final SecurityManager securityManager = System.getSecurityManager();
                    if (securityManager != null) {
                        if (address.isMulticastAddress()) {
                            securityManager.checkMulticast(address);
                        }
                        else {
                            securityManager.checkConnect(address.getHostAddress(), checkAddress.getPort());
                        }
                    }
                }
                else {
                    if (!socketAddress.equals(this.remoteAddress)) {
                        throw new IllegalArgumentException("Connected address not equal to target address");
                    }
                    return this.write(byteBuffer);
                }
            }
            int send = 0;
            try {
                this.begin();
                if (!this.isOpen()) {
                    return 0;
                }
                this.writerThread = NativeThread.current();
                do {
                    send = this.send(this.fd, byteBuffer, checkAddress);
                } while (send == -3 && this.isOpen());
                synchronized (this.stateLock) {
                    if (this.isOpen() && this.localAddress == null) {
                        this.localAddress = Net.localAddress(this.fd);
                    }
                }
                return IOStatus.normalize(send);
            }
            finally {
                this.writerThread = 0L;
                this.end(send > 0 || send == -2);
                assert IOStatus.check(send);
            }
        }
    }
    
    private int send(final FileDescriptor fileDescriptor, final ByteBuffer byteBuffer, final InetSocketAddress inetSocketAddress) throws IOException {
        if (byteBuffer instanceof DirectBuffer) {
            return this.sendFromNativeBuffer(fileDescriptor, byteBuffer, inetSocketAddress);
        }
        final int position = byteBuffer.position();
        final int limit = byteBuffer.limit();
        assert position <= limit;
        final ByteBuffer temporaryDirectBuffer = Util.getTemporaryDirectBuffer((position <= limit) ? (limit - position) : 0);
        try {
            temporaryDirectBuffer.put(byteBuffer);
            temporaryDirectBuffer.flip();
            byteBuffer.position(position);
            final int sendFromNativeBuffer = this.sendFromNativeBuffer(fileDescriptor, temporaryDirectBuffer, inetSocketAddress);
            if (sendFromNativeBuffer > 0) {
                byteBuffer.position(position + sendFromNativeBuffer);
            }
            return sendFromNativeBuffer;
        }
        finally {
            Util.releaseTemporaryDirectBuffer(temporaryDirectBuffer);
        }
    }
    
    private int sendFromNativeBuffer(final FileDescriptor fileDescriptor, final ByteBuffer byteBuffer, final InetSocketAddress inetSocketAddress) throws IOException {
        final int position = byteBuffer.position();
        final int limit = byteBuffer.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        final boolean b = this.family != StandardProtocolFamily.INET;
        int send0;
        try {
            send0 = this.send0(b, fileDescriptor, ((DirectBuffer)byteBuffer).address() + position, n, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
        }
        catch (final PortUnreachableException ex) {
            if (this.isConnected()) {
                throw ex;
            }
            send0 = n;
        }
        if (send0 > 0) {
            byteBuffer.position(position + send0);
        }
        return send0;
    }
    
    @Override
    public int read(final ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer == null) {
            throw new NullPointerException();
        }
        synchronized (this.readLock) {
            synchronized (this.stateLock) {
                this.ensureOpen();
                if (!this.isConnected()) {
                    throw new NotYetConnectedException();
                }
            }
            int read = 0;
            try {
                this.begin();
                if (!this.isOpen()) {
                    return 0;
                }
                this.readerThread = NativeThread.current();
                do {
                    read = IOUtil.read(this.fd, byteBuffer, -1L, DatagramChannelImpl.nd);
                } while (read == -3 && this.isOpen());
                return IOStatus.normalize(read);
            }
            finally {
                this.readerThread = 0L;
                this.end(read > 0 || read == -2);
                assert IOStatus.check(read);
            }
        }
    }
    
    @Override
    public long read(final ByteBuffer[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n > array.length - n2) {
            throw new IndexOutOfBoundsException();
        }
        synchronized (this.readLock) {
            synchronized (this.stateLock) {
                this.ensureOpen();
                if (!this.isConnected()) {
                    throw new NotYetConnectedException();
                }
            }
            long read = 0L;
            try {
                this.begin();
                if (!this.isOpen()) {
                    return 0L;
                }
                this.readerThread = NativeThread.current();
                do {
                    read = IOUtil.read(this.fd, array, n, n2, DatagramChannelImpl.nd);
                } while (read == -3L && this.isOpen());
                return IOStatus.normalize(read);
            }
            finally {
                this.readerThread = 0L;
                this.end(read > 0L || read == -2L);
                assert IOStatus.check(read);
            }
        }
    }
    
    @Override
    public int write(final ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer == null) {
            throw new NullPointerException();
        }
        synchronized (this.writeLock) {
            synchronized (this.stateLock) {
                this.ensureOpen();
                if (!this.isConnected()) {
                    throw new NotYetConnectedException();
                }
            }
            int write = 0;
            try {
                this.begin();
                if (!this.isOpen()) {
                    return 0;
                }
                this.writerThread = NativeThread.current();
                do {
                    write = IOUtil.write(this.fd, byteBuffer, -1L, DatagramChannelImpl.nd);
                } while (write == -3 && this.isOpen());
                return IOStatus.normalize(write);
            }
            finally {
                this.writerThread = 0L;
                this.end(write > 0 || write == -2);
                assert IOStatus.check(write);
            }
        }
    }
    
    @Override
    public long write(final ByteBuffer[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n > array.length - n2) {
            throw new IndexOutOfBoundsException();
        }
        synchronized (this.writeLock) {
            synchronized (this.stateLock) {
                this.ensureOpen();
                if (!this.isConnected()) {
                    throw new NotYetConnectedException();
                }
            }
            long write = 0L;
            try {
                this.begin();
                if (!this.isOpen()) {
                    return 0L;
                }
                this.writerThread = NativeThread.current();
                do {
                    write = IOUtil.write(this.fd, array, n, n2, DatagramChannelImpl.nd);
                } while (write == -3L && this.isOpen());
                return IOStatus.normalize(write);
            }
            finally {
                this.writerThread = 0L;
                this.end(write > 0L || write == -2L);
                assert IOStatus.check(write);
            }
        }
    }
    
    @Override
    protected void implConfigureBlocking(final boolean b) throws IOException {
        IOUtil.configureBlocking(this.fd, b);
    }
    
    public SocketAddress localAddress() {
        synchronized (this.stateLock) {
            return this.localAddress;
        }
    }
    
    public SocketAddress remoteAddress() {
        synchronized (this.stateLock) {
            return this.remoteAddress;
        }
    }
    
    @Override
    public DatagramChannel bind(final SocketAddress socketAddress) throws IOException {
        synchronized (this.readLock) {
            synchronized (this.writeLock) {
                synchronized (this.stateLock) {
                    this.ensureOpen();
                    if (this.localAddress != null) {
                        throw new AlreadyBoundException();
                    }
                    InetSocketAddress checkAddress;
                    if (socketAddress == null) {
                        if (this.family == StandardProtocolFamily.INET) {
                            checkAddress = new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 0);
                        }
                        else {
                            checkAddress = new InetSocketAddress(0);
                        }
                    }
                    else {
                        checkAddress = Net.checkAddress(socketAddress);
                        if (this.family == StandardProtocolFamily.INET && !(checkAddress.getAddress() instanceof Inet4Address)) {
                            throw new UnsupportedAddressTypeException();
                        }
                    }
                    final SecurityManager securityManager = System.getSecurityManager();
                    if (securityManager != null) {
                        securityManager.checkListen(checkAddress.getPort());
                    }
                    Net.bind(this.family, this.fd, checkAddress.getAddress(), checkAddress.getPort());
                    this.localAddress = Net.localAddress(this.fd);
                }
            }
        }
        return this;
    }
    
    @Override
    public boolean isConnected() {
        synchronized (this.stateLock) {
            return this.state == 1;
        }
    }
    
    void ensureOpenAndUnconnected() throws IOException {
        synchronized (this.stateLock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (this.state != 0) {
                throw new IllegalStateException("Connect already invoked");
            }
        }
    }
    
    @Override
    public DatagramChannel connect(final SocketAddress socketAddress) throws IOException {
        synchronized (this.readLock) {
            synchronized (this.writeLock) {
                synchronized (this.stateLock) {
                    this.ensureOpenAndUnconnected();
                    final InetSocketAddress checkAddress = Net.checkAddress(socketAddress);
                    final SecurityManager securityManager = System.getSecurityManager();
                    if (securityManager != null) {
                        securityManager.checkConnect(checkAddress.getAddress().getHostAddress(), checkAddress.getPort());
                    }
                    if (Net.connect(this.family, this.fd, checkAddress.getAddress(), checkAddress.getPort()) <= 0) {
                        throw new Error();
                    }
                    this.state = 1;
                    this.remoteAddress = checkAddress;
                    this.sender = checkAddress;
                    this.cachedSenderInetAddress = checkAddress.getAddress();
                    this.cachedSenderPort = checkAddress.getPort();
                    this.localAddress = Net.localAddress(this.fd);
                    synchronized (this.blockingLock()) {
                        final boolean blocking = this.isBlocking();
                        try {
                            final ByteBuffer allocate = ByteBuffer.allocate(1);
                            if (blocking) {
                                this.configureBlocking(false);
                            }
                            do {
                                allocate.clear();
                            } while (this.receive(allocate) != null);
                        }
                        finally {
                            if (blocking) {
                                this.configureBlocking(true);
                            }
                        }
                    }
                }
            }
        }
        return this;
    }
    
    @Override
    public DatagramChannel disconnect() throws IOException {
        synchronized (this.readLock) {
            synchronized (this.writeLock) {
                synchronized (this.stateLock) {
                    if (!this.isConnected() || !this.isOpen()) {
                        return this;
                    }
                    final InetSocketAddress remoteAddress = this.remoteAddress;
                    final SecurityManager securityManager = System.getSecurityManager();
                    if (securityManager != null) {
                        securityManager.checkConnect(remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort());
                    }
                    disconnect0(this.fd, this.family == StandardProtocolFamily.INET6);
                    this.remoteAddress = null;
                    this.state = 0;
                    this.localAddress = Net.localAddress(this.fd);
                }
            }
        }
        return this;
    }
    
    private MembershipKey innerJoin(final InetAddress inetAddress, final NetworkInterface networkInterface, final InetAddress inetAddress2) throws IOException {
        if (!inetAddress.isMulticastAddress()) {
            throw new IllegalArgumentException("Group not a multicast address");
        }
        if (inetAddress instanceof Inet4Address) {
            if (this.family == StandardProtocolFamily.INET6 && !Net.canIPv6SocketJoinIPv4Group()) {
                throw new IllegalArgumentException("IPv6 socket cannot join IPv4 multicast group");
            }
        }
        else {
            if (!(inetAddress instanceof Inet6Address)) {
                throw new IllegalArgumentException("Address type not supported");
            }
            if (this.family != StandardProtocolFamily.INET6) {
                throw new IllegalArgumentException("Only IPv6 sockets can join IPv6 multicast group");
            }
        }
        if (inetAddress2 != null) {
            if (inetAddress2.isAnyLocalAddress()) {
                throw new IllegalArgumentException("Source address is a wildcard address");
            }
            if (inetAddress2.isMulticastAddress()) {
                throw new IllegalArgumentException("Source address is multicast address");
            }
            if (inetAddress2.getClass() != inetAddress.getClass()) {
                throw new IllegalArgumentException("Source address is different type to group");
            }
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkMulticast(inetAddress);
        }
        synchronized (this.stateLock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (this.registry == null) {
                this.registry = new MembershipRegistry();
            }
            else {
                final MembershipKey checkMembership = this.registry.checkMembership(inetAddress, networkInterface, inetAddress2);
                if (checkMembership != null) {
                    return checkMembership;
                }
            }
            MembershipKeyImpl membershipKeyImpl;
            if (this.family == StandardProtocolFamily.INET6 && (inetAddress instanceof Inet6Address || Net.canJoin6WithIPv4Group())) {
                final int index = networkInterface.getIndex();
                if (index == -1) {
                    throw new IOException("Network interface cannot be identified");
                }
                final byte[] inet6AsByteArray = Net.inet6AsByteArray(inetAddress);
                final byte[] array = (byte[])((inetAddress2 == null) ? null : Net.inet6AsByteArray(inetAddress2));
                if (Net.join6(this.fd, inet6AsByteArray, index, array) == -2) {
                    throw new UnsupportedOperationException();
                }
                membershipKeyImpl = new MembershipKeyImpl.Type6(this, inetAddress, networkInterface, inetAddress2, inet6AsByteArray, index, array);
            }
            else {
                final Inet4Address anyInet4Address = Net.anyInet4Address(networkInterface);
                if (anyInet4Address == null) {
                    throw new IOException("Network interface not configured for IPv4");
                }
                final int inet4AsInt = Net.inet4AsInt(inetAddress);
                final int inet4AsInt2 = Net.inet4AsInt(anyInet4Address);
                final int n = (inetAddress2 == null) ? 0 : Net.inet4AsInt(inetAddress2);
                if (Net.join4(this.fd, inet4AsInt, inet4AsInt2, n) == -2) {
                    throw new UnsupportedOperationException();
                }
                membershipKeyImpl = new MembershipKeyImpl.Type4(this, inetAddress, networkInterface, inetAddress2, inet4AsInt, inet4AsInt2, n);
            }
            this.registry.add(membershipKeyImpl);
            return membershipKeyImpl;
        }
    }
    
    @Override
    public MembershipKey join(final InetAddress inetAddress, final NetworkInterface networkInterface) throws IOException {
        return this.innerJoin(inetAddress, networkInterface, null);
    }
    
    @Override
    public MembershipKey join(final InetAddress inetAddress, final NetworkInterface networkInterface, final InetAddress inetAddress2) throws IOException {
        if (inetAddress2 == null) {
            throw new NullPointerException("source address is null");
        }
        return this.innerJoin(inetAddress, networkInterface, inetAddress2);
    }
    
    void drop(final MembershipKeyImpl membershipKeyImpl) {
        assert membershipKeyImpl.channel() == this;
        synchronized (this.stateLock) {
            if (!membershipKeyImpl.isValid()) {
                return;
            }
            try {
                if (membershipKeyImpl instanceof MembershipKeyImpl.Type6) {
                    final MembershipKeyImpl.Type6 type6 = (MembershipKeyImpl.Type6)membershipKeyImpl;
                    Net.drop6(this.fd, type6.groupAddress(), type6.index(), type6.source());
                }
                else {
                    final MembershipKeyImpl.Type4 type7 = (MembershipKeyImpl.Type4)membershipKeyImpl;
                    Net.drop4(this.fd, type7.groupAddress(), type7.interfaceAddress(), type7.source());
                }
            }
            catch (final IOException ex) {
                throw new AssertionError((Object)ex);
            }
            membershipKeyImpl.invalidate();
            this.registry.remove(membershipKeyImpl);
        }
    }
    
    void block(final MembershipKeyImpl membershipKeyImpl, final InetAddress inetAddress) throws IOException {
        assert membershipKeyImpl.channel() == this;
        assert membershipKeyImpl.sourceAddress() == null;
        synchronized (this.stateLock) {
            if (!membershipKeyImpl.isValid()) {
                throw new IllegalStateException("key is no longer valid");
            }
            if (inetAddress.isAnyLocalAddress()) {
                throw new IllegalArgumentException("Source address is a wildcard address");
            }
            if (inetAddress.isMulticastAddress()) {
                throw new IllegalArgumentException("Source address is multicast address");
            }
            if (inetAddress.getClass() != membershipKeyImpl.group().getClass()) {
                throw new IllegalArgumentException("Source address is different type to group");
            }
            int n;
            if (membershipKeyImpl instanceof MembershipKeyImpl.Type6) {
                final MembershipKeyImpl.Type6 type6 = (MembershipKeyImpl.Type6)membershipKeyImpl;
                n = Net.block6(this.fd, type6.groupAddress(), type6.index(), Net.inet6AsByteArray(inetAddress));
            }
            else {
                final MembershipKeyImpl.Type4 type7 = (MembershipKeyImpl.Type4)membershipKeyImpl;
                n = Net.block4(this.fd, type7.groupAddress(), type7.interfaceAddress(), Net.inet4AsInt(inetAddress));
            }
            if (n == -2) {
                throw new UnsupportedOperationException();
            }
        }
    }
    
    void unblock(final MembershipKeyImpl membershipKeyImpl, final InetAddress inetAddress) {
        assert membershipKeyImpl.channel() == this;
        assert membershipKeyImpl.sourceAddress() == null;
        synchronized (this.stateLock) {
            if (!membershipKeyImpl.isValid()) {
                throw new IllegalStateException("key is no longer valid");
            }
            try {
                if (membershipKeyImpl instanceof MembershipKeyImpl.Type6) {
                    final MembershipKeyImpl.Type6 type6 = (MembershipKeyImpl.Type6)membershipKeyImpl;
                    Net.unblock6(this.fd, type6.groupAddress(), type6.index(), Net.inet6AsByteArray(inetAddress));
                }
                else {
                    final MembershipKeyImpl.Type4 type7 = (MembershipKeyImpl.Type4)membershipKeyImpl;
                    Net.unblock4(this.fd, type7.groupAddress(), type7.interfaceAddress(), Net.inet4AsInt(inetAddress));
                }
            }
            catch (final IOException ex) {
                throw new AssertionError((Object)ex);
            }
        }
    }
    
    @Override
    protected void implCloseSelectableChannel() throws IOException {
        synchronized (this.stateLock) {
            if (this.state != 2) {
                DatagramChannelImpl.nd.preClose(this.fd);
            }
            ResourceManager.afterUdpClose();
            if (this.registry != null) {
                this.registry.invalidateAll();
            }
            final long readerThread;
            if ((readerThread = this.readerThread) != 0L) {
                NativeThread.signal(readerThread);
            }
            final long writerThread;
            if ((writerThread = this.writerThread) != 0L) {
                NativeThread.signal(writerThread);
            }
            if (!this.isRegistered()) {
                this.kill();
            }
        }
    }
    
    @Override
    public void kill() throws IOException {
        synchronized (this.stateLock) {
            if (this.state == 2) {
                return;
            }
            if (this.state == -1) {
                this.state = 2;
                return;
            }
            assert !this.isOpen() && !this.isRegistered();
            DatagramChannelImpl.nd.close(this.fd);
            this.state = 2;
        }
    }
    
    @Override
    protected void finalize() throws IOException {
        if (this.fd != null) {
            this.close();
        }
    }
    
    public boolean translateReadyOps(final int n, final int n2, final SelectionKeyImpl selectionKeyImpl) {
        final int nioInterestOps = selectionKeyImpl.nioInterestOps();
        final int nioReadyOps = selectionKeyImpl.nioReadyOps();
        int n3 = n2;
        if ((n & Net.POLLNVAL) != 0x0) {
            return false;
        }
        if ((n & (Net.POLLERR | Net.POLLHUP)) != 0x0) {
            final int n4 = nioInterestOps;
            selectionKeyImpl.nioReadyOps(n4);
            return (n4 & ~nioReadyOps) != 0x0;
        }
        if ((n & Net.POLLIN) != 0x0 && (nioInterestOps & 0x1) != 0x0) {
            n3 |= 0x1;
        }
        if ((n & Net.POLLOUT) != 0x0 && (nioInterestOps & 0x4) != 0x0) {
            n3 |= 0x4;
        }
        selectionKeyImpl.nioReadyOps(n3);
        return (n3 & ~nioReadyOps) != 0x0;
    }
    
    @Override
    public boolean translateAndUpdateReadyOps(final int n, final SelectionKeyImpl selectionKeyImpl) {
        return this.translateReadyOps(n, selectionKeyImpl.nioReadyOps(), selectionKeyImpl);
    }
    
    @Override
    public boolean translateAndSetReadyOps(final int n, final SelectionKeyImpl selectionKeyImpl) {
        return this.translateReadyOps(n, 0, selectionKeyImpl);
    }
    
    int poll(final int n, final long n2) throws IOException {
        assert Thread.holdsLock(this.blockingLock()) && !this.isBlocking();
        synchronized (this.readLock) {
            int poll = 0;
            try {
                this.begin();
                synchronized (this.stateLock) {
                    if (!this.isOpen()) {
                        return 0;
                    }
                    this.readerThread = NativeThread.current();
                }
                poll = Net.poll(this.fd, n, n2);
            }
            finally {
                this.readerThread = 0L;
                this.end(poll > 0);
            }
            return poll;
        }
    }
    
    @Override
    public void translateAndSetInterestOps(final int n, final SelectionKeyImpl selectionKeyImpl) {
        int n2 = 0;
        if ((n & 0x1) != 0x0) {
            n2 |= Net.POLLIN;
        }
        if ((n & 0x4) != 0x0) {
            n2 |= Net.POLLOUT;
        }
        if ((n & 0x8) != 0x0) {
            n2 |= Net.POLLIN;
        }
        selectionKeyImpl.selector.putEventOps(selectionKeyImpl, n2);
    }
    
    @Override
    public FileDescriptor getFD() {
        return this.fd;
    }
    
    @Override
    public int getFDVal() {
        return this.fdVal;
    }
    
    private static native void initIDs();
    
    private static native void disconnect0(final FileDescriptor p0, final boolean p1) throws IOException;
    
    private native int receive0(final FileDescriptor p0, final long p1, final int p2, final boolean p3) throws IOException;
    
    private native int send0(final boolean p0, final FileDescriptor p1, final long p2, final int p3, final InetAddress p4, final int p5) throws IOException;
    
    static {
        DatagramChannelImpl.nd = new DatagramDispatcher();
        IOUtil.load();
        initIDs();
    }
    
    private static class DefaultOptionsHolder
    {
        static final Set<SocketOption<?>> defaultOptions;
        
        private static Set<SocketOption<?>> defaultOptions() {
            final HashSet set = new HashSet(8);
            set.add(StandardSocketOptions.SO_SNDBUF);
            set.add(StandardSocketOptions.SO_RCVBUF);
            set.add(StandardSocketOptions.SO_REUSEADDR);
            set.add(StandardSocketOptions.SO_BROADCAST);
            set.add(StandardSocketOptions.IP_TOS);
            set.add(StandardSocketOptions.IP_MULTICAST_IF);
            set.add(StandardSocketOptions.IP_MULTICAST_TTL);
            set.add(StandardSocketOptions.IP_MULTICAST_LOOP);
            if (ExtendedOptionsImpl.flowSupported()) {
                set.add(ExtendedSocketOptions.SO_FLOW_SLA);
            }
            return (Set<SocketOption<?>>)Collections.unmodifiableSet((Set<?>)set);
        }
        
        static {
            defaultOptions = defaultOptions();
        }
    }
}
