package sun.nio.ch;

import java.util.Collections;
import java.util.Collection;
import sun.net.ExtendedOptionsHelper;
import java.util.HashSet;
import java.nio.channels.NetworkChannel;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.SocketChannel;
import sun.net.NetHooks;
import java.nio.channels.AlreadyBoundException;
import java.util.Set;
import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.net.SocketOption;
import java.nio.channels.ClosedChannelException;
import java.net.SocketAddress;
import java.io.IOException;
import java.nio.channels.spi.SelectorProvider;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.io.FileDescriptor;
import java.nio.channels.ServerSocketChannel;

class ServerSocketChannelImpl extends ServerSocketChannel implements SelChImpl
{
    private static NativeDispatcher nd;
    private final FileDescriptor fd;
    private final int fdVal;
    private volatile long thread;
    private final Object lock;
    private final Object stateLock;
    private static final int ST_UNINITIALIZED = -1;
    private static final int ST_INUSE = 0;
    private static final int ST_KILLED = 1;
    private int state;
    private InetSocketAddress localAddress;
    private boolean isReuseAddress;
    ServerSocket socket;
    
    ServerSocketChannelImpl(final SelectorProvider selectorProvider) throws IOException {
        super(selectorProvider);
        this.thread = 0L;
        this.lock = new Object();
        this.stateLock = new Object();
        this.state = -1;
        this.fd = Net.serverSocket(true);
        this.fdVal = IOUtil.fdVal(this.fd);
        this.state = 0;
    }
    
    ServerSocketChannelImpl(final SelectorProvider selectorProvider, final FileDescriptor fd, final boolean b) throws IOException {
        super(selectorProvider);
        this.thread = 0L;
        this.lock = new Object();
        this.stateLock = new Object();
        this.state = -1;
        this.fd = fd;
        this.fdVal = IOUtil.fdVal(fd);
        this.state = 0;
        if (b) {
            this.localAddress = Net.localAddress(fd);
        }
    }
    
    @Override
    public ServerSocket socket() {
        synchronized (this.stateLock) {
            if (this.socket == null) {
                this.socket = ServerSocketAdaptor.create(this);
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
            return (this.localAddress == null) ? this.localAddress : Net.getRevealedLocalAddress(Net.asInetSocketAddress(this.localAddress));
        }
    }
    
    @Override
    public <T> ServerSocketChannel setOption(final SocketOption<T> socketOption, final T t) throws IOException {
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
            }
            else {
                Net.setSocketOption(this.fd, Net.UNSPEC, socketOption, t);
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
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (socketOption == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
                return (T)Boolean.valueOf(this.isReuseAddress);
            }
            return (T)Net.getSocketOption(this.fd, Net.UNSPEC, socketOption);
        }
    }
    
    @Override
    public final Set<SocketOption<?>> supportedOptions() {
        return DefaultOptionsHolder.defaultOptions;
    }
    
    public boolean isBound() {
        synchronized (this.stateLock) {
            return this.localAddress != null;
        }
    }
    
    public InetSocketAddress localAddress() {
        synchronized (this.stateLock) {
            return this.localAddress;
        }
    }
    
