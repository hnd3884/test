package mssql.googlecode.concurrentlinkedhashmap;

import java.util.HashMap;
import java.util.AbstractQueue;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Queue;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;

public final class ConcurrentLinkedHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable
{
    static final int NCPU;
    static final long MAXIMUM_CAPACITY = 9223372034707292160L;
    static final int NUMBER_OF_READ_BUFFERS;
    static final int READ_BUFFERS_MASK;
    static final int READ_BUFFER_THRESHOLD = 32;
    static final int READ_BUFFER_DRAIN_THRESHOLD = 64;
    static final int READ_BUFFER_SIZE = 128;
    static final int READ_BUFFER_INDEX_MASK = 127;
    static final int WRITE_BUFFER_DRAIN_THRESHOLD = 16;
    static final Queue<?> DISCARDING_QUEUE;
    final ConcurrentMap<K, Node<K, V>> data;
    final int concurrencyLevel;
    final long[] readBufferReadCount;
    final LinkedDeque<Node<K, V>> evictionDeque;
    final AtomicLong weightedSize;
    final AtomicLong capacity;
    final Lock evictionLock;
    final Queue<Runnable> writeBuffer;
    final AtomicLong[] readBufferWriteCount;
    final AtomicLong[] readBufferDrainAtWriteCount;
    final AtomicReference<Node<K, V>>[][] readBuffers;
    final AtomicReference<DrainStatus> drainStatus;
    final EntryWeigher<? super K, ? super V> weigher;
    final Queue<Node<K, V>> pendingNotifications;
    final EvictionListener<K, V> listener;
    transient Set<K> keySet;
    transient Collection<V> values;
    transient Set<Map.Entry<K, V>> entrySet;
    static final long serialVersionUID = 1L;
    
    static int ceilingNextPowerOfTwo(final int x) {
        return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
    }
    
    private ConcurrentLinkedHashMap(final Builder<K, V> builder) {
        this.concurrencyLevel = builder.concurrencyLevel;
        this.capacity = new AtomicLong(Math.min(builder.capacity, 9223372034707292160L));
        this.data = new ConcurrentHashMap<K, Node<K, V>>(builder.initialCapacity, 0.75f, this.concurrencyLevel);
        this.weigher = builder.weigher;
        this.evictionLock = new ReentrantLock();
        this.weightedSize = new AtomicLong();
        this.evictionDeque = new LinkedDeque<Node<K, V>>();
        this.writeBuffer = new ConcurrentLinkedQueue<Runnable>();
        this.drainStatus = new AtomicReference<DrainStatus>(DrainStatus.IDLE);
        this.readBufferReadCount = new long[ConcurrentLinkedHashMap.NUMBER_OF_READ_BUFFERS];
        this.readBufferWriteCount = new AtomicLong[ConcurrentLinkedHashMap.NUMBER_OF_READ_BUFFERS];
        this.readBufferDrainAtWriteCount = new AtomicLong[ConcurrentLinkedHashMap.NUMBER_OF_READ_BUFFERS];
        this.readBuffers = new AtomicReference[ConcurrentLinkedHashMap.NUMBER_OF_READ_BUFFERS][128];
        for (int i = 0; i < ConcurrentLinkedHashMap.NUMBER_OF_READ_BUFFERS; ++i) {
            this.readBufferWriteCount[i] = new AtomicLong();
            this.readBufferDrainAtWriteCount[i] = new AtomicLong();
            this.readBuffers[i] = new AtomicReference[128];
            for (int j = 0; j < 128; ++j) {
                this.readBuffers[i][j] = new AtomicReference<Node<K, V>>();
            }
        }
        this.listener = builder.listener;
        this.pendingNotifications = (Queue<Node<K, V>>)((this.listener == DiscardingListener.INSTANCE) ? ConcurrentLinkedHashMap.DISCARDING_QUEUE : new ConcurrentLinkedQueue<Object>());
    }
    
    static void checkNotNull(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }
    
    static void checkArgument(final boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }
    
    static void checkState(final boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }
    
    public long capacity() {
        return this.capacity.get();
    }
    
    public void setCapacity(final long capacity) {
        checkArgument(capacity >= 0L);
        this.evictionLock.lock();
        try {
            this.capacity.lazySet(Math.min(capacity, 9223372034707292160L));
            this.drainBuffers();
            this.evict();
        }
        finally {
            this.evictionLock.unlock();
        }
        this.notifyListener();
    }
    
