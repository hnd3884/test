package org.glassfish.jersey.internal.guava;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.io.Serializable;
import java.util.function.Function;
import java.lang.ref.Reference;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.ref.WeakReference;
import java.util.AbstractQueue;
import java.lang.ref.ReferenceQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.ExecutionException;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.Queue;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;

class LocalCache<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>
{
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private static final int MAX_SEGMENTS = 65536;
    private static final int CONTAINS_VALUE_RETRIES = 3;
    private static final int DRAIN_THRESHOLD = 63;
    private static final int DRAIN_MAX = 16;
    private static final Logger logger;
    private static final ValueReference<Object, Object> UNSET;
    private static final Queue<?> DISCARDING_QUEUE;
    private final int segmentMask;
    private final int segmentShift;
    private final Segment<K, V>[] segments;
    private final int concurrencyLevel;
    private final Equivalence<Object> keyEquivalence;
    private final Equivalence<Object> valueEquivalence;
    private final Strength keyStrength;
    private final Strength valueStrength;
    private final long maxWeight;
    private final long expireAfterAccessNanos;
    private final long expireAfterWriteNanos;
    private final long refreshNanos;
    private final Queue<RemovalNotification<K, V>> removalNotificationQueue;
    private final Ticker ticker;
    private final EntryFactory entryFactory;
    private final CacheLoader<? super K, V> defaultLoader;
    private Set<K> keySet;
    private Collection<V> values;
    private Set<Map.Entry<K, V>> entrySet;
    
    private LocalCache(final CacheBuilder<? super K, ? super V> builder, final CacheLoader<? super K, V> loader) {
        this.concurrencyLevel = Math.min(builder.getConcurrencyLevel(), 65536);
        this.keyStrength = Strength.STRONG;
        this.valueStrength = Strength.STRONG;
        this.keyEquivalence = this.keyStrength.defaultEquivalence();
        this.valueEquivalence = this.valueStrength.defaultEquivalence();
        this.maxWeight = -1L;
        this.expireAfterAccessNanos = builder.getExpireAfterAccessNanos();
        this.expireAfterWriteNanos = 0L;
        this.refreshNanos = 0L;
        this.removalNotificationQueue = discardingQueue();
        this.ticker = (this.recordsTime() ? Ticker.systemTicker() : CacheBuilder.NULL_TICKER);
        this.entryFactory = EntryFactory.getFactory(this.keyStrength, this.usesAccessEntries(), this.usesWriteEntries());
        this.defaultLoader = loader;
        int initialCapacity = Math.min(16, 1073741824);
        if (this.evictsBySize()) {
            initialCapacity = Math.min(initialCapacity, (int)this.maxWeight);
        }
        int segmentShift = 0;
        int segmentCount;
        for (segmentCount = 1; segmentCount < this.concurrencyLevel && (!this.evictsBySize() || segmentCount * 20 <= this.maxWeight); segmentCount <<= 1) {
            ++segmentShift;
        }
        this.segmentShift = 32 - segmentShift;
        this.segmentMask = segmentCount - 1;
        this.segments = this.newSegmentArray(segmentCount);
        int segmentCapacity = initialCapacity / segmentCount;
        if (segmentCapacity * segmentCount < initialCapacity) {
            ++segmentCapacity;
        }
        int segmentSize;
        for (segmentSize = 1; segmentSize < segmentCapacity; segmentSize <<= 1) {}
        if (this.evictsBySize()) {
            long maxSegmentWeight = this.maxWeight / segmentCount + 1L;
            final long remainder = this.maxWeight % segmentCount;
            for (int i = 0; i < this.segments.length; ++i) {
                if (i == remainder) {
                    --maxSegmentWeight;
                }
                this.segments[i] = this.createSegment(segmentSize, maxSegmentWeight);
            }
        }
        else {
            for (int j = 0; j < this.segments.length; ++j) {
                this.segments[j] = this.createSegment(segmentSize, -1L);
            }
        }
    }
    
    private static <K, V> ValueReference<K, V> unset() {
        return (ValueReference<K, V>)LocalCache.UNSET;
    }
    
    private static <K, V> ReferenceEntry<K, V> nullEntry() {
        return (ReferenceEntry<K, V>)NullEntry.INSTANCE;
    }
    
    private static <E> Queue<E> discardingQueue() {
        return (Queue<E>)LocalCache.DISCARDING_QUEUE;
    }
    
    private static int rehash(int h) {
        h += (h << 15 ^ 0xFFFFCD7D);
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> 16;
    }
    
    private static <K, V> void connectAccessOrder(final ReferenceEntry<K, V> previous, final ReferenceEntry<K, V> next) {
        previous.setNextInAccessQueue(next);
        next.setPreviousInAccessQueue(previous);
    }
    
    private static <K, V> void nullifyAccessOrder(final ReferenceEntry<K, V> nulled) {
        final ReferenceEntry<K, V> nullEntry = nullEntry();
        nulled.setNextInAccessQueue(nullEntry);
        nulled.setPreviousInAccessQueue(nullEntry);
    }
    
    private static <K, V> void connectWriteOrder(final ReferenceEntry<K, V> previous, final ReferenceEntry<K, V> next) {
        previous.setNextInWriteQueue(next);
        next.setPreviousInWriteQueue(previous);
    }
    
    private static <K, V> void nullifyWriteOrder(final ReferenceEntry<K, V> nulled) {
        final ReferenceEntry<K, V> nullEntry = nullEntry();
        nulled.setNextInWriteQueue(nullEntry);
        nulled.setPreviousInWriteQueue(nullEntry);
    }
    
    boolean evictsBySize() {
        return this.maxWeight >= 0L;
    }
    
    private boolean expiresAfterWrite() {
        return this.expireAfterWriteNanos > 0L;
    }
    
    private boolean expiresAfterAccess() {
        return this.expireAfterAccessNanos > 0L;
    }
    
    boolean refreshes() {
        return this.refreshNanos > 0L;
    }
    
    boolean usesAccessQueue() {
        return this.expiresAfterAccess() || this.evictsBySize();
    }
    
    boolean usesWriteQueue() {
        return this.expiresAfterWrite();
    }
    
    boolean recordsWrite() {
        return this.expiresAfterWrite() || this.refreshes();
    }
    
    boolean recordsAccess() {
        return this.expiresAfterAccess();
    }
    
    private boolean recordsTime() {
        return this.recordsWrite() || this.recordsAccess();
    }
    
    private boolean usesWriteEntries() {
        return this.usesWriteQueue() || this.recordsWrite();
    }
    
    private boolean usesAccessEntries() {
        return this.usesAccessQueue() || this.recordsAccess();
    }
    
    boolean usesKeyReferences() {
        return this.keyStrength != Strength.STRONG;
    }
    
    boolean usesValueReferences() {
        return this.valueStrength != Strength.STRONG;
    }
    
    private int hash(final Object key) {
        final int h = this.keyEquivalence.hash(key);
        return rehash(h);
    }
    
    void reclaimValue(final ValueReference<K, V> valueReference) {
        final ReferenceEntry<K, V> entry = valueReference.getEntry();
        final int hash = entry.getHash();
        this.segmentFor(hash).reclaimValue(entry.getKey(), hash, valueReference);
    }
    
    void reclaimKey(final ReferenceEntry<K, V> entry) {
        final int hash = entry.getHash();
        this.segmentFor(hash).reclaimKey(entry, hash);
    }
    
    private Segment<K, V> segmentFor(final int hash) {
        return this.segments[hash >>> this.segmentShift & this.segmentMask];
    }
    
    private Segment<K, V> createSegment(final int initialCapacity, final long maxSegmentWeight) {
        return new Segment<K, V>(this, initialCapacity, maxSegmentWeight);
    }
    
    private V getLiveValue(final ReferenceEntry<K, V> entry, final long now) {
        if (entry.getKey() == null) {
            return null;
        }
        final V value = entry.getValueReference().get();
        if (value == null) {
            return null;
        }
        if (this.isExpired(entry, now)) {
            return null;
        }
        return value;
    }
    
    boolean isExpired(final ReferenceEntry<K, V> entry, final long now) {
        Preconditions.checkNotNull(entry);
        return (this.expiresAfterAccess() && now - entry.getAccessTime() >= this.expireAfterAccessNanos) || (this.expiresAfterWrite() && now - entry.getWriteTime() >= this.expireAfterWriteNanos);
    }
    
    private Segment<K, V>[] newSegmentArray(final int ssize) {
        return new Segment[ssize];
    }
    
    @Override
    public boolean isEmpty() {
        long sum = 0L;
        final Segment<K, V>[] segments = this.segments;
        for (int i = 0; i < segments.length; ++i) {
            if (segments[i].count != 0) {
                return false;
            }
            sum += segments[i].modCount;
        }
        if (sum != 0L) {
            for (int i = 0; i < segments.length; ++i) {
                if (segments[i].count != 0) {
                    return false;
                }
                sum -= segments[i].modCount;
            }
            if (sum != 0L) {
                return false;
            }
        }
        return true;
    }
    
    private long longSize() {
        final Segment<K, V>[] segments = this.segments;
        long sum = 0L;
        for (int i = 0; i < segments.length; ++i) {
            sum += segments[i].count;
        }
        return sum;
    }
    
    @Override
    public int size() {
        return Ints.saturatedCast(this.longSize());
    }
    
    @Override
    public V get(final Object key) {
        if (key == null) {
            return null;
        }
        final int hash = this.hash(key);
        return this.segmentFor(hash).get(key, hash);
    }
    
    V getIfPresent(final Object key) {
        final int hash = this.hash(Preconditions.checkNotNull(key));
        return this.segmentFor(hash).get(key, hash);
    }
    
    private V get(final K key, final CacheLoader<? super K, V> loader) throws ExecutionException {
        final int hash = this.hash(Preconditions.checkNotNull(key));
        return this.segmentFor(hash).get(key, hash, loader);
    }
    
    V getOrLoad(final K key) throws ExecutionException {
        return this.get(key, this.defaultLoader);
    }
    
