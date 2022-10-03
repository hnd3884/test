package sun.nio.ch;

import java.nio.channels.InterruptedByTimeoutException;
import java.nio.BufferOverflowException;
import java.nio.channels.AsynchronousCloseException;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.nio.ByteBuffer;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.Future;
import java.nio.channels.CompletionHandler;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.nio.channels.ShutdownChannelGroupException;
import sun.misc.Unsafe;

class WindowsAsynchronousSocketChannelImpl extends AsynchronousSocketChannelImpl implements Iocp.OverlappedChannel
{
    private static final Unsafe unsafe;
    private static int addressSize;
    private static final int SIZEOF_WSABUF;
    private static final int OFFSETOF_LEN = 0;
    private static final int OFFSETOF_BUF;
    private static final int MAX_WSABUF = 16;
    private static final int SIZEOF_WSABUFARRAY;
    final long handle;
    private final Iocp iocp;
    private final int completionKey;
    private final PendingIoCache ioCache;
    private final long readBufferArray;
    private final long writeBufferArray;
    
    private static int dependsArch(final int n, final int n2) {
        return (WindowsAsynchronousSocketChannelImpl.addressSize == 4) ? n : n2;
    }
    
    WindowsAsynchronousSocketChannelImpl(final Iocp iocp, final boolean b) throws IOException {
        super(iocp);
        final long handle = IOUtil.fdVal(this.fd);
        int associate = 0;
        try {
            associate = iocp.associate(this, handle);
        }
        catch (final ShutdownChannelGroupException ex) {
            if (b) {
                closesocket0(handle);
                throw ex;
            }
        }
        catch (final IOException ex2) {
            closesocket0(handle);
            throw ex2;
        }
        this.handle = handle;
        this.iocp = iocp;
        this.completionKey = associate;
        this.ioCache = new PendingIoCache();
        this.readBufferArray = WindowsAsynchronousSocketChannelImpl.unsafe.allocateMemory(WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUFARRAY);
        this.writeBufferArray = WindowsAsynchronousSocketChannelImpl.unsafe.allocateMemory(WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUFARRAY);
    }
    
    WindowsAsynchronousSocketChannelImpl(final Iocp iocp) throws IOException {
        this(iocp, true);
    }
    
    @Override
    public AsynchronousChannelGroupImpl group() {
        return this.iocp;
    }
    
    @Override
    public <V, A> PendingFuture<V, A> getByOverlapped(final long n) {
        return this.ioCache.remove(n);
    }
    
    long handle() {
        return this.handle;
    }
    
    void setConnected(final InetSocketAddress localAddress, final InetSocketAddress remoteAddress) {
        synchronized (this.stateLock) {
            this.state = 2;
            this.localAddress = localAddress;
            this.remoteAddress = remoteAddress;
        }
    }
    
    @Override
    void implClose() throws IOException {
        closesocket0(this.handle);
        this.ioCache.close();
        WindowsAsynchronousSocketChannelImpl.unsafe.freeMemory(this.readBufferArray);
        WindowsAsynchronousSocketChannelImpl.unsafe.freeMemory(this.writeBufferArray);
        if (this.completionKey != 0) {
            this.iocp.disassociate(this.completionKey);
        }
    }
    
    @Override
    public void onCancel(final PendingFuture<?, ?> pendingFuture) {
        if (pendingFuture.getContext() instanceof ConnectTask) {
            this.killConnect();
        }
        if (pendingFuture.getContext() instanceof ReadTask) {
            this.killReading();
        }
        if (pendingFuture.getContext() instanceof WriteTask) {
            this.killWriting();
        }
    }
    
