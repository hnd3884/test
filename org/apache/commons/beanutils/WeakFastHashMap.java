package org.apache.commons.beanutils;

import java.util.ConcurrentModificationException;
import java.util.WeakHashMap;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

class WeakFastHashMap<K, V> extends HashMap<K, V>
{
    private Map<K, V> map;
    private boolean fast;
    
    public WeakFastHashMap() {
        this.map = null;
        this.fast = false;
        this.map = this.createMap();
    }
    
    public WeakFastHashMap(final int capacity) {
        this.map = null;
        this.fast = false;
        this.map = this.createMap(capacity);
    }
    
    public WeakFastHashMap(final int capacity, final float factor) {
        this.map = null;
        this.fast = false;
        this.map = this.createMap(capacity, factor);
    }
    
    public WeakFastHashMap(final Map<? extends K, ? extends V> map) {
        this.map = null;
        this.fast = false;
        this.map = this.createMap(map);
    }
    
    public boolean getFast() {
        return this.fast;
    }
    
    public void setFast(final boolean fast) {
        this.fast = fast;
    }
    
    @Override
    public V get(final Object key) {
        if (this.fast) {
            return this.map.get(key);
        }
        synchronized (this.map) {
            return this.map.get(key);
        }
    }
    
    @Override
    public int size() {
        if (this.fast) {
            return this.map.size();
        }
        synchronized (this.map) {
            return this.map.size();
        }
    }
    
    @Override
    public boolean isEmpty() {
        if (this.fast) {
            return this.map.isEmpty();
        }
        synchronized (this.map) {
            return this.map.isEmpty();
        }
    }
    
    @Override
    public boolean containsKey(final Object key) {
        if (this.fast) {
            return this.map.containsKey(key);
        }
        synchronized (this.map) {
            return this.map.containsKey(key);
        }
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (this.fast) {
            return this.map.containsValue(value);
        }
        synchronized (this.map) {
            return this.map.containsValue(value);
        }
    }
    
    @Override
    public V put(final K key, final V value) {
        if (this.fast) {
            synchronized (this) {
                final Map<K, V> temp = this.cloneMap((Map<? extends K, ? extends V>)this.map);
                final V result = temp.put(key, value);
                this.map = temp;
                return result;
            }
        }
        synchronized (this.map) {
            return this.map.put(key, value);
        }
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> in) {
        if (this.fast) {
            synchronized (this) {
                final Map<K, V> temp = this.cloneMap((Map<? extends K, ? extends V>)this.map);
                temp.putAll(in);
                this.map = temp;
            }
        }
        else {
            synchronized (this.map) {
                this.map.putAll(in);
            }
        }
    }
    
    @Override
    public V remove(final Object key) {
        if (this.fast) {
            synchronized (this) {
                final Map<K, V> temp = this.cloneMap((Map<? extends K, ? extends V>)this.map);
                final V result = temp.remove(key);
                this.map = temp;
                return result;
            }
        }
        synchronized (this.map) {
            return this.map.remove(key);
        }
    }
    
