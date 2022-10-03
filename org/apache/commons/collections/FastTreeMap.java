package org.apache.commons.collections;

import java.util.ConcurrentModificationException;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.Map;
import java.util.Comparator;
import java.util.TreeMap;

public class FastTreeMap extends TreeMap
{
    protected TreeMap map;
    protected boolean fast;
    
    public FastTreeMap() {
        this.map = null;
        this.fast = false;
        this.map = new TreeMap();
    }
    
    public FastTreeMap(final Comparator comparator) {
        this.map = null;
        this.fast = false;
        this.map = new TreeMap(comparator);
    }
    
    public FastTreeMap(final Map map) {
        this.map = null;
        this.fast = false;
        this.map = new TreeMap(map);
    }
    
    public FastTreeMap(final SortedMap map) {
        this.map = null;
        this.fast = false;
        this.map = new TreeMap((SortedMap<K, ? extends V>)map);
    }
    
    public boolean getFast() {
        return this.fast;
    }
    
    public void setFast(final boolean fast) {
        this.fast = fast;
    }
    
    public Object get(final Object key) {
        if (this.fast) {
            return this.map.get(key);
        }
        synchronized (this.map) {
            return this.map.get(key);
        }
    }
    
    public int size() {
        if (this.fast) {
            return this.map.size();
        }
        synchronized (this.map) {
            return this.map.size();
        }
    }
    
    public boolean isEmpty() {
        if (this.fast) {
            return this.map.isEmpty();
        }
        synchronized (this.map) {
            return this.map.isEmpty();
        }
    }
    
    public boolean containsKey(final Object key) {
        if (this.fast) {
            return this.map.containsKey(key);
        }
        synchronized (this.map) {
            return this.map.containsKey(key);
        }
    }
    
    public boolean containsValue(final Object value) {
        if (this.fast) {
            return this.map.containsValue(value);
        }
        synchronized (this.map) {
            return this.map.containsValue(value);
        }
    }
    
    public Comparator comparator() {
        if (this.fast) {
            return this.map.comparator();
        }
        synchronized (this.map) {
            return this.map.comparator();
        }
    }
    
    public Object firstKey() {
        if (this.fast) {
            return this.map.firstKey();
        }
        synchronized (this.map) {
            return this.map.firstKey();
        }
    }
    
    public Object lastKey() {
        if (this.fast) {
            return this.map.lastKey();
        }
        synchronized (this.map) {
            return this.map.lastKey();
        }
    }
    
    public Object put(final Object key, final Object value) {
        if (this.fast) {
            synchronized (this) {
                final TreeMap temp = (TreeMap)this.map.clone();
                final Object result = temp.put(key, value);
                this.map = temp;
                return result;
            }
        }
        synchronized (this.map) {
            return this.map.put(key, value);
        }
    }
    
    public void putAll(final Map in) {
        if (this.fast) {
            synchronized (this) {
                final TreeMap temp = (TreeMap)this.map.clone();
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
    
    public Object remove(final Object key) {
        if (this.fast) {
            synchronized (this) {
                final TreeMap temp = (TreeMap)this.map.clone();
                final Object result = temp.remove(key);
                this.map = temp;
                return result;
            }
        }
        synchronized (this.map) {
            return this.map.remove(key);
        }
    }
    
    public void clear() {
        if (this.fast) {
            synchronized (this) {
                this.map = new TreeMap();
            }
        }
        else {
            synchronized (this.map) {
                this.map.clear();
            }
        }
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        final Map mo = (Map)o;
        if (this.fast) {
            if (mo.size() != this.map.size()) {
                return false;
            }
            final Iterator i = this.map.entrySet().iterator();
            while (i.hasNext()) {
                final Map.Entry e = i.next();
                final Object key = e.getKey();
                final Object value = e.getValue();
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
                final Iterator j = this.map.entrySet().iterator();
                while (j.hasNext()) {
                    final Map.Entry e2 = j.next();
                    final Object key2 = e2.getKey();
                    final Object value2 = e2.getValue();
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
    
    public int hashCode() {
        if (this.fast) {
            int h = 0;
            final Iterator i = this.map.entrySet().iterator();
            while (i.hasNext()) {
                h += i.next().hashCode();
            }
            return h;
        }
        synchronized (this.map) {
            int h2 = 0;
            final Iterator j = this.map.entrySet().iterator();
            while (j.hasNext()) {
                h2 += j.next().hashCode();
            }
            return h2;
        }
    }
    
    public Object clone() {
        FastTreeMap results = null;
        if (this.fast) {
            results = new FastTreeMap(this.map);
        }
        else {
            synchronized (this.map) {
                results = new FastTreeMap(this.map);
            }
        }
        results.setFast(this.getFast());
        return results;
    }
    
    public SortedMap headMap(final Object key) {
        if (this.fast) {
            return this.map.headMap(key);
        }
        synchronized (this.map) {
            return this.map.headMap(key);
        }
    }
    
    public SortedMap subMap(final Object fromKey, final Object toKey) {
        if (this.fast) {
            return this.map.subMap(fromKey, toKey);
        }
        synchronized (this.map) {
            return this.map.subMap(fromKey, toKey);
        }
    }
    
    public SortedMap tailMap(final Object key) {
        if (this.fast) {
            return this.map.tailMap(key);
        }
        synchronized (this.map) {
            return this.map.tailMap(key);
        }
    }
    
    public Set entrySet() {
        return new EntrySet();
    }
    
    public Set keySet() {
        return new KeySet();
    }
    
    public Collection values() {
        return new Values();
    }
    
    private abstract class CollectionView implements Collection
    {
        private final /* synthetic */ FastTreeMap this$0;
        
        public CollectionView() {
        }
        
        protected abstract Collection get(final Map p0);
        
        protected abstract Object iteratorNext(final Map.Entry p0);
        
        public void clear() {
            if (FastTreeMap.this.fast) {
                synchronized (FastTreeMap.this) {
                    FastTreeMap.this.map = new TreeMap();
                }
            }
            else {
                synchronized (FastTreeMap.this.map) {
                    this.get(FastTreeMap.this.map).clear();
                }
            }
        }
        
        public boolean remove(final Object o) {
            if (FastTreeMap.this.fast) {
                synchronized (FastTreeMap.this) {
                    final TreeMap temp = (TreeMap)FastTreeMap.this.map.clone();
                    final boolean r = this.get(temp).remove(o);
                    FastTreeMap.this.map = temp;
                    return r;
                }
            }
            synchronized (FastTreeMap.this.map) {
                return this.get(FastTreeMap.this.map).remove(o);
            }
        }
        
        public boolean removeAll(final Collection o) {
            if (FastTreeMap.this.fast) {
                synchronized (FastTreeMap.this) {
                    final TreeMap temp = (TreeMap)FastTreeMap.this.map.clone();
                    final boolean r = this.get(temp).removeAll(o);
                    FastTreeMap.this.map = temp;
                    return r;
                }
            }
            synchronized (FastTreeMap.this.map) {
                return this.get(FastTreeMap.this.map).removeAll(o);
            }
        }
        
        public boolean retainAll(final Collection o) {
            if (FastTreeMap.this.fast) {
                synchronized (FastTreeMap.this) {
                    final TreeMap temp = (TreeMap)FastTreeMap.this.map.clone();
                    final boolean r = this.get(temp).retainAll(o);
                    FastTreeMap.this.map = temp;
                    return r;
                }
            }
            synchronized (FastTreeMap.this.map) {
                return this.get(FastTreeMap.this.map).retainAll(o);
            }
        }
        
        public int size() {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).size();
            }
            synchronized (FastTreeMap.this.map) {
                return this.get(FastTreeMap.this.map).size();
            }
        }
        
        public boolean isEmpty() {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).isEmpty();
            }
            synchronized (FastTreeMap.this.map) {
                return this.get(FastTreeMap.this.map).isEmpty();
            }
        }
        
        public boolean contains(final Object o) {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).contains(o);
            }
            synchronized (FastTreeMap.this.map) {
                return this.get(FastTreeMap.this.map).contains(o);
            }
        }
        
        public boolean containsAll(final Collection o) {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).containsAll(o);
            }
            synchronized (FastTreeMap.this.map) {
                return this.get(FastTreeMap.this.map).containsAll(o);
            }
        }
        