    @Override
    public ServerSocketChannel bind(final SocketAddress socketAddress, final int n) throws IOException {
        synchronized (this.lock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (this.isBound()) {
                throw new AlreadyBoundException();
            }
            final InetSocketAddress inetSocketAddress = (socketAddress == null) ? new InetSocketAddress(0) : Net.checkAddress(socketAddress);
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkListen(inetSocketAddress.getPort());
            }
            NetHooks.beforeTcpBind(this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
            Net.bind(this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
            Net.listen(this.fd, (n < 1) ? 50 : n);
            synchronized (this.stateLock) {
                this.localAddress = Net.localAddress(this.fd);
            }
        }
        return this;
    }
    
    @Override
    public SocketChannel accept() throws IOException {
        synchronized (this.lock) {
            if (!this.isOpen()) {
                throw new ClosedChannelException();
            }
            if (!this.isBound()) {
                throw new NotYetBoundException();
            }
            int accept = 0;
            final FileDescriptor fileDescriptor = new FileDescriptor();
            final InetSocketAddress[] array = { null };
            try {
                this.begin();
                if (!this.isOpen()) {
                    return null;
                }
                this.thread = NativeThread.current();
                do {
                    accept = this.accept(this.fd, fileDescriptor, array);
                } while (accept == -3 && this.isOpen());
            }
            finally {
                this.thread = 0L;
                this.end(accept > 0);
                assert IOStatus.check(accept);
            }
            if (accept < 1) {
                return null;
            }
            IOUtil.configureBlocking(fileDescriptor, true);
            final InetSocketAddress inetSocketAddress = array[0];
            final SocketChannelImpl socketChannelImpl = new SocketChannelImpl(this.provider(), fileDescriptor, inetSocketAddress);
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                try {
                    securityManager.checkAccept(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
                }
                catch (final SecurityException ex) {
                    socketChannelImpl.close();
                    throw ex;
                }
            }
            return socketChannelImpl;
        }
    }
    
    @Override
    protected void implConfigureBlocking(final boolean b) throws IOException {
        IOUtil.configureBlocking(this.fd, b);
    }
    
    @Override
    protected void implCloseSelectableChannel() throws IOException {
        synchronized (this.stateLock) {
            if (this.state != 1) {
                ServerSocketChannelImpl.nd.preClose(this.fd);
            }
            final long thread = this.thread;
            if (thread != 0L) {
                NativeThread.signal(thread);
            }
            if (!this.isRegistered()) {
                this.kill();
            }
        }
    }
    
    @Override
    public void kill() throws IOException {
        synchronized (this.stateLock) {
            if (this.state == 1) {
                return;
            }
            if (this.state == -1) {
                this.state = 1;
                return;
            }
            assert !this.isOpen() && !this.isRegistered();
            ServerSocketChannelImpl.nd.close(this.fd);
            this.state = 1;
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
        if ((n & Net.POLLIN) != 0x0 && (nioInterestOps & 0x10) != 0x0) {
            n3 |= 0x10;
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
        synchronized (this.lock) {
            int poll = 0;
            try {
                this.begin();
                synchronized (this.stateLock) {
                    if (!this.isOpen()) {
                        return 0;
                    }
                    this.thread = NativeThread.current();
                }
                poll = Net.poll(this.fd, n, n2);
            }
            finally {
                this.thread = 0L;
                this.end(poll > 0);
            }
            return poll;
        }
    }
    
    @Override
    public void translateAndSetInterestOps(final int n, final SelectionKeyImpl selectionKeyImpl) {
        int n2 = 0;
        if ((n & 0x10) != 0x0) {
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
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName());
        sb.append('[');
        if (!this.isOpen()) {
            sb.append("closed");
        }
        else {
            synchronized (this.stateLock) {
                final InetSocketAddress localAddress = this.localAddress();
                if (localAddress == null) {
                    sb.append("unbound");
                }
                else {
                    sb.append(Net.getRevealedLocalAddressAsString(localAddress));
                }
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    private int accept(final FileDescriptor fileDescriptor, final FileDescriptor fileDescriptor2, final InetSocketAddress[] array) throws IOException {
        return this.accept0(fileDescriptor, fileDescriptor2, array);
    }
    
    private native int accept0(final FileDescriptor p0, final FileDescriptor p1, final InetSocketAddress[] p2) throws IOException;
    
    private static native void initIDs();
    
    static {
        IOUtil.load();
        initIDs();
        ServerSocketChannelImpl.nd = new SocketDispatcher();
    }
    
    private static class DefaultOptionsHolder
    {
        static final Set<SocketOption<?>> defaultOptions;
        
        private static Set<SocketOption<?>> defaultOptions() {
            final HashSet set = new HashSet(2);
            set.add(StandardSocketOptions.SO_RCVBUF);
            set.add(StandardSocketOptions.SO_REUSEADDR);
            set.add(StandardSocketOptions.IP_TOS);
            set.addAll(ExtendedOptionsHelper.keepAliveOptions());
            return (Set<SocketOption<?>>)Collections.unmodifiableSet((Set<?>)set);
        }
        
        static {
            defaultOptions = defaultOptions();
        }
    }
}
