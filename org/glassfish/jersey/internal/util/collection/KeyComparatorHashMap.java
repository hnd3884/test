package org.glassfish.jersey.internal.util.collection;

import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.Set;
import java.io.Serializable;
import java.util.Map;
import java.util.AbstractMap;

public class KeyComparatorHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable
{
    private static final long serialVersionUID = 3000273665665137463L;
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    transient Entry<K, V>[] table;
    transient int size;
    int threshold;
    final float loadFactor;
    transient volatile int modCount;
    final KeyComparator<K> keyComparator;
    static final Object NULL_KEY;
    private transient Set<Map.Entry<K, V>> entrySet;
    
    public KeyComparatorHashMap(int initialCapacity, final float loadFactor, final KeyComparator<K> keyComparator) {
        this.entrySet = null;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(LocalizationMessages.ILLEGAL_INITIAL_CAPACITY(initialCapacity));
        }
        if (initialCapacity > 1073741824) {
            initialCapacity = 1073741824;
        }
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException(LocalizationMessages.ILLEGAL_LOAD_FACTOR(loadFactor));
        }
        int capacity;
        for (capacity = 1; capacity < initialCapacity; capacity <<= 1) {}
        this.loadFactor = loadFactor;
        this.threshold = (int)(capacity * loadFactor);
        this.table = new Entry[capacity];
        this.keyComparator = keyComparator;
        this.init();
    }
    
    public KeyComparatorHashMap(final int initialCapacity, final KeyComparator<K> keyComparator) {
        this(initialCapacity, 0.75f, keyComparator);
    }
    
    public KeyComparatorHashMap(final KeyComparator<K> keyComparator) {
        this(16, 0.75f, keyComparator);
    }
    
    public KeyComparatorHashMap(final Map<? extends K, ? extends V> m, final KeyComparator<K> keyComparator) {
        this(Math.max((int)(m.size() / 0.75f) + 1, 16), 0.75f, keyComparator);
        this.putAllForCreate(m);
    }
    
    public int getModCount() {
        return this.modCount;
    }
    
    void init() {
    }
    
    static <T> T maskNull(final T key) {
        return (T)((key == null) ? KeyComparatorHashMap.NULL_KEY : key);
    }
    
    static <T> boolean isNull(final T key) {
        return key == KeyComparatorHashMap.NULL_KEY;
    }
    
    static <T> T unmaskNull(final T key) {
        return (key == KeyComparatorHashMap.NULL_KEY) ? null : key;
    }
    
    static int hash(final Object x) {
        int h = x.hashCode();
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }
    
    static boolean eq(final Object x, final Object y) {
        return x == y || x.equals(y);
    }
    
    static int indexFor(final int h, final int length) {
        return h & length - 1;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    int keyComparatorHash(final K k) {
        return isNull(k) ? this.hash(k.hashCode()) : this.hash(this.keyComparator.hash(k));
    }
    
    int hash(int h) {
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }
    
    boolean keyComparatorEq(final K x, final K y) {
        if (isNull(x)) {
            return x == y;
        }
        if (isNull(y)) {
            return x == y;
        }
        return x == y || this.keyComparator.equals(x, y);
    }
    
    @Override
    public V get(final Object key) {
        final K k = maskNull(key);
        final int hash = this.keyComparatorHash(k);
        final int i = indexFor(hash, this.table.length);
        for (Entry<K, V> e = this.table[i]; e != null; e = e.next) {
            if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
                return e.value;
            }
        }
        return null;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        final K k = maskNull(key);
        final int hash = this.keyComparatorHash(k);
        final int i = indexFor(hash, this.table.length);
        for (Entry<K, V> e = this.table[i]; e != null; e = e.next) {
            if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
                return true;
            }
        }
        return false;
    }
    
    Entry<K, V> getEntry(final K key) {
        final K k = maskNull(key);
        final int hash = this.keyComparatorHash(k);
        final int i = indexFor(hash, this.table.length);
        Entry<K, V> e;
        for (e = this.table[i]; e != null && (e.hash != hash || !this.keyComparatorEq(k, e.key)); e = e.next) {}
        return e;
    }
    
    @Override
    public V put(final K key, final V value) {
        final K k = maskNull(key);
        final int hash = this.keyComparatorHash(k);
        final int i = indexFor(hash, this.table.length);
        for (Entry<K, V> e = this.table[i]; e != null; e = e.next) {
            if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
                final V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }
        ++this.modCount;
        this.addEntry(hash, k, value, i);
        return null;
    }
    
    private void putForCreate(final K key, final V value) {
        final K k = maskNull(key);
        final int hash = this.keyComparatorHash(k);
        final int i = indexFor(hash, this.table.length);
        for (Entry<K, V> e = this.table[i]; e != null; e = e.next) {
            if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
                e.value = value;
                return;
            }
        }
        this.createEntry(hash, k, value, i);
    }
    
    private void putAllForCreate(final Map<? extends K, ? extends V> m) {
        for (final Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.putForCreate(e.getKey(), e.getValue());
        }
    }
    
    void resize(final int newCapacity) {
        final Entry<K, V>[] oldTable = this.table;
        final int oldCapacity = oldTable.length;
        if (oldCapacity == 1073741824) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }
        final Entry<K, V>[] newTable = new Entry[newCapacity];
        this.transfer(newTable);
        this.table = newTable;
        this.threshold = (int)(newCapacity * this.loadFactor);
    }
    
    void transfer(final Entry<K, V>[] newTable) {
        final Entry<K, V>[] src = this.table;
        final int newCapacity = newTable.length;
        for (int j = 0; j < src.length; ++j) {
            Entry<K, V> e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    final Entry<K, V> next = e.next;
                    final int i = indexFor(e.hash, newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        final int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0) {
            return;
        }
        if (numKeysToBeAdded > this.threshold) {
            int targetCapacity = (int)(numKeysToBeAdded / this.loadFactor + 1.0f);
            if (targetCapacity > 1073741824) {
                targetCapacity = 1073741824;
            }
            int newCapacity;
            for (newCapacity = this.table.length; newCapacity < targetCapacity; newCapacity <<= 1) {}
            if (newCapacity > this.table.length) {
                this.resize(newCapacity);
            }
        }
        for (final Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }
    
    @Override
    public V remove(final Object key) {
        final Entry<K, V> e = this.removeEntryForKey(key);
        return (e == null) ? null : e.value;
    }
    
    Entry<K, V> removeEntryForKey(final Object key) {
        final K k = maskNull(key);
        final int hash = this.keyComparatorHash(k);
        final int i = indexFor(hash, this.table.length);
        Entry<K, V> e;
        Entry<K, V> next;
        for (Entry<K, V> prev = e = this.table[i]; e != null; e = next) {
            next = e.next;
            if (e.hash == hash && this.keyComparatorEq(k, e.key)) {
                ++this.modCount;
                --this.size;
                if (prev == e) {
                    this.table[i] = next;
                }
                else {
                    prev.next = next;
                }
                e.recordRemoval(this);
                return e;
            }
            prev = e;
        }
        return e;
    }
    
    Entry<K, V> removeMapping(final Object o) {
        if (!(o instanceof Map.Entry)) {
            return null;
        }
        final Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
        final K k = maskNull(entry.getKey());
        final int hash = this.keyComparatorHash(k);
        final int i = indexFor(hash, this.table.length);
        Entry<K, V> e;
        Entry<K, V> next;
        for (Entry<K, V> prev = e = this.table[i]; e != null; e = next) {
            next = e.next;
            if (e.hash == hash && e.equals(entry)) {
                ++this.modCount;
                --this.size;
                if (prev == e) {
                    this.table[i] = next;
                }
                else {
                    prev.next = next;
                }
                e.recordRemoval(this);
                return e;
            }
            prev = e;
        }
        return e;
    }
    
    @Override
    public void clear() {
        ++this.modCount;
        final Entry[] tab = this.table;
        for (int i = 0; i < tab.length; ++i) {
            tab[i] = null;
        }
        this.size = 0;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            return this.containsNullValue();
        }
        final Entry[] tab = this.table;
        for (int i = 0; i < tab.length; ++i) {
            for (Entry e = tab[i]; e != null; e = e.next) {
                if (value.equals(e.value)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean containsNullValue() {
        final Entry<K, V>[] table;
        final Entry[] tab = table = this.table;
        for (Entry e : table) {
            final Entry aTab = e;
            while (e != null) {
                if (e.value == null) {
                    return true;
                }
                e = e.next;
            }
        }
        return false;
    }
    
    public Object clone() {
        KeyComparatorHashMap<K, V> result = null;
        try {
            result = (KeyComparatorHashMap)super.clone();
            result.table = new Entry[this.table.length];
            result.entrySet = null;
            result.modCount = 0;
            result.size = 0;
            result.init();
            result.putAllForCreate((Map<? extends K, ? extends V>)this);
        }
        catch (final CloneNotSupportedException ex) {}
        return result;
    }
    
    void addEntry(final int hash, final K key, final V value, final int bucketIndex) {
        final Entry<K, V> e = this.table[bucketIndex];
        this.table[bucketIndex] = new Entry<K, V>(hash, key, value, e);
        if (this.size++ >= this.threshold) {
            this.resize(2 * this.table.length);
        }
    }
    
    void createEntry(final int hash, final K key, final V value, final int bucketIndex) {
        final Entry<K, V> e = this.table[bucketIndex];
        this.table[bucketIndex] = new Entry<K, V>(hash, key, value, e);
        ++this.size;
    }
    
    Iterator<K> newKeyIterator() {
        return new KeyIterator();
    }
    
    Iterator<V> newValueIterator() {
        return new ValueIterator();
    }
    
    Iterator<Map.Entry<K, V>> newEntryIterator() {
        return new EntryIterator();
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final Set<Map.Entry<K, V>> es = this.entrySet;
        return (es != null) ? es : (this.entrySet = new EntrySet());
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        final Iterator<Map.Entry<K, V>> i = this.entrySet().iterator();
        s.defaultWriteObject();
        s.writeInt(this.table.length);
        s.writeInt(this.size);
        while (i.hasNext()) {
            final Map.Entry<K, V> e = i.next();
            s.writeObject(e.getKey());
            s.writeObject(e.getValue());
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        final int numBuckets = s.readInt();
        this.table = new Entry[numBuckets];
        this.init();
        for (int ss = s.readInt(), i = 0; i < ss; ++i) {
            final K key = (K)s.readObject();
            final V value = (V)s.readObject();
            this.putForCreate(key, value);
        }
    }
    
    int capacity() {
        return this.table.length;
    }
    
    float loadFactor() {
        return this.loadFactor;
    }
    
    static {
        NULL_KEY = new Object();
    }
    
    static class Entry<K, V> implements Map.Entry<K, V>
    {
        final K key;
        V value;
        final int hash;
        Entry<K, V> next;
        
        Entry(final int h, final K k, final V v, final Entry<K, V> n) {
            this.value = v;
            this.next = n;
            this.key = k;
            this.hash = h;
        }
        
        @Override
        public K getKey() {
            return KeyComparatorHashMap.unmaskNull(this.key);
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public V setValue(final V newValue) {
            final V oldValue = this.value;
            this.value = newValue;
            return oldValue;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            final Object k1 = this.getKey();
            final Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                final Object v1 = this.getValue();
                final Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2))) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return ((this.key == KeyComparatorHashMap.NULL_KEY) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        @Override
        public String toString() {
            return this.getKey() + "=" + this.getValue();
        }
        
        void recordAccess(final KeyComparatorHashMap<K, V> m) {
        }
        
        void recordRemoval(final KeyComparatorHashMap<K, V> m) {
        }
    }
    
    private abstract class HashIterator<E> implements Iterator<E>
    {
        Entry<K, V> next;
        int expectedModCount;
        int index;
        Entry<K, V> current;
        
        HashIterator() {
            this.expectedModCount = KeyComparatorHashMap.this.modCount;
            final Entry<K, V>[] t = KeyComparatorHashMap.this.table;
            int i = t.length;
            Entry<K, V> n = null;
            if (KeyComparatorHashMap.this.size != 0) {
                while (i > 0 && (n = t[--i]) == null) {}
            }
            this.next = n;
            this.index = i;
        }
        
        @Override
        public boolean hasNext() {
            return this.next != null;
        }
        
        Entry<K, V> nextEntry() {
            if (KeyComparatorHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            final Entry<K, V> e = this.next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            Entry<K, V> n;
            Entry<K, V>[] t;
            int i;
            for (n = e.next, t = KeyComparatorHashMap.this.table, i = this.index; n == null && i > 0; n = t[--i]) {}
            this.index = i;
            this.next = n;
            return this.current = e;
        }
        
        @Override
        public void remove() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            if (KeyComparatorHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            final K k = this.current.key;
            this.current = null;
            KeyComparatorHashMap.this.removeEntryForKey(k);
            this.expectedModCount = KeyComparatorHashMap.this.modCount;
        }
    }
    
    private class ValueIterator extends HashIterator<V>
    {
        @Override
        public V next() {
            return this.nextEntry().value;
        }
    }
    
    private class KeyIterator extends HashIterator<K>
    {
        @Override
        public K next() {
            return this.nextEntry().getKey();
        }
    }
    
    private class EntryIterator extends HashIterator<Map.Entry<K, V>>
    {
        @Override
        public Map.Entry<K, V> next() {
            return this.nextEntry();
        }
    }
    
    private class EntrySet extends AbstractSet
    {
        @Override
        public Iterator iterator() {
            return KeyComparatorHashMap.this.newEntryIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<K, V> e = (Map.Entry<K, V>)o;
            final Entry<K, V> candidate = KeyComparatorHashMap.this.getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }
        
        @Override
        public boolean remove(final Object o) {
            return KeyComparatorHashMap.this.removeMapping(o) != null;
        }
        
        @Override
        public int size() {
            return KeyComparatorHashMap.this.size;
        }
        
        @Override
        public void clear() {
            KeyComparatorHashMap.this.clear();
        }
    }
}
