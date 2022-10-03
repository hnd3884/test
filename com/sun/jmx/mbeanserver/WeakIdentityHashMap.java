package com.sun.jmx.mbeanserver;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;

class WeakIdentityHashMap<K, V>
{
    private Map<WeakReference<K>, V> map;
    private ReferenceQueue<K> refQueue;
    
    private WeakIdentityHashMap() {
        this.map = Util.newMap();
        this.refQueue = new ReferenceQueue<K>();
    }
    
    static <K, V> WeakIdentityHashMap<K, V> make() {
        return new WeakIdentityHashMap<K, V>();
    }
    
    V get(final K k) {
        this.expunge();
        return this.map.get(this.makeReference(k));
    }
    
    public V put(final K k, final V v) {
        this.expunge();
        if (k == null) {
            throw new IllegalArgumentException("Null key");
        }
        return this.map.put(this.makeReference(k, this.refQueue), v);
    }
    
    public V remove(final K k) {
        this.expunge();
        return this.map.remove(this.makeReference(k));
    }
    
    private void expunge() {
        Reference<? extends K> poll;
        while ((poll = this.refQueue.poll()) != null) {
            this.map.remove(poll);
        }
    }
    
    private WeakReference<K> makeReference(final K k) {
        return new IdentityWeakReference<K>(k);
    }
    
    private WeakReference<K> makeReference(final K k, final ReferenceQueue<K> referenceQueue) {
        return new IdentityWeakReference<K>(k, referenceQueue);
    }
    
    private static class IdentityWeakReference<T> extends WeakReference<T>
    {
        private final int hashCode;
        
        IdentityWeakReference(final T t) {
            this(t, null);
        }
        
        IdentityWeakReference(final T t, final ReferenceQueue<T> referenceQueue) {
            super(t, referenceQueue);
            this.hashCode = ((t == null) ? 0 : System.identityHashCode(t));
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof IdentityWeakReference)) {
                return false;
            }
            final IdentityWeakReference identityWeakReference = (IdentityWeakReference)o;
            final Object value = this.get();
            return value != null && value == identityWeakReference.get();
        }
        
        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }
}
