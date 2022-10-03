package sun.nio.ch;

import java.util.Collections;
import java.util.Collection;
import sun.net.ExtendedOptionsHelper;
import jdk.net.ExtendedSocketOptions;
import sun.net.ExtendedOptionsImpl;
import java.util.HashSet;
import java.nio.channels.NetworkChannel;
import java.util.Set;
import java.net.StandardSocketOptions;
import java.net.SocketOption;
import sun.net.NetHooks;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.WritePendingException;
import java.nio.channels.ReadPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.AsynchronousChannel;
import java.util.concurrent.TimeUnit;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import java.nio.channels.CompletionHandler;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.net.InetSocketAddress;
import java.io.FileDescriptor;
import java.nio.channels.AsynchronousSocketChannel;

abstract class AsynchronousSocketChannelImpl extends AsynchronousSocketChannel implements Cancellable, Groupable
{
    protected final FileDescriptor fd;
    protected final Object stateLock;
    protected volatile InetSocketAddress localAddress;
    protected volatile InetSocketAddress remoteAddress;
    static final int ST_UNINITIALIZED = -1;
    static final int ST_UNCONNECTED = 0;
    static final int ST_PENDING = 1;
    static final int ST_CONNECTED = 2;
    protected volatile int state;
    private final Object readLock;
    private boolean reading;
    private boolean readShutdown;
    private boolean readKilled;
    private final Object writeLock;
    private boolean writing;
    private boolean writeShutdown;
    private boolean writeKilled;
    private final ReadWriteLock closeLock;
    private volatile boolean open;
    private boolean isReuseAddress;
    
    AsynchronousSocketChannelImpl(final AsynchronousChannelGroupImpl asynchronousChannelGroupImpl) throws IOException {
        super(asynchronousChannelGroupImpl.provider());
        this.stateLock = new Object();
        this.localAddress = null;
        this.remoteAddress = null;
        this.state = -1;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.closeLock = new ReentrantReadWriteLock();
        this.open = true;
        this.fd = Net.socket(true);
        this.state = 0;
    }
    
