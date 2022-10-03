package com.sun.beans.util;

import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;
import java.lang.ref.Reference;
import java.util.Objects;
import java.lang.ref.ReferenceQueue;

public abstract class Cache<K, V>
{
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private final boolean identity;
    private final Kind keyKind;
    private final Kind valueKind;
    private final ReferenceQueue<Object> queue;
    private volatile CacheEntry<K, V>[] table;
    private int threshold;
    private int size;
    
    public abstract V create(final K p0);
    
    public Cache(final Kind kind, final Kind kind2) {
        this(kind, kind2, false);
    }
    
    public Cache(final Kind keyKind, final Kind valueKind, final boolean identity) {
        this.queue = new ReferenceQueue<Object>();
        this.table = this.newTable(8);
        this.threshold = 6;
        Objects.requireNonNull(keyKind, "keyKind");
        Objects.requireNonNull(valueKind, "valueKind");
        this.keyKind = keyKind;
        this.valueKind = valueKind;
        this.identity = identity;
    }
    
    public final V get(final K k) {
        Objects.requireNonNull(k, "key");
        this.removeStaleEntries();
        final int hash = this.hash(k);
        final CacheEntry<K, V>[] table = this.table;
        final Object entryValue = this.getEntryValue(k, hash, (CacheEntry<K, Object>)table[index(hash, table)]);
        if (entryValue != null) {
            return (V)entryValue;
        }
        synchronized (this.queue) {
            final Object entryValue2 = this.getEntryValue(k, hash, (CacheEntry<K, Object>)this.table[index(hash, this.table)]);
            if (entryValue2 != null) {
                return (V)entryValue2;
            }
            final V create = this.create(k);
            Objects.requireNonNull(create, "value");
            final int index = index(hash, this.table);
            this.table[index] = new CacheEntry<K, V>(hash, (Object)k, (Object)create, (CacheEntry<Object, Object>)this.table[index]);
            if (++this.size >= this.threshold) {
                if (this.table.length == 1073741824) {
                    this.threshold = Integer.MAX_VALUE;
                }
                else {
                    this.removeStaleEntries();
                    final CacheEntry<K, V>[] table2 = this.newTable(this.table.length << 1);
                    this.transfer(this.table, table2);
                    if (this.size >= this.threshold / 2) {
                        this.table = table2;
                        this.threshold <<= 1;
                    }
                    else {
                        this.transfer(table2, this.table);
                    }
                    this.removeStaleEntries();
                }
            }
            return create;
        }
    }
    
    public final void remove(final K k) {
        if (k != null) {
            synchronized (this.queue) {
                this.removeStaleEntries();
                final int hash = this.hash(k);
                final int index = index(hash, this.table);
                CacheEntry<K, V> cacheEntry;
                CacheEntry<Object, Object> access$100;
                for (Object o = cacheEntry = this.table[index]; cacheEntry != null; cacheEntry = (CacheEntry<K, V>)access$100) {
                    access$100 = ((CacheEntry<Object, Object>)cacheEntry).next;
                    if (((CacheEntry<Object, Object>)cacheEntry).matches(hash, k)) {
                        if (cacheEntry == o) {
                            this.table[index] = (CacheEntry<K, V>)access$100;
                        }
                        else {
                            ((CacheEntry<Object, Object>)o).next = access$100;
                        }
                        ((CacheEntry<Object, Object>)cacheEntry).unlink();
                        break;
                    }
                    o = cacheEntry;
                }
            }
        }
    }
    
    public final void clear() {
        synchronized (this.queue) {
            int length = this.table.length;
            while (0 < length--) {
                CacheEntry<Object, Object> access$100;
                for (CacheEntry<K, V> cacheEntry = this.table[length]; cacheEntry != null; cacheEntry = (CacheEntry<K, V>)access$100) {
                    access$100 = ((CacheEntry<Object, Object>)cacheEntry).next;
                    ((CacheEntry<Object, Object>)cacheEntry).unlink();
                }
                this.table[length] = null;
            }
            while (null != this.queue.poll()) {}
        }
    }
    
    private int hash(final Object o) {
        if (this.identity) {
            final int identityHashCode = System.identityHashCode(o);
            return (identityHashCode << 1) - (identityHashCode << 8);
        }
        final int hashCode = o.hashCode();
        final int n = hashCode ^ (hashCode >>> 20 ^ hashCode >>> 12);
        return n ^ n >>> 7 ^ n >>> 4;
    }
    
    private static int index(final int n, final Object[] array) {
        return n & array.length - 1;
    }
    
    private CacheEntry<K, V>[] newTable(final int n) {
        return new CacheEntry[n];
    }
    
    private V getEntryValue(final K k, final int n, CacheEntry<K, V> access$100) {
        while (access$100 != null) {
            if (access$100.matches(n, k)) {
                return (V)access$100.value.getReferent();
            }
            access$100 = access$100.next;
        }
        return null;
    }
    