    @Override
    public void clear() {
        if (this.fast) {
            synchronized (this) {
                this.map = this.createMap();
            }
        }
        else {
            synchronized (this.map) {
                this.map.clear();
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        final Map<?, ?> mo = (Map<?, ?>)o;
        if (this.fast) {
            if (mo.size() != this.map.size()) {
                return false;
            }
            for (final Map.Entry<K, V> e : this.map.entrySet()) {
                final K key = e.getKey();
                final V value = e.getValue();
                if (value == null) {
                    if (mo.get(key) != null || !mo.containsKey(key)) {
                        return false;
                    }
                    continue;
                }
                else {
                    if (!value.equals(mo.get(key))) {
                        return false;
                    }
                    continue;
                }
            }
            return true;
        }
        else {
            synchronized (this.map) {
                if (mo.size() != this.map.size()) {
                    return false;
                }
                for (final Map.Entry<K, V> e2 : this.map.entrySet()) {
                    final K key2 = e2.getKey();
                    final V value2 = e2.getValue();
                    if (value2 == null) {
                        if (mo.get(key2) != null || !mo.containsKey(key2)) {
                            return false;
                        }
                        continue;
                    }
                    else {
                        if (!value2.equals(mo.get(key2))) {
                            return false;
                        }
                        continue;
                    }
                }
                return true;
            }
        }
    }
    
    @Override
    public int hashCode() {
        if (this.fast) {
            int h = 0;
            for (final Map.Entry<K, V> e : this.map.entrySet()) {
                h += e.hashCode();
            }
            return h;
        }
        synchronized (this.map) {
            int h2 = 0;
            for (final Map.Entry<K, V> e2 : this.map.entrySet()) {
                h2 += e2.hashCode();
            }
            return h2;
        }
    }
    
    @Override
    public Object clone() {
        WeakFastHashMap<K, V> results = null;
        if (this.fast) {
            results = new WeakFastHashMap<K, V>((Map<? extends K, ? extends V>)this.map);
        }
        else {
            synchronized (this.map) {
                results = new WeakFastHashMap<K, V>((Map<? extends K, ? extends V>)this.map);
            }
        }
        results.setFast(this.getFast());
        return results;
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }
    
    @Override
    public Set<K> keySet() {
        return new KeySet();
    }
    
    @Override
    public Collection<V> values() {
        return new Values();
    }
    
    protected Map<K, V> createMap() {
        return new WeakHashMap<K, V>();
    }
    
    protected Map<K, V> createMap(final int capacity) {
        return new WeakHashMap<K, V>(capacity);
    }
    
    protected Map<K, V> createMap(final int capacity, final float factor) {
        return new WeakHashMap<K, V>(capacity, factor);
    }
    
    protected Map<K, V> createMap(final Map<? extends K, ? extends V> map) {
        return new WeakHashMap<K, V>(map);
    }
    
    protected Map<K, V> cloneMap(final Map<? extends K, ? extends V> map) {
        return this.createMap(map);
    }
    
    private abstract class CollectionView<E> implements Collection<E>
    {
        final /* synthetic */ WeakFastHashMap this$0;
        
        public CollectionView() {
        }
        
        protected abstract Collection<E> get(final Map<K, V> p0);
        
        protected abstract E iteratorNext(final Map.Entry<K, V> p0);
        
        @Override
        public void clear() {
            if (WeakFastHashMap.this.fast) {
                synchronized (WeakFastHashMap.this) {
                    WeakFastHashMap.this.map = WeakFastHashMap.this.createMap();
                }
            }
            else {
                synchronized (WeakFastHashMap.this.map) {
                    this.get(WeakFastHashMap.this.map).clear();
                }
            }
        }
        
        @Override
        public boolean remove(final Object o) {
            if (WeakFastHashMap.this.fast) {
                synchronized (WeakFastHashMap.this) {
                    final Map<K, V> temp = WeakFastHashMap.this.cloneMap(WeakFastHashMap.this.map);
                    final boolean r = this.get(temp).remove(o);
                    WeakFastHashMap.this.map = temp;
                    return r;
                }
            }
            synchronized (WeakFastHashMap.this.map) {
                return this.get(WeakFastHashMap.this.map).remove(o);
            }
        }
        
        @Override
        public boolean removeAll(final Collection<?> o) {
            if (WeakFastHashMap.this.fast) {
                synchronized (WeakFastHashMap.this) {
                    final Map<K, V> temp = WeakFastHashMap.this.cloneMap(WeakFastHashMap.this.map);
                    final boolean r = this.get(temp).removeAll(o);
                    WeakFastHashMap.this.map = temp;
                    return r;
                }
            }
            synchronized (WeakFastHashMap.this.map) {
                return this.get(WeakFastHashMap.this.map).removeAll(o);
            }
        }
        
        @Override
        public boolean retainAll(final Collection<?> o) {
            if (WeakFastHashMap.this.fast) {
                synchronized (WeakFastHashMap.this) {
                    final Map<K, V> temp = WeakFastHashMap.this.cloneMap(WeakFastHashMap.this.map);
                    final boolean r = this.get(temp).retainAll(o);
                    WeakFastHashMap.this.map = temp;
                    return r;
                }
            }
            synchronized (WeakFastHashMap.this.map) {
                return this.get(WeakFastHashMap.this.map).retainAll(o);
            }
        }
        
        @Override
        public int size() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).size();
            }
            synchronized (WeakFastHashMap.this.map) {
                return this.get(WeakFastHashMap.this.map).size();
            }
        }
        
