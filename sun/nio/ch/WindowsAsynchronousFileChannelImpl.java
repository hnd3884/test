package sun.nio.ch;

import java.nio.BufferOverflowException;
import sun.misc.SharedSecrets;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.NonReadableChannelException;
import java.util.concurrent.Future;
import java.nio.channels.FileLock;
import java.nio.channels.CompletionHandler;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.nio.channels.AsynchronousFileChannel;
import java.io.IOException;
import java.io.FileDescriptor;
import sun.misc.JavaIOFileDescriptorAccess;

public class WindowsAsynchronousFileChannelImpl extends AsynchronousFileChannelImpl implements Iocp.OverlappedChannel, Groupable
{
    private static final JavaIOFileDescriptorAccess fdAccess;
    private static final int ERROR_HANDLE_EOF = 38;
    private static final FileDispatcher nd;
    private final long handle;
    private final int completionKey;
    private final Iocp iocp;
    private final boolean isDefaultIocp;
    private final PendingIoCache ioCache;
    static final int NO_LOCK = -1;
    static final int LOCKED = 0;
    
    private WindowsAsynchronousFileChannelImpl(final FileDescriptor fileDescriptor, final boolean b, final boolean b2, final Iocp iocp, final boolean isDefaultIocp) throws IOException {
        super(fileDescriptor, b, b2, iocp.executor());
        this.handle = WindowsAsynchronousFileChannelImpl.fdAccess.getHandle(fileDescriptor);
        this.iocp = iocp;
        this.isDefaultIocp = isDefaultIocp;
        this.ioCache = new PendingIoCache();
        this.completionKey = iocp.associate(this, this.handle);
    }
    
    public static AsynchronousFileChannel open(final FileDescriptor fileDescriptor, final boolean b, final boolean b2, final ThreadPool threadPool) throws IOException {
        Iocp iocp;
        boolean b3;
        if (threadPool == null) {
            iocp = DefaultIocpHolder.defaultIocp;
            b3 = true;
        }
        else {
            iocp = new Iocp(null, threadPool).start();
            b3 = false;
        }
        try {
            return new WindowsAsynchronousFileChannelImpl(fileDescriptor, b, b2, iocp, b3);
        }
        catch (final IOException ex) {
            if (!b3) {
                iocp.implClose();
            }
            throw ex;
        }
    }
    
    @Override
    public <V, A> PendingFuture<V, A> getByOverlapped(final long n) {
        return this.ioCache.remove(n);
    }
    
    @Override
    public void close() throws IOException {
        this.closeLock.writeLock().lock();
        try {
            if (this.closed) {
                return;
            }
            this.closed = true;
        }
        finally {
            this.closeLock.writeLock().unlock();
        }
        this.invalidateAllLocks();
        close0(this.handle);
        this.ioCache.close();
        this.iocp.disassociate(this.completionKey);
        if (!this.isDefaultIocp) {
            this.iocp.detachFromThreadPool();
        }
    }
    
    @Override
    public AsynchronousChannelGroupImpl group() {
        return this.iocp;
    }
    
    private static IOException toIOException(Throwable t) {
        if (t instanceof IOException) {
            if (t instanceof ClosedChannelException) {
                t = new AsynchronousCloseException();
            }
            return (IOException)t;
        }
        return new IOException(t);
    }
    
    @Override
    public long size() throws IOException {
        try {
            this.begin();
            return WindowsAsynchronousFileChannelImpl.nd.size(this.fdObj);
        }
        finally {
            this.end();
        }
    }
    
    @Override
    public AsynchronousFileChannel truncate(final long n) throws IOException {
        if (n < 0L) {
            throw new IllegalArgumentException("Negative size");
        }
        if (!this.writing) {
            throw new NonWritableChannelException();
        }
        try {
            this.begin();
            if (n > WindowsAsynchronousFileChannelImpl.nd.size(this.fdObj)) {
                return this;
            }
            WindowsAsynchronousFileChannelImpl.nd.truncate(this.fdObj, n);
        }
        finally {
            this.end();
        }
        return this;
    }
    
    @Override
    public void force(final boolean b) throws IOException {
        try {
            this.begin();
            WindowsAsynchronousFileChannelImpl.nd.force(this.fdObj, b);
        }
        finally {
            this.end();
        }
    }
    