    boolean hasOverflowed() {
        return this.weightedSize.get() > this.capacity.get();
    }
    
    void evict() {
        while (this.hasOverflowed()) {
            final Node<K, V> node = this.evictionDeque.poll();
            if (node == null) {
                return;
            }
            if (this.data.remove(node.key, node)) {
                this.pendingNotifications.add(node);
            }
            this.makeDead(node);
        }
    }
    
    void afterRead(final Node<K, V> node) {
        final int bufferIndex = readBufferIndex();
        final long writeCount = this.recordRead(bufferIndex, node);
        this.drainOnReadIfNeeded(bufferIndex, writeCount);
        this.notifyListener();
    }
    
    static int readBufferIndex() {
        return (int)Thread.currentThread().getId() & ConcurrentLinkedHashMap.READ_BUFFERS_MASK;
    }
    
    long recordRead(final int bufferIndex, final Node<K, V> node) {
        final AtomicLong counter = this.readBufferWriteCount[bufferIndex];
        final long writeCount = counter.get();
        counter.lazySet(writeCount + 1L);
        final int index = (int)(writeCount & 0x7FL);
        this.readBuffers[bufferIndex][index].lazySet(node);
        return writeCount;
    }
    
    void drainOnReadIfNeeded(final int bufferIndex, final long writeCount) {
        final long pending = writeCount - this.readBufferDrainAtWriteCount[bufferIndex].get();
        final boolean delayable = pending < 32L;
        final DrainStatus status = this.drainStatus.get();
        if (status.shouldDrainBuffers(delayable)) {
            this.tryToDrainBuffers();
        }
    }
    
    void afterWrite(final Runnable task) {
        this.writeBuffer.add(task);
        this.drainStatus.lazySet(DrainStatus.REQUIRED);
        this.tryToDrainBuffers();
        this.notifyListener();
    }
    
    void tryToDrainBuffers() {
        if (this.evictionLock.tryLock()) {
            try {
                this.drainStatus.lazySet(DrainStatus.PROCESSING);
                this.drainBuffers();
            }
            finally {
                this.drainStatus.compareAndSet(DrainStatus.PROCESSING, DrainStatus.IDLE);
                this.evictionLock.unlock();
            }
        }
    }
    
    void drainBuffers() {
        this.drainReadBuffers();
        this.drainWriteBuffer();
    }
    
    void drainReadBuffers() {
        final int start = (int)Thread.currentThread().getId();
        for (int end = start + ConcurrentLinkedHashMap.NUMBER_OF_READ_BUFFERS, i = start; i < end; ++i) {
            this.drainReadBuffer(i & ConcurrentLinkedHashMap.READ_BUFFERS_MASK);
        }
    }
    
    void drainReadBuffer(final int bufferIndex) {
        final long writeCount = this.readBufferWriteCount[bufferIndex].get();
        for (int i = 0; i < 64; ++i) {
            final int index = (int)(this.readBufferReadCount[bufferIndex] & 0x7FL);
            final AtomicReference<Node<K, V>> slot = this.readBuffers[bufferIndex][index];
            final Node<K, V> node = slot.get();
            if (node == null) {
                break;
            }
            slot.lazySet(null);
            this.applyRead(node);
            final long[] readBufferReadCount = this.readBufferReadCount;
            ++readBufferReadCount[bufferIndex];
        }
        this.readBufferDrainAtWriteCount[bufferIndex].lazySet(writeCount);
    }
    
    void applyRead(final Node<K, V> node) {
        if (this.evictionDeque.contains(node)) {
            this.evictionDeque.moveToBack(node);
        }
    }
    
    void drainWriteBuffer() {
        for (int i = 0; i < 16; ++i) {
            final Runnable task = this.writeBuffer.poll();
            if (task == null) {
                break;
            }
            task.run();
        }
    }
    
    boolean tryToRetire(final Node<K, V> node, final WeightedValue<V> expect) {
        if (expect.isAlive()) {
            final WeightedValue<V> retired = new WeightedValue<V>(expect.value, -expect.weight);
            return node.compareAndSet((WeightedValue<V>)expect, (WeightedValue<V>)retired);
        }
        return false;
    }
    
