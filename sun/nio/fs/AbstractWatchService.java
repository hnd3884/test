package sun.nio.fs;

import java.util.concurrent.TimeUnit;
import java.nio.file.ClosedWatchServiceException;
import java.io.IOException;
import java.nio.file.WatchEvent;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.util.concurrent.LinkedBlockingDeque;
import java.nio.file.WatchService;

abstract class AbstractWatchService implements WatchService
{
    private final LinkedBlockingDeque<WatchKey> pendingKeys;
    private final WatchKey CLOSE_KEY;
    private volatile boolean closed;
    private final Object closeLock;
    
    protected AbstractWatchService() {
        this.pendingKeys = new LinkedBlockingDeque<WatchKey>();
        this.CLOSE_KEY = new AbstractWatchKey((Path)null, (AbstractWatchService)null) {
            @Override
            public boolean isValid() {
                return true;
            }
            
            @Override
            public void cancel() {
            }
        };
        this.closeLock = new Object();
    }
    
    abstract WatchKey register(final Path p0, final WatchEvent.Kind<?>[] p1, final WatchEvent.Modifier... p2) throws IOException;
    
    final void enqueueKey(final WatchKey watchKey) {
        this.pendingKeys.offer(watchKey);
    }
    
    private void checkOpen() {
        if (this.closed) {
            throw new ClosedWatchServiceException();
        }
    }
    
    private void checkKey(final WatchKey watchKey) {
        if (watchKey == this.CLOSE_KEY) {
            this.enqueueKey(watchKey);
        }
        this.checkOpen();
    }
    
    @Override
    public final WatchKey poll() {
        this.checkOpen();
        final WatchKey watchKey = this.pendingKeys.poll();
        this.checkKey(watchKey);
        return watchKey;
    }
    
    @Override
    public final WatchKey poll(final long n, final TimeUnit timeUnit) throws InterruptedException {
        this.checkOpen();
        final WatchKey watchKey = this.pendingKeys.poll(n, timeUnit);
        this.checkKey(watchKey);
        return watchKey;
    }
    
    @Override
    public final WatchKey take() throws InterruptedException {
        this.checkOpen();
        final WatchKey watchKey = this.pendingKeys.take();
        this.checkKey(watchKey);
        return watchKey;
    }
    
    final boolean isOpen() {
        return !this.closed;
    }
    
    final Object closeLock() {
        return this.closeLock;
    }
    
    abstract void implClose() throws IOException;
    
    @Override
    public final void close() throws IOException {
        synchronized (this.closeLock) {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.implClose();
            this.pendingKeys.clear();
            this.pendingKeys.offer(this.CLOSE_KEY);
        }
    }
}
