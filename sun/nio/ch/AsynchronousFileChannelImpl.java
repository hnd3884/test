package sun.nio.ch;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.nio.channels.Channel;
import java.util.concurrent.Future;
import java.nio.channels.FileLock;
import java.nio.channels.CompletionHandler;
import java.nio.channels.AsynchronousCloseException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ExecutorService;
import java.io.FileDescriptor;
import java.util.concurrent.locks.ReadWriteLock;
import java.nio.channels.AsynchronousFileChannel;

abstract class AsynchronousFileChannelImpl extends AsynchronousFileChannel
{
    protected final ReadWriteLock closeLock;
    protected volatile boolean closed;
    protected final FileDescriptor fdObj;
    protected final boolean reading;
    protected final boolean writing;
    protected final ExecutorService executor;
    private volatile FileLockTable fileLockTable;
    
    protected AsynchronousFileChannelImpl(final FileDescriptor fdObj, final boolean reading, final boolean writing, final ExecutorService executor) {
        this.closeLock = new ReentrantReadWriteLock();
        this.fdObj = fdObj;
        this.reading = reading;
        this.writing = writing;
        this.executor = executor;
    }
    
    final ExecutorService executor() {
        return this.executor;
    }
    
    @Override
    public final boolean isOpen() {
        return !this.closed;
    }
    
    protected final void begin() throws IOException {
        this.closeLock.readLock().lock();
        if (this.closed) {
            throw new ClosedChannelException();
        }
    }
    
    protected final void end() {
        this.closeLock.readLock().unlock();
    }
    
    protected final void end(final boolean b) throws IOException {
        this.end();
        if (!b && !this.isOpen()) {
            throw new AsynchronousCloseException();
        }
    }
    
    abstract <A> Future<FileLock> implLock(final long p0, final long p1, final boolean p2, final A p3, final CompletionHandler<FileLock, ? super A> p4);
    
    @Override
    public final Future<FileLock> lock(final long n, final long n2, final boolean b) {
        return this.implLock(n, n2, b, (Object)null, null);
    }
    
    @Override
    public final <A> void lock(final long n, final long n2, final boolean b, final A a, final CompletionHandler<FileLock, ? super A> completionHandler) {
        if (completionHandler == null) {
            throw new NullPointerException("'handler' is null");
        }
        this.implLock(n, n2, b, a, completionHandler);
    }
    
    final void ensureFileLockTableInitialized() throws IOException {
        if (this.fileLockTable == null) {
            synchronized (this) {
                if (this.fileLockTable == null) {
                    this.fileLockTable = FileLockTable.newSharedFileLockTable(this, this.fdObj);
                }
            }
        }
    }
    
    final void invalidateAllLocks() throws IOException {
        if (this.fileLockTable != null) {
            for (final FileLock fileLock : this.fileLockTable.removeAll()) {
                synchronized (fileLock) {
                    if (!fileLock.isValid()) {
                        continue;
                    }
                    final FileLockImpl fileLockImpl = (FileLockImpl)fileLock;
                    this.implRelease(fileLockImpl);
                    fileLockImpl.invalidate();
                }
            }
        }
    }
    
    protected final FileLockImpl addToFileLockTable(final long n, final long n2, final boolean b) {
        FileLockImpl fileLockImpl;
        try {
            this.closeLock.readLock().lock();
            if (this.closed) {
                return null;
            }
            try {
                this.ensureFileLockTableInitialized();
            }
            catch (final IOException ex) {
                throw new AssertionError((Object)ex);
            }
            fileLockImpl = new FileLockImpl(this, n, n2, b);
            this.fileLockTable.add(fileLockImpl);
        }
        finally {
            this.end();
        }
        return fileLockImpl;
    }
    
    protected final void removeFromFileLockTable(final FileLockImpl fileLockImpl) {
        this.fileLockTable.remove(fileLockImpl);
    }
    
    protected abstract void implRelease(final FileLockImpl p0) throws IOException;
    
    final void release(final FileLockImpl fileLockImpl) throws IOException {
        try {
            this.begin();
            this.implRelease(fileLockImpl);
            this.removeFromFileLockTable(fileLockImpl);
        }
        finally {
            this.end();
        }
    }
    
    abstract <A> Future<Integer> implRead(final ByteBuffer p0, final long p1, final A p2, final CompletionHandler<Integer, ? super A> p3);
    
    @Override
    public final Future<Integer> read(final ByteBuffer byteBuffer, final long n) {
        return this.implRead(byteBuffer, n, (Object)null, null);
    }
    
    @Override
    public final <A> void read(final ByteBuffer byteBuffer, final long n, final A a, final CompletionHandler<Integer, ? super A> completionHandler) {
        if (completionHandler == null) {
            throw new NullPointerException("'handler' is null");
        }
        this.implRead(byteBuffer, n, a, completionHandler);
    }
    
    abstract <A> Future<Integer> implWrite(final ByteBuffer p0, final long p1, final A p2, final CompletionHandler<Integer, ? super A> p3);
    
    @Override
    public final Future<Integer> write(final ByteBuffer byteBuffer, final long n) {
        return this.implWrite(byteBuffer, n, (Object)null, null);
    }
    
    @Override
    public final <A> void write(final ByteBuffer byteBuffer, final long n, final A a, final CompletionHandler<Integer, ? super A> completionHandler) {
        if (completionHandler == null) {
            throw new NullPointerException("'handler' is null");
        }
        this.implWrite(byteBuffer, n, a, completionHandler);
    }
}