    AsynchronousSocketChannelImpl(final AsynchronousChannelGroupImpl asynchronousChannelGroupImpl, final FileDescriptor fd, final InetSocketAddress remoteAddress) throws IOException {
        super(asynchronousChannelGroupImpl.provider());
        this.stateLock = new Object();
        this.localAddress = null;
        this.remoteAddress = null;
        this.state = -1;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.closeLock = new ReentrantReadWriteLock();
        this.open = true;
        this.fd = fd;
        this.state = 2;
        this.localAddress = Net.localAddress(fd);
        this.remoteAddress = remoteAddress;
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
    
    final void enableReading(final boolean b) {
        synchronized (this.readLock) {
            this.reading = false;
            if (b) {
                this.readKilled = true;
            }
        }
    }
    
    final void enableReading() {
        this.enableReading(false);
    }
    
    final void enableWriting(final boolean b) {
        synchronized (this.writeLock) {
            this.writing = false;
            if (b) {
                this.writeKilled = true;
            }
        }
    }
    
    final void enableWriting() {
        this.enableWriting(false);
    }
    
    final void killReading() {
        synchronized (this.readLock) {
            this.readKilled = true;
        }
    }
    
    final void killWriting() {
        synchronized (this.writeLock) {
            this.writeKilled = true;
        }
    }
    
    final void killConnect() {
        this.killReading();
        this.killWriting();
    }
    
    abstract <A> Future<Void> implConnect(final SocketAddress p0, final A p1, final CompletionHandler<Void, ? super A> p2);
    
    @Override
    public final Future<Void> connect(final SocketAddress socketAddress) {
        return this.implConnect(socketAddress, (Object)null, null);
    }
    
    @Override
    public final <A> void connect(final SocketAddress socketAddress, final A a, final CompletionHandler<Void, ? super A> completionHandler) {
        if (completionHandler == null) {
            throw new NullPointerException("'handler' is null");
        }
        this.implConnect(socketAddress, a, completionHandler);
    }
    
    abstract <V extends Number, A> Future<V> implRead(final boolean p0, final ByteBuffer p1, final ByteBuffer[] p2, final long p3, final TimeUnit p4, final A p5, final CompletionHandler<V, ? super A> p6);
    
    private <V extends Number, A> Future<V> read(final boolean b, final ByteBuffer byteBuffer, final ByteBuffer[] array, final long n, final TimeUnit timeUnit, final A a, final CompletionHandler<V, ? super A> completionHandler) {
        if (!this.isOpen()) {
            final ClosedChannelException ex = new ClosedChannelException();
            if (completionHandler == null) {
                return (Future<V>)CompletedFuture.withFailure(ex);
            }
            Invoker.invoke(this, completionHandler, a, (V)null, ex);
            return null;
        }
        else {
            if (this.remoteAddress == null) {
                throw new NotYetConnectedException();
            }
            final boolean b2 = b || byteBuffer.hasRemaining();
            boolean b3 = false;
            synchronized (this.readLock) {
                if (this.readKilled) {
                    throw new IllegalStateException("Reading not allowed due to timeout or cancellation");
                }
                if (this.reading) {
                    throw new ReadPendingException();
                }
                if (this.readShutdown) {
                    b3 = true;
                }
                else if (b2) {
                    this.reading = true;
                }
            }
            if (!b3 && b2) {
                return (Future<V>)this.implRead(b, byteBuffer, array, n, timeUnit, (Object)a, (CompletionHandler<V, ? super Object>)completionHandler);
            }
            Number value;
            if (b) {
                value = (b3 ? -1L : 0L);
            }
            else {
                value = (b3 ? -1 : 0);
            }
            if (completionHandler == null) {
                return (Future<V>)CompletedFuture.withResult(value);
            }
            Invoker.invoke(this, (CompletionHandler<Long, ? super A>)completionHandler, a, (Long)value, null);
            return null;
        }
    }
    
    @Override
    public final Future<Integer> read(final ByteBuffer byteBuffer) {
        if (byteBuffer.isReadOnly()) {
            throw new IllegalArgumentException("Read-only buffer");
        }
        return this.read(false, byteBuffer, null, 0L, TimeUnit.MILLISECONDS, (Object)null, (CompletionHandler<Integer, ? super Object>)null);
    }
    
    @Override
    public final <A> void read(final ByteBuffer byteBuffer, final long n, final TimeUnit timeUnit, final A a, final CompletionHandler<Integer, ? super A> completionHandler) {
        if (completionHandler == null) {
            throw new NullPointerException("'handler' is null");
        }
        if (byteBuffer.isReadOnly()) {
            throw new IllegalArgumentException("Read-only buffer");
        }
        this.read(false, byteBuffer, null, n, timeUnit, a, (CompletionHandler<Number, ? super A>)completionHandler);
    }
    
    @Override
    public final <A> void read(final ByteBuffer[] array, final int n, final int n2, final long n3, final TimeUnit timeUnit, final A a, final CompletionHandler<Long, ? super A> completionHandler) {
        if (completionHandler == null) {
            throw new NullPointerException("'handler' is null");
        }
        if (n < 0 || n2 < 0 || n > array.length - n2) {
            throw new IndexOutOfBoundsException();
        }
        final ByteBuffer[] subsequence = Util.subsequence(array, n, n2);
        for (int i = 0; i < subsequence.length; ++i) {
            if (subsequence[i].isReadOnly()) {
                throw new IllegalArgumentException("Read-only buffer");
            }
        }
        this.read(true, null, subsequence, n3, timeUnit, a, (CompletionHandler<Number, ? super A>)completionHandler);
    }
    
    abstract <V extends Number, A> Future<V> implWrite(final boolean p0, final ByteBuffer p1, final ByteBuffer[] p2, final long p3, final TimeUnit p4, final A p5, final CompletionHandler<V, ? super A> p6);
    
    private <V extends Number, A> Future<V> write(final boolean b, final ByteBuffer byteBuffer, final ByteBuffer[] array, final long n, final TimeUnit timeUnit, final A a, final CompletionHandler<V, ? super A> completionHandler) {
        final boolean b2 = b || byteBuffer.hasRemaining();
        boolean b3 = false;
        if (this.isOpen()) {
            if (this.remoteAddress == null) {
                throw new NotYetConnectedException();
            }
            synchronized (this.writeLock) {
                if (this.writeKilled) {
                    throw new IllegalStateException("Writing not allowed due to timeout or cancellation");
                }
                if (this.writing) {
                    throw new WritePendingException();
                }
                if (this.writeShutdown) {
                    b3 = true;
                }
                else if (b2) {
                    this.writing = true;
                }
            }
        }
        else {
            b3 = true;
        }
        if (b3) {
            final ClosedChannelException ex = new ClosedChannelException();
            if (completionHandler == null) {
                return (Future<V>)CompletedFuture.withFailure(ex);
            }
            Invoker.invoke(this, completionHandler, a, (V)null, ex);
            return null;
        }
        else {
            if (b2) {
                return (Future<V>)this.implWrite(b, byteBuffer, array, n, timeUnit, (Object)a, (CompletionHandler<V, ? super Object>)completionHandler);
            }
            final Number n2 = b ? Long.valueOf(0L) : Integer.valueOf(0);
            if (completionHandler == null) {
                return (Future<V>)CompletedFuture.withResult(n2);
            }
            Invoker.invoke(this, (CompletionHandler<Number, ? super A>)completionHandler, a, n2, null);
            return null;
        }
    }
    
    @Override
    public final Future<Integer> write(final ByteBuffer byteBuffer) {
        return this.write(false, byteBuffer, null, 0L, TimeUnit.MILLISECONDS, (Object)null, (CompletionHandler<Integer, ? super Object>)null);
    }
    
    @Override
    public final <A> void write(final ByteBuffer byteBuffer, final long n, final TimeUnit timeUnit, final A a, final CompletionHandler<Integer, ? super A> completionHandler) {
        if (completionHandler == null) {
            throw new NullPointerException("'handler' is null");
        }
        this.write(false, byteBuffer, null, n, timeUnit, a, (CompletionHandler<Number, ? super A>)completionHandler);
    }
    
    @Override
    public final <A> void write(ByteBuffer[] subsequence, final int n, final int n2, final long n3, final TimeUnit timeUnit, final A a, final CompletionHandler<Long, ? super A> completionHandler) {
        if (completionHandler == null) {
            throw new NullPointerException("'handler' is null");
        }
        if (n < 0 || n2 < 0 || n > subsequence.length - n2) {
            throw new IndexOutOfBoundsException();
        }
        subsequence = Util.subsequence(subsequence, n, n2);
        this.write(true, null, subsequence, n3, timeUnit, a, (CompletionHandler<Number, ? super A>)completionHandler);
    }
    
    @Override
    public final AsynchronousSocketChannel bind(final SocketAddress socketAddress) throws IOException {
        try {
            this.begin();
            synchronized (this.stateLock) {
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
    public final <T> AsynchronousSocketChannel setOption(final SocketOption<T> socketOption, final T t) throws IOException {
        if (socketOption == null) {
            throw new NullPointerException();
        }
        if (!this.supportedOptions().contains(socketOption)) {
            throw new UnsupportedOperationException("'" + socketOption + "' not supported");
        }
        try {
            this.begin();
            if (this.writeShutdown) {
                throw new IOException("Connection has been shutdown for writing");
            }
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
    public final SocketAddress getRemoteAddress() throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        return this.remoteAddress;
    }
    
    @Override
    public final AsynchronousSocketChannel shutdownInput() throws IOException {
        try {
            this.begin();
            if (this.remoteAddress == null) {
                throw new NotYetConnectedException();
            }
            synchronized (this.readLock) {
                if (!this.readShutdown) {
                    Net.shutdown(this.fd, 0);
                    this.readShutdown = true;
                }
            }
        }
        finally {
            this.end();
        }
        return this;
    }
    
    @Override
    public final AsynchronousSocketChannel shutdownOutput() throws IOException {
        try {
            this.begin();
            if (this.remoteAddress == null) {
                throw new NotYetConnectedException();
            }
            synchronized (this.writeLock) {
                if (!this.writeShutdown) {
                    Net.shutdown(this.fd, 1);
                    this.writeShutdown = true;
                }
            }
        }
        finally {
            this.end();
        }
        return this;
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append('[');
        synchronized (this.stateLock) {
            if (!this.isOpen()) {
                sb.append("closed");
            }
            else {
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
                        if (this.readShutdown) {
                            sb.append(" ishut");
                        }
                        if (this.writeShutdown) {
                            sb.append(" oshut");
                            break;
                        }
                        break;
                    }
                }
                if (this.localAddress != null) {
                    sb.append(" local=");
                    sb.append(Net.getRevealedLocalAddressAsString(this.localAddress));
                }
                if (this.remoteAddress != null) {
                    sb.append(" remote=");
                    sb.append(this.remoteAddress.toString());
                }
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    private static class DefaultOptionsHolder
    {
        static final Set<SocketOption<?>> defaultOptions;
        
        private static Set<SocketOption<?>> defaultOptions() {
            final HashSet set = new HashSet(5);
            set.add(StandardSocketOptions.SO_SNDBUF);
            set.add(StandardSocketOptions.SO_RCVBUF);
            set.add(StandardSocketOptions.SO_KEEPALIVE);
            set.add(StandardSocketOptions.SO_REUSEADDR);
            set.add(StandardSocketOptions.TCP_NODELAY);
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
