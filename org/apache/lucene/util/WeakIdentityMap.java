package org.apache.lucene.util;

import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
import java.lang.ref.ReferenceQueue;

public final class WeakIdentityMap<K, V>
{
    private final ReferenceQueue<Object> queue;
    private final Map<IdentityWeakReference, V> backingStore;
    private final boolean reapOnRead;
    static final Object NULL;
    
    public static <K, V> WeakIdentityMap<K, V> newHashMap() {
        return newHashMap(true);
    }
    
    public static <K, V> WeakIdentityMap<K, V> newHashMap(final boolean reapOnRead) {
        return new WeakIdentityMap<K, V>(new HashMap<IdentityWeakReference, V>(), reapOnRead);
    }
    
    public static <K, V> WeakIdentityMap<K, V> newConcurrentHashMap() {
        return newConcurrentHashMap(true);
    }
    
    public static <K, V> WeakIdentityMap<K, V> newConcurrentHashMap(final boolean reapOnRead) {
        return new WeakIdentityMap<K, V>(new ConcurrentHashMap<IdentityWeakReference, V>(), reapOnRead);
    }
    
    private WeakIdentityMap(final Map<IdentityWeakReference, V> backingStore, final boolean reapOnRead) {
        this.queue = new ReferenceQueue<Object>();
        this.backingStore = backingStore;
        this.reapOnRead = reapOnRead;
    }
    
    public void clear() {
        this.backingStore.clear();
        this.reap();
    }
    
    public boolean containsKey(final Object key) {
        if (this.reapOnRead) {
            this.reap();
        }
        return this.backingStore.containsKey(new IdentityWeakReference(key, null));
    }
    
    public V get(final Object key) {
        if (this.reapOnRead) {
            this.reap();
        }
        return this.backingStore.get(new IdentityWeakReference(key, null));
    }
    
    public V put(final K key, final V value) {
        this.reap();
        return this.backingStore.put(new IdentityWeakReference(key, this.queue), value);
    }
    
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    public V remove(final Object key) {
        this.reap();
        return this.backingStore.remove(new IdentityWeakReference(key, null));
    }
    
    public int size() {
        if (this.backingStore.isEmpty()) {
            return 0;
        }
        if (this.reapOnRead) {
            this.reap();
        }
        return this.backingStore.size();
    }
    
    public Iterator<K> keyIterator() {
        this.reap();
        final Iterator<IdentityWeakReference> iterator = this.backingStore.keySet().iterator();
        return new Iterator<K>() {
            private Object next = null;
            private boolean nextIsSet = false;
            
            @Override
            public boolean hasNext() {
                return this.nextIsSet || this.setNext();
            }
            
            @Override
            public K next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                assert this.nextIsSet;
                try {
                    return (K)this.next;
                }
                finally {
                    this.nextIsSet = false;
                    this.next = null;
                }
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            private boolean setNext() {
                assert !this.nextIsSet;
                while (iterator.hasNext()) {
                    this.next = iterator.next().get();
                    if (this.next != null) {
                        if (this.next == WeakIdentityMap.NULL) {
                            this.next = null;
                        }
                        return this.nextIsSet = true;
                    }
                    iterator.remove();
                }
                return false;
            }
        };
    }
    
    public Iterator<V> valueIterator() {
        if (this.reapOnRead) {
            this.reap();
        }
        return this.backingStore.values().iterator();
    }
    
    public void reap() {
        Reference<?> zombie;
        while ((zombie = this.queue.poll()) != null) {
            this.backingStore.remove(zombie);
        }
    }
    
    static {
        NULL = new Object();
    }
    
    private static final class IdentityWeakReference extends WeakReference<Object>
    {
        private final int hash;
        
        IdentityWeakReference(final Object obj, final ReferenceQueue<Object> queue) {
            super((obj == null) ? WeakIdentityMap.NULL : obj, queue);
            this.hash = System.identityHashCode(obj);
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof IdentityWeakReference) {
                final IdentityWeakReference ref = (IdentityWeakReference)o;
                if (this.get() == ref.get()) {
                    return true;
                }
            }
            return false;
        }
    }
}
