package org.glassfish.hk2.utilities.general.internal;

import java.util.Iterator;
import java.util.LinkedList;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.WeakHashMap;
import org.glassfish.hk2.utilities.general.WeakHashLRU;

public class WeakHashLRUImpl<K> implements WeakHashLRU<K>
{
    private static final Object VALUE;
    private final boolean isWeak;
    private final WeakHashMap<K, DoubleNode<K, Object>> byKey;
    private final ConcurrentHashMap<K, DoubleNode<K, Object>> byKeyNotWeak;
    private final ReferenceQueue<? super K> myQueue;
    private DoubleNode<K, Object> mru;
    private DoubleNode<K, Object> lru;
    
    public WeakHashLRUImpl(final boolean isWeak) {
        this.myQueue = new ReferenceQueue<Object>();
        this.isWeak = isWeak;
        if (isWeak) {
            this.byKey = new WeakHashMap<K, DoubleNode<K, Object>>();
            this.byKeyNotWeak = null;
        }
        else {
            this.byKey = null;
            this.byKeyNotWeak = new ConcurrentHashMap<K, DoubleNode<K, Object>>();
        }
    }
    
    private DoubleNode<K, Object> addToHead(final K key) {
        final DoubleNode<K, Object> added = new DoubleNode<K, Object>(key, WeakHashLRUImpl.VALUE, this.myQueue);
        if (this.mru == null) {
            this.mru = added;
            return this.lru = added;
        }
        added.setNext(this.mru);
        this.mru.setPrevious(added);
        return this.mru = added;
    }
    
    private K remove(final DoubleNode<K, Object> removeMe) {
        final K retVal = removeMe.getWeakKey().get();
        if (removeMe.getNext() != null) {
            removeMe.getNext().setPrevious(removeMe.getPrevious());
        }
        if (removeMe.getPrevious() != null) {
            removeMe.getPrevious().setNext(removeMe.getNext());
        }
        if (removeMe == this.mru) {
            this.mru = removeMe.getNext();
        }
        if (removeMe == this.lru) {
            this.lru = removeMe.getPrevious();
        }
        removeMe.setNext(null);
        removeMe.setPrevious(null);
        return retVal;
    }
    
    @Override
    public synchronized void add(final K key) {
        if (key == null) {
            throw new IllegalArgumentException("key may not be null");
        }
        DoubleNode<K, Object> existing;
        if (this.isWeak) {
            this.clearStale();
            existing = this.byKey.get(key);
        }
        else {
            existing = this.byKeyNotWeak.get(key);
        }
        if (existing != null) {
            this.remove(existing);
        }
        final DoubleNode<K, Object> added = this.addToHead(key);
        if (this.isWeak) {
            this.byKey.put(key, added);
        }
        else {
            this.byKeyNotWeak.put(key, added);
        }
    }
    
    @Override
    public boolean contains(final K key) {
        if (this.isWeak) {
            synchronized (this) {
                this.clearStale();
                return this.byKey.containsKey(key);
            }
        }
        return this.byKeyNotWeak.containsKey(key);
    }
    
    @Override
    public synchronized boolean remove(final K key) {
        if (this.isWeak) {
            this.clearStale();
        }
        return this.removeNoClear(key);
    }
    
    private boolean removeNoClear(final K key) {
        if (key == null) {
            return false;
        }
        DoubleNode<K, Object> removeMe;
        if (this.isWeak) {
            removeMe = this.byKey.remove(key);
        }
        else {
            removeMe = this.byKeyNotWeak.remove(key);
        }
        if (removeMe == null) {
            return false;
        }
        this.remove(removeMe);
        return true;
    }
    
    @Override
    public int size() {
        if (this.isWeak) {
            synchronized (this) {
                this.clearStale();
                return this.byKey.size();
            }
        }
        return this.byKeyNotWeak.size();
    }
    
    @Override
    public synchronized K remove() {
        try {
            if (this.lru == null) {
                return null;
            }
            DoubleNode<K, Object> previous;
            for (DoubleNode<K, Object> current = this.lru; current != null; current = previous) {
                previous = current.getPrevious();
                final K retVal = current.getWeakKey().get();
                if (retVal != null) {
                    this.removeNoClear(retVal);
                    return retVal;
                }
                this.remove(current);
            }
            return null;
        }
        finally {
            this.clearStale();
        }
    }
    
    @Override
    public synchronized void releaseMatching(final CacheKeyFilter<K> filter) {
        if (filter == null) {
            return;
        }
        if (this.isWeak) {
            this.clearStale();
        }
        final LinkedList<K> removeMe = new LinkedList<K>();
        for (DoubleNode<K, Object> current = this.mru; current != null; current = current.getNext()) {
            final K key = current.getWeakKey().get();
            if (key != null && filter.matches(key)) {
                removeMe.add(key);
            }
        }
        for (final K removeKey : removeMe) {
            this.removeNoClear(removeKey);
        }
    }
    
    @Override
    public synchronized void clear() {
        if (this.isWeak) {
            this.clearStale();
            this.byKey.clear();
        }
        else {
            this.byKeyNotWeak.clear();
        }
        this.mru = null;
        this.lru = null;
    }
    
    @Override
    public synchronized void clearStaleReferences() {
        this.clearStale();
    }
    
    private void clearStale() {
        boolean goOn = false;
        while (this.myQueue.poll() != null) {
            goOn = true;
        }
        if (!goOn) {
            return;
        }
        DoubleNode<K, Object> next;
        for (DoubleNode<K, Object> current = this.mru; current != null; current = next) {
            next = current.getNext();
            if (current.getWeakKey().get() == null) {
                this.remove(current);
            }
        }
    }
    
    @Override
    public synchronized String toString() {
        final StringBuffer sb = new StringBuffer("WeakHashLRUImpl({");
        boolean first = true;
        for (DoubleNode<K, Object> current = this.mru; current != null; current = current.getNext()) {
            final K key = current.getWeakKey().get();
            final String keyString = (key == null) ? "null" : key.toString();
            if (first) {
                first = false;
                sb.append(keyString);
            }
            else {
                sb.append("," + keyString);
            }
        }
        sb.append("}," + System.identityHashCode(this) + ")");
        return sb.toString();
    }
    
    static {
        VALUE = new Object();
    }
}