    @Override
     <A> Future<FileLock> implLock(final long n, final long n2, final boolean b, final A a, final CompletionHandler<FileLock, ? super A> completionHandler) {
        if (b && !this.reading) {
            throw new NonReadableChannelException();
        }
        if (!b && !this.writing) {
            throw new NonWritableChannelException();
        }
        final FileLockImpl addToFileLockTable = this.addToFileLockTable(n, n2, b);
        if (addToFileLockTable != null) {
            final PendingFuture pendingFuture = new PendingFuture(this, (CompletionHandler<V, ? super A>)completionHandler, (A)a);
            final LockTask context = new LockTask(n, addToFileLockTable, pendingFuture);
            pendingFuture.setContext(context);
            if (Iocp.supportsThreadAgnosticIo()) {
                context.run();
            }
            else {
                boolean b2 = false;
                try {
                    Invoker.invokeOnThreadInThreadPool(this, context);
                    b2 = true;
                }
                finally {
                    if (!b2) {
                        this.removeFromFileLockTable(addToFileLockTable);
                    }
                }
            }
            return pendingFuture;
        }
        final ClosedChannelException ex = new ClosedChannelException();
        if (completionHandler == null) {
            return (Future<FileLock>)CompletedFuture.withFailure(ex);
        }
        Invoker.invoke(this, (CompletionHandler<V, ? super A>)completionHandler, a, (V)null, ex);
        return null;
    }
    
    @Override
    public FileLock tryLock(final long n, final long n2, final boolean b) throws IOException {
        if (b && !this.reading) {
            throw new NonReadableChannelException();
        }
        if (!b && !this.writing) {
            throw new NonWritableChannelException();
        }
        final FileLockImpl addToFileLockTable = this.addToFileLockTable(n, n2, b);
        if (addToFileLockTable == null) {
            throw new ClosedChannelException();
        }
        boolean b2 = false;
        try {
            this.begin();
            if (WindowsAsynchronousFileChannelImpl.nd.lock(this.fdObj, false, n, n2, b) == -1) {
                return null;
            }
            b2 = true;
            return addToFileLockTable;
        }
        finally {
            if (!b2) {
                this.removeFromFileLockTable(addToFileLockTable);
            }
            this.end();
        }
    }
    
    @Override
    protected void implRelease(final FileLockImpl fileLockImpl) throws IOException {
        WindowsAsynchronousFileChannelImpl.nd.release(this.fdObj, fileLockImpl.position(), fileLockImpl.size());
    }
    
    @Override
     <A> Future<Integer> implRead(final ByteBuffer byteBuffer, final long n, final A a, final CompletionHandler<Integer, ? super A> completionHandler) {
        if (!this.reading) {
            throw new NonReadableChannelException();
        }
        if (n < 0L) {
            throw new IllegalArgumentException("Negative position");
        }
        if (byteBuffer.isReadOnly()) {
            throw new IllegalArgumentException("Read-only buffer");
        }
        if (!this.isOpen()) {
            final ClosedChannelException ex = new ClosedChannelException();
            if (completionHandler == null) {
                return (Future<Integer>)CompletedFuture.withFailure(ex);
            }
            Invoker.invoke(this, (CompletionHandler<V, ? super A>)completionHandler, a, (V)null, ex);
            return null;
        }
        else {
            final int position = byteBuffer.position();
            final int limit = byteBuffer.limit();
            assert position <= limit;
            final int n2 = (position <= limit) ? (limit - position) : 0;
            if (n2 != 0) {
                final PendingFuture pendingFuture = new PendingFuture<Integer, Object>(this, (CompletionHandler<Integer, ? super Object>)completionHandler, a);
                final ReadTask context = new ReadTask(byteBuffer, position, n2, n, pendingFuture);
                pendingFuture.setContext(context);
                if (Iocp.supportsThreadAgnosticIo()) {
                    context.run();
                }
                else {
                    Invoker.invokeOnThreadInThreadPool(this, context);
                }
                return (Future<Integer>)pendingFuture;
            }
            if (completionHandler == null) {
                return CompletedFuture.withResult(0);
            }
            Invoker.invoke(this, completionHandler, a, 0, null);
            return null;
        }
    }
    
