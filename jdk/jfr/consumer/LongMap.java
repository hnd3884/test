package jdk.jfr.consumer;

import java.util.Iterator;
import java.util.HashMap;

final class LongMap<T> implements Iterable<T>
{
    private final HashMap<Long, T> map;
    
    LongMap() {
        this.map = new HashMap<Long, T>(101);
    }
    
    void put(final long n, final T t) {
        this.map.put(n, t);
    }
    
    T get(final long n) {
        return this.map.get(n);
    }
    
    @Override
    public Iterator<T> iterator() {
        return this.map.values().iterator();
    }
    
    Iterator<Long> keys() {
        return this.map.keySet().iterator();
    }
}