        @Override
        public boolean isEmpty() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).isEmpty();
            }
            synchronized (WeakFastHashMap.this.map) {
                return this.get(WeakFastHashMap.this.map).isEmpty();
            }
        }
        
        @Override
        public boolean contains(final Object o) {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).contains(o);
            }
            synchronized (WeakFastHashMap.this.map) {
                return this.get(WeakFastHashMap.this.map).contains(o);
            }
        }
        
        @Override
        public boolean containsAll(final Collection<?> o) {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).containsAll(o);
            }
            synchronized (WeakFastHashMap.this.map) {
                return this.get(WeakFastHashMap.this.map).containsAll(o);
            }
        }
        
        @Override
        public <T> T[] toArray(final T[] o) {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).toArray(o);
            }
            synchronized (WeakFastHashMap.this.map) {
                return this.get(WeakFastHashMap.this.map).toArray(o);
            }
        }
        
        @Override
        public Object[] toArray() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).toArray();
            }
            synchronized (WeakFastHashMap.this.map) {
                return this.get(WeakFastHashMap.this.map).toArray();
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).equals(o);
            }
            synchronized (WeakFastHashMap.this.map) {
                return this.get(WeakFastHashMap.this.map).equals(o);
            }
        }
        
        @Override
        public int hashCode() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).hashCode();
            }
            synchronized (WeakFastHashMap.this.map) {
                return this.get(WeakFastHashMap.this.map).hashCode();
            }
        }
        
        @Override
        public boolean add(final E o) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean addAll(final Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Iterator<E> iterator() {
            return new CollectionViewIterator();
        }
        
        private class CollectionViewIterator implements Iterator<E>
        {
            private Map<K, V> expected;
            private Map.Entry<K, V> lastReturned;
            private final Iterator<Map.Entry<K, V>> iterator;
            
            public CollectionViewIterator() {
                this.lastReturned = null;
                this.expected = CollectionView.this.this$0.map;
                this.iterator = this.expected.entrySet().iterator();
            }
            
            @Override
            public boolean hasNext() {
                if (this.expected != WeakFastHashMap.this.map) {
                    throw new ConcurrentModificationException();
                }
                return this.iterator.hasNext();
            }
            
            @Override
            public E next() {
                if (this.expected != WeakFastHashMap.this.map) {
                    throw new ConcurrentModificationException();
                }
                this.lastReturned = this.iterator.next();
                return CollectionView.this.iteratorNext(this.lastReturned);
            }
            
            @Override
            public void remove() {
                if (this.lastReturned == null) {
                    throw new IllegalStateException();
                }
                if (WeakFastHashMap.this.fast) {
                    synchronized (WeakFastHashMap.this) {
                        if (this.expected != WeakFastHashMap.this.map) {
                            throw new ConcurrentModificationException();
                        }
                        WeakFastHashMap.this.remove(this.lastReturned.getKey());
                        this.lastReturned = null;
                        this.expected = WeakFastHashMap.this.map;
                    }
                }
                else {
                    this.iterator.remove();
                    this.lastReturned = null;
                }
            }
        }
    }
    
    private class KeySet extends CollectionView<K> implements Set<K>
    {
        @Override
        protected Collection<K> get(final Map<K, V> map) {
            return map.keySet();
        }
        
        @Override
        protected K iteratorNext(final Map.Entry<K, V> entry) {
            return entry.getKey();
        }
    }
    
    private class Values extends CollectionView<V>
    {
        @Override
        protected Collection<V> get(final Map<K, V> map) {
            return map.values();
        }
        
        @Override
        protected V iteratorNext(final Map.Entry<K, V> entry) {
            return entry.getValue();
        }
    }
    
    private class EntrySet extends CollectionView<Map.Entry<K, V>> implements Set<Map.Entry<K, V>>
    {
        @Override
        protected Collection<Map.Entry<K, V>> get(final Map<K, V> map) {
            return map.entrySet();
        }
        
        @Override
        protected Map.Entry<K, V> iteratorNext(final Map.Entry<K, V> entry) {
            return entry;
        }
    }
}
