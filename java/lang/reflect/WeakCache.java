package java.lang.reflect;

import java.util.Iterator;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.concurrent.ConcurrentMap;
import java.lang.ref.ReferenceQueue;

final class WeakCache<K, P, V>
{
    private final ReferenceQueue<K> refQueue;
    private final ConcurrentMap<Object, ConcurrentMap<Object, Supplier<V>>> map;
    private final ConcurrentMap<Supplier<V>, Boolean> reverseMap;
    private final BiFunction<K, P, ?> subKeyFactory;
    private final BiFunction<K, P, V> valueFactory;
    
    public WeakCache(final BiFunction<K, P, ?> biFunction, final BiFunction<K, P, V> biFunction2) {
        this.refQueue = new ReferenceQueue<K>();
        this.map = new ConcurrentHashMap<Object, ConcurrentMap<Object, Supplier<V>>>();
        this.reverseMap = new ConcurrentHashMap<Supplier<V>, Boolean>();
        this.subKeyFactory = Objects.requireNonNull(biFunction);
        this.valueFactory = Objects.requireNonNull(biFunction2);
    }
    
    public V get(final K k, final P p2) {
        Objects.requireNonNull(p2);
        this.expungeStaleEntries();
        final Object value = CacheKey.valueOf(k, this.refQueue);
        ConcurrentMap concurrentMap = this.map.get(value);
        if (concurrentMap == null) {
            final ConcurrentMap concurrentMap2 = this.map.putIfAbsent(value, concurrentMap = new ConcurrentHashMap());
            if (concurrentMap2 != null) {
                concurrentMap = concurrentMap2;
            }
        }
        final Object requireNonNull = Objects.requireNonNull(this.subKeyFactory.apply(k, p2));
        Object o = concurrentMap.get(requireNonNull);
        Supplier supplier = null;
        V value2;
        while (true) {
            if (o != null) {
                value2 = ((Supplier<V>)o).get();
                if (value2 != null) {
                    break;
                }
            }
            if (supplier == null) {
                supplier = new Factory(k, p2, requireNonNull, concurrentMap);
            }
            if (o == null) {
                o = concurrentMap.putIfAbsent(requireNonNull, supplier);
                if (o != null) {
                    continue;
                }
                o = supplier;
            }
            else if (concurrentMap.replace(requireNonNull, o, supplier)) {
                o = supplier;
            }
            else {
                o = concurrentMap.get(requireNonNull);
            }
        }
        return value2;
    }
    
    public boolean containsValue(final V v) {
        Objects.requireNonNull(v);
        this.expungeStaleEntries();
        return this.reverseMap.containsKey(new LookupValue(v));
    }
    
    public int size() {
        this.expungeStaleEntries();
        return this.reverseMap.size();
    }
    
    private void expungeStaleEntries() {
        CacheKey cacheKey;
        while ((cacheKey = (CacheKey)this.refQueue.poll()) != null) {
            cacheKey.expungeFrom(this.map, this.reverseMap);
        }
    }
    
    private final class Factory implements Supplier<V>
    {
        private final K key;
        private final P parameter;
        private final Object subKey;
        private final ConcurrentMap<Object, Supplier<V>> valuesMap;
        
        Factory(final K key, final P parameter, final Object subKey, final ConcurrentMap<Object, Supplier<V>> valuesMap) {
            this.key = key;
            this.parameter = parameter;
            this.subKey = subKey;
            this.valuesMap = valuesMap;
        }
        
        @Override
        public synchronized V get() {
            if (this.valuesMap.get(this.subKey) != this) {
                return null;
            }
            V requireNonNull = null;
            try {
                requireNonNull = Objects.requireNonNull(WeakCache.this.valueFactory.apply(this.key, this.parameter));
            }
            finally {
                if (requireNonNull == null) {
                    this.valuesMap.remove(this.subKey, this);
                }
            }
            assert requireNonNull != null;
            final CacheValue cacheValue = new CacheValue(requireNonNull);
            WeakCache.this.reverseMap.put(cacheValue, Boolean.TRUE);
            if (!this.valuesMap.replace(this.subKey, this, (Supplier<V>)cacheValue)) {
                throw new AssertionError((Object)"Should not reach here");
            }
            return requireNonNull;
        }
    }
    
    private static final class LookupValue<V> implements Value<V>
    {
        private final V value;
        
        LookupValue(final V value) {
            this.value = value;
        }
        
        @Override
        public V get() {
            return this.value;
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this.value);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o == this || (o instanceof Value && this.value == ((Value)o).get());
        }
    }
    
    private static final class CacheValue<V> extends WeakReference<V> implements Value<V>
    {
        private final int hash;
        
        CacheValue(final V v) {
            super(v);
            this.hash = System.identityHashCode(v);
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object o) {
            final Object value;
            return o == this || (o instanceof Value && (value = this.get()) != null && value == ((Value)o).get());
        }
    }
    
    private static final class CacheKey<K> extends WeakReference<K>
    {
        private static final Object NULL_KEY;
        private final int hash;
        
        static <K> Object valueOf(final K k, final ReferenceQueue<K> referenceQueue) {
            return (k == null) ? CacheKey.NULL_KEY : new CacheKey(k, (ReferenceQueue<Object>)referenceQueue);
        }
        
        private CacheKey(final K k, final ReferenceQueue<K> referenceQueue) {
            super(k, referenceQueue);
            this.hash = System.identityHashCode(k);
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object o) {
            final Object value;
            return o == this || (o != null && o.getClass() == this.getClass() && (value = this.get()) != null && value == ((CacheKey)o).get());
        }
        
        void expungeFrom(final ConcurrentMap<?, ? extends ConcurrentMap<?, ?>> concurrentMap, final ConcurrentMap<?, Boolean> concurrentMap2) {
            final ConcurrentMap concurrentMap3 = concurrentMap.remove(this);
            if (concurrentMap3 != null) {
                final Iterator iterator = concurrentMap3.values().iterator();
                while (iterator.hasNext()) {
                    concurrentMap2.remove(iterator.next());
                }
            }
        }
        
        static {
            NULL_KEY = new Object();
        }
    }
    
    private interface Value<V> extends Supplier<V>
    {
    }
}
