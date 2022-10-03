package sun.nio.ch;

import java.util.Collections;
import java.util.Collection;
import sun.net.ExtendedOptionsHelper;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import java.util.HashSet;
import java.nio.channels.NetworkChannel;
import java.nio.channels.NoConnectionPendingException;
import java.net.InetAddress;
import java.nio.channels.AlreadyConnectedException;
import sun.net.NetHooks;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.util.Set;
import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.net.SocketOption;
import java.nio.channels.ClosedChannelException;
import java.net.SocketAddress;
import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.FileDescriptor;
import java.nio.channels.SocketChannel;

class SocketChannelImpl extends SocketChannel implements SelChImpl
{
    private static NativeDispatcher nd;
    private final FileDescriptor fd;
    private final int fdVal;
    private volatile long readerThread;
    private volatile long writerThread;
    private final Object readLock;
    private final Object writeLock;
    private final Object stateLock;
    private boolean isReuseAddress;
    private static final int ST_UNINITIALIZED = -1;
    private static final int ST_UNCONNECTED = 0;
    private static final int ST_PENDING = 1;
    private static final int ST_CONNECTED = 2;
    private static final int ST_KILLPENDING = 3;
    private static final int ST_KILLED = 4;
    private int state;
    private InetSocketAddress localAddress;
    private InetSocketAddress remoteAddress;
    private boolean isInputOpen;
    private boolean isOutputOpen;
    private boolean readyToConnect;
    private Socket socket;
    
    SocketChannelImpl(final SelectorProvider selectorProvider) throws IOException {
        super(selectorProvider);
        this.readerThread = 0L;
        this.writerThread = 0L;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.stateLock = new Object();
        this.state = -1;
        this.isInputOpen = true;
        this.isOutputOpen = true;
        this.readyToConnect = false;
        this.fd = Net.socket(true);
        this.fdVal = IOUtil.fdVal(this.fd);
        this.state = 0;
    }
    
    SocketChannelImpl(final SelectorProvider selectorProvider, final FileDescriptor fd, final boolean b) throws IOException {
        super(selectorProvider);
        this.readerThread = 0L;
        this.writerThread = 0L;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.stateLock = new Object();
        this.state = -1;
        this.isInputOpen = true;
        this.isOutputOpen = true;
        this.readyToConnect = false;
        this.fd = fd;
        this.fdVal = IOUtil.fdVal(fd);
        this.state = 0;
        if (b) {
            this.localAddress = Net.localAddress(fd);
        }
    }
    
    SocketChannelImpl(final SelectorProvider selectorProvider, final FileDescriptor fd, final InetSocketAddress remoteAddress) throws IOException {
        super(selectorProvider);
        this.readerThread = 0L;
        this.writerThread = 0L;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.stateLock = new Object();
        this.state = -1;
        this.isInputOpen = true;
        this.isOutputOpen = true;
        this.readyToConnect = false;
        this.fd = fd;
        this.fdVal = IOUtil.fdVal(fd);
        this.state = 2;
        this.localAddress = Net.localAddress(fd);
        this.remoteAddress = remoteAddress;
    }
    
