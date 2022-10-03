package sun.nio.ch;

import java.nio.channels.ShutdownChannelGroupException;
import java.nio.channels.AsynchronousCloseException;
import java.net.InetSocketAddress;
import java.security.PrivilegedAction;
import java.security.AccessControlContext;
import java.nio.channels.AcceptPendingException;
import java.security.AccessController;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.Future;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.misc.Unsafe;

class WindowsAsynchronousServerSocketChannelImpl extends AsynchronousServerSocketChannelImpl implements Iocp.OverlappedChannel
{
    private static final Unsafe unsafe;
    private static final int DATA_BUFFER_SIZE = 88;
    private final long handle;
    private final int completionKey;
    private final Iocp iocp;
    private final PendingIoCache ioCache;
    private final long dataBuffer;
    private AtomicBoolean accepting;
    
    WindowsAsynchronousServerSocketChannelImpl(final Iocp iocp) throws IOException {
        super(iocp);
        this.accepting = new AtomicBoolean();
        final long handle = IOUtil.fdVal(this.fd);
        int associate;
        try {
            associate = iocp.associate(this, handle);
        }
        catch (final IOException ex) {
            closesocket0(handle);
            throw ex;
        }
        this.handle = handle;
        this.completionKey = associate;
        this.iocp = iocp;
        this.ioCache = new PendingIoCache();
        this.dataBuffer = WindowsAsynchronousServerSocketChannelImpl.unsafe.allocateMemory(88L);
    }
    
    @Override
    public <V, A> PendingFuture<V, A> getByOverlapped(final long n) {
        return this.ioCache.remove(n);
    }
    
    @Override
    void implClose() throws IOException {
        closesocket0(this.handle);
        this.ioCache.close();
        this.iocp.disassociate(this.completionKey);
        WindowsAsynchronousServerSocketChannelImpl.unsafe.freeMemory(this.dataBuffer);
    }
    
    @Override
    public AsynchronousChannelGroupImpl group() {
        return this.iocp;
    }
    
    @Override
    Future<AsynchronousSocketChannel> implAccept(final Object o, final CompletionHandler<AsynchronousSocketChannel, Object> completionHandler) {
        if (!this.isOpen()) {
            final ClosedChannelException ex = new ClosedChannelException();
            if (completionHandler == null) {
                return (Future<AsynchronousSocketChannel>)CompletedFuture.withFailure(ex);
            }
            Invoker.invokeIndirectly(this, (CompletionHandler<V, ? super Object>)completionHandler, o, (V)null, ex);
            return null;
        }
        else {
            if (this.isAcceptKilled()) {
                throw new RuntimeException("Accept not allowed due to cancellation");
            }
            if (this.localAddress == null) {
                throw new NotYetBoundException();
            }
            WindowsAsynchronousSocketChannelImpl windowsAsynchronousSocketChannelImpl = null;
            Throwable t = null;
            try {
                this.begin();
                windowsAsynchronousSocketChannelImpl = new WindowsAsynchronousSocketChannelImpl(this.iocp, false);
            }
            catch (final IOException ex2) {
                t = ex2;
            }
            finally {
                this.end();
            }
            if (t != null) {
                if (completionHandler == null) {
                    return (Future<AsynchronousSocketChannel>)CompletedFuture.withFailure(t);
                }
                Invoker.invokeIndirectly(this, (CompletionHandler<V, ? super Object>)completionHandler, o, (V)null, t);
                return null;
            }
            else {
                final AccessControlContext accessControlContext = (System.getSecurityManager() == null) ? null : AccessController.getContext();
                final PendingFuture pendingFuture = new PendingFuture<AsynchronousSocketChannel, Object>(this, completionHandler, o);
                final AcceptTask context = new AcceptTask(windowsAsynchronousSocketChannelImpl, accessControlContext, (PendingFuture<AsynchronousSocketChannel, Object>)pendingFuture);
                pendingFuture.setContext(context);
                if (!this.accepting.compareAndSet(false, true)) {
                    throw new AcceptPendingException();
                }
                if (Iocp.supportsThreadAgnosticIo()) {
                    context.run();
                }
                else {
                    Invoker.invokeOnThreadInThreadPool(this, context);
                }
                return (Future<AsynchronousSocketChannel>)pendingFuture;
            }
        }
    }
    
    private static native void initIDs();
    
    private static native int accept0(final long p0, final long p1, final long p2, final long p3) throws IOException;
    
