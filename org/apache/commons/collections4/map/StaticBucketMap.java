package org.apache.commons.collections4.map;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import org.apache.commons.collections4.KeyValue;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.Set;

public final class StaticBucketMap<K, V> extends AbstractIterableMap<K, V>
{
    private static final int DEFAULT_BUCKETS = 255;
    private final Node<K, V>[] buckets;
    private final Lock[] locks;
    
    public StaticBucketMap() {
        this(255);
    }
    
    public StaticBucketMap(final int numBuckets) {
        int size = Math.max(17, numBuckets);
        if (size % 2 == 0) {
            --size;
        }
        this.buckets = new Node[size];
        this.locks = new Lock[size];
        for (int i = 0; i < size; ++i) {
            this.locks[i] = new Lock();
        }
    }
    
    private int getHash(final Object key) {
        if (key == null) {
            return 0;
        }
        int hash = key.hashCode();
        hash += ~(hash << 15);
        hash ^= hash >>> 10;
        hash += hash << 3;
        hash ^= hash >>> 6;
        hash += ~(hash << 11);
        hash ^= hash >>> 16;
        hash %= this.buckets.length;
        return (hash < 0) ? (hash * -1) : hash;
    }
    
    @Override
    public int size() {
        int cnt = 0;
        for (int i = 0; i < this.buckets.length; ++i) {
            synchronized (this.locks[i]) {
                cnt += this.locks[i].size;
            }
        }
        return cnt;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public V get(final Object key) {
        final int hash = this.getHash(key);
        synchronized (this.locks[hash]) {
            for (Node<K, V> n = this.buckets[hash]; n != null; n = n.next) {
                if (n.key == key || (n.key != null && n.key.equals(key))) {
                    return n.value;
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        final int hash = this.getHash(key);
        synchronized (this.locks[hash]) {
            for (Node<K, V> n = this.buckets[hash]; n != null; n = n.next) {
                if (n.key == key || (n.key != null && n.key.equals(key))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        for (int i = 0; i < this.buckets.length; ++i) {
            synchronized (this.locks[i]) {
                for (Node<K, V> n = this.buckets[i]; n != null; n = n.next) {
                    if (n.value == value || (n.value != null && n.value.equals(value))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public V put(final K key, final V value) {
        final int hash = this.getHash(key);
        synchronized (this.locks[hash]) {
            Node<K, V> n = this.buckets[hash];
            if (n == null) {
                n = new Node<K, V>();
                n.key = key;
                n.value = value;
                this.buckets[hash] = n;
                final Lock lock = this.locks[hash];
                ++lock.size;
                return null;
            }
            for (Node<K, V> next = n; next != null; next = next.next) {
                n = next;
                if (n.key == key || (n.key != null && n.key.equals(key))) {
                    final V returnVal = n.value;
                    n.value = value;
                    return returnVal;
                }
            }
            final Node<K, V> newNode = new Node<K, V>();
            newNode.key = key;
            newNode.value = value;
            n.next = newNode;
            final Lock lock2 = this.locks[hash];
            ++lock2.size;
        }
        return null;
    }
    
    @Override
    public V remove(final Object key) {
        final int hash = this.getHash(key);
        synchronized (this.locks[hash]) {
            Node<K, V> n = this.buckets[hash];
            Node<K, V> prev = null;
            while (n != null) {
                if (n.key == key || (n.key != null && n.key.equals(key))) {
                    if (null == prev) {
                        this.buckets[hash] = n.next;
                    }
                    else {
                        prev.next = n.next;
                    }
                    final Lock lock = this.locks[hash];
                    --lock.size;
                    return n.value;
                }
                prev = n;
                n = n.next;
            }
        }
        return null;
    }
    
    @Override
    public Set<K> keySet() {
        return new KeySet();
    }
    
    @Override
    public Collection<V> values() {
        return new Values();
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < this.buckets.length; ++i) {
            final Lock lock = this.locks[i];
            synchronized (lock) {
                this.buckets[i] = null;
                lock.size = 0;
            }
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        final Map<?, ?> other = (Map<?, ?>)obj;
        return this.entrySet().equals(other.entrySet());
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (int i = 0; i < this.buckets.length; ++i) {
            synchronized (this.locks[i]) {
                for (Node<K, V> n = this.buckets[i]; n != null; n = n.next) {
                    hashCode += n.hashCode();
                }
            }
        }
        return hashCode;
    }
    
    public void atomic(final Runnable r) {
        if (r == null) {
            throw new NullPointerException();
        }
        this.atomic(r, 0);
    }
    
    private void atomic(final Runnable r, final int bucket) {
        if (bucket >= this.buckets.length) {
            r.run();
            return;
        }
        synchronized (this.locks[bucket]) {
            this.atomic(r, bucket + 1);
        }
    }
    
    private static final class Node<K, V> implements Map.Entry<K, V>, KeyValue<K, V>
    {
        protected K key;
        protected V value;
        protected Node<K, V> next;
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e2 = (Map.Entry<?, ?>)obj;
            if (this.key == null) {
                if (e2.getKey() != null) {
                    return false;
                }
            }
            else if (!this.key.equals(e2.getKey())) {
                return false;
            }
            if ((this.value != null) ? this.value.equals(e2.getValue()) : (e2.getValue() == null)) {
                return true;
            }
            return false;
        }
        
        @Override
        public V setValue(final V obj) {
            final V retVal = this.value;
            this.value = obj;
            return retVal;
        }
    }
    
    private static final class Lock
    {
        public int size;
    }
    
    private class BaseIterator
    {
        private final ArrayList<Map.Entry<K, V>> current;
        private int bucket;
        private Map.Entry<K, V> last;
        
        private BaseIterator() {
            this.current = new ArrayList<Map.Entry<K, V>>();
        }
        
        public boolean hasNext() {
            if (this.current.size() > 0) {
                return true;
            }
            while (this.bucket < StaticBucketMap.this.buckets.length) {
                synchronized (StaticBucketMap.this.locks[this.bucket]) {
                    for (Node<K, V> n = StaticBucketMap.this.buckets[this.bucket]; n != null; n = n.next) {
                        this.current.add(n);
                    }
                    ++this.bucket;
                    if (this.current.size() > 0) {
                        return true;
                    }
                    continue;
                }
            }
            return false;
        }
        
        protected Map.Entry<K, V> nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.last = this.current.remove(this.current.size() - 1);
        }
        
        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException();
            }
            StaticBucketMap.this.remove(this.last.getKey());
            this.last = null;
        }
    }
    
    private class EntryIterator extends BaseIterator implements Iterator<Map.Entry<K, V>>
    {
        @Override
        public Map.Entry<K, V> next() {
            return this.nextEntry();
        }
    }
    
    private class ValueIterator extends BaseIterator implements Iterator<V>
    {
        @Override
        public V next() {
            return this.nextEntry().getValue();
        }
    }
    
    private class KeyIterator extends BaseIterator implements Iterator<K>
    {
        @Override
        public K next() {
            return this.nextEntry().getKey();
        }
    }
    
    private class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        @Override
        public int size() {
            return StaticBucketMap.this.size();
        }
        
        @Override
        public void clear() {
            StaticBucketMap.this.clear();
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public boolean contains(final Object obj) {
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            final int hash = StaticBucketMap.this.getHash(entry.getKey());
            synchronized (StaticBucketMap.this.locks[hash]) {
                for (Node<K, V> n = StaticBucketMap.this.buckets[hash]; n != null; n = n.next) {
                    if (n.equals(entry)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        @Override
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            final int hash = StaticBucketMap.this.getHash(entry.getKey());
            synchronized (StaticBucketMap.this.locks[hash]) {
                for (Node<K, V> n = StaticBucketMap.this.buckets[hash]; n != null; n = n.next) {
                    if (n.equals(entry)) {
                        StaticBucketMap.this.remove(n.getKey());
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    private class KeySet extends AbstractSet<K>
    {
        @Override
        public int size() {
            return StaticBucketMap.this.size();
        }
        
        @Override
        public void clear() {
            StaticBucketMap.this.clear();
        }
        
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public boolean contains(final Object obj) {
            return StaticBucketMap.this.containsKey(obj);
        }
        
        @Override
        public boolean remove(final Object obj) {
            final int hash = StaticBucketMap.this.getHash(obj);
            synchronized (StaticBucketMap.this.locks[hash]) {
                for (Node<K, V> n = StaticBucketMap.this.buckets[hash]; n != null; n = n.next) {
                    final Object k = n.getKey();
                    if (k == obj || (k != null && k.equals(obj))) {
                        StaticBucketMap.this.remove(k);
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    private class Values extends AbstractCollection<V>
    {
        @Override
        public int size() {
            return StaticBucketMap.this.size();
        }
        
        @Override
        public void clear() {
            StaticBucketMap.this.clear();
        }
        
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
    }
}