    @Override
    public Socket socket() {
        synchronized (this.stateLock) {
            if (this.socket == null) {
                this.socket = SocketAdaptor.create(this);
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
    public <T> SocketChannel setOption(final SocketOption<T> socketOption, final T t) throws IOException {
        if (socketOption == null) {
            throw new NullPointerException();
        }
        if (!this.supportedOptions().contains(socketOption)) {
            throw new UnsupportedOperationException("'" + socketOption + "' not supported");
        }
        synchronized (this.stateLock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (socketOption == StandardSocketOptions.IP_TOS) {
                Net.setSocketOption(this.fd, Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET, socketOption, t);
                return this;
            }
            if (socketOption == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
                this.isReuseAddress = (boolean)t;
                return this;
            }
            Net.setSocketOption(this.fd, Net.UNSPEC, socketOption, t);
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
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (socketOption == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
                return (T)Boolean.valueOf(this.isReuseAddress);
            }
            if (socketOption == StandardSocketOptions.IP_TOS) {
                return (T)Net.getSocketOption(this.fd, Net.isIPv6Available() ? StandardProtocolFamily.INET6 : StandardProtocolFamily.INET, socketOption);
            }
            return (T)Net.getSocketOption(this.fd, Net.UNSPEC, socketOption);
        }
    }
    
    @Override
    public final Set<SocketOption<?>> supportedOptions() {
        return DefaultOptionsHolder.defaultOptions;
    }
    
    private boolean ensureReadOpen() throws ClosedChannelException {
        synchronized (this.stateLock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (!this.isConnected()) {
                throw new NotYetConnectedException();
            }
            return this.isInputOpen;
        }
    }
    
    private void ensureWriteOpen() throws ClosedChannelException {
        synchronized (this.stateLock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (!this.isOutputOpen) {
                throw new ClosedChannelException();
            }
            if (!this.isConnected()) {
                throw new NotYetConnectedException();
            }
        }
    }
    
    private void readerCleanup() throws IOException {
        synchronized (this.stateLock) {
            this.readerThread = 0L;
            if (this.state == 3) {
                this.kill();
            }
        }
    }
    
    private void writerCleanup() throws IOException {
        synchronized (this.stateLock) {
            this.writerThread = 0L;
            if (this.state == 3) {
                this.kill();
            }
        }
    }
    
    @Override
    public int read(final ByteBuffer byteBuffer) throws IOException {
        if (byteBuffer == null) {
            throw new NullPointerException();
        }
        synchronized (this.readLock) {
            if (!this.ensureReadOpen()) {
                return -1;
            }
            int read = 0;
            try {
                this.begin();
                synchronized (this.stateLock) {
                    if (!this.isOpen()) {
                        return 0;
                    }
                    this.readerThread = NativeThread.current();
                }
                do {
                    read = IOUtil.read(this.fd, byteBuffer, -1L, SocketChannelImpl.nd);
                } while (read == -3 && this.isOpen());
                return IOStatus.normalize(read);
            }
            finally {
                this.readerCleanup();
                this.end(read > 0 || read == -2);
                synchronized (this.stateLock) {
                    if (read <= 0 && !this.isInputOpen) {
                        return -1;
                    }
                }
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
            if (!this.ensureReadOpen()) {
                return -1L;
            }
            long read = 0L;
            try {
                this.begin();
                synchronized (this.stateLock) {
                    if (!this.isOpen()) {
                        return 0L;
                    }
                    this.readerThread = NativeThread.current();
                }
                do {
                    read = IOUtil.read(this.fd, array, n, n2, SocketChannelImpl.nd);
                } while (read == -3L && this.isOpen());
                return IOStatus.normalize(read);
            }
            finally {
                this.readerCleanup();
                this.end(read > 0L || read == -2L);
                synchronized (this.stateLock) {
                    if (read <= 0L && !this.isInputOpen) {
                        return -1L;
                    }
                }
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
            this.ensureWriteOpen();
            int write = 0;
            try {
                this.begin();
                synchronized (this.stateLock) {
                    if (!this.isOpen()) {
                        return 0;
                    }
                    this.writerThread = NativeThread.current();
                }
                do {
                    write = IOUtil.write(this.fd, byteBuffer, -1L, SocketChannelImpl.nd);
                } while (write == -3 && this.isOpen());
                return IOStatus.normalize(write);
            }
            finally {
                this.writerCleanup();
                this.end(write > 0 || write == -2);
                synchronized (this.stateLock) {
                    if (write <= 0 && !this.isOutputOpen) {
                        throw new AsynchronousCloseException();
                    }
                }
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
            this.ensureWriteOpen();
            long write = 0L;
            try {
                this.begin();
                synchronized (this.stateLock) {
                    if (!this.isOpen()) {
                        return 0L;
                    }
                    this.writerThread = NativeThread.current();
                }
                do {
                    write = IOUtil.write(this.fd, array, n, n2, SocketChannelImpl.nd);
                } while (write == -3L && this.isOpen());
                return IOStatus.normalize(write);
            }
            finally {
                this.writerCleanup();
                this.end(write > 0L || write == -2L);
                synchronized (this.stateLock) {
                    if (write <= 0L && !this.isOutputOpen) {
                        throw new AsynchronousCloseException();
                    }
                }
                assert IOStatus.check(write);
            }
        }
    }
    
    int sendOutOfBandData(final byte b) throws IOException {
        synchronized (this.writeLock) {
            this.ensureWriteOpen();
            int sendOutOfBandData = 0;
            try {
                this.begin();
                synchronized (this.stateLock) {
                    if (!this.isOpen()) {
                        return 0;
                    }
                    this.writerThread = NativeThread.current();
                }
                do {
                    sendOutOfBandData = sendOutOfBandData(this.fd, b);
                } while (sendOutOfBandData == -3 && this.isOpen());
                return IOStatus.normalize(sendOutOfBandData);
            }
            finally {
                this.writerCleanup();
                this.end(sendOutOfBandData > 0 || sendOutOfBandData == -2);
                synchronized (this.stateLock) {
                    if (sendOutOfBandData <= 0 && !this.isOutputOpen) {
                        throw new AsynchronousCloseException();
                    }
                }
                assert IOStatus.check(sendOutOfBandData);
            }
        }
    }
    
    @Override
    protected void implConfigureBlocking(final boolean b) throws IOException {
        IOUtil.configureBlocking(this.fd, b);
    }
    
    public InetSocketAddress localAddress() {
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
    public SocketChannel bind(final SocketAddress socketAddress) throws IOException {
        synchronized (this.readLock) {
            synchronized (this.writeLock) {
                synchronized (this.stateLock) {
                    if (!this.isOpen()) {
                        throw new ClosedChannelException();
                    }
                    if (this.state == 1) {
                        throw new ConnectionPendingException();
                    }
                    if (this.localAddress != null) {
                        throw new AlreadyBoundException();
                    }
                    final InetSocketAddress inetSocketAddress = (socketAddress == null) ? new InetSocketAddress(0) : Net.checkAddress(socketAddress);
                    final SecurityManager securityManager = System.getSecurityManager();
                    if (securityManager != null) {
                        securityManager.checkListen(inetSocketAddress.getPort());
                    }
                    NetHooks.beforeTcpBind(this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
                    Net.bind(this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
                    this.localAddress = Net.localAddress(this.fd);
                }
            }
        }
        return this;
    }
    
    @Override
    public boolean isConnected() {
        synchronized (this.stateLock) {
            return this.state == 2;
        }
    }
    
    @Override
    public boolean isConnectionPending() {
        synchronized (this.stateLock) {
            return this.state == 1;
        }
    }
    
    void ensureOpenAndUnconnected() throws IOException {
        synchronized (this.stateLock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (this.state == 2) {
                throw new AlreadyConnectedException();
            }
            if (this.state == 1) {
                throw new ConnectionPendingException();
            }
        }
    }
    
    @Override
    public boolean connect(final SocketAddress socketAddress) throws IOException {
        synchronized (this.readLock) {
            synchronized (this.writeLock) {
                this.ensureOpenAndUnconnected();
                final InetSocketAddress checkAddress = Net.checkAddress(socketAddress);
                final SecurityManager securityManager = System.getSecurityManager();
                if (securityManager != null) {
                    securityManager.checkConnect(checkAddress.getAddress().getHostAddress(), checkAddress.getPort());
                }
                synchronized (this.blockingLock()) {
                    int connect = 0;
                    try {
                        try {
                            this.begin();
                            synchronized (this.stateLock) {
                                if (!this.isOpen()) {
                                    return false;
                                }
                                if (this.localAddress == null) {
                                    NetHooks.beforeTcpConnect(this.fd, checkAddress.getAddress(), checkAddress.getPort());
                                }
                                this.readerThread = NativeThread.current();
                            }
                            do {
                                InetAddress inetAddress = checkAddress.getAddress();
                                if (inetAddress.isAnyLocalAddress()) {
                                    inetAddress = InetAddress.getLocalHost();
                                }
                                connect = Net.connect(this.fd, inetAddress, checkAddress.getPort());
                            } while (connect == -3 && this.isOpen());
                        }
                        finally {
                            this.readerCleanup();
                            this.end(connect > 0 || connect == -2);
                            assert IOStatus.check(connect);
                        }
                    }
                    catch (final IOException ex) {
                        this.close();
                        throw ex;
                    }
                    synchronized (this.stateLock) {
                        this.remoteAddress = checkAddress;
                        if (connect > 0) {
                            this.state = 2;
                            if (this.isOpen()) {
                                this.localAddress = Net.localAddress(this.fd);
                            }
                            return true;
                        }
                        if (!this.isBlocking()) {
                            this.state = 1;
                        }
                        else {
                            assert false;
                        }
                    }
                }
                return false;
            }
        }
    }
    
    @Override
    public boolean finishConnect() throws IOException {
        synchronized (this.readLock) {
            synchronized (this.writeLock) {
                synchronized (this.stateLock) {
                    if (!this.isOpen()) {
                        throw new ClosedChannelException();
                    }
                    if (this.state == 2) {
                        return true;
                    }
                    if (this.state != 1) {
                        throw new NoConnectionPendingException();
                    }
                }
                int n = 0;
                try {
                    try {
                        this.begin();
                        synchronized (this.blockingLock()) {
                            synchronized (this.stateLock) {
                                if (!this.isOpen()) {
                                    return false;
                                }
                                this.readerThread = NativeThread.current();
                            }
                            if (!this.isBlocking()) {
                                do {
                                    n = checkConnect(this.fd, false, this.readyToConnect);
                                } while (n == -3 && this.isOpen());
                            }
                            else {
                                while (true) {
                                    n = checkConnect(this.fd, true, this.readyToConnect);
                                    if (n == 0) {
                                        continue;
                                    }
                                    if (n == -3 && this.isOpen()) {
                                        continue;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    finally {
                        synchronized (this.stateLock) {
                            this.readerThread = 0L;
                            if (this.state == 3) {
                                this.kill();
                                n = 0;
                            }
                        }
                        this.end(n > 0 || n == -2);
                        assert IOStatus.check(n);
                    }
                }
                catch (final IOException ex) {
                    this.close();
                    throw ex;
                }
                if (n > 0) {
                    synchronized (this.stateLock) {
                        this.state = 2;
                        if (this.isOpen()) {
                            this.localAddress = Net.localAddress(this.fd);
                        }
                    }
                    return true;
                }
                return false;
            }
        }
    }
    
    @Override
    public SocketChannel shutdownInput() throws IOException {
        synchronized (this.stateLock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (!this.isConnected()) {
                throw new NotYetConnectedException();
            }
            if (this.isInputOpen) {
                Net.shutdown(this.fd, 0);
                if (this.readerThread != 0L) {
                    NativeThread.signal(this.readerThread);
                }
                this.isInputOpen = false;
            }
            return this;
        }
    }
    
    @Override
    public SocketChannel shutdownOutput() throws IOException {
        synchronized (this.stateLock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (!this.isConnected()) {
                throw new NotYetConnectedException();
            }
            if (this.isOutputOpen) {
                Net.shutdown(this.fd, 1);
                if (this.writerThread != 0L) {
                    NativeThread.signal(this.writerThread);
                }
                this.isOutputOpen = false;
            }
            return this;
        }
    }
    
    public boolean isInputOpen() {
        synchronized (this.stateLock) {
            return this.isInputOpen;
        }
    }
    
    public boolean isOutputOpen() {
        synchronized (this.stateLock) {
            return this.isOutputOpen;
        }
    }
    
    @Override
    protected void implCloseSelectableChannel() throws IOException {
        synchronized (this.stateLock) {
            this.isInputOpen = false;
            this.isOutputOpen = false;
            if (this.state != 4) {
                SocketChannelImpl.nd.preClose(this.fd);
            }
            if (this.readerThread != 0L) {
                NativeThread.signal(this.readerThread);
            }
            if (this.writerThread != 0L) {
                NativeThread.signal(this.writerThread);
            }
            if (!this.isRegistered()) {
                this.kill();
            }
        }
    }
    
    @Override
    public void kill() throws IOException {
        synchronized (this.stateLock) {
            if (this.state == 4) {
                return;
            }
            if (this.state == -1) {
                this.state = 4;
                return;
            }
            assert !this.isOpen() && !this.isRegistered();
            if (this.readerThread == 0L && this.writerThread == 0L) {
                SocketChannelImpl.nd.close(this.fd);
                this.state = 4;
            }
            else {
                this.state = 3;
            }
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
            this.readyToConnect = true;
            return (n4 & ~nioReadyOps) != 0x0;
        }
        if ((n & Net.POLLIN) != 0x0 && (nioInterestOps & 0x1) != 0x0 && this.state == 2) {
            n3 |= 0x1;
        }
        if ((n & Net.POLLCONN) != 0x0 && (nioInterestOps & 0x8) != 0x0 && (this.state == 0 || this.state == 1)) {
            n3 |= 0x8;
            this.readyToConnect = true;
        }
        if ((n & Net.POLLOUT) != 0x0 && (nioInterestOps & 0x4) != 0x0 && this.state == 2) {
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
                this.readerCleanup();
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
            n2 |= Net.POLLCONN;
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
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSuperclass().getName());
        sb.append('[');
        if (!this.isOpen()) {
            sb.append("closed");
        }
        else {
            synchronized (this.stateLock) {
                switch (this.state) {
                    case 0: {
                        sb.append("unconnected");
                        break;
                    }
                    case 1: {
                        sb.append("connection-pending");
                        break;
                    }
                    case 2: {
                        sb.append("connected");
                        if (!this.isInputOpen) {
                            sb.append(" ishut");
                        }
                        if (!this.isOutputOpen) {
                            sb.append(" oshut");
                            break;
                        }
                        break;
                    }
                }
                final InetSocketAddress localAddress = this.localAddress();
                if (localAddress != null) {
                    sb.append(" local=");
                    sb.append(Net.getRevealedLocalAddressAsString(localAddress));
                }
                if (this.remoteAddress() != null) {
                    sb.append(" remote=");
                    sb.append(this.remoteAddress().toString());
                }
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    private static native int checkConnect(final FileDescriptor p0, final boolean p1, final boolean p2) throws IOException;
    
    private static native int sendOutOfBandData(final FileDescriptor p0, final byte p1) throws IOException;
    
    static {
        IOUtil.load();
        SocketChannelImpl.nd = new SocketDispatcher();
    }
    
    private static class DefaultOptionsHolder
    {
        static final Set<SocketOption<?>> defaultOptions;
        
        private static Set<SocketOption<?>> defaultOptions() {
            final HashSet set = new HashSet(8);
            set.add(StandardSocketOptions.SO_SNDBUF);
            set.add(StandardSocketOptions.SO_RCVBUF);
            set.add(StandardSocketOptions.SO_KEEPALIVE);
            set.add(StandardSocketOptions.SO_REUSEADDR);
            set.add(StandardSocketOptions.SO_LINGER);
            set.add(StandardSocketOptions.TCP_NODELAY);
            set.add(StandardSocketOptions.IP_TOS);
            set.add(ExtendedSocketOption.SO_OOBINLINE);
            if (ExtendedOptionsImpl.flowSupported()) {
                set.add(ExtendedSocketOptions.SO_FLOW_SLA);
            }
            set.addAll(ExtendedOptionsHelper.keepAliveOptions());
            return (Set<SocketOption<?>>)Collections.unmodifiableSet((Set<?>)set);
        }
        
        static {
            defaultOptions = defaultOptions();
        }
    }
}