    private static native void updateAcceptContext(final long p0, final long p1) throws IOException;
    
    private static native void closesocket0(final long p0) throws IOException;
    
    static {
        unsafe = Unsafe.getUnsafe();
        IOUtil.load();
        initIDs();
    }
    
    private class AcceptTask implements Runnable, Iocp.ResultHandler
    {
        private final WindowsAsynchronousSocketChannelImpl channel;
        private final AccessControlContext acc;
        private final PendingFuture<AsynchronousSocketChannel, Object> result;
        
        AcceptTask(final WindowsAsynchronousSocketChannelImpl channel, final AccessControlContext acc, final PendingFuture<AsynchronousSocketChannel, Object> result) {
            this.channel = channel;
            this.acc = acc;
            this.result = result;
        }
        
        void enableAccept() {
            WindowsAsynchronousServerSocketChannelImpl.this.accepting.set(false);
        }
        
        void closeChildChannel() {
            try {
                this.channel.close();
            }
            catch (final IOException ex) {}
        }
        
        void finishAccept() throws IOException {
            updateAcceptContext(WindowsAsynchronousServerSocketChannelImpl.this.handle, this.channel.handle());
            final InetSocketAddress localAddress = Net.localAddress(this.channel.fd);
            final InetSocketAddress remoteAddress = Net.remoteAddress(this.channel.fd);
            this.channel.setConnected(localAddress, remoteAddress);
            if (this.acc != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        System.getSecurityManager().checkAccept(remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort());
                        return null;
                    }
                }, this.acc);
            }
        }
        
        @Override
        public void run() {
            long add = 0L;
            try {
                WindowsAsynchronousServerSocketChannelImpl.this.begin();
                try {
                    this.channel.begin();
                    synchronized (this.result) {
                        add = WindowsAsynchronousServerSocketChannelImpl.this.ioCache.add(this.result);
                        if (accept0(WindowsAsynchronousServerSocketChannelImpl.this.handle, this.channel.handle(), add, WindowsAsynchronousServerSocketChannelImpl.this.dataBuffer) == -2) {
                            return;
                        }
                        this.finishAccept();
                        this.enableAccept();
                        this.result.setResult(this.channel);
                    }
                }
                finally {
                    this.channel.end();
                }
            }
            catch (final Throwable failure) {
                if (add != 0L) {
                    WindowsAsynchronousServerSocketChannelImpl.this.ioCache.remove(add);
                }
                this.closeChildChannel();
                if (failure instanceof ClosedChannelException) {
                    failure = new AsynchronousCloseException();
                }
                if (!(failure instanceof IOException) && !(failure instanceof SecurityException)) {
                    failure = new IOException(failure);
                }
                this.enableAccept();
                this.result.setFailure(failure);
            }
            finally {
                WindowsAsynchronousServerSocketChannelImpl.this.end();
            }
            if (this.result.isCancelled()) {
                this.closeChildChannel();
            }
            Invoker.invokeIndirectly(this.result);
        }
        
        @Override
        public void completed(final int n, final boolean b) {
            try {
                if (WindowsAsynchronousServerSocketChannelImpl.this.iocp.isShutdown()) {
                    throw new IOException(new ShutdownChannelGroupException());
                }
                try {
                    WindowsAsynchronousServerSocketChannelImpl.this.begin();
                    try {
                        this.channel.begin();
                        this.finishAccept();
                    }
                    finally {
                        this.channel.end();
                    }
                }
                finally {
                    WindowsAsynchronousServerSocketChannelImpl.this.end();
                }
                this.enableAccept();
                this.result.setResult(this.channel);
            }
            catch (final Throwable failure) {
                this.enableAccept();
                this.closeChildChannel();
                if (failure instanceof ClosedChannelException) {
                    failure = new AsynchronousCloseException();
                }
                if (!(failure instanceof IOException) && !(failure instanceof SecurityException)) {
                    failure = new IOException(failure);
                }
                this.result.setFailure(failure);
            }
            if (this.result.isCancelled()) {
                this.closeChildChannel();
            }
            Invoker.invokeIndirectly(this.result);
        }
        
        @Override
        public void failed(final int n, final IOException failure) {
            this.enableAccept();
            this.closeChildChannel();
            if (WindowsAsynchronousServerSocketChannelImpl.this.isOpen()) {
                this.result.setFailure(failure);
            }
            else {
                this.result.setFailure(new AsynchronousCloseException());
            }
            Invoker.invokeIndirectly(this.result);
        }
    }
}
