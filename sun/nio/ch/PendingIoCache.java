package sun.nio.ch;

import java.util.Iterator;
import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.util.HashMap;
import java.util.Map;
import sun.misc.Unsafe;

class PendingIoCache
{
    private static final Unsafe unsafe;
    private static final int addressSize;
    private static final int SIZEOF_OVERLAPPED;
    private boolean closed;
    private boolean closePending;
    private final Map<Long, PendingFuture> pendingIoMap;
    private long[] overlappedCache;
    private int overlappedCacheCount;
    
    private static int dependsArch(final int n, final int n2) {
        return (PendingIoCache.addressSize == 4) ? n : n2;
    }
    
    PendingIoCache() {
        this.pendingIoMap = new HashMap<Long, PendingFuture>();
        this.overlappedCache = new long[4];
        this.overlappedCacheCount = 0;
    }
    
    long add(final PendingFuture<?, ?> pendingFuture) {
        synchronized (this) {
            if (this.closed) {
                throw new AssertionError((Object)"Should not get here");
            }
            long allocateMemory;
            if (this.overlappedCacheCount > 0) {
                final long[] overlappedCache = this.overlappedCache;
                final int overlappedCacheCount = this.overlappedCacheCount - 1;
                this.overlappedCacheCount = overlappedCacheCount;
                allocateMemory = overlappedCache[overlappedCacheCount];
            }
            else {
                allocateMemory = PendingIoCache.unsafe.allocateMemory(PendingIoCache.SIZEOF_OVERLAPPED);
            }
            this.pendingIoMap.put(allocateMemory, pendingFuture);
            return allocateMemory;
        }
    }
    
     <V, A> PendingFuture<V, A> remove(final long n) {
        synchronized (this) {
            final PendingFuture pendingFuture = this.pendingIoMap.remove(n);
            if (pendingFuture != null) {
                if (this.overlappedCacheCount < this.overlappedCache.length) {
                    this.overlappedCache[this.overlappedCacheCount++] = n;
                }
                else {
                    PendingIoCache.unsafe.freeMemory(n);
                }
                if (this.closePending) {
                    this.notifyAll();
                }
            }
            return pendingFuture;
        }
    }
    
    void close() {
        synchronized (this) {
            if (this.closed) {
                return;
            }
            if (!this.pendingIoMap.isEmpty()) {
                this.clearPendingIoMap();
            }
            while (this.overlappedCacheCount > 0) {
                final Unsafe unsafe = PendingIoCache.unsafe;
                final long[] overlappedCache = this.overlappedCache;
                final int overlappedCacheCount = this.overlappedCacheCount - 1;
                this.overlappedCacheCount = overlappedCacheCount;
                unsafe.freeMemory(overlappedCache[overlappedCacheCount]);
            }
            this.closed = true;
        }
    }
    
    private void clearPendingIoMap() {
        assert Thread.holdsLock(this);
        this.closePending = true;
        try {
            this.wait(50L);
        }
        catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        this.closePending = false;
        if (this.pendingIoMap.isEmpty()) {
            return;
        }
        for (final Long n : this.pendingIoMap.keySet()) {
            final PendingFuture pendingFuture = this.pendingIoMap.get(n);
            assert !pendingFuture.isDone();
            final Iocp iocp = (Iocp)((Groupable)pendingFuture.channel()).group();
            iocp.makeStale(n);
            iocp.executeOnPooledThread(new Runnable() {
                final /* synthetic */ Iocp.ResultHandler val$rh = (Iocp.ResultHandler)pendingFuture.getContext();
                
                @Override
                public void run() {
                    this.val$rh.failed(-1, new AsynchronousCloseException());
                }
            });
        }
        this.pendingIoMap.clear();
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        addressSize = PendingIoCache.unsafe.addressSize();
        SIZEOF_OVERLAPPED = dependsArch(20, 32);
    }
}
