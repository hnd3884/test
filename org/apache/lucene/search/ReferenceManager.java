package org.apache.lucene.search;

import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.store.AlreadyClosedException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.io.Closeable;

public abstract class ReferenceManager<G> implements Closeable
{
    private static final String REFERENCE_MANAGER_IS_CLOSED_MSG = "this ReferenceManager is closed";
    protected volatile G current;
    private final Lock refreshLock;
    private final List<RefreshListener> refreshListeners;
    
    public ReferenceManager() {
        this.refreshLock = new ReentrantLock();
        this.refreshListeners = new CopyOnWriteArrayList<RefreshListener>();
    }
    
    private void ensureOpen() {
        if (this.current == null) {
            throw new AlreadyClosedException("this ReferenceManager is closed");
        }
    }
    
    private synchronized void swapReference(final G newReference) throws IOException {
        this.ensureOpen();
        final G oldReference = this.current;
        this.current = newReference;
        this.release(oldReference);
    }
    
    protected abstract void decRef(final G p0) throws IOException;
    
    protected abstract G refreshIfNeeded(final G p0) throws IOException;
    
    protected abstract boolean tryIncRef(final G p0) throws IOException;
    
    public final G acquire() throws IOException {
        G ref;
        while ((ref = this.current) != null) {
            if (this.tryIncRef(ref)) {
                return ref;
            }
            if (this.getRefCount(ref) != 0 || this.current != ref) {
                continue;
            }
            assert ref != null;
            throw new IllegalStateException("The managed reference has already closed - this is likely a bug when the reference count is modified outside of the ReferenceManager");
        }
        throw new AlreadyClosedException("this ReferenceManager is closed");
    }
    
    @Override
    public final synchronized void close() throws IOException {
        if (this.current != null) {
            this.swapReference(null);
            this.afterClose();
        }
    }
    
    protected abstract int getRefCount(final G p0);
    
    protected void afterClose() throws IOException {
    }
    
    private void doMaybeRefresh() throws IOException {
        this.refreshLock.lock();
        boolean refreshed = false;
        try {
            final G reference = this.acquire();
            try {
                this.notifyRefreshListenersBefore();
                final G newReference = this.refreshIfNeeded(reference);
                if (newReference != null) {
                    assert newReference != reference : "refreshIfNeeded should return null if refresh wasn't needed";
                    try {
                        this.swapReference(newReference);
                        refreshed = true;
                    }
                    finally {
                        if (!refreshed) {
                            this.release(newReference);
                        }
                    }
                }
            }
            finally {
                this.release(reference);
                this.notifyRefreshListenersRefreshed(refreshed);
            }
            this.afterMaybeRefresh();
        }
        finally {
            this.refreshLock.unlock();
        }
    }
    
    public final boolean maybeRefresh() throws IOException {
        this.ensureOpen();
        final boolean doTryRefresh = this.refreshLock.tryLock();
        if (doTryRefresh) {
            try {
                this.doMaybeRefresh();
            }
            finally {
                this.refreshLock.unlock();
            }
        }
        return doTryRefresh;
    }
    
    public final void maybeRefreshBlocking() throws IOException {
        this.ensureOpen();
        this.refreshLock.lock();
        try {
            this.doMaybeRefresh();
        }
        finally {
            this.refreshLock.unlock();
        }
    }
    
    protected void afterMaybeRefresh() throws IOException {
    }
    
    public final void release(final G reference) throws IOException {
        assert reference != null;
        this.decRef(reference);
    }
    
    private void notifyRefreshListenersBefore() throws IOException {
        for (final RefreshListener refreshListener : this.refreshListeners) {
            refreshListener.beforeRefresh();
        }
    }
    
    private void notifyRefreshListenersRefreshed(final boolean didRefresh) throws IOException {
        for (final RefreshListener refreshListener : this.refreshListeners) {
            refreshListener.afterRefresh(didRefresh);
        }
    }
    
    public void addListener(final RefreshListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null");
        }
        this.refreshListeners.add(listener);
    }
    
    public void removeListener(final RefreshListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null");
        }
        this.refreshListeners.remove(listener);
    }
    
    public interface RefreshListener
    {
        void beforeRefresh() throws IOException;
        
        void afterRefresh(final boolean p0) throws IOException;
    }
}