    private void removeStaleEntries() {
        Reference<?> reference = this.queue.poll();
        if (reference != null) {
            synchronized (this.queue) {
                do {
                    if (reference instanceof Ref) {
                        final CacheEntry cacheEntry = (CacheEntry)((Ref)reference).getOwner();
                        if (cacheEntry != null) {
                            final int index = index(cacheEntry.hash, this.table);
                            CacheEntry<K, V> cacheEntry2;
                            CacheEntry<Object, Object> access$100;
                            for (Object o = cacheEntry2 = this.table[index]; cacheEntry2 != null; cacheEntry2 = (CacheEntry<K, V>)access$100) {
                                access$100 = ((CacheEntry<Object, Object>)cacheEntry2).next;
                                if (cacheEntry2 == cacheEntry) {
                                    if (cacheEntry2 == o) {
                                        this.table[index] = (CacheEntry<K, V>)access$100;
                                    }
                                    else {
                                        ((CacheEntry<Object, Object>)o).next = access$100;
                                    }
                                    ((CacheEntry<Object, Object>)cacheEntry2).unlink();
                                    break;
                                }
                                o = cacheEntry2;
                            }
                        }
                    }
                    reference = this.queue.poll();
                } while (reference != null);
            }
        }
    }
    
    private void transfer(final CacheEntry<K, V>[] array, final CacheEntry<K, V>[] array2) {
        int length = array.length;
        while (0 < length--) {
            CacheEntry<K, V> cacheEntry = array[length];
            array[length] = null;
            while (cacheEntry != null) {
                final CacheEntry<Object, Object> access$100 = ((CacheEntry<Object, Object>)cacheEntry).next;
                if (((CacheEntry<Object, Object>)cacheEntry).key.isStale() || ((CacheEntry<Object, Object>)cacheEntry).value.isStale()) {
                    ((CacheEntry<Object, Object>)cacheEntry).unlink();
                }
                else {
                    final int index = index(((CacheEntry<Object, Object>)cacheEntry).hash, array2);
                    ((CacheEntry<Object, Object>)cacheEntry).next = (CacheEntry<Object, Object>)array2[index];
                    array2[index] = cacheEntry;
                }
                cacheEntry = (CacheEntry<K, V>)access$100;
            }
        }
    }
    
    private final class CacheEntry<K, V>
    {
        private final int hash;
        private final Ref<K> key;
        private final Ref<V> value;
        private volatile CacheEntry<K, V> next;
        
        private CacheEntry(final int hash, final K k, final V v, final CacheEntry<K, V> next) {
            this.hash = hash;
            this.key = Cache.this.keyKind.create(this, k, Cache.this.queue);
            this.value = Cache.this.valueKind.create(this, v, Cache.this.queue);
            this.next = next;
        }
        
        private boolean matches(final int n, final Object o) {
            if (this.hash != n) {
                return false;
            }
            final K referent = this.key.getReferent();
            return referent == o || (!Cache.this.identity && referent != null && referent.equals(o));
        }
        
        private void unlink() {
            this.next = null;
            this.key.removeOwner();
            this.value.removeOwner();
            Cache.this.size--;
        }
    }
    
    public enum Kind
    {
        STRONG {
            @Override
             <T> Ref<T> create(final Object o, final T t, final ReferenceQueue<? super T> referenceQueue) {
                return new Strong<T>(o, (Object)t);
            }
        }, 
        SOFT {
            @Override
             <T> Ref<T> create(final Object o, final T t, final ReferenceQueue<? super T> referenceQueue) {
                return (Ref<T>)((t == null) ? new Strong<T>(o, (Object)t) : new Soft<T>(o, (Object)t, (ReferenceQueue)referenceQueue));
            }
        }, 
        WEAK {
            @Override
             <T> Ref<T> create(final Object o, final T t, final ReferenceQueue<? super T> referenceQueue) {
                return (Ref<T>)((t == null) ? new Strong<T>(o, (Object)t) : new Weak<T>(o, (Object)t, (ReferenceQueue)referenceQueue));
            }
        };
        
        abstract <T> Ref<T> create(final Object p0, final T p1, final ReferenceQueue<? super T> p2);
        
        private static final class Strong<T> implements Ref<T>
        {
            private Object owner;
            private final T referent;
            
            private Strong(final Object owner, final T referent) {
                this.owner = owner;
                this.referent = referent;
            }
            
            @Override
            public Object getOwner() {
                return this.owner;
            }
            
            @Override
            public T getReferent() {
                return this.referent;
            }
            
            @Override
            public boolean isStale() {
                return false;
            }
            
            @Override
            public void removeOwner() {
                this.owner = null;
            }
        }
        
        private static final class Soft<T> extends SoftReference<T> implements Ref<T>
        {
            private Object owner;
            
            private Soft(final Object owner, final T t, final ReferenceQueue<? super T> referenceQueue) {
                super(t, referenceQueue);
                this.owner = owner;
            }
            
            @Override
            public Object getOwner() {
                return this.owner;
            }
            
            @Override
            public T getReferent() {
                return this.get();
            }
            
            @Override
            public boolean isStale() {
                return null == this.get();
            }
            
            @Override
            public void removeOwner() {
                this.owner = null;
            }
        }
        
        private static final class Weak<T> extends WeakReference<T> implements Ref<T>
        {
            private Object owner;
            
            private Weak(final Object owner, final T t, final ReferenceQueue<? super T> referenceQueue) {
                super(t, referenceQueue);
                this.owner = owner;
            }
            
            @Override
            public Object getOwner() {
                return this.owner;
            }
            
            @Override
            public T getReferent() {
                return this.get();
            }
            
            @Override
            public boolean isStale() {
                return null == this.get();
            }
            
            @Override
            public void removeOwner() {
                this.owner = null;
            }
        }
    }
    
    private interface Ref<T>
    {
        Object getOwner();
        
        T getReferent();
        
        boolean isStale();
        
        void removeOwner();
    }
}