    @Override
     <A> Future<Integer> implWrite(final ByteBuffer byteBuffer, final long n, final A a, final CompletionHandler<Integer, ? super A> completionHandler) {
        if (!this.writing) {
            throw new NonWritableChannelException();
        }
        if (n < 0L) {
            throw new IllegalArgumentException("Negative position");
        }
        if (!this.isOpen()) {
            final ClosedChannelException ex = new ClosedChannelException();
            if (completionHandler == null) {
                return (Future<Integer>)CompletedFuture.withFailure(ex);
            }
            Invoker.invoke(this, (CompletionHandler<V, ? super A>)completionHandler, a, (V)null, ex);
            return null;
        }
        else {
            final int position = byteBuffer.position();
            final int limit = byteBuffer.limit();
            assert position <= limit;
            final int n2 = (position <= limit) ? (limit - position) : 0;
            if (n2 != 0) {
                final PendingFuture pendingFuture = new PendingFuture<Integer, Object>(this, (CompletionHandler<Integer, ? super Object>)completionHandler, a);
                final WriteTask context = new WriteTask(byteBuffer, position, n2, n, pendingFuture);
                pendingFuture.setContext(context);
                if (Iocp.supportsThreadAgnosticIo()) {
                    context.run();
                }
                else {
                    Invoker.invokeOnThreadInThreadPool(this, context);
                }
                return (Future<Integer>)pendingFuture;
            }
            if (completionHandler == null) {
                return CompletedFuture.withResult(0);
            }
            Invoker.invoke(this, completionHandler, a, 0, null);
            return null;
        }
    }
    
    private static native int readFile(final long p0, final long p1, final int p2, final long p3, final long p4) throws IOException;
    
    private static native int writeFile(final long p0, final long p1, final int p2, final long p3, final long p4) throws IOException;
    
    private static native int lockFile(final long p0, final long p1, final long p2, final boolean p3, final long p4) throws IOException;
    
    private static native void close0(final long p0);
    
    static {
        fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
        nd = new FileDispatcherImpl();
        IOUtil.load();
    }
    
    private static class DefaultIocpHolder
    {
        static final Iocp defaultIocp;
        
        private static Iocp defaultIocp() {
            try {
                return new Iocp(null, ThreadPool.createDefault()).start();
            }
            catch (final IOException ex) {
                throw new InternalError(ex);
            }
        }
        
        static {
            defaultIocp = defaultIocp();
        }
    }
    
    private class LockTask<A> implements Runnable, Iocp.ResultHandler
    {
        private final long position;
        private final FileLockImpl fli;
        private final PendingFuture<FileLock, A> result;
        
        LockTask(final long position, final FileLockImpl fli, final PendingFuture<FileLock, A> result) {
            this.position = position;
            this.fli = fli;
            this.result = result;
        }
        
        @Override
        public void run() {
            long add = 0L;
            boolean b = false;
            try {
                WindowsAsynchronousFileChannelImpl.this.begin();
                add = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
                synchronized (this.result) {
                    if (lockFile(WindowsAsynchronousFileChannelImpl.this.handle, this.position, this.fli.size(), this.fli.isShared(), add) == -2) {
                        b = true;
                        return;
                    }
                    this.result.setResult(this.fli);
                }
            }
            catch (final Throwable t) {
                WindowsAsynchronousFileChannelImpl.this.removeFromFileLockTable(this.fli);
                this.result.setFailure(toIOException(t));
            }
            finally {
                if (!b && add != 0L) {
                    WindowsAsynchronousFileChannelImpl.this.ioCache.remove(add);
                }
                WindowsAsynchronousFileChannelImpl.this.end();
            }
            Invoker.invoke(this.result);
        }
        
        @Override
        public void completed(final int n, final boolean b) {
            this.result.setResult(this.fli);
            if (b) {
                Invoker.invokeUnchecked(this.result);
            }
            else {
                Invoker.invoke(this.result);
            }
        }
        