    private void doPrivilegedBind(final SocketAddress socketAddress) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    WindowsAsynchronousSocketChannelImpl.this.bind(socketAddress);
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    @Override
     <A> Future<Void> implConnect(final SocketAddress socketAddress, final A a, final CompletionHandler<Void, ? super A> completionHandler) {
        if (!this.isOpen()) {
            final ClosedChannelException ex = new ClosedChannelException();
            if (completionHandler == null) {
                return (Future<Void>)CompletedFuture.withFailure(ex);
            }
            Invoker.invoke(this, (CompletionHandler<V, ? super A>)completionHandler, a, (V)null, ex);
            return null;
        }
        else {
            final InetSocketAddress checkAddress = Net.checkAddress(socketAddress);
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkConnect(checkAddress.getAddress().getHostAddress(), checkAddress.getPort());
            }
            Throwable t = null;
            synchronized (this.stateLock) {
                if (this.state == 2) {
                    throw new AlreadyConnectedException();
                }
                if (this.state == 1) {
                    throw new ConnectionPendingException();
                }
                if (this.localAddress == null) {
                    try {
                        final InetSocketAddress inetSocketAddress = new InetSocketAddress(0);
                        if (securityManager == null) {
                            this.bind(inetSocketAddress);
                        }
                        else {
                            this.doPrivilegedBind(inetSocketAddress);
                        }
                    }
                    catch (final IOException ex2) {
                        t = ex2;
                    }
                }
                if (t == null) {
                    this.state = 1;
                }
            }
            if (t == null) {
                final PendingFuture pendingFuture = new PendingFuture<Void, Object>(this, (CompletionHandler<V, ? super A>)completionHandler, (A)a);
                final ConnectTask context = new ConnectTask(checkAddress, pendingFuture);
                pendingFuture.setContext(context);
                if (Iocp.supportsThreadAgnosticIo()) {
                    context.run();
                }
                else {
                    Invoker.invokeOnThreadInThreadPool(this, context);
                }
                return pendingFuture;
            }
            try {
                this.close();
            }
            catch (final IOException ex3) {}
            if (completionHandler == null) {
                return (Future<Void>)CompletedFuture.withFailure(t);
            }
            Invoker.invoke(this, (CompletionHandler<V, ? super A>)completionHandler, a, (V)null, t);
            return null;
        }
    }
    
    @Override
     <V extends Number, A> Future<V> implRead(final boolean b, final ByteBuffer byteBuffer, final ByteBuffer[] array, final long n, final TimeUnit timeUnit, final A a, final CompletionHandler<V, ? super A> completionHandler) {
        final PendingFuture pendingFuture = new PendingFuture(this, (CompletionHandler<V, ? super A>)completionHandler, (A)a);
        ByteBuffer[] array2;
        if (b) {
            array2 = array;
        }
        else {
            array2 = new ByteBuffer[] { byteBuffer };
        }
        final ReadTask context = new ReadTask(array2, b, pendingFuture);
        pendingFuture.setContext(context);
        if (n > 0L) {
            pendingFuture.setTimeoutTask(this.iocp.schedule(new Runnable() {
                @Override
                public void run() {
                    context.timeout();
                }
            }, n, timeUnit));
        }
        if (Iocp.supportsThreadAgnosticIo()) {
            context.run();
        }
        else {
            Invoker.invokeOnThreadInThreadPool(this, context);
        }
        return pendingFuture;
    }
    
    @Override
     <V extends Number, A> Future<V> implWrite(final boolean b, final ByteBuffer byteBuffer, final ByteBuffer[] array, final long n, final TimeUnit timeUnit, final A a, final CompletionHandler<V, ? super A> completionHandler) {
        final PendingFuture pendingFuture = new PendingFuture(this, (CompletionHandler<V, ? super A>)completionHandler, (A)a);
        ByteBuffer[] array2;
        if (b) {
            array2 = array;
        }
        else {
            array2 = new ByteBuffer[] { byteBuffer };
        }
        final WriteTask context = new WriteTask(array2, b, pendingFuture);
        pendingFuture.setContext(context);
        if (n > 0L) {
            pendingFuture.setTimeoutTask(this.iocp.schedule(new Runnable() {
                @Override
                public void run() {
                    context.timeout();
                }
            }, n, timeUnit));
        }
        if (Iocp.supportsThreadAgnosticIo()) {
            context.run();
        }
        else {
            Invoker.invokeOnThreadInThreadPool(this, context);
        }
        return pendingFuture;
    }
    
    private static native void initIDs();
    
    private static native int connect0(final long p0, final boolean p1, final InetAddress p2, final int p3, final long p4) throws IOException;
    
    private static native void updateConnectContext(final long p0) throws IOException;
    
    private static native int read0(final long p0, final int p1, final long p2, final long p3) throws IOException;
    
    private static native int write0(final long p0, final int p1, final long p2, final long p3) throws IOException;
    
    private static native void shutdown0(final long p0, final int p1) throws IOException;
    
    private static native void closesocket0(final long p0) throws IOException;
    
    static {
        unsafe = Unsafe.getUnsafe();
        WindowsAsynchronousSocketChannelImpl.addressSize = WindowsAsynchronousSocketChannelImpl.unsafe.addressSize();
        SIZEOF_WSABUF = dependsArch(8, 16);
        OFFSETOF_BUF = dependsArch(4, 8);
        SIZEOF_WSABUFARRAY = 16 * WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUF;
        IOUtil.load();
        initIDs();
    }
    
    private class ConnectTask<A> implements Runnable, Iocp.ResultHandler
    {
        private final InetSocketAddress remote;
        private final PendingFuture<Void, A> result;
        
        ConnectTask(final InetSocketAddress remote, final PendingFuture<Void, A> result) {
            this.remote = remote;
            this.result = result;
        }
        
        private void closeChannel() {
            try {
                WindowsAsynchronousSocketChannelImpl.this.close();
            }
            catch (final IOException ex) {}
        }
        
        private IOException toIOException(Throwable t) {
            if (t instanceof IOException) {
                if (t instanceof ClosedChannelException) {
                    t = new AsynchronousCloseException();
                }
                return (IOException)t;
            }
            return new IOException(t);
        }
        
        private void afterConnect() throws IOException {
            updateConnectContext(WindowsAsynchronousSocketChannelImpl.this.handle);
            synchronized (WindowsAsynchronousSocketChannelImpl.this.stateLock) {
                WindowsAsynchronousSocketChannelImpl.this.state = 2;
                WindowsAsynchronousSocketChannelImpl.this.remoteAddress = this.remote;
            }
        }
        
        @Override
        public void run() {
            long add = 0L;
            Throwable t = null;
            try {
                WindowsAsynchronousSocketChannelImpl.this.begin();
                synchronized (this.result) {
                    add = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
                    if (connect0(WindowsAsynchronousSocketChannelImpl.this.handle, Net.isIPv6Available(), this.remote.getAddress(), this.remote.getPort(), add) == -2) {
                        return;
                    }
                    this.afterConnect();
                    this.result.setResult(null);
                }
            }
            catch (final Throwable t2) {
                if (add != 0L) {
                    WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(add);
                }
                t = t2;
            }
            finally {
                WindowsAsynchronousSocketChannelImpl.this.end();
            }
            if (t != null) {
                this.closeChannel();
                this.result.setFailure(this.toIOException(t));
            }
            Invoker.invoke(this.result);
        }
        
        @Override
        public void completed(final int n, final boolean b) {
            Throwable t = null;
            try {
                WindowsAsynchronousSocketChannelImpl.this.begin();
                this.afterConnect();
                this.result.setResult(null);
            }
            catch (final Throwable t2) {
                t = t2;
            }
            finally {
                WindowsAsynchronousSocketChannelImpl.this.end();
            }
            if (t != null) {
                this.closeChannel();
                this.result.setFailure(this.toIOException(t));
            }
            if (b) {
                Invoker.invokeUnchecked(this.result);
            }
            else {
                Invoker.invoke(this.result);
            }
        }
        
        @Override
        public void failed(final int n, final IOException failure) {
            if (WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
                this.closeChannel();
                this.result.setFailure(failure);
            }
            else {
                this.result.setFailure(new AsynchronousCloseException());
            }
            Invoker.invoke(this.result);
        }
    }
    
    private class ReadTask<V, A> implements Runnable, Iocp.ResultHandler
    {
        private final ByteBuffer[] bufs;
        private final int numBufs;
        private final boolean scatteringRead;
        private final PendingFuture<V, A> result;
        private ByteBuffer[] shadow;
        
        ReadTask(final ByteBuffer[] bufs, final boolean scatteringRead, final PendingFuture<V, A> result) {
            this.bufs = bufs;
            this.numBufs = ((bufs.length > 16) ? 16 : bufs.length);
            this.scatteringRead = scatteringRead;
            this.result = result;
        }
        
        void prepareBuffers() {
            this.shadow = new ByteBuffer[this.numBufs];
            long access$300 = WindowsAsynchronousSocketChannelImpl.this.readBufferArray;
            for (int i = 0; i < this.numBufs; ++i) {
                final ByteBuffer byteBuffer = this.bufs[i];
                final int position = byteBuffer.position();
                final int limit = byteBuffer.limit();
                assert position <= limit;
                final int n = (position <= limit) ? (limit - position) : 0;
                long address;
                if (!(byteBuffer instanceof DirectBuffer)) {
                    final ByteBuffer temporaryDirectBuffer = Util.getTemporaryDirectBuffer(n);
                    this.shadow[i] = temporaryDirectBuffer;
                    address = ((DirectBuffer)temporaryDirectBuffer).address();
                }
                else {
                    this.shadow[i] = byteBuffer;
                    address = ((DirectBuffer)byteBuffer).address() + position;
                }
                WindowsAsynchronousSocketChannelImpl.unsafe.putAddress(access$300 + WindowsAsynchronousSocketChannelImpl.OFFSETOF_BUF, address);
                WindowsAsynchronousSocketChannelImpl.unsafe.putInt(access$300 + 0L, n);
                access$300 += WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUF;
            }
        }
        
        void updateBuffers(int n) {
            int i = 0;
            while (i < this.numBufs) {
                final ByteBuffer byteBuffer = this.shadow[i];
                final int position = byteBuffer.position();
                final int remaining = byteBuffer.remaining();
                if (n >= remaining) {
                    n -= remaining;
                    final int n2 = position + remaining;
                    try {
                        byteBuffer.position(n2);
                    }
                    catch (final IllegalArgumentException ex) {}
                    ++i;
                }
                else {
                    if (n <= 0) {
                        break;
                    }
                    assert position + n < 2147483647L;
                    final int n3 = position + n;
                    try {
                        byteBuffer.position(n3);
                    }
                    catch (final IllegalArgumentException ex2) {}
                    break;
                }
            }
            for (int j = 0; j < this.numBufs; ++j) {
                if (!(this.bufs[j] instanceof DirectBuffer)) {
                    this.shadow[j].flip();
                    try {
                        this.bufs[j].put(this.shadow[j]);
                    }
                    catch (final BufferOverflowException ex3) {}
                }
            }
        }
        
        void releaseBuffers() {
            for (int i = 0; i < this.numBufs; ++i) {
                if (!(this.bufs[i] instanceof DirectBuffer)) {
                    Util.releaseTemporaryDirectBuffer(this.shadow[i]);
                }
            }
        }
        
        @Override
        public void run() {
            long add = 0L;
            boolean b = false;
            boolean b2 = false;
            try {
                WindowsAsynchronousSocketChannelImpl.this.begin();
                this.prepareBuffers();
                b = true;
                add = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
                final int access$700 = read0(WindowsAsynchronousSocketChannelImpl.this.handle, this.numBufs, WindowsAsynchronousSocketChannelImpl.this.readBufferArray, add);
                if (access$700 == -2) {
                    b2 = true;
                    return;
                }
                if (access$700 != -1) {
                    throw new InternalError("Read completed immediately");
                }
                WindowsAsynchronousSocketChannelImpl.this.enableReading();
                if (this.scatteringRead) {
                    this.result.setResult((V)(-1L));
                }
                else {
                    this.result.setResult((V)(-1));
                }
            }
            catch (final Throwable failure) {
                WindowsAsynchronousSocketChannelImpl.this.enableReading();
                if (failure instanceof ClosedChannelException) {
                    failure = new AsynchronousCloseException();
                }
                if (!(failure instanceof IOException)) {
                    failure = new IOException(failure);
                }
                this.result.setFailure(failure);
            }
            finally {
                if (!b2) {
                    if (add != 0L) {
                        WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(add);
                    }
                    if (b) {
                        this.releaseBuffers();
                    }
                }
                WindowsAsynchronousSocketChannelImpl.this.end();
            }
            Invoker.invoke(this.result);
        }
        
        @Override
        public void completed(int n, final boolean b) {
            if (n == 0) {
                n = -1;
            }
            else {
                this.updateBuffers(n);
            }
            this.releaseBuffers();
            synchronized (this.result) {
                if (this.result.isDone()) {
                    return;
                }
                WindowsAsynchronousSocketChannelImpl.this.enableReading();
                if (this.scatteringRead) {
                    this.result.setResult((V)(long)n);
                }
                else {
                    this.result.setResult((V)n);
                }
            }
            if (b) {
                Invoker.invokeUnchecked(this.result);
            }
            else {
                Invoker.invoke(this.result);
            }
        }
        
        @Override
        public void failed(final int n, IOException failure) {
            this.releaseBuffers();
            if (!WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
                failure = new AsynchronousCloseException();
            }
            synchronized (this.result) {
                if (this.result.isDone()) {
                    return;
                }
                WindowsAsynchronousSocketChannelImpl.this.enableReading();
                this.result.setFailure(failure);
            }
            Invoker.invoke(this.result);
        }
        
        void timeout() {
            synchronized (this.result) {
                if (this.result.isDone()) {
                    return;
                }
                WindowsAsynchronousSocketChannelImpl.this.enableReading(true);
                this.result.setFailure(new InterruptedByTimeoutException());
            }
            Invoker.invoke(this.result);
        }
    }
    
    private class WriteTask<V, A> implements Runnable, Iocp.ResultHandler
    {
        private final ByteBuffer[] bufs;
        private final int numBufs;
        private final boolean gatheringWrite;
        private final PendingFuture<V, A> result;
        private ByteBuffer[] shadow;
        
        WriteTask(final ByteBuffer[] bufs, final boolean gatheringWrite, final PendingFuture<V, A> result) {
            this.bufs = bufs;
            this.numBufs = ((bufs.length > 16) ? 16 : bufs.length);
            this.gatheringWrite = gatheringWrite;
            this.result = result;
        }
        
        void prepareBuffers() {
            this.shadow = new ByteBuffer[this.numBufs];
            long access$800 = WindowsAsynchronousSocketChannelImpl.this.writeBufferArray;
            for (int i = 0; i < this.numBufs; ++i) {
                final ByteBuffer byteBuffer = this.bufs[i];
                final int position = byteBuffer.position();
                final int limit = byteBuffer.limit();
                assert position <= limit;
                final int n = (position <= limit) ? (limit - position) : 0;
                long address;
                if (!(byteBuffer instanceof DirectBuffer)) {
                    final ByteBuffer temporaryDirectBuffer = Util.getTemporaryDirectBuffer(n);
                    temporaryDirectBuffer.put(byteBuffer);
                    temporaryDirectBuffer.flip();
                    byteBuffer.position(position);
                    this.shadow[i] = temporaryDirectBuffer;
                    address = ((DirectBuffer)temporaryDirectBuffer).address();
                }
                else {
                    this.shadow[i] = byteBuffer;
                    address = ((DirectBuffer)byteBuffer).address() + position;
                }
                WindowsAsynchronousSocketChannelImpl.unsafe.putAddress(access$800 + WindowsAsynchronousSocketChannelImpl.OFFSETOF_BUF, address);
                WindowsAsynchronousSocketChannelImpl.unsafe.putInt(access$800 + 0L, n);
                access$800 += WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUF;
            }
        }
        
        void updateBuffers(int n) {
            int i = 0;
            while (i < this.numBufs) {
                final ByteBuffer byteBuffer = this.bufs[i];
                final int position = byteBuffer.position();
                final int limit = byteBuffer.limit();
                final int n2 = (position <= limit) ? (limit - position) : limit;
                if (n >= n2) {
                    n -= n2;
                    final int n3 = position + n2;
                    try {
                        byteBuffer.position(n3);
                    }
                    catch (final IllegalArgumentException ex) {}
                    ++i;
                }
                else {
                    if (n <= 0) {
                        break;
                    }
                    assert position + n < 2147483647L;
                    final int n4 = position + n;
                    try {
                        byteBuffer.position(n4);
                    }
                    catch (final IllegalArgumentException ex2) {}
                    break;
                }
            }
        }
        
        void releaseBuffers() {
            for (int i = 0; i < this.numBufs; ++i) {
                if (!(this.bufs[i] instanceof DirectBuffer)) {
                    Util.releaseTemporaryDirectBuffer(this.shadow[i]);
                }
            }
        }
        
        @Override
        public void run() {
            long add = 0L;
            boolean b = false;
            boolean b2 = false;
            boolean b3 = false;
            try {
                WindowsAsynchronousSocketChannelImpl.this.begin();
                this.prepareBuffers();
                b = true;
                add = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
                final int access$900 = write0(WindowsAsynchronousSocketChannelImpl.this.handle, this.numBufs, WindowsAsynchronousSocketChannelImpl.this.writeBufferArray, add);
                if (access$900 == -2) {
                    b2 = true;
                    return;
                }
                if (access$900 == -1) {
                    b3 = true;
                    throw new ClosedChannelException();
                }
                throw new InternalError("Write completed immediately");
            }
            catch (final Throwable failure) {
                WindowsAsynchronousSocketChannelImpl.this.enableWriting();
                if (!b3 && failure instanceof ClosedChannelException) {
                    failure = new AsynchronousCloseException();
                }
                if (!(failure instanceof IOException)) {
                    failure = new IOException(failure);
                }
                this.result.setFailure(failure);
            }
            finally {
                if (!b2) {
                    if (add != 0L) {
                        WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(add);
                    }
                    if (b) {
                        this.releaseBuffers();
                    }
                }
                WindowsAsynchronousSocketChannelImpl.this.end();
            }
            Invoker.invoke(this.result);
        }
        
        @Override
        public void completed(final int n, final boolean b) {
            this.updateBuffers(n);
            this.releaseBuffers();
            synchronized (this.result) {
                if (this.result.isDone()) {
                    return;
                }
                WindowsAsynchronousSocketChannelImpl.this.enableWriting();
                if (this.gatheringWrite) {
                    this.result.setResult((V)(long)n);
                }
                else {
                    this.result.setResult((V)n);
                }
            }
            if (b) {
                Invoker.invokeUnchecked(this.result);
            }
            else {
                Invoker.invoke(this.result);
            }
        }
        
        @Override
        public void failed(final int n, IOException failure) {
            this.releaseBuffers();
            if (!WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
                failure = new AsynchronousCloseException();
            }
            synchronized (this.result) {
                if (this.result.isDone()) {
                    return;
                }
                WindowsAsynchronousSocketChannelImpl.this.enableWriting();
                this.result.setFailure(failure);
            }
            Invoker.invoke(this.result);
        }
        
        void timeout() {
            synchronized (this.result) {
                if (this.result.isDone()) {
                    return;
                }
                WindowsAsynchronousSocketChannelImpl.this.enableWriting(true);
                this.result.setFailure(new InterruptedByTimeoutException());
            }
            Invoker.invoke(this.result);
        }
    }
}
