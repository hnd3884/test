package org.apache.lucene.search;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.AbstractCollection;

final class Multiset<T> extends AbstractCollection<T>
{
    private final Map<T, Integer> map;
    private int size;
    
    Multiset() {
        this.map = new HashMap<T, Integer>();
    }
    
    @Override
    public Iterator<T> iterator() {
        final Iterator<Map.Entry<T, Integer>> mapIterator = this.map.entrySet().iterator();
        return new Iterator<T>() {
            T current;
            int remaining;
            
            @Override
            public boolean hasNext() {
                return this.remaining > 0 || mapIterator.hasNext();
            }
            
            @Override
            public T next() {
                if (this.remaining == 0) {
                    final Map.Entry<T, Integer> next = mapIterator.next();
                    this.current = next.getKey();
                    this.remaining = next.getValue();
                }
                assert this.remaining > 0;
                --this.remaining;
                return this.current;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public void clear() {
        this.map.clear();
        this.size = 0;
    }
    
    @Override
    public boolean add(final T e) {
        final Integer currentFreq = this.map.get(e);
        if (currentFreq == null) {
            this.map.put(e, 1);
        }
        else {
            this.map.put(e, this.map.get(e) + 1);
        }
        ++this.size;
        return true;
    }
    
    @Override
    public boolean remove(final Object o) {
        final Integer count = this.map.get(o);
        if (count == null) {
            return false;
        }
        if (1 == count) {
            this.map.remove(o);
        }
        else {
            this.map.put((T)o, count - 1);
        }
        --this.size;
        return true;
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.map.containsKey(o);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final Multiset<?> that = (Multiset<?>)obj;
        return this.size == that.size && this.map.equals(that.map);
    }
    
    @Override
    public int hashCode() {
        return 31 * this.getClass().hashCode() + this.map.hashCode();
    }
}