        public Object[] toArray(final Object[] o) {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).toArray(o);
            }
            synchronized (FastTreeMap.this.map) {
                return this.get(FastTreeMap.this.map).toArray(o);
            }
        }
        
        public Object[] toArray() {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).toArray();
            }
            synchronized (FastTreeMap.this.map) {
                return this.get(FastTreeMap.this.map).toArray();
            }
        }
        
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).equals(o);
            }
            synchronized (FastTreeMap.this.map) {
                return this.get(FastTreeMap.this.map).equals(o);
            }
        }
        
        public int hashCode() {
            if (FastTreeMap.this.fast) {
                return this.get(FastTreeMap.this.map).hashCode();
            }
            synchronized (FastTreeMap.this.map) {
                return this.get(FastTreeMap.this.map).hashCode();
            }
        }
        
        public boolean add(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        public boolean addAll(final Collection c) {
            throw new UnsupportedOperationException();
        }
        
        public Iterator iterator() {
            return new CollectionViewIterator();
        }
        
        private class CollectionViewIterator implements Iterator
        {
            private Map expected;
            private Map.Entry lastReturned;
            private Iterator iterator;
            
            public CollectionViewIterator() {
                this.lastReturned = null;
                this.expected = CollectionView.this.this$0.map;
                this.iterator = this.expected.entrySet().iterator();
            }
            
            public boolean hasNext() {
                if (this.expected != FastTreeMap.this.map) {
                    throw new ConcurrentModificationException();
                }
                return this.iterator.hasNext();
            }
            
            public Object next() {
                if (this.expected != FastTreeMap.this.map) {
                    throw new ConcurrentModificationException();
                }
                this.lastReturned = this.iterator.next();
                return CollectionView.this.iteratorNext(this.lastReturned);
            }
            
            public void remove() {
                if (this.lastReturned == null) {
                    throw new IllegalStateException();
                }
                if (FastTreeMap.this.fast) {
                    synchronized (FastTreeMap.this) {
                        if (this.expected != FastTreeMap.this.map) {
                            throw new ConcurrentModificationException();
                        }
                        FastTreeMap.this.remove(this.lastReturned.getKey());
                        this.lastReturned = null;
                        this.expected = FastTreeMap.this.map;
                    }
                }
                else {
                    this.iterator.remove();
                    this.lastReturned = null;
                }
            }
        }
    }
    
    private class KeySet extends CollectionView implements Set
    {
        protected Collection get(final Map map) {
            return map.keySet();
        }
        
        protected Object iteratorNext(final Map.Entry entry) {
            return entry.getKey();
        }
    }
    
    private class Values extends CollectionView
    {
        protected Collection get(final Map map) {
            return map.values();
        }
        
        protected Object iteratorNext(final Map.Entry entry) {
            return entry.getValue();
        }
    }
    
    private class EntrySet extends CollectionView implements Set
    {
        protected Collection get(final Map map) {
            return map.entrySet();
        }
        
        protected Object iteratorNext(final Map.Entry entry) {
            return entry;
        }
    }
}
