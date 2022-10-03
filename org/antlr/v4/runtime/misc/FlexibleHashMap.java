package org.antlr.v4.runtime.misc;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class FlexibleHashMap<K, V> implements Map<K, V>
{
    public static final int INITAL_CAPACITY = 16;
    public static final int INITAL_BUCKET_CAPACITY = 8;
    public static final double LOAD_FACTOR = 0.75;
    protected final AbstractEqualityComparator<? super K> comparator;
    protected LinkedList<Entry<K, V>>[] buckets;
    protected int n;
    protected int threshold;
    protected int currentPrime;
    protected int initialBucketCapacity;
    
    public FlexibleHashMap() {
        this(null, 16, 8);
    }
    
    public FlexibleHashMap(final AbstractEqualityComparator<? super K> comparator) {
        this(comparator, 16, 8);
    }
    
    public FlexibleHashMap(AbstractEqualityComparator<? super K> comparator, final int initialCapacity, final int initialBucketCapacity) {
        this.n = 0;
        this.threshold = 12;
        this.currentPrime = 1;
        this.initialBucketCapacity = 8;
        if (comparator == null) {
            comparator = ObjectEqualityComparator.INSTANCE;
        }
        this.comparator = comparator;
        this.buckets = createEntryListArray(initialBucketCapacity);
        this.initialBucketCapacity = initialBucketCapacity;
    }
    
    private static <K, V> LinkedList<Entry<K, V>>[] createEntryListArray(final int length) {
        final LinkedList<Entry<K, V>>[] result = new LinkedList[length];
        return result;
    }
    
    protected int getBucket(final K key) {
        final int hash = this.comparator.hashCode(key);
        final int b = hash & this.buckets.length - 1;
        return b;
    }
    
    @Override
    public V get(final Object key) {
        final K typedKey = (K)key;
        if (key == null) {
            return null;
        }
        final int b = this.getBucket(typedKey);
        final LinkedList<Entry<K, V>> bucket = this.buckets[b];
        if (bucket == null) {
            return null;
        }
        for (final Entry<K, V> e : bucket) {
            if (this.comparator.equals((Object)e.key, (Object)typedKey)) {
                return e.value;
            }
        }
        return null;
    }
    
    @Override
    public V put(final K key, final V value) {
        if (key == null) {
            return null;
        }
        if (this.n > this.threshold) {
            this.expand();
        }
        final int b = this.getBucket(key);
        LinkedList<Entry<K, V>> bucket = this.buckets[b];
        if (bucket == null) {
            final LinkedList<Entry<K, V>>[] buckets = this.buckets;
            final int n = b;
            final LinkedList<Entry<K, V>> list = new LinkedList<Entry<K, V>>();
            buckets[n] = list;
            bucket = list;
        }
        for (final Entry<K, V> e : bucket) {
            if (this.comparator.equals((Object)e.key, (Object)key)) {
                final V prev = e.value;
                e.value = value;
                ++this.n;
                return prev;
            }
        }
        bucket.add(new Entry<K, V>(key, value));
        ++this.n;
        return null;
    }
    
    @Override
    public V remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection<V> values() {
        final List<V> a = new ArrayList<V>(this.size());
        for (final LinkedList<Entry<K, V>> bucket : this.buckets) {
            if (bucket != null) {
                for (final Entry<K, V> e : bucket) {
                    a.add(e.value);
                }
            }
        }
        return a;
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.get(key) != null;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        for (final LinkedList<Entry<K, V>> bucket : this.buckets) {
            if (bucket != null) {
                for (final Entry<K, V> e : bucket) {
                    if (e == null) {
                        break;
                    }
                    hash = MurmurHash.update(hash, this.comparator.hashCode(e.key));
                }
            }
        }
        hash = MurmurHash.finish(hash, this.size());
        return hash;
    }
    
    @Override
    public boolean equals(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    protected void expand() {
        final LinkedList<Entry<K, V>>[] old = this.buckets;
        this.currentPrime += 4;
        final int newCapacity = this.buckets.length * 2;
        final LinkedList<Entry<K, V>>[] newTable = createEntryListArray(newCapacity);
        this.buckets = newTable;
        this.threshold = (int)(newCapacity * 0.75);
        final int oldSize = this.size();
        for (final LinkedList<Entry<K, V>> bucket : old) {
            if (bucket != null) {
                for (final Entry<K, V> e : bucket) {
                    if (e == null) {
                        break;
                    }
                    this.put(e.key, e.value);
                }
            }
        }
        this.n = oldSize;
    }
    
    @Override
    public int size() {
        return this.n;
    }
    
    @Override
    public boolean isEmpty() {
        return this.n == 0;
    }
    
    @Override
    public void clear() {
        this.buckets = createEntryListArray(16);
        this.n = 0;
    }
    
    @Override
    public String toString() {
        if (this.size() == 0) {
            return "{}";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append('{');
        boolean first = true;
        for (final LinkedList<Entry<K, V>> bucket : this.buckets) {
            if (bucket != null) {
                for (final Entry<K, V> e : bucket) {
                    if (e == null) {
                        break;
                    }
                    if (first) {
                        first = false;
                    }
                    else {
                        buf.append(", ");
                    }
                    buf.append(e.toString());
                }
            }
        }
        buf.append('}');
        return buf.toString();
    }
    
    public String toTableString() {
        final StringBuilder buf = new StringBuilder();
        for (final LinkedList<Entry<K, V>> bucket : this.buckets) {
            if (bucket == null) {
                buf.append("null\n");
            }
            else {
                buf.append('[');
                boolean first = true;
                for (final Entry<K, V> e : bucket) {
                    if (first) {
                        first = false;
                    }
                    else {
                        buf.append(" ");
                    }
                    if (e == null) {
                        buf.append("_");
                    }
                    else {
                        buf.append(e.toString());
                    }
                }
                buf.append("]\n");
            }
        }
        return buf.toString();
    }
    
    public static void main(final String[] args) {
        final FlexibleHashMap<String, Integer> map = new FlexibleHashMap<String, Integer>();
        map.put("hi", 1);
        map.put("mom", 2);
        map.put("foo", 3);
        map.put("ach", 4);
        map.put("cbba", 5);
        map.put("d", 6);
        map.put("edf", 7);
        map.put("mom", 8);
        map.put("hi", 9);
        System.out.println(map);
        System.out.println(map.toTableString());
    }
    
    public static class Entry<K, V>
    {
        public final K key;
        public V value;
        
        public Entry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public String toString() {
            return this.key.toString() + ":" + this.value.toString();
        }
    }
}
