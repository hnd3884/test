package io.netty.resolver.dns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import io.netty.channel.EventLoop;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

abstract class Cache<E>
{
    private static final AtomicReferenceFieldUpdater<Entries, ScheduledFuture> FUTURE_UPDATER;
    private static final ScheduledFuture<?> CANCELLED;
    static final int MAX_SUPPORTED_TTL_SECS;
    private final ConcurrentMap<String, Entries> resolveCache;
    
    Cache() {
        this.resolveCache = PlatformDependent.newConcurrentHashMap();
    }
    
    final void clear() {
        while (!this.resolveCache.isEmpty()) {
            final Iterator<Map.Entry<String, Entries>> i = (Iterator<Map.Entry<String, Entries>>)this.resolveCache.entrySet().iterator();
            while (i.hasNext()) {
                final Map.Entry<String, Entries> e = i.next();
                i.remove();
                e.getValue().clearAndCancel();
            }
        }
    }
    
    final boolean clear(final String hostname) {
        final Entries entries = this.resolveCache.remove(hostname);
        return entries != null && entries.clearAndCancel();
    }
    
    final List<? extends E> get(final String hostname) {
        final Entries entries = this.resolveCache.get(hostname);
        return (entries == null) ? null : ((AtomicReference<List<? extends E>>)entries).get();
    }
    
    final void cache(final String hostname, final E value, final int ttl, final EventLoop loop) {
        Entries entries = this.resolveCache.get(hostname);
        if (entries == null) {
            entries = new Entries(hostname);
            final Entries oldEntries = this.resolveCache.putIfAbsent(hostname, entries);
            if (oldEntries != null) {
                entries = oldEntries;
            }
        }
        entries.add(value, ttl, loop);
    }
    
    final int size() {
        return this.resolveCache.size();
    }
    
    protected abstract boolean shouldReplaceAll(final E p0);
    
    protected void sortEntries(final String hostname, final List<E> entries) {
    }
    
    protected abstract boolean equals(final E p0, final E p1);
    
    static {
        FUTURE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(Entries.class, ScheduledFuture.class, "expirationFuture");
        CANCELLED = new ScheduledFuture<Object>() {
            @Override
            public boolean cancel(final boolean mayInterruptIfRunning) {
                return false;
            }
            
            @Override
            public long getDelay(final TimeUnit unit) {
                return Long.MIN_VALUE;
            }
            
            @Override
            public int compareTo(final Delayed o) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean isCancelled() {
                return true;
            }
            
            @Override
            public boolean isDone() {
                return true;
            }
            
            @Override
            public Object get() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Object get(final long timeout, final TimeUnit unit) {
                throw new UnsupportedOperationException();
            }
        };
        MAX_SUPPORTED_TTL_SECS = (int)TimeUnit.DAYS.toSeconds(730L);
    }
    
    private final class Entries extends AtomicReference<List<E>> implements Runnable
    {
        private final String hostname;
        volatile ScheduledFuture<?> expirationFuture;
        
        Entries(final String hostname) {
            super(Collections.emptyList());
            this.hostname = hostname;
        }
        
        void add(final E e, final int ttl, final EventLoop loop) {
            if (Cache.this.shouldReplaceAll(e)) {
                this.set(Collections.singletonList(e));
                this.scheduleCacheExpirationIfNeeded(ttl, loop);
                return;
            }
            while (true) {
                final List<E> entries = this.get();
                if (!entries.isEmpty()) {
                    final E firstEntry = entries.get(0);
                    if (Cache.this.shouldReplaceAll(firstEntry)) {
                        assert entries.size() == 1;
                        if (this.compareAndSet(entries, Collections.singletonList(e))) {
                            this.scheduleCacheExpirationIfNeeded(ttl, loop);
                            return;
                        }
                        continue;
                    }
                    else {
                        final List<E> newEntries = new ArrayList<E>(entries.size() + 1);
                        int i = 0;
                        E replacedEntry = null;
                        do {
                            final E entry = entries.get(i);
                            if (Cache.this.equals(e, entry)) {
                                replacedEntry = entry;
                                newEntries.add(e);
                                ++i;
                                while (i < entries.size()) {
                                    newEntries.add(entries.get(i));
                                    ++i;
                                }
                                break;
                            }
                            newEntries.add(entry);
                        } while (++i < entries.size());
                        if (replacedEntry == null) {
                            newEntries.add(e);
                        }
                        Cache.this.sortEntries(this.hostname, newEntries);
                        if (this.compareAndSet(entries, Collections.unmodifiableList((List<? extends E>)newEntries))) {
                            this.scheduleCacheExpirationIfNeeded(ttl, loop);
                            return;
                        }
                        continue;
                    }
                }
                else {
                    if (this.compareAndSet(entries, Collections.singletonList(e))) {
                        this.scheduleCacheExpirationIfNeeded(ttl, loop);
                        return;
                    }
                    continue;
                }
            }
        }
        
        private void scheduleCacheExpirationIfNeeded(final int ttl, final EventLoop loop) {
            while (true) {
                final ScheduledFuture<?> oldFuture = Cache.FUTURE_UPDATER.get(this);
                if (oldFuture != null && oldFuture.getDelay(TimeUnit.SECONDS) <= ttl) {
                    break;
                }
                final ScheduledFuture<?> newFuture = loop.schedule((Runnable)this, (long)ttl, TimeUnit.SECONDS);
                if (Cache.FUTURE_UPDATER.compareAndSet(this, oldFuture, newFuture)) {
                    if (oldFuture != null) {
                        oldFuture.cancel(true);
                        break;
                    }
                    break;
                }
                else {
                    newFuture.cancel(true);
                }
            }
        }
        
        boolean clearAndCancel() {
            final List<E> entries = this.getAndSet(Collections.emptyList());
            if (entries.isEmpty()) {
                return false;
            }
            final ScheduledFuture<?> expirationFuture = Cache.FUTURE_UPDATER.getAndSet(this, Cache.CANCELLED);
            if (expirationFuture != null) {
                expirationFuture.cancel(false);
            }
            return true;
        }
        
        @Override
        public void run() {
            Cache.this.resolveCache.remove(this.hostname, this);
            this.clearAndCancel();
        }
    }
}
