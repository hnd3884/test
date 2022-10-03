package sun.nio.ch;

import java.util.Collections;
import java.util.Collection;
import sun.net.ExtendedOptionsHelper;
import java.util.HashSet;
import java.nio.channels.NetworkChannel;
import java.util.Set;
import java.net.StandardSocketOptions;
import java.net.SocketOption;
import sun.net.NetHooks;
import java.nio.channels.AlreadyBoundException;
import java.net.SocketAddress;
import java.util.concurrent.Future;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.net.InetSocketAddress;
import java.io.FileDescriptor;
import java.nio.channels.AsynchronousServerSocketChannel;

abstract class AsynchronousServerSocketChannelImpl extends AsynchronousServerSocketChannel implements Cancellable, Groupable
{
    protected final FileDescriptor fd;
    protected volatile InetSocketAddress localAddress;
    private final Object stateLock;
    private ReadWriteLock closeLock;
    private volatile boolean open;
    private volatile boolean acceptKilled;
    private boolean isReuseAddress;
    
    AsynchronousServerSocketChannelImpl(final AsynchronousChannelGroupImpl asynchronousChannelGroupImpl) {
        super(asynchronousChannelGroupImpl.provider());
        this.localAddress = null;
        this.stateLock = new Object();
        this.closeLock = new ReentrantReadWriteLock();
        this.open = true;
        this.fd = Net.serverSocket(true);
    }
    
    @Override
    public final boolean isOpen() {
        return this.open;
    }
    
    final void begin() throws IOException {
        this.closeLock.readLock().lock();
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
    }
    
    final void end() {
        this.closeLock.readLock().unlock();
    }
    
    abstract void implClose() throws IOException;
    
    @Override
    public final void close() throws IOException {
        this.closeLock.writeLock().lock();
        try {
            if (!this.open) {
                return;
            }
            this.open = false;
        }
        finally {
            this.closeLock.writeLock().unlock();
        }
        this.implClose();
    }
    
    abstract Future<AsynchronousSocketChannel> implAccept(final Object p0, final CompletionHandler<AsynchronousSocketChannel, Object> p1);
    
    @Override
    public final Future<AsynchronousSocketChannel> accept() {
        return this.implAccept(null, null);
    }
    
    @Override
    public final <A> void accept(final A a, final CompletionHandler<AsynchronousSocketChannel, ? super A> completionHandler) {
        if (completionHandler == null) {
            throw new NullPointerException("'handler' is null");
        }
        this.implAccept(a, (CompletionHandler<AsynchronousSocketChannel, Object>)completionHandler);
    }
    
    final boolean isAcceptKilled() {
        return this.acceptKilled;
    }
    
    @Override
    public final void onCancel(final PendingFuture<?, ?> pendingFuture) {
        this.acceptKilled = true;
    }
    
    @Override
    public final AsynchronousServerSocketChannel bind(final SocketAddress socketAddress, final int n) throws IOException {
        final InetSocketAddress inetSocketAddress = (socketAddress == null) ? new InetSocketAddress(0) : Net.checkAddress(socketAddress);
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkListen(inetSocketAddress.getPort());
        }
        try {
            this.begin();
            synchronized (this.stateLock) {
                if (this.localAddress != null) {
                    throw new AlreadyBoundException();
                }
                NetHooks.beforeTcpBind(this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
                Net.bind(this.fd, inetSocketAddress.getAddress(), inetSocketAddress.getPort());
                Net.listen(this.fd, (n < 1) ? 50 : n);
                this.localAddress = Net.localAddress(this.fd);
            }
        }
        finally {
            this.end();
        }
        return this;
    }
    
    @Override
    public final SocketAddress getLocalAddress() throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        return Net.getRevealedLocalAddress(this.localAddress);
    }
    
    @Override
    public final <T> AsynchronousServerSocketChannel setOption(final SocketOption<T> socketOption, final T t) throws IOException {
        if (socketOption == null) {
            throw new NullPointerException();
        }
        if (!this.supportedOptions().contains(socketOption)) {
            throw new UnsupportedOperationException("'" + socketOption + "' not supported");
        }
        try {
            this.begin();
            if (socketOption == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
                this.isReuseAddress = (boolean)t;
            }
            else {
                Net.setSocketOption(this.fd, Net.UNSPEC, socketOption, t);
            }
            return this;
        }
        finally {
            this.end();
        }
    }
    
    @Override
    public final <T> T getOption(final SocketOption<T> socketOption) throws IOException {
        if (socketOption == null) {
            throw new NullPointerException();
        }
        if (!this.supportedOptions().contains(socketOption)) {
            throw new UnsupportedOperationException("'" + socketOption + "' not supported");
        }
        try {
            this.begin();
            if (socketOption == StandardSocketOptions.SO_REUSEADDR && Net.useExclusiveBind()) {
                return (T)Boolean.valueOf(this.isReuseAddress);
            }
            return (T)Net.getSocketOption(this.fd, Net.UNSPEC, socketOption);
        }
        finally {
            this.end();
        }
    }
    
    @Override
    public final Set<SocketOption<?>> supportedOptions() {
        return DefaultOptionsHolder.defaultOptions;
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append('[');
        if (!this.isOpen()) {
            sb.append("closed");
        }
        else if (this.localAddress == null) {
            sb.append("unbound");
        }
        else {
            sb.append(Net.getRevealedLocalAddressAsString(this.localAddress));
        }
        sb.append(']');
        return sb.toString();
    }
    
    private static class DefaultOptionsHolder
    {
        static final Set<SocketOption<?>> defaultOptions;
        
        private static Set<SocketOption<?>> defaultOptions() {
            final HashSet set = new HashSet(2);
            set.add(StandardSocketOptions.SO_RCVBUF);
            set.add(StandardSocketOptions.SO_REUSEADDR);
            set.addAll(ExtendedOptionsHelper.keepAliveOptions());
            return (Set<SocketOption<?>>)Collections.unmodifiableSet((Set<?>)set);
        }
        
        static {
            defaultOptions = defaultOptions();
        }
    }
}