    @Override
    public boolean containsKey(final Object key) {
        if (key == null) {
            return false;
        }
        final int hash = this.hash(key);
        return this.segmentFor(hash).containsKey(key, hash);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            return false;
        }
        final long now = this.ticker.read();
        final Segment<K, V>[] segments = this.segments;
        long last = -1L;
        for (int i = 0; i < 3; ++i) {
            long sum = 0L;
            for (final Segment<K, V> segment : segments) {
                final int c = segment.count;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = segment.table;
                for (int j = 0; j < table.length(); ++j) {
                    for (ReferenceEntry<K, V> e = table.get(j); e != null; e = e.getNext()) {
                        final V v = segment.getLiveValue(e, now);
                        if (v != null && this.valueEquivalence.equivalent(value, v)) {
                            return true;
                        }
                    }
                }
                sum += segment.modCount;
            }
            if (sum == last) {
                break;
            }
            last = sum;
        }
        return false;
    }
    
    @Override
    public V put(final K key, final V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        final int hash = this.hash(key);
        return this.segmentFor(hash).put(key, hash, value, false);
    }
    
    @Override
    public V putIfAbsent(final K key, final V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        final int hash = this.hash(key);
        return this.segmentFor(hash).put(key, hash, value, true);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }
    
    @Override
    public V remove(final Object key) {
        if (key == null) {
            return null;
        }
        final int hash = this.hash(key);
        return this.segmentFor(hash).remove(key, hash);
    }
    
    @Override
    public boolean remove(final Object key, final Object value) {
        if (key == null || value == null) {
            return false;
        }
        final int hash = this.hash(key);
        return this.segmentFor(hash).remove(key, hash, value);
    }
    
    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(newValue);
        if (oldValue == null) {
            return false;
        }
        final int hash = this.hash(key);
        return this.segmentFor(hash).replace(key, hash, oldValue, newValue);
    }
    
    @Override
    public V replace(final K key, final V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        final int hash = this.hash(key);
        return this.segmentFor(hash).replace(key, hash, value);
    }
    
    @Override
    public void clear() {
        for (final Segment<K, V> segment : this.segments) {
            segment.clear();
        }
    }
    
    @Override
    public Set<K> keySet() {
        final Set<K> ks = this.keySet;
        return (ks != null) ? ks : (this.keySet = new KeySet(this));
    }
    
    @Override
    public Collection<V> values() {
        final Collection<V> vs = this.values;
        return (vs != null) ? vs : (this.values = new Values(this));
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final Set<Map.Entry<K, V>> es = this.entrySet;
        return (es != null) ? es : (this.entrySet = new EntrySet(this));
    }
    
    static {
        logger = Logger.getLogger(LocalCache.class.getName());
        UNSET = new ValueReference<Object, Object>() {
            @Override
            public Object get() {
                return null;
            }
            
            @Override
            public int getWeight() {
                return 0;
            }
            
            @Override
            public ReferenceEntry<Object, Object> getEntry() {
                return null;
            }
            
            @Override
            public ValueReference<Object, Object> copyFor(final ReferenceQueue<Object> queue, final Object value, final ReferenceEntry<Object, Object> entry) {
                return this;
            }
            
            @Override
            public boolean isLoading() {
                return false;
            }
            
            @Override
            public boolean isActive() {
                return false;
            }
            
            @Override
            public Object waitForValue() {
                return null;
            }
            
            @Override
            public void notifyNewValue(final Object newValue) {
            }
        };
        DISCARDING_QUEUE = new AbstractQueue<Object>() {
            @Override
            public boolean offer(final Object o) {
                return true;
            }
            
            @Override
            public Object peek() {
                return null;
            }
            
            @Override
            public Object poll() {
                return null;
            }
            
            @Override
            public int size() {
                return 0;
            }
            
            @Override
            public Iterator<Object> iterator() {
                return Iterators.emptyIterator();
            }
        };
    }
    
    enum Strength
    {
        STRONG {
            @Override
             <K, V> ValueReference<K, V> referenceValue(final Segment<K, V> segment, final ReferenceEntry<K, V> entry, final V value, final int weight) {
                return (weight == 1) ? new StrongValueReference<K, V>(value) : new WeightedStrongValueReference<K, V>(value, weight);
            }
            
            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.equals();
            }
        }, 
        WEAK {
            @Override
             <K, V> ValueReference<K, V> referenceValue(final Segment<K, V> segment, final ReferenceEntry<K, V> entry, final V value, final int weight) {
                return (weight == 1) ? new WeakValueReference<K, V>(segment.valueReferenceQueue, value, entry) : new WeightedWeakValueReference<K, V>(segment.valueReferenceQueue, value, entry, weight);
            }
            
            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.identity();
            }
        };
        
        abstract <K, V> ValueReference<K, V> referenceValue(final Segment<K, V> p0, final ReferenceEntry<K, V> p1, final V p2, final int p3);
        
        abstract Equivalence<Object> defaultEquivalence();
    }
    
    enum EntryFactory
    {
        STRONG {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K key, final int hash, final ReferenceEntry<K, V> next) {
                return new StrongEntry<K, V>(key, hash, next);
            }
        }, 
        STRONG_ACCESS {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K key, final int hash, final ReferenceEntry<K, V> next) {
                return new StrongAccessEntry<K, V>(key, hash, next);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
                final ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
                this.copyAccessEntry(original, newEntry);
                return newEntry;
            }
        }, 
        STRONG_WRITE {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K key, final int hash, final ReferenceEntry<K, V> next) {
                return new StrongWriteEntry<K, V>(key, hash, next);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
                final ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
                this.copyWriteEntry(original, newEntry);
                return newEntry;
            }
        }, 
        STRONG_ACCESS_WRITE {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K key, final int hash, final ReferenceEntry<K, V> next) {
                return new StrongAccessWriteEntry<K, V>(key, hash, next);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
                final ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
                this.copyAccessEntry(original, newEntry);
                this.copyWriteEntry(original, newEntry);
                return newEntry;
            }
        }, 
        WEAK {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K key, final int hash, final ReferenceEntry<K, V> next) {
                return new WeakEntry<K, V>(segment.keyReferenceQueue, key, hash, next);
            }
        }, 
        WEAK_ACCESS {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K key, final int hash, final ReferenceEntry<K, V> next) {
                return new WeakAccessEntry<K, V>(segment.keyReferenceQueue, key, hash, next);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
                final ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
                this.copyAccessEntry(original, newEntry);
                return newEntry;
            }
        }, 
        WEAK_WRITE {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K key, final int hash, final ReferenceEntry<K, V> next) {
                return new WeakWriteEntry<K, V>(segment.keyReferenceQueue, key, hash, next);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
                final ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
                this.copyWriteEntry(original, newEntry);
                return newEntry;
            }
        }, 
        WEAK_ACCESS_WRITE {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K key, final int hash, final ReferenceEntry<K, V> next) {
                return new WeakAccessWriteEntry<K, V>(segment.keyReferenceQueue, key, hash, next);
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
                final ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
                this.copyAccessEntry(original, newEntry);
                this.copyWriteEntry(original, newEntry);
                return newEntry;
            }
        };
        
        static final int ACCESS_MASK = 1;
        static final int WRITE_MASK = 2;
        static final int WEAK_MASK = 4;
        static final EntryFactory[] factories;
        
        static EntryFactory getFactory(final Strength keyStrength, final boolean usesAccessQueue, final boolean usesWriteQueue) {
            final int flags = ((keyStrength == Strength.WEAK) ? 4 : 0) | (usesAccessQueue ? 1 : 0) | (usesWriteQueue ? 2 : 0);
            return EntryFactory.factories[flags];
        }
        
        abstract <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> p0, final K p1, final int p2, final ReferenceEntry<K, V> p3);
        
         <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
            return this.newEntry(segment, original.getKey(), original.getHash(), newNext);
        }
        
         <K, V> void copyAccessEntry(final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newEntry) {
            newEntry.setAccessTime(original.getAccessTime());
            connectAccessOrder(original.getPreviousInAccessQueue(), (ReferenceEntry<Object, Object>)newEntry);
            connectAccessOrder(newEntry, (ReferenceEntry<Object, Object>)original.getNextInAccessQueue());
            nullifyAccessOrder((ReferenceEntry<Object, Object>)original);
        }
        
         <K, V> void copyWriteEntry(final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newEntry) {
            newEntry.setWriteTime(original.getWriteTime());
            connectWriteOrder(original.getPreviousInWriteQueue(), (ReferenceEntry<Object, Object>)newEntry);
            connectWriteOrder(newEntry, (ReferenceEntry<Object, Object>)original.getNextInWriteQueue());
            nullifyWriteOrder((ReferenceEntry<Object, Object>)original);
        }
        
        static {
            factories = new EntryFactory[] { EntryFactory.STRONG, EntryFactory.STRONG_ACCESS, EntryFactory.STRONG_WRITE, EntryFactory.STRONG_ACCESS_WRITE, EntryFactory.WEAK, EntryFactory.WEAK_ACCESS, EntryFactory.WEAK_WRITE, EntryFactory.WEAK_ACCESS_WRITE };
        }
    }
    
    private enum NullEntry implements ReferenceEntry<Object, Object>
    {
        INSTANCE;
        
        @Override
        public ValueReference<Object, Object> getValueReference() {
            return null;
        }
        
        @Override
        public void setValueReference(final ValueReference<Object, Object> valueReference) {
        }
        
        @Override
        public ReferenceEntry<Object, Object> getNext() {
            return null;
        }
        
        @Override
        public int getHash() {
            return 0;
        }
        
        @Override
        public Object getKey() {
            return null;
        }
        
        @Override
        public long getAccessTime() {
            return 0L;
        }
        
        @Override
        public void setAccessTime(final long time) {
        }
        
        @Override
        public ReferenceEntry<Object, Object> getNextInAccessQueue() {
            return this;
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<Object, Object> next) {
        }
        
        @Override
        public ReferenceEntry<Object, Object> getPreviousInAccessQueue() {
            return this;
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<Object, Object> previous) {
        }
        
        @Override
        public long getWriteTime() {
            return 0L;
        }
        
        @Override
        public void setWriteTime(final long time) {
        }
        
        @Override
        public ReferenceEntry<Object, Object> getNextInWriteQueue() {
            return this;
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<Object, Object> next) {
        }
        
        @Override
        public ReferenceEntry<Object, Object> getPreviousInWriteQueue() {
            return this;
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<Object, Object> previous) {
        }
    }
    
    abstract static class AbstractReferenceEntry<K, V> implements ReferenceEntry<K, V>
    {
        @Override
        public ValueReference<K, V> getValueReference() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setValueReference(final ValueReference<K, V> valueReference) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNext() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int getHash() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public K getKey() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long getAccessTime() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setAccessTime(final long time) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> next) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previous) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long getWriteTime() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setWriteTime(final long time) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> next) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previous) {
            throw new UnsupportedOperationException();
        }
    }
    
    static class StrongEntry<K, V> extends AbstractReferenceEntry<K, V>
    {
        final K key;
        final int hash;
        final ReferenceEntry<K, V> next;
        volatile ValueReference<K, V> valueReference;
        
        StrongEntry(final K key, final int hash, final ReferenceEntry<K, V> next) {
            this.valueReference = (ValueReference<K, V>)unset();
            this.key = key;
            this.hash = hash;
            this.next = next;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }
        
        @Override
        public void setValueReference(final ValueReference<K, V> valueReference) {
            this.valueReference = valueReference;
        }
        
        @Override
        public int getHash() {
            return this.hash;
        }
        
        @Override
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }
    
    static final class StrongAccessEntry<K, V> extends StrongEntry<K, V>
    {
        volatile long accessTime;
        ReferenceEntry<K, V> nextAccess;
        ReferenceEntry<K, V> previousAccess;
        
        StrongAccessEntry(final K key, final int hash, final ReferenceEntry<K, V> next) {
            super(key, hash, next);
            this.accessTime = Long.MAX_VALUE;
            this.nextAccess = (ReferenceEntry<K, V>)nullEntry();
            this.previousAccess = (ReferenceEntry<K, V>)nullEntry();
        }
        
        @Override
        public long getAccessTime() {
            return this.accessTime;
        }
        
        @Override
        public void setAccessTime(final long time) {
            this.accessTime = time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> next) {
            this.nextAccess = next;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previous) {
            this.previousAccess = previous;
        }
    }
    
    static final class StrongWriteEntry<K, V> extends StrongEntry<K, V>
    {
        volatile long writeTime;
        ReferenceEntry<K, V> nextWrite;
        ReferenceEntry<K, V> previousWrite;
        
        StrongWriteEntry(final K key, final int hash, final ReferenceEntry<K, V> next) {
            super(key, hash, next);
            this.writeTime = Long.MAX_VALUE;
            this.nextWrite = (ReferenceEntry<K, V>)nullEntry();
            this.previousWrite = (ReferenceEntry<K, V>)nullEntry();
        }
        
        @Override
        public long getWriteTime() {
            return this.writeTime;
        }
        
        @Override
        public void setWriteTime(final long time) {
            this.writeTime = time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> next) {
            this.nextWrite = next;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previous) {
            this.previousWrite = previous;
        }
    }
    
    static final class StrongAccessWriteEntry<K, V> extends StrongEntry<K, V>
    {
        volatile long accessTime;
        ReferenceEntry<K, V> nextAccess;
        ReferenceEntry<K, V> previousAccess;
        volatile long writeTime;
        ReferenceEntry<K, V> nextWrite;
        ReferenceEntry<K, V> previousWrite;
        
        StrongAccessWriteEntry(final K key, final int hash, final ReferenceEntry<K, V> next) {
            super(key, hash, next);
            this.accessTime = Long.MAX_VALUE;
            this.nextAccess = (ReferenceEntry<K, V>)nullEntry();
            this.previousAccess = (ReferenceEntry<K, V>)nullEntry();
            this.writeTime = Long.MAX_VALUE;
            this.nextWrite = (ReferenceEntry<K, V>)nullEntry();
            this.previousWrite = (ReferenceEntry<K, V>)nullEntry();
        }
        
        @Override
        public long getAccessTime() {
            return this.accessTime;
        }
        
        @Override
        public void setAccessTime(final long time) {
            this.accessTime = time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> next) {
            this.nextAccess = next;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previous) {
            this.previousAccess = previous;
        }
        
        @Override
        public long getWriteTime() {
            return this.writeTime;
        }
        
        @Override
        public void setWriteTime(final long time) {
            this.writeTime = time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> next) {
            this.nextWrite = next;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previous) {
            this.previousWrite = previous;
        }
    }
    
    static class WeakEntry<K, V> extends WeakReference<K> implements ReferenceEntry<K, V>
    {
        final int hash;
        final ReferenceEntry<K, V> next;
        volatile ValueReference<K, V> valueReference;
        
        WeakEntry(final ReferenceQueue<K> queue, final K key, final int hash, final ReferenceEntry<K, V> next) {
            super(key, queue);
            this.valueReference = (ValueReference<K, V>)unset();
            this.hash = hash;
            this.next = next;
        }
        
        @Override
        public K getKey() {
            return this.get();
        }
        
        @Override
        public long getAccessTime() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setAccessTime(final long time) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> next) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previous) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long getWriteTime() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setWriteTime(final long time) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> next) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previous) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }
        
        @Override
        public void setValueReference(final ValueReference<K, V> valueReference) {
            this.valueReference = valueReference;
        }
        
        @Override
        public int getHash() {
            return this.hash;
        }
        
        @Override
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }
    }
    
    static final class WeakAccessEntry<K, V> extends WeakEntry<K, V>
    {
        volatile long accessTime;
        ReferenceEntry<K, V> nextAccess;
        ReferenceEntry<K, V> previousAccess;
        
        WeakAccessEntry(final ReferenceQueue<K> queue, final K key, final int hash, final ReferenceEntry<K, V> next) {
            super(queue, key, hash, next);
            this.accessTime = Long.MAX_VALUE;
            this.nextAccess = (ReferenceEntry<K, V>)nullEntry();
            this.previousAccess = (ReferenceEntry<K, V>)nullEntry();
        }
        
        @Override
        public long getAccessTime() {
            return this.accessTime;
        }
        
        @Override
        public void setAccessTime(final long time) {
            this.accessTime = time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> next) {
            this.nextAccess = next;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previous) {
            this.previousAccess = previous;
        }
    }
    
    static final class WeakWriteEntry<K, V> extends WeakEntry<K, V>
    {
        volatile long writeTime;
        ReferenceEntry<K, V> nextWrite;
        ReferenceEntry<K, V> previousWrite;
        
        WeakWriteEntry(final ReferenceQueue<K> queue, final K key, final int hash, final ReferenceEntry<K, V> next) {
            super(queue, key, hash, next);
            this.writeTime = Long.MAX_VALUE;
            this.nextWrite = (ReferenceEntry<K, V>)nullEntry();
            this.previousWrite = (ReferenceEntry<K, V>)nullEntry();
        }
        
        @Override
        public long getWriteTime() {
            return this.writeTime;
        }
        
        @Override
        public void setWriteTime(final long time) {
            this.writeTime = time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> next) {
            this.nextWrite = next;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previous) {
            this.previousWrite = previous;
        }
    }
    
    static final class WeakAccessWriteEntry<K, V> extends WeakEntry<K, V>
    {
        volatile long accessTime;
        ReferenceEntry<K, V> nextAccess;
        ReferenceEntry<K, V> previousAccess;
        volatile long writeTime;
        ReferenceEntry<K, V> nextWrite;
        ReferenceEntry<K, V> previousWrite;
        
        WeakAccessWriteEntry(final ReferenceQueue<K> queue, final K key, final int hash, final ReferenceEntry<K, V> next) {
            super(queue, key, hash, next);
            this.accessTime = Long.MAX_VALUE;
            this.nextAccess = (ReferenceEntry<K, V>)nullEntry();
            this.previousAccess = (ReferenceEntry<K, V>)nullEntry();
            this.writeTime = Long.MAX_VALUE;
            this.nextWrite = (ReferenceEntry<K, V>)nullEntry();
            this.previousWrite = (ReferenceEntry<K, V>)nullEntry();
        }
        
        @Override
        public long getAccessTime() {
            return this.accessTime;
        }
        
        @Override
        public void setAccessTime(final long time) {
            this.accessTime = time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> next) {
            this.nextAccess = next;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previous) {
            this.previousAccess = previous;
        }
        
        @Override
        public long getWriteTime() {
            return this.writeTime;
        }
        
        @Override
        public void setWriteTime(final long time) {
            this.writeTime = time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> next) {
            this.nextWrite = next;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previous) {
            this.previousWrite = previous;
        }
    }
    
    static class WeakValueReference<K, V> extends WeakReference<V> implements ValueReference<K, V>
    {
        final ReferenceEntry<K, V> entry;
        
        WeakValueReference(final ReferenceQueue<V> queue, final V referent, final ReferenceEntry<K, V> entry) {
            super(referent, queue);
            this.entry = entry;
        }
        
        @Override
        public int getWeight() {
            return 1;
        }
        
        @Override
        public ReferenceEntry<K, V> getEntry() {
            return this.entry;
        }
        
        @Override
        public void notifyNewValue(final V newValue) {
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> queue, final V value, final ReferenceEntry<K, V> entry) {
            return new WeakValueReference((ReferenceQueue<Object>)queue, value, (ReferenceEntry<Object, Object>)entry);
        }
        
        @Override
        public boolean isLoading() {
            return false;
        }
        
        @Override
        public boolean isActive() {
            return true;
        }
        
        @Override
        public V waitForValue() {
            return this.get();
        }
    }
    
    static class StrongValueReference<K, V> implements ValueReference<K, V>
    {
        final V referent;
        
        StrongValueReference(final V referent) {
            this.referent = referent;
        }
        
        @Override
        public V get() {
            return this.referent;
        }
        
        @Override
        public int getWeight() {
            return 1;
        }
        
        @Override
        public ReferenceEntry<K, V> getEntry() {
            return null;
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> queue, final V value, final ReferenceEntry<K, V> entry) {
            return this;
        }
        
        @Override
        public boolean isLoading() {
            return false;
        }
        
        @Override
        public boolean isActive() {
            return true;
        }
        
        @Override
        public V waitForValue() {
            return this.get();
        }
        
        @Override
        public void notifyNewValue(final V newValue) {
        }
    }
    
    static final class WeightedWeakValueReference<K, V> extends WeakValueReference<K, V>
    {
        final int weight;
        
        WeightedWeakValueReference(final ReferenceQueue<V> queue, final V referent, final ReferenceEntry<K, V> entry, final int weight) {
            super(queue, referent, entry);
            this.weight = weight;
        }
        
        @Override
        public int getWeight() {
            return this.weight;
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> queue, final V value, final ReferenceEntry<K, V> entry) {
            return new WeightedWeakValueReference((ReferenceQueue<Object>)queue, value, (ReferenceEntry<Object, Object>)entry, this.weight);
        }
    }
    
    static final class WeightedStrongValueReference<K, V> extends StrongValueReference<K, V>
    {
        final int weight;
        
        WeightedStrongValueReference(final V referent, final int weight) {
            super(referent);
            this.weight = weight;
        }
        
        @Override
        public int getWeight() {
            return this.weight;
        }
    }
    
    static class Segment<K, V> extends ReentrantLock
    {
        final LocalCache<K, V> map;
        final long maxSegmentWeight;
        final ReferenceQueue<K> keyReferenceQueue;
        final ReferenceQueue<V> valueReferenceQueue;
        final Queue<ReferenceEntry<K, V>> recencyQueue;
        final AtomicInteger readCount;
        final Queue<ReferenceEntry<K, V>> writeQueue;
        final Queue<ReferenceEntry<K, V>> accessQueue;
        volatile int count;
        long totalWeight;
        int modCount;
        int threshold;
        volatile AtomicReferenceArray<ReferenceEntry<K, V>> table;
        
        Segment(final LocalCache<K, V> map, final int initialCapacity, final long maxSegmentWeight) {
            this.readCount = new AtomicInteger();
            this.map = map;
            this.maxSegmentWeight = maxSegmentWeight;
            this.initTable(this.newEntryArray(initialCapacity));
            this.keyReferenceQueue = (map.usesKeyReferences() ? new ReferenceQueue<K>() : null);
            this.valueReferenceQueue = (map.usesValueReferences() ? new ReferenceQueue<V>() : null);
            this.recencyQueue = (Queue<ReferenceEntry<K, V>>)(map.usesAccessQueue() ? new ConcurrentLinkedQueue<ReferenceEntry<K, V>>() : discardingQueue());
            this.writeQueue = (Queue<ReferenceEntry<K, V>>)(map.usesWriteQueue() ? new WriteQueue() : discardingQueue());
            this.accessQueue = (Queue<ReferenceEntry<K, V>>)(map.usesAccessQueue() ? new AccessQueue() : discardingQueue());
        }
        
        AtomicReferenceArray<ReferenceEntry<K, V>> newEntryArray(final int size) {
            return new AtomicReferenceArray<ReferenceEntry<K, V>>(size);
        }
        
        void initTable(final AtomicReferenceArray<ReferenceEntry<K, V>> newTable) {
            this.threshold = newTable.length() * 3 / 4;
            if (this.threshold == this.maxSegmentWeight) {
                ++this.threshold;
            }
            this.table = newTable;
        }
        
        ReferenceEntry<K, V> newEntry(final K key, final int hash, final ReferenceEntry<K, V> next) {
            return ((LocalCache<Object, Object>)this.map).entryFactory.newEntry(this, (K)Preconditions.checkNotNull((K)key), hash, next);
        }
        
        ReferenceEntry<K, V> copyEntry(final ReferenceEntry<K, V> original, final ReferenceEntry<K, V> newNext) {
            if (original.getKey() == null) {
                return null;
            }
            final ValueReference<K, V> valueReference = original.getValueReference();
            final V value = valueReference.get();
            if (value == null && valueReference.isActive()) {
                return null;
            }
            final ReferenceEntry<K, V> newEntry = ((LocalCache<Object, Object>)this.map).entryFactory.copyEntry(this, original, newNext);
            newEntry.setValueReference(valueReference.copyFor(this.valueReferenceQueue, value, newEntry));
            return newEntry;
        }
        
        void setValue(final ReferenceEntry<K, V> entry, final K key, final V value, final long now) {
            final ValueReference<K, V> previous = entry.getValueReference();
            final int weight = 1;
            Preconditions.checkState(weight >= 0, (Object)"Weights must be non-negative");
            final ValueReference<K, V> valueReference = ((LocalCache<Object, Object>)this.map).valueStrength.referenceValue(this, entry, value, weight);
            entry.setValueReference(valueReference);
            this.recordWrite(entry, weight, now);
            previous.notifyNewValue(value);
        }
        
        V get(final K key, final int hash, final CacheLoader<? super K, V> loader) throws ExecutionException {
            Preconditions.checkNotNull(key);
            Preconditions.checkNotNull(loader);
            try {
                if (this.count != 0) {
                    final ReferenceEntry<K, V> e = this.getEntry(key, hash);
                    if (e != null) {
                        final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
                        final V value = this.getLiveValue(e, now);
                        if (value != null) {
                            this.recordRead(e, now);
                            return (V)this.scheduleRefresh((ReferenceEntry<K, Object>)e, key, hash, value, now, (CacheLoader<? super K, Object>)loader);
                        }
                        final ValueReference<K, V> valueReference = e.getValueReference();
                        if (valueReference.isLoading()) {
                            return (V)this.waitForLoadingValue((ReferenceEntry<K, Object>)e, key, (ValueReference<K, Object>)valueReference);
                        }
                    }
                }
                return (V)this.lockedGetOrLoad(key, hash, (CacheLoader<? super K, Object>)loader);
            }
            catch (final ExecutionException ee) {
                final Throwable cause = ee.getCause();
                if (cause instanceof Error) {
                    throw new ExecutionError((Error)cause);
                }
                if (cause instanceof RuntimeException) {
                    throw new UncheckedExecutionException(cause);
                }
                throw ee;
            }
            finally {
                this.postReadCleanup();
            }
        }
        
        V lockedGetOrLoad(final K key, final int hash, final CacheLoader<? super K, V> loader) throws ExecutionException {
            ValueReference<K, V> valueReference = null;
            LoadingValueReference<K, V> loadingValueReference = null;
            boolean createNewEntry = true;
            this.lock();
            ReferenceEntry<K, V> e;
            try {
                final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
                this.preWriteCleanup(now);
                final int newCount = this.count - 1;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int index = hash & table.length() - 1;
                final ReferenceEntry<K, V> first = e = table.get(index);
                while (e != null) {
                    final K entryKey = e.getKey();
                    if (e.getHash() == hash && entryKey != null && ((LocalCache<Object, Object>)this.map).keyEquivalence.equivalent(key, entryKey)) {
                        valueReference = e.getValueReference();
                        if (valueReference.isLoading()) {
                            createNewEntry = false;
                            break;
                        }
                        final V value = valueReference.get();
                        if (value == null) {
                            this.enqueueNotification(entryKey, hash, valueReference, RemovalCause.COLLECTED);
                        }
                        else {
                            if (!this.map.isExpired(e, now)) {
                                this.recordLockedRead(e, now);
                                return value;
                            }
                            this.enqueueNotification(entryKey, hash, valueReference, RemovalCause.EXPIRED);
                        }
                        this.writeQueue.remove(e);
                        this.accessQueue.remove(e);
                        this.count = newCount;
                        break;
                    }
                    else {
                        e = e.getNext();
                    }
                }
                if (createNewEntry) {
                    loadingValueReference = new LoadingValueReference<K, V>();
                    if (e == null) {
                        e = this.newEntry(key, hash, first);
                        e.setValueReference(loadingValueReference);
                        table.set(index, e);
                    }
                    else {
                        e.setValueReference(loadingValueReference);
                    }
                }
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
            if (createNewEntry) {
                synchronized (e) {
                    return this.loadSync(key, hash, loadingValueReference, loader);
                }
            }
            return this.waitForLoadingValue(e, key, valueReference);
        }
        
        V waitForLoadingValue(final ReferenceEntry<K, V> e, final K key, final ValueReference<K, V> valueReference) throws ExecutionException {
            if (!valueReference.isLoading()) {
                throw new AssertionError();
            }
            Preconditions.checkState(!Thread.holdsLock(e), "Recursive load of: %s", key);
            final V value = valueReference.waitForValue();
            if (value == null) {
                throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + ".");
            }
            final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
            this.recordRead(e, now);
            return value;
        }
        
        V loadSync(final K key, final int hash, final LoadingValueReference<K, V> loadingValueReference, final CacheLoader<? super K, V> loader) throws ExecutionException {
            final ListenableFuture<V> loadingFuture = loadingValueReference.loadFuture(key, loader);
            return this.getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
        }
        
        ListenableFuture<V> loadAsync(final K key, final int hash, final LoadingValueReference<K, V> loadingValueReference, final CacheLoader<? super K, V> loader) {
            final ListenableFuture<V> loadingFuture = loadingValueReference.loadFuture(key, loader);
            loadingFuture.addListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        Segment.this.getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
                    }
                    catch (final Throwable t) {
                        LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", t);
                        loadingValueReference.setException(t);
                    }
                }
            }, MoreExecutors.directExecutor());
            return loadingFuture;
        }
        
        V getAndRecordStats(final K key, final int hash, final LoadingValueReference<K, V> loadingValueReference, final ListenableFuture<V> newValue) throws ExecutionException {
            V value = null;
            try {
                value = Uninterruptibles.getUninterruptibly(newValue);
                if (value == null) {
                    throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + ".");
                }
                this.storeLoadedValue(key, hash, loadingValueReference, value);
                return value;
            }
            finally {
                if (value == null) {
                    this.removeLoadingValue(key, hash, loadingValueReference);
                }
            }
        }
        
        V scheduleRefresh(final ReferenceEntry<K, V> entry, final K key, final int hash, final V oldValue, final long now, final CacheLoader<? super K, V> loader) {
            if (this.map.refreshes() && now - entry.getWriteTime() > ((LocalCache<Object, Object>)this.map).refreshNanos && !entry.getValueReference().isLoading()) {
                final V newValue = this.refresh(key, hash, loader, true);
                if (newValue != null) {
                    return newValue;
                }
            }
            return oldValue;
        }
        
        V refresh(final K key, final int hash, final CacheLoader<? super K, V> loader, final boolean checkTime) {
            final LoadingValueReference<K, V> loadingValueReference = this.insertLoadingValueReference(key, hash, checkTime);
            if (loadingValueReference == null) {
                return null;
            }
            final ListenableFuture<V> result = this.loadAsync(key, hash, loadingValueReference, loader);
            if (result.isDone()) {
                try {
                    return Uninterruptibles.getUninterruptibly(result);
                }
                catch (final Throwable t) {}
            }
            return null;
        }
        
        LoadingValueReference<K, V> insertLoadingValueReference(final K key, final int hash, final boolean checkTime) {
            ReferenceEntry<K, V> e = null;
            this.lock();
            try {
                final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
                this.preWriteCleanup(now);
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int index = hash & table.length() - 1;
                final ReferenceEntry<K, V> first = e = table.get(index);
                while (e != null) {
                    final K entryKey = e.getKey();
                    if (e.getHash() == hash && entryKey != null && ((LocalCache<Object, Object>)this.map).keyEquivalence.equivalent(key, entryKey)) {
                        final ValueReference<K, V> valueReference = e.getValueReference();
                        if (valueReference.isLoading() || (checkTime && now - e.getWriteTime() < ((LocalCache<Object, Object>)this.map).refreshNanos)) {
                            return null;
                        }
                        ++this.modCount;
                        final LoadingValueReference<K, V> loadingValueReference = new LoadingValueReference<K, V>(valueReference);
                        e.setValueReference(loadingValueReference);
                        return loadingValueReference;
                    }
                    else {
                        e = e.getNext();
                    }
                }
                ++this.modCount;
                final LoadingValueReference<K, V> loadingValueReference2 = new LoadingValueReference<K, V>();
                e = this.newEntry(key, hash, first);
                e.setValueReference(loadingValueReference2);
                table.set(index, e);
                return loadingValueReference2;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        void tryDrainReferenceQueues() {
            if (this.tryLock()) {
                try {
                    this.drainReferenceQueues();
                }
                finally {
                    this.unlock();
                }
            }
        }
        
        void drainReferenceQueues() {
            if (this.map.usesKeyReferences()) {
                this.drainKeyReferenceQueue();
            }
            if (this.map.usesValueReferences()) {
                this.drainValueReferenceQueue();
            }
        }
        
        void drainKeyReferenceQueue() {
            int i = 0;
            Reference<? extends K> ref;
            while ((ref = this.keyReferenceQueue.poll()) != null) {
                final ReferenceEntry<K, V> entry = (ReferenceEntry)ref;
                this.map.reclaimKey(entry);
                if (++i == 16) {
                    break;
                }
            }
        }
        
        void drainValueReferenceQueue() {
            int i = 0;
            Reference<? extends V> ref;
            while ((ref = this.valueReferenceQueue.poll()) != null) {
                final ValueReference<K, V> valueReference = (ValueReference)ref;
                this.map.reclaimValue(valueReference);
                if (++i == 16) {
                    break;
                }
            }
        }
        
        void clearReferenceQueues() {
            if (this.map.usesKeyReferences()) {
                this.clearKeyReferenceQueue();
            }
            if (this.map.usesValueReferences()) {
                this.clearValueReferenceQueue();
            }
        }
        
        void clearKeyReferenceQueue() {
            while (this.keyReferenceQueue.poll() != null) {}
        }
        
        void clearValueReferenceQueue() {
            while (this.valueReferenceQueue.poll() != null) {}
        }
        
        void recordRead(final ReferenceEntry<K, V> entry, final long now) {
            if (this.map.recordsAccess()) {
                entry.setAccessTime(now);
            }
            this.recencyQueue.add(entry);
        }
        
        void recordLockedRead(final ReferenceEntry<K, V> entry, final long now) {
            if (this.map.recordsAccess()) {
                entry.setAccessTime(now);
            }
            this.accessQueue.add(entry);
        }
        
        void recordWrite(final ReferenceEntry<K, V> entry, final int weight, final long now) {
            this.drainRecencyQueue();
            this.totalWeight += weight;
            if (this.map.recordsAccess()) {
                entry.setAccessTime(now);
            }
            if (this.map.recordsWrite()) {
                entry.setWriteTime(now);
            }
            this.accessQueue.add(entry);
            this.writeQueue.add(entry);
        }
        
        void drainRecencyQueue() {
            ReferenceEntry<K, V> e;
            while ((e = this.recencyQueue.poll()) != null) {
                if (this.accessQueue.contains(e)) {
                    this.accessQueue.add(e);
                }
            }
        }
        
        void tryExpireEntries(final long now) {
            if (this.tryLock()) {
                try {
                    this.expireEntries(now);
                }
                finally {
                    this.unlock();
                }
            }
        }
        
        void expireEntries(final long now) {
            this.drainRecencyQueue();
            ReferenceEntry<K, V> e;
            while ((e = this.writeQueue.peek()) != null && this.map.isExpired(e, now)) {
                if (!this.removeEntry(e, e.getHash(), RemovalCause.EXPIRED)) {
                    throw new AssertionError();
                }
            }
            while ((e = this.accessQueue.peek()) != null && this.map.isExpired(e, now)) {
                if (!this.removeEntry(e, e.getHash(), RemovalCause.EXPIRED)) {
                    throw new AssertionError();
                }
            }
        }
        
        void enqueueNotification(final ReferenceEntry<K, V> entry, final RemovalCause cause) {
            this.enqueueNotification(entry.getKey(), entry.getHash(), entry.getValueReference(), cause);
        }
        
        void enqueueNotification(final K key, final int hash, final ValueReference<K, V> valueReference, final RemovalCause cause) {
            this.totalWeight -= valueReference.getWeight();
            if (((LocalCache<Object, Object>)this.map).removalNotificationQueue != LocalCache.DISCARDING_QUEUE) {
                final V value = valueReference.get();
                final RemovalNotification<K, V> notification = new RemovalNotification<K, V>(key, value, cause);
                ((LocalCache<Object, Object>)this.map).removalNotificationQueue.offer(notification);
            }
        }
        
        void evictEntries() {
            if (!this.map.evictsBySize()) {
                return;
            }
            this.drainRecencyQueue();
            while (this.totalWeight > this.maxSegmentWeight) {
                final ReferenceEntry<K, V> e = this.getNextEvictable();
                if (!this.removeEntry(e, e.getHash(), RemovalCause.SIZE)) {
                    throw new AssertionError();
                }
            }
        }
        
        ReferenceEntry<K, V> getNextEvictable() {
            for (final ReferenceEntry<K, V> e : this.accessQueue) {
                final int weight = e.getValueReference().getWeight();
                if (weight > 0) {
                    return e;
                }
            }
            throw new AssertionError();
        }
        
        ReferenceEntry<K, V> getFirst(final int hash) {
            final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
            return table.get(hash & table.length() - 1);
        }
        
        ReferenceEntry<K, V> getEntry(final Object key, final int hash) {
            for (ReferenceEntry<K, V> e = this.getFirst(hash); e != null; e = e.getNext()) {
                if (e.getHash() == hash) {
                    final K entryKey = e.getKey();
                    if (entryKey == null) {
                        this.tryDrainReferenceQueues();
                    }
                    else if (((LocalCache<Object, Object>)this.map).keyEquivalence.equivalent(key, entryKey)) {
                        return e;
                    }
                }
            }
            return null;
        }
        
        ReferenceEntry<K, V> getLiveEntry(final Object key, final int hash, final long now) {
            final ReferenceEntry<K, V> e = this.getEntry(key, hash);
            if (e == null) {
                return null;
            }
            if (this.map.isExpired(e, now)) {
                this.tryExpireEntries(now);
                return null;
            }
            return e;
        }
        
        V getLiveValue(final ReferenceEntry<K, V> entry, final long now) {
            if (entry.getKey() == null) {
                this.tryDrainReferenceQueues();
                return null;
            }
            final V value = entry.getValueReference().get();
            if (value == null) {
                this.tryDrainReferenceQueues();
                return null;
            }
            if (this.map.isExpired(entry, now)) {
                this.tryExpireEntries(now);
                return null;
            }
            return value;
        }
        
        V get(final Object key, final int hash) {
            try {
                if (this.count != 0) {
                    final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
                    final ReferenceEntry<K, V> e = this.getLiveEntry(key, hash, now);
                    if (e == null) {
                        return null;
                    }
                    final V value = e.getValueReference().get();
                    if (value != null) {
                        this.recordRead(e, now);
                        return (V)this.scheduleRefresh((ReferenceEntry<K, Object>)e, e.getKey(), hash, value, now, ((LocalCache<Object, Object>)this.map).defaultLoader);
                    }
                    this.tryDrainReferenceQueues();
                }
                return null;
            }
            finally {
                this.postReadCleanup();
            }
        }
        
        boolean containsKey(final Object key, final int hash) {
            try {
                if (this.count != 0) {
                    final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
                    final ReferenceEntry<K, V> e = this.getLiveEntry(key, hash, now);
                    return e != null && e.getValueReference().get() != null;
                }
                return false;
            }
            finally {
                this.postReadCleanup();
            }
        }
        
        V put(final K key, final int hash, final V value, final boolean onlyIfAbsent) {
            this.lock();
            try {
                final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
                this.preWriteCleanup(now);
                int newCount = this.count + 1;
                if (newCount > this.threshold) {
                    this.expand();
                    newCount = this.count + 1;
                }
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int index = hash & table.length() - 1;
                ReferenceEntry<K, V> e;
                final ReferenceEntry<K, V> first = e = table.get(index);
                while (e != null) {
                    final K entryKey = e.getKey();
                    if (e.getHash() == hash && entryKey != null && ((LocalCache<Object, Object>)this.map).keyEquivalence.equivalent(key, entryKey)) {
                        final ValueReference<K, V> valueReference = e.getValueReference();
                        final V entryValue = valueReference.get();
                        if (entryValue == null) {
                            ++this.modCount;
                            if (valueReference.isActive()) {
                                this.enqueueNotification(key, hash, valueReference, RemovalCause.COLLECTED);
                                this.setValue(e, key, value, now);
                                newCount = this.count;
                            }
                            else {
                                this.setValue(e, key, value, now);
                                newCount = this.count + 1;
                            }
                            this.count = newCount;
                            this.evictEntries();
                            return null;
                        }
                        if (onlyIfAbsent) {
                            this.recordLockedRead(e, now);
                            return entryValue;
                        }
                        ++this.modCount;
                        this.enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
                        this.setValue(e, key, value, now);
                        this.evictEntries();
                        return entryValue;
                    }
                    else {
                        e = e.getNext();
                    }
                }
                ++this.modCount;
                final ReferenceEntry<K, V> newEntry = this.newEntry(key, hash, first);
                this.setValue(newEntry, key, value, now);
                table.set(index, newEntry);
                newCount = this.count + 1;
                this.count = newCount;
                this.evictEntries();
                return null;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        void expand() {
            final AtomicReferenceArray<ReferenceEntry<K, V>> oldTable = this.table;
            final int oldCapacity = oldTable.length();
            if (oldCapacity >= 1073741824) {
                return;
            }
            int newCount = this.count;
            final AtomicReferenceArray<ReferenceEntry<K, V>> newTable = this.newEntryArray(oldCapacity << 1);
            this.threshold = newTable.length() * 3 / 4;
            final int newMask = newTable.length() - 1;
            for (int oldIndex = 0; oldIndex < oldCapacity; ++oldIndex) {
                final ReferenceEntry<K, V> head = oldTable.get(oldIndex);
                if (head != null) {
                    final ReferenceEntry<K, V> next = head.getNext();
                    final int headIndex = head.getHash() & newMask;
                    if (next == null) {
                        newTable.set(headIndex, head);
                    }
                    else {
                        ReferenceEntry<K, V> tail = head;
                        int tailIndex = headIndex;
                        for (ReferenceEntry<K, V> e = next; e != null; e = e.getNext()) {
                            final int newIndex = e.getHash() & newMask;
                            if (newIndex != tailIndex) {
                                tailIndex = newIndex;
                                tail = e;
                            }
                        }
                        newTable.set(tailIndex, tail);
                        for (ReferenceEntry<K, V> e = head; e != tail; e = e.getNext()) {
                            final int newIndex = e.getHash() & newMask;
                            final ReferenceEntry<K, V> newNext = newTable.get(newIndex);
                            final ReferenceEntry<K, V> newFirst = this.copyEntry(e, newNext);
                            if (newFirst != null) {
                                newTable.set(newIndex, newFirst);
                            }
                            else {
                                this.removeCollectedEntry(e);
                                --newCount;
                            }
                        }
                    }
                }
            }
            this.table = newTable;
            this.count = newCount;
        }
        
        boolean replace(final K key, final int hash, final V oldValue, final V newValue) {
            this.lock();
            try {
                final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
                this.preWriteCleanup(now);
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int index = hash & table.length() - 1;
                ReferenceEntry<K, V> e;
                final ReferenceEntry<K, V> first = e = table.get(index);
                while (e != null) {
                    final K entryKey = e.getKey();
                    if (e.getHash() == hash && entryKey != null && ((LocalCache<Object, Object>)this.map).keyEquivalence.equivalent(key, entryKey)) {
                        final ValueReference<K, V> valueReference = e.getValueReference();
                        final V entryValue = valueReference.get();
                        if (entryValue == null) {
                            if (valueReference.isActive()) {
                                int newCount = this.count - 1;
                                ++this.modCount;
                                final ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
                                newCount = this.count - 1;
                                table.set(index, newFirst);
                                this.count = newCount;
                            }
                            return false;
                        }
                        if (((LocalCache<Object, Object>)this.map).valueEquivalence.equivalent(oldValue, entryValue)) {
                            ++this.modCount;
                            this.enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
                            this.setValue(e, key, newValue, now);
                            this.evictEntries();
                            return true;
                        }
                        this.recordLockedRead(e, now);
                        return false;
                    }
                    else {
                        e = e.getNext();
                    }
                }
                return false;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        V replace(final K key, final int hash, final V newValue) {
            this.lock();
            try {
                final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
                this.preWriteCleanup(now);
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int index = hash & table.length() - 1;
                ReferenceEntry<K, V> e;
                final ReferenceEntry<K, V> first = e = table.get(index);
                while (e != null) {
                    final K entryKey = e.getKey();
                    if (e.getHash() == hash && entryKey != null && ((LocalCache<Object, Object>)this.map).keyEquivalence.equivalent(key, entryKey)) {
                        final ValueReference<K, V> valueReference = e.getValueReference();
                        final V entryValue = valueReference.get();
                        if (entryValue == null) {
                            if (valueReference.isActive()) {
                                int newCount = this.count - 1;
                                ++this.modCount;
                                final ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
                                newCount = this.count - 1;
                                table.set(index, newFirst);
                                this.count = newCount;
                            }
                            return null;
                        }
                        ++this.modCount;
                        this.enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
                        this.setValue(e, key, newValue, now);
                        this.evictEntries();
                        return entryValue;
                    }
                    else {
                        e = e.getNext();
                    }
                }
                return null;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        V remove(final Object key, final int hash) {
            this.lock();
            try {
                final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
                this.preWriteCleanup(now);
                int newCount = this.count - 1;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int index = hash & table.length() - 1;
                ReferenceEntry<K, V> e;
                for (ReferenceEntry<K, V> first = e = table.get(index); e != null; e = e.getNext()) {
                    final K entryKey = e.getKey();
                    if (e.getHash() == hash && entryKey != null && ((LocalCache<Object, Object>)this.map).keyEquivalence.equivalent(key, entryKey)) {
                        final ValueReference<K, V> valueReference = e.getValueReference();
                        final V entryValue = valueReference.get();
                        RemovalCause cause;
                        if (entryValue != null) {
                            cause = RemovalCause.EXPLICIT;
                        }
                        else {
                            if (!valueReference.isActive()) {
                                return null;
                            }
                            cause = RemovalCause.COLLECTED;
                        }
                        ++this.modCount;
                        final ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
                        newCount = this.count - 1;
                        table.set(index, newFirst);
                        this.count = newCount;
                        return entryValue;
                    }
                }
                return null;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        boolean storeLoadedValue(final K key, final int hash, final LoadingValueReference<K, V> oldValueReference, final V newValue) {
            this.lock();
            try {
                final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
                this.preWriteCleanup(now);
                int newCount = this.count + 1;
                if (newCount > this.threshold) {
                    this.expand();
                    newCount = this.count + 1;
                }
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int index = hash & table.length() - 1;
                ReferenceEntry<K, V> e;
                final ReferenceEntry<K, V> first = e = table.get(index);
                while (e != null) {
                    final K entryKey = e.getKey();
                    if (e.getHash() == hash && entryKey != null && ((LocalCache<Object, Object>)this.map).keyEquivalence.equivalent(key, entryKey)) {
                        ValueReference<K, V> valueReference = e.getValueReference();
                        final V entryValue = valueReference.get();
                        if (oldValueReference == valueReference || (entryValue == null && valueReference != LocalCache.UNSET)) {
                            ++this.modCount;
                            if (oldValueReference.isActive()) {
                                final RemovalCause cause = (entryValue == null) ? RemovalCause.COLLECTED : RemovalCause.REPLACED;
                                this.enqueueNotification(key, hash, oldValueReference, cause);
                                --newCount;
                            }
                            this.setValue(e, key, newValue, now);
                            this.count = newCount;
                            this.evictEntries();
                            return true;
                        }
                        valueReference = new WeightedStrongValueReference<K, V>(newValue, 0);
                        this.enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
                        return false;
                    }
                    else {
                        e = e.getNext();
                    }
                }
                ++this.modCount;
                final ReferenceEntry<K, V> newEntry = this.newEntry(key, hash, first);
                this.setValue(newEntry, key, newValue, now);
                table.set(index, newEntry);
                this.count = newCount;
                this.evictEntries();
                return true;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        boolean remove(final Object key, final int hash, final Object value) {
            this.lock();
            try {
                final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
                this.preWriteCleanup(now);
                int newCount = this.count - 1;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int index = hash & table.length() - 1;
                ReferenceEntry<K, V> e;
                for (ReferenceEntry<K, V> first = e = table.get(index); e != null; e = e.getNext()) {
                    final K entryKey = e.getKey();
                    if (e.getHash() == hash && entryKey != null && ((LocalCache<Object, Object>)this.map).keyEquivalence.equivalent(key, entryKey)) {
                        final ValueReference<K, V> valueReference = e.getValueReference();
                        final V entryValue = valueReference.get();
                        RemovalCause cause;
                        if (((LocalCache<Object, Object>)this.map).valueEquivalence.equivalent(value, entryValue)) {
                            cause = RemovalCause.EXPLICIT;
                        }
                        else {
                            if (entryValue != null || !valueReference.isActive()) {
                                return false;
                            }
                            cause = RemovalCause.COLLECTED;
                        }
                        ++this.modCount;
                        final ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
                        newCount = this.count - 1;
                        table.set(index, newFirst);
                        this.count = newCount;
                        return cause == RemovalCause.EXPLICIT;
                    }
                }
                return false;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        void clear() {
            if (this.count != 0) {
                this.lock();
                try {
                    final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                    for (int i = 0; i < table.length(); ++i) {
                        for (ReferenceEntry<K, V> e = table.get(i); e != null; e = e.getNext()) {
                            if (e.getValueReference().isActive()) {
                                this.enqueueNotification(e, RemovalCause.EXPLICIT);
                            }
                        }
                    }
                    for (int i = 0; i < table.length(); ++i) {
                        table.set(i, null);
                    }
                    this.clearReferenceQueues();
                    this.writeQueue.clear();
                    this.accessQueue.clear();
                    this.readCount.set(0);
                    ++this.modCount;
                    this.count = 0;
                }
                finally {
                    this.unlock();
                    this.postWriteCleanup();
                }
            }
        }
        
        ReferenceEntry<K, V> removeValueFromChain(final ReferenceEntry<K, V> first, final ReferenceEntry<K, V> entry, final K key, final int hash, final ValueReference<K, V> valueReference, final RemovalCause cause) {
            this.enqueueNotification(key, hash, valueReference, cause);
            this.writeQueue.remove(entry);
            this.accessQueue.remove(entry);
            if (valueReference.isLoading()) {
                valueReference.notifyNewValue(null);
                return first;
            }
            return this.removeEntryFromChain(first, entry);
        }
        
        ReferenceEntry<K, V> removeEntryFromChain(final ReferenceEntry<K, V> first, final ReferenceEntry<K, V> entry) {
            int newCount = this.count;
            ReferenceEntry<K, V> newFirst = entry.getNext();
            for (ReferenceEntry<K, V> e = first; e != entry; e = e.getNext()) {
                final ReferenceEntry<K, V> next = this.copyEntry(e, newFirst);
                if (next != null) {
                    newFirst = next;
                }
                else {
                    this.removeCollectedEntry(e);
                    --newCount;
                }
            }
            this.count = newCount;
            return newFirst;
        }
        
        void removeCollectedEntry(final ReferenceEntry<K, V> entry) {
            this.enqueueNotification(entry, RemovalCause.COLLECTED);
            this.writeQueue.remove(entry);
            this.accessQueue.remove(entry);
        }
        
        boolean reclaimKey(final ReferenceEntry<K, V> entry, final int hash) {
            this.lock();
            try {
                int newCount = this.count - 1;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int index = hash & table.length() - 1;
                ReferenceEntry<K, V> e;
                for (ReferenceEntry<K, V> first = e = table.get(index); e != null; e = e.getNext()) {
                    if (e == entry) {
                        ++this.modCount;
                        final ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), RemovalCause.COLLECTED);
                        newCount = this.count - 1;
                        table.set(index, newFirst);
                        this.count = newCount;
                        return true;
                    }
                }
                return false;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        boolean reclaimValue(final K key, final int hash, final ValueReference<K, V> valueReference) {
            this.lock();
            try {
                int newCount = this.count - 1;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int index = hash & table.length() - 1;
                ReferenceEntry<K, V> e;
                final ReferenceEntry<K, V> first = e = table.get(index);
                while (e != null) {
                    final K entryKey = e.getKey();
                    if (e.getHash() == hash && entryKey != null && ((LocalCache<Object, Object>)this.map).keyEquivalence.equivalent(key, entryKey)) {
                        final ValueReference<K, V> v = e.getValueReference();
                        if (v == valueReference) {
                            ++this.modCount;
                            final ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
                            newCount = this.count - 1;
                            table.set(index, newFirst);
                            this.count = newCount;
                            return true;
                        }
                        return false;
                    }
                    else {
                        e = e.getNext();
                    }
                }
                return false;
            }
            finally {
                this.unlock();
                if (!this.isHeldByCurrentThread()) {
                    this.postWriteCleanup();
                }
            }
        }
        
        boolean removeLoadingValue(final K key, final int hash, final LoadingValueReference<K, V> valueReference) {
            this.lock();
            try {
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int index = hash & table.length() - 1;
                ReferenceEntry<K, V> e;
                final ReferenceEntry<K, V> first = e = table.get(index);
                while (e != null) {
                    final K entryKey = e.getKey();
                    if (e.getHash() == hash && entryKey != null && ((LocalCache<Object, Object>)this.map).keyEquivalence.equivalent(key, entryKey)) {
                        final ValueReference<K, V> v = e.getValueReference();
                        if (v == valueReference) {
                            if (valueReference.isActive()) {
                                e.setValueReference(valueReference.getOldValue());
                            }
                            else {
                                final ReferenceEntry<K, V> newFirst = this.removeEntryFromChain(first, e);
                                table.set(index, newFirst);
                            }
                            return true;
                        }
                        return false;
                    }
                    else {
                        e = e.getNext();
                    }
                }
                return false;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        boolean removeEntry(final ReferenceEntry<K, V> entry, final int hash, final RemovalCause cause) {
            int newCount = this.count - 1;
            final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
            final int index = hash & table.length() - 1;
            ReferenceEntry<K, V> e;
            for (ReferenceEntry<K, V> first = e = table.get(index); e != null; e = e.getNext()) {
                if (e == entry) {
                    ++this.modCount;
                    final ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), cause);
                    newCount = this.count - 1;
                    table.set(index, newFirst);
                    this.count = newCount;
                    return true;
                }
            }
            return false;
        }
        
        void postReadCleanup() {
            if ((this.readCount.incrementAndGet() & 0x3F) == 0x0) {
                this.cleanUp();
            }
        }
        
        void preWriteCleanup(final long now) {
            this.runLockedCleanup(now);
        }
        
        void postWriteCleanup() {
        }
        
        void cleanUp() {
            final long now = ((LocalCache<Object, Object>)this.map).ticker.read();
            this.runLockedCleanup(now);
        }
        
        void runLockedCleanup(final long now) {
            if (this.tryLock()) {
                try {
                    this.drainReferenceQueues();
                    this.expireEntries(now);
                    this.readCount.set(0);
                }
                finally {
                    this.unlock();
                }
            }
        }
    }
    
    static class LoadingValueReference<K, V> implements ValueReference<K, V>
    {
        final SettableFuture<V> futureValue;
        final Stopwatch stopwatch;
        volatile ValueReference<K, V> oldValue;
        
        public LoadingValueReference() {
            this(unset());
        }
        
        public LoadingValueReference(final ValueReference<K, V> oldValue) {
            this.futureValue = SettableFuture.create();
            this.stopwatch = Stopwatch.createUnstarted();
            this.oldValue = oldValue;
        }
        
        @Override
        public boolean isLoading() {
            return true;
        }
        
        @Override
        public boolean isActive() {
            return this.oldValue.isActive();
        }
        
        @Override
        public int getWeight() {
            return this.oldValue.getWeight();
        }
        
        public boolean set(final V newValue) {
            return this.futureValue.set(newValue);
        }
        
        public boolean setException(final Throwable t) {
            return this.futureValue.setException(t);
        }
        
        private ListenableFuture<V> fullyFailedFuture(final Throwable t) {
            return Futures.immediateFailedFuture(t);
        }
        
        @Override
        public void notifyNewValue(final V newValue) {
            if (newValue != null) {
                this.set(newValue);
            }
            else {
                this.oldValue = (ValueReference<K, V>)unset();
            }
        }
        
        public ListenableFuture<V> loadFuture(final K key, final CacheLoader<? super K, V> loader) {
            this.stopwatch.start();
            final V previousValue = this.oldValue.get();
            try {
                if (previousValue == null) {
                    final V newValue = loader.load(key);
                    return this.set(newValue) ? this.futureValue : Futures.immediateFuture(newValue);
                }
                final ListenableFuture<V> newValue2 = loader.reload(key, previousValue);
                if (newValue2 == null) {
                    return Futures.immediateFuture((V)null);
                }
                return Futures.transform(newValue2, (Function<? super V, ? extends V>)new Function<V, V>() {
                    @Override
                    public V apply(final V newValue) {
                        LoadingValueReference.this.set(newValue);
                        return newValue;
                    }
                });
            }
            catch (final Throwable t) {
                if (t instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                return this.setException(t) ? this.futureValue : this.fullyFailedFuture(t);
            }
        }
        
        @Override
        public V waitForValue() throws ExecutionException {
            return Uninterruptibles.getUninterruptibly(this.futureValue);
        }
        
        @Override
        public V get() {
            return this.oldValue.get();
        }
        
        public ValueReference<K, V> getOldValue() {
            return this.oldValue;
        }
        
        @Override
        public ReferenceEntry<K, V> getEntry() {
            return null;
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> queue, final V value, final ReferenceEntry<K, V> entry) {
            return this;
        }
    }
    
    static final class WriteQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>>
    {
        final ReferenceEntry<K, V> head;
        
        WriteQueue() {
            this.head = new AbstractReferenceEntry<K, V>() {
                ReferenceEntry<K, V> nextWrite = this;
                ReferenceEntry<K, V> previousWrite = this;
                
                @Override
                public long getWriteTime() {
                    return Long.MAX_VALUE;
                }
                
                @Override
                public void setWriteTime(final long time) {
                }
                
                @Override
                public ReferenceEntry<K, V> getNextInWriteQueue() {
                    return this.nextWrite;
                }
                
                @Override
                public void setNextInWriteQueue(final ReferenceEntry<K, V> next) {
                    this.nextWrite = next;
                }
                
                @Override
                public ReferenceEntry<K, V> getPreviousInWriteQueue() {
                    return this.previousWrite;
                }
                
                @Override
                public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previous) {
                    this.previousWrite = previous;
                }
            };
        }
        
        @Override
        public boolean offer(final ReferenceEntry<K, V> entry) {
            connectWriteOrder(entry.getPreviousInWriteQueue(), (ReferenceEntry<Object, Object>)entry.getNextInWriteQueue());
            connectWriteOrder(this.head.getPreviousInWriteQueue(), (ReferenceEntry<Object, Object>)entry);
            connectWriteOrder(entry, (ReferenceEntry<Object, Object>)this.head);
            return true;
        }
        
        @Override
        public ReferenceEntry<K, V> peek() {
            final ReferenceEntry<K, V> next = this.head.getNextInWriteQueue();
            return (next == this.head) ? null : next;
        }
        
        @Override
        public ReferenceEntry<K, V> poll() {
            final ReferenceEntry<K, V> next = this.head.getNextInWriteQueue();
            if (next == this.head) {
                return null;
            }
            this.remove(next);
            return next;
        }
        
        @Override
        public boolean remove(final Object o) {
            final ReferenceEntry<K, V> e = (ReferenceEntry<K, V>)o;
            final ReferenceEntry<K, V> previous = e.getPreviousInWriteQueue();
            final ReferenceEntry<K, V> next = e.getNextInWriteQueue();
            connectWriteOrder(previous, (ReferenceEntry<Object, Object>)next);
            nullifyWriteOrder((ReferenceEntry<Object, Object>)e);
            return next != NullEntry.INSTANCE;
        }
        
        @Override
        public boolean contains(final Object o) {
            final ReferenceEntry<K, V> e = (ReferenceEntry<K, V>)o;
            return e.getNextInWriteQueue() != NullEntry.INSTANCE;
        }
        
        @Override
        public boolean isEmpty() {
            return this.head.getNextInWriteQueue() == this.head;
        }
        
        @Override
        public int size() {
            int size = 0;
            for (ReferenceEntry<K, V> e = this.head.getNextInWriteQueue(); e != this.head; e = e.getNextInWriteQueue()) {
                ++size;
            }
            return size;
        }
        
        @Override
        public void clear() {
            ReferenceEntry<K, V> next;
            for (ReferenceEntry<K, V> e = this.head.getNextInWriteQueue(); e != this.head; e = next) {
                next = e.getNextInWriteQueue();
                nullifyWriteOrder((ReferenceEntry<Object, Object>)e);
            }
            this.head.setNextInWriteQueue(this.head);
            this.head.setPreviousInWriteQueue(this.head);
        }
        
        @Override
        public Iterator<ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<ReferenceEntry<K, V>>(this.peek()) {
                @Override
                protected ReferenceEntry<K, V> computeNext(final ReferenceEntry<K, V> previous) {
                    final ReferenceEntry<K, V> next = previous.getNextInWriteQueue();
                    return (next == WriteQueue.this.head) ? null : next;
                }
            };
        }
    }
    
    static final class AccessQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>>
    {
        final ReferenceEntry<K, V> head;
        
        AccessQueue() {
            this.head = new AbstractReferenceEntry<K, V>() {
                ReferenceEntry<K, V> nextAccess = this;
                ReferenceEntry<K, V> previousAccess = this;
                
                @Override
                public long getAccessTime() {
                    return Long.MAX_VALUE;
                }
                
                @Override
                public void setAccessTime(final long time) {
                }
                
                @Override
                public ReferenceEntry<K, V> getNextInAccessQueue() {
                    return this.nextAccess;
                }
                
                @Override
                public void setNextInAccessQueue(final ReferenceEntry<K, V> next) {
                    this.nextAccess = next;
                }
                
                @Override
                public ReferenceEntry<K, V> getPreviousInAccessQueue() {
                    return this.previousAccess;
                }
                
                @Override
                public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previous) {
                    this.previousAccess = previous;
                }
            };
        }
        
        @Override
        public boolean offer(final ReferenceEntry<K, V> entry) {
            connectAccessOrder(entry.getPreviousInAccessQueue(), (ReferenceEntry<Object, Object>)entry.getNextInAccessQueue());
            connectAccessOrder(this.head.getPreviousInAccessQueue(), (ReferenceEntry<Object, Object>)entry);
            connectAccessOrder(entry, (ReferenceEntry<Object, Object>)this.head);
            return true;
        }
        
        @Override
        public ReferenceEntry<K, V> peek() {
            final ReferenceEntry<K, V> next = this.head.getNextInAccessQueue();
            return (next == this.head) ? null : next;
        }
        
        @Override
        public ReferenceEntry<K, V> poll() {
            final ReferenceEntry<K, V> next = this.head.getNextInAccessQueue();
            if (next == this.head) {
                return null;
            }
            this.remove(next);
            return next;
        }
        
        @Override
        public boolean remove(final Object o) {
            final ReferenceEntry<K, V> e = (ReferenceEntry<K, V>)o;
            final ReferenceEntry<K, V> previous = e.getPreviousInAccessQueue();
            final ReferenceEntry<K, V> next = e.getNextInAccessQueue();
            connectAccessOrder(previous, (ReferenceEntry<Object, Object>)next);
            nullifyAccessOrder((ReferenceEntry<Object, Object>)e);
            return next != NullEntry.INSTANCE;
        }
        
        @Override
        public boolean contains(final Object o) {
            final ReferenceEntry<K, V> e = (ReferenceEntry<K, V>)o;
            return e.getNextInAccessQueue() != NullEntry.INSTANCE;
        }
        
        @Override
        public boolean isEmpty() {
            return this.head.getNextInAccessQueue() == this.head;
        }
        
        @Override
        public int size() {
            int size = 0;
            for (ReferenceEntry<K, V> e = this.head.getNextInAccessQueue(); e != this.head; e = e.getNextInAccessQueue()) {
                ++size;
            }
            return size;
        }
        
        @Override
        public void clear() {
            ReferenceEntry<K, V> next;
            for (ReferenceEntry<K, V> e = this.head.getNextInAccessQueue(); e != this.head; e = next) {
                next = e.getNextInAccessQueue();
                nullifyAccessOrder((ReferenceEntry<Object, Object>)e);
            }
            this.head.setNextInAccessQueue(this.head);
            this.head.setPreviousInAccessQueue(this.head);
        }
        
        @Override
        public Iterator<ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<ReferenceEntry<K, V>>(this.peek()) {
                @Override
                protected ReferenceEntry<K, V> computeNext(final ReferenceEntry<K, V> previous) {
                    final ReferenceEntry<K, V> next = previous.getNextInAccessQueue();
                    return (next == AccessQueue.this.head) ? null : next;
                }
            };
        }
    }
    
    static class LocalManualCache<K, V> implements Cache<K, V>, Serializable
    {
        private static final long serialVersionUID = 1L;
        final LocalCache<K, V> localCache;
        
        LocalManualCache(final CacheBuilder<? super K, ? super V> builder) {
            this((LocalCache)new LocalCache(builder, null, null));
        }
        
        private LocalManualCache(final LocalCache<K, V> localCache) {
            this.localCache = localCache;
        }
        
        @Override
        public V getIfPresent(final Object key) {
            return this.localCache.getIfPresent(key);
        }
        
        @Override
        public void put(final K key, final V value) {
            this.localCache.put(key, value);
        }
    }
    
    static class LocalLoadingCache<K, V> extends LocalManualCache<K, V> implements LoadingCache<K, V>
    {
        private static final long serialVersionUID = 1L;
        
        LocalLoadingCache(final CacheBuilder<? super K, ? super V> builder, final CacheLoader<? super K, V> loader) {
            super((LocalCache)new LocalCache(builder, Preconditions.checkNotNull(loader), null));
        }
        
        @Override
        public V get(final K key) throws ExecutionException {
            return this.localCache.getOrLoad(key);
        }
        
        public V getUnchecked(final K key) {
            try {
                return this.get(key);
            }
            catch (final ExecutionException e) {
                throw new UncheckedExecutionException(e.getCause());
            }
        }
        
        @Override
        public final V apply(final K key) {
            return this.getUnchecked(key);
        }
    }
    
    abstract class HashIterator<T> implements Iterator<T>
    {
        int nextSegmentIndex;
        int nextTableIndex;
        Segment<K, V> currentSegment;
        AtomicReferenceArray<ReferenceEntry<K, V>> currentTable;
        ReferenceEntry<K, V> nextEntry;
        WriteThroughEntry nextExternal;
        WriteThroughEntry lastReturned;
        
        HashIterator() {
            this.nextSegmentIndex = LocalCache.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.advance();
        }
        
        @Override
        public abstract T next();
        
        final void advance() {
            this.nextExternal = null;
            if (this.nextInChain()) {
                return;
            }
            if (this.nextInTable()) {
                return;
            }
            while (this.nextSegmentIndex >= 0) {
                this.currentSegment = LocalCache.this.segments[this.nextSegmentIndex--];
                if (this.currentSegment.count != 0) {
                    this.currentTable = this.currentSegment.table;
                    this.nextTableIndex = this.currentTable.length() - 1;
                    if (this.nextInTable()) {
                        return;
                    }
                    continue;
                }
            }
        }
        
        boolean nextInChain() {
            if (this.nextEntry != null) {
                this.nextEntry = this.nextEntry.getNext();
                while (this.nextEntry != null) {
                    if (this.advanceTo(this.nextEntry)) {
                        return true;
                    }
                    this.nextEntry = this.nextEntry.getNext();
                }
            }
            return false;
        }
        
        boolean nextInTable() {
            while (this.nextTableIndex >= 0) {
                final ReferenceEntry nextEntry = this.currentTable.get(this.nextTableIndex--);
                this.nextEntry = nextEntry;
                if (nextEntry != null && (this.advanceTo(this.nextEntry) || this.nextInChain())) {
                    return true;
                }
            }
            return false;
        }
        
        boolean advanceTo(final ReferenceEntry<K, V> entry) {
            try {
                final long now = LocalCache.this.ticker.read();
                final K key = entry.getKey();
                final V value = (V)LocalCache.this.getLiveValue(entry, now);
                if (value != null) {
                    this.nextExternal = new WriteThroughEntry(key, value);
                    return true;
                }
                return false;
            }
            finally {
                this.currentSegment.postReadCleanup();
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.nextExternal != null;
        }
        
        WriteThroughEntry nextEntry() {
            if (this.nextExternal == null) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.nextExternal;
            this.advance();
            return this.lastReturned;
        }
        
        @Override
        public void remove() {
            Preconditions.checkState(this.lastReturned != null);
            LocalCache.this.remove(this.lastReturned.getKey());
            this.lastReturned = null;
        }
    }
    
    final class KeyIterator extends HashIterator<K>
    {
        @Override
        public K next() {
            return this.nextEntry().getKey();
        }
    }
    
    final class ValueIterator extends HashIterator<V>
    {
        @Override
        public V next() {
            return this.nextEntry().getValue();
        }
    }
    
    final class WriteThroughEntry implements Map.Entry<K, V>
    {
        final K key;
        final V value;
        
        WriteThroughEntry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public boolean equals(final Object object) {
            if (object instanceof Map.Entry) {
                final Map.Entry<?, ?> that = (Map.Entry<?, ?>)object;
                return this.key.equals(that.getKey()) && this.value.equals(that.getValue());
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.key.hashCode() ^ this.value.hashCode();
        }
        
        @Override
        public V setValue(final V newValue) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
            return this.getKey() + "=" + this.getValue();
        }
    }
    
    final class EntryIterator extends HashIterator<Map.Entry<K, V>>
    {
        @Override
        public Map.Entry<K, V> next() {
            return this.nextEntry();
        }
    }
    
    abstract class AbstractCacheSet<T> extends AbstractSet<T>
    {
        final ConcurrentMap<?, ?> map;
        
        AbstractCacheSet(final ConcurrentMap<?, ?> map) {
            this.map = map;
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        @Override
        public void clear() {
            this.map.clear();
        }
    }
    
    final class KeySet extends AbstractCacheSet<K>
    {
        KeySet(final ConcurrentMap<?, ?> map) {
            super(map);
        }
        
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.map.containsKey(o);
        }
        
        @Override
        public boolean remove(final Object o) {
            return this.map.remove(o) != null;
        }
    }
    
    final class Values extends AbstractCollection<V>
    {
        private final ConcurrentMap<?, ?> map;
        
        Values(final ConcurrentMap<?, ?> map) {
            this.map = map;
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        @Override
        public void clear() {
            this.map.clear();
        }
        
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.map.containsValue(o);
        }
    }
    
    final class EntrySet extends AbstractCacheSet<Map.Entry<K, V>>
    {
        EntrySet(final ConcurrentMap<?, ?> map) {
            super(map);
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            final Object key = e.getKey();
            if (key == null) {
                return false;
            }
            final V v = LocalCache.this.get(key);
            return v != null && LocalCache.this.valueEquivalence.equivalent(e.getValue(), v);
        }
        
        @Override
        public boolean remove(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
            final Object key = e.getKey();
            return key != null && LocalCache.this.remove(key, e.getValue());
        }
    }
    
    interface ReferenceEntry<K, V>
    {
        ValueReference<K, V> getValueReference();
        
        void setValueReference(final ValueReference<K, V> p0);
        
        ReferenceEntry<K, V> getNext();
        
        int getHash();
        
        K getKey();
        
        long getAccessTime();
        
        void setAccessTime(final long p0);
        
        ReferenceEntry<K, V> getNextInAccessQueue();
        
        void setNextInAccessQueue(final ReferenceEntry<K, V> p0);
        
        ReferenceEntry<K, V> getPreviousInAccessQueue();
        
        void setPreviousInAccessQueue(final ReferenceEntry<K, V> p0);
        
        long getWriteTime();
        
        void setWriteTime(final long p0);
        
        ReferenceEntry<K, V> getNextInWriteQueue();
        
        void setNextInWriteQueue(final ReferenceEntry<K, V> p0);
        
        ReferenceEntry<K, V> getPreviousInWriteQueue();
        
        void setPreviousInWriteQueue(final ReferenceEntry<K, V> p0);
    }
    
    interface ValueReference<K, V>
    {
        V get();
        
        V waitForValue() throws ExecutionException;
        
        int getWeight();
        
        ReferenceEntry<K, V> getEntry();
        
        ValueReference<K, V> copyFor(final ReferenceQueue<V> p0, final V p1, final ReferenceEntry<K, V> p2);
        
        void notifyNewValue(final V p0);
        
        boolean isLoading();
        
        boolean isActive();
    }
}