    void makeRetired(final Node<K, V> node) {
        while (true) {
            final WeightedValue<V> current = (WeightedValue<V>)node.get();
            if (!current.isAlive()) {
                return;
            }
            final WeightedValue<V> retired = new WeightedValue<V>(current.value, -current.weight);
            if (node.compareAndSet((WeightedValue<V>)current, (WeightedValue<V>)retired)) {
                return;
            }
        }
    }
    
    void makeDead(final Node<K, V> node) {
        WeightedValue<V> current;
        WeightedValue<V> dead;
        do {
            current = (WeightedValue)node.get();
            dead = new WeightedValue<V>(current.value, 0);
        } while (!node.compareAndSet((WeightedValue<V>)current, (WeightedValue<V>)dead));
        this.weightedSize.lazySet(this.weightedSize.get() - Math.abs(current.weight));
    }
    
    void notifyListener() {
        Node<K, V> node;
        while ((node = this.pendingNotifications.poll()) != null) {
            this.listener.onEviction(node.key, node.getValue());
        }
    }
    
    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }
    
    @Override
    public int size() {
        return this.data.size();
    }
    
    public long weightedSize() {
        return Math.max(0L, this.weightedSize.get());
    }
    
    @Override
    public void clear() {
        this.evictionLock.lock();
        try {
            Node<K, V> node;
            while ((node = this.evictionDeque.poll()) != null) {
                this.data.remove(node.key, node);
                this.makeDead(node);
            }
            for (final AtomicReference<Node<K, V>>[] array : this.readBuffers) {
                final AtomicReference<Node<K, V>>[] buffer = array;
                for (final AtomicReference<Node<K, V>> slot : array) {
                    slot.lazySet(null);
                }
            }
            Runnable task;
            while ((task = this.writeBuffer.poll()) != null) {
                task.run();
            }
        }
        finally {
            this.evictionLock.unlock();
        }
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.data.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        checkNotNull(value);
        for (final Node<K, V> node : this.data.values()) {
            if (node.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public V get(final Object key) {
        final Node<K, V> node = this.data.get(key);
        if (node == null) {
            return null;
        }
        this.afterRead(node);
        return node.getValue();
    }
    
    public V getQuietly(final Object key) {
        final Node<K, V> node = this.data.get(key);
        return (node == null) ? null : node.getValue();
    }
    
    @Override
    public V put(final K key, final V value) {
        return this.put(key, value, false);
    }
    
    @Override
    public V putIfAbsent(final K key, final V value) {
        return this.put(key, value, true);
    }
    
    V put(final K key, final V value, final boolean onlyIfAbsent) {
        checkNotNull(key);
        checkNotNull(value);
        final int weight = this.weigher.weightOf((Object)key, (Object)value);
        final WeightedValue<V> weightedValue = new WeightedValue<V>(value, weight);
        final Node<K, V> node = new Node<K, V>(key, weightedValue);
        while (true) {
            final Node<K, V> prior = this.data.putIfAbsent(node.key, node);
            if (prior == null) {
                this.afterWrite(new AddTask(node, weight));
                return null;
            }
            if (onlyIfAbsent) {
                this.afterRead(prior);
                return prior.getValue();
            }
            while (true) {
                final WeightedValue<V> oldWeightedValue = (WeightedValue<V>)prior.get();
                if (!oldWeightedValue.isAlive()) {
                    break;
                }
                if (prior.compareAndSet((WeightedValue<V>)oldWeightedValue, (WeightedValue<V>)weightedValue)) {
                    final int weightedDifference = weight - oldWeightedValue.weight;
                    if (weightedDifference == 0) {
                        this.afterRead(prior);
                    }
                    else {
                        this.afterWrite(new UpdateTask(prior, weightedDifference));
                    }
                    return oldWeightedValue.value;
                }
            }
        }
    }
    
    @Override
    public V remove(final Object key) {
        final Node<K, V> node = this.data.remove(key);
        if (node == null) {
            return null;
        }
        this.makeRetired(node);
        this.afterWrite(new RemovalTask(node));
        return node.getValue();
    }
    
    @Override
    public boolean remove(final Object key, final Object value) {
        final Node<K, V> node = this.data.get(key);
        if (node == null || value == null) {
            return false;
        }
        WeightedValue<V> weightedValue = (WeightedValue<V>)node.get();
        while (weightedValue.contains(value)) {
            if (this.tryToRetire(node, weightedValue)) {
                if (this.data.remove(key, node)) {
                    this.afterWrite(new RemovalTask(node));
                    return true;
                }
                break;
            }
            else {
                weightedValue = (WeightedValue)node.get();
                if (weightedValue.isAlive()) {
                    continue;
                }
                break;
            }
        }
        return false;
    }
    
    @Override
    public V replace(final K key, final V value) {
        checkNotNull(key);
        checkNotNull(value);
        final int weight = this.weigher.weightOf((Object)key, (Object)value);
        final WeightedValue<V> weightedValue = new WeightedValue<V>(value, weight);
        final Node<K, V> node = this.data.get(key);
        if (node == null) {
            return null;
        }
        while (true) {
            final WeightedValue<V> oldWeightedValue = (WeightedValue<V>)node.get();
            if (!oldWeightedValue.isAlive()) {
                return null;
            }
            if (node.compareAndSet((WeightedValue<V>)oldWeightedValue, (WeightedValue<V>)weightedValue)) {
                final int weightedDifference = weight - oldWeightedValue.weight;
                if (weightedDifference == 0) {
                    this.afterRead(node);
                }
                else {
                    this.afterWrite(new UpdateTask(node, weightedDifference));
                }
                return oldWeightedValue.value;
            }
        }
    }
    
    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        checkNotNull(key);
        checkNotNull(oldValue);
        checkNotNull(newValue);
        final int weight = this.weigher.weightOf((Object)key, (Object)newValue);
        final WeightedValue<V> newWeightedValue = new WeightedValue<V>(newValue, weight);
        final Node<K, V> node = this.data.get(key);
        if (node == null) {
            return false;
        }
        while (true) {
            final WeightedValue<V> weightedValue = (WeightedValue<V>)node.get();
            if (!weightedValue.isAlive() || !weightedValue.contains(oldValue)) {
                return false;
            }
            if (node.compareAndSet((WeightedValue<V>)weightedValue, (WeightedValue<V>)newWeightedValue)) {
                final int weightedDifference = weight - weightedValue.weight;
                if (weightedDifference == 0) {
                    this.afterRead(node);
                }
                else {
                    this.afterWrite(new UpdateTask(node, weightedDifference));
                }
                return true;
            }
        }
    }
    
    @Override
    public Set<K> keySet() {
        final Set<K> ks = this.keySet;
        return (ks == null) ? (this.keySet = new KeySet()) : ks;
    }
    
    public Set<K> ascendingKeySet() {
        return this.ascendingKeySetWithLimit(Integer.MAX_VALUE);
    }
    
    public Set<K> ascendingKeySetWithLimit(final int limit) {
        return this.orderedKeySet(true, limit);
    }
    
    public Set<K> descendingKeySet() {
        return this.descendingKeySetWithLimit(Integer.MAX_VALUE);
    }
    
    public Set<K> descendingKeySetWithLimit(final int limit) {
        return this.orderedKeySet(false, limit);
    }
    
    Set<K> orderedKeySet(final boolean ascending, final int limit) {
        checkArgument(limit >= 0);
        this.evictionLock.lock();
        try {
            this.drainBuffers();
            final int initialCapacity = (this.weigher == Weighers.entrySingleton()) ? Math.min(limit, (int)this.weightedSize()) : 16;
            final Set<K> keys = new LinkedHashSet<K>(initialCapacity);
            final Iterator<Node<K, V>> iterator = ascending ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();
            while (iterator.hasNext() && limit > keys.size()) {
                keys.add((K)iterator.next().key);
            }
            return Collections.unmodifiableSet((Set<? extends K>)keys);
        }
        finally {
            this.evictionLock.unlock();
        }
    }
    
    @Override
    public Collection<V> values() {
        final Collection<V> vs = this.values;
        return (vs == null) ? (this.values = new Values()) : vs;
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final Set<Map.Entry<K, V>> es = this.entrySet;
        return (es == null) ? (this.entrySet = new EntrySet()) : es;
    }
    
    public Map<K, V> ascendingMap() {
        return this.ascendingMapWithLimit(Integer.MAX_VALUE);
    }
    
    public Map<K, V> ascendingMapWithLimit(final int limit) {
        return this.orderedMap(true, limit);
    }
    
    public Map<K, V> descendingMap() {
        return this.descendingMapWithLimit(Integer.MAX_VALUE);
    }
    
    public Map<K, V> descendingMapWithLimit(final int limit) {
        return this.orderedMap(false, limit);
    }
    
    Map<K, V> orderedMap(final boolean ascending, final int limit) {
        checkArgument(limit >= 0);
        this.evictionLock.lock();
        try {
            this.drainBuffers();
            final int initialCapacity = (this.weigher == Weighers.entrySingleton()) ? Math.min(limit, (int)this.weightedSize()) : 16;
            final Map<K, V> map = new LinkedHashMap<K, V>(initialCapacity);
            final Iterator<Node<K, V>> iterator = ascending ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();
            while (iterator.hasNext() && limit > map.size()) {
                final Node<K, V> node = iterator.next();
                map.put(node.key, node.getValue());
            }
            return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
        }
        finally {
            this.evictionLock.unlock();
        }
    }
    
    Object writeReplace() {
        return new SerializationProxy((ConcurrentLinkedHashMap<Object, Object>)this);
    }
    
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }
    
    static {
        NCPU = Runtime.getRuntime().availableProcessors();
        NUMBER_OF_READ_BUFFERS = ceilingNextPowerOfTwo(ConcurrentLinkedHashMap.NCPU);
        READ_BUFFERS_MASK = ConcurrentLinkedHashMap.NUMBER_OF_READ_BUFFERS - 1;
        DISCARDING_QUEUE = new DiscardingQueue();
    }
    
    final class AddTask implements Runnable
    {
        final Node<K, V> node;
        final int weight;
        
        AddTask(final Node<K, V> node, final int weight) {
            this.weight = weight;
            this.node = node;
        }
        
        @Override
        public void run() {
            ConcurrentLinkedHashMap.this.weightedSize.lazySet(ConcurrentLinkedHashMap.this.weightedSize.get() + this.weight);
            if (this.node.get().isAlive()) {
                ConcurrentLinkedHashMap.this.evictionDeque.add(this.node);
                ConcurrentLinkedHashMap.this.evict();
            }
        }
    }
    
    final class RemovalTask implements Runnable
    {
        final Node<K, V> node;
        
        RemovalTask(final Node<K, V> node) {
            this.node = node;
        }
        
        @Override
        public void run() {
            ConcurrentLinkedHashMap.this.evictionDeque.remove(this.node);
            ConcurrentLinkedHashMap.this.makeDead(this.node);
        }
    }
    
    final class UpdateTask implements Runnable
    {
        final int weightDifference;
        final Node<K, V> node;
        
        public UpdateTask(final Node<K, V> node, final int weightDifference) {
            this.weightDifference = weightDifference;
            this.node = node;
        }
        
        @Override
        public void run() {
            ConcurrentLinkedHashMap.this.weightedSize.lazySet(ConcurrentLinkedHashMap.this.weightedSize.get() + this.weightDifference);
            ConcurrentLinkedHashMap.this.applyRead(this.node);
            ConcurrentLinkedHashMap.this.evict();
        }
    }
    
    enum DrainStatus
    {
        IDLE {
            @Override
            boolean shouldDrainBuffers(final boolean delayable) {
                return !delayable;
            }
        }, 
        REQUIRED {
            @Override
            boolean shouldDrainBuffers(final boolean delayable) {
                return true;
            }
        }, 
        PROCESSING {
            @Override
            boolean shouldDrainBuffers(final boolean delayable) {
                return false;
            }
        };
        
        abstract boolean shouldDrainBuffers(final boolean p0);
    }
    
    static final class WeightedValue<V>
    {
        final int weight;
        final V value;
        
        WeightedValue(final V value, final int weight) {
            this.weight = weight;
            this.value = value;
        }
        
        boolean contains(final Object o) {
            return o == this.value || this.value.equals(o);
        }
        
        boolean isAlive() {
            return this.weight > 0;
        }
        
        boolean isRetired() {
            return this.weight < 0;
        }
        
        boolean isDead() {
            return this.weight == 0;
        }
    }
    
    static final class Node<K, V> extends AtomicReference<WeightedValue<V>> implements Linked<Node<K, V>>
    {
        final K key;
        Node<K, V> prev;
        Node<K, V> next;
        
        Node(final K key, final WeightedValue<V> weightedValue) {
            super(weightedValue);
            this.key = key;
        }
        
        @Override
        public Node<K, V> getPrevious() {
            return this.prev;
        }
        
        @Override
        public void setPrevious(final Node<K, V> prev) {
            this.prev = prev;
        }
        
        @Override
        public Node<K, V> getNext() {
            return this.next;
        }
        
        @Override
        public void setNext(final Node<K, V> next) {
            this.next = next;
        }
        
        V getValue() {
            return (V)this.get().value;
        }
    }
    
    final class KeySet extends AbstractSet<K>
    {
        final ConcurrentLinkedHashMap<K, V> map;
        
        KeySet() {
            this.map = ConcurrentLinkedHashMap.this;
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
        
        @Override
        public void clear() {
            this.map.clear();
        }
        
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public boolean contains(final Object obj) {
            return ConcurrentLinkedHashMap.this.containsKey(obj);
        }
        
        @Override
        public boolean remove(final Object obj) {
            return this.map.remove(obj) != null;
        }
        
        @Override
        public Object[] toArray() {
            return this.map.data.keySet().toArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            return this.map.data.keySet().toArray(array);
        }
    }
    
    final class KeyIterator implements Iterator<K>
    {
        final Iterator<K> iterator;
        K current;
        
        KeyIterator() {
            this.iterator = ConcurrentLinkedHashMap.this.data.keySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public K next() {
            return this.current = this.iterator.next();
        }
        
        @Override
        public void remove() {
            ConcurrentLinkedHashMap.checkState(this.current != null);
            ConcurrentLinkedHashMap.this.remove(this.current);
            this.current = null;
        }
    }
    
    final class Values extends AbstractCollection<V>
    {
        @Override
        public int size() {
            return ConcurrentLinkedHashMap.this.size();
        }
        
        @Override
        public void clear() {
            ConcurrentLinkedHashMap.this.clear();
        }
        
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            return ConcurrentLinkedHashMap.this.containsValue(o);
        }
    }
    
    final class ValueIterator implements Iterator<V>
    {
        final Iterator<Node<K, V>> iterator;
        Node<K, V> current;
        
        ValueIterator() {
            this.iterator = ConcurrentLinkedHashMap.this.data.values().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public V next() {
            this.current = this.iterator.next();
            return this.current.getValue();
        }
        
        @Override
        public void remove() {
            ConcurrentLinkedHashMap.checkState(this.current != null);
            ConcurrentLinkedHashMap.this.remove(this.current.key);
            this.current = null;
        }
    }
    
    final class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        final ConcurrentLinkedHashMap<K, V> map;
        
        EntrySet() {
            this.map = ConcurrentLinkedHashMap.this;
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
        
        @Override
        public void clear() {
            this.map.clear();
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public boolean contains(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            final Node<K, V> node = this.map.data.get(entry.getKey());
            return node != null && node.getValue().equals(entry.getValue());
        }
        
        @Override
        public boolean add(final Map.Entry<K, V> entry) {
            return this.map.putIfAbsent(entry.getKey(), entry.getValue()) == null;
        }
        
        @Override
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            return this.map.remove(entry.getKey(), entry.getValue());
        }
    }
    
    final class EntryIterator implements Iterator<Map.Entry<K, V>>
    {
        final Iterator<Node<K, V>> iterator;
        Node<K, V> current;
        
        EntryIterator() {
            this.iterator = ConcurrentLinkedHashMap.this.data.values().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public Map.Entry<K, V> next() {
            this.current = this.iterator.next();
            return new WriteThroughEntry(this.current);
        }
        
        @Override
        public void remove() {
            ConcurrentLinkedHashMap.checkState(this.current != null);
            ConcurrentLinkedHashMap.this.remove(this.current.key);
            this.current = null;
        }
    }
    
    final class WriteThroughEntry extends SimpleEntry<K, V>
    {
        static final long serialVersionUID = 1L;
        
        WriteThroughEntry(final Node<K, V> node) {
            super(node.key, node.getValue());
        }
        
        @Override
        public V setValue(final V value) {
            ConcurrentLinkedHashMap.this.put(((SimpleEntry<K, V>)this).getKey(), value);
            return super.setValue(value);
        }
        
        Object writeReplace() {
            return new SimpleEntry(this);
        }
    }
    
    static final class BoundedEntryWeigher<K, V> implements EntryWeigher<K, V>, Serializable
    {
        static final long serialVersionUID = 1L;
        final EntryWeigher<? super K, ? super V> weigher;
        
        BoundedEntryWeigher(final EntryWeigher<? super K, ? super V> weigher) {
            ConcurrentLinkedHashMap.checkNotNull(weigher);
            this.weigher = weigher;
        }
        
        @Override
        public int weightOf(final K key, final V value) {
            final int weight = this.weigher.weightOf((Object)key, (Object)value);
            ConcurrentLinkedHashMap.checkArgument(weight >= 1);
            return weight;
        }
        
        Object writeReplace() {
            return this.weigher;
        }
    }
    
    static final class DiscardingQueue extends AbstractQueue<Object>
    {
        @Override
        public boolean add(final Object e) {
            return true;
        }
        
        @Override
        public boolean offer(final Object e) {
            return true;
        }
        
        @Override
        public Object poll() {
            return null;
        }
        
        @Override
        public Object peek() {
            return null;
        }
        
        @Override
        public int size() {
            return 0;
        }
        
        @Override
        public Iterator<Object> iterator() {
            return Collections.emptyList().iterator();
        }
    }
    
    enum DiscardingListener implements EvictionListener<Object, Object>
    {
        INSTANCE;
        
        @Override
        public void onEviction(final Object key, final Object value) {
        }
    }
    
    static final class SerializationProxy<K, V> implements Serializable
    {
        final EntryWeigher<? super K, ? super V> weigher;
        final EvictionListener<K, V> listener;
        final int concurrencyLevel;
        final Map<K, V> data;
        final long capacity;
        static final long serialVersionUID = 1L;
        
        SerializationProxy(final ConcurrentLinkedHashMap<K, V> map) {
            this.concurrencyLevel = map.concurrencyLevel;
            this.data = new HashMap<K, V>((Map<? extends K, ? extends V>)map);
            this.capacity = map.capacity.get();
            this.listener = map.listener;
            this.weigher = map.weigher;
        }
        
        Object readResolve() {
            final ConcurrentLinkedHashMap<K, V> map = new Builder<K, V>().concurrencyLevel(this.concurrencyLevel).maximumWeightedCapacity(this.capacity).listener(this.listener).weigher(this.weigher).build();
            map.putAll((Map<?, ?>)this.data);
            return map;
        }
    }
    
    public static final class Builder<K, V>
    {
        static final int DEFAULT_CONCURRENCY_LEVEL = 16;
        static final int DEFAULT_INITIAL_CAPACITY = 16;
        EvictionListener<K, V> listener;
        EntryWeigher<? super K, ? super V> weigher;
        int concurrencyLevel;
        int initialCapacity;
        long capacity;
        
        public Builder() {
            this.capacity = -1L;
            this.weigher = Weighers.entrySingleton();
            this.initialCapacity = 16;
            this.concurrencyLevel = 16;
            this.listener = (EvictionListener<K, V>)DiscardingListener.INSTANCE;
        }
        
        public Builder<K, V> initialCapacity(final int initialCapacity) {
            ConcurrentLinkedHashMap.checkArgument(initialCapacity >= 0);
            this.initialCapacity = initialCapacity;
            return this;
        }
        
        public Builder<K, V> maximumWeightedCapacity(final long capacity) {
            ConcurrentLinkedHashMap.checkArgument(capacity >= 0L);
            this.capacity = capacity;
            return this;
        }
        
        public Builder<K, V> concurrencyLevel(final int concurrencyLevel) {
            ConcurrentLinkedHashMap.checkArgument(concurrencyLevel > 0);
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }
        
        public Builder<K, V> listener(final EvictionListener<K, V> listener) {
            ConcurrentLinkedHashMap.checkNotNull(listener);
            this.listener = listener;
            return this;
        }
        
        public Builder<K, V> weigher(final Weigher<? super V> weigher) {
            this.weigher = ((weigher == Weighers.singleton()) ? Weighers.entrySingleton() : new BoundedEntryWeigher<Object, Object>(Weighers.asEntryWeigher((Weigher<? super Object>)weigher)));
            return this;
        }
        
        public Builder<K, V> weigher(final EntryWeigher<? super K, ? super V> weigher) {
            this.weigher = ((weigher == Weighers.entrySingleton()) ? Weighers.entrySingleton() : new BoundedEntryWeigher<Object, Object>((EntryWeigher<? super Object, ? super Object>)weigher));
            return this;
        }
        
        public ConcurrentLinkedHashMap<K, V> build() {
            ConcurrentLinkedHashMap.checkState(this.capacity >= 0L);
            return new ConcurrentLinkedHashMap<K, V>(this, null);
        }
    }
}