        @Override
        public void failed(final int n, final IOException failure) {
            WindowsAsynchronousFileChannelImpl.this.removeFromFileLockTable(this.fli);
            if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
                this.result.setFailure(failure);
            }
            else {
                this.result.setFailure(new AsynchronousCloseException());
            }
            Invoker.invoke(this.result);
        }
    }
    
    private class ReadTask<A> implements Runnable, Iocp.ResultHandler
    {
        private final ByteBuffer dst;
        private final int pos;
        private final int rem;
        private final long position;
        private final PendingFuture<Integer, A> result;
        private volatile ByteBuffer buf;
        
        ReadTask(final ByteBuffer dst, final int pos, final int rem, final long position, final PendingFuture<Integer, A> result) {
            this.dst = dst;
            this.pos = pos;
            this.rem = rem;
            this.position = position;
            this.result = result;
        }
        
        void releaseBufferIfSubstituted() {
            if (this.buf != this.dst) {
                Util.releaseTemporaryDirectBuffer(this.buf);
            }
        }
        
        void updatePosition(final int n) {
            if (n > 0) {
                if (this.buf == this.dst) {
                    try {
                        this.dst.position(this.pos + n);
                    }
                    catch (final IllegalArgumentException ex) {}
                }
                else {
                    this.buf.position(n).flip();
                    try {
                        this.dst.put(this.buf);
                    }
                    catch (final BufferOverflowException ex2) {}
                }
            }
        }
        
        @Override
        public void run() {
            long add = 0L;
            long address;
            if (this.dst instanceof DirectBuffer) {
                this.buf = this.dst;
                address = ((DirectBuffer)this.dst).address() + this.pos;
            }
            else {
                this.buf = Util.getTemporaryDirectBuffer(this.rem);
                address = ((DirectBuffer)this.buf).address();
            }
            boolean b = false;
            try {
                WindowsAsynchronousFileChannelImpl.this.begin();
                add = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
                final int access$400 = readFile(WindowsAsynchronousFileChannelImpl.this.handle, address, this.rem, this.position, add);
                if (access$400 == -2) {
                    b = true;
                    return;
                }
                if (access$400 != -1) {
                    throw new InternalError("Unexpected result: " + access$400);
                }
                this.result.setResult(access$400);
            }
            catch (final Throwable t) {
                this.result.setFailure(toIOException(t));
            }
            finally {
                if (!b) {
                    if (add != 0L) {
                        WindowsAsynchronousFileChannelImpl.this.ioCache.remove(add);
                    }
                    this.releaseBufferIfSubstituted();
                }
                WindowsAsynchronousFileChannelImpl.this.end();
            }
            Invoker.invoke(this.result);
        }
        
        @Override
        public void completed(final int n, final boolean b) {
            this.updatePosition(n);
            this.releaseBufferIfSubstituted();
            this.result.setResult(n);
            if (b) {
                Invoker.invokeUnchecked(this.result);
            }
            else {
                Invoker.invoke(this.result);
            }
        }
        
        @Override
        public void failed(final int n, final IOException failure) {
            if (n == 38) {
                this.completed(-1, false);
            }
            else {
                this.releaseBufferIfSubstituted();
                if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
                    this.result.setFailure(failure);
                }
                else {
                    this.result.setFailure(new AsynchronousCloseException());
                }
                Invoker.invoke(this.result);
            }
        }
    }
    
    private class WriteTask<A> implements Runnable, Iocp.ResultHandler
    {
        private final ByteBuffer src;
        private final int pos;
        private final int rem;
        private final long position;
        private final PendingFuture<Integer, A> result;
        private volatile ByteBuffer buf;
        
        WriteTask(final ByteBuffer src, final int pos, final int rem, final long position, final PendingFuture<Integer, A> result) {
            this.src = src;
            this.pos = pos;
            this.rem = rem;
            this.position = position;
            this.result = result;
        }
        
        void releaseBufferIfSubstituted() {
            if (this.buf != this.src) {
                Util.releaseTemporaryDirectBuffer(this.buf);
            }
        }
        
        void updatePosition(final int n) {
            if (n > 0) {
                try {
                    this.src.position(this.pos + n);
                }
                catch (final IllegalArgumentException ex) {}
            }
        }
        
        @Override
        public void run() {
            long add = 0L;
            long address;
            if (this.src instanceof DirectBuffer) {
                this.buf = this.src;
                address = ((DirectBuffer)this.src).address() + this.pos;
            }
            else {
                (this.buf = Util.getTemporaryDirectBuffer(this.rem)).put(this.src);
                this.buf.flip();
                this.src.position(this.pos);
                address = ((DirectBuffer)this.buf).address();
            }
            try {
                WindowsAsynchronousFileChannelImpl.this.begin();
                add = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
                final int access$500 = writeFile(WindowsAsynchronousFileChannelImpl.this.handle, address, this.rem, this.position, add);
                if (access$500 == -2) {
                    return;
                }
                throw new InternalError("Unexpected result: " + access$500);
            }
            catch (final Throwable t) {
                this.result.setFailure(toIOException(t));
                if (add != 0L) {
                    WindowsAsynchronousFileChannelImpl.this.ioCache.remove(add);
                }
                this.releaseBufferIfSubstituted();
            }
            finally {
                WindowsAsynchronousFileChannelImpl.this.end();
            }
            Invoker.invoke(this.result);
        }
        
        @Override
        public void completed(final int n, final boolean b) {
            this.updatePosition(n);
            this.releaseBufferIfSubstituted();
            this.result.setResult(n);
            if (b) {
                Invoker.invokeUnchecked(this.result);
            }
            else {
                Invoker.invoke(this.result);
            }
        }
        
        @Override
        public void failed(final int n, final IOException failure) {
            this.releaseBufferIfSubstituted();
            if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
                this.result.setFailure(failure);
            }
            else {
                this.result.setFailure(new AsynchronousCloseException());
            }
            Invoker.invoke(this.result);
        }
    }
}
